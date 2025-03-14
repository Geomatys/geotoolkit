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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.util.privy.UnmodifiableArrayList;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.TileMatrixSet;
import org.apache.sis.storage.tiling.TiledResource;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.storage.coverage.finder.DefaultCoverageFinder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * View a tile matrix set composed of GridCoverageResource tiles as a continous multi-resolution GridCoverageResource.
 *
 * If a matrix contain holes, tiles from higher level matrices may be used to fill them.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class TileMatrixSetCoverageResource extends AbstractGridCoverageResource implements TiledResource {

    private final GenericName identifier;
    private final List<TileMatrixSet> sets = new ArrayList<>();
    private final int[] tileSize;
    private final List<SampleDimension> sampleDimensions;
    private boolean sparse = false;

    public TileMatrixSetCoverageResource(GenericName identifier, Collection<? extends TileMatrixSet> tms, int[] tileSize, List<SampleDimension> sampleDimensions) {
        super(null, false);
        ArgumentChecks.ensureNonNull("tms", tms);
        ArgumentChecks.ensureNonNull("tileSize", tileSize);
        ArgumentChecks.ensureNonNull("sampleDimensions", sampleDimensions);
        this.identifier = identifier;
        this.sets.addAll(tms);
        this.tileSize = tileSize;
        this.sampleDimensions = UnmodifiableArrayList.wrap(sampleDimensions.toArray(SampleDimension[]::new));
    }

    /**
     * If set to true, upper lods will be used to fill holes in lower level matrices
     * when creating coverages.
     * @param sparse set to True if tile matrix set has irregular filling.
     */
    public void setSparse(boolean sparse) {
        this.sparse = sparse;
    }

    /**
     * @return true if tile matrix set has irregular filling which requires upper lods to be tested
     *        when creating coverages.
     */
    public boolean isSparse() {
        return sparse;
    }

    private TileMatrixCoverageResource getTileMatrixResource(TileMatrixSet tms, GenericName name) {
        TileMatrix tm = tms.getTileMatrices().get(name);
        if (sparse && !name.equals(tms.getTileMatrices().firstKey())) {
            //use upscaling for all matrices not at the top level
            tm = UpsampledTileMatrix.create(tms, tm, sampleDimensions, tileSize);
        }
        return new TileMatrixCoverageResource(tm, tileSize, sampleDimensions);
    }

    /**
     * @return GridGeometry of the default TileMatrixSet.
     */
    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        if (sets.isEmpty()) return GridGeometry.UNDEFINED;
        return getGridGeometry(sets.get(0));
    }

    /**
     * Get TileMatrixSet GridGeometry.
     */
    private GridGeometry getGridGeometry(TileMatrixSet tms) throws DataStoreException {
        final SortedMap<GenericName, ? extends TileMatrix> matrices = tms.getTileMatrices();
        if (sets.isEmpty()) return GridGeometry.UNDEFINED;
        return getTileMatrixResource(tms, matrices.lastKey()).getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return sampleDimensions;
    }

    @Override
    public Collection<? extends TileMatrixSet> getTileMatrixSets() throws DataStoreException {
        return Collections.unmodifiableList(sets);
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... ints) throws DataStoreException {

        final TileMatrixSet tms;
        if (domain != null && domain.isDefined(GridGeometry.CRS)) {
            CoordinateReferenceSystem crs = domain.getCoordinateReferenceSystem();
            try {
                 tms = new DefaultCoverageFinder().findPyramid(sets, crs);
            } catch (FactoryException ex) {
                throw new DataStoreException(ex);
            }
            if (tms == null) {
                throw new NoSuchDataException("No data pyramids available in this resource.");
            }
        } else {
            //use default TileMatrixSet
            tms = sets.get(0);
        }

        GridGeometry readLocal;
        if (domain == null) {
            readLocal = getGridGeometry(tms);
        } else {
            try {
                readLocal = getGridGeometry().derive().rounding(GridRoundingMode.ENCLOSING).subgrid(domain).build();
            } catch (DisjointExtentException ex) {
                throw new DisjointCoverageDomainException(ex);
            } catch (IllegalArgumentException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
        final double[] wantedRes = readLocal.getResolution(true);

        //search tilematrix with appropriate resolution
        TileMatrixCoverageResource bestMatch = null;
        for (GenericName tm : tms.getTileMatrices().keySet()) {
            final TileMatrixCoverageResource tcr = getTileMatrixResource(tms, tm);
            final double[] candidateResolution = tcr.getGridGeometry().getResolution(true);
            if (bestMatch == null) {
                bestMatch = tcr;
            }
            if (compareResolution(wantedRes, candidateResolution) <= 0) {
                //found most accurate resolution
                bestMatch = tcr;
                break;
            }
        }

        return bestMatch.read(readLocal, ints);
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.ofNullable(identifier);
    }

    /**
     * Compare resolution.
     * @param request
     * @param candidate
     * @return +1 if candidate has a higher resolution on any axe, 0 if all resolutions are identical, -1 if at least one resolution is smaller.
     */
    private static int compareResolution(double[] request, double[] candidate) {
        int r = 0;
        for (int i = 0; i <request.length; i++) {
            if (candidate[i] > request[i]) {
                return 1;
            } else if (candidate[i] == request[i]) {
                r += 0;
            } else {
                r = -1;
            }
        }
        return r;
    }
}
