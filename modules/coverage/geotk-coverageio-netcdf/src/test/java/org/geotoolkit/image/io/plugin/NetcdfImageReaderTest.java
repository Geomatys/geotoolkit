/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.util.Iterator;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.DataBuffer;

import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;

import org.geotoolkit.test.image.ImageReaderTestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests {@link NetcdfImageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.08
 */
public final class NetcdfImageReaderTest extends ImageReaderTestBase {
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
            "│   ├───origin=“-179.5 -77.0104751586914 5.0 0.0”\n" +
            "│   ├───CoordinateReferenceSystem\n" +
            "│   │   ├───name=“NetCDF:time depth latitude longitude”\n" +
            "│   │   ├───type=“geographic”\n" +
            "│   │   ├───Datum\n" +
            "│   │   │   ├───name=“OGC:WGS84”\n" +
            "│   │   │   ├───type=“geodetic”\n" +
            "│   │   │   ├───Ellipsoid\n" +
            "│   │   │   │   ├───name=“WGS84”\n" +
            "│   │   │   │   ├───axisUnit=“m”\n" +
            "│   │   │   │   ├───semiMajorAxis=“6378137.0”\n" +
            "│   │   │   │   └───inverseFlattening=“298.257223563”\n" +
            "│   │   │   └───PrimeMeridian\n" +
            "│   │   │       ├───name=“Greenwich”\n" +
            "│   │   │       ├───greenwichLongitude=“0.0”\n" +
            "│   │   │       └───angularUnit=“deg”\n" +
            "│   │   └───CoordinateSystem\n" +
            "│   │       ├───name=“NetCDF:time depth latitude longitude”\n" +
            "│   │       ├───type=“ellipsoidal”\n" +
            "│   │       ├───dimension=“4”\n" +
            "│   │       └───Axes\n" +
            "│   │           ├───CoordinateSystemAxis\n" +
            "│   │           │   ├───name=“NetCDF:longitude”\n" +
            "│   │           │   ├───axisAbbrev=“λ”\n" +
            "│   │           │   ├───direction=“east”\n" +
            "│   │           │   ├───minimumValue=“-179.5”\n" +
            "│   │           │   ├───maximumValue=“180.0”\n" +
            "│   │           │   └───unit=“deg”\n" +
            "│   │           ├───CoordinateSystemAxis\n" +
            "│   │           │   ├───name=“NetCDF:latitude”\n" +
            "│   │           │   ├───axisAbbrev=“φ”\n" +
            "│   │           │   ├───direction=“north”\n" +
            "│   │           │   ├───minimumValue=“-77.0104751586914”\n" +
            "│   │           │   ├───maximumValue=“77.0104751586914”\n" +
            "│   │           │   └───unit=“deg”\n" +
            "│   │           ├───CoordinateSystemAxis\n" +
            "│   │           │   ├───name=“NetCDF:depth”\n" +
            "│   │           │   ├───axisAbbrev=“d”\n" +
            "│   │           │   ├───direction=“down”\n" +
            "│   │           │   ├───minimumValue=“5.0”\n" +
            "│   │           │   ├───maximumValue=“1950.0”\n" +
            "│   │           │   └───unit=“m”\n" +
            "│   │           └───CoordinateSystemAxis\n" +
            "│   │               ├───name=“NetCDF:time”\n" +
            "│   │               ├───axisAbbrev=“t”\n" +
            "│   │               ├───direction=“future”\n" +
            "│   │               ├───minimumValue=“20975.0”\n" +
            "│   │               ├───maximumValue=“20975.0”\n" +
            "│   │               └───unit=“d”\n" +
            "│   ├───OffsetVectors\n" +
            "│   │   ├───OffsetVector\n" +
            "│   │   │   └───values=“0.5 0.0 0.0 0.0”\n" +
            "│   │   ├───OffsetVector\n" +
            "│   │   │   └───values=“0.0 NaN 0.0 0.0”\n" +
            "│   │   ├───OffsetVector\n" +
            "│   │   │   └───values=“0.0 0.0 NaN 0.0”\n" +
            "│   │   └───OffsetVector\n" +
            "│   │       └───values=“0.0 0.0 0.0 0.0”\n" +
            "│   └───Limits\n" +
            "│       ├───low=“0 0 0 0”\n" +
            "│       └───high=“719 498 58 0”\n";

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
            "            └───transferFunctionType=“linear”"), metadata.toString());
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
            "            └───transferFunctionType=“linear”"), metadata.toString());
        reader.dispose();
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
