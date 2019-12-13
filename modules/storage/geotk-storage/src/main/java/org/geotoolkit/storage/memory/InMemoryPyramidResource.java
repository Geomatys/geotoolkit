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
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.coverage.PyramidReader;
import org.geotoolkit.storage.multires.AbstractMosaic;
import org.geotoolkit.storage.multires.AbstractPyramid;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.MultiResolutionModel;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Pyramids;
import org.geotoolkit.storage.multires.Tile;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class InMemoryPyramidResource extends AbstractGridResource implements MultiResolutionResource, StoreResource, WritableGridCoverageResource {

    private final InMemoryStore store;
    private final GenericName identifier;
    private final List<Pyramid> pyramids = new ArrayList<>();
    private List<SampleDimension> dimensions;

    public InMemoryPyramidResource(final GenericName name) {
        this(null, name);
    }

    public InMemoryPyramidResource(final InMemoryStore store, final GenericName identifier) {
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
    public Collection<Pyramid> getModels() throws DataStoreException {
        return pyramids;
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
        if (template instanceof Pyramid) {
            final Pyramid p = (Pyramid) template;
            final InMemoryPyramid py = new InMemoryPyramid(UUID.randomUUID().toString(), p.getCoordinateReferenceSystem());
            Pyramids.copyStructure(p, py);
            pyramids.add(py);
            return py;
        } else {
            throw new DataStoreException("Unsupported model "+template);
        }
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        ArgumentChecks.ensureNonNull("identifier", identifier);
        final Iterator<Pyramid> it     = pyramids.iterator();
        while (it.hasNext()) {
            final Pyramid py = it.next();
            if (identifier.equalsIgnoreCase(py.getIdentifier())) {
                pyramids.remove(py);
                return;
            }
        }
        throw new DataStoreException("Identifier "+identifier+" not found in models.");
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return new PyramidReader<>(this).getGridGeometry();
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return new PyramidReader<>(this).read(domain, range);
    }

    @Override
    public void write(GridCoverage coverage, WritableGridCoverageResource.Option... options) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported.");
    }

    private final class InMemoryPyramid extends AbstractPyramid {

        private final List<InMemoryMosaic> mosaics = new ArrayList<>();

        public InMemoryPyramid(String id, CoordinateReferenceSystem crs) {
            super(id, crs);
        }

        @Override
        public Collection<? extends Mosaic> getMosaics() {
            return Collections.unmodifiableList(mosaics);
        }

        @Override
        public Mosaic createMosaic(Mosaic template) throws DataStoreException {
            final String mosaicId = UUID.randomUUID().toString();
            final InMemoryMosaic gm = new InMemoryMosaic(mosaicId,
                    this, template.getUpperLeftCorner(), template.getGridSize(), template.getTileSize(), template.getScale());
            mosaics.add(gm);
            return gm;
        }

        @Override
        public void deleteMosaic(String mosaicId) throws DataStoreException {
            for (int id = 0, len = mosaics.size(); id < len; id++) {
                if (mosaics.get(id).getIdentifier().equalsIgnoreCase(mosaicId)) {
                    mosaics.remove(id);
                    break;
                }
            }
        }

    }

    private final class InMemoryMosaic extends AbstractMosaic {

        private final Map<Point,InMemoryTile> mpTileReference = new HashMap<>();

        public InMemoryMosaic(final String id, Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize, Dimension tileSize, double scale) {
            super(id, pyramid, upperLeft, gridSize, tileSize, scale);
        }

        @Override
        public boolean isMissing(long col, long row) {
            return mpTileReference.get(new Point(Math.toIntExact(col), Math.toIntExact(row))) == null;
        }

        @Override
        public InMemoryTile getTile(long col, long row, Map hints) throws DataStoreException {
            return mpTileReference.get(new Point(Math.toIntExact(col), Math.toIntExact(row)));
        }

        public synchronized void setTile(int col, int row, InMemoryTile tile) {
            mpTileReference.put(new Point(Math.toIntExact(col), Math.toIntExact(row)), tile);
        }

        @Override
        protected boolean isWritable() throws CoverageStoreException {
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
