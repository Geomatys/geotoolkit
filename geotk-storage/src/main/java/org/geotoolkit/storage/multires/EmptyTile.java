package org.geotoolkit.storage.multires;

import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileStatus;

/**
 * Describe a tile without any valid data associated to it. It can represent an area without any data on sparse dataset,
 * but can also be used as basis for error description (Ex: see {@link TileInError }).
 */
public interface EmptyTile extends Tile {
    @Override
    default TileStatus getStatus() {
        return TileStatus.EXISTS;
    }
}
