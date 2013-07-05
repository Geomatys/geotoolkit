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
package org.geotoolkit.referencing.factory;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import javax.measure.unit.Unit;
import net.jcip.annotations.ThreadSafe;

import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.metadata.citation.Citation;
import org.opengis.util.FactoryException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.util.collection.WeakHashSet;
import org.apache.sis.util.Classes;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.lang.Decorator;

import static org.apache.sis.util.collection.Containers.hashMapCapacity;


/**
 * An authority factory which returns modified {@linkplain CoordinateReferenceSystem CRS},
 * {@linkplain CoordinateSystem CS} or {@linkplain Datum datum} objects from other factory
 * implementations. This class provides a set of {@code replace(...)} methods to be overridden
 * by subclasses in order to replace some {@linkplain CoordinateReferenceSystem CRS},
 * {@linkplain CoordinateSystem CS} or {@linkplain Datum datum} objects by other ones.
 * The replacement rules are determined by the subclass being used. For example the
 * {@link OrderedAxisAuthorityFactory} subclass can replace
 * {@linkplain CoordinateSystem coordinate systems} using (<var>latitude</var>,
 * <var>longitude</var>) axis order by coordinate systems using (<var>longitude</var>,
 * <var>latitude</var>) axis order.
 * <p>
 * All constructors are protected because this class must be subclassed in order to
 * determine which of the {@link DatumAuthorityFactory}, {@link CSAuthorityFactory}
 * and {@link CRSAuthorityFactory} interfaces to implement.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.01
 *
 * @since 2.3
 * @module
 */
@ThreadSafe
@Decorator(AuthorityFactory.class)
public class TransformedAuthorityFactory extends AuthorityFactoryAdapter {
    /**
     * Axis that need to be renamed if their direction changes.
     */
    private static final DefaultCoordinateSystemAxis[] RENAMEABLE = {
        DefaultCoordinateSystemAxis.NORTHING,   DefaultCoordinateSystemAxis.SOUTHING,
        DefaultCoordinateSystemAxis.EASTING,    DefaultCoordinateSystemAxis.WESTING
    };

    /**
     * The coordinate operation factory. Will be created only when first needed.
     */
    private transient CoordinateOperationFactory opFactory;

    /**
     * A pool of modified objects created up to date.
     */
    private final WeakHashSet<IdentifiedObject> pool = new WeakHashSet<>(IdentifiedObject.class);

    /**
     * A set of low-level factories to be used if none were found in {@link #datumFactory},
     * {@link #csFactory}, {@link #crsFactory} or {@link #operationFactory}. Will be created
     * only when first needed.
     *
     * @see #getFactoryContainer
     */
    private transient ReferencingFactoryContainer factories;

    /**
     * Creates a wrapper around the specified factory.
     *
     * @param factory The factory to wrap.
     */
    protected TransformedAuthorityFactory(final AuthorityFactory factory) {
        super(factory);
    }

    /**
     * Creates a wrapper around the specified factories.
     *
     * @param crsFactory
     *          The {@linkplain CoordinateReferenceSystem coordinate reference system}
     *          authority factory, or {@code null}.
     * @param csFactory
     *          The {@linkplain CoordinateSystem coordinate system} authority factory,
     *          or {@code null}.
     * @param datumFactory
     *          The {@linkplain Datum datum} authority factory, or {@code null}.
     * @param operationFactory
     *          The {@linkplain CoordinateOperation coordinate operation} authority factory,
     *          or {@code null}.
     */
    protected TransformedAuthorityFactory(final CRSAuthorityFactory crsFactory,
                                          final CSAuthorityFactory csFactory,
                                          final DatumAuthorityFactory datumFactory,
                                          final CoordinateOperationAuthorityFactory operationFactory)
    {
        super(crsFactory, csFactory, datumFactory, operationFactory);
    }

    /**
     * Creates a wrappers around the default factories for the specified authority.
     * The factories are fetched using {@link AuthorityFactoryFinder}.
     *
     * @param  authority The authority to wrap (example: {@code "EPSG"}). If {@code null},
     *         then all authority factories must be explicitly specified in the set of hints.
     * @param  userHints An optional set of hints, or {@code null} if none.
     * @throws FactoryRegistryException if at least one factory can not be obtained.
     *
     * @since 2.4
     */
    protected TransformedAuthorityFactory(final String authority, final Hints userHints)
            throws FactoryRegistryException
    {
        super(authority, userHints);
    }

