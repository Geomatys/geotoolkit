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
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.LenientComparable;


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
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.xml.NilReason}.
 */
@Deprecated
public final class NilReason {
    /**
     * There is no value.
     * <p>
     * The string representation is {@code "inapplicable"}.
     */
    public static final org.apache.sis.xml.NilReason INAPPLICABLE = org.apache.sis.xml.NilReason.INAPPLICABLE;

    /**
     * The correct value is not readily available to the sender of this data.
     * Furthermore, a correct value may not exist.
     * <p>
     * The string representation is {@code "missing"}.
     */
    public static final org.apache.sis.xml.NilReason MISSING = org.apache.sis.xml.NilReason.MISSING;

    /**
     * The value will be available later.
     * <p>
     * The string representation is {@code "template"}.
     */
    public static final org.apache.sis.xml.NilReason TEMPLATE = org.apache.sis.xml.NilReason.TEMPLATE;

    /**
     * The correct value is not known to, and not computable by, the sender of this data.
     * However, a correct value probably exists.
     * <p>
     * The string representation is {@code "unknown"}.
     */
    public static final org.apache.sis.xml.NilReason UNKNOWN = org.apache.sis.xml.NilReason.UNKNOWN;

    /**
     * The value is not divulged.
     * <p>
     * The string representation is {@code "withheld"}.
     */
    public static final org.apache.sis.xml.NilReason WITHHELD = org.apache.sis.xml.NilReason.WITHHELD;

    /**
     * Other brief explanation. This constant does not provide any explanation. In order to test
     * if an enumeration is {@code "other"}, users should invoke the {@link #getExplanation()}
     * method instead than comparing against this enumeration.
     * <p>
     * The string representation is {@code "other:text"}, where text is a string of two or more
     * characters with no included spaces.
     */
    public static final org.apache.sis.xml.NilReason OTHER = org.apache.sis.xml.NilReason.OTHER;

    /**
     * Do not allow instantiation.
     */
    private NilReason() {
    }

    /**
     * Returns an array containing every instances of this type that have not yet been
     * garbage collected. The first elements of the returned array are the enumeration
     * constants, in declaration order. All other elements are the instances created
     * by the {@link #valueOf(String)} method, in no particular order.
     *
     * @return An array containing the instances of this type.
     */
    public static org.apache.sis.xml.NilReason[] values() {
        return org.apache.sis.xml.NilReason.values();
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
    public static org.apache.sis.xml.NilReason valueOf(String reason) throws URISyntaxException {
        return org.apache.sis.xml.NilReason.valueOf(reason);
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
    public static <T> T createNilObject(final org.apache.sis.xml.NilReason reason, final Class<T> type) {
        ArgumentChecks.ensureNonNull("type", type);
        if (NilObjectHandler.isIgnoredInterface(type)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2, "type", type));
        }
        InvocationHandler h= new NilObjectHandler(reason);
        return (T) Proxy.newProxyInstance(NilReason.class.getClassLoader(),
                new Class<?>[] {type, NilObject.class, LenientComparable.class}, h);
    }
}
