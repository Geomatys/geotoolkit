/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.db;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import org.apache.sis.internal.filter.FunctionNames;
import org.apache.sis.internal.filter.Visitor;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.cql.FilterToCQLVisitor;
import org.geotoolkit.db.reverse.ColumnMetaModel;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.filter.visitor.AbstractVisitor;
import org.geotoolkit.util.NamesExt;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.opengis.feature.FeatureType;
import org.opengis.filter.BetweenComparisonOperator;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.DistanceOperator;
import org.opengis.filter.DistanceOperatorName;
import org.opengis.filter.Expression;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.LikeOperator;
import org.opengis.filter.Literal;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.ResourceId;
import org.opengis.filter.SpatialOperator;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.ValueReference;
import org.opengis.geometry.Envelope;
import org.opengis.util.GenericName;

/**
 * Convert filters and expressions in SQL.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class FilterToSQL extends Visitor<Object,StringBuilder> {
    protected final FeatureType featureType;

    protected int currentsrid;

    /**
     * Creates a new instance initialized with a default set of handlers.
     */
    protected FilterToSQL(final FeatureType featureType, final PrimaryKey pkey) {
        this.featureType = featureType;
        setFilterHandler(AbstractVisitor.RESOURCEID_NAME, (f, sb) -> {
            final ResourceId filter = (ResourceId) f;
            sb.append('(');
            final FilterFactory ff = FilterUtilities.FF;
            final List<ColumnMetaModel> columns = pkey.getColumns();

            final String ids = filter.getIdentifier();
            final List<Filter> idPartFilters = new ArrayList<>();
            final Object[] idValues = pkey.decodeFID(ids);
            for (int k=0; k < idValues.length; k++) {
                idPartFilters.add(ff.equal(ff.property(columns.get(k).getName()), ff.literal(idValues[k])));
            }
            final Filter and = ff.and(idPartFilters);
            visit(and, sb);
            sb.append(')');
        });
        setNullAndNilHandlers((f, sb) -> {
            visit(f.getExpressions().get(0), sb);
            sb.append(" IS NULL");
        });
        setFilterHandler(Filter.exclude().getOperatorType(), new Constant("1=0"));
        setFilterHandler(Filter.include().getOperatorType(), new Constant("1=1"));
        setFilterHandler(LogicalOperatorName.AND, new Logical("AND"));
        setFilterHandler(LogicalOperatorName.OR,  new Logical("OR"));
        setFilterHandler(LogicalOperatorName.NOT, (f, sb) -> {
            final LogicalOperator<Object> filter = (LogicalOperator<Object>) f;
            sb.append("NOT(");
            visit(filter.getOperands().get(0), sb);
            sb.append(')');
        });
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_BETWEEN), (f, sb) -> {
            final BetweenComparisonOperator<Object> filter = (BetweenComparisonOperator<Object>) f;
            visit(filter.getExpression(),    sb); sb.append(" BETWEEN ");
            visit(filter.getLowerBoundary(), sb); sb.append(" AND ");
            visit(filter.getUpperBoundary(), sb);
        });
        // TODO: use of "ilike" should escape % and _ characters.
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_EQUAL_TO,                 new Comparison("=", "ilike"));
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_NOT_EQUAL_TO,             new Comparison("<>", "not ilike"));
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_GREATER_THAN,             new Comparison(">"));
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_GREATER_THAN_OR_EQUAL_TO, new Comparison(">="));
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_LESS_THAN,                new Comparison("<"));
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_LESS_THAN_OR_EQUAL_TO,    new Comparison("<="));
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_LIKE), (f, sb) -> {
            final LikeOperator<Object> filter = (LikeOperator<Object>) f;
            final boolean matchingCase = filter.isMatchingCase();
            List<Expression<Object, ?>> expressions = filter.getExpressions();
            final Expression<Object,?>  expression  = expressions.get(0);
            final Expression<Object,?>  literal     = expressions.get(1);
            String pattern = FilterToCQLVisitor.convertToSQL92(
                    filter.getEscapeChar(),
                    filter.getWildCard(),
                    filter.getSingleChar(),
                    (String) ((Literal<Object,?>) literal).getValue());

            if (!matchingCase) {
                pattern = pattern.toUpperCase();
                sb.append(" UPPER(");
            }

            // we don't know the type, make a type cast to be on the safe side
            sb.append(" CAST( ");
            visit(expression, sb);
            sb.append(" AS VARCHAR)");
            if (!matchingCase){
                sb.append(')');
            }
            sb.append(" LIKE '").append(pattern).append("' ");
        });
        setFilterHandler(SpatialOperatorName.BBOX, new Spatial("st_intersects"));
        setFilterHandler(DistanceOperatorName.BEYOND, new SpatialWithDistance("st_distance", "st_dwithin"));
        setFilterHandler(SpatialOperatorName.CONTAINS, new Spatial("st_contains", "st_within"));
        setFilterHandler(SpatialOperatorName.CROSSES, new Spatial("st_crosses"));
        setFilterHandler(SpatialOperatorName.DISJOINT, (f, sb) -> {
            final BinarySpatialOperator<Object> filter = (BinarySpatialOperator<Object>) f;
            final PreparedSpatialFilter prepared = new PreparedSpatialFilter(filter);
            sb.append("not(st_intersects(");
            visit(prepared.property, sb); sb.append(',');
            visit(prepared.geometry, sb); sb.append("))");
        });
        setFilterHandler(DistanceOperatorName.WITHIN, new SpatialWithDistance("st_dwithin", "st_distance"));
        setFilterHandler(SpatialOperatorName.EQUALS, new Spatial("st_equals"));
        setFilterHandler(SpatialOperatorName.INTERSECTS, new Spatial("st_intersects"));
        setFilterHandler(SpatialOperatorName.OVERLAPS, new Spatial("st_overlaps"));
        setFilterHandler(SpatialOperatorName.TOUCHES, new Spatial("st_touches"));
        setFilterHandler(SpatialOperatorName.WITHIN, new Spatial("st_within", "st_contains"));
        setExpressionHandler(FunctionNames.Add, new Arithmetic('+'));
        setExpressionHandler(FunctionNames.Subtract, new Arithmetic('-'));
        setExpressionHandler(FunctionNames.Multiply, new Arithmetic('*'));
        setExpressionHandler(FunctionNames.Divide, new Arithmetic('/'));
        setExpressionHandler(FunctionNames.ValueReference, (e, sb) -> {
            final ValueReference<Object,?> expression = (ValueReference<Object,?>) e;
            final GenericName name = NamesExt.valueOf(expression.getXPath());
            sb.append('"').append(name.tip()).append('"');
        });
        setExpressionHandler(FunctionNames.Literal, (e, sb) -> {
            final Literal<Object,?> expression = (Literal<Object,?>) e;
            final Object value = expression.getValue();
            writeValue(sb, value, currentsrid);
        });
    }

    /**
     * Ensures the given double is not an infinite, doesn't work well with SQL and postgres.
     *
     * @return double unchanged if not an infinite.
     */
    private static double checkInfinites(final double value) {
        if (value == Double.NEGATIVE_INFINITY) {
            return Double.MIN_VALUE;
        } else if (value == Double.POSITIVE_INFINITY) {
            return Double.MAX_VALUE;
        } else {
            return value;
        }
    }

    /**
     * Handler for filters materialized by a constant values.
     * This is used for "include" or "exclude" filters.
     */
    private static final class Constant implements BiConsumer<Filter<Object>, StringBuilder> {
        private final String text;

        /**
         * Creates a new handler with the given constant value.
         *
         * @param  text  the constant value.
         */
        public Constant(final String text) {
            this.text = text;
        }

        @Override public void accept(final Filter<Object> f, final StringBuilder sb) {
            sb.append(text);
        }
    }

    /**
     * Handler for {@link BinaryLogicOperator} materialized by an expression such as
     * {@code "(a AND b AND c AND d"} (taking the "AND" operator as an example).
     */
    private final class Logical implements BiConsumer<Filter<Object>, StringBuilder> {
        private final String operator;

        /**
         * Creates a new handler for the given operator.
         * Examples: {@code "AND"}, {@code "OR="}.
         *
         * @param  operator  the operator without space.
         */
        public Logical(final String operator) {
            this.operator = operator;
        }

        @Override public void accept(final Filter<Object> f, final StringBuilder sb) {
            final LogicalOperator filter = (LogicalOperator) f;
            final List<Filter<Object>> subs = filter.getOperands();
            sb.append('(');
            final int n = subs.size();
            for (int i=0; i<n; i++) {
                if (i > 0) {
                    sb.append(' ').append(operator).append(' ');
                }
                visit(subs.get(i), sb);
            }
            sb.append(')');
        }
    }

    /**
     * Handler for {@link BinaryComparisonOperator} materialized by an expression such as {@code "(a = d"}
     * (taking the "=" operator as an example). A different operator can optionally be used for case-sensitive
     * comparisons.
     */
    protected final class Comparison implements BiConsumer<Filter<Object>, StringBuilder> {
        private final String operator, caseInsensitive;

        /**
         * Creates a new handler for the given operator.
         * Examples: {@code ">"}, {@code ">="}.
         *
         * @param  operator  the operator without space.
         */
        public Comparison(final String operator) {
            this.operator   = operator;
            caseInsensitive = operator;
        }

        /**
         * Creates a new handler for the given operator.
         * Examples: {@code ">"}, {@code ">="}.
         *
         * @param  operator  the operator (without case) for case-sensitive comparisons.
         * @param  caseInsensitive  the operator for case-insensitive operations.
         */
        public Comparison(final String operator, final String caseInsensitive) {
            this.operator = operator;
            this.caseInsensitive = caseInsensitive;
        }

        @SuppressWarnings("StringEquality")
        @Override public void accept(final Filter<Object> f, final StringBuilder sb) {
            final BinaryComparisonOperator filter = (BinaryComparisonOperator) f;
            final boolean isMatchingCase = (operator == caseInsensitive) || filter.isMatchingCase();
            visit(filter.getOperand1(), sb);
            sb.append(' ').append(isMatchingCase ? operator : caseInsensitive).append(' ');
            visit(filter.getOperand2(), sb);
        }
    }

    /**
     * Handler for {@link BinarySpatialOperator} materialized by an expression such as
     * {@code "st_intersects(a, d)"} (taking the "st_intersects" operator as an example).
     * A different operator can optionally be used when arguments are swapped.
     */
    private final class Spatial implements BiConsumer<Filter<Object>, StringBuilder> {
        private final String operator, swapped;

        public Spatial(final String operator) {
            this.operator = operator;
            this.swapped  = operator;
        }

        public Spatial(final String operator, final String swapped) {
            this.operator = operator;
            this.swapped  = swapped;
        }

        @Override public void accept(final Filter<Object> f, final StringBuilder sb) {
            final BinarySpatialOperator filter = (BinarySpatialOperator) f;
            final PreparedSpatialFilter prepared = new PreparedSpatialFilter(filter);
            sb.append(prepared.swap ? swapped : operator).append('(');
            visit(prepared.property, sb); sb.append(',');
            visit(prepared.geometry, sb); sb.append(')');
        }
    }

    /**
     * Handler for {@link DistanceBufferOperator} materialized by an expression such as
     * {@code "st_distance(a, d) > c"} (taking the "st_distance" operator as an example).
     * A different operator can optionally be used when arguments are swapped.
     */
    private final class SpatialWithDistance implements BiConsumer<Filter<Object>, StringBuilder> {
        private final String operator, swapped;

        public SpatialWithDistance(final String operator) {
            this.operator = operator;
            this.swapped  = operator;
        }

        public SpatialWithDistance(final String operator, final String swapped) {
            this.operator = operator;
            this.swapped  = swapped;
        }

        @Override public void accept(final Filter<Object> f, final StringBuilder sb) {
            final DistanceOperator<Object> filter = (DistanceOperator<Object>) f;
            final PreparedSpatialFilter prepared = new PreparedSpatialFilter(filter);
            if (prepared.swap) {
                sb.append(swapped).append('(');
                visit(prepared.property, sb); sb.append(',');
                visit(prepared.geometry, sb); sb.append(',');
                sb.append(filter.getDistance()).append(')');
            } else {
                sb.append(operator).append('(');
                visit(prepared.property, sb ); sb.append(',');
                visit(prepared.geometry, sb); sb.append(") > ");
                sb.append(filter.getDistance());
            }
        }
    }

    private final class Arithmetic implements BiConsumer<Expression<Object,?>, StringBuilder> {
        private final char operator;

        public Arithmetic(final char operator) {
            this.operator = operator;
        }

        @Override public void accept(final Expression<Object,?> e, final StringBuilder sb) {
            List<Expression<? super Object, ?>> parameters = e.getParameters();
            visit(parameters.get(0), sb.append('('));
            visit(parameters.get(1), sb.append(' ').append(operator).append(' '));
            sb.append(')');
        }
    }

    /**
     * Prepares a spatial filter, isolate the field and geometry parts.
     * Eventually converting it in a geometry.
     */
    private final class PreparedSpatialFilter {
        public ValueReference<Object,?> property;
        public Literal<Object,?> geometry;
        public boolean swap;

        public PreparedSpatialFilter(final SpatialOperator<Object> filter) {
            List<Expression<? super Object, ?>> expressions = filter.getExpressions();
            final Expression<Object,?> exp1 = expressions.get(0);
            final Expression<Object,?> exp2 = expressions.get(1);
            if (exp1 instanceof ValueReference) {
                swap     = false;
                property = (ValueReference<Object,?>) exp1;
                geometry = (Literal<Object,?>) exp2;
            } else {
                swap     = true;
                property = (ValueReference<Object,?>) exp2;
                geometry = (Literal<Object,?>) exp1;
            }
            // Change Envelope in polygon.
            final Object obj = geometry.getValue();
            if (obj instanceof Envelope) {
                final Envelope env = (Envelope) obj;
                final FilterFactory ff = FilterUtilities.FF;
                final GeometryFactory gf = org.geotoolkit.geometry.jts.JTS.getFactory();
                final Coordinate[] coords = new Coordinate[5];
                double minx = checkInfinites(env.getMinimum(0));
                double maxx = checkInfinites(env.getMaximum(0));
                double miny = checkInfinites(env.getMinimum(1));
                double maxy = checkInfinites(env.getMaximum(1));

                coords[0] = new Coordinate(minx,miny);
                coords[1] = new Coordinate(minx,maxy);
                coords[2] = new Coordinate(maxx,maxy);
                coords[3] = new Coordinate(maxx,miny);
                coords[4] = new Coordinate(minx,miny);
                final LinearRing ring = gf.createLinearRing(coords);
                final Geometry geom = gf.createPolygon(ring, new LinearRing[0]);
                geometry = ff.literal(geom);
                property = setSRID(property);
            }
        }
    }

    /**
     * Set the current srid, extract it from feature type.
     * Required when encoding geometry.
     */
    protected abstract ValueReference setSRID(ValueReference property);

    public void writeValue(final StringBuilder sb, Object candidate, int srid){
        if (candidate instanceof Date) {
            // Convert it to a timestamp, string representation won't be ambiguious like dates toString()
            candidate = new Timestamp(((Date)candidate).getTime());
        }
        if (candidate == null) {
          sb.append("NULL");
        } else if (candidate instanceof Boolean) {
            sb.append(candidate);
        } else if (candidate instanceof Double) {
            if (((Double) candidate).isNaN()) {
                sb.append("'NaN'");
            } else {
                sb.append(candidate);
            }
        } else if (candidate instanceof Float) {
            if (((Float)candidate).isNaN()) {
                sb.append("'NaN'");
            } else {
                sb.append(candidate);
            }
        } else if (candidate instanceof Number) {
            sb.append(candidate);
        } else if (candidate instanceof byte[]) {
            // special case for byte array
            sb.append("decode('")
              .append(Base64.getEncoder().encodeToString((byte[]) candidate))
              .append("','base64')");
        } else if (candidate instanceof Geometry) {
            // evaluate the literal and store it for later
            Geometry geom = (Geometry) candidate;
            if (emptyAsNull(geom)) {
                //empty geometries are interpreted as Geometrycollection in postgis < 2
                //this breaks the column geometry type constraint so we replace those by null
                sb.append("NULL");
            } else {
                if (geom instanceof LinearRing) {
                    //postgis does not handle linear rings, convert to just a line string
                    geom = geom.getFactory().createLineString(((LinearRing) geom).getCoordinateSequence());
                }
                sb.append("st_geomfromtext('")
                  .append(geom.toText());
                if (srid > 0) {
                    sb.append("',").append(srid).append(')');
                } else {
                    sb.append("')");
                }
            }
        } else if (candidate.getClass().isArray()) {
            final int size = Array.getLength(candidate);
            sb.append("'{");
            for (int i=0; i<size; i++) {
                if (i>0) {
                    sb.append(',');
                }
                final Object o = Array.get(candidate, i);
                if (o != null && o.getClass().isArray()) {
                    final StringBuilder suba = new StringBuilder();
                    writeValue(suba, o, -1);
                    if (suba.charAt(0) == '\'') {
                        sb.append(suba.substring(1, suba.length() - 1));
                    } else {
                        sb.append(suba.toString());
                    }
                } else if (!(o instanceof Number || o instanceof Boolean) && o != null) {
                    // we don't know what this is, let's convert back to a string
                    String encoding = null;
                    try {
                        encoding = ObjectConverters.convert(o, String.class);
                    } catch (UnconvertibleObjectException | UnsupportedOperationException e) {
                        Logging.recoverableException(getLogger(), getClass(), "writeValue", e);
                    }
                    if (encoding == null) {
                        // could not convert back to string, use original value
                        encoding = o.toString();
                    }
                    // single quotes must be escaped to have a valid sql string
                    appendArrayElement(sb, encoding.replaceAll("'", "''"));
                }else{
                    writeValue(sb,o,-1);
                }
            }
            sb.append("}'");
        } else {
            // we don't know what this is, let's convert back to a string
            String encoded = null;
            try {
                encoded = ObjectConverters.convert(candidate, String.class);
            } catch (UnconvertibleObjectException | UnsupportedOperationException e) {
                Logging.recoverableException(getLogger(), getClass(), "writeValue", e);
            }
            if (encoded == null) {
                // could not convert back to string, use original value
                encoded = candidate.toString();
            }
            // single quotes must be escaped to have a valid sql string
            final String escaped = encoded.replaceAll("'", "''");
            sb.append('\'').append(escaped).append('\'');
        }
    }

    protected boolean emptyAsNull(Geometry geom) {
        return geom.isEmpty();
    }

    protected void appendArrayElement(final StringBuilder sb, final String escaped) {
        sb.append('"').append(escaped).append('"');
    }

    protected Logger getLogger() {
        return Logger.getLogger(getClass().getName());
    }
}
