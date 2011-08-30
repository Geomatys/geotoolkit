/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.text.commons;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.geotoolkit.filter.IllegalFilterException;
import org.geotoolkit.filter.function.other.OtherFunctionFactory;
import org.geotoolkit.filter.text.cql2.CQLException;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.filter.And;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Not;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.BinaryExpression;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.DistanceBufferOperator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;


/**
 *
 * This abstract class provides the common behavior to build the filters for the related
 * semantic actions of parsing language process.
 *
 * <p>
 * Builds Filter or Expression and their components (literal, functions, etc).
 * It maintains the results of semantic actions in the stack used to build complex
 * filters and expressions.
 * </p>
 *
 * <p>
 * Warning: This component is not published. It is part of module implementation.
 * Client module should not use this feature.
 * </p>
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.6
 */
public abstract class AbstractFilterBuilder {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private final FilterFactory filterFactory;

    private final BuildResultStack resultStack;

    protected final String cqlSource;

    /**
     * New instance of FilterBuilder
     * @param cqlSource
     * @param filterFactory
     */
    public AbstractFilterBuilder(final String cqlSource, final FilterFactory filterFactory) {
        assert cqlSource != null : "illegal argument";
        assert filterFactory != null : "illegal argument";

        this.cqlSource = cqlSource;
        this.filterFactory = filterFactory;

        this.resultStack = new BuildResultStack(cqlSource);

    }

    protected FilterFactory getFilterFactory() {
        return filterFactory;
    }

    protected final BuildResultStack getResultStack() {
        return resultStack;
    }

    protected final String getStatement() {
        return cqlSource;
    }

    public Filter getFilter() throws CQLException {
        return resultStack.popFilter();
    }

    public Expression getExpression() throws CQLException {
        return resultStack.popExpression();
    }

    public List<Filter> getFilterList() throws CQLException {

        final int size = resultStack.size();
        final List<Filter> results = new ArrayList<Filter>(size);

        for (int i = 0; i < size; i++) {
            final Result item = resultStack.popResult();
            final Filter result = (Filter) item.getBuilt();
            results.add(0, result);
        }

        return results;
    }

    public BinaryExpression buildAddExpression() throws CQLException {

        final Expression right = resultStack.popExpression();
        final Expression left  = resultStack.popExpression();

        return filterFactory.add(left, right);
    }

    public BinaryExpression buildSubtractExression() throws CQLException {
        final Expression right = resultStack.popExpression();
        final Expression left  = resultStack.popExpression();

        return filterFactory.subtract(left, right);
    }

    public BinaryExpression buildMultiplyExpression() throws CQLException {

        final Expression right = resultStack.popExpression();
        final Expression left  = resultStack.popExpression();

        return filterFactory.multiply(left, right);
    }

    public BinaryExpression buildDivideExpression() throws CQLException {

        final Expression right = resultStack.popExpression();
        final Expression left  = resultStack.popExpression();

        return filterFactory.divide(left, right);
    }

    public Filter buildAndFilter() throws CQLException {

        final Filter right = resultStack.popFilter();
        final Filter left  = resultStack.popFilter();

        final Filter logicFilter;
        if (Filter.INCLUDE.equals(right)) {
            logicFilter = left;
        } else if (Filter.INCLUDE.equals(left)) {
            logicFilter = right;
        } else if (Filter.EXCLUDE.equals(right) || Filter.EXCLUDE.equals(left)) {
            logicFilter = Filter.EXCLUDE;
        } else {
            logicFilter = filterFactory.and(left, right);
        }
        return logicFilter;

    }

    public Filter buildOrFilter() throws CQLException {
        final Filter right = resultStack.popFilter();
        final Filter left  = resultStack.popFilter();

        final Filter logicFilter;
        if (Filter.INCLUDE.equals(right) || Filter.INCLUDE.equals(left)) {
            logicFilter = Filter.INCLUDE;
        } else if (Filter.EXCLUDE.equals(left)) {
            logicFilter = right;
        } else if (Filter.EXCLUDE.equals(right)) {
            logicFilter = left;
        } else {
            logicFilter = filterFactory.or(left, right);
        }

        return logicFilter;
    }

