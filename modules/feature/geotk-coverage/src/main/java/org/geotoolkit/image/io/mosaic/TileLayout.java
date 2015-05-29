/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.mosaic;


/**
 * The layout of tiles in a {@link TileManager}. This is an enumeration that specify how
 * {@link MosaicBuilder} should lay out the new tiles relative to each other. For example
 * if the pixels in an image <cite>overview</cite> cover a geographic area 2 time larger
 * (in width and height) than the pixels in an <cite>original</cite> image, then we have
 * a choice:
 * <p>
 * <ul>
 *   <li>The overview image as a whole covers the same geographic area than the original image,
 *       in which case the overview has 2&times;2 less pixels than the original image
 *       ({@link #CONSTANT_GEOGRAPHIC_AREA}).</li>
 *   <li>The overview image has the same amount of pixels than the original image, in which
 *       case the image as a whole covers a geographic area 2&times;2 bigger than the original
 *       image ({@link #CONSTANT_TILE_SIZE}).</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @see MosaicBuilder
 *
 * @since 2.5
 * @module
 */
public enum TileLayout {
    /**
     * A generic layout with heteregenous tile size or geographic area.
     * This enum is returned when no other enum fit.
     */
    GENERIC,

    /**
     * All tiles have the same width and height in pixels. Consequently the levels at the finest
     * resolution have more tiles than levels at lower resolution. In other words, the tiles at
     * the finest resolution cover smaller geographic area. This is the most efficient tile layout.
     */
    CONSTANT_TILE_SIZE,

    /**
     * All tiles cover the same geographic area. Consequently, tiles at the finest resolution may
     * be very big while tiles at lower resolutions are smaller. This is the simplest tile layout,
     * easy to manage but inefficient. It is provided for testing purpose and compatibility with
     * some external softwares using such layout.
     */
    CONSTANT_GEOGRAPHIC_AREA
}
