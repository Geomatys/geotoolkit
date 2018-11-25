/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.SearchArea;
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
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.GraphicBuilder;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.map.ElevationModel;
import org.geotoolkit.map.ItemListener;
import org.geotoolkit.map.LayerListener;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.style.MutableStyle;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;
import org.opengis.util.GenericName;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.Description;
import org.geotoolkit.storage.coverage.CollectionCoverageResource;
import org.geotoolkit.storage.coverage.GridCoverageResource;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class StatelessCollectionCoverageLayerJ2D extends StatelessMapLayerJ2D<CoverageMapLayer> implements ChangeListener<ChangeEvent>{

    protected StorageListener.Weak weakStoreListener = new StorageListener.Weak(this);

    private final ProjectedCoverage projectedCoverage;
    private final boolean ignoreBuilders;

    //compare values to update caches if necessary
    private final StatelessContextParams params;
    private CoordinateReferenceSystem lastObjectiveCRS = null;

    public StatelessCollectionCoverageLayerJ2D(final J2DCanvas canvas, final CoverageMapLayer layer){
        this(canvas,layer,false);
    }

    public StatelessCollectionCoverageLayerJ2D(final J2DCanvas canvas, final CoverageMapLayer layer, final boolean ignoreBuilders){
        super(canvas, layer, false);
        this.ignoreBuilders = ignoreBuilders;
        this.params = new StatelessContextParams(canvas,null);
        this.projectedCoverage = new ProjectedCoverage(params, layer);
        this.weakStoreListener.registerSource(layer.getCoverageReference());
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

        final GenericName coverageName = item.getCoverageReference().getIdentifier();
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(item.getStyle(),
                renderingContext.getSEScale(), coverageName,null);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.length == 0) {
            return false;
        }

        boolean dataRendered = false;
        final CollectionCoverageResource ref = (CollectionCoverageResource) item.getCoverageReference();
        final Collection<GridCoverageResource> references = ref.getCoverages(null);
        final LoopLayer layer = new LoopLayer();
        for (GridCoverageResource cref : references) {
            layer.ref = cref;
            dataRendered |= paintRaster(layer, rules, renderingContext);
        }
        return dataRendered;
    }

    private boolean paintRaster(final CoverageMapLayer item, final CachedRule[] rules,
            final RenderingContext2D context) {
        updateCache(context);

        boolean dataRendered = false;
        //search for a special graphic renderer
        if(!ignoreBuilders){
            final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) item.getGraphicBuilder(GraphicJ2D.class);
            if(builder != null){
                //this layer has a special graphic rendering, use it instead of normal rendering
                final Collection<GraphicJ2D> graphics = builder.createGraphics(item, getCanvas());
                for(GraphicJ2D gra : graphics){
                    dataRendered |= gra.paint(context);
                }
                return dataRendered;
            }
        }

        final ProjectedCoverage projectedCoverage = new ProjectedCoverage(params, item);
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

        if (!(context instanceof RenderingContext2D)) return graphics;
        if (!item.isSelectable())                     return graphics;
        if (!item.isVisible())                        return graphics;

        final RenderingContext2D renderingContext = (RenderingContext2D) context;

        final GenericName coverageName = item.getCoverageReference().getIdentifier();
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(item.getStyle(),
                renderingContext.getSEScale(), coverageName,null);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.length == 0) {
            return graphics;
        }

        if (graphics == null) graphics = new ArrayList<>();
        if (mask instanceof SearchAreaJ2D) {
            graphics = searchAt(item,rules,renderingContext,(SearchAreaJ2D)mask,filter,graphics);
        } else {
            graphics = searchAt(item,rules,renderingContext,new DefaultSearchAreaJ2D(mask),filter,graphics);
        }
        return graphics;
    }

    private List<Graphic> searchAt(final CoverageMapLayer layer, final CachedRule[] rules,
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

    /**
     * Fake coverage layer to avoid the expensive instanciation.
     */
    private class LoopLayer implements CoverageMapLayer {

        private GridCoverageResource ref;

        @Override
        public GridCoverageResource getCoverageReference() {
            return ref;
        }

        @Override
        public boolean isWellKnownedType() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public MutableStyle getStyle() {
            return item.getStyle();
        }

        @Override
        public void setStyle(MutableStyle style) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public double getOpacity() {
            return item.getOpacity();
        }

        @Override
        public void setOpacity(double opacity) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isSelectable() {
            return item.isSelectable();
        }

        @Override
        public void setSelectable(boolean selectable) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public MutableStyle getSelectionStyle() {
            return item.getSelectionStyle();
        }

        @Override
        public void setSelectionStyle(MutableStyle style) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Envelope getBounds() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ElevationModel getElevationModel() {
            return item.getElevationModel();
        }

        @Override
        public void setElevationModel(ElevationModel model) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public List<GraphicBuilder> graphicBuilders() {
            return item.graphicBuilders();
        }

        @Override
        public <T extends Graphic> GraphicBuilder<? extends T> getGraphicBuilder(Class<T> type) {
            return item.getGraphicBuilder(type);
        }

        @Override
        public void addLayerListener(LayerListener listener) {
        }

        @Override
        public void removeLayerListener(LayerListener listener) {
        }

        @Override
        public void setName(String name) {
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setDescription(Description desc) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Description getDescription() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isVisible() {
            return item.isVisible();
        }

        @Override
        public void setVisible(boolean visible) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public List<MapItem> items() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setUserProperty(String key, Object value) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Object getUserProperty(String key) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Map<String, Object> getUserProperties() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void addItemListener(ItemListener listener) {
        }

        @Override
        public void removeItemListener(ItemListener listener) {
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
        }


    }

}
