/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.io.plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.CharArrayWriter;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import javax.media.jai.RasterFactory;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.test.TestData;
import org.geotoolkit.test.image.ImageTestBase;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.factory.Hints;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.PredefinedCRS;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;

import org.apache.sis.referencing.CommonCRS;
import ucar.nc2.NCdumpW;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests writing a NetCDF file through the {@link ImageCoverageWriter} API.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public class NetcdfCoverageWriterTest extends ImageTestBase {
    /**
     * Necessary for some tests for now because GeographicBoundingBox.setBounds(Envelope)
     * does not have the possibility to specify whether it wants a lenient or non-lenient
     * factory.
     */
    static {
        Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
    }

    /**
     * Tolerance threshold for floating point number comparisons.
     */
    private static final double EPS = 1E-7;

    /**
     * The images size, in pixels.
     */
    private static final int WIDTH=4, HEIGHT=2;

    /**
     * Temporary variable for skipping read operations for some unsupported projections.
     */
    private boolean skipRead;

    /**
     * Creates a new test case.
     */
    public NetcdfCoverageWriterTest() {
        super(NetcdfImageWriter.class);
    }

    private static Map<String,String> name(final String name) {
        return Collections.singletonMap(IdentifiedObject.NAME_KEY, name);
    }

    /**
     * Tests the creation of a NetCDF file using {@code "CRS:84"} on the whole world.
     *
     * @throws Exception If an I/O, CRS factory or coverage store error occurred.
     */
    @Test
    @Ignore("CDL has changed while upgrading NetCDF dependency to 4.3.21")
    public void testCRS84() throws Exception {
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1,  -90,  90);
        testWriteRead(env, "CRS84.cdl");
    }

    /**
     * Tests the creation of a NetCDF file using {@code "EPSG:4326"} on the whole world.
     * This test writes the latitude axis before the longitude one.
     *
     * @throws Exception If an I/O, CRS factory or coverage store error occurred.
     */
    @Test
    @Ignore("CDL has changed while upgrading NetCDF dependency to 4.3.21")
    public void testEPSG4326() throws Exception {
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);
        testWriteRead(env, "EPSG4326.cdl");
    }

    /**
     * Tests the creation of a NetCDF file using {@code "EPSG:4088"}.
     * This is a Equidistant projection, which is more suitable for testing than
     * more complex projections because of the potential for rounding errors.
     *
     * @throws Exception If an I/O, CRS factory or coverage store error occurred.
     */
    @Test
    @Ignore("CDL has changed while upgrading NetCDF dependency to 4.3.21")
    public void testEPSG4088() throws Exception {
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4088"));
        env.setRange(0, -2E7, 2E7);
        env.setRange(1, -1E7, 1E7);
        skipRead = true;
        testWriteRead(env, "EPSG4088.cdl");
    }

    /**
     * Tests the creation of a NetCDF file with the (indirectly) given CRS definition.
     * Then reads the NetCDF file back and ensures that Geotk properly found the CRS back,
     * or at least something close.
     *
     * @param envelope The geographic envelope. The envelope CRS is the CRS to be encoded in the NetCDF.
     * @param cdlFile  The name (without path) of the file containing the expected NetCDF content
     *                 expressed in Common Data Language (CDL).
     */
    private void testWriteRead(final Envelope envelope, final String cdlFile) throws IOException, CoverageStoreException {
        final GridCoverage2D coverage = writeAndRead(cdlFile, createGridCoverage(envelope, "data", 0));
        verifyGridGeometry(coverage, envelope);
        assertSampleValuesEqual(cdlFile, image, coverage.getRenderedImage(), EPS);
    }

    /**
     * Tests writing a sequence of 3 variables.
     *
     * @throws Exception If an I/O, CRS factory or coverage store error occurred.
     */
    @Test
    @Ignore("CDL has changed while upgrading NetCDF dependency to 4.3.21")
    public void testSequence() throws Exception {
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1,  -90,  90);
        final GridCoverage2D coverage = writeAndRead("sequence.cdl",
                createGridCoverage(env, "cat1", 100),
                createGridCoverage(env, "cat2", 200),
                createGridCoverage(env, "cat3", 300));
        verifyGridGeometry(coverage, env);
        assertSampleValuesEqual("Sequence", image, coverage.getRenderedImage(), EPS);
    }

    /**
     * Tests the creation of a three-dimensional NetCDF file. This is similar to
     * {@link #testSequence()}, except that the coverages have the same name.
     *
     * @throws Exception If an I/O, CRS factory or coverage store error occurred.
     */
    @Test
    @Ignore("Not yet implemented")
    public void testXYZ() throws Exception {
        final GeneralEnvelope env = new GeneralEnvelope(PredefinedCRS.WGS84_3D);
        env.setRange(0, -180, 180);
        env.setRange(1,  -90,  90);
        env.setRange(2,   10,  12); final GridCoverage2D coverage1 = createGridCoverage(env, "data", 100);
        env.setRange(2,   20,  22); final GridCoverage2D coverage2 = createGridCoverage(env, "data", 200);
        env.setRange(2,   30,  32); final GridCoverage2D coverage3 = createGridCoverage(env, "data", 300);
        final GridCoverage2D coverage = writeAndRead("xyz.cdl", coverage1, coverage2, coverage3);
        verifyGridGeometry(coverage, env);
    }

    /**
     * Tests the creation of a four-dimensional NetCDF file.
     *
     * @throws Exception If an I/O, CRS factory or coverage store error occurred.
     */
    @Test
    @Ignore("Not yet implemented")
    public void testXYZT() throws Exception {
        final CoordinateReferenceSystem crs = new DefaultCompoundCRS(name("WGS84 + z + t"),
                PredefinedCRS.WGS84_3D,
                CommonCRS.Temporal.JAVA.crs());

        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -180, 180);
        env.setRange(1,  -90,  90);
        env.setRange(2,   10,  10);
        env.setRange(3,    3,   3);
        final GridCoverage2D coverage1 = createGridCoverage(env, "data", 100);

        env.setRange(2, 20, 20);
        env.setRange(3,  6,  6);
        final GridCoverage2D coverage2 = createGridCoverage(env, "data", 200);

        env.setRange(2, 30, 30);
        env.setRange(3,  9,  9);
        final GridCoverage2D coverage3 = createGridCoverage(env, "data", 300);

        final GridCoverage2D coverage = writeAndRead("xyzt.cdl", coverage1, coverage2, coverage3);
        verifyGridGeometry(coverage, env);
    }

    /**
     * Writes the given coverage as a NetCDF file, ensures that the CDL is equals to the given one,
     * then reads the coverage back.
     *
     * @param  cdlFile   The name (without path) of the file containing the expected NetCDF content
     *                   expressed in Common Data Language (CDL).
     * @param  coverages The coverages to write.
     * @return The coverage which has been read back.
     */
    private GridCoverage2D writeAndRead(final String cdlFile, final GridCoverage2D... coverages)
            throws IOException, CoverageStoreException
    {
        final GridCoverage2D coverage;
        final File tempFile = File.createTempFile("test", ".nc");
        try {
            CoverageIO.write(Arrays.asList(coverages), "NetCDF", tempFile);
            assertEqualsCDL(cdlFile, tempFile);
            if (skipRead) return coverages[0]; // Temporary workaround for unsupported projections.
            final GridCoverageReader reader = CoverageIO.createSimpleReader(tempFile);
            coverage = (GridCoverage2D) reader.read(0, null);
            reader.dispose();
        } finally {
            tempFile.delete();
        }
        return coverage;
    }

    /**
     * Creates a new grid coverage using the given envelope and a single band of the given name.
     * The coverages created by this method are used by the {@link #testWriteRead(Envelope, String)}
     * method in order to write a NetCDF file. The coverage needs to be small, because the sample
     * values will be formatted as Common Data Language (CDL) string.
     * <p>
     * The image created by this method is stored in the {@link #image} field.
     *
     * @param  envelope     The geographic envelope.
     * @param  variableName The name of the single band.
     * @param  firstValue   The value in the upper-left corner.
     * @return The grid coverage.
     */
    private GridCoverage2D createGridCoverage(final Envelope envelope, final String variableName, final double firstValue) {
        /*
         * Define the sample values from 0 inclusive to WIDTH*HEIGHT exclusive, in the order they
         * will be written in the NetCDF file. This has the consequence of putting the 0 value in
         * the lower-left corner, since NetCDF file typically use the geometric axis directions.
         */
        final WritableRaster raster = RasterFactory.createBandedRaster(DataBuffer.TYPE_FLOAT, WIDTH, HEIGHT, 1, null);
        for (int inc=0,y=0; y<HEIGHT; y++) {
            for (int x=0; x<WIDTH; x++) {
                raster.setSample(x, y, 0, firstValue + inc++);
            }
        }
        /*
         * Define the 'gridToCRS' transform.
         */
        final int dim = envelope.getDimension();
        final GeneralMatrix matrix = new GeneralMatrix(dim+1, dim+1);
        matrix.setElement(0, 0, envelope.getSpan(0) / WIDTH);
        matrix.setElement(1, 1, envelope.getSpan(1) / HEIGHT);
        for (int i=0; i<dim; i++) {
            matrix.setElement(i, dim, envelope.getMinimum(i));
        }
        /*
         * Build the coverage.
         */
        final GridCoverageBuilder builder = new GridCoverageBuilder();
        builder.variable(0).setName(variableName);
        builder.variable(0).setColors("grayscale");
        builder.setGridToCRS(matrix);
        builder.setPixelAnchor(PixelInCell.CELL_CORNER);
        builder.setCoordinateReferenceSystem(envelope.getCoordinateReferenceSystem());
        builder.setRenderedImage(raster);
        final GridCoverage2D coverage = builder.getGridCoverage2D();
        if (image == null) { // Remember only the first image.
            image = coverage.getRenderedImage();
        }
        return coverage;
    }

    /**
     * Verifies the CRS and envelope of the given coverage against the given expected values.
     *
     * @param coverage The coverage to verify.
     * @param envelope The expected envelope, which must contain the expected CRS.
     */
    private void verifyGridGeometry(final GridCoverage2D coverage, final Envelope envelope) {
        final GridGeometry2D gridGeom = coverage.getGridGeometry();
        final CoordinateReferenceSystem actualCRS = gridGeom.getCoordinateReferenceSystem();
        assertNotNull(actualCRS);
        /*
         * We can not perform the usual assertTrue(Utilitie.deepEquals(..., ComparisonMode.DEBUG)
         * call, because the CRS read from the NetCDF file is not the usual DefaultGeographicCRS
         * implementations. Instead we get the NetCDF wrappers, which are not directly comparable.
         * We have to split the components.
         */
        final CoordinateReferenceSystem expectedCRS = envelope.getCoordinateReferenceSystem();
        final CoordinateSystem expectedCS  =  expectedCRS.getCoordinateSystem();
        final CoordinateSystem candidateCS = actualCRS.getCoordinateSystem();
        assertEquals(expectedCS.getDimension(), candidateCS.getDimension());
        /*
         * Compares each axis.
         */
        for (int i=0,n=expectedCS.getDimension(); i<n; i++) {
            final CoordinateSystemAxis candidateAxis = candidateCS.getAxis(i);
            final CoordinateSystemAxis expectedAxis  =  expectedCS.getAxis(i);
            assertEquals(expectedAxis.getDirection(), candidateAxis.getDirection());
            assertEquals(expectedAxis.getUnit(), candidateAxis.getUnit());
        }
        /*
         * Compares the envelopes.
         */
        final Envelope candidateEnvope = coverage.getEnvelope();
        assertEquals(envelope.getDimension(), candidateEnvope.getDimension());
        final int dimension = envelope.getDimension();
        for (int i=0; i<dimension; i++) {
            assertEquals(envelope.getMinimum(i), candidateEnvope.getMinimum(i), EPS);
            assertEquals(envelope.getMaximum(i), candidateEnvope.getMaximum(i), EPS);
        }
    }

    /**
     * Asserts that the header of the given NetCDF file has the structure defined by the given
     * Common Data Language (CDL) string.
     *
     * @param  cdlFile     The filename (without path) of the expected content of the NetCDF file.
     * @param  netcdfFile  The NetCDF file to inspect.
     * @throws IOException If an I/O error occurred while reading the NetCDF file.
     */
    private static void assertEqualsCDL(final String cdlFile, final File netcdfFile) throws IOException {
        final CharArrayWriter buffer = new CharArrayWriter();
        final PrintWriter writer = new PrintWriter(buffer);
        NCdumpW.print(netcdfFile.getPath(), writer,
                true,   // If true, show all variables
                false,  // If true, show only coordinate variables
                false,  // If true, print NcML and ignore other arguments
                true,   // If true, print strict CDL representation
                null,   // Semi-colon delimited list of variables to print
                null);  // If non-null, allow task to be cancelled
        final String expected = TestData.readText(NetcdfCoverageWriterTest.class, cdlFile);
        String actual = buffer.toString();
        actual = actual.substring(actual.indexOf('{')); // Trims the filename before '{'.
        actual = actual.replace("\n\n", "\n");
        assertMultilinesEquals(expected, actual);
    }
}
