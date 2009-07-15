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

import java.io.StringReader;
import java.util.List;

import org.geotoolkit.filter.text.commons.ICompiler;
import org.geotoolkit.filter.IllegalFilterException;
import org.geotoolkit.filter.text.commons.IToken;
import org.geotoolkit.filter.text.commons.Result;
import org.geotoolkit.filter.text.commons.TokenAdapter;
import org.geotoolkit.filter.text.cql2.CQLException;
import org.geotoolkit.filter.text.generated.parsers.ECQLParser;
import org.geotoolkit.filter.text.generated.parsers.Node;
import org.geotoolkit.filter.text.generated.parsers.ParseException;
import org.geotoolkit.filter.text.generated.parsers.TokenMgrError;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.expression.BinaryExpression;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.DistanceBufferOperator;


/**
 * ECQLCompiler
 *
 * <p>
 * Builds the filter, expression or arguments related with the visited
 * node of syntax tree
 * </p>
 *
 * @author Jody Garnett
 * @author Mauricio Pazos (Axios Engineering)
 *
 * @version Revision: 1.9
 * @since 2.6
 */
public class ECQLCompiler extends ECQLParser implements ICompiler{

    private static final String ATTRIBUTE_PATH_SEPARATOR = "/";

    /** cql expression to compile */
    private final String source;

    private final ECQLFilterBuilder builder;

    /**
     * new instance of TXTCompiler
     * @param txtSource
     * @param filterFactory
     */
    public ECQLCompiler(final String txtSource, final FilterFactory filterFactory) {

        super(new StringReader(txtSource));

        assert txtSource != null: "txtSource cannot be null";
        assert filterFactory != null: "filterFactory cannot be null";

        this.source = txtSource;
        builder =  new ECQLFilterBuilder(txtSource, filterFactory);
    }

    /**
     * compile source to produce a Filter. The filter
     * result must be retrieved with {@link #getFilter()}.
     *
     * @throws CQLException
     */
    @Override
    public void compileFilter() throws CQLException {
        try {
            super.FilterCompilationUnit();
        } catch (TokenMgrError tokenError) {
            throw new CQLException(tokenError.getMessage(), getTokenInPosition(0), this.source);
        } catch (ParseException e) {
            throw new CQLException(e.getMessage(), getTokenInPosition(0), e.getCause(), this.source);
        }
    }

    /**
     * compiles source to produce a Expression
     */
    @Override
    public void compileExpression() throws CQLException {
        try {
            super.ExpressionCompilationUnit();
        } catch (TokenMgrError tokenError) {
            throw new CQLException(tokenError.getMessage(), getTokenInPosition(0), this.source);
        } catch (ParseException e) {
            throw new CQLException(e.getMessage(), getTokenInPosition(0), e.getCause(), this.source);
        }
    }

    /**
     * Compiles a list of filters
     */
    @Override
    public void compileFilterList() throws CQLException {
        try {
            super.FilterListCompilationUnit();
        } catch (TokenMgrError tokenError) {
            throw new CQLException(tokenError.getMessage(), getTokenInPosition(0), this.source);
        } catch (ParseException e) {
            throw new CQLException(e.getMessage(), getTokenInPosition(0), e.getCause(), this.source);
        }
    }

    /**
     * @return the ECQLsource
     */
    @Override
    public final String getSource() {
        return source;
    }

    /**
     * Return the filter resultant of compiling process
     * @return Filter
     * @throws CQLException
     */
    @Override
    public final Filter getFilter() throws CQLException {
        return builder.getFilter();
    }

    /**
     * Return the expression resultant of compiling process
     * @return Expression
     * @throws CQLException
     */
    @Override
    public final Expression getExpression() throws CQLException {
        return builder.getExpression();
    }

    @Override
    public IToken getTokenInPosition(int index) {
        return TokenAdapter.newAdapterFor(super.getToken(index));
    }