    /**
     * Suggests a low-level factory group. If {@code crs} is {@code true}, then this method will
     * try to fetch the factory group from the CRS authority factory. Otherwise it will try to
     * fetch the factory group from the CS authority factory. This is used by subclasses like
     * {@link TransformedAuthorityFactory} that need low-level access to factories. Do not change
     * this method into a public one; we would need a better API before to do such thing.
     */
    private ReferencingFactoryContainer getFactoryContainer(final boolean crs) {
        ensureInitialized(); // Must be invoked before getFactory.
        final AuthorityFactory factory = getFactory(crs ? 0 : 1);
        if (factory instanceof DirectAuthorityFactory) {
            return ((DirectAuthorityFactory) factory).factories;
        }
        /*
         * No predefined factory group. Create one.  There is no need to synchronize, since
         * ReferencingFactoryContainer.instance is already synchronized and it is not a big
         * deal if we invoke this method twice.
         */
        ReferencingFactoryContainer candidate = factories;
        if (candidate == null) {
            factories = candidate = ReferencingFactoryContainer.instance(dependencyHints());
        }
        return candidate;
    }

    /**
     * Replaces the specified unit, if applicable. This method is invoked
     * automatically by the {@link #replace(CoordinateSystem)} method. The
     * default implementation returns the unit unchanged.
     *
     * @param  units The units to replace.
     * @return The new units, or {@code units} if no change were needed.
     * @throws FactoryException if an error occurred while creating the new units.
     */
    @Override
    protected Unit<?> replace(final Unit<?> units) throws FactoryException {
        return super.replace(units);
    }

    /**
     * Replaces the specified direction, if applicable. This method is invoked
     * automatically by the {@link #replace(CoordinateSystem)} method. The
     * default implementation returns the axis direction unchanged.
     *
     * @param  direction The axis direction to replace.
     * @return The new direction, or {@code direction} if no change were needed.
     * @throws FactoryException if an error occurred while creating the new axis direction.
     */
    protected AxisDirection replace(final AxisDirection direction) throws FactoryException {
        return direction;
    }

    /**
     * Replaces (if needed) the specified axis by a new one. The default implementation
     * invokes {@link #replace(Unit)} and {@link #replace(AxisDirection)}.
     *
     * @param  axis The coordinate system axis to replace.
     * @return The new coordinate system axis, or {@code axis} if no change were needed.
     * @throws FactoryException if an error occurred while creating the new coordinate system axis.
     */
    @Override
    protected CoordinateSystemAxis replace(CoordinateSystemAxis axis) throws FactoryException {
        final AxisDirection oldDirection = axis.getDirection();
        final AxisDirection newDirection = replace(oldDirection);
              Unit<?>       oldUnits     = axis.getUnit();
        final Unit<?>       newUnits     = replace(oldUnits);
        boolean directionChanged = !oldDirection.equals(newDirection);
        if (directionChanged) {
            /*
             * Check if the direction change implies an axis renaming.  For example if the axis
             * name was "Southing" and the direction has been changed from SOUTH to NORTH, then
             * the axis should be renamed as "Northing".
             */
            final String name = axis.getName().getCode();
            for (int i=0; i<RENAMEABLE.length; i++) {
                if (RENAMEABLE[i].nameMatches(name)) {
                    for (i=0; i<RENAMEABLE.length; i++) { // NOSONAR: The outer loop will not continue.
                        final CoordinateSystemAxis candidate = RENAMEABLE[i];
                        if (newDirection.equals(candidate.getDirection())) {
                            axis = candidate;          // The new axis, but may change again later.
                            oldUnits = axis.getUnit(); // For detecting change relative to new axis.
                            directionChanged = false;  // The new axis has the requested direction.
                            break;
                        }
                    }
                    break;
                }
            }
        }
        if (directionChanged || !oldUnits.equals(newUnits)) {
            final ReferencingFactoryContainer factories = getFactoryContainer(false);
            final CSFactory csFactory = factories.getCSFactory();
            final Map<String,?> properties = getProperties(axis);
            axis = csFactory.createCoordinateSystemAxis(properties,
                    axis.getAbbreviation(), newDirection, newUnits);
            axis = pool.unique(axis);
        }
        return axis;
    }

