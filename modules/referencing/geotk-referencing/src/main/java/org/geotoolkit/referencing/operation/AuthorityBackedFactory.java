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
package org.geotoolkit.referencing.operation;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.FactoryException;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.operation.*;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.internal.referencing.Identifier3D;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.transform.EllipsoidalTransform;
import org.geotoolkit.referencing.operation.transform.ConcatenatedTransform;
import org.geotoolkit.referencing.factory.NoSuchIdentifiedResource;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Descriptions;

import static org.geotoolkit.referencing.CRS.equalsApproximatively;
import static org.geotoolkit.util.collection.XCollections.isNullOrEmpty;
import static org.geotoolkit.factory.AuthorityFactoryFinder.getCoordinateOperationAuthorityFactory;


/**
 * A {@linkplain CoordinateOperationFactory coordinate operation factory} extended with the extra
 * informations provided by an {@linkplain CoordinateOperationAuthorityFactory authority factory}.
 * Such authority factory may help to find transformation paths not available otherwise (often
 * determined from empirical parameters). Authority factories can also provide additional
 * informations like the
 * {@linkplain CoordinateOperation#getDomainOfValidity domain of validity},
 * {@linkplain CoordinateOperation#getScope scope} and
 * {@linkplain CoordinateOperation#getCoordinateOperationAccuracy accuracy}.
 * <p>
 * When <code>{@linkplain #createOperation createOperation}(sourceCRS, targetCRS)</code> is invoked,
 * {@code AuthorityBackedFactory} fetches the authority codes for source and target CRS and submits
 * them to the {@linkplain #getAuthorityFactory underlying authority factory} through a call to its
 * <code>{@linkplain CoordinateOperationAuthorityFactory#createFromCoordinateReferenceSystemCodes
 * createFromCoordinateReferenceSystemCodes}(sourceCode, targetCode)</code> method. If the
 * authority factory doesn't know about the specified CRS, then the default (standalone)
 * process from the super-class is used as a fallback.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.2
 * @module
 */
@ThreadSafe
public class AuthorityBackedFactory extends DefaultCoordinateOperationFactory {
    /**
     * The default authority factory to use.
     */
    private static final String DEFAULT_AUTHORITY = "EPSG";

    /**
     * The authority factory to use for creating new operations.
     * If {@code null}, a default factory will be fetched when first needed.
     */
    private volatile CoordinateOperationAuthorityFactory authorityFactory;

    /**
     * Used as a guard against infinite recursivity.
     */
    private final ThreadLocal<Boolean> processing = new ThreadLocal<>();

    /**
     * Creates a new factory backed by a default EPSG authority factory.
     */
    public AuthorityBackedFactory() {
        this(EMPTY_HINTS);
    }

    /**
     * Creates a new factory backed by an authority factory fetched using the specified hints.
     * This constructor recognizes the {@link Hints#CRS_FACTORY CRS}, {@link Hints#CS_FACTORY CS},
     * {@link Hints#DATUM_FACTORY DATUM} and {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM}
     * {@code FACTORY} hints.
     *
     * @param userHints The hints, or {@code null} if none.
     */
    public AuthorityBackedFactory(Hints userHints) {
        super(userHints);
        /*
         * Removes the hint processed by the super-class. This include hints like
         * LENIENT_DATUM_SHIFT, which usually don't apply to authority factories.
         * An other way to see this is to said that this class "consumed" the hints.
         * By removing them, we increase the chances to get an empty map of remaining hints,
         * which in turn help to get the default CoordinateOperationAuthorityFactory
         * (instead of forcing a new instance).
         */
        if (userHints == null) {
            userHints = EMPTY_HINTS;
        }
        userHints = userHints.clone();
        userHints.keySet().removeAll(hints.keySet());
        userHints.remove(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER);
        userHints.remove(Hints.FORCE_STANDARD_AXIS_DIRECTIONS);
        userHints.remove(Hints.FORCE_STANDARD_AXIS_UNITS);
        if (!userHints.isEmpty()) {
            noForce(userHints);
            authorityFactory = getCoordinateOperationAuthorityFactory(DEFAULT_AUTHORITY, userHints);
        }
    }

