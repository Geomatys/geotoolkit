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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.coverage.TileMatrixSetCoverageReader;
import org.geotoolkit.storage.event.ContentEvent;
import org.geotoolkit.storage.event.ModelEvent;
import org.geotoolkit.storage.multires.AbstractTileMatrix;
import org.geotoolkit.storage.multires.AbstractTileMatrixSet;
import org.geotoolkit.storage.multires.MultiResolutionModel;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Tile;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * Mock pyramid loggin called operations.
 *
 * @author Johann Sorel (Geomatys)
 */
final class MockPyramidResource extends AbstractGridResource implements MultiResolutionResource, StoreResource, WritableGridCoverageResource {

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
    private final List<TileMatrixSet> tileMatrixSets = new CopyOnWriteArrayList<>();
    private List<SampleDimension> dimensions;

    /**
     * Operation which occured in the pyramid.
     */
    public final List<MockEvent> localEvents = new ArrayList<>();

    public MockPyramidResource(final GenericName name) {
        this(null, name);
    }

    public MockPyramidResource(final InMemoryStore store, final GenericName identifier) {
        super(null);
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
    public Collection<TileMatrixSet> getModels() throws DataStoreException {
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
    public MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException {
        if (template instanceof TileMatrixSet) {
            final TileMatrixSet p = (TileMatrixSet) template;
            final InMemoryTileMatrixSet py = new InMemoryTileMatrixSet(UUID.randomUUID().toString(), p.getCoordinateReferenceSystem());
            py.setBuildPhase(true);
            localEvents.add(new MockEvent(EventType.TILE_MATRIX_SET_CREATED, py));
            TileMatrices.copyStructure(p, py);
            py.setBuildPhase(false);
            tileMatrixSets.add(py);
            fire(new ModelEvent(this), StoreEvent.class);
            return py;
        } else {
            throw new DataStoreException("Unsupported model "+template);
        }
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        ArgumentChecks.ensureNonNull("identifier", identifier);
        final Iterator<TileMatrixSet> it     = tileMatrixSets.iterator();
        while (it.hasNext()) {
            final TileMatrixSet tms = it.next();
            if (identifier.equalsIgnoreCase(tms.getIdentifier())) {
                tileMatrixSets.remove(tms);
                localEvents.add(new MockEvent(EventType.TILE_MATRIX_SET_REMOVED, tms));
                fire(new ModelEvent(this), StoreEvent.class);
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

    private final class InMemoryTileMatrixSet extends AbstractTileMatrixSet {

        private final List<InMemoryTileMatrix> tileMatrices = new CopyOnWriteArrayList<>();
        private boolean buildPhase = false;

        public InMemoryTileMatrixSet(String id, CoordinateReferenceSystem crs) {
            super(id, crs);
        }

        @Override
        public Collection<? extends TileMatrix> getTileMatrices() {
            return Collections.unmodifiableList(tileMatrices);
        }

        private void setBuildPhase(boolean buildPhase) {
            this.buildPhase = buildPhase;
            for (InMemoryTileMatrix it : tileMatrices) {
                it.setBuildPhase(buildPhase);
            }
        }

        @Override
        public TileMatrix createTileMatrix(TileMatrix template) throws DataStoreException {
            final String mosaicId = UUID.randomUUID().toString();
            final InMemoryTileMatrix gm = new InMemoryTileMatrix(mosaicId,
                    this, template.getUpperLeftCorner(), template.getGridSize(), template.getTileSize(), template.getScale());
            tileMatrices.add(gm);
            localEvents.add(new MockEvent(EventType.TILE_MATRIX_CREATED, gm));
            if (!buildPhase) {
                //we are creating object, dont send an event until we are finished.
                MockPyramidResource.this.fire(new ModelEvent(MockPyramidResource.this), StoreEvent.class);
            }
            return gm;
        }

        @Override
        public void deleteTileMatrix(String mosaicId) throws DataStoreException {
            for (int id = 0, len = tileMatrices.size(); id < len; id++) {
                final InMemoryTileMatrix gm = tileMatrices.get(id);
                if (gm.getIdentifier().equalsIgnoreCase(mosaicId)) {
                    tileMatrices.remove(id);
                    localEvents.add(new MockEvent(EventType.TILE_MATRIX_REMOVED, gm));
                    if (!buildPhase) {
                        MockPyramidResource.this.fire(new ModelEvent(MockPyramidResource.this), StoreEvent.class);
                    }
                    break;
                }
            }
        }
    }

    private final class InMemoryTileMatrix extends AbstractTileMatrix {

        private final Map<Point,InMemoryTile> mpTileReference = new HashMap<>();
        private boolean buildPhase = false;

        public InMemoryTileMatrix(final String id, TileMatrixSet pyramid, DirectPosition upperLeft, Dimension gridSize, Dimension tileSize, double scale) {
            super(id, pyramid, upperLeft, gridSize, tileSize, scale);
        }

        private void setBuildPhase(boolean buildPhase) {
            this.buildPhase = buildPhase;
        }

        @Override
        public boolean isMissing(long col, long row) {
            return mpTileReference.get(new Point(Math.toIntExact(col), Math.toIntExact(row))) == null;
        }

        @Override
        public InMemoryTile getTile(long col, long row, Map hints) throws DataStoreException {
            InMemoryTile imt = mpTileReference.get(new Point(Math.toIntExact(col), Math.toIntExact(row)));
            localEvents.add(new MockEvent(EventType.TILE_GET, imt));
            return imt;
        }

        public synchronized void setTile(int col, int row, InMemoryTile tile) {
            mpTileReference.put(new Point(Math.toIntExact(col), Math.toIntExact(row)), tile);
            localEvents.add(new MockEvent(EventType.TILE_SET, tile));
            if (!buildPhase) {
                //we are creating object, dont send an event until we are finished.
                MockPyramidResource.this.fire(new ContentEvent(MockPyramidResource.this), StoreEvent.class);
            }
        }

        @Override
        protected boolean isWritable() throws DataStoreException {
            return true;
        }

        @Override
        protected void writeTile(Tile tile) throws DataStoreException {
            if (tile instanceof ImageTile) {
                final ImageTile imgTile = (ImageTile) tile;
                try {
                    RenderedImage image = imgTile.getImage();

                    final Dimension tileSize = getTileSize();
                    if (tileSize.width < image.getWidth() || tileSize.height < image.getHeight()) {
                        throw new IllegalArgumentException("Uncorrect image size ["+image.getWidth()+","+image.getHeight()+"] expecting size ["+tileSize.width+","+tileSize.height+"]");
                    }
                    final int tileX = imgTile.getPosition().x;
                    final int tileY = imgTile.getPosition().y;
                    setTile(tileX, tileY, new InMemoryTile(image, 0, new Point(tileX, tileY)));
                } catch (IOException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            } else {
                throw new DataStoreException("Only ImageTile are supported.");
            }
        }

        @Override
        public void deleteTile(int tileX, int tileY) throws DataStoreException {
            setTile(tileX,tileY,null);
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

        public InMemoryTile(RenderedImage input, int imageIndex, Point position) {
            super(IImageReader.IISpi.INSTANCE, input, imageIndex, position);
        }

        @Override
        public RenderedImage getInput() {
            return (RenderedImage) super.getInput();
        }
    }

}
