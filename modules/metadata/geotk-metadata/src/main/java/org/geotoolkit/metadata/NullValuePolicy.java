/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata;


/**
 * Whatever {@link MetadataStandard#asMap(Object,NullValuePolicy,KeyNamePolicy) MetadataStandard.asMap(...)}
 * should contain entries for null values or empty collections. By default the map does not provide
 * {@linkplain java.util.Map.Entry entries} for {@code null} metadata attributes or
 * {@linkplain java.util.Collection#isEmpty() empty} collections. This enumeration
 * allows control on this behavior.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @see MetadataStandard#asMap(Object, NullValuePolicy, KeyNamePolicy)
 *
 * @since 3.03
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.metadata.NullValuePolicy}.
 */
@Deprecated
public final class NullValuePolicy {
    private NullValuePolicy() {
    }

    /**
     * Includes all entries in the map, including those having a null value or an
     * empty collection.
     */
    public static final org.apache.sis.metadata.NullValuePolicy ALL =
            org.apache.sis.metadata.NullValuePolicy.ALL;

    /**
     * Includes only the non-null attributes. Collections are included no matter if
     * they are empty or not.
     */
    public static final org.apache.sis.metadata.NullValuePolicy NON_NULL =
            org.apache.sis.metadata.NullValuePolicy.NON_NULL;

    /**
     * Includes only the attributes that are non-null and, in the case of collections,
     * non-{@linkplain java.util.Collection#isEmpty() empty}. This is the default behavior
     * of {@link AbstractMetadata#asMap()}.
     */
    public static final org.apache.sis.metadata.NullValuePolicy NON_EMPTY =
            org.apache.sis.metadata.NullValuePolicy.NON_EMPTY;
}
