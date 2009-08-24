/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.referencing.factory;

import java.util.*;
import java.awt.RenderingHints;

import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.Factory;

import org.geotoolkit.util.XArrays;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.FactoryRegistry;
import org.geotoolkit.factory.DynamicFactoryRegistry;
import org.geotoolkit.internal.referencing.VerticalDatumTypes;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.cs.AbstractCS;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.lang.ThreadSafe;

import static org.geotoolkit.internal.FactoryUtilities.addImplementationHints;


/**
 * A set of utilities methods working on more than one referencing factories.
 * This class can be used as a container for the following factories:
 * <p>
 * <ul>
 *   <li>{@link DatumFactory}</li>
 *   <li>{@link CSFactory}</li>
 *   <li>{@link CRSFactory}</li>
 *   <li>{@link MathTransformFactory}</li>
 * </ul>
 * <p>
 * The omission of {@link CoordinateOperationFactory} is intentional, because it is more
 * a processing class than an object builder.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.01
 *
 * @since 2.1
 * @level advanced
 * @module
 */
@ThreadSafe
public class ReferencingFactoryContainer extends ReferencingFactory {
    /**
     * A factory registry used as a cache for factory groups created up to date.
     */
    private static FactoryRegistry cache;

    /**
     * The {@linkplain Datum datum} factory.
     * If null, then a default factory will be created only when first needed.
     */
    private DatumFactory datumFactory;

    /**
     * The {@linkplain CoordinateSystem coordinate system} factory.
     * If null, then a default factory will be created only when first needed.
     */
    private CSFactory csFactory;

    /**
     * The {@linkplain CoordinateReferenceSystem coordinate reference system} factory.
     * If null, then a default factory will be created only when first needed.
     */
    private CRSFactory crsFactory;

    /**
     * The {@linkplain MathTransform math transform} factory.
     * If null, then a default factory will be created only when first needed.
     */
    private MathTransformFactory mtFactory;

    // WARNING: Do NOT put a CoordinateOperationFactory field in this class. We tried that in
    // Geotoolkit 2.2, and removed it in Geotoolkit 2.3 because it leads to very tricky recursivity
    // problems when we try to initialize it with FactoryFinder.getCoordinateOperationFactory.
    // The Datum, CS, CRS and MathTransform factories above are standalone, while the Geotoolkit
    // implementation of CoordinateOperationFactory has complex dependencies to all of those,
    // and even with authority factories.

    /**
     * Creates an instance from the specified hints. This method recognizes the
     * {@link Hints#CRS_FACTORY CRS}, {@link Hints#CS_FACTORY CS}, {@link Hints#DATUM_FACTORY
     * DATUM} and {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM} {@code FACTORY} hints.
     *
     * @param  hints The hints, or {@code null} for the system-wide default.
     * @return A factory group created from the specified set of hints.
     */
    public static ReferencingFactoryContainer instance(Hints hints) {
        if (hints == null) {
            hints = new Hints(); // Get the system-wide default hints.
        }
        /*
         * Use the same synchronization lock than FactoryFinder (instead of this class) in order
         * to reduce the risk of dead lock. This is because ReferencingFactoryContainer creation
         * may queries FactoryFinder, and some implementations managed by FactoryFinder may ask
         * for a ReferencingFactoryContainer in turn.
         */
        synchronized (FactoryFinder.class) {
            if (cache == null) {
                cache = new DynamicFactoryRegistry(ReferencingFactoryContainer.class);
                cache.registerServiceProvider(new ReferencingFactoryContainer(null),
                                                  ReferencingFactoryContainer.class);
            }
            return cache.getServiceProvider(ReferencingFactoryContainer.class, null, hints, null);
        }
    }

