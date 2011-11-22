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
package org.geotoolkit.filter.text.ecql;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.geotoolkit.filter.text.commons.AbstractFilterBuilder;
import org.geotoolkit.filter.text.commons.IToken;
import org.geotoolkit.filter.text.commons.Result;
import org.geotoolkit.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.BinarySpatialOperator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;


/**
 * Builds the filters required by the {@link ECQLCompiler}.
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.6
 */
final class ECQLFilterBuilder extends AbstractFilterBuilder {

    public ECQLFilterBuilder(final String ecqlSource, final FilterFactory filterFactory) {
        super(ecqlSource, filterFactory);
    }

    /**
     * builds the filter id
     *
     * @param token
     *            <character>
     * @return String without the quotes
     */
    public FeatureId buildFeatureID(final IToken token) {
        final String strId = removeQuotes(token.toString());
        return getFilterFactory().featureId(strId);
    }

    /**
     * builds the filter id
     *
     * @param jjtfeature_id_separator_node
     * @return Id
     * @throws CQLException
     */
    public Id buildFilterId(final int nodeFeatureId) throws CQLException {

        // retrieves the id from stack
        final List<FeatureId> idList = new LinkedList<FeatureId>();
        while (!getResultStack().empty()) {

            final Result result = getResultStack().peek();

            final int node = result.getNodeType();
            if (node != nodeFeatureId) {
                break;
            }
            final FeatureId id = (FeatureId) result.getBuilt();
            idList.add(id);
            getResultStack().popResult();
        }
        assert idList.size() >= 1 : "must have one or more FeatureIds";

        // shorts the id list and builds the filter Id
        Collections.reverse(idList);
        final Set<FeatureId> idSet = new LinkedHashSet<FeatureId>(idList);
        return getFilterFactory().id(idSet);
    }

    /**
     * Builds a negative Number
     *
     * @return Negative number
     * @throws CQLException
     */
    public Literal bulidNegativeNumber() throws CQLException {

        // retrieves the number value from stack and adds the (-) minus
        final Literal literal = getResultStack().popLiteral();
        final String strNumber = "-" + literal.getValue();
        final Object value = literal.getValue();

        // builds the negative number
        if (value instanceof Double) {
            Double.parseDouble(strNumber);
        } else if (value instanceof Float) {
            Float.parseFloat(strNumber);
        } else if (value instanceof Integer) {
            Integer.parseInt(strNumber);
        } else if (value instanceof Long) {
            Long.parseLong(strNumber);
        } else {
            assert false : "Number instnce is expected";
        }
        return getFilterFactory().literal(strNumber);
    }

    /**
     * builds the or filter for the in predicate. The method retrieves the list
     * of expressions and the property name from stack to make the Or filter.
     *
     * <pre>
     * Thus if the stack have the following predicate
     * propName in (expr1, expr2)
     * this method will produce:
     * (propName = expr1) or (propName = expr2)
     * </pre>
     *
     * @param nodeExpression
     * @return
     * @throws CQLException
     */
    public Or buildInPredicate(final int nodeExpression) throws CQLException {
        // retrieves the expressions from stack
        final List<Expression> exprList = new LinkedList<Expression>();
        while (!getResultStack().empty()) {

            Result result = getResultStack().peek();

            int node = result.getNodeType();
            if (node != nodeExpression) {
                break;
            }
            getResultStack().popResult();

            Expression expr = (Expression) getResultStack().popExpression();
            exprList.add(expr);
        }

        assert exprList.size() >= 1 : "must have one or more expressions";

        // retrieve the left hand expression from the stack
        final Expression leftHandExpr = getResultStack().popExpression();

        // makes one comparison for each expression in the expression list,
        // associated by the Or filter.
        final List<Filter> filterList = new LinkedList<Filter>();
        for (Expression expression : exprList) {
            PropertyIsEqualTo eq = getFilterFactory().equals(leftHandExpr,
                    expression);
            filterList.add(eq);
        }
        return getFilterFactory().or(filterList);
    }

    public Coordinate buildCoordinate() throws CQLException {
        final double y = getResultStack().popDoubleValue();
        final double x = getResultStack().popDoubleValue();

        return new Coordinate(x, y);
    }
    
