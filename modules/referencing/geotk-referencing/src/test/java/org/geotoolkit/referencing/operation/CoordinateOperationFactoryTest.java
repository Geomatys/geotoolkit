/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.Operation;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.Transformation;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.WKT;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.referencing.operation.transform.TransformTestCase;
import org.geotoolkit.referencing.operation.transform.AbstractMathTransform;

import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.WGS84;
import static org.geotoolkit.referencing.crs.DefaultEngineeringCRS.GENERIC_2D;
import static org.geotoolkit.referencing.crs.DefaultEngineeringCRS.CARTESIAN_2D;
import static org.geotoolkit.referencing.crs.DefaultEngineeringCRS.CARTESIAN_3D;
import static org.geotoolkit.metadata.iso.quality.AbstractPositionalAccuracy.*;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Test the default coordinate operation factory.
 * <p>
 * <strong>NOTE:</strong> Some tests are disabled in the particular case when the
 * {@link CoordinateOperationFactory} is actually an {@link AuthorityBackedFactory}
 * instance. This is because the later can replace source or target CRS by some CRS
 * found in the EPSG authority factory, causing {@code assertSame} to fails. It may
 * also returns a more accurate operation than the one expected from the WKT in the
 * code below, causing transformation checks to fail as well. This situation occurs
 * only if some EPSG authority factory like {@code plugin/epsg-hsql} is found in the
 * classpath while the test are running. It should not occurs during Maven build, so
 * all tests should be executed with Maven. It may occurs during an execution from
 * the IDE however, in which case the tests are disabled in order to allows normal
 * execution of other tests.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.1
 */
public final class CoordinateOperationFactoryTest extends TransformTestCase {
    /**
     * WKT of compound CRS to be tested.
     */
    private static final String
            WGS84_Z = "COMPD_CS[\"WGS84 + Z\"," + WKT.GEOGCS_WGS84_DMHS + ',' + WKT.VERTCS_Z      + ']',
            NAD27_Z = "COMPD_CS[\"NAD27 + Z\"," + WKT.GEOGCS_NAD27      + ',' + WKT.VERTCS_Z      + ']',
            Z_NAD27 = "COMPD_CS[\"Z + NAD27\"," + WKT.VERTCS_Z          + ',' + WKT.GEOGCS_NAD27  + ']',
            WGS84_H = "COMPD_CS[\"WGS84 + H\"," + WKT.GEOGCS_WGS84_DMHS + ',' + WKT.VERTCS_HEIGHT + ']',
            NAD27_H = "COMPD_CS[\"NAD27 + Z\"," + WKT.GEOGCS_NAD27      + ',' + WKT.VERTCS_HEIGHT + ']';

    /**
     * {@code true} if {@link #opFactory} is <strong>not</strong> an instance of
     * {@link AuthorityBackedFactory}. See class javadoc for rational.
     */
    private boolean usingDefaultFactory;

    /**
     * Creates a new test suite.
     */
    public CoordinateOperationFactoryTest() {
        super(AbstractMathTransform.class, null);
    }

    /**
     * Ensures that positional accuracy dependencies are properly loaded. This is not needed for
     * normal execution, but JUnit behavior with class loaders is sometime surprising.
     */
    @Before
    public void ensureClassLoaded() {
        assertNotNull(DATUM_SHIFT_APPLIED);
        assertNotNull(DATUM_SHIFT_OMITTED);
        usingDefaultFactory = ((AbstractCoordinateOperationFactory) opFactory).getBackingFactory() == null;
    }

    /**
     * Make sure that a factory can be find in the presence of some global hints.
     *
     * @see http://jira.codehaus.org/browse/GEOT-1618
     */
    @Test
    public void testFactoryWithHints() {
        final Hints hints = new Hints();
        hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        hints.put(Hints.FORCE_STANDARD_AXIS_DIRECTIONS,   Boolean.TRUE);
        hints.put(Hints.FORCE_STANDARD_AXIS_UNITS,        Boolean.TRUE);

        final CoordinateOperationFactory factory =
                AuthorityFactoryFinder.getCoordinateOperationFactory(hints);
        assertSame(opFactory, factory);
    }

