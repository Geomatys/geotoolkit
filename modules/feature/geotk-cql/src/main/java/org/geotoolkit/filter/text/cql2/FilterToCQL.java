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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.geotoolkit.filter.DefaultPropertyIsLike;
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
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
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
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
import com.vividsolutions.jts.io.WKTWriter;

import org.geotoolkit.util.logging.Logging;


/**
 * This is a utility class used by CQL.encode( Filter ) method to do the
 * hard work.
 * <p>
 * Please note that this encoder is a bit more strict than you may be used to
 * (the Common Query Language for example demands Equals.getExpression1() is a
 * PropertyName). If you used FilterFactory to produce your filter you should be
 * okay (as it only provides methods to make a valid Filter); if not please
 * expect ClassCastExceptions.
 * <p>
 * This visitor will return a StringBuffer; you can also provide a StringBuffer
 * as the data parameter in order to cut down on the number of objects
 * created during encoding.<pre><code>
 * FilterToCQL toCQL = new FilterToCQL();
 * StringBuffer output = filter.accepts( toCQL, new StringBuffer() );
 * String cql = output.toString();
 * </code></pre>
 * @author Johann Sorel
 * @module pending
 */
class FilterToCQL implements FilterVisitor, ExpressionVisitor {
    /** Standard java logger */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.filter");
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Process the possibly user supplied extraData parameter into a StringBuffer.
     * @param extraData
     * @return
     */
    protected StringBuffer asStringBuffer( final Object extraData){
        if( extraData instanceof StringBuffer){
            return (StringBuffer) extraData;
        }
        return new StringBuffer();
    }

    /**
     * Exclude everything; using an old SQL trick of 1=0.
     */
    @Override
    public Object visit(final ExcludeFilter filter, final Object extraData) {
        StringBuffer output = asStringBuffer(extraData);
        output.append("1 = 1");
        return output;
    }

    /**
     * Include everything; using an old SQL trick of 1=1.
     */
    @Override
    public Object visit(final IncludeFilter filter, final Object extraData) {
        StringBuffer output = asStringBuffer(extraData);
        output.append("1 = 1");
        return output;
    }

    @Override
    public Object visit(final And filter, final Object extraData) {
        LOGGER.finer("exporting And filter");

        StringBuffer output = asStringBuffer(extraData);
        List<Filter> children = filter.getChildren();
        if( children != null ){
            output.append("(");
            for( Iterator<Filter> i=children.iterator(); i.hasNext(); ){
                Filter child = i.next();
                child.accept(this, output);
                if (i.hasNext()) {
                    output.append(" AND ");
                }
            }
            output.append(")");
        }
        return output;
    }

    /**
     * Encoding an Id filter is not supported by CQL.
     * <p>
     * This is because in the Catalog specification retreiving an object
     * by an id is a distinct operation seperate from a filter based query.
     */
    @Override
    public Object visit(final Id filter, final Object extraData) {
        throw new IllegalStateException("Cannot encode an Id as legal CQL");
    }

    @Override
    public Object visit(final Not filter, final Object extraData) {
        LOGGER.finer("exporting Not filter");

        StringBuffer output = asStringBuffer(extraData);
        output.append( "NOT (");
        filter.getFilter().accept(this, output );
        output.append( ")");
        return output;
    }

    @Override
    public Object visit(final Or filter, final Object extraData) {
        LOGGER.finer("exporting Or filter");

        StringBuffer output = asStringBuffer(extraData);
        List<Filter> children = filter.getChildren();
        if( children != null ){
            output.append("(");
            for( Iterator<Filter> i=children.iterator(); i.hasNext(); ){
                Filter child = i.next();
                child.accept(this, output);
                if (i.hasNext()) {
                    output.append(" OR ");
                }
            }
            output.append(")");
        }
        return output;
    }

