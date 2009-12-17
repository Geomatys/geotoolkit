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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessFeatureLayerJ2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.GraphicBuilder;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import static org.geotoolkit.display2d.GO2Utilities.*;

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
    private final Set<String> cachedAttributs = new HashSet<String>();
    
    public StatefullFeatureLayerJ2D(ReferencedCanvas2D canvas, FeatureMapLayer layer){
        super(canvas, layer);
        params = new StatefullContextParams(canvas,layer);
        dataCRS = layer.getCollection().getSchema().getCoordinateReferenceSystem();
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
    protected void paintVectorLayer(final CachedRule[] rules, final RenderingContext2D context) {
        updateCache(context);

        //search for a special graphic renderer---------------------------------
        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) layer.getGraphicBuilder(GraphicJ2D.class);
        if(builder != null){
            //this layer has a special graphic rendering, use it instead of normal rendering
            final Collection<GraphicJ2D> graphics = builder.createGraphics(layer, canvas);
            for(GraphicJ2D gra : graphics){
                gra.paint(context);
            }
            return;
        }

        final FeatureCollection<SimpleFeature> fs                = (FeatureCollection<SimpleFeature>) layer.getCollection();
        final FeatureType schema                                 = fs.getSchema();
        final String geomAttName                                 = schema.getGeometryDescriptor().getLocalName();
        BoundingBox bbox                                         = context.getPaintingObjectiveBounds();
        final CoordinateReferenceSystem bboxCRS                  = bbox.getCoordinateReferenceSystem();
        final CanvasMonitor monitor                              = context.getMonitor();
        final Envelope layerBounds                     = layer.getBounds();

        if( !CRS.equalsIgnoreMetadata(layerBounds.getCoordinateReferenceSystem(),bboxCRS)){
            //BBox and layer bounds have different CRS. reproject bbox bounds
            Envelope env;

            try{
                env = CRS.transform(bbox, layerBounds.getCoordinateReferenceSystem());
            }catch(TransformException ex){
                //TODO is fixed in geotidy, the result envelope will have infinte values where needed
                //TODO should do something about this, since canvas bounds may be over the crs bounds
                System.err.println("FeatureGraphicLayerJ2D ligne 150 :" +ex.getMessage());
//                renderingContext.getMonitor().exceptionOccured(ex, Level.SEVERE);
                env = new Envelope2D();
            }

            //TODO looks like the envelope after transform operation doesnt have always exactly the same CRS.
            //fix CRS classes method and remove the two next lines.
            env = new GeneralEnvelope(env);
            ((GeneralEnvelope)env).setCoordinateReferenceSystem(layerBounds.getCoordinateReferenceSystem());

            bbox = new DefaultBoundingBox(env);
        }

        Filter filter;
        if( ((BoundingBox)bbox).contains(new DefaultBoundingBox(layerBounds))){
            //the layer bounds in within the bbox, no need for a spatial filter
            filter = Filter.INCLUDE;
        }else{
            //make a bbox filter
            filter = FILTER_FACTORY.bbox(FILTER_FACTORY.property(geomAttName),bbox);
        }

        //concatenate geographique filter with data filter if there is one
        if(layer.getQuery() != null && layer.getQuery().getFilter() != null){
            filter = FILTER_FACTORY.and(filter,layer.getQuery().getFilter());
        }

        final Set<String> attributs = GO2Utilities.propertiesCachedNames(rules);
        final Set<String> copy = new HashSet<String>(attributs);
        copy.add(geomAttName);
        final String[] atts = copy.toArray(new String[copy.size()]);

        final QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(schema.getName());
        qb.setFilter(filter);
        qb.setProperties(atts);
        final Query query = qb.buildQuery();
        
        if(copy.size() != cachedAttributs.size() || !cachedAttributs.containsAll(copy)){
            //the attributs needed for styling have changed, the cache is obsolete
            clearCache();
            cachedAttributs.clear();
            cachedAttributs.addAll(copy);
        }


        if(monitor.stopRequested()) return;

        final FeatureCollection<SimpleFeature> features;
        try{
            features = fs.subCollection(query);
        }catch(DataStoreException ex){
            context.getMonitor().exceptionOccured(ex, Level.SEVERE);
            //can not continue this layer with this error
            return;
        }

        //we check that we have features left after the user Query
        //if empty we stop this layer rendering
        if(features == null || features.isEmpty()) return;

        final Boolean SymbolOrder = (Boolean) canvas.getRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER);

        if(SymbolOrder == null || SymbolOrder == false){
            renderByFeatureOrder(features, monitor, context, rules);
        }else{
            renderBySymbolOrder(features, monitor, context, rules);
        }
        
    }

    private void renderByFeatureOrder(FeatureCollection<SimpleFeature> features,
            CanvasMonitor monitor, RenderingContext2D context, CachedRule[] rules){
        // read & paint in the same thread, all symbolizer for each feature
        final FeatureIterator<SimpleFeature> iterator = features.iterator();

        //sort the rules
        final int elseRuleIndex = sortByElseRule(rules);

        try{
            while(iterator.hasNext()){
                if(monitor.stopRequested()) return;
                final SimpleFeature feature = iterator.next();

                //search in the cache
                final String id = feature.getID();
                StatefullProjectedFeature projectedFeature = cache.get(id);

                if(projectedFeature == null){
                    //not in cache, create it
                    projectedFeature = new StatefullProjectedFeature(params, feature);
                    cache.put(id, projectedFeature);
                }

                boolean painted = false;
                for(int i=0;i<elseRuleIndex;i++){
                    final CachedRule rule = rules[i];
                    final Filter ruleFilter = rule.getFilter();
                    //test if the rule is valid for this feature
                    if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                        painted = true;
                        for (final CachedSymbolizer symbol : rule.symbolizers()) {
                            final SymbolizerRenderer renderer = GO2Utilities.findRenderer(symbol);
                            if(renderer != null){
                                renderer.portray(projectedFeature, symbol, context);
                            }
                        }
                    }
                }

                //the feature hasn't been painted, paint it with the 'else' rules
                if(!painted){
                    for(int i=elseRuleIndex; i<rules.length; i++){
                        final CachedRule rule = rules[i];
                        final Filter ruleFilter = rule.getFilter();
                        //test if the rule is valid for this feature
                        if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                            for (final CachedSymbolizer symbol : rule.symbolizers()) {
                                final SymbolizerRenderer renderer = GO2Utilities.findRenderer(symbol);
                                if(renderer != null){
                                    renderer.portray(projectedFeature, symbol, context);
                                }
                            }
                        }
                    }
                }

            }
        }catch(PortrayalException ex){
            context.getMonitor().exceptionOccured(ex, Level.WARNING);
        }finally{
            iterator.close();
        }
    }

    private void renderBySymbolOrder(FeatureCollection<SimpleFeature> features,
            CanvasMonitor monitor, RenderingContext2D context, CachedRule[] rules){
        // read & paint in the same thread, all symbolizer for each feature
        final FeatureIterator<SimpleFeature> iterator = features.iterator();

        List<StatefullProjectedFeature> cycle = new ArrayList<StatefullProjectedFeature>();

        //sort the rules
        final int elseRuleIndex = sortByElseRule(rules);

        try{
            while(iterator.hasNext()){
                if(monitor.stopRequested()) return;
                final SimpleFeature feature = iterator.next();

                //search in the cache
                final String id = feature.getID();
                StatefullProjectedFeature graphic = cache.get(id);

                if(graphic == null){
                    //not in cache, create it
                    graphic = new StatefullProjectedFeature(params, feature);
                    cache.put(id, graphic);
                }

                cycle.add(graphic);
            }
        }finally{
            iterator.close();
        }

        List<StatefullProjectedFeature> unPainted = new ArrayList<StatefullProjectedFeature>(cycle);

        try{
           for(int i=0;i<elseRuleIndex;i++){
                final CachedRule rule = rules[i];
                final Filter rulefilter = rule.getFilter();
                for (final CachedSymbolizer symbol : rule.symbolizers()) {
                    for(StatefullProjectedFeature feature : cycle){
                        //test if the rule is valid for this feature
                        if (rulefilter == null || rulefilter.evaluate(feature.getFeature())) {
                            unPainted.remove(feature);
                            final SymbolizerRenderer renderer = GO2Utilities.findRenderer(symbol);
                            if(renderer != null){
                                renderer.portray(feature, symbol, context);
                            }
                        }
                    }
                }
            }

            for(int i=elseRuleIndex;i<rules.length;i++){
                final CachedRule rule = rules[i];
                final Filter rulefilter = rule.getFilter();
                for (final CachedSymbolizer symbol : rule.symbolizers()) {
                    for(StatefullProjectedFeature feature : unPainted){
                        //test if the rule is valid for this feature
                        if (rulefilter == null || rulefilter.evaluate(feature.getFeature())) {
                            unPainted.remove(feature);
                            final SymbolizerRenderer renderer = GO2Utilities.findRenderer(symbol);
                            if(renderer != null){
                                renderer.portray(feature, symbol, context);
                            }
                        }
                    }
                }
            }

        }catch(PortrayalException ex){
            context.getMonitor().exceptionOccured(ex, Level.WARNING);
        }

    }

    @Override
    protected List<Graphic> searchGraphicAt(final FeatureMapLayer layer, final CachedRule[] rules,
            final RenderingContext2D context, final SearchAreaJ2D mask, VisitFilter visitFilter, List<Graphic> graphics) {
        updateCache(context);

        final FeatureCollection<SimpleFeature> fs               = (FeatureCollection<SimpleFeature>) layer.getCollection();
        final FeatureType schema                                 = fs.getSchema();
        final String geomAttName                                 = schema.getGeometryDescriptor().getLocalName();
        BoundingBox bbox                                         = context.getPaintingObjectiveBounds();
        final CoordinateReferenceSystem bboxCRS                  = bbox.getCoordinateReferenceSystem();
        final Envelope layerBounds                               = layer.getBounds();

        if( !CRS.equalsIgnoreMetadata(layerBounds.getCoordinateReferenceSystem(),bboxCRS)){
            //BBox and layer bounds have different CRS. reproject bbox bounds
            Envelope env;

            try{
                env = CRS.transform(bbox, layerBounds.getCoordinateReferenceSystem());
            }catch(TransformException ex){
                //TODO is fixed in geotidy, the result envelope will have infinte values where needed
                //TODO should do something about this, since canvas bounds may be over the crs bounds
                System.err.println("StatefullFeatureGraphicLayerJ2D ligne 272 :" +ex.getMessage());
//                renderingContext.getMonitor().exceptionOccured(ex, Level.SEVERE);
                env = new Envelope2D();
            }

            //TODO looks like the envelope after transform operation doesnt have always exactly the same CRS.
            //fix CRS classes method and remove the two next lines.
            env = new GeneralEnvelope(env);
            ((GeneralEnvelope)env).setCoordinateReferenceSystem(layerBounds.getCoordinateReferenceSystem());

            bbox = new DefaultBoundingBox(env);
        }

        //efficient but doesnt take in count the symbolizer size and width
//        try {
//            AffineTransform trs = new AffineTransform(context.getObjectiveToDisplay());
//            trs.invert();
//            Geometry bb = JTS.transform(mask, new AffineTransform2D(trs));
//            bb = JTS.transform(bb, CRS.findMathTransform(context.getObjectiveCRS(), layerBounds.getCoordinateReferenceSystem(),true));
//            bbox = new DefaultBoundingBox(JTS.toEnvelope(bb),layerBounds.getCoordinateReferenceSystem());
//        } catch (Exception ex) {
//            Logging.getLogger(StatefullFeatureLayerJ2D.class).log(Level.SEVERE, null, ex);
//        }


        Filter filter;
        if(bbox.contains(new DefaultBoundingBox(layerBounds))){
            //the layer bounds in wihin the bbox, no need for a spatial filter
            filter = Filter.INCLUDE;
        }else{
            //make a bbox filter
            filter = FILTER_FACTORY.bbox(FILTER_FACTORY.property(geomAttName),bbox);
        }

        //concatenate geographique filter with data filter if there is one
        if(layer.getQuery() != null && layer.getQuery().getFilter() != null){
            filter = FILTER_FACTORY.and(filter,layer.getQuery().getFilter());
        }

        final Set<String> attributs = GO2Utilities.propertiesCachedNames(rules);
        final Set<String> copy = new HashSet<String>(attributs);
        copy.add(geomAttName);
        final String[] atts = copy.toArray(new String[copy.size()]);

        final QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(schema.getName());
        qb.setFilter(filter);
         qb.setProperties(atts);
        final Query query = qb.buildQuery();
        
        if(copy.size() != cachedAttributs.size() || !cachedAttributs.containsAll(copy)){
            //the attributs needed for styling have changed, the cache is obsolete
            clearCache();
            cachedAttributs.clear();
            cachedAttributs.addAll(copy);
        }


        final FeatureCollection<SimpleFeature> features;
        try{
            features = fs.subCollection(query);
        }catch(DataStoreException ex){
            context.getMonitor().exceptionOccured(ex, Level.SEVERE);
            //can not continue this layer with this error
            return graphics;
        }

        //we check that we have features left after the user Query
        //if empty we stop this layer rendering
        if(features == null || features.isEmpty()) return graphics;


        //sort the rules
        final int elseRuleIndex = sortByElseRule(rules);

        // read & paint in the same thread
        final FeatureIterator<SimpleFeature> iterator = features.iterator();
        try{
            while(iterator.hasNext()){
                final SimpleFeature feature = iterator.next();

                //search in the cache
                final String id = feature.getID();
                StatefullProjectedFeature projectedFeature = cache.get(id);

                if(projectedFeature == null){
                    //not in cache, create it
                    projectedFeature = new StatefullProjectedFeature(params, feature);
                    cache.put(id, projectedFeature);
                }

                boolean painted = false;
                for(int i=0;i<elseRuleIndex;i++){
                    final CachedRule rule = rules[i];
                    final Filter ruleFilter = rule.getFilter();
                    //test if the rule is valid for this feature
                    if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                        painted = true;
                        for (final CachedSymbolizer symbol : rule.symbolizers()) {
                            if(GO2Utilities.hit(projectedFeature, symbol, context, mask, visitFilter)){
                                if(feature != null) graphics.add( projectedFeature );
                                break;
                            }
                        }
                    }
                }

                //the feature hasn't been painted, paint it with the 'else' rules
                if(!painted){
                    for(int i=elseRuleIndex; i<rules.length; i++){
                        final CachedRule rule = rules[i];
                        final Filter ruleFilter = rule.getFilter();
                        //test if the rule is valid for this feature
                        if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                            for (final CachedSymbolizer symbol : rule.symbolizers()) {
                                if(GO2Utilities.hit(projectedFeature, symbol, context, mask, visitFilter)){
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

}
