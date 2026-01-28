/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.TileMatrixSet;
import org.apache.sis.storage.tiling.TileStatus;
import static org.apache.sis.storage.tiling.TileStatus.OUTSIDE_EXTENT;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.storage.multires.ScaleSortedMap;
import org.geotoolkit.storage.multires.TileInError;
import org.geotoolkit.storage.multires.TileMatrices;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * Group several TileMatrixSet as one.
 * TileMatrix and Tile will be returned by priority order.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class VirtualTileMatrixSet implements TileMatrixSet {

    private final GenericName name;
    private final List<TileMatrixSet> tilematrixSets;
    private final CoordinateReferenceSystem crs;
    private final Optional<Envelope> envelope;
    private final SortedMap<GenericName, ? extends TileMatrix> matrices;

    public VirtualTileMatrixSet(GenericName name, List<TileMatrixSet> tilematrixSets) {
        this.name = name;
        this.tilematrixSets = tilematrixSets;

        CoordinateReferenceSystem crs = null;
        GeneralEnvelope all = null;
        final List<TileMatrix> matrices = new ArrayList<>();
        for (TileMatrixSet tms : tilematrixSets) {
            final Optional<Envelope> opt = tms.getEnvelope();
            if (opt.isPresent()) {
                Envelope env = opt.get();
                if (all == null) {
                    crs = env.getCoordinateReferenceSystem();
                    all = new GeneralEnvelope(env);
                } else {
                    if (!CRS.equivalent(crs, env.getCoordinateReferenceSystem())) {
                        throw new IllegalArgumentException("Tile matrix sets have different coordinate reference systems.");
                    }
                    all.add(env);
                }
            }

            matrices.addAll(tms.getTileMatrices().values());
        }
        this.crs = crs;
        this.envelope = Optional.ofNullable(all);
        this.matrices = regroup(matrices);
    }

    @Override
    public GenericName getIdentifier() {
        return name;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public Optional<Envelope> getEnvelope() {
        return envelope;
    }

    @Override
    public SortedMap<GenericName, ? extends TileMatrix> getTileMatrices() {
        return matrices;
    }

    private SortedMap<GenericName, ? extends TileMatrix> regroup(List<TileMatrix> matrices) {
        //we want sorting by higest resolution first, so LOD 0 is the highest level in the set
        final Map<Double, List<TileMatrix>> sorted = new TreeMap<>(Comparator.reverseOrder());
        for (TileMatrix tm : matrices) {
            final double res = tm.getResolution()[0];
            List<TileMatrix> lst = sorted.get(res);
            if (lst == null) {
               lst = new ArrayList<>();
               sorted.put(res, lst);
            }

            if (lst.isEmpty()) {
                //first tile matrix
                lst.add(tm);
            } else {
                GridGeometry baseTilingScheme = lst.get(0).getTilingScheme();
                GridGeometry candidateTilingScheme = tm.getTilingScheme();
                if (!baseTilingScheme.equals(candidateTilingScheme, ComparisonMode.IGNORE_METADATA)) {
                    throw new IllegalArgumentException("Two tile matrices of same scale have different tiling scheme.");
                }
                lst.add(tm);
            }
        }

        final ScaleSortedMap<TileMatrix> sm = new ScaleSortedMap<>();
        int lod = 0;
        for (List<TileMatrix> tm : sorted.values()) {
            final TileMatrix result = new CombineTileMatrix(Names.createLocalName(null, null, ""+lod), tm);
            sm.insertByScale(result);
            lod++;
        }
        return sm;
    }

    private static class CombineTileMatrix implements TileMatrix {

        private final GenericName identifier;
        private final List<TileMatrix> matrices;

        public CombineTileMatrix(GenericName identifier, List<TileMatrix> matrices) {
            this.identifier = identifier;
            this.matrices = matrices;
        }

        @Override
        public GenericName getIdentifier() {
            return identifier;
        }

        @Override
        public double[] getResolution() {
            //all matrices have the same gridGeometry
            return matrices.get(0).getResolution();
        }

        @Override
        public GridGeometry getTilingScheme() {
            //all matrices have the same gridGeometry
            return matrices.get(0).getTilingScheme();
        }

        @Override
        public TileStatus getTileStatus(long... indices) throws DataStoreException {
            TileStatus status = TileStatus.MISSING;
            for (TileMatrix matrix : matrices) {
                final TileStatus cdt = matrix.getTileStatus(indices);
                switch (cdt) {
                    case EXISTS: return cdt;
                    case OUTSIDE_EXTENT : return cdt;
                    case IN_ERROR : status = cdt;
                    default: //do nothing
                }
            }
            return status;
        }

        @Override
        public Optional<Tile> getTile(long... indices) throws DataStoreException {
            for (TileMatrix matrix : matrices) {
                Optional<Tile> tile = matrix.getTile(indices);
                if (tile.isPresent()) return tile;
            }
            return Optional.empty();
        }

        @Override
        public Stream<Tile> getTiles(GridExtent ge, boolean parallel) throws DataStoreException {
            if (ge == null) ge = getTilingScheme().getExtent();
            Stream<long[]> stream = TileMatrices.pointStream(ge);
            if (parallel) stream = stream.parallel();
            return stream.map(new Function<long[], Tile>() {
                @Override
                public Tile apply(long[] t) {
                    try {
                        Optional<Tile> tile = getTile(t);
                        return tile.orElse(null);
                    } catch (DataStoreException ex) {
                        return TileInError.create(t, ex);
                    }
                }
            }).filter(Objects::nonNull);
        }

    }

}
