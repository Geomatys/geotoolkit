/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.misc;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.geotools.data.jdbc.fidmapper.FIDMapper;
import org.geotools.filter.FilterCapabilities;
import org.geotools.filter.LikeFilterImpl;
import org.geotools.util.Converters;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.BinaryLogicOperator;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.BinaryExpression;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.util.logging.Logging;

/**
 * Encodes a filter into a CQL statement.  NOT TRUSTABLE YET !!!
 * 
 * 
 * This version was ported from the original to support org.opengis.filter type
 * Filters.
 *
 * @author originally by Chris Holmes, TOPP
 * @author ported by Saul Farber, MassGIS
 * @author updated to CQL by Johann Sorel
 *
 * @task REVISIT: need to figure out exceptions, we're currently eating io
 *       errors, which is bad. Probably need a generic visitor exception.
 * 
 */
/*
 * TODO: Use the new FilterCapabilities.  This may fall out of using the new
 * PrePostFilterSplitter code.
 * 
 * TODO: Use the new Geometry classes from org.opengis.  Not sure
 * when this will be required, but it's on the horizon here.
 * 
 * Non Javadoc comments:
 * 
 * Note that the old method allowed us to write WAY fewer methods, as we didn't
 * need to cover all the cases with exlpicit methods (as the new
 * org.opengis.filter.FilterVisitor and ExpressionVisitor methods require
 * us to do).
 * 
 * The code is split into methods to support the FilterVisitor interface first
 * then the ExpressionVisitor methods second.
 *  
 */

public class FilterToCQL implements FilterVisitor, ExpressionVisitor {

    /** error message for exceptions */
    protected static final String IO_ERROR = "io problem writing filter";
    /** The filter types that this class can encode */
    protected FilterCapabilities capabilities = null;
    /** Standard java logger */
    private static final Logger LOGGER = Logging.getLogger(FilterToCQL.class);
    /** Map of expression types to cql representation */
    private static Map expressions = new HashMap();

    static {
        expressions.put(Add.class, "+");
        expressions.put(Divide.class, "/");
        expressions.put(Multiply.class, "*");
        expressions.put(Subtract.class, "-");
    }
    /** Character used to escape database schema, table and column names */
    private String cqlNameEscape = "";
    /** where to write the constructed string from visiting the filters. */
    protected Writer out;
    /** the fid mapper used to encode the fid filters */
    protected FIDMapper mapper;
    /** the schmema the encoder will be used to be encode cql for */
    protected SimpleFeatureType featureType;

    /**
     * Default constructor
     */
    public FilterToCQL() {
    }

    public FilterToCQL(Writer out) {
        this.out = out;
    }

    /**
     * Performs the encoding, sends the encoded CQL to the writer passed in.
     *
     * @param filter the Filter to be encoded.
     * @throws org.geotoolkit.gui.swing.misc.FilterToCQLException  If filter type not supported, or if there
     *         were io problems.
     *
     */
    public void encode(Filter filter) throws FilterToCQLException {
        if (out == null) {
            throw new FilterToCQLException("Can't encode to a null writer.");
        }
        if (getCapabilities().fullySupports(filter)) {
            filter.accept(this, null);
        } else {
            throw new FilterToCQLException("Filter type not supported");
        }
    }

    /**
     * purely a convenience method.
     * 
     * Equivalent to:
     * 
     *  StringWriter out = new StringWriter();
     *  new FilterToCQL(out).encode(filter);
     *  out.getBuffer().toString();
     * 
     * @param filter
     * @return a string representing the filter encoded to CQL.
     * @throws FilterToCQLException If filter type not supported, or if there
     *         were io problems.
     */
    public String encodeToString(Filter filter) throws FilterToCQLException {
        StringWriter out = new StringWriter();
        this.out = out;
        this.encode(filter);
        return out.getBuffer().toString();
    }

    /**
     * Sets the featuretype the encoder is encoding cql for.
     * <p>
     * This is used for context for attribute expressions when encoding to cql. 
     * </p>
     * 
     * @param featureType
     */
    public void setFeatureType(SimpleFeatureType featureType) {
        this.featureType = featureType;
    }

