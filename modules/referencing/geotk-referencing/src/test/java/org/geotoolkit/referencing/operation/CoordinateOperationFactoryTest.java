/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation;

import java.awt.geom.Point2D;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.Transformation;

import org.geotoolkit.test.crs.WKT;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingTestCase;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.crs.DefaultDerivedCRS;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.datum.DefaultTemporalDatum;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.referencing.operation.transform.TransformTestCase;
import org.geotoolkit.referencing.operation.transform.AbstractMathTransform;
import org.geotoolkit.referencing.operation.transform.ProjectiveTransform;

import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.WGS84;
import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.WGS84_3D;
import static org.geotoolkit.referencing.crs.DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;
import static org.geotoolkit.referencing.crs.DefaultTemporalCRS.MODIFIED_JULIAN;
import static org.geotoolkit.referencing.crs.DefaultTemporalCRS.UNIX;
import static org.geotoolkit.referencing.crs.DefaultEngineeringCRS.GENERIC_2D;
import static org.geotoolkit.referencing.crs.DefaultEngineeringCRS.CARTESIAN_2D;
import static org.geotoolkit.referencing.crs.DefaultEngineeringCRS.CARTESIAN_3D;
import static org.geotoolkit.metadata.iso.quality.AbstractPositionalAccuracy.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 * Tests the default coordinate operation factory.
 * <p>
 * <strong>NOTE:</strong> Some tests are disabled in the particular case when the
 * {@link CoordinateOperationFactory} is actually an {@link AuthorityBackedFactory}
 * instance. This is because the later can replace source or target CRS by some CRS
 * found in the EPSG authority factory, causing {@code assertSame} to fails. It may
 * also returns a more accurate operation than the one expected from the WKT in the
 * code below, causing transformation checks to fail as well.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.1
 */
public class CoordinateOperationFactoryTest extends TransformTestCase {
    /**
     * WKT of compound CRS to be tested.
     */
    private static final String
            WGS84_Z    = "COMPD_CS[\"WGS84 + Z\","   + WKT.GEOGCS_WGS84_DMHS + ',' + WKT.VERTCS_Z      + ']',
            NAD27_Z    = "COMPD_CS[\"NAD27 + Z\","   + WKT.GEOGCS_NAD27      + ',' + WKT.VERTCS_Z      + ']',
            Z_NAD27    = "COMPD_CS[\"Z + NAD27\","   + WKT.VERTCS_Z          + ',' + WKT.GEOGCS_NAD27  + ']',
            WGS84_H    = "COMPD_CS[\"WGS84 + H\","   + WKT.GEOGCS_WGS84_DMHS + ',' + WKT.VERTCS_HEIGHT + ']',
            NAD27_H    = "COMPD_CS[\"NAD27 + Z\","   + WKT.GEOGCS_NAD27      + ',' + WKT.VERTCS_HEIGHT + ']',
            MERCATOR_Z = "COMPD_CS[\"Mercator + Z\","+ WKT.PROJCS_MERCATOR   + ',' + WKT.VERTCS_Z      + ']';

    /**
     * The hints used for fetching the factories.
     */
    private final Hints testHints;

    /**
     * {@code true} if {@link #opFactory} is <strong>not</strong> an instance of
     * {@link AuthorityBackedFactory}. See class javadoc for rational.
     */
    private boolean usingDefaultFactory;

    /**
     * Creates a new test suite.
     */
    public CoordinateOperationFactoryTest() {
        this(null);
    }

    /**
     * Creates a new test suite using factories created from the given hints.
     *
     * @param hints The hints to use for fetching factories, or {@code null} for the default ones.
     */
    protected CoordinateOperationFactoryTest(final Hints hints) {
        super(AbstractMathTransform.class, hints);
        this.testHints = hints;
    }

    /**
     * Ensures that positional accuracy dependencies are properly loaded. This is not needed for
     * normal execution, but JUnit behavior with class loaders is sometime surprising.
     */
    @Before
    public void ensureClassLoaded() {
        assertNotNull(DATUM_SHIFT_APPLIED);
        assertNotNull(DATUM_SHIFT_OMITTED);
        usingDefaultFactory = isUsingDefaultFactory(opFactory);
    }

