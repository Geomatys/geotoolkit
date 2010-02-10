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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.geotoolkit.display2d.style.renderer.AbstractCoverageRenderer;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PatternRenderer extends AbstractCoverageRenderer<PatternSymbolizer,CachedPatternSymbolizer>{

    private static final Logger LOGGER = Logging.getLogger(PatternRenderer.class);
    
    /**
     * minimum size of the blocks to use. 
     * Values between 1 and 3 give fair result.
     */
    private static final float MINIMUM_BLOCK_SIZE = 1.5f;

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<PatternSymbolizer> getSymbolizerClass() {
        return PatternSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<CachedPatternSymbolizer> getCachedSymbolizerClass() {
        return CachedPatternSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CachedPatternSymbolizer createCachedSymbolizer(PatternSymbolizer symbol) {
        return new CachedPatternSymbolizer(symbol,this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(ProjectedFeature graphic, CachedPatternSymbolizer symbol, RenderingContext2D context)
            throws PortrayalException {
        //nothing to portray;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(ProjectedCoverage projectedCoverage, CachedPatternSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException {

        double[] resolution = context.getResolution();
        final Envelope bounds = new GeneralEnvelope(context.getCanvasObjectiveBounds());
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

        if(!CRS.equalsIgnoreMetadata(dataCoverage.getCoordinateReferenceSystem2D(), context.getObjectiveCRS())){
            //coverage is not in objective crs, resample it
            try{
                //we resample the native view of the coverage only, the style will be applied later.
                dataCoverage = (GridCoverage2D) Operations.DEFAULT.resample(
                        dataCoverage.view(ViewType.NATIVE),
                        context.getObjectiveCRS());
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
        final ReferencedCanvas2D canvas = context.getCanvas();
        final CoordinateReferenceSystem dataCRS = dataCoverage.getCoordinateReferenceSystem();
        final StatefullContextParams params = new StatefullContextParams(canvas,null);
        final CoordinateReferenceSystem objectiveCRS = context.getObjectiveCRS();

        try {
            params.dataToObjective = context.getMathTransform(dataCRS, objectiveCRS);
            params.dataToObjectiveTransformer.setMathTransform(params.dataToObjective);
        } catch (FactoryException ex) {
            ex.printStackTrace();
        }

        final AffineTransform objtoDisp = context.getObjectiveToDisplay();
        params.objectiveToDisplay.setTransform(objtoDisp);
        params.updateGeneralizationFactor(context, dataCRS);
        try {
            params.dataToDisplayTransformer.setMathTransform(context.getMathTransform(dataCRS, context.getDisplayCRS()));
        } catch (FactoryException ex) {
            ex.printStackTrace();
        }

        final StatefullProjectedFeature projectedFeature = new StatefullProjectedFeature(params);
        for(final Map.Entry<SimpleFeature,List<CachedSymbolizer>> entry : features.entrySet()){
            projectedFeature.setFeature(entry.getKey());

            for(final CachedSymbolizer cached : entry.getValue()){
                GO2Utilities.portray(projectedFeature, cached, context);
            }

        }

        context.switchToDisplayCRS();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedCoverage projectedCoverage, final CachedPatternSymbolizer symbol,
            final RenderingContext2D context, final SearchAreaJ2D search, final VisitFilter filter) {


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

    /**
     * {@inheritDoc }
     */
    @Override
    public Rectangle2D glyphPreferredSize(CachedPatternSymbolizer symbol,MapLayer layer) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CachedPatternSymbolizer symbol,MapLayer layer) {
        //todo glyph
    }

}
