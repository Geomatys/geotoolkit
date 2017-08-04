/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Locale;
import javax.measure.IncommensurableException;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.util.FactoryException;

import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.apache.sis.referencing.datum.DefaultGeodeticDatum;
import org.apache.sis.referencing.IdentifiedObjects;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.TestBase;

import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.junit.*;
import static org.junit.Assume.assumeTrue;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.test.Commons.*;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import static java.util.Collections.singletonMap;


/**
 * Tests if the CRS utility class is functioning correctly when using EPSG database.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Andrea Aime (TOPP)
 * @version 3.16
 *
 * @since 2.4
 */
@DependsOn(CRS_Test.class)
public final strictfp class CRS_WithEpsgTest extends TestBase {
    /**
     * Ensures that the EPSG database is available. If no EPSG database is installed,
     * then the tests will be skipped. We do not cause a test failure because the EPSG
     * database is not expected to be installed when Geotk is built for the first time
     * on a new machine.
     */
    @Before
    public void ensureEpsgAvailable() {
        assumeTrue(false /*isEpsgFactoryAvailable()*/);
    }

    /**
     * Tests the (latitude, longitude) axis order for EPSG:4326.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testCorrectAxisOrder() throws FactoryException {
        final CoordinateReferenceSystem crs = CommonCRS.WGS84.geographic();
        assertEquals("EPSG:4326", IdentifiedObjects.getIdentifierOrName(crs));
        final CoordinateSystem cs = crs.getCoordinateSystem();
        assertEquals(2, cs.getDimension());

        CoordinateSystemAxis axis0 = cs.getAxis(0);
        assertEquals("Lat", axis0.getAbbreviation());

        CoordinateSystemAxis axis1 = cs.getAxis(1);
        assertEquals("Long", axis1.getAbbreviation());
    }

    /**
     * Tests the (longitude, latitude) axis order for EPSG:4326.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testForcedAxisOrder() throws FactoryException {
        final CoordinateReferenceSystem crs = AbstractCRS.castOrCopy(CommonCRS.WGS84.geographic()).forConvention(AxesConvention.RIGHT_HANDED);
        assertEquals("EPSG:4326", IdentifiedObjects.getIdentifierOrName(crs));
        final CoordinateSystem cs = crs.getCoordinateSystem();
        assertEquals(2, cs.getDimension());

        CoordinateSystemAxis axis0 = cs.getAxis(0);
        assertEquals("Long", axis0.getAbbreviation());

        CoordinateSystemAxis axis1 = cs.getAxis(1);
        assertEquals("Lat", axis1.getAbbreviation());

        assertNotDeepEquals(crs, CommonCRS.WGS84.geographic());     // Should not be (lon,lat) axis order.
    }

    /**
     * Tests again EPSG:4326, but forced to (longitude, latitude) axis order.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testSystemPropertyToForceXY() throws FactoryException {
        assertNull(Hints.getSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER));
        assertNull(Hints.putSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
        final CoordinateReferenceSystem crs;
        try {
            crs = CommonCRS.WGS84.geographic();

            final CoordinateSystem cs = crs.getCoordinateSystem();
            assertEquals(2, cs.getDimension());

            final CoordinateSystemAxis axis0 = cs.getAxis(0);
            assertEquals("forceXY did not work", "Long", axis0.getAbbreviation());

            final CoordinateSystemAxis axis1 = cs.getAxis(1);
            assertEquals("forceXY did not work", "Lat", axis1.getAbbreviation());
        } catch (AssertionError failure) {
            // A debugging help in case of test failure.
            System.err.println(">>> INFORMATION ON TEST FAILURE");
            throw failure;
        } finally {
            assertEquals(Boolean.TRUE, Hints.removeSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER));
        }
    }

    /**
     * Returns a ED50 CRS with the specified name.
     */
    private static CoordinateReferenceSystem getED50(final String name) throws FactoryException {
        final String wkt =
                "GEOGCS[\"" + name + "\",\n" +
                "  DATUM[\"European Datum 1950\",\n" +
                "  SPHEROID[\"International 1924\", 6378388.0, 297.0]],\n" +
                "PRIMEM[\"Greenwich\", 0.0],\n" +
                "UNIT[\"degree\", 0.017453292519943295]]";
        return CRS.fromWKT(wkt);
    }

    /**
     * Tests the {@link CRS#parseWKT} method.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testWKT() throws FactoryException {
        String wkt = "GEOGCS[\"WGS 84\",\n"
                   + "  DATUM[\"WGS_1984\",\n"
                   + "    SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],\n"
                   + "    TOWGS84[0,0,0,0,0,0,0], AUTHORITY[\"EPSG\",\"6326\"]],\n"
                   + "  PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],\n"
                   + "  UNIT[\"DMSH\",0.0174532925199433],\n"
                   + "  AXIS[\"Lat\",NORTH], AXIS[\"Long\",EAST],\n"
                   + "  AUTHORITY[\"EPSG\",\"4326\"]]";
        CoordinateReferenceSystem crs = CRS.fromWKT(wkt);
        assertNotNull(crs);
    }

    /**
     * Makes sure that the transform between two EPSG:4326 is the identity transform.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testFindMathTransformIdentity() throws FactoryException {
        CoordinateReferenceSystem crs1default = CommonCRS.WGS84.geographic();
        CoordinateReferenceSystem crs2default = CommonCRS.WGS84.geographic();
        MathTransform tDefault = CRS.findOperation(crs1default, crs2default, null).getMathTransform();
        assertTrue("WSG84 transformed to WSG84 should be Identity", tDefault.isIdentity());

        CoordinateReferenceSystem crs1force = AbstractCRS.castOrCopy(CommonCRS.WGS84.geographic()).forConvention(AxesConvention.RIGHT_HANDED);
        CoordinateReferenceSystem crs2force = AbstractCRS.castOrCopy(CommonCRS.WGS84.geographic()).forConvention(AxesConvention.RIGHT_HANDED);
        MathTransform tForce = CRS.findOperation(crs1force, crs2force, null).getMathTransform();
        assertTrue("WSG84 transformed to WSG84 should be Identity", tForce.isIdentity());
    }

    /**
     * Makes sure that the authority factory has the proper name.
     */
    @Test
    public void testAuthority() {
        CRSAuthorityFactory factory;
        Citation authority;

        // Tests the official factory.
        factory   = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", null);
        authority = factory.getAuthority();
        assertNotNull(authority);
        assertEquals("EPSG Geodetic Parameter Dataset", authority.getTitle().toString(Locale.US));
        assertTrue(org.apache.sis.metadata.iso.citation.Citations.identifierMatches(authority, "EPSG"));
    }

    /**
     * Tests "NAD83 / Massachusetts Mainland".
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void test26986() throws FactoryException {
        CoordinateReferenceSystem crs = CRS.forCode("epsg:26986");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("EPSG:26986", IdentifiedObjects.getIdentifierOrName(crs));
    }

    /**
     * Tests "AD27 / California zone II".
     * WFS requires this to work.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void test26742() throws FactoryException {
        CoordinateReferenceSystem crs = CRS.forCode("epsg:26742");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("EPSG:26742", IdentifiedObjects.getIdentifierOrName(crs));
    }

    /**
     * Tests "Popular Visualisation CRS / Mercator".
     *
     * @throws FactoryException Should not happen.
     *
     * @since 3.15
     *
     * @deprecated This is the legacy pseudo-Mercator, no longer in EPSG database.
     */
    @Test
    public void test3785() throws FactoryException {
        CoordinateReferenceSystem crs = CRS.forCode("epsg:3785");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("EPSG:3785", IdentifiedObjects.getIdentifierOrName(crs));
    }

    /**
     * Tests "WGS 84 / Pseudo-Mercator".
     * This is the "Google projection".
     *
     * @throws FactoryException Should not happen.
     *
     * @since 3.15
     */
    @Test
    public void test3857() throws FactoryException {
        CoordinateReferenceSystem crs = CRS.forCode("epsg:3857");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("EPSG:3857", IdentifiedObjects.getIdentifierOrName(crs));
    }

    /**
     * Tests {@link CRS#getHorizontalCRS} from a compound CRS.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testHorizontalFromCompound() throws FactoryException {
        // retrives "NTF (Paris) / France II + NGF Lallemand"
        CoordinateReferenceSystem compound = CRS.forCode("EPSG:7401");
        CoordinateReferenceSystem horizontal = CRS.getHorizontalComponent(compound);
        // compares with "NTF (Paris) / France II"
        assertEquals(CRS.forCode("EPSG:27582"), horizontal);
    }

    /**
     * Tests {@link CRS#getHorizontalCRS} from a Geographic 3D CR.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testHorizontalFromGeodetic() throws FactoryException {
        // retrives "WGS 84 (geographic 3D)"
        CoordinateReferenceSystem compound = CRS.forCode("EPSG:4327");
        CoordinateReferenceSystem horizontal = CRS.getHorizontalComponent(compound);
        // the horizonal version is basically 4326, but it won't compare positively with 4326,
        // not even using Utilities.equalsIgnoreMetadata(), so we check the axis directly.
        CoordinateSystem cs = horizontal.getCoordinateSystem();
        assertEquals(2, cs.getDimension());
        assertEquals(AxisDirection.NORTH, cs.getAxis(0).getDirection());
        assertEquals(AxisDirection.EAST,  cs.getAxis(1).getDirection());
    }

    /**
     * Tests the creation of a math transform from 4D to 3D CRS.
     *
     * @throws FactoryException Should not happen.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-81">GEOTK-81</a>
     */
    @Test
    @Ignore
    public void testProjected4D() throws FactoryException {
        CoordinateReferenceSystem targetCRS = CRS.forCode("EPSG:3395");
        CoordinateReferenceSystem sourceCRS = CRS.forCode("EPSG:27572");
        sourceCRS = new DefaultCompoundCRS(singletonMap(NAME_KEY, "3D"), sourceCRS, CommonCRS.Vertical.ELLIPSOIDAL.crs());
        sourceCRS = new DefaultCompoundCRS(singletonMap(NAME_KEY, "4D"), sourceCRS, CommonCRS.Temporal.JULIAN.crs());
        final MathTransform tr = CRS.findOperation(sourceCRS, targetCRS, null).getMathTransform();
        assertEquals(4, tr.getSourceDimensions());
        assertEquals(2, tr.getTargetDimensions());
    }

    /**
     * Tests the conversion from {@code EPSG:4979} to {@code EPSG:4326}.
     * Note that {@code EPSG:4979} is the replacement of {@code EPSG:4327}
     * with degrees units instead of DMS.
     *
     * @throws FactoryException Should never happen.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-65">GEOTK-65</a>
     */
    @Test
    public void testGeographic3D_to_2D() throws FactoryException {
        CoordinateReferenceSystem sourceCRS = CRS.forCode("EPSG:4327");
        CoordinateReferenceSystem targetCRS = CommonCRS.WGS84.geographic();
        MathTransform tr;
        try {
            CRS.findOperation(sourceCRS, targetCRS, null).getMathTransform();
            fail("No conversion from EPSG:4327 to EPSG:4326 should be allowed because the units " +
                 "conversion from DMS to degrees is not linear. Note that this exception may be " +
                 "removed in a future version if we implement non-linear unit conversions.");
        } catch (OperationNotFoundException e) {
            assertTrue("The operation should have failed because of a unit conversion error.",
                    e.getCause() instanceof IncommensurableException);
        }
        sourceCRS = CommonCRS.WGS84.geographic3D();
        tr = CRS.findOperation(sourceCRS, targetCRS, null).getMathTransform();
        assertEquals(3, tr.getSourceDimensions());
        assertEquals(2, tr.getTargetDimensions());
        assertDiagonalMatrix(tr, true, 1, 1, 0);
    }

    /**
     * Tests the conversion from {@code CompoundCRS[EPSG:3035 + Sigma-level]} to {@code EPSG:4326}.
     * The interesting part in this test is that the height is not a standard height, and the
     * referencing module is not supposed to known how to build a 3D Geographic CRS (needed as
     * an intermediate step for the datum shift) with that height.
     *
     * @throws FactoryException Should never happen.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-71">GEOTK-71</a>
     */
    @Test
    @Ignore("JSR-275 does not accept unit named 'level'.")
    public void testProjected3D_to_2D() throws FactoryException {
        CoordinateReferenceSystem targetCRS = CommonCRS.WGS84.geographic();
        CoordinateReferenceSystem sourceCRS = CRS.forCode("EPSG:3035");
        GeodeticDatum targetDatum = ((GeographicCRS) targetCRS).getDatum();
        GeodeticDatum sourceDatum =  ((ProjectedCRS) sourceCRS).getDatum();
        final BursaWolfParameters[] params = ((DefaultGeodeticDatum) sourceDatum).getBursaWolfParameters();
        assertEquals("This test requires that an explicit BursaWolf parameter exists.", 1, params.length);
        assertEquals("targetDatum", targetDatum, params[0].getTargetDatum());
        assertTrue("This test requires that the BursaWolf parameter is set to identity.", params[0].isIdentity());

        CoordinateReferenceSystem vertCRS = CRS.fromWKT(
                "VERT_CS[\"Sigma Level\",VERT_DATUM[\"Sigma Level\",2000],UNIT[\"level\",1.0],AXIS[\"Sigma Level\",DOWN]]");
        sourceCRS = new DefaultCompoundCRS(singletonMap(NAME_KEY, "ETRS89 + Sigma level"), sourceCRS, vertCRS);
        final MathTransform tr = CRS.findOperation(sourceCRS, targetCRS, null).getMathTransform();
        assertSame(tr, CRS.findOperation(sourceCRS, targetCRS, null).getMathTransform());
        assertSame(tr, CRS.findOperation(sourceCRS, targetCRS, null).getMathTransform());
        assertEquals(3, tr.getSourceDimensions());
        assertEquals(2, tr.getTargetDimensions());
    }

    /**
     * Tests {@link CRS#findMathTransform(CoordinateReferenceSystem, CoordinateReferenceSystem,
     * GeographicBoundingBox, boolean)}.
     *
     * @throws FactoryException Should never happen.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-80">GEOTK-80</a>
     */
    @Test
    @Ignore
    public void testCRSWithGeographicArea() throws FactoryException {
        final CoordinateReferenceSystem sourceCRS = CRS.forCode("EPSG:4267"); // NAD27
        final CoordinateReferenceSystem targetCRS = CommonCRS.WGS84.geographic(); // WGS84
        final DefaultGeographicBoundingBox box = new DefaultGeographicBoundingBox(-91.64, -88.09, 30.02, 35.00); // Mississipi (EPSG:1393)
        final MathTransform mt = CRS.findOperation(sourceCRS, targetCRS, box).getMathTransform();
        /*
         * We expect "NAD27 to WGS 84 (56)" (EPSG:8609). Since the CoordinateOperation is lost at this stagen
         * we can not test the identifier code. So we will test the MathTransform WKT. In particular, we look
         * for the "mshpgn.las" and "mshpgn.los" parameter value as an indication of Mississipi data.
         */
        assertMultilinesEquals(decodeQuotes(
            "CONCAT_MT[PARAM_MT[“Affine”, \n" +
            "    PARAMETER[“num_row”, 3], \n" +
            "    PARAMETER[“num_col”, 3], \n" +
            "    PARAMETER[“elt_0_0”, 0.0], \n" +
            "    PARAMETER[“elt_0_1”, 1.0], \n" +
            "    PARAMETER[“elt_1_0”, 1.0], \n" +
            "    PARAMETER[“elt_1_1”, 0.0]], \n" +
            "  PARAM_MT[“NADCON”, \n" +
            "    PARAMETER[“Latitude difference file”, “conus.las”], \n" +
            "    PARAMETER[“Longitude difference file”, “conus.los”]], \n" +
            "  PARAM_MT[“NADCON”, \n" +
            "    PARAMETER[“Latitude difference file”, “mshpgn.las”], \n" +
            "    PARAMETER[“Longitude difference file”, “mshpgn.los”]], \n" +
            "  PARAM_MT[“Affine”, \n" +
            "    PARAMETER[“num_row”, 3], \n" +
            "    PARAMETER[“num_col”, 3], \n" +
            "    PARAMETER[“elt_0_0”, 0.0], \n" +
            "    PARAMETER[“elt_0_1”, 1.0], \n" +
            "    PARAMETER[“elt_1_0”, 1.0], \n" +
            "    PARAMETER[“elt_1_1”, 0.0]]]"),
            mt.toWKT());
    }
}