    public Coordinate buildCoordinate3D() throws CQLException {
        final double z = getResultStack().popDoubleValue();
        final double y = getResultStack().popDoubleValue();
        final double x = getResultStack().popDoubleValue();
        

        return new Coordinate(x, y, z);
    }

    public Point buildPointText() throws CQLException {
        final PointBuilder builder = new PointBuilder(getStatement(), getResultStack());
        return (Point) builder.build();
    }
    
    public Point buildPoint3DText() throws CQLException {
        final PointBuilder builder = new PointBuilder(getStatement(), getResultStack());
        return (Point) builder.build();
    }

    public LineString buildLineString(final int pointNode) throws CQLException {
        final LineStringBuilder builder = new LineStringBuilder(getStatement(),
                getResultStack());
        return (LineString) builder.build(pointNode);
    }

    public Polygon buildPolygon(final int linestringNode) throws CQLException {
        final PolygonBuilder builder = new PolygonBuilder(getStatement(), getResultStack());
        return (Polygon) builder.build(linestringNode);
    }

    /**
     * Retrieves all points built in previous parsing process from stack and
     * creates the multipoint geometry.
     *
     * @param pointNode
     * @return a MultiPoint
     * @throws CQLException
     */
    public MultiPoint buildMultiPoint(final int pointNode) throws CQLException {
        final MultiPointBuilder builder = new MultiPointBuilder(getStatement(), getResultStack());
        return (MultiPoint) builder.build(pointNode);
    }

    /**
     * Retrieves all linestring built from stack and creates the multilinestring
     * geometry
     *
     * @param pointNode
     * @return a MultiLineString
     *
     * @throws CQLException
     */
    public MultiLineString buildMultiLineString(final int linestringtextNode) throws CQLException {
        final MultiLineStringBuilder builder = new MultiLineStringBuilder(getStatement(), getResultStack());
        return (MultiLineString) builder.build(linestringtextNode);
    }

    /**
     * Builds a {@link MuliPolygon} using the {@link Polygon} staked in the
     * parsing process
     *
     * @param polygontextNode
     *            .
     *
     * @return MultiPolygon
     * @throws CQLException
     */
    public MultiPolygon buildMultiPolygon(final int polygontextNode) throws CQLException {
        final MultiPolygonBuilder builder = new MultiPolygonBuilder(getStatement(), getResultStack());
        return (MultiPolygon) builder.build(polygontextNode);
    }

    /**
     * Builds a {@link GeometryCollection}
     *
     * @param jjtgeometryliteral
     * @return GeometryCollection
     * @throws CQLException
     */
    public GeometryCollection buildGeometryCollection(final int jjtgeometryliteral) throws CQLException {
        final GeometryCollectionBuilder builder = new GeometryCollectionBuilder(getStatement(), getResultStack());
        return (GeometryCollection) builder.build(jjtgeometryliteral);
    }

    /**
     * Builds literal geometry
     *
     * @param geometry
     * @return a Literal Geometry
     * @throws CQLException
     */
    public Literal buildGeometry() throws CQLException {
        final Geometry geometry = getResultStack().popGeometry();
        return getFilterFactory().literal(geometry);
    }

    public Literal buildGeometryLiteral() throws CQLException {
        return getResultStack().popLiteral();
    }

    @Override
    public BinarySpatialOperator buildSpatialEqualFilter() throws CQLException {
        final SpatialOperationBuilder builder = new SpatialOperationBuilder(
                getResultStack(), getFilterFactory());
        return builder.buildEquals();
    }

    @Override
    public BinarySpatialOperator buildSpatialDisjointFilter() throws CQLException {
        final SpatialOperationBuilder builder = new SpatialOperationBuilder(
                getResultStack(), getFilterFactory());
        return builder.buildDisjoint();
    }

    @Override
    public BinarySpatialOperator buildSpatialIntersectsFilter() throws CQLException {
        final SpatialOperationBuilder builder = new SpatialOperationBuilder(
                getResultStack(), getFilterFactory());
        return builder.buildIntersects();
    }

