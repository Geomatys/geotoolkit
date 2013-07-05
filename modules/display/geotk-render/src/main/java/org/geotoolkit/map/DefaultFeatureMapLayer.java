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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.filter.visitor.ListingPropertyVisitor;
import org.apache.sis.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Range;
import org.geotoolkit.util.collection.NotifiedCheckedList;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Default implementation of the MapLayer.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class DefaultFeatureMapLayer extends DefaultCollectionMapLayer implements FeatureMapLayer {

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
    DefaultFeatureMapLayer(final FeatureCollection<? extends Feature> collection, final MutableStyle style) {
        super(collection,style);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Query getQuery() {
        if(query == null){
            query = QueryBuilder.all(getCollection().getFeatureType().getName());
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
    public FeatureCollection<? extends Feature> getCollection() {
        return (FeatureCollection<? extends Feature>) super.getCollection();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {
        final FeatureCollection<? extends Feature> featureCol = getCollection();
        final CoordinateReferenceSystem sourceCrs = featureCol.getFeatureType().getCoordinateReferenceSystem();
        Envelope env = null;
        try {
            env = featureCol.getEnvelope();
        } catch (DataStoreException e) {
            LOGGER.log(Level.WARNING, "Could not create referecenced envelope.",e);
        }

        if(env == null){
            Envelope crsEnv = CRS.getEnvelope(sourceCrs);
            if(crsEnv != null){
                //we couldn't estime the features envelope, return the crs envelope if possible
                //we assume the features are not out of the crs valide envelope
                env = new GeneralEnvelope(crsEnv);
            }else{
                //never return a null envelope, we better return an infinite envelope
                env = new Envelope2D(sourceCrs,Double.NaN,Double.NaN,Double.NaN,Double.NaN);
            }
        }

        return env;
    }

    @Override
    public List<DimensionDef> getExtraDimensions() {
        return extraDims;
    }

    @Override
    public Collection<Range> getDimensionRange(final DimensionDef def) throws DataStoreException{
        final List<Range> values = new ArrayList<Range>();
        final Expression lower = def.getLower();
        final Expression upper = def.getUpper();

        final Set<String> properties = new HashSet<String>();
        lower.accept(ListingPropertyVisitor.VISITOR, properties);
        upper.accept(ListingPropertyVisitor.VISITOR, properties);

        final QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(getCollection().getFeatureType().getName());
        qb.setProperties(properties.toArray(new String[properties.size()]));
        final FeatureCollection col = getCollection().subCollection(qb.buildQuery());

        final FeatureIterator ite = col.iterator();
        while(ite.hasNext()){
            Feature f = ite.next();
            final Comparable c1 = (Comparable) lower.evaluate(f);
            final Comparable c2 = (Comparable) upper.evaluate(f);
            values.add( new Range(Comparable.class, c1, true, c2, true));
        }
        ite.close();

        return values;
    }

}