    /**
     * Creates an instance from the specified hints. This constructor recognizes the
     * {@link Hints#CRS_FACTORY CRS}, {@link Hints#CS_FACTORY CS}, {@link Hints#DATUM_FACTORY
     * DATUM} and {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM} {@code FACTORY} hints.
     * <p>
     * This constructor is public mainly for {@link org.geotoolkit.factory.DynamicFactoryRegistry}
     * usage. Consider invoking <code>{@linkplain #instance instance}(userHints)</code> instead.
     *
     * @param userHints The hints, or {@code null} if none (<strong>not</strong> the
     *        system-wide default; factory constructors consume the hints verbatism).
     */
    public ReferencingFactoryContainer(Hints userHints) {
        if (userHints != null) {
            userHints = userHints.clone();
            /*
             * If some factories were explicitly specified, fetch them immediately (no lazy
             * instantiation for them). Any factory not explicitly specified will be left to
             * null for now.
             */
            datumFactory =         (DatumFactory) getFactory(userHints, Hints.DATUM_FACTORY);
            csFactory    =            (CSFactory) getFactory(userHints, Hints.CS_FACTORY);
            crsFactory   =           (CRSFactory) getFactory(userHints, Hints.CRS_FACTORY);
            mtFactory    = (MathTransformFactory) getFactory(userHints, Hints.MATH_TRANSFORM_FACTORY);
            /*
             * Remove the entries consumed in the search for factories. We can do that only after
             * the search is done for all factories. If there is no remaining hints, then it doesn't
             * matter if some fields are still null. If there is some remaining hints, then we need
             * to fetch all factories that were not fetched by the above code because we can't guess
             * which hints are relevant and which ones are not.
             */
            if (datumFactory != null) userHints.remove(Hints.DATUM_FACTORY);
            if (csFactory    != null) userHints.remove(Hints.CS_FACTORY);
            if (crsFactory   != null) userHints.remove(Hints.CRS_FACTORY);
            if (mtFactory    != null) userHints.remove(Hints.MATH_TRANSFORM_FACTORY);
            if (!userHints.isEmpty()) {
                declaredFactoryHints(userHints);          // Adds the hints for the factories we fetched above.
                addImplementationHints(userHints, hints); // Copies temporarily the result in super.hints map.
                fetchAllFactories();                      // Forces fetching of remaining factories.
                hints.clear();                            // Cancel addImplementationHints(userHints).
            }
        }
    }

    /**
     * Returns the factory for the specified hint, or {@code null} if the hint is not a factory
     * instance (it could be for example a {@link Class}). The given hints are left untouched.
     */
    private static Factory getFactory(final Map<?,?> hints, final Hints.Key key) {
        final Object candidate = hints.get(key);
        return (candidate instanceof Factory) ? (Factory) candidate : null;
    }

    /**
     * Forces the initialisation of all factories. This method is invoked in one of the
     * following situations:
     * <p>
     * <ul>
     *   <li>When we need to consume all hints specified at construction time.</li>
     *   <li>When we need to resolve all hints used by the factories.</li>
     * </ul>
     *
     * {@note We try to create the factories in typical dependency order, with the CRS factory
     *        last because it has the greatest chances to depends on other factories.}
     */
    private void fetchAllFactories() {
        mtFactory    = getMathTransformFactory();
        datumFactory = getDatumFactory();
        csFactory    = getCSFactory();
        crsFactory   = getCRSFactory();
    }

    /**
     * Puts all available factories into the specified map of hints. This method is invoked
     * before or after {@link #fetchAllFactories}, depending on whatever we are consuming or
     * resolving hints.
     */
    private void declaredFactoryHints(final Map<? super RenderingHints.Key, Object> hints) {
        if (  crsFactory != null) hints.put(Hints.           CRS_FACTORY,   crsFactory);
        if (   csFactory != null) hints.put(Hints.            CS_FACTORY,    csFactory);
        if (datumFactory != null) hints.put(Hints.         DATUM_FACTORY, datumFactory);
        if (   mtFactory != null) hints.put(Hints.MATH_TRANSFORM_FACTORY,    mtFactory);
    }

    /**
     * Returns all factories in this group. The returned map contains values for the
     * {@link Hints#CRS_FACTORY CRS}, {@link Hints#CS_FACTORY CS}, {@link Hints#DATUM_FACTORY DATUM}
     * and {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM} {@code FACTORY} hints.
     */
    @Override
    public synchronized Map<RenderingHints.Key, ?> getImplementationHints() {
        if (hints.isEmpty()) {
            fetchAllFactories();
            declaredFactoryHints(hints);
        }
        return super.getImplementationHints();
    }

    /**
     * Returns the hints to be used for lazy creation of <em>default</em> factories in various
     * {@code getFoo} methods. At the difference of {@link #getImplementationHints}, this method
     * do not force fetching of factories that were not already obtained.
     */
    private Hints getCurrentHints() {
        final Hints completed = EMPTY_HINTS.clone();
        assert Thread.holdsLock(this);
        completed.putAll(hints);
        declaredFactoryHints(completed);
        return completed;
    }

