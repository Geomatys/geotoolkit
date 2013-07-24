/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.io.large;

import org.geotoolkit.image.io.IllegalImageDimensionException;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.FileUtilities;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.media.jai.RasterFactory;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Stock all {@link java.awt.image.Raster} contained from define {@link java.awt.image.RenderedImage}.
 *
 * @author Rémi Maréchal (Geomatys).
 * @author Alexis Manin  (Geomatys).
 */
class LargeArray {

    private static final String TEMPORARY_PATH = System.getProperty("java.io.tmpdir");
    private static final String FORMAT = "tiff";
    private static final Point WPOINT = new Point(0, 0);
    private static final Point RPOINT = new Point();

    private long memoryCapacity;
    private long remainingCapacity;
    private final int minTileX;
    private final int minTileY;
    private final int numTilesX;
    private final int numTilesY;
    private final QuadTreeDirectory qTD;
    private final String dirPath;
    private final ColorModel cm;
    private final int riMinX;
    private final int riMinY;
    private final int riTileWidth;
    private final int riTileHeight;
    private final int dataTypeWeight;

    private final ImageReader imgReader;
    private final ImageWriter imgWriter;
    private final boolean isWritableRenderedImage;

    private final LargeRaster[] array;

    private final LinkedList<Integer> stack = new LinkedList<Integer>();

    /**
     * <p>List which contain {@link java.awt.image.Raster} from {@link java.awt.image.RenderedImage} owner.<br/>
     * If somme of {@link java.awt.image.Raster} weight within array exceed memory capacity, {@link java.awt.image.Raster} are stored
     * on hard disk at appropriate quad tree emplacement in temporary system directory.<br/><br/>
     *
     * Note : {@link java.awt.image.Raster} are stored in tiff format to avoid onerous, compression decompression, cost during disk writing reading.</p>
     *
     * @param ri {@link java.awt.image.RenderedImage} which contain all raster in array.
     * @param memoryCapacity storage capacity in Byte.
     * @throws java.io.IOException if impossible to create {@link javax.imageio.ImageReader} or {@link javax.imageio.ImageWriter}.
     */
    LargeArray(RenderedImage ri, long memoryCapacity) throws IOException {
        //cache properties.
        this.memoryCapacity = memoryCapacity;
        isWritableRenderedImage = ri instanceof WritableRenderedImage;
        //image owner properties.
        this.cm                = ri.getColorModel();
        this.remainingCapacity = memoryCapacity;
        this.numTilesX = ri.getNumXTiles();
        this.numTilesY = ri.getNumYTiles();
        this.riMinX            = ri.getMinX();
        this.riMinY            = ri.getMinY();
        this.riTileWidth       = ri.getTileWidth();
        this.riTileHeight      = ri.getTileHeight();
        this.minTileX      = ri.getMinTileX();
        this.minTileY      = ri.getMinTileY();

        //quad tree directory architecture.
        this.dirPath = TEMPORARY_PATH + "/img_"+ri.hashCode();
        this.qTD     = new QuadTreeDirectory(dirPath, numTilesX, numTilesY, FORMAT, true);
        qTD.create4rchitecture();

        //reader writer
        this.imgReader = XImageIO.getReaderByFormatName(FORMAT, null, Boolean.FALSE, Boolean.TRUE);
        this.imgWriter = XImageIO.getWriterByFormatName(FORMAT, null, null);

        final int datatype = cm.createCompatibleSampleModel(riTileWidth, riTileHeight).getDataType();
        switch (datatype) {
            case DataBuffer.TYPE_BYTE      : dataTypeWeight = 1; break;
            case DataBuffer.TYPE_DOUBLE    : dataTypeWeight = 8; break;
            case DataBuffer.TYPE_FLOAT     : dataTypeWeight = 4; break;
            case DataBuffer.TYPE_INT       : dataTypeWeight = 4; break;
            case DataBuffer.TYPE_SHORT     : dataTypeWeight = 2; break;
            case DataBuffer.TYPE_UNDEFINED : dataTypeWeight = 8; break;
            case DataBuffer.TYPE_USHORT    : dataTypeWeight = 2; break;
            default : throw new IllegalStateException("unknown raster data type");
        }
        
        array = new LargeRaster[numTilesX*numTilesY];
    }

