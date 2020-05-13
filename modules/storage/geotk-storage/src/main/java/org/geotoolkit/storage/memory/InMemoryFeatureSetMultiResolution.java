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

import java.awt.Dimension;
import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;
import org.apache.sis.internal.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.storage.feature.PyramidFeatureSetReader;
import org.geotoolkit.storage.multires.AbstractMosaic;
import org.geotoolkit.storage.multires.AbstractPyramid;
import org.geotoolkit.storage.multires.DeferredTile;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.MultiResolutionModel;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Pyramids;
import org.geotoolkit.storage.multires.Tile;
import org.geotoolkit.storage.multires.TileFormat;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class InMemoryFeatureSetMultiResolution extends AbstractFeatureSet implements MultiResolutionResource {

    private final Map<String,MultiResolutionModel> models = new HashMap<>();
    private final FeatureType type;

    public InMemoryFeatureSetMultiResolution(FeatureType type) {
        super(null);
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
    public Collection<? extends MultiResolutionModel> getModels() throws DataStoreException {
        return Collections.unmodifiableCollection(models.values());
    }

    @Override
    public MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException {
        if (template instanceof Pyramid) {
            Pyramid p = (Pyramid) template;
            String id = p.getIdentifier();
            if (id == null) {
                //create a unique id
                id = UUID.randomUUID().toString();
            } else if (models.containsKey(id)) {
                //change id to avoid overriding an existing pyramid
                id = UUID.randomUUID().toString();
            }

            final InMemoryPyramid py = new InMemoryPyramid(id, p.getCoordinateReferenceSystem());
            Pyramids.copyStructure(p, py);
            models.put(id, py);
            return py;
        } else {
            throw new DataStoreException("Unsupported model " + template);
        }
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        ArgumentChecks.ensureNonNull("identifier", identifier);
        models.remove(identifier);
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        return new PyramidFeatureSetReader(this, type).features(null, parallel);
    }

    private final class InMemoryPyramid extends AbstractPyramid {

        private final List<InMemoryMosaic> mosaics = new CopyOnWriteArrayList<>();

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

        private final Map<Point,InMemoryDeferredTile> mpTileReference = new HashMap<>();

        public InMemoryMosaic(final String id, Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize, Dimension tileSize, double scale) {
            super(id, pyramid, upperLeft, gridSize, tileSize, scale);
        }

        @Override
        public boolean isMissing(long col, long row) {
            return mpTileReference.get(new Point(Math.toIntExact(col), Math.toIntExact(row))) == null;
        }

        @Override
        public InMemoryDeferredTile getTile(long col, long row, Map hints) throws DataStoreException {
            return mpTileReference.get(new Point(Math.toIntExact(col), Math.toIntExact(row)));
        }

        public synchronized void setTile(int col, int row, InMemoryDeferredTile tile) {
            mpTileReference.put(new Point(Math.toIntExact(col), Math.toIntExact(row)), tile);
        }

        @Override
        protected boolean isWritable() throws DataStoreException {
            return true;
        }

        @Override
        protected void writeTile(Tile tile) throws DataStoreException {
            Resource r = tile;
            if (r instanceof DeferredTile) {
                r = ((DeferredTile) r).open();
            }

            if (r instanceof FeatureSet) {
                final FeatureSet imgTile = (FeatureSet) r;
                InMemoryFeatureSet imfs = new InMemoryFeatureSet(imgTile.getType());
                try (Stream<Feature> stream = imgTile.features(false)) {
                    imfs.add(stream.iterator());
                }
                final int tileX = tile.getPosition().x;
                final int tileY = tile.getPosition().y;
                setTile(tileX, tileY, new InMemoryDeferredTile(new Point(tileX, tileY), imfs));
            } else {
                throw new DataStoreException("Only FeatureSet tiles are supported.");
            }
        }

        @Override
        public void deleteTile(int tileX, int tileY) throws DataStoreException {
            setTile(tileX,tileY,null);
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