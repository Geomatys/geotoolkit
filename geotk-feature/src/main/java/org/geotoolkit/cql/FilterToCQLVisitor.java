/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.cql;

import org.apache.sis.geometry.Envelope2D;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.sis.filter.privy.FunctionNames;
import org.apache.sis.filter.privy.Visitor;
import org.opengis.filter.BetweenComparisonOperator;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.ComparisonOperator;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.DistanceOperator;
import org.opengis.filter.DistanceOperatorName;
import org.opengis.filter.Filter;
import org.opengis.filter.Expression;
import org.opengis.filter.LikeOperator;
import org.opengis.filter.Literal;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.NilOperator;
import org.opengis.filter.NullOperator;
import org.opengis.filter.SpatialOperator;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.TemporalOperator;
import org.opengis.filter.TemporalOperatorName;
import org.opengis.filter.ValueReference;
import org.opengis.geometry.Envelope;

/**
 * Visitor to convert a Filter in CQL.
 * Returned object is a StringBuilder containing the CQL text.
 *
 * @author  Johann Sorel (Geomatys)
 */
public final class FilterToCQLVisitor extends Visitor<Object,StringBuilder> {

    public static final FilterToCQLVisitor INSTANCE = new FilterToCQLVisitor();

    /**
     * Pattern to check for property name to escape against regExp
     */
    private final Pattern patternValueReference = Pattern.compile("[,+\\-/*\\t\\n\\r\\d\\s]");

