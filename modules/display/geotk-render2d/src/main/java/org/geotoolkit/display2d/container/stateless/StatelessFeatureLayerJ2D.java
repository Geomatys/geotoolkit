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

import java.awt.RenderingHints;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.FeatureTypeExt;
import org.geotoolkit.feature.ViewFeatureType;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStoreContentEvent;
import org.geotoolkit.data.FeatureStoreListener;
import org.geotoolkit.data.FeatureStoreManagementEvent;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.memory.GenericCachedFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import static org.geotoolkit.display2d.GO2Utilities.*;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessCollectionLayerJ2D.RenderingIterator;
import org.geotoolkit.display2d.primitive.DefaultSearchAreaJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.opengis.util.GenericName;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.filter.DefaultPropertyName;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.filter.binaryspatial.LooseBBox;
import org.geotoolkit.filter.binaryspatial.UnreprojectedLooseBBox;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.StyleUtilities;
import org.opengis.display.primitive.Graphic;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.util.Utilities;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;

/**
 * Single object to represent a complete feature map layer.
 * This is a Stateless graphic object.
 *
 * @author johann sorel (Geomatys)
 * @module
 */
public class StatelessFeatureLayerJ2D extends StatelessCollectionLayerJ2D<FeatureMapLayer> implements FeatureStoreListener{

    protected FeatureStoreListener.Weak weakSessionListener = new FeatureStoreListener.Weak(this);

    protected Query currentQuery = null;
    // symbols margins, in objective CRS units, used to expand query and intersection enveloppes.
    private double symbolsMargin = 0.0;


    public StatelessFeatureLayerJ2D(final J2DCanvas canvas, final FeatureMapLayer layer){
        super(canvas, layer);
        final Session session = layer.getCollection().getSession();
        weakSessionListener.registerSource(session);
    }

    @Override
    public void structureChanged(FeatureStoreManagementEvent event) {
        if(item.isVisible() && getCanvas().isAutoRepaint()){
            //TODO should call a repaint only on this graphic
            getCanvas().repaint();
        }
    }

    @Override
    public void contentChanged(FeatureStoreContentEvent event) {
        if(item.isVisible() && getCanvas().isAutoRepaint()){
            //TODO should call a repaint only on this graphic
            getCanvas().repaint();
        }
    }

    @Override
    protected StatelessContextParams getStatefullParameters(final RenderingContext2D context){
        params.update(context);
        //expand the search area by the maximum symbol size
        if(symbolsMargin>0 && params.objectiveJTSEnvelope!=null){
            params.objectiveJTSEnvelope = new com.vividsolutions.jts.geom.Envelope(params.objectiveJTSEnvelope);
            params.objectiveJTSEnvelope.expandBy(symbolsMargin);
        }
        return params;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void paintLayer(final RenderingContext2D renderingContext) {

        //search for a special graphic renderer
        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) item.getGraphicBuilder(GraphicJ2D.class);
        if(builder != null){
            //let the parent class handle it
            super.paintLayer(renderingContext);
            return;
        }

        if(Boolean.TRUE.equals(item.getUserProperty(MapLayer.USERKEY_STYLED_FEATURE))){
            //feature have self defined styles.
            renderStyledFeature(renderingContext);
            return;
        }

        //first extract the valid rules at this scale
        final List<Rule> validRules = getValidRules(renderingContext,item,item.getCollection().getFeatureType());

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if(validRules.isEmpty()){
            return;
        }

        //extract the used names
        Set<String> names = propertiesNames(validRules);
        if(names.contains("*")){
            //we need all properties
            names = null;
        }

        //calculate max symbol size, to expand search envelope.
        symbolsMargin = 0.0;
        for (Rule rule : validRules) {
            for (Symbolizer s : rule.symbolizers()) {
                final CachedSymbolizer cs = GO2Utilities.getCached(s, null);
                symbolsMargin = Math.max(symbolsMargin, cs.getMargin(null, renderingContext));
            }
        }
        if (Double.isNaN(symbolsMargin) || Double.isInfinite(symbolsMargin)) {
            //symbol margin can not be pre calculated, expect a max of 300pixels
            symbolsMargin = 300f;
        }
        if (symbolsMargin > 0) {
            final double scale = XAffineTransform.getScale(renderingContext.getDisplayToObjective());
            symbolsMargin = scale * symbolsMargin;
        }


        final FeatureCollection candidates;
        try {
            //optimize
            candidates = (FeatureCollection)optimizeCollection(renderingContext, names, validRules);
        } catch (Exception ex) {
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        }

        //get the expected result type
        final FeatureType expected = candidates.getFeatureType();

        //calculate optimized rules and included filter + expressions
        final CachedRule[] rules = toCachedRules(validRules, expected);

        paintVectorLayer(rules, candidates, renderingContext);
    }