    /**
     * Returns {@code true} if the given factory is an {@link AuthorityBackedFactory}
     * and is not backed neither by such factory.
     */
    static boolean isUsingDefaultFactory(final CoordinateOperationFactory factory) {
        if (factory instanceof AuthorityBackedFactory) {
            return false;
        }
        if (factory instanceof AbstractCoordinateOperationFactory) {
            return isUsingDefaultFactory(((AbstractCoordinateOperationFactory) factory).getBackingFactory());
        }
        return true;
    }

    /**
     * Make sure that a factory can be find in the presence of some global hints.
     *
     * @see http://jira.codehaus.org/browse/GEOT-1618
     */
    @Test
    public void testFactoryWithHints() {
        final Hints hints = new Hints(testHints);
        assertNull(hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
        assertNull(hints.put(Hints.FORCE_STANDARD_AXIS_DIRECTIONS,   Boolean.TRUE));
        assertNull(hints.put(Hints.FORCE_STANDARD_AXIS_UNITS,        Boolean.TRUE));

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

        final ParameterValueGroup param = ((SingleOperation) operation).getParameterValues();
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
        assertSame(sourceCRS, operation.getSourceCRS());
        assertSame(targetCRS, operation.getTargetCRS());
        transform = operation.getMathTransform();
        validate();
        tolerance = 1E-6;
        assertTransformEquals2_2(-180, -88.21076182660325, -180, -88.21076182655470);
        assertTransformEquals2_2(+180,  85.41283436546335, -180,  85.41283436531322);
//      assertTransformEquals2_2(+180,  85.41283436546335, +180,  85.41283436548373);
        // Note 1: Expected values above were computed with Geotk (not an external library).
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
        assertSame (sourceCRS, operation.getSourceCRS());
        assertSame (targetCRS, operation.getTargetCRS());
        assertTrue (operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_APPLIED));
        assertFalse(operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_OMITTED));
        transform = operation.getMathTransform();
        validate();
        tolerance = 1E-6;
        assertTransformEquals2_2( 0,   0,  2.3367521703619816, 0.0028940088671177986);
        assertTransformEquals2_2(20, -10, -6.663517606186469, 18.00134508026729);
        // Note: Expected values above were computed with Geotk (not an external library).
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
        final Hints hints = new Hints(testHints);
        assertNull(hints.put(Hints.LENIENT_DATUM_SHIFT, Boolean.FALSE));
        lenientFactory = AuthorityFactoryFinder.getCoordinateOperationFactory(hints);
        assertSame(opFactory, lenientFactory);
        assertEquals(Boolean.FALSE, hints.put(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE));
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
        // Note 1: Expected values above were computed with Geotk (not an external library).
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
        final CoordinateReferenceSystem sourceCRS = WGS84;
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.PROJCS_UTM_58S);
        CoordinateOperation operation = opFactory.createOperation(sourceCRS, targetCRS);
        assertSame(sourceCRS, operation.getSourceCRS());
        assertSame(targetCRS, operation.getTargetCRS());
        assertTrue (operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_APPLIED));
        assertFalse(operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_OMITTED));
        transform = operation.getMathTransform();
        validate();
        tolerance = 1E-6;
        assertTransformEquals2_2(168.1075, -21.597283333333, 822023.338884308, 7608648.67486555);
        // Note: Expected values above were computed with Geotk (not an external library).

        /*
         * Try again using lenient factory. The result should be identical, since we do have
         * Bursa-Wolf parameters. This test failed before GEOT-661 fix.
         */
        final Hints hints = new Hints(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
        final CoordinateOperationFactory lenientFactory =
                AuthorityFactoryFinder.getCoordinateOperationFactory(hints);
        assertNotSame(opFactory, lenientFactory);
        operation = lenientFactory.createOperation(sourceCRS, targetCRS);
        assertSame(sourceCRS, operation.getSourceCRS());
        assertSame(targetCRS, operation.getTargetCRS());
        assertTrue (operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_APPLIED));
        assertFalse(operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_OMITTED));
        transform = operation.getMathTransform();
        validate();
        tolerance = 1E-6;
        assertTransformEquals2_2(168.1075, -21.597283333333, 822023.338884308, 7608648.67486555);
        // Note: Expected values above were computed with Geotk (not an external library).
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
        assertSame(sourceCRS, op.getSourceCRS());
        if (usingDefaultFactory) {
            assertSame(targetCRS, op.getTargetCRS());
        }
        assertFalse(transform.isIdentity());
        validate();
        if (usingDefaultFactory) {
            tolerance = 1E-6;
            // Note: Expected values below were computed with Geotk (not an external library).
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
    public void testVerticalConversion_ellipsoidal() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.VERTCS_Z);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.VERTCS_Z);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertSame(sourceCRS, op.getSourceCRS());
        assertSame(targetCRS, op.getTargetCRS());
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
    public void testVerticalConversion_height() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.VERTCS_HEIGHT);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.VERTCS_HEIGHT);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertSame(sourceCRS, op.getSourceCRS());
        assertSame(targetCRS, op.getTargetCRS());
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
    public void testIncompatibleVerticalCRS() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.VERTCS_Z);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.VERTCS_HEIGHT);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        assertNull(op); // We should not reach this point.
    }

    /**
     * Tests a conversion of the temporal axis. We convert 1899-12-31 from a CRS having its
     * epoch at 1970-1-1 to an other CRS having its epoch at 1858-11-17, so the new value shall
     * be approximatively 41 years after the new epoch. This conversion also implies a change of
     * units from milliseconds to days.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testTemporalConversion() throws Exception {
        transform = opFactory.createOperation(UNIX, MODIFIED_JULIAN).getMathTransform();
        validate();
        final long time = DefaultTemporalDatum.DUBLIN_JULIAN.getOrigin().getTime(); // December 31, 1899 at 12:00 UTC
        assertEquals(15019.5, ((MathTransform1D) transform).transform(time / 1000.0), 1E-12);
    }

    /**
     * Tests the conversion from a geographic CRS 3D to 2D and conversely.
     * The converse part is the interesting one.
     *
     * @throws Exception Should never happen.
     *
     * @see #testGeographic2D_to_3D_withDatumShift()
     */
    @Test
    public void testGeographic2D_to_3D() throws Exception {
        transform = opFactory.createOperation(WGS84_3D, WGS84).getMathTransform();
        validate();
        assertTrue(transform instanceof LinearTransform);
        assertEquals(3, transform.getSourceDimensions());
        assertEquals(2, transform.getTargetDimensions());
        assertEquals(new GeneralMatrix(3, 4, new double[] {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 0, 1
        }), ((LinearTransform) transform).getMatrix());

        transform = opFactory.createOperation(WGS84, WGS84_3D).getMathTransform();
        validate();
        assertTrue(transform instanceof LinearTransform);
        assertEquals(2, transform.getSourceDimensions());
        assertEquals(3, transform.getTargetDimensions());
        assertEquals(new GeneralMatrix(4, 3, new double[] {
            1, 0, 0,
            0, 1, 0,
            0, 0, 0,
            0, 0, 1
        }), ((LinearTransform) transform).getMatrix());
    }

    /**
     * Tests transformation from 3D to 4D Geographic CRS where the last dimension is time.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testGeographic3D_to_4D() throws Exception {
        final CoordinateReferenceSystem sourceCRS = new DefaultCompoundCRS("Test3D", WGS84, UNIX);
        final CoordinateReferenceSystem targetCRS = new DefaultCompoundCRS("Test4D", WGS84_3D, MODIFIED_JULIAN);
        transform = opFactory.createOperation(sourceCRS, targetCRS).getMathTransform();
        validate();
        assertTrue(transform instanceof LinearTransform);
        assertEquals(3, transform.getSourceDimensions());
        assertEquals(4, transform.getTargetDimensions());
        assertTrue(new GeneralMatrix(5, 4, new double[] {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 0, 0,
            0, 0, 1./(24*60*60), 40587,
            0, 0, 0, 1
        }).equals(((LinearTransform) transform).getMatrix(), 1E-12));
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
        // Note: Expected values below were computed with Geotk (not an external library).
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
        // Note: Expected values below were computed with Geotk (not an external library).
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
    public void testGeographic3D_to_2D() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(NAD27_Z);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.GEOGCS_WGS84_DMHS);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertNotSame(sourceCRS, op.getSourceCRS());
        assertSame   (targetCRS, op.getTargetCRS());
        assertFalse(transform.isIdentity());
        validate();
        // Note: Expected values below were computed with Geotk (not an external library).
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
     * Tests transformation from 3D to 2D CRS where the last dimension of the 3D CRS is time.
     * This test case reproduce a situation which have been observed in practice.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testGeoTemporal_to_Display() throws Exception {
        final CoordinateReferenceSystem sourceCRS = new DefaultCompoundCRS("Test3D", WGS84, UNIX);
        final CoordinateReferenceSystem targetCRS = new DefaultDerivedCRS("Display", WGS84,
                ProjectiveTransform.create(new GeneralMatrix(3, 3, new double[]
        {
            12.889604810996564, 0, 482.74226804123714,
            0, -12.889604810996564, 792.4484536082475,
            0, 0, 1
        })), DefaultCartesianCS.DISPLAY);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        validate();
        assertTrue(transform instanceof LinearTransform);
        final Matrix m = ((LinearTransform) transform).getMatrix();
        assertEquals(3, m.getNumRow());
        assertEquals(4, m.getNumCol());
    }

    /**
     * Tests transformation from 3D to vertical CRS.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testGeographic3D_to_Vertical() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(NAD27_Z);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.VERTCS_Z);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertSame(sourceCRS, op.getSourceCRS());
        assertSame(targetCRS, op.getTargetCRS());
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
     *
     * @see #testGeographic2D_to_3D()
     */
    @Test
    public void testGeographic2D_to_3D_withDatumShift() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.GEOGCS_NAD27);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WGS84_Z);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertSame   (sourceCRS, op.getSourceCRS());
        assertNotSame(targetCRS, op.getTargetCRS());
        assertFalse(transform.isIdentity());
        validate();
        // Note: Expected values below were computed with Geotk (not an external library).
        //       However, it was tested with both Molodenski and Geocentric transformations.
        tolerance = 1E-6;
        assertTransformEquals2_3(0,                    0,
                                 0.001654978796746043, 0.0012755944235822696, 66.4042236590758);
        assertTransformEquals2_3(5,                    8,
                                 5.001262960018587,    8.001271733843957,    100.27929787896574);
    }

    /**
     * Tests transformation from 2D-Geographic to 3D-Projected.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testGeographic2D_to_Projected3D() throws Exception {
        final CoordinateReferenceSystem sourceCRS = WGS84;
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(MERCATOR_Z);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertSame   (sourceCRS, op.getSourceCRS());
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
    public void testGeographicCRS_HtoZ() throws Exception {
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
    public void testGeographicCRS_HtoH() throws Exception {
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
    public void testGeographic2D_to_Geographic3D_withHeight() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.GEOGCS_NAD27);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WGS84_H);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertSame   (sourceCRS, op.getSourceCRS());
        assertNotSame(targetCRS, op.getTargetCRS());
        assertFalse(transform.isIdentity());
        validate();
    }

    /**
     * Tests transformation from a 3D Geographic CRS to a single height.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testGeographic3D_to_height() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(NAD27_H);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.VERTCS_HEIGHT);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        assertSame(sourceCRS, op.getSourceCRS());
        assertSame(targetCRS, op.getTargetCRS());
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
    public void testProjected4D_to_2D() throws Exception {
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.PROJCS_MERCATOR);
        CoordinateReferenceSystem sourceCRS = targetCRS;
        sourceCRS = new DefaultCompoundCRS("Mercator 3D", sourceCRS, ELLIPSOIDAL_HEIGHT);
        sourceCRS = new DefaultCompoundCRS("Mercator 4D", sourceCRS, MODIFIED_JULIAN);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        validate();
        assertSame(sourceCRS, op.getSourceCRS());
        assertSame(targetCRS, op.getTargetCRS());
        assertFalse(transform.isIdentity());
        assertTrue("The somewhat complex MathTransform chain should have been simplified " +
                   "to a single affine transform.", transform instanceof LinearTransform);
        assertTrue("The operation should be a simple axis change, not a complex" +
                   "chain of ConcatenatedOperations.", op instanceof Conversion);
        tolerance = 1E-6;
        assertTransformEquals4_2(   0,     0,  0,    0,    0,     0);
        assertTransformEquals4_2(1000, -2000, 20, 4000, 1000, -2000);
    }

    /**
     * Tests a transformation from a 2D projection to an other 2D projection which imply a
     * change of prime meridian. The purpose of this test is to isolate the two-dimensional
     * part of the transform tested by {@link #testProjected4D_to2D_withMeridianShift()}.
     * <p>
     * This tests requires the EPSG database, because it requires the coordinate operation
     * path which is defined there.
     *
     * @throws Exception Should never happen.
     *
     * @since 3.16
     */
    @Test
    public void testProjected2D_withMeridianShift() throws Exception {
        assumeTrue(ReferencingTestCase.isEpsgFactoryAvailable());
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.PROJCS_LAMBERT_CONIC_NTF);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.PROJCS_MERCATOR);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        validate();
        assertFalse(transform.isIdentity());
        tolerance = 0.02;
        // Test using the location of Paris (48.856578°N, 2.351828°E)
        // Only after, test using a coordinate different than the prime meridian.
        assertTransformEquals2_2(601124.99, 2428693.45, 261804.30, 6218365.72);
        assertTransformEquals2_2(600000.00, 2420000.00, 260098.74, 6205194.95);
    }

    /**
     * Tests a transformation from a 4D projection to a 2D projection which imply a change of
     * prime meridian. This is the same test than {@link #testProjected2D_withMeridianShift()},
     * with extra dimension which should be just dropped.
     * <p>
     * This tests requires the EPSG database, because it requires the coordinate operation
     * path which is defined there.
     *
     * @throws Exception Should never happen.
     *
     * @since 3.16
     */
    @Test
    @Ignore
    public void testProjected4D_to2D_withMeridianShift() throws Exception {
        assumeTrue(ReferencingTestCase.isEpsgFactoryAvailable());
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.PROJCS_MERCATOR);
        CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.PROJCS_LAMBERT_CONIC_NTF);
        sourceCRS = new DefaultCompoundCRS("NTF 3D", sourceCRS, ELLIPSOIDAL_HEIGHT);
        sourceCRS = new DefaultCompoundCRS("NTF 4D", sourceCRS, MODIFIED_JULIAN);
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        transform = op.getMathTransform();
        validate();
        assertFalse(transform.isIdentity());
        tolerance = 0.02;
        // Same coordinates than testProjected2D_withMeridianShift(),
        // but with random elevation and time which should be dropped.
        assertTransformEquals4_2(601124.99, 2428693.45, 400, 1000, 261804.30, 6218365.72);
        assertTransformEquals4_2(600000.00, 2420000.00, 400, 1000, 260098.74, 6205194.95);

        // BUG!! La conversion de Lambert vers Geographique donne des angles en gradiants
        // plutôt qu'en degrés.
    }

    /**
     * Tests the conversion from Mercator projection to the Google projection. The referencing
     * module should detects that the conversion is something more complex that an identity
     * transform.
     *
     * @throws Exception Should never happen.
     *
     * @since 3.15
     */
    @Test
    public void testMercatorToGoogle() throws Exception {
        assumeTrue(ReferencingTestCase.isEpsgFactoryAvailable());
        final CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:3395");
        final CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3857");
        final CoordinateOperation op = opFactory.createOperation(sourceCRS, targetCRS);
        final MathTransform2D tr = (MathTransform2D) op.getMathTransform();
        assertFalse("Mercator to Google should not be an identity transform.", tr.isIdentity());
        final Point2D sourcePt = new Point2D.Double(334000, 4840000); // Approximatively 40°N 3°W
        final Point2D targetPt = tr.transform(sourcePt, null);
        assertEquals("Easting should be unchanged", sourcePt.getX(), targetPt.getX(), 0);
        assertEquals("Expected 27 km shift", 27476, targetPt.getY() - sourcePt.getY(), 1);
    }
}
