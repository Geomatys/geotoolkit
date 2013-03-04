/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation;

import java.io.IOException;
import java.awt.geom.Point2D;
import javax.imageio.spi.ServiceRegistry;

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

import org.geotoolkit.test.referencing.WKT;
import org.apache.sis.util.Classes;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.crs.DefaultDerivedCRS;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.datum.DefaultTemporalDatum;
import org.geotoolkit.referencing.factory.FactoryDependencies;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.referencing.operation.transform.TransformTestBase;
import org.geotoolkit.referencing.operation.transform.AbstractMathTransform;

import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.WGS84;
import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.WGS84_3D;
import static org.geotoolkit.referencing.crs.DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;
import static org.geotoolkit.referencing.crs.DefaultTemporalCRS.MODIFIED_JULIAN;
import static org.geotoolkit.referencing.crs.DefaultTemporalCRS.UNIX;
import static org.geotoolkit.referencing.crs.DefaultEngineeringCRS.GENERIC_2D;
import static org.geotoolkit.referencing.crs.DefaultEngineeringCRS.CARTESIAN_2D;
import static org.geotoolkit.referencing.crs.DefaultEngineeringCRS.CARTESIAN_3D;
import static org.geotoolkit.referencing.Commons.isEpsgFactoryAvailable;
import static org.geotoolkit.metadata.iso.quality.AbstractPositionalAccuracy.*;

import org.junit.*;
import static org.junit.Assume.*;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.operation.SamplePoints.MOLODENSKY_TOLERANCE;


/**
 * Tests the default coordinate operation factory.
 * This base class tests {@link DefaultCoordinateOperationFactory}, without EPSG database.
 * The {@link AuthorityBackedFactoryTest} subclass will performs the same tests, but with
 * the EPSG database (if any) enabled.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.1
 */
