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
package org.geotoolkit.referencing.factory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.awt.RenderingHints;
import java.text.ParseException;
import javax.measure.unit.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import net.jcip.annotations.ThreadSafe;

import org.opengis.referencing.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.io.wkt.ReferencingParser;
import org.apache.sis.io.wkt.Symbols;
import org.geotoolkit.lang.Buffered;
import org.apache.sis.referencing.cs.*;
import org.apache.sis.referencing.crs.*;
import org.apache.sis.referencing.datum.*;
import org.apache.sis.referencing.crs.DefaultDerivedCRS;
import org.apache.sis.referencing.crs.DefaultProjectedCRS;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.apache.sis.internal.referencing.OperationMethods;
import org.apache.sis.referencing.cs.DefaultAffineCS;
import org.apache.sis.util.collection.WeakHashSet;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * Builds Geotk implementations of {@linkplain CoordinateReferenceSystem CRS},
 * {@linkplain CoordinateSystem CS} and {@linkplain Datum datum} objects. Most factory methods
 * expect properties given through a {@link Map} argument. The content of this map is described
 * in the {@link ObjectFactory} interface.
 *
 * @author Martin Desruisseaux (IRD)
 * @since 1.2
 * @module
 */
@Buffered
@ThreadSafe
public class ReferencingObjectFactory extends ReferencingFactory
        implements CSFactory, DatumFactory, CRSFactory
{
    /**
     * The math transform factory. Will be created only when first needed.
     */
    private volatile MathTransformFactory mtFactory;

    /**
     * The datum factory, which should be {@code this} or a {@link DatumAliases} backed
     * by {@code this}. This information is stored in order to ensure that every WKT
     * parser created by {@link #createFromWKT} use the same factories.
     */
    private DatumFactory datumFactory;

    /**
     * The object to use for parsing <cite>Well-Known Text</cite> (WKT) strings.
     * Values will be created only when first needed.
     */
    private final ThreadLocal<ReferencingParser> parser;

    /**
     * Set of weak references to existing objects (identifiers, CRS, Datum, whatever).
     * This set is used in order to return a pre-existing object instead of creating a
     * new one.
     */
    private final WeakHashSet<IdentifiedObject> pool;

    /**
     * Constructs a default factory. This method is public in order to allows instantiations
     * from a {@linkplain java.util.ServiceLoader service loaders}. Users should not instantiate
     * this factory directly, but use one of the following lines instead:
     *
     * {@preformat java
     *     DatumFactory factory = FactoryFinder.getDatumFactory (null);
     *     CSFactory    factory = FactoryFinder.getCSFactory    (null);
     *     CRSFactory   factory = FactoryFinder.getCRSFactory   (null);
     * }
     *
     * @see FactoryFinder
     */
    public ReferencingObjectFactory() {
        this(EMPTY_HINTS);
    }

    /**
     * Constructs a factory with the specified hints. Users should not instantiate this
     * factory directly, but use one of the following lines instead:
     *
     * {@preformat java
     *     DatumFactory factory = FactoryFinder.getDatumFactory (hints);
     *     CSFactory    factory = FactoryFinder.getCSFactory    (hints);
     *     CRSFactory   factory = FactoryFinder.getCRSFactory   (hints);
     * }
     *
     * @param hints An optional set of hints, or {@code null} if none.
     *
     * @see FactoryFinder
     *
     * @since 2.5
     */
    public ReferencingObjectFactory(final Hints hints) {
        pool = new WeakHashSet<>(IdentifiedObject.class);
        parser = new ThreadLocal<>();
        if (!isNullOrEmpty(hints)) {
            /*
             * Creates the dependencies (MathTransform factory, WKT parser...) now because
             * we need to process user's hints. Then, we will keep only the relevant hints.
             */
            mtFactory = FactoryFinder.getMathTransformFactory(hints);
        }
    }

    /**
     * Returns the datum factory, which should be {@code this} or a {@link DatumAliases}
     * backed by {@code this}.
     */
    private synchronized DatumFactory getDatumFactory() {
        if (datumFactory == null) {
            datumFactory = this; // The fallback value.
            for (final DatumFactory factory : FactoryFinder.getDatumFactories(EMPTY_HINTS)) {
                if (factory instanceof DatumAliases) {
                    if (((DatumAliases) factory).getDatumFactory() == this) {
                        datumFactory = factory;
                        break;
                    }
                }
            }
        }
        return datumFactory;
    }

    /**
     * Returns the math transform factory for internal usage only. The hints given to
     * {@link ReferencingFactoryFinder} must be null, since the non-null case should
     * have been handled by the constructor.
     *
     * @see #createParser
     */
    private MathTransformFactory getMathTransformFactory() {
        MathTransformFactory factory = mtFactory;
        if (factory == null) {
            // Following line must be outside the synchronized block, as a safety against
            // deadlocks. This is not a big deal if this information is fetched twice.
            final MathTransformFactory candidate = FactoryFinder.getMathTransformFactory(EMPTY_HINTS);
            synchronized (this) {
                // Double-checked locking - was a deprecated practice before Java 5.
                // Is okay since Java 5 provided that the variable is volatile.
                factory = mtFactory;
                if (factory == null) {
                    mtFactory = factory = candidate;
                    hints.put(Hints.MATH_TRANSFORM_FACTORY, factory);
                }
            }
        }
        return factory;
    }

    /**
     * Returns the hints used by this factory to customize its use.
     */
    @Override
    public Map<RenderingHints.Key, ?> getImplementationHints() {
        getMathTransformFactory(); // Forces the initialization of hints.
        return super.getImplementationHints();
    }



    /////////////////////////////////////////////////////////////////////////////////////////
    ////////                                                                         ////////
    ////////                        D A T U M   F A C T O R Y                        ////////
    ////////                                                                         ////////
    /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates an ellipsoid from radius values.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  semiMajorAxis Equatorial radius in supplied linear units.
     * @param  semiMinorAxis Polar radius in supplied linear units.
     * @param  unit Linear units of ellipsoid axes.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Ellipsoid createEllipsoid(final Map<String,?> properties,
            final double semiMajorAxis, final double semiMinorAxis,
            final Unit<Length> unit) throws FactoryException
    {
        Ellipsoid ellipsoid;
        try {
            ellipsoid = DefaultEllipsoid.createEllipsoid(properties,
                    semiMajorAxis, semiMinorAxis, unit);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        ellipsoid = pool.unique(ellipsoid);
        return ellipsoid;
    }

    /**
     * Creates an ellipsoid from an major radius, and inverse flattening.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  semiMajorAxis Equatorial radius in supplied linear units.
     * @param  inverseFlattening Eccentricity of ellipsoid.
     * @param  unit Linear units of major axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public Ellipsoid createFlattenedSphere(final Map<String,?> properties,
            final double semiMajorAxis, final double inverseFlattening,
            final Unit<Length> unit) throws FactoryException
    {
        Ellipsoid ellipsoid;
        try {
            ellipsoid = DefaultEllipsoid.createFlattenedSphere(properties,
                    semiMajorAxis, inverseFlattening, unit);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        ellipsoid = pool.unique(ellipsoid);
        return ellipsoid;
    }

    /**
     * Creates a prime meridian, relative to Greenwich.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  longitude Longitude of prime meridian in supplied angular units East of Greenwich.
     * @param  angularUnit Angular units of longitude.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public PrimeMeridian createPrimeMeridian(final Map<String,?> properties,
            final double longitude, final Unit<Angle> angularUnit) throws FactoryException
    {
        PrimeMeridian meridian;
        try {
            meridian = new DefaultPrimeMeridian(properties, longitude, angularUnit);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        meridian = pool.unique(meridian);
        return meridian;
    }

    /**
     * Creates geodetic datum from ellipsoid and (optionally) Bursa-Wolf parameters.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  ellipsoid Ellipsoid to use in new geodetic datum.
     * @param  primeMeridian Prime meridian to use in new geodetic datum.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public GeodeticDatum createGeodeticDatum(final Map<String,?> properties,
            final Ellipsoid ellipsoid, final PrimeMeridian primeMeridian) throws FactoryException
    {
        GeodeticDatum datum;
        try {
            datum = new DefaultGeodeticDatum(properties, ellipsoid, primeMeridian);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        datum = pool.unique(datum);
        return datum;
    }

    /**
     * Creates a vertical datum from an enumerated type value.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  type The type of this vertical datum (often geoidal).
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public VerticalDatum createVerticalDatum(final Map<String,?> properties,
            final VerticalDatumType type) throws FactoryException
    {
        VerticalDatum datum;
        try {
            datum = new DefaultVerticalDatum(properties, type);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        datum = pool.unique(datum);
        return datum;
    }

    /**
     * Creates a temporal datum from an enumerated type value.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  origin The date and time origin of this temporal datum.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public TemporalDatum createTemporalDatum(final Map<String,?> properties,
            final Date origin) throws FactoryException
    {
        TemporalDatum datum;
        try {
            datum = new DefaultTemporalDatum(properties, origin);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        datum = pool.unique(datum);
        return datum;
    }

    /**
     * Creates an engineering datum.
     *
     * @param  properties Name and other properties to give to the new object.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public EngineeringDatum createEngineeringDatum(final Map<String,?> properties)
            throws FactoryException
    {
        EngineeringDatum datum;
        try {
            datum = new DefaultEngineeringDatum(properties);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        datum = pool.unique(datum);
        return datum;
    }

    /**
     * Creates an image datum.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  pixelInCell Specification of the way the image grid is associated
     *         with the image data attributes.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public ImageDatum createImageDatum(final Map<String,?> properties,
            final PixelInCell pixelInCell) throws FactoryException
    {
        ImageDatum datum;
        try {
            datum = new DefaultImageDatum(properties, pixelInCell);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        datum = pool.unique(datum);
        return datum;
    }



    /////////////////////////////////////////////////////////////////////////////////////////
    ////////                                                                         ////////
    ////////            C O O R D I N A T E   S Y S T E M   F A C T O R Y            ////////
    ////////                                                                         ////////
    /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a coordinate system axis from an abbreviation and a unit.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  abbreviation The coordinate axis abbreviation.
     * @param  direction The axis direction.
     * @param  unit The coordinate axis unit.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateSystemAxis createCoordinateSystemAxis(final Map<String,?> properties,
            final String abbreviation, final AxisDirection direction,
            final Unit<?> unit) throws FactoryException
    {
        CoordinateSystemAxis axis;
        try {
            axis = new DefaultCoordinateSystemAxis(properties, abbreviation, direction, unit);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        axis = pool.unique(axis);
        return axis;
    }

    /**
     * Creates a two dimensional Cartesian coordinate system from the given pair of axis.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis0 The first  axis.
     * @param  axis1 The second axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CartesianCS createCartesianCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis0,
            final CoordinateSystemAxis axis1) throws FactoryException
    {
        CartesianCS cs;
        try {
            cs = new DefaultCartesianCS(properties, axis0, axis1);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates a three dimensional Cartesian coordinate system from the given set of axis.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis0 The first  axis.
     * @param  axis1 The second axis.
     * @param  axis2 The third  axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CartesianCS createCartesianCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis0,
            final CoordinateSystemAxis axis1,
            final CoordinateSystemAxis axis2) throws FactoryException
    {
        CartesianCS cs;
        try {
            cs = new DefaultCartesianCS(properties, axis0, axis1, axis2);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates a two dimensional coordinate system from the given pair of axis.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis0 The first  axis.
     * @param  axis1 The second axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public AffineCS createAffineCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis0,
            final CoordinateSystemAxis axis1) throws FactoryException
    {
        AffineCS cs;
        try {
            cs = new DefaultAffineCS(properties, axis0, axis1);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates a three dimensional coordinate system from the given set of axis.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis0 The first  axis.
     * @param  axis1 The second axis.
     * @param  axis2 The third  axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public AffineCS createAffineCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis0,
            final CoordinateSystemAxis axis1,
            final CoordinateSystemAxis axis2) throws FactoryException
    {
        AffineCS cs;
        try {
            cs = new DefaultAffineCS(properties, axis0, axis1, axis2);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates a polar coordinate system from the given pair of axis.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis0 The first  axis.
     * @param  axis1 The second axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public PolarCS createPolarCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis0,
            final CoordinateSystemAxis axis1) throws FactoryException
    {
        PolarCS cs;
        try {
            cs = new DefaultPolarCS(properties, axis0, axis1);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates a cylindrical coordinate system from the given set of axis.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis0 The first  axis.
     * @param  axis1 The second axis.
     * @param  axis2 The third  axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CylindricalCS createCylindricalCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis0,
            final CoordinateSystemAxis axis1,
            final CoordinateSystemAxis axis2) throws FactoryException
    {
        CylindricalCS cs;
        try {
            cs = new DefaultCylindricalCS(properties, axis0, axis1, axis2);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates a spherical coordinate system from the given set of axis.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis0 The first  axis.
     * @param  axis1 The second axis.
     * @param  axis2 The third  axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public SphericalCS createSphericalCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis0,
            final CoordinateSystemAxis axis1,
            final CoordinateSystemAxis axis2) throws FactoryException
    {
        SphericalCS cs;
        try {
            cs = new DefaultSphericalCS(properties, axis0, axis1, axis2);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates an ellipsoidal coordinate system without ellipsoidal height.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis0 The first  axis.
     * @param  axis1 The second axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public EllipsoidalCS createEllipsoidalCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis0,
            final CoordinateSystemAxis axis1) throws FactoryException
    {
        EllipsoidalCS cs;
        try {
            cs = new DefaultEllipsoidalCS(properties, axis0, axis1);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates an ellipsoidal coordinate system with ellipsoidal height.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis0 The first  axis.
     * @param  axis1 The second axis.
     * @param  axis2 The third  axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public EllipsoidalCS createEllipsoidalCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis0,
            final CoordinateSystemAxis axis1,
            final CoordinateSystemAxis axis2) throws FactoryException
    {
        EllipsoidalCS cs;
        try {
            cs = new DefaultEllipsoidalCS(properties, axis0, axis1, axis2);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates a vertical coordinate system.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis The axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public VerticalCS createVerticalCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis) throws FactoryException
    {
        VerticalCS cs;
        try {
            cs = new DefaultVerticalCS(properties, axis);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates a temporal coordinate system.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis The axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public TimeCS createTimeCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis) throws FactoryException
    {
        TimeCS cs;
        try {
            cs = new DefaultTimeCS(properties, axis);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates a linear coordinate system.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis The axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public LinearCS createLinearCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis) throws FactoryException
    {
        LinearCS cs;
        try {
            cs = new DefaultLinearCS(properties, axis);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates a two dimensional user defined coordinate system from the given pair of axis.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis0 The first  axis.
     * @param  axis1 The second axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public UserDefinedCS createUserDefinedCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis0,
            final CoordinateSystemAxis axis1) throws FactoryException
    {
        UserDefinedCS cs;
        try {
            cs = new DefaultUserDefinedCS(properties, axis0, axis1);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }

    /**
     * Creates a three dimensional user defined coordinate system from the given set of axis.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  axis0 The first  axis.
     * @param  axis1 The second axis.
     * @param  axis2 The third  axis.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public UserDefinedCS createUserDefinedCS(final Map<String,?> properties,
            final CoordinateSystemAxis axis0,
            final CoordinateSystemAxis axis1,
            final CoordinateSystemAxis axis2) throws FactoryException
    {
        UserDefinedCS cs;
        try {
            cs = new DefaultUserDefinedCS(properties, axis0, axis1, axis2);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        cs = pool.unique(cs);
        return cs;
    }



    /////////////////////////////////////////////////////////////////////////////////////////
    ////////                                                                         ////////
    ////////  C O O R D I N A T E   R E F E R E N C E   S Y S T E M   F A C T O R Y  ////////
    ////////                                                                         ////////
    /////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Creates a compound coordinate reference system from an ordered
     * list of {@code CoordinateReferenceSystem} objects.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  elements ordered array of {@code CoordinateReferenceSystem} objects.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CompoundCRS createCompoundCRS(final Map<String,?> properties,
            final CoordinateReferenceSystem... elements) throws FactoryException
    {
        CompoundCRS crs;
        try {
            crs = new DefaultCompoundCRS(properties, elements);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        crs = pool.unique(crs);
        return crs;
    }

    /**
     * Creates a engineering coordinate reference system.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  datum Engineering datum to use in created CRS.
     * @param  cs The coordinate system for the created CRS.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public EngineeringCRS createEngineeringCRS(final Map<String,?> properties,
            final EngineeringDatum datum, final CoordinateSystem cs) throws FactoryException
    {
        EngineeringCRS crs;
        try {
            crs = new DefaultEngineeringCRS(properties, datum, cs);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        crs = pool.unique(crs);
        return crs;
    }

    /**
     * Creates an image coordinate reference system.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  datum Image datum to use in created CRS.
     * @param  cs The Cartesian or Oblique Cartesian coordinate system for the created CRS.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public ImageCRS createImageCRS(final Map<String,?> properties,
            final ImageDatum datum, final AffineCS cs) throws FactoryException
    {
        ImageCRS crs;
        try {
            crs = new DefaultImageCRS(properties, datum, cs);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        crs = pool.unique(crs);
        return crs;
    }

    /**
     * Creates a temporal coordinate reference system.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  datum Temporal datum to use in created CRS.
     * @param  cs The Temporal coordinate system for the created CRS.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public TemporalCRS createTemporalCRS(final Map<String,?> properties,
            final TemporalDatum datum, final TimeCS cs) throws FactoryException
    {
        TemporalCRS crs;
        try {
            crs = new DefaultTemporalCRS(properties, datum, cs);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        crs = pool.unique(crs);
        return crs;
    }

    /**
     * Creates a vertical coordinate reference system.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  datum Vertical datum to use in created CRS.
     * @param  cs The Vertical coordinate system for the created CRS.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public VerticalCRS createVerticalCRS(final Map<String,?> properties,
            final VerticalDatum datum, final VerticalCS cs) throws FactoryException
    {
        VerticalCRS crs;
        try {
            crs = new DefaultVerticalCRS(properties, datum, cs);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        crs = pool.unique(crs);
        return crs;
    }

    /**
     * Creates a geocentric coordinate reference system from a {@linkplain CartesianCS
     * Cartesian coordinate system}.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  datum Geodetic datum to use in created CRS.
     * @param  cs The Cartesian coordinate system for the created CRS.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public GeocentricCRS createGeocentricCRS(final Map<String,?> properties,
            final GeodeticDatum datum, final CartesianCS cs) throws FactoryException
    {
        GeocentricCRS crs;
        try {
            crs = new DefaultGeocentricCRS(properties, datum, cs);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        crs = pool.unique(crs);
        return crs;
    }

    /**
     * Creates a geocentric coordinate reference system from a {@linkplain SphericalCS
     * spherical coordinate system}.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  datum Geodetic datum to use in created CRS.
     * @param  cs The spherical coordinate system for the created CRS.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public GeocentricCRS createGeocentricCRS(final Map<String,?> properties,
            final GeodeticDatum datum, final SphericalCS cs) throws FactoryException
    {
        GeocentricCRS crs;
        try {
            crs = new DefaultGeocentricCRS(properties, datum, cs);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        crs = pool.unique(crs);
        return crs;
    }

    /**
     * Creates a geographic coordinate reference system.
     * It could be <var>Latitude</var>/<var>Longitude</var> or
     * <var>Longitude</var>/<var>Latitude</var>.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  datum Geodetic datum to use in created CRS.
     * @param  cs The ellipsoidal coordinate system for the created CRS.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public GeographicCRS createGeographicCRS(final Map<String,?> properties,
            final GeodeticDatum datum, final EllipsoidalCS cs) throws FactoryException
    {
        GeographicCRS crs;
        try {
            crs = new DefaultGeographicCRS(properties, datum, cs);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        crs = pool.unique(crs);
        return crs;
    }

    /**
     * Creates a derived coordinate reference system from a conversion.
     * It is the user's responsibility to ensure that the conversion performs all required steps,
     * including {@linkplain AbstractCS#swapAndScaleAxis unit conversions and change of axis order},
     * if needed.
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  baseCRS Coordinate reference system to base projection on.
     * @param  conversionFromBase The {@linkplain DefiningConversion defining conversion}.
     * @param  derivedCS The coordinate system for the derived CRS.
     * @throws FactoryException if the object creation failed.
     *
     * @since 2.5
     */
    @Override
    public DerivedCRS createDerivedCRS(final Map<String,?> properties,
            final CoordinateReferenceSystem baseCRS, final Conversion conversionFromBase,
            final CoordinateSystem derivedCS) throws FactoryException
    {
        DerivedCRS crs;
        try {
            crs = new DefaultDerivedCRS(properties, (SingleCRS) baseCRS, conversionFromBase, derivedCS);    // TODO: cast
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        crs = pool.unique(crs);
        return crs;
    }

    /**
     * Creates a projected coordinate reference system from a conversion. The supplied
     * conversion should <strong>not</strong> includes the operation steps for performing
     * {@linkplain AbstractCS#swapAndScaleAxis unit conversions and change of axis order}
     * since those operations will be inferred by this constructor
     *
     * @param  properties Name and other properties to give to the new object.
     * @param  baseCRS Geographic coordinate reference system to base projection on.
     * @param  conversionFromBase The {@linkplain DefiningConversion defining conversion}.
     * @param  derivedCS The coordinate system for the projected CRS.
     * @throws FactoryException if the object creation failed.
     *
     * @since 2.5
     */
    @Override
    public ProjectedCRS createProjectedCRS(Map<String,?> properties,
            final GeographicCRS baseCRS, Conversion conversionFromBase,
            final CartesianCS derivedCS) throws FactoryException
    {
        final Map<String,Object> copy = new HashMap<>(properties);
        copy.put(OperationMethods.MT_FACTORY, getMathTransformFactory());
        ProjectedCRS crs;
        try {
            crs = new DefaultProjectedCRS(properties, baseCRS, conversionFromBase, derivedCS);
        } catch (IllegalArgumentException exception) {
            throw new FactoryException(exception);
        }
        crs = pool.unique(crs);
        return crs;
    }

    /**
     * Creates a coordinate reference system object from a XML string.
     *
     * @param  xml Coordinate reference system encoded in XML format.
     * @throws FactoryException if the object creation failed.
     *
     * @todo Not yet implemented.
     */
    @Override
    public CoordinateReferenceSystem createFromXML(final String xml) throws FactoryException {
        throw new FactoryException("Not yet implemented");
    }

    /**
     * Creates a coordinate reference system object from a string.
     *
     * @param  wkt Coordinate system encoded in Well-Known Text format.
     * @throws FactoryException if the object creation failed.
     */
    @Override
    public CoordinateReferenceSystem createFromWKT(final String wkt) throws FactoryException {
        /*
         * Note: while this factory is thread safe, the WKT parser is not.
         * We need either to synchronize, or use one instance per thread.
         */
        ReferencingParser parser = this.parser.get();
        if (parser == null) {
            parser = new ReferencingParser(Symbols.getDefault(),
                    getDatumFactory(), this, this, getMathTransformFactory());
            parser.setISOConform(true);
            this.parser.set(parser);
        }
        try {
            return parser.parseCoordinateReferenceSystem(wkt);
        } catch (ParseException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof FactoryException) {
                throw (FactoryException) cause;
            }
            throw new FactoryException(exception);
        }
    }
}