    /**
     * Replaces (if needed) the specified coordinate system by a new one. The
     * default implementation invokes {@link #replace(CoordinateSystemAxis) replace}
     * for each axis. In addition, axis are sorted if this factory implements the
     * {@link Comparator} interface.
     *
     * @param  cs The coordinate system to replace.
     * @return The new coordinate system, or {@code cs} if no change were needed.
     * @throws FactoryException if an error occurred while creating the new coordinate system.
     */
    @Override
    @SuppressWarnings("unchecked") // Parameterized type must not appear in public API.
    protected CoordinateSystem replace(final CoordinateSystem cs) throws FactoryException {
        final int dimension = cs.getDimension();
        final CoordinateSystemAxis[] orderedAxis = new CoordinateSystemAxis[dimension];
        for (int i=0; i<dimension; i++) {
            orderedAxis[i] = replace(cs.getAxis(i));
        }
        if (this instanceof Comparator<?>) {
            Arrays.sort(orderedAxis, (Comparator<CoordinateSystemAxis>) this);
        }
        for (int i=0; i<dimension; i++) {
            if (!orderedAxis[i].equals(cs.getAxis(i))) {
                final Class<? extends CoordinateSystem> type = cs.getClass();
                final CoordinateSystem modified = createCS(type, getProperties(cs), orderedAxis);
                assert Classes.implementSameInterfaces(type, modified.getClass(), CoordinateSystem.class);
                return pool.unique(modified);
            }
        }
        // All axis are identical - the CS was actually not changed.
        return cs;
    }

    /**
     * Replaces (if needed) the specified datum by a new one. The default
     * implementation returns the datum unchanged. Subclasses should override
     * this method if some datum replacements are desired.
     *
     * @param  datum The datum to replace.
     * @return The new datum, or {@code datum} if no change were needed.
     * @throws FactoryException if an error occurred while creating the new datum.
     */
    @Override
    @SuppressWarnings("unchecked") // Parameterized type must not appear in public API.
    protected Datum replace(final Datum datum) throws FactoryException {
        return super.replace(datum);
    }

    /**
     * Replaces (if needed) the specified coordinate reference system. The default
     * implementation checks if there is a {@linkplain #replace(Datum) datum replacement}
     * or a {@linkplain #replace(CoordinateSystem) coordinate system replacement}.
     * If there is at least one of those, then this method returns a new
     * coordinate reference system using the new datum and coordinate system.
     *
     * @param  crs The coordinate reference system to replace.
     * @return A new CRS, or {@code crs} if no change were needed.
     * @throws FactoryException if an error occurred while creating the new CRS object.
     */
    @Override
    @SuppressWarnings("unchecked") // Parameterized type must not appear in public API.
    protected CoordinateReferenceSystem replace(final CoordinateReferenceSystem crs)
            throws FactoryException
    {
        /*
         * Gets the replaced coordinate system and datum, and checks if there is any change.
         */
        final CoordinateSystem oldCS = crs.getCoordinateSystem();
        final CoordinateSystem cs = replace(oldCS);
        final Datum oldDatum, datum;
        if (crs instanceof SingleCRS) {
            oldDatum = ((SingleCRS) crs).getDatum();
            datum = replace(oldDatum);
        } else {
            datum = oldDatum = null;
        }
        final boolean sameCS = Objects.equals(cs, oldCS) && Objects.equals(datum, oldDatum);
        /*
         * Creates a new coordinate reference system using the same properties
         * than the original CRS, except for the coordinate system, datum and
         * authority code.
         */
        final CoordinateReferenceSystem modified;
        if (crs instanceof GeneralDerivedCRS) {
            final GeneralDerivedCRS         derivedCRS = (GeneralDerivedCRS) crs;
            final CoordinateReferenceSystem oldBaseCRS = derivedCRS.getBaseCRS();
            final CoordinateReferenceSystem    baseCRS = replace(oldBaseCRS);
            if (sameCS && Objects.equals(baseCRS, oldBaseCRS)) {
                return crs;
            }
            final Map<String,?> properties = getProperties(crs);
            final ReferencingFactoryContainer factories = getFactoryContainer(true);
            final CRSFactory crsFactory = factories.getCRSFactory();
            Conversion fromBase = derivedCRS.getConversionFromBase();
            fromBase = new DefiningConversion(getProperties(fromBase),
                    fromBase.getMethod(), fromBase.getParameterValues());
            if (crs instanceof ProjectedCRS) {
                modified = crsFactory.createProjectedCRS(properties,
                        (GeographicCRS) baseCRS, fromBase, (CartesianCS) cs);
            } else {
                // TODO: Need a createDerivedCRS method.
                throw new FactoryException(Errors.format(
                        Errors.Keys.UNSUPPORTED_CRS_1, crs.getName().getCode()));
            }
        } else if (sameCS) {
            return crs;
        } else {
            final Map<String,?> properties = getProperties(crs);
            final ReferencingFactoryContainer factories = getFactoryContainer(true);
            final CRSFactory crsFactory = factories.getCRSFactory();
            if (crs instanceof GeographicCRS) {
                modified = crsFactory.createGeographicCRS(properties,
                        (GeodeticDatum) datum, (EllipsoidalCS) cs);
            } else if (crs instanceof GeocentricCRS) {
                final GeodeticDatum gd = (GeodeticDatum) datum;
                if (cs instanceof CartesianCS) {
                    modified = crsFactory.createGeocentricCRS(properties, gd, (CartesianCS) cs);
                } else {
                    modified = crsFactory.createGeocentricCRS(properties, gd, (SphericalCS) cs);
                }
            } else if (crs instanceof VerticalCRS) {
                modified = crsFactory.createVerticalCRS(properties,
                        (VerticalDatum) datum, (VerticalCS) cs);
            } else if (crs instanceof TemporalCRS) {
                modified = crsFactory.createTemporalCRS(properties,
                        (TemporalDatum) datum, (TimeCS) cs);
            } else if (crs instanceof ImageCRS) {
                modified = crsFactory.createImageCRS(properties,
                        (ImageDatum) datum, (AffineCS) cs);
            } else if (crs instanceof EngineeringCRS) {
                modified = crsFactory.createEngineeringCRS(properties,
                        (EngineeringDatum) datum, cs);
            } else if (crs instanceof CompoundCRS) {
                final List<CoordinateReferenceSystem> elements = ((CompoundCRS) crs).getComponents();
                final CoordinateReferenceSystem[] m = new CoordinateReferenceSystem[elements.size()];
                for (int i=0; i<m.length; i++) {
                    m[i] = replace(elements.get(i));
                }
                modified = crsFactory.createCompoundCRS(properties, m);
            } else {
                throw new FactoryException(Errors.format(
                        Errors.Keys.UNSUPPORTED_CRS_1, crs.getName().getCode()));
            }
        }
        return pool.unique(modified);
    }

