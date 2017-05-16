 /*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.image;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.TileObserver;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.util.Vector;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;

/**
 * A l'arrache.com
 * @author rmarechal
 */
public class WritableMemoryRenderedImage implements WritableRenderedImage {

    /**
     * Default tile size.
     */
    private static final int DEFAULT_TILE_SIZE = 256;

    /**
     * Minimum required tile size.
     */
    private static final int MIN_TILE_SIZE = 64;

    /**
     * Upper left corner of all image tiles.
     */
    private Point[] tileIndices = null;

    /**
     * Image attributes.
     */
    private final Raster[][] tiles;

    /**
     * Image attributs.
     */
    private final int minX;
    private final int minY;
    private final int width;
    private final int height;
    private final int tileWidth;
    private final int tileHeight;
    private final int tileGridXOffset;
    private final int tileGridYOffset;
    private int minTileGridX;
    private int minTileGridY;
    private final int nbrTileX;
    private final int nbrTileY;
    private ColorModel cm;
    private SampleModel sm;

    /**
     * Create {@link WritableLargeRenderedImage} with default upper corner at position (0, 0),
     * a default tile size of 256 x 256 pixels and a default tile grid offset at position (0, 0).
     *
     * @param width
     * @param height
     * @param sampleModel
     * @param colorModel
     */
    public WritableMemoryRenderedImage(final int width, final int height, final SampleModel sampleModel, final ColorModel colorModel) {
        this.minX = 0;
        this.minY = 0;
        this.width = width;
        this.height = height;
        this.tileWidth = DEFAULT_TILE_SIZE;
        this.tileHeight = DEFAULT_TILE_SIZE;
        this.tileGridXOffset = 0;
        this.tileGridYOffset = 0;
        this.nbrTileX = (int) StrictMath.ceil(width / tileWidth);
        this.nbrTileY = (int) StrictMath.ceil(height / tileHeight);
        tiles = new WritableRaster[nbrTileY][nbrTileX];
        this.cm = colorModel;
        this.sm = sampleModel;
    }

    public WritableMemoryRenderedImage(int minX, int minY, int width, int height,
                                       int tileWidth, int tileHeight, int tileGridXOffset, int tileGridYOffset,
                                       int nbrTileX, int nbrTileY, ColorModel cm, SampleModel sm) {
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileGridXOffset = tileGridXOffset;
        this.tileGridYOffset = tileGridYOffset;
        this.nbrTileX = nbrTileX;
        this.nbrTileY = nbrTileY;
        tiles = new WritableRaster[nbrTileY][nbrTileX];
        this.cm = cm;
        this.sm = sm;
    }

