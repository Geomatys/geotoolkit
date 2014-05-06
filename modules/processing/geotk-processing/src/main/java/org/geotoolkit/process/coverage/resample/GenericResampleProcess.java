package org.geotoolkit.process.coverage.resample;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;

import java.io.RandomAccessFile;
import org.apache.sis.internal.storage.IOUtilities;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.io.large.LargeCache;
import org.geotoolkit.image.io.large.LargeRenderedImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;

/**
 * A resample operation using a provided MathTransform to convert a source coverage.
 * 
 * The process is designed to use multi-threading capacity, and not overload in memory.
 * To do such things, we need an {@link ImageWriter} suporting {@link ImageWriter#canReplacePixels(int) } operation.
 * 
 * Here is how it works : 
 * 1 - Using specified parameter {@link GenericResampleDescriptor#BLOCK_SIZE}, we take strips from input image, and put them in a queue, waiting for resampling.
 * 2 - We create a fix number of threads (as specified by {@link GenericResampleDescriptor#THREAD_COUNT}), which will poll data from above queue, and resmple it.
 * 3 - Each resampled strip is put into another queue, waiting to be written.
 * 4 - We've got a thread for image writing. It's listening on output queue, and write each resampled strip inserted into it.
 * 
 * /!\ IMPORTANT : The process is designed to work with strips, but it can be easily modified to use tiles instead : 
 * Modify {@link #populateResamplingQueue(int, int, java.awt.Dimension)} parameters to build tiles instead of strips.
 *
 * @author Alexis Manin (Geomatys)
 */
public class GenericResampleProcess extends AbstractProcess {

    private static final Logger LOGGER = Logging.getLogger(GenericResampleProcess.class);

    private static final int LANCZOS_WINDOW = 2;

    /**
     * The default size (in number of bytes) for the tiles to create. It will be used if no {@link GenericResampleDescriptor#BLOCK_SIZE} is given by user.
     * To make it easier to understand (and modify), we decompose it as :
     * tile width(px) * tile height(px) * band number * component type size (byte)
     */
    public final static long BASE_MEMORY_SIZE = 1024 * 1024 * 4 * 1;

    /** Maximum size for image cache. */
    public final static long MAX_MEMORY_SIZE = BASE_MEMORY_SIZE * 512;

    public static final int WAIT_TIME = 100;

    /**
     * A queue to store strips we want to resample.
     */
    private final LinkedBlockingQueue<Rectangle> resamplingQueue = new LinkedBlockingQueue<>(100);

    /**
     * A queue to store resampled strips we must write.
     */
    private final PriorityBlockingQueue<Map.Entry<Point, RenderedImage>> writingQueue = new PriorityBlockingQueue<>(100, new Comparator<Map.Entry<Point, RenderedImage>>() {
        @Override
        public int compare(Map.Entry<Point, RenderedImage> o1, Map.Entry<Point, RenderedImage> o2) {
            Point first = o1.getKey();
            Point second = o2.getKey();

            final int linePriority = second.x - first.x;
            // If the two point are on the same line, we must know which is the most advanced on it.
            return (linePriority != 0)? linePriority : second.y - first.y;
        }
    });

    private Integer threadNumber;

