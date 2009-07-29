/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.filter.function.other;

import org.geotoolkit.filter.function.FunctionFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;


/**
 * Factory registering the various functions.
 * 
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
public class OtherFunctionFactory implements FunctionFactory{

    public static final String CONVERT = "convert";
    public static final String DATE_FORMAT = "dateFormat";
    public static final String DOUBLE_TO_BOOL = "double2bool";
    public static final String DATE_PARSE = "dateParse";
    public static final String EQUAL_TO = "equalTo";
    public static final String EQUALS_EXACT = "equalsExact";
    public static final String EQUALS_EXACT_TOLERANCE = "equalsExactTolerance";
    public static final String EXPRESSION_VALUE_LENGHT  = "length";
    public static final String GREATER_EQUAL_THAN = "greaterEqualThan";
    public static final String GREATER_THAN = "greaterThan";
    public static final String IF_THEN_ELSE = "if_then_else";
    public static final String IN = "in";
    public static final String INT_TO_BBOOL = "int2bbool";
    public static final String INT_TO_DDOUBLE = "int2ddouble";
    public static final String IS_LIKE = "isLike";
    public static final String IS_NULL = "isNull";
    public static final String LESS_EQUAL_THAN = "lessEqualThan";
    public static final String LESS_THAN = "lessThan";
    public static final String NOT = "not";
    public static final String NOT_EQUAL_TO = "notEqualTo";
    public static final String NUMBER_FORMAT = "numberFormat";
    public static final String PARSE_BOOLEAN = "parseBoolean";
    public static final String PARSE_DOUBLE = "parseDouble";
    public static final String PARSE_INT = "parseInt";
    public static final String PARSE_LONG = "parseLong";
    public static final String PROPERTY_EXISTS  = "PropertyExists";
    public static final String ROUND_DOUBLE = "roundDouble";


    private static final String[] NAMES;

    static {
        NAMES = new String[] {
                    CONVERT, DATE_FORMAT, DATE_PARSE, DOUBLE_TO_BOOL, EQUALS_EXACT, EQUALS_EXACT_TOLERANCE,
                    EQUAL_TO, EXPRESSION_VALUE_LENGHT, GREATER_EQUAL_THAN, GREATER_THAN, IF_THEN_ELSE, IN,
                    INT_TO_BBOOL, INT_TO_DDOUBLE, IS_LIKE, IS_NULL,
                    LESS_EQUAL_THAN, LESS_THAN, NOT, NOT_EQUAL_TO, NUMBER_FORMAT, PARSE_BOOLEAN, PARSE_DOUBLE,
                    PARSE_INT, PARSE_LONG, PROPERTY_EXISTS, ROUND_DOUBLE
        };
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getNames() {
        return NAMES;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function createFunction(String name, Literal fallback, Expression... parameters) throws IllegalArgumentException {

        if(name.equals(CONVERT)) return new ConvertFunction(parameters[0], parameters[1]);
        if(name.equals(DATE_FORMAT)) return new DateFormatFunction(parameters[0], parameters[1]);
        if(name.equals(DATE_PARSE)) return new DateParseFunction(parameters[0], parameters[1]);
        if(name.equals(DOUBLE_TO_BOOL)) return new DoubleToBoolFunction(parameters[0]);
        if(name.equals(EQUALS_EXACT)) return new EqualsExactFunction(parameters[0], parameters[1]);
        if(name.equals(EQUALS_EXACT_TOLERANCE)) return new EqualsExactFunction(parameters[0], parameters[1]);
        if(name.equals(EQUAL_TO)) return new EqualToFunction(parameters[0], parameters[1]);
        if(name.equals(EXPRESSION_VALUE_LENGHT))   return new LengthFunction((PropertyName) parameters[0]);
        if(name.equals(GREATER_EQUAL_THAN)) return new GreaterEqualThanFunction(parameters[0], parameters[1]);
        if(name.equals(GREATER_THAN)) return new GreaterThanFunction(parameters[0], parameters[1]);
        if(name.equals(IF_THEN_ELSE)) return new IfThenElseFunction(parameters[0], parameters[1], parameters[2]);
        if(name.equals(IN)) return new InFunction(parameters);
        if(name.equals(INT_TO_BBOOL)) return new IntToBbool(parameters[0]);
        if(name.equals(INT_TO_DDOUBLE)) return new IntToDdoubleFunction(parameters[0]);
        if(name.equals(IS_LIKE)) return new IsLikeFunction(parameters[0], parameters[1]);
        if(name.equals(IS_NULL)) return new IsNullFunction(parameters[0]);
        if(name.equals(LESS_EQUAL_THAN)) return new LessEqualThanFunction(parameters[0], parameters[1]);
        if(name.equals(LESS_THAN)) return new LessThanFunction(parameters[0], parameters[1]);
        if(name.equals(NOT)) return new NotFunction(parameters[0]);
        if(name.equals(NOT_EQUAL_TO)) return new NotEqualToFunction(parameters[0], parameters[1]);
        if(name.equals(NUMBER_FORMAT)) return new NumberFormatFunction(parameters[0], parameters[1]);
        if(name.equals(PARSE_BOOLEAN)) return new ParseBooleanFunction(parameters[0]);
        if(name.equals(PARSE_DOUBLE)) return new ParseDoubleFunction(parameters[0]);
        if(name.equals(PARSE_INT)) return new ParseIntFunction(parameters[0]);
        if(name.equals(PARSE_LONG)) return new ParseLongFunction(parameters[0]);
        if(name.equals(PROPERTY_EXISTS))           return new PropertyExistsFunction(parameters[0]);
        if(name.equals(ROUND_DOUBLE)) return new RoundDoubleFunction(parameters[0]);

        throw new IllegalArgumentException("Unknowed function name : "+ name);
    }

}
