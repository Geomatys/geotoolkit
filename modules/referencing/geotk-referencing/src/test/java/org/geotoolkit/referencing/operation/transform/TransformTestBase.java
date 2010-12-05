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
package org.geotoolkit.referencing.operation.transform;

import java.util.Random;
import java.io.PrintStream;
import java.awt.geom.Point2D;
import javax.measure.unit.Unit;

import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.ConcatenatedOperation;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.TransformException;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.geometry.DirectPosition;

import org.geotoolkit.test.Commons;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.math.Statistics;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.io.wkt.FormattableObject;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.geometry.GeneralDirectPosition;

import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.util.converter.Classes.*;

import org.opengis.test.Validators;


/**
 * Base class for tests of {@link MathTransform} implementations. This base class inherits
 * the convenience methods defined in GeoAPI and adds a few {@code asserts} statements.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.0
 */
public abstract class TransformTestBase extends org.opengis.test.referencing.TransformTestCase {
    /**
     * The number of ordinates to use for stressing the math transform. We use a number that
     * encompass at least 2 time the default buffer size in order to test the code that use
     * the buffer. We add an arbitrary number just for making the transform job harder.
     */
    static final int ORDINATE_COUNT = AbstractMathTransform.MAXIMUM_BUFFER_SIZE * 2 + 137;

    /**
     * The tolerance level for height above the ellipsoid. This tolerance is usually
     * higher than the tolerance level for horizontal ordinate values.
     */
    protected double zTolerance = 1E-2;

    /**
     * The datum factory to use for testing.
     */
    protected final DatumFactory datumFactory;

    /**
     * The coordinate reference system factory to use for testing.
     */
    protected final CRSFactory crsFactory;

    /**
     * The math transform factory to use for testing.
     */
    protected final MathTransformFactory mtFactory;

    /**
     * The transformation factory to use for testing.
     */
    protected final CoordinateOperationFactory opFactory;

    /**
     * Set to {@code true} for sending debugging information to the standard output stream.
     */
    private boolean verbose = false;

    /**
     * Creates a new test case using the given hints for fetching the factories.
     *
     * @param type  The base class of the transform being tested.
     * @param hints The hints to use for fetching factories, or {@code null} for the default ones.
     */
    protected TransformTestBase(final Class<? extends MathTransform> type, final Hints hints) {
        assertTrue("Tests should be run with assertions enabled.", type.desiredAssertionStatus());

        datumFactory = FactoryFinder.getDatumFactory(hints);
        crsFactory   = FactoryFinder.getCRSFactory(hints);
        mtFactory    = FactoryFinder.getMathTransformFactory(hints);
        opFactory    = FactoryFinder.getCoordinateOperationFactory(hints);
    }

    /**
     * Turns this test suite in verbose mode. If this method is invoked, then debugging
     * informations will be sent to the standard output stream.
     */
    protected final void verbose() {
        verbose = true;
        final PrintStream out = System.out;
        out.println("Factory classes");
        out.print  ("├─ Datum                : "); out.println(getShortClassName(datumFactory));
        out.print  ("├─ CRS                  : "); out.println(getShortClassName(datumFactory));
        out.print  ("├─ Math Transform       : "); out.println(getShortClassName(mtFactory));
        out.print  ("└─ Coordinate Operation : "); out.println(getShortClassName(opFactory));
        out.println();
    }

