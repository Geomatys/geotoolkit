/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata;


/**
 * Whatever {@link java.util.Map} of metadata should contain entries for null values or empty
 * collections. By default the map returned by {@link AbstractMetadata#asMap()} does not provide
 * {@linkplain java.util.Map.Entry entries} for {@code null} metadata attributes or
 * {@linkplain java.util.Collection#isEmpty() empty} collections. This enumeration
 * allows control on this behavior.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @see MetadataStandard#asMap(Object, MapContent, MetadataKeyName)
 *
 * @since 3.03
 * @module
 */
public enum MapContent {
    /**
     * Includes all entries in the map, including those having a null value or an
     * empty collection.
     */
    ALL,

    /**
     * Includes only the non-null attributes. Collections are included no matter if
     * they are empty or not.
     */
    NON_NULL,

    /**
     * Includes only the attributes that are non-null and, in the case of collections,
     * non-{@linkplain java.util.Collection#isEmpty() empty}. This is the default behavior
     * of {@link AbstractMetadata#asMap()}.
     */
    NON_EMPTY
}
