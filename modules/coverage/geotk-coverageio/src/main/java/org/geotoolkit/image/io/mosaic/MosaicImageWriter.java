/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.io.mosaic;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.imageio.*; // Lot of them in this class.
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.util.*; // Lot of them in this class.
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.lang.reflect.UndeclaredThrowableException;

import org.apache.sis.math.MathFunctions;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.Utilities;
import org.apache.sis.util.Disposable;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.util.logging.LogProducer;
import org.apache.sis.util.logging.PerformanceLevel;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.resources.IndexedResourceBundle;
import org.geotoolkit.image.io.InvalidImageStoreException;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.internal.Threads;
import org.geotoolkit.internal.image.ImageUtilities;
import org.geotoolkit.internal.image.io.IIOUtilities;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.internal.image.io.RawFile;
import org.geotoolkit.internal.io.TemporaryFile;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.io.TemporaryFile;

import static org.geotoolkit.image.io.mosaic.Tile.LOGGER;


/**
 * An image writer which takes a large image (potentially tiled) in input and write tiles as
 * output. The mosaic to write is specified as a collection of {@link Tile} objects given to
 * the {@link #setOutput(Object)} method. The pixel values to write can be specified either
 * as a {@link RenderedImage} (this is the {@linkplain #write standard API}), or as a single
 * {@link File} or a collection of source tiles given to the {@link #writeFromInput(Object,
 * ImageWriteParam)} method. The later alternative is non-standard but often required since
 * the image to mosaic is typically bigger than the capacity of a single {@link RenderedImage}.
 *
 * {@section Caching of source tiles}
 * This class may be slow when reading source images encoded in a compressed format like PNG,
 * because multiple passes over the same image may be necessary for writing different tiles
 * and compression makes the seeks harder. This problem can be mitigated by copying the source
 * images to temporary files in an uncompressed RAW format. The inconvenient is that a large
 * amount of disk space will be temporarily required until the write operation is completed.
 * <p>
 * Caching are enabled by default. If the environment is constrained by disk space or if the
 * source tiles are known to be already uncompressed, then caching can be disabled by overriding
 * the {@link #isCachingEnabled(ImageReader,int)} method.
 *
 * {@section Filtering source images}
 * It is possible to apply an operation on source images before to create the target tiles. The
 * operation can be specified by the {@link MosaicImageWriteParam#setSourceTileFilter(BufferedImageOp)}
 * method, for example in order to add transparency to fully opaque images. Note that if an operation
 * is applied, then the source tiles will be cached in temporary RAW files as described in the above
 * section even if {@link #isCachingEnabled(ImageReader,int)} returns {@code false}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.18
 *
 * @since 2.5
 * @module
 */
public class MosaicImageWriter extends ImageWriter implements LogProducer, Disposable {
    /**
     * The value for filling empty images. The value is fixed to 0 in current implementation
     * because this is the value of newly created image, and we do not fill them at this time.
     */
    private static final int FILL_VALUE = 0;

    /**
     * The preferred tile size inside the images. This apply only to format that support tile
     * size, like TIFF. The tile size effectively used may be slightly different.
     *
     * {@note The default value used by GDAL is 64.}
     *
     * @since 3.15
     */
    private static final int IMAGE_TILE_SIZE = 64;

    /**
     * The logging level for tiling information during read and write operations. If {@code null},
     * then the level shall be selected by {@link PerformanceLevel#forDuration(long, TimeUnit)}.
     */
    private Level logLevel;

    /**
     * The temporary files created for each input tile.
     */
    private final Map<Tile,RawFile> temporaryFiles;

    /**
     * Constructs an image writer with the default provider.
     */
    public MosaicImageWriter() {
        this(null);
    }

    /**
     * Constructs an image writer with the specified provider.
     *
     * @param spi The service provider, or {@code null} for the default one.
     */
    public MosaicImageWriter(final ImageWriterSpi spi) {
        super(spi != null ? spi : Spi.DEFAULT);
        temporaryFiles = new HashMap<>();
    }

    /**
     * Returns {@code true} if logging is enabled.
     */
    private boolean isLoggable() {
        Level level = logLevel;
        if (level == null) {
            level = PerformanceLevel.SLOWEST;
        }
        return LOGGER.isLoggable(level);
    }

    /**
     * Returns the logging level is explicitely set, or the {@link Level#FINE} level
     * otherwise. This is used for logging operations that are not measurement of
     * execution time.
     */
    private Level getFineLevel() {
        final Level level = logLevel;
        return (level != null) ? level : PerformanceLevel.FINE;
    }

    /**
     * Returns the logging level for tile information during read and write operations.
     * The default value is one of the {@link PerformanceLevel} constants, determined
     * according the duration of the operation.
     *
     * @return The current logging level.
     */
    @Override
    public Level getLogLevel() {
        final Level level = logLevel;
        return (level != null) ? level : PerformanceLevel.PERFORMANCE;
    }

    /**
     * Sets the logging level for tile information during read and write operations.
     * A {@code null} value restores the default level documented in the {@link #getLogLevel()}
     * method.
     *
     * @param level The new logging level, or {@code null} for the default.
     */
    @Override
    public void setLogLevel(Level level) {
        logLevel = level;
    }

    /**
     * Returns the output, which is a an array of {@linkplain TileManager tile managers}.
     * The array length is the maximum number of images that can be inserted. The element
     * at index <var>i</var> is the tile manager to use when writing at image index <var>i</var>.
     */
    @Override
    public TileManager[] getOutput() {
        final TileManager[] managers = (TileManager[]) super.getOutput();
        return (managers != null) ? managers.clone() : null;
    }

