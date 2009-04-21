/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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
import static java.lang.Math.*;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.internal.referencing.ParameterizedAffine;
import org.geotoolkit.referencing.operation.transform.Parameterized;
import org.geotoolkit.referencing.operation.provider.EquidistantCylindrical;
import static org.geotoolkit.internal.referencing.Identifiers.*;


/**
 * Equidistant Cylindrical projection (EPSG codes 9842, 9823). See the
 * <A HREF="http://mathworld.wolfram.com/CylindricalEquidistantProjection.html">Cylindrical
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
 *       EPSG Guidence Note Number 7 part 2, Version 24.</li>
 * </ul>
 *
 * @author John Grange
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.0
 *
 * @since 2.2
 * @module
 */
public class Equirectangular extends UnitaryProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -848975059471102069L;

    /**
     * Creates an Equidistant Cylindrical projection from the given parameters. The
     * descriptor argument is usually one of the {@code PARAMETERS} constants defined
     * in the {@link EquidistantCylindrical} class or a subclass, but is not restricted to.
     * If a different descriptor is supplied, it is user's responsability to ensure that it
     * is suitable to an Equidistant Cylindrical projection.
     *
     * @param  descriptor Typically {@link EquidistantCylindrical#PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.0
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
     * @version 3.0
     *
     * @since 3.0
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
     * Transforms the specified (<var>&lambda;</var>,<var>&phi;</var>) coordinates
     * (units in radians) and stores the result in {@code dstPts} (linear distance
     * on a unit sphere).
     */
    @Override
    protected void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        final double y = srcPts[srcOff + 1]; // Must be before writing x.
        dstPts[dstOff] = rollLongitude(srcPts[srcOff]);
        dstPts[dstOff + 1] = y;
    }

    /**
     * Converts a list of coordinate point ordinal values.
     *
     * {@note We override the super-class method only as an optimization in the special case
     *        where the target coordinates are writen at the same locations than the source
     *        coordinates. In such case, we can take advantage of the fact that the phi value
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
        if (verifyCoordinateRanges()) {
            verifyGeographicRanges(srcPts, srcOff, numPts);
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
    protected void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        final double y = srcPts[srcOff + 1];                // Must be before writing x.
        dstPts[dstOff] = unrollLongitude(srcPts[srcOff]);   // Must be before writing y.
        dstPts[dstOff + 1] = y;
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

    /**
     * Returns an estimation of the error in linear distance on the unit ellipse. In
     * the case of Equirectangular projection the error is close to zero everywhere.
     */
    @Override
    double getErrorEstimate(final double lambda, final double phi) {
        return 0;
    }
}
