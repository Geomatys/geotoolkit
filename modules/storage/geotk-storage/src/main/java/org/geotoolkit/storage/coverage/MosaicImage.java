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
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.math.XMath;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.Tile;

/**
 * Implementation of RenderedImage using GridMosaic.
 * With this a GridMosaic can be see as a RenderedImage.
 *
 * @author Thomas Rouby (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MosaicImage implements RenderedImage {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.storage.coverage");

    /**
     * A tile cache
     */
    private final Cache<Point,Raster> tileCache = new Cache<>(10, 12, true);

    /**
     * The original mosaic to read
     */
    private final Mosaic mosaic;
    /**
     * The original mosaic to read
     */
    private final List<SampleDimension> sampleDimensions;

    /**
     * Tile range to map as a rendered image in the mosaic
     */
    private final Rectangle gridRange;

    /**
     * The color model of the mosaic rendered image
     */
    private ColorModel colorModel = null;
    /**
     * The sample model of the mosaic rendered image
     */
    private SampleModel sampleModel = null;
    /**
     * The raster model of the mosaic rendered image
     */
    private Raster rasterModel = null;


    /**
     * Constructor
     * @param mosaic the mosaic to read as a rendered image
     */
    public MosaicImage(final Mosaic mosaic, final List<SampleDimension> sampleDimensions){
        this(mosaic,new Rectangle(mosaic.getGridSize()), sampleDimensions);
    }

    /**
     * Constructor
     * @param mosaic the mosaic to read as a rendered image
     * @param gridRange the tile to include in the rendered image.
     *        rectangle max max values are exclusive.
     */
    public MosaicImage(final Mosaic mosaic, Rectangle gridRange, final List<SampleDimension> sampleDimensions){
        ArgumentChecks.ensureNonNull("mosaic", mosaic);
        ArgumentChecks.ensureNonNull("range", gridRange);
        ArgumentChecks.ensureNonNull("sampleDimensions", sampleDimensions);

        if (mosaic.getGridSize().width == 0 || mosaic.getGridSize().height == 0) {
            throw new IllegalArgumentException("Mosaic grid can not be empty.");
        }
        this.mosaic = mosaic;
        this.gridRange = gridRange;
        this.sampleDimensions = sampleDimensions;
    }

    /**
     * Try to find a valid tile and load sample and color models from it.
     */
    private synchronized void loadImageModel() {
        try {
            final Optional<Tile> anyTile = mosaic.anyTile();
            if (anyTile.isPresent()) {
                final ImageTile imgTile = (ImageTile) anyTile.get();
                final RenderedImage image = imgTile.getImage();
                sampleModel = image.getSampleModel();
                colorModel = image.getColorModel();
                rasterModel = image.getTile(0, 0);
            }
        } catch (DataStoreException | IOException e) {
            LOGGER.log(Level.FINE, e.getMessage(), e);
        }

        if (sampleModel == null) {
            //use a fake tile created from sample dimensions
            final Dimension tileSize = mosaic.getTileSize();
            final BufferedImage image = BufferedImages.createImage(tileSize.width, tileSize.height, sampleDimensions.size(), DataBuffer.TYPE_DOUBLE);
            setImageModel(image);
        }
    }

    public synchronized void setImageModel(RenderedImage image) {
        if (sampleModel != null) return;
        sampleModel = image.getSampleModel();
        colorModel = image.getColorModel();
        rasterModel = image.getTile(0, 0);

        if (sampleDimensions.size() != sampleModel.getNumBands()) {
            throw new BackingStoreException("Mosaic tile image declares " + sampleModel.getNumBands()
                    + " bands, but sample dimensions have " + sampleDimensions.size()
                    + ", please fix mosaic implementation "+mosaic.getClass().getName());
        }

    }

    public List<SampleDimension> getSampleDimensions() {
        return sampleDimensions;
    }

    /**
     * Return intern GridMosaic
     * @return GridMosaic
     */
    public Mosaic getGridMosaic(){
        return this.mosaic;
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
    public Vector<RenderedImage> getSources() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getProperty(String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getPropertyNames() {
        return new String[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColorModel getColorModel() {
        if (colorModel == null) {
            loadImageModel();
        }
        return this.colorModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleModel getSampleModel() {
        if (sampleModel == null) {
            loadImageModel();
        }
        return this.sampleModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
        return gridRange.width * this.mosaic.getTileSize().width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight() {
        return gridRange.height * this.mosaic.getTileSize().height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinX() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinY() {
        return 0;
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
    public int getMinTileX() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinTileY() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTileWidth() {
        return this.mosaic.getTileSize().width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTileHeight() {
        return this.mosaic.getTileSize().height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTileGridXOffset() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTileGridYOffset() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Raster getTile(final int tileX, final int tileY) {
        return getTile(tileX, tileY, false);
    }

    private Raster getTile(final int tileX, final int tileY, boolean nullable) {
        final int mosaictileX = gridRange.x + tileX;
        final int mosaictileY = gridRange.y + tileY;

        Raster raster;
        try {
            raster = this.tileCache.get(new Point(tileX, tileY));
        } catch (IllegalArgumentException ex) {
            raster = null;
        }

        if (raster == null) {
            try {
                DataBuffer buffer = null;

                if (!mosaic.isMissing(mosaictileX,mosaictileY)) {
                    final ImageTile tile = (ImageTile) mosaic.getTile(mosaictileX,mosaictileY);
                    //can happen if tile is really missing, the isMissing method is a best effort call
                    if (tile != null) {
                        final RenderedImage image = tile.getImage();
                        setImageModel(image);
                        Raster tileRaster = image.getData();
                        tileRaster = makeConform(tileRaster);
                        buffer = tileRaster.getDataBuffer();
                    }
                }

                if (!nullable && buffer == null) {
                    //create an empty buffer
                    buffer = getSampleModel().createDataBuffer();
                    //TODO should be filled with no data pixel value
                }

                if (buffer != null) {
                    //create a raster from tile image with tile position offset.
                    LOGGER.log(Level.FINE, "Request tile {0}:{1} ", new Object[]{tileX,tileY});
                    final int rX = tileX * this.getTileWidth();
                    final int rY = tileY * this.getTileHeight();
                    raster = Raster.createWritableRaster(getSampleModel(), buffer, new Point(rX, rY));
                    this.tileCache.put(new Point(tileX, tileY), raster);
                }

            } catch (DataStoreException | IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }

        return raster;
    }

    private boolean isTileMissing(int x, int y) throws DataStoreException{
        return mosaic.isMissing(x+gridRange.x, y+gridRange.y);
    }

    private ImageTile getTileReference(int x, int y) throws DataStoreException{
        return (ImageTile) mosaic.getTile(x+gridRange.x,y+gridRange.y);
    }

    /**
     * Ensure the given raster is compatible with declared sample model.
     * If not the data will be copied to a new raster.
     */
    private Raster makeConform(Raster in) {
        final int inNumBands = in.getNumBands();
        final int outNumBands = rasterModel.getNumBands();
        if (inNumBands != outNumBands) {
            //this is a severe issue, the mosaic to no respect it's own sample dimension definition
            throw new BackingStoreException("Mosaic tile image declares " + inNumBands
                    + " bands, but sample dimensions have " + outNumBands
                    + ", please fix mosaic implementation " + mosaic.getClass().getName());
        }

        final int inDataType = in.getDataBuffer().getDataType();
        final int outDataType = rasterModel.getDataBuffer().getDataType();
        if (inDataType != outDataType) {
            //difference in input and output types, this may be caused by an aggregated resource
            final int x = 0;
            final int y = 0;
            final int width = in.getWidth();
            final int height = in.getHeight();
            final WritableRaster raster = rasterModel.createCompatibleWritableRaster(width, height);
            final int nbSamples = width * height * inNumBands;
            switch (outDataType) {
                case DataBuffer.TYPE_BYTE :
                case DataBuffer.TYPE_SHORT :
                case DataBuffer.TYPE_USHORT :
                case DataBuffer.TYPE_INT :
                    int[] arrayi = new int[nbSamples];
                    in.getPixels(x, y, width, height, arrayi);
                    raster.setPixels(x, y, width, height, arrayi);
                    break;
                case DataBuffer.TYPE_FLOAT :
                    float[] arrayf = new float[nbSamples];
                    in.getPixels(x, y, width, height, arrayf);
                    raster.setPixels(x, y, width, height, arrayf);
                    break;
                case DataBuffer.TYPE_DOUBLE :
                default :
                    double[] arrayd = new double[nbSamples];
                    in.getPixels(x, y, width, height, arrayd);
                    raster.setPixels(x, y, width, height, arrayd);
                    break;
            }
            in = raster;
        }
        return in;
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
                    if (!isTileMissing(x, y)) {
                        Raster tile = getTile(x, y, true);
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
                Raster raster = Raster.createRaster(sampleModel, databuffer, new Point(0, 0));
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
    private Point getPositionOf(int x, int y){

        final int posX = (int)(Math.floor(x/this.getTileWidth()));
        final int posY = (int)(Math.floor(y/this.getTileHeight()));

        return new Point(posX, posY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WritableRaster copyData(WritableRaster raster) {
        return null;
    }
}