    /**
     * Sets the output, which is expected to be an array of {@linkplain TileManager tile managers}.
     * If the given input is a singleton, an array or a {@linkplain Collection collection} of
     * {@link Tile} objects, then it will be wrapped in an array of {@link TileManager}s.
     *
     * @param  output The output.
     * @throws IllegalArgumentException if {@code output} is not an instance of one of the
     *         expected classes, or if the output can not be used because of an I/O error
     *         (in which case the exception has a {@link IOException} as its
     *         {@linkplain IllegalArgumentException#getCause cause}).
     */
    @Override
    public void setOutput(final Object output) throws IllegalArgumentException {
        final TileManager[] managers;
        try {
            managers = TileManagerFactory.DEFAULT.createFromObject(output);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage(), e);
        }
        super.setOutput(managers);
    }

    /**
     * Returns default parameters appropriate for this format.
     */
    @Override
    public MosaicImageWriteParam getDefaultWriteParam() {
        return new MosaicImageWriteParam();
    }

    /**
     * Writes the specified image as a set of tiles. The default implementation copies the image in
     * a temporary file, then invokes {@link #writeFromInput}. This very inefficient approach
     * may be changed in a future version.
     *
     * @param  metadata The stream metadata.
     * @param  image    The image to write.
     * @param  param    The parameter for the image to write.
     * @throws IOException if an error occurred while writing the image.
     */
    @Override
    public void write(final IIOMetadata metadata, final IIOImage image, ImageWriteParam param)
            throws IOException
    {
        /*
         * We could check for 'output' before to create the temporary file in order to avoid
         * creating the file if we are going to fail anyway,  but we don't because users are
         * allowed to override the 'filter' method and set the output there (undocumented
         * but possible, and TileBuilder do something like that).
         *
         * Uses the PNG format, which is lossless and bundled in standard Java distributions.
         */
        final Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
        while (writers.hasNext()) {
            final ImageWriter writer = writers.next();
            if (!filter(writer)) {
                continue;
            }
            final File file = File.createTempFile("MIW", ".png");
            try {
                try (ImageOutputStream output = ImageIO.createImageOutputStream(file)) {
                    writer.setOutput(output);
                    writer.write(metadata, image, param);
                }
                /*
                 * We don't want to take in account parameters like source region, subsampling, etc.
                 * since they were already handled by the writing process above. But we want to take
                 * in account the parameters specific to MosaicImageWriteParam. So retain only them.
                 */
                if (param instanceof MosaicImageWriteParam) {
                    param = new MosaicImageWriteParam((MosaicImageWriteParam) param);
                } else {
                    param = null;
                }
                writeFromInput(file, 0, param);
            } finally {
                file.delete();
            }
            return;
        }
        throw new UnsupportedImageFormatException(Errors.format(Errors.Keys.NO_IMAGE_WRITER));
    }

    /**
     * Reads the image from the given input and writes it as a set of tiles. This is equivalent
     * to <code>{@linkplain #writeFromInput(Object,int,ImageWriteParam) writeFromInput}(input,
     * <b>0</b>, param)</code> except that this method ensures that the input contains only one
     * image. If more than one image is found, then an exception is throw. This is often desirable
     * when the input is a collection of {@link Tile}s, since having more than one "image" (where
     * "image" in this context means an input mosaic as a whole) means that we failed to create a
     * single mosaic from a set of source tiles.
     *
     * @param  input The image input, typically as a {@link File}.
     * @param  param The write parameters, or {@code null} for the default.
     * @return {@code true} on success, or {@code false} if the process has been aborted.
     * @throws IOException If an error occurred while reading or writing.
     *
     * @since 3.00
     */
    public boolean writeFromInput(final Object input, final ImageWriteParam param) throws IOException {
        return writeFromInput(input, 0, param, true);
    }

    /**
     * Reads the image from the given input and writes it as a set of tiles. The input is typically
     * a {@link File} object, but other kind of inputs may be accepted depending on available image
     * readers. The output files and tiling layout can be specified as a collection of {@link Tile}
     * objects given to {@link #setOutput(Object)} method.
     *
     * @param  input The image input, typically as a {@link File}.
     * @param  inputIndex The image index to read from the given input file.
     * @param  param The write parameters, or {@code null} for the default.
     * @return {@code true} on success, or {@code false} if the process has been aborted.
     * @throws IOException If an error occurred while reading or writing.
     *
     * @todo Current implementation do not yet supports source region and subsampling settings.
     *       An exception will be thrown if any of those parameters are set.
     */
    public boolean writeFromInput(final Object input, final int inputIndex,
                                  final ImageWriteParam param) throws IOException
    {
        return writeFromInput(input, inputIndex, param, false);
    }

    /**
     * Implements the public {@code writeFromInput} method.
     *
     * @param onlyOneImage If {@code true}, then the operation fails if the input contains more than
     *        one image. This is often necessary if the input is a collection of {@link TileManager}s,
     *        since more than 1 image means that the manager failed to create a single mosaic from
     *        a set of source images.
     */
    final boolean writeFromInput(final Object input, final int inputIndex,
            final ImageWriteParam param, boolean onlyOneImage) throws IOException
    {
        final boolean success;
        final ImageReader reader = getImageReader(input, inputIndex, param);
        final Queue<ReaderInputPair.WithWriter> cache = new LinkedList<>();
        try {
            if (onlyOneImage && reader.getNumImages(false) <= 1) {
                onlyOneImage = false;
            }
            // Write the image only if 'onlyOneImage' is false, otherwise we already failed.
            success = !onlyOneImage && writeFromReader(reader, inputIndex, param, cache);
            close(reader.getInput(), input);
        } finally {
            /*
             * Make sure that we delete the temporary files. This is the most important
             * cleanup. Other cleanup (disposing the ImageWriters) is good practice but
             * less important since the garbage collector would collect them anyway.
             */
            try {
                reader.dispose();
            } finally {
                deleteTemporaryFiles();
            }
            synchronized (cache) {
                for (final ReaderInputPair.WithWriter entry : cache) {
                    entry.writer.dispose();
                }
                cache.clear();
            }
        }
        if (onlyOneImage) {
            throw new InvalidImageStoreException(Errors.getResources(locale)
                    .getString(Errors.Keys.ILLEGAL_MOSAIC_INPUT));
        }
        return success;
    }

    /**
     * Reads the image from the given reader and writes it as a set of tiles.
     * It is the caller responsibility to dispose the reader when writing is done.
     *
     * @param reader The image reader configured for reading the image to mosaic. This reader
     *        should have been created by {@link #getImageReader(Object, int, ImageWriteParam)}.
     */
    private boolean writeFromReader(final ImageReader reader, final int inputIndex,
            final ImageWriteParam writeParam, final Queue<ReaderInputPair.WithWriter> cache)
            throws IOException
    {
        clearAbortRequest();
        final int outputIndex;
        final TileWritingPolicy policy;
        if (writeParam instanceof MosaicImageWriteParam) {
            final MosaicImageWriteParam param = (MosaicImageWriteParam) writeParam;
            outputIndex = param.getOutputIndex();
            policy = param.getTileWritingPolicy();
        } else {
            outputIndex = 0;
            policy = TileWritingPolicy.DEFAULT;
        }
        processImageStarted(outputIndex);
        /*
         * Gets the reader first - especially before getOutput() - because the user may have
         * overridden filter(ImageReader) and set the output accordingly. TileBuilder does that.
         */
        final TileManager[] managers = getOutput();
        if (managers == null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_IMAGE_OUTPUT));
        }
        final List<Tile> tiles;
        final int bytesPerPixel;
        if (policy == TileWritingPolicy.NO_WRITE) {
            tiles = Collections.emptyList();
            bytesPerPixel = 1;
        } else {
            tiles = new LinkedList<>(managers[outputIndex].getTiles());
            /*
             * Computes an estimation of the amount of memory to be required for each pixel.
             * This estimation may not be accurate especially for image packing many pixels
             * per byte, but a value too high is probably better than a value too low.
             */
            final SampleModel model;
            final ImageTypeSpecifier type = reader.getRawImageType(inputIndex);
            if (type != null && (model = type.getSampleModel()) != null) {
                bytesPerPixel = Math.max(1, model.getNumBands() *
                        DataBuffer.getDataTypeSize(model.getDataType()) / Byte.SIZE);
            } else {
                bytesPerPixel = 3; // Assuming RGB, since we don't have better information.
            }
        }
        final int initialTileCount = tiles.size();
        final float progressScale = 100f / initialTileCount;
        /*
         * If the user do not wants to overwrite existing tiles (for faster processing when this
         * write process is started again after a previous failure), removes from the collection
         * every tiles which already exist.
         */
        if (!policy.overwrite) {
            for (final Iterator<Tile> it=tiles.iterator(); it.hasNext();) {
                final Tile tile = it.next();
                final Object input = tile.getInput();
                if (input instanceof File) {
                    final File file = (File) input;
                    if (file.isFile()) {
                        it.remove();
                    }
                }
            }
        }
        /*
         * Creates now the various other objects to be required in the loop. This include a
         * RTree initialized with the tiles remaining after the removal in the previous block.
         */
        final int             nThreads  = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor  = Executors.newFixedThreadPool(nThreads, Threads.createThreadFactory("MosaicImageWriter #"));
        final Semaphore  submitPermits  = new Semaphore(nThreads + 1);
        final List<Future<?>> tasks     = new ArrayList<>();
        final TreeNode        tree      = new GridNode(tiles.toArray(new Tile[tiles.size()]));
        final ImageReadParam  readParam = reader.getDefaultReadParam();
        final boolean         logWrites = isLoggable();
        final boolean         logReads  = !(reader instanceof LogProducer);
        if (!logReads) {
            // Use the field, not the local variable, because we want null for the default logging.
            ((LogProducer) reader).setLogLevel(logLevel);
        }
        if (writeParam != null) {
            if (writeParam.getSourceXSubsampling() != 1 || writeParam.getSubsamplingXOffset() != 0 ||
                writeParam.getSourceYSubsampling() != 1 || writeParam.getSubsamplingYOffset() != 0 ||
                writeParam.getSourceRegion() != null)
            {
                // TODO: Not yet supported. May be supported in a future version
                // if we have time to implement such support.
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.UNEXPECTED_ARGUMENT_FOR_INSTRUCTION_1, "writeFromInput"));
            }
            readParam.setSourceBands(writeParam.getSourceBands());
        }
        final long maximumMemory = getMaximumMemoryAllocation();
        int maximumPixelCount = (int) (maximumMemory / bytesPerPixel);
        BufferedImage image = null;
        int cachedImageWidth  = 0, cachedImageTileWidth  = 0,
            cachedImageHeight = 0, cachedImageTileHeight = 0;
        while (!tiles.isEmpty()) {
            if (abortRequested()) {
                processWriteAborted();
                executor.shutdown();
                return false;
            }
            /*
             * Gets the source region for some initial tile from the list. We will write as many
             * tiles as we can using a single image. The tiles successfully written will be removed
             * from the list, so next iterations will process only the remaining tiles.
             */
            final Dimension imageSubsampling = new Dimension(); // Computed by next line.
            final Tile imageTile = getEnclosingTile(tiles, tree, imageSubsampling, maximumPixelCount);
            final Rectangle imageRegion = imageTile.getAbsoluteRegion();
            /*
             * Following line must be invoked before we touch to the image. It makes sure that
             * all background threads have finished their work with the previous image content.
             */
            awaitTermination(tasks, initialTileCount - tiles.size(), progressScale);
            if (image != null) {
                final int width  = imageRegion.width  / imageSubsampling.width;
                final int height = imageRegion.height / imageSubsampling.height;
                if (width <= image.getWidth() && height <= image.getHeight()) {
                    ImageUtilities.fill(image, FILL_VALUE);
                    assert isEmpty(image, new Rectangle(width, height));
                } else {
                    // Needs a bigger image than what we had previously.
                    image = null;
                }
                /*
                 * Next iteration may try to allocate bigger images. We do that unconditionally,
                 * even if current image fits, because current image may be small due to memory
                 * constraint during a previous iteration.
                 */
                maximumPixelCount = (int) (maximumMemory / bytesPerPixel);
            }
            readParam.setDestination(image);
            readParam.setSourceRegion(imageRegion);
            readParam.setSourceSubsampling(imageSubsampling.width, imageSubsampling.height, 0, 0);
            if (readParam instanceof MosaicImageReadParam) {
                ((MosaicImageReadParam) readParam).setNullForEmptyImage(true);
            }
            if (logReads) {
                log(false, Vocabulary.Keys.LOADING_1, imageTile);
            }
            /*
             * Before to attempt image loading, ask explicitly for a garbage collection cycle.
             * In theory we should not do that, but experience suggests that it really prevents
             * OutOfMemoryError when creating large images. If we still get OutOfMemoryError,
             * we will try again with smaller value of 'maximumPixelCount'.
             */
            System.gc();
            /*
             * Now process to the image loading. If we fail with an OutOfMemoryError (which
             * typically happen while creating the large BufferedImage), reduce the amount
             * of memory that we are allowed to use and try again.
             */
            try {
                image = reader.read(inputIndex, readParam);
            } catch (OutOfMemoryError error) {
                maximumPixelCount >>>= 1;
                if (maximumPixelCount == 0) {
                    throw error;
                }
                if (logWrites) {
                    log(true, Loggings.Keys.RECOVERABLE_OUT_OF_MEMORY_1,
                            ((float) imageRegion.width * imageRegion.height) / (1024 * 1024f));
                }
                // Go back to the while(!tiles.isEmpty()) condition, which will ask for a
                // new Tile from the same (not yet modified) collection of tiles but with
                // a smaller maximumPixelCount value.
                continue;
            }
            if (logWrites && image != readParam.getDestination()) {
                // Logs only if we have created a new image.
                logMemoryUsage();
            }
            assert (image == null) || // Image can be null if MosaicImageReader found no tiles.
                   (image.getWidth()  * imageSubsampling.width  >= imageRegion.width &&
                    image.getHeight() * imageSubsampling.height >= imageRegion.height) : imageTile;
            /*
             * Searches tiles inside the same region with a resolution which is equals or lower by
             * an integer ratio. If such tiles are found we can write them using the image loaded
             * above instead of loading subimages.  Giving that loading even a portion from a big
             * file may be long, the performance enhancement of doing so is significant.
             */
            assert tiles.contains(imageTile) : imageTile;
            for (final Iterator<Tile> it=tiles.iterator(); it.hasNext();) {
                final Tile tile = it.next();
                final Dimension subsampling = tile.getSubsampling();
                if (!isDivisor(subsampling, imageSubsampling)) {
                    continue;
                }
                final Rectangle sourceRegion = tile.getAbsoluteRegion();
                if (!imageRegion.contains(sourceRegion)) {
                    continue;
                }
                final int xSubsampling = subsampling.width  / imageSubsampling.width;
                final int ySubsampling = subsampling.height / imageSubsampling.height;
                sourceRegion.translate(-imageRegion.x, -imageRegion.y);
                sourceRegion.x      /= imageSubsampling.width;
                sourceRegion.y      /= imageSubsampling.height;
                sourceRegion.width  /= imageSubsampling.width;
                sourceRegion.height /= imageSubsampling.height;
                if (image != null && (policy.includeEmpty || !isEmpty(image, sourceRegion))) {
                    /*
                     * Before to create a new ImageWriter, wait for the executor to catch up
                     * with pending tasks. If we enqueue every tasks without waiting, we would
                     * have a lot of ImageWriter instances with only a few of them actually in
                     * use.
                     */
                    try {
                        submitPermits.acquire();
                    } catch (InterruptedException e) {
                        throw new InterruptedIOException(e.getLocalizedMessage());
                    }
                    /*
                     * Get an image writer (maybe from the cache) and configure it. We do the
                     * configuration here because we want the 'filter' and 'onTileWrite' methods
                     * (which may be overridden by the user) to be invoked in this thread.
                     */
                    final ReaderInputPair.WithWriter cacheEntry = getImageWriter(tile, image, cache);
                    final ImageWriteParam wp = cacheEntry.writer.getDefaultWriteParam();
                    onTileWrite(tile, wp);
                    wp.setSourceRegion(sourceRegion);
                    wp.setSourceSubsampling(xSubsampling, ySubsampling, 0, 0);
                    if (wp.canWriteTiles()) {
                        if (cachedImageWidth != sourceRegion.width) {
                            cachedImageWidth  = sourceRegion.width;
                            cachedImageTileWidth = imageTileSize(cachedImageWidth);
                        }
                        if (cachedImageHeight != sourceRegion.height) {
                            cachedImageHeight  = sourceRegion.height;
                            cachedImageTileHeight = imageTileSize(cachedImageHeight);
                        }
                        wp.setTilingMode(ImageWriteParam.MODE_EXPLICIT);
                        wp.setTiling(cachedImageTileWidth, cachedImageTileHeight, 0, 0);
                    }
                    final IIOImage iioImage = new IIOImage(image, null, null);
                    /*
                     * Submit the image write for execution in a background thread.
                     */
                    tasks.add(executor.submit(new Callable<Object>() {
                        @Override public Object call() throws IOException {
                            final Object tileInput;
                            try {
                                if (abortRequested()) {
                                    return null;
                                }
                                if (logWrites) {
                                    log(false, Vocabulary.Keys.SAVING_1, tile);
                                }
                                tileInput = tile.getInput();
                                final ImageWriter writer = cacheEntry.writer;
                                boolean success = false;
                                try {
                                    writer.write(null, iioImage, wp);
                                    close(writer.getOutput(), tileInput);
                                    success = true;
                                } finally {
                                    if (success) {
                                        /*
                                         * The write operation succeed: return the ImageWriter
                                         * to the pool of writers, so the next write operation
                                         * can recycle it.
                                         */
                                        writer.reset();
                                        synchronized (cache) {
                                            cache.add(cacheEntry);
                                        }
                                    } else {
                                        /*
                                         * The write operation failed. Dispose the ImageWriter
                                         * (do not return it to the pool) and delete the file
                                         * if possible. The exception that caused the failure
                                         * will be propagated after this block.
                                         */
                                        final Object output = writer.getOutput();
                                        writer.dispose();
                                        close(output, null); // Unconditional close.
                                        if (tileInput instanceof File) {
                                            ((File) tileInput).delete();
                                        }
                                    }
                                }
                            } finally {
                                submitPermits.release();
                            }
                            /*
                             * Write the TFW file.
                             */
                            if (tileInput instanceof File) {
                                AffineTransform gridToCRS = tile.getGridToCRS();
                                if (gridToCRS != null) {
                                    gridToCRS = new AffineTransform(gridToCRS);
                                    final Point location = tile.getLocation();
                                    gridToCRS.translate(location.x, location.y);
                                    SupportFiles.writeTFW((File) tileInput, gridToCRS);
                                }
                            }
                            return null;
                        }
                    }));
                    /*
                     * While submitPermits.acquire() was waiting in the above code, some image
                     * writers may have been pushed back to the cache. Ensure that the cache is
                     * keept to a reasonable size by disposing, if needed, the oldest writers.
                     * We do this cleanup here instead than after submitPermits.acquire() because
                     * getImageWriter(...) may have selected a writer that we would otherwise have
                     * disposed.
                     */
                    synchronized (cache) {
                        while (cache.size() > 2*nThreads) {
                            cache.remove().writer.dispose();
                        }
                        if (false) { // Removed from compilation except when debugging.
                            final ThreadPoolExecutor e = (ThreadPoolExecutor) executor;
                            LOGGER.log(getFineLevel(),
                                    "Active threads: {0}  " +
                                    "Enqueued tasks: {1}  " +
                                    "Available permits: {2}  " +
                                    "Available ImageWriters: {3}",
                                new Integer[] {
                                    e.getActiveCount(),
                                    e.getQueue().size(),
                                    submitPermits.availablePermits(),
                                    cache.size()
                            });
                        }
                    }
                }
                /*
                 * The current tile has been processed. It may have been discarded because it
                 * contains only transparent pixels, or it may have been enqueued for writing
                 * in a background thread. Remove this tile from the list of tiles to process,
                 * and inspect the next tile.
                 */
                it.remove();
                if (!tree.remove(tile)) {
                    throw new AssertionError(tile); // Should never happen.
                }
            }
            assert !tiles.contains(imageTile) : imageTile;
        }
        /*
         * At this point, every tiles have been submitted for writing. Wait for the write
         * operations to complete. The remaining ImageWriter instances will be disposed by
         * the caller.
         */
        awaitTermination(tasks, initialTileCount, progressScale);
        executor.shutdown();
        if (abortRequested()) {
            processWriteAborted();
            return false;
        } else {
            processImageComplete();
            return true;
        }
    }

    /**
     * Closes the given stream if it is different than the user object. When different, the
     * output is not the {@link File} (or whatever object) given by {@link Tile}. It is probably
     * an {@link ImageOutputStream} created by {@link #getImageWriter}, so we need to close it.
     */
    private static void close(final Object stream, final Object user) throws IOException {
        if (stream != user) {
            IOUtilities.close(stream);
        }
    }

    /**
     * Waits for every tasks to be completed. The tasks collection is emptied by this method.
     *
     * @param  tasks The list of tasks to wait for completion.
     * @param  done The number of tiles written so far, assuming that every pending tasks are
     *         already completed.
     * @param  progressScale The factor by which to multiply the number of tiles done in order
     *         to get a percentage of completion.
     * @throws IOException if at least one task threw a {@code IOException}.
     */
    private void awaitTermination(final List<Future<?>> tasks, int done, final float progressScale)
            throws IOException
    {
        done -= tasks.size();
        Throwable exception = null;
        for (int i=0; i<tasks.size(); i++) {
            Future<?> task = tasks.get(i);
            try {
                task.get();
                processImageProgress(progressScale * done++);
                continue;
            } catch (ExecutionException e) {
                if (exception == null) {
                    exception = e.getCause();
                }
                // Abort after the catch block.
            } catch (InterruptedException e) {
                // Abort after the catch block.
            }
            abort();
            for (int j=tasks.size(); --j>i;) {
                task = tasks.get(j);
                if (task.cancel(false)) {
                    tasks.remove(j);
                }
            }
        }
        tasks.clear();
        if (exception != null) {
            if (exception instanceof IOException) {
                throw (IOException) exception;
            }
            if (exception instanceof RuntimeException) {
                throw (RuntimeException) exception;
            }
            if (exception instanceof Error) {
                throw (Error) exception;
            }
            throw new UndeclaredThrowableException(exception);
        }
    }

    /**
     * Logs message for the given tile.
     *
     * @param log {@code true} if the given key is from the {@link Loggings} bundle,
     *        or {@code false} if it is from the {@link Vocabulary} bundle.
     */
    private void log(final boolean log, final short key, final Object arg) {
        final IndexedResourceBundle bundle = log ?
                Loggings.getResources(locale) : Vocabulary.getResources(locale);
        final LogRecord record = bundle.getLogRecord(getFineLevel(), key, arg);
        record.setSourceClassName(MosaicImageWriter.class.getName());
        record.setSourceMethodName("writeFromInput");
        record.setLoggerName(LOGGER.getName());
        LOGGER.log(record);
    }

    /**
     * Returns a tile which enclose other tiles at finer resolution. Some tile layouts have a few
     * big tiles with low resolution covering the same geographic area than many smaller tiles with
     * finer resolution. If such overlapping is found, then this method returns one of the big tiles
     * and sets {@code imageSubsampling} to some subsampling that may be finer than usual for the
     * returned tile. Reading the big tile with that subsampling allows {@code MosaicImageWriter}
     * to use the same {@link RenderedImage} for writing both the big tile and the finer ones.
     * Example:
     *
     * {@preformat text
     *     ┌───────────────────────┐          ┌───────────┬───────────┐
     *     │                       │          │Subsampling│Subsampling│
     *     │      Subsampling      │          │  = (2,2)  │  = (2,2)  │
     *     │        = (4,4)        │          ├───────────┼───────────┤
     *     │                       │          │Subsampling│Subsampling│
     *     │                       │          │  = (2,2)  │  = (2,2)  │
     *     └───────────────────────┘          └───────────┴───────────┘
     * }
     *
     * Given the above, this method will returns the tile illustrated on the left side and set
     * {@code imageSubsampling} to (2,2).
     * <p>
     * The algorithm implemented in this method is far from optimal and surely doesn't return
     * the best tile in all case. It is a compromize attempting to reduce the amount of image
     * data to load without too much CPU cost.
     *
     * @param tiles The tiles to examine. This collection is not modified.
     * @param tree Same as {@code tiles}, but as a tree for faster searches.
     * @param imageSubsampling Where to store the subsampling to use for reading the tile.
     *        This is an output parameter only.
     * @param maximumPixelCount Tries to return a tile having an area smaller than this limit.
     *        This is only a hint honored on a "best effort" basis: there is no guarantees that
     *        this limit will actually be respected.
     */
    private static Tile getEnclosingTile(final List<Tile> tiles, final TreeNode tree,
            final Dimension imageSubsampling, final int maximumPixelCount) throws IOException
    {
        final Set<Dimension> subsamplingDone = tiles.size() > 24 ? new HashSet<Dimension>() : null;
        int     selectedCount = 0;
        Tile    selectedTile  = null;
        Tile    fallbackTile  = null; // Used only if we failed to select a tile.
        long    fallbackArea  = Long.MAX_VALUE;
        assert tree.containsAll(tiles);
search: for (final Tile tile : tiles) {
            /*
             * Gets the collection of tiles in the same area than the tile we are examinating.
             * We will retain the tile with the largest filtered collection. Filtering will be
             * performed only if there is some chance to get a larger collection than the most
             * sucessful one so far.
             */
            final Rectangle region = tile.getAbsoluteRegion();
            final Dimension subsampling = tile.getSubsampling();
            if (subsamplingDone != null && !subsamplingDone.add(subsampling)) {
                /*
                 * In order to speedup this method, examine only one tile for each subsampling
                 * value. This is a totally arbitrary choice but work well for the most common
                 * tile layouts (constant area & constant size). Without such reduction, the
                 * execution time of this method is too long for large tile collections. For
                 * smaller collections, we can afford a systematic examination of all tiles.
                 */
                continue;
            }
            // Reminder: Collection in next line will be modified, so it needs to be mutable.
            final Collection<Tile> enclosed = tree.containedIn(region);
            assert enclosed.contains(tile) : tile;
            if (enclosed.size() <= selectedCount) {
                assert selectedTile != null : selectedCount;
                continue; // Already a smaller collection - no need to do more in this iteration.
            }
            /*
             * Found a collection that may be larger than the most successful one so far. First,
             * gets the smallest subsampling. We will require tiles at the finest resolution to
             * be written in the first pass before to go up in the pyramid. If they were written
             * last, those small tiles would be read one-by-one, which defeat the purpose of this
             * method (to read a bunch of tiles at once).
             */
            Dimension finestSubsampling = subsampling;
            int smallestPixelArea = subsampling.width * subsampling.height;
            for (final Tile subtile : enclosed) {
                final Dimension s = subtile.getSubsampling();
                final int pixelArea = s.width * s.height;
                if (pixelArea < smallestPixelArea) {
                    smallestPixelArea = pixelArea;
                    finestSubsampling = s;
                }
            }
            long area = (long) region.width * (long) region.height;
            area /= smallestPixelArea;
            if (area > maximumPixelCount) {
                // Retains the smallest one of the too big tiles.
                // To be used only if no suitable tile is found.
                if (area < fallbackArea) {
                    fallbackArea = area;
                    fallbackTile = tile;
                }
                continue;
            }
            /*
             * Found a subsampling that may be finer than the tile subsampling. Now removes
             * every tiles which would consume too much memory if we try to read them using
             * that subsampling. If the tiles to be removed are the finest ones, search for
             * an other tile to write because we really want the finest resolution to be
             * written first (see previous comment).
             */
            for (final Iterator<Tile> it=enclosed.iterator(); it.hasNext();) {
                final Tile subtile = it.next();
                final Dimension s = subtile.getSubsampling();
                if (!isDivisor(subsampling, s)) {
                    it.remove();
                    if (s.equals(finestSubsampling)) {
                        continue search;
                    }
                    continue;
                }
                final Rectangle subregion = subtile.getAbsoluteRegion();
                area = (long) subregion.width * (long) subregion.height;
                area /= smallestPixelArea;
                if (area > maximumPixelCount) {
                    it.remove();
                    if (s.equals(finestSubsampling)) {
                        continue search;
                    }
                }
            }
            /*
             * Retains the tile with the largest filtered collection of sub-tiles.
             * We count the number of tiles remaining after the removal performed
             * by the code above.
             */
            final int tileCount = enclosed.size();
            if (selectedTile == null || tileCount > selectedCount) {
                selectedTile = tile;
                selectedCount = tileCount;
                imageSubsampling.setSize(finestSubsampling);
            }
        }
        /*
         * The selected tile may still null if 'maximumPixelCount' is so small than even the
         * smallest tile doesn't fit. We will return the smallest tile anyway, maybe letting
         * a OutOfMemoryError to occurs in the caller if really the tile can't fit in the
         * available memory. We perform this try anyway because estimation of available memory
         * in Java is only approximative.
         */
        if (selectedTile == null) {
            selectedTile = fallbackTile;
            imageSubsampling.setSize(fallbackTile.getSubsampling());
        }
        return selectedTile;
    }

    /**
     * Returns {@code true} if the given denominator is a divisor of the given numerator
     * for both {@linkplain Dimension#width width} and {@linkplain Dimension#height height}.
     */
    private static boolean isDivisor(final Dimension numerator, final Dimension denominator) {
        return (numerator.width  % denominator.width ) == 0 &&
               (numerator.height % denominator.height) == 0;
    }

    /**
     * Returns the maximal amount of memory that {@link #writeFromInput writeFromInput} is allowed
     * to use. The default implementation computes a value from the amount of memory available in
     * the current JVM. Subclasses can override this method for returning a different value.
     * <p>
     * The returned value will be considered on a <cite>best effort</cite> basis. There is no
     * guarantee that no more memory than the returned value will be used.
     *
     * @return An estimation of the maximum amount of memory allowed for allocation, in bytes.
     */
    public long getMaximumMemoryAllocation() {
        final Runtime runtime = Runtime.getRuntime();
        final long maxMemory = runtime.maxMemory();
        runtime.gc();
        long memory = runtime.freeMemory();
        /*
         * Add the amount of memory that the JVM is still allowed to allocate from the OS.
         */
        memory += maxMemory - runtime.totalMemory();
        /*
         * Keep a safety margin of 12.5% of the maximum available memory. This is 8 Mb if the
         * default maximum memory is 64 Mb. This is 128 Mb if the maximum memory is 1 Gb.
         */
        memory -= maxMemory / 8;
        /*
         * Return at least 16 Mb, assuming that we can't do much with less than that.
         */
        return Math.max(16L*1024*1024, memory);
    }

    /**
     * Send to the given logger some informations about current memory usage.
     */
    private void logMemoryUsage() {
        final Vocabulary resources = Vocabulary.getResources(locale);
        final Runtime runtime = Runtime.getRuntime();
        final long totalMemory = runtime.totalMemory();
        final double usage = 1 - (float) runtime.freeMemory() / totalMemory;
        final LogRecord record = new LogRecord(getFineLevel(),
                resources.getString(Vocabulary.Keys.MEMORY_HEAP_SIZE_1, totalMemory / (1024*1024L)) + ". " +
                resources.getString(Vocabulary.Keys.MEMORY_HEAP_USAGE_1, usage) + '.');
        record.setSourceClassName(MosaicImageWriter.class.getName());
        record.setSourceMethodName("writeFromInput"); // The public API invoking this method.
        record.setLoggerName(LOGGER.getName());
        LOGGER.log(record);
    }

    /**
     * Returns {@code true} if this writer is allowed to copy source images to temporary
     * uncompressed RAW files. Doing so can speed up considerably the creation of large
     * mosaics, at the expense of temporary disk space.
     * <p>
     * The default implementation returns {@code true} if the following conditions are meet:
     * <p>
     * <ul>
     *   <li>The input tiles format is not an uncompressed format like RAW, BMP or TIFF (the
     *       later is assumed uncompressed, while this is not always true). There is no
     *       advantage to copy an uncompressed format to an other uncompressed format.</li>
     *   <li>The {@linkplain File#getUsableSpace() usable space} in the temporary directory
     *       is greater then the space required for writing the input tiles in RAW format.</li>
     *   <li>The usable space in the output directory is greater than the space required for
     *       writing the output tiles in their output format (a very approximative compression
     *       factor is guessed).</li>
     *   <li>Other conditions (not documented because they may change in future implementations).
     *       In case of doubt, it is safer to conservatively return {@code false}.</li>
     * </ul>
     * <p>
     * Subclasses should override this method if they can provide a better answer, or if they
     * known that their source tiles are already uncompressed.
     *
     * {@note This method is invoked only in the context of <code>writeFromInput(Object, &hellip;)</code>
     *        methods, which get the image to write from an <code>ImageReader</code>. This method is
     *        not invoked in the context of the <code>write(RenderedImage)</code> method because an
     *        image available in memory (ignoring JAI tiling) is assumed to not need disk cache. For
     *        this reason there is no <code>isCachingEnabled(RenderedImage)</code> method.}
     *
     * @param  input The input image or mosaic, an an {@code ImageReader} with its
     *         {@linkplain ImageReader#getInput() input} set.
     * @param  inputIndex The image index to read from the given input file.
     * @return {@code true} if this writer can cache the source tiles.
     * @throws IOException If this method required an I/O operation and that operation failed.
     *
     * @since 3.00
     */
    protected boolean isCachingEnabled(final ImageReader input, final int inputIndex) throws IOException {
        /*
         * Gets an estimation of the available memory. This will be used for computing the maximal
         * size of input images. The calculation will need to take in account the number of bits per
         * pixels. Note that we use "bits", not "bytes". We do not divide bitPerPixels by Byte.SIZE
         * now, because we don't want the rounding toward zero here.
         */
        final Runtime rt = Runtime.getRuntime(); rt.gc();
        final long maxInputSize = rt.maxMemory() - (rt.totalMemory() - rt.freeMemory());
        final int bitPerPixels = IIOUtilities.bitsPerPixel(input.getRawImageType(inputIndex));
        /*
         * Checks the space available in the temporary directory, which
         * will contain the temporary uncompressed files for source tiles.
         */
        long available = TemporaryFile.getSharedTemporaryDirectory().getUsableSpace();
        long required = 4L * 1024 * 1024; // Arbitrary margin of 4 Mb.
        if (input instanceof MosaicImageReader) {
            boolean compressed = false;
            for (final TileManager tiles : ((MosaicImageReader) input).getInput()) {
                /*
                 * If there is not enough disk space for holding the uncompressed files,
                 * stops immediately the check: we can not cache the tiles.
                 */
                required += tiles.diskUsage() * bitPerPixels / Byte.SIZE;
                if (required > available) {
                    return false;
                }
                /*
                 * If there is not enough memory for holding fully the largest tile,
                 * stops immediately the check: we can not cache the tiles.
                 */
                if (tiles.largestTileArea() * bitPerPixels / Byte.SIZE > maxInputSize) {
                    return false;
                }
                /*
                 * Checks if there is at least one uncompressed tile. As soon as we have
                 * found one such uncompressed file, we will not need to check anymore.
                 */
                if (!compressed) {
                    for (final ImageReaderSpi spi : tiles.getImageReaderSpis()) {
                        if (IIOUtilities.guessCompressionRatio(spi) != 1) {
                            compressed = true;
                            break;
                        }
                    }
                }
            }
            /*
             * If every tiles are written in an uncompressed format like RAW, then there
             * is no advantage to copy them to an other uncompressed format. Note that we
             * consider TIFF as uncompressed, which is what we get by default using the
             * Java image I/O writers.
             */
            if (!compressed) {
                return false;
            }
        } else {
            /*
             * Same tests than above, but in the case where the source is a single image
             * instead than a mosaic of source tiles.
             */
            if (IIOUtilities.guessCompressionRatio(input.getOriginatingProvider()) == 1) {
                return false;
            }
            final int width  = input.getWidth (inputIndex);
            final int height = input.getHeight(inputIndex);
            final long size = ((long) width) * ((long) height) * bitPerPixels / Byte.SIZE;
            if (size > maxInputSize || (required += size) > available) {
                return false;
            }
        }
        /*
         * Checks the space available for the target tiles, which may be compressed.
         * Note that we do not reset the 'required' count to zero - we assume that
         * the target directory is on the same device than the temporary directory,
         * so the required space needs to be summed. I'm not aware of an easy way to
         * check if we are on the same device in Java 6, however new Java 7 API would
         * allow to do so.
         */
        final TileManager[] outputs = getOutput();
        if (outputs != null) {
            for (final TileManager tiles : outputs) {
                final File root = tiles.rootDirectory();
                if (root == null) {
                    continue; // Not a file - there is nothing we can do.
                }
                available = root.getUsableSpace();
                long usage = tiles.diskUsage() * bitPerPixels / Byte.SIZE;
                for (final ImageReaderSpi spi : tiles.getImageReaderSpis()) {
                    int ir = IIOUtilities.guessCompressionRatio(spi);
                    if (ir != 0) {
                        usage /= ir;
                        break; // Use the first known format as the reference.
                    }
                }
                required += usage;
                if (required > available) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Invoked after {@code MosaicImageWriter} has created a reader and
     * {@linkplain ImageReader#setInput(Object) set the input}. Users can override this method
     * for performing additional configuration and may returns {@code false} if the given reader
     * is not suitable. The default implementation returns {@code true} in all case.
     *
     * @param  reader The image reader created and configured by {@code MosaicImageWriter}.
     * @return {@code true} If the given reader is ready for use, or {@code false} if an other
     *         reader should be fetched.
     * @throws IOException if an error occurred while inspecting or configuring the reader.
     */
    protected boolean filter(final ImageReader reader) throws IOException {
        return true;
    }

    /**
     * Invoked after {@code MosaicImageWriter} has created a writer and
     * {@linkplain ImageWriter#setOutput(Object) set the output}. Users can override this method
     * for performing additional configuration and may returns {@code false} if the given writer
     * is not suitable. The default implementation returns {@code true} in all case.
     *
     * @param  writer The image writer created and configured by {@code MosaicImageWriter}.
     * @return {@code true} If the given writer is ready for use, or {@code false} if an other
     *         writer should be fetched.
     * @throws IOException if an error occurred while inspecting or configuring the writer.
     */
    protected boolean filter(final ImageWriter writer) throws IOException {
        return true;
    }

    /**
     * Returns {@code true} if the given region in the given image contains only fill values.
     *
     * @param image The image to test.
     * @param region The region in the image to test.
     */
    private boolean isEmpty(final BufferedImage image, final Rectangle region) {
        int[] data = null;
        final Raster raster = image.getRaster();
        final int xmax = region.x + region.width;
        final int ymax = region.y + region.height;
        for (int y=region.y; y<ymax; y++) {
            for (int x=region.x; x<xmax; x++) {
                data = raster.getPixel(x, y, data);
                for (int i=data.length; --i>=0;) {
                    if (data[i] != FILL_VALUE) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Invoked automatically when a tile is about to be written. The default implementation does
     * nothing. Subclasses can override this method in order to set custom write parameters.
     * <p>
     * The {@linkplain ImageWriteParam#setSourceRegion source region} and
     * {@linkplain ImageWriteParam#setSourceSubsampling source subsampling} parameters can not be
     * set through this method. Their setting will be overwritten by the caller because their
     * values depend on the strategy chosen by {@code MosaicImageWriter} for reading images,
     * which itself depends on the amount of available memory.
     *
     * @param  tile The tile to be written.
     * @param  parameters The parameters to be given to the {@linkplain ImageWriter image writer}.
     *         This method is allowed to change the parameter values.
     * @throws IOException if an I/O operation was required and failed.
     */
    protected void onTileWrite(final Tile tile, ImageWriteParam parameters) throws IOException {
    }

    /**
     * Gets and initializes an {@linkplain ImageReader image reader} that can decode the
     * {@linkplain BufferedImageOp#filter filtered} input. The returned reader has its
     * {@linkplain ImageReader#setInput input} already set. If the reader input is different than
     * the specified one, then it is probably an {@linkplain ImageInputStream image input stream}
     * and closing it is caller's responsibility.
     *
     * @param  input The input to read.
     * @param  inputIndex The image index to read from the given input file.
     * @param  The parameters given by the user, or {@code null} if none.
     * @return The image reader that seems to be the most appropriated (never {@code null}).
     * @throws IOException If no suitable image reader has been found or if an error occurred
     *         while creating an image reader or initializing it.
     */
    private ImageReader getImageReader(final Object input, final int inputIndex,
            final ImageWriteParam parameters) throws IOException
    {
        BufferedImageOp op = null;
        final ImageReader reader = getImageReader(input);
        if (parameters instanceof MosaicImageWriteParam) {
            final MosaicImageWriteParam param = (MosaicImageWriteParam) parameters;
            if (param.getTileWritingPolicy() == TileWritingPolicy.NO_WRITE) {
                return reader;
            }
            op = param.getSourceTileFilter();
        }
        /*
         * If we are allowed to cache an uncompressed copies of the tiles, do that now.
         */
        if (op != null || isCachingEnabled(reader, inputIndex)) {
            final Collection<Tile> tiles = new ArrayList<>();
            if (reader instanceof MosaicImageReader) {
                for (final TileManager manager : ((MosaicImageReader) reader).getInput()) {
                    tiles.addAll(manager.getTiles());
                }
            } else {
                // Note: we must not use reader.getInput() since it may be an ImageInputStream.
                tiles.add(new Tile(reader.getOriginatingProvider(), input, 0, new Point(), (Dimension) null));
            }
            /*
             * TODO: Folllowing has been disabled for now because the whole org.geotoolkit.internal.rmi package
             *       has been removed. This is because JDK8 provides a new "fork join" framework make our stuff
             *       obsolete (but we didn't migrated to JDK8 yet). The plan is to rewrite the functionality
             *       from scratch in the Apache SIS project. Since the temporary files are optional, disabling
             *       this code should not break the application. It may make it much slower, but we had issues
             *       with the RAW format used by this code anyway.
             */
//          temporaryFiles.putAll(RMI.execute(new TileCopier(tiles, op)));
        }
        return reader;
    }

    /**
     * Gets and initializes an {@linkplain ImageReader image reader} that can decode the specified
     * input. The returned reader has its {@linkplain ImageReader#setInput input} already set. If
     * the reader input is different than the specified one, then it is probably an {@linkplain
     * ImageInputStream image input stream} and closing it is caller's responsibility.
     * <p>
     * This method partially duplicates the work of
     * {@link org.geotoolkit.image.io.XImageIO#getReader(Object, Boolean, Boolean)} except for the
     * special handling of mosaic input (which may use the internal {@link CachingMosaicReader})
     * and the calls to the user-overrideable {@link #filter(ImageReader)} method.
     *
     * @param  input The input to read.
     * @return The image reader that seems to be the most appropriated (never {@code null}).
     * @throws IOException If no suitable image reader has been found or if an error occurred
     *         while creating an image reader or initializing it.
     */
    private ImageReader getImageReader(final Object input) throws IOException {
        /*
         * First check if the input type is one of those accepted by MosaicImageReader.
         * We perform this check in a dedicaced code because we replace the default reader
         * by our custom CachingMosaicReader.
         */
        if (MosaicImageReader.Spi.DEFAULT.canDecodeInput(input)) {
            // Creates an instance of CachingMosaicReader unconditionally, even if
            // the map of temporary files is empty, because it may be filled later.
            final ImageReader reader = new CachingMosaicReader(temporaryFiles, input);
            if (filter(reader)) {
                return reader;
            }
            reader.dispose();
        }
        ImageInputStream stream = null;
        boolean createStream = false;
        /*
         * The following loop will be executed at most twice. The first iteration tries the given
         * input directly. The second iteration tries the input wrapped in an ImageInputStream.
         */
        do {
            final Object candidate;
            if (createStream) {
                stream = Tile.createImageInputStream(input);
                if (stream == null) {
                    continue;
                }
                candidate = stream;
            } else {
                candidate = input;
            }
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(candidate);
            while (readers.hasNext()) {
                final ImageReader reader = readers.next();
                reader.setInput(candidate);
                // If there is any more advanced check to perform, we should do it here.
                // For now we accept the reader unconditionally.
                if (filter(reader)) {
                    return reader;
                }
                reader.dispose();
            }
        } while ((createStream = !createStream) == true);
        if (stream != null) {
            stream.close();
        }
        /*
         * Before to give up, if the input is a file or a directory, try to open it has a mosaic.
         */
        Exception cause = null;
        if (input instanceof File) {
            TileManager[] managers = null;
            try {
                managers = TileManagerFactory.DEFAULT.create((File) input);
            } catch (Exception e) { // Catch: IOException and RuntimeException
                cause = e;
            }
            if (managers != null) {
                final ImageReader reader = new CachingMosaicReader(temporaryFiles, managers);
                if (filter(reader)) {
                    return reader;
                }
                reader.dispose();
            }
        }
        throw new UnsupportedImageFormatException(Errors.format(Errors.Keys.NO_IMAGE_READER), cause);
    }

    /**
     * Gets and initializes an {@link ImageWriter} that can encode the specified image. The
     * returned writer has its {@linkplain ImageWriter#setOutput output} already set. If the
     * output is different than the {@linkplain Tile#getInput tile input}, then it is probably
     * an {@link ImageOutputStream} and closing it is caller responsibility.
     * <p>
     * This method extracts an {@code ImageWriter} instance from the given cache, if possible.
     * If no suitable writer is available, then a new one is created but <strong>not</strong>
     * cached; it is caller responsibility to reset the writer and cache it after the write
     * operation has been completed.
     *
     * @param  tile  The tile to encode.
     * @param  image The image associated to the specified tile.
     * @param  cache An initially empty list of image writers created during the write process.
     * @return The image writer that seems to be the most appropriated (never {@code null}).
     * @throws IOException If no suitable image writer has been found or if an error occurred
     *         while creating an image writer or initializing it.
     */
    private ReaderInputPair.WithWriter getImageWriter(final Tile tile, final RenderedImage image,
            final Queue<ReaderInputPair.WithWriter> cache) throws IOException
    {
        // Note: we rename "Tile.input" as "output" because we want to write in it.
        final Object         output     = tile.getInput();
        final Class<?>       outputType = output.getClass();
        final ImageReaderSpi readerSpi  = tile.getImageReaderSpi();
        ImageOutputStream    stream     = null; // Created only if needed.
        /*
         * The result of this method is determined entirely by the (readerSpi, outputType)
         * pair and by implementation of the user-overrideable filter(ImageWriter) method.
         * We will search iteratively for the first suitable entry.
         *
         * Note: Using Map<ReaderInputPair, Queue<ImageWriter>> could be more performant than
         * the iteration performed below, but the queue is usually very short since its length
         * is approximatively the number of processors. In addition, in the typical case where
         * all tiles use the same format, the iterator will stop at the first item in the queue.
         * So a Map would really bring no performance benefit for the "normal" case at the cost
         * of more complex code (harder to count the total number of ImageWriters and to dispose
         * the oldest ones).
         */
        final ReaderInputPair.WithWriter cacheEntry = new ReaderInputPair.WithWriter(readerSpi, outputType);
        if (cache != null) {
            ReaderInputPair.WithWriter candidate = null;
            synchronized (cache) {
                for (final Iterator<ReaderInputPair.WithWriter> it=cache.iterator(); it.hasNext();) {
                    final ReaderInputPair.WithWriter c = it.next();
                    if (cacheEntry.equals(c)) {
                        candidate = c;
                        it.remove();
                        break;
                    }
                }
            }
            /*
             * If we have found a candidate, define its output and check if filter(ImageWriter)
             * accepts it. If the image writer is not accepted, the remaining of this method will
             * try to get an other instance from the IIORegistry.
             *
             * Note that the remaining of this method basically perform the same check 4 times,
             * each time using a different way to get the ImageWriter instances to test.
             */
            if (candidate != null) {
                final ImageWriter writer = candidate.writer;
                if (candidate.needStream) {
                    stream = ImageIO.createImageOutputStream(output);
                    writer.setOutput(stream);
                } else {
                    writer.setOutput(output);
                }
                if (filter(writer)) {
                    return candidate;
                }
                writer.dispose();
            }
        }
        /*
         * The search will be performed using two different strategies:
         *
         *  1) Check the plugins specified in 'spiNames' since we assume that they
         *     will encode the image in the best suited format for the reader.
         *  2) (to be run if the above strategy did not found a suitable writer),
         *     look for providers by their format name.
         */
        int spiNameIndex = 0;
        final String[] spiNames    = readerSpi.getImageWriterSpiNames();
        final String[] formatNames = readerSpi.getFormatNames();
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        final Set<ImageWriterSpi> providers = new LinkedHashSet<>();
        List<ImageWriterSpi> ignored = null; // To be initialized when first needed.
        Iterator<ImageWriterSpi>  it = null; // To be initialized when first needed.
        boolean canIgnore = true;
        while (true) { // The exit point is in the middle of the loop, after "if (!it.hasNext())".
            final ImageWriterSpi spi;
            if (spiNames != null && spiNameIndex < spiNames.length) {
                /*
                 * ---- First strategy (see above comment) --------
                 */
                final String spiName = spiNames[spiNameIndex++];
                final Class<?> spiType;
                try {
                    spiType = Class.forName(spiName);
                } catch (ClassNotFoundException e) {
                    // May be normal. Search for an other writer.
                    Logging.recoverableException(LOGGER, MosaicImageWriter.class, "getImageWriter", e);
                    continue;
                }
                final Object candidate = registry.getServiceProviderByClass(spiType);
                if (!(candidate instanceof ImageWriterSpi)) {
                    // No instance is registered for the given class. Search an other writer.
                    continue;
                }
                spi = (ImageWriterSpi) candidate;
            } else {
                /*
                 * ---- Second strategy (see above comment) --------
                 *      To be run only after every providers
                 *      listed in 'spiNames' have been tried.
                 */
                if (it == null) {
                    // Executed the first time that the second strategy is run.
                    it = registry.getServiceProviders(ImageWriterSpi.class, true);
                }
                /*
                 * If we have examined every pertinent providers listed by IIORegistry but some
                 * of those providers have been ignored, and if we found no "normal" provider,
                 * then take a second look to the providers that we have ignored.
                 *
                 * The ignored providers are considered only if we found no "normal" providers,
                 * because the "normal" providers are examined again after this loop and should
                 * have precedence over the ignored providers.
                 */
                if (!it.hasNext()) {
                    if (ignored == null || !providers.isEmpty()) {
                        // This is the only exit point of the while(true) loop.
                        break;
                    }
                    it = ignored.iterator();
                    ignored   = null;
                    canIgnore = false;
                }
                spi = it.next();
                if (!ArraysExt.intersects(formatNames, spi.getFormatNames())) {
                    // Not a provider for the format we are looking for.
                    continue;
                }
                /*
                 * Ignore the providers that are wrappers around "native" providers, because we
                 * don't want to write metadata like ".prj" or ".tfw" files for every tiles.
                 * However keep trace of the ignored providers, so we can give a second look
                 * at them later if we find no "normal" provider.
                 */
                if (canIgnore && Tile.ignore(spi)) {
                    if (ignored == null) {
                        ignored = new ArrayList<>(4);
                    }
                    ignored.add(spi);
                    continue;
                }
            }
            /*
             * If a suitable writer is found and is capable to encode the output, returns
             * it immediately. Otherwise the writers are stored in an Set as we found them,
             * in order to try them again with an ImageOutputStream after this loop.
             */
            if (spi.canEncodeImage(image) && providers.add(spi)) {
                for (final Class<?> legalType : spi.getOutputTypes()) {
                    if (legalType.isAssignableFrom(outputType)) {
                        final ImageWriter writer = spi.createWriterInstance();
                        writer.setOutput(output);
                        if (filter(writer)) {
                            if (stream != null) {
                                stream.close();
                            }
                            cacheEntry.writer = writer;
                            return cacheEntry;
                        }
                        writer.dispose();
                        break;
                    }
                }
            }
        }
        /*
         * No provider accept the output directly. This output is typically a File or URL.
         * Creates an image output stream from it and try again.
         */
        if (!providers.isEmpty()) {
            if (stream == null) {
                stream = ImageIO.createImageOutputStream(output);
            }
            if (stream != null) {
                final Class<? extends ImageOutputStream> streamType = stream.getClass();
                for (final ImageWriterSpi spi : providers) {
                    for (final Class<?> legalType : spi.getOutputTypes()) {
                        if (legalType.isAssignableFrom(streamType)) {
                            final ImageWriter writer = spi.createWriterInstance();
                            writer.setOutput(stream);
                            if (filter(writer)) {
                                cacheEntry.writer = writer;
                                cacheEntry.needStream = true;
                                return cacheEntry;
                            }
                            writer.dispose();
                            break;
                        }
                    }
                }
            }
        }
        if (stream != null) {
            stream.close();
        }
        throw new UnsupportedImageFormatException(Errors.format(Errors.Keys.NO_IMAGE_WRITER));
    }

    /**
     * Suggests a tile size for the given image size. This is invoked only for image format
     * that support tiling, for example TIFF. Current implementation search for the first
     * value equals or greater than 64, which is the tile size used by GDAL.
     *
     * @param  imageSize The image size.
     * @return The suggested tile size.
     */
    private static int imageTileSize(final int imageSize) {
        final int[] divisors = MathFunctions.divisors(imageSize);
        int i = Arrays.binarySearch(divisors, IMAGE_TILE_SIZE);
        if (i < 0) i = ~i;
        return (i < divisors.length) ? divisors[i] : imageSize;
    }

    /**
     * Returns the default stream metadata, or {@code null} if none.
     * The default implementation returns {@code null} in all cases.
     */
    @Override
    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return null;
    }

    /**
     * Returns the default image metadata, or {@code null} if none.
     * The default implementation returns {@code null} in all cases.
     */
    @Override
    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
        return null;
    }

    /**
     * Returns stream metadata initialized to the specified state, or {@code null}.
     * The default implementation returns {@code null} in all cases since this plugin
     * doesn't provide metadata encoding capabilities.
     */
    @Override
    public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
        return null;
    }

    /**
     * Returns image metadata initialized to the specified state, or {@code null}.
     * The default implementation returns {@code null} in all cases since this plugin
     * doesn't provide metadata encoding capabilities.
     */
    @Override
    public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
        return null;
    }

    /**
     * Deletes all temporary files.
     */
    private void deleteTemporaryFiles() {
        for (final Iterator<RawFile> it=temporaryFiles.values().iterator(); it.hasNext();) {
            final File file = it.next().file;
            if (!TemporaryFile.delete(file)) {
                file.deleteOnExit();
            }
            it.remove();
        }
    }

    /**
     * Resets this writer to its initial state. If there is any temporary files,
     * they will be deleted.
     */
    @Override
    public void reset() {
        deleteTemporaryFiles();
        super.reset();
    }

    /**
     * Disposes resources held by this writer. This method should be invoked when this
     * writer is no longer in use, in order to release some threads created by the writer.
     */
    @Override
    public void dispose() {
        deleteTemporaryFiles();
        super.dispose();
    }

    // It is not strictly necessary to override finalize() since ThreadPoolExecutor
    // already invokes shutdown() in its own finalize() method.

    /**
     * Service provider for {@link MosaicImageWriter}. This service provider is not strictly
     * compliant with the Image I/O specification since it cant not work with
     * {@link javax.imageio.stream.ImageOutputStream}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @author Cédric Briançon (Geomatys)
     * @version 3.18
     *
     * @since 2.5
     * @module
     */
    public static class Spi extends ImageWriterSpi {
        /**
         * The default instance. There is no instance of this provider registered in the
         * standard {@link javax.imageio.spi.IIORegistry}, because this provider is not
         * strictly compliant with the Image I/O requirement (in particular, the Mosaic
         * Image Writer does not accept {@link javax.imageio.stream.ImageOutputStream}).
         * This constant can be used as a replacement.
         */
        public static final Spi DEFAULT = new Spi();

        /**
         * Creates a default provider. This constructor does not set the {@link #outputTypes}
         * field in order to delay loading of {@link Tile} and {@link TileManager} classes
         * as much as possible.
         */
        public Spi() {
            vendorName      = "Geotoolkit.org";
            version         = Utilities.VERSION.toString();
            names           = MosaicImageReader.Spi.NAMES;
            pluginClassName = "org.geotoolkit.image.io.mosaic.MosaicImageWriter";
        }

        /**
         * Returns the types of objects that may be used as arguments to the
         * {@link MosaicImageWriter#setOutput(Object)} method. This method
         * initializes the {@link #outputTypes} field when first needed.
         *
         * @since 3.18
         */
        @Override
        public synchronized Class<?>[] getOutputTypes() {
            if (outputTypes == null) {
                // Initializes the field only when first needed in order to
                // delay the class loading of TileManager and Tile classes.
                outputTypes = new Class<?>[] {
                    TileManager[].class, // Preferred type.
                    TileManager.class,
                    Tile[].class,
                    Collection.class
                    // No File type, because directories of tiles are not expected to exist
                    // (after all, this is MosaicImageWriter job to create it!).
                };
            }
            return super.getOutputTypes();
        }

        /**
         * Returns {@code true} if the image writer can encode the given output. The default
         * implementation returns {@code true} if the given object is an instance assignable
         * to one of the types returned by the {@linkplain #getOutputTypes()} implementation
         * of this {@code Spi} class, and other type-specific restrictions are meet (e.g.
         * {@link Collection} contains only instances of {@link Tile}, <i>etc.</i>).
         *
         * @param  destination The output to be encoded.
         * @return {@code true} If the image writer can encode the given output.
         * @throws IOException If an I/O operation was required and failed.
         *
         * @since 3.14
         */
        public boolean canEncodeOutput(final Object destination) throws IOException {
            return MosaicImageReader.Spi.DEFAULT.canDecodeInput(destination);
        }

        /**
         * Returns {@code true} if this writer is likely to be able to encode images with the given
         * layout. The default implementation returns {@code true} in all cases. The capability to
         * encode images depends on the tile format specified in {@link Tile} objects, which are
         * not known to this provider.
         */
        @Override
        public boolean canEncodeImage(ImageTypeSpecifier type) {
            return true;
        }

        /**
         * Returns a new {@link MosaicImageWriter}.
         *
         * @throws IOException If an I/O operation was required and failed.
         */
        @Override
        public ImageWriter createWriterInstance(Object extension) throws IOException {
            return new MosaicImageWriter(this);
        }

        /**
         * Returns a brief, human-readable description of this service provider.
         *
         * @todo Localize.
         */
        @Override
        public String getDescription(final Locale locale) {
            return "Mosaic Image Writer";
        }
    }
}
