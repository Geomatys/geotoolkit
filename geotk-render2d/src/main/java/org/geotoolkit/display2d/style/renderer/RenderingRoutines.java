/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2020, Geomatys
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
package org.geotoolkit.display2d.style.renderer;

import java.awt.RenderingHints;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.feature.Features;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.feature.internal.AttributeConvention;
import org.apache.sis.feature.internal.FeatureExpression;
import org.apache.sis.measure.Quantities;
import org.apache.sis.measure.Units;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Query;
import org.apache.sis.util.Utilities;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import static org.geotoolkit.display2d.GO2Utilities.FILTER_FACTORY;
import static org.geotoolkit.display2d.GO2Utilities.LOGGER;
import static org.geotoolkit.display2d.GO2Utilities.STYLE_FACTORY;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.ViewMapper;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.filter.function.geometry.GeometryFunctionFactory;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleUtilities;
import org.opengis.coverage.Coverage;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Expression;
import org.opengis.filter.Filter;
import org.opengis.filter.Literal;
import org.opengis.filter.ValueReference;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class RenderingRoutines {

    private RenderingRoutines(){}

    /**
     * Merge a layer style with a selection style.
     * The selection style fts elements will be placed after those of the default style.
     */
    public static MutableStyle mergeStyles(MutableStyle style, Filter selectionFilter, MutableStyle selectionStyle) {

        if (selectionFilter == null || Filter.exclude().equals(selectionFilter) || selectionStyle == null) {
            //unmodified
            return style;
        }

        final Filter exclusionFilter = FILTER_FACTORY.not(selectionFilter);

        final MutableStyle result = GO2Utilities.STYLE_FACTORY.style();

        for (FeatureTypeStyle fts : style.featureTypeStyles()) {
            final MutableFeatureTypeStyle resultfts = GO2Utilities.STYLE_FACTORY.featureTypeStyle();
            resultfts.setDescription(fts.getDescription());
            resultfts.setFeatureInstanceIDs(fts.getFeatureInstanceIDs());
            resultfts.setName(fts.getName());
            resultfts.setOnlineResource(fts.getOnlineResource());
            result.featureTypeStyles().add(resultfts);

            for (Rule rule : fts.rules()) {
                final MutableRule modifiedRule = STYLE_FACTORY.rule(rule.symbolizers().toArray(new Symbolizer[0]));
                Filter f = rule.getFilter();
                if (f == null) {
                    f = exclusionFilter;
                } else {
                    f = FILTER_FACTORY.and(f, exclusionFilter);
                }
                modifiedRule.setFilter(f);
                resultfts.rules().add(modifiedRule);
            }
        }
        if (selectionStyle != null) {
            for (FeatureTypeStyle fts : selectionStyle.featureTypeStyles()) {
                final MutableFeatureTypeStyle resultfts = GO2Utilities.STYLE_FACTORY.featureTypeStyle();
                resultfts.setDescription(fts.getDescription());
                resultfts.setFeatureInstanceIDs(fts.getFeatureInstanceIDs());
                resultfts.setName(fts.getName());
                resultfts.setOnlineResource(fts.getOnlineResource());
                result.featureTypeStyles().add(resultfts);

                for (Rule rule : fts.rules()) {
                    final MutableRule modifiedRule = STYLE_FACTORY.rule(rule.symbolizers().toArray(new Symbolizer[0]));
                    Filter f = rule.getFilter();
                    if (f == null) {
                        f = selectionFilter;
                    } else {
                        f = FILTER_FACTORY.and(f, selectionFilter);
                    }
                    modifiedRule.setFilter(f);
                    resultfts.rules().add(modifiedRule);
                }
            }
        }
        return result;
    }

    /**
     * Creates an optimal query to send to the datastore, knowing which properties are knowned and
     * the appropriate bounding box to filter.
     */
    public static Query prepareQuery(final RenderingContext2D renderingContext, FeatureSet fs, final MapLayer layer,
            final Set<String> styleRequieredAtts, final List<Rule> rules, double symbolsMargin) throws PortrayalException{

        final FeatureType schema;
        try {
            schema = fs.getType();
        } catch (DataStoreException ex) {
            throw new PortrayalException(ex.getMessage(), ex);
        }
        // Note: do not use layer boundary to define the target bbox, because it can be expensive.
        // Anyway, the target resource will be better to determine clipping between rendering boundaries and its own.
        final Envelope bbox                      = optimizeBBox(renderingContext, fs, 0/*symbolsMargin*/); //TODO symbol margin cause bug for envelope close to their limit, it needs a fix
        final CoordinateReferenceSystem layerCRS = FeatureExt.getCRS(schema);
        final RenderingHints hints               = renderingContext.getRenderingHints();

        /*
         * To restrict queried values to the rendering area, we must identify what geometries are used by the style.
         * For each applied symbol, there are 3 possible cases:
         * - if the rule uses default geometries, they will be added to the geometry property list after the loop
         * - The geometric expression is a value reference, we can safely register it in geometric properties. The
         *   reference xpath is unwrapped in a set to ensure we won't create any doublon filters.
         * - If the geometry property is a complex expression(Ex: a value computed from non geometric fields), we keep
         *   it as is to apply a filter directly upon it. Note that even if it's an expression derived from geometric
         *   fields, we cannot apply spatial filter on them, because the expression could drastically change topology.
         *   For example, if the expression is 'buffer', the result geometry would be larger than any of its operands.
         *   TODO: such cases are maybe manageable by replacing bbox filter by a distance filter based upon the buffer
         *   distance. But would it do more good than harm ?
         */
        boolean isDefaultGeometryNeeded = rules == null || rules.isEmpty();
        final Set<String> geomProperties = new HashSet<>();
        final Set<Expression> complexProperties = new HashSet<>();
        if (rules != null) {
            for (Rule r : rules) {
                for (Symbolizer s : r.symbolizers()) {
                    final Expression expGeom = s.getGeometry();
                    if (isNil(expGeom)) isDefaultGeometryNeeded = true;
                    else if (expGeom instanceof ValueReference) geomProperties.add( ((ValueReference)expGeom).getXPath() );
                    else complexProperties.add(expGeom);
                }
            }
        }

        if (isDefaultGeometryNeeded) {
            try {
                final PropertyType defaultGeometry = FeatureExt.getDefaultGeometry(schema);
                final String geomName = Features.getLinkTarget(defaultGeometry)
                        .orElseGet(() -> defaultGeometry.getName().toString());
                geomProperties.add(geomName);
            } catch (PropertyNotFoundException e) {
                throw new PortrayalException("Default geometry cannot be determined. " +
                        "However, it is needed to properly define filtering rules.");
            } catch (IllegalStateException e) {
                // If there's multiple geometric properties, and no primary one, we will use them all
                schema.getProperties(true)
                        .stream()
                        // Ignore links: they're doublons of existing attributes. However, we want to keep computed values.
                        .filter(p -> !Features.getLinkTarget(p).isPresent())
                        .filter(AttributeConvention::isGeometryAttribute)
                        .map(p -> p.getName().toString())
                        .forEach(geomProperties::add);
            }
        }

        if (!complexProperties.isEmpty()) {
            LOGGER.fine("A style rule uses complex geometric properties. It can severly affect performance");
        }

        /*
         * We may have coverage properties for geometry
         * add an expression to convert them to geometries for the filter.
         */
        Stream<Expression> geomStream = geomProperties.stream().map(FILTER_FACTORY::property).map((Expression t) -> {
            final Expression<? super Feature,?> expression = t;
            final FeatureExpression<?,?> fex = FeatureExpression.castOrCopy(expression);
            final PropertyTypeBuilder resultType = fex.expectedType(schema, new FeatureTypeBuilder());
            if (resultType != null) {
                PropertyType pt = resultType.build();
                if (pt instanceof AttributeType at) {
                    final Class valueClass = at.getValueClass();
                    if (GridCoverage.class.isAssignableFrom(valueClass)) {
                        t = FILTER_FACTORY.function(GeometryFunctionFactory.COVERAGE_BOUNDINGBOX, t);
                    }
                }
            }
            return t;
        });


        final Optional<Filter> spatialFilter =
                Stream.concat(
                        geomStream,
                        complexProperties.stream()
                )
                        .<Filter>map(expression -> FILTER_FACTORY.bbox(expression, bbox))
                        .reduce(FILTER_FACTORY::or);

        Filter userFilter= null;
        //concatenate geographic filter with data filter if there is one
        if (layer != null) {
            Query query = layer.getQuery();
            if (query instanceof FeatureQuery) {
                userFilter = ((FeatureQuery) query).getSelection();
            }
        }

        Filter filter;
        if (spatialFilter.isPresent()) {
            if (userFilter == null) filter = spatialFilter.get();
            // Note: we give priority to the spatial filter here, because it is our main use case: rendering is driven
            // by bounding box.
            else filter = FILTER_FACTORY.and(spatialFilter.get(), userFilter);
        } else if (userFilter == null) {
            throw new PortrayalException("No spatial filter can be determined from style rules, and no user filter specified." +
                    "We refuse dataset full-scan. To authorize it, manually specify Filter 'INCLUDE' on your map layer.");
        } else {
            LOGGER.warning("Spatial filter cannot be determined for rendering. However, user has provided a custom filter that we'll use as sole filtering policy");
            filter = userFilter;
        }

        final Set<String> copy = new HashSet<>();

        final FeatureType expected;
        final String[] atts;
        if (styleRequieredAtts == null) {
            //all properties are requiered
            expected = schema;
            atts = null;
        } else {
            final Set<String> attributs = styleRequieredAtts;
            copy.addAll(attributs);
            copy.addAll(geomProperties);

            try {
                //always include the identifier if it exist
                schema.getProperty(AttributeConvention.IDENTIFIER);
                copy.add(AttributeConvention.IDENTIFIER);
            } catch (PropertyNotFoundException ex) {
                //no id, ignore it
            }

            atts = copy.toArray(new String[copy.size()]);

            //check that properties names does not hold sub properties values, if one is found
            //then we reduce it to the first parent property.
            for (int i=0; i<atts.length; i++) {
                String attName = atts[i];
                int index = attName.indexOf('/');
                if (index == 0) {

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

                        if (match.charAt(0) == '/') {
                            //we don't query precisely sub elements
                            position = attName.length();
                            break;
                        } else if(match.charAt(0) == '{') {
                            sb.append(match);
                        } else if(match.charAt(0) == '[') {
                            //strip indexes or xpath searches
                        }
                    }
                    sb.append(attName.substring(position));
                    atts[i] = sb.toString();
                }
            }
            try {
                expected = new ViewMapper(schema, atts).getMappedType();
            } catch (MismatchedFeatureException ex) {
                throw new PortrayalException(ex);
            }
        }

        //combine the filter with rule filters----------------------------------
        if (rules != null) {
            List<Filter<Object>> rulefilters = new ArrayList<>();
            for (Rule rule : rules) {
                if(rule.isElseFilter()){
                    //we can't append styling filters, an else rule match all features
                    rulefilters = null;
                    break;
                }
                final Filter rf = rule.getFilter();
                if (rf == null || rf == Filter.include()) {
                    //we can't append styling filters, this rule matchs all features.
                    rulefilters = null;
                    break;
                }
                rulefilters.add(rf);
            }

            if (rulefilters != null) {
                final Filter combined;
                if (rulefilters.size() == 1) {
                    //we can optimze here, since we pass the filter on the query, we can remove
                    //the filter on the rule.
                    final MutableRule mr = StyleUtilities.copy(rules.get(0));
                    mr.setFilter(null);
                    rules.set(0, mr);
                    combined = rulefilters.get(0);
                } else {
                    combined = FILTER_FACTORY.or(rulefilters);
                }

                if (filter != Filter.include()) {
                    filter = FILTER_FACTORY.and(filter,combined);
                } else {
                    filter = combined;
                }
            }
        }


        //optimize the filter---------------------------------------------------
        filter = FilterUtilities.prepare(filter, Feature.class, expected);

        final Hints queryHints = new Hints();
        final org.geotoolkit.storage.feature.query.Query qb = new org.geotoolkit.storage.feature.query.Query();
        qb.setTypeName(schema.getName());
        qb.setSelection(filter);
        qb.setProperties(atts);

        //resampling and ignore flag only works when we know the layer crs
        if (layerCRS != null) {
            //add resampling -------------------------------------------------------
            Boolean resample = (hints == null) ? null : (Boolean) hints.get(GO2Hints.KEY_GENERALIZE);
            if (!Boolean.FALSE.equals(resample)) {
                //we only disable resampling if it is explictly specified
                double[] res = renderingContext.getResolution(layerCRS);

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

                try {
                    res = renderingContext.getResolution(CRS.forCode("EPSG:3395"));
                    res[0] *= factor;
                    res[1] *= factor;
                    qb.setLinearResolution(Quantities.create(res[0], Units.METRE));
                } catch (FactoryException ex) {
                    throw new PortrayalException(ex.getMessage(), ex);
                }

            }

            //add ignore flag ------------------------------------------------------
            //TODO this is efficient but erases values, when plenty of then are to be rendered
            //we should find another way to handle this
            //if(!GO2Utilities.visibleMargin(rules, 1.01f, renderingContext)){
            //    //style does not expend itself further than the feature geometry
            //    //that mean geometries smaller than a pixel will not be renderer or barely visible
            //    queryHints.put(Hints.KEY_IGNORE_SMALL_FEATURES, renderingContext.getResolution(layerCRS));
            //}
        }

        //add reprojection -----------------------------------------------------
        //we don't reproject, the reprojection may produce curves but JTS can not represent those.
        //so we generate those curves in java2d shapes by doing the transformation ourself.
        //TODO wait for a new geometry implementation
        //qb.setCRS(renderingContext.getObjectiveCRS2D());

        //set the acumulated hints
        qb.setHints(queryHints);
        return qb;
    }

    private static boolean isNil(Expression expGeom) {
        if (expGeom == null) return true;
        if (expGeom instanceof Literal) {
            final Object value = ((Literal<?, ?>) expGeom).getValue();
            return (value == null);
        }

        return false;
    }

    public static Envelope optimizeBBox(RenderingContext2D renderingContext, FeatureSet featureSet, double symbolsMargin) throws PortrayalException{
        Envelope bbox                                            = renderingContext.getCanvasObjectiveBounds2D();
        final CoordinateReferenceSystem bboxCRS                  = bbox.getCoordinateReferenceSystem();
        final CanvasMonitor monitor                              = renderingContext.getMonitor();
        final CoordinateReferenceSystem layerCRS;
        try {
            layerCRS = FeatureExt.getCRS(featureSet.getType());
        } catch (DataStoreException ex) {
            throw new PortrayalException(ex.getMessage(), ex);
        }

        //expand the search area by the maximum symbol size
        if(symbolsMargin>0){
            final GeneralEnvelope env = new GeneralEnvelope(bbox);
            env.setRange(0, env.getMinimum(0)-symbolsMargin, env.getMaximum(0)+symbolsMargin);
            env.setRange(1, env.getMinimum(1)-symbolsMargin, env.getMaximum(1)+symbolsMargin);
            bbox = env;
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

            bbox = env;
        } else if (layerCRS != null && layerCRS != bboxCRS) {
            //replace by data crs to avoid any further not exact equality tests which can be expensive
            final GeneralEnvelope env = new GeneralEnvelope(bbox);
            env.setCoordinateReferenceSystem(layerCRS);
            bbox = env;
        }
        return bbox;
    }

    public static GraphicIterator getIterator(final FeatureSet features,
            final RenderingContext2D renderingContext) throws DataStoreException {

        final FeatureIterator iterator;
        final Stream<Feature> stream = features.features(false);
        final Iterator<Feature> i = stream.iterator();
        iterator = new FeatureIterator() {
            @Override
            public Feature next() throws FeatureStoreRuntimeException {
                return i.next();
            }
            @Override
            public boolean hasNext() throws FeatureStoreRuntimeException {
                return i.hasNext();
            }
            @Override
            public void close() {
                stream.close();
            }
        };
        final ProjectedFeature projectedFeature = new ProjectedFeature(renderingContext);
        return new GraphicIterator(iterator, projectedFeature);
    }


    public static class GraphicIterator implements Iterator<ProjectedObject>,Closeable {

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
