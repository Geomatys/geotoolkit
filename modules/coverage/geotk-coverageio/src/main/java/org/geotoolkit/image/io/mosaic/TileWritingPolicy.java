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
 * Controls the way {@link MosaicImageWriter} writes the tiles. This include the behavior
 * when a file to be written already exists.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @see MosaicImageWriteParam#setTileWritingPolicy
 * @see MosaicBuilder#createTileManager
 *
 * @since 2.5
 * @module
 */
public enum TileWritingPolicy {
    /**
     * Overwrite existing tiles unconditionally.
     * This is the default behavior.
     */
    OVERWRITE(true, true),

    /**
     * Skip existing tiles. This option works only for {@linkplain Tile#getInput tile input}
     * of type {@link java.io.File}. Other types like {@link java.net.URL} are not guaranteed
     * to be checked for existence; they may be always overwritten.
     */
    WRITE_NEWS_ONLY(false, true),

    /**
     * Skip existing and empty tiles. This option works like {@link #WRITE_NEWS_ONLY}, but
     * skips also tiles having all pixels set to 0.
     */
    WRITE_NEWS_NONEMPTY(false, false),

    /**
     * Do not write any tile. This option can be given to {@link MosaicBuilder#createTileManager}.
     * While it is legal to {@linkplain MosaicImageWriteParam#setTileWritingPolicy give this option
     * as a parameter} to the writer, this is typically useless except for testing purpose.
     */
    NO_WRITE(false, false);

    /**
     * {@code true} if tiles should be overwritten.
     */
    final boolean overwrite;

    /**
     * {@code true} if empty tiles should be written as well.
     */
    final boolean includeEmpty;

    /**
     * The default value. This value is documented in
     * {@link MosaicImageWriteParam#getTileWritingPolicy()}.
     */
    static final TileWritingPolicy DEFAULT = OVERWRITE;

    /**
     * Creates a new enum.
     *
     * @param overwrite    {@code true} if tiles should be overwritten.
     * @param includeEmpty {@code true} if empty tiles should be written as well.
     */
    private TileWritingPolicy(final boolean overwrite, final boolean includeEmpty) {
        this.overwrite    = overwrite;
        this.includeEmpty = includeEmpty;
    }
}