    /**
     * Returns the list of Filters built as the result of calling
     * {@link #FilterListCompilationUnit()()}
     *
     * @return List<Filter>
     * @throws CQLException
     *             if a ClassCastException occurs while casting a built item to
     *             a Filter.
     */
    @Override
    public List<Filter> getFilterList() throws CQLException {
        return builder.getFilterList();
    }

    @Override
    public final void jjtreeOpenNodeScope(Node n) {
    }

    /**
     * called by parser when the node is closed.
     */
    @Override
    public final void jjtreeCloseNodeScope(Node n) throws ParseException {

        try {
            Object built = build(n);

            IToken tokenAdapter = TokenAdapter.newAdapterFor(this.token);
            Result r = new Result(built, tokenAdapter, n.getType());
            builder.pushResult(r);

        } finally {
            n.dispose();
        }
    }

    /**
     * This method is called when the parser close a node. Here is built the
     * filters an expressions recognized in the parsing process.
     *
     * @param n a Node instance
     * @return Filter or Expression
     * @throws CQLException
     */
    private Object build(Node n) throws CQLException {

        switch (n.getType()) {

            // ----------------------------------------
            // (+|-) Integer and Float
            // ----------------------------------------
            case JJTINTEGERNODE:
                return builder.buildLiteralInteger(getTokenInPosition(0).toString());
            case JJTFLOATINGNODE:
                return builder.buildLiteralDouble(getTokenInPosition(0).toString());
            case JJTNEGATIVENUMBER_NODE:
                return builder.bulidNegativeNumber();

            // ----------------------------------------
            // String
            // ----------------------------------------
            case JJTSTRINGNODE:
                return builder.buildLiteralString(getTokenInPosition(0).toString());

            // ----------------------------------------
            // Identifier
            // ----------------------------------------
            case JJTIDENTIFIER_NODE:
                return builder.buildIdentifier(JJTIDENTIFIER_PART_NODE);

            case JJTIDENTIFIER_PART_NODE:
                return builder.buildIdentifierPart(getTokenInPosition(0));

            // ----------------------------------------
            // attribute
            // ----------------------------------------
            case JJTSIMPLE_ATTRIBUTE_NODE:
                return builder.buildSimpleAttribute();

            case JJTCOMPOUND_ATTRIBUTE_NODE:
                return builder.buildCompoundAttribute(
                        JJTSIMPLE_ATTRIBUTE_NODE, ATTRIBUTE_PATH_SEPARATOR);

            // ----------------------------------------
            // function
            // ----------------------------------------
            case JJTFUNCTION_NODE:
                return builder.buildFunction(JJTFUNCTIONNAME_NODE);

            case JJTFUNCTIONNAME_NODE:
                return n; // used as mark of function name in stack

            case JJTFUNCTIONARG_NODE:
                return n; // used as mark of args in stack

            // Math Nodes
            case JJTADDNODE:
            case JJTSUBTRACTNODE:
            case JJTMULNODE:
            case JJTDIVNODE:
                return buildBinaryExpression(n.getType());

            // Boolean expression
            case JJTBOOLEAN_AND_NODE:
                return buildLogicFilter(JJTBOOLEAN_AND_NODE);

            case JJTBOOLEAN_OR_NODE:
                return buildLogicFilter(JJTBOOLEAN_OR_NODE);

            case JJTBOOLEAN_NOT_NODE:
                return buildLogicFilter(JJTBOOLEAN_NOT_NODE);

            // ----------------------------------------
            // between predicate actions
            // ----------------------------------------
            case JJTBETWEEN_NODE:
                return builder.buildBetween();

            case JJTNOT_BETWEEN_NODE:
                return builder.buildNotBetween();

            // ----------------------------------------
            // Compare predicate actions
            // ----------------------------------------
            case JJTCOMPARISONPREDICATE_EQ_NODE:
            case JJTCOMPARISONPREDICATE_GT_NODE:
            case JJTCOMPARISONPREDICATE_LT_NODE:
            case JJTCOMPARISONPREDICATE_GTE_NODE:
            case JJTCOMPARISONPREDICATE_LTE_NODE:
                return buildBinaryComparasionOperator(n.getType());

            case JJTCOMPARISONPREDICATE_NOT_EQUAL_NODE:

                Filter eq = buildBinaryComparasionOperator(JJTCOMPARISONPREDICATE_EQ_NODE);
                Not notFilter = builder.buildNotFilter(eq);

                return notFilter;

            // ----------------------------------------
            // Text predicate (Like)
            // ----------------------------------------
            case JJTLIKE_NODE:
                return builder.buildLikeFilter();

            case JJTNOT_LIKE_NODE:

                return builder.buildNotLikeFilter();

            // ----------------------------------------
            // Null predicate
            // ----------------------------------------
            case JJTNULLPREDICATENODE:
                return builder.buildPropertyIsNull();

            case JJTNOTNULLPREDICATENODE:
                return builder.buildPorpertyNotIsNull();

            // ----------------------------------------
            // temporal predicate actions
            // ----------------------------------------
            case JJTDATETIME_NODE:
                return builder.buildDateTimeExpression(getTokenInPosition(0));

            case JJTDURATION_DATE_NODE:
                return builder.buildDurationExpression(getTokenInPosition(0));

            case JJTPERIOD_BETWEEN_DATES_NODE:
                return builder.buildPeriodBetweenDates();

            case JJTPERIOD_WITH_DATE_DURATION_NODE:
                return builder.buildPeriodDateAndDuration();

            case JJTPERIOD_WITH_DURATION_DATE_NODE:
                return builder.buildPeriodDurationAndDate();

            case JJTTPBEFORE_DATETIME_NODE:
                return buildTemporalPredicateBefore();

            case JJTTPAFTER_DATETIME_NODE:
                return buildTemporalPredicateAfter();

            case JJTTPDURING_PERIOD_NODE:
                return buildTemporalPredicateDuring();

            case JJTTPBEFORE_OR_DURING_PERIOD_NODE:
                return buildTemporalPredicateBeforeOrDuring();

            case JJTTPDURING_OR_AFTER_PERIOD_NODE:
                return buildTemporalPredicateDuringOrAfter();

            // ----------------------------------------
            // existence predicate actions
            // ----------------------------------------
            case JJTEXISTENCE_PREDICATE_EXISTS_NODE:
                return builder.buildPropertyExists();

            case JJTEXISTENCE_PREDICATE_DOESNOTEXIST_NODE:

                Filter filter = builder.buildPropertyExists();
                Filter filterPropNotExist = builder.buildNotFilter(filter);

                return filterPropNotExist;

            // ----------------------------------------
            // routine invocation Geo Operation
            // ----------------------------------------
            case JJTROUTINEINVOCATION_GEOOP_EQUAL_NODE:
            case JJTROUTINEINVOCATION_GEOOP_DISJOINT_NODE:
            case JJTROUTINEINVOCATION_GEOOP_INTERSECT_NODE:
            case JJTROUTINEINVOCATION_GEOOP_TOUCH_NODE:
            case JJTROUTINEINVOCATION_GEOOP_CROSS_NODE:
            case JJTROUTINEINVOCATION_GEOOP_WITHIN_NODE:
            case JJTROUTINEINVOCATION_GEOOP_CONTAIN_NODE:
            case JJTROUTINEINVOCATION_GEOOP_OVERLAP_NODE:
                return buildBinarySpatialOperator(n.getType());

            case JJTROUTINEINVOCATION_GEOOP_BBOX_NODE:
            case JJTROUTINEINVOCATION_GEOOP_BBOX_SRS_NODE:
                return buildBBox(n.getType());

            case JJTRELATE_NODE:
                return builder.buildRelate();

            // ----------------------------------------
            // Spatial Relate Like
            // ----------------------------------------
//                TODO  these will be extensions in the ECQL (under analysis)
//            case JJTPATTERN9IM_NODE:
//                return builder.buildPattern9IM();
//
//            case JJTSPATIALRELATELIKE_NODE:
//                return builder.buildRelatePattern();
//
//            case JJTNOT_SPATIALRELATELIKE_NODE:
//                return builder.buildNotRelatePattern();
            // ----------------------------------------
            // routine invocation RelGeo Operation
            // ----------------------------------------
            case JJTTOLERANCE_NODE:
                return builder.buildTolerance();

            case JJTDISTANCEUNITS_NODE:
                return builder.buildDistanceUnit(getTokenInPosition(0));

            case JJTROUTINEINVOCATION_RELOP_BEYOND_NODE:
            case JJTROUTINEINVOCATION_RELOP_DWITHIN_NODE:
                return buildDistanceBufferOperator(n.getType());

            // ----------------------------------------
            // Geometries:
            // ----------------------------------------
            case JJTPOINT_NODE:
                return builder.buildCoordinate();

            case JJTPOINTTEXT_NODE:
                return builder.buildPointText();

            case JJTLINESTRINGTEXT_NODE:
                return builder.buildLineString(JJTPOINT_NODE);

            case JJTPOLYGONTEXT_NODE:
                return builder.buildPolygon(JJTLINESTRINGTEXT_NODE);

            case JJTMULTIPOINTTEXT_NODE:
                return builder.buildMultiPoint(JJTPOINTTEXT_NODE);

            case JJTMULTILINESTRINGTEXT_NODE:
                return builder.buildMultiLineString(JJTLINESTRINGTEXT_NODE);

            case JJTMULTIPOLYGONTEXT_NODE:
                return builder.buildMultiPolygon(JJTPOLYGONTEXT_NODE);

            case JJTGEOMETRYLITERAL:
                return builder.buildGeometryLiteral();

            case JJTGEOMETRYCOLLECTIONTEXT_NODE:
                return builder.buildGeometryCollection(JJTGEOMETRYLITERAL);


            case JJTWKTNODE:
                return builder.buildGeometry();

            case JJTENVELOPETAGGEDTEXT_NODE:
                return builder.buildEnvelop(TokenAdapter.newAdapterFor(n.getToken()));

            case JJTINCLUDE_NODE:
                return Filter.INCLUDE;

            case JJTEXCLUDE_NODE:
                return Filter.EXCLUDE;

            case JJTTRUENODE:
                return builder.buildTrueLiteral();

            case JJTFALSENODE:
                return builder.buildFalseLiteral();

            // ----------------------------------------
            //  ID Predicate
            // ----------------------------------------
            case JJTFEATURE_ID_NODE:
                return builder.buildFeatureID(getTokenInPosition(0));

            case JJTID_PREDICATE_NODE:
                return builder.buildFilterId(JJTFEATURE_ID_NODE);

            case JJTNOT_ID_PREDICATE_NODE:

                Id idFilter = builder.buildFilterId(JJTFEATURE_ID_NODE);
                Not notIdFilter = builder.buildNotFilter(idFilter);

                return notIdFilter;

            // ----------------------------------------
            //  IN Predicate
            // ----------------------------------------
            case JJTIN_PREDICATE_NODE:
                return builder.buildInPredicate(JJTEXPRESSION_IN_LIST_NODE);

            case JJTNOT_IN_PREDICATE_NODE:
                Or orFilter = builder.buildInPredicate(JJTEXPRESSION_IN_LIST_NODE);
                Not notOrFilter = builder.buildNotFilter(orFilter);

                return notOrFilter;
        }

        return null;
    }