    /**
     * Returns the {@linkplain Datum datum} factory.
     *
     * @return The Datum factory.
     */
    public synchronized DatumFactory getDatumFactory() {
        if (datumFactory == null) {
            datumFactory = FactoryFinder.getDatumFactory(getCurrentHints());
        }
        return datumFactory;
    }

    /**
     * Returns the {@linkplain CoordinateSystem coordinate system} factory.
     *
     * @return The Coordinate System factory.
     */
    public synchronized CSFactory getCSFactory() {
        if (csFactory == null) {
            csFactory = FactoryFinder.getCSFactory(getCurrentHints());
        }
        return csFactory;
    }

    /**
     * Returns the {@linkplain CoordinateReferenceSystem coordinate reference system} factory.
     *
     * @return The Coordinate Reference System factory.
     */
    public synchronized CRSFactory getCRSFactory() {
        if (crsFactory == null) {
            crsFactory = FactoryFinder.getCRSFactory(getCurrentHints());
        }
        return crsFactory;
    }

    /**
     * Returns the {@linkplain MathTransform math transform} factory.
     *
     * @return The Math Transform factory.
     */
    public synchronized MathTransformFactory getMathTransformFactory() {
        if (mtFactory == null) {
            mtFactory = FactoryFinder.getMathTransformFactory(getCurrentHints());
        }
        return mtFactory;
    }

    /**
     * Converts a 2D&nbsp;+&nbsp;1D compound CRS into a 3D CRS, if possible.
     * More specifically, if the specified {@linkplain CompoundCRS compound CRS} is made
     * of a {@linkplain GeographicCRS geographic} (or {@linkplain ProjectedCRS projected})
     * and a {@linkplain VerticalCRS vertical} CRS, and if the vertical CRS datum type is
     * {@code ELLIPSOIDAL} (height above the ellipsoid), then this method converts the compound
     * CRS in a single 3D CRS. Otherwise, the {@code crs} argument is returned unchanged.
     *
     * @param  crs The compound CRS to converts in a 3D geographic or projected CRS.
     * @return The 3D geographic or projected CRS, or {@code crs} if the change can't be applied.
     * @throws FactoryException if the object creation failed.
     */
    public CoordinateReferenceSystem toGeodetic3D(final CompoundCRS crs) throws FactoryException {
        List<SingleCRS> components = DefaultCompoundCRS.getSingleCRS(crs);
        final int count = components.size();
        SingleCRS   horizontal = null;
        VerticalCRS vertical   = null;
        int hi=0, vi=0;
        for (int i=0; i<count; i++) {
            final SingleCRS candidate = components.get(i);
            if (candidate instanceof VerticalCRS) {
                if (vertical == null) {
                    vertical = (VerticalCRS) candidate;
                    if (VerticalDatumTypes.ELLIPSOIDAL.equals(
                            vertical.getDatum().getVerticalDatumType()))
                    {
                        vi = i;
                        continue;
                    }
                }
                return crs;
            }
            if (candidate instanceof GeographicCRS || candidate instanceof  ProjectedCRS) {
                if (horizontal == null) {
                    horizontal = candidate;
                    if (horizontal.getCoordinateSystem().getDimension() == 2) {
                        hi = i;
                        continue;
                    }
                }
                return crs;
            }
        }
        if (horizontal != null && vertical != null && Math.abs(vi-hi) == 1) {
            /*
             * Exactly one horizontal and one vertical CRS has been found, and those two CRS are
             * consecutives. Constructs the new 3D CS. If the two above-cited components are the
             * only ones, the result is returned directly. Otherwise, a new compound CRS is created.
             */
            final boolean xyFirst = (hi < vi);
            final SingleCRS single = toGeodetic3D(count == 2 ? crs : null,
                    horizontal, vertical, xyFirst);
            if (count == 2) {
                return single;
            }
            final int i = xyFirst ? hi : vi;
            components = new ArrayList<SingleCRS>(components);
            components.remove(i);
            components.set(i, single);
            final SingleCRS[] c = components.toArray(new SingleCRS[components.size()]);
            return getCRSFactory().createCompoundCRS(AbstractIdentifiedObject.getProperties(crs), c);
        }
        return crs;
    }