    public Filter buildNotFilter() throws CQLException {

        final Filter right = resultStack.popFilter();

        final Filter logicFilter;

        if (Filter.INCLUDE.equals(right)) {
            logicFilter = Filter.EXCLUDE;
        } else if (Filter.EXCLUDE.equals(right)) {
            logicFilter = Filter.INCLUDE;
        } else {
            logicFilter = filterFactory.not(right);
        }

        return logicFilter;
    }

    /**
     * Bulds a like filter
     *
     * @return a PropertyIsLike
     * @throws CQLException
     */
    public PropertyIsLike buildLikeFilter() throws CQLException {
        final String WC_MULTI = "%";
        final String WC_SINGLE = "_";
        final String ESCAPE = "\\";

        try {
            final Expression pattern = resultStack.popExpression();
            final Expression expr    = resultStack.popExpression();
            return filterFactory.like(expr, pattern.toString(),
                    WC_MULTI, WC_SINGLE, ESCAPE);
        } catch (IllegalFilterException ife) {
            throw new CQLException("Exception building LikeFilter: " + ife.getMessage(), this.cqlSource);
        }
    }

    /**
     * Builds property is null filter
     *
     * @return PropertyIsNull
     * @throws CQLException
     */
    public PropertyIsNull buildPropertyIsNull() throws CQLException {
        try {
            final Expression property = resultStack.popExpression();
            return filterFactory.isNull(property);
        } catch (CQLException e) {
            throw new CQLException("Exception building Null Predicate", this.cqlSource);
        }
    }

    public Not buildPorpertyNotIsNull() throws CQLException {
        return filterFactory.not(this.buildPropertyIsNull());
    }

    /**
     * builds PropertyIsBetween filter
     *
     * @return PropertyIsBetween
     * @throws CQLException
     */
    public PropertyIsBetween buildBetween() throws CQLException {
        try {
            final Expression sup = resultStack.popExpression();
            final Expression inf = resultStack.popExpression();
            final Expression expr = resultStack.popExpression();

            return filterFactory.between(expr, inf, sup);
        } catch (IllegalFilterException ife) {
            throw new CQLException("Exception building CompareFilter: " + ife.getMessage(), this.cqlSource);
        }
    }

    public Not buildNotBetween() throws CQLException {
        return filterFactory.not(buildBetween());
    }

    public Not buildNotLikeFilter() throws CQLException {
        return filterFactory.not(buildLikeFilter());
    }

    /**
     * Creates PropertyIsEqualTo with PropertyExists predicate
     *
     * @return PropertyIsEqualTo
     * @throws CQLException
     */
    public PropertyIsEqualTo buildPropertyExists() throws CQLException {

        final PropertyName property = resultStack.popPropertyName();

        final Expression[] args = new Expression[1];
        args[0] = filterFactory.literal(property.getPropertyName());

        final Function function = filterFactory.function(OtherFunctionFactory.PROPERTY_EXISTS, args);
        final Literal literalTrue = filterFactory.literal(Boolean.TRUE);

        return filterFactory.equals(function, literalTrue);
    }

    /**
     * Creates a literal with date time
     *
     * @param token with date time
     * @return Literal
     * @throws CQLException
     */
    public Literal buildDateTimeExpression(final IToken token) throws CQLException {
        try {
            final DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            final Date dateTime = formatter.parse(token.toString());
            return filterFactory.literal(dateTime);
        } catch (ParseException e) {
            throw new CQLException("Unsupported date time format: " + e.getMessage(), cqlSource);
        }
    }

    public Not buildNotFilter(final Filter eq) {
        return filterFactory.not(eq);
    }

    public Literal buildTrueLiteral() {
        return filterFactory.literal(Boolean.TRUE);
    }

