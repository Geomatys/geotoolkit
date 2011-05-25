/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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

import java.lang.reflect.Proxy;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.LenientComparable;


/**
 * Invoked by the unmarshaller when {@code xlink} attributes are found in place of an object
 * definition. The default implementation is not linked to any catalog; {@code xlink} attributes
 * are simply stored in otherwise empty objects. However subclasses may override the methods
 * defined in this class in order to find in some calalog the object associated to a given
 * set of {@code xlink} attributes.
 * <p>
 * See the {@link XML#LINKER} javadoc for an example of registering a custom
 * {@code ObjectLinker} to a unmarshaller.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
public class ObjectLinker {
    /**
     * The default, thread-safe and immutable instance. This instance defines the methods
     * used during every unmarshalling if no {@code ObjectLinker} was explicitly set.
     */
    public static final ObjectLinker DEFAULT = new ObjectLinker();

    /**
     * Creates a default {@code ObjectLinker}. This is for subclasses only,
     * since new instances are useful only if at least one method is overridden.
     */
    protected ObjectLinker() {
    }

    /**
     * Returns an object of the given type for the given {@code xlink} attributes.
     * The default implementation returns an immutable object which implement the
     * given interface together with the {@link IdentifiedObject} interface. The
     * {@link IdentifiedObject#getXLink()} method will return the given link, and
     * all other methods (except the ones inherited from the {@link Object} class)
     * will return {@code null} or an empty collection as appropriate.
     *
     * @param  <T> The compile-time type of the {@code type} argument.
     * @param  type The type of object to be unmarshalled as an <strong>interface</strong>.
     *         This is usually a <a href="http://www.geoapi.org">GeoAPI</a> interface.
     * @param  link The {@code xlink} attributes.
     * @return An object of the given type for the given {@code xlink} attributes,
     *         or {@code null} if none.
     */
    @SuppressWarnings("unchecked")
    public <T> T resolve(final Class<T> type, final XLink link) {
        ArgumentChecks.ensureNonNull("type", type);
        ArgumentChecks.ensureNonNull("link", link);
        if (EmptyObjectHandler.isIgnoredInterface(type)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_$2, "type", type));
        }
        return (T) Proxy.newProxyInstance(ObjectLinker.class.getClassLoader(),
                new Class<?>[] {type, IdentifiedObject.class, EmptyObject.class, LenientComparable.class},
                new EmptyObjectHandler(link));
    }

    /**
     * Returns an object of the given type for the given {@code nilReason} attributes. The default
     * implementation returns an immutable object which implement the given interface together
     * with the {@link EmptyObject} interface. The {@link EmptyObject#getNilReason()} method will
     * return the given link, and all other methods (except the ones inherited from the
     * {@link Object} class) will return {@code null} or an empty collection as appropriate.
     *
     * @param  <T> The compile-time type of the {@code type} argument.
     * @param  type The type of object to be unmarshalled as an <strong>interface</strong>.
     *         This is usually a <a href="http://www.geoapi.org">GeoAPI</a> interface.
     * @param  nilReason The {@code nilReason} attribute.
     * @return An object of the given type for the given {@code nilReason} attribute,
     *         or {@code null} if none.
     */
    @SuppressWarnings("unchecked")
    public <T> T resolve(final Class<T> type, final NilReason nilReason) {
        ArgumentChecks.ensureNonNull("type", type);
        ArgumentChecks.ensureNonNull("nilReason", nilReason);
        if (EmptyObjectHandler.isIgnoredInterface(type)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_$2, "type", type));
        }
        return (T) Proxy.newProxyInstance(ObjectLinker.class.getClassLoader(),
                new Class<?>[] {type, EmptyObject.class}, new EmptyObjectHandler(nilReason));
    }
}