    /**
     * Add a {@link java.awt.image.Raster} in array and check array to don't exceed memory capacity.
     *
     * @param tileX mosaic index in X direction of raster will be stocked.
     * @param tileY mosaic index in Y direction of raster will be stocked.
     * @param raster raster will be stocked in array.
     * @throws java.io.IOException if an error occurs during writing.
     */
    void add(int tileX, int tileY, WritableRaster raster) throws IOException {
        final int tX = tileX - minTileX;
        final int tY = tileY - minTileY;
        final long rastWeight = getRasterWeight(raster);
        if (rastWeight > memoryCapacity) throw new IllegalImageDimensionException("raster too large");
        final int indice = getIndice(tX, tY);
        array[indice] = new LargeRaster(tX, tY, rastWeight, checkRaster(raster, tX, tY));
        stack.addLast(indice);
        remainingCapacity -= rastWeight;
        checkArray();
    }

    /**
     * Remove {@link java.awt.image.Raster} at tileX tileY mosaic coordinates.
     *
     * @param tileX mosaic index in X direction.
     * @param tileY mosaic index in Y direction.
     */
    void remove(int tileX, int tileY) {
        final int tX = tileX - minTileX;
        final int tY = tileY - minTileY;
        final int indice = getIndice(tX, tY);
        array[indice] = null;
        stack.removeFirstOccurrence(indice);
        //quad tree
        final File removeFile = new File(qTD.getPath(tX, tY));
        //delete on hard disk if exist.
        if (removeFile.exists()) removeFile.delete();
    }

    /**
     * Return {@link java.awt.image.Raster} at tileX tileY mosaic coordinates.
     *
     * @param tileX mosaic index in X direction.
     * @param tileY mosaic index in Y direction.
     * @return Raster at tileX tileY mosaic coordinates.
     * @throws java.io.IOException if an error occurs during reading..
     */
    Raster getRaster(int tileX, int tileY) throws IOException {
        Raster res = null;
        
        final int tX = tileX - minTileX;
        final int tY = tileY - minTileY;

        LargeRaster lRaster;
        if ((lRaster = array[tY * numTilesX + tX]) == null) {
            final File getFile = new File(qTD.getPath(tX, tY));

            if (getFile.exists()) {
                imgReader.setInput(ImageIO.createImageInputStream(getFile));
                final BufferedImage buff = imgReader.read(0);
                imgReader.dispose();
                //add in cache array.
                res = buff.getRaster();
                final int indice = getIndice(tX, tY);
                final long rastWeight = getRasterWeight(res);
                array[indice] = new LargeRaster(tX, tY, rastWeight, checkRaster((WritableRaster)res, tX, tY));
                stack.addLast(indice);
                remainingCapacity -= rastWeight;
                checkArray();
            }
        } else {
            res = lRaster.getRaster();
        }
        
        return res;
    }

    /**
     * Return all {@link java.awt.image.Raster} within this cache system.
     *
     * @return all raster within this cache system.
     * @throws java.io.IOException if impossible to read raster from disk.
     */
    Raster[] getTiles() throws IOException {
        Raster[] result = new Raster[array.length];
        int rID = 0;
        for (LargeRaster lr : array) if (lr != null) result[rID++] = lr.getRaster();
        return Arrays.copyOf(result, rID);
    }

    /**
     * Remove all file and directory relevant to this cache system.
     */
    void removeTiles() {
        remainingCapacity = memoryCapacity;
        for (int i = 0 ; i < array.length ; i++) {
            array[i] = null;
        }
        stack.clear();
        final File removeFile = new File(dirPath);
        FileUtilities.deleteDirectory(removeFile);
    }