    /**
     * Returns {@code true} if the given operation is, directly or indirectly, a transformation.
     * This method returns {@code true} if the operation is either a {@link Transformation}, or
     * a {@link ConcatenatedOperation} in which at least one step is a transformation.
     *
     * @param  operation The operation to test.
     * @return {@code true} if the given operation is, directly or indirectly, a transformation.
     *
     * @since 3.16
     */
    protected static boolean isTransformation(final CoordinateOperation operation) {
        if (operation instanceof Transformation) {
            return true;
        }
        if (operation instanceof ConcatenatedOperation) {
            for (final SingleOperation step : ((ConcatenatedOperation) operation).getOperations()) {
                if (step instanceof Transformation) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Validates the current {@linkplain #transform transform}. This method verifies that
     * the transform implements {@link MathTransform1D} or {@link MathTransform2D} if the
     * transform dimension suggests that it should.
     *
     * @see Validators#validate(MathTransform)
     */
    protected final void validate() {
        assertNotNull("Transform field must be assigned a value.", transform);
        Validators.validate(transform);
        final int dimension = transform.getSourceDimensions();
        if (transform.getTargetDimensions() == dimension) {
            assertEquals("MathTransform1D", dimension == 1, (transform instanceof MathTransform1D));
            assertEquals("MathTransform2D", dimension == 2, (transform instanceof MathTransform2D));
        } else {
            assertFalse("MathTransform1D", transform instanceof MathTransform1D);
            assertFalse("MathTransform2D", transform instanceof MathTransform2D);
        }
    }

    /**
     * Prints the current {@linkplain #transform transform} as internal WKT. This method is for
     * debugging purpose only. It should never be invoked after the test suite is "final".
     */
    protected final void printInternalWKT() {
        final String name;
        final MathTransform transform = this.transform;
        if (transform instanceof AbstractMathTransform) {
            name = ((AbstractMathTransform) transform).getName();
        } else {
            name = Classes.getShortClassName(transform);
        }
        System.out.println("\nInternal WKT of " + name + ':');
        if (transform instanceof FormattableObject) {
            System.out.println(((FormattableObject) transform).toWKT(FormattableObject.INTERNAL, 2));
        } else {
            System.out.println(transform);
        }
    }

    /**
     * Prints the current {@linkplain #transform transform} WKT as Java code snipset. This method
     * is a helper tool for building test cases. This method may be invoked temporarily while the
     * developer is creating the test suite, but should never be invoked after the test suite is
     * "final".
     */
    protected final void printAsJavaCode() {
        Commons.printAsJavaCode(String.valueOf(transform));
    }

    /**
     * Asserts that the current {@linkplain #transform transform} produces the given WKT.
     *
     * @param  expected The expected WKT.
     * @return The actual WKT.
     *
     * @todo We may need to allow this method to use the {@linkplain #tolerance} value
     *       when comparing floating point numbers, but that would require a kind of WKT
     *       parsing in this method.
     */
    protected final String assertWktEquals(String expected) {
        assertNotNull("Transform field must be assigned a value.", transform);
        assertEquals("WKT comparison with tolerance not yet implemented.", 0.0, tolerance, 0.0);
        expected = Commons.decodeQuotes(expected);
        final String actual = transform.toWKT();
        final String name = (transform instanceof AbstractMathTransform) ?
                ((AbstractMathTransform) transform).getName() : null;
        assertMultilinesEquals(name, expected, actual);
        return actual;
    }

    /**
     * Asserts that the parameters of current {@linkplain #transform transform} are equal to
     * the given ones. This method can check the descriptor separately, for easier isolation of
     * mismatch in case of failure.
     *
     * @param descriptor
     *          The expected parameter descriptor, or {@code null} for bypassing this check.
     *          The descriptor is required to be strictly the same instance, since Geotk
     *          implementation returns constant values.
     * @param values
     *          The expected parameter values, or {@code null} for bypassing this check.
     *          Floating points values are compared in the units of the expected value,
     *          tolerating a difference up to the {@linkplain #tolerance(double) tolerance}
     *          threshold.
     */
    protected final void assertParameterEquals(final ParameterDescriptorGroup descriptor,
            final ParameterValueGroup values)
    {
        assertInstanceOf("TransformTestCase.transform", AbstractMathTransform.class, transform);
        final Parameterized transform = (Parameterized) this.transform;
        if (descriptor != null) {
            assertSame("ParameterDescriptor", descriptor, transform.getParameterDescriptors());
        }
        if (values != null) {
            assertSame(descriptor, values.getDescriptor());
            assertEquals(values, transform.getParameterValues());
        }
    }

    /**
     * Transforms a two-dimensional point and compare the result with the expected value.
     *
     * @param  x The x value to transform.
     * @param  y The y value to transform.
     * @param ex The expected x value.
     * @param ey The expected y value.
     * @throws TransformException If the given point can not be transformed.
     */
    protected final void assertTransformEquals2_2(
            final double  x, final double  y,
            final double ex, final double ey) throws TransformException
    {
        final DirectPosition2D source = new DirectPosition2D(x,y);
        final DirectPosition2D target = new DirectPosition2D();
        assertSame(target, transform.transform(source, target));
        final String message = "Expected ("+ex+", "+ey+"), "+
                "transformed=("+target.x+", "+target.y+")";
        assertEquals(message, ex, target.x, tolerance);
        assertEquals(message, ey, target.y, tolerance);
    }

    /**
     * Transforms a three-dimensional point and compare the result with the expected value.
     *
     * @param  x The x value to transform.
     * @param  y The y value to transform.
     * @param  z The z value to transform.
     * @param ex The expected x value.
     * @param ey The expected y value.
     * @param ez The expected z value.
     * @throws TransformException If the given point can not be transformed.
     */
    protected final void assertTransformEquals3_3(
            final double  x, final double  y, final double  z,
            final double ex, final double ey, final double ez) throws TransformException
    {
        final GeneralDirectPosition source = new GeneralDirectPosition(x,y,z);
        final GeneralDirectPosition target = new GeneralDirectPosition(3);
        assertSame(target, transform.transform(source, target));
        final String message = "Expected ("+ex+", "+ey+", "+ez+"), "+
              "transformed=("+target.ordinates[0]+", "+target.ordinates[1]+", "+target.ordinates[2]+")";
        assertEquals(message, ex, target.ordinates[0], tolerance);
        assertEquals(message, ey, target.ordinates[1], tolerance);
        assertEquals(message, ez, target.ordinates[2], zTolerance);
    }

    /**
     * Transforms a two-dimensional point and compare the result with the expected
     * three-dimensional value.
     *
     * @param  x The x value to transform.
     * @param  y The y value to transform.
     * @param ex The expected x value.
     * @param ey The expected y value.
     * @param ez The expected z value.
     * @throws TransformException If the given point can not be transformed.
     */
    protected final void assertTransformEquals2_3(
            final double  x, final double  y,
            final double ex, final double ey, final double ez) throws TransformException
    {
        final GeneralDirectPosition source = new GeneralDirectPosition(x,y);
        final GeneralDirectPosition target = new GeneralDirectPosition(3);
        assertSame(target, transform.transform(source, target));
        final String message = "Expected ("+ex+", "+ey+", "+ez+"), "+
              "transformed=("+target.ordinates[0]+", "+target.ordinates[1]+", "+target.ordinates[2]+")";
        assertEquals(message, ex, target.ordinates[0], tolerance);
        assertEquals(message, ey, target.ordinates[1], tolerance);
        assertEquals(message, ez, target.ordinates[2], zTolerance);
    }

    /**
     * Transforms a three-dimensional point and compare the result with the expected
     * two-dimensional value.
     *
     * @param  x The x value to transform.
     * @param  y The y value to transform.
     * @param  z The z value to transform.
     * @param ex The expected x value.
     * @param ey The expected y value.
     * @throws TransformException If the given point can not be transformed.
     */
    protected final void assertTransformEquals3_2(
            final double  x, final double  y, final double  z,
            final double ex, final double ey) throws TransformException
    {
        final GeneralDirectPosition source = new GeneralDirectPosition(x,y,z);
        final GeneralDirectPosition target = new GeneralDirectPosition(2);
        assertSame(target, transform.transform(source, target));
        final String message = "Expected ("+ex+", "+ey+"), "+
              "transformed=("+target.ordinates[0]+", "+target.ordinates[1]+")";
        assertEquals(message, ex, target.ordinates[0], tolerance);
        assertEquals(message, ey, target.ordinates[1], tolerance);
    }

    /**
     * Transforms a three-dimensional point and compare the result with the expected
     * one-dimensional value.
     *
     * @param  x The x value to transform.
     * @param  y The y value to transform.
     * @param  z The z value to transform.
     * @param ez The expected z value.
     * @throws TransformException If the given point can not be transformed.
     */
    protected final void assertTransformEquals3_1(
            final double  x, final double  y, final double  z,
            final double ez) throws TransformException
    {
        final GeneralDirectPosition source = new GeneralDirectPosition(x,y,z);
        final GeneralDirectPosition target = new GeneralDirectPosition(1);
        assertSame(target, transform.transform(source, target));
        final String message = "Expected ("+ez+"), "+
              "transformed=("+target.ordinates[0]+")";
        assertEquals(message, ez, target.ordinates[0], zTolerance);
    }

    /**
     * Transforms a four-dimensional point and compare the result with the expected
     * two-dimensional value.
     *
     * @param  x The x value to transform.
     * @param  y The y value to transform.
     * @param  z The z value to transform.
     * @param  t The t value to transform.
     * @param ex The expected x value.
     * @param ey The expected y value.
     * @throws TransformException If the given point can not be transformed.
     *
     * @since 3.16
     */
    protected final void assertTransformEquals4_2(
            final double  x, final double  y, final double  z, final double t,
            final double ex, final double ey) throws TransformException
    {
        final GeneralDirectPosition source = new GeneralDirectPosition(x,y,z,t);
        final GeneralDirectPosition target = new GeneralDirectPosition(2);
        assertSame(target, transform.transform(source, target));
        final String message = "Expected ("+ex+", "+ey+"), "+
              "transformed=("+target.ordinates[0]+", "+target.ordinates[1]+")";
        assertEquals(message, ex, target.ordinates[0], tolerance);
        assertEquals(message, ey, target.ordinates[1], tolerance);
    }

    /**
     * Asserts that the given parameter values are equal to the expected ones within a
     * positive delta. Only the elements in the given descriptor are compared, and the
     * comparisons are done in the units declared in the descriptor.
     *
     * @param expected The expected parameter values.
     * @param actual   The actual parameter values.
     */
    private void assertEquals(final ParameterValueGroup expected, final ParameterValueGroup actual) {
        for (final GeneralParameterValue candidate : expected.values()) {
            if (!(candidate instanceof ParameterValue<?>)) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
            final ParameterValue<?> value = (ParameterValue<?>) candidate;
            final ParameterDescriptor<?> descriptor = value.getDescriptor();
            final String name = descriptor.getName().getCode();
            final Unit<?> unit = descriptor.getUnit();
            final ParameterValue<?> e = expected.parameter(name);
            final ParameterValue<?> a = actual  .parameter(name);
            if (unit != null) {
                final double f = e.doubleValue(unit);
                assertEquals(name, f, a.doubleValue(unit), tolerance(f));
            } else if (Classes.isFloat(descriptor.getValueClass())) {
                final double f = e.doubleValue();
                assertEquals(name, f, a.doubleValue(), tolerance(f));
            } else {
                assertEquals(name, e.getValue(), a.getValue());
            }
        }
    }

    /**
     * Transforms the given coordinates and verifies that the result is equals (within a positive
     * delta) to the expected ones. If the difference between an expected and actual ordinate value
     * is greater than the {@linkplain #tolerance(double) tolerance} threshold, then the assertion
     * fails.
     * <p>
     * If {@link #isInverseTransformSupported} is {@code true}, then this method will also
     * transform the expected coordinate points using the {@linkplain MathTransform#inverse
     * inverse transform} and compare with the source coordinates.
     *
     * @param  coordinates The coordinate points to transform.
     * @param  expected The expect result of the transformation, or
     *         {@code null} if {@code coordinates} is expected to be null.
     * @throws TransformException if the transformation failed.
     *
     * @since 3.15
     */
    @Override
    protected final void verifyTransform(final double[] coordinates, final double[] expected)
            throws TransformException
    {
        super.verifyTransform(coordinates, expected);
        /*
         * In addition to the GeoAPI "verifyTransform" check, check also for consistency.
         * A previous version of Geotk had a bug with the Google projection which was
         * unnoticed because of lack of this consistency check.
         */
        final float[] copy = new float[coordinates.length];
        for (int i=0; i<copy.length; i++) {
            copy[i] = (float) coordinates[i];
        }
        final float[] result = verifyConsistency(copy);
        /*
         * The comparison below needs a higher tolerance threshold, because we converted the source
         * ordinates to floating points which induce a lost of precision. The multiplication factor
         * used here has been determined empirically. The value is quite high, but this is only an
         * oportunist check anyway. The "real" test is the one performed by 'verifyConsistency'.
         */
        final double tol = Math.max(tolerance * 1000, 1);
        for (int i=0; i<expected.length; i++) {
            assertEquals(expected[i], result[i], tol);
        }
    }

    /**
     * Computes the derivative at the given point, and compares the matrix values with
     * estimations computed from the four corners of a square around the given point.
     * The transform is assumed two-dimensional.
     *
     * @param  point The point where to compute the derivative, in radians.
     * @param  delta The distance between 2 points to use for computing derivatives, in
     *         units of the source CRS. It should be a small value, for example 1 metre.
     * @throws TransformException If the derivative can not be computed,
     *         or a point can not be projected.
     *
     * @since 3.12
     */
    protected final void checkDerivative2D(final Point2D point, final double delta)
            throws TransformException
    {
        final MathTransform2D transform = (MathTransform2D) this.transform;
        final Matrix matrix = transform.derivative(point);
        assertEquals(2, matrix.getNumRow());
        assertEquals(2, matrix.getNumCol());

        final double x = point.getX();
        final double y = point.getY();
        final Point2D.Double P1 = new Point2D.Double();
        final Point2D.Double P2 = new Point2D.Double();

        // First, test the horizontal component of the derivative.
        P1.x = x - delta/2; P1.y = y;
        P2.x = x + delta/2; P2.y = y;
        assertSame(P1, transform.transform(P1, P1));
        assertSame(P2, transform.transform(P2, P2));
        assertEquals("scaleX", (P2.x - P1.x) / delta, matrix.getElement(0, 0), tolerance);
        assertEquals("shearY", (P2.y - P1.y) / delta, matrix.getElement(1, 0), tolerance);

        // Next, test the vertical component of the derivative.
        P1.x = x; P1.y = y - delta/2;
        P2.x = x; P2.y = y + delta/2;
        assertSame(P1, transform.transform(P1, P1));
        assertSame(P2, transform.transform(P2, P2));
        assertEquals("shearX", (P2.x - P1.x) / delta, matrix.getElement(0, 1), tolerance);
        assertEquals("scaleY", (P2.y - P1.y) / delta, matrix.getElement(1, 1), tolerance);
    }

    /**
     * Computes the derivative at the given point, and compares the matrix values with
     * estimations computed from the corners of a cube around the given point.
     *
     * @param  point The point where to compute the derivative, in radians.
     * @param  delta The distance between 2 points to use for computing derivatives, in
     *         units of the source CRS. It should be a small value, for example 1 metre.
     * @throws TransformException If the derivative can not be computed,
     *         or a point can not be projected.
     *
     * @since 3.15
     */
    protected final void checkDerivative(final DirectPosition point, final double delta)
            throws TransformException
    {
        final Matrix matrix = transform.derivative(point);
        final int sourceDim = matrix.getNumCol();
        final int targetDim = matrix.getNumRow();
        final GeneralDirectPosition S1 = new GeneralDirectPosition(sourceDim);
        final GeneralDirectPosition S2 = new GeneralDirectPosition(sourceDim);
        final GeneralDirectPosition T1 = new GeneralDirectPosition(targetDim);
        final GeneralDirectPosition T2 = new GeneralDirectPosition(targetDim);
        for (int i=0; i<sourceDim; i++) {
            S1.setLocation(point);
            S2.setLocation(point);
            final double ordinate = point.getOrdinate(i);
            S1.setOrdinate(i, ordinate - delta/2);
            S2.setOrdinate(i, ordinate + delta/2);
            assertSame(T1, transform.transform(S1, T1));
            assertSame(T2, transform.transform(S2, T2));
            for (int j=0; j<targetDim; j++) {
                assertEquals("derivative(" + j + ',' + i + ')',
                        (T2.getOrdinate(j) - T1.getOrdinate(j)) / delta, matrix.getElement(j, i), tolerance);
            }
        }
    }

    /**
     * Generates random numbers that can be used for the current transform.
     *
     * @param  domain     The domain of the numbers to be generated.
     * @param  randomSeed The seed for the random number generator, in order to keep the test
     *                    suite reproducible.
     * @return Random coordinates in the given domain.
     */
    protected final double[] generateRandomCoordinates(final CoordinateDomain domain, final long randomSeed) {
        assertNotNull("Transform field must be assigned a value.", transform);
        final int dimension = transform.getSourceDimensions();
        final int numPts    = ORDINATE_COUNT / dimension;
        final double[] coordinates = domain.generateRandomInput(new Random(randomSeed), dimension, numPts);
        if (verbose) {
            final PrintStream out = System.out;
            out.print("Random input coordinates for ");
            out.print(domain); out.println(" domain:");
            final Statistics[] stats = new Statistics[dimension];
            for (int i=0; i<stats.length; i++) {
                stats[i] = new Statistics();
            }
            for (int i=0; i<coordinates.length; i++) {
                stats[i % dimension].add(coordinates[i]);
            }
            Statistics.printTable(null, stats, null);
            out.println();
        }
        return coordinates;
    }

    /**
     * Stress the current {@linkplain #transform transform} using random ordinates in the given
     * domain.
     *
     * @param  domain     The domain of the numbers to be generated.
     * @param  randomSeed The seed for the random number generator, in order to keep the test
     *                    suite reproducible.
     * @throws TransformException If at transformation failed.
     */
    protected final void stress(final CoordinateDomain domain, final long randomSeed) throws TransformException {
        stress(generateRandomCoordinates(domain, randomSeed));
    }

    /**
     * Stress the current {@linkplain #transform transform} using the given coordinates.
     * This method do not {@linkplain #validate validate} the transform. This is caller's
     * responsibility to do so if applicable.
     *
     * @param  source The input coordinates to use for testing.
     * @throws TransformException If at transformation failed.
     */
    protected final void stress(final double[] source) throws TransformException {
        final MathTransform transform = this.transform;
        if (verbose) {
            final PrintStream out = System.out;
            out.print("Stress "); out.print(getShortClassName(transform));
            out.print(" from ");  out.print(transform.getSourceDimensions());
            out.print("D to ");   out.print(transform.getTargetDimensions());
            out.println('D');
        }
        final float[] asFloats = new float[source.length];
        for (int i=0; i<source.length; i++) {
            asFloats[i] = (float) source[i];
        }
        if (isInverseTransformSupported) {
            verifyInverse(source);
        }
        for (int i=0; i<source.length; i++) {
            assertEquals("Detected change in source coordinates.",
                    asFloats[i], (float) source[i], 0f); // Paranoiac check.
        }
        verifyConsistency(asFloats);
        for (int i=0; i<source.length; i++) {
            assertEquals("Detected change in source coordinates.",
                    (float) source[i], asFloats[i], 0f); // Paranoiac check.
        }
    }
}
