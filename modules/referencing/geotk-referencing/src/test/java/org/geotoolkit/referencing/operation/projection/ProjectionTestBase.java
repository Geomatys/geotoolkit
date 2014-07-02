/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.projection;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.test.CalculationType;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.operation.transform.CoordinateDomain;
import org.geotoolkit.referencing.operation.transform.TransformTestBase;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;

import static java.lang.StrictMath.*;
import static org.opengis.test.Assert.*;


/**
 * Base class for tests of {@link UnitaryProjection} implementations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 */
//@DependsOn({ProjectiveTransformTest.class, ConcatenatedTransformTest.class})
public abstract strictfp class ProjectionTestBase extends TransformTestBase {
    /**
     * The radius of the sphere used in sphere test cases.
     */
    static final double SPHERE_RADIUS = CommonCRS.SPHERE.ellipsoid().getSemiMajorAxis();

    /**
     * Creates a new test case using the given hints for fetching the factories.
     *
     * @param type  The base class of the projection being tested.
     * @param hints The hints to use for fetching factories, or {@code null} for the default ones.
     */
    protected ProjectionTestBase(final Class<? extends MathTransform> type, final Hints hints) {
        super(type, hints);
    }

    /**
     * Returns default projection parameter for the given descriptor.
     *
     * @param  The descriptor for which to create projection parameters.
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @return Newly created projection parameters.
     */
    static UnitaryProjection.Parameters parameters(final ParameterDescriptorGroup descriptor, final boolean ellipse) {
        return parameters(descriptor, ellipse, 0, UnitaryProjection.Parameters.class);
    }

    /**
     * Returns default projection parameter for the given descriptor.
     *
     * @param  The descriptor for which to create projection parameters.
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @param  numStandardParallels Number of standard parallels (0, 1 or 2).
     * @return Newly created projection parameters.
     */
    @SuppressWarnings("fallthrough")
    static <T extends UnitaryProjection.Parameters> T parameters(final ParameterDescriptorGroup descriptor,
            final boolean ellipse, final int numStandardParallels, final Class<T> type)
    {
        final ParameterValueGroup values = descriptor.createValue();
        final Ellipsoid ellipsoid = (ellipse ? CommonCRS.WGS84 : CommonCRS.SPHERE).ellipsoid();
        values.parameter("semi_major").setValue(ellipsoid.getSemiMajorAxis());
        values.parameter("semi_minor").setValue(ellipsoid.getSemiMinorAxis());
        switch (numStandardParallels) {
            default: fail("Unexpected number of standard parallels.");
            case 2:  values.parameter("standard_parallel_2").setValue(0);
            case 1:  values.parameter("standard_parallel_1").setValue(0);
            case 0:  break;
        }
        final UnitaryProjection.Parameters param;
        if (TransverseMercator.Parameters.class.isAssignableFrom(type)) {
            param = new TransverseMercator.Parameters(descriptor, values);
        } else if (ObliqueMercator.Parameters.class.isAssignableFrom(type)) {
            param = new ObliqueMercator.Parameters(descriptor, values);
        } else {
            param = new UnitaryProjection.Parameters(descriptor, values);
        }
        return type.cast(param);
    }

    /**
     * Returns the full projection from the given unitary projection.
     *
     * @param  projection The unitary projection (or the "kernel").
     * @return The complete projection, from degrees to linear units.
     */
    static MathTransform concatenated(final UnitaryProjection projection) {
        return projection.parameters.createConcatenatedTransform(projection);
    }

    /**
     * Projects the given latitude value. The longitude is fixed to zero.
     * This method is useful for testing the behavior close to poles in a
     * simple case.
     *
     * @param  phi The latitude.
     * @return The northing.
     * @throws ProjectionException if the projection failed.
     */
    final double transform(final double phi) throws ProjectionException {
        final double[] coordinate = new double[2];
        coordinate[1] = phi;
        ((UnitaryProjection) transform).transform(coordinate, 0, coordinate, 0, false);
        final double y = coordinate[1];
        if (!Double.isNaN(y) && !Double.isInfinite(y)) {
            assertEquals(0, coordinate[0], tolerance);
        }
        return y;
    }

    /**
     * Inverse projects the given northing value. The longitude is fixed to zero.
     * This method is useful for testing the behavior close to poles in a simple case.
     *
     * @param  y The northing.
     * @return The latitude.
     * @throws ProjectionException if the projection failed.
     */
    final double inverseTransform(final double y) throws ProjectionException {
        return inverseTransform(y, false);
    }

    /**
     * Same than {@link #inverseTransform(double)}, but said if the resulting longitude
     * is expected to be the anti-meridian.
     */
    final double inverseTransform(final double y, final boolean antimeridian) throws ProjectionException {
        final double[] coordinate = new double[2];
        coordinate[1] = y;
        ((UnitaryProjection) transform).inverseTransform(coordinate, 0, coordinate, 0);
        final double phi = coordinate[1];
        if (!Double.isNaN(phi)) {
            final double lambda = coordinate[0];
            assertEquals(antimeridian ? copySign(PI, lambda) : 0, lambda, tolerance);
        }
        return phi;
    }

    /**
     * Tests longitude rolling. Testing on the sphere is sufficient, since the
     * assertions contained in the {@code Spherical} nested class will compare
     * with the ellipsoidal case.
     * <p>
     * The domain should be geographic coordinates in decimal degrees.
     *
     * @param  domain The domain of the random coordinate points to generate.
     * @throws TransformException If a projection failed.
     */
    final void stressLongitudeRolling(final CoordinateDomain domain) throws TransformException {
        assertInstanceOf("This method works on unitary projections.", UnitaryProjection.class, transform);
        final UnitaryProjection projection = (UnitaryProjection) transform;
        final double centralMeridian = projection.parameters.centralMeridian;
        assertEquals("Longitude rolling", centralMeridian != 0, projection.rollLongitude());

        final MathTransform inverse = projection.inverse();
        final AffineTransform normalize = projection.parameters.normalize(true);
        final AffineTransform denormalize;
        try {
            denormalize = normalize.createInverse();
        } catch (NoninvertibleTransformException e) {
            throw new AssertionError(e);
        }
        final double[] source = generateRandomCoordinates(domain, 159484270);
        final double[] target = source.clone();
        final int numPts = target.length/2;
        normalize.  transform(target, 0, target, 0, numPts);
        projection. transform(target, 0, target, 0, numPts);
        inverse.    transform(target, 0, target, 0, numPts);
        denormalize.transform(target, 0, target, 0, numPts);
        assertCoordinatesEqual("Longitude rolling", 2, source, 0, target, 0, numPts, CalculationType.DIRECT_TRANSFORM);
    }

    /**
     * Returns the unitary projection.
     */
    private static UnitaryProjection unitary(final MathTransform transform) {
        for (final MathTransform step : MathTransforms.getSteps(transform)) {
            if (step instanceof UnitaryProjection) {
                return (UnitaryProjection) step;
            }
        }
        return null;
    }

    /**
     * Returns {@code true} if the given transform is one of the {@code Spherical}
     * inner classes.
     *
     * @param  transform The transform to test.
     * @return {@code true} if the transform is spherical.
     */
    static boolean isSpherical(final MathTransform transform) {
        return unitary(transform).isSpherical();
    }

    /**
     * Returns {@code true} if the current transform is one of the {@code Spherical}
     * inner classes.
     */
    final boolean isSpherical() {
        return isSpherical(transform);
    }
}
