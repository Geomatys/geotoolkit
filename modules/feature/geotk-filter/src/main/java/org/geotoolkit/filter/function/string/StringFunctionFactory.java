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
package org.geotoolkit.filter.function.string;

import org.geotoolkit.filter.function.FunctionFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;


/**
 * Factory registering the string functions.
 * 
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
public class StringFunctionFactory implements FunctionFactory{

    public static final String CONCAT = "strConcat";
    public static final String ENDS_WITH = "strEndsWith";
    public static final String EQUALS_IGNORE_CASE = "strEqualsIgnoreCase";
    public static final String INDEX_OF = "strIndexOf";
    public static final String LAST_INDEX_OF = "strLastIndexOf";
    public static final String LENGTH = "strLength";
    public static final String MATCHES = "strMatches";
    public static final String REPLACE = "strReplace";
    public static final String STARTS_WITH = "strStartsWith";
    public static final String SUBSTRING = "strSubstring";
    public static final String SUBSTRING_START = "strSubstringStart";
    public static final String TO_LOWER_CASE = "strToLowerCase";
    public static final String TO_UPPER_CASE = "strToUpperCase";
    public static final String TRIM = "strTrim";

    private static final String[] NAMES;

    static {
        NAMES = new String[] {
                  CONCAT, ENDS_WITH, EQUALS_IGNORE_CASE, INDEX_OF, LAST_INDEX_OF, LENGTH,
                  MATCHES, REPLACE, STARTS_WITH, SUBSTRING, SUBSTRING_START, TO_LOWER_CASE,
                  TO_UPPER_CASE, TRIM
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

        if(name.equals(CONCAT)) return new ConcatFunction(parameters[0], parameters[1]);
        if(name.equals(ENDS_WITH)) return new EndsWithFunction(parameters[0], parameters[1]);
        if(name.equals(EQUALS_IGNORE_CASE)) return new EqualsIgnoreCaseFunction(parameters[0], parameters[1]);
        if(name.equals(INDEX_OF)) return new IndexOfFunction(parameters[0], parameters[1]);
        if(name.equals(LAST_INDEX_OF)) return new LastIndexOfFunction(parameters[0], parameters[1]);
        if(name.equals(LENGTH)) return new LengthFunction(parameters[0]);
        if(name.equals(MATCHES)) return new MatchesFunction(parameters[0], parameters[1]);
        if(name.equals(REPLACE)) return new ReplaceFunction(parameters[0], parameters[1], parameters[2], parameters[3]);
        if(name.equals(STARTS_WITH)) return new StartsWithFunction(parameters[0], parameters[1]);
        if(name.equals(SUBSTRING)) return new SubstringFunction(parameters[0], parameters[1], parameters[2]);
        if(name.equals(SUBSTRING_START)) return new SubstringStartFunction(parameters[0], parameters[1]);
        if(name.equals(TO_LOWER_CASE)) return new ToLowerCaseFunction(parameters[0]);
        if(name.equals(TO_UPPER_CASE)) return new ToUpperCaseFunction(parameters[0]);
        if(name.equals(TRIM)) return new TrimFunction(parameters[0]);

        throw new IllegalArgumentException("Unknowed function name : "+ name);
    }

}