    /**
     * Makes sure that {@code createOperation(sourceCRS, targetCRS)} returns an identity transform
     * when {@code sourceCRS} and {@code targetCRS} are identical, and tests the generic CRS.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testGenericTransform() throws FactoryException {
        assertTrue(opFactory.createOperation(WGS84,        WGS84       ).getMathTransform().isIdentity());
        assertTrue(opFactory.createOperation(CARTESIAN_2D, CARTESIAN_2D).getMathTransform().isIdentity());
        assertTrue(opFactory.createOperation(CARTESIAN_3D, CARTESIAN_3D).getMathTransform().isIdentity());
        assertTrue(opFactory.createOperation(GENERIC_2D,   GENERIC_2D  ).getMathTransform().isIdentity());
        assertTrue(opFactory.createOperation(GENERIC_2D,   CARTESIAN_2D).getMathTransform().isIdentity());
        assertTrue(opFactory.createOperation(CARTESIAN_2D, GENERIC_2D  ).getMathTransform().isIdentity());
        assertTrue(opFactory.createOperation(WGS84,        GENERIC_2D  ).getMathTransform().isIdentity());
        assertTrue(opFactory.createOperation(GENERIC_2D,   WGS84       ).getMathTransform().isIdentity());
        try {
            opFactory.createOperation(CARTESIAN_2D, WGS84);
            fail();
        } catch (OperationNotFoundException exception) {
            // This is the expected exception.
        }
        try {
            opFactory.createOperation(WGS84, CARTESIAN_2D);
            fail();
        } catch (OperationNotFoundException exception) {
            // This is the expected exception.
        }
    }

    /**
     * Tests a transformation with unit conversion.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testUnitConversion() throws Exception {
        // NOTE: TOWGS84[0,0,0,0,0,0,0] is used here as a hack for
        //       avoiding datum shift. Shifts will be tested later.
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.PROJCS_TRANSVERSE_MERCATOR_GOOGLE);
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.GEOGCS_SPHERE);
        final CoordinateOperation operation = opFactory.createOperation(sourceCRS, targetCRS);
        assertEquals(sourceCRS, operation.getSourceCRS());
        assertEquals(targetCRS, operation.getTargetCRS());
        assertTrue  (operation instanceof Projection);

        final ParameterValueGroup param = ((Operation) operation).getParameterValues();
        assertEquals("semi_major",     6370997.0, param.parameter("semi_major"        ).doubleValue(), 1E-5);
        assertEquals("semi_minor",     6370997.0, param.parameter("semi_minor"        ).doubleValue(), 1E-5);
        assertEquals("latitude_of_origin",  50.0, param.parameter("latitude_of_origin").doubleValue(), 1E-8);
        assertEquals("central_meridian",   170.0, param.parameter("central_meridian"  ).doubleValue(), 1E-8);
        assertEquals("scale_factor",        0.95, param.parameter("scale_factor"      ).doubleValue(), 1E-8);
        assertEquals("false_easting",        0.0, param.parameter("false_easting"     ).doubleValue(), 1E-8);
        assertEquals("false_northing",       0.0, param.parameter("false_northing"    ).doubleValue(), 1E-8);

        transform = operation.getMathTransform();
        validate();
        tolerance = 1E-6;
        assertTransformEquals2_2(170, 50, 0, 0);
        transform = transform.inverse();
        assertTransformEquals2_2(0, 0, 170, 50);
    }

    /**
     * Tests a transformation that requires a datum shift with TOWGS84[0,0,0].
     * In addition, this method tests datum aliases.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testEllipsoidShift() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.GEOGCS_NAD83);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.GEOGCS_WGS84_ESRI);
        final CoordinateOperation operation = opFactory.createOperation(sourceCRS, targetCRS);
        if (usingDefaultFactory) {
            assertSame(sourceCRS, operation.getSourceCRS());
            assertSame(targetCRS, operation.getTargetCRS());
        }
        transform = operation.getMathTransform();
        validate();
        tolerance = 1E-6;
        assertTransformEquals2_2(-180, -88.21076182660325, -180, -88.21076182655470);
        assertTransformEquals2_2(+180,  85.41283436546335, -180,  85.41283436531322);
//      assertTransformEquals2_2(+180,  85.41283436546335, +180,  85.41283436548373);
        // Note 1: Expected values above were computed with Geotoolkit (not an external library).
        // Note 2: The commented-out test it the one we get when using geocentric instead of
        //         Molodenski method.
    }

    /**
     * Tests a transformation that requires a datum shift.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testDatumShift() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.GEOGCS_NTF);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.GEOGCS_WGS84);
        final CoordinateOperation operation = opFactory.createOperation(sourceCRS, targetCRS);
        if (usingDefaultFactory) {
            assertSame (sourceCRS, operation.getSourceCRS());
            assertSame (targetCRS, operation.getTargetCRS());
            assertTrue (operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_APPLIED));
            assertFalse(operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_OMITTED));
        }
        transform = operation.getMathTransform();
        validate();
        tolerance = 1E-6;
        assertTransformEquals2_2( 0,   0,  2.3367521703619816, 0.0028940088671177986);
        assertTransformEquals2_2(20, -10, -6.663517606186469, 18.00134508026729);
        // Note: Expected values above were computed with Geotoolkit (not an external library).
        //       However, it was tested with both Molodenski and Geocentric transformations.

        /*
         * Remove the TOWGS84 element and test again. An exception should be throws,
         * since no Bursa-Wolf parameters were available.
         */
        final CoordinateReferenceSystem amputedCRS;
        if (true) {
            String wkt = sourceCRS.toWKT();
            final int start = wkt.indexOf("TOWGS84");  assertTrue(start >= 0);
            final int end   = wkt.indexOf(']', start); assertTrue(end   >= 0);
            final int comma = wkt.indexOf(',', end);   assertTrue(comma >= 0);
            wkt = wkt.substring(0, start) + wkt.substring(comma+1);
            amputedCRS = crsFactory.createFromWKT(wkt);
        } else {
            amputedCRS = sourceCRS;
        }
        try {
            assertNotNull(opFactory.createOperation(amputedCRS, targetCRS));
            fail("Operation without Bursa-Wolf parameters should not have been allowed.");
        } catch (OperationNotFoundException excption) {
            // This is the expected exception.
        }
        /*
         * Try again with hints, asking for a lenient factory.
         */
        CoordinateOperationFactory lenientFactory;
        Hints hints = new Hints(Hints.LENIENT_DATUM_SHIFT, Boolean.FALSE);
        lenientFactory = AuthorityFactoryFinder.getCoordinateOperationFactory(hints);
        assertSame(opFactory, lenientFactory);
        hints.put(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
        lenientFactory = AuthorityFactoryFinder.getCoordinateOperationFactory(hints);
        assertNotSame(opFactory, lenientFactory);
        final CoordinateOperation lenient = lenientFactory.createOperation(amputedCRS, targetCRS);
        assertSame(amputedCRS, lenient.getSourceCRS());
        assertSame( targetCRS, lenient.getTargetCRS());
        assertFalse(lenient.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_APPLIED));
        assertTrue (lenient.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_OMITTED));

        transform = lenient.getMathTransform();
        validate();
        tolerance = 1E-6;
        assertTransformEquals2_2(0,   0,  2.33722917, 0.0);
        assertTransformEquals2_2(20, -10, -6.66277083, 17.99814879585781);
