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
package org.geotoolkit.coverage.io;

import java.io.File;
import java.io.LineNumberReader;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.text.ParseException;
import java.io.IOException;
import java.io.StringWriter;
import javax.imageio.ImageWriter;

import org.geotoolkit.test.Depend;
import org.geotoolkit.test.TestData;
import org.geotoolkit.io.LineFormat;
import org.geotoolkit.io.LineReader;
import org.geotoolkit.io.LineReaders;
import org.geotoolkit.image.SampleModels;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.image.io.plugin.TextMatrixImageReaderTest;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link ImageCoverageWriter}. This test will read the {@code "matrix.txt"} file
 * defined in the {@code org/geotoolkit/image/io/plugin/test-data} directory because it
 * is the easiest one to debug.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 */
@Depend(ImageCoverageReaderTest.class)
public class ImageCoverageWriterTest {
    /**
     * {@code true} for printing debugging information.
     */
    private static final boolean VERBOSE = false;

    /**
     * Tolerance factor when comparing values from the "matrix.txt" file.
     * We set the tolerance to the first decimal digit after the significant digits.
     */
    private static final double EPS = 1E-4;

    /**
     * Registers a "matrix" reader forced to the US format.
     */
    @BeforeClass
    public static void registerReaderUS() {
        ImageCoverageReaderTest.registerReaderUS();
    }

    /**
     * Unregisters the reader defined by {@link #registerReaderUS()}.
     */
    @AfterClass
    public static void deregisterReaderUS() {
        ImageCoverageReaderTest.deregisterReaderUS();
    }

    /**
     * Creates a {@link GridCoverage2D} from the given file.
     */
    private static GridCoverage2D read(final String file) throws IOException, CoverageStoreException {
        final ImageCoverageReader reader = new ImageCoverageReader();
        reader.setInput(TestData.file(TextMatrixImageReaderTest.class, file));
        final GridCoverage2D coverage = reader.read(0, null);
        reader.dispose();
        return coverage;
    }

    /**
     * Asserts that the numbers in the given string are equal. Numbers are parsed using the
     * given locale, which may not be the same for the expected and the actual strings.
     */
    private static void assertMatrixEquals(
            final LineReader expected, final Locale expectedLocale,
            final LineReader actual,   final Locale actualLocale,
            final double missingValue) throws IOException, ParseException
    {
        final LineFormat f1 = new LineFormat(expectedLocale);
        final LineFormat f2 = new LineFormat(actualLocale);
        double[] expectedArray = null;
        double[] actualArray = null;
        String line;
        while ((line = expected.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0 || line.startsWith("#")) {
                continue;
            }
            final String actualLine = actual.readLine();
            assertNotNull("Missing line.", actualLine);
            f1.setLine(line);
            f2.setLine(actualLine);
            expectedArray = f1.getValues(expectedArray);
            actualArray   = f2.getValues(actualArray);
            assertEquals("Line length differ.", expectedArray.length, actualArray.length);
            for (int i=0; i<expectedArray.length; i++) {
                double expectedValue = expectedArray[i];
                if (expectedValue == missingValue) {
                    expectedValue = Double.NaN;
                }
                double actualValue = actualArray[i];
                if (actualValue == missingValue) {
                    actualValue = Double.NaN;
                }
                assertEquals("Sample value differ.", expectedValue, actualValue, EPS);
            }
        }
        while ((line = actual.readLine()) != null) {
            assertEquals("Extra line.", "", line.trim());
        }
    }

    /**
     * Convenience method invoking the above {@code assertImageEquals} for an expected
     * string in a fixed locale, and an actual string given explicitly in current locale.
     */
    private static void assertMatrixEquals(final String expected, final String actual,
            final double missingValue) throws IOException, ParseException
    {
        assertMatrixEquals(LineReaders.wrap(expected), Locale.CANADA,
                           LineReaders.wrap(actual),   Locale.getDefault(),
                           missingValue);
    }

    /**
     * Convenience method invoking the above {@code assertImageEquals} for an expected string read
     * from the given file in fixed locale, and an actual string given explicitly in current locale.
     */
    private static void assertMatrixEqualsFile(final String expectedFile, final String actual,
            final double missingValue) throws IOException, ParseException
    {
        final LineNumberReader in = TestData.openReader(TextMatrixImageReaderTest.class, expectedFile);
        assertMatrixEquals(LineReaders.wrap(in),     Locale.CANADA,
                           LineReaders.wrap(actual), Locale.getDefault(),
                           missingValue);
        in.close();
    }

    /**
     * Writes the full image.
     *
     * @throws IOException If the text file can not be open (should not happen).
     * @throws CoverageStoreException Should not happen.
     * @throws ParseException Should not happen.
     */
    @Test
    public void writeFull() throws IOException, CoverageStoreException, ParseException {
        final GridCoverage2D coverage = read("matrix.txt");
        final ImageCoverageWriterInspector writer = new ImageCoverageWriterInspector("writeFull", "matrix");
        final StringWriter buffer = new StringWriter();
        writer.setOutput(buffer);
        writer.write(coverage, null);
        if (VERBOSE) {
            System.out.println(writer);
        }
        assertTrue("No transformation expected.", writer.getReadMatchesRequest());
        writer.dispose();
        assertMatrixEqualsFile("matrix.txt", buffer.toString(), -9999);
    }

