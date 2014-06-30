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
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import net.jcip.annotations.Immutable;

import org.opengis.util.FactoryException;
import org.opengis.metadata.quality.PositionalAccuracy;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.ConcatenatedOperation;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;

import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.resources.Errors;
import org.apache.sis.io.wkt.Formatter;

import static org.apache.sis.util.Utilities.deepEquals;
import static org.apache.sis.util.ArgumentChecks.*;
import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * An ordered sequence of two or more single coordinate operations. The sequence of operations is
 * constrained by the requirement that the source coordinate reference system of step
 * (<var>n</var>+1) must be the same as the target coordinate reference system of step
 * (<var>n</var>). The source coordinate reference system of the first step and the target
 * coordinate reference system of the last step are the source and target coordinate reference
 * system associated with the concatenated operation.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultConcatenatedOperation extends AbstractCoordinateOperation
        implements ConcatenatedOperation
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 4199619838029045700L;

    /**
     * The sequence of operations.
     */
    private final List<SingleOperation> operations;

    /**
     * Constructs a concatenated operation from the specified name.
     *
     * @param name The operation name.
     * @param operations The sequence of operations.
     */
    public DefaultConcatenatedOperation(final String name,
                                        final CoordinateOperation... operations)
    {
        this(Collections.singletonMap(NAME_KEY, name), operations);
    }

    /**
     * Constructs a concatenated operation from a set of properties.
     * The properties given in argument follow the same rules than for the
     * {@linkplain AbstractCoordinateOperation#AbstractCoordinateOperation(Map,
     * CoordinateReferenceSystem, CoordinateReferenceSystem, MathTransform) super-class
     * constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param operations The sequence of operations.
     */
    public DefaultConcatenatedOperation(final Map<String,?> properties,
                                        final CoordinateOperation... operations)
    {
        this(properties, new ArrayList<SingleOperation>(operations.length), operations);
    }

    /**
     * Constructs a concatenated operation from a set of properties and a
     * {@linkplain MathTransformFactory math transform factory}.
     * The properties given in argument follow the same rules than for the
     * {@linkplain AbstractCoordinateOperation#AbstractCoordinateOperation(Map,
     * CoordinateReferenceSystem, CoordinateReferenceSystem, MathTransform) super-class
     * constructor}.
     *
     * @param  properties Set of properties. Should contains at least {@code "name"}.
     * @param  operations The sequence of operations.
     * @param  factory    The math transform factory to use for math transforms concatenation.
     * @throws FactoryException if the factory can't concatenate the math transforms.
     */
    public DefaultConcatenatedOperation(final Map<String,?> properties,
                                        final CoordinateOperation[] operations,
                                        final MathTransformFactory factory)
            throws FactoryException
    {
        this(properties, new ArrayList<SingleOperation>(operations.length), operations, factory);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private DefaultConcatenatedOperation(final Map<String,?> properties,
                                         final ArrayList<SingleOperation> list,
                                         final CoordinateOperation[] operations)
    {
        this(properties, expand(operations, list), list);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private DefaultConcatenatedOperation(final Map<String,?> properties,
                                         final ArrayList<SingleOperation> list,
                                         final CoordinateOperation[] operations,
                                         final MathTransformFactory factory)
            throws FactoryException
    {
        this(properties, expand(operations, list, factory, true), list);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private DefaultConcatenatedOperation(final Map<String,?> properties,
                                         final MathTransform transform,
                                         final List<SingleOperation> operations)
    {
        super(mergeAccuracy(properties, operations),
              operations.get(0).getSourceCRS(),
              operations.get(operations.size() - 1).getTargetCRS(),
              transform);
        this.operations = UnmodifiableArrayList.wrap(
                operations.toArray(new SingleOperation[operations.size()]));
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static MathTransform expand(final CoordinateOperation[] operations,
                                        final List<SingleOperation> list)
    {
        try {
            return expand(operations, list, null, true);
        } catch (FactoryException exception) {
            // Should not happen, since we didn't used any MathTransformFactory.
            throw new AssertionError(exception);
        }
    }

    /**
     * Transforms the list of operations into a list of single operations. This method
     * also check against null value and make sure that all CRS dimension matches.
     *
     * @param  operations The array of operations to expand.
     * @param  target The destination list in which to add {@code SingleOperation}.
     * @param  factory The math transform factory to use, or {@code null}
     * @param  wantTransform {@code true} if the concatenated math transform should be computed.
     *         This is set to {@code false} only when this method invokes itself recursively.
     * @return The concatenated math transform.
     * @throws FactoryException if the factory can't concatenate the math transforms.
     */
    private static MathTransform expand(final CoordinateOperation[] operations,
                                        final List<SingleOperation> target,
                                        final MathTransformFactory factory,
                                        final boolean wantTransform)
            throws FactoryException
    {
        MathTransform transform = null;
        ensureNonNull("operations", operations);
        for (int i=0; i<operations.length; i++) {
            ensureNonNullElement("operations", i, operations);
            final CoordinateOperation op = operations[i];
            if (op instanceof SingleOperation) {
                target.add((SingleOperation) op);
            } else if (op instanceof ConcatenatedOperation) {
                final ConcatenatedOperation cop = (ConcatenatedOperation) op;
                final List<SingleOperation> cops = cop.getOperations();
                expand(cops.toArray(new CoordinateOperation[cops.size()]), target, factory, false);
            } else {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_CLASS_2,
                        op.getClass(), SingleOperation.class));
            }
            /*
             * Checks the CRS dimensions.
             */
            if (i != 0) {
                final CoordinateReferenceSystem previous = operations[i-1].getTargetCRS();
                final CoordinateReferenceSystem next     = op             .getSourceCRS();
                if (previous!=null && next!=null) {
                    final int dim1 = previous.getCoordinateSystem().getDimension();
                    final int dim2 =     next.getCoordinateSystem().getDimension();
                    if (dim1 != dim2) {
                        throw new IllegalArgumentException(Errors.format(
                                Errors.Keys.MISMATCHED_DIMENSION_2, dim1, dim2));
                    }
                }
            }
            /*
             * Concatenates the math transform.
             */
            if (wantTransform) {
                final MathTransform step = op.getMathTransform();
                if (transform == null) {
                    transform = step;
                } else if (factory != null) {
                    transform = factory.createConcatenatedTransform(transform, step);
                } else {
                    transform = MathTransforms.concatenate(transform, step);
                }
            }
        }
        if (wantTransform) {
            final int size = target.size();
            if (size <= 1) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.NO_PARAMETER_1, "operations[" + size + ']'));
            }
        }
        return transform;
    }

    /**
     * If no accuracy were specified in the given properties map, adds all accuracies found in the
     * operation to concatenate. This method considers only {@link Transformation} components and
     * ignores all conversions. According ISO 19111, the accuracy attribute is allowed only for
     * transformations. However, this restriction is not enforced everywhere. The EPSG database
     * declares an accuracy of 0 meters for conversions, which is conceptually exact. However we
     * are departing from the specification, since we are adding accuracy informations to a
     * concatenated operation. This departure should be considered as a convenience feature
     * only; accuracies are really relevant in transformations only.
     * <p>
     * There is also a technical reasons for ignoring conversions. If a concatenated operation
     * contains a datum shift (i.e. a transformation) with unknown accuracy, and a projection
     * (i.e. a conversion) with a declared 0 meter error, we don't want to declare this 0 meter
     * error as the concatenated operation's accuracy; it would be a false information.
     * <p>
     * Note that a concatenated operation typically contains an arbitrary amount of conversions,
     * but only one transformation. So considering transformation only usually means to pickup
     * only one operation in the given {@code operations} list.
     *
     * @todo We should use a Map and merge only one accuracy for each specification.
     */
    private static Map<String,?> mergeAccuracy(final Map<String,?> properties,
            final List<? extends CoordinateOperation> operations)
    {
        if (!properties.containsKey(COORDINATE_OPERATION_ACCURACY_KEY)) {
            Set<PositionalAccuracy> accuracy = null;
            for (final CoordinateOperation op : operations) {
                if (op instanceof Transformation) {
                    // See javadoc for a rational why we take only transformations in account.
                    Collection<PositionalAccuracy> candidates = op.getCoordinateOperationAccuracy();
                    if (!isNullOrEmpty(candidates)) {
                        if (accuracy == null) {
                            accuracy = new LinkedHashSet<>();
                        }
                        accuracy.addAll(candidates);
                    }
                }
            }
            if (accuracy != null) {
                final Map<String,Object> merged = new HashMap<>(properties);
                merged.put(COORDINATE_OPERATION_ACCURACY_KEY,
                           accuracy.toArray(new PositionalAccuracy[accuracy.size()]));
                return merged;
            }
        }
        return properties;
    }

    /**
     * Returns the GeoAPI interface implemented by this class.
     * The SIS implementation returns {@code ConcatenatedOperation.class}.
     *
     * {@note Subclasses usually do not need to override this method since GeoAPI does not define
     *        <code>ConcatenatedOperation</code> sub-interface. Overriding possibility is left mostly
     *        for implementors who wish to extend GeoAPI with their own set of interfaces.}
     *
     * @return {@code ConcatenatedOperation.class} or a user-defined sub-interface.
     */
    @Override
    public Class<? extends ConcatenatedOperation> getInterface() {
        return ConcatenatedOperation.class;
    }

    /**
     * Returns the sequence of operations.
     */
    @Override
    public List<SingleOperation> getOperations() {
        return operations;
    }

    /**
     * Compares this concatenated operation with the specified object for equality.
     * If the {@code mode} argument value is {@link ComparisonMode#STRICT STRICT} or
     * {@link ComparisonMode#BY_CONTRACT BY_CONTRACT}, then all available properties are
     * compared including the {@linkplain #getDomainOfValidity() domain of validity} and
     * the {@linkplain #getScope() scope}.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true; // Slight optimization.
        }
        if (super.equals(object, mode)) {
            switch (mode) {
                case STRICT: {
                    final DefaultConcatenatedOperation that = (DefaultConcatenatedOperation) object;
                    return Objects.equals(this.operations, that.operations);
                }
                default: {
                    final ConcatenatedOperation that = (ConcatenatedOperation) object;
                    return deepEquals(getOperations(), that.getOperations(), mode);
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
        return super.computeHashCode() + operations.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatTo(final Formatter formatter) {
        final String label = super.formatTo(formatter);
        for (final CoordinateOperation op : operations) {
            formatter.newLine();
            formatter.append((org.apache.sis.io.wkt.FormattableObject) op); // TODO
        }
        return label;
    }
}
