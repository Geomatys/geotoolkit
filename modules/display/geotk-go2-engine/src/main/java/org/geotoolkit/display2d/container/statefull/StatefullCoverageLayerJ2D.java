/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.container.statefull;


import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.io.CoverageStoreException;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.container.stateless.AbstractLayerJ2D;
import org.geotoolkit.display2d.primitive.DefaultSearchAreaJ2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.GraphicBuilder;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullCoverageLayerJ2D extends AbstractLayerJ2D<CoverageMapLayer>{

    private final StatefullProjectedCoverage projectedCoverage;

    //compare values to update caches if necessary
    private final StatefullContextParams params;
    private CoordinateReferenceSystem dataCRS;
    private CoordinateReferenceSystem lastObjectiveCRS = null;

    public StatefullCoverageLayerJ2D(ReferencedCanvas2D canvas, CoverageMapLayer layer){
        super(canvas, layer, true);

        try {
            this.dataCRS = layer.getCoverageReader().getGridGeometry(0).getCoordinateReferenceSystem();
        } catch (CoverageStoreException ex) {
            Logger.getLogger(StatefullCoverageLayerJ2D.class.getName()).log(Level.SEVERE, null, ex);
        }


        params = new StatefullContextParams(canvas,null);
        this.projectedCoverage = new StatefullProjectedCoverage(params, layer);
    }

    private synchronized void updateCache(RenderingContext2D context){

        boolean objectiveCleared = false;

        //clear objective cache is objective crs changed -----------------------
        final CoordinateReferenceSystem objectiveCRS = context.getObjectiveCRS();
        if(objectiveCRS != lastObjectiveCRS){
            params.objectiveToDisplay.setToIdentity();
            lastObjectiveCRS = objectiveCRS;
            objectiveCleared = true;

            try {
                params.dataToObjective = context.getMathTransform(dataCRS, objectiveCRS);
                params.dataToObjectiveTransformer.setMathTransform(params.dataToObjective);
            } catch (FactoryException ex) {
                ex.printStackTrace();
            }

            projectedCoverage.clearObjectiveCache();

        }

        //clear display cache if needed ----------------------------------------
        final AffineTransform objtoDisp = context.getObjectiveToDisplay();

        if(!objtoDisp.equals(params.objectiveToDisplay)){
            params.objectiveToDisplay.setTransform(objtoDisp);
            params.updateGeneralizationFactor(context, dataCRS);
            try {
                params.dataToDisplayTransformer.setMathTransform(context.getMathTransform(dataCRS, context.getDisplayCRS()));
            } catch (FactoryException ex) {
                ex.printStackTrace();
            }

            if(!objectiveCleared){
                //no need to clear the display cache if the objective clear has already been called
                projectedCoverage.clearDisplayCache();
            }

        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void paint(final RenderingContext2D renderingContext) {
        
        //we abort painting if the layer is not visible.
        if (!layer.isVisible()) return;        
             
        final Name coverageName = layer.getCoverageName();
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(layer.getStyle(), renderingContext.getGeographicScale(), coverageName);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.length == 0) {
            return;
        }

        paintRasterLayer(layer, rules, renderingContext);
    }

    private void paintRasterLayer(final CoverageMapLayer layer, final CachedRule[] rules,
            final RenderingContext2D context) {
        updateCache(context);

        //search for a special graphic renderer
        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) layer.getGraphicBuilder(GraphicJ2D.class);
        if(builder != null){
            //this layer has a special graphic rendering, use it instead of normal rendering
            final Collection<GraphicJ2D> graphics = builder.createGraphics(layer, canvas);
            for(GraphicJ2D gra : graphics){
                gra.paint(context);
            }
            return;
        }
        
        if(!intersects(context.getCanvasObjectiveBounds2D())){
            //grid not in the envelope, we have finisehd
            return;
        }
        
        for(final CachedRule rule : rules){
            for(final CachedSymbolizer symbol : rule.symbolizers()){
                try {
                    GO2Utilities.portray(projectedCoverage, symbol, context);
                } catch (PortrayalException ex) {
                    context.getMonitor().exceptionOccured(ex, Level.SEVERE);
                }
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {

        if(!(context instanceof RenderingContext2D) ) return graphics;
        if(!layer.isSelectable())                     return graphics;
        if(!layer.isVisible())                        return graphics;

        final RenderingContext2D renderingContext = (RenderingContext2D) context;

        final Name coverageName = layer.getCoverageName();
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(layer.getStyle(), renderingContext.getGeographicScale(), coverageName);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.length == 0) {
            return graphics;
        }

        if(graphics == null) graphics = new ArrayList<Graphic>();
        if(mask instanceof SearchAreaJ2D){
            graphics = searchAt(layer,rules,renderingContext,(SearchAreaJ2D)mask,filter,graphics);
        }else{
            graphics = searchAt(layer,rules,renderingContext,new DefaultSearchAreaJ2D(mask),filter,graphics);
        }
        

        return graphics;
    }

    private List<Graphic> searchAt(final CoverageMapLayer layer, final CachedRule[] rules,
            final RenderingContext2D renderingContext, final SearchAreaJ2D mask, VisitFilter filter, List<Graphic> graphics) {
        updateCache(renderingContext);

        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) layer.getGraphicBuilder(GraphicJ2D.class);
        if(builder != null){
            //this layer hasa special graphic rendering, use it instead of normal rendering
            final Collection<GraphicJ2D> gras = builder.createGraphics(layer, canvas);
            for(final GraphicJ2D gra : gras){
                graphics = gra.getGraphicAt(renderingContext, mask, filter,graphics);
            }
            return graphics;
        }
       

        for (final CachedRule rule : rules) {
            for (final CachedSymbolizer symbol : rule.symbolizers()) {
                if(GO2Utilities.hit(projectedCoverage, symbol, renderingContext, mask, filter)){
                    graphics.add(projectedCoverage);
                    break;
                }
            }
        }

        return graphics;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoverageMapLayer getUserObject() {
        return layer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope() {
        return super.getEnvelope();
    }

    @Override
    public void dispose() {
        projectedCoverage.dispose();
        super.dispose();
    }

}
