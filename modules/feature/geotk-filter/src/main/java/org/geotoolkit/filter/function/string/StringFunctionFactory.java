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
package org.geotoolkit.filter.function.string;

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.filter.function.AbstractFunctionFactory;


/**
 * Factory registering the string functions.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class StringFunctionFactory extends AbstractFunctionFactory{

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
    public static final String TRUNCATE_FIRST = "strTruncateFirst";
    public static final String TRUNCATE_LAST = "strTruncateLast";

    private static final Map<String,Class> FUNCTIONS = new HashMap<>();

    static{
        FUNCTIONS.put(CONCAT,               ConcatFunction.class);
        FUNCTIONS.put(ENDS_WITH,            EndsWithFunction.class);
        FUNCTIONS.put(EQUALS_IGNORE_CASE,   EqualsIgnoreCaseFunction.class);
        FUNCTIONS.put(INDEX_OF,             IndexOfFunction.class);
        FUNCTIONS.put(LAST_INDEX_OF,        LastIndexOfFunction.class);
        FUNCTIONS.put(LENGTH,               LengthFunction.class);
        FUNCTIONS.put(MATCHES,              MatchesFunction.class);
        FUNCTIONS.put(REPLACE,              ReplaceFunction.class);
        FUNCTIONS.put(STARTS_WITH,          StartsWithFunction.class);
        FUNCTIONS.put(SUBSTRING,            SubstringFunction.class);
        FUNCTIONS.put(SUBSTRING_START,      SubstringStartFunction.class);
        FUNCTIONS.put(TO_LOWER_CASE,        ToLowerCaseFunction.class);
        FUNCTIONS.put(TO_UPPER_CASE,        ToUpperCaseFunction.class);
        FUNCTIONS.put(TRIM,                 TrimFunction.class);
        FUNCTIONS.put(TRUNCATE_FIRST,       TruncateFirstFunction.class);
        FUNCTIONS.put(TRUNCATE_LAST,        TruncateLastFunction.class);

    }

    public StringFunctionFactory() {
        super("string", FUNCTIONS);
    }

}
