/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io;

import javax.imageio.spi.ImageWriterSpi;


/**
 * Base class for writers of geographic images.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.06
 *
 * @since 2.4
 * @module
 *
 * @deprecated Renamed {@link SpatialImageWriter}.
 */
@Deprecated
public abstract class GeographicImageWriter extends SpatialImageWriter {
    /**
     * Constructs a {@code GeographicImageWriter}.
     *
     * @param provider The {@code ImageWriterSpi} that is constructing this object, or {@code null}.
     */
    protected GeographicImageWriter(final ImageWriterSpi provider) {
        super(provider);
    }
}
