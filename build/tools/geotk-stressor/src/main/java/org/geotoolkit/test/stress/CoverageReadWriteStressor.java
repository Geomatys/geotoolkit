/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.test.stress;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageInputStream;
import java.util.logging.Level;
import java.util.Locale;

import org.opengis.geometry.Envelope;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.apache.sis.geometry.Envelopes;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.TileManagerFactory;


/**
 * A stressor for the {@code geotk-coverage-io} module.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 */
public class CoverageReadWriteStressor extends Stressor {
    /**
     * The coverage reader to stress.
     */
    protected final GridCoverageReader reader;

    /**
     * A grid coverage writer for testing write operations, or {@code null} if this
     * {@code CoverageReadWriteStressor} instance is testing only read operations.
     * <p>
     * Will be created when first needed.
     */
    private transient GridCoverageWriter writer;

    /**
     * The index of the image to read (usually 0).
     */
    protected final int imageIndex;

    /**
     * If specified, reproject the request result in the given CRS.
     */
    protected CoordinateReferenceSystem outputCRS;

    /**
     * If specified, write the request result in an image of the given format and read it back. It
     * must be a format name recognized by Image I/O. The {@code "(native)"} or {@code "(standard)"}
     * part, if any, shall be been processed outside this class.
     *
     * @see #processFormatName(String)
     */
    protected String outputFormat;

    /**
     * A buffer where to write to the image, or {@code null} if none.
     * Will be created when first needed.
     */
    private transient MemoryOutputStream out;

    /**
     * An image reader, used only for checking the image created by {@link #writer}.
     * Will be created when first needed.
     */
    private transient ImageReader imageReader;

    /**
     * Creates a new stressor for the given input. This constructor creates automatically
     * an {@link ImageCoverageReader} for the given input.
     *
     * @param  input The input to use.
     * @throws CoverageStoreException If an error occurred while reading the input.
     */
    public CoverageReadWriteStressor(final Object input) throws CoverageStoreException {
        this(createReader(input), 0);
    }

    /**
     * Creates a new stressor for the given reader. Callers shall
     * {@linkplain GridCoverageReader#setInput set the reader input}
     * before to invoke this method.
     *
     * @param  reader The coverage reader to stress.
     * @param  imageIndex The index of the image to read (usually 0).
     * @throws CoverageStoreException If an error occurred while reading the input.
     */
    private CoverageReadWriteStressor(final GridCoverageReader reader, final int imageIndex)
            throws CoverageStoreException
    {
        super(clip(reader.getGridGeometry(imageIndex)));
        this.reader     = reader;
        this.imageIndex = imageIndex;
    }

    /**
     * Sets the logging level to the given value.
     */
    @Override
    public void setLogLevel(final Level level) {
        if (reader != null) reader.setLogLevel(level);
        if (writer != null) writer.setLogLevel(level);
        super.setLogLevel(level);
    }

    /**
     * Clips the given geometry. This can be used when the input raster include the poles
     * and the output raster user some CRS like Mercator.
     * <p>
     * This code is disabled for now - it have to be enabled manually if desired.
     * A future version may detect automatically whatever a clip is desired or not
     * depending on the source coordinate system axes.
     */
    private static GeneralGridGeometry clip(GeneralGridGeometry geometry) {
        if (false) {
            GridEnvelope range = geometry.getExtent();
            final int[] lower = range.getLow().getCoordinateValues();
            final int[] upper = range.getHigh().getCoordinateValues();
            for (int i=range.getDimension(); --i>=0;) {
                final int hs = (upper[i] - lower[i] + 1) / 200;
                lower[i] += hs;
                upper[i] -= hs;
            }
            range = new GeneralGridEnvelope(lower, upper, true);
            geometry = new GeneralGridGeometry(range, PixelInCell.CELL_CORNER,
                    geometry.getGridToCRS(PixelInCell.CELL_CORNER),
                    geometry.getCoordinateReferenceSystem());
        }
        return geometry;
    }

    /**
     * Creates a reader for the given input. If the given object input is a file ending
     * with {@code ".serialized"}, it will be deserialized.
     *
     * @param  input The input to give to the image reader.
     * @return The image reader using the given input.
     */
    private static GridCoverageReader createReader(final Object input)
            throws CoverageStoreException
    {
        if (input instanceof GridCoverageReader) {
            return (GridCoverageReader) input;
        }
        final GridCoverageReader reader = new ImageCoverageReader();
        reader.setInput(input);
        return reader;
    }

    /**
     * Creates the input of a coverage reader.
     *
     * @param  input The input to give to the image reader.
     * @return A potentially modified input to give to the image reader.
     */
    static Object createReaderInput(Object input) throws CoverageStoreException {
        if (input instanceof File) {
            File file = (File) input;
            LOGGER.log(Level.INFO, "Loading {0}", file);
            try {
                input = TileManagerFactory.DEFAULT.create(file);
                if (!file.isFile() || !file.getName().endsWith(".serialized")) {
                    file = new File(file, TileManager.SERIALIZED_FILENAME);
                    LOGGER.log(Level.INFO, "Saving {0}", file);
                    try (ObjectOutputStream bs = new ObjectOutputStream(new FileOutputStream(file))) {
                        bs.writeObject(input);
                    }
                }
            } catch (IOException e) {
                throw new CoverageStoreException(e);
            }
        }
        return input;
    }

