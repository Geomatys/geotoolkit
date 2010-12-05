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

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.geotoolkit.test.Depend;
import org.geotoolkit.test.TestData;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.io.mosaic.TileTest;
import org.geotoolkit.image.io.TextImageReaderTestBase;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests {@link WorldFileImageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.07
 */
@Depend(TextMatrixImageReader.class)
public final class WorldFileImageReaderTest extends TextImageReaderTestBase {
    /**
     * Creates a new test suite.
     */
    public WorldFileImageReaderTest() {
        super(WorldFileImageReader.class);
    }

    /**
     * Creates a reader.
     */
    @Override
    protected WorldFileImageReader createImageReader() throws IOException {
        final WorldFileImageReader.Spi spi = new WorldFileImageReader.Spi(new TextMatrixImageReaderTest.Spi());
        final WorldFileImageReader reader = new WorldFileImageReader(spi);
        reader.setInput(TestData.file(this, "matrix.txt"));
        return reader;
    }

    /**
     * Tests the metadata of the {@link "matrix.txt"} file.
     *
     * @throws IOException if an error occurred while reading the file.
     */
    @Test
    public void testMetadata() throws IOException {
        final WorldFileImageReader reader = createImageReader();
        assertEquals(20, reader.getWidth (0));
        assertEquals(42, reader.getHeight(0));
        assertNull(reader.getStreamMetadata());
        final SpatialMetadata metadata = reader.getImageMetadata(0);
        assertNotNull(metadata);
        assertMultilinesEquals(decodeQuotes(SpatialMetadataFormat.FORMAT_NAME + '\n' +
                "├───ImageDescription\n" +
                "│   └───Dimensions\n" +
                "│       └───Dimension\n" +
                "│           ├───minValue=“-1.893”\n" +
                "│           ├───maxValue=“31.14”\n" +
                "│           └───fillSampleValues=“-9999.0”\n" +
                "├───RectifiedGridDomain\n" +
                "│   ├───origin=“-10000.0 21000.0”\n" +
                "│   ├───OffsetVectors\n" +
                "│   │   ├───OffsetVector\n" +
                "│   │   │   └───values=“1000.0 0.0”\n" +
                "│   │   └───OffsetVector\n" +
                "│   │       └───values=“0.0 -1000.0”\n" +
                "│   ├───Limits\n" +
                "│   │   ├───low=“0 0”\n" +
                "│   │   └───high=“19 41”\n" +
                "│   └───CoordinateReferenceSystem\n" +
                "│       ├───name=“WGS 84 / World Mercator”\n" +
                "│       ├───type=“projected”\n" +
                "│       ├───Datum\n" +
                "│       │   ├───name=“World Geodetic System 1984”\n" +
                "│       │   ├───type=“geodetic”\n" +
                "│       │   ├───Ellipsoid\n" +
                "│       │   │   ├───name=“WGS 84”\n" +
                "│       │   │   ├───axisUnit=“m”\n" +
                "│       │   │   ├───semiMajorAxis=“6378137.0”\n" +
                "│       │   │   └───inverseFlattening=“298.257223563”\n" +
                "│       │   └───PrimeMeridian\n" +
                "│       │       ├───name=“Greenwich”\n" +
                "│       │       ├───greenwichLongitude=“0.0”\n" +
                "│       │       └───angularUnit=“deg”\n" +
                "│       ├───CoordinateSystem\n" +
                "│       │   ├───name=“WGS 84 / World Mercator”\n" +
                "│       │   ├───type=“cartesian”\n" +
                "│       │   ├───dimension=“2”\n" +
                "│       │   └───Axes\n" +
                "│       │       ├───CoordinateSystemAxis\n" +
                "│       │       │   ├───name=“x”\n" +
                "│       │       │   ├───direction=“east”\n" +
                "│       │       │   └───unit=“m”\n" +
                "│       │       └───CoordinateSystemAxis\n" +
                "│       │           ├───name=“y”\n" +
                "│       │           ├───direction=“north”\n" +
                "│       │           └───unit=“m”\n" +
                "│       └───Conversion\n" +
                "│           ├───name=“WGS 84 / World Mercator”\n" +
                "│           └───method=“Mercator_1SP”\n" +
                "└───SpatialRepresentation\n" +
                "    ├───numberOfDimensions=“2”\n" +
                "    ├───centerPoint=“0.0 0.0”\n" +
                "    └───pointInPixel=“upperLeft”"), metadata.toString());
        reader.dispose();
    }

    /**
     * Tests reading an image though the standard {@link ImageIO} API and
     * the {@link XImageIO} extension.
     *
     * @throws IOException If an error occurred while reading the test image or
     *         writing it to the temporary file.
     *
     * @since 3.10
     */
    @Test
    public void testImageIO() throws IOException {
        final Locale locale = Locale.getDefault();
        File file = TestData.file(TileTest.class, "A2.png");
        WorldFileImageReader.Spi.registerDefaults(null);
        Locale.setDefault(Locale.US);
        try {
            assertNotNull(ImageIO.read(file));
            /*
             * When using the XImageIO methods, the WorldFileImageReader plugin
             * should be selected if the input is a file except if there is no
             * PGW (or TFW) and no PRJ file. This is the case of A2.png.
             */
            ImageReader reader = XImageIO.getReaderBySuffix(file, true, null);
            assertFalse(reader instanceof WorldFileImageReader);
            ((ImageInputStream) reader.getInput()).close();
            reader.dispose();
            /*
             * Test again, but now using a file which have a TFW file.
             */
            file = TestData.file(this, "matrix.txt");
            reader = XImageIO.getReaderByFormatName("matrix", file, true, null);
            assertTrue(reader instanceof WorldFileImageReader);
            reader.dispose();
            /*
             * Test again, but now ignoring metadata. XImageIO should avoid
             * the usage of WorldFileImageReader.
             */
            reader = XImageIO.getReaderByFormatName("matrix", file, true, true);
            assertFalse(reader instanceof WorldFileImageReader);
            reader.dispose();
            /*
             * If the input is a stream, then the standard reader should be selected.
             */
            final ImageInputStream in = ImageIO.createImageInputStream(file);
            try {
                reader = XImageIO.getReaderByFormatName("matrix", in, true, true);
                assertTrue(reader instanceof TextMatrixImageReader);
                // Don't botter to read the image. The purpose of
                // this test is not to test the Matrix ImageReader.
                reader.dispose();
            } finally {
                in.close();
            }
        } finally {
            Locale.setDefault(locale);
            WorldFileImageReader.Spi.unregisterDefaults(null);
        }
    }
}
