/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.image.io.large;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.media.jai.RasterFactory;
import javax.media.jai.TileCache;

/**
 *
 * @author rmarech
 */
public class LargeRenderedImage implements RenderedImage {

    private final TileCache tilecache;

    private static final int DEFAULT_TILE_SIZE = 256;
    private static final int MIN_TILE_SIZE = 64;

    private static final long DEFAULT_MEMORY_CAPACITY = 16000000;
    private final int minIndex;
    private final int numImages;
    private final ImageReader imageReader;
//    private final Boolean isTiled;
//    private final int tileWidth;
//    private final int tileHeight;
    private final int imageIndex;
    private final int width;
    private final int height;
    private final int tileWidth;
    private final int tileHeight;
    private final int tileGridXOffset;
    private final int tileGridYOffset;
//    private final boolean isTiled;

//    private final int tileGridXOffset;
//    private final int tileGridYOffset;
    private Vector<RenderedImage> vector = null;
    private final ImageReadParam imgParam;

    private final int nbrTileX;
    private final int nbrTileY;

    private final boolean[][] isRead;
    private final int[][] debug;

    private final Rectangle srcRegion = new Rectangle();

    private ColorModel cm = null;
    private SampleModel sm = null;

    public LargeRenderedImage(ImageReader imageReader, int imageIndex, TileCache tilecache, Dimension tileSize) throws IOException {
        this.imageReader = imageReader;
        this.imageIndex  = imageIndex;
        this.minIndex    = imageReader.getMinIndex();
        this.imgParam    = new ImageReadParam();
        this.numImages   = imageReader.getNumImages(true);
        this.width       = imageReader.getWidth(imageIndex);
        this.height      = imageReader.getHeight(imageIndex);
        if (tilecache != null) {
            this.tilecache = tilecache;
        } else {
            this.tilecache = LargeCache.getInstance(DEFAULT_MEMORY_CAPACITY);
        }
        this.tileGridXOffset = 0;
        this.tileGridYOffset = 0;
        if (tileSize != null) {
            tileWidth  = Math.min(Math.max(MIN_TILE_SIZE, tileSize.width),  DEFAULT_TILE_SIZE);
            tileHeight = Math.min(Math.max(MIN_TILE_SIZE, tileSize.height), DEFAULT_TILE_SIZE);
        } else {
            tileWidth = tileHeight = DEFAULT_TILE_SIZE;
        }
        this.nbrTileX = (width + tileWidth - 1)   / tileWidth;
        this.nbrTileY = (height + tileHeight - 1) / tileHeight;
        isRead = new boolean[nbrTileY][nbrTileX];
        debug = new int[nbrTileY][nbrTileX];
        for (boolean[] bool : isRead) {
            Arrays.fill(bool, false);
        }

//        this.isTiled        = imageReader.isImageTiled(imageIndex);
//        if (isTiled) {
//            tileWidth       = imageReader.getTileWidth(imageIndex);
//            tileHeight      = imageReader.getTileHeight(imageIndex);
//            tileGridXOffset = imageReader.getTileGridXOffset(imageIndex);
//            tileGridYOffset = imageReader.getTileGridYOffset(imageIndex);
//        } else {
//            tileWidth       = DEFAULT_TILE_SIZE;
//            tileHeight      = DEFAULT_TILE_SIZE;
//            tileGridXOffset = 0;
//            tileGridYOffset = 0;
//        }
    }

    @Override
    public Vector<RenderedImage> getSources() {
        if (vector != null) return vector;
        vector = new Vector<RenderedImage>(numImages);
        for (int id = minIndex; id < numImages; id++) {
            try {
                vector.add(new LargeRenderedImage(imageReader, id, tilecache, new Dimension(tileWidth, tileHeight)));
            } catch (IOException ex) {
                Logger.getLogger(LargeRenderedImage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return vector;
    }

    @Override
    public Object getProperty(String name) {
        return Image.UndefinedProperty;
    }

    @Override
    public String[] getPropertyNames() {
        return null;
    }

    @Override
    public ColorModel getColorModel() {
        if (cm == null) getTile(0, 0);
        return cm;
    }

    @Override
    public SampleModel getSampleModel() {
//        if (sm == null) sm = getColorModel().createCompatibleSampleModel(width, height);
        if (sm == null) sm = getColorModel().createCompatibleSampleModel(tileWidth, tileHeight);
        return sm;
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
        return nbrTileX;
    }

    @Override
    public int getNumYTiles() {
        return nbrTileY;
    }

    @Override
    public int getMinTileX() {
        return (int) - (tileGridXOffset + (tileWidth - 1) * Math.signum(tileGridXOffset)) / tileWidth;
    }

    @Override
    public int getMinTileY() {
        return (int) - (tileGridYOffset + (tileHeight - 1) * Math.signum(tileGridYOffset)) / tileHeight;
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
        return tileGridXOffset;
    }

    @Override
    public int getTileGridYOffset() {
        return tileGridYOffset;
    }

    @Override
    public Raster getTile(int tileX, int tileY) {
        if (isRead[tileY][tileX]) {
            final Raster rast = tilecache.getTile(this, tileX, tileY);
            return Raster.createWritableRaster(rast.getSampleModel(), rast.getDataBuffer(), new Point(tileX*tileWidth, tileY*tileHeight));
        }
        // si elle na pas ete demandée :
        // 1 : la demandée au reader
        final int minRx = tileX*tileWidth;
        final int minRy = tileY*tileHeight;
        int wRx = Math.min(minRx + tileWidth, width) - minRx;
        int hRy = Math.min(minRy + tileHeight, height) - minRy;
        srcRegion.setBounds(minRx, minRy, wRx, hRy);
        imgParam.setSourceRegion(srcRegion);
        BufferedImage buff = null;
        try {
            buff = imageReader.read(imageIndex, imgParam);
        } catch (IOException ex) {
            Logger.getLogger(LargeRenderedImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 2 : la setter au tile cache
        if (cm == null) cm = buff.getColorModel();
        final WritableRaster wRaster = Raster.createWritableRaster(buff.getSampleModel(), buff.getRaster().getDataBuffer(), new Point(tileX*tileWidth, tileY*tileHeight));
        tilecache.add(this, tileX, tileY, wRaster);
        isRead[tileY][tileX] = true;
        return wRaster;
    }

    @Override
    public Raster getData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Raster getData(Rectangle rect) {

//        System.out.println(rect);
//        return null;
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WritableRaster copyData(WritableRaster raster) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void finalize() throws Throwable {
        tilecache.removeTiles(this);
        super.finalize();
    }
}
