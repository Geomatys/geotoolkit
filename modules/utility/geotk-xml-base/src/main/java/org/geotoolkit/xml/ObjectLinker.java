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

import java.util.UUID;
import java.lang.reflect.Proxy;

import org.opengis.metadata.Identifier;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.LenientComparable;
import org.geotoolkit.internal.jaxb.gco.ObjectIdentification;

import static org.apache.sis.util.ArgumentChecks.*;


/**
 * Invoked by the unmarshaller when {@code xlink} or {@code uuidref} attributes are found instead
 * of object definition. This class provides methods for assigning a {@linkplain UUID} to an
 * arbitrary object, or fetching an existing object from a UUID.
 * <p>
 * Subclasses can override the methods defined in this class in order to search in their
 * own catalog. See the {@link XML#LINKER} javadoc for an example of registering a custom
 * {@code ObjectLinker} to a unmarshaller.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.18
 * @module
 */
public class ObjectLinker {
    /**
     * The default and thread-safe instance. This instance is used at unmarshalling time
     * when no {@code ObjectLinker} was explicitly set by the {@link XML#LINKER} property.
     */
    @SuppressWarnings("unchecked")
    public static final ObjectLinker DEFAULT = new ObjectLinker();

    /**
     * Creates a default {@code ObjectLinker}. This constructor is for subclasses only.
     */
    protected ObjectLinker() {
    }

    /**
     * Returns an empty object of the given type having the given identifiers.
     * The object returned by the default implementation has the following properties:
     * <p>
     * <ul>
     *   <li>Implements the given {@code type} interface.</li>
     *   <li>Implements the {@link IdentifiedObject} interface.</li>
     *   <li>{@link IdentifiedObject#getIdentifiers()} will return the given identifiers.</li>
     *   <li>{@link IdentifiedObject#getIdentifierMap()} will return a {@link java.util.Map}
     *        view over the given identifiers.</li>
     *   <li>All other methods except the ones inherited from the {@link Object} class will return
     *       {@code null} or an empty collection.</li>
     * </ul>
     *
     * @param  <T> The compile-time type of the {@code type} argument.
     * @param  type The type of object to be unmarshalled as an <strong>interface</strong>.
     *         This is usually a <a href="http://www.geoapi.org">GeoAPI</a> interface.
     * @param  identifiers An arbitrary amount of identifiers. For each identifier, the
     *         {@linkplain Identifier#getAuthority() authority} is typically (but not
     *         necessarily) one of the constants defined in {@link IdentifierSpace}.
     * @return An object of the given type for the given identifiers, or {@code null} if none.
     *
     * @since 3.19
     */
    @SuppressWarnings("unchecked")
    public <T> T newIdentifiedObject(final Class<T> type, final Identifier... identifiers) {
        if (NilObjectHandler.isIgnoredInterface(type)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_$2, "type", type));
        }
        return (T) Proxy.newProxyInstance(ObjectLinker.class.getClassLoader(),
                new Class<?>[] {type, IdentifiedObject.class, NilObject.class, LenientComparable.class},
                new NilObjectHandler(Identifier.class, identifiers));
    }

    /**
     * Returns an object of the given type for the given {@code uuid} attribute, or {@code null}
     * if none. The default implementation first looks in an internal map for previously unmarshalled
     * object having the given UUID.
     *
     * @param  <T> The compile-time type of the {@code type} argument.
     * @param  type The type of object to be unmarshalled as an <strong>interface</strong>.
     *         This is usually a <a href="http://www.geoapi.org">GeoAPI</a> interface.
     * @param  uuid The {@code uuid} attributes.
     * @return An object of the given type for the given {@code uuid} attribute,
     *         or {@code null} if none.
     *
     * @since 3.19
     */
    @SuppressWarnings("unchecked")
    public <T> T resolve(final Class<T> type, final UUID uuid) {
        ensureNonNull("type", type);
        ensureNonNull("uuid", uuid);
        final Object object = ObjectIdentification.UUIDs.lookup(uuid);
        return type.isInstance(object) ? (T) object : null;
    }

    /**
     * Returns an object of the given type for the given {@code xlink} attribute, or {@code null}
     * if none. The default implementation returns {@code null} in all cases.
     *
     * @param  <T> The compile-time type of the {@code type} argument.
     * @param  type The type of object to be unmarshalled as an <strong>interface</strong>.
     *         This is usually a <a href="http://www.geoapi.org">GeoAPI</a> interface.
     * @param  link The {@code xlink} attributes.
     * @return An object of the given type for the given {@code xlink} attribute,
     *         or {@code null} if none.
     */
    public <T> T resolve(final Class<T> type, final XLink link) {
        ensureNonNull("type",  type);
        ensureNonNull("xlink", link);
        return null;
    }

    /**
     * Returns an object of the given type for the given {@code nilReason} attributes. The default
     * implementation returns an immutable object which implement the {@link NilObject} interface
     * and the given {@code type}. The {@link NilObject#getNilReason()} method will return the
     * given reason, and all other methods (except the ones inherited from the {@link Object}
     * class) will return {@code null} or an empty collection as appropriate.
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
        ensureNonNull("type", type);
        ensureNonNull("nilReason", nilReason);
        return nilReason.createNilObject(type);
    }

    /**
     * Returns {@code true} if the marshaller can use a reference to the given metadata
     * instead than writing the full element. This method is invoked when a metadata to
     * be marshalled has a UUID identifier. Because those metadata may be defined externally,
     * Geotk can not know if the metadata shall be fully marshalled or not. This information
     * must be provided by the application.
     * <p>
     * The default implementation conservatively returns {@code false} in every cases.
     *
     * @param  <T> The compile-time type of the {@code type} argument.
     * @param  type The type of object to be marshalled as an <strong>interface</strong>.
     *         This is usually a <a href="http://www.geoapi.org">GeoAPI</a> interface.
     * @param  object The object to be marshalled.
     * @param  uuid The unique identifier of the object to be marshalled.
     * @return {@code true} if the marshaller can use the {@code uuidref} attribute
     *         instead than marshalling the given metadata.
     *
     * @since 3.19
     */
    public <T> boolean canUseReference(final Class<T> type, final T object, final UUID uuid) {
        return false;
    }
}
