/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.util.Collections;
import java.util.Objects;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.Formula;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.metadata.citation.Citation;

import org.apache.sis.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.apache.sis.parameter.Parameterized;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.PassThroughTransform;
import org.geotoolkit.io.wkt.Formattable;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;
import org.apache.sis.io.wkt.Formatter;

import org.apache.sis.referencing.operation.transform.Accessor;
import static org.apache.sis.util.ArgumentChecks.*;


/**
 * Definition of an algorithm used to perform a coordinate operation. Most operation
 * methods use a number of operation parameters, although some coordinate conversions
 * use none. Each coordinate operation using the method assigns values to these parameters.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @see DefaultSingleOperation
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultOperationMethod extends AbstractIdentifiedObject implements OperationMethod, Formattable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8181774670648793964L;

    /**
     * Formula(s) or procedure used by this operation method. This may be a reference to a
     * publication. Note that the operation method may not be analytic, in which case this
     * attribute references or contains the procedure, not an analytic formula.
     */
    private final Formula formula;

    /**
     * Number of dimensions in the source CRS of this operation method.
     * May be {@code null} if this method can work with any number of
     * source dimensions (e.g. <cite>Affine Transform</cite>).
     */
    protected final Integer sourceDimension;

    /**
     * Number of dimensions in the target CRS of this operation method.
     * May be {@code null} if this method can work with any number of
     * target dimensions (e.g. <cite>Affine Transform</cite>).
     */
    protected final Integer targetDimension;

    /**
     * The set of parameters, or {@code null} if none.
     */
    private final ParameterDescriptorGroup parameters;

    /**
     * Convenience constructor that creates an operation method from a math transform.
     * The information provided in the newly created object are approximative, and
     * usually acceptable only as a fallback when no other information are available.
     *
     * @param transform The math transform to describe.
     */
    public DefaultOperationMethod(final MathTransform transform) {
        this(getProperties(transform),
             transform.getSourceDimensions(),
             transform.getTargetDimensions(),
             getDescriptor(transform));
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Map<String,?> getProperties(final MathTransform transform) {
        ensureNonNull("transform", transform);
        if (transform instanceof Parameterized) {
            final Parameterized mt = (Parameterized) transform;
            final ParameterDescriptorGroup parameters = mt.getParameterDescriptors();
            if (parameters != null) {
                return IdentifiedObjects.getProperties(parameters, null);
            }
        }
        return Collections.singletonMap(NAME_KEY, Vocabulary.format(Vocabulary.Keys.UNKNOWN));
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     * This code should have been merged with {@code getProperties} above.
     */
    private static ParameterDescriptorGroup getDescriptor(final MathTransform transform) {
        ParameterDescriptorGroup descriptor = null;
        if (transform instanceof Parameterized) {
            descriptor = ((Parameterized) transform).getParameterDescriptors();
        }
        return descriptor;
    }

    /**
     * Constructs a new operation method with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param method The operation method to copy.
     */
    public DefaultOperationMethod(final OperationMethod method) {
        super(method);
        formula         = method.getFormula();
        parameters      = method.getParameters();
        sourceDimension = method.getSourceDimensions();
        targetDimension = method.getTargetDimensions();
    }

    /**
     * Constructs a new operation method with the same values than the specified one except the
     * dimensions. The source and target dimensions may be {@code null} if this method can work
     * with any number of dimensions (e.g. <cite>Affine Transform</cite>).
     *
     * @param method The operation method to copy.
     * @param sourceDimension Number of dimensions in the source CRS of this operation method.
     * @param targetDimension Number of dimensions in the target CRS of this operation method.
     */
    public DefaultOperationMethod(final OperationMethod method,
                                  final Integer sourceDimension,
                                  final Integer targetDimension)
    {
        super(method);
        this.formula    = method.getFormula();
        this.parameters = method.getParameters();
        this.sourceDimension = sourceDimension;
        this.targetDimension = targetDimension;
        checkDimension();
    }

    /**
     * Constructs an operation method from a set of properties and a descriptor group.
     * The properties given in argument follow the same rules than for the
     * {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map) super-class constructor}.
     * Additionally, the following properties are understood by this construtor:
     * <p>
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value org.opengis.referencing.operation.OperationMethod#FORMULA_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link Formula}, {@link Citation} or {@link CharSequence}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getFormula}</td>
     *   </tr>
     * </table>
     * <p>
     * The source and target dimensions may be {@code null} if this method can work
     * with any number of dimensions (e.g. <cite>Affine Transform</cite>).
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param sourceDimension Number of dimensions in the source CRS of this operation method.
     * @param targetDimension Number of dimensions in the target CRS of this operation method.
     * @param parameters The set of parameters, or {@code null} if none.
     */
    public DefaultOperationMethod(final Map<String,?> properties,
                                  final Integer sourceDimension,
                                  final Integer targetDimension,
                                  final ParameterDescriptorGroup parameters)
    {
        super(properties);
        Object formula = properties.get(FORMULA_KEY);
        if (formula != null) {
            if (formula instanceof Citation) {
                formula = new DefaultFormula((Citation) formula);
            } else if (formula instanceof CharSequence) {
                formula = new DefaultFormula((CharSequence) formula);
            } else if (!(formula instanceof Formula)) {
                throw new InvalidParameterValueException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2,
                        "formula", formula), "formula", formula);
            }
        }
        this.formula = (Formula) formula;
        // 'parameters' may be null, which is okay. A null value will
        // make serialization smaller and faster than an empty object.
        this.parameters      = parameters;
        this.sourceDimension = sourceDimension;
        this.targetDimension = targetDimension;
        checkDimension();
    }

    /**
     * Checks the validity of source and target dimensions.
     */
    private void checkDimension() {
        if (sourceDimension != null) ensurePositive("sourceDimension", sourceDimension);
        if (targetDimension != null) ensurePositive("targetDimension", targetDimension);
    }

    /**
     * Returns the GeoAPI interface implemented by this class.
     * The SIS implementation returns {@code OperationMethod.class}.
     *
     * {@note Subclasses usually do not need to override this method since GeoAPI does not define
     *        <code>OperationMethod</code> sub-interface. Overriding possibility is left mostly for
     *        implementors who wish to extend GeoAPI with their own set of interfaces.}
     *
     * @return {@code OperationMethod.class} or a user-defined sub-interface.
     */
    @Override
    public Class<? extends OperationMethod> getInterface() {
        return OperationMethod.class;
    }

    /**
     * Formula(s) or procedure used by this operation method. This may be a reference to a
     * publication. Note that the operation method may not be analytic, in which case this
     * attribute references or contains the procedure, not an analytic formula.
     */
    @Override
    public Formula getFormula() {
        return formula;
    }

    /**
     * Number of dimensions in the source CRS of this operation method.
     * May be null if unknown, as in an <cite>Affine Transform</cite>.
     *
     */
    @Override
    public Integer getSourceDimensions() {
        return sourceDimension;
    }

    /**
     * Number of dimensions in the target CRS of this operation method.
     * May be null if unknown, as in an <cite>Affine Transform</cite>.
     */
    @Override
    public Integer getTargetDimensions() {
        return targetDimension;
    }

    /**
     * Returns the set of parameters.
     */
    @Override
    public ParameterDescriptorGroup getParameters() {
        return (parameters != null) ? parameters : Parameters.EMPTY_GROUP;
    }

    /**
     * Returns the operation type. Current implementation returns {@code Projection.class} for
     * proper WKT formatting using an unknown implementation. But the {@link MathTransformProvider}
     * subclass (with protected access) will overrides this method with a more conservative default
     * value.
     *
     * @return The GeoAPI interface implemented by this operation.
     */
    Class<? extends SingleOperation> getOperationType() {
        return Projection.class;
    }

    /**
     * Compares this operation method with the specified object for equality.
     * If the {@code mode} argument value is {@link ComparisonMode#STRICT STRICT} or
     * {@link ComparisonMode#BY_CONTRACT BY_CONTRACT}, then all available properties
     * are compared including the {@linkplain #getFormula() formula}.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true; // Slight optimization.
        }
        if (super.equals(object, mode)) {
            switch (mode) {
                case BY_CONTRACT: {
                    if (!Objects.equals(getFormula(), ((OperationMethod) object).getFormula())) {
                        return false;
                    }
                    // Fall through
                }
                default: {
                    final OperationMethod that = (OperationMethod) object;
                    return Objects.equals(getSourceDimensions(), that.getSourceDimensions()) &&
                           Objects.equals(getTargetDimensions(), that.getTargetDimensions()) &&
                           Utilities.deepEquals(getParameters(),   that.getParameters(), mode);
                }
                case STRICT: {
                    final DefaultOperationMethod that = (DefaultOperationMethod) object;
                    return Objects.equals(this.formula,         that.formula) &&
                           Objects.equals(this.sourceDimension, that.sourceDimension) &&
                           Objects.equals(this.targetDimension, that.targetDimension) &&
                           Objects.equals(this.parameters,      that.parameters);
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected long computeHashCode() {
        return super.computeHashCode() + Objects.hash(sourceDimension, targetDimension, parameters);
    }

    /**
     * Formats the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
     * Known Text</cite> (WKT)</A> element.
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name.
     */
    @Override
    public String formatTo(final Formatter formatter) {
        String keyword = super.formatTo(formatter);
        formatter.newLine();
        if (Projection.class.isAssignableFrom(getOperationType())) {
            keyword = "PROJECTION";
        }
        return keyword;
    }

    /**
     * Returns {@code true} if the specified transform is likely to exists only for axis switch
     * and/or unit conversions. The heuristic rule checks if the transform is backed by a square
     * matrix with exactly one non-null value in each row and each column. This method is used
     * for implementation of the {@link #checkDimensions} method only.
     */
    private static boolean isTrivial(final MathTransform transform) {
        if (transform instanceof LinearTransform) {
            final Matrix matrix = ((LinearTransform) transform).getMatrix();
            final int size = matrix.getNumRow();
            if (matrix.getNumCol() == size) {
                for (int j=0; j<size; j++) {
                    int n1=0, n2=0;
                    for (int i=0; i<size; i++) {
                        if (matrix.getElement(j,i) != 0) n1++;
                        if (matrix.getElement(i,j) != 0) n2++;
                    }
                    if (n1 != 1 || n2 != 1) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if an operation method and a math transform have a compatible number of source
     * and target dimensions. In the particular case of a {@linkplain PassThroughTransform pass
     * through transform} with more dimension than the expected number, the check will rather be
     * performed against the {@linkplain PassThroughTransform#getSubTransform sub transform}.
     * <p>
     * This convenience method is provided for argument checking.
     *
     * @param  method    The operation method to compare to the math transform, or {@code null}.
     * @param  transform The math transform to compare to the operation method, or {@code null}.
     * @throws MismatchedDimensionException if the number of dimensions are incompatibles.
     *
     * @todo The check for {@link ConcatenatedTransform} and {@link PassThroughTransform} works
     *       only for Geotk implementations.
     */
    public static void checkDimensions(final OperationMethod method, MathTransform transform)
            throws MismatchedDimensionException
    {
        if (method == null || transform == null) {
            return;
        }
        Integer expected = method.getSourceDimensions();
        if (expected == null) {
            return;
        }
        int actual;
        while ((actual = transform.getSourceDimensions()) > expected.intValue()) {
            if (Accessor.isConcatenatedTransform(transform)) {
                // Ignore axis switch and unit conversions.
                if (isTrivial(Accessor.transform1(transform))) {
                    transform = Accessor.transform2(transform);
                } else if (isTrivial(Accessor.transform2(transform))) {
                    transform = Accessor.transform1(transform);
                } else {
                    // The transform is something more complex than an axis switch.
                    // Stop the loop with the current illegal transform and let the
                    // exception be thrown after the loop.
                    break;
                }
            } else if (transform instanceof PassThroughTransform) {
                transform = ((PassThroughTransform) transform).getSubTransform();
            } else {
                break;
            }
        }
        final String name;
        if (actual != expected.intValue()) {
            name = "sourceDimension";
        } else {
            expected = method.getTargetDimensions();
            if (expected == null) {
                return;
            }
            actual = transform.getTargetDimensions();
            if (actual != expected.intValue()) {
                name = "targetDimension";
            } else {
                return;
            }
        }
        throw new IllegalArgumentException(Errors.format(
                Errors.Keys.MISMATCHED_DIMENSION_3, name, actual, expected));
    }
}
