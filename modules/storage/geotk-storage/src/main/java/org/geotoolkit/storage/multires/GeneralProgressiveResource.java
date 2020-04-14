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

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.internal.storage.AbstractResource;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.process.Monitor;
import org.geotoolkit.process.ProcessListener;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * Note : if base resource contains DefiningMosaic instances, the tile will be generated but not written.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeneralProgressiveResource extends AbstractResource implements ProgressiveResource {

    protected final MultiResolutionResource base;
    private GenericName identifier;
    private final Map<String,ProgressivePyramid> cacheMap = new HashMap<>();
    protected TileGenerator generator;

    public GeneralProgressiveResource(MultiResolutionResource base, TileGenerator generator) throws DataStoreException {
        super(null);
        this.base = base;
        this.generator = generator;
    }

    public void setIdentifier(GenericName identifier) {
        this.identifier = identifier;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        if (identifier != null) {
            return Optional.ofNullable(identifier);
        }
        return base.getIdentifier();
    }

    public TileGenerator getGenerator() {
        return generator;
    }

    @Override
    public void setGenerator(TileGenerator generator) {
        this.generator = generator;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        final Collection<Pyramid> parentPyramids = (Collection<Pyramid>) base.getModels();

        final List<Pyramid> pyramids;
        synchronized (cacheMap) {
            //check pyramids, we need to do this until an event system is created

            //add missing pyramids in the view
            final Set<String> keys = new HashSet<>();
            for (Pyramid candidate : parentPyramids) {
                if (!cacheMap.containsKey(candidate.getIdentifier())) {
                    cacheMap.put(candidate.getIdentifier(), new ProgressivePyramid(candidate));
                }
                keys.add(candidate.getIdentifier());
            }
            if (cacheMap.size() != parentPyramids.size()) {
                //some pyramids have been deleted from parent
                cacheMap.keySet().retainAll(keys);
            }
            pyramids = UnmodifiableArrayList.wrap(cacheMap.values().toArray(new Pyramid[0]));
        }

        return pyramids;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException {
        synchronized (cacheMap) {
            final MultiResolutionModel newParentPyramid = base.createModel(template);
            final ProgressivePyramid cached = new ProgressivePyramid((Pyramid) newParentPyramid);
            cacheMap.put(cached.getIdentifier(), cached);
            return cached;
        }
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        synchronized (cacheMap) {
            base.removeModel(identifier);
            cacheMap.remove(identifier);
        }
    }

    @Override
    public void clear(Envelope env, NumberRange resolutions) throws DataStoreException {
        for (Pyramid pyramid : Pyramids.getPyramids(base)) {
            Pyramids.clear(pyramid, env, resolutions);
        }
    }

    @Override
    public void generate(Envelope env, NumberRange resolutions, ProcessListener listener) throws DataStoreException {
        try {
            for (Pyramid pyramid : Pyramids.getPyramids(base)) {
                generator.generate(pyramid, env, resolutions, listener);
            }
        } catch (InterruptedException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    private final class ProgressivePyramid implements Pyramid {

        final Pyramid parent;

        ProgressivePyramid(Pyramid pyramid) {
            this.parent = pyramid;
        }

        @Override
        public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            return parent.getCoordinateReferenceSystem();
        }

        @Override
        public Collection<? extends Mosaic> getMosaics() {
            final Collection<? extends Mosaic> mosaics = parent.getMosaics();
            final List<Mosaic> pmosaics = new ArrayList<>(mosaics.size());
            for (Mosaic m : mosaics) {
                pmosaics.add(new ProgressiveMosaic(ProgressivePyramid.this, m));
            }
            return pmosaics;
        }

        @Override
        public double[] getScales() {
            return parent.getScales();
        }

        @Override
        public Envelope getEnvelope() {
            return parent.getEnvelope();
        }

        @Override
        public String getIdentifier() {
            return parent.getIdentifier();
        }

        @Override
        public String getFormat() {
            return parent.getFormat();
        }

        @Override
        public Mosaic createMosaic(Mosaic template) throws DataStoreException {
            return new ProgressiveMosaic(ProgressivePyramid.this,  parent.createMosaic(template));
        }

        @Override
        public void deleteMosaic(String mosaicId) throws DataStoreException {
            parent.deleteMosaic(mosaicId);
        }

    }

    final class ProgressiveMosaic implements Mosaic {

        private final ProgressivePyramid pyramid;
        private final Mosaic base;

        ProgressiveMosaic(ProgressivePyramid pyramid, Mosaic base) {
            this.pyramid = pyramid;
            this.base = base;
        }

        @Override
        public String getIdentifier() {
            return base.getIdentifier();
        }

        @Override
        public DirectPosition getUpperLeftCorner() {
            return base.getUpperLeftCorner();
        }

        @Override
        public Dimension getGridSize() {
            return base.getGridSize();
        }

        @Override
        public double getScale() {
            return base.getScale();
        }

        @Override
        public Dimension getTileSize() {
            return base.getTileSize();
        }

        @Override
        public Envelope getEnvelope() {
            return base.getEnvelope();
        }

        @Override
        public boolean isMissing(long col, long row) throws PointOutsideCoverageException {
            if (generator == null) {
                return base.isMissing(col, row);
            }
            //tile will be generated
            return false;
        }

        @Override
        public Tile getTile(long col, long row, Map hints) throws DataStoreException {
            Tile tile = base.getTile(col, row);
            if (tile == null && generator != null) {
                //generate tile
                tile = generator.generateTile(pyramid, base, new Point(Math.toIntExact(col), Math.toIntExact(row)));
                if (!(base instanceof DefiningMosaic)) {
                    base.writeTiles(Stream.of(tile), null);
                    tile = base.getTile(col, row);
                }
            }
            return tile;
        }

        /**
         * Return the full extent, tiles will be generated.
         * @return
         */
        @Override
        public GridExtent getDataExtent() {
            final Dimension tileSize = getTileSize();
            final Dimension gridSize = getGridSize();
            return new GridExtent(
                    ((long) gridSize.width) * tileSize.width,
                    ((long) gridSize.height) * tileSize.height);
        }

        @Override
        public void writeTiles(Stream<Tile> tiles, Monitor monitor) throws DataStoreException {
            base.writeTiles(tiles, monitor);
        }

        @Override
        public void deleteTile(int tileX, int tileY) throws DataStoreException {
            base.deleteTile(tileX, tileY);
        }

        @Override
        public Tile anyTile() throws DataStoreException {
            try {
                return base.anyTile();
            } catch (DataStoreException ex) {
                //may be empty
            }
            return getTile(0, 0, null);
        }

    }

}
