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
import org.geotoolkit.util.ArgumentChecks;

/**
 *
 * @author Rémi Maréchal (Geomatys).
 */
class LargeList {

    private static String TEMPORARY_PATH = System.getProperty("java.io.tmpdir");
    private static String FORMAT = "tiff";
    private long memoryCapacity;
    private long remainingCapacity;
    private final LinkedList<LargeRaster> list;
    private final int numXTiles;
    private final int numYTiles;
    private final QuadTreeDirectory qTD;
    private final String dirPath;
    private final ColorModel cm;
    private final int riMinX;
    private final int riMinY;
    private final int riTileWidth;
    private final int riTileHeight;

    private final ImageReader imgReader;
    private final ImageWriter imgWriter;


    LargeList(RenderedImage ri, long memoryCapacity) throws IOException {
        //cache properties.
        this.list           = new LinkedList<LargeRaster>();
        this.memoryCapacity = memoryCapacity;

        //image owner properties.
        this.cm                = ri.getColorModel();
        this.remainingCapacity = memoryCapacity;
        this.numXTiles         = ri.getNumXTiles();
        this.numYTiles         = ri.getNumYTiles();
        this.riMinX            = ri.getMinX();
        this.riMinY            = ri.getMinY();
        this.riTileWidth       = ri.getTileWidth();
        this.riTileHeight      = ri.getTileHeight();

        //quad tree directory architecture.
        this.dirPath = TEMPORARY_PATH + "/img_"+ri.hashCode();
        this.qTD     = new QuadTreeDirectory(dirPath, numXTiles, numYTiles, FORMAT, true);
        qTD.create4rchitecture();

        //reader writer
        this.imgReader = XImageIO.getReaderByFormatName(FORMAT, null, Boolean.FALSE, Boolean.TRUE);
        this.imgWriter = XImageIO.getWriterByFormatName(FORMAT, null, null);
    }

    void add(int x, int y, WritableRaster raster) throws IOException {
        final long rastWeight = getRasterWeight(raster);
        if (rastWeight > memoryCapacity) throw new IllegalImageDimensionException("raster too large");
        list.addLast(new LargeRaster(x, y, rastWeight, checkRaster(raster, x, y)));
        remainingCapacity -= rastWeight;
        checkList();
    }

    void remove(int x, int y) {
        for (int id = 0, s = list.size(); id < s; id++) {
            final LargeRaster lr = list.get(id);
            if (lr.getGridX() == x && lr.getGridY() == y) {
                list.remove(id);
                break;
            }
        }
        //delete on hard disk if exist.
        //quad tree
        final File removeFile = new File(qTD.getPath(x, y));
        if (removeFile.exists()) removeFile.delete();
    }

    Raster getRaster(int x, int y) throws IOException {
        for (int id = 0; id < list.size(); id++) {
            final LargeRaster lr = list.get(id);
            if (lr.getGridX() == x && lr.getGridY() == y) return lr.getRaster();
        }
        final File getFile = new File(qTD.getPath(x, y));
        if (getFile.exists()) {
            imgReader.setInput(ImageIO.createImageInputStream(getFile));
            final BufferedImage buff = imgReader.read(0);
            imgReader.dispose();
            //add in cache list.
            final WritableRaster wr = checkRaster(buff.getRaster(), x, y);
            add(x, y, wr);
            return wr;
        }
        return null;
    }

