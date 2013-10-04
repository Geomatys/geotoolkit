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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.collection.Cache;
import org.geotoolkit.math.XMath;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.opengis.geometry.Envelope;
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

    private final Cache<Point,Raster> tileCache = new Cache<>(10, 12, true);

    private final GridMosaic mosaic;
    private RenderedImage firstTileImage = null;
    private DataBuffer emptyBuffer = null;
    private SampleModel sampleModel = null;

    private int width;
    private int height;
    private int nbXTiles;
    private int nbYTiles;
    private int tileWidth;
    private int tileHeight;

    public GridMosaicRenderedImage(final GridMosaic mosaic) {
        if(mosaic.getGridSize().width == 0 || mosaic.getGridSize().height == 0){
            throw new IllegalArgumentException("Mosaic grid can not be empty.");
        }

        this.mosaic = mosaic;
        this.nbXTiles = mosaic.getGridSize().width;
        this.nbYTiles = mosaic.getGridSize().height;
        this.tileWidth = mosaic.getTileSize().width;
        this.tileHeight = mosaic.getTileSize().height;
        this.width = nbXTiles * tileWidth;
        this.height = nbYTiles * tileHeight;

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
            sampleModel = this.getColorModel().createCompatibleSampleModel(this.width, this.height);
        }

        return sampleModel;
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
                final Point offset = new Point(tileX*tileWidth, tileY*tileHeight);
                raster = Raster.createWritableRaster(firstTileImage.getSampleModel(), buffer , offset);
                this.tileCache.put(new Point(tileX, tileY), raster);

            } catch ( DataStoreException | IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }

        return raster;
    }

    @Override
    public Raster getData() {
        Raster rasterOut = this.getColorModel().createCompatibleWritableRaster(this.width, this.height);

        // Clear dataBuffer to 0 value for all bank
        for (int s=0; s<rasterOut.getDataBuffer().getSize(); s++){
            for (int b=0; b<rasterOut.getDataBuffer().getNumBanks(); b++){
                rasterOut.getDataBuffer().setElem(b, s, 0);
            }
        }

        try {

            for (int y=0; y<this.nbYTiles; y++){
                for (int x=0; x<this.nbXTiles; x++){
                    if (!mosaic.isMissing(x, y)){
                        final TileReference tile = mosaic.getTile(x,y,null);
                        final RenderedImage sourceImg;

                        if (tile.getInput() instanceof RenderedImage) {
                            sourceImg = (RenderedImage) tile.getInput();
                        } else {
                            sourceImg = tile.getImageReader().read(tile.getImageIndex());
                        }

                        final Raster rasterIn = sourceImg.getData();

                        rasterOut.getSampleModel().setDataElements(x*tileWidth, y*tileHeight, tileWidth, tileHeight,
                                rasterIn.getSampleModel().getDataElements(0, 0, tileWidth, tileHeight, null, rasterIn.getDataBuffer()),
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

            for (int y=Math.max(upperLeftPosition.y,0); y<Math.min(lowerRightPosition.y+1,this.nbYTiles); y++){
                for (int x=Math.max(upperLeftPosition.x,0); x<Math.min(lowerRightPosition.x+1, this.nbXTiles); x++){
                    if (!mosaic.isMissing(x, y)){
                        final TileReference tile = mosaic.getTile(x, y, null);
                        final Rectangle tileRect = new Rectangle(x*tileWidth, y*tileHeight, tileWidth, tileHeight);

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

    public Raster getData(Envelope env) throws FactoryException, TransformException {
        final Envelope envMosaic = this.mosaic.getEnvelope();
        final Envelope envOut;
        if (!CRS.equalsApproximatively(env.getCoordinateReferenceSystem(), envMosaic.getCoordinateReferenceSystem())){
            envOut = CRS.transform(env, envMosaic.getCoordinateReferenceSystem());
        } else {
            envOut = env;
        }

        Rectangle rect = new Rectangle(
                (int)((envOut.getMinimum(0)-envMosaic.getMinimum(0))/envMosaic.getSpan(0)*this.getWidth()),
                (int)((envMosaic.getMaximum(1)-envOut.getMaximum(1))/envMosaic.getSpan(1)*this.getHeight()),
                (int)(envOut.getSpan(0)/envMosaic.getSpan(0)*this.getWidth()),
                (int)(envOut.getSpan(1)/envMosaic.getSpan(1)*this.getHeight())
        );

        return this.getData(rect);
    }

    private Point getPositionOf(int x, int y){

        final int posX = (int)(Math.floor(x/tileWidth));
        final int posY = (int)(Math.floor(y/tileHeight));

        return new Point(posX, posY);
    }

    @Override
    public WritableRaster copyData(WritableRaster raster) {
        return null;
    }
}