    private FilterToCQLVisitor() {

        ////////////////////////////////////////////////////////////////////////////
        // FILTER //////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////

        setFilterHandler(Filter.exclude().getOperatorType(), (f, sb) -> {
            sb.append("1=0");
        });
        setFilterHandler(Filter.include().getOperatorType(), (f, sb) -> {
            sb.append("1=1");
        });
        setFilterHandler(LogicalOperatorName.AND, (f, sb) -> {
            final LogicalOperator<Object> filter = (LogicalOperator<Object>) f;
            final List<Filter<Object>> filters = filter.getOperands();
            if (!filters.isEmpty()) {
                final int size = filters.size();
                sb.append('(');
                for (int i = 0, n = size - 1; i < n; i++) {
                    visit(filters.get(i), sb);
                    sb.append(" AND ");
                }
                visit(filters.get(size - 1), sb);
                sb.append(')');
            }
        });
        setFilterHandler(LogicalOperatorName.OR, (f, sb) -> {
            final LogicalOperator<Object> filter = (LogicalOperator<Object>) f;
            final List<Filter<Object>> filters = filter.getOperands();
            if (!filters.isEmpty()) {
                final int size = filters.size();
                sb.append('(');
                for (int i = 0, n = size - 1; i < n; i++) {
                    visit(filters.get(i), sb);
                    sb.append(" OR ");
                }
                visit(filters.get(size - 1), sb);
                sb.append(')');
            }
        });
        setFilterHandler(LogicalOperatorName.NOT, (f, sb) -> {
            final LogicalOperator<Object> filter = (LogicalOperator<Object>) f;
            sb.append("NOT ");
            visit(filter.getOperands().get(0), sb);
        });
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_BETWEEN), (f, sb) -> {
            final BetweenComparisonOperator<Object> filter = (BetweenComparisonOperator<Object>) f;
            visit(filter.getExpression(), sb);
            sb.append(" BETWEEN ");
            visit(filter.getLowerBoundary(), sb);
            sb.append(" AND ");
            visit(filter.getUpperBoundary(), sb);
        });
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_EQUAL_TO, (f, sb) -> {
            final ComparisonOperator<Object> filter = (ComparisonOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" = ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_NOT_EQUAL_TO, (f, sb) -> {
            final ComparisonOperator<Object> filter = (ComparisonOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" <> ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_GREATER_THAN, (f, sb) -> {
            final ComparisonOperator<Object> filter = (ComparisonOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" > ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_GREATER_THAN_OR_EQUAL_TO, (f, sb) -> {
            final ComparisonOperator<Object> filter = (ComparisonOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" >= ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_LESS_THAN, (f, sb) -> {
            final ComparisonOperator<Object> filter = (ComparisonOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" < ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_LESS_THAN_OR_EQUAL_TO, (f, sb) -> {
            final ComparisonOperator<Object> filter = (ComparisonOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" <= ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_LIKE), (f, sb) -> {
            final LikeOperator<Object> filter = (LikeOperator<Object>) f;
            final char escape = filter.getEscapeChar();
            final char wildCard = filter.getWildCard();
            final char singleChar = filter.getSingleChar();
            final boolean matchingCase = filter.isMatchingCase();
            final String literal = (String) filter.getExpressions().get(1).apply(null);
            final String pattern = convertToSQL92(escape, wildCard, singleChar, literal);
            visit(filter.getExpressions().get(0), sb);
            if (matchingCase) {
                sb.append(" LIKE ");
            } else {
                sb.append(" ILIKE ");
            }
            sb.append('\'');
            sb.append(pattern);
            sb.append('\'');
        });
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_NULL), (f, sb) -> {
            final NullOperator<Object> filter = (NullOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" IS NULL");
        });
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_NIL), (f, sb) -> {
            final NilOperator<Object> filter = (NilOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" IS NIL");
        });

        ////////////////////////////////////////////////////////////////////////////
        // GEOMETRY FILTER /////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////

        setFilterHandler(SpatialOperatorName.BBOX, (f, sb) -> {
            final SpatialOperator<Object> filter = (SpatialOperator<Object>) f;
            Expression<Object, ?> exp1 = filter.getExpressions().get(0);
            Expression<Object, ?> exp2 = filter.getExpressions().get(1);
            if (exp1 instanceof ValueReference && exp2 instanceof Literal) {
                //use writing : BBOX(att,v1,v2,v3,v4)
                Object value = ((Literal) exp2).getValue();
                Envelope2D e = new Envelope2D((Envelope) value);
                sb.append("BBOX(");
                sb.append(((ValueReference) exp1).getXPath());
                sb.append(',');
                sb.append(e.getMinX());
                sb.append(',');
                sb.append(e.getMaxX());
                sb.append(',');
                sb.append(e.getMinY());
                sb.append(',');
                sb.append(e.getMaxY());
                sb.append(')');
            } else {
                //use writing BBOX(exp1,exp2)
                sb.append("BBOX(");
                visit(filter.getExpressions().get(0), sb);
                sb.append(',');
                visit(filter.getExpressions().get(1), sb);
                sb.append(')');
            }
        });
        setFilterHandler(DistanceOperatorName.BEYOND, (f, sb) -> {
            final DistanceOperator<Object> filter = (DistanceOperator<Object>) f;
            sb.append("BEYOND(");
            visit(filter.getExpressions().get(0), sb);
            sb.append(',');
            visit(filter.getExpressions().get(1), sb);
            sb.append(')');
        });
        setFilterHandler(SpatialOperatorName.CONTAINS, (f, sb) -> {
            final BinarySpatialOperator<Object> filter = (BinarySpatialOperator<Object>) f;
            sb.append("CONTAINS(");
            visit(filter.getOperand1(), sb);
            sb.append(',');
            visit(filter.getOperand2(), sb);
            sb.append(')');
        });
        setFilterHandler(SpatialOperatorName.CROSSES, (f, sb) -> {
            final BinarySpatialOperator<Object> filter = (BinarySpatialOperator<Object>) f;
            sb.append("CROSSES(");
            visit(filter.getOperand1(), sb);
            sb.append(',');
            visit(filter.getOperand2(), sb);
            sb.append(')');
        });
        setFilterHandler(SpatialOperatorName.DISJOINT, (f, sb) -> {
            final BinarySpatialOperator<Object> filter = (BinarySpatialOperator<Object>) f;
            sb.append("DISJOINT(");
            visit(filter.getOperand1(), sb);
            sb.append(',');
            visit(filter.getOperand2(), sb);
            sb.append(')');
        });
        setFilterHandler(DistanceOperatorName.WITHIN, (f, sb) -> {
            final DistanceOperator<Object> filter = (DistanceOperator<Object>) f;
            sb.append("DWITHIN(");
            visit(filter.getExpressions().get(0), sb);
            sb.append(',');
            visit(filter.getExpressions().get(1), sb);
            sb.append(')');
        });
        setFilterHandler(SpatialOperatorName.EQUALS, (f, sb) -> {
            final BinarySpatialOperator<Object> filter = (BinarySpatialOperator<Object>) f;
            sb.append("EQUALS(");
            visit(filter.getOperand1(), sb);
            sb.append(',');
            visit(filter.getOperand2(), sb);
            sb.append(')');
        });
        setFilterHandler(SpatialOperatorName.INTERSECTS, (f, sb) -> {
            final BinarySpatialOperator<Object> filter = (BinarySpatialOperator<Object>) f;
            sb.append("INTERSECTS(");
            visit(filter.getOperand1(), sb);
            sb.append(',');
            visit(filter.getOperand2(), sb);
            sb.append(')');
        });
        setFilterHandler(SpatialOperatorName.OVERLAPS, (f, sb) -> {
            final BinarySpatialOperator<Object> filter = (BinarySpatialOperator<Object>) f;
            sb.append("OVERLAPS(");
            visit(filter.getOperand1(), sb);
            sb.append(',');
            visit(filter.getOperand2(), sb);
            sb.append(')');
        });
        setFilterHandler(SpatialOperatorName.TOUCHES, (f, sb) -> {
            final BinarySpatialOperator<Object> filter = (BinarySpatialOperator<Object>) f;
            sb.append("TOUCHES(");
            visit(filter.getOperand1(), sb);
            sb.append(',');
            visit(filter.getOperand2(), sb);
            sb.append(')');
        });
        setFilterHandler(SpatialOperatorName.WITHIN, (f, sb) -> {
            final BinarySpatialOperator<Object> filter = (BinarySpatialOperator<Object>) f;
            sb.append("WITHIN(");
            visit(filter.getOperand1(), sb);
            sb.append(',');
            visit(filter.getOperand2(), sb);
            sb.append(')');
        });

        ////////////////////////////////////////////////////////////////////////////
        // TEMPORAL FILTER /////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////

        setFilterHandler(TemporalOperatorName.AFTER, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" AFTER ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.ANY_INTERACTS, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" ANYINTERACTS ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.BEFORE, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" BEFORE ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.BEGINS, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" BEGINS ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.BEGUN_BY, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" BEGUNBY ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.DURING, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" DURING ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.ENDED_BY, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" ENDEDBY ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.ENDS, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" ENDS ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.MEETS, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" MEETS ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.MET_BY, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" METBY ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.OVERLAPPED_BY, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" OVERLAPPEDBY ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.CONTAINS, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" TCONTAINS ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.EQUALS, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" TEQUALS ");
            visit(filter.getExpressions().get(1), sb);
        });
        setFilterHandler(TemporalOperatorName.OVERLAPS, (f, sb) -> {
            final TemporalOperator<Object> filter = (TemporalOperator<Object>) f;
            visit(filter.getExpressions().get(0), sb);
            sb.append(" TOVERLAPS ");
            visit(filter.getExpressions().get(1), sb);
        });

        ////////////////////////////////////////////////////////////////////////////
        // EXPRESSIONS /////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////

        setExpressionHandler(FunctionNames.Literal, (e, sb) -> {
            final Literal<Object,?> exp = (Literal<Object,?>) e;
            final Object value = exp.getValue();
            if (value instanceof Number) {
                final Number num = (Number) value;
                sb.append(num.toString());
            } else if (value instanceof Date) {
                final Date date = (Date) value;
                sb.append(date.toInstant());
            } else if (value instanceof Geometry) {
                final Geometry geometry = (Geometry) value;
                final WKTWriter writer = new WKTWriter();
                final String wkt = writer.write(geometry);
                sb.append(wkt);
            } else {
                sb.append('\'').append(value != null ? value.toString() : null).append('\'');
            }
        });
        setExpressionHandler(FunctionNames.ValueReference, (e, sb) -> {
            final ValueReference<Object,?> exp = (ValueReference<Object,?>) e;
            final String name = exp.getXPath();
            if (patternValueReference.matcher(name).find()) {
                //escape for special chars
                sb.append('"').append(name).append('"');
            } else {
                sb.append(name);
            }
        });
        setExpressionHandler(FunctionNames.Add, (e, sb) -> {
            visit(e.getParameters().get(0), sb);
            sb.append(" + ");
            visit(e.getParameters().get(1), sb);
        });
        setExpressionHandler(FunctionNames.Divide, (e, sb) -> {
            visit(e.getParameters().get(0), sb);
            sb.append(" / ");
            visit(e.getParameters().get(1), sb);
        });
        setExpressionHandler(FunctionNames.Multiply, (e, sb) -> {
            visit(e.getParameters().get(0), sb);
            sb.append(" * ");
            visit(e.getParameters().get(1), sb);
        });
        setExpressionHandler(FunctionNames.Subtract, (e, sb) -> {
            visit(e.getParameters().get(0), sb);
            sb.append(" - ");
            visit(e.getParameters().get(1), sb);
        });
    }

    @Override
    protected void typeNotFound(final String type, final Expression<Object, ?> e, final StringBuilder sb) {
        sb.append(e.getFunctionName().tip()).append('(');
        final List<Expression<Object,?>> exps = e.getParameters();
        if (exps != null) {
            final int size = exps.size();
            if (size == 1) {
                visit(exps.get(0), sb);
            } else if (size > 1) {
                for (int i = 0, n = size - 1; i < n; i++) {
                    visit(exps.get(i), sb);
                    sb.append(" , ");
                }
                visit(exps.get(size - 1), sb);
            }
        }
        sb.append(')');
    }

    /**
     * Given OGC PropertyIsLike Filter information, construct
     * an SQL-compatible 'like' pattern.
     *
     *   SQL   % --> match any number of characters
     *         _ --> match a single character
     *
     *    NOTE; the SQL command is 'string LIKE pattern [ESCAPE escape-character]'
     *    We could re-define the escape character, but I'm not doing to do that in this code
     *    since some databases will not handle this case.
     *
     *   Method:
     *     1.
     *
     *  Examples: ( escape ='!',  multi='*',    single='.'  )
     *    broadway*  -> 'broadway%'
     *    broad_ay   -> 'broad_ay'
     *    broadway   -> 'broadway'
     *
     *    broadway!* -> 'broadway*'  (* has no significance and is escaped)
     *    can't      -> 'can''t'     ( ' escaped for SQL compliance)
     *
     *
     *  NOTE: we also handle "'" characters as special because they are
     *        end-of-string characters.  SQL will convert ' to '' (double single quote).
     *
     *  NOTE: we dont handle "'" as a 'special' character because it would be
     *        too confusing to have a special char as another special char.
     *        Using this will throw an error  (IllegalArgumentException).
     *
     * @author Rob Hranac, Vision for New York
     */
    public static String convertToSQL92(final char escape, final char multi, final char single, final String pattern)
            throws IllegalArgumentException
    {
        if ((escape == '\'') || (multi == '\'') || (single == '\'')) {
            throw new IllegalArgumentException("do not use single quote (') as special char!");
        }
        final StringBuffer result = new StringBuffer(pattern.length() + 5);
        for (int i = 0; i < pattern.length(); i++) {
            final char chr = pattern.charAt(i);
            if (chr == escape) {
                // emit the next char and skip it
                if (i != (pattern.length() - 1)) {
                    result.append(pattern.charAt(i + 1));//
                }
                i++; // skip next char
            } else if (chr == single) {
                result.append('_');
            } else if (chr == multi) {
                result.append('%');
            } else if (chr == '\'') {
                result.append('\'');
                result.append('\'');
            } else {
                result.append(chr);
            }
        }
        return result.toString();
    }
}
