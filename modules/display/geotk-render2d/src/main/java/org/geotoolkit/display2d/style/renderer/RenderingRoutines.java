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
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.storage.query.SimpleQuery;
import org.apache.sis.measure.Quantities;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Query;
import org.apache.sis.util.Utilities;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import static org.geotoolkit.display2d.GO2Utilities.FILTER_FACTORY;
import static org.geotoolkit.display2d.GO2Utilities.STYLE_FACTORY;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.ViewMapper;
import org.geotoolkit.filter.DefaultPropertyName;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.geometry.BoundingBox;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.storage.feature.query.QueryBuilder;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleUtilities;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
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
     *
     * @param style
     * @param selectionFilter
     * @param selectionStyle
     * @return
     */
    public static MutableStyle mergeStyles(MutableStyle style, Filter selectionFilter, MutableStyle selectionStyle) {

        if (selectionFilter == null || Filter.EXCLUDE.equals(selectionFilter) || selectionStyle == null) {
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
    public static Query prepareQuery(final RenderingContext2D renderingContext, FeatureSet fs, final FeatureMapLayer layer,
            final Set<String> styleRequieredAtts, final List<Rule> rules, double symbolsMargin) throws PortrayalException{

        final FeatureType schema;
        try {
            schema = fs.getType();
        } catch (DataStoreException ex) {
            throw new PortrayalException(ex.getMessage(), ex);
        }
        PropertyType geomDesc = null;
        try {
            geomDesc = FeatureExt.getDefaultGeometry(schema);
        } catch(PropertyNotFoundException | IllegalStateException ex){};
        final BoundingBox bbox                   = optimizeBBox(renderingContext, fs, symbolsMargin);
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
        } else {
            allDefined = false;
        }
        if (geomDesc!=null && !allDefined) {
            geomProperties.add(geomDesc.getName().toString());
        }

        Filter filter;

        //final Envelope layerBounds = layer.getBounds();
        //we better not do any call to the layer bounding box before since it can be
        //really expensive, the featurestore is the best placed to check if he might
        //optimize the filter.
        //make a bbox filter
        if(!geomProperties.isEmpty()){
            if (geomProperties.size() == 1) {
                final String geomAttName = geomProperties.iterator().next();
                filter = FILTER_FACTORY.bbox(FILTER_FACTORY.property(geomAttName),bbox);
            } else {
                //make an OR filter with all geometries
                final List<Filter> geomFilters = new ArrayList<>();
                for (String geomAttName : geomProperties) {
                    geomFilters.add(FILTER_FACTORY.bbox(FILTER_FACTORY.property(geomAttName),bbox));
                }
                filter = FILTER_FACTORY.or(geomFilters);
            }

        }else{
            filter = Filter.EXCLUDE;
        }

        //concatenate geographic filter with data filter if there is one
        if (layer != null) {
            Query query = layer.getQuery();
            if (query instanceof SimpleQuery) {
                filter = FILTER_FACTORY.and(filter, ((SimpleQuery) query).getFilter());
            }
        }

        final Set<String> copy = new HashSet<>();

        //concatenate with temporal range if needed ----------------------------
        if (layer != null) {
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
        }

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
            List<Filter> rulefilters = new ArrayList<>();
            for (Rule rule : rules) {
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

                if (filter != Filter.INCLUDE) {
                    filter = FILTER_FACTORY.and(filter,combined);
                } else {
                    filter = combined;
                }
            }
        }


        //optimize the filter---------------------------------------------------
        filter = FilterUtilities.prepare(filter, Feature.class, expected);

        final Hints queryHints = new Hints();
        final QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(schema.getName());
        qb.setFilter(filter);
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
                qb.setResolution(res);

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
    public static SimpleQuery prepareQuery(final RenderingContext2D renderingContext,
            final FeatureMapLayer layer, double symbolsMargin) throws PortrayalException{

        final FeatureSet fs                      = layer.getResource();
        final FeatureType schema;
        try {
            schema = fs.getType();
        } catch (DataStoreException ex) {
            throw new PortrayalException(ex.getMessage(), ex);
        }
        final BoundingBox bbox                   = optimizeBBox(renderingContext, fs, symbolsMargin);
        final CoordinateReferenceSystem layerCRS = FeatureExt.getCRS(schema);
        final RenderingHints hints               = renderingContext.getRenderingHints();

        String geomAttName;
        try {
            geomAttName = FeatureExt.getDefaultGeometry(schema).getName().toString();
        } catch (Exception e) {
            // We don't want rendering to fail because of a single layer.
            geomAttName = null;
        }

        Filter filter;

        //final Envelope layerBounds = layer.getBounds();
        //we better not do any call to the layer bounding box before since it can be
        //really expensive, the featurestore is the best placed to check if he might
        //optimize the filter.
        //if( ((BoundingBox)bbox).contains(new BoundingBox(layerBounds))){
            //the layer bounds overlaps the bbox, no need for a spatial filter
        //   filter = Filter.INCLUDE;
        //}else{
        //make a bbox filter
        if(geomAttName != null){
            filter = FILTER_FACTORY.bbox(FILTER_FACTORY.property(geomAttName),bbox);
        }else{
            filter = Filter.EXCLUDE;
        }
        //}

        //concatenate geographic filter with data filter if there is one
        Query query = layer.getQuery();
        if (query instanceof SimpleQuery) {
            filter = FILTER_FACTORY.and(filter, ((SimpleQuery) query).getFilter());
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

        return qb.buildQuery();
    }

    public static BoundingBox optimizeBBox(RenderingContext2D renderingContext, FeatureSet featureSet, double symbolsMargin) throws PortrayalException{
        BoundingBox bbox                                         = renderingContext.getPaintingObjectiveBounds2D();
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
            bbox = new BoundingBox(env);
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

            bbox = new BoundingBox(env);
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