    /**
     * If the given format name contains a variant (a {@code "(standard)"} or {@code "(native)"}
     * suffix), configures the {@code IIORegistry} accordingly and returns the format name without
     * the variant suffix.
     *
     * @param  formatName The format name, with an optional variant suffix.
     * @return The format name without variant suffix.
     * @throws IllegalArgumentException If the variant suffix is not recognized.
     *
     * @see #outputFormat
     */
    protected static String processFormatName(String formatName) throws IllegalArgumentException {
        final int s = formatName.indexOf('(');
        if (s >= 0) {
            final String variant = formatName.substring(s).toLowerCase();
            formatName = formatName.substring(0, s);
            final boolean useNative;
            switch (variant) {
                case "(native)":   useNative = true;  break;
                case "(standard)": useNative = false; break;
                default: throw new IllegalArgumentException("Unrecognized format variant: " + variant);
            }
            Registry.setNativeCodecAllowed(formatName, ImageReaderSpi.class, useNative);
            Registry.setNativeCodecAllowed(formatName, ImageWriterSpi.class, useNative);
        }
        return formatName;
    }

    /**
     * Sets the locale of the reader and writer.
     *
     * @param locale The locale.
     */
    public void setLocale(final Locale locale) {
        reader.setLocale(locale);
        if (writer != null) {
            writer.setLocale(locale);
        }
    }

    /**
     * Reads the given random request. If {@link #outputFormat} is non-null, the image will be
     * written in a memory buffer, then read again (unless the output is not shown in a viewer).
     *
     * @throws CoverageStoreException If an error occurred while using the {@link org.geotoolkit.coverage.io} API.
     * @throws IOException If an error occurred while using the {@link javax.imageio} API.
     * @throws TransformException If an error occurred while projecting the source envelope.
     */
    @Override
    protected RenderedImage executeQuery(final GeneralGridGeometry request)
            throws CoverageStoreException, IOException, TransformException
    {
        /*
         * Tests read operation.
         */
        final GridCoverageReadParam readParam = new GridCoverageReadParam();
        readParam.setEnvelope(request.getEnvelope());
        readParam.setResolution(getResolution(request));
        GridCoverage2D coverage = (GridCoverage2D) reader.read(imageIndex, readParam);
        if (outputFormat != null) {
            /*
             * Tests write operation.
             */
            final GridCoverageWriteParam writeParam = new GridCoverageWriteParam(readParam);
            if (outputCRS != null) {
                final Envelope sourceEnvelope = writeParam.getEnvelope();
                final Envelope targetEnvelope = Envelopes.transform(sourceEnvelope, outputCRS);
                final double[] resolution     = writeParam.getResolution();
                if (resolution != null) {
                    for (int i=0; i<resolution.length; i++) {
                        // The naive algoritm below assumes that axis order didn't changed.
                        resolution[i] *= targetEnvelope.getSpan(i) / sourceEnvelope.getSpan(i);
                    }
                }
                writeParam.setEnvelope(targetEnvelope);
                writeParam.setResolution(resolution);
            }
            writeParam.setFormatName(outputFormat);
            if (out == null) {
                out = new MemoryOutputStream();
            }
            out.reset();
            if (writer == null) {
                writer = new ImageCoverageWriter();
                writer.setLogLevel(getLogLevel());
            }
            writer.setOutput(out);
            writer.write(coverage, writeParam);
            writer.setOutput(null);
            /*
             * Reads the image that we just wrote, for checking purpose.
             * We will skip this step if there is no visual check.
             */
            if (viewer != null) {
                final RenderedImage image;
                try (ImageInputStream in = ImageIO.createImageInputStream(out.getInputStream())) {
                    if (imageReader == null) {
                        imageReader = XImageIO.getReaderByFormatName(outputFormat, null, Boolean.TRUE, Boolean.TRUE);
                    }
                    imageReader.setInput(in);
                    image = imageReader.read(0);
                    imageReader.reset();
                }
                return image;
            }
        } else if (outputCRS != null) {
            /*
             * No write operation, but a CRS is specified. Test reprojection.
             */
            coverage = (GridCoverage2D) Operations.DEFAULT.resample(coverage, outputCRS);
        }
        return coverage.view(ViewType.RENDERED).getRenderedImage();
    }

    /**
     * Disposes the reader, the writer and the buffer after the test is done.
     */
    @Override
    protected void dispose() throws Exception {
        reader.dispose();
        if (writer != null) {
            writer.dispose();
            writer = null;
        }
        if (imageReader != null) {
            imageReader.dispose();
            imageReader = null;
        }
        out = null;
        super.dispose();
    }
}