    /**
     * Replaces (if needed) the specified coordinate operation. The default
     * implementation checks if there is a source or target
     * {@linkplain #replace(CoordinateReferenceSystem) CRS replacement}. If
     * there is at least one of those, then this method returns a new coordinate
     * operation using the new CRS.
     *
     * @param  operation The coordinate operation to replace.
     * @return A new operation, or {@code operation} if no change were needed.
     * @throws FactoryException if an error occurred while creating the new operation object.
     */
    @Override
    @SuppressWarnings("unchecked") // Parameterized type must not appear in public API.
    protected CoordinateOperation replace(final CoordinateOperation operation)
            throws FactoryException
    {
        final CoordinateReferenceSystem oldSrcCRS = operation.getSourceCRS();
        final CoordinateReferenceSystem oldTgtCRS = operation.getTargetCRS();
        final CoordinateReferenceSystem sourceCRS = (oldSrcCRS != null) ? replace(oldSrcCRS) : null;
        final CoordinateReferenceSystem targetCRS = (oldTgtCRS != null) ? replace(oldTgtCRS) : null;
        if (Objects.equals(oldSrcCRS, sourceCRS) && Objects.equals(oldTgtCRS, targetCRS)) {
            return operation;
        }
        if (opFactory == null) {
            opFactory = getCoordinateOperationFactory();
        }
        return pool.unique(opFactory.createOperation(sourceCRS, targetCRS));
    }

