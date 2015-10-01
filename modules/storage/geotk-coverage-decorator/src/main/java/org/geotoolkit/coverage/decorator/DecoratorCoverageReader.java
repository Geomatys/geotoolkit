/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.coverage.decorator;

import java.util.List;
import java.util.concurrent.CancellationException;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DecoratorCoverageReader extends GridCoverageReader{

    private final DecoratorCoverageReference ref;
    private final GridCoverageReader reader;

    public DecoratorCoverageReader(DecoratorCoverageReference ref) throws CoverageStoreException {
        this.ref = ref;
        this.reader = ref.getDecorated().acquireReader();
    }

    public CoverageReader getDecorated(){
        return reader;
    }

    @Override
    public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
        return reader.getCoverageNames();
    }

    @Override
    public GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException, CancellationException {
        return ref.getGridGeometry(index);
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException, CancellationException {
        return ref.getSampleDimensions(index);
    }

    @Override
    public GridCoverage read(int index, GridCoverageReadParam param) throws CoverageStoreException, CancellationException {



        GridCoverage coverage;
        if(ref.isGridGeometryOverriden()){

            final CoordinateReferenceSystem overrideCRS = ref.getOverrideCRS();
            final MathTransform overrideGridToCrs = ref.getOverrideGridToCrs();
            final PixelInCell overridePixelInCell = ref.getOverridePixelInCell();
            final GeneralGridGeometry overrideGridGeometry = ref.getGridGeometry(index);
            final GeneralGridGeometry originalGridGeometry = ref.getOriginalGridGeometry(index);

            //convert parameters to fit overrides
            double[] queryRes = param.getResolution();
            CoordinateReferenceSystem queryCrs = param.getCoordinateReferenceSystem();
            Envelope queryEnv = param.getEnvelope();

            //find requested envelope
            if(queryEnv==null && param.getCoordinateReferenceSystem()!=null){
                try {
                    queryEnv = CRS.transform(overrideGridGeometry.getEnvelope(),param.getCoordinateReferenceSystem());
                } catch (TransformException ex) {
                    throw new CoverageStoreException(ex.getMessage(), ex);
                }
            }

            //convert resolution to coverage crs
            double[] coverageRes = queryRes;
            if(queryRes!=null){
                try {
                    coverageRes = ReferencingUtilities.convertResolution(queryEnv, queryRes,
                            overrideGridGeometry.getCoordinateReferenceSystem());
                } catch (TransformException ex) {
                    throw new CoverageStoreException(ex.getMessage(), ex);
                }
            }

            //get envelope in coverage crs
            Envelope coverageEnv;
            if(queryEnv==null){
                //if no envelope is defined, use the full extent
                coverageEnv = overrideGridGeometry.getEnvelope();
            }else{
                try {
                    coverageEnv = CRS.transform(queryEnv, overrideGridGeometry.getCoordinateReferenceSystem());
                } catch (TransformException ex) {
                    throw new CoverageStoreException(ex.getMessage(), ex);
                }
            }

            //change the crs to original one
            if(overrideCRS!=null){
                coverageEnv = GeneralEnvelope.castOrCopy(coverageEnv);
                ((GeneralEnvelope)coverageEnv).setCoordinateReferenceSystem(
                        originalGridGeometry.getCoordinateReferenceSystem());
            }

            //change the queried envelope
            MathTransform fixedToOriginal = null;
            MathTransform originalToFixed = null;
            if(overrideGridToCrs!=null || overridePixelInCell!=null){
                try {
                    final MathTransform overrideCrsToGrid = overrideGridGeometry.getGridToCRS().inverse();
                    fixedToOriginal = MathTransforms.concatenate(overrideCrsToGrid, originalGridGeometry.getGridToCRS());
                    originalToFixed = fixedToOriginal.inverse();
                    coverageEnv = CRS.transform(fixedToOriginal, coverageEnv);
                    coverageEnv = GeneralEnvelope.castOrCopy(coverageEnv);
                    ((GeneralEnvelope)coverageEnv).setCoordinateReferenceSystem(
                            originalGridGeometry.getCoordinateReferenceSystem());
                } catch (TransformException ex) {
                    throw new CoverageStoreException(ex);
                }
            }


            //query original reader
            final GridCoverageReadParam refParam = new GridCoverageReadParam(param);
            refParam.setResolution(coverageRes);
            refParam.setCoordinateReferenceSystem(null);
            refParam.setEnvelope(null);
            if(coverageEnv!=null){
                refParam.setCoordinateReferenceSystem(coverageEnv.getCoordinateReferenceSystem());
                refParam.setEnvelope(coverageEnv);
            }
            coverage = reader.read(index, refParam);

            //fix coverage transform and crs
            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setGridCoverage(coverage);
            if(overrideCRS!=null){
                gcb.setCoordinateReferenceSystem(overrideCRS);
            }
            if(overrideGridToCrs!=null || overridePixelInCell!=null){
                final MathTransform localGridToCrs = gcb.getGridToCRS();
                final MathTransform localFixedGridToCrs = MathTransforms.concatenate(localGridToCrs, originalToFixed);
                gcb.setGridToCRS(localFixedGridToCrs);
            }

            coverage = gcb.build();

        }else{
            coverage = reader.read(index, param);
        }

        //override sample dimensions
        final List<GridSampleDimension> overrideDims = ref.getOverrideDims();
        if(overrideDims!=null){
            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setGridCoverage(coverage);
            gcb.setSampleDimensions(overrideDims.toArray(new SampleDimension[overrideDims.size()]));
            coverage = gcb.build();
        }

        return coverage;
    }

    @Override
    public void reset() throws CoverageStoreException {
        reader.reset();
    }

    @Override
    public void dispose() {
        ref.getDecorated().recycle(reader);
    }


}