//      assertTransformEquals2_2(lenientTr, 20, -10, -6.66277083, 17.998143675921714);
        // Note 1: Expected values above were computed with Geotoolkit (not an external library).
        // Note 2: The commented-out test is the one we get with "Abridged_Molodenski" method
        //         instead of "Molodenski".
    }

    /**
     * Tests a transformation that requires a datum shift with 7 parameters.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testDatumShift7Param() throws Exception {
        final CoordinateReferenceSystem sourceCRS = DefaultGeographicCRS.WGS84;
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.PROJCS_UTM_58S);
        CoordinateOperation operation = opFactory.createOperation(sourceCRS, targetCRS);
        if (usingDefaultFactory) {
            assertSame(sourceCRS, operation.getSourceCRS());
            assertSame(targetCRS, operation.getTargetCRS());
            assertTrue (operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_APPLIED));
            assertFalse(operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_OMITTED));
        }
        transform = operation.getMathTransform();
        validate();
        tolerance = 1E-6;
        assertTransformEquals2_2(168.1075, -21.597283333333, 822023.338884308, 7608648.67486555);
        // Note: Expected values above were computed with Geotoolkit (not an external library).

        /*
         * Try again using lenient factory. The result should be identical, since we do have
         * Bursa-Wolf parameters. This test failed before GEOT-661 fix.
         */
        final Hints hints = new Hints(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
        final CoordinateOperationFactory lenientFactory =
                AuthorityFactoryFinder.getCoordinateOperationFactory(hints);
        assertNotSame(opFactory, lenientFactory);
        operation = lenientFactory.createOperation(sourceCRS, targetCRS);
        if (usingDefaultFactory) {
            assertSame(sourceCRS, operation.getSourceCRS());
            assertSame(targetCRS, operation.getTargetCRS());
            assertTrue (operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_APPLIED));
            assertFalse(operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_OMITTED));
        }
        transform = operation.getMathTransform();
        validate();
        tolerance = 1E-6;
        assertTransformEquals2_2(168.1075, -21.597283333333, 822023.338884308, 7608648.67486555);
        // Note: Expected values above were computed with Geotoolkit (not an external library).
    }

    /**
     * Tests a CRS involving DMHS units.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testDMHS() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.GEOGCS_NAD27);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.GEOGCS_WGS84_DMHS);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertTrue(op instanceof Transformation);
        if (usingDefaultFactory) {
            assertSame(sourceCRS, op.getSourceCRS());
            assertSame(targetCRS, op.getTargetCRS());
        }
        assertFalse(transform.isIdentity());
        validate();
        if (usingDefaultFactory) {
            tolerance = 1E-6;
            // Note: Expected values below were computed with Geotoolkit (not an external library).
            //       However, it was tested with both Molodenski and Geocentric transformations.
            assertTransformEquals2_2(0.0,                   0.0,
                                     0.001654978796746043,  0.0012755944235822696);
            assertTransformEquals2_2(5.0,                   8.0,
                                     5.001262960018587,     8.001271733843957);
        }
    }

    /**
     * Tests transformation between vertical CRS.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testZIdentity() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.VERTCS_Z);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.VERTCS_Z);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        if (usingDefaultFactory) {
            assertSame(sourceCRS, op.getSourceCRS());
            assertSame(targetCRS, op.getTargetCRS());
        }
        assertTrue(op instanceof Conversion);
        assertTrue(transform.isIdentity());
        validate();
    }

    /**
     * Tests transformation between vertical CRS.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testHeightIdentity() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.VERTCS_HEIGHT);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.VERTCS_HEIGHT);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        if (usingDefaultFactory) {
            assertSame(sourceCRS, op.getSourceCRS());
            assertSame(targetCRS, op.getTargetCRS());
        }
        assertTrue(op instanceof Conversion);
        assertTrue(transform.isIdentity());
        validate();
    }

    /**
     * Tests transformation between incompatible vertical CRS.
     *
     * @throws Exception Should never happen.
     */
    @Test(expected = OperationNotFoundException.class)
    public void testIncompatibleVertical() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.VERTCS_Z);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.VERTCS_HEIGHT);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        assertNull(op); // We should not reach this point.
    }

    /**
     * Tests transformation involving 3D Geographic CRS.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testGeographic3D() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(NAD27_Z);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WGS84_Z);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertNotSame(sourceCRS, op.getSourceCRS());
        assertNotSame(targetCRS, op.getTargetCRS());
        assertTrue(op                instanceof Transformation);
        assertTrue(sourceCRS         instanceof CompoundCRS);
        assertTrue(op.getSourceCRS() instanceof GeographicCRS);   // 2D + 1D  --->  3D
        assertTrue(targetCRS         instanceof CompoundCRS);
        assertTrue(op.getTargetCRS() instanceof GeographicCRS);   // 2D + 1D  --->  3D
        assertFalse(sourceCRS.equals(targetCRS));
        assertFalse(op.getSourceCRS().equals(op.getTargetCRS()));
        assertFalse(transform.isIdentity());
        validate();
        // Note: Expected values below were computed with Geotoolkit (not an external library).
        //       However, it was tested with both Molodenski and Geocentric transformations.
        tolerance = 1E-6;
        assertTransformEquals3_3(0,                    0,                      0,
                                 0.001654978796746043, 0.0012755944235822696, 66.4042236590758);
        assertTransformEquals3_3(5,                    8,                     20,
                                 5.0012629560319874,   8.001271729856333,    120.27929787151515);
        assertTransformEquals3_3(5,                    8,                    -20,
                                 5.001262964005206,    8.001271737831601,     80.2792978901416);
        assertTransformEquals3_3(-5,                   -8,                    -20,
                                 -4.99799698932651,    -7.998735783965731,      9.007854541763663);
    }

    /**
     * Tests transformation involving 3D Geographic CRS.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testGeographic3D_ZFirst() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(Z_NAD27);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WGS84_Z);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertNotSame(sourceCRS, op.getSourceCRS());
        assertNotSame(targetCRS, op.getTargetCRS());
        assertTrue(op                instanceof Transformation);
        assertTrue(sourceCRS         instanceof CompoundCRS);
        assertTrue(op.getSourceCRS() instanceof GeographicCRS);   // 2D + 1D  --->  3D
        assertTrue(targetCRS         instanceof CompoundCRS);
        assertTrue(op.getTargetCRS() instanceof GeographicCRS);   // 2D + 1D  --->  3D
        assertFalse(sourceCRS.equals(targetCRS));
        assertFalse(op.getSourceCRS().equals(op.getTargetCRS()));
        assertFalse(transform.isIdentity());
        validate();
        // Note: Expected values below were computed with Geotoolkit (not an external library).
        //       However, it was tested with both Molodenski and Geocentric transformations.
        tolerance = 1E-6;
        assertTransformEquals3_3(0,                    0,                      0,
                                 0.001654978796746043, 0.0012755944235822696, 66.4042236590758);
        assertTransformEquals3_3(-20,                  5,                      8,
                                 5.001262964005206,    8.001271737831601,     80.2792978901416);
    }

    /**
     * Tests transformation from 3D to 2D Geographic CRS.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void test3D_to_2D() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(NAD27_Z);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.GEOGCS_WGS84_DMHS);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        if (usingDefaultFactory) {
            assertNotSame(sourceCRS, op.getSourceCRS());
            assertSame   (targetCRS, op.getTargetCRS());
        }
        assertFalse(transform.isIdentity());
        validate();
        // Note: Expected values below were computed with Geotoolkit (not an external library).
        //       However, it was tested with both Molodenski and Geocentric transformations.
        tolerance = 1E-6;
        assertTransformEquals3_2(0,                    0,                      0,
                                 0.001654978796746043, 0.0012755944235822696);
        assertTransformEquals3_2(5,                    8,                     20,
                                 5.0012629560319874,   8.001271729856333);
        assertTransformEquals3_2(5,                    8,                    -20,
                                 5.001262964005206,    8.001271737831601);
    }

    /**
     * Tests transformation from 3D to vertical CRS.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void test3D_to_Z() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(NAD27_Z);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.VERTCS_Z);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        if (usingDefaultFactory) {
            assertSame(sourceCRS, op.getSourceCRS());
            assertSame(targetCRS, op.getTargetCRS());
        }
        assertFalse(transform.isIdentity());
        validate();
        tolerance = 1E-6;
        assertTransformEquals3_1( 0,  0, 0,   0);
        assertTransformEquals3_1( 5,  8, 20, 20);
        assertTransformEquals3_1(-5, -8, 20, 20);
    }

    /**
     * Tests transformation from 2D to 3D with Z above the ellipsoid.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void test2D_to_3D() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.GEOGCS_NAD27);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WGS84_Z);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        if (usingDefaultFactory) {
            assertSame   (sourceCRS, op.getSourceCRS());
            assertNotSame(targetCRS, op.getTargetCRS());
        }
        assertFalse(transform.isIdentity());
        validate();
        // Note: Expected values below were computed with Geotoolkit (not an external library).
        //       However, it was tested with both Molodenski and Geocentric transformations.
        tolerance = 1E-6;
        assertTransformEquals2_3(0,                    0,
                                 0.001654978796746043, 0.0012755944235822696, 66.4042236590758);
        assertTransformEquals2_3(5,                    8,
                                 5.001262960018587,    8.001271733843957,    100.27929787896574);
    }

    /**
     * Should fails unless GEOT-352 has been fixed.
     *
     * @throws Exception Should never happen.
     */
    @Test(expected = OperationNotFoundException.class)
    public void testHtoZ() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(NAD27_H);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(NAD27_Z);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertNotSame(sourceCRS, op.getSourceCRS());
        assertNotSame(targetCRS, op.getTargetCRS());
        assertFalse(transform.isIdentity());
        validate();
    }

    /**
     * Should fails unless GEOT-352 has been fixed.
     *
     * @throws Exception Should never happen.
     */
    @Test(expected = OperationNotFoundException.class)
    public void testHtoH() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(NAD27_H);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WGS84_H);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertNotSame(sourceCRS, op.getSourceCRS());
        assertNotSame(targetCRS, op.getTargetCRS());
        assertFalse(transform.isIdentity());
        validate();
    }

    /**
     * Should fails unless GEOT-352 has been fixed.
     *
     * @throws Exception Should never happen.
     */
    @Test(expected = OperationNotFoundException.class)
    public void test2DtoH() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.GEOGCS_NAD27);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WGS84_H);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        if (usingDefaultFactory) {
            assertSame   (sourceCRS, op.getSourceCRS());
            assertNotSame(targetCRS, op.getTargetCRS());
        }
        assertFalse(transform.isIdentity());
        validate();
    }

    /**
     * Tests transformation from a 3D Geographic CRS to a single height.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void test3D_to_H() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(NAD27_H);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.VERTCS_HEIGHT);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        if (usingDefaultFactory) {
            assertSame(sourceCRS, op.getSourceCRS());
            assertSame(targetCRS, op.getTargetCRS());
        }
        assertFalse(transform.isIdentity());
        validate();
        tolerance = 1E-6;
        assertTransformEquals3_1( 0,  0, 0,   0);
        assertTransformEquals3_1( 5,  8, 20, 20);
        assertTransformEquals3_1(-5, -8, 20, 20);
    }

    /**
     * Tests transformation from 4D to 2D projected.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void test4D_to_2D() throws Exception {
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.PROJCS_MERCATOR);
        CoordinateReferenceSystem sourceCRS = targetCRS;
        sourceCRS = new DefaultCompoundCRS("Mercator 3D", sourceCRS, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT);
        sourceCRS = new DefaultCompoundCRS("Mercator 4D", sourceCRS, DefaultTemporalCRS.MODIFIED_JULIAN);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        if (usingDefaultFactory) {
            assertSame(sourceCRS, op.getSourceCRS());
            assertSame(targetCRS, op.getTargetCRS());
        }
        assertFalse(transform.isIdentity());
        assertTrue("The somewhat complex MathTransform chain should have been simplified " +
                   "to a single affine transform.", transform instanceof LinearTransform);
        assertTrue("The operation should be a simple axis change, not a complex" +
                   "chain of ConcatenatedOperations.", op instanceof Conversion);
    }
}
