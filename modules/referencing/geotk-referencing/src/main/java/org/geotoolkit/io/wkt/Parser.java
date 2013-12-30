/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Map;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.ParseException;
import org.apache.sis.io.wkt.Accessor;
import org.apache.sis.io.wkt.Symbols;
import org.apache.sis.io.wkt.Formatter;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Parses <cite>Well Known Text</cite> (WKT). Parsers are the converse of {@link Formatter}.
 * Like the later, a parser is constructed with a given set of {@linkplain Symbols symbols}.
 * Parsers also need a set of factories to be used for instantiating the parsed objects.
 * <p>
 * In current version, parsers are usually not intended to be subclassed outside this package.
 * A few exceptions exist, for example {@link ReferencingParser#alterProperties(Map)} is one
 * of the few hooks provided for overriding.
 * <p>
 * Parsers are not synchronized. It is recommended to create separate parser instances for each
 * thread. If multiple threads access a parser concurrently, it must be synchronized externally.
 *
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD)
 * @version 4.00
 *
 * @since 2.0
 * @level advanced
 * @module
 */
public abstract class Parser {
    /**
     * Set to {@code true} if parsing of number in scientific notation is allowed.
     * The way to achieve that is currently a hack, because {@link NumberFormat} has no
     * API for managing that as of J2SE 1.5.
     *
     * @todo See if a future version of J2SE allows us to get ride of this ugly hack.
     */
    static final boolean SCIENTIFIC_NOTATION = true;

    static {
        Accessor.init();
    }

    /**
     * The symbols to use for parsing WKT.
     */
    private Symbols symbols;

    /**
     * The object to use for parsing numbers.
     */
    private NumberFormat numberFormat;

    /**
     * Constructs a parser using the specified set of symbols.
     *
     * @param symbols The set of symbols to use.
     */
    Parser(final Symbols symbols) {
        setSymbols(symbols);
    }

    /**
     * Sets the symbols to be used by this parser.
     *
     * @param symbols The new set of symbol to use.
     */
    final void setSymbols(final Symbols symbols) {
        ensureNonNull("symbols", symbols);
        this.symbols = symbols;
        this.numberFormat = Accessor.createNumberFormat(symbols);
        if (SCIENTIFIC_NOTATION && numberFormat instanceof DecimalFormat) {
            final DecimalFormat numberFormat = (DecimalFormat) this.numberFormat;
            String pattern = numberFormat.toPattern();
            if (pattern.indexOf("E0") < 0) {
                final StringBuilder buffer = new StringBuilder(pattern);
                final int split = pattern.indexOf(';');
                if (split >= 0) {
                    buffer.insert(split, "E0");
                }
                buffer.append("E0");
                numberFormat.applyPattern(buffer.toString());
            }
        }
    }

    /**
     * Returns the symbols currently used by this parser.
     *
     * @return The current set of symbols.
     */
    final Symbols getSymbols() {
        return symbols;
    }

    /**
     * Parses a <cite>Well Know Text</cite> (WKT).
     *
     * @param  text The text to be parsed.
     * @return The object.
     * @throws ParseException if the string can't be parsed.
     */
    public final Object parseObject(final String text) throws ParseException {
        final Element element = getTree(text, new ParsePosition(0));
        final Object object = parse(element);
        element.close();
        return object;
    }

    /**
     * Parses a <cite>Well Know Text</cite> (WKT).
     *
     * @param  text The text to be parsed.
     * @param  position The position to start parsing from.
     * @return The object.
     */
    public final Object parseObject(final String text, final ParsePosition position) {
        final int origin = position.getIndex();
        try {
            return parse(getTree(text, position));
        } catch (ParseException exception) {
            position.setIndex(origin);
            if (position.getErrorIndex() < origin) {
                position.setErrorIndex(exception.getErrorOffset());
            }
            return null;
        }
    }

    /**
     * Parses the number at the given position.
     */
    final Number parseNumber(String text, final ParsePosition position) {
        if (SCIENTIFIC_NOTATION) {
            /*
             * HACK: DecimalFormat.parse(...) do not understand lower case 'e' for scientific
             *       notation. It understand upper case 'E' only. Performs the replacement...
             */
            final int base = position.getIndex();
            Number number = numberFormat.parse(text, position);
            if (number != null) {
                int i = position.getIndex();
                if (i<text.length() && text.charAt(i) == 'e') {
                    final StringBuilder buffer = new StringBuilder(text);
                    buffer.setCharAt(i, 'E');
                    text = buffer.toString();
                    position.setIndex(base);
                    number = numberFormat.parse(text, position);
                }
            }
            return number;
        } else {
            return numberFormat.parse(text, position);
        }
    }

    /**
     * Parses the next element in the specified <cite>Well Know Text</cite> (WKT) tree.
     *
     * @param  element The element to be parsed.
     * @return The object.
     * @throws ParseException if the element can't be parsed.
     */
    abstract Object parse(final Element element) throws ParseException;

    /**
     * Returns a tree of {@link Element} for the specified text.
     *
     * @param  text       The text to parse.
     * @param  position   In input, the position where to start parsing from.
     *                    In output, the first character after the separator.
     * @return The tree of elements to parse.
     * @throws ParseException If an parsing error occurred while creating the tree.
     */
    final Element getTree(final String text, final ParsePosition position) throws ParseException {
        return new Element(new Element(this, text, position));
    }

    /**
     * Returns the keyword for the given element.
     *
     * @param  key The element for which to get the keyword.
     * @return The keyword of the given elements, in upper-case letters and without leading
     *         or heading spaces.
     */
    final String keyword(final Element key) {
        return key.keyword.trim().toUpperCase(symbols.getLocale());
    }

    /**
     * Creates a new formatter using the same symbols and number format than this parser.
     */
    final Formatter formatter() {
        return Accessor.newFormatter(symbols, numberFormat);
    }
}
