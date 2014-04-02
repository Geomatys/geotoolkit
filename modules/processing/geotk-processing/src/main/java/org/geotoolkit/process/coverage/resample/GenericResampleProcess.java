package org.geotoolkit.process.coverage.resample;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;

import org.apache.sis.internal.storage.IOUtilities;
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
 * @author Alexis Manin (Geomatys)
 */
public class GenericResampleProcess extends AbstractProcess {

    private static final Logger LOGGER = Logging.getLogger(GenericResampleProcess.class);

    private static int LANCZOS_WINDOW = 2;

    /**
     * The default size (in number of bytes) for the tiles to create. To make it easier to understand (and modify), we
     * decompose it as :
     * tile width(px) * tile height(px) * band number * component type size (byte)
     */
    public final static long BASE_MEMORY_SIZE = 1024 * 1024 * 4 * 1;

    public final static long MAX_MEMORY_SIZE = BASE_MEMORY_SIZE * 512;

    public GenericResampleProcess(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    @Override
    protected void execute() throws ProcessException {

        final List<Long> execTimes = new ArrayList<Long>();
        execTimes.add(System.currentTimeMillis());

        final ImageReader inImage = (ImageReader) inputParameters.parameter("image").getValue();
        final MathTransform operator = (MathTransform) inputParameters.parameter("operation").getValue();
        final String interpolation = (String) inputParameters.parameter("interpolation").getValue();

        Integer threadNumber = Parameters.value(GenericResampleDescriptor.THREAD_COUNT, inputParameters);
        if (threadNumber == null || threadNumber != threadNumber || threadNumber < 1) {
            threadNumber = Math.min(5, Math.max(2, Runtime.getRuntime().availableProcessors()/2 ));
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
            // Prepare the input image. We use a LargeRenderedImage, because we'll get random access to image pixels,
            // and if it's too big, we'll have to use a cache system.
            final ImageTypeSpecifier rawImageType = inImage.getRawImageType(0);
            final ColorModel colorModel = rawImageType.getColorModel();
            final Dimension rawTileSize = new Dimension(512, 512);
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
                output =  File.createTempFile("GR_" + UUID.randomUUID(), ".tif");
            }

            if(imageFormat == null || imageFormat.isEmpty()) {
                imageFormat = "tif";
            }

            ImageOutputStream outStream = ImageIO.createImageOutputStream(output);
            ImageWriter writer;
            try {
                writer = getWriter(imageFormat, outStream);
            } catch (Exception e) {
                writer = getWriter(imageFormat, output);
                outStream.close();
                outStream = null;
            }

            if (writer == null) {
                throw new IOException("No fitting image writer can be found on the system for format : "+imageFormat
                        +". Note that orthorectification process needs a writer capable of writing images piece by piece.");
            }

            writer.prepareWriteEmpty(null, rawImageType, width, height, null, null, null);
            writer.prepareReplacePixels(0, new Rectangle(0, 0, width, height));
            final ImageWriteParam writeParam = writer.getDefaultWriteParam();

            final int bandNumber = colorModel.getNumComponents();
            final double[] defaultPixelValue = new double[bandNumber];

            LOGGER.log(Level.INFO, "We'll now start resampling pass. Output image dimension : width {0} px | height {1} px.",
                    new Object[] {width, height});
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
             * We also ensure we've got at least as many lines as threads to process them.
             */
            int tile_size_y;
            long rowSize = 0;
            for (int bandCount = 0; bandCount < bandNumber; bandCount++) {
                rowSize += (width * rawImageType.getBitsPerBand(bandCount));
            }
            if ((tile_size_y = (int)(blockSize / rowSize)) < threadNumber) {
                tile_size_y = threadNumber;
            }

            /* Prepare multi-threading service. We'll divide each tile to treat
             * into a fix number of areas we will fill in different threads. Each
             * thread treat the tile on his entire width, but start at different 
             * rows.
             */
            final ExecutorService threadService = Executors.newFixedThreadPool(threadNumber);
            final ArrayList<ResampleThread> threads = new ArrayList<ResampleThread>(threadNumber);
            final int threadHeight = (int) Math.ceil(tile_size_y / (double) threadNumber);
            for (int threadCounter = 0 ; threadCounter < threadNumber ; threadCounter++) {
                // Duplicate iterators and interpolator because they're not thread-safe.
                final PixelIterator it = PixelIteratorFactory.createDefaultIterator(rawImage);
                final Interpolation interpol = Interpolation.create(it, toUse, LANCZOS_WINDOW);
                final int startRow = threadCounter*threadHeight;
                final Rectangle area = new Rectangle(0, startRow, width, Math.min(tile_size_y-startRow, threadHeight));
                threads.add(new ResampleThread(interpol, area, defaultPixelValue));
            }
            
            execTimes.add(System.currentTimeMillis());

            int tileHeight;
            // Iterate through upper left corner of tiles.
            for (int y = 0; y < height; y += tile_size_y) {
                if (y + tile_size_y > height) {
                    tileHeight = height - y;
                } else {
                    tileHeight = tile_size_y;
                }

                LOGGER.log(Level.INFO, "Computing rows {0} to {1}. Total row number : {2}",
                        new Object[] {y, y+tileHeight, height});

                final long begin = System.currentTimeMillis();

                final WritableRaster raster = colorModel.createCompatibleWritableRaster(width, tileHeight);
                final BufferedImage destImage = new BufferedImage(colorModel, raster, false, null);

                final AffineTransform2D gridTranslation = new AffineTransform2D(1d, 0, 0, 1d, 0d, (double)y);
                final MathTransform transformer = MathTransforms.concatenate(gridTranslation, operator);
                
                // If the last tile is smaller than others, we don't mess with threads.
                if (tileHeight < tile_size_y) {
                    final PixelIterator it = PixelIteratorFactory.createDefaultIterator(rawImage);
                    final Interpolation interpol = Interpolation.create(it, toUse, LANCZOS_WINDOW);
                    final Resample resampler = new Resample(transformer, destImage, interpol, defaultPixelValue);
                    resampler.fillImage();
                } else {
                    for (ResampleThread thread : threads) {
                        thread.setDestination(destImage);
                        thread.setGridTransform(transformer);
                    }
                    final List<Future<Boolean>> status = threadService.invokeAll(threads);
                    try {
                        for (Future threadStatus : status) {
                            threadStatus.get();
                        }
                    } catch (ExecutionException e) {
                        throw e.getCause();
                    }
                }

                final long endResample = System.currentTimeMillis();

                writeParam.setDestinationOffset(new Point(0, y));
                writer.replacePixels(raster, writeParam);

                final long endWriting = System.currentTimeMillis();

                LOGGER.log(Level.INFO, "Resample times for current rows : {0}," +
                        "\nWriting time for row writing : {1}",
                        new Object[]{endResample-begin, endWriting-endResample});
            }
            
            writer.endReplacePixels();
            writer.endWriteEmpty();
            writer.setOutput(null);
            writer.dispose();
            if (outStream != null) {
                outStream.close();
            }

            threadService.shutdown();
            
            execTimes.add(System.currentTimeMillis());

            Parameters.getOrCreate(GenericResampleDescriptor.OUT_COVERAGE, outputParameters).setValue(output);

            LOGGER.log(Level.INFO, "Data preparation lasts " + (execTimes.get(1) - execTimes.get(0)) + " ms\n");
            LOGGER.log(Level.INFO, "Resample lasts " + (execTimes.get(2) - execTimes.get(1)) + " ms\n");
        } catch (Throwable e) {
            throw new ProcessException(e.getMessage(), this, e);
        }
    }

    /**
     * Try to get an image writer which can process output image piece by piece.
     * @param extension The extension of the output image file, used to get the right format writer.
     * @param output The output the writer will have to fill.
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
    private class ResampleThread implements Callable<Boolean> {

        final Interpolation interpolator;
        final Rectangle area;
        final double[] fillValue;
        
        BufferedImage destination = null;
        MathTransform gridTransform = null;
        
        public ResampleThread(Interpolation source, Rectangle toFill, double[] defaultValue) {
            interpolator = source;
            area = toFill;
            fillValue = defaultValue;
        }
        
        public void setDestination (BufferedImage toSet) {
            final Rectangle imageDim = new Rectangle(toSet.getMinX(), toSet.getMinY(), toSet.getWidth(), toSet.getHeight());
            if (!imageDim.contains(area)) {
                throw new IllegalArgumentException("The given bufferedImage does not contains the area to fill.");
            }
            destination = toSet;
        }
        
        public void setGridTransform(final MathTransform transform) {
            gridTransform = transform;
        }
        
        @Override
        public Boolean call() throws Exception {
            final Resample resampler = new Resample(gridTransform, destination, area, interpolator, fillValue);
            resampler.fillImage();
            return true;
        }
        
    }
}
