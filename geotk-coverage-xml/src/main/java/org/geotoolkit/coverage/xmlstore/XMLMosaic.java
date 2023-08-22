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
import java.util.Base64;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.io.stream.IOUtilities;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.process.Monitor;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.multires.AbstractTileMatrix;
import org.geotoolkit.storage.multires.ImageTileMatrix;
import org.geotoolkit.storage.multires.TileInError;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.WritableTileMatrix;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 * @module
 */
@XmlAccessorType(XmlAccessType.NONE)
public class XMLMosaic implements WritableTileMatrix, ImageTileMatrix {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.coverage.xmlstore");
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
    private Tile anyTile = null;

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
                    tileExist = BitSet.valueOf(Base64.getDecoder().decode(existMask));
                    /*
                     * Caching tile state can only be determined at pyramid creation, because a switch of behavior after
                     * that seems a little bit tricky.
                     */
                    cacheTileState = false;
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    tileExist = new BitSet(gridWidth * gridHeight);
                }
            } else {
                tileExist = cacheTileState ? null : new BitSet(gridWidth * gridHeight);
            }

            if (emptyMask != null && !emptyMask.isEmpty()) {
                try {
                    tileEmpty = BitSet.valueOf(Base64.getDecoder().decode(emptyMask));
                } catch (IllegalArgumentException ex) {
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
                    sampleModel = sampleModel.createCompatibleSampleModel(tileWidth, tileHeight);
                    WritableRaster r = Raster.createWritableRaster(sampleModel, sampleModel.createDataBuffer(), null);
                    emptyTile = new BufferedImage(colorModel, r, colorModel.isAlphaPremultiplied(), null);

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
        return Base64.getEncoder().encodeToString(input.toByteArray());
    }

    /**
     * Id equals scale string value
     */
    @Override
    public GenericName getIdentifier() {
        final StringBuilder sb = new StringBuilder();
        sb.append(scale);

        final XMLCoverageResource ref = pyramid.getPyramidSet().getRef();
        final String version = ref.getVersion();

        String name;
        if("1.0".equals(version)){
            //backward compatibility for older pyramid files
            for(int i=0;i<upperLeft.length;i++){
                sb.append('x');
                sb.append(upperLeft[i]);
            }
            name = sb.toString().replace(DecimalFormatSymbols.getInstance().getDecimalSeparator(), 'd');
        }else{
            for(int i=0;i<upperLeft.length;i++){
                sb.append('x');
                synchronized(DECIMAL_FORMAT){
                    sb.append(DECIMAL_FORMAT.format(upperLeft[i]));
                }
            }
            name = sb.toString().replace('.', 'd');
        }
        return Names.createLocalName(null, null, name);
    }

    public Path getFolder() {
        if (folder == null) {
            final Path pyramidDirectory = pyramid.getFolder();
            folder = pyramidDirectory.resolve(getIdentifier().toString());
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
    public GridGeometry getTilingScheme() {
        final GeneralDirectPosition ul = new GeneralDirectPosition(pyramid.getCoordinateReferenceSystem());
        for (int i=0;i<upperLeft.length;i++) ul.setOrdinate(i, upperLeft[i]);
        final Dimension gridSize = new Dimension(gridWidth, gridHeight);
        return TileMatrices.toTilingScheme(ul, gridSize, scale, new int[]{tileWidth, tileHeight});
    }

    @Override
    public int[] getTileSize() {
        return new int[]{tileWidth, tileHeight};
    }

    @Override
    public TileStatus getTileStatus(long... indices) throws PointOutsideCoverageException {
        bitsetLock.readLock().lock();
        try {
            if (tileExist == null || tileExist.isEmpty()) {
                try {
                    final Point key = new Point(Math.toIntExact(indices[0]), Math.toIntExact(indices[1]));
                    boolean missing = getIsMissingCache().getOrCreate(key, new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return getTileFile(key.x, key.y) == null;
                        }
                    });
                    return missing ? TileStatus.MISSING : TileStatus.EXISTS;
                } catch (PointOutsideCoverageException e) {
                    return TileStatus.OUTSIDE_EXTENT;
                } catch (Exception e) {
                    LOGGER.log(Level.FINE, e.getLocalizedMessage(), e);
                    return TileStatus.IN_ERROR;
                }
            } else {
                final long index = getTileIndex(indices[0], indices[1]);
                if (index < 0) {
                    LOGGER.log(Level.FINE, "You try to request a tile out of mosaic tile boundary at coordinates : X = "+indices[0]+", Y = "+indices[1]
                    +"Expected grid boundary : [(0, 0) ; ("+getTilingScheme().getExtent().getSize(0)+","+getTilingScheme().getExtent().getSize(1)+")]");
                    return TileStatus.OUTSIDE_EXTENT;
                }
                boolean missing = !tileExist.get(Math.toIntExact(index));
                return missing ? TileStatus.MISSING : TileStatus.EXISTS;
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
    public Stream<Tile> getTiles(GridExtent indicesRanges, boolean parallel) throws DataStoreException {
        if (indicesRanges == null || indicesRanges.equals(getTilingScheme().getExtent())) {
            //optimized case, loop on all files
            final String[] suffixx = pyramid.getPyramidSet().getReaderSpi().getFileSuffixes();
             try {
                 return Files.list(getFolder())
                    .filter((Path t) -> ArraysExt.contains(suffixx, IOUtilities.extension(t)))
                    .map(new Function<Path, Tile>() {
                        @Override
                        public Tile apply(Path t) {
                            String name = IOUtilities.filenameWithoutExtension(t.getFileName().toString());
                            String[] parts = name.split("_");
                            final long x = Long.parseLong(parts[0]);
                            final long y = Long.parseLong(parts[1]);
                            try {
                                return getTile(y,x).orElse(null);
                            } catch (DataStoreException ex) {
                                return TileInError.create(new long[]{x,y}, ex);
                            }
                        }
                    })
                    .filter(Objects::nonNull);
             } catch (IOException ex) {
                 throw new DataStoreException(ex.getMessage(), ex);
             }
        }

        return WritableTileMatrix.super.getTiles(indicesRanges, parallel);
    }

    @Override
    public Optional<Tile> getTile(long... indices) throws DataStoreException {

        final Tile tile;
        // Before any heavy validation, just ensure that we can represent a point from given row/column
        if (isEmpty(indices[0], indices[1])) {
            tile = createEmptyTile(indices);
        } else {
            Path tileFile = getTileFile(indices[0], indices[1]);
            if (tileFile == null) {
                // It happens sometimes, but how ? We need to search further, or stop using XML-based pyramids.
                LOGGER.warning(() -> "Tile is not marked empty, but associated file does not exists: " + Arrays.toString(indices));
                tile = null;
            } else {
                tile = new DefaultImageTile(this, pyramid.getPyramidSet().getReaderSpi(), tileFile, 0, indices);
            }
        }

        return Optional.ofNullable(tile);
    }

    private Tile createEmptyTile(long... tilePosition) throws DataStoreException {
        try {
            return new DefaultImageTile(this,
                    pyramid.getPyramidSet().getReaderSpi(),
                    ImageIO.createImageInputStream(new ByteArrayInputStream(createEmptyTile())),
                    0, tilePosition);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public String toString() {
        return AbstractTileMatrix.toString(this);
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
    private Path getDefaultTileFile(long col, long row) throws DataStoreException {
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
    public void writeTiles(Stream<Tile> tiles) throws DataStoreException {
        try {
            tiles.parallel().forEach((Tile tile) -> {
                try {
                    final Resource resource = tile.getResource();
                    if (resource instanceof GridCoverageResource) {
                        final GridCoverageResource gcr = (GridCoverageResource) resource;
                        writeTile(tile.getIndices(), gcr.read(null).render(null));
                    } else {
                        throw new BackingStoreException(new DataStoreException("Only ImageTile are supported."));
                    }
                } catch (DataStoreException ex) {
                    throw new BackingStoreException(ex);
                }
            });
        } catch (BackingStoreException ex) {
            throw ex.unwrapOrRethrow(DataStoreException.class);
        }
    }

    private void writeTile(long[] pt, RenderedImage image) throws DataStoreException {
        long col = pt[0];
        long row = pt[1];
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

    void createTile(long col, long row, RenderedImage image) throws DataStoreException {
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

    void createTile(final long col, final long row, final RenderedImage image, final ImageWriter writer) throws DataStoreException {

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
            if (ArraysExt.contains(outTypes, Path.class)) {
                //writer support files directly, let him handle it
                writer.setOutput(tilePath);
            }else{
                out = ImageIO.createImageOutputStream(tilePath);
                writer.setOutput(out);
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
                getIsMissingCache().put(new Point(Math.toIntExact(col), Math.toIntExact(row)), false);
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
    public long deleteTiles(GridExtent indicesRanges) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    private void checkPosition(long col, long row) throws PointOutsideCoverageException {
        // TODO : Negative indices are allowed ?
        GridExtent extent = getTilingScheme().getExtent();
        if (col < 0 || row < 0 || col >= extent.getSize(0) || row >= extent.getSize(1)) {
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
        final GridExtent extent = getTilingScheme().getExtent();
        return row * extent.getSize(0) + col;
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
                tileExist = BitSet.valueOf(Base64.getDecoder().decode(existMask));
            } catch (IllegalArgumentException ex) {
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
                tileEmpty = BitSet.valueOf(Base64.getDecoder().decode(emptyMask));
            } catch (IllegalArgumentException ex) {
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

    @Override
    public synchronized Tile anyTile() throws DataStoreException {
        if (anyTile == null) {
            anyTile = getTile(0, 0).orElse(null);
        }
        return anyTile != null ? anyTile : createEmptyTile(0,0);
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
