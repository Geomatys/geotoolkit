/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.processing.coverage.resample;

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

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.storage.IOUtilities;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.geometry.Envelopes;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.io.large.LargeCache;
import org.geotoolkit.image.io.large.LargeRenderedImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;

/**
 * A resample operation using a provided MathTransform to convert a source coverage.
 *
 * The process is designed to use multi-threading capacity, and not overload in memory.
 * To do such things, we need an {@link ImageWriter} supporting {@link ImageWriter#canReplacePixels(int) } operation.
 *
 * Here is how it works :
 * 1 - Using specified parameter {@link IOResampleDescriptor#TILE_SIZE}, we prepare tiles from output image, and put them in a queue, waiting for resampling.
 * 2 - We create a fix number of threads (as specified by {@link IOResampleDescriptor#THREAD_COUNT}), which will poll data from above queue, and resample it.
 * 3 - Each computed tile is put into another queue, waiting to be written.
 * 4 - We've got a thread for image writing. It's listening on output queue, and write each ready tile inserted into it.
 *
 * @author Alexis Manin (Geomatys)
 */
public class IOResampleProcess extends AbstractProcess {

    private static final Logger LOGGER = Logging.getLogger(IOResampleProcess.class);

    private static final int LANCZOS_WINDOW = 2;

    /**
     * Timeout parameters for queue transactions.
     */
    public static final int TIMEOUT = 100;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    /** A default size to use if user did not specified a tile size. */
    private static final Dimension DEFAULT_TILE_SIZE = new Dimension(256, 256);

    /** Limit for queue capacity. */
    private static final int QUEUE_SIZE = 20;

