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
package org.geotoolkit.image.io.text;

import java.util.Locale;
import java.io.IOException;
import java.nio.charset.Charset;

import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.test.TestData;
import org.geotoolkit.test.Depend;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests {@link WorldFileImageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 */
@Depend(TextMatrixImageReader.class)
public final class WorldFileImageReaderTest extends TextImageReaderTestBase {
    /**
     * Creates a reader.
     */
    @Override
    protected WorldFileImageReader createImageReader() throws IOException {
        final TextMatrixImageReader.Spi main = new TextMatrixImageReader.Spi();
        main.padValue = -9999;
        main.locale   = Locale.CANADA;
        main.charset  = Charset.forName("UTF-8");
        final WorldFileImageReader.Spi spi = new WorldFileImageReader.Spi(main);
        final WorldFileImageReader reader = new WorldFileImageReader(spi);
        reader.setInput(TestData.file(this, "matrix.txt"));
        return reader;
    }

    /**
     * Tests the metadata of the {@link "grid.asc"} file.
     *
     * @throws IOException if an error occured while reading the file.
     */
    @Test
    public void testMetadata() throws IOException {
        final WorldFileImageReader reader = createImageReader();
        assertEquals(20, reader.getWidth (0));
        assertEquals(42, reader.getHeight(0));
        assertNull(reader.getStreamMetadata());
        final SpatialMetadata metadata = reader.getImageMetadata(0);
        assertNotNull(metadata);
        assertMultilinesEquals(decodeQuotes(
            SpatialMetadataFormat.FORMAT_NAME + '\n' +
                "├───RectifiedGridDomain\n" +
                "│   ├───origin=“-10000.0 21000.0”\n" +
                "│   ├───OffsetVectors\n" +
                "│   │   ├───OffsetVector\n" +
                "│   │   │   └───values=“1000.0 0.0”\n" +
                "│   │   └───OffsetVector\n" +
                "│   │       └───values=“0.0 -1000.0”\n" +
                "│   └───Limits\n" +
                "│       ├───low=“0 0”\n" +
                "│       └───high=“19 41”\n" +
                "├───SpatialRepresentation\n" +
                "│   ├───numberOfDimensions=“2”\n" +
                "│   └───centerPoint=“0.0 0.0”\n" +
                "└───CoordinateReferenceSystem\n" +
                "    ├───name=“WGS 84 / World Mercator”\n" +
                "    ├───type=“projected”\n" +
                "    ├───Datum\n" +
                "    │   ├───name=“World Geodetic System 1984”\n" +
                "    │   ├───type=“geodetic”\n" +
                "    │   ├───Ellipsoid\n" +
                "    │   │   ├───name=“WGS 84”\n" +
                "    │   │   ├───axisUnit=“m”\n" +
                "    │   │   ├───semiMajorAxis=“6378137.0”\n" +
                "    │   │   └───inverseFlattening=“298.257223563”\n" +
                "    │   └───PrimeMeridian\n" +
                "    │       ├───name=“Greenwich”\n" +
                "    │       ├───greenwichLongitude=“0.0”\n" +
                "    │       └───angularUnit=“deg”\n" +
                "    ├───CoordinateSystem\n" +
                "    │   ├───name=“WGS 84 / World Mercator”\n" +
                "    │   ├───type=“cartesian”\n" +
                "    │   ├───dimension=“2”\n" +
                "    │   └───Axes\n" +
                "    │       ├───CoordinateSystemAxis\n" +
                "    │       │   ├───name=“x”\n" +
                "    │       │   ├───axisAbbrev=“x”\n" +
                "    │       │   ├───direction=“east”\n" +
                "    │       │   └───unit=“m”\n" +
                "    │       └───CoordinateSystemAxis\n" +
                "    │           ├───name=“y”\n" +
                "    │           ├───axisAbbrev=“y”\n" +
                "    │           ├───direction=“north”\n" +
                "    │           └───unit=“m”\n" +
                "    └───Conversion\n" +
                "        ├───name=“WGS 84 / World Mercator”\n" +
                "        ├───method=“Mercator_1SP”\n" +
                "        └───Parameters\n" +
                "            ├───ParameterValue\n" +
                "            │   ├───name=“semi_major”\n" +
                "            │   └───value=“6378137.0”\n" +
                "            ├───ParameterValue\n" +
                "            │   ├───name=“semi_minor”\n" +
                "            │   └───value=“6356752.314245179”\n" +
                "            ├───ParameterValue\n" +
                "            │   ├───name=“latitude_of_origin”\n" +
                "            │   └───value=“0.0”\n" +
                "            ├───ParameterValue\n" +
                "            │   ├───name=“central_meridian”\n" +
                "            │   └───value=“0.0”\n" +
                "            ├───ParameterValue\n" +
                "            │   ├───name=“scale_factor”\n" +
                "            │   └───value=“1.0”\n" +
                "            ├───ParameterValue\n" +
                "            │   ├───name=“false_easting”\n" +
                "            │   └───value=“0.0”\n" +
                "            └───ParameterValue\n" +
                "                ├───name=“false_northing”\n" +
                "                └───value=“0.0”"), metadata.toString());
    }
}