    /**
     * Makes sure that every {@code FORCE_*} hints are set to false. We do that because we want
     * {@link CoordinateOperationAuthorityFactory#createFromCoordinateReferenceSystemCodes} to
     * returns coordinate operations straight from the EPSG database; we don't want an instance
     * like {@link org.geotoolkit.referencing.factory.OrderedAxisAuthorityFactory}. Axis swapping
     * are performed by {@link #createFromDatabase} in this class <strong>after</strong> we invoked
     * {@link CoordinateOperationAuthorityFactory#createFromCoordinateReferenceSystemCodes}. An
     * {@code OrderedAxisAuthorityFactory} instance in this class would be in the way and cause
     * an infinite recursivity.
     *
     * @see <a href="http://jira.codehaus.org/browse/GEOT-1161">GEOT-1161</a>
     */
    private static void noForce(final Hints userHints) {
        userHints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.FALSE);
        userHints.put(Hints.FORCE_STANDARD_AXIS_DIRECTIONS,   Boolean.FALSE);
        userHints.put(Hints.FORCE_STANDARD_AXIS_UNITS,        Boolean.FALSE);
    }

    /**
     * Invoked by {@link org.geotoolkit.factory.FactoryRegistry} in order to set the ordering
     * relative to other factories. The current implementation specifies that this factory
     * should have priority over a plain (not backed by an authority)
     * {@code DefaultCoordinateOperationFactory}.
     *
     * @since 3.00
     */
    @Override
    protected void setOrdering(final Organizer organizer) {
        super.setOrdering(organizer); // Defer to CachingCoordinateOperationFactory
        organizer.before(DefaultCoordinateOperationFactory.class, false);
    }

    /**
     * Returns the underlying coordinate operation authority factory. This is the factory
     * where this {@code AuthorityBackedFactory} will search for an explicitly specified
     * operation before to fallback to the super-class.
     *
     * @return The underlying coordinate operation authority factory.
     */
    protected CoordinateOperationAuthorityFactory getAuthorityFactory() {
        /*
         * No need to synchronize. This is not a big deal if AuthorityFactoryFinder is invoked
         * twice since it is already synchronized. Actually, we should not synchronize at all.
         * Every methods from the super-class are thread-safe without synchronized statements,
         * and we should preserve this advantage in order to reduce the risk of contention.
         */
        CoordinateOperationAuthorityFactory factory = authorityFactory;
        if (factory == null) {
            /*
             * Factory creation at this stage will happen only if null hints were specified at
             * construction time, which explain why it is correct to use {@link FactoryFinder}
             * with empty hints here.
             */
            final Hints hints = EMPTY_HINTS.clone();
            noForce(hints);
            authorityFactory = factory = getCoordinateOperationAuthorityFactory(DEFAULT_AUTHORITY, hints);
        }
        return factory;
    }

    /**
     * Invokes {@link #createOperation(CoordinateReferenceSystem, CoordinateReferenceSystem)}
     * with a guard against infinite recursivity. The check against recursivity is required
     * because the {@code createOperation(...)} method implemented in the super-class may
     * invoke {@code createFromDatabase(...)} again.
     *
     * @param  source The source CRS.
     * @param  target The target CRS.
     * @return The transform from source CRS to target CRS.
     * @throws FactoryException If an error occurred while creating the math transform.
     */
    private MathTransform getMathTransform(final CoordinateReferenceSystem source,
                                           final CoordinateReferenceSystem target)
            throws FactoryException
    {
        processing.set(Boolean.TRUE);
        try {
            return createOperation(source, target).getMathTransform();
        } finally {
            processing.set(Boolean.FALSE);
        }
    }

    /**
     * Returns an operation for conversion or transformation between two coordinate reference
     * systems. The default implementation extracts the authority code from the supplied
     * {@code sourceCRS} and {@code targetCRS}, and submit them to the
     * <code>{@linkplain CoordinateOperationAuthorityFactory#createFromCoordinateReferenceSystemCodes
     * createFromCoordinateReferenceSystemCodes}(sourceCode, targetCode)</code> methods.
     * If no operation is found for those codes, then this method returns {@code null}.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}, or {@code null}
     *         if no such operation is explicitly defined in the underlying database.
     *
     * @since 2.3
     */
    @Override
    protected CoordinateOperation createFromDatabase(final CoordinateReferenceSystem sourceCRS,
                                                     final CoordinateReferenceSystem targetCRS)
    {
        /*
         * Safety check against recursivity: returns null if this method is invoked indirectly
         * by the above getMathTransform(...) method. Note: there is no need to synchronize
         * since the Boolean is thread-local.
         */
        if (Boolean.TRUE.equals(processing.get())) {
            return null;
        }
        final CoordinateOperationAuthorityFactory authorityFactory = getAuthorityFactory();
        CoordinateOperation operation = null;
        int combine = 0;
        do {
            /*
             * First, try directly the provided (sourceCRS, targetCRS) pair. If it doesn't
             * work, try to use different combinations of original CRS and two-dimensional
             * components of those CRS.
             */
            final CoordinateReferenceSystem source, target;
            source = (combine & 2) == 0 ? sourceCRS : getHorizontalCRS(sourceCRS);
            target = (combine & 1) == 0 ? targetCRS : getHorizontalCRS(targetCRS);
            if (source != null && target != null) try {
                operation = createFromDatabase(source, target, authorityFactory);
                if (operation != null) {
                    /*
                     * Found an operation. If we had to extract the horizontal part of
                     * some 3D CRS, then we need to modify the coordinate operation.
                     */
                    if (combine != 0) {
                        operation = propagateVertical(operation, source != sourceCRS, target != targetCRS);
                        operation = complete(operation, sourceCRS, targetCRS);
                    }
                    break;
                }
            } catch (FactoryException exception) {
                /*
                 * Some kind of error more serious than NoSuchAuthorityCodeException (which was caught
                 * by the createFromDatabase(...) method invoked in the 'try' block). It may be serious,
                 * but the super-class is capable to provide a reasonable default behavior. Log as a
                 * warning and stop this method.
                 */
                log(exception, authorityFactory);
                return null;
            }
        } while (++combine != 4);
        return operation;
    }

    /**
     * Implementation of {@link #createFromDatabase(CoordinateReferenceSystem, CoordinateReferenceSystem)}
     * looking only for the specified CRS. This method does not try to get the 2D components of a 3D CRS.
     *
     * @param  sourceCRS Input coordinate reference system.
     * @param  targetCRS Output coordinate reference system.
     * @param  authorityFactory The factory to query for getting operation from the CRS.
     * @return A coordinate operation from {@code sourceCRS} to {@code targetCRS}, or {@code null}
     *         if no such operation is explicitly defined in the underlying database.
     * @throws FactoryException If an error occurred while creating the operation.
     */
    private CoordinateOperation createFromDatabase(
            final CoordinateReferenceSystem sourceCRS,
            final CoordinateReferenceSystem targetCRS,
            final CoordinateOperationAuthorityFactory authorityFactory) throws FactoryException
    {
        final Citation  authority = authorityFactory.getAuthority();
        final Identifier sourceID = IdentifiedObjects.getIdentifier(sourceCRS, authority);
        if (sourceID == null) {
            return null;
        }
        final Identifier targetID = IdentifiedObjects.getIdentifier(targetCRS, authority);
        if (targetID == null) {
            return null;
        }
        final String sourceCode = sourceID.getCode().trim();
        final String targetCode = targetID.getCode().trim();
        if (sourceCode.equals(targetCode)) {
            /*
             * NOTE: This check is mandatory because this method may be invoked in some situations
             *       where (sourceCode == targetCode) but (sourceCRS != targetCRS). Such situation
             *       should be illegal  (or at least the MathTransform from sourceCRS to targetCRS
             *       should be the identity transform),  but unfortunately it still happen because
             *       EPSG defines axis order as (latitude,longitude) for geographic CRS while most
             *       softwares expect (longitude,latitude) no matter what the EPSG authority said.
             *       We will need to computes a transform from sourceCRS to targetCRS ignoring the
             *       source and target codes. The superclass can do that, providing that we prevent
             *       the authority database to (legitimately) claims that the transformation from
             *       sourceCode to targetCode is the identity transform. See GEOT-854.
             */
            return null;
        }
        final boolean inverse;
        Set<CoordinateOperation> operations;
        try {
            operations = authorityFactory.createFromCoordinateReferenceSystemCodes(sourceCode, targetCode);
            inverse = isNullOrEmpty(operations);
            if (inverse) {
                /*
                 * No operation from 'source' to 'target' available. But maybe there is an inverse
                 * operation. This is typically the case when the user wants to convert from a
                 * projected to a geographic CRS. The EPSG database usually contains transformation
                 * paths for geographic to projected CRS only.
                 */
                operations = authorityFactory.createFromCoordinateReferenceSystemCodes(targetCode, sourceCode);
                if (operations == null) {
                    return null;
                }
            }
        } catch (NoSuchAuthorityCodeException exception) {
            /*
             * sourceCode or targetCode is unknown to the underlying authority factory.
             * Ignores the exception and fallback on the generic algorithm provided by
             * the super-class.
             */
            log(Level.FINE, exception, authorityFactory, true);
            return null;
        }
        for (final Iterator<CoordinateOperation> it=operations.iterator(); it.hasNext();) {
            CoordinateOperation candidate;
            try {
                // The call to it.next() must be inside the try..catch block,
                // which is why we don't use the Java 5 for loop syntax here.
                candidate = it.next();
                if (candidate == null) {
                    continue;
                }
                if (inverse) {
                    candidate = inverse(candidate);
                }
            } catch (NoninvertibleTransformException exception) {
                // The transform is non invertible. Log only at the fine level, since it
                // may be a normal failure - the transform is not required to be invertible.
                log(Level.FINE, exception, authorityFactory, true);
                continue;
            } catch (FactoryException exception) {
                // Other kind of error. Log a warning and try the next coordinate operation.
                // Note that this exception can occur only during the call to 'inverse', not
                // during the iteration.
                log(exception, authorityFactory);
                continue;
            } catch (BackingStoreException exception) {
                // Exception during the iteration. It may be a failure to instantiate
                // the CoordinateOperation because of an unsupported operation method.
                final Throwable cause = exception.getCause();
                log(cause != null ? cause : exception, authorityFactory);
                continue;
            }
            /*
             * It is possible that the Identifier in user's CRS is not quite right.   For
             * example the user may have created his source and target CRS from WKT using
             * a different axis order than the official one and still call it "EPSG:xxxx"
             * as if it were the official CRS. Checks if the source and target CRS for the
             * operation just created are really the same (ignoring metadata) than the one
             * specified by the user.
             *
             * NOTE:
             * A FactoryException is thrown by the getMathTransform(...) methods if we have been
             * unable to create a transform from the user-provided CRS to the authority-provided
             * CRS. In theory, the two above-cited CRS should been the same and the transform is
             * the identity transform. In practice, it is not always the case because of axis
             * swapping issue (see GEOT-854).
             *
             * If the getMathTransform(...) methods failed to create what should merely be an
             * affine transform for swapping axes (if not the identity transform), then we are
             * likely to fail for all other transforms. Let the FactoryException propagate in
             * order to stop the loop and avoid logging the same warning many time.
             */
            candidate = complete(candidate, sourceCRS, targetCRS);
            if (accept(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Completes (if necessary) the given coordinate operation for making sure that the source CRS
     * is the given one and the target CRS is the given one.  In principle, the given CRS shall be
     * equivalent to the operation source/target CRS. However discrepancies happen if the user CRS
     * have flipped axis order, or if we looked for 2D operation while the user provided 3D CRS.
     *
     * @param  operation The coordinate operation to complete.
     * @param  sourceCRS The source CRS requested by the user.
     * @param  targetCRS The target CRS requested by the user.
     * @return A coordinate operation for the given source and target CRS.
     * @throws FactoryException if the operation can't be constructed.
     */
    private CoordinateOperation complete(
            final CoordinateOperation       operation,
            final CoordinateReferenceSystem sourceCRS,
            final CoordinateReferenceSystem targetCRS)
            throws FactoryException
    {
        CoordinateReferenceSystem source = operation.getSourceCRS();
        CoordinateReferenceSystem target = operation.getTargetCRS();
        final MathTransform prepend, append;
        if (!equalsApproximatively(sourceCRS, source)) {
            prepend = getMathTransform(sourceCRS, source);
            source  = sourceCRS;
        } else {
            prepend = null;
        }
        if (!equalsApproximatively(target, targetCRS)) {
            append = getMathTransform(target, targetCRS);
            target = targetCRS;
        } else {
            append = null;
        }
        return transform(source, prepend, operation, append, target);
    }

    /**
     * Appends or prepends the specified math transforms to the
     * {@linkplain CoordinateOperation#getMathTransform math transform of the given operation}.
     * The new coordinate operation (if any) will share the same metadata than the original
     * operation, including the authority code.
     * <p>
     * This method is used in order to change axis order when the user-specified CRS
     * disagree with the authority-supplied CRS.
     *
     * @param sourceCRS The source CRS to give to the new operation.
     * @param prepend   The transform to prepend to the operation math transform.
     * @param operation The operation in which to prepend the math transforms.
     * @param append    The transform to append to the operation math transform.
     * @param targetCRS The target CRS to give to the new operation.
     * @return A new operation, or {@code operation} if {@code prepend} and {@code append} were
     *         nulls or identity transforms.
     * @throws FactoryException if the operation can't be constructed.
     */
    private CoordinateOperation transform(final CoordinateReferenceSystem sourceCRS,
                                          final MathTransform             prepend,
                                          final CoordinateOperation       operation,
                                          final MathTransform             append,
                                          final CoordinateReferenceSystem targetCRS)
            throws FactoryException
    {
        if ((prepend == null || prepend.isIdentity()) && (append == null || append.isIdentity())) {
            return operation;
        }
        final Map<String,?> properties = IdentifiedObjects.getProperties(operation);
        /*
         * In the particular case of concatenated operations, we can not prepend or append a math
         * transform to the operation as a whole (the math transform for a concatenated operation
         * is computed automatically as the concatenation of the math transform from every single
         * operations, and we need to stay consistent with that). Instead, we prepend to the first
         * single operation and append to the last single operation.
         */
        if (operation instanceof ConcatenatedOperation) {
            final List<SingleOperation> c = ((ConcatenatedOperation) operation).getOperations();
            final CoordinateOperation[] op = c.toArray(new CoordinateOperation[c.size()]);
            if (op.length != 0) {
                final CoordinateOperation first = op[0];
                if (op.length == 1) {
                    op[0] = transform(sourceCRS, prepend, first, append, targetCRS);
                } else {
                    final CoordinateOperation last = op[op.length - 1];
                    op[0]           = transform(sourceCRS, prepend, first, null, first.getTargetCRS());
                    op[op.length-1] = transform(last.getSourceCRS(), null, last, append, targetCRS);
                }
                return createConcatenatedOperation(properties, op);
            }
        }
        /*
         * Single operation case.
         */
        MathTransform transform = operation.getMathTransform();
        final MathTransformFactory mtFactory = getMathTransformFactory();
        if (prepend != null) {
            transform = mtFactory.createConcatenatedTransform(prepend, transform);
        }
        if (append != null) {
            transform = mtFactory.createConcatenatedTransform(transform, append);
        }
        assert !transform.equals(operation.getMathTransform()) : transform;
        final Class<? extends CoordinateOperation> type = AbstractCoordinateOperation.getType(operation);
        OperationMethod method = null;
        if (operation instanceof SingleOperation) {
            method = ((SingleOperation) operation).getMethod();
            if (method != null) {
                final Integer sourceDimensions = transform.getSourceDimensions();
                final Integer targetDimensions = transform.getTargetDimensions();
                if (!Objects.equals(sourceDimensions, method.getSourceDimensions()) ||
                    !Objects.equals(targetDimensions, method.getTargetDimensions()))
                {
                    method = new DefaultOperationMethod(method, sourceDimensions, targetDimensions);
                }
            }
        }
        return createFromMathTransform(properties, sourceCRS, targetCRS, transform, method, type);
    }

    /**
     * Returns a new coordinate operation with the ellipsoidal height added either in the source
     * coordinates, in the target coordinates or both. If there is an ellipsoidal transform, then
     * this method updates the transforms in order to use the ellipsoidal height (it has an impact
     * on the transformed values).
     * <p>
     * This method is not guaranteed to succeed in adding the ellipsoidal height. It works on a
     * <cite>best effort</cite> basis. In any case, the {@link #complete} method should be invoked
     * after this one in order to ensure that the source and target CRS are the expected ones.
     *
     * @param  operation The original (typically two-dimensional) coordinate operation.
     * @param  source3D  {@code true} for adding ellipsoidal height in source coordinates.
     * @param  target3D  {@code true} for adding ellipsoidal height in target coordinates.
     * @return A coordinate operation with the source and/or target coordinates made 3D.
     * @throws FactoryException If an error occurred while creating the coordinate operation.
     *
     * @since 3.16
     */
    private CoordinateOperation propagateVertical(CoordinateOperation operation,
            final boolean source3D, final boolean target3D) throws FactoryException
    {
        /*
         * Get the list of all single (non-concatenated) transformation steps.
         */
        final MathTransform[] steps;
        MathTransform transform = operation.getMathTransform();
        if (transform instanceof ConcatenatedTransform) {
            final List<MathTransform> list = ((ConcatenatedTransform) transform).getSteps();
            steps = list.toArray(new MathTransform[list.size()]);
        } else {
            steps = new MathTransform[] {transform};
        }
        /*
         * Find the first and the last EllipsoidalTransform. In the case of MolodenskyTransform,
         * the first and last occurences are typically the same instance. In the Geocentric datum
         * shift case, they are two different instances with an affine transform between them.
         */
        EllipsoidalTransform first=null, last=null;
        int indexFirst=0, indexLast=0;
        for (int i=0; i<steps.length; i++) {
            final MathTransform step = steps[i];
            if (step instanceof EllipsoidalTransform) {
                last = (EllipsoidalTransform) step;
                indexLast = i;
                if (first == null) {
                    first = last;
                    indexFirst = i;
                }
            }
        }
        CoordinateReferenceSystem  sourceCRS = operation.getSourceCRS();
        CoordinateReferenceSystem  targetCRS = operation.getTargetCRS();
        final MathTransformFactory mtFactory = getMathTransformFactory();
        boolean updated = false;
        /*
         * If we found ellipsoidal transforms, change their source and target dimensions
         * according the user request. Then, update the affine transform which is before
         * the first ellipsoidal transform, and the affine transform which is after the
         * last ellipsoidal transform.
         */
        if (first != null) {
            final EllipsoidalTransform newFirst, newLast;
            if (indexFirst == indexLast) {
                newFirst = newLast = first.forDimensions(source3D, target3D);
            } else {
                newFirst = first.forDimensions(source3D, true);
                newLast  = last .forDimensions(true, target3D);
            }
            /*
             * Now update the transformation steps with the new operations. The following loop
             * will be executed twice: once for the first ellipsoidal transform and once for
             * the last ellispoidal transform. It is okay if the last ellipsoid transform is
             * actually the same instance than the first one.
             */
            boolean isLast = false; do {
                final EllipsoidalTransform oldTr, newTr;
                final int index, remaining, toAdjust;
                CoordinateReferenceSystem crs;
                if (!isLast) {
                    oldTr     = first;
                    newTr     = newFirst;
                    index     = indexFirst;
                    toAdjust  = index - 1;
                    remaining = index;
                    crs       = sourceCRS;
                } else {
                    oldTr     = last;
                    newTr     = newLast;
                    index     = indexLast;
                    toAdjust  = index + 1;
                    remaining = steps.length - index - 1;
                    crs       = targetCRS;
                }
                if (oldTr != newTr) {
                    /*
                     * If the first ellipsoidal transform is the very first step, or if the
                     * last ellipsoidal transform is the very last step, then the update is
                     * easy: just replace the transform.
                     *
                     * But if there is an affine transform before the first ellipsoidal transform,
                     * or an affine transform after the last ellipsoidal transform, then we need
                     * to increase the number of dimensions of that affine transform.
                     */
                    MathTransform step = null; // The transform to adjust, if any.
                    boolean addVertical = true;
                    if (remaining != 0) {
                        addVertical = (remaining == 1);
                        step = steps[toAdjust];
                        int srcDim, tgtDim;
                        if (!isLast) {
                            srcDim = step .getSourceDimensions();
                            tgtDim = newTr.getSourceDimensions();
                            if (addVertical &= (tgtDim > srcDim)) {
                                srcDim = tgtDim;
                            }
                        } else {
                            srcDim = newTr.getTargetDimensions();
                            tgtDim = step .getTargetDimensions();
                            if (addVertical &= (srcDim > tgtDim)) {
                                tgtDim = srcDim;
                            }
                        }
                        Matrix matrix = Matrices.getMatrix(step);
                        if (matrix == null || !Matrices.isAffine(matrix)) {
                            continue; // Non affine step: do not update the transform steps.
                        }
                        matrix = Matrices.resizeAffine(matrix, srcDim, tgtDim);
                        step = mtFactory.createAffineTransform(matrix);
                    }
                    /*
                     * Now update the ellipsoidal transform and the CRS. The CRS should be an
                     * instance of SingleCRS. However we check for safety. If it is not, then
                     * we can not update the transforms.
                     */
                    if (addVertical) {
                        if (!(crs instanceof SingleCRS)) {
                            continue; // Can't update the CRS, so don't update the transforms.
                        }
                        crs = factories.toGeodetic3D((SingleCRS) crs);
                        if (!isLast) sourceCRS = crs;
                        else         targetCRS = crs;
                    }
                    steps[index] = newTr;
                    if (step != null) {
                        steps[toAdjust] = step;
                    }
                    updated = true;
                }
            } while ((isLast = !isLast) == true);
            /*
             * If at least one step has been changed, rebuild the concatenated transform.
             */
            if (updated) {
                transform = steps[0];
                for (int i=1; i<steps.length; i++) {
                    transform = mtFactory.createConcatenatedTransform(transform, steps[i]);
                }
            }
        } else if (source3D && target3D) {
            /*
             * If the transformation chain does not contain a datum shift, just let the
             * ellipsoidal height pass through. Current implementation handles only the
             * two-dimensional CRS.
             */
            if (transform.getSourceDimensions() == 2 && transform.getTargetDimensions() == 2) {
                if (sourceCRS instanceof SingleCRS && targetCRS instanceof SingleCRS) {
                    sourceCRS = factories.toGeodetic3D((SingleCRS) sourceCRS);
                    targetCRS = factories.toGeodetic3D((SingleCRS) targetCRS);
                    transform = mtFactory.createPassThroughTransform(0, transform, 1);
                    updated   = true;
                }
            }
        }
        /*
         * If the transform has been updated, rebuild the operation. The new operation will
         * inherit the same properties than the original operation. However a vertical axis
         * may have been added to the source and/or target CRS.
         */
        if (updated) {
            final Integer srcDim = sourceCRS.getCoordinateSystem().getDimension();
            final Integer tgtDim = targetCRS.getCoordinateSystem().getDimension();
            Class<? extends CoordinateOperation> type = AbstractCoordinateOperation.getType(operation);
            OperationMethod method = null;
            if (operation instanceof SingleOperation) {
                /*
                 * If the original operation was a SingleOperation, make an exact copy of
                 * the original method except for the number of source/target dimensions.
                 */
                method = ((SingleOperation) operation).getMethod();
                if (!Objects.equals(srcDim, method.getSourceDimensions()) ||
                    !Objects.equals(tgtDim, method.getTargetDimensions()))
                {
                    method = new DefaultOperationMethod(method, srcDim, tgtDim);
                }
            } else if (operation instanceof ConcatenatedOperation) {
                /*
                 * If the original operation was a ConcatenatedOperation, build a SingleOperation
                 * which will represent the operation as a whole.  In the current implementation,
                 * we don't try to build a new ConcatenatedOperation because separating the steps
                 * is hard (because of propagation of dimension changes accross different steps).
                 */
                type = SingleOperation.class;
                final StringBuilder buffer = new StringBuilder();
                for (final SingleOperation step : ((ConcatenatedOperation) operation).getOperations()) {
                    final String id = IdentifiedObjects.getIdentifier(step);
                    if (id != null) {
                        if (buffer.length() != 0) {
                            buffer.append(" + ");
                        }
                        buffer.append(id);
                    }
                    if (step instanceof Transformation) {
                        type = Transformation.class;
                    } else if (step instanceof Conversion && type != Transformation.class) {
                        type = Conversion.class;
                    }
                    // Don't check the Projection case; it is confusing
                    // since users expect a well known projection name.
                }
                method = createOperationMethod(Collections.singletonMap(OperationMethod.NAME_KEY,
                        Descriptions.format(Descriptions.Keys.CONCATENATED_OPERATION_ADAPTED_1, buffer)),
                        srcDim, tgtDim, null);
            }
            operation = createFromMathTransform(IdentifiedObjects.getProperties(operation),
                    sourceCRS, targetCRS, transform, method, type);
        }
        return operation;
    }

    /**
     * If a horizontal CRS can be extracted from the given CRS, returns it.
     * Otherwise returns {@code null}.
     *
     * @param  crs The CRS from which to extract the horizontal component.
     * @return The horizontal component, or {@code null} if none or unknown.
     *
     * @since 3.16
     */
    private static SingleCRS getHorizontalCRS(final CoordinateReferenceSystem crs) {
        final Identifier id = crs.getName();
        if (id instanceof Identifier3D) {
            return ((Identifier3D) id).horizontalCRS;
        }
        return null;
    }

    /**
     * Logs a warning when an object can't be created from the specified factory.
     *
     * @param exception The exception which occurred.
     * @param factory The factory used in the attempt to create an operation.
     */
    private static void log(final Throwable exception, final AuthorityFactory factory) {
        log(Level.WARNING, exception, factory, exception instanceof NoSuchIdentifiedResource);
    }

    /**
     * Logs an exception at the given level.
     *
     * @param level      The level to use for logging.
     * @param exception  The exception which occurred.
     * @param factory    The factory used in the attempt to create an operation.
     * @param isOptional Whatever the operation we just attempted was optional.
     */
    private static void log(final Level level, final Throwable exception,
            final AuthorityFactory factory, final boolean isOptional)
    {
        if (LOGGER.isLoggable(level)) {
            final LogRecord record = Loggings.format(level,
                    Loggings.Keys.CANT_CREATE_COORDINATE_OPERATION_1,
                    factory.getAuthority().getTitle());
            record.setSourceClassName(AuthorityBackedFactory.class.getName());
            record.setSourceMethodName("createFromDatabase");
            if (isOptional) {
                record.setMessage(Loggings.format(record) + ' ' + exception.getLocalizedMessage());
            } else {
                record.setThrown(exception);
            }
            record.setLoggerName(LOGGER.getName());
            LOGGER.log(record);
        }
    }

    /**
     * Returns {@code true} if the specified operation is acceptable. This method is invoked
     * automatically by <code>{@linkplain #createFromDatabase createFromDatabase}(...)</code>
     * for every operation candidates found. The default implementation returns always {@code
     * true}. Subclasses should override this method if they wish to filter the coordinate
     * operations to be returned.
     *
     * @param  operation The operation that {@code createFromDatabase} wants to return.
     * @return {@code true} if the given operation is acceptable, or {@code false} if
     *         {@code createFromDatabase} should look for an other one.
     *
     * @since 2.3
     */
    protected boolean accept(final CoordinateOperation operation) {
        return true;
    }

    /**
     * Returns whatever this factory and its underlying
     * {@linkplain #getAuthorityFactory authority factory} are available for use.
     *
     * @since 3.03
     */
    @Override
    public ConformanceResult availability() {
        try {
            final CoordinateOperationAuthorityFactory authorityFactory = getAuthorityFactory();
            if (authorityFactory instanceof Factory) {
                return ((Factory) authorityFactory).availability();
            }
        } catch (FactoryRegistryException exception) {
            // Declares as not available for the given raison.
            return new Availability(exception);
        }
        // Declare as available if not disposed.
        return super.availability();
    }
}
