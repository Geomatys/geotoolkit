/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2014, Geomatys
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
package org.geotoolkit.display2d.container.stateless;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.DefaultSearchAreaJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.StorageListener;
import org.opengis.display.primitive.Graphic;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class StatelessCoverageLayerJ2D extends StatelessMapLayerJ2D<MapLayer> implements ChangeListener<ChangeEvent> {

    protected StorageListener.Weak weakStoreListener = new StorageListener.Weak(this);

    private final ProjectedCoverage projectedCoverage;
    private final boolean ignoreBuilders;

    //compare values to update caches if necessary
    private final StatelessContextParams params;
    private CoordinateReferenceSystem lastObjectiveCRS = null;

    public StatelessCoverageLayerJ2D(final J2DCanvas canvas, final MapLayer layer){
        this(canvas,layer,false);
    }

    public StatelessCoverageLayerJ2D(final J2DCanvas canvas, final MapLayer layer, final boolean ignoreBuilders){
        super(canvas, layer, false);
        this.ignoreBuilders = ignoreBuilders;
        this.params = new StatelessContextParams(canvas,null);
        this.projectedCoverage = new ProjectedCoverage(params, layer);
        this.weakStoreListener.registerSource(layer.getResource());
    }

    private synchronized void updateCache(final RenderingContext2D context){
        params.update(context);
        params.objectiveCRS = context.getObjectiveCRS();
        params.displayCRS = context.getDisplayCRS();
        boolean objectiveCleared = false;

        //clear objective cache is objective crs changed -----------------------
        //todo use only the 2D CRS, the transform parameters are only used for the border
        //geometry if needed, the gridcoverageReader will handle itself the transform
        final CoordinateReferenceSystem objectiveCRS2D = context.getObjectiveCRS2D();
        if(objectiveCRS2D != lastObjectiveCRS){
            params.objectiveToDisplay.setToIdentity();
            lastObjectiveCRS = objectiveCRS2D;
            objectiveCleared = true;
            projectedCoverage.clearObjectiveCache();
        }

        //clear display cache if needed ----------------------------------------
        final AffineTransform2D objtoDisp = context.getObjectiveToDisplay();

        if(!objtoDisp.equals(params.objectiveToDisplay)){
            params.objectiveToDisplay.setTransform(objtoDisp);
            ((CoordinateSequenceMathTransformer)params.objToDisplayTransformer.getCSTransformer())
                    .setTransform(objtoDisp);

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
    public boolean paintLayer(final RenderingContext2D renderingContext) {

        GenericName coverageName = null;
        try {
            coverageName = item.getResource().getIdentifier();
        } catch (DataStoreException ex) {
            //do nothing
        }
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(item.getStyle(),
                renderingContext.getSEScale(), coverageName,null);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.length == 0) {
            return false;
        }

        return paintRaster(item, rules, renderingContext);
    }

    private boolean paintRaster(final MapLayer item, final CachedRule[] rules,
            final RenderingContext2D context) {
        updateCache(context);

        //search for a special graphic renderer
        if(!ignoreBuilders){
            final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) item.getGraphicBuilder(GraphicJ2D.class);
            if(builder != null){
                //this layer has a special graphic rendering, use it instead of normal rendering
                final Collection<GraphicJ2D> graphics = builder.createGraphics(item, getCanvas());
                boolean dataRendered = false;
                for(GraphicJ2D gra : graphics){
                    dataRendered |= gra.paint(context);
                }
                return dataRendered;
            }
        }

        //no need to do this here, it may open a coverage reader for nothing
        //if(!intersects(context.getCanvasObjectiveBounds2D())){
        //    //grid not in the envelope, we have finisehd
        //    return;
        //}

        boolean dataRendered = false;
        for(final CachedRule rule : rules){
            for(final CachedSymbolizer symbol : rule.symbolizers()){
                try {
                    dataRendered |= GO2Utilities.portray(projectedCoverage, symbol, context);
                } catch (PortrayalException ex) {
                    context.getMonitor().exceptionOccured(ex, Level.WARNING);
                }
            }
        }
        return dataRendered;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(final RenderingContext context, final SearchArea mask, final VisitFilter filter, List<Graphic> graphics) {

        if(!(context instanceof RenderingContext2D) ) return graphics;
        if(!item.isSelectable())                     return graphics;
        if(!item.isVisible())                        return graphics;

        final RenderingContext2D renderingContext = (RenderingContext2D) context;

        GenericName coverageName = null;
        try {
            coverageName = item.getResource().getIdentifier();
        } catch (DataStoreException ex) {
            //do nothing
        }
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(item.getStyle(),
                renderingContext.getSEScale(), coverageName,null);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.length == 0) {
            return graphics;
        }

        if(graphics == null) graphics = new ArrayList<>();
        if(mask instanceof SearchAreaJ2D){
            graphics = searchAt(item,rules,renderingContext,(SearchAreaJ2D)mask,filter,graphics);
        }else{
            graphics = searchAt(item,rules,renderingContext,new DefaultSearchAreaJ2D(mask),filter,graphics);
        }


        return graphics;
    }

    private List<Graphic> searchAt(final MapLayer layer, final CachedRule[] rules,
            final RenderingContext2D renderingContext, final SearchAreaJ2D mask, final VisitFilter filter, List<Graphic> graphics) {
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

    @Override
    public void changeOccured(ChangeEvent event) {
        if(item.isVisible() && getCanvas().isAutoRepaint()){
            //TODO should call a repaint only on this graphic
            projectedCoverage.clearObjectiveCache();
            getCanvas().repaint();
        }
    }

    @Override
    public void dispose() {
        projectedCoverage.dispose();
        super.dispose();
    }

}
