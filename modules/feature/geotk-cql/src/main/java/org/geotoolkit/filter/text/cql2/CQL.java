/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.text.cql2;

import java.util.List;

import org.geotoolkit.filter.text.commons.CompilerUtil;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;


/**
 * Utility class to parse <b>CQL</b> predicates and expressions to GeoAPI
 * {@link Filter}s and {@link Expression}s, respectively.
 *
 * <p>
 * <b>CQL</b> is an acronym for OGC Common Query Language, a query predicate
 * language whose syntax is similar to a SQL WHERE clause, defined in clause
 * 6.2.2 of the OGC Catalog Service for Web, version 2.0.1 implementation
 * specification.
 * </p>
 * <p>
 * This class provides three methods, {@link #toFilter(String)},
 * {@link #toExpression(String)} and {@link #toFilterList(String)}; and an
 * overloaded version of each one for the user to provide a
 * {@link FilterFactory} implementation to use.
 * </p>
 * <p>
 * <h2>Usage</h2>
 * Here are some usage examples. Refer to the <a
 * href="http://docs.codehaus.org/display/GEOTOOLS/CQL+Parser+Design">complete
 * grammar</a> to see what exactly you can do.
 *
 *
 * <pre>
 * <code>
 * Filter f = CQL.toFilter(&quot;ATTR1 &lt; 10 AND ATTR2 &lt; 2 OR ATTR3 &gt; 10&quot;);
 *
 * Filter f = CQL.toFilter(&quot;NAME = 'New York' &quot;);
 *
 * Filter f = CQL.toFilter(&quot;NAME LIKE 'New%' &quot;);
 *
 * Filter f = CQL.toFilter(&quot;NAME IS NULL&quot;);
 *
 * Filter f = CQL.toFilter(&quot;DATE BEFORE 2006-11-30T01:30:00Z&quot;);
 *
 * Filter f = CQL.toFilter(&quot;NAME DOES-NOT-EXIST&quot;);
 *
 * Filter f = CQL.toFilter(&quot;QUANTITY BETWEEN 10 AND 20&quot;);
 *
 * Filter f = CQL.toFilter(&quot;CROSS(SHAPE, LINESTRING(1 2, 10 15))&quot;);
 *
 * Filter f = CQL.toFilter(&quot;BBOX(SHAPE, 10,20,30,40)&quot;);
 *
 * Expression e = CQL.toExpression(&quot;NAME&quot;);
 *
 * Expression e = CQL.toExpression(&quot;QUANTITY * 2&quot;);
 *
 * Expression e = CQL.toExpression(&quot;strConcat(NAME, 'suffix')&quot;);
 *
 * List filters = CQL.toFilterList(&quot;NAME IS NULL;BBOX(SHAPE, 10,20,30,40);INCLUDE&quot;);
 * </code>
 * </pre>
 *
 * @since 2.5
 * @author Mauricio Pazos (Axios Engineering)
 * @author Gabriel Roldan (Axios Engineering)
 */
public class CQL {
    private CQL() {
        // do nothing, private constructor
        // to indicate it is a pure utility class
    }

    /**
     * Parses the input string in OGC CQL format into a Filter, using the
     * systems default FilterFactory implementation.
     *
     * @param cqlPredicate
     *            a string containing a query predicate in OGC CQL format.
     * @return a {@link Filter} equivalent to the constraint specified in
     *         <code>cqlPredicate</code>.
     */
    public static Filter toFilter(final String cqlPredicate) throws CQLException {
        return toFilter(cqlPredicate, null);
    }

    /**
     * Parses the input string in OGC CQL format into a Filter, using the
     * provided FilterFactory.
     *
     * @param cqlPredicate
     *            a string containing a query predicate in OGC CQL format.
     * @param filterFactory
     *            the {@link FilterFactory} to use for the creation of the
     *            Filter. If it is null the method finds the default implementation.
     * @return a {@link Filter} equivalent to the constraint specified in
     *         <code>Predicate</code>.
     */
    public static Filter toFilter(final String cqlPredicate, final FilterFactory filterFactory)
            throws CQLException
    {
        return CompilerUtil.parseFilter(cqlPredicate, new CQLCompilerFactory(), filterFactory);
    }


    /**
     * Parses the input string in OGC CQL format into an Expression, using the
     * systems default {@link FilterFactory} implementation.
     *
     * @param cqlExpression
     *            a string containing an OGC CQL expression.
     * @return a {@link Expression} equivalent to the one specified in
     *         <code>cqlExpression</code>.
     */
    public static Expression toExpression(final String cqlExpression) throws CQLException {
        return toExpression(cqlExpression, null);
    }

    /**
     * Parses the input string in OGC CQL format into an {@link Expression}, using the
     * provided {@link FilterFactory}.
     *
     * @param cqlExpression
     *            a string containing a OGC CQL expression.
     *
     * @param filterFactory
     *            the {@link FilterFactory} to use for the creation of the
     *            Expression. If it is null the method finds the default implementation.
     * @return a {@link Filter} equivalent to the constraint specified in
     *         <code>cqlExpression</code>.
     */
    public static Expression toExpression(final String cqlExpression, final FilterFactory filterFactory)
            throws CQLException
    {
        return CompilerUtil.parseExpression(cqlExpression, new CQLCompilerFactory(), filterFactory);
    }

    /**
     * Parses the input string, which has to be a list of OGC CQL predicates
     * separated by <code>;</code> into a <code>List</code> of
     * <code>Filter</code>s, using the provided FilterFactory.
     *
     * @param cqlFilterList
     *            a list of OGC CQL predicates separated by "<code>;</code>"
     *
     * @return a {@link List} of {@link Filter}, one for each input CQL statement
     */
    public static List<Filter> toFilterList(final String cqlFilterList) throws CQLException {
        return toFilterList(cqlFilterList, null);
    }

    /**
     * WARNING THIS IS A WORK IN PROGRESS.
     *
     * @param filter
     * @return
     */
    public static String toCQL(final Filter filter) {
        final StringBuffer output = (StringBuffer) filter.accept(new FilterToCQL(), new StringBuffer());
        return output.toString();
    }
    /**
     * WARNING THIS IS A WORK IN PROGRESS.
     *
     * @param filter
     * @return
     */
    public static String toCQL(final Expression expression) {
        final StringBuffer output = (StringBuffer) expression.accept(new FilterToCQL(), new StringBuffer());
        return output.toString();
    }
    /**
     * Parses the input string which has to be a list of OGC CQL predicates
     * separated by "<code>;</code>" into a <code>List</code> of
     * <code>Filter</code>s, using the provided FilterFactory.
     *
     * @param cqlSequencePredicate
     *            a list of OGC CQL predicates separated by "<code>;</code>"
     *
     * @param filterFactory
     *            the {@link FilterFactory} to use for the creation of the
     *            Expression. If it is null the method finds the default implementation.
     * @return a List of {@link Filter}, one for each input CQL statement
     */
    public static List<Filter> toFilterList(final String cqlSequencePredicate, final FilterFactory filterFactory)
            throws CQLException
    {
        return CompilerUtil.parseFilterList(cqlSequencePredicate, new CQLCompilerFactory(), filterFactory);
    }
}