    /**
     * Sets the FIDMapper that will be used in subsequente visit calls. There
     * must be a FIDMapper in order to invoke the FIDFilter encoder.
     *
     * @param mapper
     */
    public void setFIDMapper(FIDMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Sets the capabilities of this filter.
     *
     * @return FilterCapabilities for this Filter
     */
    protected FilterCapabilities createFilterCapabilities() {
        FilterCapabilities capabilities = new FilterCapabilities();

        capabilities.addAll(FilterCapabilities.LOGICAL_OPENGIS);
        capabilities.addAll(FilterCapabilities.SIMPLE_COMPARISONS_OPENGIS);
        capabilities.addType(PropertyIsNull.class);
        capabilities.addType(PropertyIsBetween.class);
        capabilities.addType(Id.class);
        capabilities.addType(IncludeFilter.class);
        capabilities.addType(ExcludeFilter.class);

        return capabilities;
    }

    /**
     * Describes the capabilities of this encoder.
     * 
     * <p>
     * Performs lazy creation of capabilities.
     * </p>
     * 
     * If you're subclassing this class, override createFilterCapabilities
     * to declare which filtercapabilities you support.  Don't use
     * this method.
     *
     * @return The capabilities supported by this encoder.
     */
    public synchronized final FilterCapabilities getCapabilities() {
        if (capabilities == null) {
            capabilities = createFilterCapabilities();
        }

        return capabilities; //maybe clone?  Make immutable somehow
    }

    // BEGIN IMPLEMENTING org.opengis.filter.FilterVisitor METHODS
    /**
     * @see {@link FilterVisitor#visit(ExcludeFilter, Object)}
     * 
     * not used in CQL
     * 
     * @param filter the filter to be visited
     */
    public Object visit(ExcludeFilter filter, Object extraData) {
        //        try {
//            out.write("FALSE");
//        } catch (IOException ioe) {
//            throw new RuntimeException(IO_ERROR, ioe);
//        }
        return extraData;
    }

    /**
     * @see {@link FilterVisitor#visit(IncludeFilter, Object)}
     * 
     * not used in CQL
     * 
     * @param filter the filter to be visited
     *  
     */
    public Object visit(IncludeFilter filter, Object extraData) {
        //        try {
//            out.write("TRUE");
//        } catch (IOException ioe) {
//            throw new RuntimeException(IO_ERROR, ioe);
//        }
        return extraData;
    }

    /**
     * Writes the CQL for the PropertyIsBetween Filter.
     *
     * @param filter the Filter to be visited.
     *
     * @throws RuntimeException for io exception with writer
     */
    public Object visit(PropertyIsBetween filter, Object extraData) throws RuntimeException {
        LOGGER.finer("exporting PropertyIsBetween");

        Expression expr = (Expression) filter.getExpression();
        Expression lowerbounds = (Expression) filter.getLowerBoundary();
        Expression upperbounds = (Expression) filter.getUpperBoundary();

        Class context;
        AttributeDescriptor attType = (AttributeDescriptor) expr.evaluate(featureType);
        if (attType != null) {
            context = attType.getType().getBinding();
        } else {
            //assume it's a string?
            context = String.class;
        }

        try {
            expr.accept(this, extraData);
            out.write(" BETWEEN ");
            lowerbounds.accept(this, context);
            out.write(" AND ");
            upperbounds.accept(this, context);
        } catch (java.io.IOException ioe) {
            throw new RuntimeException(IO_ERROR, ioe);
        }
        return extraData;
    }

    /**
     * Writes the CQL for the Like Filter.  Assumes the current java
     * implemented wildcards for the Like Filter: . for multi and .? for
     * single. And replaces them with the CQL % and _, respectively.
     *
     * @param filter the Like Filter to be visited.
     *
     * @task REVISIT: Need to think through the escape char, so it works  right
     *       when Java uses one, and escapes correctly with an '_'.
     */
    public Object visit(PropertyIsLike filter, Object extraData) {
        char esc = filter.getEscape().charAt(0);
        char multi = filter.getWildCard().charAt(0);
        char single = filter.getSingleChar().charAt(0);
        String pattern = LikeFilterImpl.convertToSQL92(esc, multi, single, false, filter.getLiteral());


        Expression att = filter.getExpression();

        try {
            att.accept(this, extraData);
            out.write(" LIKE '");
            out.write(pattern);
            out.write("' ");
        } catch (java.io.IOException ioe) {
            throw new RuntimeException(IO_ERROR, ioe);
        }
        return extraData;
    }

    /**
     * Write the CQL for an And filter
     * 
     * @param filter the filter to visit
     * @param extraData extra data (unused by this method)
     * 
     */
    public Object visit(And filter, Object extraData) {
        return visit((BinaryLogicOperator) filter, "AND");
    }

    /**
     * Write the CQL for a Not filter
     * 
     * @param filter the filter to visit
     * @param extraData extra data (unused by this method)
     * 
     */
    public Object visit(Not filter, Object extraData) {
        return visit((BinaryLogicOperator) filter, "NOT");
    }

    /**
     * Write the CQL for an Or filter
     * 
     * @param filter the filter to visit
     * @param extraData extra data (unused by this method)
     * 
     */
    public Object visit(Or filter, Object extraData) {
        return visit((BinaryLogicOperator) filter, "OR");
    }

    /**
     * Common implementation for BinaryLogicOperator filters.  This way
     * they're all handled centrally.
     *
     * @param filter the logic statement to be turned into CQL.
     * @param extraData extra filter data.  Not modified directly by this method.
     */
    protected Object visit(BinaryLogicOperator filter, Object extraData) {
        LOGGER.finer("exporting LogicFilter");

        String type = (String) extraData;

        try {
            java.util.Iterator list = filter.getChildren().iterator();

            if (filter instanceof Not) {
                out.write(type + " (");
                ((Filter) list.next()).accept(this, extraData);
                out.write(")");
            } else { //AND or OR
                out.write("(");

                while (list.hasNext()) {
                    ((Filter) list.next()).accept(this, extraData);

                    if (list.hasNext()) {
                        out.write(" " + type + " ");
                    }
                }

                out.write(")");
            }
        } catch (java.io.IOException ioe) {
            throw new RuntimeException(IO_ERROR, ioe);
        }
        return extraData;
    }

    /**
     * Write the CQL for this kind of filter
     * 
     * @param filter the filter to visit
     * @param extraData extra data (unused by this method)
     * 
     */
    public Object visit(PropertyIsEqualTo filter, Object extraData) {
        visitBinaryComparisonOperator((BinaryComparisonOperator) filter, "=");
        return extraData;
    }

    /**
     * Write the CQL for this kind of filter
     * 
     * @param filter the filter to visit
     * @param extraData extra data (unused by this method)
     * 
     */
    public Object visit(PropertyIsGreaterThanOrEqualTo filter, Object extraData) {
        visitBinaryComparisonOperator((BinaryComparisonOperator) filter, ">=");
        return extraData;
    }

    /**
     * Write the CQL for this kind of filter
     * 
     * @param filter the filter to visit
     * @param extraData extra data (unused by this method)
     * 
     */
    public Object visit(PropertyIsGreaterThan filter, Object extraData) {
        visitBinaryComparisonOperator((BinaryComparisonOperator) filter, ">");
        return extraData;
    }

    /**
     * Write the CQL for this kind of filter
     * 
     * @param filter the filter to visit
     * @param extraData extra data (unused by this method)
     * 
     */
    public Object visit(PropertyIsLessThan filter, Object extraData) {
        visitBinaryComparisonOperator((BinaryComparisonOperator) filter, "<");
        return extraData;
    }

    /**
     * Write the CQL for this kind of filter
     * 
     * @param filter the filter to visit
     * @param extraData extra data (unused by this method)
     * 
     */
    public Object visit(PropertyIsLessThanOrEqualTo filter, Object extraData) {
        visitBinaryComparisonOperator((BinaryComparisonOperator) filter, "<=");
        return extraData;
    }

    /**
     * Write the CQL for this kind of filter
     * 
     * @param filter the filter to visit
     * @param extraData extra data (unused by this method)
     * 
     */
    public Object visit(PropertyIsNotEqualTo filter, Object extraData) {
        visitBinaryComparisonOperator((BinaryComparisonOperator) filter, "!=");
        return extraData;
    }

    /**
     * Common implementation for BinaryComparisonOperator filters.  This way
     * they're all handled centrally.
     *  
     *  DJB: note, postgis overwrites this implementation because of the way
     *       null is handled.  This is for <PropertyIsNull> filters and <PropertyIsEqual> filters
     *       are handled.  They will come here with "property = null".  
     *       NOTE: 
     *        SELECT * FROM <table> WHERE <column> isnull;  -- postgreCQL
     *        SELECT * FROM <table> WHERE isnull(<column>); -- oracle???
     *
     * @param filter the comparison to be turned into CQL.
     *
     * @throws RuntimeException for io exception with writer
     */
    protected void visitBinaryComparisonOperator(BinaryComparisonOperator filter, Object extraData) throws RuntimeException {
        LOGGER.finer("exporting CQL ComparisonFilter");

        Expression left = filter.getExpression1();
        Expression right = filter.getExpression2();
        Class leftContext = null, rightContext = null;
        if (left instanceof PropertyName) {
            // aha!  It's a propertyname, we should get the class and pass it in
            // as context to the tree walker.
            AttributeDescriptor attType = (AttributeDescriptor) left.evaluate(featureType);
            if (attType != null) {
                rightContext = attType.getType().getBinding();
            }
        }

        if (right instanceof PropertyName) {
            AttributeDescriptor attType = (AttributeDescriptor) right.evaluate(featureType);
            if (attType != null) {
                leftContext = attType.getType().getBinding();
            }
        }

        String type = (String) extraData;

        try {
            left.accept(this, leftContext);
            out.write(" " + type + " ");
            right.accept(this, rightContext);
        } catch (java.io.IOException ioe) {
            throw new RuntimeException(IO_ERROR, ioe);
        }
    }

    /**
     * Writes the CQL for the Null Filter.
     *
     * @param filter the null filter to be written to CQL.
     *
     * @throws RuntimeException for io exception with writer
     */
    public Object visit(PropertyIsNull filter, Object extraData) throws RuntimeException {
        LOGGER.finer("exporting NullFilter");

        Expression expr = filter.getExpression();

        try {
            expr.accept(this, extraData);
            out.write(" IS NULL ");
        } catch (java.io.IOException ioe) {
            throw new RuntimeException(IO_ERROR, ioe);
        }
        return extraData;
    }

    /**
     * Encodes an Id filter
     *
     * @param filter the
     *
     * @throws RuntimeException If there's a problem writing output
     *
     */
    public Object visit(Id filter, Object extraData) {
        if (mapper == null) {
            throw new RuntimeException(
                    "Must set a fid mapper before trying to encode FIDFilters");
        }

        FeatureId[] fids = (FeatureId[]) filter.getIdentifiers().toArray(new FeatureId[filter.getIdentifiers().size()]);
        LOGGER.finer("Exporting FID=" + Arrays.asList(fids));

        // prepare column name array
        String[] colNames = new String[mapper.getColumnCount()];

        for (int i = 0; i < colNames.length; i++) {
            colNames[i] = mapper.getColumnName(i);
        }

        for (int i = 0; i < fids.length; i++) {
            try {
                Object[] attValues = mapper.getPKAttributes(fids[i].getID());

                out.write("(");

                for (int j = 0; j < attValues.length; j++) {
                    out.write(escapeName(colNames[j]));
                    out.write(" = '");
                    out.write(attValues[j].toString()); //DJB: changed this to attValues[j] from attValues[i].
                    out.write("'");

                    if (j < (attValues.length - 1)) {
                        out.write(" AND ");
                    }
                }

                out.write(")");

                if (i < (fids.length - 1)) {
                    out.write(" OR ");
                }
            } catch (java.io.IOException e) {
                throw new RuntimeException(IO_ERROR, e);
            }
        }

        return extraData;
    }

    public Object visit(BBOX filter, Object extraData) {
        return visitBinarySpatialOperator((BinarySpatialOperator) filter, extraData);
    }

    public Object visit(Beyond filter, Object extraData) {
        return visitBinarySpatialOperator((BinarySpatialOperator) filter, extraData);
    }

    public Object visit(Contains filter, Object extraData) {
        return visitBinarySpatialOperator((BinarySpatialOperator) filter, extraData);
    }

    public Object visit(Crosses filter, Object extraData) {
        return visitBinarySpatialOperator((BinarySpatialOperator) filter, extraData);
    }

    public Object visit(Disjoint filter, Object extraData) {
        return visitBinarySpatialOperator((BinarySpatialOperator) filter, extraData);
    }

    public Object visit(DWithin filter, Object extraData) {
        return visitBinarySpatialOperator((BinarySpatialOperator) filter, extraData);
    }

    public Object visit(Equals filter, Object extraData) {
        return visitBinarySpatialOperator((BinarySpatialOperator) filter, extraData);
    }

    public Object visit(Intersects filter, Object extraData) {
        return visitBinarySpatialOperator((BinarySpatialOperator) filter, extraData);
    }

    public Object visit(Overlaps filter, Object extraData) {
        return visitBinarySpatialOperator((BinarySpatialOperator) filter, extraData);
    }

    public Object visit(Touches filter, Object extraData) {
        return visitBinarySpatialOperator((BinarySpatialOperator) filter, extraData);
    }

    public Object visit(Within filter, Object extraData) {
        return visitBinarySpatialOperator((BinarySpatialOperator) filter, extraData);
    }

    /**
     * @see {@link FilterVisitor#visit()}
     */
    protected Object visitBinarySpatialOperator(BinarySpatialOperator filter, Object extraData) {
        throw new RuntimeException(
                "Subclasses must implement this method in order to handle geometries");
    }

    /**
     * Encodes a null filter value.  The current implementation
     * does exactly nothing.
     * @param extraData extra data to be used to evaluate the filter
     * @return the untouched extraData parameter
     */
    public Object visitNullFilter(Object extraData) {
        return extraData;
    }

    // END IMPLEMENTING org.opengis.filter.FilterVisitor METHODS
    // START IMPLEMENTING org.opengis.filter.ExpressionVisitor METHODS
    /**
     * Writes the CQL for the attribute Expression.
     * 
     * NOTE:  This (default) implementation doesn't handle XPath at all.
     * Not sure exactly how to do that in a general way.  How to map from the XPATH of the
     * property name into a column or something?  Use propertyName.evaluate()?
     *
     * @param expression the attribute to turn to CQL.
     *
     * @throws RuntimeException for io exception with writer
     */
    public Object visit(PropertyName expression, Object extraData) throws RuntimeException {
        LOGGER.finer("exporting PropertyName");

        try {
            out.write(escapeName(expression.getPropertyName()));
        } catch (java.io.IOException ioe) {
            throw new RuntimeException("IO problems writing attribute exp", ioe);
        }
        return extraData;
    }

    /**
     * Export the contents of a Literal Expresion
     *
     * @param expression the Literal to export
     *
     * @throws RuntimeException for io exception with writer
     */
    public Object visit(Literal expression, Object context) throws RuntimeException {
        LOGGER.finer("exporting LiteralExpression");

        //type to convert the literal to
        Class target = (Class) context;

        try {
            Object literal = null;

            if (target == Geometry.class && expression.getValue() instanceof Geometry) {
                //call this method for backwards compatability with subclasses
                visitLiteralGeometry(expression);
                return context;
            } else if (target != null) {
                //convert the literal to the required type
				//JD except for numerics, let the database do the converstion

                if (Number.class.isAssignableFrom(target)) {
                //dont convert
                } else {
                    //convert
                    literal = expression.evaluate(null, target);
                }

                if (literal == null) {
                    //just use string
                    literal = expression.getValue().toString();
                }

                //geometry hook
				//if ( literal instanceof Geometry ) {
                if (Geometry.class.isAssignableFrom(target)) {
                    visitLiteralGeometry(expression);
                } //else if ( literal instanceof Number ) {
                else if (Number.class.isAssignableFrom(target)) {
                    out.write(literal.toString());
                } //else if ( literal instanceof String ) {
                else if (String.class.isAssignableFrom(target)) {
                    // sigle quotes must be escaped to have a valid CQL string
                    String escaped = literal.toString().replaceAll("'", "''");
                    out.write("'" + escaped + "'");
                }
            } else {
                //convert back to a string
                String encoding = (String) Converters.convert(literal, String.class, null);
                if (encoding == null) {
                    //could not convert back to string, use original l value
                    encoding = expression.getValue().toString();
                }


                // sigle quotes must be escaped to have a valid CQL string
                String escaped = encoding.replaceAll("'", "''");
                out.write("'" + escaped + "'");
            }

        } catch (IOException e) {
            throw new RuntimeException("IO problems writing literal", e);
        }
        return context;
    }

    /**
     * Subclasses must implement this method in order to encode geometry
     * filters according to the specific database implementation
     *
     * @param expression
     *
     * @throws IOException DOCUMENT ME!
     * @throws RuntimeException DOCUMENT ME!
     */
    protected void visitLiteralGeometry(Literal expression)
            throws IOException {
        throw new RuntimeException(
                "Subclasses must implement this method in order to handle geometries");
    }

    public Object visit(Add expression, Object extraData) {
        return visit((BinaryExpression) expression, extraData);
    }

    public Object visit(Divide expression, Object extraData) {
        return visit((BinaryExpression) expression, extraData);
    }

    public Object visit(Multiply expression, Object extraData) {
        return visit((BinaryExpression) expression, extraData);
    }

    public Object visit(Subtract expression, Object extraData) {
        return visit((BinaryExpression) expression, extraData);
    }

    /**
     * Writes the CQL for the Math Expression.
     *
     * @param expression the Math phrase to be written.
     *
     * @throws RuntimeException for io problems
     */
    protected Object visit(BinaryExpression expression, Object extraData) throws RuntimeException {
        LOGGER.finer("exporting Expression Math");

        String type = (String) expressions.get(expression.getClass());

        try {
            expression.getExpression1().accept(this, extraData);
            out.write(" " + type + " ");
            expression.getExpression2().accept(this, extraData);
        } catch (java.io.IOException ioe) {
            throw new RuntimeException("IO problems writing expression", ioe);
        }
        return extraData;
    }

    /**
     * Writes CQL for a function expression.  Not currently supported.
     *
     * @param expression a function expression
     *
     * @throws UnsupportedOperationException every time, this isn't supported.
     */
    public Object visit(Function expression, Object extraData)
            throws UnsupportedOperationException {
        String message = "Function expression support not yet added.";
        throw new UnsupportedOperationException(message);
    }

    public Object visit(NilExpression expression, Object extraData) {
        try {
            out.write(" ");
        } catch (java.io.IOException ioe) {
            throw new RuntimeException("IO problems writing expression", ioe);
        }

        return extraData;
    }

    /**
     * Sets the CQL name escape string.
     * 
     * <p>
     * The value of this string is prefixed and appended to table schema names,
     * table names and column names in an CQL statement to support mixed-case
     * and non-English names. Without this, the DBMS may assume a mixed-case
     * name in the query should be treated as upper-case and an CQLCODE of
     * -204 or 206 may result if the name is not found.
     * </p>
     * 
     * <p>
     * Typically this is the double-quote character, ", but may not be for all
     * databases.
     * </p>
     * 
     * <p>
     * For example, consider the following query:
     * </p>
     * 
     * <p>
     * SELECT Geom FROM Spear.ArchSites May be interpreted by the database as:
     * SELECT GEOM FROM SPEAR.ARCHSITES  If the column and table names were
     * actually created using mixed-case, the query needs to be specified as:
     * SELECT "Geom" from "Spear"."ArchSites"
     * </p>
     *
     * @param escape the character to be used to escape database names
     */
    public void setCQLNameEscape(String escape) {
        cqlNameEscape = escape;
    }

    /**
     * Surrounds a name with the CQL escape character.
     *
     * @param name
     *
     * @return DOCUMENT ME!
     */
    public String escapeName(String name) {
        return cqlNameEscape + name + cqlNameEscape;
    }
}
