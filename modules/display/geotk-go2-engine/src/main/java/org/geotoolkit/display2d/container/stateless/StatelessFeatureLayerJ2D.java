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
package org.geotoolkit.display2d.container.stateless;


import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.geotoolkit.data.query.DefaultQuery;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.DefaultGraphicFeatureJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.statefull.StatefullContextParams;
import org.geotoolkit.display2d.container.statefull.StatefullProjectedFeature;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.primitive.DefaultSearchAreaJ2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.data.FeatureSource;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.collection.FeatureIterator;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;

import static org.geotoolkit.display2d.GO2Utilities.*;

/**
 * Single object to represent a complete mapcontext.
 * This is a Stateless graphic object.
 *
 * @author johann sorel (Geomatys)
 * @module pending
 */
public class StatelessFeatureLayerJ2D extends AbstractLayerJ2D<FeatureMapLayer>{
    
    public StatelessFeatureLayerJ2D(ReferencedCanvas2D canvas, FeatureMapLayer layer){
        super(canvas, layer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void paint(final RenderingContext2D renderingContext) {
        
        //we abort painting if the layer is not visible.
        if (!layer.isVisible()) return;  

        final SimpleFeatureType sft = layer.getFeatureSource().getSchema();
        final CachedRule[] rules;

        final Filter selectionFilter = layer.getSelectionFilter();
        if(selectionFilter != null && !Filter.EXCLUDE.equals(selectionFilter)){
            final List<Rule> selectionRules;
            final List<Rule> normalRules = GO2Utilities.getValidRules(
                    layer.getStyle(), renderingContext.getGeographicScale(), sft);

            final List<CachedRule> mixedRules = new ArrayList<CachedRule>();
            final MutableStyle selectionStyle = layer.getSelectionStyle();
            if(selectionStyle == null){
                selectionRules = GO2Utilities.getValidRules(
                        ContextContainer2D.DEFAULT_SELECTION_STYLE, renderingContext.getScale(), sft);
            }else{
                selectionRules = GO2Utilities.getValidRules(
                        selectionStyle, renderingContext.getScale(), sft);
            }

            //update the rules filters
            for(final Rule rule : selectionRules){
                final MutableRule mixedRule = STYLE_FACTORY.rule(rule.symbolizers().toArray(new Symbolizer[0]));
                Filter f = rule.getFilter();
                if(f == null){
                    f = selectionFilter;
                }else{
                    f = FILTER_FACTORY.and(f,selectionFilter);
                }
                mixedRule.setFilter(f);
                mixedRules.add(GO2Utilities.getCached(mixedRule));
            }

            final Filter notSelectionFilter = FILTER_FACTORY.not(selectionFilter);

            for(final Rule rule : normalRules){
                final MutableRule mixedRule = STYLE_FACTORY.rule(rule.symbolizers().toArray(new Symbolizer[0]));
                Filter f = rule.getFilter();
                if(f == null){
                    f = notSelectionFilter;
                }else{
                    f = FILTER_FACTORY.and(f,notSelectionFilter);
                }
                mixedRule.setFilter(f);
                mixedRules.add(GO2Utilities.getCached(mixedRule));
            }

            rules = mixedRules.toArray(new CachedRule[mixedRules.size()]);;

        }else{
            rules = GO2Utilities.getValidCachedRules(
                layer.getStyle(), renderingContext.getScale(), sft);
        }

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if (rules.length == 0) {
            return;
        }
        
        paintVectorLayer(rules, renderingContext);
    }
    
    protected void paintVectorLayer(final CachedRule[] rules, final RenderingContext2D renderingContext) {
                
        //search for a special graphic renderer
        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) layer.getGraphicBuilder(GraphicJ2D.class);
        if(builder != null){
            //this layer has a special graphic rendering, use it instead of normal rendering
            final Collection<GraphicJ2D> graphics = builder.createGraphics(layer, canvas);
            for(GraphicJ2D gra : graphics){
                gra.paint(renderingContext);
            }
            return;
        }

        final FeatureSource<SimpleFeatureType, SimpleFeature> fs = layer.getFeatureSource();
        final FeatureType schema                                 = fs.getSchema();
        final String geomAttName                                 = schema.getGeometryDescriptor().getLocalName();
        BoundingBox bbox                                         = renderingContext.getPaintingObjectiveBounds();
        final CoordinateReferenceSystem bboxCRS                  = bbox.getCoordinateReferenceSystem();
        final CanvasMonitor monitor                              = renderingContext.getMonitor();
        final Envelope layerBounds                               = layer.getBounds();
        
        if( !CRS.equalsIgnoreMetadata(layerBounds.getCoordinateReferenceSystem(),bboxCRS)){
            //BBox and layer bounds have different CRS. reproject bbox bounds
            Envelope env;
            
            try{
                env = CRS.transform(bbox, layerBounds.getCoordinateReferenceSystem());
            }catch(TransformException ex){
                //TODO is fixed in geotidy, the result envelope will have infinte values where needed
                //TODO should do something about this, since canvas bounds may be over the crs bounds
                System.err.println("FeatureGraphicLayerJ2D ligne 191 :" +ex.getMessage());
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
            //the layer bounds overlaps the bbox, no need for a spatial filter
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
            renderingContext.getMonitor().exceptionOccured(ex, Level.SEVERE);
            //can not continue this layer with this error
            return;
        }
                
        //we check that we have features left after the user Query
        //if empty we stop this layer rendering
        if(features == null || features.isEmpty()) return;
        
        final CoordinateReferenceSystem dataCRS      = features.getSchema().getCoordinateReferenceSystem();
        final CoordinateReferenceSystem displayCRS   = renderingContext.getDisplayCRS();
        final CoordinateReferenceSystem objectiveCRS = renderingContext.getObjectiveCRS();
        final AffineTransform objtoDisp              = renderingContext.getObjectiveToDisplay();


        final StatefullContextParams params = new StatefullContextParams(getCanvas(),layer);
        final StatefullProjectedFeature projectedFeature = new StatefullProjectedFeature(params);
        params.displayCRS = displayCRS;
        params.objectiveToDisplay.setTransform(objtoDisp);
        params.updateGeneralizationFactor(renderingContext, dataCRS);
        try {
            params.dataToObjective = renderingContext.getMathTransform(dataCRS, objectiveCRS);
            params.dataToObjectiveTransformer.setMathTransform(params.dataToObjective);
            params.dataToDisplayTransformer.setMathTransform(renderingContext.getMathTransform(dataCRS,displayCRS));
        } catch (FactoryException ex) {
            monitor.exceptionOccured(ex, Level.SEVERE);
        }

        //sort the rules
        final int elseRuleIndex = sortByElseRule(rules);

        // read & paint in the same thread
        final FeatureIterator<SimpleFeature> iterator = features.features();
        try{
            while(iterator.hasNext()){
                if(monitor.stopRequested()) return;
                final SimpleFeature feature = iterator.next();
                projectedFeature.setFeature(feature);

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
                                renderer.portray(projectedFeature, symbol, renderingContext);
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
                                    renderer.portray(projectedFeature, symbol, renderingContext);
                                }
                            }
                        }
                    }
                }

            }
        }catch(PortrayalException ex){
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
        }finally{ 
            iterator.close();
        }
                
    }

    protected List<Graphic> searchGraphicAt(final FeatureMapLayer layer, final CachedRule[] rules,
            final RenderingContext2D renderingContext, final SearchAreaJ2D mask, VisitFilter visitFilter, List<Graphic> graphics) {

        final FeatureSource<SimpleFeatureType, SimpleFeature> fs = layer.getFeatureSource();
        final FeatureType schema                                 = fs.getSchema();
        final String geomAttName                                 = schema.getGeometryDescriptor().getLocalName();
        BoundingBox bbox                                         = renderingContext.getPaintingObjectiveBounds();
        final CoordinateReferenceSystem bboxCRS                  = bbox.getCoordinateReferenceSystem();
        final Envelope layerBounds                               = layer.getBounds();

        if( !CRS.equalsIgnoreMetadata(layerBounds.getCoordinateReferenceSystem(),bboxCRS)){
            //BBox and layer bounds have different CRS. reproject bbox bounds
            Envelope env;

            try{
                env = CRS.transform(bbox, layerBounds.getCoordinateReferenceSystem());
            }catch(TransformException ex){
                renderingContext.getMonitor().exceptionOccured(ex, Level.SEVERE);
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
            //the layer bounds overlaps the bbox, no need for a spatial filter
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
            renderingContext.getMonitor().exceptionOccured(ex, Level.SEVERE);
            //can not continue this layer with this error
            return graphics;
        }

        //we check that we have features left after the user Query
        //if empty we stop this layer rendering
        if(features == null || features.isEmpty()) return graphics;

        final CoordinateReferenceSystem dataCRS      = features.getSchema().getCoordinateReferenceSystem();
        final CoordinateReferenceSystem displayCRS   = renderingContext.getDisplayCRS();
        final CoordinateReferenceSystem objectiveCRS = renderingContext.getObjectiveCRS();
        final AffineTransform objtoDisp              = renderingContext.getObjectiveToDisplay();


        final StatefullContextParams params = new StatefullContextParams(getCanvas(),layer);
        final StatefullProjectedFeature projectedFeature = new StatefullProjectedFeature(params);
        params.displayCRS = displayCRS;
        params.objectiveToDisplay.setTransform(objtoDisp);
        params.updateGeneralizationFactor(renderingContext, dataCRS);
        try {
            params.dataToObjective = renderingContext.getMathTransform(dataCRS, objectiveCRS);
            params.dataToObjectiveTransformer.setMathTransform(params.dataToObjective);
            params.dataToDisplayTransformer.setMathTransform(renderingContext.getMathTransform(dataCRS,displayCRS));
        } catch (FactoryException ex) {
            ex.printStackTrace();
            return graphics;
        }


        // iterate and find the first graphic that hit the given point
        final FeatureIterator<SimpleFeature> iterator = features.features();
//        final StatelessProjectedFeature graphic = new StatelessProjectedFeature(getCanvas(), getCanvas().getObjectiveCRS());

//        try {
//            MathTransform dataToDisp = CRS.findMathTransform(dataCRS, displayCRS,true);
//            MathTransform dataToObj = CRS.findMathTransform(dataCRS, objectiveCRS,true);
//            graphic.initContext(dataToDisp, dataToObj);
//        } catch (FactoryException ex) {
//            ex.printStackTrace();
//            return graphics;
//        }


        //sort the rules
        final int elseRuleIndex = sortByElseRule(rules);


        try{
            while(iterator.hasNext()){
                SimpleFeature feature = iterator.next();
                projectedFeature.setFeature(feature);

                boolean painted = false;
                for(int i=0;i<elseRuleIndex;i++){
                    final CachedRule rule = rules[i];
                    final Filter ruleFilter = rule.getFilter();
                    //test if the rule is valid for this feature
                    if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                        painted = true;
                        for (final CachedSymbolizer symbol : rule.symbolizers()) {
                            if(GO2Utilities.hit(projectedFeature, symbol, renderingContext, mask, visitFilter)){
                                if(feature != null) graphics.add( new DefaultGraphicFeatureJ2D(getCanvas(), layer, feature) );
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
                                if(GO2Utilities.hit(projectedFeature, symbol, renderingContext, mask, visitFilter)){
                                    if(feature != null) graphics.add( new DefaultGraphicFeatureJ2D(getCanvas(), layer, feature) );
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

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(RenderingContext rdcontext, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        
        if(!layer.isSelectable()) return graphics;

        if(!(rdcontext instanceof RenderingContext2D)) return graphics;
        final RenderingContext2D c2d = (RenderingContext2D) rdcontext;

        //nothing visible so no possible selection
        if (!layer.isVisible()) return graphics;

        final Name featureTypeName = layer.getFeatureSource().getSchema().getName();
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(layer.getStyle(), c2d.getScale(), featureTypeName);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just return null.
        if (rules.length == 0) {
            return graphics;
        }

        if(graphics == null) graphics = new ArrayList<Graphic>();

        if(mask instanceof SearchAreaJ2D){
            return searchGraphicAt(layer, rules, c2d, (SearchAreaJ2D)mask, filter, graphics);
        }else{
            return searchGraphicAt(layer, rules, c2d, new DefaultSearchAreaJ2D(mask), filter, graphics);
        }
    }

    /**
     * sort the rules, isolate the else rules, they must be handle differently
     */
    protected int sortByElseRule(CachedRule[] sortedRules){
        int elseRuleIndex = sortedRules.length;

        for(int i=0; i<elseRuleIndex; i++){
            CachedRule r =sortedRules[i];
            if(r.getSource().isElseFilter()){
                elseRuleIndex--;

                for(int j=i+1;j<sortedRules.length;j++){
                    sortedRules[j-1] = sortedRules[j];
                }

                //move the rule at the end
                sortedRules[sortedRules.length-1] = r;
            }
        }

        return elseRuleIndex;
    }

}