    /**
     * Writes a region of the image.
     *
     * @throws IOException If the text file can not be open (should not happen).
     * @throws CoverageStoreException Should not happen.
     * @throws ParseException Should not happen.
     */
    @Test
    public void writeRegion() throws IOException, CoverageStoreException, ParseException {
        final GridCoverage2D coverage = read("matrix.txt");
        final ImageCoverageWriterInspector writer = new ImageCoverageWriterInspector("writeRegion");
        final GridCoverageWriteParam param = new GridCoverageWriteParam();
        param.setFormatName("matrix");
        param.setEnvelope(new Envelope2D(null, -1000, -2000, 8000 - -1000, 12000 - -2000));
        final StringWriter buffer = new StringWriter();
        writer.setOutput(buffer);
        writer.write(coverage, param);
        if (VERBOSE) {
            System.out.println(writer);
        }
        assertTrue("No transformation expected.", writer.getReadMatchesRequest());
        writer.dispose();
        assertMatrixEquals(
            "12.783  12.499   -9999   -9999   -9999   -9999   -9999   -9999   -9999\n" +
            "14.020   -9999   -9999   -9999   -9999   -9999   -9999   -9999   -9999\n" +
            "15.162   -9999   -9999   -9999   -9999   -9999   -9999   -9999   -9999\n" +
            "15.994   -9999   -9999  19.006   -9999   -9999   -9999   -9999   -9999\n" +
            "17.356  20.674   -9999   -9999   -9999   -9999   -9999   -9999   -9999\n" +
            "19.070   -9999  21.312   -9999   -9999   -9999   -9999   -9999  16.912\n" +
            "19.766   -9999   -9999   -9999   -9999   -9999   -9999   -9999  22.195\n" +
            " -9999   -9999   -9999   -9999  31.140   -9999   -9999   -9999  27.057\n" +
            " -9999   -9999   -9999  28.996   -9999   -9999  29.741  27.922  29.127\n" +
            " -9999   -9999   -9999   -9999  28.365   -9999  29.445  28.823  29.871\n" +
            " -9999   -9999   -9999   -9999  27.067  28.951  29.540  29.253  29.824\n" +
            "28.094  28.104   -9999   -9999  28.954  29.224  29.287  29.609  29.188\n" +
            "25.454  25.820   -9999   -9999  28.810  29.347  29.633  29.522  28.902\n" +
            "26.769  25.656   -9999  26.820  27.631  28.311  29.058  29.028  27.949\n",
            buffer.toString(), -9999);
    }

    /**
     * Writes the same region than above, with subsampling.
     *
     * @throws IOException If the text file can not be open (should not happen).
     * @throws CoverageStoreException Should not happen.
     * @throws ParseException Should not happen.
     */
    @Test
    public void writeSubsampledRegion() throws IOException, CoverageStoreException, ParseException {
        final GridCoverage2D coverage = read("matrix.txt");
        final ImageCoverageWriterInspector writer = new ImageCoverageWriterInspector("writeSubsampledRegion");
        final GridCoverageWriteParam param = new GridCoverageWriteParam();
        param.setFormatName("matrix");
        param.setEnvelope(new Envelope2D(null, -1000, -2000, 8000 - -1000, 12000 - -2000));
        param.setResolution(2000, 3000);
        final StringWriter buffer = new StringWriter();
        writer.setOutput(buffer);
        writer.write(coverage, param);
        if (VERBOSE) {
            System.out.println(writer);
        }
        assertTrue("No transformation expected.", writer.getReadMatchesRequest());
        writer.dispose();
        assertMatrixEquals(
            "12.783   -9999   -9999   -9999   -9999\n" +
            "15.994   -9999   -9999   -9999   -9999\n" +
            "19.766   -9999   -9999   -9999  22.195\n" +
            " -9999   -9999  28.365  29.445  29.871\n" +
            "25.454   -9999  28.810  29.633  28.902\n",
            buffer.toString(), -9999);
    }

    /**
     * Writes an image twice, asking for different parts. The purpose of this test is to ensure
     * that {@link ImageWriter} are properly used (with the right output set) when recycled. For
     * this test, we need an image writer which doesn't accept {@link File} object directly.
     *
     * @throws IOException If the text file can not be open (should not happen).
     * @throws CoverageStoreException Should not happen.
     */
    @Test
    public void writeTwice() throws IOException, CoverageStoreException {
        final ImageCoverageReader reader = new ImageCoverageReader();
        reader.setInput(TestData.file(SampleModels.class, "Contour.png"));
        final GridCoverage2D coverage = reader.read(0, null);
        reader.dispose();

        final ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
        assertEquals("Expected an initially empty stream.", 0, out.size());
        final GridCoverageWriteParam param = new GridCoverageWriteParam();
        param.setFormatName("PNG");

        final ImageCoverageWriterInspector writer = new ImageCoverageWriterInspector("writeTwice");
        writer.setOutput(out);
        writer.write(coverage, param);
        if (VERBOSE) {
            System.out.println(writer);
        }
        assertTrue("No transformation expected.", writer.getReadMatchesRequest());
        final long length = out.size();
        assertTrue("Empty file.", length > 0);

        out.reset();
        assertEquals("Expected an initially empty stream.", 0, out.size());
        param.setEnvelope(new Envelope2D(null, 100, 50, 800, 1200));
        param.setResolution(20, 30);
        writer.setOutput(out);
        writer.write(coverage, param);
        if (VERBOSE) {
            System.out.println(writer);
        }
        assertFalse("Translation expected.", writer.getReadMatchesRequest());
        assertTrue("Expected a smaller file", out.size() < length);
        writer.dispose();
    }
}