    public Literal buildFalseLiteral() {
        return filterFactory.literal(Boolean.FALSE);
    }

    public Literal buildLiteralInteger(final String image) {
        return filterFactory.literal(Integer.parseInt(image));
    }

    public Literal buildLiteralDouble(final String tokenImage) {
        return filterFactory.literal(Double.parseDouble(tokenImage));
    }

    public Literal buildLiteralString(final String tokenImage) {
        final String strLiteral = removeQuotes(tokenImage);
        return filterFactory.literal(strLiteral);
    }

    /**
     * Removes initial and final "'" from string. If some "''" is found
     * it will be changed by a single quote "'".
     *
     * @param source
     * @return string without initial and final quote, and "''" replaced
     * by "'".
     */
    protected String removeQuotes(final String source) {
        // checks if it has initial an final quote
        final String quote = "'";
        if (!(source.startsWith(quote) && source.endsWith(quote))) {
            return source;
        }

        final int length = source.length();

        // removes the first and last quote
        String result = source.substring(1, length - 1);
        // removes internal quotes
        result = result.replaceAll("''", "'");

        return result;
    }

    public String buildIdentifier(final int nodeIdentifier) throws CQLException {
        // precondition: the stack have one or more identifier part parts
        try {
            // retrieves all part of identifier from result stack
            final ArrayList<String> arrayParts = new ArrayList<String>();

            while (resultStack.size() > 0) {
                final Result r = resultStack.peek();

                if (r.getNodeType() != nodeIdentifier) {
                    break;
                }
                final String part = resultStack.popIdentifierPart();
                arrayParts.add(part);
            }
            assert arrayParts.size() >= 1 : "postcondition: the list of identifier part must have one or more elements ";

            // makes the identifier
            final StringBuffer identifier = new StringBuffer(100);
            String part;

            int i = 0;

            for (i = arrayParts.size() - 1; i > 0; i--) {
                part = (String) arrayParts.get(i);
                identifier.append(part).append(":");
            } // postcondition i=0

            part = arrayParts.get(i);
            identifier.append(part);

            return identifier.toString();

        } catch (CQLException e) {
            throw new CQLException("Fail builing identifier: " + e.getMessage(), this.cqlSource);
        }
    }

    /**
     * Creates the identifier part. An identifier like "idpart1:idpart2:idpart3:
     * ... idpartN" has N part.
     *
     * @return identifier part
     */
    public String buildIdentifierPart(final IToken token) {
        return token.toString();
    }

    public PropertyName buildSimpleAttribute() throws CQLException {
        // Only retrieve the identifier built before
        final String identifier = resultStack.popIdentifier();
        return filterFactory.property(identifier);
    }

    /**
     *
     * @param nodeSimpleAttr
     * @param nodeAttrSeparator
     * @return PropertyName
     * @throws CQLException
     */
    public PropertyName buildCompoundAttribute(final int nodeSimpleAttr, final String nodeAttrSeparator)
            throws CQLException
    {

        final ArrayList<String> arrayIdentifiers = new ArrayList<String>();

        // precondition: stack has one or more simple attributes
        while (resultStack.size() > 0) {
            final Result r = resultStack.peek();

            if (r.getNodeType() != nodeSimpleAttr) {
                break;
            }

            final PropertyName simpleAttribute = resultStack.popPropertyName();
            arrayIdentifiers.add(simpleAttribute.getPropertyName());
        }

        // postcondition: array has one or more simple attribute
        final StringBuffer attribute = new StringBuffer(100);
        int i = 0;

        for (i = arrayIdentifiers.size() - 1; i > 0; i--) {
            attribute.append(arrayIdentifiers.get(i));
            attribute.append(nodeAttrSeparator);
        }

        attribute.append(arrayIdentifiers.get(i));

        return filterFactory.property(attribute.toString());
    }

    public Literal buildDistanceUnit(final IToken token) throws CQLException {
        return filterFactory.literal(token.toString());
    }

