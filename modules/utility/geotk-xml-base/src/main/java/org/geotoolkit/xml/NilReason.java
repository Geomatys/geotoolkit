/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.xml;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import net.jcip.annotations.Immutable;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.LenientComparable;
import org.geotoolkit.util.collection.WeakHashSet;


/**
 * Explanation for a missing XML element. The nil reason can be parsed and formatted as
 * a string using the {@link #valueOf(String)} and {@link #toString()} methods respectively. The
 * string can be either a {@link URI} or an enumeration value described below. More specifically,
 * {@code NilReason} can be:
 * <p>
 * <ul>
 *   <li>One of the predefined {@link #INAPPLICABLE}, {@link #MISSING}, {@link #TEMPLATE},
 *       {@link #UNKNOWN} or {@link #WITHHELD} enumeration values.</li>
 *   <li>The {@link #OTHER} enumeration value, or a new enumeration value formatted as
 *       {@code "other:"} concatenated with a brief textual explanation.</li>
 *   <li>A URI which should refer to a resource which describes the reason for the exception.</li>
 * </ul>
 * <p>
 * {@code NilReason} is used in a number of XML elements where it is necessary to permit
 * one of the above values as an alternative to the primary element.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see NilObject
 *
 * @since 3.18
 * @module
 */
@Immutable
public final class NilReason implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -1302619137838086028L;

    /**
     * There is no value.
     * <p>
     * The string representation is {@code "inapplicable"}.
     */
    public static final NilReason INAPPLICABLE = new NilReason("inapplicable");

    /**
     * The correct value is not readily available to the sender of this data.
     * Furthermore, a correct value may not exist.
     * <p>
     * The string representation is {@code "missing"}.
     */
    public static final NilReason MISSING = new NilReason("missing");

    /**
     * The value will be available later.
     * <p>
     * The string representation is {@code "template"}.
     */
    public static final NilReason TEMPLATE = new NilReason("template");

    /**
     * The correct value is not known to, and not computable by, the sender of this data.
     * However, a correct value probably exists.
     * <p>
     * The string representation is {@code "unknown"}.
     */
    public static final NilReason UNKNOWN = new NilReason("unknown");

    /**
     * The value is not divulged.
     * <p>
     * The string representation is {@code "withheld"}.
     */
    public static final NilReason WITHHELD = new NilReason("withheld");

    /**
     * Other brief explanation. This constant does not provide any explanation. In order to test
     * if an enumeration is {@code "other"}, users should invoke the {@link #getExplanation()}
     * method instead than comparing against this enumeration.
     * <p>
     * The string representation is {@code "other:text"}, where text is a string of two or more
     * characters with no included spaces.
     */
    public static final NilReason OTHER = new NilReason("other");

    /**
     * List of predefined constants.
     */
    private static final NilReason[] PREDEFINED = {
        INAPPLICABLE, MISSING, TEMPLATE, UNKNOWN, WITHHELD, OTHER
    };

    /**
     * The pool of other and URI created up to date.
     */
    private static final WeakHashSet<NilReason> POOL = WeakHashSet.newInstance(NilReason.class);

    /**
     * Either the XML enum as a {@code String} (including the explanation if the prefix
     * is "{@code other}", or an {@link URI}.
     */
    private final Object reason;

    /**
     * The invocation handler for empty objects, created when first needed.
     * The same handler can be shared for all objects.
     */
    private transient InvocationHandler handler;

    /**
     * Creates a new enum for the given XML enum or the given URI.
     */
    private NilReason(final Object reason) {
        this.reason = reason;
    }

    /**
     * Returns an array containing every instances of this type that have not yet been
     * garbage collected. The first elements of the returned array are the enumeration
     * constants, in declaration order. All other elements are the instances created
     * by the {@link #valueOf(String)} method, in no particular order.
     *
     * @return An array containing the instances of this type.
     */
    public static NilReason[] values() {
        final int predefinedCount = PREDEFINED.length;
        NilReason[] reasons;
        synchronized (POOL) {
            reasons = POOL.toArray(new NilReason[predefinedCount + POOL.size()]);
        }
        int count = reasons.length;
        while (count != 0 && reasons[count-1] == null) count--;
        count += predefinedCount;
        final NilReason[] source = reasons;
        if (count != reasons.length) {
            reasons = new NilReason[count];
        }
        System.arraycopy(source, 0, reasons, predefinedCount, count - predefinedCount);
        System.arraycopy(PREDEFINED, 0, reasons, 0, predefinedCount);
        return reasons;
    }

    /**
     * Parses the given nil reason. This method accepts the following cases:
     *
     * <ul>
     *   <li><p>If the given argument is one of the {@code "inapplicable"}, {@code "missing"},
     *       {@code "template"}, {@code "unknown"}, {@code "withheld"} or {@code "other"} strings,
     *       then the corresponding pre-defined constant is returned.</p></li>
     *   <li><p>Otherwise if the given argument is {@code "other:"} followed by an explanation
     *       text, then a new instance is created and returned for that explanation. Note that
     *       if the given explanation contains any characters that are not
     *       {@linkplain Character#isUnicodeIdentifierPart(char) unicode identifier}
     *       (for example white spaces), then those characters are omitted.</p></li>
     *   <li><p>Otherwise this method attempts to parse the given argument as a {@link URI}.
     *       Such URI should refer to a resource which describes the reason for the exception.</p></li>
     * </ul>
     *
     * This method returns existing instances when possible.
     *
     * @param  reason The reason why an element is not present.
     * @return The reason as a {@code NilReason} object.
     * @throws URISyntaxException If the given string is not one of the predefined enumeration
     *         values and can not be parsed as a URI.
     */
    public static NilReason valueOf(String reason) throws URISyntaxException {
        reason = reason.trim();
        int i = reason.indexOf(':');
        if (i < 0) {
            for (final NilReason candidate : PREDEFINED) {
                if (reason.equalsIgnoreCase((String) candidate.reason)) {
                    return candidate;
                }
            }
        } else {
            if (reason.substring(0, i).trim().equalsIgnoreCase("other")) {
                final StringBuilder buffer = new StringBuilder("other:");
                final int length = reason.length();
                while (++i < length) {
                    final char c = reason.charAt(i);
                    if (Character.isUnicodeIdentifierPart(c)) {
                        buffer.append(c);
                    }
                }
                String result = buffer.toString();
                if (result.isEmpty()) {
                    return OTHER;
                }
                if (result.equals(reason)) {
                    result = reason; // Share existing instance.
                }
                return POOL.unique(new NilReason(result));
            }
        }
        return POOL.unique(new NilReason(new URI(reason)));
    }

    /**
     * Invoked after deserialization in order to return a unique instance if possible.
     */
    private Object readResolve() {
        if (reason instanceof String) {
            for (final NilReason candidate : PREDEFINED) {
                if (reason.equals(candidate.reason)) {
                    return candidate;
                }
            }
        }
        return POOL.unique(this);
    }

    /**
     * If this {@code NilReason} is an enumeration of kind {@link #OTHER}, returns the explanation
     * text. Otherwise returns {@code null}. If non-null, then the explanation is a unicode
     * identifier without white space.
     * <p>
     * Note that in the special case where {@code this} nil reason is the {@link #OTHER}
     * instance itself, then this method returns an empty string.
     *
     * @return The explanation as a unicode identifier, or {@code null} if this {@code NilReason}
     *         is not an enumeration of kind {@link #OTHER}.
     */
    public String getExplanation() {
        if (reason instanceof String) {
            final String text = (String) reason;
            final int s = text.indexOf(':');
            if (s >= 0) {
                return text.substring(s + 1);
            }
            if (text.equals("other")) {
                return "";
            }
        }
        return null;
    }

    /**
     * If the explanation of this {@code NilReason} is referenced by a URI, returns that URI.
     * Otherwise returns {@code null}.
     *
     * @return The URI, or {@code null} if the explanation of this {@code NilReason}
     *         is not referenced by a URI.
     */
    public URI getURI() {
        return (reason instanceof URI) ? (URI) reason : null;
    }

    /**
     * Returns the GML string representation of this {@code NilReason}. The returned string
     * is a simple enumeration value (e.g. {@code "inapplicable"}) if this {@code NilReason}
     * is one of the predefined constants, or a string of the form {@code "other:text"}, or
     * a URI.
     *
     * @return The GML string representation of this {@code NilReason}.
     */
    @Override
    public String toString() {
        return reason.toString();
    }

    /**
     * Returns a hash code value for this {@code NilReason}.
     */
    @Override
    public int hashCode() {
        return reason.hashCode() ^ (int)serialVersionUID;
    }

    /**
     * Compares this {@code NilReason} with the specified object for equality.
     *
     * @param other The object to compare with this {@code NilReason}.
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof NilReason) {
            return reason.equals(((NilReason) other).reason);
        }
        return false;
    }

    /**
     * Returns an object of the given type which is nil for the reason represented by this enum.
     * This method returns an object which implement the given interface together with the
     * {@link NilObject} interface. The {@link NilObject#getNilReason()} method will return
     * this enum, and all other methods (except the ones inherited from the {@link Object} class)
     * will return {@code null} or an empty collection as appropriate.
     *
     * @param  <T> The compile-time type of the {@code type} argument.
     * @param  type The object type as an <strong>interface</strong>.
     *         This is usually a <a href="http://www.geoapi.org">GeoAPI</a> interface.
     * @return An {@link NilObject} of the given type.
     */
    @SuppressWarnings("unchecked")
    public <T> T createNilObject(final Class<T> type) {
        ArgumentChecks.ensureNonNull("type", type);
        if (NilObjectHandler.isIgnoredInterface(type)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_$2, "type", type));
        }
        InvocationHandler h;
        synchronized (this) {
            if ((h = handler) == null) {
                handler = h = new NilObjectHandler(this);
            }
        }
        return (T) Proxy.newProxyInstance(NilReason.class.getClassLoader(),
                new Class<?>[] {type, NilObject.class, LenientComparable.class}, h);
    }
}