    /**
     * Implementation of {@link #toGeodetic3D(CompoundCRS)} invoked after the horizontal and
     * vertical parts have been identified. This method may invokes itself recursively if the
     * horizontal CRS is a derived one.
     *
     * @param  crs        The compound CRS to converts in a 3D geographic CRS, or {@code null}.
     *                    Used only in order to infer the name properties of objects to create.
     * @param  horizontal The horizontal component of {@code crs}.
     * @param  vertical   The vertical   component of {@code crs}.
     * @param  xyFirst    {@code true} if the horizontal component appears before the vertical
     *                    component, or {@code false} for the converse.
     * @return The 3D geographic or projected CRS.
     * @throws FactoryException if the object creation failed.
     */
    private SingleCRS toGeodetic3D(final CompoundCRS crs, final SingleCRS horizontal,
                                   final VerticalCRS vertical, final boolean xyFirst)
            throws FactoryException
    {
        /*
         * Creates the set of axis in an order which depends of the xyFirst argument.
         * Then creates the property maps to be given to the object to be created.
         * They are common to whatever CRS type this method will create.
         */
        final CoordinateSystemAxis[] axis = new CoordinateSystemAxis[3];
        final CoordinateSystem cs = horizontal.getCoordinateSystem();
        axis[xyFirst ? 0 : 1] = cs.getAxis(0);
        axis[xyFirst ? 1 : 2] = cs.getAxis(1);
        axis[xyFirst ? 2 : 0] = vertical.getCoordinateSystem().getAxis(0);
        final Map<String,?> csName, crsName;
        if (crs != null) {
            csName  = AbstractIdentifiedObject.getProperties(crs.getCoordinateSystem());
            crsName = AbstractIdentifiedObject.getProperties(crs);
        } else {
            csName  = getTemporaryName(cs);
            crsName = getTemporaryName(horizontal);
        }
        final  CSFactory  csFactory = getCSFactory();
        final CRSFactory crsFactory = getCRSFactory();
        if (horizontal instanceof GeographicCRS) {
            /*
             * Merges a 2D geographic CRS with the vertical CRS. This is the easiest
             * part - we just give the 3 axis all together to a new GeographicCRS.
             */
            final GeographicCRS sourceCRS = (GeographicCRS) horizontal;
            final EllipsoidalCS targetCS  = csFactory.createEllipsoidalCS(csName, axis[0], axis[1], axis[2]);
            return crsFactory.createGeographicCRS(crsName, sourceCRS.getDatum(), targetCS);
        }
        if (horizontal instanceof ProjectedCRS) {
            /*
             * Merges a 2D projected CRS with the vertical CRS. This part is more tricky,
             * since we need a defining conversion which does not include axis swapping or
             * unit conversions. We revert them with concatenation of "CS to standardCS"
             * transform. The axis swapping will be added back by createProjectedCRS(...)
             * but not in the same place (they will be performed sooner than they would be
             * otherwise).
             */
            final ProjectedCRS  sourceCRS = (ProjectedCRS) horizontal;
            final CartesianCS   targetCS  = csFactory.createCartesianCS(csName, axis[0], axis[1], axis[2]);
            final GeographicCRS base2D    = sourceCRS.getBaseCRS();
            final GeographicCRS base3D    = (GeographicCRS) toGeodetic3D(null, base2D, vertical, xyFirst);
            final Matrix        prepend   = toStandard(base2D, false);
            final Matrix        append    = toStandard(sourceCRS, true);
            Conversion projection = sourceCRS.getConversionFromBase();
            if (!prepend.isIdentity() || !append.isIdentity()) {
                final MathTransformFactory mtFactory = getMathTransformFactory();
                MathTransform mt = projection.getMathTransform();
                mt = mtFactory.createConcatenatedTransform(
                     mtFactory.createConcatenatedTransform(
                     mtFactory.createAffineTransform(prepend), mt),
                     mtFactory.createAffineTransform(append));
                projection = new DefiningConversion(AbstractCS.getProperties(projection),
                                                    projection.getMethod(), mt);
            }
            return crsFactory.createProjectedCRS(crsName, base3D, projection, targetCS);
        }
        // Should never happen.
        throw new AssertionError(horizontal);
    }