    public Literal buildTolerance() throws CQLException {
        try {
            return resultStack.popLiteral();
        } catch (NumberFormatException e) {
            throw new CQLException("Unsupported number format", cqlSource);
        }
    }

    public BinarySpatialOperator buildSpatialEqualFilter() throws CQLException {
        final Literal geom = resultStack.popLiteral();
        final Expression property = resultStack.popExpression();
        final FilterFactory2 ff = (FilterFactory2) filterFactory; // TODO this cast must be removed. It depends of Geometry implementation

        return ff.equal(property, geom);
    }

    public BinarySpatialOperator buildSpatialDisjointFilter() throws CQLException {
        final Literal geom = resultStack.popLiteral();
        final Expression property = resultStack.popExpression();
        final FilterFactory2 ff = (FilterFactory2) filterFactory; // TODO this cast must be removed. It depends of Geometry implementation

        return ff.disjoint(property, geom);
    }

    public BinarySpatialOperator buildSpatialIntersectsFilter() throws CQLException {
        final Literal geom = resultStack.popLiteral();
        final Expression property = resultStack.popExpression();
        final FilterFactory2 ff = (FilterFactory2) filterFactory; // TODO this cast must be removed. It depends of Geometry implementation

        return ff.intersects(property, geom);
    }

    public BinarySpatialOperator buildSpatialTouchesFilter() throws CQLException {
        final Literal geom = resultStack.popLiteral();
        final Expression property = resultStack.popExpression();
        final FilterFactory2 ff = (FilterFactory2) filterFactory; // TODO this cast must be removed. It depends of Geometry implementation

        return ff.touches(property, geom);
    }

    public BinarySpatialOperator buildSpatialCrossesFilter() throws CQLException {
        final Literal geom = resultStack.popLiteral();
        final Expression property = resultStack.popExpression();
        final FilterFactory2 ff = (FilterFactory2) filterFactory; // TODO this cast must be removed. It depends of Geometry implementation

        return ff.crosses(property, geom);
    }

    public BinarySpatialOperator buildSpatialWithinFilter() throws CQLException {
        final Literal geom = resultStack.popLiteral();
        final Expression property = resultStack.popExpression();
        final FilterFactory2 ff = (FilterFactory2) filterFactory; // TODO this cast must be removed. It depends of Geometry implementation

        return ff.within(property, geom);
    }

    public BinarySpatialOperator buildSpatialContainsFilter() throws CQLException {
        final Literal geom = resultStack.popLiteral();
        final Expression property = resultStack.popExpression();
        final FilterFactory2 ff = (FilterFactory2) filterFactory; // TODO this cast must be removed. It depends of Geometry implementation

        return ff.contains(property, geom);

    }

    public BinarySpatialOperator buildSpatialOverlapsFilter() throws CQLException {
        final Literal geom = resultStack.popLiteral();
        final Expression property = resultStack.popExpression();
        final FilterFactory2 ff = (FilterFactory2) filterFactory; // TODO this cast must be removed. It depends of Geometry implementation

        return ff.overlaps(property, geom);
    }

    public BBOX buildBBox() throws CQLException {
        return buildBbox(null);
    }

    public BBOX buildBBoxWithCRS() throws CQLException {
        final String crs = resultStack.popStringValue();
        assert crs != null;

        return buildBbox(crs);
    }

    private BBOX buildBbox(final String crs) throws CQLException {
        final double maxY = resultStack.popDoubleValue();
        final double maxX = resultStack.popDoubleValue();
        final double minY = resultStack.popDoubleValue();
        final double minX = resultStack.popDoubleValue();

        final PropertyName property = resultStack.popPropertyName();
        final String strProperty = property.getPropertyName();

        return filterFactory.bbox(strProperty, minX, minY, maxX, maxY, crs);
    }

