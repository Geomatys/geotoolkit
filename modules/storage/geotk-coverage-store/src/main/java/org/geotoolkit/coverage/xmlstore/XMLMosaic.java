/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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
package org.geotoolkit.coverage.xmlstore;

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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ProgressMonitor;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import net.iharder.Base64;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Classes;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.*;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.util.BufferedImageUtilities;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 * @module pending
 */
@XmlAccessorType(XmlAccessType.NONE)
public class XMLMosaic implements GridMosaic {

    private static final Logger LOGGER = Logging.getLogger(XMLMosaic.class);

    /** Executor used to write images */
    private static final RejectedExecutionHandler LOCAL_REJECT_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static final BlockingQueue IMAGEQUEUE = new ArrayBlockingQueue(Runtime.getRuntime().availableProcessors()*2);

    private static final ThreadPoolExecutor TILEWRITEREXECUTOR = new ThreadPoolExecutor(
            0, Runtime.getRuntime().availableProcessors(), 1, TimeUnit.MINUTES, IMAGEQUEUE, LOCAL_REJECT_EXECUTION_HANDLER);

    /*
     * Used only if we use the tile state cache mecanism, which means we don't use XML document to read / write tile states.
     */
    private Cache<Point, Boolean> tileExistCache = new Cache<>(1000, 1000, true);

    //empty tile informations
    private byte[] emptyTileEncoded = null;

    //written values
    @XmlElement
    double scale;
    @XmlElement
    double[] upperLeft;
    @XmlElement
    int gridWidth;
    @XmlElement
    int gridHeight;
    @XmlElement
    int tileWidth;
    @XmlElement
    int tileHeight;
    // Use getter /setter to bind those two, because we must perform special operation at flush.
    String existMask;
    String emptyMask;

    XMLPyramid pyramid = null;
    BitSet tileExist;
    BitSet tileEmpty;

    @XmlElement
    Boolean cacheTileState;

    File folder;

