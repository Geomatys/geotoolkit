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
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import net.iharder.Base64;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Classes;
import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Tile;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.process.Monitor;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.ImageTile;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 * @module
 */
@XmlAccessorType(XmlAccessType.NONE)
public class XMLMosaic implements Mosaic {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.coverage.xmlstore");
    private static final NumberFormat DECIMAL_FORMAT = NumberFormat.getInstance(Locale.ENGLISH);

    /** Executor used to write images */
    private static final RejectedExecutionHandler LOCAL_REJECT_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static final BlockingQueue IMAGEQUEUE = new ArrayBlockingQueue(getMaxExecutors()*2);

    private static final ThreadPoolExecutor TILEWRITEREXECUTOR = new ThreadPoolExecutor(
            0, getMaxExecutors(), 1, TimeUnit.MINUTES, IMAGEQUEUE, LOCAL_REJECT_EXECUTION_HANDLER);

    /*
     * Used only if we use the tile state cache mechanism, which means we don't use XML document to read / write tile states.
     */
    private volatile Cache<Point, Boolean> isMissingCache = null;

    //empty tile information
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
    @XmlElement
    long dataPixelWidth;
    @XmlElement
    long dataPixelHeight;

    // Use getter /setter to bind those two, because we must perform special operation at flush.
    String existMask;
    String emptyMask;

    XMLPyramid pyramid = null;
    BitSet tileExist;
    BitSet tileEmpty;

    @XmlElement
    Boolean cacheTileState;

    Path folder;

    final ReentrantReadWriteLock bitsetLock = new ReentrantReadWriteLock();

    /**
     * Read the maximum number of threads in {@link #TILEWRITEREXECUTOR} from
     * {@code geotk.pyramid.xml.max.painters} system property.
     *
     * @return value of {@code geotk.pyramid.xml.max.painters} system property} or
     * number of available processors - 1.
     */
    private static int getMaxExecutors() {
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        final String property = System.getProperty("geotk.pyramid.xml.max.painters");
        final int nbPainters;
        if (property != null) {
            nbPainters = Integer.valueOf(property);
        } else {
            nbPainters = availableProcessors > 1 ? availableProcessors - 1 : 1;
        }
        LOGGER.log(Level.FINE, "Initialize XML tile writer executor pool with a size of "+nbPainters);
        return nbPainters;
    }

    /**
     * Mosaic initialization. Should ALWAYS be called at mosaic instantiation, before doing anything else.
     * @param pyramid The owner pyramid of this mosaic. Cannot be null.
     */
    void initialize(XMLPyramid pyramid) {
        ArgumentChecks.ensureNonNull("Owner pyramid", pyramid);
        this.pyramid = pyramid;

        // If we create a new mosaic, behavior for tile state management has not been determined yet, we try to get it from store parameters.
        if (cacheTileState == null) {
            try {
                cacheTileState = ((XMLCoverageStore) pyramid.getPyramidSet().getRef().getOriginator()).cacheTileState;
            } catch (Exception e) {
                // If we've got a problem retrieving cache state parameter, we use default behavior (flushing tile states).
                cacheTileState = false;
            }
        }

        bitsetLock.writeLock().lock();
        try {
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
                tileExist = cacheTileState ? null : new BitSet(gridWidth * gridHeight);
            }

            if (emptyMask != null && !emptyMask.isEmpty()) {
                try {
                    tileEmpty = BitSet.valueOf(Base64.decode(emptyMask));
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    tileEmpty = new BitSet(gridWidth * gridHeight);
                }
            } else {
                tileEmpty = cacheTileState ? null : new BitSet(gridWidth * gridHeight);
            }
        } finally {
            bitsetLock.writeLock().unlock();
        }

        /* Here is an handy check, mainly for retro-compatibility purpose. We should only get an empty bit set if the
         * mosaic has just been created. So, if the mosaic directory exists and contains at least one file, it means
         * that we've got an old version of pyramid descriptor, or it is corrupted. In such cases, we must cache tile
         * state in order to retrieve existing ones.
         */
        if (tileExist != null && tileExist.isEmpty() && Files.isDirectory(getFolder())) {
            try (DirectoryStream dStream = Files.newDirectoryStream(folder)) {
                if (dStream.iterator().hasNext()) {
                    cacheTileState = true;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Mosaic folder cannot be scanned.", e);
            }
        }
    }

