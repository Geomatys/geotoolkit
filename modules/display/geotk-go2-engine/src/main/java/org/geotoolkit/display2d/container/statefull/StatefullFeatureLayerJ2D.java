/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2009, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.display2d.container.statefull;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;

import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.canvas.GO2Hints;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessFeatureLayerJ2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.GO2Utilities;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.GraphicBuilder;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import static org.geotoolkit.display2d.style.GO2Utilities.*;

/**
 * Single object to represent a complete mapcontext.
 * This is a Stateless graphic object.
 *
 * @author Johann Sorel
 */
public class StatefullFeatureLayerJ2D extends StatelessFeatureLayerJ2D{

    private final Map<String,StatefullProjectedFeature> cache = new HashMap<String, StatefullProjectedFeature>();

    //compare values to update cahces if necessary
    private final StatefullContextParams params;
    private final CoordinateReferenceSystem dataCRS;
    private CoordinateReferenceSystem lastObjectiveCRS = null;
    
    public StatefullFeatureLayerJ2D(ReferencedCanvas2D canvas, FeatureMapLayer layer){
        super(canvas, layer);
        params = new StatefullContextParams(layer);
        dataCRS = layer.getFeatureSource().getSchema().getCoordinateReferenceSystem();
    }

    private void updateCache(RenderingContext2D context){

        //clear objective cache is objective crs changed -----------------------
        final CoordinateReferenceSystem objectiveCRS = context.getObjectiveCRS();
        if(objectiveCRS != lastObjectiveCRS){
            params.objectiveToDisplay.setToIdentity();
            lastObjectiveCRS = objectiveCRS;

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

            try {
                params.dataToDisplayTransformer.setMathTransform(context.getMathTransform(dataCRS, context.getDisplayCRS()));
            } catch (FactoryException ex) {
                ex.printStackTrace();
            }

            Boolean generalize = (Boolean) context.getCanvas().getRenderingHint(GO2Hints.KEY_GENERALIZE);

            if(generalize == null || generalize == true){
                params.decimate = true;
                try {
                    final MathTransform trs = context.getMathTransform(context.getObjectiveCRS(), dataCRS);
                    DirectPosition vect = new DirectPosition2D(context.getResolution()[0], context.getResolution()[1]);
                    vect = trs.transform(vect, vect);
                    double[] decim = vect.getCoordinate();
                    params.decimation = (decim[0]<decim[1]) ? decim[0] : decim[1] ;
                    params.decimation = params.decimation * 1.3f;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    params.decimation = 0;
                }

                for(StatefullProjectedFeature gra : cache.values()){
                    //clear decimation at the data level
                    gra.clearDataCache();
                }
            }else{
                params.decimate = false;
                for(StatefullProjectedFeature gra : cache.values()){
                    //clear decimation at the display level
                    gra.clearDisplayCache();
                }
            }
        }
    }

    @Override
    protected void paintVectorLayer(final List<CachedRule> rules, final RenderingContext2D context) {
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

        final FeatureSource<SimpleFeatureType, SimpleFeature> fs = layer.getFeatureSource();
        final FeatureType schema                                 = fs.getSchema();
        final String geomAttName                                 = schema.getGeometryDescriptor().getLocalName();
        BoundingBox bbox                                         = context.getPaintingObjectiveBounds();
        final CoordinateReferenceSystem bboxCRS                  = bbox.getCoordinateReferenceSystem();
        final CanvasMonitor monitor                              = context.getMonitor();
        final ReferencedEnvelope layerBounds                     = layer.getBounds();

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

            bbox = new ReferencedEnvelope(env);
        }