    /**
     * Delegates to {@link AbstractCS#standard(CoordinateSystem)}.
     *
     * @param  crs The CRS from which to extract the CS to standardize.
     * @param  inverse {@code true} for the transform from the standardized to the non-standardized
     *         CS, instead of the usual opposite way.
     * @return The matrix of an affine transform performing the requested standardization.
     */
    private static Matrix toStandard(final CoordinateReferenceSystem crs, final boolean inverse) {
        final CoordinateSystem sourceCS = crs.getCoordinateSystem();
        final CoordinateSystem targetCS = AbstractCS.standard(sourceCS);
        if (inverse) {
            return AbstractCS.swapAndScaleAxis(targetCS, sourceCS);
        } else {
            return AbstractCS.swapAndScaleAxis(sourceCS, targetCS);
        }
    }

    /**
     * Returns a new coordinate reference system with only the specified dimension.
     * This method is used for example in order to get a component of a
     * {@linkplain CompoundCRS compound CRS}.
     *
     * @param  crs The original (usually compound) CRS.
     * @param  dimensions The dimensions to keep.
     * @return The CRS with only the specified dimensions.
     * @throws FactoryException if the given dimensions can not be isolated in the given CRS.
     */
    public CoordinateReferenceSystem separate(final CoordinateReferenceSystem crs,
                                              final int[] dimensions)
            throws FactoryException
    {
        final int length = dimensions.length;
        final int crsDimension = crs.getCoordinateSystem().getDimension();
        if (length==0 || dimensions[0]<0 || dimensions[length-1]>=crsDimension ||
            !XArrays.isSorted(dimensions, true))
        {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$1, "dimension"));
        }
        if (length == crsDimension) {
            return crs;
        }
        /*
         * If the CRS is a compound one, separate each components independently.
         * For each component, we search the sub-array of 'dimensions' that apply
         * to this component and invoke 'separate' recursively.
         */
        if (crs instanceof CompoundCRS) {
            int count=0, lowerDimension=0, lowerIndex=0;
            final List<CoordinateReferenceSystem> sources = ((CompoundCRS) crs).getComponents();
            final CoordinateReferenceSystem[] targets = new CoordinateReferenceSystem[sources.size()];
search:     for (final CoordinateReferenceSystem source : sources) {
                final int upperDimension = lowerDimension + source.getCoordinateSystem().getDimension();
                /*
                 * 'source' CRS applies to dimension 'lowerDimension' inclusive to 'upperDimension'
                 * exclusive. Now search the smallest range in the user-specified 'dimensions' that
                 * cover the [lowerDimension .. upperDimension] range.
                 */
                if (lowerIndex == dimensions.length) {
                    break search;
                }
                while (dimensions[lowerIndex] < lowerDimension) {
                    if (++lowerIndex == dimensions.length) {
                        break search;
                    }
                }
                int upperIndex = lowerIndex;
                while (dimensions[upperIndex] < upperDimension) {
                    if (++upperIndex == dimensions.length) {
                        break;
                    }
                }
                if (lowerIndex != upperIndex) {
                    final int[] sub = new int[upperIndex - lowerIndex];
                    for (int j=0; j<sub.length; j++) {
                        sub[j] = dimensions[j+lowerIndex] - lowerDimension;
                    }
                    targets[count++] = separate(source, sub);
                }
                lowerDimension = upperDimension;
                lowerIndex     = upperIndex;
            }
            if (count == 1) {
                return targets[0];
            }
            return getCRSFactory().createCompoundCRS(getTemporaryName(crs), XArrays.resize(targets, count));
        }
        /*
         * TODO: Implement other cases here (3D-GeographicCRS, etc.).
         *       It may requires the creation of new CoordinateSystem objects,
         *       which is why this method live in ReferencingFactoryContainer.
         */
        throw new FactoryException(Errors.format(
                Errors.Keys.CANT_SEPARATE_CRS_$1, crs.getName().getCode()));
    }

    /**
     * Returns a temporary name for object derived from the specified one.
     */
    private static Map<String,?> getTemporaryName(final IdentifiedObject source) {
        return Collections.singletonMap(IdentifiedObject.NAME_KEY,
                source.getName().getCode() + " (3D)");
    }
}
