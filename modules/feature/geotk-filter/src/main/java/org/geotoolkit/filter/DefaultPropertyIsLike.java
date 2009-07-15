/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotoolkit.util.logging.Logging;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.expression.Expression;

/**
 * Defines a like filter, which checks to see if an attribute matches a REGEXP.
 *
 * @author Rob Hranac, Vision for New York
 * @source $URL$
 * @version $Id$
 */
public class DefaultPropertyIsLike implements PropertyIsLike {

    private final Logger LOGGER = Logging.getLogger(DefaultPropertyIsLike.class);

    /** The attribute value, which must be an attribute expression. */
    private final Expression attribute;
    /** The (limited) REGEXP pattern. */
    private final String pattern;
    /** The single wildcard for the REGEXP pattern. */
    private final String wildcardSingle;
    /** The multiple wildcard for the REGEXP pattern. */
    private final String wildcardMulti;
    /** The escape sequence for the REGEXP pattern. */
    private final String escape;
    /** the pattern compiled into a java regex */
    private Pattern compPattern;
    /** The matcher to match patterns with. */
    private Matcher match;
    /** Used to indicate if case should be ignored or not */
    private final boolean matchingCase;

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
     * @param escape
     * @param multi
     * @param single
     * @param pattern
     *
     */
    public static String convertToSQL92(char escape, char multi, char single, String pattern)
            throws IllegalArgumentException {
        if ((escape == '\'') || (multi == '\'') || (single == '\'')) {
            throw new IllegalArgumentException("do not use single quote (') as special char!");
        }

        StringBuffer result = new StringBuffer(pattern.length() + 5);
        for (int i = 0; i < pattern.length(); i++) {
            char chr = pattern.charAt(i);
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

    /**
     * see convertToSQL92
     *
     * @throws IllegalArgumentException
     */
    public String getSQL92LikePattern() throws IllegalArgumentException {
        if (escape.length() != 1) {
            throw new IllegalArgumentException("Like Pattern --> escape char should be of length exactly 1");
        }
        if (wildcardSingle.length() != 1) {
            throw new IllegalArgumentException("Like Pattern --> wildcardSingle char should be of length exactly 1");
        }
        if (wildcardMulti.length() != 1) {
            throw new IllegalArgumentException("Like Pattern --> wildcardMulti char should be of length exactly 1");
        }
        return DefaultPropertyIsLike.convertToSQL92(
                escape.charAt(0),
                wildcardMulti.charAt(0),
                wildcardSingle.charAt(0),
                pattern);
    }

    private Matcher getMatcher() {
        if (match == null) {
            // protect the vars as this is moved code

            String pattern1 = new String(this.pattern);
            String wildcardMulti1 = new String(this.wildcardMulti);
            String wildcardSingle1 = new String(this.wildcardSingle);
            String escape1 = new String(this.escape);

//          The following things happen for both wildcards:
            //  (1) If a user-defined wildcard exists, replace with Java wildcard
            //  (2) If a user-defined escape exists, Java wildcard + user-escape
            //  Then, test for matching pattern and return result.
            char esc = escape1.charAt(0);
            LOGGER.finer("wildcard " + wildcardMulti1 + " single " + wildcardSingle1);
            LOGGER.finer("escape " + escape1 + " esc " + esc + " esc == \\ " + (esc == '\\'));

            String escapedWildcardMulti = fixSpecials(wildcardMulti1);
            String escapedWildcardSingle = fixSpecials(wildcardSingle1);

            // escape any special chars which are not our wildcards
            StringBuffer tmp = new StringBuffer("");

            boolean escapedMode = false;

            for (int i = 0; i < pattern1.length(); i++) {
                char chr = pattern1.charAt(i);
                LOGGER.finer("tmp = " + tmp + " looking at " + chr);

                if (pattern1.regionMatches(false, i, escape1, 0, escape1.length())) {
                    // skip the escape string
                    LOGGER.finer("escape ");
                    escapedMode = true;

                    i += escape1.length();
                    chr = pattern1.charAt(i);
                }

                if (pattern1.regionMatches(false, i, wildcardMulti1, 0,
                        wildcardMulti1.length())) { // replace with java wildcard
                    LOGGER.finer("multi wildcard");

                    if (escapedMode) {
                        LOGGER.finer("escaped ");
                        tmp.append(escapedWildcardMulti);
                    } else {
                        tmp.append(".*");
                    }

                    i += (wildcardMulti1.length() - 1);
                    escapedMode = false;

                    continue;
                }

                if (pattern1.regionMatches(false, i, wildcardSingle1, 0,
                        wildcardSingle1.length())) {
                    // replace with java single wild card
                    LOGGER.finer("single wildcard");

                    if (escapedMode) {
                        LOGGER.finer("escaped ");
                        tmp.append(escapedWildcardSingle);
                    } else {
                        // From the OpenGIS filter encoding spec, 
                        // "the single singleChar character matches exactly one character"
                        tmp.append(".{1}");
                    }

                    i += (wildcardSingle1.length() - 1);
                    escapedMode = false;

                    continue;
                }

                if (isSpecial(chr)) {
                    LOGGER.finer("special");
                    tmp.append(this.escape + chr);
                    escapedMode = false;

                    continue;
                }

                tmp.append(chr);
                escapedMode = false;
            }

            pattern1 = tmp.toString();
            LOGGER.finer("final pattern " + pattern1);
            compPattern = java.util.regex.Pattern.compile(pattern1);
            match = compPattern.matcher("");
        }
        return match;
    }

    public DefaultPropertyIsLike(Expression expr, String pattern, String wildcardMulti,
            String wildcardSingle, String escape, boolean matchCase) {
        this.attribute = expr;
        this.pattern = pattern;
        this.wildcardMulti = wildcardMulti;
        this.wildcardSingle = wildcardSingle;
        this.escape = escape;
        this.matchingCase = matchCase;
    }

    @Override
    public org.opengis.filter.expression.Expression getExpression() {
        return attribute;
    }

    @Override
    public String getLiteral() {
        return this.pattern;
    }

    @Override
    public boolean evaluate(Object feature) {
        //Checks to ensure that the attribute has been set
        if (attribute == null) {
            return false;
        }
        // Note that this converts the attribute to a string
        //  for comparison.  Unlike the math or geometry filters, which
        //  require specific types to function correctly, this filter
        //  using the mandatory string representation in Java
        // Of course, this does not guarantee a meaningful result, but it
        //  does guarantee a valid result.
        //LOGGER.finest("pattern: " + pattern);
        //LOGGER.finest("string: " + attribute.getValue(feature));
        //return attribute.getValue(feature).toString().matches(pattern);
        Object value = attribute.evaluate(feature);

        if (null == value) {
            return false;
        }

        Matcher matcher = getMatcher();
        matcher.reset(attribute.evaluate(feature).toString());

        return matcher.matches();
    }

    @Override
    public java.lang.String getEscape() {
        return escape;
    }

    @Override
    public String getWildCard() {
        return wildcardMulti;
    }

    @Override
    public String getSingleChar() {
        return wildcardSingle;
    }

    @Override
    public boolean isMatchingCase() {
        return matchingCase;
    }

    /**
     * convienience method to determine if a character is special to the regex
     * system.
     *
     * @param chr the character to test
     *
     * @return is the character a special character.
     */
    private boolean isSpecial(final char chr) {
        return ((chr == '.') || (chr == '?') || (chr == '*') || (chr == '^') || (chr == '$') || (chr == '+') || (chr == '[') || (chr == ']') || (chr == '(') || (chr == ')') || (chr == '|') || (chr == '\\') || (chr == '&'));
    }

    /**
     * convienience method to escape any character that is special to the regex
     * system.
     *
     * @param inString the string to fix
     *
     * @return the fixed string
     */
    private String fixSpecials(final String inString) {
        StringBuffer tmp = new StringBuffer("");

        for (int i = 0; i < inString.length(); i++) {
            char chr = inString.charAt(i);

            if (isSpecial(chr)) {
                tmp.append(this.escape + chr);
            } else {
                tmp.append(chr);
            }
        }

        return tmp.toString();
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.attribute != null ? this.attribute.hashCode() : 0);
        hash = 59 * hash + (this.pattern != null ? this.pattern.hashCode() : 0);
        hash = 59 * hash + (this.wildcardSingle != null ? this.wildcardSingle.hashCode() : 0);
        hash = 59 * hash + (this.wildcardMulti != null ? this.wildcardMulti.hashCode() : 0);
        hash = 59 * hash + (this.escape != null ? this.escape.hashCode() : 0);
        hash = 59 * hash + (this.compPattern != null ? this.compPattern.hashCode() : 0);
        hash = 59 * hash + (this.match != null ? this.match.hashCode() : 0);
        hash = 59 * hash + (this.matchingCase ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultPropertyIsLike other = (DefaultPropertyIsLike) obj;
        if (this.attribute != other.attribute && (this.attribute == null || !this.attribute.equals(other.attribute))) {
            return false;
        }
        if ((this.pattern == null) ? (other.pattern != null) : !this.pattern.equals(other.pattern)) {
            return false;
        }
        if ((this.wildcardSingle == null) ? (other.wildcardSingle != null) : !this.wildcardSingle.equals(other.wildcardSingle)) {
            return false;
        }
        if ((this.wildcardMulti == null) ? (other.wildcardMulti != null) : !this.wildcardMulti.equals(other.wildcardMulti)) {
            return false;
        }
        if ((this.escape == null) ? (other.escape != null) : !this.escape.equals(other.escape)) {
            return false;
        }
        if (this.compPattern != other.compPattern && (this.compPattern == null || !this.compPattern.equals(other.compPattern))) {
            return false;
        }
        if (this.match != other.match && (this.match == null || !this.match.equals(other.match))) {
            return false;
        }
        if (this.matchingCase != other.matchingCase) {
            return false;
        }
        return true;
    }

}
