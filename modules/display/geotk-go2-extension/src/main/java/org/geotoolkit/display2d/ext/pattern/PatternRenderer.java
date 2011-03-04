/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.display2d.ext.pattern;

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.statefull.StatefullContextParams;
import org.geotoolkit.display2d.container.statefull.StatefullProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryTransformer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Renderer for Pattern symbolizer.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PatternRenderer extends AbstractCoverageSymbolizerRenderer<CachedPatternSymbolizer>{

    public PatternRenderer(final CachedPatternSymbolizer symbol, final RenderingContext2D context){
        super(symbol,context);
    }

    /**
     * minimum size of the blocks to use. 
     * Values between 1 and 3 give fair result.
     */
    private static final float MINIMUM_BLOCK_SIZE = 1.5f;

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedCoverage projectedCoverage) throws PortrayalException {

        double[] resolution = renderingContext.getResolution();
        final Envelope bounds = new GeneralEnvelope(renderingContext.getCanvasObjectiveBounds());
        resolution = checkResolution(resolution,bounds);
        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(bounds);
        param.setResolution(resolution);

        GridCoverage2D dataCoverage;
        try {
            dataCoverage = projectedCoverage.getCoverage(param);
        } catch (CoverageStoreException ex) {
            throw new PortrayalException(ex);
        }

        if(!CRS.equalsIgnoreMetadata(dataCoverage.getCoordinateReferenceSystem2D(), renderingContext.getObjectiveCRS())){
            //coverage is not in objective crs, resample it
            try{
                //we resample the native view of the coverage only, the style will be applied later.
                dataCoverage = (GridCoverage2D) Operations.DEFAULT.resample(
                        dataCoverage.view(ViewType.NATIVE),
                        renderingContext.getObjectiveCRS());
            }catch(Exception ex){
                LOGGER.log(Level.WARNING, "ERROR resample in raster symbolizer renderer",ex);
            }
        }


        final Map<SimpleFeature, List<CachedSymbolizer>> features;
        try {
            features = symbol.getMasks(dataCoverage);
        } catch (IOException ex) {
            throw new PortrayalException(ex);
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }


        //paint all dynamicly generated features -------------------------------
        final ReferencedCanvas2D canvas = renderingContext.getCanvas();
        final CoordinateReferenceSystem dataCRS = dataCoverage.getCoordinateReferenceSystem();
        final StatefullContextParams params = new StatefullContextParams(canvas,null);
        final CoordinateReferenceSystem objectiveCRS = renderingContext.getObjectiveCRS();


        final AffineTransform2D objtoDisp = renderingContext.getObjectiveToDisplay();
        params.objectiveToDisplay.setTransform(objtoDisp);
        ((CoordinateSequenceMathTransformer)params.objToDisplayTransformer.getCSTransformer())
                .setTransform(objtoDisp);


        //data to objective
        final CoordinateSequenceMathTransformer cstrs;
        try {
            cstrs = new CoordinateSequenceMathTransformer(CRS.findMathTransform(dataCRS, objectiveCRS));
        } catch (FactoryException ex) {
            throw new PortrayalException(ex);
        }
        GeometryTransformer trs = new GeometryCSTransformer(cstrs);

        final StatefullProjectedFeature projectedFeature = new StatefullProjectedFeature(params);
        try {
            for(final Map.Entry<SimpleFeature,List<CachedSymbolizer>> entry : features.entrySet()){
                Feature f = entry.getKey();
                f.getDefaultGeometryProperty().setValue(trs.transform((Geometry)f.getDefaultGeometryProperty().getValue()));
                projectedFeature.setCandidate(entry.getKey());

                for(final CachedSymbolizer cached : entry.getValue()){
                    GO2Utilities.portray(projectedFeature, cached, renderingContext);
                }
            }
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        renderingContext.switchToDisplayCRS();
    }

}
