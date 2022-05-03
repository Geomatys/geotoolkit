/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2019, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.image.ComputedImage;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.math.XMath;
import org.geotoolkit.storage.DataStores;
import org.apache.sis.storage.tiling.Tile;
import org.geotoolkit.storage.multires.TileMatrix;
import org.apache.sis.storage.tiling.TileStatus;
import org.geotoolkit.storage.multires.TileMatrices;

/**
 * Implementation of RenderedImage using GridMosaic.
 * With this a GridMosaic can be see as a RenderedImage.
 *
 * @author Thomas Rouby (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TileMatrixImage extends ComputedImage implements RenderedImage {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.storage.coverage");

    /**
     * The original TileMatrix to read
     */
    private final TileMatrix matrix;
    /**
     * Tile range to map as a rendered image in the TileMatrix
     */
    private final Rectangle gridRange;

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

    public static TileMatrixImage create(TileMatrix matrix, Rectangle gridRange, final List<SampleDimension> sampleDimensions) throws DataStoreException {
        if (gridRange == null) {
            gridRange = new Rectangle(TileMatrices.getGridSize(matrix));
        }

        if (gridRange.width == 0 || gridRange.height == 0) {
            throw new DataStoreException("Mosaic grid range is empty : " + gridRange);
        }

        RenderedImage sample;
        try {
            Tile tile = matrix.anyTile();
            if (tile instanceof ImageTile) {
                sample = ((ImageTile) tile).getImage();
            } else {
                throw new DataStoreException("Mosaic does not contain images");
            }
        } catch (DataStoreException | IOException ex) {
            if (sampleDimensions != null) {
                //use a fake tile created from sample dimensions
                final Dimension tileSize = matrix.getTileSize();
                sample = BufferedImages.createImage(tileSize.width, tileSize.height, sampleDimensions.size(), DataBuffer.TYPE_DOUBLE);
            } else if (ex instanceof DataStoreException) {
                throw (DataStoreException) ex;
            } else {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        final double[] fillPixel;
        if (sampleDimensions != null && !sampleDimensions.isEmpty()) {
            fillPixel = SampleDimensionUtils.getFillPixel(sampleDimensions.toArray(new SampleDimension[sampleDimensions.size()]));
        } else {
            fillPixel = new double[sample.getSampleModel().getNumBands()];
            Arrays.fill(fillPixel, Double.NaN);
        }

        final SampleModel sm = sample.getSampleModel();
        final ColorModel cm = sample.getColorModel();
        final Raster rm = sample.getTile(sample.getMinTileX(), sample.getMinTileY());
        return new TileMatrixImage(matrix, gridRange, sm, cm, rm, fillPixel);
    }

    /**
     * Constructor
     * @param matrix the TileMatrix to read as a rendered image
     * @param gridRange the tile to include in the rendered image.
     *        rectangle max max values are exclusive.
     */
    private TileMatrixImage(final TileMatrix matrix, Rectangle gridRange, SampleModel sampleModel, ColorModel colorModel, Raster rasterModel, double[] fillPixel){
        super(sampleModel);
        this.matrix = matrix;
        this.gridRange = gridRange;
        this.sampleModel = sampleModel;
        this.colorModel = colorModel;
        this.rasterModel = rasterModel;
        this.fillPixel = fillPixel;
    }

    /**
     * Return intern GridMosaic
     * @return GridMosaic
     */
    public TileMatrix getTileMatrix(){
        return this.matrix;
    }

    /**
     *
     * @return
     */
    public Rectangle getGridRange() {
        return (Rectangle) gridRange.clone();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
        return gridRange.width * this.matrix.getTileSize().width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight() {
        return gridRange.height * this.matrix.getTileSize().height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumXTiles() {
        return  gridRange.width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumYTiles() {
        return  gridRange.height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTileWidth() {
        return this.matrix.getTileSize().width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTileHeight() {
        return this.matrix.getTileSize().height;
    }

    @Override
    protected Raster computeTile(int tileX, int tileY, WritableRaster previous) throws Exception {
        return getTile(tileX, tileY, false, true);
    }

    private Raster getTile(final int tileX, final int tileY, boolean nullable, boolean allowException) throws Exception {
        final int mosaictileX = gridRange.x + tileX;
        final int mosaictileY = gridRange.y + tileY;

        Raster raster = null;
        try {
            DataBuffer buffer = null;

            if (!isTileMissing(matrix, mosaictileX, mosaictileY)) {
                final ImageTile tile = (ImageTile) matrix.getTile(mosaictileX,mosaictileY).orElse(null);
                //can happen if tile is really missing, the isMissing method is a best effort call
                if (tile != null) {
                    final RenderedImage image = tile.getImage();
                    Raster tileRaster;
                    if (image instanceof BufferedImage) {
                        tileRaster = ((BufferedImage) image).getRaster();
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

            boolean isEmpty = false;
            if (!nullable && buffer == null) {
                //create an empty buffer
                buffer = getSampleModel().createDataBuffer();
                isEmpty = true;
            }

            if (buffer != null) {
                //create a raster from tile image with tile position offset.
                LOGGER.log(Level.FINE, "Request tile {0}:{1} ", new Object[]{tileX,tileY});
                final int rX = tileX * this.getTileWidth();
                final int rY = tileY * this.getTileHeight();
                raster = Raster.createWritableRaster(getSampleModel(), buffer, new Point(rX, rY));
                if (isEmpty) {
                    BufferedImages.setAll((WritableRaster) raster, fillPixel);
                }
            }

        } catch (DataStoreException | IOException e) {
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
            final DataBuffer buffer = getSampleModel().createDataBuffer();
            final int rX = tileX * this.getTileWidth();
            final int rY = tileY * this.getTileHeight();
            raster = Raster.createWritableRaster(getSampleModel(), buffer, new Point(rX, rY));
            BufferedImages.setAll((WritableRaster) raster, fillPixel);
        }

        return raster;
    }

    private boolean isTileMissing(TileMatrix matrix, long x, long y) throws DataStoreException{
        final TileStatus status = matrix.getTileStatus(x + gridRange.x, y + gridRange.y);
        return status == TileStatus.MISSING
            || status == TileStatus.OUTSIDE_EXTENT
            || status == TileStatus.IN_ERROR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Raster getData() {
        return getData(new Rectangle(getWidth(), getHeight()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Raster getData(Rectangle rect) {
        Raster rasterOut = null;
        try {
            final Point upperLeftPosition = this.getPositionOf(rect.x, rect.y);
            final Point lowerRightPosition = this.getPositionOf(rect.x + rect.width - 1, rect.y + rect.height - 1);

            for (int y = Math.max(upperLeftPosition.y, 0); y < Math.min(lowerRightPosition.y + 1, this.getNumYTiles()); y++) {
                for (int x = Math.max(upperLeftPosition.x, 0); x < Math.min(lowerRightPosition.x + 1, this.getNumXTiles()); x++) {
                    if (!isTileMissing(matrix, x, y)) {
                        Raster tile = getTile(x, y, true, false);
                        if (tile != null) {
                            final Rectangle tileRect = new Rectangle(x * this.getTileWidth(), y * this.getTileHeight(), this.getTileWidth(), this.getTileHeight());

                            final int minX, maxX, minY, maxY;
                            minX = XMath.clamp(rect.x, tileRect.x, tileRect.x + tileRect.width);
                            maxX = XMath.clamp(rect.x + rect.width, tileRect.x, tileRect.x + tileRect.width);
                            minY = XMath.clamp(rect.y, tileRect.y, tileRect.y + tileRect.height);
                            maxY = XMath.clamp(rect.y + rect.height, tileRect.y, tileRect.y + tileRect.height);

                            final Rectangle rectIn = new Rectangle(minX, minY, maxX - minX, maxY - minY);
                            rectIn.translate(-tileRect.x, -tileRect.y);
                            final Rectangle rectOut = new Rectangle(minX, minY, maxX - minX, maxY - minY);
                            rectOut.translate(-rect.x, -rect.y);

                            if (rectIn.width <= 0 || rectIn.height <= 0 || rectOut.width <= 0 || rectOut.height <= 0) {
                                continue;
                            }

                            Object dataElements = tile.getSampleModel().getDataElements(rectIn.x, rectIn.y, rectIn.width, rectIn.height, null, tile.getDataBuffer());

                            if (rasterOut == null) {
                                rasterOut = createRaster(tile, rect.width, rect.height);
                            }

                            rasterOut.getSampleModel().setDataElements(rectOut.x, rectOut.y, rectOut.width, rectOut.height,
                                    dataElements,
                                    rasterOut.getDataBuffer());
                        }
                    }
                }
            }

            if (rasterOut == null) {
                //create an empty raster
                SampleModel sampleModel = getSampleModel();
                DataBuffer databuffer = getSampleModel().createDataBuffer();
                Raster raster = Raster.createWritableRaster(sampleModel, databuffer, new Point(0, 0));  // TODO: JDK-8275345
                rasterOut = createRaster(raster, rect.width, rect.height);
            }

            if (rect.x != 0 || rect.y != 0) {
                rasterOut = rasterOut.createTranslatedChild(rect.x, rect.y);
            }

        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "", ex);
        }
        return rasterOut;
    }

    private WritableRaster createRaster(Raster base, int width, int height) {
        WritableRaster rasterOut = base.createCompatibleWritableRaster(0, 0, width, height);
        // Clear dataBuffer to 0 value for all bank
        for (int s = 0; s < rasterOut.getDataBuffer().getSize(); s++) {
            for (int b = 0; b < rasterOut.getDataBuffer().getNumBanks(); b++) {
                rasterOut.getDataBuffer().setElem(b, s, 0);
            }
        }
        return rasterOut;
    }

    /**
     * Get the tile column and row position for a pixel.
     * Return value can be out of the gridSize
     * @param x
     * @param y
     * @return
     */
    private Point getPositionOf (int x, int y) {
        final int posX = (int) (Math.floor(x / this.getTileWidth()));
        final int posY = (int) (Math.floor(y / this.getTileHeight()));
        return new Point(posX, posY);
    }

}