    public DistanceBufferOperator buildSpatialDWithinFilter() throws CQLException {
        final String unit = resultStack.popStringValue();
        final double tolerance = resultStack.popDoubleValue();
        final Expression geom = resultStack.popExpression();
        final Expression property = resultStack.popExpression();
        final FilterFactory2 ff = (FilterFactory2) filterFactory; // TODO this cast must be removed. It depends of Geometry implementation

        return ff.dwithin(property, geom, tolerance, unit);
    }

    public DistanceBufferOperator buildSpatialBeyondFilter() throws CQLException {
        final String unit = resultStack.popStringValue();
        final double tolerance = resultStack.popDoubleValue();
        final Expression geom = resultStack.popExpression();
        final Expression property = resultStack.popExpression();
        final FilterFactory2 ff = (FilterFactory2) filterFactory; // TODO this cast must be removed. It depends of Geometry implementation

        return ff.beyond(property, geom, tolerance, unit);
    }

    /**
     * builds a PeriodNode (date1,date2)
     *
     * @return PeriodNode
     *
     * @throws CQLException
     */
    public PeriodNode buildPeriodBetweenDates() throws CQLException {
        final Literal end = resultStack.popLiteral();
        final Literal begin = resultStack.popLiteral();

        return PeriodNode.createPeriodDateAndDate(begin, end);
    }

    /**
     * builds a Period Node with (duration,date).
     *
     * @return PeriodNode
     * @throws CQLException
     */
    public PeriodNode buildPeriodDurationAndDate() throws CQLException {
        final Literal date = resultStack.popLiteral();
        final Literal duration = resultStack.popLiteral();
        return PeriodNode.createPeriodDurationAndDate(duration, date, filterFactory);
    }

    /**
     * builds a Period with (date,duration)
     *
     * @return PeriodNode
     * @throws CQLException
     */
    public PeriodNode buildPeriodDateAndDuration() throws CQLException {
        final Literal duration = resultStack.popLiteral();
        final Literal date = resultStack.popLiteral();
        return PeriodNode.createPeriodDateAndDuration(date, duration, filterFactory);
    }

    /**
     * Create an integer literal with duration value.
     *
     * @return Literal
     */
    public Literal buildDurationExpression(final IToken token) {
        final String duration = token.toString();
        return filterFactory.literal(duration);
    }

    /**
     * Create an AND filter with property between dates of period.
     * (firstDate<= property <= lastDate)
     *
     * @return And filter
     *
     * @throws CQLException
     */
    public And buildPropertyBetweenDates() throws CQLException {

        // retrieves date and duration of expression
        final Result node = resultStack.popResult();
        final PeriodNode period = (PeriodNode) node.getBuilt();

        final Literal begin = period.getBeginning();
        final Literal end = period.getEnding();

        // creates and filter firstDate<= property <= lastDate
        final Expression property = resultStack.popExpression();

        return filterFactory.and(filterFactory.lessOrEqual(begin,
                property), filterFactory.lessOrEqual(property, end));
    }

    /**
     * Builds PropertyIsGreaterThanOrEqualTo begin of period
     *
     * @return PropertyIsGreaterThanOrEqualTo
     * @throws CQLException
     */
    public PropertyIsGreaterThanOrEqualTo buildPropertyIsGTEFirstDate() throws CQLException {
        final Result node = resultStack.popResult();
        final PeriodNode period = (PeriodNode) node.getBuilt();
        final Literal begin = period.getBeginning();
        final Expression property = (Expression) resultStack.popExpression();

        return filterFactory.greaterOrEqual(property, begin);
    }

    /**
     * creates PropertyIsGreaterThan end date of period
     *
     * @return PropertyIsGreaterThan
     *
     * @throws CQLException
     */
    public PropertyIsGreaterThan buildPropertyIsGTLastDate() throws CQLException {
        final Result node = resultStack.popResult();
        final PeriodNode period = (PeriodNode) node.getBuilt();
        final Literal date = period.getEnding();
        final Expression property = resultStack.popExpression();

        return filterFactory.greater(property, date);
    }

