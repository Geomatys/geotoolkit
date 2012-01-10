/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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

import java.awt.image.RenderedImage;


/**
 * The type of information produced or modified by an {@linkplain javax.imageio.ImageReader
 * image reader} or {@linkplain javax.imageio.ImageWriter writer}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public enum InformationType {
    /**
     * The reader or writer may produce or modify stream
     * {@linkplain javax.imageio.metadata.IIOMetadata metadata}.
     *
     * @see javax.imageio.ImageReader#getStreamMetadata()
     */
    STREAM_METADATA,

    /**
     * The reader or writer may produce or modify image
     * {@linkplain javax.imageio.metadata.IIOMetadata metadata}.
     *
     * @see javax.imageio.ImageReader#getImageMetadata(int)
     */
    IMAGE_METADATA,

    /**
     * The reader or writer may produce or modify {@linkplain java.awt.image.Raster raster}.
     *
     * @see javax.imageio.ImageReader#canReadRaster()
     * @see javax.imageio.ImageWriter#canWriteRasters()
     */
    RASTER,

    /**
     * The reader or writer may produce or modify {@linkplain java.awt.image.Raster raster}
     * or {@linkplain RenderedImage rendered images}.
     * <p>
     * Note that {@code IMAGE} usually implies {@link #RASTER} with the addition of
     * {@linkplain java.awt.image.ColorModel color model} processing.
     *
     * @see javax.imageio.ImageReader#read(int)
     * @see javax.imageio.ImageWriter#write(RenderedImage)
     */
    IMAGE
}
