/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;

import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class QueryUtilities {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    
    private QueryUtilities(){}

    public static boolean queryAll(Query query){

        return     query.retrieveAllProperties()
                && query.getCoordinateSystemReproject() == null
                && query.getCoordinateSystemReproject() == null
                && query.getFilter() == Filter.INCLUDE
                && query.getMaxFeatures() == null
                && query.getSortBy() == null
                && query.getStartIndex() == 0;
    }

    /**
     * Combine two queries in the way that the resulting query act
     * as if it was a sub query result. 
     * For exemple if the original query has a start index of 10 and the
     * subquery a start index of 5, the resulting startIndex will be 15.
     * The typename of the first query will override the one of the second.
     * 
     * @param original
     * @param second
     * @return sub query
     */
    public static Query subQuery(final Query original, final Query second){
        if ( original==null || second==null ) {
            throw new NullPointerException("Both query must not be null.");
        }

        final QueryBuilder qb = new QueryBuilder(original.getTypeName());

        //use the more restrictive max features field---------------------------
        Integer max = original.getMaxFeatures();
        if(second.getMaxFeatures() != null){
            if(max == null){
                max = second.getMaxFeatures();
            }else{
                max = Math.min(max, second.getMaxFeatures());
            }
        }
        qb.setMaxFeatures(max);

        //join attributes names-------------------------------------------------
        final String[] propNames = joinAttributes(
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
        int start = original.getStartIndex() + second.getStartIndex();
        qb.setStartIndex(start);

        //hints of the second query
        qb.setHints(second.getHints());

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
     * filter: the filtets of both queries are or'ed
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
     * @param secondQuery econd query
     *
     * @return Query restricted to the limits of definitionQuery
     *
     * @throws NullPointerException if some of the queries is null
     * @throws IllegalArgumentException if the type names of both queries do
     *         not match
     */
    public static Query mixQueries(final Query firstQuery, final Query secondQuery) {
        if (  firstQuery==null || secondQuery==null ) {
            throw new NullPointerException("Both query must not be null.");
        }

        if ((firstQuery.getTypeName() != null) && (secondQuery.getTypeName() != null)) {
            if (!firstQuery.getTypeName().equals(secondQuery.getTypeName())) {
                String msg = "Type names do not match: " + firstQuery.getTypeName() + " != " + secondQuery.getTypeName();
                throw new IllegalArgumentException(msg);
            }
        }


        //none of the queries equals Query.ALL, mix them
        //use the more restrictive max features field
        final int maxFeatures = Math.min(firstQuery.getMaxFeatures(),
                secondQuery.getMaxFeatures());

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

        int start = firstQuery.getStartIndex() + secondQuery.getStartIndex();
        //build the mixed query
        final Name typeName = firstQuery.getTypeName() != null ?
            firstQuery.getTypeName() :
            secondQuery.getTypeName();

        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(typeName);
        builder.setFilter(filter);
        builder.setMaxFeatures(maxFeatures);
        builder.setProperties(propNames);
        builder.setStartIndex(start);
        
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

}
