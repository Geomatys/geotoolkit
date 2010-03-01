/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.query.Join;
import org.geotoolkit.data.query.JoinType;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.util.collection.CloseableIterator;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.PropertyName;

/**
 * FeatureCollection that takes it'es source from a join query.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultJoinFeatureCollection extends AbstractFeatureCollection<Feature>{

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private final Query query;
    private final FeatureCollection leftCollection;
    private final FeatureCollection rightCollection;
    private FeatureType type = null;

    public DefaultJoinFeatureCollection(String id, Query query){
        super(id,query.getSource());

        if(!(query.getSource() instanceof Join)){
            throw new IllegalArgumentException("Query must have a join source.");
        }

        if(!QueryUtilities.isAbsolute(getSource())){
            throw new IllegalArgumentException("Query source must be absolute.");
        }

        this.query = query;

        //todo must parse the filter on each selector
        leftCollection = QueryUtilities.evaluate("left", QueryBuilder.all(getSource().getLeft()));
        rightCollection = QueryUtilities.evaluate("right", QueryBuilder.all(getSource().getRight()));

    }

    @Override
    public Join getSource() {
        return (Join) super.getSource();
    }

    @Override
    public synchronized FeatureType getFeatureType() {
        if(type == null){
            final FeatureType leftType = leftCollection.getFeatureType();
            final FeatureType rightType = rightCollection.getFeatureType();

            final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
            sftb.setName("combine");

            for(final PropertyDescriptor desc : leftType.getDescriptors()){
                sftb.add((AttributeDescriptor) desc);
            }
            for(final PropertyDescriptor desc : rightType.getDescriptors()){
                sftb.add((AttributeDescriptor) desc);
            }

            type = sftb.buildFeatureType();
        }

        return type;
    }

    @Override
    public FeatureCollection<Feature> subCollection(Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CloseableIterator<FeatureCollectionRow> getRows() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureIterator<Feature> iterator(Hints hints) throws DataStoreRuntimeException {
        final JoinType jt = getSource().getJoinType();

        if(jt == JoinType.INNER){
            return new JoinInnerIterator(hints);
        }else if(jt == JoinType.LEFT_OUTER){
            throw new UnsupportedOperationException("Not supported yet.");
        }else if(jt == JoinType.RIGHT_OUTER){
            throw new UnsupportedOperationException("Not supported yet.");
        }else{
            throw new IllegalArgumentException("Unknowned Join type : " + jt);
        }

    }

    @Override
    public void update(Filter filter, Map<? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(Filter filter) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Iterate on both collections with an Inner join condition.
     */
    private class JoinInnerIterator implements FeatureIterator<Feature>{

        private final FeatureIterator leftIterator;
        private FeatureIterator rightIterator;
        private Feature leftFeature;
        private Feature nextFeature;

        JoinInnerIterator(Hints hints){
            leftIterator = leftCollection.iterator(hints);
        }

        @Override
        public Feature next() {
            try {
                searchNext();
            } catch (DataStoreException ex) {
                throw new DataStoreRuntimeException(ex);
            }
            Feature f = nextFeature;
            nextFeature = null;
            return f;
        }

        @Override
        public void close() {
            leftIterator.close();
            if(rightIterator != null){
                rightIterator.close();
            }
        }

        @Override
        public boolean hasNext() {
            try {
                searchNext();
            } catch (DataStoreException ex) {
                throw new DataStoreRuntimeException(ex);
            }
            return nextFeature != null;
        }

        private void searchNext() throws DataStoreException{
            if(nextFeature != null) return;

            final FeatureType combineType = getFeatureType();

            final PropertyIsEqualTo equal = getSource().getJoinCondition();
            final PropertyName leftProperty = (PropertyName) equal.getExpression1();
            final PropertyName rightProperty = (PropertyName) equal.getExpression2();

            //we might have several right features for one left
            if(leftFeature != null && rightIterator != null){
                while(nextFeature== null && rightIterator.hasNext()){
                    final Feature rightFeature = rightIterator.next();
                    nextFeature = checkValid(leftFeature, rightFeature);
                }
            }

            while(nextFeature==null && leftIterator.hasNext()){
                rightIterator = null;
                leftFeature = leftIterator.next();
                final Object leftValue = leftProperty.evaluate(leftFeature);


                if(rightIterator == null){
                    QueryBuilder qb = new QueryBuilder(DefaultName.valueOf("test"));
                    qb.setFilter(FF.equals(rightProperty, FF.literal(leftValue)));
                    Query rightQuery = qb.buildQuery();
                    rightIterator = rightCollection.subCollection(rightQuery).iterator();
                }

                while(nextFeature== null && rightIterator.hasNext()){
                    final Feature rightFeature = rightIterator.next();
                    nextFeature = checkValid(leftFeature, rightFeature);
                }

            }

        }

        private Feature checkValid(Feature left, Feature right){
            final SimpleFeatureBuilder sfb = new SimpleFeatureBuilder((SimpleFeatureType) getFeatureType());
            final String leftId = left.getIdentifier().getID();
            final String rightId = right.getIdentifier().getID();

            for(Property prop : left.getProperties()){
                sfb.set(prop.getName(), prop.getValue());
            }

            for(Property prop : right.getProperties()){
                sfb.set(prop.getName(), prop.getValue());
            }

            final SimpleFeature candidate = sfb.buildFeature(leftId +" / "+ rightId);
            if(query.getFilter().evaluate(candidate)){
                return candidate;
            }else{
                //not a valid combinaison
                return null;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet on join queries.");
        }

    }

}
