/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory;

import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.awt.RenderingHints;

import org.opengis.util.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.referencing.factory.epsg.LongitudeFirstEpsgFactory;
import org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory;

import org.apache.sis.util.Classes;
import org.geotoolkit.test.Depend;
import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;
import static org.junit.Assume.assumeTrue;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.Commons.*;


/**
 * Tests the usage of {@link OrderedAxisAuthorityFactory} with the help of the
 * EPSG database. Any EPSG plugin should fit.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.12
 *
 * @since 3.00
 */
@Depend(FactoryFinderTest.class)
public final strictfp class OrderedAxisAuthorityFactoryTest extends ReferencingTestBase {
    /**
     * {@code true} if metadata (especially identifiers) should be erased, or {@code false} if
     * they should be kept. The {@code true} value matches the pre GEOT-854 state, while the
     * {@code false} value matches the post GEOT-854 state.
     *
     * @see <a href="http://jira.codehaus.org/browse/GEOT-854">GEOT-854</a>
     */
    private static final boolean METADATA_ERASED = false;

    /**
     * Small number for floating points comparisons.
     */
    private static final double EPS = 1E-8;

    /**
     * Returns the ordered axis factory for the specified set of hints.
     */
    private static OrderedAxisAuthorityFactory getFactory(final Hints hints) {
        CRSAuthorityFactory factory;
        hints.put(Hints.CRS_AUTHORITY_FACTORY, LongitudeFirstEpsgFactory.class);
        factory = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", hints);

        assertTrue(factory.getClass().toString(), factory instanceof LongitudeFirstEpsgFactory);
        final OrderedAxisAuthorityFactory asLongitudeFirst = (LongitudeFirstEpsgFactory) factory;
        assertFalse("Expected default toBackingFactoryCode(...)", asLongitudeFirst.isCodeMethodOverridden());

        final Map<RenderingHints.Key, ?> implementationHints = asLongitudeFirst.getImplementationHints();
        factory = (CRSAuthorityFactory) implementationHints.get(Hints.CRS_AUTHORITY_FACTORY);
        assertTrue(factory.getClass().toString(), factory instanceof ThreadedAuthorityFactory);

        return asLongitudeFirst;
    }

    /**
     * Returns a positive number if the specified coordinate system is right-handed,
     * or a negative number if it is left handed.
     */
    private static double getAngle(final CoordinateReferenceSystem crs) {
        final CoordinateSystem cs = crs.getCoordinateSystem();
        assertEquals(2, cs.getDimension());
        return DefaultCoordinateSystemAxis.getAngle(
                cs.getAxis(0).getDirection(),
                cs.getAxis(1).getDirection());
    }

    /**
     * Makes sure that {@link ThreadedAuthorityFactory} is before {@link LongitudeFirstEpsgFactory},
     * which should be before {@link PropertyEpsgFactory}. This test works even if there is no EPSG
     * factory registered.
     */
    @Test
    public void testFactoryOrdering() {
        boolean ordered    = false;
        boolean properties = false;
        for (final CRSAuthorityFactory check : AuthorityFactoryFinder.getCRSAuthorityFactories(null)) {
            if (check instanceof ThreadedAuthorityFactory) {
                assertFalse(ordered);
                assertFalse(properties);
            }
            if (check instanceof LongitudeFirstEpsgFactory) {
                ordered = true;
                assertFalse(properties);
            }
            if (check instanceof PropertyEpsgFactory) {
                properties = true;
            }
        }
    }

    /**
     * Tests the registration of the various flavor of {@link OrderedAxisAuthorityFactoryTest}
     * for the EPSG authority factory.
     */
    @Test
    public void testRegistration() {
        assumeTrue(isEpsgFactoryAvailable());

        final Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        OrderedAxisAuthorityFactory factory;
        factory = getFactory(hints);
        assertFalse(factory.forceStandardDirections);
        assertFalse(factory.forceStandardUnits);

        hints.put(Hints.FORCE_STANDARD_AXIS_DIRECTIONS, Boolean.FALSE);
        assertSame(factory, getFactory(hints));
        assertFalse(factory.forceStandardDirections);
        assertFalse(factory.forceStandardUnits);

        hints.put(Hints.FORCE_STANDARD_AXIS_UNITS, Boolean.FALSE);
        assertSame(factory, getFactory(hints));
        assertFalse(factory.forceStandardDirections);
        assertFalse(factory.forceStandardUnits);

        hints.put(Hints.FORCE_STANDARD_AXIS_UNITS, Boolean.TRUE);
        assertNotSame(factory, factory = getFactory(hints));
        assertFalse  (factory.forceStandardDirections);
        assertTrue   (factory.forceStandardUnits);

        hints.put(Hints.FORCE_STANDARD_AXIS_DIRECTIONS, Boolean.TRUE);
        assertNotSame(factory, factory = getFactory(hints));
        assertTrue   (factory.forceStandardDirections);
        assertTrue   (factory.forceStandardUnits);

        hints.put(Hints.FORCE_STANDARD_AXIS_UNITS, Boolean.FALSE);
        assertNotSame(factory, factory = getFactory(hints));
        assertTrue   (factory.forceStandardDirections);
        assertFalse  (factory.forceStandardUnits);
    }

    /**
     * Tests the axis reordering.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testAxisReordering() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

        /*
         * Tests the OrderedAxisAuthorityFactory creating using FactoryFinder. The following
         * conditions are not tested directly, but are required in order to get the test to
         * succeed:
         *
         *    - EPSG factories must be provided for both "official" and "modified" axis order.
         *    - The "official" axis order must have precedence over the modified one.
         *    - The hints are correctly understood by FactoryFinder.
         */
        final AbstractAuthorityFactory factory0, factory1;
        final Hints hints = new Hints(Hints.CRS_AUTHORITY_FACTORY, AbstractAuthorityFactory.class);
        factory0 = (AbstractAuthorityFactory) AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
        assertFalse(factory0 instanceof OrderedAxisAuthorityFactory);
        assertFalse(factory0 instanceof LongitudeFirstEpsgFactory);
        hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        hints.put(Hints.FORCE_STANDARD_AXIS_DIRECTIONS,   Boolean.TRUE);
        hints.put(Hints.FORCE_STANDARD_AXIS_UNITS,        Boolean.TRUE);
        factory1 = (AbstractAuthorityFactory) AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
        assertTrue(factory1 instanceof LongitudeFirstEpsgFactory);
        /*
         * The local variables to be used for all remaining tests
         * (useful to setup in the debugger).
         */
        String code;
        CoordinateReferenceSystem crs0, crs1;
        CoordinateOperationFactory opFactory = AuthorityFactoryFinder.getCoordinateOperationFactory(null);
        MathTransform mt;
        Matrix matrix;
        /*
         * Tests a WGS84 geographic CRS (2D) with (NORTH, EAST) axis directions.
         * The factory should reorder the axis with no more operation than an axis swap.
         */
        code = "4326";
        crs0 = factory0.createCoordinateReferenceSystem(code);
        crs1 = factory1.createCoordinateReferenceSystem(code);
        final CoordinateReferenceSystem cacheTest = crs1;
        assertNotSame(crs0, crs1);
        assertNotSame(crs0.getCoordinateSystem(), crs1.getCoordinateSystem());
        assertSame(((SingleCRS) crs0).getDatum(), ((SingleCRS) crs1).getDatum());
        assertEquals("Expected a left-handed CS.",  -90, getAngle(crs0), EPS);
        assertEquals("Expected a right-handed CS.", +90, getAngle(crs1), EPS);
        assertFalse(crs0.getIdentifiers().isEmpty());
        if (METADATA_ERASED) {
            assertTrue(crs1.getIdentifiers().isEmpty());
        } else {
            assertEquals(crs0.getIdentifiers(), crs1.getIdentifiers());
        }
        mt = opFactory.createOperation(crs0, crs1).getMathTransform();
        assertFalse(mt.isIdentity());
        assertTrue(mt instanceof LinearTransform);
        matrix = ((LinearTransform) mt).getMatrix();
        assertEquals(new GeneralMatrix(new double[][] {
            {0, 1, 0},
            {1, 0, 0},
            {0, 0, 1}}), new GeneralMatrix(matrix));
        /*
         * Tests a WGS84 geographic CRS (3D) with (NORTH, EAST, UP) axis directions.
         * Because this CRS uses sexagesimal units, conversions are not supported and
         * will not be tested.
         */
        code = "4329";
        crs0 = factory0.createCoordinateReferenceSystem(code);
        crs1 = factory1.createCoordinateReferenceSystem(code);
        assertNotSame(crs0, crs1);
        assertNotSame(crs0.getCoordinateSystem(), crs1.getCoordinateSystem());
        assertSame(((SingleCRS) crs0).getDatum(), ((SingleCRS) crs1).getDatum());
        assertFalse(crs0.getIdentifiers().isEmpty());
        if (METADATA_ERASED) {
            assertTrue(crs1.getIdentifiers().isEmpty());
        } else {
            assertEquals(crs0.getIdentifiers(), crs1.getIdentifiers());
        }
        /*
         * Tests a WGS84 geographic CRS (3D) with (NORTH, EAST, UP) axis directions.
         * The factory should reorder the axis with no more operation than an axis swap.
         */
        code = "63266413";
        crs0 = factory0.createCoordinateReferenceSystem(code);
        crs1 = factory1.createCoordinateReferenceSystem(code);
        assertNotSame(crs0, crs1);
        assertNotSame(crs0.getCoordinateSystem(), crs1.getCoordinateSystem());
        assertSame(((SingleCRS) crs0).getDatum(), ((SingleCRS) crs1).getDatum());
        assertFalse(crs0.getIdentifiers().isEmpty());
        if (METADATA_ERASED) {
            assertTrue(crs1.getIdentifiers().isEmpty());
        } else {
            assertEquals(crs0.getIdentifiers(), crs1.getIdentifiers());
        }
        mt = opFactory.createOperation(crs0, crs1).getMathTransform();
        assertFalse(mt.isIdentity());
        assertTrue(mt instanceof LinearTransform);
        matrix = ((LinearTransform) mt).getMatrix();
        assertEquals(new GeneralMatrix(new double[][] {
            {0, 1, 0, 0},
            {1, 0, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}}), new GeneralMatrix(matrix));
        /*
         * Tests a projected CRS with (EAST, NORTH) axis orientation. No axis reordering is needed,
         * which means that their coordinate systems are identical and the math transform should be
         * the identity one. Note that while no axis swap is needed, the base GeographicCRS are not
         * the same since an axis reordering has been done there.
         */
        code = "2027";
        crs0 = factory0.createCoordinateReferenceSystem(code);
        crs1 = factory1.createCoordinateReferenceSystem(code);
        assertNotSame(crs0, crs1);
        assertSame(crs0.getCoordinateSystem(), crs1.getCoordinateSystem());
        assertSame(((SingleCRS) crs0).getDatum(), ((SingleCRS) crs1).getDatum());
        assertNotSame(((ProjectedCRS) crs0).getBaseCRS(), ((ProjectedCRS) crs1).getBaseCRS());
        assertFalse(crs0.getIdentifiers().isEmpty());
        if (METADATA_ERASED) {
            assertTrue(crs1.getIdentifiers().isEmpty());
        } else {
            assertEquals(crs0.getIdentifiers(), crs1.getIdentifiers());
        }
        mt = opFactory.createOperation(crs0, crs1).getMathTransform();
        assertTrue(mt.isIdentity());
        /*
         * Tests a projected CRS with (WEST, SOUTH) axis orientation.
         * The factory should arrange the axis with no more operation than a direction change.
         * While the end result is a matrix like the GeographicCRS case, the path that lead to
         * this result is much more complex.
         */
        code = "22275";
        crs0 = factory0.createCoordinateReferenceSystem(code);
        crs1 = factory1.createCoordinateReferenceSystem(code);
        assertNotSame(crs0, crs1);
        assertNotSame(crs0.getCoordinateSystem(), crs1.getCoordinateSystem());
        assertSame(((SingleCRS) crs0).getDatum(), ((SingleCRS) crs1).getDatum());
        assertFalse(crs0.getIdentifiers().isEmpty());
        if (METADATA_ERASED) {
            assertTrue(crs1.getIdentifiers().isEmpty());
        } else {
            assertEquals(crs0.getIdentifiers(), crs1.getIdentifiers());
        }
        mt = opFactory.createOperation(crs0, crs1).getMathTransform();
        assertFalse(mt.isIdentity());
        assertTrue(mt instanceof LinearTransform);
        matrix = ((LinearTransform) mt).getMatrix();
        assertEquals(new GeneralMatrix(new double[][] {
            {-1,  0,  0},
            { 0, -1,  0},
            { 0,  0,  1}}), new GeneralMatrix(matrix));
        /*
         * Tests the cache.
         */
        assertSame(cacheTest, factory1.createCoordinateReferenceSystem("4326"));
    }

    /**
     * Tests the creation of EPSG:4326 CRS with different axis order.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testLongitudeFirst() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

        final CoordinateReferenceSystem standard = CRS.decode("EPSG:4326", false);
        final CoordinateReferenceSystem modified = CRS.decode("EPSG:4326", true );
        assertEquals("Expected a left-handed CS.",  -90, getAngle(standard), EPS);
        assertEquals("Expected a right-handed CS.", +90, getAngle(modified), EPS);
        final MathTransform transform = CRS.findMathTransform(standard, modified);
        assertTrue(transform instanceof LinearTransform);
        final Matrix matrix = ((LinearTransform) transform).getMatrix();
        assertEquals(new GeneralMatrix(new double[][] {
            { 0,  1,  0},
            { 1,  0,  0},
            { 0,  0,  1}}), new GeneralMatrix(matrix));
    }

    /**
     * Tests the creation of EPSG:32661 CRS with different axis order. The axis order declared in
     * the database is "South along 180°" and "South along 90°E". When "xy" axis order is forced,
     * it should be reverse axis order.
     *
     * @throws FactoryException Should not happen.
     *
     * @since 3.12
     */
    @Test
    public void testPolarProjection() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

        final CoordinateReferenceSystem standard = CRS.decode("EPSG:32661", false);
        final CoordinateReferenceSystem modified = CRS.decode("EPSG:32661", true );
        assertEquals("Expected a left-handed CS.",  -90, getAngle(standard), EPS);
        assertEquals("Expected a right-handed CS.", +90, getAngle(modified), EPS);
        final MathTransform transform = CRS.findMathTransform(standard, modified);
        assertTrue(transform instanceof LinearTransform);
        final Matrix matrix = ((LinearTransform) transform).getMatrix();
        assertEquals(new GeneralMatrix(new double[][] {
            { 0,  1,  0},
            { 1,  0,  0},
            { 0,  0,  1}}), new GeneralMatrix(matrix));
    }

    /**
     * Tests the {@link IdentifiedObjectFinder#find} method with axis order forced.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testFind() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

        final CRSAuthorityFactory factory = AuthorityFactoryFinder.getCRSAuthorityFactory(
                "EPSG", new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));

        assertTrue(factory instanceof AbstractAuthorityFactory);
        AbstractAuthorityFactory findable = (AbstractAuthorityFactory) factory;
        final IdentifiedObjectFinder finder = findable.getIdentifiedObjectFinder(CoordinateReferenceSystem.class);

        /*
         * We tested in DefaultFactoryTest that WGS84 is not found when searching
         * directly in DefaultFactory. Now we perform the same search through the
         * ordered axis authority factory.
         */
        finder.setFullScanAllowed(false);
        assertNull("Should not find the CRS without a scan.",
                   finder.find(DefaultGeographicCRS.WGS84));

        finder.setFullScanAllowed(true);
        IdentifiedObject find = finder.find(DefaultGeographicCRS.WGS84);
        assertNotNull("With scan allowed, should find the CRS.", find);
        assertEqualsIgnoreMetadata(DefaultGeographicCRS.WGS84, find, false);
        assertEquals("Expected a right-handed CS.", +90, getAngle((CoordinateReferenceSystem) find), EPS);
        /*
         * Search a CRS using (latitude,longitude) axis order. The IdentifiedObjectFinder
         * should be able to find it even if it is backed by LongitudeFirstAuthorityFactory,
         * because the later is itself backed by EPSG factory and IdentifiedObjectFinder
         * should queries CRS from both.
         */
        final String wkt =
                "GEOGCS[\"WGS 84\",\n" +
                "  DATUM[\"WGS84\",\n" +
                "    SPHEROID[\"WGS 84\", 6378137.0, 298.257223563]],\n" +
                "  PRIMEM[\"Greenwich\", 0.0],\n" +
                "  UNIT[\"degree\", 0.017453292519943295],\n" +
                "  AXIS[\"Geodetic latitude\", NORTH],\n" +
                "  AXIS[\"Geodetic longitude\", EAST]]";
        final CoordinateReferenceSystem search   = CRS.parseWKT(wkt);
        final CoordinateReferenceSystem standard = CRS.decode("EPSG:4326", false);
        assertEqualsIgnoreMetadata(search, standard, false);
        assertFalse("Identifiers should not be the same.", search.equals(standard));
        finder.setFullScanAllowed(false);
        assertNull("Should not find the CRS without a scan.", finder.find(search));

        finder.setFullScanAllowed(true);
        find = finder.find(search);
        final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) find;
        assertNotNull("Should find the CRS despite the different axis order.", find);
        assertEquals("Expected a left-handed CS.", -90, getAngle(crs), EPS);
        assertNotDeepEquals(find, DefaultGeographicCRS.WGS84);
        assertEqualsIgnoreMetadata(find, search,   false);
        assertEqualsIgnoreMetadata(find, standard, false);
        assertSame("Expected caching to work.", standard, find);
    }

    /**
     * Prints the list of default factories. This is used for debugging purpose only,
     * in order to detect if axis order is properly considered.
     *
     * @throws FactoryException Should not happen.
     */
    public static void printCurrentFactoryList() throws FactoryException {
        AuthorityFactory factory = CRS.getAuthorityFactory(null);
        final boolean isStandard = (factory == CRS.getAuthorityFactory(Boolean.FALSE));
        final boolean isForceXY  = (factory == CRS.getAuthorityFactory(Boolean.TRUE));
        final String prefix = (isStandard) ? (isForceXY ? "Both standard and XY" : "Standard") :
                (isForceXY ? "XY axis order" : "Unknow factory (probably an error)");
        assertTrue(factory instanceof CachingAuthorityFactory);
        factory = ((CachingAuthorityFactory) factory).getBackingStore();
        assertTrue(factory instanceof MultiAuthoritiesFactory);
        final Collection<AuthorityFactory> factories = ((MultiAuthoritiesFactory) factory).getFactories();
        final ThreadedAuthorityFactory threaded = print("   " + prefix + ": ", factories);
        if (threaded != null) {
            System.err.println("More information about the ThreadedAuthorityFactory:");
            System.err.println(threaded);
        }
        System.err.flush();
    }

    /**
     * Prints the given collection of factories. This method is for debugging purpose only.
     */
    private static ThreadedAuthorityFactory print(final String prefix, final Collection<?> factories) {
        ThreadedAuthorityFactory threaded = null;
        for (final Object factory : factories) {
            // Keep trace of EPSG factory for information purpose.
            if (factory instanceof ThreadedAuthorityFactory) {
                threaded = (ThreadedAuthorityFactory) factory;
            }
            // For the following special cases, print the
            // dependencies instead than the factory itself.
            if (factory instanceof FallbackAuthorityFactory ||
                factory instanceof MultiAuthoritiesFactory)
            {
                final ThreadedAuthorityFactory candidate;
                candidate = print(prefix, ((AbstractAuthorityFactory) factory).dependencies());
                if (threaded == null) {
                    threaded = candidate;
                }
                continue;
            }
            // Print a single line with factory debug information.
            final Map<RenderingHints.Key, ?> hints;
            if (factory instanceof Factory) {
                hints = ((Factory) factory).getImplementationHints();
            } else {
                hints = Collections.emptyMap();
            }
            System.err.println(prefix + Classes.getShortClassName(factory) +" (forcexy=" +
                    hints.get(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER) + ')');
        }
        return threaded;
    }
}
