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
package org.geotoolkit.storage.coverage.tiling;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.storage.tiling.WritableTileMatrix;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.storage.multires.TileInError;
import org.geotoolkit.storage.multires.TileMatrices;
import org.opengis.util.GenericName;

/**
 * A simple writable tile matrix which stores in a map the written tiles unchanged.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class MemoryTileMatrix implements WritableTileMatrix {

    private final GenericName identifier;
    private final GridGeometry tilingScheme;
    private final int[] tileSize;
    private final Map<LongKey,Tile> tiles = Collections.synchronizedMap(new HashMap<>());

    /**
     * Create a new MemoryTileMatrix.
     *
     * @param identifier matrix identifier, not null
     * @param tilingScheme tiling scheme, not null
     * @param tileSize tile size in pixel, if tiles are not images an estimated size should be used.
     *        This information is used only for resolution computation.
     */
    public MemoryTileMatrix(final GenericName identifier, GridGeometry tilingScheme, int[] tileSize) {
        ArgumentChecks.ensureNonNull("identifier", identifier);
        ArgumentChecks.ensureNonNull("tilingScheme", tilingScheme);
        ArgumentChecks.ensureNonNull("tileSize", tileSize);
        this.identifier = identifier;
        this.tilingScheme = tilingScheme;
        this.tileSize = tileSize.clone();
    }

    @Override
    public GenericName getIdentifier() {
        return identifier;
    }

    @Override
    public GridGeometry getTilingScheme() {
        return tilingScheme;
    }

    @Override
    public double[] getResolution() {
        final double[] resolution = getTilingScheme().getResolution(true);
        for (int i = 0; i < resolution.length; i++) {
            resolution[i] /= tileSize[i];
        }
        return resolution;
    }

    @Override
    public TileStatus getTileStatus(long... indices) {
        return tiles.containsKey(new LongKey(indices)) ? TileStatus.EXISTS : TileStatus.MISSING;
    }

    @Override
    public Stream<Tile> getTiles(GridExtent indicesRanges, boolean parallel) throws DataStoreException {
        if (indicesRanges == null) indicesRanges = getTilingScheme().getExtent();
        final Stream<long[]> stream = TileMatrices.pointStream(indicesRanges);
        return stream.map((long[] t) -> {
            try {
                return getTile(t).orElse(null);
            } catch (DataStoreException ex) {
                return TileInError.create(t, ex);
            }
        }).filter(Objects::nonNull);
    }

    @Override
    public Optional<Tile> getTile(long... indices) throws DataStoreException {
        return Optional.ofNullable(tiles.get(new LongKey(indices)));
    }

    @Override
    public void writeTiles(Stream<Tile> tiles) throws DataStoreException {
        try {
            tiles.parallel().forEach((Tile tile) -> {
                this.tiles.put(new LongKey(tile.getIndices()), tile);
            });
        } catch (BackingStoreException ex) {
            throw ex.unwrapOrRethrow(DataStoreException.class);
        }
    }

    @Override
    public long deleteTiles(GridExtent indicesRanges) throws DataStoreException {
        if (indicesRanges == null) {
            long nb = tiles.size();
            tiles.clear();
            return nb;
        } else {
            long nb = 0;
            try (Stream<long[]> stream = TileMatrices.pointStream(indicesRanges)) {
                Iterator<long[]> iterator = stream.iterator();
                while (iterator.hasNext()) {
                    Tile previous = tiles.remove(new LongKey(iterator.next()));
                    if (previous != null) nb++;
                }
            }
            return nb;
        }
    }

    private static final class LongKey {
        private final long[] indices;

        LongKey(long ... indices) {
            this.indices = indices;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(indices);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LongKey k) {
                return Arrays.equals(indices, k.indices);
            }
            return false;
        }
    }
}