        Filter filter;
        if( ((BoundingBox)bbox).contains(layerBounds)){
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
        final DefaultQuery query = new DefaultQuery();
        query.setFilter(filter);
        query.setPropertyNames(atts);

        if(monitor.stopRequested()) return;

        final FeatureCollection<SimpleFeatureType,SimpleFeature> features;
        try{
            features = fs.getFeatures(query);
        }catch(IOException ex){
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

    private void renderByFeatureOrder(FeatureCollection<SimpleFeatureType,SimpleFeature> features,
            CanvasMonitor monitor, RenderingContext2D context, List<CachedRule> rules){
        // read & paint in the same thread, all symbolizer for each feature
        final FeatureIterator<SimpleFeature> iterator = features.features();
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

                for (final CachedRule rule : rules) {
                    final Filter rulefilter = rule.getFilter();
                    //test if the rule is valid for this feature
                    if (rulefilter == null || rulefilter.evaluate(feature)) {
                        final List<CachedSymbolizer> symbols = rule.symbolizers();
                        for (final CachedSymbolizer symbol : symbols) {
                            final SymbolizerRenderer renderer = GO2Utilities.findRenderer(symbol);
                            if(renderer != null){
                                renderer.portray(graphic, symbol, context);
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

    private void renderBySymbolOrder(FeatureCollection<SimpleFeatureType,SimpleFeature> features,
            CanvasMonitor monitor, RenderingContext2D context, List<CachedRule> rules){
        // read & paint in the same thread, all symbolizer for each feature
        final FeatureIterator<SimpleFeature> iterator = features.features();

        List<StatefullProjectedFeature> cycle = new ArrayList<StatefullProjectedFeature>();


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

        try{
            for (final CachedRule rule : rules) {
                final Filter rulefilter = rule.getFilter();
                final List<CachedSymbolizer> symbols = rule.symbolizers();
                for (final CachedSymbolizer symbol : symbols) {
                    for(StatefullProjectedFeature feature : cycle){
                        //test if the rule is valid for this feature
                        if (rulefilter == null || rulefilter.evaluate(feature.getFeature())) {
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
    protected List<Graphic> searchGraphicAt(final FeatureMapLayer layer, final List<CachedRule> rules,
            final RenderingContext2D context, final SearchArea mask, VisitFilter visitFilter, List<Graphic> graphics) {
        updateCache(context);

        final FeatureSource<SimpleFeatureType, SimpleFeature> fs = layer.getFeatureSource();
        final FeatureType schema                                 = fs.getSchema();
        final String geomAttName                                 = schema.getGeometryDescriptor().getLocalName();
        BoundingBox bbox                                         = context.getPaintingObjectiveBounds();
        final CoordinateReferenceSystem bboxCRS                  = bbox.getCoordinateReferenceSystem();
        final ReferencedEnvelope layerBounds                     = layer.getBounds();

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

            bbox = new ReferencedEnvelope(env);
        }

        //efficient but doesnt take in count the symbolizer size and width
//        try {
//            AffineTransform trs = new AffineTransform(context.getObjectiveToDisplay());
//            trs.invert();
//            Geometry bb = JTS.transform(mask, new AffineTransform2D(trs));
//            bb = JTS.transform(bb, CRS.findMathTransform(context.getObjectiveCRS(), layerBounds.getCoordinateReferenceSystem(),true));
//            bbox = new ReferencedEnvelope(JTS.toEnvelope(bb),layerBounds.getCoordinateReferenceSystem());
//        } catch (Exception ex) {
//            Logger.getLogger(StatefullFeatureLayerJ2D.class.getName()).log(Level.SEVERE, null, ex);
//        }


        Filter filter;
        if( ((BoundingBox)bbox).contains(layerBounds)){
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
        final DefaultQuery query = new DefaultQuery();
        query.setFilter(filter);
        query.setPropertyNames(atts);


        final FeatureCollection<SimpleFeatureType,SimpleFeature> features;
        try{
            features = fs.getFeatures(query);
        }catch(IOException ex){
            context.getMonitor().exceptionOccured(ex, Level.SEVERE);
            //can not continue this layer with this error
            return graphics;
        }

        //we check that we have features left after the user Query
        //if empty we stop this layer rendering
        if(features == null || features.isEmpty()) return graphics;

        // read & paint in the same thread
        final FeatureIterator<SimpleFeature> iterator = features.features();
        try{
            while(iterator.hasNext()){
                final SimpleFeature feature = iterator.next();

                //search in the cache
                final String id = feature.getID();
                StatefullProjectedFeature graphic = cache.get(id);

                if(graphic == null){
                    //not in cache, create it
                    graphic = new StatefullProjectedFeature(params, feature);
                    cache.put(id, graphic);
                }

                for (final CachedRule rule : rules) {
                    final Filter ruleFilter = rule.getFilter();
                    //test if the rule is valid for this feature
                    if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                        final List<CachedSymbolizer> symbols = rule.symbolizers();
                        for (final CachedSymbolizer symbol : symbols) {
                            if(GO2Utilities.hit(graphic, symbol, context, mask, visitFilter)){
                                graphics.add( graphic );
                                break;
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
