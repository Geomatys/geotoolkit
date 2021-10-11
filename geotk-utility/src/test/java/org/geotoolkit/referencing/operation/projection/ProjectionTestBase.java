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

import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.operation.transform.TransformTestBase;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.DefaultOperationMethod;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.apache.sis.referencing.operation.transform.MathTransforms;

import static java.lang.StrictMath.*;
import static java.util.Collections.singletonMap;
import static org.opengis.test.Assert.*;


/**
 * Base class for tests of {@link UnitaryProjection} implementations.
 *
 * @author Martin Desruisseaux (Geomatys)
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

    static OperationMethod wrap(final ParameterDescriptorGroup descriptor) {
        return new DefaultOperationMethod(singletonMap(DefaultOperationMethod.NAME_KEY, "Test"), 2, 2, descriptor);
    }

    /**
     * Returns default projection parameter for the given descriptor.
     *
     * @param  descriptor The descriptor for which to create projection parameters.
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @param  numStandardParallels Number of standard parallels (0, 1 or 2).
     * @return Newly created projection parameters.
     */
    @SuppressWarnings("fallthrough")
    static Parameters parameters(final OperationMethod descriptor,
            final boolean ellipse, final int numStandardParallels)
    {
        final ParameterValueGroup values = descriptor.getParameters().createValue();
        final Ellipsoid ellipsoid = (ellipse ? CommonCRS.WGS84 : CommonCRS.SPHERE).ellipsoid();
        values.parameter("semi_major").setValue(ellipsoid.getSemiMajorAxis());
        values.parameter("semi_minor").setValue(ellipsoid.getSemiMinorAxis());
        switch (numStandardParallels) {
            default: fail("Unexpected number of standard parallels.");
            case 2:  values.parameter("standard_parallel_2").setValue(0);
            case 1:  values.parameter("standard_parallel_1").setValue(0);
            case 0:  break;
        }
        return Parameters.castOrWrap(values);
    }

    /**
     * Returns the full projection from the given unitary projection.
     *
     * @param  projection The unitary projection (or the "kernel").
     * @return The complete projection, from degrees to linear units.
     */
    static MathTransform concatenated(final UnitaryProjection projection) {
        try {
            return projection.createMapProjection(
                    org.apache.sis.internal.system.DefaultFactories.forBuildin(
                            org.opengis.referencing.operation.MathTransformFactory.class));
        } catch (org.opengis.util.FactoryException e) {
            throw new AssertionError(e); // TODO
        }
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
        return unitary(transform).getClass().getSimpleName().equals("Spherical");
    }

    /**
     * Returns {@code true} if the current transform is one of the {@code Spherical}
     * inner classes.
     */
    final boolean isSpherical() {
        return isSpherical(transform);
    }
}
