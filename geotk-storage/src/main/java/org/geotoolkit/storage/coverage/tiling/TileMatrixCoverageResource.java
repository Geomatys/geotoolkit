/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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

import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.util.privy.UnmodifiableArrayList;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.BufferedImages;
import org.opengis.coverage.CannotEvaluateException;

/**
 * View a tile matrix composed of GridCoverageResource tiles as a continous GridCoverageResource.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class TileMatrixCoverageResource extends AbstractGridCoverageResource {

    private final TileMatrix matrix;
    private final int[] tileSize;
    private final GridGeometry tilingScheme;
    private final GridGeometry coverageGrid;
    private final List<SampleDimension> sampleDimensions;

    public TileMatrixCoverageResource(TileMatrix matrix, int[] tileSize, List<SampleDimension> sampleDimensions) {
        super(null, false);
        ArgumentChecks.ensureNonNull("matrix", matrix);
        ArgumentChecks.ensureNonNull("tileSize", tileSize);
        ArgumentChecks.ensureNonNull("sampleDimensions", sampleDimensions);
        this.matrix = matrix;
        this.tileSize = tileSize.clone();
        this.sampleDimensions = UnmodifiableArrayList.wrap(sampleDimensions.toArray(SampleDimension[]::new));
        this.tilingScheme = matrix.getTilingScheme();
        this.coverageGrid = tilingScheme.upsample(tileSize);
    }

    TileMatrix getMatrix() {
        return matrix;
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return coverageGrid;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return sampleDimensions;
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        final GridExtent schemeIntersection;
        if (domain == null) {
            schemeIntersection = tilingScheme.getExtent();
        } else {
            schemeIntersection = tilingScheme.derive().rounding(GridRoundingMode.ENCLOSING).subgrid(domain).getIntersection();
        }

        //ensure range and samples dimensions are aligned
        List<SampleDimension> dimensions;
        if (range == null || range.length == 0) {
            range = new int[sampleDimensions.size()];
            for (int i = 0; i < range.length; i++) {
                range[i] = i;
            }
            dimensions = getSampleDimensions();
        } else {
            final List<SampleDimension> all = getSampleDimensions();
            dimensions = new ArrayList<>(range.length);
            for (int i = 0; i < range.length; i++) {
                dimensions.add(all.get(range[i]));
            }
        }

        return new TileMatrixCoverage(this, schemeIntersection, tileSize, range, dimensions);
    }

    /**
     * TODO this should be initialized only once,
     * but changes because of sample range and requested grid extent.
     */
    Object[] getImageModel(GridExtent gridRange, int ... sampleRange) {

        final int[] xyAxis = gridRange.getSubspaceDimensions(2);
        final int tileSizeX = Math.toIntExact(tileSize[xyAxis[0]]);
        final int tileSizeY = Math.toIntExact(tileSize[xyAxis[1]]);
        RenderedImage sample;
        try {
            Tile tile = null;
            if (matrix instanceof org.geotoolkit.storage.multires.TileMatrix tm) {
                tile = tm.anyTile();
            }
            if (tile != null) {
                final Resource resource = tile.getResource();
                if (resource instanceof GridCoverageResource gcr) {
                    final GridGeometry gridGeometry = gcr.getGridGeometry();
                    final GridExtent extent = gridGeometry.getExtent();
                    final long[] low = extent.getLow().getCoordinateValues();
                    final GridExtent subExtent = new GridExtent(null, low, low, true);
                    GridCoverage coverage = gcr.read(gridGeometry.derive().subgrid(subExtent).build(), sampleRange);
                    sample = coverage.render(null);
                } else {
                    throw new DataStoreException("TileMatrix does not contain a coverage");
                }
            } else {
                throw new DataStoreException("TileMatrix does not contain a coverage");
            }
        } catch (DataStoreException ex) {
            if (sampleDimensions != null) {
                //use a fake tile created from sample dimensions
                sample = BufferedImages.createImage(tileSizeX, tileSizeY, sampleDimensions.size(), DataBuffer.TYPE_DOUBLE);
            } else {
                throw new CannotEvaluateException(ex.getMessage(), ex);
            }
        }

        final double[] fillPixel;
        if (sampleDimensions != null && !sampleDimensions.isEmpty()) {
            fillPixel = SampleDimensionUtils.getFillPixel(sampleDimensions.toArray(new SampleDimension[sampleDimensions.size()]));
        } else {
            fillPixel = new double[sample.getSampleModel().getNumBands()];
            Arrays.fill(fillPixel, Double.NaN);
        }

        final SampleModel sm = sample.getSampleModel().createCompatibleSampleModel(tileSizeX, tileSizeY);
        final ColorModel cm = sample.getColorModel();
        final Raster rm = sample.getTile(sample.getMinTileX(), sample.getMinTileY());
        return new Object[]{sm,cm,rm,fillPixel};
    }

}