    @Override
    public Object visit(final PropertyIsBetween filter, final Object extraData) {
        LOGGER.finer("exporting PropertyIsBetween");

        StringBuffer output = asStringBuffer(extraData);
        PropertyName propertyName = (PropertyName) filter.getExpression();
        propertyName.accept(this, output);
        output.append(" BETWEEN ");
        filter.getLowerBoundary().accept(this, output);
        output.append(" AND ");
        filter.getUpperBoundary().accept(this, output);

        return output;
    }

    @Override
    public Object visit(final PropertyIsEqualTo filter, final Object extraData) {
        LOGGER.finer("exporting PropertyIsEqualTo");
        StringBuffer output = asStringBuffer(extraData);

        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(" = ");
        filter.getExpression2().accept(this, output);

        return output;
    }

    @Override
    public Object visit(final PropertyIsNotEqualTo filter, final Object extraData) {
        LOGGER.finer("exporting PropertyIsNotEqualTo");
        StringBuffer output = asStringBuffer(extraData);

        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(" != ");
        filter.getExpression2().accept(this, output);

        return output;
    }

    @Override
    public Object visit(final PropertyIsGreaterThan filter, final Object extraData) {
        StringBuffer output = asStringBuffer(extraData);
        if( isDate( filter )){
            return after( filter, output);
        }
        LOGGER.finer("exporting PropertyIsGreaterThan");

        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(" > ");
        filter.getExpression2().accept(this, output);

        return output;
    }

    /**
     * Check if we are working with dates; may ned to check propertyName against
     * the current SimpleFature type.
     * @param compare
     * @return true if we are working on Date
     */
    protected boolean isDate( final BinaryComparisonOperator compare ){
        if( compare.getExpression2() instanceof Literal){
            Literal literal = (Literal) compare.getExpression2();
            return literal.getValue() instanceof Date;
        }
        return false;
    }

    /**
     * This is where it would be noice to know if we are working on a Date.
     * <p>
     * I am tempted to do the SimpleFeature look aisde in order to guess
     * what kind of type I am working with.
     */
    public StringBuffer after( final PropertyIsGreaterThan filter, final StringBuffer output ){
        LOGGER.finer("exporting AFTER");
        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(" AFTER ");

        filter.getExpression2().accept(this, output);
        return output;

    }

    @Override
    public Object visit(final PropertyIsGreaterThanOrEqualTo filter, final Object extraData) {
        LOGGER.finer("exporting PropertyIsGreaterThanOrEqualTo");
        StringBuffer output = asStringBuffer(extraData);

        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(" >= ");
        filter.getExpression2().accept(this, output);

        return output;
    }

    @Override
    public Object visit(final PropertyIsLessThan filter, final Object extraData) {
        LOGGER.finer("exporting PropertyIsLessThan");
        StringBuffer output = asStringBuffer(extraData);

        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(" < ");
        filter.getExpression2().accept(this, output);

        return output;
    }

    @Override
    public Object visit(final PropertyIsLessThanOrEqualTo filter, final Object extraData) {
        LOGGER.finer("exporting PropertyIsLessThanOrEqualTo");
        StringBuffer output = asStringBuffer(extraData);

        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(" <= ");
        filter.getExpression2().accept(this, output);

        return output;
    }

    @Override
    public Object visit(final PropertyIsLike filter, final Object extraData) {
        StringBuffer output = asStringBuffer(extraData);

        char esc = filter.getEscape().charAt(0);
        char multi = filter.getWildCard().charAt(0);
        char single = filter.getSingleChar().charAt(0);
        boolean matchCase = filter.isMatchingCase();
        String pattern = DefaultPropertyIsLike.convertToSQL92(esc, multi, single,
            filter.getLiteral());

        if (!matchCase) {
            output.append(" UPPER(");
        }

        PropertyName propertyName = (PropertyName) filter.getExpression();
        propertyName.accept(this, output);

        if (!matchCase){
            output.append(") LIKE '");
        } else {
            output.append(" LIKE '");
        }

        output.append(pattern);
        output.append("' ");

        return output;
    }

