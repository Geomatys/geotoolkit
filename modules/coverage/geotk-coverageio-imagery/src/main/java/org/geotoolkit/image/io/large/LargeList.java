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

import java.awt.Point;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.media.jai.RasterFactory;
import org.geotoolkit.image.io.IllegalImageDimensionException;
import org.geotoolkit.image.io.XImageIO;
import org.apache.sis.util.ArgumentChecks;

/**
 * Stock all {@link Raster} contained from define {@link RenderedImage}.
 *
 * @author Rémi Maréchal (Geomatys).
 */
class LargeList {

    private static final String TEMPORARY_PATH = System.getProperty("java.io.tmpdir");
    private static final String FORMAT = "tiff";
    private static final Point WPOINT = new Point(0, 0);
    private static final Point RPOINT = new Point();

    private long memoryCapacity;
    private long remainingCapacity;
    private final LinkedList<LargeRaster> list;
    private final int minTileX;
    private final int minTileY;
    private final int numXTiles;
    private final int numYTiles;
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


    /**
     * <p>List which contain {@link Raster} from {@link RenderedImage} owner.<br/>
     * If somme of {@link Raster} weight within list exceed memory capacity, {@link Raster} are stored
     * on hard disk at appropriate quad tree emplacement in temporary system directory.<br/><br/>
     *
     * Note : {@link Raster} are stored in tiff format to avoid onerous, compression decompression, cost during disk writing reading.</p>
     *
     * @param ri {@link RenderedImage} which contain all raster in list.
     * @param memoryCapacity storage capacity in Byte.
     * @throws IOException if impossible to create {@link ImageReader} or {@link ImageWriter}.
     */
    LargeList(RenderedImage ri, long memoryCapacity) throws IOException {
        //cache properties.
        this.list           = new LinkedList<LargeRaster>();
        this.memoryCapacity = memoryCapacity;
        isWritableRenderedImage = ri instanceof WritableRenderedImage;
        //image owner properties.
        this.cm                = ri.getColorModel();
        this.remainingCapacity = memoryCapacity;
        this.numXTiles         = ri.getNumXTiles();
        this.numYTiles         = ri.getNumYTiles();
        this.riMinX            = ri.getMinX();
        this.riMinY            = ri.getMinY();
        this.riTileWidth       = ri.getTileWidth();
        this.riTileHeight      = ri.getTileHeight();
        this.minTileX      = ri.getMinTileX();
        this.minTileY      = ri.getMinTileY();

        //quad tree directory architecture.
        this.dirPath = TEMPORARY_PATH + "/img_"+ri.hashCode();
        this.qTD     = new QuadTreeDirectory(dirPath, numXTiles, numYTiles, FORMAT, true);
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
            default : throw new IllegalStateException("unknow raster data type");
        }
    }

    /**
     * Add a {@link Raster} in list and check list to don't exceed memory capacity.
     *
     * @param tileX mosaic index in X direction of raster will be stocked.
     * @param tileY mosaic index in Y direction of raster will be stocked.
     * @param raster raster will be stocked in list.
     * @throws IOException if an error occurs during writing.
     */
    void add(int tileX, int tileY, WritableRaster raster) throws IOException {
        final int tX = tileX - minTileX;
        final int tY = tileY - minTileY;
        final long rastWeight = getRasterWeight(raster);
        if (rastWeight > memoryCapacity) throw new IllegalImageDimensionException("raster too large");
        list.addLast(new LargeRaster(tX, tY, rastWeight, checkRaster(raster, tX, tY)));
        remainingCapacity -= rastWeight;
        checkList();
    }

    /**
     * Remove {@link Raster} at tileX tileY mosaic coordinates.
     *
     * @param tX mosaic index in X direction.
     * @param tY mosaic index in Y direction.
     */
    void remove(int tileX, int tileY) {
        final int tX = tileX - minTileX;
        final int tY = tileY - minTileY;
        for (int id = 0, s = list.size(); id < s; id++) {
            final LargeRaster lr = list.get(id);
            if (lr.getGridX() == tX && lr.getGridY() == tY) {
                list.remove(id);
                break;
            }
        }
        //quad tree
        final File removeFile = new File(qTD.getPath(tX, tY));
        //delete on hard disk if exist.
        if (removeFile.exists()) removeFile.delete();
    }

    /**
     * Return {@link Raster} at tileX tileY mosaic coordinates.
     *
     * @param tileX mosaic index in X direction.
     * @param tileY mosaic index in Y direction.
     * @return Raster at tileX tileY mosaic coordinates.
     * @throws IOException if an error occurs during reading..
     */
    Raster getRaster(int tileX, int tileY) throws IOException {
        final int tX = tileX - minTileX;
        final int tY = tileY - minTileY;
        for (int id = 0; id < list.size(); id++) {
            final LargeRaster lr = list.get(id);
            if (lr.getGridX() == tX && lr.getGridY() == tY) return lr.getRaster();
        }
        final File getFile = new File(qTD.getPath(tX, tY));
        if (getFile.exists()) {
            imgReader.setInput(ImageIO.createImageInputStream(getFile));
            final BufferedImage buff = imgReader.read(0);
            imgReader.dispose();
            //add in cache list.
            final Raster wr       = checkRaster(buff.getRaster(), tX, tY);
            final long rastWeight = getRasterWeight(wr);
            list.addLast(new LargeRaster(tX, tY, rastWeight, wr));
            remainingCapacity -= rastWeight;
            checkList();
            return wr;
        }
        return null;
    }

    /**
     * Return all {@link Raster} within this cache system.
     *
     * @return all raster within this cache system.
     * @throws IOException if impossible to read raster from disk.
     */
    Raster[] getTiles() throws IOException {
        int id = 0;
        final Raster[] rasters = new Raster[numXTiles * numYTiles];
        for (int ty = minTileY, tmy = minTileY+numYTiles; ty < tmy; ty++) {
            for (int tx = minTileX, tmx = minTileX+numXTiles; tx < tmx; tx++) {
                rasters[id++] = getRaster(tx, ty);
            }
        }
        return rasters;
    }

    /**
     * Remove all file and directory relevant to this cache system.
     */
    void removeTiles() {
        remainingCapacity = memoryCapacity;
        list.clear();
        final File removeFile = new File(dirPath);
        cleanDirectory(removeFile);
        removeFile.delete();
    }

    /**
     * Affect a new memory capacity and update {@link Raster} list from new memory capacity set.
     *
     * @param memoryCapacity new memory capacity.
     * @throws IllegalImageDimensionException if capacity is too low from raster weight.
     * @throws IOException if impossible to write raster on disk.
     */
    void setCapacity(long memoryCapacity) throws IllegalImageDimensionException, IOException {
        ArgumentChecks.ensurePositive("LargeList : memory capacity", memoryCapacity);
        final long diff    = this.memoryCapacity - memoryCapacity;
        remainingCapacity -= diff;
        checkList();
        this.memoryCapacity = memoryCapacity;
    }

    /**
     * Define the weight of a {@link Raster}.
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
     * Write {@link Raster} on hard disk.
     *
     * @param path emplacement to write.
     * @param raster raster which will be writing.
     * @throws IOException if impossible to write raster on disk.
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
     * Write {@link Raster} within {@link LargeRaster} object on hard disk at appropriate quad tree emplacement.
     *
     * @param lRaster object which contain raster.
     * @throws IOException if impossible to write raster on disk.
     */
    private void writeRaster(LargeRaster lRaster) throws IOException {
        final File file = new File(qTD.getPath(lRaster.getGridX(), lRaster.getGridY()));
        if (!file.exists() || isWritableRenderedImage) {
            writeRaster(file, lRaster.getRaster());
        }
    }

    /**
     * Clean all subDirectory of {@link parentDirectory}.
     *
     * @param parentDirectory directory which will be cleaned.
     */
    private void cleanDirectory(File parentDirectory) {
        for (File file : parentDirectory.listFiles()) {
            if (file.isDirectory()) cleanDirectory(file);
            file.delete();
        }
    }

    /**
     * <p>Verify that {@link Raster} coordinate is agree from {@link RenderedImage} location.<br/>
     * If location is correct return {@link Raster} else return new {@link Raster} with correct<br/>
     * location but with same internal value from {@link Raster}.</p>
     *
     * @param raster raster will be checked.
     * @param tx tile location within renderedImage owner in X direction.
     * @param ty tile location within renderedImage owner in Y direction.
     * @return raster with correct coordinate from its image owner.
     */
    private Raster checkRaster(WritableRaster raster, int tx, int ty) {
        final int mx = riTileWidth  * tx + riMinX;
        final int my = riTileHeight * ty + riMinY;
        if (raster.getMinX() != mx || raster.getMinY() != my) {
            RPOINT.setLocation(mx, my);
            return Raster.createWritableRaster(raster.getSampleModel(), raster.getDataBuffer(), RPOINT);
        }
        return raster;
    }

    /**
     * <p>Verify that list weight do not exceed memory capacity.<br/>
     * If memory capacity is exceed write {@link Raster} on hard disk up to don't exceed memory capacity.</p>
     *
     * @throws IllegalImageDimensionException if raster too large for this Tilecache.
     * @throws IOException if impossible to write raster.
     */
    private void checkList() throws IOException {
        while (remainingCapacity < 0) {
            if (list.isEmpty())
                throw new IllegalImageDimensionException("raster too large");
            final LargeRaster lr = list.pollFirst();
            remainingCapacity   += lr.getWeight();
            //quad tree
            writeRaster(lr);
        }
    }
}
/**
 * Contain {@link Raster} and different raster properties.
 *
 * @author Remi Marechal (Geomatys).
 */
