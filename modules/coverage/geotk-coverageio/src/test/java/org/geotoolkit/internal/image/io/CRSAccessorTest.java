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
package org.geotoolkit.internal.image.io;

import java.util.Locale;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.metadata.SpatialMetadataFormat;
import org.geotoolkit.image.io.metadata.MetadataAccessorTest;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.WKT;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;

import org.junit.*;
import org.geotoolkit.test.Depend;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests the {@link CRSAccessor} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.07
 */
@Depend(MetadataAccessorTest.class)
public final class CRSAccessorTest {
    /**
     * The previous locale before the test is run.
     * This is usually the default locale.
     */
    private Locale defaultLocale;

    /**
     * Sets the locale to a compile-time value. We need to use a fixed value because the
     * name of the coordinate system is locale-sensitive in this test.
     */
    @Before
    public void fixLocale() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.FRANCE);
    }

    /**
     * Restores the locales to its original value.
     */
    @After
    public void restoreLocale() {
        Locale.setDefault(defaultLocale);
    }

    /**
     * Tests the formatting of the WGS84 CRS.
     */
    @Test
    public void testGeographicCRS() {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final CRSAccessor accessor = new CRSAccessor(metadata);
        accessor.setCRS(DefaultGeographicCRS.WGS84);
        String expected = SpatialMetadataFormat.FORMAT_NAME + '\n' +
            "└───RectifiedGridDomain\n" +
            "    └───CoordinateReferenceSystem\n" +
            "        ├───name=“WGS84(DD)”\n" +
            "        ├───type=“geographic”\n" +
            "        ├───Datum\n" +
            "        │   ├───name=“OGC:WGS84”\n" +
            "        │   ├───type=“geodetic”\n" +
            "        │   ├───Ellipsoid\n" +
            "        │   │   ├───name=“WGS84”\n" +
            "        │   │   ├───axisUnit=“m”\n" +
            "        │   │   ├───semiMajorAxis=“6378137.0”\n" +
            "        │   │   └───inverseFlattening=“298.257223563”\n" +
            "        │   └───PrimeMeridian\n" +
            "        │       ├───name=“Greenwich”\n" +
            "        │       ├───greenwichLongitude=“0.0”\n" +
            "        │       └───angularUnit=“deg”\n" +
            "        └───CoordinateSystem\n" +
            "            ├───name=“Géodésique 2D”\n" +
            "            ├───type=“ellipsoidal”\n" +
            "            ├───dimension=“2”\n" +
            "            └───Axes\n" +
            "                ├───CoordinateSystemAxis\n" +
            "                │   ├───name=“Geodetic longitude”\n" +
            "                │   ├───axisAbbrev=“λ”\n" +
            "                │   ├───direction=“east”\n" +
            "                │   ├───minimumValue=“-180.0”\n" +
            "                │   ├───maximumValue=“180.0”\n" +
            "                │   ├───rangeMeaning=“wraparound”\n" +
            "                │   └───unit=“deg”\n" +
            "                └───CoordinateSystemAxis\n" +
            "                    ├───name=“Geodetic latitude”\n" +
            "                    ├───axisAbbrev=“φ”\n" +
            "                    ├───direction=“north”\n" +
            "                    ├───minimumValue=“-90.0”\n" +
            "                    ├───maximumValue=“90.0”\n" +
            "                    ├───rangeMeaning=“exact”\n" +
            "                    └───unit=“deg”";
        /*
         * We must replace the name of the Coordinate System from French to current locale
         * because the above CRS uses the DefaultEllipsoidalCS.GEODETIC_2D static final constant,
         * which has been initialized to the current locale and is not refreshed after the call
         * to Locale.setDefault(Locale.FRANCE).
         */
        final String localizedName = DefaultEllipsoidalCS.GEODETIC_2D.getName().getCode();
        expected = expected.replace("“Géodésique 2D”", '"' + localizedName + '"');
        assertMultilinesEquals(decodeQuotes(expected), metadata.toString());
    }

    /**
     * Tests the formatting of a Mercator CRS.
     * In the particular case of the Mercator projection used in this test,
     * every parameter values are omitted because they are all equal to the
     * default values.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testProjectedCRS() throws FactoryException {
        final CoordinateReferenceSystem crs = CRS.parseWKT(WKT.PROJCS_MERCATOR);
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final CRSAccessor accessor = new CRSAccessor(metadata);
        accessor.setCRS(crs);
        assertMultilinesEquals(decodeQuotes(SpatialMetadataFormat.FORMAT_NAME + '\n' +
            "└───RectifiedGridDomain\n" +
            "    └───CoordinateReferenceSystem\n" +
            "        ├───name=“EPSG:WGS 84 / World Mercator”\n" +
            "        ├───type=“projected”\n" +
            "        ├───Datum\n" +
            "        │   ├───name=“EPSG:World Geodetic System 1984”\n" +
            "        │   ├───type=“geodetic”\n" +
            "        │   ├───Ellipsoid\n" +
            "        │   │   ├───name=“EPSG:WGS 84”\n" +
            "        │   │   ├───axisUnit=“m”\n" +
            "        │   │   ├───semiMajorAxis=“6378137.0”\n" +
            "        │   │   └───inverseFlattening=“298.257223563”\n" +
            "        │   └───PrimeMeridian\n" +
            "        │       ├───name=“EPSG:Greenwich”\n" +
            "        │       ├───greenwichLongitude=“0.0”\n" +
            "        │       └───angularUnit=“deg”\n" +
            "        ├───CoordinateSystem\n" +
            "        │   ├───name=“EPSG:WGS 84 / World Mercator”\n" +
            "        │   ├───type=“cartesian”\n" +
            "        │   ├───dimension=“2”\n" +
            "        │   └───Axes\n" +
            "        │       ├───CoordinateSystemAxis\n" +
            "        │       │   ├───name=“Easting”\n" +
            "        │       │   ├───axisAbbrev=“E”\n" +
            "        │       │   ├───direction=“east”\n" +
            "        │       │   └───unit=“m”\n" +
            "        │       └───CoordinateSystemAxis\n" +
            "        │           ├───name=“Northing”\n" +
            "        │           ├───axisAbbrev=“N”\n" +
            "        │           ├───direction=“north”\n" +
            "        │           └───unit=“m”\n" +
            "        └───Conversion\n" +
            "            ├───name=“WGS 84 / World Mercator”\n" +
            "            └───method=“Mercator_1SP”"), metadata.toString());
    }

    /**
     * Tests the formatting of a Transverse Mercator CRS.
     * This projection contains some parameter values different than the default ones.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testTransverseMercatorCRS() throws FactoryException {
        final CoordinateReferenceSystem crs = CRS.parseWKT(WKT.PROJCS_UTM_10N);
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final CRSAccessor accessor = new CRSAccessor(metadata);
        accessor.setCRS(crs);
        assertMultilinesEquals(decodeQuotes(SpatialMetadataFormat.FORMAT_NAME + '\n' +
            "└───RectifiedGridDomain\n" +
            "    └───CoordinateReferenceSystem\n" +
            "        ├───name=“NAD_1983_UTM_Zone_10N”\n" +
            "        ├───type=“projected”\n" +
            "        ├───Datum\n" +
            "        │   ├───name=“D_North_American_1983”\n" +
            "        │   ├───type=“geodetic”\n" +
            "        │   ├───Ellipsoid\n" +
            "        │   │   ├───name=“GRS_1980”\n" +
            "        │   │   ├───axisUnit=“m”\n" +
            "        │   │   ├───semiMajorAxis=“6378137.0”\n" +
            "        │   │   └───inverseFlattening=“298.257222101”\n" +
            "        │   └───PrimeMeridian\n" +
            "        │       ├───name=“Greenwich”\n" +
            "        │       ├───greenwichLongitude=“0.0”\n" +
            "        │       └───angularUnit=“deg”\n" +
            "        ├───CoordinateSystem\n" +
            "        │   ├───name=“NAD_1983_UTM_Zone_10N”\n" +
            "        │   ├───type=“cartesian”\n" +
            "        │   ├───dimension=“2”\n" +
            "        │   └───Axes\n" +
            "        │       ├───CoordinateSystemAxis\n" +
            "        │       │   ├───name=“x”\n" +
            "        │       │   ├───direction=“east”\n" +
            "        │       │   └───unit=“m”\n" +
            "        │       └───CoordinateSystemAxis\n" +
            "        │           ├───name=“y”\n" +
            "        │           ├───direction=“north”\n" +
            "        │           └───unit=“m”\n" +
            "        └───Conversion\n" +
            "            ├───name=“NAD_1983_UTM_Zone_10N”\n" +
            "            ├───method=“Transverse_Mercator”\n" +
            "            └───Parameters\n" +
            "                ├───ParameterValue\n" +
            "                │   ├───name=“central_meridian”\n" +
            "                │   └───value=“-123.0”\n" +
            "                ├───ParameterValue\n" +
            "                │   ├───name=“scale_factor”\n" +
            "                │   └───value=“0.9996”\n" +
            "                └───ParameterValue\n" +
            "                    ├───name=“false_easting”\n" +
            "                    └───value=“500000.0”"), metadata.toString());
    }
}
