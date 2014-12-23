/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2013, Geomatys
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

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.filter.function.AbstractFunctionFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;


/**
 * Factory registering the various functions.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class OtherFunctionFactory extends AbstractFunctionFactory{

    public static final String CONVERT = "convert";
    public static final String DATE_FORMAT = "dateFormat";
    public static final String DOUBLE_TO_BOOL = "double2bool";
    public static final String DATE_PARSE = "dateParse";
    public static final String EQUAL_TO = "equalTo";
    public static final String EQUALS_EXACT = "equalsExact";
    public static final String EQUALS_EXACT_TOLERANCE = "equalsExactTolerance";
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

    private static final Map<String,Class> FUNCTIONS = new HashMap<>();

    static{
        FUNCTIONS.put(CONVERT,                  ConvertFunction.class);
        FUNCTIONS.put(DATE_FORMAT,              DateFormatFunction.class);
        FUNCTIONS.put(DATE_PARSE,               DateParseFunction.class);
        FUNCTIONS.put(EQUAL_TO,                 EqualToFunction.class);
        FUNCTIONS.put(EQUALS_EXACT,             EqualsExactFunction.class);
        FUNCTIONS.put(EQUALS_EXACT_TOLERANCE,   EqualsExactToleranceFunction.class);
        FUNCTIONS.put(GREATER_EQUAL_THAN,       GreaterEqualThanFunction.class);
        FUNCTIONS.put(GREATER_THAN,             GreaterThanFunction.class);
        FUNCTIONS.put(IF_THEN_ELSE,             IfThenElseFunction.class);
        FUNCTIONS.put(IN,                       InFunction.class);
        FUNCTIONS.put(INT_TO_BBOOL,             IntToBbool.class);
        FUNCTIONS.put(INT_TO_DDOUBLE,           IntToDdoubleFunction.class);
        FUNCTIONS.put(IS_LIKE,                  IsLikeFunction.class);
        FUNCTIONS.put(IS_NULL,                  IsNullFunction.class);
        FUNCTIONS.put(LESS_EQUAL_THAN,          LessEqualThanFunction.class);
        FUNCTIONS.put(LESS_THAN,                LessThanFunction.class);
        FUNCTIONS.put(NOT,                      NotFunction.class);
        FUNCTIONS.put(NOT_EQUAL_TO,             NotEqualToFunction.class);
        FUNCTIONS.put(NUMBER_FORMAT,            NumberFormatFunction.class);
        FUNCTIONS.put(PARSE_BOOLEAN,            ParseBooleanFunction.class);
        FUNCTIONS.put(PARSE_DOUBLE,             ParseDoubleFunction.class);
        FUNCTIONS.put(PARSE_INT,                ParseIntFunction.class);
        FUNCTIONS.put(PARSE_LONG,               ParseLongFunction.class);
        FUNCTIONS.put(PROPERTY_EXISTS,          PropertyExistsFunction.class);
        FUNCTIONS.put(ROUND_DOUBLE,             RoundDoubleFunction.class);

    }

    public OtherFunctionFactory() {
        super("other", FUNCTIONS);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function createFunction(final String name, final Literal fallback, final Expression... parameters) throws IllegalArgumentException {
        if(name.equals(IN)) return new InFunction(parameters);
        return super.createFunction(name,fallback,parameters);
    }

}