    @Override
    public Object visit(final PropertyIsNull filter, final Object extraData) {
        StringBuffer output = asStringBuffer(extraData);

        PropertyName propertyName = (PropertyName) filter.getExpression();
        propertyName.accept(this, output);
        output.append(" IS NULL");
        return output;
    }

    @Override
    public Object visit(final BBOX filter, final Object extraData) {
        StringBuffer output = asStringBuffer(extraData);

        output.append( "BBOX(");
        output.append( filter.getPropertyName() );
        output.append( ", ");
        output.append( filter.getMinX() );
        output.append( ",");
        output.append( filter.getMaxX() );
        output.append( ",");
        output.append( filter.getMinY() );
        output.append( ",");
        output.append( filter.getMaxY() );
        output.append( ")");

        return output;
    }

    @Override
    public Object visit(final Beyond filter, final Object extraData) {
        LOGGER.finer("exporting Beyond");
        StringBuffer output = asStringBuffer(extraData);

        output.append("BEYOND(");
        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(", ");
        filter.getExpression2().accept(this, output);
        output.append(")");

        return output;
    }

    @Override
    public Object visit(final Contains filter, final Object extraData) {
        LOGGER.finer("exporting Contains");
        StringBuffer output = asStringBuffer(extraData);

        output.append("CONTAINS(");
        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(", ");
        filter.getExpression2().accept(this, output);
        output.append(")");

        return output;
    }

    @Override
    public Object visit(final Crosses filter, final Object extraData) {
        LOGGER.finer("exporting Crosses");
        StringBuffer output = asStringBuffer(extraData);

        output.append("CROSS(");
        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(", ");
        filter.getExpression2().accept(this, output);
        output.append(")");

        return output;
    }

    @Override
    public Object visit(final Disjoint filter, final Object extraData) {
        LOGGER.finer("exporting Crosses");
        StringBuffer output = asStringBuffer(extraData);

        output.append("DISJOINT(");
        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(", ");
        filter.getExpression2().accept(this, output);
        output.append(")");

        return output;
    }

    @Override
    public Object visit(final DWithin filter, final Object extraData) {
        LOGGER.finer("exporting Crosses");
        StringBuffer output = asStringBuffer(extraData);

        output.append("DWITHIN(");
        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(", ");
        filter.getExpression2().accept(this, output);
        output.append(", ");
        output.append( filter.getDistance() );
        output.append(", ");
        output.append( filter.getDistanceUnits() );
        output.append(")");

        return output;
    }

    @Override
    public Object visit(final Equals filter, final Object extraData) {
        LOGGER.finer("exporting Equals");
        StringBuffer output = asStringBuffer(extraData);

        output.append("EQUALS(");
        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(", ");
        filter.getExpression2().accept(this, output);
        output.append(")");

        return output;
    }

    @Override
    public Object visit(final Intersects filter, final Object extraData) {
        LOGGER.finer("exporting Intersects");
        StringBuffer output = asStringBuffer(extraData);

        output.append("INTERSECT(");
        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(", ");
        filter.getExpression2().accept(this, output);
        output.append(")");

        return output;
    }

    @Override
    public Object visit(final Overlaps filter, final Object extraData) {
        LOGGER.finer("exporting Overlaps");
        StringBuffer output = asStringBuffer(extraData);

        output.append("OVERLAP(");
        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(", ");
        filter.getExpression2().accept(this, output);
        output.append(")");

        return output;
    }

    @Override
    public Object visit(final Touches filter, final Object extraData) {
        LOGGER.finer("exporting Touches");
        StringBuffer output = asStringBuffer(extraData);

        output.append("TOUCH(");
        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(", ");
        filter.getExpression2().accept(this, output);
        output.append(")");

        return output;
    }

    @Override
    public Object visit(final Within filter, final Object extraData) {
        LOGGER.finer("exporting Within");
        StringBuffer output = asStringBuffer(extraData);

        output.append("WITHIN(");
        PropertyName propertyName = (PropertyName) filter.getExpression1();
        propertyName.accept(this, output);
        output.append(", ");
        filter.getExpression2().accept(this, output);
        output.append(")");

        return output;
    }

