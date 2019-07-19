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
package org.geotoolkit.coverage.amended;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.AbstractGridCoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * Decorate a coverage reader changing behavior to match overriden properties
 * in the coverage reference.
 *
 * @author Johann Sorel (Geomatys)
 */
public class AmendedCoverageReader extends AbstractGridCoverageReader {

    private final AmendedCoverageResource ref;
    private final GridCoverageReader reader;

    public AmendedCoverageReader(AmendedCoverageResource ref) throws DataStoreException {
        this.ref = ref;
        this.reader = ref.getDecorated().acquireReader();
    }

    public GridCoverageReader getDecorated(){
        return reader;
    }

    /**
     * Delegates to wrapped coverage reader.
     *
     * @return wrapped real coverage names
     * @throws DataStoreException
     * @throws CancellationException
     */
    @Override
    public GenericName getCoverageName() throws DataStoreException, CancellationException {
        return reader.getCoverageName();
    }

    /**
     * Returns the corrected grid geometry based on overriden properties in the coverage reference.
     *
     * @param index : image index
     * @return GeneralGridGeometry, never null
     * @throws DataStoreException
     * @throws CancellationException
     */
    @Override
    public GridGeometry getGridGeometry() throws DataStoreException, CancellationException {
        return ref.getGridGeometry();
    }

    /**
     * Returns the corrected sample dimensions based on overriden properties in the coverage reference.
     *
     * @param index : image index
     * @return sample dimensions, can be null or empty
     * @throws DataStoreException
     * @throws CancellationException
     */
    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException, CancellationException {
        return ref.getSampleDimensions(0);
    }

    /**
     * Decorates and fixes the reading parameters passed and returned by the wrapped coverage reader
     * to match overriden properties in the coverage reference.
     *
     *
     * @param index : image index
     * @param param : features parameters.
     * @return GridCoverage
     * @throws DataStoreException
     * @throws CancellationException
     */
    @Override
    public GridCoverage read(GridCoverageReadParam param) throws DataStoreException, CancellationException {

        GridCoverage coverage;
        if(ref.isGridGeometryOverriden()){

            final CoordinateReferenceSystem overrideCRS = ref.getOverrideCRS();
            final MathTransform overrideGridToCrs = ref.getOverrideGridToCrs();
            final PixelInCell overridePixelInCell = ref.getOverridePixelInCell();
            final GridGeometry overrideGridGeometry = ref.getGridGeometry();
            final GridGeometry originalGridGeometry = ref.getOriginalGridGeometry();

            //convert parameters to fit overrides
            double[] queryRes = param==null ? null : param.getResolution();
            CoordinateReferenceSystem queryCrs = param==null ? null : param.getCoordinateReferenceSystem();
            Envelope queryEnv = param==null ? null : param.getEnvelope();

            //find requested envelope
            if(queryEnv==null && queryCrs!=null){
                try {
                    queryEnv = Envelopes.transform(overrideGridGeometry.getEnvelope(),queryCrs);
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
                    coverageEnv = Envelopes.transform(queryEnv, overrideGridGeometry.getCoordinateReferenceSystem());
                } catch (TransformException ex) {
                    throw new CoverageStoreException(ex.getMessage(), ex);
                }
            }

            //change the crs to original one
            if(overrideCRS!=null){
                coverageEnv = new GeneralEnvelope(coverageEnv);
                ((GeneralEnvelope)coverageEnv).setCoordinateReferenceSystem(
                        originalGridGeometry.getCoordinateReferenceSystem());
            }

            //change the queried envelope
            MathTransform fixedToOriginal = null;
            MathTransform originalToFixed = null;
            if(overrideGridToCrs!=null || overridePixelInCell!=null){
                try {
                    final MathTransform overrideCrsToGrid = overrideGridGeometry.getGridToCRS(PixelInCell.CELL_CENTER).inverse();
                    fixedToOriginal = MathTransforms.concatenate(overrideCrsToGrid, originalGridGeometry.getGridToCRS(PixelInCell.CELL_CENTER));
                    originalToFixed = fixedToOriginal.inverse();
                    coverageEnv = Envelopes.transform(fixedToOriginal, coverageEnv);
                    coverageEnv = new GeneralEnvelope(coverageEnv);
                    ((GeneralEnvelope)coverageEnv).setCoordinateReferenceSystem(
                            originalGridGeometry.getCoordinateReferenceSystem());
                } catch (TransformException ex) {
                    throw new CoverageStoreException(ex);
                }
            }

            if (originalToFixed!=null && coverageRes!=null) {
                //adjust resolution
                double s = XAffineTransform.getScale((AffineTransform2D)originalToFixed);
                coverageRes[0] /= s;
                coverageRes[1] /= s;
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
            coverage = reader.read(refParam);

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
            coverage = reader.read(param);
        }

        //override sample dimensions
        final List<SampleDimension> overrideDims = ref.getOverrideDims();
        if (overrideDims != null) {
            List<SampleDimension> sd = coverage.getSampleDimensions();

            //check if size match
            List<SampleDimension> overs = overrideDims;
            int[] sourceBands = param.getSourceBands();
            if (sourceBands != null) {
                //we make an extra check not all readers honor the band selection parameters
                if (sd == null || overrideDims.size() != sd.size()) {
                    overs = new ArrayList<>();
                    for (int i : sourceBands) {
                        overs.add(overrideDims.get(i));
                    }
                }
            }

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setGridCoverage(coverage);
            gcb.setSampleDimensions(overrideDims.toArray(new SampleDimension[overs.size()]));
            coverage = gcb.build();
        }

        return coverage;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void reset() throws DataStoreException {
        reader.reset();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        ref.getDecorated().recycle(reader);
    }


}
