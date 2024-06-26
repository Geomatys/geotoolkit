/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.base.StoreResource;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.storage.coverage.TileMatrixSetCoverageReader;
import org.geotoolkit.storage.event.ContentEvent;
import org.geotoolkit.storage.event.ModelEvent;
import org.geotoolkit.storage.multires.AbstractTileMatrix;
import org.geotoolkit.storage.multires.AbstractTileMatrixSet;
import org.geotoolkit.storage.multires.ScaleSortedMap;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.WritableTileMatrix;
import org.geotoolkit.storage.multires.WritableTileMatrixSet;
import org.geotoolkit.storage.multires.WritableTiledResource;
import org.geotoolkit.util.NamesExt;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class InMemoryTiledGridCoverageResource extends AbstractGridCoverageResource
        implements WritableTiledResource, StoreResource, WritableGridCoverageResource
{
    private final InMemoryStore store;
    private final GenericName identifier;
    private final List<WritableTileMatrixSet> tileMatrixSets = new CopyOnWriteArrayList<>();
    private List<SampleDimension> dimensions;

    public InMemoryTiledGridCoverageResource(final GenericName name) {
        this(null, name);
    }

    public InMemoryTiledGridCoverageResource(final InMemoryStore store, final GenericName identifier) {
        super(null, false);
        this.store = store;
        this.identifier = identifier;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(identifier);
    }

    @Override
    public DataStore getOriginator() {
        return store;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Collection<WritableTileMatrixSet> getTileMatrixSets() throws DataStoreException {
        return tileMatrixSets;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return dimensions;
    }

    /**
     * Configure resource sample dimensions.
     */
    public void setSampleDimensions(List<SampleDimension> dimensions) throws DataStoreException {
        this.dimensions = dimensions;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public WritableTileMatrixSet createTileMatrixSet(org.apache.sis.storage.tiling.TileMatrixSet template) throws DataStoreException {
        final TileMatrixSet p = (TileMatrixSet) template;
        final InMemoryTileMatrixSet py = new InMemoryTileMatrixSet(NamesExt.createRandomUUID(), p.getCoordinateReferenceSystem());
        py.setBuildPhase(true);
        TileMatrices.copyStructure(p, py);
        py.setBuildPhase(false);
        tileMatrixSets.add(py);
        listeners.fire(StoreEvent.class, new ModelEvent(this));
        return py;
    }

    @Override
    public void deleteTileMatrixSet(String identifier) throws DataStoreException {
        ArgumentChecks.ensureNonNull("identifier", identifier);
        final Iterator<WritableTileMatrixSet> it     = tileMatrixSets.iterator();
        while (it.hasNext()) {
            final WritableTileMatrixSet tms = it.next();
            if (identifier.equalsIgnoreCase(tms.getIdentifier().toString())) {
                tileMatrixSets.remove(tms);
                listeners.fire(StoreEvent.class, new ModelEvent(this));
                return;
            }
        }
        throw new DataStoreException("Identifier "+identifier+" not found in models.");
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return new TileMatrixSetCoverageReader<>(this).getGridGeometry();
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return new TileMatrixSetCoverageReader<>(this).read(domain, range);
    }

    @Override
    public void write(GridCoverage coverage, WritableGridCoverageResource.Option... options) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported.");
    }

    private final class InMemoryTileMatrixSet extends AbstractTileMatrixSet implements WritableTileMatrixSet {

        private final ScaleSortedMap<InMemoryTileMatrix> tileMatrices = new ScaleSortedMap<>();
        private boolean buildPhase = false;

        public InMemoryTileMatrixSet(GenericName id, CoordinateReferenceSystem crs) {
            super(id, crs);
        }

        @Override
        public SortedMap<GenericName, ? extends WritableTileMatrix> getTileMatrices() {
            return Collections.unmodifiableSortedMap(tileMatrices);
        }

        private void setBuildPhase(boolean buildPhase) {
            this.buildPhase = buildPhase;
            for (InMemoryTileMatrix it : tileMatrices.values()) {
                it.setBuildPhase(buildPhase);
            }
        }

        @Override
        public WritableTileMatrix createTileMatrix(org.apache.sis.storage.tiling.TileMatrix template) throws DataStoreException {
            final InMemoryTileMatrix gm = new InMemoryTileMatrix(NamesExt.createRandomUUID(),
                    this, template.getTilingScheme(), ((TileMatrix)template).getTileSize());
            tileMatrices.insertByScale(gm);
            if (!buildPhase) {
                //we are creating object, dont send an event until we are finished.
                listeners.fire(StoreEvent.class, new ModelEvent(InMemoryTiledGridCoverageResource.this));
            }
            return gm;
        }

        @Override
        public void deleteTileMatrix(String mosaicId) throws DataStoreException {
            for (TileMatrix tm : tileMatrices.values()) {
                if (tm.getIdentifier().toString().equals(mosaicId)) {
                    tileMatrices.remove(tm.getIdentifier());
                    if (!buildPhase) {
                        listeners.fire(StoreEvent.class, new ModelEvent(InMemoryTiledGridCoverageResource.this));
                    }
                    break;
                }
            }
        }
    }

    private final class InMemoryTileMatrix extends AbstractTileMatrix implements WritableTileMatrix {

        private final Map<Point,Tile> mpTileReference = new HashMap<>();
        private boolean buildPhase = false;

        public InMemoryTileMatrix(final GenericName id, WritableTileMatrixSet pyramid, GridGeometry tilingScheme, int[] tileSize) {
            super(id, pyramid, tilingScheme, tileSize);
        }

        private void setBuildPhase(boolean buildPhase) {
            this.buildPhase = buildPhase;
        }

        @Override
        public TileStatus getTileStatus(long... indices) {
            return mpTileReference.get(new Point(Math.toIntExact(indices[0]), Math.toIntExact(indices[1]))) == null ?
                    TileStatus.MISSING : TileStatus.EXISTS;
        }

        @Override
        public Stream<Tile> getTiles(GridExtent indicesRanges, boolean parallel) throws DataStoreException {
            if (mpTileReference.isEmpty()) {
                return Stream.empty();
            }
            return super.getTiles(indicesRanges, parallel);
        }

        @Override
        public Optional<Tile> getTile(long... indices) throws DataStoreException {
            return Optional.ofNullable(mpTileReference.get(new Point(Math.toIntExact(indices[0]), Math.toIntExact(indices[1]))));
        }

        public synchronized void setTile(long col, long row, Tile tile) {
            mpTileReference.put(new Point(Math.toIntExact(col), Math.toIntExact(row)), tile);
            if (!buildPhase) {
                //we are creating object, dont send an event until we are finished.
                listeners.fire(StoreEvent.class, new ContentEvent(InMemoryTiledGridCoverageResource.this));
            }
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
            final long tileX = tile.getIndices()[0];
            final long tileY = tile.getIndices()[1];
            //TODO : what should we do ?
            // - keep the given tile
            // - copy the tile : tile resource may have a wide range of interfaces, complicated...
            setTile(tileX, tileY, tile);
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
                        Tile previous = mpTileReference.remove(new Point(Math.toIntExact(x), Math.toIntExact(y)));
                        if (previous != null) nb++;
                    }
                }
                return nb;
            }
        }

        @Override
        public Tile anyTile() throws DataStoreException {
            final Iterator<Tile> iterator = mpTileReference.values().iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
            throw new DataStoreException("No tiles in mosaic");
        }
    }

}
