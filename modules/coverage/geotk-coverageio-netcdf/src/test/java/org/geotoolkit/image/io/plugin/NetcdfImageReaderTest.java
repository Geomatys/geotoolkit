/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.net.URI;
import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.awt.Rectangle;
import java.awt.image.Raster;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.DataBuffer;
import java.awt.image.BufferedImage;

import org.geotoolkit.image.io.DimensionSlice;
import org.geotoolkit.image.io.SpatialImageReadParam;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;

import org.geotoolkit.test.image.ImageReaderTestBase;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests {@link NetcdfImageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.08
 */
public final strictfp class NetcdfImageReaderTest extends ImageReaderTestBase {
    /**
     * Creates a new test suite.
     */
    public NetcdfImageReaderTest() {
        super(NetcdfImageReader.class);
    }

    /**
     * Creates a reader.
     */
    @Override
    protected NetcdfImageReader createImageReader() throws IOException {
        NetcdfImageReader.Spi spi = new NetcdfImageReader.Spi();
        final NetcdfImageReader reader = new NetcdfImageReader(spi);
        reader.setInput(NetcdfTestBase.getTestFile());
        return reader;
    }

    /**
     * The first part of expected metadata (without the sample dimensions).
     */
    private static final String EXPECTED_METADATA =
            SpatialMetadataFormat.FORMAT_NAME + '\n' +
            "├───RectifiedGridDomain\n" +
            "│   ├───origin=“-1.9959489E7 1.3899365E7 5.0 20975.0”\n" +
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
    private static final String[] SIMPLIFIED = {"-1.9959489", "1.3899365", "55597.46"};

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
        final NetcdfImageReader reader = createImageReader();
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
            "            ├───scaleFactor=“0.0010”\n" +
            "            ├───offset=“20.0”\n" +
            "            └───transferFunctionType=“linear”"), simplify(metadata.toString()));
        reader.dispose();
    }

    /**
     * Tests the metadata with two named bands.
     *
     * @throws IOException if an error occurred while reading the file.
     */
    @Test
    public void testMetadataTwoBands() throws IOException {
        final NetcdfImageReader reader = createImageReader();
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
            "        │   ├───scaleFactor=“0.0010”\n" +
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
        reader.dispose();
    }

    /**
     * Reads the image using the given image reader, and returns the data as a single raster.
     * The image is optionally shown in a widget if the {@link #viewEnabled} field is set to
     * {@code true}.
     */
    private Raster read(final String method, final NetcdfImageReader reader, final int imageIndex,
            final SpatialImageReadParam param) throws IOException
    {
        final BufferedImage image = reader.read(imageIndex, param);
        this.image = image;
        showCurrentImage(method);
        return image.getRaster();
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
        final NetcdfImageReader reader = createImageReader();
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
        Raster data;
        /*
         * Read data at the default band index, which is 0.
         */
        assertArrayEquals(new int[] {0}, param.getSourceBands());
        data = read("testReadSliceThroughBandAPI(0)", reader, 0, param);
        assertEquals(2, data.getWidth());
        assertEquals(3, data.getHeight());
        assertEquals(1, data.getNumBands());
        assertArrayEquals(new int[] {5941, 5933, 6046, 6029, 6138, 6120},
                data.getSamples(0, 0, 2, 3, 0, (int[]) null));
        /*
         * Select a band and read data again.
         */
        param.setSourceBands(new int[] {1});
        data = read("testReadSliceThroughBandAPI(1)", reader, 0, param);
        assertEquals(2, data.getWidth());
        assertEquals(3, data.getHeight());
        assertEquals(1, data.getNumBands());
        assertArrayEquals(new int[] {5880, 5888, 6007, 6007, 6125, 6124},
                data.getSamples(0, 0, 2, 3, 0, (int[]) null));
        reader.dispose();
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
        final NetcdfImageReader reader = createImageReader();
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
        data = read("testReadSliceThroughImageAPI(0)", reader, 0, param);
        assertEquals(2, data.getWidth());
        assertEquals(3, data.getHeight());
        assertEquals(1, data.getNumBands());
        assertArrayEquals(new int[] {5941, 5933, 6046, 6029, 6138, 6120},
                data.getSamples(0, 0, 2, 3, 0, (int[]) null));
        /*
         * Read data in the slice z1.
         */
        data = read("testReadSliceThroughImageAPI(1)", reader, 1, param);
        assertEquals(2, data.getWidth());
        assertEquals(3, data.getHeight());
        assertEquals(1, data.getNumBands());
        assertArrayEquals(new int[] {5880, 5888, 6007, 6007, 6125, 6124},
                data.getSamples(0, 0, 2, 3, 0, (int[]) null));
        reader.dispose();
    }

    /**
     * Tests reading a NcML file.
     *
     * @throws IOException if an error occurred while reading the file.
     *
     * @since 3.16
     */
    @Test
    public void testNcML() throws IOException {
        final NetcdfImageReader reader = new NetcdfImageReader(null);
        reader.setInput(new File(NetcdfTestBase.getTestFile().getParentFile(), "Aggregation.ncml"));
        assertEquals("Unexpected number of variables.",  4, reader.getNumImages(true));
        assertEquals("Expected only 1 band by default.", 1, reader.getNumBands(0));
        assertArrayEquals("Expected the names of the variables found in the NcML file.",
                new String[] { // Note that "pct_variance" variables are renamed in the NcML file.
                    "temperature", "temperature_pct_variance",
                    "salinity",    "salinity_pct_variance"},
                reader.getImageNames().toArray());
        /*
         * Test the paths to the file components for the "temperature" variable.
         */
        assertArrayEquals(new String[] {
                "OA_RTQCGL01_20070606_FLD_TEMP.nc",
                "OA_RTQCGL01_20070613_FLD_TEMP.nc",
                "OA_RTQCGL01_20070620_FLD_TEMP.nc"
            }, filenames(reader.getAggregatedFiles(0)));
        /*
         * Test the paths to the file components for the "salinity_pct_variance" variable.
         */
        assertArrayEquals(new String[] {
                "OA_RTQCGL01_20070606_FLD_PSAL.nc",
                "OA_RTQCGL01_20070613_FLD_PSAL.nc",
                "OA_RTQCGL01_20070620_FLD_PSAL.nc"
            }, filenames(reader.getAggregatedFiles(3)));
        reader.dispose();
    }

    /**
     * Returns the filename of the given aggregated URI. We omit the parent
     * directory because they are platform-dependent.
     */
    private static String[] filenames(final List<URI> aggregated) {
        assertNotNull("Expected aggregated NetCDF files.", aggregated);
        final String[] filenames = new String[aggregated.size()];
        for (int i=0; i<filenames.length; i++) {
            filenames[i] = new File(aggregated.get(i).getPath()).getName();
        }
        return filenames;
    }

    /**
     * Tests the registration of the image reader in the Image I/O framework.
     */
    @Test
    public void testRegistrationByFormatName() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("NetCDF");
        assertTrue("Expected a reader.", it.hasNext());
        assertTrue(it.next() instanceof NetcdfImageReader);
        assertFalse("Expected no more reader.", it.hasNext());
    }

    /**
     * Tests the registration by MIME type.
     * Note that more than one writer may be registered.
     */
    @Test
    public void testRegistrationByMIMEType() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByMIMEType("application/netcdf");
        while (it.hasNext()) {
            if (it.next() instanceof NetcdfImageReader) {
                return;
            }
        }
        fail("Reader not found.");
    }
}
