/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessFeatureLayerJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.map.FeatureMapLayer;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.Feature;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Single object to represent a complete mapcontext.
 * This is a Stateless graphic object.
 *
 * @author Johann Sorel
 * @module pending
 */
public class StatefullFeatureLayerJ2D extends StatelessFeatureLayerJ2D{

    private final Map<String,StatefullProjectedFeature> cache = new HashMap<String, StatefullProjectedFeature>();

    //compare values to update caches if necessary
    private final StatefullContextParams params;
    private final CoordinateReferenceSystem dataCRS;
    private CoordinateReferenceSystem lastObjectiveCRS = null;

    //List of attributs currently in the cached features
    //the cache must be cleared when the content of the style attributs needed changes
    private Name[] cachedAttributs = null;
    
    public StatefullFeatureLayerJ2D(ReferencedCanvas2D canvas, FeatureMapLayer layer){
        super(canvas, layer);
        params = new StatefullContextParams(canvas,layer);
        dataCRS = layer.getCollection().getFeatureType().getCoordinateReferenceSystem();
    }

    private synchronized void updateCache(RenderingContext2D context){

        boolean objectiveCleared = false;

        //clear objective cache is objective crs changed -----------------------
        //we work against the 2D crs for features
        //todo we should work against the ND crs when datastore will handle more than 2D.
        final CoordinateReferenceSystem objectiveCRS = context.getObjectiveCRS2D();
        if(objectiveCRS != lastObjectiveCRS){
            //change the aff value to force it's refresh
            params.objectiveToDisplay.setTransform(2, 0, 0, 2, 0, 0);
            lastObjectiveCRS = objectiveCRS;
            objectiveCleared = true;

            try {
                params.dataToObjective = context.getMathTransform(dataCRS, objectiveCRS);
                params.dataToObjectiveTransformer.setMathTransform(params.dataToObjective);
            } catch (FactoryException ex) {
                Logger.getLogger(StatefullFeatureLayerJ2D.class.getName()).log(Level.WARNING, null, ex);
            }

            for(StatefullProjectedFeature gra : cache.values()){
                gra.clearObjectiveCache();
            }

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
                for(StatefullProjectedFeature gra : cache.values()){
                    gra.clearDisplayCache();
                }
            }

        }
    }

    private synchronized void clearCache(){
        cache.clear();
    }

    @Override
    protected RenderingIterator getIterator(FeatureCollection<? extends Feature> features,
            RenderingContext2D renderingContext, StatefullContextParams params){
        updateCache(renderingContext);
        return new StatefullGraphicIterator(features.iterator());
    }

    @Override
    protected void paintVectorLayer(final CachedRule[] rules, final RenderingContext2D context) {
        updateCache(context);

        final CanvasMonitor monitor = context.getMonitor();
        final Query query = prepareQuery(context, layer, rules);

        final Name[] copy = query.getPropertyNames();
        if(!Arrays.deepEquals(copy, cachedAttributs)){
            //the attributs needed for styling have changed, the cache is obsolete
            clearCache();
            if(copy == null){
                cachedAttributs = null;
            }else{
                cachedAttributs = copy.clone();
            }
        }

        if(monitor.stopRequested()) return;

        final FeatureCollection<? extends Feature> features;
        try{
            features = layer.getCollection().subCollection(query);
        }catch(DataStoreException ex){
            context.getMonitor().exceptionOccured(ex, Level.WARNING);
            //can not continue this layer with this error
            return;
        }

        //we do not check if the collection is empty or not since
        //it can be a very expensive operation

        //prepare the renderers
        final StatefullCachedRule preparedRenderers = new StatefullCachedRule(rules, context);

        final Boolean SymbolOrder = (Boolean) canvas.getRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER);
        if(SymbolOrder == null || SymbolOrder == false){
            try{
                renderByFeatureOrder(features, context, preparedRenderers, params);
            }catch(PortrayalException ex){
                monitor.exceptionOccured(ex, Level.WARNING);
            }
        }else{
            try{
                renderBySymbolOrder(features, context, preparedRenderers, params);
            }catch(PortrayalException ex){
                monitor.exceptionOccured(ex, Level.WARNING);
            }
        }
    }

    @Override
    protected List<Graphic> searchGraphicAt(final FeatureMapLayer layer, final CachedRule[] rules,
            final RenderingContext2D context, final SearchAreaJ2D mask, VisitFilter visitFilter, List<Graphic> graphics) {
        updateCache(context);

        final Query query = prepareQuery(context, layer, rules);
        
        final Name[] copy = query.getPropertyNames();
        if(!Arrays.deepEquals(copy, cachedAttributs)){
            //the attributs needed for styling have changed, the cache is obsolete
            clearCache();
            if(copy == null){
                cachedAttributs = null;
            }else{
                cachedAttributs = copy.clone();
            }
        }


        final FeatureCollection<? extends Feature> features;
        try{
            features = layer.getCollection().subCollection(query);
        }catch(DataStoreException ex){
            context.getMonitor().exceptionOccured(ex, Level.WARNING);
            //can not continue this layer with this error
            return graphics;
        }

        //we do not check if the collection is empty or not since
        //it can be a very expensive operation


        //prepare the renderers
        final StatefullCachedRule preparedRenderers = new StatefullCachedRule(rules, context);

        // read & paint in the same thread
        final FeatureIterator<? extends Feature> iterator = features.iterator();
        try{
            while(iterator.hasNext()){
                final Feature feature = iterator.next();

                //search in the cache
                final String id = feature.getIdentifier().getID();
                StatefullProjectedFeature projectedFeature = cache.get(id);

                if(projectedFeature == null){
                    //not in cache, create it
                    projectedFeature = new StatefullProjectedFeature(params, feature);
                    cache.put(id, projectedFeature);
                }

                boolean painted = false;
                for(int i=0;i<preparedRenderers.elseRuleIndex;i++){
                    final CachedRule rule = preparedRenderers.rules[i];
                    final Filter ruleFilter = rule.getFilter();
                    //test if the rule is valid for this feature
                    if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                        painted = true;
                        for(SymbolizerRenderer renderer : preparedRenderers.renderers[i]){
                            if(renderer.hit(projectedFeature, mask, visitFilter)){
                                if(feature != null) graphics.add( projectedFeature );
                                break;
                            }
                        }
                    }
                }

                //the feature hasn't been painted, paint it with the 'else' rules
                if(!painted){
                    for(int i=preparedRenderers.elseRuleIndex; i<rules.length; i++){
                        final CachedRule rule = preparedRenderers.rules[i];
                        final Filter ruleFilter = rule.getFilter();
                        //test if the rule is valid for this feature
                        if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                            for(SymbolizerRenderer renderer : preparedRenderers.renderers[i]){
                                if(renderer.hit(projectedFeature, mask, visitFilter)){
                                    if(feature != null) graphics.add( projectedFeature );
                                    break;
                                }
                            }
                        }
                    }
                }

            }
        }finally{
            iterator.close();
        }

        return graphics;
    }

    private class StatefullGraphicIterator implements RenderingIterator{

        private final FeatureIterator<? extends Feature> ite;

        public StatefullGraphicIterator(FeatureIterator<? extends Feature> ite) {
            this.ite = ite;
        }

        @Override
        public boolean hasNext() {
            return ite.hasNext();
        }

        @Override
        public ProjectedFeature next() {
            //search in the cache
            final Feature feature = ite.next();
            final String id = feature.getIdentifier().getID();
            StatefullProjectedFeature graphic = cache.get(id);

            if(graphic == null){
                //not in cache, create it
                graphic = new StatefullProjectedFeature(params, feature);
                cache.put(id, graphic);
            }
            
            return graphic;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void close() throws IOException {
            ite.close();
        }

    }

}