    private BinaryExpression buildBinaryExpression(int nodeType) throws CQLException {

        BinaryExpression expr = null;

        switch (nodeType) {
            case JJTADDNODE:
                expr = builder.buildAddExpression();

                break;

            case JJTSUBTRACTNODE:
                expr = builder.buildSubtractExression();

                break;

            case JJTMULNODE:

                expr = builder.buildMultiplyExpression();
                break;

            case JJTDIVNODE:

                expr = builder.buildDivideExpression();
                break;

            default:
                break;
        }

        return expr;
    }

    private Filter buildLogicFilter(int nodeType) throws CQLException {
        try {
            final Filter logicFilter;

            switch (nodeType) {
                case JJTBOOLEAN_AND_NODE:
                    logicFilter = builder.buildAndFilter();
                    break;

                case JJTBOOLEAN_OR_NODE:
                    logicFilter = builder.buildOrFilter();
                    break;

                case JJTBOOLEAN_NOT_NODE:

                    logicFilter = builder.buildNotFilter();
                    break;

                default:
                    throw new CQLException(
                            "Expression not supported. And, Or, Not is required",
                            getTokenInPosition(0), this.source);
            }

            return logicFilter;
        } catch (IllegalFilterException ife) {
            throw new CQLException("Exception building LogicFilter",
                    getTokenInPosition(0), ife, this.source);
        }
    }

