/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.plugin;

import java.io.File;
import java.io.IOException;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.DataBuffer;
import javax.measure.unit.Unit;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.image.io.DimensionSlice;
import org.geotoolkit.image.io.SpatialImageReadParam;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.*;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.NonSI.DAY;
import static javax.measure.unit.NonSI.DEGREE_ANGLE;


/**
 * Tests using the {@code "World/Coriolis/OA_RTQCGL01_20070606_FLD_TEMP.nc"} file.
 * This test class queries many different aspects of the same file. The data are
 * loaded using:
 * <p>
 * <ul>
 *   <li>{@link NetcdfImageReader}</li>
 *   <li>{@link ImageCoverageReader} wrapping an {@code NetcdfImageReader}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.08
 */
public final strictfp class CoriolisFormatTest extends NetcdfImageReaderTestBase {
    /**
     * The directory which contains the data used by the tests.
     *
     * @see #getTestFile()
     * @see CoriolisFormatTest#getTestFile()
     */
    static final String DIRECTORY = "World/Coriolis/";

    /**
     * The file to be used for the tests.
     *
     * @see #getTestFile()
     */
    private static final String FILENAME = "OA_RTQCGL01_20070606_FLD_TEMP.nc";

    /**
     * The name of variables in the {@value #FILENAME} file.
     *
     */
    private static final String[] VARIABLE_NAMES = new String[] {"temperature", "pct_variance"};

    /**
     * The size of the grid in each dimension. This field is public for usage by
     * {@link org.geotoolkit.referencing.adapters.NetcdfCRSTest}. Do not modify.
     */
    public static final int[] GRID_SIZE = new int[] {720, 499, 59, 1};

    /**
     * Name of axes in the {@value #FILENAME} file.
     *
     * @see #assertExpectedAxes(CoordinateSystem, boolean)
     */
    private static final String[] AXIS_NAMES = new String[] {"longitude", "latitude", "depth", "time"},
                        PROJECTED_AXIS_NAMES = new String[] {"Easting",   "Northing", "depth", "time"};

    /**
     * Abbreviations of axes in the {@value #FILENAME} file.
     *
     * @see #assertExpectedAxes(CoordinateSystem, boolean)
     */
    private static final String[] AXIS_ABBREVIATIONS = new String[] {"λ", "φ", "d", "t"},
                        PROJECTED_AXIS_ABBREVIATIONS = new String[] {"E", "N", "d", "t"};

    /**
     * Directions of axes in the {@value #FILENAME} file.
     *
     * @see #assertExpectedAxes(CoordinateSystem, boolean)
     */
    private static final AxisDirection[] AXIS_DIRECTIONS = new AxisDirection[] {
        AxisDirection.EAST, AxisDirection.NORTH, AxisDirection.DOWN, AxisDirection.FUTURE
    };

    /**
     * Units of axes in the {@value #FILENAME} file.
     *
     * @see #assertExpectedAxes(CoordinateSystem, boolean)
     */
    private static final Unit<?>[] AXIS_UNITS = new Unit<?>[] {DEGREE_ANGLE, DEGREE_ANGLE,  METRE, DAY},
                         PROJECTED_AXIS_UNITS = new Unit<?>[] {METRE,        METRE,         METRE, DAY};

    /**
     * The first part of expected metadata (without the sample dimensions).
     */
    private static final String EXPECTED_METADATA =
            SpatialMetadataFormat.FORMAT_NAME + '\n' +
            "├───RectifiedGridDomain\n" +
            "│   ├───origin=“-1.9959489E7 1.3843768E7 5.0 20975.0”\n" +
            "│   ├───Limits\n" +
            "│   │   ├───low=“0 0 0 0”\n" +
            "│   │   └───high=“719 498 58 0”\n" +
            "│   ├───OffsetVectors\n" +
            "│   │   ├───OffsetVector\n" +
            "│   │   │   └───values=“55597.46 0.0 0.0 0.0”\n" +
            "│   │   ├───OffsetVector\n" +
            "│   │   │   └───values=“0.0 -55597.46 0.0 0.0”\n" +
            "│   │   ├───OffsetVector\n" +
            "│   │   │   └───values=“0.0 0.0 NaN 0.0”\n" +
            "│   │   └───OffsetVector\n" +
            "│   │       └───values=“0.0 0.0 0.0 NaN”\n" +
            "│   └───CoordinateReferenceSystem\n" +
            "│       ├───name=“NetCDF:time depth latitude longitude”\n" +
            "│       └───CoordinateSystem\n" +
            "│           ├───name=“NetCDF:time depth latitude longitude”\n" +
            "│           ├───dimension=“4”\n" +
            "│           └───Axes\n" +
            "│               ├───CoordinateSystemAxis\n" +
            "│               │   ├───name=“Easting”\n" +
            "│               │   ├───axisAbbrev=“E”\n" +
            "│               │   ├───direction=“east”\n" +
            "│               │   └───unit=“m”\n" +
            "│               ├───CoordinateSystemAxis\n" +
            "│               │   ├───name=“Northing”\n" +
            "│               │   ├───axisAbbrev=“N”\n" +
            "│               │   ├───direction=“north”\n" +
            "│               │   └───unit=“m”\n" +
            "│               ├───CoordinateSystemAxis\n" +
            "│               │   ├───name=“NetCDF:depth”\n" +
            "│               │   ├───axisAbbrev=“d”\n" +
            "│               │   ├───direction=“down”\n" +
            "│               │   ├───minimumValue=“5.0”\n" +
            "│               │   ├───maximumValue=“1950.0”\n" +
            "│               │   └───unit=“m”\n" +
            "│               └───CoordinateSystemAxis\n" +
            "│                   ├───name=“NetCDF:time”\n" +
            "│                   ├───axisAbbrev=“t”\n" +
            "│                   ├───direction=“future”\n" +
            "│                   ├───minimumValue=“20975.0”\n" +
            "│                   ├───maximumValue=“20975.0”\n" +
            "│                   └───unit=“d”\n" +
            "├───SpatialRepresentation\n" +
            "│   ├───numberOfDimensions=“4”\n" +
            "│   └───centerPoint=“27798.73166114092 1.862645149230957E-9 NaN NaN”\n";

    /**
     * Numbers which were simplified in the above metadata. This simplification
     * is performed in order to protect the test suite from slight variations in
     * floating point computations.
     */
    private static final String[] SIMPLIFIED = {"-1.9959489", "1.3843768", "55597.46"};

    /**
     * Returns the {@value #FILENAME} test file, which is optional.
     * If the test file is not present, the test will be interrupted
     * by the JUnit {@link org.junit.Assume} class.
     *
     * @return The test file (never null).
     */
    public static File getTestFile() {
        return getLocallyInstalledFile(DIRECTORY + FILENAME);
    }

    /**
     * Creates a reader and initializes its input to the test file defined in
     * {@link #getTestFile()}. This method is invoked by each tests inherited
     * from the parent class, and by the tests defined in this class.
     */
    @Override
    protected void prepareImageReader(final boolean setInput) throws IOException {
        if (reader == null) {
            NetcdfImageReader.Spi spi = new NetcdfImageReader.Spi();
            reader = new NetcdfImageReader(spi);
        }
        if (setInput) {
            reader.setInput(getTestFile());
        }
    }

    /**
     * Ensures that axes in the given coordinate system have the expected name, abbreviation,
     * direction and unit for a geographic or projected CRS.
     *
     * @param cs The coordinate system to test.
     * @param isProjected {@code true} if the CRS is expected to be projected,
     *                     or {@code false} if it is expected to be geographic.
     */
    public static void assertExpectedAxes(final CoordinateSystem cs, final boolean isProjected) {
        assertNotNull("The coordinate system can't be null.", cs);
        final int dimension = cs.getDimension();
        final String [] axisNames;
        final String [] axisAbbreviations;
        final Unit<?>[] axisUnits;
        if (isProjected) {
            axisNames         = PROJECTED_AXIS_NAMES;
            axisAbbreviations = PROJECTED_AXIS_ABBREVIATIONS;
            axisUnits         = PROJECTED_AXIS_UNITS;
        } else {
            axisNames         = AXIS_NAMES;
            axisAbbreviations = AXIS_ABBREVIATIONS;
            axisUnits         = AXIS_UNITS;
        }
        for (int i=0; i<dimension; i++) {
            final CoordinateSystemAxis axis = cs.getAxis(i);
            assertEquals("Unexpected axis name.",      axisNames        [i], axis.getName().getCode());
            assertEquals("Unexpected abbreviation.",   axisAbbreviations[i], axis.getAbbreviation());
            assertEquals("Unexpected axis direction.", AXIS_DIRECTIONS  [i], axis.getDirection());
            assertEquals("Unexpected axis unit.",      axisUnits        [i], axis.getUnit());
            if (!isProjected) {
                assertEquals("Unexpected toString().", "NetCDF:" + AXIS_NAMES[i], axis.toString());
            }
        }
    }

    /**
     * Removes a few digits to some numbers, in order to protect the test suite from
     * slight variation in floating point computation.
     */
    private static String simplify(final String tree) {
        final StringBuilder buffer = new StringBuilder(tree);
        for (final String search : SIMPLIFIED) {
            final int length = search.length();
            for (int i=buffer.indexOf(search); i>=0; i=buffer.indexOf(search, i)) {
                int j = (i += length);
                char c;
                do c = buffer.charAt(++j);
                while (c >= '0' && c <= '9');
                buffer.delete(i, j);
            }
        }
        return buffer.toString();
    }

    /**
     * Tests the metadata.
     *
     * @throws IOException if an error occurred while reading the file.
     */
    @Test
    public void testMetadata() throws IOException {
        prepareImageReader(true);
        final NetcdfImageReader reader = (NetcdfImageReader) this.reader;
        assertArrayEquals(new String[] {"temperature", "pct_variance"}, reader.getImageNames().toArray());
        assertEquals(  2, reader.getNumImages(false));
        assertEquals(  1, reader.getNumBands (0));
        assertEquals(  4, reader.getDimension(0));
        assertEquals(720, reader.getWidth    (0));
        assertEquals(499, reader.getHeight   (0));
        assertEquals(DataBuffer.TYPE_SHORT, reader.getRawDataType(0));
        final SpatialMetadata metadata = reader.getImageMetadata(0);
        assertNotNull(metadata);
        assertMultilinesEquals(decodeQuotes(EXPECTED_METADATA +
            "└───ImageDescription\n" +
            "    └───Dimensions\n" +
            "        └───Dimension\n" +
            "            ├───descriptor=“temperature”\n" +
            "            ├───units=“degree_Celsius”\n" +
            "            ├───minValue=“-3.0”\n" +
            "            ├───maxValue=“40.0”\n" +
            "            ├───validSampleValues=“[-23000 … 20000]”\n" +
            "            ├───fillSampleValues=“32767.0”\n" +
            "            ├───scaleFactor=“0.001”\n" +
            "            ├───offset=“20.0”\n" +
            "            └───transferFunctionType=“linear”"), simplify(metadata.toString()));
    }

    /**
     * Tests the metadata with two named bands.
     *
     * @throws IOException if an error occurred while reading the file.
     */
    @Test
    public void testMetadataTwoBands() throws IOException {
        prepareImageReader(true);
        final NetcdfImageReader reader = (NetcdfImageReader) this.reader;
        reader.setBandNames(0, "temperature", "pct_variance");
        assertEquals(  2, reader.getNumBands (0));
        assertEquals(  4, reader.getDimension(0));
        assertEquals(720, reader.getWidth    (0));
        assertEquals(499, reader.getHeight   (0));
        assertEquals(DataBuffer.TYPE_SHORT, reader.getRawDataType(0));
        final SpatialMetadata metadata = reader.getImageMetadata(0);
        assertNotNull(metadata);
        assertMultilinesEquals(decodeQuotes(EXPECTED_METADATA +
            "└───ImageDescription\n" +
            "    └───Dimensions\n" +
            "        ├───Dimension\n" +
            "        │   ├───descriptor=“temperature”\n" +
            "        │   ├───units=“degree_Celsius”\n" +
            "        │   ├───minValue=“-3.0”\n" +
            "        │   ├───maxValue=“40.0”\n" +
            "        │   ├───validSampleValues=“[-23000 … 20000]”\n" +
            "        │   ├───fillSampleValues=“32767.0”\n" +
            "        │   ├───scaleFactor=“0.001”\n" +
            "        │   ├───offset=“20.0”\n" +
            "        │   └───transferFunctionType=“linear”\n" +
            "        └───Dimension\n" +
            "            ├───descriptor=“pct_variance”\n" +
            "            ├───units=“percent”\n" +
            "            ├───minValue=“0.0”\n" +
            "            ├───maxValue=“1.0”\n" +
            "            ├───validSampleValues=“[0 … 100]”\n" +
            "            ├───fillSampleValues=“32767.0”\n" +
            "            ├───scaleFactor=“0.01”\n" +
            "            └───transferFunctionType=“linear”"), simplify(metadata.toString()));
    }

    /**
     * Tests reading a few sample values at different slices, selected as band index.
     *
     * @throws IOException if an error occurred while reading the file.
     *
     * @since 3.15
     */
    @Test
    public void testReadSliceThroughBandAPI() throws IOException {
        prepareImageReader(true);
        final NetcdfImageReader reader = (NetcdfImageReader) this.reader;
        assertEquals("Unexpected number of variables.",      2, reader.getNumImages(true));
        assertEquals("Expected only 1 band by default.",     1, reader.getNumBands(0));
        reader.getDimensionForAPI(DimensionSlice.API.BANDS).addDimensionId("depth");
        assertEquals("Expected the number of z values.",    59, reader.getNumBands(0));
        assertEquals("Number of images shall be unchanged.", 2, reader.getNumImages(true));
        assertNull  ("Should not be an aggregation.",           reader.getAggregatedFiles(0));
        assertArrayEquals("Expected the names of the variables found in the NetCDF file.",
                new String[] {"temperature", "pct_variance"}, reader.getImageNames().toArray());
        /*
         * Set the subregion to load.
         */
        final SpatialImageReadParam param = reader.getDefaultReadParam();
        param.setSourceRegion(new Rectangle(360, 260, 2, 3));
        param.setSourceBands(new int[] {0});
        Raster data;
        /*
         * Read data at the first band index, which is 0.
         */
        assertArrayEquals(new int[] {0}, param.getSourceBands());
        data = reader.readRaster(0, param);
        assertEquals(2, data.getWidth());
        assertEquals(3, data.getHeight());
        assertEquals(1, data.getNumBands());
        assertArrayEquals(new int[] {5941, 5933, 6046, 6029, 6138, 6120},
                data.getSamples(0, 0, 2, 3, 0, (int[]) null));
        /*
         * Select a band and read data again.
         */
        param.setSourceBands(new int[] {1});
        data = reader.readRaster(0, param);
        assertEquals(2, data.getWidth());
        assertEquals(3, data.getHeight());
        assertEquals(1, data.getNumBands());
        assertArrayEquals(new int[] {5880, 5888, 6007, 6007, 6125, 6124},
                data.getSamples(0, 0, 2, 3, 0, (int[]) null));
    }

    /**
     * Tests reading a few sample values at different slices, selected as image index.
     *
     * @throws IOException if an error occurred while reading the file.
     *
     * @since 3.15
     */
    @Test
    public void testReadSliceThroughImageAPI() throws IOException {
        prepareImageReader(true);
        final NetcdfImageReader reader = (NetcdfImageReader) this.reader;
        assertEquals("Unexpected number of variables.",      2, reader.getNumImages(true));
        assertEquals("Expected only 1 band by default.",     1, reader.getNumBands(0));
        reader.getDimensionForAPI(DimensionSlice.API.IMAGES).addDimensionId("depth");
        assertEquals("Number of bands shall be unchanged.",  1, reader.getNumBands(0));
        assertEquals("Expected the number of z values.",    59, reader.getNumImages(true));
        assertNull  ("Should not be an aggregation.",           reader.getAggregatedFiles(0));
        assertArrayEquals("Expected the names of the variables found in the NetCDF file.",
                new String[] {"temperature", "pct_variance"}, reader.getImageNames().toArray());
        /*
         * Set the subregion to load.
         */
        final SpatialImageReadParam param = reader.getDefaultReadParam();
        param.setSourceRegion(new Rectangle(360, 260, 2, 3));
        assertNull(param.getSourceBands());
        Raster data;
        /*
         * Read data in the slice z0.
         */
        data = reader.readRaster(0, param);
        assertEquals(2, data.getWidth());
        assertEquals(3, data.getHeight());
        assertEquals(1, data.getNumBands());
        assertArrayEquals(new int[] {5941, 5933, 6046, 6029, 6138, 6120},
                data.getSamples(0, 0, 2, 3, 0, (int[]) null));
        /*
         * Read data in the slice z1.
         */
        data = reader.readRaster(1, param);
        assertEquals(2, data.getWidth());
        assertEquals(3, data.getHeight());
        assertEquals(1, data.getNumBands());
        assertArrayEquals(new int[] {5880, 5888, 6007, 6007, 6125, 6124},
                data.getSamples(0, 0, 2, 3, 0, (int[]) null));
    }

    /**
     * Tests a {@link ImageCoverageReader#read} operation.
     *
     * @throws CoverageStoreException If an error occurred while reading the NetCDF file.
     * @throws TransformException Should not occur.
     */
    @Test
    public void testCoverageReader() throws CoverageStoreException, TransformException {
        final ImageCoverageReader reader = new ImageCoverageReader();
        reader.setInput(getTestFile());
        assertArrayEquals(VARIABLE_NAMES, toStringArray(reader.getCoverageNames()));
        final GridCoverage2D coverage = reader.read(0, null);
        assertNotNull(coverage);
        reader.dispose();
        /*
         * Verify the grid coverage.
         */
        CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem();
        assertEquals(4, crs.getCoordinateSystem().getDimension());
        assertTrue(crs instanceof CompoundCRS);
        crs = ((CompoundCRS) crs).getComponents().get(0);
        assertTrue(crs instanceof ProjectedCRS);
        /*
         * Verify the envelope.
         */
        Envelope envelope = coverage.getEnvelope();
        assertEquals(-19987288, envelope.getMinimum(0), 1);
        assertEquals(-13871567, envelope.getMinimum(1), 1);
        envelope = Envelopes.transform(envelope, DefaultGeographicCRS.SPHERE);
        /*
         * Note: Coriolis data have a 0.25° offset in longitude. This is a known
         * problem of the tested data, not a problem of the Geotk library.
         */
        assertEquals(-179.750, envelope.getMinimum(0), 1E-10);
        assertEquals( 180.250, envelope.getMaximum(0), 1E-10);
        assertEquals( -77.067, envelope.getMinimum(1), 1E-3);
        assertEquals(  77.067, envelope.getMaximum(1), 1E-3);
    }
}
