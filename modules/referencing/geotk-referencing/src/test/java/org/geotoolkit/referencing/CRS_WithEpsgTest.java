/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing;

import java.util.Set;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.sql.Connection;
import java.sql.SQLException;

import org.opengis.geometry.Envelope;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.FactoryException;

import org.geotoolkit.test.Depend;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory;
import org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory;
import org.geotoolkit.referencing.factory.FallbackAuthorityFactory;
import org.geotoolkit.referencing.factory.OrderedAxisAuthorityFactory;
import org.geotoolkit.referencing.factory.OrderedAxisAuthorityFactoryTest;

import org.junit.*;
import static org.junit.Assume.assumeTrue;
import static org.geotoolkit.test.Commons.EPSG_VERSION;


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
@Depend(CRS_Test.class)
public class CRS_WithEpsgTest extends ReferencingTestCase {
    /**
     * Tests the (latitude, longitude) axis order for EPSG:4326.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testCorrectAxisOrder() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

        final CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        assertEquals("EPSG:4326", CRS.getDeclaredIdentifier(crs));
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
        assumeTrue(isEpsgFactoryAvailable());

        final CoordinateReferenceSystem crs = CRS.decode("EPSG:4326", true);
        assertEquals("EPSG:4326", CRS.getDeclaredIdentifier(crs));
        final CoordinateSystem cs = crs.getCoordinateSystem();
        assertEquals(2, cs.getDimension());

        CoordinateSystemAxis axis0 = cs.getAxis(0);
        assertEquals("Long", axis0.getAbbreviation());

        CoordinateSystemAxis axis1 = cs.getAxis(1);
        assertEquals("Lat", axis1.getAbbreviation());

        final CoordinateReferenceSystem standard = CRS.decode("EPSG:4326");
        assertFalse("Should not be (long,lat) axis order.", CRS.equalsIgnoreMetadata(crs, standard));
    }

    /**
     * Tests again EPSG:4326, but forced to (longitude, latitude) axis order.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testSystemPropertyToForceXY() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

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
        assumeTrue(isEpsgFactoryAvailable());
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
                assertEquals("3035", AbstractIdentifiedObject.getIdentifier(crs, Citations.EPSG).getCode());
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
     * Tests {@link CRS#lookupIdentifier}.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testLookupIdentifier() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

        CoordinateReferenceSystem crs = getED50("ED50");
        assertEquals("Should find without scan thanks to the name.", "EPSG:4230",
                     CRS.lookupIdentifier(crs, false));
        assertEquals(Integer.valueOf(4230), CRS.lookupEpsgCode(crs, false));

        crs = getED50("ED50 with unknown name");
        assertNull("Should not find the CRS without a scan.",
                   CRS.lookupIdentifier(crs, false));
        assertEquals(null, CRS.lookupEpsgCode(crs, false));

        assertEquals("With scan allowed, should find the CRS.", "EPSG:4230",
                     CRS.lookupIdentifier(crs, true));
        assertEquals(Integer.valueOf(4230), CRS.lookupEpsgCode(crs, true));
    }

    /**
     * Tests {@link CRS#lookupIdentifier} in the URN namespace.
     * Also test the HTTP namespace by opportunity.
     * <p>
     * <strong>THIS TEST ASSUMES THAT THE EPSG DATABASE VERSION {@value #EPSG_VERSION} IS USED</strong>.
     * If this is not the case, please update the {@link #EPSG_VERSION} field.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testLookupIdentifierWithURN() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

        final CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        assertEquals("http://www.opengis.net/gml/srs/epsg.xml#4326",
                CRS.lookupIdentifier(Citations.HTTP_OGC, crs, false));
        assertEquals("NOTE: This test assumes that the EPSG database version " + EPSG_VERSION +
                " is used. It should be the case if the embedded database is used (geotk-epsg)." +
                " If that module is upgrated with a newer version of the EPSG database, please" +
                " update this test.",
                "urn:ogc:def:crs:epsg:" + EPSG_VERSION + ":4326",
                CRS.lookupIdentifier(Citations.URN_OGC, crs, false));
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
        assumeTrue(isEpsgFactoryAvailable());

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
        assumeTrue(isEpsgFactoryAvailable());

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
        assumeTrue(isEpsgFactoryAvailable());
        CRSAuthorityFactory factory;
        Citation authority;

        // Tests the official factory.
        factory   = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", null);
        authority = factory.getAuthority();
        assertNotNull(authority);
        assertEquals("European Petroleum Survey Group", authority.getTitle().toString(Locale.US));
        assertTrue(Citations.identifierMatches(authority, "EPSG"));

        // Tests the modified factory.
        factory   = new OrderedAxisAuthorityFactory("EPSG", null, null);
        authority = factory.getAuthority();
        assertNotNull(authority);
        assertTrue(Citations.identifierMatches(authority, "EPSG"));
    }

    /**
     * Tests the vendor name.
     */
    @Test
    public void testVendor() {
        assumeTrue(isEpsgFactoryAvailable());
        CRSAuthorityFactory factory;
        Citation vendor;

        factory = new OrderedAxisAuthorityFactory("EPSG", null, null);
        vendor  = factory.getVendor();
        assertNotNull(vendor);
        assertEquals("Geotoolkit.org", vendor.getTitle().toString(Locale.US));
        assertFalse(Citations.identifierMatches(vendor, "EPSG"));
    }

