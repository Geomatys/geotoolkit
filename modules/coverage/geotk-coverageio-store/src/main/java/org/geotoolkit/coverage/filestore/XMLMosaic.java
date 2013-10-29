/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.coverage.filestore;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.AbstractGridMosaic;
import org.geotoolkit.coverage.DefaultTileReference;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.TileReference;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.image.io.XImageIO;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.util.BufferedImageUtilities;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLMosaic implements GridMosaic{

    @XmlTransient
    private static final Logger LOGGER = Logging.getLogger(XMLMosaic.class);

    /** Executor used to write images */
    @XmlTransient
    private static final RejectedExecutionHandler LOCAL_REJECT_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();
    @XmlTransient
    private static final BlockingQueue IMAGEQUEUE = new ArrayBlockingQueue(Runtime.getRuntime().availableProcessors()*2);
    @XmlTransient
    private static final ThreadPoolExecutor TILEWRITEREXECUTOR = new ThreadPoolExecutor(
            0, Runtime.getRuntime().availableProcessors(), 1, TimeUnit.MINUTES, IMAGEQUEUE, LOCAL_REJECT_EXECUTION_HANDLER);

    //empty tile informations
    @XmlTransient
    private byte[] emptyTileEncoded = null;

    //written values
    double scale;
    double upperleftX;
    double upperleftY;
    int gridWidth;
    int gridHeight;
    int tileWidth;
    int tileHeight;
    String completion;

    @XmlTransient
    XMLPyramid pyramid = null;
    @XmlTransient
    BitSet tileExist;
    @XmlTransient
    BitSet tileEmpty;


    void initialize(XMLPyramid pyramid){
        this.pyramid = pyramid;
        if(completion == null){
            completion = "";
        }
        tileExist = new BitSet(gridWidth*gridHeight);
        tileEmpty = new BitSet(gridWidth*gridHeight);
        String packed = completion.replaceAll("\n", "");
        packed = packed.replaceAll("\t", "");
        packed = packed.replaceAll(" ", "");
        for(int i=0,n=packed.length();i<n;i++){
            final char c = packed.charAt(i);
            tileExist.set(i, c!='0');
            tileEmpty.set(i, c=='2');
        }

    }

    private synchronized byte[] createEmptyTile(){
        if(emptyTileEncoded==null){
            //create an empty tile
            final List<XMLSampleDimension> dims = pyramid.getPyramidSet().getSampleDimensions();
            final BufferedImage emptyTile;
            if(dims!=null && !dims.isEmpty()){
                emptyTile = BufferedImageUtilities.createImage(tileWidth, tileHeight, dims.size(), dims.get(0).getDataType());
            }else{
                emptyTile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
            }


            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                ImageIO.write(emptyTile, pyramid.getPyramidSet().getFormatName(), out);
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(XMLMosaic.class.getName()).log(Level.SEVERE, null, ex);
            }
            emptyTileEncoded = out.toByteArray();
        }

        return emptyTileEncoded;
    }

    private void updateCompletionString(){
        final StringBuilder sb = new StringBuilder();
        int index = 0;
        for(int y=0,l=getGridSize().height;y<l;y++){
            sb.append('\n');
            for(int x=0,n=getGridSize().width;x<n;x++){
                char tc = !tileExist.get(index) ? '0':
                          !tileEmpty.get(index) ? '1':
                                                  '2';
                sb.append(tc);
                index++;
            }
        }
        sb.append('\n');
        completion = sb.toString();
    }

    /**
     * Id equals scale string value
     */
    @Override
    public String getId() {
        return String.valueOf(scale);
    }

    public File getFolder(){
        return new File(getPyramid().getFolder(),getId());
    }

    @Override
    public XMLPyramid getPyramid() {
        return pyramid;
    }

    @Override
    public DirectPosition getUpperLeftCorner() {
        final GeneralDirectPosition ul = new GeneralDirectPosition(getPyramid().getCoordinateReferenceSystem());
        ul.setOrdinate(0, upperleftX);
        ul.setOrdinate(1, upperleftY);
        return ul;
    }

    @Override
    public Dimension getGridSize() {
        return new Dimension(gridWidth, gridHeight);
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public Dimension getTileSize() {
        return new Dimension(tileWidth, tileHeight);
    }

    @Override
    public Envelope getEnvelope(){
        final DirectPosition ul = getUpperLeftCorner();
        final double minX = ul.getOrdinate(0);
        final double maxY = ul.getOrdinate(1);
        final double spanX = getTileSize().width * getGridSize().width * scale;
        final double spanY = getTileSize().height* getGridSize().height* scale;

        final GeneralEnvelope envelope = new GeneralEnvelope(
                getPyramid().getCoordinateReferenceSystem());
        envelope.setRange(0, minX, minX + spanX);
        envelope.setRange(1, maxY - spanY, maxY );

        return envelope;
    }

    @Override
    public Envelope getEnvelope(int col, int row) {
        final DirectPosition ul = getUpperLeftCorner();
        final double minX = ul.getOrdinate(0);
        final double maxY = ul.getOrdinate(1);
        final double spanX = getTileSize().width * scale;
        final double spanY = getTileSize().height * scale;

        final GeneralEnvelope envelope = new GeneralEnvelope(
                getPyramid().getCoordinateReferenceSystem());
        envelope.setRange(0, minX + col*spanX, minX + (col+1)*spanX);
        envelope.setRange(1, maxY - (row+1)*spanY, maxY - row*spanY);

        return envelope;
    }

    @Override
    public boolean isMissing(int col, int row) {
        return !tileExist.get(getTileIndex(col, row));
    }

    private boolean isEmpty(int col, int row){
        return tileEmpty.get(getTileIndex(col, row));
    }

    @Override
    public TileReference getTile(int col, int row, Map hints) throws DataStoreException {

        final TileReference tile;
        if(isEmpty(col, row)){
            try {
                tile = new DefaultTileReference(getPyramid().getPyramidSet().getReaderSpi(),
                        ImageIO.createImageInputStream(new ByteArrayInputStream(createEmptyTile())), 0, new Point(col, row));
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            }
        }else{
            tile = new DefaultTileReference(getPyramid().getPyramidSet().getReaderSpi(),
                    getTileFile(col, row), 0, new Point(col, row));
        }

        return tile;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append("   scale = ").append(getScale());
        sb.append("   gridSize[").append(getGridSize().width).append(',').append(getGridSize().height).append(']');
        sb.append("   tileSize[").append(getTileSize().width).append(',').append(getTileSize().height).append(']');
        return sb.toString();
    }

    public File getTileFile(int col, int row) throws DataStoreException{
        checkPosition(col, row);
        final String postfix = getPyramid().getPyramidSet().getReaderSpi().getFileSuffixes()[0];
        return new File(getFolder(),row+"_"+col+"."+postfix);
    }

    void createTile(int col, int row, RenderedImage image) throws DataStoreException {
        if(isEmpty(image.getData())){
            tileExist.set(getTileIndex(col, row), true);
            tileEmpty.set(getTileIndex(col, row), true);
            updateCompletionString();
            return;
        }

        checkPosition(col, row);
        final File f = getTileFile(col, row);
        f.getParentFile().mkdirs();

        ImageOutputStream out = null;
        ImageWriter writer = null;
        try {
            out = ImageIO.createImageOutputStream(f);
            writer = XImageIO.getWriterByFormatName(
                    getPyramid().getPyramidSet().getFormatName(), out, image);
            writer.setOutput(out);
            writer.write(image);
            writer.dispose();
            tileExist.set(getTileIndex(col, row), true);
            tileEmpty.set(getTileIndex(col, row), false);
            updateCompletionString();
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(),ex);
        } finally{
            if(writer != null){
                writer.dispose();
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException ex) {
                    throw new DataStoreException(ex.getMessage(),ex);
                }
            }
        }
    }

    void writeTiles(final RenderedImage image, final boolean onlyMissing) throws DataStoreException{

        final List<Future> futurs = new ArrayList<>();
        try {
            for(int y=0,ny=image.getNumYTiles(); y<ny; y++){
                for(int x=0,nx=image.getNumXTiles(); x<nx; x++){
                    if(onlyMissing && !isMissing(x, y)){
                        continue;
                    }

                    final int tileIndex = getTileIndex(x, y);
                    checkPosition(x, y);

                    final File f = getTileFile(x, y);
                    f.getParentFile().mkdirs();
                    Future fut = TILEWRITEREXECUTOR.submit(new TileWriter(f, image, x, y, tileIndex, image.getColorModel(), getPyramid().getPyramidSet().getFormatName()));
                    futurs.add(fut);
                }
            }

            //wait for all writing tobe done
            for(Future f : futurs){
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }

        } finally{
            updateCompletionString();
        }

    }

    private void checkPosition(int col, int row) throws DataStoreException{
        if(col >= getGridSize().width || row >=getGridSize().height){
            throw new DataStoreException("Tile position is outside the grid : " + col +" "+row);
        }
    }

    private int getTileIndex(int col, int row){
        final int index = row*getGridSize().width + col;
        return index;
    }

    /**
     * check if image is empty
     */
    private static boolean isEmpty(Raster raster){
        double[] array = null;
        searchEmpty:
        for(int x=0,width=raster.getWidth(); x<width; x++){
            for(int y=0,height=raster.getHeight(); y<height; y++){
                array = raster.getPixel(x, y, array);
                for(double d : array){
                    if(d != 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public BlockingQueue<Object> getTiles(Collection<? extends Point> positions, Map hints) throws DataStoreException{
        return AbstractGridMosaic.getTiles(this, positions, hints);
    }

    private class TileWriter implements Runnable{

        private final File f;
        private final RenderedImage image;
        private final int idx;
        private final int idy;
        private final int tileIndex;
        private final ColorModel cm;
        private final String formatName;

        public TileWriter(File f,RenderedImage image, int idx, int idy, int tileIndex, ColorModel cm, String formatName) {
            this.f = f;
            this.image = image;
            this.idx = idx;
            this.idy = idy;
            this.tileIndex = tileIndex;
            this.cm = cm;
            this.formatName = formatName;
        }

        @Override
        public void run() {
            ImageWriter writer = null;
            ImageOutputStream out = null;
            try{
                Raster raster = image.getTile(idx, idy);

                //check if image is empty
                if(isEmpty(raster)){
                    synchronized(tileExist){
                        tileExist.set(tileIndex, true);
                    }
                    synchronized(tileEmpty){
                        tileEmpty.set(tileIndex, true);
                    }
                    return;
                }

                out = ImageIO.createImageOutputStream(f);
                writer = ImageIO.getImageWritersByFormatName(formatName).next();
                writer.setOutput(out);

                final boolean canWriteRaster = writer.canWriteRasters();
                //write tile
                if(canWriteRaster){
                    final IIOImage buffer = new IIOImage(raster, null, null);
                    writer.write(buffer);
                }else{
                    //encapsulate image in a buffered image with parent color model
                    final BufferedImage buffer = new BufferedImage(
                            cm, (WritableRaster)raster, true, null);
                    writer.write(buffer);
                }

                synchronized(tileExist){
                    tileExist.set(tileIndex, true);
                }
                synchronized(tileEmpty){
                    tileEmpty.set(tileIndex, false);
                }

            }catch(Exception ex){
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                throw new RuntimeException(ex.getMessage(),ex);
            }finally{
                writer.dispose();
                if(out != null){
                    try {
                        out.close();
                    } catch (IOException ex) {
                        Logger.getLogger(XMLMosaic.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }

    }

}
