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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.canvas.AbstractCanvas2D;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryTransformer;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.processing.coverage.resample.ResampleProcess;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Renderer for Pattern symbolizer.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class PatternRenderer extends AbstractCoverageSymbolizerRenderer<CachedPatternSymbolizer>{

    public PatternRenderer(final SymbolizerRendererService service,final CachedPatternSymbolizer symbol, final RenderingContext2D context){
        super(service,symbol,context);
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
    public boolean portray(final ProjectedCoverage projectedCoverage) throws PortrayalException {

        double[] resolution = renderingContext.getResolution();
        final Envelope bounds = new GeneralEnvelope(renderingContext.getCanvasObjectiveBounds());
        resolution = checkResolution(resolution,bounds);
        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(bounds);
        param.setResolution(resolution);

        GridCoverage dataCoverage;
        try {
            dataCoverage = projectedCoverage.getCoverage(param);
        } catch (DataStoreException ex) {
            throw new PortrayalException(ex);
        }

        if (!Utilities.equalsIgnoreMetadata(CRS.getHorizontalComponent(dataCoverage.getCoordinateReferenceSystem()), renderingContext.getObjectiveCRS())) {
            //coverage is not in objective crs, resample it
            try {
                //we resample the native view of the coverage only, the style will be applied later.

                dataCoverage = new ResampleProcess(dataCoverage, renderingContext.getObjectiveCRS(), null, InterpolationCase.NEIGHBOR, null).executeNow();
            } catch(Exception ex) {
                LOGGER.log(Level.WARNING, "ERROR resample in raster symbolizer renderer",ex);
            }
        }


        final Map<Feature, List<CachedSymbolizer>> features;
        try {
            features = symbol.getMasks(dataCoverage);
        } catch (IOException ex) {
            throw new PortrayalException(ex);
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }


        //paint all dynamicly generated features -------------------------------
        final AbstractCanvas2D canvas = renderingContext.getCanvas();
        final CoordinateReferenceSystem dataCRS = dataCoverage.getCoordinateReferenceSystem();
        final StatelessContextParams params = new StatelessContextParams(canvas,null);
        final CoordinateReferenceSystem objectiveCRS = renderingContext.getObjectiveCRS();


        final AffineTransform2D objtoDisp = renderingContext.getObjectiveToDisplay();
        params.objectiveToDisplay.setTransform(objtoDisp);
        ((CoordinateSequenceMathTransformer)params.objToDisplayTransformer.getCSTransformer())
                .setTransform(objtoDisp);


        //data to objective
        final CoordinateSequenceMathTransformer cstrs;
        try {
            cstrs = new CoordinateSequenceMathTransformer(CRS.findOperation(dataCRS, objectiveCRS, null).getMathTransform());
        } catch (FactoryException ex) {
            throw new PortrayalException(ex);
        }
        GeometryTransformer trs = new GeometryCSTransformer(cstrs);

        final ProjectedFeature projectedFeature = new ProjectedFeature(params);
        boolean dataRendered = false;
        try {
            for(final Map.Entry<Feature,List<CachedSymbolizer>> entry : features.entrySet()){
                Feature f = entry.getKey();
                f.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(),
                        trs.transform((Geometry)f.getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString())));
                projectedFeature.setCandidate(entry.getKey());

                for(final CachedSymbolizer cached : entry.getValue()){
                    dataRendered |= GO2Utilities.portray(projectedFeature, cached, renderingContext);
                }
            }
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        renderingContext.switchToDisplayCRS();
        return dataRendered;
    }
}
