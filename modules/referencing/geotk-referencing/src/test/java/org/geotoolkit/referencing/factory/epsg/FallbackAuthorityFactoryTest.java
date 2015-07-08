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
package org.geotoolkit.referencing.factory.epsg;

import java.util.Set;
import java.io.IOException;

import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.test.referencing.ReferencingTestBase;
import org.geotoolkit.test.referencing.WKT;

import org.apache.sis.test.DependsOn;
import org.junit.*;

import static org.junit.Assume.assumeTrue;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.Commons.isEpsgFactoryAvailable;


/**
 * Checks the exception thrown by the fallback system do report actual errors when the code is
 * available but for some reason broken, and not "code not found" ones.
 *
 * @author Andrea Aime (TOPP)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 2.4
 */
@DependsOn(PropertyEpsgFactoryTest.class)
public final strictfp class FallbackAuthorityFactoryTest extends ReferencingTestBase {
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
     * The extra factory.
     */
    private ExtraEpsgFactory extra;

    /**
     * Adds the extra factory to the set of authority factories.
     *
     * @throws IOException Should never happen.
     */
    @Before
    public void setUp() throws IOException {
        assertNull(extra);
        extra = new ExtraEpsgFactory();
        AuthorityFactoryFinder.addAuthorityFactory(extra);
    }

    /**
     * Removes the extra factory from the set of authority factories.
     */
    @After
    public void tearDown() {
        assertNotNull(extra);
        AuthorityFactoryFinder.removeAuthorityFactory(extra);
        extra = null;
    }

    /**
     * Makes sure that the testing {@link FactoryEPSGExtra} has precedence over
     * {@link FactoryUsingWKT}.
     */
    @Test
    public void testFactoryOrdering() {
        final Set<CRSAuthorityFactory> factories = AuthorityFactoryFinder.getCRSAuthorityFactories(null);
        boolean foundWkt = false;
        boolean foundExtra = false;
        if (out != null) {
            out.println("List of CRSAuthorityFactories in iteration order:");
            out.println("ExtraEpsgFactory should be after ThreadedEpsgFactory (if present) but before PropertyEpsgFactory.");
        }
        for (final CRSAuthorityFactory factory : factories) {
            final Class<? extends CRSAuthorityFactory> type = factory.getClass();
            if (out != null) {
                out.print("  \u2022 ");
                out.println(type);
            }
            if (type.equals(ExtraEpsgFactory.class)) {
                foundExtra = true;
                assertFalse("We should not have encountered the WKT factory yet.", foundWkt);
            } else if (type.equals(PropertyEpsgFactory.class)) {
                foundWkt = true;
                assertTrue("We should have encountered WKT factory after the extra one", foundExtra);
            }
        }
        assertTrue(foundWkt);
        assertTrue(foundExtra);
    }

    /**
     * Tests the {@code 27572} code. The purpose of this test is mostly
     * to make sure that {@link PropertyEpsgFactory} is in the chain.
     *
     * @throws FactoryException If the CRS can't be created.
     */
    @Test
    public void test27572() throws FactoryException {
        assertTrue(CRS.decode("EPSG:27572") instanceof ProjectedCRS);
    }

    /**
     * Tests the {@code 00001} fake code.
     *
     * @throws FactoryException If the CRS can't be created.
     */
    @Test
    public void test00001() throws FactoryException {
        try {
            CRS.decode("EPSG:00001");
            fail("This code should not be there");
        } catch (NoSuchAuthorityCodeException e) {
            fail("The code 00001 is there, exception should report it's broken");
        } catch (FactoryException e) {
            // cool, that's what we expected
        }
    }

    /**
     * Makes sure looking up for an existing code does not result in a
     * {@link StackOverflowException}. This method is called "trivial"
     * because the identifier is contained in the CRS we are looking at.
     *
     * @throws FactoryException If the CRS can't be created.
     *
     * @see http://jira.codehaus.org/browse/GEOT-1702
     */
    @Test
    public void testLookupTrivial() throws FactoryException {
        final CoordinateReferenceSystem crsXY = CRS.decode("EPSG:27572", true);
        String code = IdentifiedObjects.lookupIdentifier(crsXY, false);
        assertEquals("Should find the identifier because this CRS has " +
                "an explicit AUTHORITY element.", "EPSG:27572", code);

        final CoordinateReferenceSystem crs = CRS.decode("EPSG:27572");
        assertNotDeepEquals(crs, crsXY);
        code = IdentifiedObjects.lookupIdentifier(crs, false);
        assertEquals("EPSG:27572", code);
    }

    /**
     * Makes sure looking up for an existing code does not result in a
     * {@link StackOverflowException}.
     *
     * @throws FactoryException If the CRS can't be created.
     *
     * @see http://jira.codehaus.org/browse/GEOT-1702
     */
    @Test
    public void testLookupSuccessfull() throws FactoryException {
        final CoordinateReferenceSystem crsXY = CRS.decode("EPSG:3035", true);
        String code = IdentifiedObjects.lookupIdentifier(crsXY, false);
        assertEquals("Should find the identifier even if the WKT doesn't have an explicit AUTHORITY " +
                "element because the factory should have added it automatically.", "EPSG:3035", code);

        final CoordinateReferenceSystem crs = CRS.decode("EPSG:3035");
        assertNotDeepEquals(crs, crsXY);
        code = IdentifiedObjects.lookupIdentifier(crs, false);
        assertEquals("EPSG:3035", code);
    }

    /**
     * Makes sure looking up for a non existing code does not result in a
     * {@link StackOverflowException}.
     *
     * @throws FactoryException If the CRS can't be created.
     *
     * @see http://jira.codehaus.org/browse/GEOT-1702
     */
    @Test
    public void testLookupFailing() throws FactoryException {
        CoordinateReferenceSystem crs = CRS.parseWKT(WKT.PROJCS_MERCATOR_GOOGLE);
        assertNull(IdentifiedObjects.lookupIdentifier(crs, true));
    }
}
