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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.coverage.grid.IllegalGridGeometryException;
import org.apache.sis.image.ImageCombiner;
import org.apache.sis.image.Interpolation;
import org.apache.sis.util.internal.shared.UnmodifiableArrayList;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.storage.tiling.WritableTileMatrix;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.mosaic.AggregatedCoverageResource;
import org.geotoolkit.storage.multires.TileMatrices;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.util.FactoryException;

/**
 * View a tile matrix composed of GridCoverageResource tiles as a continous GridCoverageResource.
 *
 * @author Johann Sorel (Geomatys)
 */
public class TileMatrixCoverageResource extends AbstractGridCoverageResource {

    protected final TileMatrix matrix;
    protected final int[] tileSize;
    protected final GridGeometry tilingScheme;
    protected final GridGeometry coverageGrid;
    protected final List<SampleDimension> sampleDimensions;

    public TileMatrixCoverageResource(TileMatrix matrix, int[] tileSize, List<SampleDimension> sampleDimensions) {
        super(null, false);
        ArgumentChecks.ensureNonNull("matrix", matrix);
        ArgumentChecks.ensureNonNull("tileSize", tileSize);
        ArgumentChecks.ensureNonNull("sampleDimensions", sampleDimensions);
        this.matrix = matrix;
        this.tileSize = tileSize.clone();
        this.sampleDimensions = UnmodifiableArrayList.wrap(sampleDimensions.toArray(SampleDimension[]::new));
        this.tilingScheme = matrix.getTilingScheme();
        this.coverageGrid = tilingScheme.upsample(ArraysExt.copyAsLongs(tileSize));
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

    /**
     * Writable version of the TileMatrixCoverageResource with support for
     * writing with UPDATE option only.
     */
    public static class Writable extends TileMatrixCoverageResource implements WritableGridCoverageResource {

        private RenderedImage template;

        public Writable(WritableTileMatrix matrix, int[] tileSize, List<SampleDimension> sampleDimensions) {
            super(matrix, tileSize, sampleDimensions);
        }

        private synchronized void init() throws DataStoreException {
            //image datas are not loaded
            final GridCoverage coverage = read(getGridGeometry());
            template = coverage.render(null);
        }

        @Override
        public void write(GridCoverage updateCoverage, Option... options) throws DataStoreException {
            init();
            for (Option opt : options) {
                if (opt == CommonOption.REPLACE) {
                    throw new DataStoreException("REPLACE not supported");
                }
            }

            final GridExtent intersection = matrix.getTilingScheme().derive()
                    .rounding(GridRoundingMode.ENCLOSING)
                    .subgrid(updateCoverage.getGridGeometry())
                    .getIntersection();


            try (Stream<long[]> pointStream = TileMatrices.pointStream(intersection)) {
                pointStream.forEach(new Consumer<long[]>(){
                    @Override
                    public void accept(long[] t) {
                        updateTile(t[0], t[1], Interpolation.NEAREST, updateCoverage);
                    }
                });
            }

        }

        private void updateTile(long idx, long idy, Interpolation interpolation, GridCoverage updateCoverage) {

            final BufferedImage currentlyTile;

            try {
                //extract tile image
                if (matrix.getTileStatus(idx, idy) != TileStatus.MISSING) {
                    final Tile tile = matrix.getTile(idx, idy).orElseThrow(() -> new ConcurrentModificationException("Tile should not be missing"));
                    final GridCoverageResource resource = (GridCoverageResource) tile.getResource();
                    final GridCoverage coverage = resource.read(null);
                    currentlyTile = (BufferedImage) coverage.render(coverage.getGridGeometry().getExtent());
                } else {
                    currentlyTile = BufferedImages.createImage(tileSize[0], tileSize[1], template);
                }

                //current tile grid geometry
                final GridExtent tileExt = new GridExtent(null, new long[]{idx,idy}, new long[]{idx,idy}, true);
                final GridGeometry tileGridGeom = matrix.getTilingScheme().derive().subgrid(tileExt).build().upsample(ArraysExt.copyAsLongs(tileSize));

                //read only the area we need from the updating coverage
                final GridExtent intersection = updateCoverage.getGridGeometry().derive().rounding(GridRoundingMode.ENCLOSING).subgrid(tileGridGeom).getIntersection();
                final RenderedImage coverageImage = updateCoverage.render(intersection);
                final GridGeometry imageGridGeom = updateCoverage.getGridGeometry().derive().subgrid(intersection).build();

                //combine images
                final MathTransform toSource = AggregatedCoverageResource.createTransform(tileGridGeom, currentlyTile, imageGridGeom, coverageImage);
                final ImageCombiner ic = new ImageCombiner(currentlyTile);
                ic.setInterpolation(interpolation);
                ic.resample(coverageImage, new Rectangle(currentlyTile.getWidth(), currentlyTile.getHeight()), toSource);
                final RenderedImage tileImage = ic.result();

                //write new tile
                ((WritableTileMatrix) matrix).writeTiles(Stream.of(new DefaultImageTile(matrix, tileImage, new long[]{idx, idy})));
            } catch (IllegalGridGeometryException ex) {
                //no intesection
            } catch (DataStoreException | FactoryException | NoninvertibleTransformException ex) {
                throw new BackingStoreException("Update fail for tile ("+idx+", "+idy+")", ex);
            }
        }

    }

}
