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

import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.ImagingOpException;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.image.ComputedImage;
import org.apache.sis.internal.coverage.j2d.FillValues;
import org.apache.sis.internal.coverage.j2d.TilePlaceholder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.DataStores;

/**
 * Implementation of RenderedImage over a TileMatrix composed of GriCoverages.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
final class TileMatrixImage extends ComputedImage implements RenderedImage {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.storage.coverage");

    /**
     * The original TileMatrix to read
     */
    private final TileMatrix matrix;
    /**
     * Tile range to map as a rendered image in the TileMatrix
     */
    private final GridExtent gridRange;
    /**
     * GridGeometry of the global tilematrix coverage which is requested.
     */
    private final GridGeometry readGeometry;
    /**
     * Coverage samples to read.
     */
    private final int[] sampleRange;
    /**
     * Each tile size
     */
    private final long[] tileSize;
    /**
     * Index of the coverage axe used as X/Row in the image.
     */
    private final int xAxisIndex;
    /**
     * Index of the coverage axe used as Y/Column in the image.
     */
    private final int yAxisIndex;

    /**
     * The color model of the TileMatrix rendered image
     */
    private final ColorModel colorModel;
    /**
     * The sample model of the TileMatrix rendered image
     */
    private final SampleModel sampleModel;
    /**
     * The raster model of the TileMatrix rendered image
     */
    private final Raster rasterModel;

    /**
     * Fill pixel values used when creating empty tiles.
     */
    private final double[] fillPixel;
    private final TilePlaceholder placeHolder;
    /**
     * Image start X
     */
    private final int minX;
    /**
     * Image start Y
     */
    private final int minY;
    /**
     * Image width
     */
    private final int width;
    /**
     * Image height
     */
    private final int height;

    /**
     * Constructor
     * @param matrix the TileMatrix to read as a rendered image
     * @param gridRange the tile to include in the rendered image.
     *        rectangle max max values are exclusive.
     */
    TileMatrixImage(
            final TileMatrix matrix,
            GridExtent gridRange,
            GridGeometry readGeometry,
            long[] tileSize,
            SampleModel sampleModel,
            ColorModel colorModel,
            Raster rasterModel,
            int[] sampleRange,
            double[] fillPixel,
            int minX,
            int minY,
            int width,
            int height){
        super(sampleModel);
        this.matrix = matrix;
        this.gridRange = gridRange;
        this.readGeometry = readGeometry;
        this.tileSize = tileSize;
        this.sampleModel = sampleModel;
        this.colorModel = colorModel;
        this.rasterModel = rasterModel;
        this.sampleRange = sampleRange;
        this.fillPixel = fillPixel;
        final int[] xyAxis = gridRange.getSubspaceDimensions(2);
        this.xAxisIndex = xyAxis[0];
        this.yAxisIndex = xyAxis[1];
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;

        if (fillPixel != null) {
            final Number[] values = new Number[fillPixel.length];
            for (int i = 0; i < values.length; i++) values[i] = fillPixel[i];
            placeHolder = TilePlaceholder.filled(sampleModel, new FillValues(sampleModel, values, true));
        } else {
            placeHolder = TilePlaceholder.empty(sampleModel);
        }
    }

    /**
     * Return intern matrix
     * @return TileMatrix
     */
    public TileMatrix getTileMatrix(){
        return this.matrix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColorModel getColorModel() {
        return this.colorModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleModel getSampleModel() {
        return this.sampleModel;
    }

    @Override
    public int getMinX() {
        return minX;
    }

    @Override
    public int getMinY() {
        return minY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumXTiles() {
        return  Math.toIntExact(gridRange.getSize(xAxisIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumYTiles() {
        return  Math.toIntExact(gridRange.getSize(yAxisIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinTileX() {
        return Math.toIntExact(gridRange.getLow(xAxisIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinTileY() {
        return Math.toIntExact(gridRange.getLow(yAxisIndex));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTileWidth() {
        return Math.toIntExact(tileSize[xAxisIndex]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTileHeight() {
        return Math.toIntExact(tileSize[yAxisIndex]);
    }

    @Override
    protected Raster computeTile(int tileX, int tileY, WritableRaster previous) throws Exception {
        return getTile(tileX, tileY, false, true);
    }

    private Raster getTile(final int tileX, final int tileY, boolean nullable, boolean allowException) throws Exception {
        final long[] matrixtileIdx = gridRange.getLow().getCoordinateValues();
        matrixtileIdx[xAxisIndex] = tileX;
        matrixtileIdx[yAxisIndex] = tileY;

        final int rX = minX + (tileX-getMinTileX()) * this.getTileWidth();
        final int rY = minY + (tileY-getMinTileY()) * this.getTileHeight();

        Raster raster = null;
        try {
            DataBuffer buffer = null;

            if (!isTileMissing(tileX, tileY)) {
                final Tile tile = matrix.getTile(matrixtileIdx).orElse(null);
                //can be null if tile is really missing, the isMissing method is a best effort call
                if (tile != null) {
                    final Resource resource = tile.getResource();
                    if (resource instanceof GridCoverageResource gcr) {
                        final GridCoverage coverage = gcr.read(readGeometry, sampleRange);
                        final RenderedImage image = coverage.render(coverage.getGridGeometry().getExtent());
                        Raster tileRaster;
                        if (image.getNumXTiles() == 1 && image.getNumYTiles() == 1) {
                            tileRaster = image.getTile(image.getMinTileX(), image.getMinTileY());
                        } else {
                            tileRaster = image.getData();
                        }
                        try {
                            tileRaster = BufferedImages.makeConform(tileRaster, rasterModel);
                        } catch (ImagingOpException ex) {
                            throw new BackingStoreException("Fix mosaic implementation " + matrix.getClass().getName() + " " + ex.getMessage(), ex);
                        }
                        buffer = tileRaster.getDataBuffer();
                    }
                }
            }

            boolean isEmpty = false;
            if (!nullable && buffer == null) {
                //create an empty buffer
                return placeHolder.create(new Point(rX, rY));
            }

            if (buffer != null) {
                //create a raster from tile image with tile position offset.
                LOGGER.log(Level.FINE, "Request tile {0}:{1} ", new Object[]{tileX,tileY});
                raster = Raster.createWritableRaster(getSampleModel(), buffer, new Point(rX, rY));
                if (isEmpty) {
                    BufferedImages.setAll((WritableRaster) raster, fillPixel);
                }
            }

        } catch (DataStoreException e) {
            if (allowException) {
                throw e;
            } else if (DataStores.isInterrupted(e)){
                LOGGER.log(Level.INFO, "Tile(" + tileX +","+tileY+") creation has been interrupted.\n" + e.getMessage(), e);
            } else {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }

        if (raster == null && !nullable) {
            //create an empty buffer
            return placeHolder.create(new Point(rX, rY));
        }

        return raster;
    }

    /**
     * @param matrix
     * @param x tile X coordinate in the image range
     * @param y tile Y coordinate in the image range
     * @return
     * @throws DataStoreException
     */
    private boolean isTileMissing(long x, long y) throws DataStoreException{
        final long[] indices = gridRange.getLow().getCoordinateValues();
        indices[xAxisIndex] = x;
        indices[yAxisIndex] = y;
        final TileStatus status = matrix.getTileStatus(indices);
        return status == TileStatus.MISSING
            || status == TileStatus.OUTSIDE_EXTENT
            || status == TileStatus.IN_ERROR;
    }

}
