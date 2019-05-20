/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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
package org.geotoolkit.map;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.filter.visitor.ListingPropertyVisitor;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Range;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureStoreUtilities;
import static org.geotoolkit.map.MapLayer.SELECTION_FILTER_PROPERTY;
import org.geotoolkit.util.collection.NotifiedCheckedList;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default implementation of the MapLayer.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
final class DefaultFeatureMapLayer extends AbstractMapLayer implements FeatureMapLayer {


    private final FeatureSet collection;
    protected Id selectionFilter = null;
    private Query query = null;

    private final List<DimensionDef> extraDims = new NotifiedCheckedList<DimensionDef>(DimensionDef.class) {

        @Override
        protected void notifyAdd(DimensionDef item, int index) {
            firePropertyChange(PROP_EXTRA_DIMENSIONS, null, extraDims);
        }

        @Override
        protected void notifyAdd(Collection<? extends DimensionDef> items, NumberRange<Integer> range) {
            firePropertyChange(PROP_EXTRA_DIMENSIONS, null, extraDims);
        }

        @Override
        protected void notifyChange(DimensionDef oldItem, DimensionDef newItem, int index) {
            firePropertyChange(PROP_EXTRA_DIMENSIONS, null, extraDims);
        }

        @Override
        protected void notifyRemove(DimensionDef item, int index) {
            firePropertyChange(PROP_EXTRA_DIMENSIONS, null, extraDims);
        }

        @Override
        protected void notifyRemove(Collection<? extends DimensionDef> items, NumberRange<Integer> range) {
            firePropertyChange(PROP_EXTRA_DIMENSIONS, null, extraDims);
        }
    };

    /**
     * Creates a new instance of DefaultFeatureMapLayer
     *
     * @param collection : the data source for this layer
     * @param style : the style used to represent this layer
     */
    DefaultFeatureMapLayer(final FeatureSet collection, final MutableStyle style) {
        super(style);
        ArgumentChecks.ensureNonNull("FeatureSet", collection);
        this.collection = collection;

        trySetName(collection);
    }

    @Override
    public Id getSelectionFilter(){
        return selectionFilter;
    }

    @Override
    public void setSelectionFilter(final Id filter){

        final Filter oldfilter;
        synchronized (this) {
            oldfilter = this.selectionFilter;
            if(Objects.equals(oldfilter, filter)){
                return;
            }
            this.selectionFilter = filter;
        }
        firePropertyChange(SELECTION_FILTER_PROPERTY, oldfilter, this.selectionFilter);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Query getQuery() {
        if(query == null){
            try {
                query = QueryBuilder.all(getResource().getType().getName());
            } catch (DataStoreException ex) {
                throw new FeatureStoreRuntimeException(ex.getMessage(), ex);
            }
        }

        return query;
    }

    /**
     * Sets a filter query for this layer.
     *
     * <p>
     * Query filters should be used to reduce searched or displayed feature
     * when rendering or analyzing this layer.
     * </p>
     *
     * @param query the full filter for this layer. can not be null.
     */
    @Override
    public void setQuery(final Query query) {
        ensureNonNull("query", query);

        final Query oldQuery;
        synchronized (this) {
            oldQuery = getQuery();
            if(oldQuery.equals(query)){
                return;
            }
            this.query = query;
        }
        firePropertyChange(QUERY_PROPERTY, oldQuery, this.query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureSet getResource() {
        return collection;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {
        final FeatureSet featureSet = getResource();
        CoordinateReferenceSystem sourceCrs = null;
        Envelope env = null;
        try {
            sourceCrs = FeatureExt.getCRS(featureSet.getType());
            env = FeatureStoreUtilities.getEnvelope(featureSet);
        } catch (DataStoreException e) {
            LOGGER.log(Level.WARNING, "Could not create referecenced envelope.",e);
        }

        if(env == null){
            //no data
            //never return a null envelope, we better return an infinite envelope
            env = new Envelope2D(sourceCrs,Double.NaN,Double.NaN,Double.NaN,Double.NaN);

//            Envelope crsEnv = CRS.getEnvelope(sourceCrs);
//            if(crsEnv != null){
//                //we couldn't estime the features envelope, return the crs envelope if possible
//                //we assume the features are not out of the crs valide envelope
//                env = new GeneralEnvelope(crsEnv);
//            }else{
//                //never return a null envelope, we better return an infinite envelope
//                env = new Envelope2D(sourceCrs,Double.NaN,Double.NaN,Double.NaN,Double.NaN);
//            }
        }

        return env;
    }

    @Override
    public List<DimensionDef> getExtraDimensions() {
        return extraDims;
    }

    @Override
    public Collection<Range> getDimensionRange(final DimensionDef def) throws DataStoreException {
        final Expression lower = def.getLower();
        final Expression upper = def.getUpper();

        final Set<String> properties = new HashSet<>();
        lower.accept(ListingPropertyVisitor.VISITOR, properties);
        upper.accept(ListingPropertyVisitor.VISITOR, properties);

        final QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(getResource().getType().getName());
        qb.setProperties(properties.toArray(new String[properties.size()]));
        final FeatureSet col = getResource().subset(qb.buildQuery());

        try (Stream<Feature> stream = col.features(false)) {
            return stream
                    .map(f -> {
                        return new Range(
                                Comparable.class,
                                lower.evaluate(f, Comparable.class),
                                true,
                                upper.evaluate(f, Comparable.class),
                                true
                        );
                    })
                    .collect(Collectors.toList());
        }
    }
}
