/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.operation;

import java.util.Map;
import java.util.Objects;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.ConicProjection;
import org.opengis.referencing.operation.PlanarProjection;
import org.opengis.referencing.operation.CylindricalProjection;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.apache.sis.parameter.Parameterized;
import org.apache.sis.internal.referencing.OperationMethods;
import org.apache.sis.referencing.operation.transform.PassThroughTransform;
import org.apache.sis.internal.system.Semaphores;
import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.Classes;
import org.apache.sis.util.UnsupportedImplementationException;

import static org.apache.sis.util.Utilities.deepEquals;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.internal.referencing.CRSUtilities.PARAMETERS_KEY;


/**
 * A parameterized mathematical operation on coordinates that transforms or converts
 * coordinates to another {@linkplain CoordinateReferenceSystem coordinate reference
 * system}. This coordinate operation thus uses an {@linkplain OperationMethod operation
 * method}, usually with associated parameter values.
 * <p>
 * In the Geotk implementation, the {@linkplain #getParameterValues parameter values}
 * are inferred from the {@linkplain #transform transform}. Other implementations may
 * have to override the {@link #getParameterValues} method.
 * <p>
 * This is a single (not {@linkplain DefaultConcatenatedOperation concatenated})
 * coordinate operation.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultSingleOperation extends AbstractCoordinateOperation implements SingleOperation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -2635450075620911309L;

    /**
     * The operation method.
     */
    protected final OperationMethod method;

    /**
     * The parameter values, or {@code null} for inferring it from the math transform.
     *
     * @since 3.20
     */
    ParameterValueGroup parameters;

    /**
     * Constructs a new operation with the same values than the specified defining
     * conversion, together with the specified source and target CRS. This constructor
     * is used by {@link DefaultConversion} only.
     */
    DefaultSingleOperation(final Conversion               definition,
                           final CoordinateReferenceSystem sourceCRS,
                           final CoordinateReferenceSystem targetCRS,
                           final MathTransform             transform)
    {
        super(definition, sourceCRS, targetCRS, transform);
        method = definition.getMethod();
    }

    /**
     * Constructs an operation from a set of properties.
     * The properties given in argument follow the same rules than for the
     * {@linkplain AbstractCoordinateOperation#AbstractCoordinateOperation(Map,
     * CoordinateReferenceSystem, CoordinateReferenceSystem, MathTransform)
     * super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param sourceCRS  The source CRS.
     * @param targetCRS  The target CRS.
     * @param transform  Transform from positions in the {@linkplain #getSourceCRS source CRS}
     *                   to positions in the {@linkplain #getTargetCRS target CRS}.
     * @param method     The operation method.
     */
    public DefaultSingleOperation(final Map<String,?>             properties,
                                  final CoordinateReferenceSystem sourceCRS,
                                  final CoordinateReferenceSystem targetCRS,
                                  final MathTransform             transform,
                                  final OperationMethod           method)
    {
        super(properties, sourceCRS, targetCRS, transform);
        ensureNonNull("method", method);
        this.method = method;
        if (transform != null) {
// TODO     OperationMethods.checkDimensions(method, transform, properties);
        }
        /*
         * Undocumented property. We do not document it because parameters are usually either
         * inferred from the MathTransform, or specified explicitely in a DefiningConversion.
         * However there is a few cases, for example the Molodenski transform, where none of
         * the above can apply, because the operation is implemented by a concatenation of
         * math transform and concatenations don't have ParameterValueGroup.
         */
        final Object param = properties.get(PARAMETERS_KEY);
        if (param instanceof ParameterValueGroup) {
            parameters = (ParameterValueGroup) param;
            // We don't clone on the assumption that the caller already cloned it.
        }
    }

    /**
     * Returns a coordinate operation of the specified class. This method constructs an instance
     * of {@link Transformation}, {@link ConicProjection}, {@link CylindricalProjection},
     * {@link PlanarProjection}, {@link Projection} or {@link Conversion}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param sourceCRS  The source CRS.
     * @param targetCRS  The target CRS.
     * @param transform  Transform from positions in the {@linkplain #getSourceCRS source CRS}
     *                   to positions in the {@linkplain #getTargetCRS target CRS}.
     * @param method     The operation method, or {@code null}.
     * @param type       The minimal type as <code>{@linkplain Conversion}.class</code>,
     *                   <code>{@linkplain Projection}.class</code>, <i>etc.</i>
     *                   This method may create an instance of a subclass of {@code type}.
     * @return A new coordinate operation, as an instance of the given type if possible.
     */
    public static CoordinateOperation create(final Map<String,?>            properties,
                                             final CoordinateReferenceSystem sourceCRS,
                                             final CoordinateReferenceSystem targetCRS,
                                             final MathTransform             transform,
                                             final OperationMethod           method,
                                             Class<? extends CoordinateOperation> type)
    {
        if (method != null) {
            if (method instanceof MathTransformProvider) {
                final Class<? extends SingleOperation> candidate =
                        ((MathTransformProvider) method).getOperationType();
                if (candidate != null) {
                    if (type == null) {
                        type = candidate;
                    } else if (type.isAssignableFrom(candidate)) {
                        type = candidate.asSubclass(type);
                    }
                }
            }
            if (type != null) {
                if (Transformation.class.isAssignableFrom(type)) {
                    return new DefaultTransformation(
                               properties, sourceCRS, targetCRS, transform, method);
                }
                if (ConicProjection.class.isAssignableFrom(type)) {
                    return new DefaultConicProjection(
                               properties, sourceCRS, targetCRS, transform, method);
                }
                if (CylindricalProjection.class.isAssignableFrom(type)) {
                    return new DefaultCylindricalProjection(
                               properties, sourceCRS, targetCRS, transform, method);
                }
                if (PlanarProjection.class.isAssignableFrom(type)) {
                    return new DefaultPlanarProjection(
                               properties, sourceCRS, targetCRS, transform, method);
                }
                if (Projection.class.isAssignableFrom(type)) {
                    return new DefaultProjection(
                               properties, sourceCRS, targetCRS, transform, method);
                }
                if (Conversion.class.isAssignableFrom(type)) {
                    return new DefaultConversion(
                               properties, sourceCRS, targetCRS, transform, method);
                }
            }
            return new DefaultSingleOperation(
                       properties, sourceCRS, targetCRS, transform, method);
        }
        return new AbstractCoordinateOperation(properties, sourceCRS, targetCRS, transform);
    }

    /**
     * Returns the GeoAPI interface implemented by this class.
     * The default implementation returns {@code SingleOperation.class}.
     * Subclasses implementing a more specific GeoAPI interface shall override this method.
     *
     * @return The single coordinate operation interface implemented by this class.
     */
    @Override
    public Class<? extends SingleOperation> getInterface() {
        return SingleOperation.class;
    }

    /**
     * Returns the operation method.
     */
    @Override
    public OperationMethod getMethod() {
        return method;
    }

    /**
     * Returns the parameter values. The default implementation infers the parameter
     * values from the {@linkplain #transform transform}, if possible.
     *
     * @throws UnsupportedOperationException if the parameter values can't be determined
     *         for the current math transform implementation.
     *
     * @see DefaultMathTransformFactory#createParameterizedTransform(ParameterValueGroup)
     * @see Parameterized#getParameterValues()
     */
    @Override
    public ParameterValueGroup getParameterValues() throws UnsupportedOperationException {
        if (parameters != null) {
            return parameters.clone();
        }
        MathTransform mt = transform;
        while (mt != null) {
            if (mt instanceof Parameterized) {
                final ParameterValueGroup param;
                if (Semaphores.queryAndSet(Semaphores.PROJCS)) {
                    throw new AssertionError(); // Should never happen.
                }
                try {
                    param = ((Parameterized) mt).getParameterValues();
                } finally {
                    Semaphores.clear(Semaphores.PROJCS);
                }
                if (param != null) {
                    return param;
                }
            }
            if (mt instanceof PassThroughTransform) {
                mt = ((PassThroughTransform) mt).getSubTransform();
            } else {
                break;
            }
        }
        throw new UnsupportedImplementationException(Classes.getClass(mt));
    }

    /**
     * Compares this operation method with the specified object for equality.
     * If the {@code mode} argument value is {@link ComparisonMode#STRICT STRICT} or
     * {@link ComparisonMode#BY_CONTRACT BY_CONTRACT}, then all available properties
     * are compared including the {@linkplain DefaultOperationMethod#getFormula() formula}.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (super.equals(object, mode)) {
            switch (mode) {
                case STRICT: {
                    final DefaultSingleOperation that = (DefaultSingleOperation) object;
                    return Objects.equals(this.method, that.method);
                }
                case BY_CONTRACT: {
                    final SingleOperation that = (SingleOperation) object;
                    return deepEquals(getMethod(), that.getMethod(), mode);
                }
                default: {
                    /*
                     * We consider the operation method as metadata. We could argue that OperationMethod's
                     * 'sourceDimension' and 'targetDimension' are not metadata,  but their values should
                     * be identical to the 'sourceCRS' and 'targetCRS' dimensions, already checked by the
                     * superclass. We could also argue that 'OperationMethod.parameters' are not metadata,
                     * but their values should have been taken in account for the MathTransform creation,
                     * which was compared by the superclass.
                     *
                     * Comparing the MathTransforms instead of parameters avoid the problem of implicit
                     * parameters.  For example in a ProjectedCRS, the "semiMajor" and "semiMinor" axis
                     * lengths are sometime provided as explicit parameters, and sometime inferred from
                     * the geodetic datum.  The two cases would be different set of parameters from the
                     * OperationMethod's point of view, but still result in the creation of identical
                     * MathTransform.
                     *
                     * An other rational for treating OperationMethod as metadata is that Geotk
                     * MathTransformProvider extends DefaultOperationMethod. Consequently there is
                     * a wide range of subclasses, which make the comparisons more difficult. For
                     * example Mercator1SP and Mercator2SP providers are two different ways to describe
                     * the same projection. The SQL-backed EPSG factory uses yet an other implementation.
                     *
                     * NOTE: A previous Geotk implementation made this final check:
                     *
                     *     return nameMatches(this.method, that.method);
                     *
                     * but it was not strictly necessary since it was redundant with the comparisons of
                     * MathTransforms. Actually it was preventing to detect that two CRS were equivalent
                     * despite different method names (e.g. "Mercator (1SP)" and "Mercator (2SP)" when
                     * the parameters are properly chosen).
                     */
                    return true;
                }
            }
        }
        return false;
    }

    // Do NOT override 'computeHashCode()', since we don't want to include the 'method' field in
    // hash code calculation. See the comment inside the above 'equals(Object, ComparisonMode)'
    // method for more information. Note that the parent class uses the 'transform' hash code,
    // which should be sufficient.

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatTo(final Formatter formatter) {
        final String name = super.formatTo(formatter);
        append(formatter, method, "METHOD");
        return name;
    }
}
