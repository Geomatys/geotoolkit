/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.image.io.metadata;

import javax.imageio.metadata.IIOMetadata;

import org.opengis.referencing.crs.*;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.FORMAT_NAME;


/**
 * @deprecated Renamed {@link ReferencingBuilder} without inheritance from {@code MetadataHelper}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.07
 * @module
 */
@Deprecated
public class ReferencingMetadataHelper extends MetadataHelper {
    /**
     * Where to delegate the CRS creation.
     */
    private final ReferencingBuilder builder;

    /**
     * Creates a new metadata helper for the given metadata.
     *
     * @param metadata The Image I/O metadata. An instance of the {@link SpatialMetadata}
     *                 sub-class is recommanded, but not mandatory.
     */
    public ReferencingMetadataHelper(final IIOMetadata metadata) {
        this(new MetadataAccessor(metadata, FORMAT_NAME, "RectifiedGridDomain/CoordinateReferenceSystem", null));
    }

    /**
     * Creates a new metadata helper using the given accessor.
     */
    private ReferencingMetadataHelper(final MetadataAccessor accessor) {
        super(accessor);
        builder = new ReferencingBuilder(accessor);
    }

    /**
     * Returns the coordinate reference system, or {@code null} if it can not be created.
     * This method delegates to {@link #getCoordinateReferenceSystem(Class)} and catch the
     * exception. If an exception has been thrown, the exception is
     * {@linkplain MetadataAccessor#warningOccurred logged} and this method returns {@code null}.
     *
     * @return The CRS, or {@code null} if none.
     */
    public CoordinateReferenceSystem getOptionalCRS() {
        return builder.getOptionalCRS();
    }
}
