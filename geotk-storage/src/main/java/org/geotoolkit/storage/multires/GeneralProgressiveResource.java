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
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.util.Classes;
import org.geotoolkit.process.ProcessListener;
import org.geotoolkit.storage.coverage.IProgressiveCoverageResource;
import org.geotoolkit.storage.coverage.TileMatrixImage;
import org.geotoolkit.util.StringUtilities;
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

    protected final WritableTiledResource base;
    private GenericName identifier;
    private final Map<String,ProgressiveTileMatrixSet> cachePyramids = new HashMap<>();
    protected TileGenerator generator;

    public GeneralProgressiveResource(WritableTiledResource base, TileGenerator generator) {
        super(null, false);
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
    public Collection<WritableTileMatrixSet> getTileMatrixSets() throws DataStoreException {
        final Collection<? extends WritableTileMatrixSet> parentPyramids = base.getTileMatrixSets();

        final List<WritableTileMatrixSet> pyramids;
        synchronized (cachePyramids) {
            //check pyramids, we need to do this until an event system is created

            //add missing pyramids in the view
            final Set<String> keys = new HashSet<>();
            for (WritableTileMatrixSet candidate : parentPyramids) {
                if (!cachePyramids.containsKey(candidate.getIdentifier().toString())) {
                    cachePyramids.put(candidate.getIdentifier().toString(), new ProgressiveTileMatrixSet(candidate));
                }
                keys.add(candidate.getIdentifier().toString());
            }
            if (cachePyramids.size() != parentPyramids.size()) {
                //some pyramids have been deleted from parent
                cachePyramids.keySet().retainAll(keys);
            }
            pyramids = UnmodifiableArrayList.wrap(cachePyramids.values().toArray(new WritableTileMatrixSet[0]));
        }

        return pyramids;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public WritableTileMatrixSet createTileMatrixSet(org.apache.sis.storage.tiling.TileMatrixSet template) throws DataStoreException {
        synchronized (cachePyramids) {
            final WritableTileMatrixSet newParentPyramid = base.createTileMatrixSet(template);
            final ProgressiveTileMatrixSet cached = new ProgressiveTileMatrixSet(newParentPyramid);
            cachePyramids.put(cached.getIdentifier().toString(), cached);
            return cached;
        }
    }

    @Override
    public void deleteTileMatrixSet(String identifier) throws DataStoreException {
        synchronized (cachePyramids) {
            base.deleteTileMatrixSet(identifier);
            cachePyramids.remove(identifier);
        }
    }

    @Override
    public void clear(Envelope env, NumberRange resolutions) throws DataStoreException {
        for (WritableTileMatrixSet pyramid : base.getTileMatrixSets()) {
            TileMatrices.clear(pyramid, env, resolutions);
        }
    }

    @Override
    public void generate(Envelope env, NumberRange resolutions, ProcessListener listener) throws DataStoreException {
        try {
            for (WritableTileMatrixSet pyramid : base.getTileMatrixSets()) {
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

    private final class ProgressiveTileMatrixSet implements WritableTileMatrixSet {

        private final WritableTileMatrixSet parent;
        private final Map<GenericName,ProgressiveTileMatrix> cacheMosaics = new HashMap<>();

        ProgressiveTileMatrixSet(WritableTileMatrixSet pyramid) {
            this.parent = pyramid;
        }

        @Override
        public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            return parent.getCoordinateReferenceSystem();
        }

        @Override
        public SortedMap<GenericName, ? extends WritableTileMatrix> getTileMatrices() {
            final SortedMap<GenericName, ? extends WritableTileMatrix> parentMosaics = parent.getTileMatrices();

            SortedMap<GenericName, WritableTileMatrix> pmosaics;
            synchronized (cacheMosaics) {
                //check mosaics, we need to do this until an event system is created

                //add missing mosaics in the view
                final Set<GenericName> keys = new HashSet<>();
                for (WritableTileMatrix candidate : parentMosaics.values()) {
                    if (!cacheMosaics.containsKey(candidate.getIdentifier())) {
                        cacheMosaics.put(candidate.getIdentifier(), new ProgressiveTileMatrix(ProgressiveTileMatrixSet.this, candidate));
                    }
                    keys.add(candidate.getIdentifier());
                }
                if (cacheMosaics.size() != parentMosaics.size()) {
                    //some mosaics have been deleted from parent
                    cacheMosaics.keySet().retainAll(keys);
                }
                pmosaics = new TreeMap<>(parentMosaics.comparator());
                pmosaics.putAll(cacheMosaics);
                pmosaics = Collections.unmodifiableSortedMap(pmosaics);
            }

            return pmosaics;
        }

        @Override
        public Optional<Envelope> getEnvelope() {
            return parent.getEnvelope();
        }

        @Override
        public GenericName getIdentifier() {
            return parent.getIdentifier();
        }

        @Override
        public WritableTileMatrix createTileMatrix(org.apache.sis.storage.tiling.TileMatrix template) throws DataStoreException {
            synchronized (cacheMosaics) {
                final WritableTileMatrix newParentMosaic = parent.createTileMatrix(template);
                final ProgressiveTileMatrix cached = new ProgressiveTileMatrix(ProgressiveTileMatrixSet.this, newParentMosaic);
                cacheMosaics.put(cached.getIdentifier(), cached);
                return cached;
            }
        }

        @Override
        public void deleteTileMatrix(String mosaicId) throws DataStoreException {
            synchronized (cacheMosaics) {
                parent.deleteTileMatrix(mosaicId);
                for (TileMatrix tm : cacheMosaics.values()) {
                    if (tm.getIdentifier().toString().equals(mosaicId)) {
                        cacheMosaics.remove(tm.getIdentifier());
                        break;
                    }
                }
            }
        }

        @Override
        public String toString(){
            return AbstractTileMatrixSet.toString(this);
        }
    }

    public final class ProgressiveTileMatrix implements WritableTileMatrix {

        private final ProgressiveTileMatrixSet pyramid;
        private final WritableTileMatrix base;
        private TileMatrixImage image;

        ProgressiveTileMatrix(ProgressiveTileMatrixSet pyramid, WritableTileMatrix base) {
            this.pyramid = pyramid;
            this.base = base;
        }

        @Override
        public GenericName getIdentifier() {
            return base.getIdentifier();
        }

        @Override
        public GridGeometry getTilingScheme() {
            return base.getTilingScheme();
        }

        @Override
        public Dimension getTileSize() {
            return base.getTileSize();
        }

        @Override
        public TileStatus getTileStatus(long... indices) throws DataStoreException {
            if (generator == null) {
                return base.getTileStatus(indices);
            }
            //tile will be generated
            return TileStatus.EXISTS;
        }

        @Override
        public Optional<Tile> getTile(long... indices) throws DataStoreException {
            Tile tile = base.getTile(indices).orElse(null);
            if (tile == null && generator != null) {
                //generate tile
                tile = generator.generateTile(pyramid, base, indices);
                if (!(base instanceof DefiningTileMatrix)) {
                    base.writeTiles(Stream.of(tile));
                    tile = base.getTile(indices).orElse(null);
                }
            }
            return Optional.ofNullable(tile);
        }

        @Override
        public void writeTiles(Stream<Tile> tiles) throws DataStoreException {
            base.writeTiles(tiles);
        }

        @Override
        public long deleteTiles(GridExtent indicesRanges) throws DataStoreException {
            return base.deleteTiles(indicesRanges);
        }

        @Override
        public Tile anyTile() throws DataStoreException {
            try {
                return base.anyTile();
            } catch (DataStoreException ex) {
                //may be empty
            }
            return getTile(0, 0).orElse(null);
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