    @Override
    public BinarySpatialOperator buildSpatialTouchesFilter() throws CQLException {
        final SpatialOperationBuilder builder = new SpatialOperationBuilder(
                getResultStack(), getFilterFactory());
        return builder.buildTouches();
    }

    @Override
    public BinarySpatialOperator buildSpatialCrossesFilter() throws CQLException {
        final SpatialOperationBuilder builder = new SpatialOperationBuilder(
                getResultStack(), getFilterFactory());
        return builder.buildCrosses();
    }

    /**
     * Makes an equals to true filter with the relatePattern function
     *
     * @return relatePattern is equal to true
     * @throws CQLException
     */
    public PropertyIsEqualTo buildRelatePattern() throws CQLException {
        final RelatePatternBuilder builder = new RelatePatternBuilder(getResultStack(),
                getFilterFactory());
        final Function relatePattern = builder.build();
        return getFilterFactory().equals(relatePattern, getFilterFactory().literal(true));
    }

    /**
     * Builds a not equal filter with that evaluate the relate pattern function
     * @return Not filter
     * @throws CQLException
     */
    public Not buildNotRelatePattern() throws CQLException {
        final PropertyIsEqualTo  eq = buildRelatePattern();
        return getFilterFactory().not(eq);
    }

    /**
     * Checks the correctness of pattern and makes a literal with this pattern;
     *
     * @return a Literal with the pattern
     * @throws CQLException if the pattern has not one of the following characters:T,F,*,0,1,2
     */
    public Literal buildPattern9IM() throws CQLException {

        // retrieves the pattern from stack
        final Result resut = getResultStack().popResult();
        final IToken token = resut.getToken();

        final Literal built = (Literal)resut.getBuilt();
        final String pattern = (String)built.getValue();

        // validates the length
        if(pattern.length() != 9){
            throw new CQLException("the pattern DE-9IM must have nine (9) characters", token, getStatement() );
        }

        // validates that the pattern has only the characters T,F,*,0,1,2
        String patternUC = pattern.toUpperCase();

        char[] validFlags = new char[]{'T', 'F', '*', '0', '1', '2'};
        for (int i = 0; i < validFlags.length; i++) {
            char character = patternUC.charAt(i);

            boolean found = false;
            for (int j = 0; j < validFlags.length; j++) {
                if(validFlags[j] == character){
                    found = true;
                    break;
                }
            }
            if(!found){
                throw new CQLException("the pattern DE-9IM must have only the following characters: T, F, *, 0, 1, 2", token, getStatement() );
            }
        }

        return getFilterFactory().literal(pattern);
    }

    @Override
    public BinarySpatialOperator buildSpatialWithinFilter() throws CQLException {
        final SpatialOperationBuilder builder = new SpatialOperationBuilder(
                getResultStack(), getFilterFactory());
        return builder.buildWithin();
    }

    @Override
    public BinarySpatialOperator buildSpatialContainsFilter() throws CQLException {
        final SpatialOperationBuilder builder = new SpatialOperationBuilder(
                getResultStack(), getFilterFactory());
        return builder.buildContains();
    }

    @Override
    public BinarySpatialOperator buildSpatialOverlapsFilter() throws CQLException {
        final SpatialOperationBuilder builder = new SpatialOperationBuilder(
                getResultStack(), getFilterFactory());
        return builder.buildOverlaps();
    }

    /**
     * An equals filter with to test the relate function
     *
     * @return Relate equals true
     * @throws CQLException
     */
    public PropertyIsEqualTo buildRelate() throws CQLException {
        final RelateBuilder builder = new RelateBuilder(getResultStack(), getFilterFactory());
        final Function f = builder.build();
        return getFilterFactory().equals(f, getFilterFactory().literal(true));
    }

    @Override
    public BBOX buildBBox() throws CQLException {
        final SpatialOperationBuilder builder = new SpatialOperationBuilder(getResultStack(),
                getFilterFactory());
        return builder.buildBBox();
    }

    @Override
    public BBOX buildBBoxWithCRS() throws CQLException {
        final SpatialOperationBuilder builder = new SpatialOperationBuilder(getResultStack(),
                getFilterFactory());
        return builder.buildBBoxWithCRS();
    }

}