    /**
     * A queue to store strips we want to resample.
     */
    private final LinkedBlockingQueue<Rectangle> resamplingQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);

    /**
     * A queue to store resampled strips we must write. We give priority to the tiles which are closer to upper-left corner of the output image.
     */
    private final PriorityBlockingQueue<Map.Entry<Point, RenderedImage>> writingQueue = new PriorityBlockingQueue<>(QUEUE_SIZE, new Comparator<Map.Entry<Point, RenderedImage>>() {
        @Override
        public int compare(Map.Entry<Point, RenderedImage> o1, Map.Entry<Point, RenderedImage> o2) {
            if (o1 instanceof EndOfFile) {
                return -1;
            } else if (o2 instanceof EndOfFile) {
                return 1;
            } else {
                Point first = o1.getKey();
                Point second = o2.getKey();
                final int linePriority = second.x - first.x;
                // If the two point are on the same line, we must know which is the most advanced on it.
                return (linePriority != 0) ? linePriority : second.y - first.y;
            }
        }
    });

    private Integer threadNumber;

    public IOResampleProcess(ProcessDescriptor desc, ParameterValueGroup input) {
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

        threadNumber = Parameters.value(IOResampleDescriptor.THREAD_COUNT, inputParameters);
        if (threadNumber == null || threadNumber != threadNumber || threadNumber < 1) {
            threadNumber = Math.min(5, Math.max(2, Runtime.getRuntime().availableProcessors() / 2));
        }

        Dimension tileSize = Parameters.value(IOResampleDescriptor.TILE_SIZE, inputParameters);
        if (tileSize == null || tileSize.width <= 0 || tileSize.height <= 0) {
            tileSize = DEFAULT_TILE_SIZE;
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

            // Prepare the input image. We use a LargeRenderedImage, because we'll get random access to pixels, and if it's too big, we need a cache system.
            final ImageTypeSpecifier rawImageType = inImage.getRawImageType(0);
            final ColorModel colorModel = rawImageType.getColorModel();
            final LargeRenderedImage rawImage = new LargeRenderedImage(inImage, 0, LargeCache.getInstance(), tileSize);

            /*
             * Prepare output image for writing. If no file location is given, we create a new TIF temporary file to
             * store result. If user did not specified size for target image, we compute one from given transformation.
             */
            Integer width = Parameters.value(IOResampleDescriptor.OUT_WIDTH, inputParameters);
            Integer height = Parameters.value(IOResampleDescriptor.OUT_HEIGHT, inputParameters);
            if (width == null || height == null || width <= 0 || height <= 0) {
                final GeneralEnvelope transformed = Envelopes.transform(operator, new GeneralEnvelope(new double[]{0, 0}, new double[]{rawImage.getWidth(), rawImage.getHeight()}));
                width = (int) Math.ceil(transformed.getSpan(0));
                height = (int) Math.ceil(transformed.getSpan(1));
            }
            if (width <= 0 && height <= 0) {
                throw new ProcessException("Impossible to define a proper size for output image.", this, null);
            }

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

            ImageWriter writer = getWriter(imageFormat, output);

            if (writer == null) {
                throw new IOException("No fitting image writer can be found on the system for format : " + imageFormat
                        + ". Note that orthorectification process needs a writer capable of writing images piece by piece.");
            }

            // We want to create a tiled image as output.
            ImageWriteParam outputParam = writer.getDefaultWriteParam();
            outputParam.setTilingMode(ImageWriteParam.MODE_EXPLICIT);
            outputParam.setTiling(tileSize.width, tileSize.height, 0, 0);
            writer.prepareWriteEmpty(null, rawImageType, width, height, null, null, outputParam);
            writer.prepareReplacePixels(0, null);

            final int bandNumber = colorModel.getNumComponents();
            final double[] defaultPixelValue = new double[bandNumber];
            Arrays.fill(defaultPixelValue, Double.NaN);

            // Prepare multi-threading service. We create as many threads as user asked for resampling, plus one for strip writing.
            final ArrayList<Future> runnableResults = new ArrayList<>();
            final ExecutorService resampleService = Executors.newFixedThreadPool(threadNumber);
            for (int threadCounter = 0; threadCounter < threadNumber; threadCounter++) {
                // Duplicate iterators and interpolator because they're not thread-safe.
                final PixelIterator it = PixelIteratorFactory.createDefaultIterator(rawImage);
                final Interpolation interpol = Interpolation.create(it, toUse, LANCZOS_WINDOW);
                runnableResults.add(
                        resampleService.submit(new ResampleThread(operator, interpol, defaultPixelValue, colorModel)));
            }

            final ExecutorService writerService = Executors.newSingleThreadExecutor();
            runnableResults.add(
                    writerService.submit(new Writer(writer)));

            LOGGER.log(Level.INFO, "We'll now start resampling. Output image dimension : width " + width + " px | height " + height + " px.");
            execTimes.add(System.currentTimeMillis());

            // once we've submit all tiles to compute, we just have to wait resamplers to end their work. After that,
            // writing queue should be full. We will add the end trigger.
            populateResamplingQueue(width, height, tileSize);
            resampleService.shutdown();
            resampleService.awaitTermination(1, TimeUnit.DAYS);

            poisonWritingQueue();

            writerService.shutdown();
            writerService.awaitTermination(1, TimeUnit.DAYS);

            // Check possible thread errors :
            for (Future result : runnableResults) {
                try {
                    result.get(TIMEOUT, TIMEOUT_UNIT);
                } catch (ExecutionException e) {
                    if (e.getCause() != null) {
                        throw e.getCause();
                    } else {
                        throw e;
                    }
                }
            }

            writer.endReplacePixels();
            writer.endWriteEmpty();
            writer.setOutput(null);
            writer.dispose();

            Parameters.getOrCreate(IOResampleDescriptor.OUT_COVERAGE, outputParameters).setValue(output);

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
     * @param tileSize The {@link Dimension} we want for a single tile.
     */
    private void populateResamplingQueue(final int imageWidth, final int imageHeight, final Dimension tileSize) throws InterruptedException {
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
                resamplingQueue.offer(resampleZone, TIMEOUT, TIMEOUT_UNIT);
            }
        }

        // insert poison objects, so our threads will know it's over when they get it.
        poisonResamplingQueue();
    }

    private void poisonResamplingQueue() {
        for (int i = 0; i <= threadNumber; i++) {
            try {
                resamplingQueue.offer(new EmptyBox(), TIMEOUT, TIMEOUT_UNIT);
            } catch (InterruptedException e) {
                LOGGER.log(Level.INFO, "Process interrupted !");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void poisonWritingQueue() {
        writingQueue.offer(new EndOfFile<Point, RenderedImage>(), TIMEOUT, TIMEOUT_UNIT);
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
        final Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(extension);
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
                        LOGGER.log(Level.INFO, "Resampling thread acquired end of the queue.");
                        return;
                    }
                    gridTransform = MathTransforms.concatenate(
                            new AffineTransform2D(1d, 0, 0, 1d, (double) computeZone.x, (double) computeZone.y), baseTransform);
                    destination = new BufferedImage(outCModel,
                            outCModel.createCompatibleWritableRaster(computeZone.width, computeZone.height), false, null);
                    final Resample resampler = new Resample(gridTransform, destination, interpolator, fillValue);
                    resampler.fillImagePx();
                    final Map.Entry<Point, RenderedImage> output = new AbstractMap.SimpleEntry<>(computeZone.getLocation(), (RenderedImage) destination);

                    writingQueue.offer(output, TIMEOUT, TIMEOUT_UNIT);
                }
                LOGGER.log(Level.INFO, "Owner thread of the resample has been interrupted.");
            } catch (InterruptedException e) {
                LOGGER.log(Level.INFO, "Resampling worker interrupted !");
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Resampling thread error !", e);
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
                    if (toWrite instanceof EndOfFile) {
                        LOGGER.log(Level.INFO, "Writing thread acquired end of the queue.");
                        return;
                    }

                    writeParam.setDestinationOffset(toWrite.getKey());
                    writer.replacePixels(toWrite.getValue(), writeParam);
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.INFO, "Writing thread interrupted !");
                // We die, but not alone
                poisonResamplingQueue();
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Writer thread error !", e);
                poisonResamplingQueue();
                throw new RuntimeException(e);
            }
        }
    }

    /** A poisonous object to tell our resamplers there is no more data. */
    private static class EmptyBox extends Rectangle {}

    /** A poisonous object to tell writer he can shutdown. */
    private static class EndOfFile<A, B> implements Map.Entry<A, B> {
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
