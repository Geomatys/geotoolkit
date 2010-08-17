/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.util.Locale;

import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageWriter;


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
     */
    private GridCoverageWriter writer;

    /**
     * The index of the image to read (usually 0).
     */
    protected final int imageIndex;

    /**
     * If specified, write the request result in an image of the given format and read it back.
     */
    protected String outputFormat;

    /**
     * A buffer where to write to the image, or {@code null} if none.
     */
    private MemoryOutputStream out;

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
    public CoverageReadWriteStressor(final GridCoverageReader reader, final int imageIndex)
            throws CoverageStoreException
    {
        super(reader.getGridGeometry(imageIndex));
        this.reader     = reader;
        this.imageIndex = imageIndex;
    }

    /**
     * Creates a reader for the given input. If the given object input is a file ending
     * with {@code ".serialized"}, it will be deserialized.
     *
     * @param  input The input to give to the image reader.
     * @return The image reader using the given input.
     */
    private static GridCoverageReader createReader(Object input) throws CoverageStoreException {
        if (input instanceof GridCoverageReader) {
            return (GridCoverageReader) input;
        }
        final GridCoverageReader reader = new ImageCoverageReader();
        input = createReaderInput(input);
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
            final File file = (File) input;
            if (file.getName().endsWith(".serialized")) try {
                final ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                input = in.readObject();
                in.close();
            } catch (IOException e) { // TODO: use multi-catch with JDK7.
                throw new CoverageStoreException(e);
            } catch (ClassNotFoundException e) {
                throw new CoverageStoreException(e);
            }
        }
        return input;
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
     * Reads the given random request.
     */
    @Override
    protected RenderedImage executeQuery(final GeneralGridGeometry request) throws CoverageStoreException {
        /*
         * Tests read operation.
         */
        final GridCoverageReadParam readParam = new GridCoverageReadParam();
        readParam.setEnvelope(request.getEnvelope());
        readParam.setResolution(getResolution(request));
        final GridCoverage2D coverage = (GridCoverage2D) reader.read(imageIndex, readParam);
        if (outputFormat == null) {
            return coverage.view(ViewType.RENDERED).getRenderedImage();
        }
        /*
         * Tests write operation.
         */
        final GridCoverageWriteParam writeParam = new GridCoverageWriteParam(readParam);
        writeParam.setFormatName(outputFormat);
        if (out == null) {
            out = new MemoryOutputStream();
        }
        out.reset();
        if (writer == null) {
            writer = new ImageCoverageWriter();
        }
        writer.setOutput(out);
        writer.write(coverage, writeParam);
        writer.setOutput(null);
        final InputStream in = out.getInputStream();
        final RenderedImage image;
        try {
            image = ImageIO.read(in);
            in.close();
        } catch (IOException e) {
            throw new CoverageStoreException(e);
        }
        return image;
    }

    /**
     * Disposes the reader after the test is done.
     */
    @Override
    protected void dispose() throws CoverageStoreException {
        reader.dispose();
        if (writer != null) {
            writer.dispose();
        }
        super.dispose();
    }

    public static void main(final String[] args) throws Exception {
        Main.main(new String[] {
            "coverages",
            "/Users/desruisseaux/Documents/Données/Mosaïques/BlueMarble/output/TileManager.serialized",
            "--duration=20", "--minSize=400", "--maxSize=800", "--numThreads=4", "--outputFormat=png", "--view"
        });
    }
}
