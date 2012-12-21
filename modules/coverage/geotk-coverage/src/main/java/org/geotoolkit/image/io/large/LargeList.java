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
    private final int numXTiles;
    private final int numYTiles;
//    File temp = new File(System.getProperty("java.io.tmpdir"));
    String dirPath;
    ColorModel cm;

    public LargeList(String directoryName, long memoryCapacity, int numXTiles, int numYTiles, ColorModel cm) {
        this.list           = new LinkedList<LargeRaster>();
        this.memoryCapacity = memoryCapacity;
        this.cm = cm;
        this.remainingCapacity = memoryCapacity;
        this.numXTiles = numXTiles;
        this.numYTiles = numYTiles;
        this.dirPath = TEMPORARY_PATH+"/"+directoryName;
//        //quad tree
//        create4rchitecture(dirPath, numXTiles, numYTiles);
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

//            //quad tree
//            writeRaster(lr);

            final File dirFile   = new File(dirPath);
            if (!dirFile.exists()) {
                dirFile.mkdir();
//                dirFile.deleteOnExit();//le temp de voir 4 tree
            }

            final StringBuilder sb = new StringBuilder(dirPath);
            sb.append("/");
            sb.append(lr.getGridX());
            sb.append("_");
            sb.append(lr.getGridY());
            sb.append(".");
            sb.append(FORMAT);
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
        final File removeFile = new File(sb.toString());
//        //quad tree
//        final File removeFile = new File(getPath(x, y));
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
        final File getFile = new File(sb.toString());
//        //quad tree
//        final File getFile = new File(getPath(x, y));

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

    //a debugger le quad tree


//    private void create4rchitecture(String path, int numXTiles, int numYTiles) {
//        if (numXTiles <= 2 && numYTiles <= 2) {
//            return;
//        }
//        //ensuite plusieur cas
//        //3 cas
//        //width et height sup a 2 faire 4 sous dossiers
//        //width == 1 faire 2 sous dossiers
//        //height ==1 faire 2 sous dossiers
//        if (numXTiles == 1) {
//            //on decoupe dans la hauteur
//            int nyt = (numYTiles+1)/2;
//            //on creer 2 dossiers
//            String path0 = path+"/0";
//            new File(path0).mkdirs();
//            create4rchitecture(path0, numXTiles, nyt);
//            String path1 = path+"/1";
//            new File(path1).mkdirs();
//            create4rchitecture(path1, numXTiles, numYTiles-nyt);
//        } else if (numYTiles == 1) {
//            //on decoupe dans la longueur
//            int nxt = (numXTiles+1)/2;
//            //on creer 2 dossiers
//            String path0 = path+"/0";
//            new File(path0).mkdirs();
//            create4rchitecture(path0, nxt, numYTiles);
//            String path1 = path+"/1";
//            new File(path1).mkdirs();
//            create4rchitecture(path1, numXTiles-nxt, numYTiles);
//        } else {
//            //on decoupe en 4
//            int nxt = (numXTiles+1)/2;
//            int nyt = (numYTiles+1)/2;
//            //on creer 4 dossiers
//            String path00 = path+"/00";
//            new File(path00).mkdirs();
//            create4rchitecture(path00, nxt, nyt);
//
//            String path10 = path+"/10";
//            new File(path10).mkdirs();
//            create4rchitecture(path10, numXTiles-nxt, nyt);
//
//            String path01 = path+"/01";
//            new File(path01).mkdirs();
//            create4rchitecture(path01, nxt, numYTiles-nyt);
//
//            String path11 = path+"/11";
//            new File(path11).mkdirs();
//            create4rchitecture(path11, numXTiles-nxt, numYTiles-nyt);
//
//        }
//    }
//
//    private String getPath(int tileX, int tileY) {
//        return getPath(dirPath, 0, 0, numXTiles, numYTiles, tileX, tileY);
//    }
//
//    private String getPath(String path, int mintx, int minty, int maxtx, int maxty, int tileX, int tileY){
//        if ((maxtx-mintx) <= 2 && (maxty-mintx) <= 2) {
//            return (path+"/"+tileX+"_"+tileY+"."+FORMAT);
//        }
//        int w = maxtx-mintx;
//        int h = maxty-minty;
//        int w2 = (w+1)/2;
//        int h2 = (h+1)/2;
//
//        if (w==1) {
//            //il y a 2 sous dossiers en hauteur
//            String path0 = path+"/0";
//            int demy = minty+h2;
//            if (intersect(mintx, minty, maxtx, demy, tileX, tileY)) return getPath(path0, mintx, minty, maxtx, demy, tileX, tileY);
//
//            String path1 = path+"/1";
//            if (intersect(mintx, demy, maxtx, maxty, tileX, tileY)) return getPath(path1, mintx, demy, maxtx, maxty, tileX, tileY);
//        } else if (h==1) {
//            //il y a 2 sous dossiers en largeur
//            String path0 = path+"/0";
//            int demx = mintx+w2;
//            if (intersect(mintx, minty, demx, maxty, tileX, tileY)) return getPath(path0, mintx, minty, demx, maxty, tileX, tileY);
//
//            String path1 = path+"/1";
//            if (intersect( demx, minty, maxtx, maxty, tileX, tileY)) return getPath(path1, demx, minty, maxtx, maxty, tileX, tileY);
//        } else {
//            int demx = mintx+w2;
//            int demy = minty+h2;
//            //4 cas
//            String path00 = path+"/00";
//            if (intersect(mintx, minty, demx, demy, tileX, tileY)) return getPath(path00, mintx, minty, demx, demy, tileX, tileY);
//            String path10 = path+"/10";
//            if (intersect(demx, minty, maxtx, demy, tileX, tileY)) return getPath(path10, demx, minty, maxtx, demy, tileX, tileY);
//            String path01 = path+"/01";
//            if (intersect(mintx, demy, demx, maxty, tileX, tileY)) return getPath(path01, mintx, demy, demx, maxty, tileX, tileY);
//            String path11 = path+"/11";
//            if (intersect(demx, demy, maxtx, maxty, tileX, tileY)) return getPath(path11, demx, demy, maxtx, maxty, tileX, tileY);
//        }
//        throw new IllegalStateException("undefine path");
//    }
//
//    private boolean intersect(int minx, int miny, int maxx, int maxy, int tx, int ty){
//        final boolean x = ((tx >= minx) && (tx <= maxx));
//        final boolean y = ((ty >= miny) && (ty <= maxy));
//        return x && y;
//    }
//
//    private void writeRaster(LargeRaster lRaster) throws IOException {
//        String rastPath = getPath(dirPath, 0, 0, numXTiles, numYTiles, lRaster.getGridX(), lRaster.getGridY());
//        writeRaster(new File(rastPath), lRaster.getRaster());
//    }

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

            //quad tree
//            writeRaster(lr);

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
