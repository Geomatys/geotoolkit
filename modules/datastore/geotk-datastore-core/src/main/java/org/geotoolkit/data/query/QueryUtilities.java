/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.data.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.geotoolkit.factory.FactoryFinder;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 *
 * @author sorel
 */
public class QueryUtilities {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    
    private QueryUtilities(){}

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
     * @param firstQuery Query against this DataStore
     * @param secondQuery DOCUMENT ME!
     * @param handle DOCUMENT ME!
     *
     * @return Query restricted to the limits of definitionQuery
     *
     * @throws NullPointerException if some of the queries is null
     * @throws IllegalArgumentException if the type names of both queries do
     *         not match
     */
    public static Query mixQueries(final Query firstQuery, final Query secondQuery, final String handle) {
        if ((firstQuery == null) || (secondQuery == null)) {
            throw new NullPointerException("got a null query argument");
        }

        if (firstQuery.equals(Query.ALL)) {
            return secondQuery;
        } else if (secondQuery.equals(Query.ALL)) {
            return firstQuery;
        }

        if ((firstQuery.getTypeName() != null) && (secondQuery.getTypeName() != null)) {
            if (!firstQuery.getTypeName().equals(secondQuery.getTypeName())) {
                String msg = "Type names do not match: " + firstQuery.getTypeName() + " != " + secondQuery.getTypeName();
                throw new IllegalArgumentException(msg);
            }
        }

        // mix versions, if possible
        final String version;
        if (firstQuery.getVersion() != null) {
            if (secondQuery.getVersion() != null && !secondQuery.getVersion().equals(firstQuery.getVersion())) {
                throw new IllegalArgumentException("First and second query refer different versions");
            }
            version = firstQuery.getVersion();
        } else {
            version = secondQuery.getVersion();
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
        Integer start = 0;
        if (firstQuery.getStartIndex() != null) {
            start = firstQuery.getStartIndex();
        }
        if (secondQuery.getStartIndex() != null) {
            start += secondQuery.getStartIndex();
        }
        //build the mixed query
        final String typeName = firstQuery.getTypeName() != null ? firstQuery.getTypeName() : secondQuery.getTypeName();

        final DefaultQuery mixed = new DefaultQuery(typeName, filter, maxFeatures, propNames, handle);
        mixed.setVersion(version);
        if (start != 0) {
            mixed.setStartIndex(start);
        }
        return mixed;
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
