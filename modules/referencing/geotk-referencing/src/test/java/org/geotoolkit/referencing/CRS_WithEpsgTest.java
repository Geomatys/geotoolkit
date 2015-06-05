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

import java.util.Set;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.sql.Connection;
import java.sql.SQLException;
import javax.measure.converter.ConversionException;

import org.opengis.geometry.Envelope;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.AuthorityFactory;
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

import org.apache.sis.util.Version;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.apache.sis.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory;
import org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory;
import org.geotoolkit.referencing.factory.FallbackAuthorityFactory;
import org.geotoolkit.referencing.factory.OrderedAxisAuthorityFactory;
import org.geotoolkit.referencing.factory.OrderedAxisAuthorityFactoryTest;
import org.apache.sis.referencing.IdentifiedObjects;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;
import static org.junit.Assume.assumeTrue;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.Commons.*;
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
public final strictfp class CRS_WithEpsgTest extends ReferencingTestBase {
    /**
     * Ensures that the EPSG database is available. If no EPSG database is installed,
     * then the tests will be skipped. We do not cause a test failure because the EPSG
     * database is not expected to be installed when Geotk is built for the first time
     * on a new machine.
     */
    @Before
    public void ensureEpsgAvailable() {
        assumeTrue(isEpsgFactoryAvailable());
    }

    /**
     * Tests the (latitude, longitude) axis order for EPSG:4326.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testCorrectAxisOrder() throws FactoryException {
        final CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
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
        final CoordinateReferenceSystem crs = CRS.decode("EPSG:4326", true);
        assertEquals("EPSG:4326", IdentifiedObjects.getIdentifierOrName(crs));
        final CoordinateSystem cs = crs.getCoordinateSystem();
        assertEquals(2, cs.getDimension());

        CoordinateSystemAxis axis0 = cs.getAxis(0);
        assertEquals("Long", axis0.getAbbreviation());

        CoordinateSystemAxis axis1 = cs.getAxis(1);
        assertEquals("Lat", axis1.getAbbreviation());

        assertNotDeepEquals(crs, CRS.decode("EPSG:4326")); // Should not be (lon,lat) axis order.
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
            crs = CRS.decode("EPSG:4326");

            final CoordinateSystem cs = crs.getCoordinateSystem();
            assertEquals(2, cs.getDimension());

            final CoordinateSystemAxis axis0 = cs.getAxis(0);
            assertEquals("forceXY did not work", "Long", axis0.getAbbreviation());

            final CoordinateSystemAxis axis1 = cs.getAxis(1);
            assertEquals("forceXY did not work", "Lat", axis1.getAbbreviation());
        } catch (AssertionError failure) {
            // A debugging help in case of test failure.
            System.err.println(">>> INFORMATION ON TEST FAILURE");
            OrderedAxisAuthorityFactoryTest.printCurrentFactoryList();
            throw failure;
        } finally {
            assertEquals(Boolean.TRUE, Hints.removeSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER));
        }
    }

    /**
     * Tests EPSG:3035 from with various system hints. In some cases we will attempt to fetch
     * from the EPSG database, and in other cases from the {@code epsg.properties} file.
     *
     * @throws FactoryException Should never happen.
     *
     * @since 3.05
     */
    @Test
    public void testSystemPropertyToFactoryKind() throws FactoryException {
        final AtomicBoolean askedConnection = new AtomicBoolean();
        final DefaultDataSource dummy = new DefaultDataSource("jdbc:dummy") {
            @Override public Connection getConnection() throws SQLException {
                askedConnection.set(true);
                return super.getConnection();
            }

            @Override public Connection getConnection(String username, String password) throws SQLException {
                askedConnection.set(true);
                return super.getConnection(username, password);
            }
        };
        /*
         * The following loop is executed four time. The first execution uses the default
         * configuration, which includes both factories.  The second and third executions
         * ask only for the factory backed by the properties file.  The last execution is
         * back to the initial state.
         */
        try {
            for (int stage=0; stage<4; stage++) {
                askedConnection.set(false);
                boolean foundDatabase   = false;
                boolean foundProperties = false;
                boolean foundFallback   = false;
                for (final AuthorityFactory factory : ((DefaultAuthorityFactory)
                        CRS.getAuthorityFactory(false)).backingStore.getFactories())
                {
                    foundDatabase   |= (factory instanceof ThreadedEpsgFactory);
                    foundProperties |= (factory instanceof PropertyEpsgFactory);
                    foundFallback   |= (factory instanceof FallbackAuthorityFactory);
                }
                assertFalse("Should never found ThreadedEpsgFactory alone. If this factory is available, then it " +
                        "should be together with PropertyEpsgFactory in a FallbackAuthorityFactory.", foundDatabase);
                switch (stage) {
                    /*
                     * Tests to perform when PropertyEpsgFactory is alone. The other
                     * factory (ThreadedEpsgFactory) has been excluded either directly
                     * (case 1), or indirectly through an invalid JDBC URL (case 2).
                     */
                    case 1:
                    case 2: {
                        assertTrue ("PropertyEpsgFactory should be available directly.", foundProperties);
                        assertFalse("Expected no fallback, only PropertyEpsgFactory.",   foundFallback);
                        break;
                    }
                    /*
                     * Tests to perform when both PropertyEpsgFactory and ThreadedEpsgFactory
                     * are available. They should form a FallbackAuthorityFactory chain.
                     */
                    case 0:
                    case 3: {
                        assertFalse("Should not found PropertyEpsgFactory alone, since it should be " +
                                "part of FallbackAuthorityFactory.", foundProperties);
                        assertTrue("Expected a FallbackAuthorityFactory containing both " +
                                "ThreadedEpsgFactory and PropertyEpsgFactory", foundFallback);
                        break;
                    }
                }
                assertEquals("Should have attempted to get a connection from the dummy database.",
                        (stage == 2), askedConnection.get());
                /*
                 * Gets the EPSG:3035 CRS object. The EPSG code should be available even if not
                 * explicitly declared in the properties file, because it should have been added
                 * automatically by PropertyEpsgFactory when needed.
                 */
                final CoordinateReferenceSystem crs = CRS.decode("EPSG:3035");
                assertEquals("3035", IdentifiedObjects.getIdentifier(crs, Citations.EPSG).getCode());
                /*
                 * Modifies the configuration depending on the next stage to be tested.
                 */
                switch (stage) {
                    case 0: {
                        assertNull(Hints.putSystemDefault(Hints.CRS_AUTHORITY_FACTORY, PropertyEpsgFactory.class));
                        break;
                    }
                    case 1: {
                        assertEquals(PropertyEpsgFactory.class, Hints.removeSystemDefault(Hints.CRS_AUTHORITY_FACTORY));
                        assertNull(Hints.putSystemDefault(Hints.EPSG_DATA_SOURCE, dummy));
                        break;
                    }
                    case 2: {
                        assertEquals(dummy, Hints.removeSystemDefault(Hints.EPSG_DATA_SOURCE));
                        break;
                    }
                }
            }
        } finally {
            // In case of failure, make sure that we restore the system in its expected state.
            Hints.removeSystemDefault(Hints.CRS_AUTHORITY_FACTORY);
            Hints.removeSystemDefault(Hints.EPSG_DATA_SOURCE);
        }
    }

    /**
     * Tests {@link IdentifiedObjects#lookupIdentifier}.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    @Ignore
    public void testLookupIdentifier() throws FactoryException {
        CoordinateReferenceSystem crs = getED50("ED50");
        assertEquals("Should find without scan thanks to the name.", "EPSG:4230",
                     org.geotoolkit.referencing.IdentifiedObjects.lookupIdentifier(crs, false));
        assertEquals(Integer.valueOf(4230), org.geotoolkit.referencing.IdentifiedObjects.lookupEpsgCode(crs, false));

        crs = getED50("ED50 with unknown name");
        assertNull("Should not find the CRS without a scan.",
                   org.geotoolkit.referencing.IdentifiedObjects.lookupIdentifier(crs, false));
        assertEquals(null, org.geotoolkit.referencing.IdentifiedObjects.lookupEpsgCode(crs, false));

        assertEquals("With scan allowed, should find the CRS.", "EPSG:4230",
                     org.geotoolkit.referencing.IdentifiedObjects.lookupIdentifier(crs, true));
        assertEquals(Integer.valueOf(4230), org.geotoolkit.referencing.IdentifiedObjects.lookupEpsgCode(crs, true));
    }

    /**
     * Tests {@link IdentifiedObjects#lookupIdentifier} in the URN namespace.
     * Also test the HTTP namespace by opportunity.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testLookupIdentifierWithURN() throws FactoryException {
        final Version version = CRS.getVersion("EPSG");
        final CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        assertEquals("http://www.opengis.net/gml/srs/epsg.xml#4326",
                org.geotoolkit.referencing.IdentifiedObjects.lookupIdentifier(Citations.HTTP_OGC, crs, false));
        assertEquals("NOTE: This test assumes that the EPSG database version " + EPSG_VERSION +
                " is used. It should be the case if the embedded database is used (geotk-epsg)." +
                " If that module is upgrated with a newer version of the EPSG database, please" +
                " update this test.",
                "urn:ogc:def:crs:epsg:" + (version != null ? version : EPSG_VERSION) + ":4326",
                org.geotoolkit.referencing.IdentifiedObjects.lookupIdentifier(Citations.URN_OGC, crs, false));
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
        return CRS.parseWKT(wkt);
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
                   + "  UNIT[\"DMSH\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9108\"]],\n"
                   + "  AXIS[\"Lat\",NORTH], AXIS[\"Long\",EAST],\n"
                   + "  AUTHORITY[\"EPSG\",\"4326\"]]";
        CoordinateReferenceSystem crs = CRS.parseWKT(wkt);
        assertNotNull(crs);
    }

    /**
     * Makes sure that the transform between two EPSG:4326 is the identity transform.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testFindMathTransformIdentity() throws FactoryException {
        CoordinateReferenceSystem crs1default = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem crs2default = CRS.decode("EPSG:4326");
        MathTransform tDefault = CRS.findMathTransform(crs1default, crs2default);
        assertTrue("WSG84 transformed to WSG84 should be Identity", tDefault.isIdentity());

        CoordinateReferenceSystem crs1force = CRS.decode("EPSG:4326",true);
        CoordinateReferenceSystem crs2force = CRS.decode("EPSG:4326",true);
        MathTransform tForce = CRS.findMathTransform(crs1force, crs2force);
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

        // Tests the modified factory.
        factory   = new OrderedAxisAuthorityFactory("EPSG", null, null);
        authority = factory.getAuthority();
        assertNotNull(authority);
        assertTrue(org.apache.sis.metadata.iso.citation.Citations.identifierMatches(authority, "EPSG"));
    }

    /**
     * Tests the vendor name.
     */
    @Test
    public void testVendor() {
        CRSAuthorityFactory factory;
        Citation vendor;

        factory = new OrderedAxisAuthorityFactory("EPSG", null, null);
        vendor  = factory.getVendor();
        assertNotNull(vendor);
        assertEquals("Geotoolkit.org", vendor.getTitle().toString(Locale.US));
        assertFalse(org.apache.sis.metadata.iso.citation.Citations.identifierMatches(vendor, "EPSG"));
    }

    /**
     * Tests the amount of codes available.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testCodes() throws FactoryException {
        final CRSAuthorityFactory factory = new OrderedAxisAuthorityFactory("EPSG", null, null);
        final Set<String> codes = factory.getAuthorityCodes(CoordinateReferenceSystem.class);
        assertNotNull(codes);
        assertTrue(codes.size() >= 3000);
    }

    /**
     * Tests "WGS 84" geographic CRS.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void test4326() throws FactoryException {
        final CRSAuthorityFactory factory = new OrderedAxisAuthorityFactory("EPSG", null, null);
        final CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("EPSG:4326");
        assertTrue(crs instanceof GeographicCRS);
        assertEquals("EPSG:4326", IdentifiedObjects.getIdentifierOrName(crs));
        assertSame(crs, factory.createObject("EPSG:4326"));
        /*
         * Tests using lower-case code. This is also a test using the CRS.decode(...)
         * convenience method instead than direct use of the factory. The result should
         * be the same, thanks to the caching performed by ReferencingObjectFactory.
         */
        assertEquals("EPSG:4326", IdentifiedObjects.getIdentifierOrName(CRS.decode("epsg:4326")));
        assertSame(crs, CRS.decode("epsg:4326", true));
    }

    /**
     * Tests NAD83 geographic CRS.
     * UDIG requires this to work.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void test4269() throws FactoryException {
        final CRSAuthorityFactory factory = new OrderedAxisAuthorityFactory("EPSG", null, null);
        final CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("EPSG:4269");
        assertTrue(crs instanceof GeographicCRS);
        assertEquals("EPSG:4269", IdentifiedObjects.getIdentifierOrName(crs));
        assertSame(crs, factory.createObject("EPSG:4269"));
        /*
         * Tests using lower-case code. This is also a test using the CRS.decode(...)
         * convenience method instead than direct use of the factory. The result should
         * be the same, thanks to the caching performed by ReferencingObjectFactory.
         */
        assertEquals("EPSG:4269", IdentifiedObjects.getIdentifierOrName(CRS.decode("epsg:4269")));
        assertSame(crs, CRS.decode("epsg:4269", true));
        /*
         * The domain of validity is declared in the EPSG:4269 CRS, which declare an x axis
         * in the opposite direction than WGS84. We need to ensure that this particularity
         * has been handled.
         */
        final Envelope envelope = CRS.getEnvelope(crs);
        assertNotNull(envelope);
        assertTrue(envelope.getMinimum(0) < envelope.getMaximum(0));
        assertTrue(envelope.getMinimum(1) < envelope.getMaximum(1));
    }

    /**
     * Tests "NAD83 / UTM zone 10N".
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void test26910() throws FactoryException {
        final CRSAuthorityFactory factory = new OrderedAxisAuthorityFactory("EPSG", null, null);
        final CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("EPSG:26910");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("EPSG:26910", IdentifiedObjects.getIdentifierOrName(crs));
        assertSame(crs, factory.createObject("EPSG:26910"));
        /*
         * Tests using lower-case code. This is also a test using the CRS.decode(...)
         * convenience method instead than direct use of the factory. The result should
         * be the same, thanks to the caching performed by ReferencingObjectFactory.
         */
        assertEquals("EPSG:26910", IdentifiedObjects.getIdentifierOrName(CRS.decode("epsg:26910")));
        assertSame(crs, CRS.decode("epsg:26910", true));
    }

    /**
     * Tests "NAD83 / Massachusetts Mainland".
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void test26986() throws FactoryException {
        CoordinateReferenceSystem crs = CRS.decode("epsg:26986");
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
        CoordinateReferenceSystem crs = CRS.decode("epsg:26742");
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
        CoordinateReferenceSystem crs = CRS.decode("epsg:3785");
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
        CoordinateReferenceSystem crs = CRS.decode("epsg:3857");
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
        CoordinateReferenceSystem compound = CRS.decode("EPSG:7401");
        CoordinateReferenceSystem horizontal = org.apache.sis.referencing.CRS.getHorizontalComponent(compound);
        // compares with "NTF (Paris) / France II"
        assertEquals(CRS.decode("EPSG:27582"), horizontal);
    }

    /**
     * Tests {@link CRS#getHorizontalCRS} from a Geographic 3D CR.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testHorizontalFromGeodetic() throws FactoryException {
        // retrives "WGS 84 (geographic 3D)"
        CoordinateReferenceSystem compound = CRS.decode("EPSG:4327");
        CoordinateReferenceSystem horizontal = org.apache.sis.referencing.CRS.getHorizontalComponent(compound);
        // the horizonal version is basically 4326, but it won't compare positively
        // with 4326, not even using CRS.equalsIgnoreMetadata(), so we check the axis directly
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
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3395");
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:27572");
        sourceCRS = new DefaultCompoundCRS(singletonMap(NAME_KEY, "3D"), sourceCRS, CommonCRS.Vertical.ELLIPSOIDAL.crs());
        sourceCRS = new DefaultCompoundCRS(singletonMap(NAME_KEY, "4D"), sourceCRS, CommonCRS.Temporal.JULIAN.crs());
        final MathTransform tr = CRS.findMathTransform(sourceCRS, targetCRS, true);
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
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4327");
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
        MathTransform tr;
        try {
            CRS.findMathTransform(sourceCRS, targetCRS);
            fail("No conversion from EPSG:4327 to EPSG:4326 should be allowed because the units " +
                 "conversion from DMS to degrees is not linear. Note that this exception may be " +
                 "removed in a future version if we implement non-linear unit conversions.");
        } catch (OperationNotFoundException e) {
            assertTrue("The operation should have failed because of a unit conversion error.",
                    e.getCause() instanceof ConversionException);
        }
        sourceCRS = CRS.decode("EPSG:4979");
        tr = CRS.findMathTransform(sourceCRS, targetCRS);
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
    public void testProjected3D_to_2D() throws FactoryException {
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:3035");
        GeodeticDatum targetDatum = ((GeographicCRS) targetCRS).getDatum();
        GeodeticDatum sourceDatum =  ((ProjectedCRS) sourceCRS).getDatum();
        final BursaWolfParameters[] params = ((DefaultGeodeticDatum) sourceDatum).getBursaWolfParameters();
        assertEquals("This test requires that an explicit BursaWolf parameter exists.", 1, params.length);
        assertEquals("targetDatum", targetDatum, params[0].getTargetDatum());
        assertTrue("This test requires that the BursaWolf parameter is set to identity.", params[0].isIdentity());

        CoordinateReferenceSystem vertCRS = CRS.parseWKT(
                "VERT_CS[\"Sigma Level\",VERT_DATUM[\"Sigma Level\",2000],UNIT[\"level\",1.0],AXIS[\"Sigma Level\",DOWN]]");
        sourceCRS = new DefaultCompoundCRS(singletonMap(NAME_KEY, "ETRS89 + Sigma level"), sourceCRS, vertCRS);
        final MathTransform tr = CRS.findMathTransform(sourceCRS, targetCRS);
        assertSame(tr, CRS.findMathTransform(sourceCRS, targetCRS, false));
        assertSame(tr, CRS.findMathTransform(sourceCRS, targetCRS, true));
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
        final CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4267"); // NAD27
        final CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326"); // WGS84
        final DefaultGeographicBoundingBox box = new DefaultGeographicBoundingBox(-91.64, -88.09, 30.02, 35.00); // Mississipi (EPSG:1393)
        final MathTransform mt = CRS.findMathTransform(sourceCRS, targetCRS, box, true);
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
