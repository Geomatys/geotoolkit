/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageReadParam;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.statefull.StatefullContextParams;
import org.geotoolkit.display2d.container.statefull.StatefullProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PatternRenderer extends AbstractSymbolizerRenderer<CachedPatternSymbolizer>{

    public PatternRenderer(CachedPatternSymbolizer symbol, RenderingContext2D context){
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
    public void portray(ProjectedFeature graphic) throws PortrayalException {
        //nothing to portray;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(ProjectedCoverage projectedCoverage) throws PortrayalException {

        double[] resolution = renderingContext.getResolution();
        final Envelope bounds = new GeneralEnvelope(renderingContext.getCanvasObjectiveBounds());
        resolution = checkResolution(resolution,bounds);
        final CoverageReadParam param = new CoverageReadParam(bounds, resolution);

        GridCoverage2D dataCoverage;
        try {
            dataCoverage = projectedCoverage.getCoverage(param);
        } catch (FactoryException ex) {
            throw new PortrayalException(ex);
        } catch (IOException ex) {
            throw new PortrayalException(ex);
        } catch (TransformException ex) {
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
                System.out.println("ERROR resample in raster symbolizer renderer: " + ex.getMessage());
                ex.printStackTrace();
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

        try {
            params.dataToObjective = renderingContext.getMathTransform(dataCRS, objectiveCRS);
            params.dataToObjectiveTransformer.setMathTransform(params.dataToObjective);
        } catch (FactoryException ex) {
            ex.printStackTrace();
        }

        final AffineTransform objtoDisp = renderingContext.getObjectiveToDisplay();
        params.objectiveToDisplay.setTransform(objtoDisp);
        params.updateGeneralizationFactor(renderingContext, dataCRS);
        try {
            params.dataToDisplayTransformer.setMathTransform(renderingContext.getMathTransform(dataCRS, renderingContext.getDisplayCRS()));
        } catch (FactoryException ex) {
            ex.printStackTrace();
        }

        final StatefullProjectedFeature projectedFeature = new StatefullProjectedFeature(params);
        for(final Map.Entry<SimpleFeature,List<CachedSymbolizer>> entry : features.entrySet()){
            projectedFeature.setFeature(entry.getKey());

            for(final CachedSymbolizer cached : entry.getValue()){
                GO2Utilities.portray(projectedFeature, cached, renderingContext);
            }

        }

        renderingContext.switchToDisplayCRS();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedCoverage projectedCoverage, final SearchAreaJ2D search, final VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Shape mask = search.getDisplayShape();
        final Shape shape;
        try {
            shape = projectedCoverage.getEnvelopeGeometry().getDisplayShape();
        } catch (TransformException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }

        final Area area = new Area(mask);

        switch(filter){
            case INTERSECTS :
                area.intersect(new Area(shape));
                return !area.isEmpty();
            case WITHIN :
                Area start = new Area(area);
                area.add(new Area(shape));
                return start.equals(area);
        }

        return false;
    }

    @Override
    public boolean hit(ProjectedFeature graphic, SearchAreaJ2D mask, VisitFilter filter) {
        //nothing to hit
        return false;
    }

}
