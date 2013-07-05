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

import java.util.Map;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;


/**
 * A map view of some or all identifiers in an {@linkplain IdentifiedObject identified object}.
 * Each {@linkplain java.util.Map.Entry map entry} is associated to an {@link Identifier} where
 * {@linkplain java.util.Map.Entry#getKey() key} is the {@linkplain Identifier#getAuthority()
 * identifier authority} and the {@linkplain java.util.Map.Entry#getValue() value} is the
 * {@linkplain Identifier#getCode() identifier code}.
 * <p>
 * Some XML identifiers are difficult to handle as {@link Identifier} objects. Those identifiers are
 * rather handled using specialized classes like {@link XLink}. This {@code IdentifierMap} interface
 * mirrors the standard {@link Map#get get} and {@link Map#put put} methods with specialized methods,
 * in order to fetch and store identifiers as objects of the specialized class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see IdentifiedObject#getIdentifierMap()
 *
 * @since 3.19
 * @module
 *
 * @deprecated Moved to SIS as {@link org.apache.sis.xml.IdentifierMap}.
 */
@Deprecated
public interface IdentifierMap extends Map<Citation,String> {
    /**
     * Returns the identifier to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * @param  <T> The identifier type.
     * @param  authority The key whose associated identifier is to be returned.
     * @return The identifier to which the specified key is mapped, or
     *         {@code null} if this map contains no mapping for the key.
     */
    <T> T getSpecialized(IdentifierSpace<T> authority);

    /**
     * Associates the specified identifier with the specified key in this map
     * (optional operation). If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.
     *
     * @param  <T> The identifier type.
     * @param  authority The key with which the specified identifier is to be associated.
     * @param  value The identifier to be associated with the specified key.
     * @return The previous identifier associated with {@code key}, or {@code null} if there was
     *         no mapping for {@code key}.
     * @throws UnsupportedOperationException If the identifier map is unmodifiable.
     */
    <T> T putSpecialized(IdentifierSpace<T> authority, T value) throws UnsupportedOperationException;
}