    private Cache<Point, Boolean> getIsMissingCache() {
        if (isMissingCache == null) {
            synchronized (this) {
                //double check
                if (isMissingCache == null) {
                    long maxTile = (long)gridWidth * (long)gridHeight;
                    int cacheSize = maxTile > 1000 ? 1000 : (int) maxTile;
                    isMissingCache = new Cache<>(cacheSize, cacheSize, false);
                }
            }
        }
        return isMissingCache;
    }

    private synchronized byte[] createEmptyTile() throws DataStoreException {
        if (emptyTileEncoded == null) {
            XMLCoverageResource ref = pyramid.getPyramidSet().getRef();
            //create an empty tile
            final List<XMLSampleDimension> dims = ref.getXMLSampleDimensions();
            final BufferedImage emptyTile;
            if (dims != null && !dims.isEmpty()) {
                final int dimsSize = dims.size();
                emptyTile = BufferedImages.createImage(tileWidth, tileHeight, dimsSize, dims.get(0).getDataType());
                //-- fill image by noData if it is possible
                if (dims.get(0).buildSampleDimension().getNoDataValues() != null) {
                    final boolean[] nodataExists = new boolean[dimsSize];
                    Arrays.fill(nodataExists, false);
                    final double[] nodatas = new double[dimsSize];
                    for (int i = 0; i < dimsSize; i++) {
                        final double[] nodat = SampleDimensionUtils.getNoDataValues(dims.get(i).buildSampleDimension());
                        if (nodat != null) {
                            nodataExists[i] = true;
                            nodatas[i] = nodat[0];//-- only one value by band is supported
                        }
                    }

                    final WritablePixelIterator pix = new PixelIterator.Builder().createWritable(emptyTile);
                    while (pix.next()) {
                        for (int d=0; d < dimsSize; d++) {
                            if (nodataExists[d]) pix.setSample(d, nodatas[d]);
                        }
                    }
                }
            } else {
                ColorModel colorModel = ref.getColorModel();
                SampleModel sampleModel = ref.getSampleModel();

                if (colorModel != null && sampleModel != null) {
                    int[] java2DColorMap = null;
                    if (colorModel instanceof IndexColorModel) {
                        final IndexColorModel indexColorMod = (IndexColorModel) colorModel;
                        final int mapSize = indexColorMod.getMapSize();
                        java2DColorMap  = new int[mapSize];
                        indexColorMod.getRGBs(java2DColorMap);
//                        colorMap = new long[mapSize];
//                        for (int p = 0; p < mapSize; p++) colorMap[p] = rgbs[p];
                    }
                    emptyTile = ImageUtils.createImage(tileWidth, tileHeight, SampleType.valueOf(sampleModel.getDataType()),
                            sampleModel.getNumBands(), ImageUtils.getEnumPhotometricInterpretation(colorModel),
                            ImageUtils.getEnumPlanarConfiguration(sampleModel),
                            colorModel.hasAlpha(), colorModel.isAlphaPremultiplied(),
                            java2DColorMap);

                } else {
                    emptyTile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
                }
            }

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                ImageIO.write(emptyTile, pyramid.getPyramidSet().getFormatName(), out);
                out.flush();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            emptyTileEncoded = out.toByteArray();
        }

        return emptyTileEncoded;
    }

    private static String updateCompletionString(BitSet input) throws IOException {
        return Base64.encodeBytes(input.toByteArray(), Base64.GZIP);
    }

