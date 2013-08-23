/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.io.wkt;

import java.util.Locale;
import java.io.Serializable;
import java.text.NumberFormat;
import net.jcip.annotations.Immutable;

import org.geotoolkit.internal.InternalUtilities;


/**
 * The set of symbols to use for <cite>Well Known Text</cite> (WKT) parsing and formatting.
 * The default setting in new {@code Symbols} objects is to format WKT elements with square
 * brackets, as in {@code DATUM["WGS84"]}. However the WKT specification permits also curly
 * brackets as in {@code DATUM("WGS84")}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see WKTFormat#getSymbols()
 * @see WKTFormat#setSymbols(Symbols)
 *
 * @since 2.1
 * @level advanced
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
 @Deprecated
@Immutable
public class Symbols implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -1730166945430878916L;

    /**
     * A set of symbols with parameters between square brackets, like {@code DATUM["WGS84"]}.
     * This is the most frequent WKT format.
     */
    public static final Symbols SQUARE_BRACKETS = new Symbols(Locale.US);

    /**
     * A set of symbols with parameters between parentheses, like {@code DATUM("WGS84")}.
     * This is a less frequent but legal WKT format.
     */
    public static final Symbols CURLY_BRACKETS = new Symbols(SQUARE_BRACKETS);
    static {
        CURLY_BRACKETS.open  = '(';
        CURLY_BRACKETS.close = ')';
    }

    /**
     * The default set of symbols.
     */
    public static final Symbols DEFAULT = SQUARE_BRACKETS;

    /**
     * The locale for querying localizable information.
     */
    final Locale locale;

    /* ----------------------------------------------------------
     * NOTE: Consider all fields below as final.
     *       It is not only in order to make construction easier.
     *       If the informations provided by those fields became
     *       needed outside of this package, then we need to make
     *       them private and declare accessors instead.
     * ---------------------------------------------------------- */

    /**
     * The character used for opening brace.
     * Usually {@code '['}, but {@code '('} is legal as well.
     */
    char open = '[';

    /**
     * The character used for closing brace.
     * Usually {@code ']'}, but {@code ')'} is legal as well.
     */
    char close = ']';

    /**
     * The character used for opening an array or enumeration.
     */
    final char openArray = '{';

    /**
     * The character used for closing an array or enumeration.
     */
    final char closeArray = '}';

    /**
     * The character used for quote.
     * Usually {@code '"'}.
     */
    final char quote = '"';

    /**
     * The character used as a separator. Usually {@code ','}, but would need
     * to be changed if a non-English locale is used for formatting numbers.
     */
    char separator = ',';

    /**
     * The character used for space.
     * Usually {@code ' '}, but could be a no-break space too if unicode is allowed.
     */
    final char space = ' ';

    /**
     * List of caracters acceptable as opening bracket. The closing bracket must
     * be the character in the {@code closingBrackets} array at the same index
     * than the opening bracket.
     */
    final char[] openingBrackets = {'[', '('};

    /**
     * List of caracters acceptable as closing bracket.
     */
    final char[] closingBrackets = {']', ')'};

    /**
     * The object to use for parsing and formatting numbers.
     *
     * {@note <code>NumberFormat</code> objects are usually not thread safe. Consequently,
     *        each instance of <code>Parser</code> or <code>Formatter</code> must use a clone
     *        of this object, not this object directly (unless they synchronize on it).}
     */
    final NumberFormat numberFormat;

    /**
     * Creates a copy of the given instance. This constructor is not public because
     * it shares the same {@link NumberFormat} without cloning it.
     */
    private Symbols(final Symbols copy) {
        locale = copy.locale;
        numberFormat = copy.numberFormat;
    }

    /**
     * Creates a new set of symbols for the specified locale.
     *
     * @param locale The locale for number formatting.
     */
    public Symbols(final Locale locale) {
        this.locale = locale;
        numberFormat = NumberFormat.getNumberInstance(locale);
        numberFormat.setGroupingUsed(false);
        numberFormat.setMinimumFractionDigits(1);
        numberFormat.setMaximumFractionDigits(20);
        /*
         * The "maximum fraction digits" seems hight for the precision of floating
         * points (even in double precision), but this is because the above format
         * do not uses the scientific notation. For example 1E-5 is always formatted
         * as 0.00001, and 1E-340 would actually need a maximum fraction digits of
         * 340. For most parameters, such low values should not occurs and may be
         * rounding error for the 0 value. For semi-major and semi-minor axis, we
         * often want to avoid exponential notation as well.
         */
        separator = InternalUtilities.getSeparator(numberFormat);
    }

    /**
     * Returns the list of caracters acceptable as opening bracket. The closing bracket must
     * be the character in the {@code closingBrackets} array at the same index than the opening
     * bracket.
     *
     * @return The characters acceptable as opening bracket.
     *
     * @since 3.00
     */
    public final char[] getOpeningBrackets() {
        return openingBrackets.clone();
    }

    /**
     * Returns the list of caracters acceptable as closing bracket. The opening bracket must
     * be the character in the {@code openingBrackets} array at the same index than the closing
     * bracket.
     *
     * @return The characters acceptable as closing bracket.
     *
     * @since 3.00
     */
    public final char[] getClosingBrackets() {
        return closingBrackets.clone();
    }

    /**
     * Returns the character used for quoting texts. This is usually {@code '"'}.
     *
     * @return The character used for quoting texts
     *
     * @since 3.00
     */
    public final char getQuote() {
        return quote;
    }

    /**
     * Returns {@code true} if the specified WKT contains at least one {@code AXIS[...]} element.
     * This method tries to make a quick check taking in account a minimal set of WKT syntax rules.
     *
     * @param  wkt The WKT to inspect.
     * @return {@code true} if the given WKT contains at least one {@code AXIS} element.
     *
     * @since 2.4
     */
    public boolean containsAxis(final CharSequence wkt) {
        return indexOf(wkt, "AXIS", 0) >= 0;
    }

    /**
     * Returns the index after the specified element in the specified WKT, or -1 if not found.
     * The element must be followed (ignoring spaces) by an opening bracket. If found, this
     * method returns the index of the opening bracket after the element.
     *
     * @param  wkt The WKT to parse.
     * @param  element The element to search. Must contains only uppercase letters.
     * @param  index The index to start the search from.
     */
    private int indexOf(final CharSequence wkt, final String element, int index) {
        assert element.equals(element.trim().toUpperCase(locale)) : element;
        assert element.indexOf(quote) < 0 : element;
        boolean isQuoting = false;
        final int elementLength = element.length();
        final int length = wkt.length();
        if (index < length) {
            char c = wkt.charAt(index);
search:     while (true) {
                // Do not parse any content between quotes.
                if (c == quote) {
                    isQuoting = !isQuoting;
                }
                if (isQuoting || !Character.isJavaIdentifierStart(c)) {
                    if (++index == length) {
                        break search;
                    }
                    c = wkt.charAt(index);
                    continue;
                }
                // Check if we have a match.
                for (int j=0; j<elementLength; j++) {
                    c = Character.toUpperCase(c);
                    if (c != element.charAt(j)) {
                        // No match. Skip all remaining letters and resume the search.
                        while (Character.isJavaIdentifierPart(c)) {
                            if (++index == length) {
                                break search;
                            }
                            c = wkt.charAt(index);
                        }
                        continue search;
                    }
                    if (++index == length) {
                        break search;
                    }
                    c = wkt.charAt(index);
                }
                // Check if the next character (ignoring space) is an opening brace.
                while (Character.isWhitespace(c)) {
                    if (++index == length) {
                        break search;
                    }
                    c = wkt.charAt(index);
                }
                for (int i=0; i<openingBrackets.length; i++) {
                    if (c == openingBrackets[i]) {
                        return index;
                    }
                }
            }
        }
        return -1;
    }
}