    public GenericResampleProcess(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    @Override
    protected void execute() throws ProcessException {

        final List<Long> execTimes = new ArrayList<>();
        execTimes.add(System.currentTimeMillis());

        /*
         * CHECK INPUTS
         */
        final ImageReader inImage = (ImageReader) inputParameters.parameter("image").getValue();
        final MathTransform operator = (MathTransform) inputParameters.parameter("operation").getValue();
        final String interpolation = (String) inputParameters.parameter("interpolation").getValue();

        threadNumber = Parameters.value(GenericResampleDescriptor.THREAD_COUNT, inputParameters);
        if (threadNumber == null || threadNumber != threadNumber || threadNumber < 1) {
            threadNumber = Math.min(5, Math.max(2, Runtime.getRuntime().availableProcessors() / 2));
        }

        Long blockSize = Parameters.value(GenericResampleDescriptor.BLOCK_SIZE, inputParameters);
        if (blockSize == null || blockSize != blockSize || blockSize < 0) {
            blockSize = BASE_MEMORY_SIZE;
        }
        InterpolationCase toUse = InterpolationCase.BILINEAR;
        for (final InterpolationCase icase : InterpolationCase.values()) {
            if (icase.name().equalsIgnoreCase(interpolation)) {
                toUse = icase;
                break;
            }
        }

        try {
            /*
             * INITIALIZE IO OBJECTS
             */

            // Prepare the input image. We use a LargeRenderedImage, because we'll get random access to image pixels,
            // and if it's too big, we need a cache system.
            final ImageTypeSpecifier rawImageType = inImage.getRawImageType(0);
            final ColorModel colorModel = rawImageType.getColorModel();
            final Dimension rawTileSize = new Dimension(256, 256);
            final LargeRenderedImage rawImage = new LargeRenderedImage(inImage, 0, LargeCache.getInstance(MAX_MEMORY_SIZE), rawTileSize);

            /*
             * Prepare output image for writing. If no file location is given, we create a new TIF temporary file to
             * store result. If user did not specified size for target image, we take the same as raw image.
             */
            Integer value = (Integer) inputParameters.parameter("width").getValue();
            final int width = (value != null) ? value : rawImage.getWidth();
            value = (Integer) inputParameters.parameter("height").getValue();
            final int height = (value != null) ? value : rawImage.getHeight();

            File output;
            String imageFormat = null;
            try {
                final String outLocation = (String) inputParameters.parameter("outputLocation").getValue();
                output = new File(outLocation);
                if (output.isDirectory()) {
                    output = new File(output, "GR_" + UUID.randomUUID() + ".tif");
                } else {
                    imageFormat = IOUtilities.extension(output);
                }
            } catch (Exception e) {
                output = File.createTempFile("GR_" + UUID.randomUUID(), ".tif");
            }

            if (imageFormat == null || imageFormat.isEmpty()) {
                imageFormat = "tif";
            }

            ImageOutputStream outStream = ImageIO.createImageOutputStream(new RandomAccessFile(output, "rw"));
            ImageWriter writer;
            try {
                writer = getWriter(imageFormat, outStream);
            } catch (Exception e) {
                writer = getWriter(imageFormat, output);
                outStream.close();
                outStream = null;
            }

            if (writer == null) {
                throw new IOException("No fitting image writer can be found on the system for format : " + imageFormat
                        + ". Note that orthorectification process needs a writer capable of writing images piece by piece.");
            }

            writer.prepareWriteEmpty(null, rawImageType, width, height, null, null, null);
            writer.prepareReplacePixels(0, new Rectangle(0, 0, width, height));

            final int bandNumber = colorModel.getNumComponents();
            final double[] defaultPixelValue = new double[bandNumber];
            Arrays.fill(defaultPixelValue, Double.NaN);
            
            /*
             * Due to ImageIO bug, the writing of final image piece by piece can't be set with square tile. Problem is :
             * when using replacePixels with an ImageWriteParam using destination offset, the X component
             * of this same offset provokes severe bugs in final raster (Memory size increase, bad xAxis pixels position).
             *
             * To bypass this problem, we must write entire rows in one pass. In consequence, used tile size must be
             * computed as following :
             * Tile width = final image width.
             * The number of rows will be computed to ensure a tile does not exceed a given constant :
             * Tile height = allowed_memory_size / (tile width * image band number * Component type size (Byte, int, etc.)).
             * 
             * We also ensure we've got at least one line to process.
             */
            long rowSize = 0;
            for (int bandCount = 0; bandCount < bandNumber; bandCount++) {
                rowSize += (width * rawImageType.getBitsPerBand(bandCount));
            }
            final int tile_size_y = Math.max(1, (int) (blockSize / rowSize));

            // Prepare multi-threading service. We create as many threads as user asked for resampling, plus one for strip writing.
            final ExecutorService threadService = Executors.newFixedThreadPool(threadNumber);
            final ArrayList<Runnable> threads = new ArrayList<>(threadNumber);
            for (int threadCounter = 0; threadCounter < threadNumber; threadCounter++) {
                // Duplicate iterators and interpolator because they're not thread-safe.
                final PixelIterator it = PixelIteratorFactory.createDefaultIterator(rawImage);
                final Interpolation interpol = Interpolation.create(it, toUse, LANCZOS_WINDOW);
                threads.add(new ResampleThread(operator, interpol, defaultPixelValue, colorModel));
            }
            
            Thread writingThread = new Thread(new Writer(writer));

            LOGGER.log(Level.INFO, "We'll now start resampling. Output image dimension : width " + width + " px | height " + height + " px.");
            execTimes.add(System.currentTimeMillis());

            // Start all the workers
            writingThread.setPriority(Thread.MAX_PRIORITY);
            writingThread.start();
            final ArrayList<Future> status = new ArrayList<>();
            for (Runnable task : threads) {
                status.add(threadService.submit(task));
            }

            populateResamplingQueue(width, height, new Dimension(width, tile_size_y));

            try {
                for (Future threadStatus : status) {
                    threadStatus.get();
                }
            } catch (ExecutionException e) {
                throw e.getCause();
            }
            threadService.shutdown();
            
            // All strips have been resampled, so the writing queue should be full. We add the end trigger.
            poisonWritingQueue();
            
            while (writingThread.isAlive()) {
                try {
                    writingThread.join(WAIT_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new ProcessException("Writing thread has been stopped.", this, e);
                }
            }

            writer.endReplacePixels();
            writer.endWriteEmpty();
            writer.setOutput(null);
            writer.dispose();
            if (outStream != null) {
                outStream.close();
            }

            Parameters.getOrCreate(GenericResampleDescriptor.OUT_COVERAGE, outputParameters).setValue(output);

            LOGGER.log(Level.INFO, "Data preparation lasts " + (execTimes.get(1) - execTimes.get(0)) + " ms\n");
            LOGGER.log(Level.INFO, "Resample lasts " + (System.currentTimeMillis() - execTimes.get(1)) + " ms\n");
        } catch (Throwable e) {
            poisonResamplingQueue();
            poisonWritingQueue();
            throw new ProcessException(e.getLocalizedMessage(), this, e);
        }
    }

    /**
     * Fill the list of strips to resample. For now, the methods creates strips to fill, maybe in the future we could replace it
     * to work with tiles.
     *
     * /!\ WARNING : All the process is based on the fact that "Poisonous objects" will be put in the queue to notify threads
     * there is no more data to treat. Don't forget it if you modify this method.
     *
     * @param imageWidth  Total width to fill
     * @param imageHeight Total height to fill
     * @param tile_size_y The height we want for a single strip.
     */
    private void populateResamplingQueue(final int imageWidth, final int imageHeight, final int tile_size_y) throws InterruptedException {
        final Thread currentThread = Thread.currentThread();
        int tileHeight;
        // Iterate through upper left corner of tiles.
        for (int y = 0; y < imageHeight; y += tile_size_y) {
            if (y + tile_size_y > imageHeight) {
                tileHeight = imageHeight - y;
            } else {
                tileHeight = tile_size_y;
            }

            final Rectangle resampleZone = new Rectangle(0, y, imageWidth, tileHeight);
            while (!currentThread.isInterrupted() && !resamplingQueue.offer(resampleZone, WAIT_TIME, TimeUnit.MILLISECONDS)) {}
        }

        // insert poison objects, so our threads will know it's over when they get it.
        poisonResamplingQueue();
    }
    
    private void populateResamplingQueue(final int imageWidth, final int imageHeight, final Dimension tileSize) throws InterruptedException {
        final Thread currentThread = Thread.currentThread();
        int tileHeight, tileWidth;
        // Iterate through upper left corner of tiles.
        for (int y = 0; y < imageHeight; y += tileSize.height) {
            if (y + tileSize.height > imageHeight) {
                tileHeight = imageHeight - y;
            } else {
                tileHeight = tileSize.height;
            }

            for (int x = 0; x < imageWidth; x += tileSize.width) {
                if (x + tileSize.width > imageWidth) {
                    tileWidth = imageWidth - x;
                } else {
                    tileWidth = tileSize.width;
                }

                final Rectangle resampleZone = new Rectangle(x, y, tileWidth, tileHeight);
                while (!currentThread.isInterrupted() && !resamplingQueue.offer(resampleZone, WAIT_TIME, TimeUnit.MILLISECONDS)) {}
            }
        }

        // insert poison objects, so our threads will know it's over when they get it.
        poisonResamplingQueue();
    }

    private void poisonResamplingQueue() {
        final Thread currentThread = Thread.currentThread();
        for (int i = 0; i <= threadNumber; i++) {
            try {
                while (!currentThread.isInterrupted() && !resamplingQueue.offer(new EmptyBox(), WAIT_TIME, TimeUnit.MILLISECONDS)) {}
            } catch (InterruptedException e) {
                LOGGER.log(Level.INFO, "Process interrupted !");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void poisonWritingQueue() {
        final Thread currentThread = Thread.currentThread();
        while (!currentThread.isInterrupted() && !writingQueue.offer(new NightShade<Point, RenderedImage>(), WAIT_TIME, TimeUnit.MILLISECONDS)) {}
    }

    /**
     * Try to get an image writer which can process output image piece by piece.
     *
     * @param extension The extension of the output image file, used to get the right format writer.
     * @param output    The output the writer will have to fill.
     * @return A writer fitting our needs, or null if we couldn't find it.
     * @throws IOException if an error occured while checking writer capabilities.
     */
    private ImageWriter getWriter(final String extension, Object output) throws IOException {
        // Check the tiff writers to know if they fit our needs : writing piece per piece.
        final Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix(extension);
        ImageWriter writer = null;
        while (writers.hasNext()) {
            writer = writers.next();
            try {
                writer.setOutput(output);
            } catch (IllegalArgumentException e) {
                continue;
            }
            if (writer.canWriteEmpty()) {
                break;
            }
        }
        return writer;
    }

    /**
     * A utility thread class to resample a piece of image.
     */
    private class ResampleThread implements Runnable {

        /**
         * The math transform which contains main transformation for resampling. It will be concatenated with additional
         * transformation which is the offset for the image to fill.
         */
        final MathTransform baseTransform;

        final Interpolation interpolator;
        final double[] fillValue;

        final ColorModel outCModel;

        public ResampleThread(MathTransform operator, Interpolation source, double[] defaultValue, ColorModel outputModel) {
            ArgumentChecks.ensureNonNull("Math transform", operator);
            ArgumentChecks.ensureNonNull("interpolator", source);
            ArgumentChecks.ensureNonNull("Fill value", defaultValue);
            ArgumentChecks.ensureNonNull("Output color model", outputModel);
            baseTransform = operator;
            interpolator = source;
            fillValue = defaultValue;
            outCModel = outputModel;
        }

        @Override
        public void run() {
            Rectangle computeZone;
            BufferedImage destination;
            MathTransform gridTransform;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    computeZone = resamplingQueue.take();
                    if (computeZone instanceof EmptyBox) {
                        return;
                    }
                    gridTransform = MathTransforms.concatenate(
                            new AffineTransform2D(1d, 0, 0, 1d, (double) computeZone.x, (double) computeZone.y), baseTransform);
                    destination = new BufferedImage(outCModel,
                            outCModel.createCompatibleWritableRaster(computeZone.width, computeZone.height), false, null);
                    final Resample resampler = new Resample(gridTransform, destination, interpolator, fillValue);
                    resampler.fillImagePx();
                    final Map.Entry<Point, RenderedImage> output = new AbstractMap.SimpleEntry<>(computeZone.getLocation(), (RenderedImage) destination);

                    while (!writingQueue.offer(output, WAIT_TIME, TimeUnit.MILLISECONDS)) {}
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.INFO, "Resampling worker interrupted !");
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // We die, but not alone
                poisonResamplingQueue();
                poisonWritingQueue();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * A thread for image writing passes. The aim is to have a single runnable which will wait for strips to write. When
     * a new strip is available, the thread will put himself into the queue which was given to him as available.
     */
    private class Writer implements Runnable {

        private final ImageWriter writer;
        final ImageWriteParam writeParam;

        public Writer(final ImageWriter writer) throws IOException {
            ArgumentChecks.ensureNonNull("Image writer", writer);

            if (!writer.canReplacePixels(0)) {
                throw new IllegalArgumentException("Input image writer is not able to write images piece by piece.");
            }

            this.writer = writer;
            writeParam = writer.getDefaultWriteParam();
        }


        @Override
        public void run() {

            Map.Entry<Point, RenderedImage> toWrite;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    toWrite = writingQueue.take();
                    if (toWrite instanceof NightShade) {
                        return;
                    }

                    writeParam.setDestinationOffset(toWrite.getKey());
                    writer.replacePixels(toWrite.getValue(), writeParam);
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.INFO, "Writing thread interrupted !");
                // We die, but not alone
                poisonResamplingQueue();
                poisonWritingQueue();
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /** A poisonous object to tell our resamplers there is no more data. */
    private static class EmptyBox extends Rectangle {}

    /** A poisonous object to tell writer he can shutdown. */
    private static class NightShade<A, B> implements Map.Entry<A, B> {
        @Override
        public A getKey() {
            throw new RuntimeException("Poisonous object !");
        }

        @Override
        public B getValue() {
            throw new RuntimeException("Poisonous object !");
        }

        @Override
        public B setValue(B value) {
            throw new RuntimeException("Poisonous object !");
        }
    }

}
