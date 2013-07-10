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
package org.geotoolkit.referencing.operation.transform;

import java.util.Random;
import java.io.Writer;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;

import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.datum.DatumFactory;
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
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.geometry.DirectPosition;
import org.opengis.test.Validators;
import org.opengis.test.CalculationType;
import org.opengis.test.ToleranceModifier;
import org.opengis.test.referencing.TransformTestCase;

import org.geotoolkit.test.Commons;
import org.geotoolkit.test.TestBase;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.math.Statistics;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.io.wkt.Convention;
import org.geotoolkit.io.wkt.FormattableObject;

import static java.lang.StrictMath.*;
import static org.geotoolkit.test.Assert.*;
import static org.apache.sis.util.Classes.*;


/**
 * Base class for tests of {@link MathTransform} implementations. This base class inherits
 * the convenience methods defined in GeoAPI and adds a few {@code asserts} statements.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.0
 */
public abstract strictfp class TransformTestBase extends TransformTestCase implements ToleranceModifier {
    /**
     * Ensures that the {@link TestBase} class has been initialized. We don't really
     * need to flush the output; this is just a lazy way to ensure class initialization.
     */
    static {
        TestBase.flushVerboseOutput();
    }

    /**
     * The number of ordinates to use for stressing the math transform. We use a number that
     * encompass at least 2 time the default buffer size in order to test the code that use
     * the buffer. We add an arbitrary number just for making the transform job harder.
     */
    static final int ORDINATE_COUNT = AbstractMathTransform.MAXIMUM_BUFFER_SIZE * 2 + 137;

    /**
     * The dimension of longitude, or {@code null} if none. If non-null, then the comparison of
     * ordinate values along that dimension will ignore 360° offsets.
     *
     * The first array element is the dimension during forward transforms, and the second
     * array element is the dimension during inverse transforms (can be omitted if the later
     * is the same than the dimension during forward transforms).
     */
    protected int[] λDimension;

    /**
     * The vertical dimension, or {@code null} if none. This is the dimension for which the
     * {@link #zTolerance} value will be used rather than {@link #tolerance}.
     *
     * The first array element is the dimension during forward transforms, and the second
     * array element is the dimension during inverse transforms (can be omitted if the later
     * is the same than the dimension during forward transforms).
     */
    protected int[] zDimension;

    /**
     * The tolerance level for height above the ellipsoid. This tolerance is usually higher
     * than the {@linkplain #tolerance tolerance} level for horizontal ordinate values.
     */
    protected double zTolerance;

    /**
     * An optional message to pre-concatenate to the error message if one of the {@code assert}
     * methods fail. This field shall contain information about the test configuration that may
     * be useful in determining the cause of a test failure.
     *
     * @since 3.17
     */
    protected String messageOnFailure;

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
     * The default value is {@code false}.
     */
    private boolean verbose;

    /**
     * Creates a new test case using the given hints for fetching the factories.
     *
     * @param type  The base class of the transform being tested.
     * @param hints The hints to use for fetching factories, or {@code null} for the default ones.
     */
    protected TransformTestBase(final Class<? extends MathTransform> type, final Hints hints) {
        this(FactoryFinder.getDatumFactory(hints),
             FactoryFinder.getCRSFactory(hints),
             FactoryFinder.getMathTransformFactory(hints),
             FactoryFinder.getCoordinateOperationFactory(hints));
        assertTrue("Tests should be run with assertions enabled.", type.desiredAssertionStatus());
        toleranceModifier = this;
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private TransformTestBase(
            final DatumFactory            datumFactory,
            final CRSFactory                crsFactory,
            final MathTransformFactory       mtFactory,
            final CoordinateOperationFactory opFactory)
    {
        super(datumFactory, crsFactory, mtFactory, opFactory);
        this.datumFactory = datumFactory;
        this.mtFactory    = mtFactory;
        this.crsFactory   = crsFactory;
        this.opFactory    = opFactory;
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
     * Returns the value to use from the {@link #λDimension} or {@link zDimension} for the
     * given comparison mode, or -1 if none.
     */
    @SuppressWarnings("fallthrough")
    private static int forComparison(final int[] config, final CalculationType mode) {
        if (config != null) {
            switch (mode) {
                case INVERSE_TRANSFORM: if (config.length >= 2) return config[1];
                case DIRECT_TRANSFORM:  if (config.length >= 1) return config[0];
            }
        }
        return -1;
    }

    /**
     * Ensures that longitude values are contained in the ±180° range, applying 360° shifts if needed.
     *
     * @param expected The expected ordinate value provided by the test case.
     * @param actual   The ordinate value computed by the {@linkplain #transform}.
     *
     * @since 3.19
     */
    @Override
    protected final void normalize(final DirectPosition expected, final DirectPosition actual,
            final CalculationType mode)
    {
        final int λDimension = forComparison(this.λDimension, mode);
        if (λDimension >= 0) {
            double e;
            e = expected.getOrdinate(λDimension); e -= 360*floor(e/360); expected.setOrdinate(λDimension, e);
            e =   actual.getOrdinate(λDimension); e -= 360*floor(e/360);   actual.setOrdinate(λDimension, e);
        }
    }

    /**
     * Returns the tolerance threshold for comparing the given ordinate value.
     * We override the method defined in GeoAPI in order to take in account some special cases.
     */
    @Override
    public final void adjust(final double[] tolerance, final DirectPosition coordinate, final CalculationType mode) {
        if (mode != CalculationType.IDENTITY) {
            final int zDim = forComparison(this.zDimension, mode);
            if (zDim >= 0 && zDim < tolerance.length) {
                tolerance[zDim] = zTolerance;
            }
        }
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
     * debugging purpose only.
     */
    protected final void printInternalWKT() {
        printInternalWKT(new OutputStreamWriter(System.out));
    }

    /**
     * Prints the WKT of the current transform to the given stream.
     */
    private void printInternalWKT(final Writer out) {
        final String name;
        final MathTransform transform = this.transform;
        if (transform instanceof AbstractMathTransform) {
            name = ((AbstractMathTransform) transform).getName();
        } else {
            name = getShortClassName(transform);
        }
        final TableWriter table = new TableWriter(out);
        table.setMultiLinesCells(true);
        table.writeHorizontalSeparator();
        table.write("WKT of \"");
        table.write(name);
        table.write('"');
        table.nextColumn();
        table.write("Internal WKT");
        table.writeHorizontalSeparator();
        String wkt;
        try {
            wkt = transform.toWKT();
        } catch (UnsupportedOperationException e) {
            wkt = transform.toString();
        }
        table.write(wkt);
        table.nextColumn();
        if (transform instanceof FormattableObject) {
            wkt = ((FormattableObject) transform).toWKT(Convention.INTERNAL, 2);
        } else {
            wkt = transform.toString();
        }
        table.write(wkt);
        table.writeHorizontalSeparator();
        try {
            table.flush();
        } catch (IOException e) {
            throw new AssertionError(e);
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
     * Complete the error message by pre-concatenating {@link #messageOnFailure} if non-null.
     * Then concatenate the WKT.
     */
    private String complete(String message) {
        final String lineSeparator = System.lineSeparator();
        final StringWriter buffer = new StringWriter();
        if (messageOnFailure != null) {
            buffer.append(messageOnFailure).append(lineSeparator);
        }
        printInternalWKT(buffer);
        if (message != null) {
            buffer.append(message).append(lineSeparator);
        }
        // Note: JUnit message will begin with a space.
        return buffer.append("JUnit message:").toString();
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
        assertNotNull(complete("Transform field must be assigned a value."), transform);
        assertEquals(complete("WKT comparison with tolerance not yet implemented."), 0.0, tolerance, 0.0);
        expected = Commons.decodeQuotes(expected);
        final String actual = transform.toWKT();
        final String name = (transform instanceof AbstractMathTransform) ?
                ((AbstractMathTransform) transform).getName() : null;
        assertMultilinesEquals(complete(name), expected, actual);
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
    protected final void verifyParameters(final ParameterDescriptorGroup descriptor,
            final ParameterValueGroup values)
    {
        assertInstanceOf(complete("TransformTestCase.transform"), AbstractMathTransform.class, transform);
        verifyParameters(descriptor, values, (AbstractMathTransform) transform, tolerance);
    }

    /**
     * Implementation of the above method, to be shared by {@link GeoapiTest}.
     */
    static void verifyParameters(final ParameterDescriptorGroup descriptor, final ParameterValueGroup values,
            final Parameterized transform, final double tolerance)
    {
        if (descriptor != null) {
            assertSame("ParameterDescriptor", descriptor, transform.getParameterDescriptors());
        }
        if (values != null) {
            assertSame(descriptor, values.getDescriptor());
            assertParameterEquals(values, transform.getParameterValues(), tolerance);
        }
    }

    /**
     * Transforms the given coordinates and verifies that the result is equals (within a positive
     * delta) to the expected ones. If the difference between an expected and actual ordinate value
     * is greater than the {@linkplain #tolerance tolerance} threshold, then the assertion fails.
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
        final double tol = max(tolerance * 1000, 1);
        for (int i=0; i<expected.length; i++) {
            assertEquals(expected[i], result[i], tol);
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
        assertNotNull(complete("Transform field must be assigned a value."), transform);
        final int dimension = transform.getSourceDimensions();
        final int numPts    = ORDINATE_COUNT / dimension;
        final double[] coordinates = domain.generateRandomInput(new Random(randomSeed), dimension, numPts);
        if (verbose) {
            final PrintStream out = System.out;
            out.print("Random input coordinates for ");
            out.print(domain); out.println(" domain:");
            final Statistics[] stats = new Statistics[dimension];
            for (int i=0; i<stats.length; i++) {
                stats[i] = new Statistics(null);
            }
            for (int i=0; i<coordinates.length; i++) {
                stats[i % dimension].accept(coordinates[i]);
            }
//          Statistics.printTable(null, stats, null);
            for (final Statistics s : stats) {
                System.out.println(s);
            }
            out.println();
            out.flush();
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
            out.flush();
        }
        final float[] asFloats = new float[source.length];
        for (int i=0; i<source.length; i++) {
            asFloats[i] = (float) source[i];
        }
        if (isInverseTransformSupported) {
            verifyInverse(source);
        }
        for (int i=0; i<source.length; i++) {
            assertEquals(complete("Detected change in source coordinates."),
                    asFloats[i], (float) source[i], 0f); // Paranoiac check.
        }
        verifyConsistency(asFloats);
        for (int i=0; i<source.length; i++) {
            assertEquals(complete("Detected change in source coordinates."),
                    (float) source[i], asFloats[i], 0f); // Paranoiac check.
        }
    }
}
