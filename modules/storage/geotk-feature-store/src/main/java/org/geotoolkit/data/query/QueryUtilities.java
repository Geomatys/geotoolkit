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

package org.geotoolkit.data.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.util.NullArgumentException;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class QueryUtilities {

    private static final FilterFactory FF = DefaultFactories.forBuildin(FilterFactory.class);

    private QueryUtilities(){}

    public static boolean queryAll(final Query query){
        return     query.retrieveAllProperties()
                && query.getCoordinateSystemReproject() == null
                && query.getCoordinateSystemReproject() == null
                && query.getFilter() == Filter.INCLUDE
                && query.getLimit() == -1
                && query.getSortBy() == null
                && query.getOffset() == 0;
    }

    /**
     * Combine two queries in the way that the resulting query act
     * as if it was a sub query result.
     * For example if the original query has a start index of 10 and the
     * sub-query a start index of 5, the resulting startIndex will be 15.
     * The type name of the first query will override the one of the second.
     *
     * @param original
     * @param second
     * @return sub query
     */
    public static Query subQuery(final Query original, final Query second){
        if ( original==null || second==null ) {
            throw new NullArgumentException("Both query must not be null.");
        }

        final QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(original.getTypeName());

        //use the more restrictive max features field---------------------------
        long max = original.getLimit();
        if (second.getLimit() != -1) {
            if(max == -1){
                max = second.getLimit();
            }else{
                max = Math.min(max, second.getLimit());
            }
        }
        qb.setLimit(max);

        //join attributes names-------------------------------------------------
        final String[] propNames = retainAttributes(
                original.getPropertyNames(),
                second.getPropertyNames());
        qb.setProperties(propNames);

        //use second crs over original crs--------------------------------------
        if(second.getCoordinateSystemReproject() != null){
            qb.setCRS(second.getCoordinateSystemReproject());
        }else{
            qb.setCRS(original.getCoordinateSystemReproject());
        }

        //join filters----------------------------------------------------------
        Filter filter = original.getFilter();
        Filter filter2 = second.getFilter();

        if ( filter.equals(Filter.INCLUDE) ){
            filter = filter2;
        } else if ( !filter2.equals(Filter.INCLUDE) ){
            filter = FF.and(filter, filter2);
        }
        qb.setFilter(filter);

        //group start index ----------------------------------------------------
        long start = original.getOffset() + second.getOffset();
        qb.setOffset(start);

        //ordering -------------------------------------------------------------
        final List<SortBy> sorts = new ArrayList<SortBy>();
        SortBy[] sts = original.getSortBy();
        if(sts != null){
            sorts.addAll(Arrays.asList(sts));
        }

        sts = second.getSortBy();
        if(sts != null){
            sorts.addAll(Arrays.asList(sts));
        }

        if(sorts != null){
            qb.setSortBy(sorts.toArray(new SortBy[sorts.size()]));
        }

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

        return qb.buildQuery();
    }

    /**
     * Takes two {@link Query}objects and produce a new one by mixing the
     * restrictions of both of them.
     *
     * <p>
     * The policy to mix the queries components is the following:
     *
     * <ul>
     * <li>
     * typeName: type names MUST match (not checked if some or both queries
     * equals to <code>Query.ALL</code>)
     * </li>
     * <li>
     * handle: you must provide one since no sensible choice can be done
     * between the handles of both queries
     * </li>
     * <li>
     * maxFeatures: the lower of the two maxFeatures values will be used (most
     * restrictive)
     * </li>
     * <li>
     * attributeNames: the attributes of both queries will be joined in a
     * single set of attributes. IMPORTANT: only <b><i>explicitly</i></b>
     * requested attributes will be joint, so, if the method
     * <code>retrieveAllProperties()</code> of some of the queries returns
     * <code>true</code> it does not means that all the properties will be
     * joined. You must create the query with the names of the properties you
     * want to load.
     * </li>
     * <li>
     * filter: the filters of both queries are or'ed
     * </li>
     * <li>
     * <b>any other query property is ignored</b> and no guarantees are made of
     * their return values, so client code shall explicitly care of hints, startIndex, etc.,
     * if needed.
     * </li>
     * </ul>
     * </p>
     *
     * @param firstQuery first query
     * @param secondQuery second query
     *
     * @return Query restricted to the limits of definitionQuery
     *
     * @throws NullPointerException if some of the queries is null
     * @throws IllegalArgumentException if the type names of both queries do
     *         not match
     */
    public static Query mixQueries(final Query firstQuery, final Query secondQuery) {
        if ( firstQuery==null || secondQuery==null ) {
            throw new NullArgumentException("Both query must not be null.");
        }

        if ((firstQuery.getTypeName() != null) && (secondQuery.getTypeName() != null)) {
            if (!firstQuery.getTypeName().equals(secondQuery.getTypeName())) {
                String msg = "Type names do not match: " + firstQuery.getTypeName() + " != " + secondQuery.getTypeName();
                throw new IllegalArgumentException(msg);
            }
        }


        //none of the queries equals Query.ALL, mix them
        //use the more restrictive max features field
        final long maxFeatures = Math.min(firstQuery.getLimit(),
                secondQuery.getLimit());

        //join attributes names
        final String[] propNames = joinAttributes(firstQuery.getPropertyNames(),
                secondQuery.getPropertyNames());

        //join filters
        Filter filter = firstQuery.getFilter();
        Filter filter2 = secondQuery.getFilter();

        if ((filter == null) || filter.equals(Filter.INCLUDE)) {
            filter = filter2;
        } else if ((filter2 != null) && !filter2.equals(Filter.INCLUDE)) {
            filter = FF.and(filter, filter2);
        }

        long start = firstQuery.getOffset() + secondQuery.getOffset();
        //build the mixed query
        final String typeName = firstQuery.getTypeName() != null ?
            firstQuery.getTypeName() :
            secondQuery.getTypeName();

        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(typeName);
        builder.setFilter(filter);
        builder.setLimit(maxFeatures);
        builder.setProperties(propNames);
        builder.setOffset(start);

        //mix versions, second query version takes precedence.
        if(firstQuery.getVersionDate()!=null) builder.setVersionDate(firstQuery.getVersionDate());
        if(firstQuery.getVersionLabel()!=null) builder.setVersionLabel(firstQuery.getVersionLabel());
        if(secondQuery.getVersionDate()!=null) builder.setVersionDate(secondQuery.getVersionDate());
        if(secondQuery.getVersionLabel()!=null) builder.setVersionLabel(secondQuery.getVersionLabel());

        return builder.buildQuery();
    }

    /**
     * Creates a set of attribute names from the two input lists of names,
     * maintaining the order of the first list and appending the non repeated
     * names of the second.
     * <p>
     * In the case where both lists are <code>null</code>, <code>null</code>
     * is returned.
     * </p>
     *
     * @param atts1 the first list of attribute names, who's order will be
     *        maintained
     * @param atts2 the second list of attribute names, from wich the non
     *        repeated names will be appended to the resulting list
     *
     * @return Set of attribute names from <code>atts1</code> and
     *         <code>atts2</code>
     */
    private static String[] joinAttributes(final String[] atts1, final String[] atts2) {
        if (atts1 == null && atts2 == null) {
            return null;
        }

        final List atts = new LinkedList();

        if (atts1 != null) {
            atts.addAll(Arrays.asList(atts1));
        }

        if (atts2 != null) {
            for (int i = 0; i < atts2.length; i++) {
                if (!atts.contains(atts2[i])) {
                    atts.add(atts2[i]);
                }
            }
        }

        final String[] propNames = new String[atts.size()];
        atts.toArray(propNames);

        return propNames;
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


}