    void initialize(XMLPyramid pyramid) {
        this.pyramid = pyramid;

        // If we create a new mosaic, behavior for tile state management has not been determined yet, we try to get it from store parameters.
        if (cacheTileState == null) {
            try {
                cacheTileState = ((XMLCoverageStore) pyramid.getPyramidSet().getRef().getStore()).cacheTileState;
            } catch (Exception e) {
                // If we've got a problem retrieving cache state parameter, we use default behavior (flushing tile states).
                cacheTileState = false;
            }
        }

        if (existMask != null && !existMask.isEmpty()) {
            try {
                tileExist = BitSet.valueOf(Base64.decode(existMask));
                /*
                 * Caching tile state can only be determined at pyramid creation, because a switch of behavior after
                 * that seems a little bit tricky.
                 */
                cacheTileState = false;
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                tileExist = new BitSet(gridWidth * gridHeight);
            }
        } else {
            tileExist = new BitSet(gridWidth * gridHeight);
        }

        if (emptyMask != null && !emptyMask.isEmpty()) {
            try {
                tileEmpty = BitSet.valueOf(Base64.decode(emptyMask));
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                tileEmpty = new BitSet(gridWidth * gridHeight);
            }
        } else {
            tileEmpty = new BitSet(gridWidth * gridHeight);
        }

        /* Here is an handy check, mainly for retro-compatibility purpose. We should only get an empty bit set if the
         * mosaic has just been created. So, if the mosaic directory exists and contains at least one file, it means
         * that we've got an old version of pyramid descriptor, or it is corrupted. In such cases, we must cache tile
         * state in order to retrieve existing ones.
         */
        if (tileExist.isEmpty() && getFolder().isDirectory()) {
            try (DirectoryStream dStream = Files.newDirectoryStream(folder.toPath())) {
                if (dStream.iterator().hasNext()) {
                    cacheTileState = true;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Mosaic folder cannot be scanned.", e);
            }
        }
    }

    private synchronized byte[] createEmptyTile() {
        if (emptyTileEncoded==null) {
            //create an empty tile
            final List<XMLSampleDimension> dims = pyramid.getPyramidSet().getRef().getXMLSampleDimensions();
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

    private static String updateCompletionString(BitSet input) {
        return Base64.encodeBytes(input.toByteArray());
    }

    /**
     * Id equals scale string value
     */
    @Override
    public String getId() {
        final StringBuilder sb = new StringBuilder();
        sb.append(scale);
        for(int i=0;i<upperLeft.length;i++){
            sb.append('x');
            sb.append(upperLeft[i]);
        }
        //avoid local system formating
        return sb.toString().replace(DecimalFormatSymbols.getInstance().getDecimalSeparator(), 'd');
    }

    public File getFolder() {
        if (folder == null) {
            folder = new File(getPyramid().getFolder(), getId());
            // For retro-compatibility purpose.
            if (!folder.isDirectory()) {
                final File tmpFolder = new File(getPyramid().getFolder(), String.valueOf(scale));
                if (tmpFolder.isDirectory()) {
                    folder = tmpFolder;
                }
                // Else, it must be a new pyramid, the mosaic directory will be created when the first tile will be written.
            }
        }
        return folder;
    }

    @Override
    public XMLPyramid getPyramid() {
        return pyramid;
    }

    @Override
    public DirectPosition getUpperLeftCorner() {
        final GeneralDirectPosition ul = new GeneralDirectPosition(getPyramid().getCoordinateReferenceSystem());
        for(int i=0;i<upperLeft.length;i++) ul.setOrdinate(i, upperLeft[i]);
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
        final GeneralDirectPosition ul = new GeneralDirectPosition(getUpperLeftCorner());
        final double minX = ul.getOrdinate(0);
        final double maxY = ul.getOrdinate(1);
        final double spanX = getTileSize().width * getGridSize().width * scale;
        final double spanY = getTileSize().height* getGridSize().height* scale;

        final GeneralEnvelope envelope = new GeneralEnvelope(ul,ul);
        envelope.setRange(0, minX, minX + spanX);
        envelope.setRange(1, maxY - spanY, maxY );

        return envelope;
    }

    @Override
    public Envelope getEnvelope(int col, int row) {
        final GeneralDirectPosition ul = new GeneralDirectPosition(getUpperLeftCorner());
        final double minX = ul.getOrdinate(0);
        final double maxY = ul.getOrdinate(1);
        final double spanX = getTileSize().width * scale;
        final double spanY = getTileSize().height * scale;

        final GeneralEnvelope envelope = new GeneralEnvelope(ul,ul);
        envelope.setRange(0, minX + col*spanX, minX + (col+1)*spanX);
        envelope.setRange(1, maxY - (row+1)*spanY, maxY - row*spanY);

        return envelope;
    }

    @Override
    public boolean isMissing(int col, int row) throws PointOutsideCoverageException {
        if (tileExist == null || tileExist.isEmpty()) {
            try {
                final Point key = new Point(col, row);
                return tileExistCache.getOrCreate(key, new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !getTileFile(key.x, key.y).isFile();
                    }
                });
            } catch (PointOutsideCoverageException e) {
                throw e;
            } catch (Exception e) {
                return true;
            }
        } else {
            return !tileExist.get(getTileIndex(col, row));
        }
    }

    private boolean isEmpty(int col, int row){
        if (tileEmpty == null || tileEmpty.isEmpty()) {
            /* For now, if we keep tile state in cache, we consider empty tiles as non-existing. Because without the
             * appropriate bitset, we would need to scan the tile file to know if it's empty.
             */
            return false;
        } else {
            return tileEmpty.get(getTileIndex(col, row));
        }
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

    public File getTileFile(int col, int row) throws DataStoreException {
        checkPosition(col, row);
        final String postfix = getPyramid().getPyramidSet().getReaderSpi().getFileSuffixes()[0];
        return new File(getFolder(),row+"_"+col+"."+postfix);
    }

    ImageWriter acquireImageWriter() throws IOException {
        return XImageIO.getWriterByFormatName(getPyramid().getPyramidSet().getFormatName(), null, null);
    }

    void createTile(int col, int row, RenderedImage image) throws DataStoreException {
        ImageWriter writer = null;
        try {
            writer = acquireImageWriter();
            createTile(col, row, image, writer);
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        } finally {
            if(writer != null){
                writer.dispose();
            }
        }
    }

    void createTile(final int col, final int row, final RenderedImage image, final ImageWriter writer) throws DataStoreException {
        if (isEmpty(image.getData())) {
            if (tileExist != null) {
                tileExist.set(getTileIndex(col, row), true);
                tileEmpty.set(getTileIndex(col, row), true);
            }
            return;
        }

        checkPosition(col, row);
        final File f = getTileFile(col, row);
        f.getParentFile().mkdirs();

        ImageOutputStream out = null;
        try {
            final Class[] outTypes = writer.getOriginatingProvider().getOutputTypes();
            if(ArraysExt.contains(outTypes, File.class)){
                //writer support files directly, let him handle it
                writer.setOutput(f);
            }else{
                out = ImageIO.createImageOutputStream(f);
                writer.setOutput(out);
            }
            writer.write(image);
            if (tileExist != null) {
                final int ti = getTileIndex(col, row);
                tileExist.set(ti, true);
                tileEmpty.set(ti, false);
            }
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        } finally {
            if (writer != null) {
                writer.setOutput(null);
            }
            if(out!=null){
                try {
                    out.close();
                } catch (IOException ex) {
                    throw new DataStoreException(ex);
                }
            }
        }
    }

    void writeTiles(final RenderedImage image, final boolean onlyMissing, final ProgressMonitor monitor) throws DataStoreException{
        final List<Future> futurs = new ArrayList<>();
        for(int y=0,ny=image.getNumYTiles(); y<ny; y++){
            for(int x=0,nx=image.getNumXTiles(); x<nx; x++){
                if (monitor != null && monitor.isCanceled()) {
                    // Stops submitting new thread
                    return;
                }

                if(onlyMissing && !isMissing(x, y)){
                    continue;
                }

                final int tileIndex = getTileIndex(x, y);
                checkPosition(x, y);

                final File f = getTileFile(x, y);
                f.getParentFile().mkdirs();
                Future fut = TILEWRITEREXECUTOR.submit(new TileWriter(f, image, x, y, tileIndex, image.getColorModel(), getPyramid().getPyramidSet().getFormatName(), monitor));
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

    }

    private void checkPosition(int col, int row) throws PointOutsideCoverageException {
        // TODO : Negative indices are allowed ?
        if(col < 0 || row < 0 || col >= getGridSize().width || row >=getGridSize().height){
            throw new PointOutsideCoverageException("Tile position is outside the grid : " + col + " " + row, new GeneralDirectPosition(col, row));
        }
    }

    private int getTileIndex(int col, int row){
        final int index = row*getGridSize().width + col;
        return index;
    }

    @XmlElement
    protected String getExistMask() {
        // Flush only if user did not specify to cache tile states.
        if (tileExist == null || cacheTileState) return null;
        return existMask = updateCompletionString(tileExist);
    }

    protected void setExistMask(String newValue) {
        existMask = newValue;
    }
    
    @XmlElement
    protected String getEmptyMask() {
        // Flush only if user did not specify to cache tile states.
        if (tileEmpty == null || cacheTileState) return null;
        return emptyMask = updateCompletionString(tileEmpty);
    }

    protected void setEmptyMask(String newValue) {
        emptyMask = newValue;
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
        private final ProgressMonitor monitor;

        public TileWriter(File f,RenderedImage image, int idx, int idy, int tileIndex, ColorModel cm, String formatName, ProgressMonitor monitor) {
            ArgumentChecks.ensureNonNull("file", f);
            ArgumentChecks.ensureNonNull("image", image);
            this.f = f;
            this.image = image;
            this.idx = idx;
            this.idy = idy;
            this.tileIndex = tileIndex;
            this.cm = cm;
            this.formatName = formatName;
            this.monitor = monitor;
        }

        @Override
        public void run() {
            // Stops writing tile if process cancelled
            if (monitor != null && monitor.isCanceled()) {
                return;
            }

            ImageWriter writer = null;
            ImageOutputStream out = null;
            try{
                Raster raster = image.getTile(idx, idy);

                //check if image is empty
                if(raster == null || isEmpty(raster)){
                    synchronized(tileExist){
                        tileExist.set(tileIndex, true);
                    }
                    synchronized(tileEmpty){
                        tileEmpty.set(tileIndex, true);
                    }
                    return;
                }
                
                writer = ImageIO.getImageWritersByFormatName(formatName).next();
                
                final Class[] outTypes = writer.getOriginatingProvider().getOutputTypes();
                if(ArraysExt.contains(outTypes, File.class)){
                    //writer support files directly, let him handle it
                    writer.setOutput(f);
                }else{
                    out = ImageIO.createImageOutputStream(f);
                    writer.setOutput(out);
                }                

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
                if(writer != null){
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

    /**
     * For retro-compatibility purpose with the 2D-limited pyramids. X coordinate of the upper-left point of the mosaic.
     * DO NOT put a getter, as we don't want it to be written, only read from description file.
     * @param x The X coordinate of the upper-left point of the mosaic.
     */
    @XmlElement
    private void setupperleftX(double x) {
        if (upperLeft == null) {
            upperLeft = new double[2];
        }
        upperLeft[0] = x;
    }

    /**
     * For retro-compatibility purpose. Return null, because we don't want to write it, just need it at reading.
     * @return null
     */
    private Double getupperleftX() {
        return null;
    }

    /**
     * For retro-compatibility purpose with the 2D-limited pyramids. Y coordinate of the upper-left point of the mosaic.
     * DO NOT put a getter, as we don't want it to be written, only read from description file.
     * @param y The Y coordinate of the upper-left point of the mosaic.
     */
    @XmlElement
    private void setupperleftY(double y) {
        if (upperLeft == null) {
            upperLeft = new double[2];
        }
        upperLeft[1] = y;
    }

    /**
     * For retro-compatibility purpose. Return null, because we don't want to write it, just need it at reading.
     * @return null
     */
    private Double getupperleftY() {
        return null;
    }
}