    /**
     * Return all raster within this cache system.
     *
     * @return all raster within this cache system.
     * @throws IOException if impossible to read raster from disk.
     */
    Raster[] getTiles() throws IOException {
        int id = 0;
        final Raster[] rasters = new Raster[numXTiles * numYTiles];
        for (int y = 0; y < numYTiles; y++)
            for (int x = 0; x < numXTiles; x++) rasters[id++] = getRaster(x, y);
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
     * Affect a new memory capacity and update raster list from new memory capacity set.
     *
     * @param memoryCapacity new memory capacity.
     * @throws IllegalImageDimensionException if capacity is too low from raster weight.
     * @throws IOException if impossible to write raster on disk.
     */
    void setCapacity(long memoryCapacity) throws IllegalImageDimensionException, IOException {
        ArgumentChecks.ensurePositive("LargeList : memory capacity", memoryCapacity);
        final long diff = this.memoryCapacity - memoryCapacity;
        remainingCapacity -= diff;
        checkList();
        this.memoryCapacity = memoryCapacity;
    }

    /**
     * Define the weight of a raster.
     *
     * @param raster raster which will be weigh.
     * @return raster weight.
     */
    private long getRasterWeight(Raster raster) {
        long dataWeight;
        int type = raster.getDataBuffer().getDataType();
        switch (type) {
            case DataBuffer.TYPE_BYTE      : dataWeight = 1; break;
            case DataBuffer.TYPE_DOUBLE    : dataWeight = 8; break;
            case DataBuffer.TYPE_FLOAT     : dataWeight = 4; break;
            case DataBuffer.TYPE_INT       : dataWeight = 4; break;
            case DataBuffer.TYPE_SHORT     : dataWeight = 2; break;
            case DataBuffer.TYPE_UNDEFINED : dataWeight = 8; break;
            case DataBuffer.TYPE_USHORT    : dataWeight = 2; break;
            default : throw new IllegalStateException("unknow raster data type");
        }
        final SampleModel rsm = raster.getSampleModel();
        final int width = (rsm instanceof ComponentSampleModel) ? ((ComponentSampleModel) rsm).getScanlineStride() : raster.getWidth()*rsm.getNumDataElements();
        return width * raster.getHeight() * dataWeight ;
    }

    /**
     * Write raster on hard disk.
     *
     * @param path emplacement to write.
     * @param raster raster which will be writing.
     * @throws IOException if impossible to write raster on disk.
     */
    private void writeRaster(File path, WritableRaster raster) throws IOException {
        final WritableRaster wr  = RasterFactory.createWritableRaster(raster.getSampleModel(), raster.getDataBuffer(), new Point(0, 0));
        final RenderedImage rast = new BufferedImage(cm, wr, true, null);
        imgWriter.setOutput(ImageIO.createImageOutputStream(path));
        imgWriter.write(rast);
        imgWriter.dispose();
        path.deleteOnExit();
    }

    /**
     * Write raster within {@link LargeRaster} object on hard disk at appropriate quad tree emplacement.
     *
     * @param lRaster object which contain raster.
     * @throws IOException if impossible to write raster on disk.
     */
    private void writeRaster(LargeRaster lRaster) throws IOException {
        writeRaster(new File(qTD.getPath(lRaster.getGridX(), lRaster.getGridY())), lRaster.getRaster());
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
     * <p>Verify that raster coordinate is agree from renderedImage location.<br/>
     * If location is correct return raster else return new raster with correct<br/>
     * location but with same internal value from raster.</p>
     *
     * @param raster raster will be checked.
     * @param tx tile location within renderedImage owner in X direction.
     * @param ty tile location within renderedImage owner in Y direction.
     * @return raster with correct coordinate from its image owner.
     */
    private WritableRaster checkRaster(WritableRaster raster, int tx, int ty) {
        final int mx = riTileWidth * tx  + riMinX;
        final int my = riTileHeight * ty + riMinY;
        if (raster.getMinX() != mx || raster.getMinY() != my)
            return Raster.createWritableRaster(raster.getSampleModel(), raster.getDataBuffer(), new Point(mx, my));
        return raster;
    }

    /**
     * <p>Verify that list weight do not exceed memory capacity.<br/>
     * If memory capacity is exceed write raster on hard disk up to don't exceed memory capacity.</p>
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

class LargeRaster {
    private final int gridX;
    private final int gridY;
    private final long weight;
    private final WritableRaster raster;

    /**
     * Object to wrap raster and different raster properties.
     *
     * @param gridX raster position in X direction.
     * @param gridY raster position in Y direction.
     * @param weight raster weight.
     * @param raster
     */
    public LargeRaster(int gridX, int gridY, long weight, WritableRaster raster) {
        this.gridX  = gridX;
        this.gridY  = gridY;
        this.weight = weight;
        this.raster = raster;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public WritableRaster getRaster() {
        return raster;
    }

    public long getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LargeRaster)) return false;
        LargeRaster lr = (LargeRaster) obj;
        return (gridX == lr.getGridX() && gridY == lr.getGridY() && raster == lr.getRaster());
    }
}
