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
package org.geotoolkit.image.io.metadata;

import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.util.FactoryException;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.referencing.WKT;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultProjectedCRS;
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.geotoolkit.test.LocaleDependantTestBase;
import org.apache.sis.referencing.CommonCRS;
import org.junit.*;

import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.*;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Tests the {@link ReferencingBuilder} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.07
 */
@DependsOn(MetadataNodeAccessorTest.class)
public final strictfp class ReferencingBuilderTest extends LocaleDependantTestBase {
    /**
     * Tests the formatting of the WGS84 CRS.
     */
    @Test
    public void testFormatGeographicCRS() {
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final ReferencingBuilder builder = new ReferencingBuilder(metadata);
        builder.setCoordinateReferenceSystem(CommonCRS.WGS84.normalizedGeographic());
        String expected = GEOTK_FORMAT_NAME + '\n' +
            "└───RectifiedGridDomain\n" +
            "    └───CoordinateReferenceSystem\n" +
            "        ├───name=“EPSG:WGS 84”\n" +
            "        ├───type=“geographic”\n" +
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
            "        └───CoordinateSystem\n" +
            "            ├───name=“Ellipsoidal CS: East (deg), North (deg).”\n" +
            "            ├───type=“ellipsoidal”\n" +
            "            ├───dimension=“2”\n" +
            "            └───Axes\n" +
            "                ├───CoordinateSystemAxis\n" +
            "                │   ├───name=“EPSG:Geodetic longitude”\n" +
            "                │   ├───axisAbbrev=“λ”\n" +
            "                │   ├───direction=“east”\n" +
            "                │   ├───minimumValue=“-180.0”\n" +
            "                │   ├───maximumValue=“180.0”\n" +
            "                │   ├───rangeMeaning=“wraparound”\n" +
            "                │   └───unit=“deg”\n" +
            "                └───CoordinateSystemAxis\n" +
            "                    ├───name=“EPSG:Geodetic latitude”\n" +
            "                    ├───axisAbbrev=“φ”\n" +
            "                    ├───direction=“north”\n" +
            "                    ├───minimumValue=“-90.0”\n" +
            "                    ├───maximumValue=“90.0”\n" +
            "                    ├───rangeMeaning=“exact”\n" +
            "                    └───unit=“deg”\n";
        /*
         * We must replace the name of the Coordinate System from French to current locale
         * because the above CRS uses the DefaultEllipsoidalCS.GEODETIC_2D static final constant,
         * which has been initialized to the current locale and is not refreshed after the call
         * to Locale.setDefault(Locale.FRANCE).
         */
        final String localizedName = PredefinedCS.GEODETIC_2D.getName().getCode();
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
    public void testFormatProjectedCRS() throws FactoryException {
        final CoordinateReferenceSystem crs = CRS.parseWKT(WKT.PROJCS_MERCATOR);
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final ReferencingBuilder builder = new ReferencingBuilder(metadata);
        builder.setCoordinateReferenceSystem(crs);
        assertMultilinesEquals(decodeQuotes(GEOTK_FORMAT_NAME + '\n' +
            "└───RectifiedGridDomain\n" +
            "    └───CoordinateReferenceSystem\n" +
            "        ├───name=“WGS 84 / World Mercator”\n" +
            "        ├───type=“projected”\n" +
            "        ├───Datum\n" +
            "        │   ├───name=“World Geodetic System 1984”\n" +
            "        │   ├───type=“geodetic”\n" +
            "        │   ├───Ellipsoid\n" +
            "        │   │   ├───name=“WGS 84”\n" +
            "        │   │   ├───axisUnit=“m”\n" +
            "        │   │   ├───semiMajorAxis=“6378137.0”\n" +
            "        │   │   └───inverseFlattening=“298.257223563”\n" +
            "        │   └───PrimeMeridian\n" +
            "        │       ├───name=“Greenwich”\n" +
            "        │       ├───greenwichLongitude=“0.0”\n" +
            "        │       └───angularUnit=“deg”\n" +
            "        ├───CoordinateSystem\n" +
            "        │   ├───name=“WGS 84 / World Mercator”\n" +
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
            "            └───method=“Mercator_1SP”\n"), metadata.toString());
    }

    /**
     * Tests the formatting of a Transverse Mercator CRS.
     * This projection contains some parameter values different than the default ones.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testFormatTransverseMercatorCRS() throws FactoryException {
        final CoordinateReferenceSystem crs = CRS.parseWKT(WKT.PROJCS_UTM_10N);
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final ReferencingBuilder builder = new ReferencingBuilder(metadata);
        builder.setCoordinateReferenceSystem(crs);
        assertMultilinesEquals(decodeQuotes(GEOTK_FORMAT_NAME + '\n' +
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
            "                    └───value=“500000.0”\n"), metadata.toString());
    }

    /**
     * Tests if the two given objects are equal, ignoring metadata.
     */
    private static void assertEqualsIgnoreMetadata(final String message,
            final IdentifiedObject object1, final IdentifiedObject object2)
    {
        assertTrue(message, CRS.equalsIgnoreMetadata(object1, object2));
    }

    /**
     * Tests the parsing of the WGS84 CRS.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testParseGeographicCRS() throws FactoryException {
        /*
         * Following should have been tested by testFormatGeographicCRS()
         */
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final ReferencingBuilder builder = new ReferencingBuilder(metadata);
        builder.setCoordinateReferenceSystem(CommonCRS.WGS84.normalizedGeographic());
        /*
         * Following is the purpose of this test suite.
         */
        CoordinateReferenceSystem crs = builder.build();
        assertEquals(DefaultGeographicCRS.class, crs.getClass());
        GeodeticDatum datum = ((GeographicCRS) crs).getDatum();

        assertSame(CommonCRS.WGS84.normalizedGeographic(), crs);
        assertSame(CommonCRS.WGS84.datum(),          datum);
//      assertSame(DefaultEllipsoidalCS.GEODETIC_2D, builder.getCoordinateSystem(CoordinateSystem.class));
        assertSame(CommonCRS.WGS84.datum(),          builder.getDatum(Datum.class));

        builder.setIgnoreUserObject(true);
        crs = builder.build();
//      assertEquals(DefaultGeographicCRS.class, crs.getClass());
        datum = ((GeographicCRS) crs).getDatum();

        assertNotSame(CommonCRS.WGS84.normalizedGeographic(), crs);
        assertNotSame(PredefinedCS.GEODETIC_2D, builder.getCoordinateSystem(CoordinateSystem.class));
        assertNotSame(CommonCRS.WGS84.datum(),          builder.getDatum(Datum.class));

        assertEqualsIgnoreMetadata("PrimeMeridian", CommonCRS.WGS84.primeMeridian(),  datum.getPrimeMeridian());
        assertEqualsIgnoreMetadata("Ellipsoid",     CommonCRS.WGS84.ellipsoid(),      datum.getEllipsoid());
        assertEqualsIgnoreMetadata("Datum",         CommonCRS.WGS84.datum(),          datum);
        assertEqualsIgnoreMetadata("CS",            PredefinedCS.GEODETIC_2D,         crs.getCoordinateSystem());
        assertEqualsIgnoreMetadata("CRS",           CommonCRS.WGS84.normalizedGeographic(), crs);
    }

    /**
     * Tests the parsing of a Mercator CRS.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testParseProjectedCRS() throws FactoryException {
        /*
         * Following should have been tested by testFormatProjectedCRS()
         */
        final ProjectedCRS originalCRS = (ProjectedCRS) CRS.parseWKT(WKT.PROJCS_MERCATOR);
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final ReferencingBuilder builder = new ReferencingBuilder(metadata);
        builder.setCoordinateReferenceSystem(originalCRS);
        /*
         * Following is the purpose of this test suite.
         */
        CoordinateReferenceSystem crs = builder.build();
        assertEquals(DefaultProjectedCRS.class, crs.getClass());
        GeodeticDatum datum = ((ProjectedCRS) crs).getDatum();

        assertSame(originalCRS, crs);
        assertSame(originalCRS.getDatum(), datum);
        assertSame(originalCRS.getCoordinateSystem(), builder.getCoordinateSystem(CoordinateSystem.class));
        assertSame(originalCRS.getDatum(),            builder.getDatum(Datum.class));

        builder.setIgnoreUserObject(true);
        crs = builder.build();
        assertEquals(DefaultProjectedCRS.class, crs.getClass());
        datum = ((ProjectedCRS) crs).getDatum();

        assertNotSame(originalCRS, crs);
        assertNotSame(originalCRS.getCoordinateSystem(), builder.getCoordinateSystem(CoordinateSystem.class));
        assertNotSame(originalCRS.getDatum(),            builder.getDatum(Datum.class));

        assertEqualsIgnoreMetadata("PrimeMeridian", CommonCRS.WGS84.primeMeridian(), datum.getPrimeMeridian());
        assertEqualsIgnoreMetadata("Ellipsoid",     CommonCRS.WGS84.ellipsoid(),     datum.getEllipsoid());
        assertEqualsIgnoreMetadata("Datum",         CommonCRS.WGS84.datum(),         datum);
        assertEqualsIgnoreMetadata("CS",            PredefinedCS.PROJECTED, crs.getCoordinateSystem());
    }
}