public strictfp class COFactoryUsingMolodenskyTest extends TransformTestBase {
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
     * A filter to be used in constructor for selecting a {@link DefaultCoordinateOperationFactory}
     * instance not backed by an EPSG database ({@link AuthorityBackedFactory}).
     */
    static final ServiceRegistry.Filter FILTER = new ServiceRegistry.Filter() {
        @Override public boolean filter(final Object provider) {
            if (provider instanceof CoordinateOperationFactory) {
                return isUsingDefaultFactory((CoordinateOperationFactory) provider);
            }
            return true;
        }
    };

    /**
     * The hints used for fetching the factories.
     */
    private final Hints testHints;

    /**
     * Creates a new test suite which does <strong>not</strong> use {@link AuthorityBackedFactory}.
     *
     * @todo We should not need to specify any {@code Hints.DATUM_SHIFT_METHOD} in order to ensure
     *       that the default is really {@code Molodensky}. Unfortunately letting the default
     *       factory cause random test failure if a factory using a different method has been
     *       used previously and still in the cache. We need to see if this problem can be
     *       avoid after we switch to JSR-330.
     */
    public COFactoryUsingMolodenskyTest() {
        this(new Hints(FactoryFinder.FILTER_KEY, FILTER, Hints.DATUM_SHIFT_METHOD, "Molodensky"));
    }

    /**
     * Creates a new test suite using factories created from the given hints.
     *
     * @param hints The hints to use for fetching factories, or {@code null} for the default ones.
     */
    protected COFactoryUsingMolodenskyTest(final Hints hints) {
        super(AbstractMathTransform.class, hints);
        this.testHints = hints;
    }

    /**
     * Returns {@code true} if the test suite is using {@link AuthorityBackedFactory}.
     * This have an impact on the result of some test case.
     *
     * @return {@code true} if the test suite is using {@link AuthorityBackedFactory}.
     *
     * @since 3.18
     */
    protected boolean useAuthorityFactory() {
        return false;
    }

    /**
     * Returns the datum shift method used by this test.
     * Subclasses will override this value.
     *
     * @return The datum shift method used by this test.
     *
     * @since 3.16
     */
    protected String getDatumShiftMethod() {
        return "Molodensky";
    }

    /**
     * Returns the expected result for a transformation using the given sample point.
     * This method shall also set the {@linkplain #tolerance tolerance} threshold.
     * <p>
     * Subclasses may override this method in order to returns a different expected
     * result to be tested with a different threshold.
     *
     * @param  sample The sample point.
     * @param  withHeight {@code true} if the height is expected to be used in the datum change operation.
     * @return The expected result for a transformation using the given sample point.
     *
     * @since 3.16
     */
    SamplePoints.Target getExpectedResult(final SamplePoints sample, final boolean withHeight) {
        tolerance = SamplePoints.MOLODENSKY_TOLERANCE;
        return sample.tgt;
    }

    /**
     * Returns {@code true} if the given factory is <strong>not</strong> an
     * {@link AuthorityBackedFactory}, or a cached factory backed by an authority factory.
     *
     * @param  factory The factory to test.
     * @return {@code true} if the given factory is not backed by an authority factory.
     */
    protected static boolean isUsingDefaultFactory(final CoordinateOperationFactory factory) {
        if (factory instanceof AuthorityBackedFactory) {
            return false;
        }
        if (factory instanceof AbstractCoordinateOperationFactory) {
            return isUsingDefaultFactory(((AbstractCoordinateOperationFactory) factory).getBackingFactory());
        }
        return true;
    }

    /**
     * Ensures that the factory used is the one expected by this test suite.
     * Subclasses may override this method with a different test.
     */
    @Before
    public void ensureProperFactory() {
        assertTrue(isUsingDefaultFactory(opFactory));
    }

    /**
     * Ensures that positional accuracy dependencies are properly loaded. This is not needed for
     * normal execution, but JUnit behavior with class loaders is sometime surprising.
     */
    @Before
    public void ensureClassLoaded() {
        assertNotNull(DATUM_SHIFT_APPLIED);
        assertNotNull(DATUM_SHIFT_OMITTED);
    }

    /**
     * Initialize the {@link #messageOnFailure} field.
     */
    @Before
    public void initMessageOnFailure() {
        final StringBuilder buffer = new StringBuilder("Test failure in ")
                .append(Classes.getShortClassName(this))
                .append("[datumShiftMethod=").append(getDatumShiftMethod())
                .append(", isEpsgFactoryAvailable=").append(isEpsgFactoryAvailable())
                .append(']').append(System.getProperty("line.separator", "\n"));
        final FactoryDependencies dep = new FactoryDependencies(opFactory);
        try {
            dep.print(buffer);
        } catch (IOException e) {
            // Should never happen, since we are printing in a StringBuilder.
            throw new AssertionError(e);
        }
        messageOnFailure = buffer.toString();
    }

    /**
     * Make sure that a factory can be find in the presence of some global hints.
     *
     * @see <a href="http://jira.codehaus.org/browse/GEOT-1618">GEOT-1618</a>
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
        tolerance = MOLODENSKY_TOLERANCE;
        verifyTransform(new double[] {170, 50}, new double[] {0, 0});
        transform = transform.inverse();
        verifyTransform(new double[] {0, 0}, new double[] {170, 50});
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
        tolerance  = MOLODENSKY_TOLERANCE;
        λDimension = new int[] {0};
        final boolean isGeoc = "Geocentric".equals(getDatumShiftMethod());
        // Note: Expected values below were computed with Geotk (not an external library).
                    verifyTransform(new double[] {-180, -88.21076182660325}, new double[] {-180, -88.21076182655470});
        if (isGeoc) verifyTransform(new double[] {+180,  85.41283436546335}, new double[] {+180,  85.41283436548373});
        else        verifyTransform(new double[] {+180,  85.41283436546335}, new double[] {-180,  85.41283436531322});
    }

    /**
     * Tests a transformation that requires a datum shift. The datum shift method should be
     * either "Molodensky" or "Abridged Molodensky" with Bursa-Wolf parameters taken from
     * the {@code TOWGS84} declaration in the {@link WKT#GEOGCS_NTF} string.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testDatumShift() throws Exception {
        assumeTrue(isEpsgFactoryAvailable());
        final String datumShiftMethod = getDatumShiftMethod();
        assertEquals("Factory is not using the expected datum shift method.", datumShiftMethod,
                ((Factory) opFactory).getImplementationHints().get(Hints.DATUM_SHIFT_METHOD));
        final boolean isAbridged;
        if (datumShiftMethod == null || datumShiftMethod.equals("Geocentric") || datumShiftMethod.equals("Molodensky")) {
            isAbridged = false;
        } else if (datumShiftMethod.equals("Abridged Molodensky")) {
            isAbridged = true;
        } else {
            fail("Unexpected datum shift method: " + datumShiftMethod);
            return;
        }
        /*
         * Transform a point using the information provided in the TOWGS84 element.
         */
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.GEOGCS_NTF);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.GEOGCS_WGS84);
        final CoordinateOperation operation = opFactory.createOperation(sourceCRS, targetCRS);
        assertSame (sourceCRS, operation.getSourceCRS());
        assertSame (targetCRS, operation.getTargetCRS());
        assertTrue (operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_APPLIED));
        assertFalse(operation.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_OMITTED));
        transform = operation.getMathTransform();
        validate();
        tolerance = MOLODENSKY_TOLERANCE;
        /*
         * Note: Expected values below were computed with Geotk (not an external library).
         *       However, it was tested with both Molodenski and Geocentric transformations.
         */
        verifyTransform(new double[] {0, 0}, new double[] {2.3367521703619816, 0.0028940088671177986});
        if (isAbridged) verifyTransform(new double[] {20, -10}, new double[] {-6.663517586507632, 18.00134007052471});
        else            verifyTransform(new double[] {20, -10}, new double[] {-6.663517606186469, 18.00134508026729});
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
        } catch (OperationNotFoundException exception) {
            // This is the expected exception.
        }
        /*
         * We will try again with hints, asking for a lenient factory. The transform should now
         * succeed despite the missing {@code TOWGS84} element. But first, just as an opportunist
         * test, make sure that when LENIENT_DATUM_SHIFT is set to FALSE, we get the same factory
         * than the one used above.
         */
        CoordinateOperationFactory lenientFactory;
        final Hints hints = new Hints(testHints);
        assertNull(hints.put(Hints.LENIENT_DATUM_SHIFT, Boolean.FALSE));
        lenientFactory = AuthorityFactoryFinder.getCoordinateOperationFactory(hints);
        assertSame(opFactory, lenientFactory);
        /*
         * Now ensure that the lenient factory is different than the default one and get the
         * transform. Note that the transform will still contain an [Abridged] Molodensky
         * transform because of the ellipsoid changes, but the dx, dy, dz terms should be 0.
         */
        assertEquals(Boolean.FALSE, hints.put(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE));
        lenientFactory = AuthorityFactoryFinder.getCoordinateOperationFactory(hints);
        assertNotSame(opFactory, lenientFactory);
        if (datumShiftMethod != null) {
            assertEquals("Factory is not using the expected datum shift method.", datumShiftMethod,
                    ((Factory) lenientFactory).getImplementationHints().get(Hints.DATUM_SHIFT_METHOD));
        }
        final CoordinateOperation lenient = lenientFactory.createOperation(amputedCRS, targetCRS);
        assertSame(amputedCRS, lenient.getSourceCRS());
        assertSame( targetCRS, lenient.getTargetCRS());
        assertFalse(lenient.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_APPLIED));
        assertTrue (lenient.getCoordinateOperationAccuracy().contains(DATUM_SHIFT_OMITTED));

        transform = lenient.getMathTransform();
        validate();
        tolerance = MOLODENSKY_TOLERANCE;
        /*
         * Note: Expected values below were computed with Geotk (not an external library).
         *       However, it was tested with both Molodenski and Geocentric transformations.
         */
        verifyTransform(new double[] {0, 0}, new double[] {2.33722917, 0});
        if (isAbridged) verifyTransform(new double[] {20, -10}, new double[] {-6.66277083, 17.99814367592171});
        else            verifyTransform(new double[] {20, -10}, new double[] {-6.66277083, 17.99814879585781});
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
        tolerance = MOLODENSKY_TOLERANCE;
        verifyTransform(new double[] {168.1075, -21.597283333333},
                        new double[] {822023.338884308, 7608648.67486555});
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
        tolerance = MOLODENSKY_TOLERANCE;
        verifyTransform(new double[] {168.1075, -21.597283333333},
                        new double[] {822023.338884308, 7608648.67486555});
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
        assertTrue(isTransformation(op));
        if (useAuthorityFactory()) {
            assertNotSame(sourceCRS, op.getSourceCRS());
            assertNotSame(targetCRS, op.getTargetCRS());
            assertEqualsApproximatively(sourceCRS, op.getSourceCRS(), true);
            assertEqualsIgnoreMetadata (targetCRS, op.getTargetCRS(), false);
            assertEquals("15978", op.getIdentifiers().iterator().next().getCode());
            // Coordinate operation "NAD27 to WGS 84 (88)" for Cuba. May not be the most
            // appropriate operation, but this is the one selected by the current ordering
            // criterion in the SQL statements (GEOTK-80).
        } else {
            assertSame(sourceCRS, op.getSourceCRS());
            assertSame(targetCRS, op.getTargetCRS());
            assertTrue(op.getIdentifiers().isEmpty());
        }
        transform = op.getMathTransform();
        assertFalse(transform.isIdentity());
        validate();
        // Horizontal tolerance will be set by the call to getExpectedResults.
        for (final SamplePoints sample : SamplePoints.NAD27_TO_WGS84) {
            final SamplePoints.Source src = sample.src;
            final SamplePoints.Target tgt = getExpectedResult(sample, false);
            verifyTransform(new double[] {src.φ, src.λ}, new double[] {tgt.φ, tgt.λ});
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
        assertEquals(Matrices.create(3, 4, new double[] {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 0, 1
        }), ((LinearTransform) transform).getMatrix());

        transform = opFactory.createOperation(WGS84, WGS84_3D).getMathTransform();
        validate();
        assertTrue(transform instanceof LinearTransform);
        assertEquals(2, transform.getSourceDimensions());
        assertEquals(3, transform.getTargetDimensions());
        assertEquals(Matrices.create(4, 3, new double[] {
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
        assertTrue(Matrices.create(5, 4, new double[] {
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
        assertNotSame(sourceCRS, op.getSourceCRS());
        assertNotSame(targetCRS, op.getTargetCRS());
        assertTrue(isTransformation(op));
        assertTrue(sourceCRS         instanceof CompoundCRS);
        assertTrue(op.getSourceCRS() instanceof GeographicCRS);   // 2D + 1D  --->  3D
        assertTrue(targetCRS         instanceof CompoundCRS);
        assertTrue(op.getTargetCRS() instanceof GeographicCRS);   // 2D + 1D  --->  3D
        assertNotDeepEquals(sourceCRS, targetCRS);
        assertNotDeepEquals(op.getSourceCRS(), op.getTargetCRS());
        if (useAuthorityFactory()) {
            assertEquals("15978", op.getIdentifiers().iterator().next().getCode());
            // Coordinate operation "NAD27 to WGS 84 (88)" for Cuba. May not be the most
            // appropriate operation, but this is the one selected by the current ordering
            // criterion in the SQL statements (GEOTK-80).
        } else {
            assertTrue(op.getIdentifiers().isEmpty());
        }
        transform  = op.getMathTransform();
        assertFalse(transform.isIdentity());
        validate();
        zTolerance = 0.01;
        zDimension = new int[] {2};
        // Horizontal tolerance will be set by the call to getExpectedResults.
        for (final SamplePoints sample : SamplePoints.NAD27_TO_WGS84) {
            final SamplePoints.Source src = sample.src;
            final SamplePoints.Target tgt = getExpectedResult(sample, true);
            verifyTransform(new double[] {src.φ, src.λ, src.h},
                            new double[] {tgt.φ, tgt.λ, tgt.h});
        }
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
        assertNotSame(sourceCRS, op.getSourceCRS());
        assertNotSame(targetCRS, op.getTargetCRS());
        assertTrue(isTransformation(op));
        assertTrue(sourceCRS         instanceof CompoundCRS);
        assertTrue(op.getSourceCRS() instanceof GeographicCRS);   // 2D + 1D  --->  3D
        assertTrue(targetCRS         instanceof CompoundCRS);
        assertTrue(op.getTargetCRS() instanceof GeographicCRS);   // 2D + 1D  --->  3D
        assertFalse(sourceCRS.equals(targetCRS));
        assertFalse(op.getSourceCRS().equals(op.getTargetCRS()));
        if (useAuthorityFactory()) {
            assertEquals("15978", op.getIdentifiers().iterator().next().getCode());
            // Coordinate operation "NAD27 to WGS 84 (88)" for Cuba. May not be the most
            // appropriate operation, but this is the one selected by the current ordering
            // criterion in the SQL statements (GEOTK-80).
        } else {
            assertTrue(op.getIdentifiers().isEmpty());
        }
        transform = op.getMathTransform();
        assertFalse(transform.isIdentity());
        validate();
        zTolerance = 0.01;
        zDimension = new int[] {2, 0};
        // Horizontal tolerance will be set by the call to getExpectedResults.
        for (final SamplePoints sample : SamplePoints.NAD27_TO_WGS84) {
            final SamplePoints.Source src = sample.src;
            final SamplePoints.Target tgt = getExpectedResult(sample, true);
            verifyTransform(new double[] {src.h, src.φ, src.λ},
                            new double[] {tgt.φ, tgt.λ, tgt.h});
        }
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
        assertNotSame(sourceCRS, op.getSourceCRS());
        if (useAuthorityFactory()) {
            assertEquals("15978", op.getIdentifiers().iterator().next().getCode());
            // Coordinate operation "NAD27 to WGS 84 (88)" for Cuba. May not be the most
            // appropriate operation, but this is the one selected by the current ordering
            // criterion in the SQL statements (GEOTK-80).
        } else {
            assertTrue(op.getIdentifiers().isEmpty());
        }
        transform = op.getMathTransform();
        assertFalse(transform.isIdentity());
        validate();
        /*
         * A high z tolerance value is normal because the 2D CRS lost the height.
         * So the tolerance must be as high as the z value used in the test data.
         */
        zTolerance = 200;
        zDimension = new int[] {2};
        // Horizontal tolerance will be set by the call to getExpectedResults.
        for (final SamplePoints sample : SamplePoints.NAD27_TO_WGS84) {
            final SamplePoints.Source src = sample.src;
            final SamplePoints.Target tgt = getExpectedResult(sample, true);
            verifyTransform(new double[] {src.φ, src.λ, src.h},
                            new double[] {tgt.φ, tgt.λ});
        }
    }

    /**
     * Tests transformation from 3D to 2D CRS where the last dimension of the 3D CRS is time.
     * This test case reproduces a situation which have been observed in practice.
     *
     * @throws Exception Should never happen.
     */
    @Test
    public void testGeoTemporal_to_Display() throws Exception {
        final CoordinateReferenceSystem sourceCRS = new DefaultCompoundCRS("Test3D", WGS84, UNIX);
        final CoordinateReferenceSystem targetCRS = new DefaultDerivedCRS("Display", WGS84,
                MathTransforms.linear(Matrices.create(3, 3, new double[]
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
        tolerance = MOLODENSKY_TOLERANCE;
        isInverseTransformSupported = false;
        verifyTransform(new double[] { 0,  0,  0}, new double[] { 0});
        verifyTransform(new double[] { 5,  8, 20}, new double[] {20});
        verifyTransform(new double[] {-5, -8, 20}, new double[] {20});
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
        if (useAuthorityFactory()) {
            assertNotSame(sourceCRS, op.getSourceCRS());
            assertNotSame(targetCRS, op.getTargetCRS());
            assertEqualsApproximatively(sourceCRS, op.getSourceCRS(), true);
            assertNotDeepEquals        (targetCRS, op.getTargetCRS());
            assertEquals("15978", op.getIdentifiers().iterator().next().getCode());
            // Coordinate operation "NAD27 to WGS 84 (88)" for Cuba. May not be the most
            // appropriate operation, but this is the one selected by the current ordering
            // criterion in the SQL statements (GEOTK-80).
        } else {
            assertSame   (sourceCRS, op.getSourceCRS());
            assertNotSame(targetCRS, op.getTargetCRS());
            assertTrue(op.getIdentifiers().isEmpty());
        }
        transform  = op.getMathTransform();
        assertFalse(transform.isIdentity());
        validate();
        zTolerance = 0.01;
        zDimension = new int[] {2};
        // Horizontal tolerance will be set by the call to getExpectedResults.
        for (final SamplePoints sample : SamplePoints.NAD27_TO_WGS84) {
            final SamplePoints.Source src = sample.src;
            final SamplePoints.Target tgt = getExpectedResult(sample, false);
            verifyTransform(new double[] {src.φ, src.λ},
                            new double[] {tgt.φ, tgt.λ, tgt.h0});
        }
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
     *
     * @see <a href="http://jira.codehaus.org/browse/GEOT-352">GEOT-352</a>
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
        tolerance = MOLODENSKY_TOLERANCE;
        isInverseTransformSupported = false;
        verifyTransform(new double[] { 0,  0,  0}, new double[] { 0});
        verifyTransform(new double[] { 5,  8, 20}, new double[] {20});
        verifyTransform(new double[] {-5, -8, 20}, new double[] {20});
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
        tolerance = MOLODENSKY_TOLERANCE;
        isInverseTransformSupported = false;
        verifyTransform(new double[] {   0,     0,  0,    0}, new double[] {   0,     0});
        verifyTransform(new double[] {1000, -2000, 20, 4000}, new double[] {1000, -2000});
    }

    /**
     * A test which is not expected to work without EPSG database.
     * The {@link AuthorityBackedFactoryTest} subclass will override
     * this method with a real test.
     *
     * @throws Exception If an error occurred while creating the operation.
     *
     * @since 3.16
     */
    @Test(expected = OperationNotFoundException.class)
    public void testProjected2D_withMeridianShift() throws Exception {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromWKT(WKT.PROJCS_LAMBERT_CONIC_NTF);
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromWKT(WKT.PROJCS_MERCATOR);
        assertNotNull(opFactory.createOperation(sourceCRS, targetCRS));
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
        assumeTrue(isEpsgFactoryAvailable());
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
