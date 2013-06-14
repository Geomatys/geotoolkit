/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStoreContentEvent;
import org.geotoolkit.data.FeatureStoreListener;
import org.geotoolkit.data.FeatureStoreManagementEvent;
import org.geotoolkit.data.memory.GenericCachedFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import static org.geotoolkit.display2d.GO2Utilities.*;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessCollectionLayerJ2D.RenderingIterator;
import org.geotoolkit.display2d.primitive.DefaultGraphicFeatureJ2D;
import org.geotoolkit.display2d.primitive.DefaultSearchAreaJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedRule;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.filter.binaryspatial.LooseBBox;
import org.geotoolkit.filter.binaryspatial.UnreprojectedLooseBBox;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.apache.sis.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.StyleUtilities;
import org.opengis.display.primitive.Graphic;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Rule;

/**
 * Single object to represent a complete feature map layer.
 * This is a Stateless graphic object.
 *
 * @author johann sorel (Geomatys)
 * @module pending
 */
public class StatelessFeatureLayerJ2D extends StatelessCollectionLayerJ2D<FeatureMapLayer> implements FeatureStoreListener{

    protected FeatureStoreListener.Weak weakSessionListener = new FeatureStoreListener.Weak(this);

    protected Query currentQuery = null;


    public StatelessFeatureLayerJ2D(final J2DCanvas canvas, final FeatureMapLayer layer){
        super(canvas, layer);
        final Session session = layer.getCollection().getSession();
        weakSessionListener.registerSource(session);
    }

    @Override
    public void structureChanged(FeatureStoreManagementEvent event) {
    }

    @Override
    public void contentChanged(FeatureStoreContentEvent event) {
        if(item.isVisible() && getCanvas().getController().isAutoRepaint()){
            //TODO should call a repaint only on this graphic
            getCanvas().getController().repaint();
        }
    }

