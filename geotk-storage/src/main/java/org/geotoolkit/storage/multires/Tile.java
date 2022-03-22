/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.multires;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;

/**
 * A small hyper-rectangular representation of data which is part of a tiling scheme.
 * A tile is uniquely defined in a tile matrix by an integer index in each dimension.
 * Tiles can be a coverage subsets, or a feature based representation (e.g. vector tiles).
 *
 * <p>All methods in this interface return non-null values.</p>
 *
 * @author  Johann Sorel (Geomatys)
 * @author  Martin Desruisseaux (Geomatys)
 *
 * @see TileMatrix#getTiles(GridExtent, boolean)
 */
public interface Tile {

    /**
     * Returns the indices of this tile in the {@code TileMatrix}.
     * If this tile was obtained by a call to {@link TileMatrix#getTile(long...)},
     * then the returned array contains the indices that were given in that call.
     *
     * <p>The returned array contains coordinates in the space defined by
     * the {@linkplain GridGeometry#getExtent() extent} of
     * the {@linkplain TileMatrix#getTilingScheme() tiling scheme}.
     * As such, it complies with the following constraints:</p>
     * <ul>
     *   <li>The array length is equal to {@link GridExtent#getDimension()}.</li>
     *   <li>The axis order — usually (<var>column</var>, <var>row/</var>) — is the
     *       {@linkplain GridExtent#getAxisType(int) extent axis} order.</li>
     *   <li>Values are between the {@linkplain GridExtent#getLow(int) extent low}
     *       and {@linkplain GridExtent#getHigh(int) high} values, inclusive.</li>
     * </ul>
     *
     * @return indices of this tile in the {@link TileMatrix},
     *         as coordinates inside the matrix {@link GridExtent}.
     *
     * @see TileMatrix#getTile(long...)
     */
    long[] getIndices();

    /**
     * Returns information about whether the tile failed to load.
     * The return value can be {@link TileStatus#EXISTS} or {@link TileStatus#IN_ERROR};
     * other enumeration values should not happen after a user successfully obtained this {@code Tile} instance.
     *
     * <h4>State transition</h4>
     * {@link TileStatus#EXISTS} is not a guarantee that a call to {@link #getResource()} will succeed.
     * The error may be detected only during the first attempt to read the resource.
     * Consequently this method may initially return {@code EXISTS},
     * then return {@code IN_ERROR} later after the first read attempt.
     *
     * @return information about the availability of this tile.
     *
     * @see TileMatrix#getTileStatus(long...)
     */
    TileStatus getStatus();

    /**
     * Returns the tile content as a resource.
     * The resource type is typically {@link GridCoverageResource},
     * but it may also be other types (e.g. vector tiles).
     *
     * @return the tile content.
     * @throws DataStoreException if an error occurred while reading the content.
     */
    Resource getResource() throws DataStoreException;
}