    /**
     * A filter has not been provided.
     * <p>
     * In general this is a bad situtation which we ask people to
     * represent with Filter.INCLUDES or Filter.EXCLUDES depending
     * on what behaviour they want to see happen - in this case
     * literally <code>null</code> was provided.
     * <p>
     */
    @Override
    public Object visitNullFilter(final Object extraData) {
        throw new NullPointerException("Cannot encode null as a Filter");
    }

    /**
     * Not sure how to record an unset expression in CQL; going
     * to use an emptry string for now.
     */
    @Override
    public Object visit(final NilExpression expression, final Object extraData) {
        LOGGER.finer("exporting Expression Nil");

        StringBuffer output = asStringBuffer(extraData);
        output.append( "\"\"" );

        return output;
    }

    @Override
    public Object visit(final Add expression, final Object extraData) {
        LOGGER.finer("exporting Expression Add");

        StringBuffer output = asStringBuffer(extraData);
        expression.getExpression1().accept(this, output );
        output.append( " + " );
        expression.getExpression2().accept(this, output );

        return output;
    }

    @Override
    public Object visit(final Divide expression, final Object extraData) {
        LOGGER.finer("exporting Expression Divide");

        StringBuffer output = asStringBuffer(extraData);
        expression.getExpression1().accept(this, output );
        output.append( " - " );
        expression.getExpression2().accept(this, output );

        return output;
    }

    @Override
    public Object visit(final Function function, final Object extraData) {
        LOGGER.finer("exporting Function");

        StringBuffer output = asStringBuffer(extraData);
        output.append( function.getName() );
        output.append( "(" );
        List<Expression> parameters = function.getParameters();

        if( parameters != null ){
            for( Iterator<Expression> i=parameters.iterator(); i.hasNext(); ){
                Expression argument = i.next();
                argument.accept(this, output );
                if( i.hasNext() ){
                    output.append(",");
                }
            }
        }
        output.append( ")" );
        return output;
    }

    @Override
    public Object visit(final Literal expression, final Object extraData) {
        LOGGER.finer("exporting LiteralExpression");
        StringBuffer output = asStringBuffer(extraData);

        Object literal = expression.getValue();
        if (literal instanceof Geometry) {
            Geometry geometry = (Geometry) literal;
            WKTWriter writer = new WKTWriter();
            String wkt = writer.write( geometry );
            output.append( wkt );
        }
        else if( literal instanceof Number ){
                // don't convert to string
                output.append( literal );
        }
        else if (literal instanceof Date ){
            return date( (Date) literal, output );
        }
        else {
            String escaped = literal.toString().replaceAll("'", "''");
            output.append("'" + escaped + "'");
        }
        return output;
    }

    /**
     * Uses the format <code>yyyy-MM-dd'T'HH:mm:ss'Z'</code> for
     * output the provided date.
     * @param date
     * @param output
     * @return output
     */
    public StringBuffer date( final Date date, final StringBuffer output ){

        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

        String text = dateFormatter.format( date );
        output.append( text );
        return output;
    }

    @Override
    public Object visit(final Multiply expression, final Object extraData) {
        LOGGER.finer("exporting Expression Multiply");

        StringBuffer output = asStringBuffer(extraData);
        expression.getExpression1().accept(this, output );
        output.append( " * " );
        expression.getExpression2().accept(this, output );

        return output;
    }

    @Override
    public Object visit(final PropertyName expression, final Object extraData) {
        LOGGER.finer("exporting PropertyName");

        StringBuffer output = asStringBuffer(extraData);
        output.append( expression.getPropertyName() );

        return output;
    }

    @Override
    public Object visit(final Subtract expression, final Object extraData) {
        LOGGER.finer("exporting Expression Subtract");

        StringBuffer output = asStringBuffer(extraData);
        expression.getExpression1().accept(this, output );
        output.append( " - " );
        expression.getExpression2().accept(this, output );

        return output;
    }

}
