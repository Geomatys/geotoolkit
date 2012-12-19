/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.image.io.large;

import com.sun.media.imageioimpl.plugins.tiff.TIFFAttrInfo;
import java.awt.Point;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
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
public class LargeList {

    private long memoryCapacity;
    private long remainingCapacity;
    LinkedList<LargeRaster> list;
    private static String TEMPORARY_PATH = System.getProperty("java.io.tmpdir");
    private static String FORMAT = "tiff";
//    File temp = new File(System.getProperty("java.io.tmpdir"));
    String dirPath;
    ColorModel cm;

    public LargeList(String directoryName, long memoryCapacity, ColorModel cm) {
        this.list           = new LinkedList<LargeRaster>();
        this.memoryCapacity = memoryCapacity;
        this.cm = cm;
        this.remainingCapacity = memoryCapacity;
        this.dirPath = TEMPORARY_PATH+"/"+directoryName;
    }

    public void add(int x, int y, WritableRaster raster) throws IOException {
        final long rastWeight = getRasterWeight(raster);
        while (remainingCapacity - rastWeight < 0) {
            if (list.isEmpty())
                throw new IllegalImageDimensionException("raster too large");
            //retirer les rasters de la list et les ecrire
            final LargeRaster lr = list.pollFirst();
            final WritableRaster r = lr.getRaster();
            remainingCapacity   += lr.getWeight();

            final File dirFile   = new File(dirPath);
            if (!dirFile.exists()) {
                dirFile.mkdir();
                dirFile.deleteOnExit();
            }

            final StringBuilder sb = new StringBuilder(dirPath);
            sb.append("/");
            sb.append(lr.getGridX());
            sb.append("_");
            sb.append(lr.getGridY());
            sb.append(".");
            sb.append(FORMAT);
//            final String wrPath = dirPath+"/"+lr.getGridX()+"_"+lr.getGridY()+".tiff";
            final String wrPath = sb.toString();
            final File wrFile = new File(wrPath);
            if (!wrFile.exists()) {
                wrFile.deleteOnExit();
                writeRaster(wrFile, r);
            }
            //sur disk
        }
        list.addLast(new LargeRaster(x, y, rastWeight, raster));
        remainingCapacity -= rastWeight;
//        checkList();
    }

    public void remove(int x, int y) {
        for (int id = 0; id < list.size(); id++) {
            final LargeRaster lr = list.get(id);
            if (lr.getGridX() == x && lr.getGridY() == y) {
                list.remove(id);
//                return; // a voir si je delete ou pas le fichier kan je get ou pas
            }
        }
        final StringBuilder sb = new StringBuilder(dirPath);
        sb.append("/");
        sb.append(x);
        sb.append("_");
        sb.append(y);
        sb.append(".");
        sb.append(FORMAT);
//        final File removeFile = new File(dirPath+"/"+x+"_"+y+".tiff");
        final File removeFile = new File(sb.toString());
        if (removeFile.exists()) removeFile.delete();
    }

    public Raster getRaster(int x, int y) throws IOException {
        //sil est dans la liste on le retourn sans l'enlever de la liste
        for (int id = 0; id < list.size(); id++) {
            final LargeRaster lr = list.get(id);
            if (lr.getGridX() == x && lr.getGridY() == y) return lr.getRaster();
        }
        //s'il ny est pas on va le chercher dans le fichier temp et on le reinsere dans la liste
        final StringBuilder sb = new StringBuilder(dirPath);
        sb.append("/");
        sb.append(x);
        sb.append("_");
        sb.append(y);
        sb.append(".");
        sb.append(FORMAT);
//        final File getFile = new File(dirPath+"/"+x+"_"+y+".tiff");
        final File getFile = new File(sb.toString());
        if (getFile.exists()) {
            ImageReader imgRead = XImageIO.getReaderByFormatName(FORMAT, getFile, Boolean.FALSE, Boolean.TRUE);
            BufferedImage buff = imgRead.read(0);
            imgRead.dispose(); // a voir si je delete ou pas le fichier apres ajout dans la liste.
            //on le rajoute en cache car il peut etre tres vite redemandé
            final WritableRaster wr = buff.getRaster();
            add(x, y, wr);
            return wr;
        }
        return null;
    }

    public Raster[] getTiles() throws IOException {
        int tabLength = list.size();
        final File dirdir = new File(dirPath);
        if (dirdir.exists()) tabLength += dirdir.listFiles().length;
        int id = 0;
        final Raster[] rasters = new Raster[tabLength];
        for (LargeRaster lRast : list) {
            rasters[id++] = lRast.getRaster();
        }
        if (dirdir.exists()) {
            for (File f : dirdir.listFiles()) {
                ImageReader imgRead = XImageIO.getReaderByFormatName(FORMAT, f, Boolean.FALSE, Boolean.TRUE);
                BufferedImage buff = imgRead.read(0);
                imgRead.dispose();
                rasters[id++] = buff.getRaster();
            }
        }
        return rasters;
    }

    public void removeTiles() {
        remainingCapacity = memoryCapacity;
        list.clear();
        File removeFile = new File(dirPath);
        cleanDirectory(removeFile);
        removeFile.delete();
    }

    public void setCapacity(long memoryCapacity) throws IllegalImageDimensionException, IOException {
        ArgumentChecks.ensurePositive("LargeList : memory capacity", memoryCapacity);
        final long diff = this.memoryCapacity - memoryCapacity;
        remainingCapacity -= diff;
        checkList();
        this.memoryCapacity = memoryCapacity;
    }

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

    private void writeRaster(File path, WritableRaster raster) throws IOException {
        WritableRaster wr = RasterFactory.createWritableRaster(raster.getSampleModel(), raster.getDataBuffer(), new Point(0, 0));
//        final WritableRaster wr     = raster.createCompatibleWritableRaster(0, 0, raster.getWidth(), raster.getHeight());
        final RenderedImage rast    = new BufferedImage(cm, wr, true, null);
        final ImageWriter imgWriter = XImageIO.getWriterByFormatName(FORMAT, path, null);
        imgWriter.write(rast);
        imgWriter.dispose();
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

    private void checkList() throws IllegalImageDimensionException, IOException {
        while (remainingCapacity < 0) {
            if (list.isEmpty())
                throw new IllegalImageDimensionException("raster too large");
            //retirer les rasters de la list et les ecrire
            final LargeRaster lr = list.pollFirst();
            final WritableRaster r = lr.getRaster();
            remainingCapacity   += lr.getWeight();

            final File dirFile   = new File(dirPath);
            if (!dirFile.exists()) {
                dirFile.mkdir();
                dirFile.deleteOnExit();
            }

            final StringBuilder sb = new StringBuilder(dirPath);
            sb.append("/");
            sb.append(lr.getGridX());
            sb.append("_");
            sb.append(lr.getGridY());
            sb.append(".");
            sb.append(FORMAT);
//            final String wrPath = dirPath+"/"+lr.getGridX()+"_"+lr.getGridY()+".tiff";
            final String wrPath = sb.toString();
            final File wrFile = new File(wrPath);
            if (!wrFile.exists()) {
                wrFile.deleteOnExit();
                writeRaster(wrFile, r);
            }
            //sur disk
        }
    }
}

class LargeRaster {
    private final int gridX;
    private final int gridY;
    private final long weight;
    private final WritableRaster raster;

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