    /**
     * Creates a new coordinate system of the specified kind. This method is
     * invoked automatically by {@link #replace(CoordinateSystem)} after it
     * determined that the axis need to be changed.
     *
     * @param type       The coordinate system type to create.
     * @param properties The properties to gives to the new coordinate system.
     * @param axis       The axis to give to the new coordinate system. Subclasses are
     *                   allowed to write directly in this array (no need to copy it).
     * @return A new coordinate system of the specified kind with the specified axis.
     * @throws FactoryException if the coordinate system can't be created.
     */
    private CoordinateSystem createCS(final Class<? extends CoordinateSystem> type,
            final Map<String,?> properties, final CoordinateSystemAxis[] axis) throws FactoryException
    {
        final int dimension = axis.length;
        final ReferencingFactoryContainer factories = getFactoryContainer(false);
        final CSFactory csFactory = factories.getCSFactory();
        if (CartesianCS.class.isAssignableFrom(type)) {
            switch (dimension) {
                case 2: return csFactory.createCartesianCS(properties, axis[0], axis[1]);
                case 3: return csFactory.createCartesianCS(properties, axis[0], axis[1], axis[2]);
            }
        } else if (EllipsoidalCS.class.isAssignableFrom(type)) {
            switch (dimension) {
                case 2: return csFactory.createEllipsoidalCS(properties, axis[0], axis[1]);
                case 3: return csFactory.createEllipsoidalCS(properties, axis[0], axis[1], axis[2]);
            }
        } else if (SphericalCS.class.isAssignableFrom(type)) {
            switch (dimension) {
                case 3: return csFactory.createSphericalCS(properties, axis[0], axis[1], axis[2]);
            }
        } else if (CylindricalCS.class.isAssignableFrom(type)) {
            switch (dimension) {
                case 3: return csFactory.createCylindricalCS(properties, axis[0], axis[1], axis[2]);
            }
        } else if (PolarCS.class.isAssignableFrom(type)) {
            switch (dimension) {
                case 2: return csFactory.createPolarCS(properties, axis[0], axis[1]);
            }
        } else if (VerticalCS.class.isAssignableFrom(type)) {
            switch (dimension) {
                case 1: return csFactory.createVerticalCS(properties, axis[0]);
            }
        } else if (TimeCS.class.isAssignableFrom(type)) {
            switch (dimension) {
                case 1: return csFactory.createTimeCS(properties, axis[0]);
            }
        } else if (LinearCS.class.isAssignableFrom(type)) {
            switch (dimension) {
                case 1: return csFactory.createLinearCS(properties, axis[0]);
            }
        } else if (UserDefinedCS.class.isAssignableFrom(type)) {
            switch (dimension) {
                case 2: return csFactory.createUserDefinedCS(properties, axis[0], axis[1]);
                case 3: return csFactory.createUserDefinedCS(properties, axis[0], axis[1], axis[2]);
            }
        }
        throw new FactoryException(Errors.format(Errors.Keys.UNSUPPORTED_COORDINATE_SYSTEM_1, type));
    }

    /**
     * Returns the properties to be given to an object replacing an original
     * one. If the new object keep the same authority, then all metadata are
     * preserved. Otherwise (i.e. if a new authority is given to the new
     * object), then the old identifiers will be removed from the new object
     * metadata.
     *
     * @param object The original object.
     * @return The properties to be given to the object created as a substitute
     *         of {@code object}.
     */
    private Map<String,?> getProperties(final IdentifiedObject object) {
        final Citation authority = getAuthority();
        if (!Objects.equals(authority, object.getName().getAuthority())) {
            return IdentifiedObjects.getProperties(object, authority);
        } else {
            return IdentifiedObjects.getProperties(object);
        }
    }

    /**
     * Creates an operation from coordinate reference system codes. The default implementation first
     * invokes the same method from the {@linkplain #operationFactory underlying operation factory},
     * and next invokes {@link #replace(CoordinateOperation) replace} for each operations.
     *
     * @throws FactoryException if the object creation failed for some other reason.
     */
    @Override
    public Set<CoordinateOperation> createFromCoordinateReferenceSystemCodes(
            final String sourceCRS, final String targetCRS) throws FactoryException
    {
        final Set<CoordinateOperation> operations, modified;
        operations = super.createFromCoordinateReferenceSystemCodes(sourceCRS, targetCRS);
        modified = new LinkedHashSet<>(hashMapCapacity(operations.size()));
        for (final Iterator<CoordinateOperation> it = operations.iterator(); it.hasNext();) {
            final CoordinateOperation operation;
            try {
                operation = it.next();
            } catch (BackingStoreException exception) {
                throw exception.unwrapOrRethrow(FactoryException.class);
            }
            modified.add(replace(operation));
        }
        return modified;
    }

    /**
     * Releases resources immediately instead of waiting for the garbage collector.
     * This method does <strong>not</strong> dispose the resources of wrapped factories
     * (e.g. {@link #crsFactory crsFactory}), because they may still in use by other classes.
     *
     * @param shutdown {@code false} for normal disposal, or {@code true} if this method
     *        is invoked during the process of a JVM shutdown.
     */
    @Override
    protected void dispose(final boolean shutdown) {
        pool.clear(); // Has its own synchronization.
        super.dispose(shutdown);
    }
}