    /**
     * @return PropertyIsLessThan
     * @throws CQLException
     */
    public PropertyIsLessThan buildPropertyIsLTFirsDate() throws CQLException {
        final PeriodNode period = resultStack.popPeriod();
        final Literal date = period.getBeginning();
        final Expression property = resultStack.popExpression();

        return filterFactory.less(property, date);
    }

    /**
     *
     * @return PropertyIsLessThanOrEqualTo
     * @throws CQLException
     */
    public PropertyIsLessThanOrEqualTo buildPropertyIsLTELastDate() throws CQLException {
        final PeriodNode period = resultStack.popPeriod();
        final Literal date = period.getEnding();
        final Expression property = resultStack.popExpression();

        return filterFactory.lessOrEqual(property, date);
    }

    /**
     *
     * @return PropertyIsEqualTo
     * @throws CQLException
     */
    public PropertyIsEqualTo buildEquals() throws CQLException {
        final Expression right = resultStack.popExpression();
        final Expression left = resultStack.popExpression();

        return filterFactory.equals(left, right);
    }

    /**
     *
     * @return PropertyIsGreaterThan
     * @throws CQLException
     */
    public PropertyIsGreaterThan buildGreater() throws CQLException {
        final Expression right = resultStack.popExpression();
        final Expression left = resultStack.popExpression();
        return filterFactory.greater(left, right);
    }

    /**
     *
     * @return PropertyIsLessThan
     * @throws CQLException
     */
    public PropertyIsLessThan buildLess() throws CQLException {
        final Expression right = resultStack.popExpression();
        final Expression left = resultStack.popExpression();
        return filterFactory.less(left, right);
    }

    /**
     *
     * @return PropertyIsGreaterThanOrEqualTo
     * @throws CQLException
     */
    public PropertyIsGreaterThanOrEqualTo buildGreaterOrEqual() throws CQLException {
        final Expression right = resultStack.popExpression();
        final Expression left = resultStack.popExpression();
        return filterFactory.greaterOrEqual(left, right);
    }

    /**
     * @return PropertyIsLessThanOrEqualTo
     * @throws CQLException
     */
    public PropertyIsLessThanOrEqualTo buildLessOrEqual() throws CQLException {
        final Expression right = resultStack.popExpression();
        final Expression left = resultStack.popExpression();

        return filterFactory.lessOrEqual(left, right);
    }

    /**
     * Builds geometry
     *
     * @param geometry
     * @return a geometry
     * @throws CQLException
     */
    public Literal buildGeometry(final IToken geometry) throws CQLException {
        try {
            final String wktGeom = scanExpression(geometry);

            // transforms wkt to vividsolution geometry
            final String vividGeom = transformWKTGeometry(wktGeom);

            final WKTReader reader = new WKTReader();

            final Geometry g = reader.read(vividGeom);

            return filterFactory.literal(g);
        } catch (com.vividsolutions.jts.io.ParseException e) {
            throw new CQLException(e.getMessage(), geometry, e, this.cqlSource);
        } catch (Exception e) {
            throw new CQLException("Error building WKT Geometry: " + e.getMessage(), geometry, e, this.cqlSource);
        }
    }

    /**
     * Extracts expression between initial token and last token in buffer.
     *
     * @param initialToken
     *
     * @return String the expression
     */
    protected String scanExpression(final IToken initialToken) {
        IToken end = initialToken;

        while (end.hasNext()) {
            end = end.next();
        }

        return cqlSource.substring(initialToken.beginColumn() - 1, end.endColumn());
    }

