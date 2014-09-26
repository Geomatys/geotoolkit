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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.awt.RenderingHints;
import javax.measure.converter.ConversionException;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;
import org.opengis.metadata.quality.PositionalAccuracy;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.*;

import org.geotoolkit.factory.Hints;
import org.apache.sis.util.Classes;
import org.apache.sis.util.collection.WeakHashSet;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.factory.ReferencingFactory;
import org.geotoolkit.referencing.factory.ReferencingFactoryContainer;
import org.geotoolkit.referencing.operation.provider.Affine;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;
import org.apache.sis.referencing.cs.CoordinateSystems;

import static java.util.Collections.singletonMap;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import static org.opengis.referencing.operation.CoordinateOperation.COORDINATE_OPERATION_ACCURACY_KEY;
import static org.apache.sis.internal.referencing.PositionalAccuracyConstant.DATUM_SHIFT_APPLIED;
import static org.apache.sis.internal.referencing.PositionalAccuracyConstant.DATUM_SHIFT_OMITTED;
import static org.geotoolkit.metadata.Citations.GEOTOOLKIT;
import static org.geotoolkit.resources.Vocabulary.formatInternational;
import static org.geotoolkit.internal.InternalUtilities.debugEquals;


/**
 * Base class for coordinate operation factories. This class provides helper methods for the
 * construction of building blocks. It doesn't figure out any operation path by itself. This
 * more "intelligent" job is left to subclasses.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @level advanced
 * @module
 */