    /**
     * Creates Binary Spatial Operator
     *
     * @param tipeNode
     *
     * @return BinarySpatialOperator
     * @throws CQLException
     */
    private BinarySpatialOperator buildBinarySpatialOperator(final int nodeType)
            throws CQLException
    {

        final BinarySpatialOperator filter;

        switch (nodeType) {
            case JJTROUTINEINVOCATION_GEOOP_EQUAL_NODE:

                filter = builder.buildSpatialEqualFilter();
                break;
            case JJTROUTINEINVOCATION_GEOOP_DISJOINT_NODE:
                filter = builder.buildSpatialDisjointFilter();
                break;
            case JJTROUTINEINVOCATION_GEOOP_INTERSECT_NODE:
                filter = builder.buildSpatialIntersectsFilter();
                break;
            case JJTROUTINEINVOCATION_GEOOP_TOUCH_NODE:
                filter = builder.buildSpatialTouchesFilter();
                break;

            case JJTROUTINEINVOCATION_GEOOP_CROSS_NODE:
                filter = builder.buildSpatialCrossesFilter();
                break;
            case JJTROUTINEINVOCATION_GEOOP_WITHIN_NODE:
                filter = builder.buildSpatialWithinFilter();
                break;
            case JJTROUTINEINVOCATION_GEOOP_CONTAIN_NODE:
                filter = builder.buildSpatialContainsFilter();
                break;
            case JJTROUTINEINVOCATION_GEOOP_OVERLAP_NODE:
                filter = builder.buildSpatialOverlapsFilter();
                break;
            default:
                throw new CQLException("Binary spatial operator unexpected");
        }

        return filter;
    }

