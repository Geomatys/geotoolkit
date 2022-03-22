/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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


/**
 * Information about the availability of a tile. Some {@link TileMatrix} implementations
 * may not know whether a tile exists or not before the first attempt to read that tile.
 * Consequently a tile status may be initially {@link #UNKNOWN} and transitions
 * at a later time to a state such as {@link #EXISTS}, {@link #MISSING} or {@link #IN_ERROR}.
 *
 * @author  Alexis Manin (Geomatys)
 * @author  Johann Sorel (Geomatys)
 *
 * @see Tile#getStatus()
 * @see TileMatrix#getTileStatus(long...)
 */
public enum TileStatus {
    /**
     * The tile status can not be known unless the tile is read. This value is returned
     * by some {@link TileMatrix} implementations when determining the availability of
     * a tile would require relatively costly I/O operations.
     */
    UNKNOWN,

    /**
     * The tile exists. However this is not a guarantee that no I/O error will happen when reading the tile,
     * neither that the tile will be non-empty. If an I/O error happens at tile reading time,
     * then the tile status should transition from {@code EXISTS} to {@link #IN_ERROR}.
     */
    EXISTS,

    /**
     * The tile is flagged as missing. It may happen in regions where no data is available.
     */
    MISSING,

    /**
     * The tile for which a status has been requested is outside the {@link TileMatrix} extent.
     */
    OUTSIDE_EXTENT,

    /**
     * The tile exists but attempt to read it failed.
     * It may be because an {@link IOException} occurred while reading the tile.
     */
    IN_ERROR
}