    @Override
    protected StatelessContextParams getStatefullParameters(final RenderingContext2D context){
        params.update(context);
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

        //first extract the valid rules at this scale
        final List<Rule> validRules = getValidRules(renderingContext,item,item.getCollection().getFeatureType());

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just continue.
        if(validRules.isEmpty()){
            return;
        }

        //extract the used names
        final Set<String> names = propertiesNames(validRules);

        final FeatureCollection<Feature> candidates;
        try {
            //optimize
            candidates = (FeatureCollection<Feature>)optimizeCollection(renderingContext, names, validRules);
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

    @Override
    protected Collection<?> optimizeCollection(final RenderingContext2D context,
            final Set<String> requieredAtts, final List<Rule> rules) throws Exception {
        currentQuery = prepareQuery(context, item, requieredAtts, rules);
        //we detach feature since we are going to use a cache.
        currentQuery.getHints().put(HintsPending.FEATURE_DETACHED,Boolean.TRUE);
        final Query query = currentQuery;
        FeatureCollection col = ((FeatureCollection<Feature>)item.getCollection()).subCollection(query);
        col = GenericCachedFeatureIterator.wrap(col, 1000);
        return col;
    }

    @Override
    protected FeatureId id(Object candidate) {
        return ((Feature)candidate).getIdentifier();
    }

    @Override
    protected RenderingIterator getIterator(final Collection<?> features,
            final RenderingContext2D renderingContext, final StatelessContextParams params) {
        final Hints iteHints = new Hints(HintsPending.FEATURE_DETACHED, Boolean.FALSE);
        final FeatureIterator<? extends Feature> iterator = ((FeatureCollection)features).iterator(iteHints);
        final DefaultProjectedFeature projectedFeature = new DefaultProjectedFeature(params);
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

        final Name featureTypeName = item.getCollection().getFeatureType().getName();
        final CachedRule[] rules = GO2Utilities.getValidCachedRules(item.getStyle(),
                c2d.getSEScale(), featureTypeName,null);

        //we perform a first check on the style to see if there is at least
        //one valid rule at this scale, if not we just return null.
        if (rules.length == 0) {
            return graphics;
        }

        if(graphics == null) graphics = new ArrayList<Graphic>();

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
            query = prepareQuery(renderingContext, layer, attributs,null);
        } catch (PortrayalException ex) {
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
            return graphics;
        }

        final FeatureCollection<Feature> features;
        try{
            features = ((FeatureCollection<Feature>)layer.getCollection()).subCollection(query);
        }catch(DataStoreException ex){
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
            //can not continue this layer with this error
            return graphics;
        }

        //we do not check if the collection is empty or not since
        //it can be a very expensive operation

        final StatelessContextParams params = getStatefullParameters(renderingContext);


        // iterate and find the first graphic that hit the given point
        final FeatureIterator<Feature> iterator;
        try{
            iterator = features.iterator();
        }catch(FeatureStoreRuntimeException ex){
            renderingContext.getMonitor().exceptionOccured(ex, Level.WARNING);
            return graphics;
        }

        //prepare the renderers
        final DefaultCachedRule preparedRenderers = new DefaultCachedRule(rules, renderingContext);

        final DefaultProjectedFeature projectedFeature = new DefaultProjectedFeature(params);
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
                                if(feature != null) graphics.add( new DefaultGraphicFeatureJ2D(getCanvas(), layer, feature) );
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
     * Creates an optimal query to send to the datastore, knowing which properties are knowned and
     * the appropriate bounding box to filter.
     */
    protected static Query prepareQuery(final RenderingContext2D renderingContext, final FeatureMapLayer layer,
            final Set<String> styleRequieredAtts, final List<Rule> rules) throws PortrayalException{

        final FeatureCollection<? extends Feature> fs            = layer.getCollection();
        final FeatureType schema                                 = fs.getFeatureType();
        final GeometryDescriptor geomDesc                        = schema.getGeometryDescriptor();
        BoundingBox bbox                                         = renderingContext.getPaintingObjectiveBounds2D();
        final CoordinateReferenceSystem bboxCRS                  = bbox.getCoordinateReferenceSystem();
        final CanvasMonitor monitor                              = renderingContext.getMonitor();
        final CoordinateReferenceSystem layerCRS                 = schema.getCoordinateReferenceSystem();
        final String geomAttName                                 = (geomDesc!=null)? geomDesc.getLocalName() : null;
        final RenderingHints hints                               = renderingContext.getRenderingHints();

        //layer crs may be null if it define an abstract collection
        //or if the crs is defined only on the feature geometry
        if(layerCRS != null && !CRS.equalsIgnoreMetadata(layerCRS,bboxCRS)){
            //BBox and layer bounds have different CRS. reproject bbox bounds
            Envelope env;

            try{
                env = CRS.transform(bbox, layerCRS);
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
                filter = new UnreprojectedLooseBBox(FILTER_FACTORY.property(geomAttName),new DefaultLiteral<BoundingBox>(bbox));
            } else {
                filter = new LooseBBox(FILTER_FACTORY.property(geomAttName),new DefaultLiteral<BoundingBox>(bbox));
            }
        }else{
            filter = Filter.EXCLUDE;
        }
        //}

        //concatenate geographique filter with data filter if there is one
        if(layer.getQuery() != null && layer.getQuery().getFilter() != null){
            filter = FILTER_FACTORY.and(filter,layer.getQuery().getFilter());
        }

        //concatenate with temporal range if needed ----------------------------
        for (final FeatureMapLayer.DimensionDef def : layer.getExtraDimensions()) {
            final CoordinateReferenceSystem crs = def.getCrs();
            final Envelope canvasEnv = renderingContext.getCanvasObjectiveBounds();
            final Envelope dimEnv;
            try {
                dimEnv = CRS.transform(canvasEnv, crs);
            } catch (TransformException ex) {
                continue;
            }

            final Filter dimFilter = FILTER_FACTORY.and(
                    FILTER_FACTORY.lessOrEqual(FILTER_FACTORY.literal(dimEnv.getMinimum(0)), def.getLower()),
                    FILTER_FACTORY.greaterOrEqual(FILTER_FACTORY.literal(dimEnv.getMaximum(0)), def.getUpper()));

            filter = FILTER_FACTORY.and(filter, dimFilter);
        }

        final Set<String> attributs = styleRequieredAtts;
        final Set<String> copy = new HashSet<String>(attributs);
        if(geomAttName != null){
            copy.add(geomAttName);
        }
        final String[] atts = copy.toArray(new String[copy.size()]);

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

        final FeatureType expected;
        try {
            expected = FeatureTypeUtilities.createSubType(schema, atts);
        } catch (SchemaException ex) {
            throw new PortrayalException(ex);
        }

        //combine the filter with rule filters----------------------------------
        if(rules != null){
            List<Filter> rulefilters = new ArrayList<Filter>();
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

    private static class GraphicIterator implements RenderingIterator{

        private final FeatureIterator<? extends Feature> ite;
        private final DefaultProjectedFeature projected;

        public GraphicIterator(final FeatureIterator<? extends Feature> ite, final DefaultProjectedFeature projected) {
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
