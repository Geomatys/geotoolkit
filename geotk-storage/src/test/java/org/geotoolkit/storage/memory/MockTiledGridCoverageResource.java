/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
import java.awt.image.RenderedImage;
import java.util.ArrayList;
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
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.storage.coverage.DefaultImageTile;
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
 * Mock pyramid loggin called operations.
 *
 * @author Johann Sorel (Geomatys)
 */
final class MockTiledGridCoverageResource extends AbstractGridCoverageResource
        implements WritableTiledResource, StoreResource, WritableGridCoverageResource
{
    public static enum EventType {
        TILE_MATRIX_SET_CREATED,
        TILE_MATRIX_SET_REMOVED,
        TILE_MATRIX_CREATED,
        TILE_MATRIX_REMOVED,
        TILE_GET,
        TILE_SET
    }

    public static final class MockEvent {
        public EventType type;
        public Object data;

        public MockEvent(EventType type, Object data) {
            this.type = type;
            this.data = data;
        }
    }

    private final InMemoryStore store;
    private final GenericName identifier;
    private final List<WritableTileMatrixSet> tileMatrixSets = new CopyOnWriteArrayList<>();
    private List<SampleDimension> dimensions;

    /**
     * Operation which occured in the pyramid.
     */
    public final List<MockEvent> localEvents = new ArrayList<>();

    public MockTiledGridCoverageResource(final GenericName name) {
        this(null, name);
    }

    public MockTiledGridCoverageResource(final InMemoryStore store, final GenericName identifier) {
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
        final InMemoryTileMatrixSet py = new InMemoryTileMatrixSet(NamesExt.createRandomUUID(), template.getCoordinateReferenceSystem());
        py.setBuildPhase(true);
        localEvents.add(new MockEvent(EventType.TILE_MATRIX_SET_CREATED, py));
        TileMatrices.copyStructure(template, py);
        py.setBuildPhase(false);
        tileMatrixSets.add(py);
        listeners.fire(StoreEvent.class, new ModelEvent(this));
        return py;
    }

    @Override
    public void deleteTileMatrixSet(String identifier) throws DataStoreException {
        ArgumentChecks.ensureNonNull("identifier", identifier);
        final Iterator<WritableTileMatrixSet> it = tileMatrixSets.iterator();
        while (it.hasNext()) {
            final TileMatrixSet tms = it.next();
            if (identifier.equalsIgnoreCase(tms.getIdentifier().toString())) {
                tileMatrixSets.remove(tms);
                localEvents.add(new MockEvent(EventType.TILE_MATRIX_SET_REMOVED, tms));
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
            localEvents.add(new MockEvent(EventType.TILE_MATRIX_CREATED, gm));
            if (!buildPhase) {
                //we are creating object, dont send an event until we are finished.
                listeners.fire(StoreEvent.class, new ModelEvent(MockTiledGridCoverageResource.this));
            }
            return gm;
        }

        @Override
        public void deleteTileMatrix(String mosaicId) throws DataStoreException {
            for (TileMatrix tm : tileMatrices.values()) {
                if (tm.getIdentifier().toString().equalsIgnoreCase(mosaicId)) {
                    tileMatrices.remove(tm.getIdentifier());
                    localEvents.add(new MockEvent(EventType.TILE_MATRIX_REMOVED, tm));
                    if (!buildPhase) {
                        listeners.fire(StoreEvent.class, new ModelEvent(MockTiledGridCoverageResource.this));
                    }
                    break;
                }
            }
        }
    }

    private final class InMemoryTileMatrix extends AbstractTileMatrix implements WritableTileMatrix {

        private final Map<Point,InMemoryTile> mpTileReference = new HashMap<>();
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
        public Optional<Tile> getTile(long... indices) throws DataStoreException {
            InMemoryTile imt = mpTileReference.get(new Point(Math.toIntExact(indices[0]), Math.toIntExact(indices[1])));
            localEvents.add(new MockEvent(EventType.TILE_GET, imt));
            return Optional.ofNullable(imt);
        }

        public synchronized void setTile(long col, long row, InMemoryTile tile) {
            mpTileReference.put(new Point(Math.toIntExact(col), Math.toIntExact(row)), tile);
            localEvents.add(new MockEvent(EventType.TILE_SET, tile));
            if (!buildPhase) {
                //we are creating object, dont send an event until we are finished.
                listeners.fire(StoreEvent.class, new ContentEvent(MockTiledGridCoverageResource.this));
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
            final Resource resource = tile.getResource();
            if (resource instanceof GridCoverageResource) {
                final GridCoverageResource imgTile = (GridCoverageResource) resource;
                RenderedImage image = imgTile.read(null).render(null);

                final int[] tileSize = getTileSize();
                if (tileSize[0] < image.getWidth() || tileSize[1] < image.getHeight()) {
                    throw new IllegalArgumentException("Uncorrect image size ["+image.getWidth()+","+image.getHeight()+"] expecting size ["+tileSize[0]+","+tileSize[1]+"]");
                }
                final long tileX = tile.getIndices()[0];
                final long tileY = tile.getIndices()[1];
                setTile(tileX, tileY, new InMemoryTile(this, image, 0, tile.getIndices()));
            } else {
                throw new DataStoreException("Only ImageTile are supported.");
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
                        InMemoryTile previous = mpTileReference.remove(new Point(Math.toIntExact(x), Math.toIntExact(y)));
                        if (previous != null) nb++;
                    }
                }
                return nb;
            }
        }

        @Override
        public Tile anyTile() throws DataStoreException {
            final Iterator<InMemoryTile> iterator = mpTileReference.values().iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
            throw new DataStoreException("No tiles in mosaic");
        }
    }

    private final class InMemoryTile extends DefaultImageTile {

        public InMemoryTile(TileMatrix matrix, RenderedImage input, int imageIndex, long[] position) {
            super(matrix, IImageReader.IISpi.INSTANCE, input, imageIndex, position);
        }

        @Override
        public RenderedImage getInput() {
            return (RenderedImage) super.getInput();
        }
    }
}