class LargeRaster {
    private final int gridX;
    private final int gridY;
    private final long weight;
    private final Raster raster;

    /**
     * Object to wrap {@link Raster} and different raster properties.
     *
     * @param gridX raster position in X direction.
     * @param gridY raster position in Y direction.
     * @param weight raster weight.
     * @param raster
     */
    LargeRaster(int gridX, int gridY, long weight, Raster raster) {
        this.gridX  = gridX;
        this.gridY  = gridY;
        this.weight = weight;
        this.raster = raster;
    }

    /**
     * Return stocked {@link Raster} mosaic coordinate in X direction.
     *
     * @return stocked {@link Raster} mosaic coordinate in X direction.
     */
    int getGridX() {
        return gridX;
    }

    /**
     * Return stocked {@link Raster} mosaic coordinate in Y direction.
     *
     * @return stocked {@link Raster} mosaic coordinate in Y direction.
     */
    int getGridY() {
        return gridY;
    }

    /**
     * Return stocked {@link Raster}.
     *
     * @return stocked {@link Raster}.
     */
    Raster getRaster() {
        return raster;
    }

    /**
     * Return stocked {@link Raster} weight.
     *
     * @return stocked {@link Raster} weight.
     */
    long getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LargeRaster)) return false;
        LargeRaster lr = (LargeRaster) obj;
        return (gridX == lr.getGridX() && gridY == lr.getGridY() && raster == lr.getRaster());
    }
}