    /**
     * This transformation is required because some geometries like
     * <b>Multipoint</b> has different definition in vividsolucion library.
     * <p>
     *
     * <pre>
     * Then OGC require MULTIPOINT((1 2), (3 4))
     * but vividsolunion works without point &quot;(&quot; ans &quot;)&quot;
     * MULTIPOINT(1 2, 3 4)
     * </pre>
     *
     * <p>
     *
     * @param wktGeom ogc wkt geometry
     * @return String vividsolution geometry
     */
    protected String transformWKTGeometry(final String wktGeom) {
        final String MULTIPOINT_TYPE = "MULTIPOINT";

        final StringBuffer transformed = new StringBuffer(30);
        final StringBuffer source = new StringBuffer(wktGeom.toUpperCase());

        int cur = -1;

        if ((cur = source.indexOf(MULTIPOINT_TYPE)) != -1) {
            // extract "(" and ")" from points in arguments
            String argument = source.substring(cur + MULTIPOINT_TYPE.length() + 1, source.length() - 1);

            argument = argument.replace('(', ' ');
            argument = argument.replace(')', ' ');

            transformed.append(MULTIPOINT_TYPE).append("(").append(argument).append(")");

            return transformed.toString();
        } else {
            return wktGeom;
        }
    }

    /**
     * Return the Envelop
     * @param token
     * @return Literal
     */
    public Literal buildEnvelop(final IToken token) {
        final String source = scanExpression(token);

        final String ENVELOPE_TYPE = "ENVELOPE";

        int cur = source.indexOf(ENVELOPE_TYPE);

        // transforms CQL envelop envelop(West,East,North,South) to
        // GS84 West=minX, East=maxX, North=maxY, South=minY
        double minX, minY, maxX, maxY;

        cur = cur + ENVELOPE_TYPE.length() + 1;

        String argument = source.substring(cur, source.length() - 1);

        final String comma = ",";
        cur = 0;

        int end = argument.indexOf(comma, cur);
        String west = argument.substring(cur, end);
        minX = Double.parseDouble(west);

        cur = end + 1;
        end = argument.indexOf(comma, cur);

        String east = argument.substring(cur, end);
        maxX = Double.parseDouble(east);

        cur = end + 1;
        end = argument.indexOf(comma, cur);

        String north = argument.substring(cur, end);
        maxY = Double.parseDouble(north);

        cur = end + 1;

        String south = argument.substring(cur);
        minY = Double.parseDouble(south);

        // ReferencedEnvelope envelope = new
        // ReferencedEnvelope(DefaultGeographicCRS.WGS84);
        // envelope.init(minX, minY, maxX, maxY);
        final GeometryFactory gf = new GeometryFactory();

        final Coordinate[] coords = {new Coordinate(minX, minY),
            new Coordinate(minX, maxY), new Coordinate(maxX, maxY),
            new Coordinate(maxX, minY), new Coordinate(minX, minY)};
        final LinearRing shell = gf.createLinearRing(coords);
        final Polygon bbox = gf.createPolygon(shell, null);
        bbox.setUserData(DefaultGeographicCRS.WGS84);

        return filterFactory.literal(bbox);
    }

    /**
     * Builds a function expression
     *
     * @param functionNode symbol used to identify the function node in parser tree
     * @return Function
     * @throws CQLException
     */
    public Function buildFunction(final int functionNode) throws CQLException {

        String functionName = null; // token.image;

        // extracts the arguments from stack. Each argument in the stack
        // is preceded by an argument node. Finally extracts the function name
        final List<Expression> argList = new LinkedList<Expression>();

        while (!resultStack.empty()) {
            final Result node = resultStack.peek();

            if (node.getNodeType() == functionNode) {
                // gets the function's name
                final Result funcNameNode = resultStack.popResult();
                functionName = funcNameNode.getToken().toString();

                break;
            }

            // ejects the argument node
            resultStack.popResult();

            // extracts the argument value
            argList.add(resultStack.popExpression());
        }

        // Puts the argument in correct order
        Collections.reverse(argList);

        final Expression[] args = (Expression[]) argList.toArray(new Expression[argList.size()]);

        final Function function = filterFactory.function(functionName, args);

        if (function == null) {
            throw new CQLException("Function not found.", cqlSource);
        }

        return function;
    }

    /**
     * Adds in the result stack the partial result associated to node.
     * @param result partial result
     */
    public void pushResult(final Result result) {
        resultStack.push(result);
    }

    public Result peekResult() {
        return resultStack.peek();
    }
}