    /**
     * Id equals scale string value
     */
    @Override
    public String getIdentifier() {
        final StringBuilder sb = new StringBuilder();
        sb.append(scale);

        final XMLCoverageResource ref = pyramid.getPyramidSet().getRef();
        final String version = ref.getVersion();

        if("1.0".equals(version)){
            //backward compatibility for older pyramid files
            for(int i=0;i<upperLeft.length;i++){
                sb.append('x');
                sb.append(upperLeft[i]);
            }
            return sb.toString().replace(DecimalFormatSymbols.getInstance().getDecimalSeparator(), 'd');
        }else{
            for(int i=0;i<upperLeft.length;i++){
                sb.append('x');
                synchronized(DECIMAL_FORMAT){
                    sb.append(DECIMAL_FORMAT.format(upperLeft[i]));
                }
            }
            return sb.toString().replace('.', 'd');
        }
    }

    public Path getFolder() {
        if (folder == null) {
            final Path pyramidDirectory = pyramid.getFolder();
            folder = pyramidDirectory.resolve(getIdentifier());
            // For retro-compatibility purpose.
            if (!Files.isDirectory(folder)) {
                final Path tmpFolder = pyramidDirectory.resolve(String.valueOf(scale));
                if (Files.isDirectory(tmpFolder)) {
                    this.folder = tmpFolder;
                }
                // Else, it must be a new pyramid, the mosaic directory will be created when the first tile will be written.
            }
        }
        return folder;
    }

    public XMLPyramid getPyramid() {
        return pyramid;
    }