@ThreadSafe
public abstract class AbstractCoordinateOperationFactory extends ReferencingFactory
        implements CoordinateOperationFactory
{
    /**
     * The identifier for an identity operation.
     */
    protected static final ReferenceIdentifier IDENTITY =
            new NamedIdentifier(GEOTOOLKIT, formatInternational(Vocabulary.Keys.IDENTITY));

    /**
     * The identifier for conversion using an affine transform for axis swapping and/or
     * unit conversions.
     */
    protected static final ReferenceIdentifier AXIS_CHANGES =
            new NamedIdentifier(GEOTOOLKIT, formatInternational(Vocabulary.Keys.AXIS_CHANGES));

    /**
     * The identifier for a transformation which is a datum shift.
     *
     * @see org.apache.sis.metadata.iso.quality.AbstractPositionalAccuracy#DATUM_SHIFT_APPLIED
     */
    protected static final ReferenceIdentifier DATUM_SHIFT =
            new NamedIdentifier(GEOTOOLKIT, formatInternational(Vocabulary.Keys.DATUM_SHIFT));

    /**
     * The identifier for a transformation which is a datum shift without
     * {@linkplain BursaWolfParameters Bursa Wolf parameters}. Only the changes in ellipsoid
     * axis-length are taken in account. Such ellipsoid shifts are approximative and may have
     * 1 kilometre error. This transformation is allowed only if the factory was created with
     * {@link Hints#LENIENT_DATUM_SHIFT} set to {@link Boolean#TRUE}.
     *
     * @see org.apache.sis.metadata.iso.quality.AbstractPositionalAccuracy#DATUM_SHIFT_OMITTED
     */
    protected static final ReferenceIdentifier ELLIPSOID_SHIFT =
            new NamedIdentifier(GEOTOOLKIT, formatInternational(Vocabulary.Keys.ELLIPSOID_SHIFT));

    /**
     * The identifier for a geocentric conversion.
     */
    protected static final ReferenceIdentifier GEOCENTRIC_CONVERSION =
            new NamedIdentifier(GEOTOOLKIT, formatInternational(Vocabulary.Keys.GEOCENTRIC_TRANSFORM));

    /**
     * The identifier for an inverse operation.
     */
    protected static final ReferenceIdentifier INVERSE_OPERATION =
            new NamedIdentifier(GEOTOOLKIT, formatInternational(Vocabulary.Keys.INVERSE_OPERATION));

    /**
     * The set of helper methods on factories.
     */
    final ReferencingFactoryContainer factories;

    /**
     * A pool of coordinate operation. This pool is used in order
     * to returns instance of existing operations when possible.
     */
    private final WeakHashSet<CoordinateOperation> pool =
            new WeakHashSet<>(CoordinateOperation.class);

    /**
     * Tells if {@link FactoryGroup#hints} has been invoked. It must be invoked exactly once,
     * but can't be invoked in the constructor because it causes a {@link StackOverflowError}
     * in some situations.
     */
    private volatile boolean hintsInitialized;

    /**
     * Constructs a coordinate operation factory using the specified hints.
     * This constructor recognizes the {@link Hints#CRS_FACTORY CRS}, {@link Hints#CS_FACTORY CS},
     * {@link Hints#DATUM_FACTORY DATUM} and {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM}
     * {@code FACTORY} hints.
     *
     * @param userHints The hints, or {@code null} if none.
     */
    public AbstractCoordinateOperationFactory(final Hints userHints) {
        factories = ReferencingFactoryContainer.instance(userHints);
    }

    /**
     * If the specified factory is an instance of {@code AbstractCoordinateOperationFactory},
     * fetch the {@link ReferencingFactoryContainer} from this instance instead of from the
     * hints. This constructor is strictly reserved for factory subclasses that are wrapper
     * around an other factory, like {@link CachingCoordinateOperationFactory}.
     *
     * @param factory   The factory to wrap.
     * @param userHints The hints, or {@code null} if none.
     */
    AbstractCoordinateOperationFactory(final CoordinateOperationFactory factory, final Hints hints) {
        if (factory instanceof AbstractCoordinateOperationFactory) {
            factories = ((AbstractCoordinateOperationFactory) factory).factories;
        } else {
            factories = ReferencingFactoryContainer.instance(hints);
        }
    }

    /**
     * To be overridden by {@link CachingAuthorityFactory} only.
     */
    CoordinateOperationFactory getBackingFactory() {
        return null;
    }

    /**
     * Returns the implementation hints for this factory. The returned map contains values for
     * {@link Hints#CRS_FACTORY CRS}, {@link Hints#CS_FACTORY CS}, {@link Hints#DATUM_FACTORY DATUM}
     * and {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM} {@code FACTORY} hints. Other values
     * may be provided as well, at implementation choice.
     */
    @Override
    public Map<RenderingHints.Key,?> getImplementationHints() {
        if (!hintsInitialized) {
            final Map<RenderingHints.Key, ?> toAdd = factories.getImplementationHints();
            final CoordinateOperationFactory back = getBackingFactory();
            /*
             * Double-check locking: was a deprecated practice before Java 5, but is okay since
             * Java 5 providing that the variable is volatile. In this particular case, we want
             * the above lines to be executed outside the synchronization block in order to reduce
             * the risk of deadlock. This is not a big deal if those values are computed twice.
             */
            synchronized (this) {
                if (!hintsInitialized) {
                    hintsInitialized = true;
                    hints.putAll(toAdd);
                    if (back != null) {
                        hints.put(Hints.COORDINATE_OPERATION_FACTORY, back);
                    }
                }
            }
        }
        return super.getImplementationHints();
    }

    /**
     * Invoked when the {@link #hints} map should be initialized. This method may
     * be overridden by subclasses like {@link CachingCoordinateOperationFactory}.
     *
     * @return The hints to add to {@link #hints}.
     */
    Map<RenderingHints.Key, ?> initializeHints() {
        return factories.getImplementationHints();
    }

    /**
     * Returns the operation method of the given name. The default implementation returns the first
     * method from the set returned by {@link MathTransformFactory#getAvailableMethods(Class)}
     * which have a {@linkplain IdentifiedObjects#nameMatches matching name}.
     *
     * @param  name The name of the operation method to fetch.
     * @return The operation method of the given name.
     * @throws FactoryException if the requested operation method can not be fetched.
     *
     * @see #createOperationMethod(Map, Integer, Integer, ParameterDescriptorGroup)
     *
     * @since 3.19
     */
    @Override
    public OperationMethod getOperationMethod(final String name) throws FactoryException {
        final MathTransformFactory mtFactory = getMathTransformFactory();
        if (mtFactory instanceof DefaultMathTransformFactory) {
            return ((DefaultMathTransformFactory) mtFactory).getOperationMethod(name);
        }
        for (final OperationMethod method : mtFactory.getAvailableMethods(SingleOperation.class)) {
            if (IdentifiedObjects.isHeuristicMatchForName(method, name)) {
                return method;
            }
        }
        throw new NoSuchIdentifierException(Errors.format(
                Errors.Keys.NO_TRANSFORM_FOR_CLASSIFICATION_1, name), name);
    }

    /**
     * Returns the underlying math transform factory. This factory
     * is used for constructing {@link MathTransform} objects for
     * all {@linkplain CoordinateOperation coordinate operations}.
     *
     * @return The underlying math transform factory.
     */
    public final MathTransformFactory getMathTransformFactory() {
        return factories.getMathTransformFactory();
    }

    /**
     * Returns an affine transform between two coordinate systems. Only units and
     * axis order (e.g. transforming from (NORTH,WEST) to (EAST,NORTH)) are taken
     * in account.
     * <p>
     * Example: If coordinates in {@code sourceCS} are (x,y) pairs in metres and
     * coordinates in {@code targetCS} are (-y,x) pairs in centimetres, then the
     * transformation can be performed as below:
     *
     * {@preformat text
     *     ┌      ┐   ┌              ┐ ┌     ┐
     *     │-y(cm)│   │ 0  -100    0 │ │ x(m)│
     *     │ x(cm)│ = │ 100   0    0 │ │ y(m)│
     *     │ 1    │   │ 0     0    1 │ │ 1   │
     *     └      ┘   └              ┘ └     ┘
     * }
     *
     * The default implementation performs the same work than the static method in the
     * {@link AbstractCS#swapAndScaleAxis(CoordinateSystem, CoordinateSystem) AbstractCS}
     * class, except that the unchecked exceptions are wrapped into the checked
     * {@link OperationNotFoundException}.
     *
     * @param  sourceCS The source coordinate system.
     * @param  targetCS The target coordinate system.
     * @return The transformation from {@code sourceCS} to {@code targetCS} as
     *         an affine transform. Only axis orientation and units are taken in account.
     * @throws OperationNotFoundException If the affine transform can't be constructed.
     *
     * @see AbstractCS#swapAndScaleAxis(CoordinateSystem, CoordinateSystem)
     */
    protected Matrix swapAndScaleAxis(final CoordinateSystem sourceCS,
                                      final CoordinateSystem targetCS)
            throws OperationNotFoundException
    {
        try {
            return CoordinateSystems.swapAndScaleAxes(sourceCS,targetCS);
        } catch (IllegalArgumentException | ConversionException exception) {
            throw new OperationNotFoundException(getErrorMessage(sourceCS, targetCS), exception);
        }
        // No attempt to catch ClassCastException since such
        // exception would indicates a programming error.
    }

    /**
     * Returns the specified identifier in a map to be given to coordinate operation constructors.
     * In the special case where the {@code name} identifier is {@link #DATUM_SHIFT} or
     * {@link #ELLIPSOID_SHIFT}, the map will contains extra informations like positional
     * accuracy.
     *
     * {@note In the datum shift case, an operation version is mandatory but unknown at this time.
     *        However, we noticed that the EPSG database do not always defines a version neither.
     *        Consequently, the Geotk implementation relax the rule requirying an operation
     *        version and we do not try to provide this information here for now.}
     */
    private static Map<String,Object> getProperties(final ReferenceIdentifier name) {
        final Map<String,Object> properties;
        if ((name == DATUM_SHIFT) || (name == ELLIPSOID_SHIFT)) {
            properties = new HashMap<>(4);
            properties.put(NAME_KEY, name);
            properties.put(COORDINATE_OPERATION_ACCURACY_KEY, new PositionalAccuracy[] {
                      name == DATUM_SHIFT ? DATUM_SHIFT_APPLIED : DATUM_SHIFT_OMITTED
            });
        } else {
            properties = singletonMap(NAME_KEY, (Object) name);
        }
        return properties;
    }

    /**
     * Creates a coordinate operation from a matrix, which usually describes an affine transform.
     * A default {@link OperationMethod} object is given to this transform. In the special case
     * where the {@code name} identifier is {@link #DATUM_SHIFT} or {@link #ELLIPSOID_SHIFT},
     * the operation will be an instance of {@link Transformation} instead of the usual
     * {@link Conversion}.
     *
     * @param  name      The identifier for the operation to be created.
     * @param  sourceCRS The source coordinate reference system.
     * @param  targetCRS The target coordinate reference system.
     * @param  matrix    The matrix which describe an affine transform operation.
     * @return The conversion or transformation.
     * @throws FactoryException if the operation can't be created.
     */
    protected CoordinateOperation createFromAffineTransform(
            final ReferenceIdentifier       name,
            final CoordinateReferenceSystem sourceCRS,
            final CoordinateReferenceSystem targetCRS,
            final Matrix matrix) throws FactoryException
    {
        final MathTransformFactory mtFactory = getMathTransformFactory();
        final MathTransform transform = mtFactory.createAffineTransform(matrix);
        final Map<String,?> properties = getProperties(name);
        final Class<? extends SingleOperation> type =
                properties.containsKey(COORDINATE_OPERATION_ACCURACY_KEY) ?
                        Transformation.class : Conversion.class;
        return createFromMathTransform(properties, sourceCRS, targetCRS, transform,
                   Affine.getProvider(transform.getSourceDimensions(),
                                      transform.getTargetDimensions()), type);
    }

    /**
     * Creates a coordinate operation from a set of parameters.
     * The {@linkplain OperationMethod operation method} is inferred automatically,
     * if possible.
     *
     * @param  name       The identifier for the operation to be created.
     * @param  sourceCRS  The source coordinate reference system.
     * @param  targetCRS  The target coordinate reference system.
     * @param  parameters The parameters.
     * @return The conversion or transformation.
     * @throws FactoryException if the operation can't be created.
     */
    protected CoordinateOperation createFromParameters(
            final ReferenceIdentifier       name,
            final CoordinateReferenceSystem sourceCRS,
            final CoordinateReferenceSystem targetCRS,
            final ParameterValueGroup       parameters) throws FactoryException
    {
        final Map<String,?> properties = getProperties(name);
        final MathTransformFactory mtFactory = getMathTransformFactory();
        final MathTransform transform = mtFactory.createParameterizedTransform(parameters);
        final OperationMethod  method = mtFactory.getLastMethodUsed();
        return createFromMathTransform(properties, sourceCRS, targetCRS, transform,
                                       method, SingleOperation.class);
    }

    /**
     * Creates a coordinate operation from a math transform.
     *
     * @param  name       The identifier for the operation to be created.
     * @param  sourceCRS  The source coordinate reference system.
     * @param  targetCRS  The destination coordinate reference system.
     * @param  transform  The math transform.
     * @return A coordinate operation using the specified math transform.
     * @throws FactoryException if the operation can't be constructed.
     */
    protected CoordinateOperation createFromMathTransform(
            final ReferenceIdentifier       name,
            final CoordinateReferenceSystem sourceCRS,
            final CoordinateReferenceSystem targetCRS,
            final MathTransform             transform) throws FactoryException
    {
        final Map<String,?> properties = singletonMap(NAME_KEY, name);
        return createFromMathTransform(properties, sourceCRS, targetCRS, transform,
                createOperationMethod(properties, sourceCRS.getCoordinateSystem().getDimension(),
                targetCRS.getCoordinateSystem().getDimension(), null), CoordinateOperation.class);
    }

    /**
     * Creates a coordinate operation from a math transform.
     * If the specified math transform is already a coordinate operation, and if source
     * and target CRS match, then {@code transform} is returned with no change.
     * Otherwise, a new coordinate operation is created.
     *
     * @param  properties The properties to give to the operation.
     * @param  sourceCRS  The source coordinate reference system.
     * @param  targetCRS  The destination coordinate reference system.
     * @param  transform  The math transform.
     * @param  method     The operation method, or {@code null}.
     * @param  type       The required super-class (e.g. <code>{@linkplain Transformation}.class</code>).
     * @return A coordinate operation using the specified math transform.
     * @throws FactoryException if the operation can't be constructed.
     */
    protected CoordinateOperation createFromMathTransform(
            final Map<String,?>             properties,
            final CoordinateReferenceSystem sourceCRS,
            final CoordinateReferenceSystem targetCRS,
            final MathTransform             transform,
            final OperationMethod           method,
            final Class<? extends CoordinateOperation> type) throws FactoryException
    {
        CoordinateOperation operation;
        if (transform instanceof CoordinateOperation) {
            operation = (CoordinateOperation) transform;
            if (Objects.equals(operation.getSourceCRS(),     sourceCRS) &&
                Objects.equals(operation.getTargetCRS(),     targetCRS) &&
                Objects.equals(operation.getMathTransform(), transform))
            {
                if (operation instanceof SingleOperation) {
                    if (Objects.equals(((SingleOperation) operation).getMethod(), method)) {
                        return operation;
                    }
                } else {
                    return operation;
                }
            }
        }
        operation = DefaultSingleOperation.create(properties, sourceCRS, targetCRS, transform, method, type);
        operation = pool.unique(operation);
        return operation;
    }

    /**
     * Constructs a defining conversion from a set of properties.
     *
     * @param  properties Set of properties. Should contains at least {@code "name"}.
     * @param  method The operation method.
     * @param  parameters The parameter values.
     * @return The defining conversion.
     * @throws FactoryException if the object creation failed.
     *
     * @see DefiningConversion
     *
     * @since 2.5
     */
    @Override
    public Conversion createDefiningConversion(
            final Map<String,?>       properties,
            final OperationMethod     method,
            final ParameterValueGroup parameters) throws FactoryException
    {
        Conversion conversion = new DefiningConversion(properties, method, parameters);
        conversion = pool.unique(conversion);
        return conversion;
    }

    /**
     * Creates an operation method from a set of properties and a descriptor group. The default
     * implementation delegates to the {@link DefaultOperationMethod#DefaultOperationMethod(Map,
     * Integer, Integer, ParameterDescriptorGroup) DefaultOperationMethod} constructor.
     *
     * @param  properties Set of properties. Shall contains at least {@code "name"}.
     * @param  sourceDimension Number of dimensions in the source CRS of this operation method.
     * @param  targetDimension Number of dimensions in the target CRS of this operation method.
     * @param  parameters The set of parameters, or {@code null} if none.
     *
     * @see #getOperationMethod(String)
     *
     * @since 3.19
     */
    @Override
    public OperationMethod createOperationMethod(final Map<String,?> properties,
            final Integer sourceDimension, final Integer targetDimension,
            final ParameterDescriptorGroup parameters) throws FactoryException
    {
        return new DefaultOperationMethod(properties, sourceDimension, targetDimension, parameters);
    }

    /**
     * Creates a concatenated operation from a sequence of operations.
     *
     * @param  properties Set of properties. Should contains at least {@code "name"}.
     * @param  operations The sequence of operations.
     * @return The concatenated operation.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateOperation createConcatenatedOperation(final Map<String,?> properties,
            final CoordinateOperation... operations) throws FactoryException
    {
        CoordinateOperation operation;
        operation = new DefaultConcatenatedOperation(properties, operations, getMathTransformFactory());
        operation = pool.unique(operation);
        return operation;
    }

    /**
     * Concatenates two operation steps. If an operation is an {@link #AXIS_CHANGES},
     * it will be included as part of the second operation instead of creating an
     * {@link ConcatenatedOperation}. If a concatenated operation is created, it
     * will get an automatically generated name.
     *
     * @param  step1 The first  step, or {@code null} for the identity operation.
     * @param  step2 The second step, or {@code null} for the identity operation.
     * @return A concatenated operation, or {@code null} if all arguments was nul.
     * @throws FactoryException if the operation can't be constructed.
     */
    protected CoordinateOperation concatenate(final CoordinateOperation step1,
                                              final CoordinateOperation step2)
            throws FactoryException
    {
        if (step1 == null) return step2;
        if (step2 == null) return step1;
        // Note: we sometime get this assertion failure if the user provided CRS with two
        //       different ellipsoids but an identical TOWGS84 conversion infos (which is
        //       usually wrong, but still happen).
        assert debugEquals(step1.getTargetCRS(), step2.getSourceCRS());

        if (isIdentity(step1)) return step2;
        if (isIdentity(step2)) return step1;
        final MathTransform mt1 = step1.getMathTransform();
        final MathTransform mt2 = step2.getMathTransform();
        final CoordinateReferenceSystem sourceCRS = step1.getSourceCRS();
        final CoordinateReferenceSystem targetCRS = step2.getTargetCRS();
        CoordinateOperation step = null;
        if (step1.getName() == AXIS_CHANGES && mt1.getSourceDimensions() == mt1.getTargetDimensions()) step = step2;
        if (step2.getName() == AXIS_CHANGES && mt2.getSourceDimensions() == mt2.getTargetDimensions()) step = step1;
        if (step instanceof SingleOperation) {
            /*
             * Applies only on operation in order to avoid merging with PassThroughOperation.
             * Also applies only if the transform to hide has identical source and target
             * dimensions in order to avoid mismatch with the method's dimensions.
             */
            final MathTransformFactory mtFactory = getMathTransformFactory();
            return createFromMathTransform(IdentifiedObjects.getProperties(step),
                   sourceCRS, targetCRS, mtFactory.createConcatenatedTransform(mt1, mt2),
                   ((SingleOperation) step).getMethod(), CoordinateOperation.class);
        }
        return createConcatenatedOperation(getTemporaryName(sourceCRS, targetCRS), step1, step2);
    }

    /**
     * Concatenates three transformation steps. If the first and/or the last operation is an
     * {@link #AXIS_CHANGES}, it will be included as part of the second operation instead of
     * creating an {@link ConcatenatedOperation}. If a concatenated operation is created, it
     * will get an automatically generated name.
     *
     * @param  step1 The first  step, or {@code null} for the identity operation.
     * @param  step2 The second step, or {@code null} for the identity operation.
     * @param  step3 The third  step, or {@code null} for the identity operation.
     * @return A concatenated operation, or {@code null} if all arguments were null.
     * @throws FactoryException if the operation can't be constructed.
     */
    protected CoordinateOperation concatenate(final CoordinateOperation step1,
                                              final CoordinateOperation step2,
                                              final CoordinateOperation step3)
            throws FactoryException
    {
        if (step1 == null) return concatenate(step2, step3);
        if (step2 == null) return concatenate(step1, step3);
        if (step3 == null) return concatenate(step1, step2);
        // Note: we use approximative equality check because the CRS objects may be slightly
        //       different if one CRS has been created from the EPSG database while the other
        //       CRS has been created from WKT parsing. There is rounding error in the second
        //       defining parameter of the ellipsoid, because the WKT parser always use the
        //       createFlattenedSphere(...) constructor while the EPSG database will select
        //       createFlattenedSphere(...) or createEllipsoid(...) depending on the Ellipsoid
        //       definition.
        assert debugEquals(step1.getTargetCRS(), step2.getSourceCRS()) : step1;
        assert debugEquals(step2.getTargetCRS(), step3.getSourceCRS()) : step3;

        if (isIdentity(step1)) return concatenate(step2, step3);
        if (isIdentity(step2)) return concatenate(step1, step3);
        if (isIdentity(step3)) return concatenate(step1, step2);
        if (step1.getName() == AXIS_CHANGES) return concatenate(concatenate(step1, step2), step3);
        if (step3.getName() == AXIS_CHANGES) return concatenate(step1, concatenate(step2, step3));
        final CoordinateReferenceSystem sourceCRS = step1.getSourceCRS();
        final CoordinateReferenceSystem targetCRS = step3.getTargetCRS();
        return createConcatenatedOperation(getTemporaryName(sourceCRS, targetCRS), step1, step2, step3);
    }

    /**
     * Returns {@code true} if the specified operation is an identity conversion.
     * This method always returns {@code false} for transformations even if their
     * associated math transform is an identity one, because such transformations
     * are usually datum shift and must be visible.
     */
    private static boolean isIdentity(final CoordinateOperation operation) {
        return (operation instanceof Conversion) && operation.getMathTransform().isIdentity();
    }

    /**
     * Returns the inverse of the specified operation.
     *
     * @param  operation The operation to invert.
     * @return The inverse of {@code operation}.
     * @throws NoninvertibleTransformException if the operation is not invertible.
     * @throws FactoryException if the operation creation failed for an other reason.
     *
     * @since 2.3
     */
    protected CoordinateOperation inverse(final CoordinateOperation operation)
            throws NoninvertibleTransformException, FactoryException
    {
        final CoordinateReferenceSystem sourceCRS = operation.getSourceCRS();
        final CoordinateReferenceSystem targetCRS = operation.getTargetCRS();
        final Map<String,Object> properties = org.geotoolkit.referencing.IdentifiedObjects.getProperties(operation, null);
        properties.putAll(getTemporaryName(targetCRS, sourceCRS));
        if (operation instanceof ConcatenatedOperation) {
            final LinkedList<CoordinateOperation> inverted = new LinkedList<>();
            for (final CoordinateOperation op : ((ConcatenatedOperation) operation).getOperations()) {
                inverted.addFirst(inverse(op));
            }
            return createConcatenatedOperation(properties,
                    inverted.toArray(new CoordinateOperation[inverted.size()]));
        }
        final MathTransform transform = operation.getMathTransform().inverse();
        final Class<? extends CoordinateOperation> type = AbstractCoordinateOperation.getType(operation);
        final OperationMethod method = (operation instanceof SingleOperation) ?
                                       ((SingleOperation) operation).getMethod() : null;
        return createFromMathTransform(properties, targetCRS, sourceCRS, transform, method, type);
    }




    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ////////////                                                         ////////////
    ////////////                M I S C E L L A N E O U S                ////////////
    ////////////                                                         ////////////
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the dimension of the specified coordinate system,
     * or {@code 0} if the coordinate system is null.
     */
    static int getDimension(final CoordinateReferenceSystem crs) {
        return (crs!=null) ? crs.getCoordinateSystem().getDimension() : 0;
    }

    /**
     * An identifier for temporary objects. This identifier manage a count of temporary
     * identifiers. The count is appended to the identifier name (e.g. "WGS84 (step 1)").
     * <p>
     * This class is used only for names formatting. This class doesn't have any impact
     * on the numerical values to be computed.
     */
    private static final class TemporaryIdentifier extends NamedIdentifier {
        /** For cross-version compatibility. */
        private static final long serialVersionUID = -2784354058026177076L;

        /** The parent identifier. */
        private final ReferenceIdentifier parent;

        /** The temporary object count. */
        private final int count;

        /** Constructs an identifier derived from the specified one. */
        public TemporaryIdentifier(final ReferenceIdentifier parent) {
            this(parent, ((parent instanceof TemporaryIdentifier) ?
                         ((TemporaryIdentifier) parent).count : 0) + 1);
        }

        /** Work around for RFE #4093999 in Sun's bug database */
        private TemporaryIdentifier(final ReferenceIdentifier parent, final int count) {
            super(GEOTOOLKIT, unwrap(parent).getCode() + " (step " + count + ')');
            this.parent = parent;
            this.count  = count;
        }

        /** Returns the parent identifier for the specified identifier, if any. */
        public static ReferenceIdentifier unwrap(ReferenceIdentifier identifier) {
            while (identifier instanceof TemporaryIdentifier) {
                identifier = ((TemporaryIdentifier) identifier).parent;
            }
            return identifier;
        }
    }

    /**
     * Returns the name of the GeoAPI interface implemented by the specified object.
     * In addition, the name may be added between brackets.
     */
    private static String getClassName(final IdentifiedObject object) {
        if (object != null) {
            Class<?> type = object.getClass();
            final Class<?>[] interfaces = type.getInterfaces();
            for (int i=0; i<interfaces.length; i++) {
                final Class<?> candidate = interfaces[i];
                if (candidate.getName().startsWith("org.opengis.referencing.")) {
                    type = candidate;
                    break;
                }
            }
            String name = Classes.getShortName(type);
            final ReferenceIdentifier id = object.getName();
            if (id != null) {
                name = name + '[' + id.getCode() + ']';
            }
            return name;
        }
        return null;
    }

    /**
     * Returns a temporary name for object derived from the specified one.
     *
     * @param source The CRS to base name on, or {@code null} if none.
     */
    static Map<String,Object> getTemporaryName(final IdentifiedObject source) {
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(NAME_KEY, new TemporaryIdentifier(source.getName()));
        properties.put(IdentifiedObject.REMARKS_KEY, formatInternational(
                Vocabulary.Keys.DERIVED_FROM_1, getClassName(source)));
        return properties;
    }

    /**
     * Returns a temporary name for object derived from a concatenation.
     *
     * @param source The CRS to base name on, or {@code null} if none.
     */
    static Map<String,?> getTemporaryName(final CoordinateReferenceSystem source,
                                          final CoordinateReferenceSystem target)
    {
        final String name = getClassName(source) + " \u21E8 " + getClassName(target);
        return singletonMap(NAME_KEY, name);
    }

    /**
     * Returns an error message for "No path found from sourceCRS to targetCRS".
     * This is used for the construction of {@link OperationNotFoundException}.
     *
     * @param  source The source CRS.
     * @param  target The target CRS.
     * @return A default error message.
     */
    protected static String getErrorMessage(final IdentifiedObject source,
                                            final IdentifiedObject target)
    {
        return Errors.format(Errors.Keys.NO_TRANSFORMATION_PATH_2,
                getClassName(source), getClassName(target));
    }
}
