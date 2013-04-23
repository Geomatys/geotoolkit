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

import java.util.*;
import java.io.Writer;
import java.io.IOException;
import java.io.Serializable;
import java.text.Format;
import java.text.ParseException;

import org.opengis.referencing.IdentifiedObject;

import org.geotoolkit.io.X364;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.converter.Classes;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.Strings;

import static java.lang.Character.isJavaIdentifierPart;


/**
 * The map of definitions managed by {@link WKTFormat}. Keys are short identifiers and values
 * are long string to substitute to the identifiers when they are found in a WKT to parse.
 * The values given to this map must be parseable.
 * <p>
 * See the "<cite>String expansion</cite>" section in the
 * {@code WKTFormat} javadoc for more details.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
final class Definitions extends AbstractMap<String,String> implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2376345936250144764L;

    /**
     * The WKT parser, usually a {@link WKTFormat} object.
     */
    private final Format parser;

    /**
     * The set of objects defined by calls to {@link #put}.
     */
    private final Map<String,Parsed> definitions;

    /**
     * The map entries, to be created only when first needed.
     */
    private transient Set<Entry<String,String>> entries;

    /**
     * The character used for quote.
     */
    char quote = '"';

    /**
     * A linked list of informations about the replacements performed by {@link #substitute}.
     * Those informations are used by parsing methods in order to adjust
     * {@linkplain ParseException#getErrorOffset error offset} in case of failure.
     */
    private transient Replacement replacements;

    /**
     * Creates a new map that delegates the work to the given parser.
     *
     * @param parser The WKT parser, usually a {@link WKTFormat} object.
     */
    public Definitions(final Format parser) {
        this.parser = parser;
        definitions = new TreeMap<>();
    }

    /**
     * Removes all definitions.
     */
    @Override
    public void clear() {
        definitions.clear();
    }

    /**
     * Returns {@code true} if this map is empty.
     */
    @Override
    public boolean isEmpty() {
        return definitions.isEmpty();
    }

    /**
     * Returns the size of this map.
     */
    @Override
    public int size() {
        return definitions.size();
    }

    /**
     * Returns {@code true} if this map contains the given key.
     */
    @Override
    public boolean containsKey(final Object key) {
        return definitions.containsKey(key);
    }

    /**
     * Returns {@code true} if this map contains the given value. The value can be either a
     * string or a parsed object. The comparison is lenient in that if no exact match is found
     * while comparing the strings, the comparison will be performed against parsed objects.
     */
    @Override
    public boolean containsValue(Object value) {
        if (value != null) {
            if (value instanceof String) {
                if (super.containsValue(value)) {
                    return true;
                }
                try {
                    value = parser.parseObject((String) value);
                } catch (ParseException e) {
                    return false; // Appropriate for this method contract.
                }
            }
            for (final Parsed def : definitions.values()) {
                if (value.equals(def.asObject)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the parsed object for the given identifier, or {@code null} if none.
     * Note that this method is indirectly accessible from public API using
     * {@link WKTFormat#parse(String,Class)} with an identifier in argument.
     */
    final Object getParsed(final String key) {
        final Parsed def = definitions.get(key);
        return (def != null) ? def.asObject : null;
    }

    /**
     * Returns the predefined WKT for the given identifier, or {@code null} if none.
     */
    @Override
    public String get(final Object key) {
        final Parsed def = definitions.get(key);
        return (def != null) ? def.asString : null;
    }

    /**
     * Adds a predefined Well Know Text (WKT). The {@code value} argument given to this method
     * can contains itself other definitions specified in some previous calls to this method.
     *
     * @param  identifier The name for the definition to be added.
     * @param  value The Well Know Text (WKT) represented by the name.
     * @return The previous definition, or {@code null} if none.
     * @throws IllegalArgumentException if the name is invalid or if the value can't be parsed.
     */
    @Override
    public String put(final String identifier, String value) throws IllegalArgumentException {
        /*
         * Checks argument validity.
         */
        ArgumentChecks.ensureNonNull("identifier", identifier);
        if (!Strings.isJavaIdentifier(identifier)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_IDENTIFIER_1, identifier));
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.NO_WKT_DEFINITION));
        }
        /*
         * The value should be a complete WKT string. But if it is not, if it is an
         * identifier, then we will take that as an alias for an existing entry.
         */
        final Parsed previous;
        if (Strings.isJavaIdentifier(value)) {
            final Parsed parsed = definitions.get(identifier);
            if (parsed != null) {
                previous = definitions.put(identifier, parsed);
            } else {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_ARGUMENT_2, identifier, value));
            }
        } else {
            /*
             * Not an identifier: parses the WKT string.
             * This is the usual case.
             */
            value = substitute(value);
            final Object object;
            try {
                object = parser.parseObject(value);
            } catch (ParseException e) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_ARGUMENT_1, identifier), e);
            }
            previous = definitions.put(identifier, new Parsed(value, object));
        }
        return (previous != null) ? previous.asString : null;
    }

    /**
     * Removes the predefined WKT for the given identifier.
     */
    @Override
    public String remove(final Object key) {
        final Parsed def = definitions.remove(key);
        return (def != null) ? def.asString : null;
    }

    /**
     * Returns the set of identifiers.
     */
    @Override
    public Set<String> keySet() {
        return definitions.keySet();
    }

    /**
     * Returns the (identifier,wkt) entries.
     */
    @Override
    public Set<Entry<String,String>> entrySet() {
        if (entries == null) {
            entries = new Entries(definitions);
        }
        return entries;
    }

    /**
     * For every definition identifier found in the given string, substitutes the identifier by
     * its WKT value. The replacement will not be performed if the key was found between quotes.
     *
     * @param  text The string to process.
     * @return The string with all identifiers replaced by their values.
     */
    final String substitute(final String text) {
        final char quote = this.quote;
        String quots = null;
        Replacement last;
        replacements = last = new Replacement();
        StringBuilder buffer = null;
        for (final Map.Entry<String,Parsed> entry : definitions.entrySet()) {
            final String    name = entry.getKey();
            final Parsed def = entry.getValue();
            int index = (buffer != null) ? buffer.indexOf(name) : text.indexOf(name);
            while (index >= 0) {
                /*
                 * An occurrence of the text to substitute was found. First, make sure
                 * that the occurrence found is a full word  (e.g. if the occurrence to
                 * search is "WGS84", do not accept "TOWGS84").
                 */
                final int upper = index + name.length();
                final CharSequence cs = (buffer != null) ? buffer : text;
                if ((index == 0           || !isJavaIdentifierPart(cs.charAt(index-1))) &&
                    (upper == cs.length() || !isJavaIdentifierPart(cs.charAt(upper))))
                {
                    /*
                     * Count the number of quotes before the text to substitute. If this
                     * number is odd, then the text is between quotes and should not be
                     * substituted.
                     */
                    int count = 0;
                    for (int scan=index; --scan>=0;) {
                        scan = (buffer != null) ? buffer.lastIndexOf(quots, scan) :
                                                  text  .lastIndexOf(quote, scan);
                        if (scan < 0) {
                            break;
                        }
                        count++;
                    }
                    if ((count & 1) == 0) {
                        /*
                         * An even number of quotes was found before the text to substitute.
                         * Performs the substitution and keep trace of this replacement in a
                         * chained list of 'Replacement' objects.
                         */
                        if (buffer == null) {
                            buffer = new StringBuilder(text);
                            quots = String.valueOf(quote);
                            assert buffer.indexOf(name, index) == index;
                        }
                        final String value = def.asString;
                        buffer.replace(index, upper, value);
                        final int change = value.length() - name.length();
                        last = last.next = new Replacement(index, index+value.length(), change);
                        index = buffer.indexOf(name, index + change);
                        // Note: it is okay to skip the text we just replaced, since the
                        //       'definitions' map do not contains nested definitions.
                        continue;
                    }
                }
                /*
                 * The substitution was not performed because the text found was not a word,
                 * or was between quotes. Search the next occurrence.
                 */
                index += name.length();
                index = (buffer != null) ? buffer.indexOf(name, index)
                                         : text  .indexOf(name, index);
            }
        }
        return (buffer != null) ? buffer.toString() : text;
    }

    /**
     * Adjusts the {@linkplain ParseException#getErrorIndex error index} in order to
     * point to the character in the original text (before substitutions) where the
     * parsing failed. A new exception must be created because the error offset is
     * not modifiable, but it will be filled with the same stack trace so this change
     * should be invisible to the user.
     *
     * @param  exception The exception to adjust.
     * @param  offset An additional offset to add to the error index.
     * @return The adjusted exception.
     */
    final ParseException adjustErrorOffset(final ParseException exception, final int offset) {
        int shift = 0;
        int errorOffset = exception.getErrorOffset();
        for (Replacement r=replacements; r!=null; r=r.next) {
            if (errorOffset < r.lower) {
                break;
            }
            if (errorOffset < r.upper) {
                errorOffset = r.lower;
                break;
            }
            shift += r.shift;
        }
        errorOffset -= shift;
        errorOffset += offset;
        if (errorOffset == exception.getErrorOffset()) {
            return exception; // No adjustment needed.
        }
        ParseException adjusted = new ParseException(exception.getLocalizedMessage(), errorOffset);
        adjusted.setStackTrace(exception.getStackTrace());
        adjusted.initCause(exception.getCause());
        return adjusted;
    }

    /**
     * Prints to the specified stream a table of all definitions. The table content
     * is inferred from the values given to the {@link #put} method.
     *
     * @param  out writer The output stream where to write the table.
     * @param  colors {@code true} if X3.64 colors are enabled.
     * @throws IOException if an error occurred while writing to the output stream.
     */
    final void print(final Writer out, final boolean colors) throws IOException {
        final Locale locale = null;
        final Vocabulary resources = Vocabulary.getResources(locale);
        final TableWriter table = new TableWriter(out, TableWriter.SINGLE_VERTICAL_LINE);
        table.setMultiLinesCells(true);
        table.writeHorizontalSeparator();
        final int[] keys = { // In reverse ordeR.
            Vocabulary.Keys.DESCRIPTION,
            Vocabulary.Keys.CLASS,
            Vocabulary.Keys.TYPE,
            Vocabulary.Keys.NAME
        };
        for (int i=keys.length; --i>=0;) {
            if (colors) table.write(X364.BOLD.sequence());
            table.write(resources.getString(keys[i]));
            if (colors) table.write(X364.NORMAL.sequence());
            if (i != 0) table.nextColumn();
            else table.nextLine();
        }
        table.writeHorizontalSeparator();
        for (final Map.Entry<String,Parsed> entry : definitions.entrySet()) {
            final Object object = entry.getValue().asObject;
            table.write(entry.getKey());
            table.nextColumn();
            Class<?> classe = Classes.getClass(object);
            String type = WKTFormat.getNameOf(classe);
            if (type != null) {
                classe = WKTFormat.getClassOf(type);
            } else {
                type = resources.getString(Vocabulary.Keys.UNKNOWN);
            }
            table.write(type);
            table.nextColumn();
            table.write(Classes.getShortName(classe));
            table.nextColumn();
            if (object instanceof IdentifiedObject) {
                table.write(((IdentifiedObject) object).getName().getCode());
            }
            table.nextLine();
        }
        table.writeHorizontalSeparator();
        table.flush();
    }



    /////////////////////////////////////////////////////////////////////////////
    ////////                                                             ////////
    ////////                        INNER CLASSES                        ////////
    ////////                                                             ////////
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Values of definition map. This entry contains a definition as a well know text (WKT),
     * and the parsed value for this WKT (usually a CRS or a math transform object).
     */
    private static final class Parsed implements Serializable {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -6622917637459216208L;

        /**
         * The definition as a string. This string should not contains anymore
         * shortcut to substitute by an other WKT (i.e. compound definitions
         * must be resolved before to construct a {@code Definition} object).
         */
        final String asString;

        /**
         * The definition as an object (usually a {@code CoordinateReferenceSystem}
         * or a {@code MathTransform} object).
         */
        final Object asObject;

        /**
         * Constructs a new definition.
         */
        Parsed(final String asString, final Object asObject) {
            this.asString = asString;
            this.asObject = asObject;
        }
    }

    /**
     * Contains informations about the index changes induced by a replacement in a string.
     * All index refer to the string <strong>after</strong> the replacement. The substring
     * at index between {@link #lower} inclusive and {@link #upper} exclusive is the replacement
     * string. The {@link #shift} is the difference between the replacement substring length and
     * the replaced substring length.
     */
    private static final class Replacement {
        /** The lower index in the target string, inclusive. */ public final int  lower;
        /** The upper index in the target string, exclusive. */ public final int  upper;
        /** The shift from source string to target string.   */ public final int  shift;
        /** The next element in the linked list.             */ public Replacement next;

        /**
         * Constructs a new index shift initialized to zero.
         */
        Replacement() {
            lower = upper = shift = 0;
        }

        /**
         * Constructs a new index shift initialized with the given values.
         */
        Replacement(final int lower, final int upper, final int shift) {
            this.lower = lower;
            this.upper = upper;
            this.shift = shift;
        }

        /**
         * Returns a string representation for debugging purpose.
         */
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder();
            for (Replacement r=this; r!=null; r=r.next) {
                if (r != this) {
                    buffer.append(", ");
                }
                buffer.append('[').append(r.lower).append("..").append(r.upper).append("] \u2192 ").append(r.shift);
            }
            return buffer.toString();
        }
    }

    /**
     * A view over the (<var>key</var>,<var>WKT</var>) entries.
     */
    private static final class Entries extends AbstractSet<Entry<String,String>> {
        /** Same reference than the one stored in {@link Definitions}. */
        private final Map<String,Parsed> definitions;

        /** Creates a view for the given definitions. */
        Entries(final Map<String,Parsed> definitions) {
            this.definitions = definitions;
        }

        /** Returns the number of entries. */
        @Override
        public int size() {
            return definitions.size();
        }

        /** Returns an iterator over all entries. */
        @Override
        public Iterator<Entry<String,String>> iterator() {
            return new Iter(definitions.entrySet().iterator());
        }
    }

    /**
     * An iterator over the (<var>key</var>,<var>WKT</var>) entries.
     */
    private static final class Iter implements Iterator<Entry<String,String>> {
        /** The iterator provided by the {@link Definitions} map. */
        private final Iterator<Entry<String,Parsed>> iterator;

        /** Creates an iterator wrapping the given definitions map iterator. */
        Iter(final Iterator<Entry<String,Parsed>> iterator) {
            this.iterator = iterator;
        }

        /** Returns {@code true} if there is more entries. */
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        /** Returns the next entry. */
        @Override
        public Entry<String,String> next() {
            final Entry<String,Parsed> next = iterator.next();
            return new SimpleEntry<>(next.getKey(), next.getValue().asString);
        }

        /** Deletes the last returned entry. */
        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
