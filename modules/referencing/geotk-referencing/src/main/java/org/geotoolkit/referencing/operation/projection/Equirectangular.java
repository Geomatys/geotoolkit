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
package org.geotoolkit.referencing.operation.projection;

import java.awt.geom.AffineTransform;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.internal.referencing.ParameterizedAffine;
import org.geotoolkit.referencing.operation.matrix.Matrix2;
import org.apache.sis.parameter.Parameterized;
import org.geotoolkit.referencing.operation.provider.EquidistantCylindrical;

import static java.lang.Math.*;
import static org.geotoolkit.referencing.operation.provider.UniversalParameters.*;


/**
 * <cite>Equidistant Cylindrical</cite> projection (EPSG codes 1028, 1029, <del>9842</del>, <del>9823</del>).
 * See the <A HREF="http://mathworld.wolfram.com/CylindricalEquidistantProjection.html">Cylindrical
 * Equidistant projection on MathWorld</A> for an overview. See any of the following providers
 * for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.EquidistantCylindrical}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.PlateCarree}</li>
 * </ul>
 *
 * {@section Description}
 *
 * In the particular case where the latitude of natural origin is at the equator,
 * this projection is also called <cite>Plate Carrée</cite>. This is used for example
 * in <cite>WGS84 / Plate Carrée</cite> (EPSG:32662).
 *
 * {@section References}
 * <ul>
 *   <li>John P. Snyder (Map Projections - A Working Manual,<br>
 *       U.S. Geological Survey Professional Paper 1395, 1987)</li>
 *   <li>"Coordinate Conversions and Transformations including Formulas",<br>
 *       EPSG Guidance Note Number 7 part 2, Version 24.</li>
 * </ul>
 *
 * @author John Grange
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
@Immutable
public class Equirectangular extends UnitaryProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -848975059471102069L;

    /**
     * Creates an Equidistant Cylindrical projection from the given parameters. The
     * descriptor argument is usually one of the {@code PARAMETERS} constants defined
     * in the {@link EquidistantCylindrical} class or a subclass, but is not restricted to.
     * If a different descriptor is supplied, it is user's responsibility to ensure that it
     * is suitable to an Equidistant Cylindrical projection.
     *
     * @param  descriptor Typically {@link EquidistantCylindrical#PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final Parameters parameters = new Parameters(descriptor, values);
        final Equirectangular projection = new Equirectangular(parameters);
        MathTransform2D tr = projection.createConcatenatedTransform();
        if (tr instanceof AffineTransform) {
            tr = new Affine((AffineTransform) tr, parameters);
        }
        return tr;
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected Equirectangular(final Parameters parameters) {
        super(parameters);
        if (parameters.standardParallels.length != 0) {
            throw unknownParameter(STANDARD_PARALLEL_1);
        }
        double p = abs(parameters.latitudeOfOrigin);
        parameters.latitudeOfOrigin = p;
        parameters.normalize(true).scale(cos(p = toRadians(p)), 1);
        parameters.validate();

        p = sin(p);
        p = sqrt(1 - excentricitySquared) / (1 - (p*p)*excentricitySquared);
        parameters.normalize(false).scale(p, p);
        finish();
    }

    /**
     * Returns {@code true} since this projection is implemented using spherical formulas.
     */
    @Override
    boolean isSpherical() {
        return true;
    }

    /**
     * Returns the parameter descriptors for this unitary projection. Note that
     * the returned descriptor is about the unitary projection, not the full one.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return EquidistantCylindrical.PARAMETERS;
    }

    // No need to override getParameterValues() because no additional
    // parameter are significant to a unitary Mercator projection.

    /**
     * An affine transform that remember the projection parameters. This is useful only to
     * the Equirectangular projection because it is the only one that may be simplified to
     * an affine transform.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    private static final class Affine extends ParameterizedAffine {
        /** For cross-version compatibility. */
        private static final long serialVersionUID = -4667404054723855507L;

        /** Creates a new transform from the given affine and parameters. */
        public Affine(final AffineTransform transform, final Parameterized parameters) {
            super(transform, parameters);
        }

        /** Returns the parameter descriptors for this map projection. */
        @Override
        public ParameterDescriptorGroup getParameterDescriptors() {
            return parameters.getParameterDescriptors();
        }

        /** Returns the parameter values for this map projection. */
        @Override
        public ParameterValueGroup getParameterValues() {
            return parameters.getParameterValues();
        }
    }

    /**
     * Converts the specified (<var>&lambda;</var>,<var>&phi;</var>) coordinate (units in radians)
     * and stores the result in {@code dstPts} (linear distance on a unit sphere). In addition,
     * opportunistically computes the projection derivative if {@code derivate} is {@code true}.
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate) throws ProjectionException
    {
        if (dstPts != null) {
            final double λ = srcPts[srcOff + 1]; // Must be before writing x.
            dstPts[dstOff] = rollLongitude(srcPts[srcOff]);
            dstPts[dstOff + 1] = λ;
        }
        return derivate ? new Matrix2() : null;
    }

    /**
     * Converts a list of coordinate point ordinal values.
     *
     * {@note We override the super-class method only as an optimization in the special case
     *        where the target coordinates are written at the same locations than the source
     *        coordinates. In such case, we can take advantage of the fact that the φ value
     *        is not modified by the unitary Equirectangular projection.}
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        if (srcPts != dstPts || srcOff != dstOff) {
            super.transform(srcPts, srcOff, dstPts, dstOff, numPts);
            return;
        }
        while (--numPts >= 0) {
            dstPts[dstOff] = rollLongitude(dstPts[dstOff]);
            dstOff += 2;
        }
        // Invoking Assertions.checkReciprocal(...) here would be
        // useless since it works only for non-overlapping arrays.
    }

    /**
     * Transforms the specified (<var>x</var>,<var>y</var>) coordinates
     * and stores the result in {@code dstPts} (angles in radians).
     */
    @Override
    protected void inverseTransform(final double[] srcPts, final int srcOff,
                                    final double[] dstPts, final int dstOff)
            throws ProjectionException
    {
        final double y = srcPts[srcOff + 1];                // Must be before writing x.
        dstPts[dstOff] = unrollLongitude(srcPts[srcOff]);   // Must be before writing y.
        dstPts[dstOff+1] = y;
    }

    /**
     * Returns {@code true} if this unitary projection is the identity transform.
     * The kernel of an equirectangular projection is an identity transform if it
     * doesn't performs {@linkplain #rollLongitude(double) longitude rolling}.
     */
    @Override
    public boolean isIdentity() {
        return !rollLongitude();
    }
}
