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

import org.geotoolkit.image.io.large.LargeCache;
import org.geotoolkit.util.logging.Logging;

import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStoreException;

/**
 * Implementation of RenderedImage using GridMosaic.
 * With this a GridMosaic can be see as a RenderedImage.
 *
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class GridMosaicRenderedImage implements RenderedImage {

    private static final Logger LOGGER = Logging.getLogger(GridMosaicRenderedImage.class);

    private final LargeCache tilecache;

    private final GridMosaic mosaic;
    private final boolean emptyMosaic;
    private RenderedImage firstTileImage = null;
    private Point firstTilePosition = null;
    private DataBuffer nanBuffer = null;

    private int width;
    private int height;
    private int nbXTiles;
    private int nbYTiles;
    private int tileWidth;
    private int tileHeight;

    public GridMosaicRenderedImage(final GridMosaic mosaic) {
        this.mosaic = mosaic;
        this.tilecache = LargeCache.getInstance(64000000); //tile cache with default size
        this.emptyMosaic = (mosaic.getGridSize().equals(new Dimension(0,0)));
        this.nbXTiles = mosaic.getGridSize().width;
        this.nbYTiles = mosaic.getGridSize().height;
        this.tileWidth = mosaic.getTileSize().width;
        this.tileHeight = mosaic.getTileSize().height;
        this.width = nbXTiles * tileWidth;
        this.height = nbYTiles * tileHeight;


        if (!emptyMosaic) {
            try {
                //search the first non missing tile of the Mosaic
                TileReference tile = null;

                exitLoop :
                if (tile == null) {
                    for (int y=0; y<nbYTiles; y++){
                        for (int x=0; x<nbXTiles; x++){
                            if (mosaic.isMissing(x,y)) {
                                continue;
                            } else {
                                tile = mosaic.getTile(x,y, null);
                                firstTilePosition = new Point(x,y);
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

                    final int bufferSize = tileWidth * tileHeight * firstTileImage.getSampleModel().getNumBands();
                    final float[] buffArray = new float[bufferSize];
                    Arrays.fill(buffArray, Float.NaN);
                    nanBuffer = new DataBufferFloat(bufferSize);
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("First tile can't be read.", e);
            } catch (DataStoreException e) {
                throw new IllegalArgumentException("Input mosaic doesn't have any tile.", e);
            }
        }
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
        if (firstTileImage != null) {
            return firstTileImage.getSampleModel();
        }

        return null;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
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
        return  nbXTiles;
    }

    @Override
    public int getNumYTiles() {
        return  nbXTiles;
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
        return tileWidth;
    }

    @Override
    public int getTileHeight() {
        return tileHeight;
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
            raster = this.tilecache.getTile(this, tileX, tileY);
        } catch (IllegalArgumentException ex) {
            raster = null;
        }

        if (raster == null) {
            try {
                DataBuffer buffer = nanBuffer;

                //optimization return the first non missing tile data if requested
                if (firstTilePosition != null && firstTilePosition.equals(new Point(tileX,tileY))) {
                    buffer = firstTileImage.getData().getDataBuffer();
                } else {

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
                }

                //create a raster from tile image with tile position offset.
                LOGGER.log(Level.FINE, "Request tile {0}:{1} ", new Object[]{tileX,tileY});
                final Point offset = new Point(tileX*tileWidth, tileY*tileHeight);
                raster = Raster.createWritableRaster(getSampleModel(), buffer , offset);
                this.tilecache.add(this, tileX, tileY, raster);

            } catch (DataStoreException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }

        return raster;
    }

    @Override
    public Raster getData() {
        return null;
    }

    @Override
    public Raster getData(Rectangle rect) {
        return null;
    }

    @Override
    public WritableRaster copyData(WritableRaster raster) {
        return null;
    }
}
