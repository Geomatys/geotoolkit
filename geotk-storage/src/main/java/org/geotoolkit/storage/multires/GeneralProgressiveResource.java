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
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.internal.storage.AbstractResource;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.Classes;
import org.geotoolkit.process.Monitor;
import org.geotoolkit.process.ProcessListener;
import org.geotoolkit.storage.coverage.IProgressiveCoverageResource;
import org.geotoolkit.storage.coverage.TileMatrixImage;
import org.geotoolkit.util.StringUtilities;
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
public class GeneralProgressiveResource extends AbstractResource implements ProgressiveResource, IProgressiveCoverageResource {

    protected final TiledResource base;
    private GenericName identifier;
    private final Map<String,ProgressiveTileMatrixSet> cachePyramids = new HashMap<>();
    protected TileGenerator generator;

    public GeneralProgressiveResource(TiledResource base, TileGenerator generator) {
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

    @Override
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
    public Collection<TileMatrixSet> getTileMatrixSets() throws DataStoreException {
        final Collection<TileMatrixSet> parentPyramids = (Collection<TileMatrixSet>) base.getTileMatrixSets();

        final List<TileMatrixSet> pyramids;
        synchronized (cachePyramids) {
            //check pyramids, we need to do this until an event system is created

            //add missing pyramids in the view
            final Set<String> keys = new HashSet<>();
            for (TileMatrixSet candidate : parentPyramids) {
                if (!cachePyramids.containsKey(candidate.getIdentifier())) {
                    cachePyramids.put(candidate.getIdentifier(), new ProgressiveTileMatrixSet(candidate));
                }
                keys.add(candidate.getIdentifier());
            }
            if (cachePyramids.size() != parentPyramids.size()) {
                //some pyramids have been deleted from parent
                cachePyramids.keySet().retainAll(keys);
            }
            pyramids = UnmodifiableArrayList.wrap(cachePyramids.values().toArray(new TileMatrixSet[0]));
        }

        return pyramids;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public TileMatrixSet createTileMatrixSet(TileMatrixSet template) throws DataStoreException {
        synchronized (cachePyramids) {
            final TileMatrixSet newParentPyramid = base.createTileMatrixSet(template);
            final ProgressiveTileMatrixSet cached = new ProgressiveTileMatrixSet(newParentPyramid);
            cachePyramids.put(cached.getIdentifier(), cached);
            return cached;
        }
    }

    @Override
    public void removeTileMatrixSet(String identifier) throws DataStoreException {
        synchronized (cachePyramids) {
            base.removeTileMatrixSet(identifier);
            cachePyramids.remove(identifier);
        }
    }

    @Override
    public void clear(Envelope env, NumberRange resolutions) throws DataStoreException {
        for (TileMatrixSet pyramid : TileMatrices.getTileMatrixSets(base)) {
            TileMatrices.clear(pyramid, env, resolutions);
        }
    }

    @Override
    public void generate(Envelope env, NumberRange resolutions, ProcessListener listener) throws DataStoreException {
        try {
            for (TileMatrixSet pyramid : TileMatrices.getTileMatrixSets(base)) {
                generator.generate(pyramid, env, resolutions, listener);
            }
        } catch (InterruptedException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public String toString() {
        final String name = Classes.getShortClassName(this);
        final List<String> elements = new ArrayList<>();
        try {
            elements.add("id : "+ getIdentifier().orElse(null));
        } catch (DataStoreException ex) {
            elements.add("id : ERROR");
        }
        elements.add("generator : "+ String.valueOf(generator));
        return StringUtilities.toStringTree(name, elements);
    }

    private final class ProgressiveTileMatrixSet implements TileMatrixSet {

        private final TileMatrixSet parent;
        private final Map<String,ProgressiveTileMatrix> cacheMosaics = new HashMap<>();

        ProgressiveTileMatrixSet(TileMatrixSet pyramid) {
            this.parent = pyramid;
        }

        @Override
        public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            return parent.getCoordinateReferenceSystem();
        }

        @Override
        public Collection<? extends TileMatrix> getTileMatrices() {
            final Collection<? extends TileMatrix> parentMosaics = parent.getTileMatrices();

            final List<TileMatrix> pmosaics;
            synchronized (cacheMosaics) {
                //check mosaics, we need to do this until an event system is created

                //add missing mosaics in the view
                final Set<String> keys = new HashSet<>();
                for (TileMatrix candidate : parentMosaics) {
                    if (!cacheMosaics.containsKey(candidate.getIdentifier())) {
                        cacheMosaics.put(candidate.getIdentifier(), new ProgressiveTileMatrix(ProgressiveTileMatrixSet.this, candidate));
                    }
                    keys.add(candidate.getIdentifier());
                }
                if (cacheMosaics.size() != parentMosaics.size()) {
                    //some mosaics have been deleted from parent
                    cacheMosaics.keySet().retainAll(keys);
                }
                pmosaics = UnmodifiableArrayList.wrap(cacheMosaics.values().toArray(new TileMatrix[0]));
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
        public TileMatrix createTileMatrix(TileMatrix template) throws DataStoreException {
            synchronized (cacheMosaics) {
                final TileMatrix newParentMosaic = parent.createTileMatrix(template);
                final ProgressiveTileMatrix cached = new ProgressiveTileMatrix(ProgressiveTileMatrixSet.this, newParentMosaic);
                cacheMosaics.put(cached.getIdentifier(), cached);
                return cached;
            }
        }

        @Override
        public void deleteTileMatrix(String mosaicId) throws DataStoreException {
            synchronized (cacheMosaics) {
                parent.deleteTileMatrix(mosaicId);
                cacheMosaics.remove(mosaicId);
            }
        }

        @Override
        public String toString(){
            return AbstractTileMatrixSet.toString(this);
        }
    }

    public final class ProgressiveTileMatrix implements TileMatrix {

        private final ProgressiveTileMatrixSet pyramid;
        private final TileMatrix base;
        private TileMatrixImage image;

        ProgressiveTileMatrix(ProgressiveTileMatrixSet pyramid, TileMatrix base) {
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
                if (!(base instanceof DefiningTileMatrix)) {
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

        public synchronized RenderedImage asImage() throws DataStoreException {
            if (image == null) {
                final GeneralProgressiveResource r = GeneralProgressiveResource.this;
                List<SampleDimension> samples = null;
                if (r instanceof GridCoverageResource) {
                    samples = ((GridCoverageResource) r).getSampleDimensions();
                }
                this.image = TileMatrixImage.create(this, null, samples);
            }
            return image;
        }

        @Override
        public String toString() {
            return AbstractTileMatrix.toString(this);
        }
    }

}
