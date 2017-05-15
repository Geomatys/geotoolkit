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
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.query.Join;
import org.geotoolkit.data.query.JoinType;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.data.query.Selector;
import org.geotoolkit.data.query.Source;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.PropertyName;
import org.apache.sis.internal.feature.AttributeConvention;

/**
 * FeatureCollection that takes it'es source from a join query.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultJoinFeatureCollection extends AbstractFeatureCollection{

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private final Query query;
    private final FeatureCollection leftCollection;
    private final FeatureCollection rightCollection;
    private FeatureType type = null;
    private FeatureType leftType = null;
    private FeatureType rightType = null;
    private GenericName leftName = null;
    private GenericName rightName = null;

    public DefaultJoinFeatureCollection(final String id, final Query query){
        super(id,query.getSource());

        final Source src = query.getSource();
        if(!(src instanceof Join)){
            throw new IllegalArgumentException("Query must have a join source.");
        }

        if(!QueryUtilities.isAbsolute(src)){
            throw new IllegalArgumentException("Query source must be absolute.");
        }

        final Join join = (Join) src;

        this.query = query;

        //todo must parse the filter on each selector
        leftCollection = QueryUtilities.evaluate("left", QueryBuilder.all(join.getLeft()));
        rightCollection = QueryUtilities.evaluate("right", QueryBuilder.all(join.getRight()));
    }

    @Override
    public Join getSource() {
        return (Join) super.getSource();
    }

    @Override
    public synchronized FeatureType getFeatureType() {
        if(type == null){
            leftType = leftCollection.getFeatureType();
            rightType = rightCollection.getFeatureType();
            leftName = (leftCollection.getSource() instanceof Selector) ?
                    NamesExt.valueOf(((Selector)leftCollection.getSource()).getSelectorName()) :
                    leftType.getName();
            rightName = (rightCollection.getSource() instanceof Selector) ?
                    NamesExt.valueOf(((Selector)rightCollection.getSource()).getSelectorName()) :
                    rightType.getName();

            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY).setDefaultValue("");
            ftb.addAssociation(leftType).setName(leftName).setMinimumOccurs(0).setMaximumOccurs(1);
            ftb.addAssociation(rightType).setName(rightName).setMinimumOccurs(0).setMaximumOccurs(1);
            ftb.setName(leftName.tip().toString() + '-' + rightName.tip());
            type = ftb.build();
        }

        return type;
    }

    /**
     * Agregate all feature from selectors to a single complex feature.
     *
     * @param candidates
     * @return aggregated features
     * @throws DataStoreException
     */
    private Feature toFeature(final Feature left, final Feature right) throws DataStoreException{
        final FeatureType type = getFeatureType(); //force creating type.
        final Feature f = type.newInstance();

        String id = "";

        if(left != null){
            id += left.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString());
            f.setPropertyValue(leftName.toString(),left);
        }
        if(right != null){
            if(left!=null) id += " ";
            id += right.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString());
            f.setPropertyValue(rightName.toString(),right);
        }

        f.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), id);
        return f;
    }

    @Override
    public FeatureCollection subCollection(final Query query) throws DataStoreException {
        final Query combine = QueryUtilities.subQuery(this.query, query);
        //the result should be an absolute query too.
        return QueryUtilities.evaluate("sub-"+getID(), combine);
    }

    @Override
    public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
        final JoinType jt = getSource().getJoinType();

        try{
            if(jt == JoinType.INNER){
                return new JoinInnerRowIterator(null);
            }else if(jt == JoinType.LEFT_OUTER){
                return new JoinOuterRowIterator(true,null);
            }else if(jt == JoinType.RIGHT_OUTER){
                return new JoinOuterRowIterator(false,null);
            }else{
                throw new IllegalArgumentException("Unknowned Join type : " + jt);
            }
        }catch(DataStoreException ex){
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public void update(final Filter filter, final Map<String, ?> values) throws DataStoreException {
        if(isWritable()){
            throw new UnsupportedOperationException("Not supported yet.");
        }else{
            throw new DataStoreException("Collection is not writable.");
        }
    }

    @Override
    public void remove(final Filter filter) throws DataStoreException {
        if(isWritable()){
            throw new UnsupportedOperationException("Not supported yet.");
        }else{
            throw new DataStoreException("Collection is not writable.");
        }
    }

    /**
     * Iterate on both collections with an Inner join condition.
     */
    private class JoinInnerRowIterator implements FeatureIterator{

        private final FeatureIterator leftIterator;
        private FeatureIterator rightIterator;
        private Feature leftFeature;
        private Feature combined;

        JoinInnerRowIterator(final Hints hints) throws DataStoreException{
            leftIterator = leftCollection.iterator();
        }

        @Override
        public Feature next() {
            try {
                searchNext();
            } catch (DataStoreException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            Feature f = combined;
            combined = null;
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
                throw new FeatureStoreRuntimeException(ex);
            }
            return combined != null;
        }

        private void searchNext() throws DataStoreException{
            if(combined != null) return;

            final PropertyIsEqualTo equal = getSource().getJoinCondition();
            final PropertyName leftProperty = (PropertyName) equal.getExpression1();
            final PropertyName rightProperty = (PropertyName) equal.getExpression2();

            //we might have several right features for one left
            if(leftFeature != null && rightIterator != null){
                while(combined== null && rightIterator.hasNext()){
                    final Feature rightRes = rightIterator.next();
                    combined = checkValid(leftFeature, rightRes);
                }
            }

            if(rightIterator==null && rightIterator != null){
                //no more results in right iterator, close iterator
                rightIterator.close();
                rightIterator = null;
            }

            while(combined==null && leftIterator.hasNext()){
                rightIterator = null;
                leftFeature = leftIterator.next();

                final Object leftValue = leftProperty.evaluate(leftFeature);

                if(rightIterator == null){
                    final QueryBuilder qb = new QueryBuilder();
                    qb.setSource(getSource().getRight());
                    qb.setFilter(FF.equals(rightProperty, FF.literal(leftValue)));
                    final Query rightQuery = qb.buildQuery();
                    rightIterator = rightCollection.subCollection(rightQuery).iterator();
                }

                while(combined== null && rightIterator.hasNext()){
                    final Feature rightRow = rightIterator.next();
                    combined = checkValid(leftFeature, rightRow);
                }

                if(combined==null){
                    //no more results in right iterator, close iterator
                    rightIterator.close();
                    rightIterator = null;
                }
            }

        }

        private Feature checkValid(final Feature left, final Feature right) throws DataStoreException{
            final Feature candidate = toFeature(left,right);

            if(query.getFilter().evaluate(candidate)){
                //combine both rows
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

    /**
     * Iterate on both collections with an outer join condition.
     */
    private class JoinOuterRowIterator implements FeatureIterator{

        private final FeatureIterator primeIterator;
        private FeatureIterator secondIterator;
        private final boolean left;
        private Feature primeFeature;
        private Feature nextFeature;

        JoinOuterRowIterator(final boolean left, final Hints hints) throws DataStoreException{
            this.left = left;
            if(left){
                primeIterator = leftCollection.iterator();
            }else{
                primeIterator = rightCollection.iterator();
            }

        }

        @Override
        public Feature next() {
            try {
                searchNext();
            } catch (DataStoreException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            Feature f = nextFeature;
            nextFeature = null;
            return f;
        }

        @Override
        public void close() {
            primeIterator.close();
            if(secondIterator != null){
                secondIterator.close();
            }
        }

        @Override
        public boolean hasNext() {
            try {
                searchNext();
            } catch (DataStoreException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            return nextFeature != null;
        }

        private void searchNext() throws DataStoreException{
            if(nextFeature != null) return;

            final PropertyIsEqualTo equal = getSource().getJoinCondition();
            final PropertyName leftProperty = (PropertyName) equal.getExpression1();
            final PropertyName rightProperty = (PropertyName) equal.getExpression2();

            //we might have several right features for one left
            if(primeFeature != null && secondIterator != null){
                while(nextFeature== null && secondIterator.hasNext()){
                    final Feature secondCandidate = secondIterator.next();
                    nextFeature = checkValid(primeFeature, secondCandidate, left);
                }
            }

            while(nextFeature==null && primeIterator.hasNext()){
                primeFeature = primeIterator.next();

                if(secondIterator != null){
                    secondIterator.close();
                    secondIterator = null;
                }

                final Object primeValue;
                if(left){
                    primeValue = leftProperty.evaluate(primeFeature);
                }else{
                    primeValue = rightProperty.evaluate(primeFeature);
                }

                if(secondIterator == null){
                    final QueryBuilder qb = new QueryBuilder();
                    if(left){
                        qb.setSource(getSource().getRight());
                        qb.setFilter(FF.equals(rightProperty, FF.literal(primeValue)));
                        secondIterator = rightCollection.subCollection(qb.buildQuery()).iterator();
                    }else{
                        qb.setSource(getSource().getLeft());
                        qb.setFilter(FF.equals(leftProperty, FF.literal(primeValue)));
                        secondIterator = leftCollection.subCollection(qb.buildQuery()).iterator();
                    }
                }

                while(nextFeature==null && secondIterator.hasNext()){
                    final Feature rightRow = secondIterator.next();
                    nextFeature = checkValid(primeFeature, rightRow,left);
                }

                if(nextFeature == null){
                    //outer left effect, no right match but still we must return the left side
                    if(left){
                        nextFeature = toFeature(primeFeature,null);
                    }else{
                        nextFeature = toFeature(null,primeFeature);
                    }
                }

            }

        }

        private Feature checkValid(final Feature left, final Feature right, final boolean leftJoin) throws DataStoreException{
            final Feature candidate;
            if(leftJoin){
                candidate = toFeature(left,right);
            }else{
                candidate = toFeature(right,left);
            }

            if(query.getFilter().evaluate(candidate)){
                //combine both rows
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