    /**
     * Affect a new memory capacity and update {@link java.awt.image.Raster} array from new memory capacity set.
     *
     * @param memoryCapacity new memory capacity.
     * @throws org.geotoolkit.image.io.IllegalImageDimensionException if capacity is too low from raster weight.
     * @throws java.io.IOException if impossible to write raster on disk.
     */
    void setCapacity(long memoryCapacity) throws IllegalImageDimensionException, IOException {
        ArgumentChecks.ensurePositive("LargeList : memory capacity", memoryCapacity);
        final long diff    = this.memoryCapacity - memoryCapacity;
        remainingCapacity -= diff;
        checkArray();
        this.memoryCapacity = memoryCapacity;
    }

    /**
     * Define the weight of a {@link java.awt.image.Raster}.
     *
     * @param raster raster which will be weigh.
     * @return raster weight.
     */
    private long getRasterWeight(Raster raster) {
        final SampleModel rsm = raster.getSampleModel();
        final int width = (rsm instanceof ComponentSampleModel) ? ((ComponentSampleModel) rsm).getScanlineStride() : raster.getWidth()*rsm.getNumDataElements();
        return width * raster.getHeight() * dataTypeWeight;
    }

    /**
     * Write {@link java.awt.image.Raster} on hard disk.
     *
     * @param path emplacement to write.
     * @param raster raster which will be writing.
     * @throws java.io.IOException if impossible to write raster on disk.
     */
    private void writeRaster(File path, Raster raster) throws IOException {
        final WritableRaster wr  = RasterFactory.createWritableRaster(raster.getSampleModel(), raster.getDataBuffer(), WPOINT);
        final BufferedImage rast = new BufferedImage(cm, wr, true, null);
        imgWriter.setOutput(ImageIO.createImageOutputStream(path));
        imgWriter.write(rast);
        imgWriter.dispose();
        path.deleteOnExit();
    }

    /**
     * Write {@link java.awt.image.Raster} within {@link org.geotoolkit.image.io.large.LargeRaster} object on hard disk at appropriate quad tree emplacement.
     *
     *
     * @param raster object which contain raster.
     * @throws java.io.IOException if impossible to write raster on disk.
     */
    private void writeRaster(WritableRaster raster, int tileX, int tileY) throws IOException {
        final File file = new File(qTD.getPath(tileX, tileY));
        if (!file.exists() || isWritableRenderedImage) {
            writeRaster(file, raster);
        }
    }


    /**
     * <p>Verify that {@link java.awt.image.Raster} coordinate is agree from {@link java.awt.image.RenderedImage} location.<br/>
     * If location is correct return {@link java.awt.image.Raster} else return new {@link java.awt.image.Raster} with correct<br/>
     * location but with same internal value from {@link java.awt.image.Raster}.</p>
     *
     * @param raster raster will be checked.
     * @param tx tile location within renderedImage owner in X direction.
     * @param ty tile location within renderedImage owner in Y direction.
     * @return raster with correct coordinate from its image owner.
     */
    private WritableRaster checkRaster(WritableRaster raster, int tx, int ty) {
        final int mx = riTileWidth  * tx + riMinX;
        final int my = riTileHeight * ty + riMinY;
        if (raster.getMinX() != mx || raster.getMinY() != my) {
            RPOINT.setLocation(mx, my);
            return Raster.createWritableRaster(raster.getSampleModel(), raster.getDataBuffer(), RPOINT);
        }
        return raster;
    }

    /**
     * <p>Verify that array weight do not exceed memory capacity.<br/>
     * If memory capacity is exceed write {@link java.awt.image.Raster} on hard disk up to don't exceed memory capacity.</p>
     *
     * @throws org.geotoolkit.image.io.IllegalImageDimensionException if raster too large for this Tilecache.
     * @throws java.io.IOException if impossible to write raster.
     */
    private void checkArray() throws IOException {
        while (remainingCapacity < 0) {
            final int toRemove = stack.getFirst();
            final LargeRaster raster = array[toRemove];
            remainingCapacity   += raster.getWeight();
            //quad tree
            writeRaster((WritableRaster)raster.getRaster(), raster.getGridX(), raster.getGridY());
        }
    }
    
    private int getIndice(int tileX, int tileY) {
        return tileY*numTilesX+tileX;
    }
}

