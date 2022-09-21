/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.storage.memory;

import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.stream.Stream;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.storage.feature.TileMatrixSetFeatureReader;
import org.geotoolkit.storage.multires.AbstractTileMatrix;
import org.geotoolkit.storage.multires.AbstractTileMatrixSet;
import org.geotoolkit.storage.multires.ScaleSortedMap;
import org.apache.sis.storage.tiling.Tile;
import org.geotoolkit.storage.multires.TileFormat;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.apache.sis.storage.tiling.TileStatus;
import org.geotoolkit.storage.multires.WritableTileMatrix;
import org.geotoolkit.storage.multires.WritableTileMatrixSet;
import org.geotoolkit.storage.multires.WritableTiledResource;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class InMemoryTiledFeatureSet extends AbstractFeatureSet implements WritableTiledResource {

    private final Map<String,WritableTileMatrixSet> models = new HashMap<>();
    private final FeatureType type;

    public InMemoryTiledFeatureSet(FeatureType type) {
        super(null, false);
        this.type = type;
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return type;
    }

    @Override
    public TileFormat getTileFormat() {
        return TileFormat.UNDEFINED;
    }

    @Override
    public Collection<WritableTileMatrixSet> getTileMatrixSets() throws DataStoreException {
        return Collections.unmodifiableCollection(models.values());
    }

    @Override
    public WritableTileMatrixSet createTileMatrixSet(org.apache.sis.storage.tiling.TileMatrixSet template) throws DataStoreException {
        GenericName id = template.getIdentifier();
        if (id == null) {
            //create a unique id
            id = NamesExt.createRandomUUID();
        } else if (models.containsKey(id.toString())) {
            //change id to avoid overriding an existing pyramid
            id = NamesExt.createRandomUUID();
        }

        final InMemoryTileMatrixSet py = new InMemoryTileMatrixSet(id, template.getCoordinateReferenceSystem());
        TileMatrices.copyStructure(template, py);
        models.put(id.toString(), py);
        return py;
    }

    @Override
    public void deleteTileMatrixSet(String identifier) throws DataStoreException {
        ArgumentChecks.ensureNonNull("identifier", identifier);
        models.remove(identifier);
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        return new TileMatrixSetFeatureReader(this, type).features(null, parallel);
    }

    private final class InMemoryTileMatrixSet extends AbstractTileMatrixSet implements WritableTileMatrixSet {

        private final ScaleSortedMap<InMemoryTileMatrix> mosaics = new ScaleSortedMap<>();

        public InMemoryTileMatrixSet(GenericName id, CoordinateReferenceSystem crs) {
            super(id, crs);
        }

        @Override
        public SortedMap<GenericName,? extends WritableTileMatrix> getTileMatrices() {
            return Collections.unmodifiableSortedMap(mosaics);
        }

        @Override
        public WritableTileMatrix createTileMatrix(org.apache.sis.storage.tiling.TileMatrix template) throws DataStoreException {
            final InMemoryTileMatrix gm = new InMemoryTileMatrix(NamesExt.createRandomUUID(),
                    this, template.getTilingScheme(), ((TileMatrix)template).getTileSize());
            mosaics.insertByScale(gm);
            return gm;
        }

        @Override
        public void deleteTileMatrix(String mosaicId) throws DataStoreException {
            for (InMemoryTileMatrix tm : mosaics.values()) {
                if (tm.getIdentifier().toString().equals(mosaicId)) {
                    mosaics.removeByScale(tm);
                    return;
                }
            }
        }
    }

    private final class InMemoryTileMatrix extends AbstractTileMatrix implements WritableTileMatrix {

        private final Map<Point,InMemoryDeferredTile> mpTileReference = new HashMap<>();

        public InMemoryTileMatrix(final GenericName id, WritableTileMatrixSet pyramid, GridGeometry tilingScheme, int[] tileSize) {
            super(id, pyramid, tilingScheme, tileSize);
        }

        @Override
        public TileStatus getTileStatus(long... indices) {
            return mpTileReference.get(new Point(Math.toIntExact(indices[0]), Math.toIntExact(indices[1]))) == null ?
                    TileStatus.MISSING : TileStatus.EXISTS;
        }

        @Override
        public Optional<Tile> getTile(long... indices) throws DataStoreException {
            return Optional.ofNullable(mpTileReference.get(new Point(Math.toIntExact(indices[0]), Math.toIntExact(indices[1]))));
        }

        public synchronized void setTile(int col, int row, InMemoryDeferredTile tile) {
            mpTileReference.put(new Point(Math.toIntExact(col), Math.toIntExact(row)), tile);
        }

        @Override
        public void writeTiles(Stream<Tile> tiles) throws DataStoreException {
            try {
                tiles.parallel().forEach((Tile tile) -> {
                    try {
                        writeTile(tile);
                    } catch (DataStoreException ex) {
                        throw new BackingStoreException(ex);
                    }
                });
            } catch (BackingStoreException ex) {
                throw ex.unwrapOrRethrow(DataStoreException.class);
            }
        }

        private void writeTile(Tile tile) throws DataStoreException {
            Resource r = tile.getResource();

            if (r instanceof FeatureSet) {
                final FeatureSet imgTile = (FeatureSet) r;
                InMemoryFeatureSet imfs = new InMemoryFeatureSet(imgTile.getType());
                try (Stream<Feature> stream = imgTile.features(false)) {
                    imfs.add(stream.iterator());
                }
                long[] indices = tile.getIndices();
                setTile((int) indices[0], (int) indices[1], new InMemoryDeferredTile(indices, imfs));
            } else {
                throw new DataStoreException("Only FeatureSet tiles are supported.");
            }
        }

        @Override
        public long deleteTiles(GridExtent indicesRanges) throws DataStoreException {
            if (indicesRanges == null) {
                long nb = mpTileReference.size();
                mpTileReference.clear();
                return nb;
            } else {
                long nb = 0;
                final long[] low = indicesRanges.getLow().getCoordinateValues();
                final long[] high = indicesRanges.getHigh().getCoordinateValues();
                for (long x = low[0]; x <= high[0]; x++) {
                    for (long y = low[1]; y <= high[1]; y++) {
                        InMemoryDeferredTile previous = mpTileReference.remove(new Point(Math.toIntExact(x), Math.toIntExact(y)));
                        if (previous != null) nb++;
                    }
                }
                return nb;
            }
        }

        @Override
        public Tile anyTile() throws DataStoreException {
            final Iterator<InMemoryDeferredTile> iterator = mpTileReference.values().iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
            throw new DataStoreException("No tiles in mosaic");
        }
    }

}
