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
package org.geotoolkit.referencing;

import java.util.Set;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.OperationNotFoundException;

import org.geotoolkit.test.Depend;
import org.geotoolkit.test.referencing.WKT;
import org.geotoolkit.test.referencing.ReferencingTestBase;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.CoordinateReferenceSystemTest;
import org.geotoolkit.referencing.operation.transform.AbstractMathTransform;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.decodeQuotes;


/**
 * Tests the {@link CRS} class. This is actually an indirect way to test many referencing
 * service (WKT parsing, object comparisons, <i>etc.</i>).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Andrea Aime (OpenGeo)
 * @version 3.18
 *
 * @since 3.00
 */
@Depend(CoordinateReferenceSystemTest.class)
public final class CRS_Test extends ReferencingTestBase {
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
        assertSame(DefaultGeographicCRS.WGS84, CRS.decode("WGS84(DD)"));
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
        assertEquals("GCS_WGS_1984", IdentifiedObjects.getIdentifier(crs));
        assertNull(IdentifiedObjects.getIdentifier(crs, Citations.EPSG));
    }

    /**
     * Checks {@code "X"} is equated to {@code "Easting"} and {@code "Y"} to {@code "Northing"}.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
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
        /*
         * The above WKT will be parsed with "X" and "Y" axis names
         * by default. Now specifies explicitly different axis names.
         */
        final String wkt2 = decodeQuotes(wkt1.substring(0, wkt1.length()-1) + "," +
                "AXIS[“Easting”, EAST]," +
                "AXIS[“Northing”, NORTH]]");

        final CoordinateReferenceSystem crs1 = CRS.parseWKT(wkt1);
        final CoordinateReferenceSystem crs2 = CRS.parseWKT(wkt2);
        assertTrue(CRS.equalsIgnoreMetadata(crs1, crs2));
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
        final CoordinateReferenceSystem WGS84  = DefaultGeographicCRS.WGS84;
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

        assertEquals("NAD_1983_StatePlane_Massachusetts_Mainland_FIPS_2001", IdentifiedObjects.getIdentifier(crs1));

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

        assertEquals("EPSG:26986", IdentifiedObjects.getIdentifier(crs2));

        assertTrue(CRS.equalsApproximatively(crs1, crs2));
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
        final SingleCRS          crs2D = (SingleCRS) CRS.parseWKT(WKT.PROJCS_LAMBERT_CONIC_NTF);
        final DefaultCompoundCRS crs3D = new DefaultCompoundCRS("NTF 3D", crs2D, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT);
        final DefaultCompoundCRS crs4D = new DefaultCompoundCRS("NTF 4D", crs3D, DefaultTemporalCRS.MODIFIED_JULIAN);
        assertTrue (CRS.isHorizontalCRS(crs2D));
        assertFalse(CRS.isHorizontalCRS(crs3D));
        assertFalse(CRS.isHorizontalCRS(crs4D));
        assertSame(crs2D, CRS.getHorizontalCRS(crs2D));
        assertSame(crs2D, CRS.getHorizontalCRS(crs3D));
        assertSame(crs2D, CRS.getHorizontalCRS(crs4D));
        assertNull("No vertical component expected.",     CRS.getVerticalCRS(crs2D));
        assertSame(DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT, CRS.getVerticalCRS(crs3D));
        assertSame(DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT, CRS.getVerticalCRS(crs4D));
        assertNull("No temporal component expected.",     CRS.getTemporalCRS(crs2D));
        assertNull("No temporal component expected.",     CRS.getTemporalCRS(crs3D));
        assertSame(DefaultTemporalCRS.MODIFIED_JULIAN,    CRS.getTemporalCRS(crs4D));
        assertSame(crs3D, CRS.getCompoundCRS(crs3D, crs2D, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT));
        assertSame(crs3D, CRS.getCompoundCRS(crs4D, crs2D, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT));
        assertNull(       CRS.getCompoundCRS(crs3D, crs2D, DefaultTemporalCRS.MODIFIED_JULIAN));
        assertNull(       CRS.getCompoundCRS(crs4D, crs2D, DefaultTemporalCRS.MODIFIED_JULIAN));
        assertNull(       CRS.getCompoundCRS(crs3D, crs2D, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT, DefaultTemporalCRS.MODIFIED_JULIAN));
        assertSame(crs4D, CRS.getCompoundCRS(crs4D, crs2D, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT, DefaultTemporalCRS.MODIFIED_JULIAN));
        assertSame(crs4D, CRS.getCompoundCRS(crs4D, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT, DefaultTemporalCRS.MODIFIED_JULIAN, crs2D));
        assertNull(       CRS.getSubCRS(crs4D, 0, 1));
        assertSame(crs2D, CRS.getSubCRS(crs4D, 0, 2));
        assertSame(crs3D, CRS.getSubCRS(crs4D, 0, 3));
        assertSame(crs4D, CRS.getSubCRS(crs4D, 0, 4));
        assertSame(DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT, CRS.getSubCRS(crs4D, 2, 3));
        assertSame(DefaultTemporalCRS.MODIFIED_JULIAN,    CRS.getSubCRS(crs4D, 3, 4));
    }

    /**
     * Tests a request for a math transform which should succeed
     * only when datum shift are lenient.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testTransformationFailure() throws FactoryException, TransformException {
        final CoordinateReferenceSystem mapCRS = CRS.parseWKT(WKT.GEOGCS_WGS84_ALTERED);
        final CoordinateReferenceSystem WGS84  = DefaultGeographicCRS.WGS84;
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
     * Tests the transformations of an envelope.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testEnvelopeTransformation() throws FactoryException, TransformException {
        final CoordinateReferenceSystem mapCRS = CRS.parseWKT(WKT.PROJCS_UTM_10N);
        final CoordinateReferenceSystem WGS84  = DefaultGeographicCRS.WGS84;
        final MathTransform crsTransform = CRS.findMathTransform(WGS84, mapCRS, true);
        assertFalse(crsTransform.isIdentity());

        final GeneralEnvelope firstEnvelope, transformedEnvelope, oldEnvelope;
        firstEnvelope = new GeneralEnvelope(new double[] {-124, 42}, new double[] {-122, 43});
        firstEnvelope.setCoordinateReferenceSystem(WGS84);

        transformedEnvelope = CRS.transform(crsTransform, firstEnvelope);
        transformedEnvelope.setCoordinateReferenceSystem(mapCRS);

        oldEnvelope = CRS.transform(crsTransform.inverse(), transformedEnvelope);
        oldEnvelope.setCoordinateReferenceSystem(WGS84);

        assertTrue(oldEnvelope.contains(firstEnvelope, true));
        assertTrue(oldEnvelope.equals  (firstEnvelope, 0.02, true));
    }

    /**
     * Tests the transformations of a rectangle using a coordinate operation.
     * With assertions enabled, this also test the transformation of an envelope.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testTransformationOverPole() throws FactoryException, TransformException {
        final CoordinateReferenceSystem mapCRS = CRS.parseWKT(WKT.PROJCS_POLAR_STEREOGRAPHIC);
        final CoordinateReferenceSystem WGS84  = DefaultGeographicCRS.WGS84;
        final CoordinateOperation operation =
                CRS.getCoordinateOperationFactory(false).createOperation(mapCRS, WGS84);
        final MathTransform transform = operation.getMathTransform();
        assertTrue(transform instanceof MathTransform2D);
        /*
         * The rectangle to test, which contains the South pole.
         */
        Rectangle2D envelope = XRectangle2D.createFromExtremums(
                -3943612.4042124213, -4078471.954436003,
                 3729092.5890516187,  4033483.085688618);
        /*
         * This is what we get without special handling of singularity point.
         * Note that is doesn't include the South pole as we would expect.
         */
        Rectangle2D expected = XRectangle2D.createFromExtremums(
                -178.49352310409273, -88.99136583196398,
                 137.56220967463082, -40.905775004205864);
        /*
         * Tests what we actually get.
         */
        Rectangle2D actual = CRS.transform((MathTransform2D) transform, envelope, null);
        assertTrue(XRectangle2D.equalsEpsilon(expected, actual));
        /*
         * Using the transform(CoordinateOperation, ...) method,
         * the singularity at South pole is taken in account.
         */
        expected = XRectangle2D.createFromExtremums(-180, -90, 180, -40.905775004205864);
        actual = CRS.transform(operation, envelope, actual);
        assertTrue(XRectangle2D.equalsEpsilon(expected, actual));
        /*
         * The rectangle to test, which contains the South pole, but this time the south
         * pole is almost in a corner of the rectangle
         */
        envelope = XRectangle2D.createFromExtremums(-4000000, -4000000, 300000, 30000);
        expected = XRectangle2D.createFromExtremums(-180, -90, 180, -41.03163170198091);
        actual = CRS.transform(operation, envelope, actual);
        assertTrue(XRectangle2D.equalsEpsilon(expected, actual));
    }

    /**
     * Tests the transformations of an envelope from a 4D CRS to a 2D CRS
     * where the ordinates in one dimension are NaN.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testTransformation4to2D() throws TransformException {
        final CoordinateReferenceSystem crs = new DefaultCompoundCRS("4D CRS",
                DefaultGeographicCRS.WGS84,
                DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT,
                DefaultTemporalCRS.JAVA);

        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -170, 170);
        env.setRange(1, -80,   80);
        env.setRange(2, -50,  -50);
        env.setRange(3, Double.NaN, Double.NaN);
        assertFalse(env.isNull());
        assertTrue(env.isEmpty());
        final CoordinateReferenceSystem crs2D = CRSUtilities.getCRS2D(crs);
        assertSame(DefaultGeographicCRS.WGS84, crs2D);
        final Envelope env2D = CRS.transform(env, crs2D);
        /*
         * If the referencing framework has selected the CopyTransform implementation
         * as expected, then the envelope ordinates should not be NaN.
         */
        assertEquals(-170, env2D.getMinimum(0), 0);
        assertEquals( 170, env2D.getMaximum(0), 0);
        assertEquals( -80, env2D.getMinimum(1), 0);
        assertEquals(  80, env2D.getMaximum(1), 0);
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
            @Override protected void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff) {
                at.transform(srcPts, srcOff, dstPts, dstOff, 1);
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