    @Override
    public DirectPosition getUpperLeftCorner() {
        final GeneralDirectPosition ul = new GeneralDirectPosition(pyramid.getCoordinateReferenceSystem());
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
    public Envelope getEnvelope() {
        final GeneralDirectPosition ul = new GeneralDirectPosition(getUpperLeftCorner());
        final int xAxis = CRSUtilities.firstHorizontalAxis(ul.getCoordinateReferenceSystem());
        assert xAxis >= 0;
        final int yAxis = xAxis + 1;
        final double minX = ul.getOrdinate(xAxis);
        final double maxY = ul.getOrdinate(yAxis);
        final double spanX = tileWidth * gridWidth * getScale();
        final double spanY = tileHeight * gridHeight * getScale();

        final GeneralEnvelope envelope = new GeneralEnvelope(ul,ul);
        envelope.setRange(xAxis, minX, minX + spanX);
        envelope.setRange(yAxis, maxY - spanY, maxY);

        return envelope;
    }

    @Override
    public boolean isMissing(long col, long row) throws PointOutsideCoverageException {
        bitsetLock.readLock().lock();
        try {
            if (tileExist == null || tileExist.isEmpty()) {
                try {
                    final Point key = new Point(Math.toIntExact(col), Math.toIntExact(row));
                    return getIsMissingCache().getOrCreate(key, new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return getTileFile(key.x, key.y) == null;
                        }
                    });
                } catch (PointOutsideCoverageException e) {
                    throw e;
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, e.getLocalizedMessage(), e);
                    return true;
                }
            } else {
                final long index = getTileIndex(col, row);
                if (index < 0) {
                    LOGGER.log(Level.FINE, "You try to request a tile out of mosaic tile boundary at coordinates : X = "+col+", Y = "+row
                    +"Expected grid boundary : [(0, 0) ; ("+getGridSize().width+","+getGridSize().height+")]");
                    return true;
                }
                return !tileExist.get(Math.toIntExact(index));
            }
        } finally {
            bitsetLock.readLock().unlock();
        }
    }

    private boolean isEmpty(long col, long row){
        bitsetLock.readLock().lock();
        try {
            if (tileEmpty == null || tileEmpty.isEmpty()) {
                /* For now, if we keep tile state in cache, we consider empty tiles as non-existing. Because without the
                 * appropriate bitset, we would need to scan the tile file to know if it's empty.
                 */
                return false;
            } else {
                return tileEmpty.get(Math.toIntExact(getTileIndex(col, row)));
            }
        } finally {
            bitsetLock.readLock().unlock();
        }
    }

    @Override
    public ImageTile getTile(long col, long row, Map hints) throws DataStoreException {

        final ImageTile tile;
        if (isEmpty(col, row)) {
            try {
                tile = new DefaultImageTile(pyramid.getPyramidSet().getReaderSpi(),
                        ImageIO.createImageInputStream(new ByteArrayInputStream(createEmptyTile())), 0, new Point(Math.toIntExact(col), Math.toIntExact(row)));
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            }
        } else {
            tile = new DefaultImageTile(pyramid.getPyramidSet().getReaderSpi(),
                    getTileFile(col, row), 0, new Point(Math.toIntExact(col), Math.toIntExact(row)));
        }

        return tile;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GridExtent getDataExtent() {

        if (dataPixelWidth != 0 && dataPixelHeight != 0) {
            return new GridExtent(dataPixelWidth, dataPixelHeight);
        }
        return new GridExtent(
                ((long) gridWidth) * tileWidth,
                ((long) gridHeight) * tileHeight);

        /**
        final Path folder = getFolder();

        try (DirectoryStream<Path> tileStream = Files.newDirectoryStream(folder)) {

            Point start = new Point(gridWidth, gridHeight);
            Point end = new Point(0, 0);
            Point currPos = null;
            for (Path tile : tileStream) {
                final String tileFileName = tile.getFileName().toString();
                currPos = parsePosition(tileFileName);
                start.x = Math.min(start.x, currPos.x);
                start.y = Math.min(start.y, currPos.y);
                end.x = Math.max(end.x, currPos.x);
                end.y = Math.max(end.y, currPos.y);
            }

            //no tiles in mosaic directory
            //return null as documentation says
            if (currPos == null) {
                return null;
            }

            assert end.x >= start.x;
            assert end.y >= start.y;

            return new Rectangle(start.x, start.y, end.x - start.x, end.y - start.y);

        } catch (IOException e) {
           LOGGER.log(Level.FINE, "Data area compute failed "+e.getLocalizedMessage(), e);
            //error with directory stream
            return null;
        }*/
    }

    private Point parsePosition(String tileFile) {
        String posStr = tileFile.substring(0, tileFile.lastIndexOf('.'));
        String[] split = posStr.split("_");
        return new Point(Integer.valueOf(split[1]), Integer.valueOf(split[0]));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append("   scale = ").append(getScale());
        sb.append("   gridSize[").append(getGridSize().width).append(',').append(getGridSize().height).append(']');
        sb.append("   tileSize[").append(getTileSize().width).append(',').append(getTileSize().height).append(']');
        return sb.toString();
    }

    /**
     * Returns the {@linkplain Path file} of tile at col and row index position.<br><br>
     *
     * Moreover, this method check all possible path file suffix from pyramid SPI
     * and return the first that exist else return {@code null} if any exists.
     *
     * @param col mosaic column index.
     * @param row mosaic row index.
     * @return {@linkplain Path file} of tile at col and row index position if exist, else return {@code null}.
     * @throws DataStoreException if tile is not present at path file place.
     */
    private Path getTileFile(long col, long row) throws DataStoreException {
        checkPosition(col, row);
        for (Path fil : getTileFiles(col, row)) {
            if (Files.isRegularFile(fil)) return fil;
        }
        return null;
    }

    /**
     * Return the first available tile path {@link Path} use to write tile.<br>
     *
     * You may choose another suffix {@link Path}, with travel {@link #getTileFiles(int, int) } results.
     *
     * @param col mosaic column index.
     * @param row mosaic row index.
     * @return the first available tile path {@link Path} use to write tile.
     * @throws DataStoreException
     */
    private Path getDefaultTileFile(int col, int row) throws DataStoreException {
        final Path fil = getTileFiles(col, row)[0];
//        assert !fil.exists(): "created file should not exist : path : "+fil.getPath();
        return fil;
    }

    /**
     * Returns all possible {@linkplain Path files} from all suffix from reader spi.
     *
     * @param col mosaic column index.
     * @param row mosaic row index.
     * @return all possible {@linkplain Path files} from all suffix from reader spi.
     * @throws DataStoreException if problem during get suffix.
     */
    private Path[] getTileFiles(long col, long row) throws DataStoreException {
        final String[] suffixx = pyramid.getPyramidSet().getReaderSpi().getFileSuffixes();
        final Path[] fils = new Path[suffixx.length];
        for (int i=0;i<suffixx.length;i++) {
            fils[i] = getFolder().resolve(row+"_"+col+"."+suffixx[i]);
        }
        return fils;
    }

    ImageWriter acquireImageWriter() throws IOException {
        return XImageIO.getWriterByFormatName(pyramid.getPyramidSet().getFormatName(), null, null);
    }

    @Override
    public void writeTiles(Stream<Tile> tiles, Monitor monitor) throws DataStoreException {
        try {
            tiles.parallel().forEach((Tile tile) -> {
                if (tile instanceof ImageTile) {
                    final ImageTile imgTile = (ImageTile) tile;
                    try {
                        writeTile(imgTile.getPosition(), imgTile.getImage());
                    } catch (IOException ex) {
                        throw new BackingStoreException(new DataStoreException(ex.getMessage(), ex));
                    } catch (DataStoreException ex) {
                        throw new BackingStoreException(ex);
                    }
                } else {
                    throw new BackingStoreException(new DataStoreException("Only ImageTile are supported."));
                }
            });
        } catch (BackingStoreException ex) {
            throw (DataStoreException) ex.getCause();
        }
    }

    private void writeTile(Point pt, RenderedImage image) throws DataStoreException {
        int col = pt.x;
        int row = pt.y;
        //-- check conformity between internal data and currentImage.
        //-- if no sm and cm automatical set from image (use for the first insertion)
        final XMLCoverageResource ref = pyramid.getPyramidSet().getRef();
        synchronized (ref) {
            ref.checkOrSetSampleColor(image);
        }

        createTile(col,row,image);
        if (!cacheTileState && tileExist != null) {
            ref.save();
        }
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

        try {
            checkMosaicFolderExist();
        } catch (IOException e) {
            throw new DataStoreException("Unable to create mosaic folder "+e.getLocalizedMessage(), e);
        }

        // No empty tile with cached tile state.
        if (tileExist != null && isEmpty(image.getData())) {
            bitsetLock.writeLock().lock();
            try {
                tileExist.set(Math.toIntExact(getTileIndex(col, row)), true);
                tileEmpty.set(Math.toIntExact(getTileIndex(col, row)), true);
            } finally {
                bitsetLock.writeLock().unlock();
            }
            return;
        }

        checkPosition(col, row);
        Path tilePath = getTileFile(col, row);
        if (tilePath == null) tilePath = getDefaultTileFile(col, row);

        ImageOutputStream out = null;
        try {
            final Class[] outTypes = writer.getOriginatingProvider().getOutputTypes();
            if(ArraysExt.contains(outTypes, Path.class)){
                //writer support files directly, let him handle it
                writer.setOutput(tilePath);
            }else{
                out = ImageIO.createImageOutputStream(tilePath);
                writer.setOutput(out);
            }
            if (out == null) {
                out = ImageIO.createImageOutputStream(tilePath);
            }
            writer.write(image);
            if (tileExist != null) {
                final long ti = getTileIndex(col, row);
                bitsetLock.writeLock().lock();
                try {
                    tileExist.set(Math.toIntExact(ti), true);
                    tileEmpty.set(Math.toIntExact(ti), false);
                } finally {
                    bitsetLock.writeLock().unlock();
                }
            } else {
                getIsMissingCache().put(new Point(col, row), false);
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

    /**
     * {@inheritDoc }.
     */
    @Override
    public void deleteTile(int col, int row) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    private void checkPosition(long col, long row) throws PointOutsideCoverageException {
        // TODO : Negative indices are allowed ?
        if(col < 0 || row < 0 || col >= getGridSize().width || row >=getGridSize().height){
            throw new PointOutsideCoverageException("Tile position is outside the grid : " + col + " " + row, new GeneralDirectPosition(col, row));
        }
    }

    /**
     * Check if a current mosaic folder exist.
     * If not create it.
     *
     * @throws IOException
     */
    private void checkMosaicFolderExist() throws IOException {
        final Path mosaicFolder = getFolder();
        if (!Files.isDirectory(mosaicFolder)) {
            Files.createDirectories(mosaicFolder);
        }
    }

    private long getTileIndex(long col, long row){
        return row*getGridSize().width + col;
    }

    @XmlElement
    protected String getExistMask() {
        // Flush only if user did not specify to cache tile states.
        bitsetLock.readLock().lock();
        try {
            if (tileExist == null || cacheTileState) {
                return null;
            }
            return existMask = updateCompletionString(tileExist);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            return existMask = null;
        } finally {
            bitsetLock.readLock().unlock();
        }
    }

    protected void setExistMask(String newValue) {
        existMask = newValue;
        if (existMask != null && !existMask.isEmpty()) {
            try {
                tileExist = BitSet.valueOf(Base64.decode(existMask));
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    @XmlElement
    protected String getEmptyMask() {
        // Flush only if user did not specify to cache tile states.
        bitsetLock.readLock().lock();
        try {
            if (tileEmpty == null || cacheTileState) {
                return null;
            }
            return emptyMask = updateCompletionString(tileEmpty);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            return existMask = null;
        } finally {
            bitsetLock.readLock().unlock();
        }
    }

    protected void setEmptyMask(String newValue) {
        emptyMask = newValue;
        if (emptyMask != null && !emptyMask.isEmpty()) {
            try {
                tileEmpty = BitSet.valueOf(Base64.decode(emptyMask,Base64.GZIP));
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    /**
     * check if image is empty
     */
    private static boolean isEmpty(Raster raster) { //-- maybe use iterator is more efficiency
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

    private class TileWriter implements Runnable{

        private final Path tilePath;
        private final RenderedImage image;
        private final int idx;
        private final int idy;
        private final int tileIndex;
        private final ColorModel cm;
        private final String formatName;
        private final Monitor monitor;

        public TileWriter(Path tilePath, RenderedImage image, int idx, int idy, int tileIndex, ColorModel cm, String formatName, Monitor monitor) {
            ArgumentChecks.ensureNonNull("file", tilePath);
            ArgumentChecks.ensureNonNull("image", image);
            this.tilePath = tilePath;
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
            try {
                final int offsetX = image.getMinTileX();
                final int offsetY = image.getMinTileY();
                Raster raster = image.getTile(offsetX+idx, offsetY+idy);

                //check if image is empty
                if (tileEmpty != null && (raster == null || isEmpty(raster))) {

                    bitsetLock.writeLock().lock();
                    try {
                        tileExist.set(tileIndex, true);
                        tileEmpty.set(tileIndex, true);
                    } finally {
                        bitsetLock.writeLock().unlock();
                    }
                    return;
                }

                writer = ImageIO.getImageWritersByFormatName(formatName).next();

                final Class[] outTypes = writer.getOriginatingProvider().getOutputTypes();
                if (ArraysExt.contains(outTypes, Path.class)) {
                    //writer support files directly, let him handle it
                    writer.setOutput(tilePath);
                } else {
                    out = ImageIO.createImageOutputStream(tilePath);
                    writer.setOutput(out);
                }

                final boolean canWriteRaster = writer.canWriteRasters();
                //write tile
                if (canWriteRaster) {
                    final IIOImage buffer = new IIOImage(raster, null, null);
                    writer.write(buffer);
                } else {
                    //encapsulate image in a buffered image with parent color model
                    final BufferedImage buffer = new BufferedImage(
                            cm, (WritableRaster) raster, cm.isAlphaPremultiplied(), null);
                    writer.write(buffer);
                }

                if (tileExist != null) {
                    bitsetLock.writeLock().lock();
                    try {
                        tileExist.set(tileIndex, true);
                        tileEmpty.set(tileIndex, false);
                    } finally {
                        bitsetLock.writeLock().unlock();
                    }
                } else {
                    getIsMissingCache().put(new Point(idx, idy), false);
                }

            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                throw new RuntimeException(ex.getMessage(), ex);
            } finally {
                if (writer != null) {
                    writer.dispose();
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
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
