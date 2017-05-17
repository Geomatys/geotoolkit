/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

import org.apache.sis.util.logging.Logging;

import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.Cache;
import org.geotoolkit.math.XMath;

/**
 * Implementation of RenderedImage using GridMosaic.
 * With this a GridMosaic can be see as a RenderedImage.
 *
 * @author Thomas Rouby (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GridMosaicRenderedImage implements RenderedImage {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.storage.coverage");

    /**
     * A tile cache
     */
    private final Cache<Point,Raster> tileCache = new Cache<>(10, 12, true);

    /**
     * The original mosaic to read
     */
    private final GridMosaic mosaic;

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
     * Constructor
     * @param mosaic the mosaic to read as a rendered image
     */
    public GridMosaicRenderedImage(final GridMosaic mosaic){
        this(mosaic,new Rectangle(mosaic.getGridSize()));
    }

    /**
     * Constructor
     * @param mosaic the mosaic to read as a rendered image
     * @param gridRange the tile to include in the rendered image.
     *        rectangle max max values are exclusive.
     */
    public GridMosaicRenderedImage(final GridMosaic mosaic, Rectangle gridRange){
        ArgumentChecks.ensureNonNull("mosaic", mosaic);
        ArgumentChecks.ensureNonNull("range", gridRange);

        if(mosaic.getGridSize().width == 0 || mosaic.getGridSize().height == 0){
            throw new IllegalArgumentException("Mosaic grid can not be empty.");
        }
        this.mosaic = mosaic;
        this.gridRange = gridRange;

        RenderedImage firstTile = getFirstTile();
        if (firstTile != null) {
            this.sampleModel = firstTile.getSampleModel();
            this.colorModel = firstTile.getColorModel();
        }
    }

    private RenderedImage getFirstTile() {
        RenderedImage firstTile = null;
        if (colorModel == null && sampleModel == null) {
            try {
                //search the first non missing tile of the Mosaic
                final Rectangle dataArea = mosaic.getDataExtent();
                final Dimension tileSize = mosaic.getTileSize();
                if (dataArea != null) {

                    final TileReference tile = mosaic.getTile(dataArea.x / tileSize.width, dataArea.y / tileSize.height, null);
                    if (tile != null) {
                        if (tile.getInput() instanceof RenderedImage) {
                            firstTile = (RenderedImage) tile.getInput();
                        } else {
                            final ImageReader reader = tile.getImageReader();
                            firstTile = reader.read(0);
                            reader.dispose();
                        }
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("First tile can't be read.", e);
            } catch (DataStoreException e) {
                throw new IllegalArgumentException("Input mosaic doesn't have any tile.", e);
            }
        }
        return firstTile;
    }

    /**
     * Return intern GridMosaic
     * @return GridMosaic
     */
    public GridMosaic getGridMosaic(){
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
            RenderedImage firstTile = getFirstTile();
            if (firstTile != null) {
                this.colorModel = firstTile.getColorModel();
            }
        }

        return this.colorModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleModel getSampleModel() {

        if (sampleModel == null) {
            RenderedImage firstTile = getFirstTile();
            if (firstTile != null) {
                this.sampleModel = firstTile.getSampleModel();
            }
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
        return this.mosaic.getTileSize().width;
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
    public Raster getTile(int tileX, int tileY) {
        tileX += gridRange.x;
        tileY += gridRange.y;

        Raster raster;
        try {
            raster = this.tileCache.get(new Point(tileX, tileY));
        } catch (IllegalArgumentException ex) {
            raster = null;
        }

        if (raster == null) {
            try {
                DataBuffer buffer = null;

                if (!mosaic.isMissing(tileX,tileY)) {
                    final TileReference tile = mosaic.getTile(tileX,tileY, null);
                    if (tile != null) {
                        if (tile.getInput() instanceof RenderedImage) {
                            buffer = ((RenderedImage)tile.getInput()).getData().getDataBuffer();
                        } else {
                            final ImageReader reader = tile.getImageReader();
                            buffer = reader.read(0).getData().getDataBuffer();
                            reader.dispose();
                        }
                    }
                }

                if(buffer==null){
                    //create an empty buffer
                    buffer = getSampleModel().createDataBuffer();
                }

                //create a raster from tile image with tile position offset.
                LOGGER.log(Level.FINE, "Request tile {0}:{1} ", new Object[]{tileX,tileY});
                final int rX = tileX*this.getTileWidth();
                final int rY = tileY*this.getTileHeight();

                raster = Raster.createWritableRaster(getSampleModel(), buffer, new Point(rX, rY));

                this.tileCache.put(new Point(tileX, tileY), raster);

            } catch ( DataStoreException | IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }

        return raster;
    }

    private boolean isTileMissing(int x, int y) throws DataStoreException{
        return mosaic.isMissing(x+gridRange.x, y+gridRange.y);
    }

    private TileReference getTileReference(int x, int y) throws DataStoreException{
        return mosaic.getTile(x+gridRange.x,y+gridRange.y,null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Raster getData() {
        final RenderedImage firstTile = getFirstTile();
        Raster rasterOut = null;
        if (firstTile != null) {
            rasterOut = firstTile.getTile(0, 0).createCompatibleWritableRaster(getWidth(), getHeight());

            // Clear dataBuffer to 0 value for all bank
            for (int s = 0; s < rasterOut.getDataBuffer().getSize(); s++) {
                for (int b = 0; b < rasterOut.getDataBuffer().getNumBanks(); b++) {
                    rasterOut.getDataBuffer().setElem(b, s, 0);
                }
            }

            try {

                for (int y = 0; y < this.getNumYTiles(); y++) {
                    for (int x = 0; x < this.getNumYTiles(); x++) {
                        if (!isTileMissing(x, y)) {
                            final TileReference tile = getTileReference(x, y);
                            final RenderedImage sourceImg;

                            if (tile.getInput() instanceof RenderedImage) {
                                sourceImg = (RenderedImage) tile.getInput();
                            } else {
                                sourceImg = tile.getImageReader().read(tile.getImageIndex());
                            }

                            final Raster rasterIn = sourceImg.getData();

                            rasterOut.getSampleModel().setDataElements(x * this.getTileWidth(), y * this.getTileHeight(), this.getTileWidth(), this.getTileHeight(),
                                    rasterIn.getSampleModel().getDataElements(0, 0, this.getTileWidth(), this.getTileHeight(), null, rasterIn.getDataBuffer()),
                                    rasterOut.getDataBuffer());

                        }
                    }
                }

            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "", ex);
            }
        }
        return rasterOut;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Raster getData(Rectangle rect) {
        final RenderedImage firstTile = getFirstTile();
        Raster rasterOut = null;
        if (firstTile != null) {
            rasterOut = firstTile.getTile(0, 0).createCompatibleWritableRaster(rect.width, rect.height);

            // Clear dataBuffer to 0 value for all bank
            for (int s = 0; s < rasterOut.getDataBuffer().getSize(); s++) {
                for (int b = 0; b < rasterOut.getDataBuffer().getNumBanks(); b++) {
                    rasterOut.getDataBuffer().setElem(b, s, 0);
                }
            }

            try {
                final Point upperLeftPosition = this.getPositionOf(rect.x, rect.y);
                final Point lowerRightPosition = this.getPositionOf(rect.x + rect.width - 1, rect.y + rect.height - 1);

                for (int y = Math.max(upperLeftPosition.y, 0); y < Math.min(lowerRightPosition.y + 1, this.getNumYTiles()); y++) {
                    for (int x = Math.max(upperLeftPosition.x, 0); x < Math.min(lowerRightPosition.x + 1, this.getNumXTiles()); x++) {
                        if (!isTileMissing(x, y)) {
                            final TileReference tile = getTileReference(x, y);
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

                            final RenderedImage sourceImg;
                            if (tile.getInput() instanceof RenderedImage) {
                                sourceImg = (RenderedImage) tile.getInput();
                            } else {
                                sourceImg = tile.getImageReader().read(tile.getImageIndex());
                            }

                            final Raster rasterIn = sourceImg.getData();

                            rasterOut.getSampleModel().setDataElements(rectOut.x, rectOut.y, rectOut.width, rectOut.height,
                                    rasterIn.getSampleModel().getDataElements(rectIn.x, rectIn.y, rectIn.width, rectIn.height, null, rasterIn.getDataBuffer()),
                                    rasterOut.getDataBuffer());

                        }
                    }
                }

            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "", ex);
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
