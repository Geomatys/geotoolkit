/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

package org.geotoolkit.storage.feature.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.geometry.ImmutableEnvelope;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.SortBy;
import org.opengis.filter.SortProperty;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.geometry.Envelope;
import org.opengis.util.CodeList;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class QueryUtilities {

    private static final FilterFactory FF = FilterUtilities.FF;
    /**
     * Flag envelope used to indicate filter envelope is irrevelant.
     * This can be caused by expressions such as 'BBOX(...) OR att1 = value'.
     */
    public static final ImmutableEnvelope NO_EVAL = ImmutableEnvelope.castOrCopy(new GeneralEnvelope(2));

    private QueryUtilities(){}

    public static boolean queryAll(final Query query){
        return     query.retrieveAllProperties()
                && (query.getSelection() == null || query.getSelection() == Filter.include())
                && !query.getLimit().isPresent()
                && query.getSortBy() == null
                && query.getOffset() == 0;
    }

    /**
     * Combine two queries in the way that the resulting query act
     * as if it was a sub query result.
     * For example if the original query has a start index of 10 and the
     * sub-query a start index of 5, the resulting startIndex will be 15.
     */
    public static FeatureQuery subQuery(final FeatureQuery original, final FeatureQuery second) {
        ArgumentChecks.ensureNonNull("original", original);
        ArgumentChecks.ensureNonNull("second", second);

        final FeatureQuery qb = new FeatureQuery();

        //use the more restrictive max features field---------------------------
        long max = original.getLimit().orElse(-1);
        if (second.getLimit().isPresent()) {
            if (max == -1) {
                max = second.getLimit().getAsLong();
            } else {
                max = Math.min(max, second.getLimit().getAsLong());
            }
        }
        qb.setLimit(max);

        //join attributes names-------------------------------------------------
        final FeatureQuery.NamedExpression[] columnsOrig = original.getProjection();
        final FeatureQuery.NamedExpression[] columnsSecond = original.getProjection();
        if (columnsOrig == null) {
            if (columnsSecond != null) {
                qb.setProjection(columnsSecond);
            }
        } else {
            throw new UnsupportedOperationException();
        }

        //join filters----------------------------------------------------------
        Filter filter = original.getSelection();
        Filter filter2 = second.getSelection();
        if (filter == null) filter = Filter.include();
        if (filter2 == null) filter2 = Filter.include();

        if ( filter.equals(Filter.include()) ){
            filter = filter2;
        } else if ( !filter2.equals(Filter.include()) ){
            filter = FF.and(filter, filter2);
        }
        qb.setSelection(filter);

        //group start index ----------------------------------------------------
        long start = original.getOffset() + second.getOffset();
        qb.setOffset(start);

        //ordering -------------------------------------------------------------
        final List<SortProperty> sorts = new ArrayList<>();
        SortProperty[] sts = getSortProperties(original.getSortBy());
        if (sts != null) {
            sorts.addAll(Arrays.asList(sts));
        }

        sts = getSortProperties(second.getSortBy());
        if (sts != null) {
            sorts.addAll(Arrays.asList(sts));
        }
        qb.setSortBy(sorts.toArray(new SortProperty[sorts.size()]));


        //copy the resolution parameter-----------------------------------------
        final Quantity<Length> resFirst = original.getLinearResolution();
        final Quantity<Length> resSecond = second.getLinearResolution();
        if (resFirst == null) {
            qb.setLinearResolution(resSecond);
        } else {
            qb.setLinearResolution(resFirst);
        }

        return qb;
    }

    /**
     * Combine two queries in the way that the resulting query act
     * as if it was a sub query result.
     * For example if the original query has a start index of 10 and the
     * sub-query a start index of 5, the resulting startIndex will be 15.
     * The type name of the first query will override the one of the second.
     */
    public static Query subQuery(final Query original, final Query second){
        if ( original==null || second==null ) {
            throw new NullArgumentException("Both query must not be null.");
        }

        final Query qb = new Query();
        qb.setTypeName(original.getTypeName());

        //use the more restrictive max features field---------------------------
        long max = original.getLimit().orElse(-1);
        if (second.getLimit().isPresent()) {
            if(max == -1){
                max = second.getLimit().getAsLong();
            }else{
                max = Math.min(max, second.getLimit().getAsLong());
            }
        }
        if (max >= 0) {
            qb.setLimit(max);
        }

        //join attributes names-------------------------------------------------
        final String[] propNames = retainAttributes(
                original.getPropertyNames(),
                second.getPropertyNames());
        qb.setProperties(propNames);

        //join filters----------------------------------------------------------
        Filter filter = original.getSelection();
        Filter filter2 = second.getSelection();
        if (filter == null) filter = Filter.include();
        if (filter2 == null) filter2 = Filter.include();

        if ( filter.equals(Filter.include()) ){
            filter = filter2;
        } else if ( !filter2.equals(Filter.include()) ){
            filter = FF.and(filter, filter2);
        }
        qb.setSelection(filter);

        //group start index ----------------------------------------------------
        long start = original.getOffset() + second.getOffset();
        qb.setOffset(start);

        //ordering -------------------------------------------------------------
        final List<SortProperty> sorts = new ArrayList<>();
        SortProperty[] sts = getSortProperties(original.getSortBy());
        if(sts != null){
            sorts.addAll(Arrays.asList(sts));
        }

        sts = getSortProperties(second.getSortBy());
        if(sts != null){
            sorts.addAll(Arrays.asList(sts));
        }

        qb.setSortBy(sorts.toArray(new SortProperty[sorts.size()]));

        //hints of the second query---------------------------------------------
        qb.setHints(second.getHints());

        //copy the resolution parameter-----------------------------------------
        final double[] resFirst = original.getResolution();
        final double[] resSecond = second.getResolution();
        if(resFirst == null || Double.isNaN(resFirst[0])){
            qb.setResolution(resSecond);
        }else{
            qb.setResolution(resFirst);
        }


        //mix versions, second query version takes precedence.
        if(original.getVersionDate()!=null) qb.setVersionDate(original.getVersionDate());
        if(original.getVersionLabel()!=null) qb.setVersionLabel(original.getVersionLabel());
        if(second.getVersionDate()!=null) qb.setVersionDate(second.getVersionDate());
        if(second.getVersionLabel()!=null) qb.setVersionLabel(second.getVersionLabel());

        return qb;
    }

    /**
     * Creates a set of attribute names from the two input lists of names,
     * while keep only the attributes from the second list
     */
    private static String[] retainAttributes(final String[] atts1, final String[] atts2) {
        if (atts1 == null && atts2 == null) {
            return null;
        }

        if(atts1 == null){
            return atts2;
        }
        if(atts2 == null){
            return atts1;
        }

        final List atts = new LinkedList();

        final List lst1 = UnmodifiableArrayList.wrap(atts1);
        for (int i = 0; i < atts2.length; i++) {
            if (lst1.contains(atts2[i])) {
                atts.add(atts2[i]);
            }
        }

        final String[] propNames = new String[atts.size()];
        atts.toArray(propNames);

        return propNames;
    }

    public static <R> SortProperty<R>[] getSortProperties(final SortBy<R> sortBy) {
        if (sortBy == null) {
            return new SortProperty[0];
        }
        List<? extends SortProperty<R>> p = sortBy.getSortProperties();
        return p.toArray(new SortProperty[p.size()]);
    }


    /**
     * Extract query filter envelope.
     *
     * @param filter can be null
     * @return Envelope, or null if filter is null or no envelope is defined.
     *         NO_EVAL if envelope information is irrevelant.
     */
    public static Envelope extractEnvelope(Filter<? super Feature> filter) {
        if (filter == null) return null;
        final CodeList<?> type = filter.getOperatorType();

        if (SpatialOperatorName.BBOX.equals(type)) {
            return (Envelope) filter.getExpressions().get(1).apply(null);
        } else if (LogicalOperatorName.AND.equals(type)) {
            //compute the intersection of all envelopes
            final LogicalOperator<Feature> lo = (LogicalOperator) filter;
            Envelope env = null;
            for (Filter<? super Feature> operand : lo.getOperands()) {
                final Envelope e = extractEnvelope(operand);
                if (e != null) {
                    if (e == NO_EVAL) {
                        continue;
                    } else if (env == null) {
                        env = e;
                    } else {
                        final GeneralEnvelope diff = new GeneralEnvelope(env);
                        diff.intersect(e);
                        env = diff;
                    }
                }
            }
            return env;
        } else if (LogicalOperatorName.OR.equals(type)) {
            //combine all envelopes if all expressions are envelopes
            final LogicalOperator<Feature> lo = (LogicalOperator) filter;
            Envelope env = null;
            for (Filter<? super Feature> operand : lo.getOperands()) {
                final Envelope e = extractEnvelope(operand);
                if (e != null) {
                    if (e == NO_EVAL) {
                        return NO_EVAL;
                    } else if (env == null) {
                        env = e;
                    } else {
                        GeneralEnvelope diff = new GeneralEnvelope(env);
                        diff.add(e);
                        env = diff;
                    }
                } else {
                    //an OR component that could be anything
                    return NO_EVAL;
                }
            }
            return env;
        }  else if (LogicalOperatorName.NOT.equals(type)) {
            final LogicalOperator<Feature> lo = (LogicalOperator) filter;
            return NO_EVAL;
        }
        return null;
    }
}
