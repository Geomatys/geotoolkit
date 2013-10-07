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
package org.geotoolkit.coverage;

import org.apache.sis.util.logging.Logging;

import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.collection.Cache;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.math.XMath;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Implementation of RenderedImage using GridMosaic.
 * With this a GridMosaic can be see as a RenderedImage.
 *
 * @author Thomas Rouby (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class GridMosaicRenderedImage implements RenderedImage {

    private static final Logger LOGGER = Logging.getLogger(GridMosaicRenderedImage.class);

    /**
     * A tile cache
     */
    private final Cache<Point,Raster> tileCache = new Cache<>(10, 12, true);

    /**
     * The original mosaic to read
     */
    private final GridMosaic mosaic;

    /**
     * The first tile read as an image to initialize RenderedImage parameter
     */
    private RenderedImage firstTileImage = null;

    /**
     * The empty buffer use for missing tile
     */
    private DataBuffer emptyBuffer = null;

    /**
     * The sample model of the mosaic rendered image
     */
    private SampleModel sampleModel = null;

    /**
     * Constructor
     * @param mosaic the mosaic to read as a rendered image
     */
    public GridMosaicRenderedImage(final GridMosaic mosaic){
        if(mosaic.getGridSize().width == 0 || mosaic.getGridSize().height == 0){
            throw new IllegalArgumentException("Mosaic grid can not be empty.");
        }

        this.mosaic = mosaic;

        try {
            //search the first non missing tile of the Mosaic
            TileReference tile = null;

            exitLoop :
            if (tile == null) {
                final Dimension gridSize = this.mosaic.getGridSize();
                for (int y=0; y<gridSize.height; y++){
                    for (int x=0; x<gridSize.width; x++){
                        if (mosaic.isMissing(x,y)) {
                            continue;
                        } else {
                            tile = mosaic.getTile(x,y, null);
                            break exitLoop;
                        }
                    }
                }
            }

            if (tile != null) {
                if (tile.getInput() instanceof RenderedImage) {
                    firstTileImage = (RenderedImage)tile.getInput();
                } else {
                    final ImageReader reader = tile.getImageReader();
                    firstTileImage = reader.read(0);
                    reader.dispose();
                }

                emptyBuffer = firstTileImage.getSampleModel().createDataBuffer();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("First tile can't be read.", e);
        } catch (DataStoreException e) {
            throw new IllegalArgumentException("Input mosaic doesn't have any tile.", e);
        }
    }

    /**
     * Return intern GridMosaic
     * @return GridMosaic
     */
    public GridMosaic getGridMosaic(){
        return this.mosaic;
    }

    @Override
    public Vector<RenderedImage> getSources() {
        return null;
    }

    @Override
    public Object getProperty(String name) {
        return null;
    }

    @Override
    public String[] getPropertyNames() {
        return new String[0];
    }

    @Override
    public ColorModel getColorModel() {
        if (firstTileImage != null) {
            return firstTileImage.getColorModel();
        }

        return null;
    }

    @Override
    public SampleModel getSampleModel() {
        if (sampleModel == null && firstTileImage != null) {
            sampleModel = this.getColorModel().createCompatibleSampleModel(this.getWidth(), this.getHeight());
        }

        return sampleModel;
    }

    @Override
    public int getWidth() {
        return this.mosaic.getGridSize().width * this.mosaic.getTileSize().width;
    }

    @Override
    public int getHeight() {
        return this.mosaic.getGridSize().height * this.mosaic.getTileSize().height;
    }

    @Override
    public int getMinX() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getNumXTiles() {
        return  this.mosaic.getGridSize().width;
    }

    @Override
    public int getNumYTiles() {
        return  this.mosaic.getGridSize().height;
    }

    @Override
    public int getMinTileX() {
        return 0;
    }

    @Override
    public int getMinTileY() {
        return 0;
    }

    @Override
    public int getTileWidth() {
        return this.mosaic.getTileSize().width;
    }

    @Override
    public int getTileHeight() {
        return this.mosaic.getTileSize().width;
    }

    @Override
    public int getTileGridXOffset() {
        return 0;
    }

    @Override
    public int getTileGridYOffset() {
        return 0;
    }

    @Override
    public Raster getTile(int tileX, int tileY) {

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
                    buffer = emptyBuffer;
                }

                //create a raster from tile image with tile position offset.
                LOGGER.log(Level.FINE, "Request tile {0}:{1} ", new Object[]{tileX,tileY});
                final Point offset = new Point(tileX*this.getTileWidth(), tileY*this.getTileHeight());
                raster = Raster.createWritableRaster(this.getColorModel().createCompatibleSampleModel(this.getTileWidth(), this.getTileHeight()), buffer , offset);
                this.tileCache.put(new Point(tileX, tileY), raster);

            } catch ( DataStoreException | IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }

        return raster;
    }

    @Override
    public Raster getData() {
        Raster rasterOut = this.getColorModel().createCompatibleWritableRaster(this.getWidth(), this.getHeight());

        // Clear dataBuffer to 0 value for all bank
        for (int s=0; s<rasterOut.getDataBuffer().getSize(); s++){
            for (int b=0; b<rasterOut.getDataBuffer().getNumBanks(); b++){
                rasterOut.getDataBuffer().setElem(b, s, 0);
            }
        }

        try {

            for (int y=0; y<this.getNumYTiles(); y++){
                for (int x=0; x<this.getNumYTiles(); x++){
                    if (!mosaic.isMissing(x, y)){
                        final TileReference tile = mosaic.getTile(x,y,null);
                        final RenderedImage sourceImg;

                        if (tile.getInput() instanceof RenderedImage) {
                            sourceImg = (RenderedImage) tile.getInput();
                        } else {
                            sourceImg = tile.getImageReader().read(tile.getImageIndex());
                        }

                        final Raster rasterIn = sourceImg.getData();

                        rasterOut.getSampleModel().setDataElements(x*this.getTileWidth(), y*this.getTileHeight(), this.getTileWidth(), this.getTileHeight(),
                                rasterIn.getSampleModel().getDataElements(0, 0, this.getTileWidth(), this.getTileHeight(), null, rasterIn.getDataBuffer()),
                                rasterOut.getDataBuffer());

                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "", ex);
        }
        return rasterOut;
    }

    @Override
    public Raster getData(Rectangle rect) {
        Raster rasterOut = this.getColorModel().createCompatibleWritableRaster(rect.width, rect.height);

        // Clear dataBuffer to 0 value for all bank
        for (int s=0; s<rasterOut.getDataBuffer().getSize(); s++){
            for (int b=0; b<rasterOut.getDataBuffer().getNumBanks(); b++){
                rasterOut.getDataBuffer().setElem(b, s, 0);
            }
        }

        try {
            final Point upperLeftPosition = this.getPositionOf(rect.x, rect.y);
            final Point lowerRightPosition = this.getPositionOf(rect.x+rect.width, rect.y+rect.height);

            for (int y=Math.max(upperLeftPosition.y,0); y<Math.min(lowerRightPosition.y+1,this.getNumYTiles()); y++){
                for (int x=Math.max(upperLeftPosition.x,0); x<Math.min(lowerRightPosition.x+1, this.getNumXTiles()); x++){
                    if (!mosaic.isMissing(x, y)){
                        final TileReference tile = mosaic.getTile(x, y, null);
                        final Rectangle tileRect = new Rectangle(x*this.getTileWidth(), y*this.getTileHeight(), this.getTileWidth(), this.getTileHeight());

                        final int minX, maxX, minY, maxY;
                        minX = XMath.clamp(rect.x, tileRect.x, tileRect.x + tileRect.width);
                        maxX = XMath.clamp(rect.x+rect.width, tileRect.x, tileRect.x+tileRect.width);
                        minY = XMath.clamp(rect.y,            tileRect.y, tileRect.y+tileRect.height);
                        maxY = XMath.clamp(rect.y+rect.height,tileRect.y, tileRect.y+tileRect.height);

                        final Rectangle rectIn = new Rectangle(minX, minY, maxX-minX, maxY-minY);
                        rectIn.translate(-tileRect.x, -tileRect.y);
                        final Rectangle rectOut = new Rectangle(minX, minY, maxX-minX, maxY-minY);
                        rectOut.translate(-rect.x, -rect.y);

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

    @Override
    public WritableRaster copyData(WritableRaster raster) {
        return null;
    }
}
