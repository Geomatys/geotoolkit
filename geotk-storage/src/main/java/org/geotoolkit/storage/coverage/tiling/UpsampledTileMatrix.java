/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridClippingMode;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.util.privy.AbstractIterator;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.TileMatrixSet;
import org.apache.sis.storage.tiling.TileStatus;
import org.geotoolkit.storage.coverage.CoverageResourceTile;
import org.geotoolkit.storage.coverage.mosaic.AggregatedCoverageResource;
import org.geotoolkit.storage.coverage.mosaic.BitSetND;
import org.geotoolkit.storage.multires.TileInError;
import org.geotoolkit.storage.multires.TileMatrices;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class UpsampledTileMatrix implements TileMatrix {

    protected final TileMatrixSet tms;
    protected final int[] tileSize;
    protected final List<SampleDimension> sampleDimensions;
    protected final TileMatrix base;

    public static UpsampledTileMatrix create(TileMatrixSet tms, TileMatrix base, List<SampleDimension> sampleDimensions, int[] tileSize) {
        if (base instanceof org.geotoolkit.storage.multires.TileMatrix gtm) {
            return new GeotkUpsampledTileMatrix(tms, gtm, sampleDimensions, tileSize);
        } else {
            return new UpsampledTileMatrix(tms, base, sampleDimensions, tileSize);
        }
    }

    private UpsampledTileMatrix(TileMatrixSet tms, TileMatrix base, List<SampleDimension> sampleDimensions, int[] tileSize) {
        this.tms = tms;
        this.base = base;
        this.sampleDimensions = sampleDimensions;
        this.tileSize = tileSize;
    }

    @Override
    public GenericName getIdentifier() {
        return base.getIdentifier();
    }

    @Override
    public double[] getResolution() {
        return base.getResolution();
    }

    @Override
    public GridGeometry getTilingScheme() {
        return base.getTilingScheme();
    }

    @Override
    public TileStatus getTileStatus(long... indices) throws DataStoreException {
        TileStatus tileStatus = base.getTileStatus(indices);
        if (TileStatus.MISSING.equals(tileStatus)) {
            return getParentTileStatus(indices);
        }
        return tileStatus;
    }

    @Override
    public Optional<Tile> getTile(long... indices) throws DataStoreException {
        final Optional<Tile> opt = base.getTile(indices);
        if (opt.isEmpty()) {
            if (TileStatus.EXISTS.equals(getParentTileStatus(indices))) {
                return createUsingParentTiles(indices);
            }
        }
        return opt;
    }

    @Override
    public Stream<Tile> getTiles(GridExtent indicesRanges, boolean parallel) throws DataStoreException {

        //original tile stream
        final Stream<Tile> baseStream = base.getTiles(indicesRanges, parallel);

        if (indicesRanges == null) {
            indicesRanges = getTilingScheme().getExtent();
        }

        //keep track of available tiles
        final CountDownLatch latch = new CountDownLatch(1);
        final BitSetND bset = new BitSetND(indicesRanges);
        final Stream<Tile> stream = baseStream.map((Tile t) -> {
            bset.set(t.getIndices());
            return t;
        }).onClose(latch::countDown);

        //create a stream of all missing tiles positions
        final long[] position = indicesRanges.getLow().getCoordinateValues();
        final Iterator<long[]> ite = new AbstractIterator<long[]>() {
            @Override
            public synchronized boolean hasNext() {
                if (next == null) {
                    next = bset.nextClear(position);
                    position[0]++;
                }
                return next != null;
            }
        };
        //create stream of all upscaled tiles
        final Spliterator<long[]> split = Spliterators.spliterator(ite, Long.MAX_VALUE, Spliterator.DISTINCT);
        final Stream<Tile> upscaledStream = StreamSupport.stream(split, parallel).map((long[] t) -> {
            try {
                if (latch.getCount() != 0) {
                    throw new DataStoreException("base stream should have been fully processed");
                }
                return getTile(t).orElse(null);
            } catch (DataStoreException ex) {
                return TileInError.create(t, ex);
            }
        }).filter(Objects::nonNull);

        /*
        Ensure the first stream is fully processed before we start the upscaled tile stream.
        */
        return Stream.of(new Object()).flatMap(new Function<Object,Stream<Stream<Tile>>>() {
            @Override
            public Stream<Stream<Tile>> apply(Object t) {
                return Stream.of(stream, upscaledStream);
            }
        }).flatMap((Stream<Tile> t1) -> t1);
    }

    /**
     * Find if a parent tile exist.
     */
    private TileStatus getParentTileStatus(long... indices) throws DataStoreException {
        final SortedMap<GenericName, ? extends TileMatrix> tileMatrices = tms.getTileMatrices();
        final List<GenericName> parents = new ArrayList<>(tileMatrices.headMap(base.getIdentifier()).keySet());
        if (parents.isEmpty()) {
            //no parent
            return TileStatus.MISSING;
        }
        //reverse order, start by the closest level
        //search in parent matrices
        for (int i = parents.size() - 1; i > -1; i--) {
            final TileMatrix parent = tileMatrices.get(parents.get(i));
            final GridExtent parentTiles = parentTiles(parent, indices);
            try (Stream<long[]> stream = TileMatrices.pointStream(parentTiles)) {
                final Iterator<long[]> iterator = stream.iterator();
                while (iterator.hasNext()) {
                    final TileStatus status = parent.getTileStatus(iterator.next());
                    if (TileStatus.EXISTS.equals(status)) {
                        return status;
                    }
                }
            }
        }

        return TileStatus.MISSING;
    }

    /**
     * Create tile using higher level tiles.
     */
    private Optional<Tile> createUsingParentTiles(long... indices) throws DataStoreException {
        final SortedMap<GenericName, ? extends TileMatrix> tileMatrices = tms.getTileMatrices();
        final List<GenericName> parents = new ArrayList<>(tileMatrices.headMap(base.getIdentifier()).keySet());
        if (parents.isEmpty()) {
            //no parent
            return Optional.empty();
        }

        //reverse order, start by the closest level
        final AggregatedCoverageResource acr = new AggregatedCoverageResource();
        acr.setMode(AggregatedCoverageResource.Mode.ORDER);
        for (int i = parents.size() - 1; i > -1; i--) {
            final TileMatrix parent = tileMatrices.get(parents.get(i));
            final TileMatrixCoverageResource res = new TileMatrixCoverageResource(parent, tileSize, sampleDimensions);
            acr.add(res);
        }

        final GridExtent extent = new GridExtent(null, indices, indices, true);
        final GridGeometry tileGeom = base.getTilingScheme().derive().subgrid(extent).build().upsample(tileSize);
        GridCoverage coverage = acr.read(tileGeom);
        try {
            //ensure exact tile match
            coverage = new GridCoverageProcessor().resample(coverage, tileGeom);
        } catch (TransformException ex) {
            throw new DataStoreException("Failed to create tile from upper levels", ex);
        }
        final Tile computedTile = new CoverageResourceTile(indices, coverage);
        return Optional.of(computedTile);
    }

    /**
     * Get tiles extent in the parent matrix which will be needed to compute
     * this tile.
     */
    private GridExtent parentTiles(TileMatrix parent, long... indices) {
        final GridExtent extent = new GridExtent(null, indices, indices, true);
        final GridGeometry tileGeom = base.getTilingScheme().derive().subgrid(extent).build();
        return parent.getTilingScheme().derive().clipping(GridClippingMode.STRICT).rounding(GridRoundingMode.ENCLOSING).subgrid(tileGeom).getIntersection();
    }

    /**
     * TODO temporary hack until we find a solution to remove the anyTile function.
     */
    private static class GeotkUpsampledTileMatrix extends UpsampledTileMatrix implements org.geotoolkit.storage.multires.TileMatrix {

        public GeotkUpsampledTileMatrix(TileMatrixSet tms, TileMatrix base, List<SampleDimension> sampleDimensions, int[] tileSize) {
            super(tms,base, sampleDimensions, tileSize);
        }

        @Override
        public int[] getTileSize() {
            return ((org.geotoolkit.storage.multires.TileMatrix) base).getTileSize();
        }

        @Override
        public Tile anyTile() throws DataStoreException {
            return ((org.geotoolkit.storage.multires.TileMatrix) base).anyTile();
        }
    }
}