    private org.opengis.filter.spatial.BBOX buildBBox(int nodeType) throws CQLException {

        if (nodeType == JJTROUTINEINVOCATION_GEOOP_BBOX_SRS_NODE) {
            return builder.buildBBoxWithCRS();
        } else {
            return builder.buildBBox();
        }
    }

    /**
     * Builds Distance Buffer Operator
     *
     * @param nodeType
     * @return DistanceBufferOperator dwithin and beyond filters
     * @throws CQLException
     */
    private DistanceBufferOperator buildDistanceBufferOperator(final int nodeType) throws CQLException {

        final DistanceBufferOperator filter;

        switch (nodeType) {
            case JJTROUTINEINVOCATION_RELOP_DWITHIN_NODE:
                filter = builder.buildSpatialDWithinFilter();
                break;

            case JJTROUTINEINVOCATION_RELOP_BEYOND_NODE:

                filter = builder.buildSpatialBeyondFilter();
                break;

            default:
                throw new CQLException("Binary spatial operator unexpected");
        }

        return filter;
    }

    private Filter buildTemporalPredicateBeforeOrDuring() throws CQLException {
        final Filter filter;

        final Result node = builder.peekResult();

        switch (node.getNodeType()) {
            case JJTPERIOD_BETWEEN_DATES_NODE:
            case JJTPERIOD_WITH_DATE_DURATION_NODE:
            case JJTPERIOD_WITH_DURATION_DATE_NODE:
                filter = builder.buildPropertyIsLTELastDate();
                break;

            default:
                throw new CQLException(
                        "unexpected date time expression in temporal predicate.",
                        node.getToken(), this.source);
        }

        return filter;
    }

