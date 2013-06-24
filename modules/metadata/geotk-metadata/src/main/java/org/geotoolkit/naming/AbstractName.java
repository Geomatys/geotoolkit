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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.naming;

import java.util.List;
import java.util.Locale;
import java.util.Iterator;
import java.util.Objects;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.Immutable;

import org.opengis.util.NameSpace;
import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;

import org.geotoolkit.util.SimpleInternationalString;


/**
 * A sequence of identifiers rooted within the context of a {@linkplain NameSpace namespace}.
 * Names are <em>immutable</em>. They may be {@linkplain #toFullyQualifiedName() fully qualified}
 * like {@code "org.opengis.util.Record"}, or they may be relative to a {@linkplain #scope() scope}
 * like {@code "util.Record"} in the {@code "org.opengis"} scope. See {@link GenericName} javadoc
 * for an illustration.
 * <p>
 * Subclasses need only to implement the following methods:
 * <ul>
 *   <li>{@link #scope()}</li>
 *   <li>{@link #getParsedNames()}</li>
 * </ul>
 *
 * {@note This class has a natural ordering that is inconsistent with equals.
 *        The natural ordering may be case-insensitive and ignore the character
 *        separator between name elements.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.util.iso} package.
 */
@Deprecated
@XmlType(name = "GenericName")
public abstract class AbstractName implements GenericName, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 3362498790089164525L;

    /**
     * A view of this name as a fully-qualified one.
     * Will be created only when first needed.
     */
    transient GenericName fullyQualified;

    /**
     * The string representation of this name, to be returned by {@link #toString()} or
     * {@link #toInternationalString()}. This field will initially references a {@link String}
     * object when first needed, and may be replaced by a {@link InternationalString} object
     * later if such object is asked for.
     */
    transient CharSequence asString;

    /**
     * The cached hash code, or {@code 0} if not yet computed.
     */
    transient int hash;

    /**
     * Creates a new instance of generic name.
     */
    protected AbstractName() {
    }

    /**
     * Returns the scope (name space) in which this name is local. For example if a {@linkplain
     * #toFullyQualifiedName() fully qualified name} is {@code "org.opengis.util.Record"} and if
     * this instance is the {@code "util.Record"} part, then its scope is
     * {@linkplain DefaultNameSpace#name() named} {@code "org.opengis"}.
     * <p>
     * Continuing with the above example, the full {@code "org.opengis.util.Record"} name has
     * no scope. If this method is invoked on such name, then the Geotk implementation returns
     * a global scope instance (i.e. an instance for which {@link NameSpace#isGlobal()} returns
     * {@code true}) which is unique and named {@code "global"}.
     */
    @Override
    public abstract NameSpace scope();

    /**
     * Indicates the number of levels specified by this name. The default implementation returns
     * the {@linkplain List#size() size} of the list returned by the {@link #getParsedNames()} method.
     */
    @Override
    public int depth() {
        return getParsedNames().size();
    }

    /**
     * Returns the size of the backing array. This is used only has a hint for optimizations
     * in attempts to share internal arrays. The {@link DefaultScopedName} class is the only
     * one to override this method. For other classes, the {@link #depth()} can be assumed.
     */
    int arraySize() {
        return depth();
    }

    /**
     * Returns the sequence of {@linkplain DefaultLocalName local names} making this generic name.
     * The length of this sequence is the {@linkplain #depth() depth}. It does not include the
     * {@linkplain #scope() scope}.
     */
    @Override
    public abstract List<? extends LocalName> getParsedNames();

    /**
     * Returns the first element in the sequence of {@linkplain #getParsedNames() parsed names}.
     * For any {@link LocalName}, this is always {@code this}.
     * <p>
     * <b>Example</b>:
     * If {@code this} name is {@code "org.opengis.util.Record"} (no matter its
     * {@linkplain #scope() scope}), then this method returns {@code "org"}.
     */
    @Override
    public LocalName head() {
        return getParsedNames().get(0);
    }

    /**
     * Returns the last element in the sequence of {@linkplain #getParsedNames() parsed names}.
     * For any {@link LocalName}, this is always {@code this}.
     * <p>
     * <b>Example</b>:
     * If {@code this} name is {@code "org.opengis.util.Record"} (no matter its
     * {@linkplain #scope() scope}), then this method returns {@code "Record"}.
     */
    @Override
    public LocalName tip() {
        final List<? extends LocalName> names = getParsedNames();
        return names.get(names.size() - 1);
    }

    /**
     * Returns a view of this name as a fully-qualified name. The {@linkplain #scope() scope}
     * of a fully qualified name is {@linkplain DefaultNameSpace#isGlobal() global}. If the
     * scope of this name is already global, then this method returns {@code this}.
     */
    @Override
    public synchronized GenericName toFullyQualifiedName() {
        if (fullyQualified == null) {
            final NameSpace scope = scope();
            if (scope.isGlobal()) {
                fullyQualified = this;
            } else {
                final GenericName prefix = scope.name();
                assert prefix.scope().isGlobal() : prefix;
                fullyQualified = new DefaultScopedName(prefix, this);
            }
        }
        return fullyQualified;
    }

    /**
     * Returns this name expanded with the specified scope. One may represent this operation
     * as a concatenation of the specified {@code scope} with {@code this}. For example if
     * {@code this} name is {@code "util.Record"} and the given {@code scope} argument is
     * {@code "org.opengis"}, then {@code this.push(scope)} shall returns
     * {@code "org.opengis.util.Record"}.
     */
    @Override
    public ScopedName push(final GenericName scope) {
        return new DefaultScopedName(scope, this);
    }

    /**
     * Returns the separator to write before the given name. If the scope of the given name is
     * a {@link DefaultNameSpace} instance, then this method returns its head separator. We really
     * want {@link DefaultNameSpace#headSeparator}, not {@link DefaultNameSpace#separator}. See
     * {@link DefaultNameSpace#child(CharSequence)} for details.
     *
     * @param The name after which to write a separator.
     * @return The separator to write after the given name.
     */
    static String separator(final GenericName name) {
        if (name != null) {
            final NameSpace scope = name.scope();
            if (scope instanceof DefaultNameSpace) {
                return ((DefaultNameSpace) scope).headSeparator;
            }
        }
        return DefaultNameSpace.DEFAULT_SEPARATOR_STRING;
    }

    /**
     * Returns a string representation of this generic name. This string representation is
     * local-independent. It contains all elements listed by {@link #getParsedNames()} separated
     * by a namespace-dependent character (usually {@code :} or {@code /}). This rule implies
     * that the result may or may not be fully qualified. Special cases:
     * <p>
     * <ul>
     *   <li><code>{@linkplain #toFullyQualifiedName()}.toString()</code> is guaranteed to
     *       contains the {@linkplain #scope() scope} (if any).</li>
     *   <li><code>{@linkplain #tip()}.toString()</code> is guaranteed to not contains
     *       any scope.</li>
     * </ul>
     */
    @Override
    public synchronized String toString() {
        if (asString == null) {
            boolean insertSeparator = false;
            final StringBuilder buffer = new StringBuilder();
            for (final LocalName name : getParsedNames()) {
                if (insertSeparator) {
                    buffer.append(separator(name));
                }
                insertSeparator = true;
                buffer.append(name);
            }
            asString = buffer.toString();
        }
        // Note: there is not need to invoke InternationalString.toString(null) for the
        // unlocalized version, because our International inner class is implemented in
        // such a way that InternationalString.toString() returns AbstractName.toString().
        return asString.toString();
    }

    /**
     * Returns a local-dependent string representation of this generic name. This string is similar
     * to the one returned by {@link #toString()} except that each element has been localized in the
     * {@linkplain InternationalString#toString(Locale) specified locale}. If no international
     * string is available, then this method returns an implementation mapping to {@link #toString()}
     * for all locales.
     */
    @Override
    public synchronized InternationalString toInternationalString() {
        if (!(asString instanceof InternationalString)) {
            asString = new International(toString(), getParsedNames());
        }
        return (InternationalString) asString;
    }

    /**
     * An international string built from a snapshot of {@link GenericName}.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.1
     */
    @Immutable
    private static final class International extends SimpleInternationalString {
        /**
         * Serial number for inter-operability with different versions.
         */
        private static final long serialVersionUID = -6422809765366372062L;

        /**
         * The sequence of {@linkplain DefaultLocalName local names} making this generic name.
         * This is the value returned by {@link AbstractName#getParsedNames()}.
         */
        private final List<? extends LocalName> parsedNames;

        /**
         * Constructs a new international string from the specified {@link AbstractName} fields.
         *
         * @param asString The string representation of the enclosing abstract name.
         * @param parsedNames The value returned by {@link AbstractName#getParsedNames()}.
         */
        public International(final String asString, final List<? extends LocalName> parsedNames) {
            super(asString);
            this.parsedNames = parsedNames;
        }

        /**
         * Returns a string representation for the specified locale.
         */
        @Override
        public String toString(final Locale locale) {
            boolean insertSeparator = false;
            final StringBuilder buffer = new StringBuilder();
            for (final LocalName name : parsedNames) {
                if (insertSeparator) {
                    buffer.append(separator(name));
                }
                insertSeparator = true;
                buffer.append(name.toInternationalString().toString(locale));
            }
            return buffer.toString();
        }

        /**
         * Compares this international string with the specified object for equality.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (super.equals(object)) {
                final International that = (International) object;
                return Objects.equals(this.parsedNames, that.parsedNames);
            }
            return false;
        }
    }

    /**
     * Compares this name with the specified name for order. Returns a negative integer,
     * zero, or a positive integer as this name lexicographically precedes, is equal to,
     * or follows the specified name. The comparison is performed in the following way:
     * <p>
     * <ul>
     *   <li>For each element of the {@linkplain #getParsedNames() list of parsed names} taken
     *       in iteration order, compare the {@link LocalName}. If a name lexicographically
     *       precedes or follows the corresponding element of the specified name, returns
     *       a negative or a positive integer respectively.</li>
     *   <li>If all elements in both names are lexicographically equal, then if this name has less
     *       or more elements than the specified name, returns a negative or a positive integer
     *       respectively.</li>
     *   <li>Otherwise, returns 0.</li>
     * </ul>
     *
     * @param name The other name to compare with this name.
     * @return -1 if this name precedes the given one, +1 if it follows, 0 if equals.
     */
    @Override
    public int compareTo(final GenericName name) {
        final Iterator<? extends LocalName> thisNames = this.getParsedNames().iterator();
        final Iterator<? extends LocalName> thatNames = name.getParsedNames().iterator();
        while (thisNames.hasNext()) {
            if (!thatNames.hasNext()) {
                return +1;
            }
            final LocalName thisNext = thisNames.next();
            final LocalName thatNext = thatNames.next();
            if (thisNext == this && thatNext == name) {
                // Never-ending loop: usually an implementation error
                throw new IllegalStateException();
            }
            final int compare = thisNext.compareTo(thatNext);
            if (compare != 0) {
                return compare;
            }
        }
        return thatNames.hasNext() ? -1 : 0;
    }

    /**
     * Compares this generic name with the specified object for equality. The default
     * implementation returns {@code true} if the {@linkplain #scope() scopes} and the
     * lists of {@linkplain #getParsedNames() parsed names} are equal.
     *
     * @param object The object to compare with this name for equality.
     * @return {@code true} if the given object is equal to this name.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object != null && object.getClass() == getClass()) {
            final AbstractName that = (AbstractName) object;
            return Objects.equals(this.scope(), that.scope()) &&
                   Objects.equals(this.getParsedNames(), that.getParsedNames());
        }
        return false;
    }

    /**
     * Returns a hash code value for this generic name.
     */
    @Override
    public int hashCode() {
        if (hash == 0) {
            int code = (int) serialVersionUID;
            final NameSpace scope = scope();
            if (scope != null) {
                code ^= scope.hashCode();
            }
            final List<? extends LocalName> names = getParsedNames();
            if (names != null) {
                code += names.hashCode();
            }
            hash = code;
        }
        return hash;
    }
}
