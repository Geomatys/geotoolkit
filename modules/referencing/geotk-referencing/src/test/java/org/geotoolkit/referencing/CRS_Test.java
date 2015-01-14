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
package org.geotoolkit.referencing;

import java.util.Set;
import java.awt.geom.AffineTransform;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.OperationNotFoundException;

import org.geotoolkit.test.referencing.WKT;
import org.geotoolkit.test.referencing.ReferencingTestBase;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.AbstractMathTransform;
import org.apache.sis.referencing.IdentifiedObjects;

import org.junit.*;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.test.Commons.decodeQuotes;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import static java.util.Collections.singletonMap;


/**
 * Tests the {@link CRS} class. This is actually an indirect way to test many referencing
 * service (WKT parsing, object comparisons, <i>etc.</i>).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Andrea Aime (OpenGeo)
 * @version 3.19
 *
 * @since 3.00
 */
public final strictfp class CRS_Test extends ReferencingTestBase {
    /**
     * Tests the {@link CRS#getSupportedAuthorities} method.
     */
    @Test
    public void testSupportedAuthorities() {
        final Set<String> withoutAlias = CRS.getSupportedAuthorities(false);
        assertTrue (withoutAlias.contains("CRS"));
        assertTrue (withoutAlias.contains("AUTO2"));
        assertTrue (withoutAlias.contains("urn:ogc:def"));
        assertTrue (withoutAlias.contains("http://www.opengis.net"));
        assertFalse(withoutAlias.contains("AUTO"));
        assertFalse(withoutAlias.contains("urn:x-ogc:def"));

        final Set<String> withAlias = CRS.getSupportedAuthorities(true);
        assertTrue (withAlias.containsAll(withoutAlias));
        assertFalse(withoutAlias.containsAll(withAlias));
        assertTrue (withAlias.contains("AUTO"));
        assertTrue (withAlias.contains("urn:x-ogc:def"));
    }

    /**
     * Tests simple decode.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testDecode() throws FactoryException {
        assertSame(CommonCRS.WGS84.normalizedGeographic(), CRS.decode("WGS84(DD)"));
    }

    /**
     * Tests simple WKT parsing.
     *
     * @throws FactoryException Should not happen.
     *
     * @since 3.14
     */
    @Test
    public void testParseWKT() throws FactoryException {
        final CoordinateReferenceSystem crs = CRS.parseWKT("GEOGCS[\"GCS_WGS_1984\","
                + "DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137,298.257223563]],"
                + "PRIMEM[\"Greenwich\",0],UNIT[\"Degree\",0.0174532925199433]]");
        assertTrue(crs instanceof DefaultGeographicCRS);
        assertEquals("GCS_WGS_1984", crs.getName().getCode());
        assertEquals("GCS_WGS_1984", IdentifiedObjects.getIdentifierOrName(crs));
        assertNull(IdentifiedObjects.getIdentifier(crs, Citations.EPSG));
    }

    /**
     * Checks {@code "X"} is equated to {@code "Easting"} and {@code "Y"} to {@code "Northing"}.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    @Ignore
    public void testAxisAliases() throws FactoryException {
        final String wkt1 = decodeQuotes(
            "PROJCS[“NAD_1927_Texas_Statewide_Mapping_System”," +
            "GEOGCS[“GCS_North_American_1927”," +
            "DATUM[“D_North_American_1927”," +
            "SPHEROID[“Clarke_1866”,6378206.4,294.9786982]]," +
            "PRIMEM[“Greenwich”,0.0],UNIT[“Degree”,0.0174532925199433]]," +
            "PROJECTION[“Lambert_Conformal_Conic\"]," +
            "PARAMETER[“False_Easting”,3000000.0]," +
            "PARAMETER[“False_Northing”,3000000.0]," +
            "PARAMETER[“Central_Meridian”,-100.0]," +
            "PARAMETER[“Standard_Parallel_1”,27.416666666666668]," +
            "PARAMETER[“Standard_Parallel_2”,34.916666666666664]," +
            "PARAMETER[“Latitude_Of_Origin”,31.166666666666668]," +
            "UNIT[“Foot”,0.3048]]");

        // The above WKT will be parsed with "X" and "Y" axis names
        // by default. Now specifies explicitly different axis names.
        final String wkt2 = decodeQuotes(wkt1.substring(0, wkt1.length()-1) + "," +
                "AXIS[“Easting”, EAST]," +
                "AXIS[“Northing”, NORTH]]");
        assertEqualsIgnoreMetadata(CRS.parseWKT(wkt1), CRS.parseWKT(wkt2), false);
    }

    /**
     * Tests an ESRI code.
     *
     * @throws Exception Should never happen.
     *
     * @todo Not yet working.
     */
    @Test
    @Ignore
    public void testESRICode() throws Exception {
        String wkt = decodeQuotes(
            "PROJCS[“Albers_Conic_Equal_Area”,\n" +
             "  GEOGCS[“GCS_North_American_1983”,\n" +
             "    DATUM[“D_North_American_1983”,\n" +
             "    SPHEROID[“GRS_1980”,6378137.0,298.257222101]],\n" +
             "    PRIMEM[“Greenwich”,0.0],\n" +
             "    UNIT[“Degree”,0.0174532925199433]],\n" +
             "  PROJECTION[“Equidistant_Conic\"],\n" +
             "  PARAMETER[“False_Easting”,0.0],\n" +
             "  PARAMETER[“False_Northing”,0.0],\n" +
             "  PARAMETER[“Central_Meridian”,-96.0],\n" +
             "  PARAMETER[“Standard_Parallel_1”,33.0],\n" +
             "  PARAMETER[“Standard_Parallel_2”,45.0],\n" +
             "  PARAMETER[“Latitude_Of_Origin”,39.0],\n" +
             "  UNIT[“Meter”,1.0]]");
        CoordinateReferenceSystem crs = CRS.parseWKT(wkt);
        final CoordinateReferenceSystem WGS84 = CommonCRS.WGS84.normalizedGeographic();
        final MathTransform crsTransform = CRS.findMathTransform(WGS84, crs, true);
        assertFalse(crsTransform.isIdentity());
    }

    /**
     * Tests a reported issue. Actually, this exception is currently the expected one.
     *
     * @throws FactoryException The expected exception.
     *
     * @see <a href="https://jira.codehaus.org/browse/GEOT-1660">GEOT-1660</a>
     */
    @Test(expected = FactoryException.class)
    public void testGEOT1660() throws FactoryException {
        String wkt = decodeQuotes(
            "PROJCS[“Custom”,\n" +
            "  GEOGCS[“GCS_North_American_1983”,\n" +
            "    DATUM[“D_North_American_1983”,\n" +
            "      SPHEROID[“GRS_1980”,6378137.0,298.257222101]],\n" +
            "    PRIMEM[“Greenwich”,0.0],\n" +
            "    UNIT[“Degree”,0.0174532925199433]],\n" +
            "  PROJECTION[“Lambert_Conformal_Conic”],\n" +
            "  PARAMETER[“False_Easting”,1312335.958],\n" +
            "  PARAMETER[“False_Northing”,0.0],\n" +
            "  PARAMETER[“Central_Meridian”,-120.5],\n" +
            "  PARAMETER[“Standard_Parallel_1”,43.0],\n" +
            "  PARAMETER[“Standard_Parallel_2”,45.5],\n" +
            "  PARAMETER[“Central_Parallel”,41.75],\n" +
            "  UNIT[“Foot”,0.3048]]");
        CRS.parseWKT(wkt);
    }

    /**
     * Ensures that parsing a WKT with wrong units throws an exception.
     *
     * @throws FactoryException The expected exception.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-86">GEOTK-86</a>
     * @todo Not yet fixed.
     */
    @Ignore
    @Test(expected = FactoryException.class)
    public void testGEOTK86() throws FactoryException {
        String wkt = decodeQuotes(
                "GEOGCS[“NAD83”,\n" +
                "  DATUM[“North_American_Datum_1983”,\n" +
                "    SPHEROID[“GRS 1980”, 6378137.0, 298.257222]],\n" +
                "  PRIMEM[“Greenwich”, 0.0],\n" +
                "  UNIT[“m”, 1.0]]");
        CRS.parseWKT(wkt);
    }

    /**
     * Tests the comparisons of two objects which should be equivalent despite their
     * different representation of the math transform.
     *
     * @throws FactoryException Should never happen.
     *
     * @see <a href="http://jira.codehaus.org/browse/GEOT-1268">GEOT-1268</a>
     */
    @Test
    @Ignore
    public void testEqualsApproximatively() throws FactoryException {
        final CoordinateReferenceSystem crs1 = CRS.parseWKT(decodeQuotes(
                "PROJCS[“NAD_1983_StatePlane_Massachusetts_Mainland_FIPS_2001”, \n" +
                "  GEOGCS[“GCS_North_American_1983”, \n" +
                "    DATUM[“D_North_American_1983”, \n" +
                "      SPHEROID[“GRS_1980”, 6378137.0, 298.257222101]], \n" +
                "    PRIMEM[“Greenwich”, 0.0], \n" +
                "    UNIT[“degree”, 0.017453292519943295], \n" +
                "    AXIS[“Longitude”, EAST], \n" +
                "    AXIS[“Latitude”, NORTH]], \n" +
                "  PROJECTION[“Lambert_Conformal_Conic”], \n" +
                "  PARAMETER[“central_meridian”, -71.5], \n" +
                "  PARAMETER[“latitude_of_origin”, 41.0], \n" +
                "  PARAMETER[“standard_parallel_1”, 41.7166666666666667], \n" +
                "  PARAMETER[“scale_factor”, 1.0], \n" +
                "  PARAMETER[“false_easting”, 200000.0], \n" +
                "  PARAMETER[“false_northing”, 750000.0], \n" +
                "  PARAMETER[“standard_parallel_2”, 42.6833333333333333], \n" +
                "  UNIT[“m”, 1.0], \n" +
                "  AXIS[“x”, EAST], \n" +
                "  AXIS[“y”, NORTH]]"));

        assertEquals("NAD_1983_StatePlane_Massachusetts_Mainland_FIPS_2001", IdentifiedObjects.getIdentifierOrName(crs1));

        final CoordinateReferenceSystem crs2 = CRS.parseWKT(decodeQuotes(
                "PROJCS[“NAD83 / Massachusetts Mainland”, \n" +
                "  GEOGCS[“NAD83”, \n" +
                "    DATUM[“North American Datum 1983”, \n" +
                "      SPHEROID[“GRS 1980”, 6378137.0, 298.257222101, AUTHORITY[“EPSG”,“7019”]], \n" +
                "      TOWGS84[1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0], \n" +
                "      AUTHORITY[“EPSG”,“6269”]], \n" +
                "    PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”,“8901”]], \n" +
                "    UNIT[“degree”, 0.017453292519943295], \n" +
                "    AXIS[“Geodetic longitude”, EAST], \n" +
                "    AXIS[“Geodetic latitude”, NORTH], \n" +
                "    AUTHORITY[“EPSG”,“4269”]], \n" +
                "  PROJECTION[“Lambert Conic Conformal (2SP)”, AUTHORITY[“EPSG”,“9802”]], \n" +
                "  PARAMETER[“central_meridian”, -71.5], \n" +
                "  PARAMETER[“latitude_of_origin”, 41.0], \n" +
                "  PARAMETER[“standard_parallel_1”, 42.6833333333333333], \n" +
                "  PARAMETER[“false_easting”, 200000.0], \n" +
                "  PARAMETER[“false_northing”, 750000.0], \n" +
                "  PARAMETER[“standard_parallel_2”, 41.7166666666666667], \n" +
                "  UNIT[“m”, 1.0], \n" +
                "  AXIS[“Easting”, EAST], \n" +
                "  AXIS[“Northing”, NORTH], \n" +
                "  AUTHORITY[“EPSG”,“26986”]]"));

        assertEquals("EPSG:26986", IdentifiedObjects.getIdentifierOrName(crs2));
        assertEqualsApproximatively(crs1, crs2, false);
    }

    /**
     * Tests the extraction of components from a {@link CompoundCRS}.
     *
     * @throws FactoryException Should never happen.
     *
     * @since 3.16
     */
    @Test
    public void testComponentCRS() throws FactoryException {
        final VerticalCRS ELLIPSOIDAL_HEIGHT = CommonCRS.Vertical.ELLIPSOIDAL.crs();
        final TemporalCRS MODIFIED_JULIAN = CommonCRS.Temporal.MODIFIED_JULIAN.crs();

        final SingleCRS          crs2D = (SingleCRS) CRS.parseWKT(WKT.PROJCS_LAMBERT_CONIC_NTF);
        final DefaultCompoundCRS crs3D = new DefaultCompoundCRS(singletonMap(NAME_KEY, "NTF 3D"), crs2D, ELLIPSOIDAL_HEIGHT);
        final DefaultCompoundCRS crs4D = new DefaultCompoundCRS(singletonMap(NAME_KEY, "NTF 4D"), crs3D, MODIFIED_JULIAN);
        assertTrue (CRS.isHorizontalCRS(crs2D));
        assertFalse(CRS.isHorizontalCRS(crs3D));
        assertFalse(CRS.isHorizontalCRS(crs4D));
        assertSame(crs2D, org.apache.sis.referencing.CRS.getHorizontalComponent(crs2D));
        assertSame(crs2D, org.apache.sis.referencing.CRS.getHorizontalComponent(crs3D));
        assertSame(crs2D, org.apache.sis.referencing.CRS.getHorizontalComponent(crs4D));
        assertNull("No vertical component expected.",     org.apache.sis.referencing.CRS.getVerticalComponent(crs2D, true));
        assertSame(ELLIPSOIDAL_HEIGHT, org.apache.sis.referencing.CRS.getVerticalComponent(crs3D, true));
        assertSame(ELLIPSOIDAL_HEIGHT, org.apache.sis.referencing.CRS.getVerticalComponent(crs4D, true));
        assertNull("No temporal component expected.",     org.apache.sis.referencing.CRS.getTemporalComponent(crs2D));
        assertNull("No temporal component expected.",     org.apache.sis.referencing.CRS.getTemporalComponent(crs3D));
        assertSame(MODIFIED_JULIAN,    org.apache.sis.referencing.CRS.getTemporalComponent(crs4D));
        assertSame(crs3D, CRS.getCompoundCRS(crs3D, crs2D, ELLIPSOIDAL_HEIGHT));
        assertSame(crs3D, CRS.getCompoundCRS(crs4D, crs2D, ELLIPSOIDAL_HEIGHT));
        assertNull(       CRS.getCompoundCRS(crs3D, crs2D, MODIFIED_JULIAN));
        assertNull(       CRS.getCompoundCRS(crs4D, crs2D, MODIFIED_JULIAN));
        assertNull(       CRS.getCompoundCRS(crs3D, crs2D, ELLIPSOIDAL_HEIGHT, MODIFIED_JULIAN));
        assertSame(crs4D, CRS.getCompoundCRS(crs4D, crs2D, ELLIPSOIDAL_HEIGHT, MODIFIED_JULIAN));
        assertSame(crs4D, CRS.getCompoundCRS(crs4D, ELLIPSOIDAL_HEIGHT, MODIFIED_JULIAN, crs2D));
    }

    /**
     * Tests a request for a math transform which should succeed
     * only when datum shift are lenient.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    @Ignore("Exception for missing Bursa-Wolf parameters is disabled for now.")
    public void testTransformationFailure() throws FactoryException, TransformException {
        final CoordinateReferenceSystem mapCRS = CRS.parseWKT(WKT.GEOGCS_WGS84_ALTERED);
        final CoordinateReferenceSystem WGS84  = CommonCRS.WGS84.normalizedGeographic();
        final MathTransform crsTransform = CRS.findMathTransform(WGS84, mapCRS, true);
        assertTrue(crsTransform.isIdentity());
        try {
            CRS.findMathTransform(WGS84, mapCRS, false);
            fail("Should not have found the transform, because the datum are not equivalent.");
        } catch (OperationNotFoundException e) {
            // This is the expected exception.
        }
    }

    /**
     * Tests {@link CRS#deltaTransform}
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testDeltaTransform() throws TransformException {
        /*
         * Computes the point to be used as a reference.
         */
        final AffineTransform at = new AffineTransform();
        at.translate(-200, 300);
        at.scale(4, 6);
        at.rotate(1.5);
        final double[] vector = new double[] {4, 7};
        final double[] expected = new double[2];
        at.deltaTransform(vector, 0, expected, 0, 1);
        /*
         * Computes the same delta using the CRS.deltaTransform(...) method. We need a custom
         * class that doesn't extends AffineTransform, otherwise CRS.deltaTransform would select
         * its optimized path which doesn't really test the code we want to test.
         */
        final class TestTransform extends AbstractMathTransform {
            @Override public int getSourceDimensions() {return 2;}
            @Override public int getTargetDimensions() {return 2;}
            @Override public Matrix transform(
                    final double[] srcPts, final int srcOff,
                    final double[] dstPts, final int dstOff,
                    final boolean derivate)
            {
                at.transform(srcPts, srcOff, dstPts, dstOff, 1);
                return null;
            }
        }
        final TestTransform tr = new TestTransform();
        final DirectPosition2D origin = new DirectPosition2D(80, -20);
        final double[] result = CRS.deltaTransform(tr, origin, vector);
        assertEquals(expected.length, result.length);
        for (int i=0; i<expected.length; i++) {
            assertEquals(expected[i], result[i], 1E-10);
        }
    }

    /**
     * Tests a few CRS from the IGNF authority.
     *
     * @throws FactoryException Should never happen.
     *
     * @since 3.14
     */
    @Test
    public void testIGNF() throws FactoryException {
        final CoordinateReferenceSystem crs = CRS.decode("IGNF:MILLER");
        assertTrue(crs instanceof ProjectedCRS);
    }
}