    private Filter buildTemporalPredicateDuringOrAfter() throws CQLException {
        final Filter filter;

        Result node = builder.peekResult();

        switch (node.getNodeType()) {
            case JJTPERIOD_BETWEEN_DATES_NODE:
            case JJTPERIOD_WITH_DATE_DURATION_NODE:
            case JJTPERIOD_WITH_DURATION_DATE_NODE:
                filter = builder.buildPropertyIsGTEFirstDate();

                break;

            default:
                throw new CQLException(
                        "unexpected date time expression in temporal predicate.",
                        node.getToken(), this.source);
        }

        return filter;
    }

    /**
     * Build the convenient filter for before date and before period filters
     *
     * @param nodeType
     *
     * @return Filter
     * @throws CQLException
     */
    private Filter buildTemporalPredicateBefore() throws CQLException {
        final Filter filter;

        // analyzes if the last build is period or date
        Result node = builder.peekResult();

        switch (node.getNodeType()) {
            case JJTDATETIME_NODE:
                filter = buildBinaryComparasionOperator(JJTCOMPARISONPREDICATE_LT_NODE);

                break;

            case JJTPERIOD_BETWEEN_DATES_NODE:
            case JJTPERIOD_WITH_DATE_DURATION_NODE:
            case JJTPERIOD_WITH_DURATION_DATE_NODE:
                filter = builder.buildPropertyIsLTFirsDate();

                break;

            default:
                throw new CQLException(
                        "unexpected date time expression in temporal predicate.",
                        node.getToken(), this.source);
        }

        return filter;
    }

    /**
     * Build the convenient filter for during period filters
     *
     * @return Filter
     * @throws CQLException
     */
    private Object buildTemporalPredicateDuring() throws CQLException {
        final Filter filter;

        // determines if the node is period or date
        Result node = builder.peekResult();

        switch (node.getNodeType()) {
            case JJTPERIOD_BETWEEN_DATES_NODE:
            case JJTPERIOD_WITH_DATE_DURATION_NODE:
            case JJTPERIOD_WITH_DURATION_DATE_NODE:
                filter = builder.buildPropertyBetweenDates();

                break;

            default:
                throw new CQLException(
                        "unexpected period expression in temporal predicate.", node.getToken(), this.source);
        }

        return filter;
    }

    /**
     * build filter for after date and after period
     *
     * @return a filter
     * @throws CQLException
     */
    private Filter buildTemporalPredicateAfter() throws CQLException {
        final Filter filter;

        // determines if the node is period or date
        Result node = builder.peekResult();

        switch (node.getNodeType()) {
            case JJTDATETIME_NODE:
                filter = buildBinaryComparasionOperator(JJTCOMPARISONPREDICATE_GT_NODE);

                break;

            case JJTPERIOD_BETWEEN_DATES_NODE:
            case JJTPERIOD_WITH_DURATION_DATE_NODE:
            case JJTPERIOD_WITH_DATE_DURATION_NODE:
                filter = builder.buildPropertyIsGTLastDate();

                break;

            default:
                throw new CQLException(
                        "unexpected date time expression in temporal predicate.",
                        node.getToken(), this.source);
        }

        return filter;
    }

    /**
     * Builds a compare filter
     *
     * @param filterTipa
     *
     * @return BinaryComparisonOperator
     * @throws CQLException
     */
    private BinaryComparisonOperator buildBinaryComparasionOperator(int filterType)
            throws CQLException
    {

        switch (filterType) {
            case JJTCOMPARISONPREDICATE_EQ_NODE:
                return builder.buildEquals();

            case JJTCOMPARISONPREDICATE_GT_NODE:
                return builder.buildGreater();

            case JJTCOMPARISONPREDICATE_LT_NODE:
                return builder.buildLess();

            case JJTCOMPARISONPREDICATE_GTE_NODE:
                return builder.buildGreaterOrEqual();

            case JJTCOMPARISONPREDICATE_LTE_NODE:
                return builder.buildLessOrEqual();

            default:
                throw new CQLException("unexpected filter type.");
        }
    }
}