    /**
     * Tests the amount of codes available.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testCodes() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());
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
        assumeTrue(isEpsgFactoryAvailable());
        final CRSAuthorityFactory factory = new OrderedAxisAuthorityFactory("EPSG", null, null);
        final CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("EPSG:4326");
        assertTrue(crs instanceof GeographicCRS);
        assertEquals("EPSG:4326", CRS.getDeclaredIdentifier(crs));
        assertSame(crs, factory.createObject("EPSG:4326"));
        /*
         * Tests using lower-case code. This is also a test using the CRS.decode(...)
         * convenience method instead than direct use of the factory. The result should
         * be the same, thanks to the caching performed by ReferencingObjectFactory.
         */
        assertEquals("EPSG:4326", CRS.getDeclaredIdentifier(CRS.decode("epsg:4326")));
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
        assumeTrue(isEpsgFactoryAvailable());
        final CRSAuthorityFactory factory = new OrderedAxisAuthorityFactory("EPSG", null, null);
        final CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("EPSG:4269");
        assertTrue(crs instanceof GeographicCRS);
        assertEquals("EPSG:4269", CRS.getDeclaredIdentifier(crs));
        assertSame(crs, factory.createObject("EPSG:4269"));
        /*
         * Tests using lower-case code. This is also a test using the CRS.decode(...)
         * convenience method instead than direct use of the factory. The result should
         * be the same, thanks to the caching performed by ReferencingObjectFactory.
         */
        assertEquals("EPSG:4269", CRS.getDeclaredIdentifier(CRS.decode("epsg:4269")));
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
        assumeTrue(isEpsgFactoryAvailable());
        final CRSAuthorityFactory factory = new OrderedAxisAuthorityFactory("EPSG", null, null);
        final CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("EPSG:26910");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("EPSG:26910", CRS.getDeclaredIdentifier(crs));
        assertSame(crs, factory.createObject("EPSG:26910"));
        /*
         * Tests using lower-case code. This is also a test using the CRS.decode(...)
         * convenience method instead than direct use of the factory. The result should
         * be the same, thanks to the caching performed by ReferencingObjectFactory.
         */
        assertEquals("EPSG:26910", CRS.getDeclaredIdentifier(CRS.decode("epsg:26910")));
        assertSame(crs, CRS.decode("epsg:26910", true));
    }

    /**
     * Tests "NAD83 / Massachusetts Mainland".
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void test26986() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());
        CoordinateReferenceSystem crs = CRS.decode("epsg:26986");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("EPSG:26986", CRS.getDeclaredIdentifier(crs));
    }

    /**
     * Tests "AD27 / California zone II".
     * WFS requires this to work.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void test26742() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());
        CoordinateReferenceSystem crs = CRS.decode("epsg:26742");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("EPSG:26742", CRS.getDeclaredIdentifier(crs));
    }

    /**
     * Tests "Popular Visualisation CRS / Mercator".
     *
     * @throws FactoryException Should not happen.
     *
     * @since 3.15
     */
    @Test
    public void test3785() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());
        CoordinateReferenceSystem crs = CRS.decode("epsg:3785");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("EPSG:3785", CRS.getDeclaredIdentifier(crs));
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
        assumeTrue(isEpsgFactoryAvailable());
        CoordinateReferenceSystem crs = CRS.decode("epsg:3857");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("EPSG:3857", CRS.getDeclaredIdentifier(crs));
    }

    /**
     * Tests {@link CRS#getHorizontalCRS} from a compound CRS.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testHorizontalFromCompound() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

        // retrives "NTF (Paris) / France II + NGF Lallemand"
        CoordinateReferenceSystem compound = CRS.decode("EPSG:7401");
        CoordinateReferenceSystem horizontal = CRS.getHorizontalCRS(compound);
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
        assumeTrue(isEpsgFactoryAvailable());

        // retrives "WGS 84 (geographic 3D)"
        CoordinateReferenceSystem compound = CRS.decode("EPSG:4327");
        CoordinateReferenceSystem horizontal = CRS.getHorizontalCRS(compound);
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
     * @see http://jira.geotoolkit.org/browse/GEOTK-81
     */
    @Test
    public void testProjected4D() throws FactoryException {
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3395");
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:27572");
        sourceCRS = new DefaultCompoundCRS("3D", sourceCRS, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT);
        sourceCRS = new DefaultCompoundCRS("4D", sourceCRS, DefaultTemporalCRS.JULIAN);
        final MathTransform tr = CRS.findMathTransform(sourceCRS, targetCRS, true);
        assertEquals(4, tr.getSourceDimensions());
        assertEquals(2, tr.getTargetDimensions());
    }

    /**
     * Tests the number of CRS that can be created. This test will be executed only if
     * this test suite is run while the {@link #verbose} field is set to {@code true}.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testSuccess() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());
        if (!verbose) {
            return;
        }
        final CRSAuthorityFactory factory = new OrderedAxisAuthorityFactory("EPSG", null, null);
        Set<String> codes = factory.getAuthorityCodes(CoordinateReferenceSystem.class);
        int total = codes.size();
        int count = 0;
        for (final String code : codes) {
            CoordinateReferenceSystem crs;
            try {
                crs = factory.createCoordinateReferenceSystem(code);
                assertNotNull(crs);
                count++;
            } catch (FactoryException e) {
                System.err.println("WARNING (CRS: "+code+" ):" + e.getMessage());
            }
        }
        System.out.println("Success: " + count + "/" + total + " (" + (count*100)/total + "%)");
    }
}