    /**
     * Render styled features.
     *
     * @param context
     */
    private void renderStyledFeature(final RenderingContext2D context){

        final CanvasMonitor monitor = context.getMonitor();
        final FeatureCollection candidates;
        try {
            candidates = (FeatureCollection)optimizeCollection(context);
        } catch (Exception ex) {
            context.getMonitor().exceptionOccured(ex, Level.WARNING);
            return;
        }
        final RenderingIterator statefullIterator = getIterator(candidates, context, getStatefullParameters(context));

        //prepare the rendering parameters
        if(monitor.stopRequested()) return;

        try{
            while(statefullIterator.hasNext()){
                if(monitor.stopRequested()) return;
                final ProjectedObject projectedCandidate = statefullIterator.next();
                final Feature feature = (Feature) projectedCandidate.getCandidate();

                final List<Symbolizer> symbolizers;
                try{
                    symbolizers = (List<Symbolizer>) feature.getPropertyValue(FeatureExt.ATTRIBUTE_SYMBOLIZERS.toString());
                }catch(PropertyNotFoundException ex){
                    continue;
                }
                if(symbolizers==null) continue;
                for(Symbolizer symbolizer : symbolizers){
                    final SymbolizerRendererService srs = GO2Utilities.findRenderer(symbolizer.getClass());
                    final CachedSymbolizer cs = srs.createCachedSymbolizer(symbolizer);
                    final SymbolizerRenderer sr = srs.createRenderer(cs, context);
                    try {
                        sr.portray(projectedCandidate);
                    } catch (PortrayalException ex) {
                        monitor.exceptionOccured(ex, Level.WARNING);
                    }
                }
            }
        }finally{
            try {
                statefullIterator.close();
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, null, ex);
            }
        }

    }

    @Override
    protected Collection<?> optimizeCollection(final RenderingContext2D context,
            final Set<String> requieredAtts, final List<Rule> rules) throws Exception {
        currentQuery = prepareQuery(context, item, requieredAtts, rules, symbolsMargin);
        //we detach feature since we are going to use a cache.
        currentQuery.getHints().put(HintsPending.FEATURE_DETACHED,Boolean.TRUE);
        final Query query = currentQuery;
        FeatureCollection col = ((FeatureCollection)item.getCollection()).subCollection(query);
        col = GenericCachedFeatureIterator.wrap(col, 1000);
        return col;
    }

    protected Collection<?> optimizeCollection(final RenderingContext2D context) throws Exception {
        currentQuery = prepareQuery(context, item, symbolsMargin);
        //we detach feature since we are going to use a cache.
        currentQuery.getHints().put(HintsPending.FEATURE_DETACHED,Boolean.TRUE);
        final Query query = currentQuery;
        FeatureCollection col = ((FeatureCollection)item.getCollection()).subCollection(query);
        col = GenericCachedFeatureIterator.wrap(col, 1000);
        return col;
    }


    @Override
    protected FeatureId id(Object candidate) {
        return FeatureExt.getId((Feature)candidate);
    }

    @Override
    protected RenderingIterator getIterator(final Collection<?> features,
            final RenderingContext2D renderingContext, final StatelessContextParams params) {
        final Hints iteHints = new Hints(HintsPending.FEATURE_DETACHED, Boolean.FALSE);
        final FeatureIterator iterator = ((FeatureCollection)features).iterator(iteHints);
        final ProjectedFeature projectedFeature = new ProjectedFeature(params);
        return new GraphicIterator(iterator, projectedFeature);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(final RenderingContext rdcontext, final SearchArea mask, final VisitFilter filter, List<Graphic> graphics) {

        if(!item.isSelectable()) return graphics;

        if(!(rdcontext instanceof RenderingContext2D)) return graphics;
        final RenderingContext2D c2d = (RenderingContext2D) rdcontext;

        //nothing visible so no possible selection
        if (!item.isVisible()) return graphics;

        final GenericName featureTypeName = item.getCollection().getFeatureType().getName();
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(item.getStyle(),
                c2d.getSEScale(), featureTypeName,null);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just return null.
        if (rules.length == 0) {
            return graphics;
        }

        if(graphics == null) graphics = new ArrayList<>();

        if(mask instanceof SearchAreaJ2D){
            return searchGraphicAt(item, rules, c2d, (SearchAreaJ2D)mask, filter, graphics);
        }else{
            return searchGraphicAt(item, rules, c2d, new DefaultSearchAreaJ2D(mask), filter, graphics);
        }
    }

    protected List<Graphic> searchGraphicAt(final FeatureMapLayer layer, final CachedRule[] rules,
            final RenderingContext2D renderingContext, final SearchAreaJ2D mask, final VisitFilter visitFilter, final List<Graphic> graphics) {

        final Query query;
        try {
            final Set<String> attributs = GO2Utilities.propertiesCachedNames(rules);
            //add identifier property
            final FeatureType type = getUserObject().getCollection().getFeatureType();
            try{
                type.getProperty(AttributeConvention.IDENTIFIER_PROPERTY.toString());
                attributs.add(AttributeConvention.IDENTIFIER_PROPERTY.toString());
            }catch(PropertyNotFoundException ex){}
            query = prepareQuery(renderingContext, layer, attributs, null, symbolsMargin);
        } catch (PortrayalException ex) {
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
            return graphics;
        }

        final FeatureCollection features;
        try{
            features = ((FeatureCollection)layer.getCollection()).subCollection(query);
        }catch(DataStoreException ex){
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
            //can not continue this layer with this error
            return graphics;
        }

        //we do not check if the collection is empty or not since
        //it can be a very expensive operation

        final StatelessContextParams params = getStatefullParameters(renderingContext);


        // iterate and find the first graphic that hit the given point
        final FeatureIterator iterator;
        try{
            iterator = features.iterator();
        }catch(FeatureStoreRuntimeException ex){
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
            return graphics;
        }

        //prepare the renderers
        final DefaultCachedRule preparedRenderers = new DefaultCachedRule(rules, renderingContext);

        final ProjectedFeature projectedFeature = new ProjectedFeature(params);
        try{
            while(iterator.hasNext()){
                Feature feature = iterator.next();
                projectedFeature.setCandidate(feature);

                boolean painted = false;
                for(int i=0;i<preparedRenderers.elseRuleIndex;i++){
                    final CachedRule rule = preparedRenderers.rules[i];
                    final Filter ruleFilter = rule.getFilter();
                    //test if the rule is valid for this feature
                    if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                        painted = true;
                        for(SymbolizerRenderer renderer : preparedRenderers.renderers[i]){
                            if(renderer.hit(projectedFeature, mask, visitFilter)){
                                if(feature != null) graphics.add( new ProjectedFeature(getCanvas(), layer, feature) );
                                break;
                            }
                        }
                    }
                }

                //the feature hasn't been painted, paint it with the 'else' rules
                if(!painted){
                    for(int i=preparedRenderers.elseRuleIndex; i<preparedRenderers.rules.length; i++){
                        final CachedRule rule = preparedRenderers.rules[i];
                        final Filter ruleFilter = rule.getFilter();
                        //test if the rule is valid for this feature
                        if (ruleFilter == null || ruleFilter.evaluate(feature)) {
                            for(SymbolizerRenderer renderer : preparedRenderers.renderers[i]){
                                if(renderer.hit(projectedFeature, mask, visitFilter)){
                                    if(feature != null) graphics.add( new ProjectedFeature(getCanvas(), layer, feature) );
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
     * Creates an optimal query to send to the datastore, knowing which properties are knowned and
     * the appropriate bounding box to filter.
     */
    protected static Query prepareQuery(final RenderingContext2D renderingContext, final FeatureMapLayer layer,
            final Set<String> styleRequieredAtts, final List<Rule> rules, double symbolsMargin) throws PortrayalException{

        final FeatureCollection fs               = layer.getCollection();
        final FeatureType schema                 = fs.getFeatureType();
        PropertyType geomDesc              = null;
        try {
            geomDesc = schema.getProperty(AttributeConvention.GEOMETRY_PROPERTY.toString());
        } catch(PropertyNotFoundException ex){};
        final BoundingBox bbox                   = optimizeBBox(renderingContext, layer, symbolsMargin);
        final CoordinateReferenceSystem layerCRS = FeatureExt.getCRS(schema);
        final RenderingHints hints               = renderingContext.getRenderingHints();

        //search used geometries
        boolean allDefined = true;
        final Set<String> geomProperties = new HashSet<>();
        if(rules!=null){
            for(Rule r : rules){
                for(Symbolizer s : r.symbolizers()){
                    final Expression expGeom = s.getGeometry();
                    if(expGeom instanceof PropertyName){
                        geomProperties.add( ((PropertyName)expGeom).getPropertyName() );
                    }else{
                        allDefined = false;
                    }
                }
            }
        }else{
            allDefined = false;
        }
        if(geomDesc!=null && !allDefined){
            geomProperties.add(geomDesc.getName().toString());
        }

        Filter filter;

        //final Envelope layerBounds = layer.getBounds();
        //we better not do any call to the layer bounding box before since it can be
        //really expensive, the featurestore is the best placed to check if he might
        //optimize the filter.
        //if( ((BoundingBox)bbox).contains(new DefaultBoundingBox(layerBounds))){
            //the layer bounds overlaps the bbox, no need for a spatial filter
        //   filter = Filter.INCLUDE;
        //}else{
        //make a bbox filter
        if(!geomProperties.isEmpty()){
            if(geomProperties.size()==1){
                final String geomAttName = geomProperties.iterator().next();
                if (layerCRS != null) {
                    filter = new UnreprojectedLooseBBox(FILTER_FACTORY.property(geomAttName),new DefaultLiteral<>(bbox));
                } else {
                    filter = new LooseBBox(FILTER_FACTORY.property(geomAttName),new DefaultLiteral<>(bbox));
                }
            }else{
                //make an OR filter with all geometries
                final List<Filter> geomFilters = new ArrayList<>();
                for(String geomAttName : geomProperties){
                    geomFilters.add(new LooseBBox(FILTER_FACTORY.property(geomAttName),new DefaultLiteral<>(bbox)));
                }
                filter = FILTER_FACTORY.or(geomFilters);
            }

        }else{
            filter = Filter.EXCLUDE;
        }
        //}

        //concatenate geographic filter with data filter if there is one
        if(layer.getQuery() != null && layer.getQuery().getFilter() != null){
            filter = FILTER_FACTORY.and(filter,layer.getQuery().getFilter());
        }

        final Set<String> copy = new HashSet<>();

        //concatenate with temporal range if needed ----------------------------
        for (final FeatureMapLayer.DimensionDef def : layer.getExtraDimensions()) {
            final CoordinateReferenceSystem crs = def.getCrs();
            final Envelope canvasEnv = renderingContext.getCanvasObjectiveBounds();
            final Envelope dimEnv;
            try {
                dimEnv = Envelopes.transform(canvasEnv, crs);
            } catch (TransformException ex) {
                continue;
            }

            Object min = dimEnv.getMinimum(0);
            Object max = dimEnv.getMaximum(0);
            if(crs instanceof DefaultTemporalCRS){
                min = ((DefaultTemporalCRS)crs).toDate((Double)min);
                max = ((DefaultTemporalCRS)crs).toDate((Double)max);
            }

            final Filter dimFilter = FILTER_FACTORY.and(
                    FILTER_FACTORY.or(
                            FILTER_FACTORY.isNull(def.getLower()),
                            FILTER_FACTORY.lessOrEqual(def.getLower(), FILTER_FACTORY.literal(max) )),
                    FILTER_FACTORY.or(
                            FILTER_FACTORY.isNull(def.getUpper()),
                            FILTER_FACTORY.greaterOrEqual(def.getUpper(), FILTER_FACTORY.literal(min) ))
            );

            filter = FILTER_FACTORY.and(filter, dimFilter);

            //add extra dimension property name on attributes list for retype.
            if (def.getLower() instanceof DefaultPropertyName) {
                copy.add(((DefaultPropertyName)def.getLower()).getPropertyName());
            }

            if (def.getUpper() instanceof DefaultPropertyName) {
                copy.add(((DefaultPropertyName)def.getUpper()).getPropertyName());
            }
        }

        final FeatureType expected;
        final String[] atts;
        if(styleRequieredAtts == null){
            //all properties are requiered
            expected = schema;
            atts = null;
        }else{
            final Set<String> attributs = styleRequieredAtts;
            copy.addAll(attributs);
            copy.addAll(geomProperties);
            atts = copy.toArray(new String[copy.size()]);

            //check that properties names does not hold sub properties values, if one is found
            //then we reduce it to the first parent property.
            for(int i=0; i<atts.length; i++){
                String attName = atts[i];
                int index = attName.indexOf('/');
                if(index == 0){

                    //remove all xpath elements
                    attName = attName.substring(1); //remove first slash
                    final Pattern pattern = Pattern.compile("(\\{[^\\{\\}]*\\})|(\\[[^\\[\\]]*\\])|/{1}");
                    final Matcher matcher = pattern.matcher(attName);

                    final StringBuilder sb = new StringBuilder();
                    int position = 0;
                    while (matcher.find()) {
                        final String match = matcher.group();
                        sb.append(attName.substring(position, matcher.start()));
                        position = matcher.end();

                        if(match.charAt(0) == '/'){
                            //we don't query precisely sub elements
                            position = attName.length();
                            break;
                        }else if(match.charAt(0) == '{'){
                            sb.append(match);
                        }else if(match.charAt(0) == '['){
                            //strip indexes or xpath searches
                        }
                    }
                    sb.append(attName.substring(position));
                    atts[i] = sb.toString();

                }
            }

            try {
                expected = new ViewFeatureType(schema, atts);
            } catch (MismatchedFeatureException ex) {
                throw new PortrayalException(ex);
            }
        }

        //combine the filter with rule filters----------------------------------
        if(rules != null){
            List<Filter> rulefilters = new ArrayList<>();
            for(Rule rule : rules){
                if(rule.isElseFilter()){
                    //we can't append styling filters, an else rule match all features
                    rulefilters = null;
                    break;
                }
                final Filter rf = rule.getFilter();
                if(rf == null || rf == Filter.INCLUDE){
                    //we can't append styling filters, this rule matchs all features.
                    rulefilters = null;
                    break;
                }
                rulefilters.add(rf);
            }

            if(rulefilters != null){
                final Filter combined;
                if(rulefilters.size() == 1){
                    //we can optimze here, since we pass the filter on the query, we can remove
                    //the filter on the rule.
                    final MutableRule mr = StyleUtilities.copy(rules.get(0));
                    mr.setFilter(null);
                    rules.set(0, mr);
                    combined = rulefilters.get(0);
                }else{
                    combined = FILTER_FACTORY.or(rulefilters);
                }

                if(filter != Filter.INCLUDE){
                    filter = FILTER_FACTORY.and(filter,combined);
                }else{
                    filter = combined;
                }
            }
        }


        //optimize the filter---------------------------------------------------
        filter = FilterUtilities.prepare(filter,Feature.class,expected);

        final Hints queryHints = new Hints();
        final QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(schema.getName());
        qb.setFilter(filter);
        qb.setProperties(atts);

        //resampling and ignore flag only works when we know the layer crs
        if(layerCRS != null){
            //add resampling -------------------------------------------------------
            Boolean resample = (hints == null) ? null : (Boolean) hints.get(GO2Hints.KEY_GENERALIZE);
            if(!Boolean.FALSE.equals(resample)){
                //we only disable resampling if it is explictly specified
                final double[] res = renderingContext.getResolution(layerCRS);

                //adjust with the generalization factor
                final Number n =  (hints==null) ? null : (Number)hints.get(GO2Hints.KEY_GENERALIZE_FACTOR);
                final double factor;
                if(n != null){
                    factor = n.doubleValue();
                }else{
                    factor = GO2Hints.GENERALIZE_FACTOR_DEFAULT.doubleValue();
                }
                res[0] *= factor;
                res[1] *= factor;
                qb.setResolution(res);
            }

            //add ignore flag ------------------------------------------------------
            //TODO this is efficient but erases values, when plenty of then are to be rendered
            //we should find another way to handle this
            //if(!GO2Utilities.visibleMargin(rules, 1.01f, renderingContext)){
            //    //style does not expend itself further than the feature geometry
            //    //that mean geometries smaller than a pixel will not be renderer or barely visible
            //    queryHints.put(HintsPending.KEY_IGNORE_SMALL_FEATURES, renderingContext.getResolution(layerCRS));
            //}
        }

        //add reprojection -----------------------------------------------------
        //we don't reproject, the reprojection may produce curves but JTS can not represent those.
        //so we generate those curves in java2d shapes by doing the transformation ourself.
        //TODO wait for a new geometry implementation
        //qb.setCRS(renderingContext.getObjectiveCRS2D());

        //set the acumulated hints
        qb.setHints(queryHints);
        return qb.buildQuery();
    }

    /**
     * Creates an optimal query to send to the datastore, knowing which properties are knowned and
     * the appropriate bounding box to filter.
     */
    protected static Query prepareQuery(final RenderingContext2D renderingContext,
            final FeatureMapLayer layer, double symbolsMargin) throws PortrayalException{

        final FeatureCollection fs               = layer.getCollection();
        final FeatureType schema                 = fs.getFeatureType();
        final PropertyType geomDesc              = FeatureExt.getDefaultGeometryAttribute(schema);
        final BoundingBox bbox                   = optimizeBBox(renderingContext,layer,symbolsMargin);
        final CoordinateReferenceSystem layerCRS = FeatureExt.getCRS(schema);
        final String geomAttName                 = (geomDesc!=null)? geomDesc.getName().toString() : null;
        final RenderingHints hints               = renderingContext.getRenderingHints();

        Filter filter;

        //final Envelope layerBounds = layer.getBounds();
        //we better not do any call to the layer bounding box before since it can be
        //really expensive, the featurestore is the best placed to check if he might
        //optimize the filter.
        //if( ((BoundingBox)bbox).contains(new DefaultBoundingBox(layerBounds))){
            //the layer bounds overlaps the bbox, no need for a spatial filter
        //   filter = Filter.INCLUDE;
        //}else{
        //make a bbox filter
        if(geomAttName != null){
            if (layerCRS != null) {
                filter = new UnreprojectedLooseBBox(FILTER_FACTORY.property(geomAttName),new DefaultLiteral<>(bbox));
            } else {
                filter = new LooseBBox(FILTER_FACTORY.property(geomAttName),new DefaultLiteral<>(bbox));
            }
        }else{
            filter = Filter.EXCLUDE;
        }
        //}

        //concatenate geographic filter with data filter if there is one
        if(layer.getQuery() != null && layer.getQuery().getFilter() != null){
            filter = FILTER_FACTORY.and(filter,layer.getQuery().getFilter());
        }

        //concatenate with temporal range if needed ----------------------------
        for (final FeatureMapLayer.DimensionDef def : layer.getExtraDimensions()) {
            final CoordinateReferenceSystem crs = def.getCrs();
            final Envelope canvasEnv = renderingContext.getCanvasObjectiveBounds();
            final Envelope dimEnv;
            try {
                dimEnv = Envelopes.transform(canvasEnv, crs);
            } catch (TransformException ex) {
                continue;
            }

            final Filter dimFilter = FILTER_FACTORY.and(
                    FILTER_FACTORY.lessOrEqual(FILTER_FACTORY.literal(dimEnv.getMinimum(0)), def.getLower()),
                    FILTER_FACTORY.greaterOrEqual(FILTER_FACTORY.literal(dimEnv.getMaximum(0)), def.getUpper()));

            filter = FILTER_FACTORY.and(filter, dimFilter);
        }

        //optimize the filter---------------------------------------------------
        filter = FilterUtilities.prepare(filter,Feature.class,schema);

        final Hints queryHints = new Hints();
        final QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(schema.getName());
        qb.setFilter(filter);

        //resampling and ignore flag only works when we know the layer crs
        if(layerCRS != null){
            //add resampling -------------------------------------------------------
            Boolean resample = (hints == null) ? null : (Boolean) hints.get(GO2Hints.KEY_GENERALIZE);
            if(!Boolean.FALSE.equals(resample)){
                //we only disable resampling if it is explictly specified
                final double[] res = renderingContext.getResolution(layerCRS);

                //adjust with the generalization factor
                final Number n =  (hints==null) ? null : (Number)hints.get(GO2Hints.KEY_GENERALIZE_FACTOR);
                final double factor;
                if(n != null){
                    factor = n.doubleValue();
                }else{
                    factor = GO2Hints.GENERALIZE_FACTOR_DEFAULT.doubleValue();
                }
                res[0] *= factor;
                res[1] *= factor;
                qb.setResolution(res);
            }

            //add ignore flag ------------------------------------------------------
            //TODO this is efficient but erases values, when plenty of then are to be rendered
            //we should find another way to handle this
            //if(!GO2Utilities.visibleMargin(rules, 1.01f, renderingContext)){
            //    //style does not expend itself further than the feature geometry
            //    //that mean geometries smaller than a pixel will not be renderer or barely visible
            //    queryHints.put(HintsPending.KEY_IGNORE_SMALL_FEATURES, renderingContext.getResolution(layerCRS));
            //}
        }

        //add reprojection -----------------------------------------------------
        //we don't reproject, the reprojection may produce curves but JTS can not represent those.
        //so we generate those curves in java2d shapes by doing the transformation ourself.
        //TODO wait for a new geometry implementation
        //qb.setCRS(renderingContext.getObjectiveCRS2D());

        //set the acumulated hints
        qb.setHints(queryHints);
        return qb.buildQuery();
    }

    private static BoundingBox optimizeBBox(RenderingContext2D renderingContext, FeatureMapLayer layer, double symbolsMargin){
        BoundingBox bbox                                         = renderingContext.getPaintingObjectiveBounds2D();
        final CoordinateReferenceSystem bboxCRS                  = bbox.getCoordinateReferenceSystem();
        final CanvasMonitor monitor                              = renderingContext.getMonitor();
        final CoordinateReferenceSystem layerCRS                 = FeatureExt.getCRS(layer.getCollection().getFeatureType());

        //expand the search area by the maximum symbol size
        if(symbolsMargin>0){
            final GeneralEnvelope env = new GeneralEnvelope(bbox);
            env.setRange(0, env.getMinimum(0)-symbolsMargin, env.getMaximum(0)+symbolsMargin);
            env.setRange(1, env.getMinimum(1)-symbolsMargin, env.getMaximum(1)+symbolsMargin);
            bbox = new DefaultBoundingBox(env);
        }

        //layer crs may be null if it define an abstract collection
        //or if the crs is defined only on the feature geometry
        if(layerCRS != null && !Utilities.equalsIgnoreMetadata(layerCRS,bboxCRS)){
            //BBox and layer bounds have different CRS. reproject bbox bounds
            Envelope env;

            try{
                env = Envelopes.transform(bbox, layerCRS);
                if(GeneralEnvelope.castOrCopy(env).isEmpty()){
                    //possible NaN values or out of crs validity area
                    GeneralEnvelope benv = GeneralEnvelope.castOrCopy(bbox);
                    benv.normalize();
                    env = Envelopes.transform(benv, layerCRS);
                }
            }catch(TransformException ex){
                //TODO is fixed in geotidy, the result envelope will have infinte values where needed
                //TODO should do something about this, since canvas bounds may be over the crs bounds
                monitor.exceptionOccured(ex, Level.WARNING);
                env = new Envelope2D();
            }catch(IllegalArgumentException ex){
                //looks like the coordinate of the bbox are outside of the crs valide area.
                //some crs raise this error, other not.
                //if so we should reduce our bbox to the valide extent of the crs.
                monitor.exceptionOccured(ex, Level.WARNING);

                final GeographicBoundingBox gbox = CRS.getGeographicBoundingBox(layerCRS);

                if(gbox == null){
                    env = new GeneralEnvelope(layerCRS);
                }else{
                    env = new GeneralEnvelope(gbox);
                }

            }catch(Exception ex){
                //we should not catch this but we must not block the canvas
                monitor.exceptionOccured(ex, Level.WARNING);
                return null;
            }

            //TODO looks like the envelope after transform operation doesnt have always exactly the same CRS.
            //fix CRS classes method and remove the two next lines.
            env = new GeneralEnvelope(env);
            ((GeneralEnvelope)env).setCoordinateReferenceSystem(layerCRS);

            bbox = new DefaultBoundingBox(env);
        }

        return bbox;
    }

    private static class GraphicIterator implements RenderingIterator{

        private final FeatureIterator ite;
        private final ProjectedFeature projected;

        public GraphicIterator(final FeatureIterator ite, final ProjectedFeature projected) {
            this.ite = ite;
            this.projected = projected;
        }

        @Override
        public boolean hasNext() {
            return ite.hasNext();
        }

        @Override
        public ProjectedFeature next() {
            projected.setCandidate(ite.next());
            return projected;
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