    /**
     * {@inheritDoc }.<br>
     * note : not implemented yed.
     */
    @Override
    public void addTileObserver(TileObserver to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.<br>
     * note : not implemented yed.
     */
    @Override
    public void removeTileObserver(TileObserver to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public WritableRaster getWritableTile(int tileX, int tileY) {
        final int arrayIndexX = tileX - minTileGridX;
        final int arrayIndexY = tileY - minTileGridY;
        if (tiles[arrayIndexY][arrayIndexX] == null) {
            tiles[arrayIndexY][arrayIndexX] = Raster.createWritableRaster(sm, new Point(minX + arrayIndexX * tileWidth, minY + arrayIndexY * tileHeight));
        }
        if (!(tiles[arrayIndexY][arrayIndexX] instanceof WritableRaster)) {
            final Raster r = tiles[arrayIndexY][arrayIndexX];
            //-- re-copy
            tiles[arrayIndexY][arrayIndexX] = Raster.createWritableRaster(r.getSampleModel(), r.getDataBuffer(), new Point(r.getMinX(), r.getMinY()));
        }
        return (WritableRaster) tiles[arrayIndexY][arrayIndexX];
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void releaseWritableTile(int tileX, int tileY) {
        //-- all tiles in memory do nothing
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean isTileWritable(int tileX, int tileY) {
        return true;
    }

    @Override
    public Point[] getWritableTileIndices() {
        if (tileIndices == null) {
            tileIndices = new Point[nbrTileX*nbrTileY];
            int idTab = 0;
            for (int ty = minTileGridY, tmy = minTileGridY+nbrTileY; ty < tmy; ty++) {
                for (int tx = minTileGridX, tmx = minTileGridX+nbrTileX; tx < tmx; tx++) {
                    tileIndices[idTab++] = new Point(tx, ty);
                }
            }
        }
        return tileIndices;
    }


    /**
     * {@inheritDoc }.<br>
     * note : not implemented yed.
     */
    @Override
    public boolean hasTileWriters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setData(Raster r) {

        final int rminX = r.getMinX();
        final int rminY = r.getMinY();
        final int rw    = r.getWidth();
        final int rh    = r.getHeight();
        final int rmaxX = rminX+rw;
        final int rmaxY = rminY+rh;

        if (Math.abs(minX-rminX) % tileWidth != 0)
            throw new IllegalArgumentException("raster minX value don't tie in tile coordinate");

        if (Math.abs(minY-rminY) % tileHeight != 0)
            throw new IllegalArgumentException("raster minY value don't tie in tile coordinate");

        final int ix  = Math.max(rminX, minX);
        final int imx = Math.min(rmaxX, minX+width);
        final int iy  = Math.max(rminY, minY);
        final int imy = Math.min(rmaxY, minY+height);
        if (imx <= ix || imy <= iy) {
            throw new IllegalArgumentException("raster is not within image boundary");
        }
        if ((imx-ix) != rw || (imy-iy) != rh) {
            throw new IllegalArgumentException("raster boundary don't tie in tile coordinate");
        }
        if (r.getSampleModel().getDataType() != sm.getDataType()) {
            throw new IllegalArgumentException("raster datatype don't tie with image datatype");
        }
        final int tx  = (rminX-minX) / tileWidth;
        final int ty  = (rminY-minY) / tileHeight;
        tiles[ty][tx] = (WritableRaster) r;
    }

    /**
     * {@inheritDoc }.<br>
     * note : not implemented yed.
     */
    @Override
    public Vector<RenderedImage> getSources() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.<br>
     * note : not implemented yed.
     */
    @Override
    public Object getProperty(String name) {
        return Image.UndefinedProperty;
    }

    /**
     * {@inheritDoc }.<br>
     * note : not implemented yed.
     */
    @Override
    public String[] getPropertyNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public ColorModel getColorModel() {
        return cm;
    }

    /**
     * Set a new {@link ColorModel} in this {@linkplain WritableMemoryRenderedImage WritableRenderedImage} implementation.
     *
     * @param colorModel the new setted {@link ColorModel}.
     */
    public void setColorModel(final ColorModel colorModel) {
        cm = colorModel;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public SampleModel getSampleModel() {
        return sm;
    }

    /**
     * Set a new {@link SampleModel} in this {@linkplain WritableMemoryRenderedImage WritableRenderedImage} implementation.
     *
     * @param sampleModel the new setted {@link SampleModel}.
     */
    public void setSampleModel(final SampleModel sampleModel) {
        sm = sampleModel;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getMinX() {
        return minX;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getMinY() {
        return minY;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getNumXTiles() {
        return nbrTileX;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getNumYTiles() {
        return nbrTileY;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getMinTileX() {
        return minTileGridX;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getMinTileY() {
        return minTileGridY;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getTileGridXOffset() {
        return tileGridXOffset;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public int getTileGridYOffset() {
        return tileGridYOffset;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Raster getTile(int tileX, int tileY) {
        return tiles[tileY - tileGridYOffset][tileX - tileGridXOffset];
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Raster getData() {
        final WritableRaster wr = Raster.createWritableRaster(cm.createCompatibleSampleModel(width, height), new Point(minX, minY));
        final Rectangle rect = new Rectangle();
        int my = minY;
        for (int ty = 0, tmy = nbrTileY; ty < tmy; ty++) {
            int mx = minX;
            for (int tx = 0, tmx = nbrTileX; tx < tmx; tx++) {
                final Raster r = tiles[ty][tx];
                rect.setBounds(mx, my, tileWidth, tileHeight);
                //recopie
                final PixelIterator copix = PixelIteratorFactory.createDefaultWriteableIterator(wr, wr, rect);
                final PixelIterator pix   = PixelIteratorFactory.createDefaultIterator(r, rect);
                while (copix.next()) {
                    pix.next();
                    copix.setSampleDouble(pix.getSampleDouble());
                }
                mx += tileWidth;
            }
            my += tileHeight;
        }
        return wr;
    }

    /**
     * {@inheritDoc }.<br>
     * note : not implemented yed.
     */
    @Override
    public Raster getData(Rectangle rect) {
        throw new UnsupportedOperationException("WritableMemoryRenderedImage.getData(Rectangle) : Not supported yet.");
    }

    /**
     * {@inheritDoc }.<br>
     * note : not implemented yed.
     */
    @Override
    public WritableRaster copyData(WritableRaster raster) {
        throw new UnsupportedOperationException("WritableMemoryRenderedImage.copyData : Not supported yet.");
    }
}
