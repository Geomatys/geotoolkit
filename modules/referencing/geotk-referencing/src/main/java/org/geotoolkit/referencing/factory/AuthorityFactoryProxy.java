/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import javax.measure.unit.Unit;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.metadata.extent.Extent;
import org.opengis.util.FactoryException;

import org.geotoolkit.resources.Errors;


/**
 * Delegates object creations to one of the {@code create} methods in a backing
 * {@linkplain AuthorityFactory authority factory}. It is possible to use the generic
 * {@link AuthorityFactory#createObject(String)} method instead of this class, but some factories
 * are more efficient when we use the most specific {@code create} method. For example when using
 * a {@linkplain org.geotoolkit.referencing.factory.epsg.DirectEpsgFactory EPSG factory backed by
 * a SQL database}, invoking {@link CRSAuthorityFactory#createProjectedCRS(String)} instead of
 * {@link AuthorityFactory#createObject(String)} method will reduce the amount of tables to be
 * queried.
 * <p>
 * This class is useful when the same {@code create} method need to be invoked often,
 * but is unknown at compile time. It may also be used as a workaround for authority
 * factories that don't implement the {@code createObject} method.
 * <p>
 * <b>Example:</b> The following code creates a proxy which will delegates its work to the
 * {@link CRSAuthorityFactory#createGeographicCRS createGeographicCRS} method.
 *
 * {@preformat java
 *     String code = ...;
 *     AuthorityFactory factory = ...;
 *     AuthorityFactoryProxy proxy = AuthorityFactoryProxy.getInstance(GeographicCRS.class);
 *     GeographicCRS crs = proxy.create(factory, code); // Invokes factory.createGeographicCRS(code);
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 2.4
 * @module
 */
@Immutable
abstract class AuthorityFactoryProxy<T> {
    /**
     * The type of objects to be created.
     */
    public final Class<T> type;

    /**
     * Creates a new proxy for objects of the given type.
     */
    AuthorityFactoryProxy(final Class<T> type) {
        this.type = type;
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        return "AuthorityFactoryProxy[" + type.getSimpleName() + ']';
    }

    /**
     * Casts the given factory into a datum authority factory, or thrown a
     * {@link FactoryException} if the given factory is not of the expected type.
     */
    final DatumAuthorityFactory datumFactory(final AuthorityFactory factory) throws FactoryException {
        if (factory instanceof DatumAuthorityFactory) {
            return (DatumAuthorityFactory) factory;
        }
        throw factoryNotFound(DatumAuthorityFactory.class);
    }

    /**
     * Casts the given factory into a CS authority factory, or thrown a
     * {@link FactoryException} if the given factory is not of the expected type.
     */
    final CSAuthorityFactory csFactory(final AuthorityFactory factory) throws FactoryException {
        if (factory instanceof CSAuthorityFactory) {
            return (CSAuthorityFactory) factory;
        }
        throw factoryNotFound(CSAuthorityFactory.class);
    }

    /**
     * Casts the given factory into a CRS authority factory, or thrown a
     * {@link FactoryException} if the given factory is not of the expected type.
     */
    final CRSAuthorityFactory crsFactory(final AuthorityFactory factory) throws FactoryException {
        if (factory instanceof CRSAuthorityFactory) {
            return (CRSAuthorityFactory) factory;
        }
        throw factoryNotFound(CRSAuthorityFactory.class);
    }

    /**
     * Casts the given factory into an operation authority factory, or thrown a
     * {@link FactoryException} if the given factory is not of the expected type.
     */
    final CoordinateOperationAuthorityFactory opFactory(final AuthorityFactory factory) throws FactoryException {
        if (factory instanceof CoordinateOperationAuthorityFactory) {
            return (CoordinateOperationAuthorityFactory) factory;
        }
        throw factoryNotFound(CoordinateOperationAuthorityFactory.class);
    }

    /**
     * Casts the given factory into a Geotk authority factory, or thrown a
     * {@link FactoryException} if the given factory is not of the expected type.
     */
    final AbstractAuthorityFactory geotkFactory(final AuthorityFactory factory) throws FactoryException {
        if (factory instanceof CRSAuthorityFactory) {
            return (AbstractAuthorityFactory) factory;
        }
        throw factoryNotFound(AbstractAuthorityFactory.class);
    }

    /**
     * Returns the exception to be thrown when a factory is not found.
     */
    private static FactoryException factoryNotFound(final Class<? extends AuthorityFactory> type) {
        return new FactoryException(Errors.format(Errors.Keys.FACTORY_NOT_FOUND_1, type));
    }

    /**
     * Creates the object for the given code.
     *
     * @param  factory The factory to use for creating the object.
     * @param  code    The code for which to create an object.
     * @return The object created from the given code.
     * @throws FactoryException If an error occurred while creating the object.
     */
    public abstract T create(AbstractAuthorityFactory factory, String code) throws FactoryException;

    /**
     * Creates the object for the given code using only GeoAPI interfaces.
     * This method is slightly less efficient than the above {@link #create} method.
     *
     * @param  factory The factory to use for creating the object.
     * @param  code    The code for which to create an object.
     * @return The object created from the given code.
     * @throws FactoryException If an error occurred while creating the object.
     *
     * @since 3.18
     */
    public abstract T createFromAPI(AuthorityFactory factory, String code) throws FactoryException;

    /**
     * The proxy for the {@link AbstractAuthorityFactory#createObject} method.
     */
    public static final AuthorityFactoryProxy<IdentifiedObject> OBJECT =
        new AuthorityFactoryProxy<IdentifiedObject>(IdentifiedObject.class) {
            @Override public IdentifiedObject create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createObject(code);
            }
            @Override public IdentifiedObject createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return factory.createObject(code);
            }
    };

    public static final AuthorityFactoryProxy<Datum> DATUM =
        new AuthorityFactoryProxy<Datum>(Datum.class) {
            @Override public Datum create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createDatum(code);
            }
            @Override public Datum createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return datumFactory(factory).createDatum(code);
            }
    };

    public static final AuthorityFactoryProxy<EngineeringDatum> ENGINEERING_DATUM =
        new AuthorityFactoryProxy<EngineeringDatum>(EngineeringDatum.class) {
            @Override public EngineeringDatum create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createEngineeringDatum(code);
            }
            @Override public EngineeringDatum createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return datumFactory(factory).createEngineeringDatum(code);
            }
    };

    public static final AuthorityFactoryProxy<ImageDatum> IMAGE_DATUM =
        new AuthorityFactoryProxy<ImageDatum>(ImageDatum.class) {
            @Override public ImageDatum create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createImageDatum(code);
            }
            @Override public ImageDatum createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return datumFactory(factory).createImageDatum(code);
            }
    };

    public static final AuthorityFactoryProxy<VerticalDatum> VERTICAL_DATUM =
        new AuthorityFactoryProxy<VerticalDatum>(VerticalDatum.class) {
            @Override public VerticalDatum create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createVerticalDatum(code);
            }
            @Override public VerticalDatum createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return datumFactory(factory).createVerticalDatum(code);
            }
    };

    public static final AuthorityFactoryProxy<TemporalDatum> TEMPORAL_DATUM =
        new AuthorityFactoryProxy<TemporalDatum>(TemporalDatum.class) {
            @Override public TemporalDatum create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createTemporalDatum(code);
            }
            @Override public TemporalDatum createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return datumFactory(factory).createTemporalDatum(code);
            }
    };

    public static final AuthorityFactoryProxy<GeodeticDatum> GEODETIC_DATUM =
        new AuthorityFactoryProxy<GeodeticDatum>(GeodeticDatum.class) {
            @Override public GeodeticDatum create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createGeodeticDatum(code);
            }
            @Override public GeodeticDatum createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return datumFactory(factory).createGeodeticDatum(code);
            }
    };

    public static final AuthorityFactoryProxy<Ellipsoid> ELLIPSOID =
        new AuthorityFactoryProxy<Ellipsoid>(Ellipsoid.class) {
            @Override public Ellipsoid create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createEllipsoid(code);
            }
            @Override public Ellipsoid createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return datumFactory(factory).createEllipsoid(code);
            }
    };

    public static final AuthorityFactoryProxy<PrimeMeridian> PRIME_MERIDIAN =
        new AuthorityFactoryProxy<PrimeMeridian>(PrimeMeridian.class) {
            @Override public PrimeMeridian create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createPrimeMeridian(code);
            }
            @Override public PrimeMeridian createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return datumFactory(factory).createPrimeMeridian(code);
            }
    };

    public static final AuthorityFactoryProxy<Extent> EXTENT =
        new AuthorityFactoryProxy<Extent>(Extent.class) {
            @Override public Extent create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createExtent(code);
            }
            @Override public Extent createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return geotkFactory(factory).createExtent(code);
            }
    };

    public static final AuthorityFactoryProxy<CoordinateSystem> COORDINATE_SYSTEM =
        new AuthorityFactoryProxy<CoordinateSystem>(CoordinateSystem.class) {
            @Override public CoordinateSystem create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCoordinateSystem(code);
            }
            @Override public CoordinateSystem createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return csFactory(factory).createCoordinateSystem(code);
            }
    };

    public static final AuthorityFactoryProxy<CartesianCS> CARTESIAN_CS =
        new AuthorityFactoryProxy<CartesianCS>(CartesianCS.class) {
            @Override public CartesianCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCartesianCS(code);
            }
            @Override public CartesianCS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return csFactory(factory).createCartesianCS(code);
            }
    };

    public static final AuthorityFactoryProxy<PolarCS> POLAR_CS =
        new AuthorityFactoryProxy<PolarCS>(PolarCS.class) {
            @Override public PolarCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createPolarCS(code);
            }
            @Override public PolarCS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return csFactory(factory).createPolarCS(code);
            }
    };

    public static final AuthorityFactoryProxy<CylindricalCS> CYLINDRICAL_CS =
        new AuthorityFactoryProxy<CylindricalCS>(CylindricalCS.class) {
            @Override public CylindricalCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCylindricalCS(code);
            }
            @Override public CylindricalCS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return csFactory(factory).createCylindricalCS(code);
            }
    };

    public static final AuthorityFactoryProxy<SphericalCS> SPHERICAL_CS =
        new AuthorityFactoryProxy<SphericalCS>(SphericalCS.class) {
            @Override public SphericalCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createSphericalCS(code);
            }
            @Override public SphericalCS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return csFactory(factory).createSphericalCS(code);
            }
    };

    public static final AuthorityFactoryProxy<EllipsoidalCS> ELLIPSOIDAL_CS =
        new AuthorityFactoryProxy<EllipsoidalCS>(EllipsoidalCS.class) {
            @Override public EllipsoidalCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createEllipsoidalCS(code);
            }
            @Override public EllipsoidalCS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return csFactory(factory).createEllipsoidalCS(code);
            }
    };

    public static final AuthorityFactoryProxy<VerticalCS> VERTICAL_CS =
        new AuthorityFactoryProxy<VerticalCS>(VerticalCS.class) {
            @Override public VerticalCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createVerticalCS(code);
            }
            @Override public VerticalCS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return csFactory(factory).createVerticalCS(code);
            }
    };

    public static final AuthorityFactoryProxy<TimeCS> TIME_CS =
        new AuthorityFactoryProxy<TimeCS>(TimeCS.class) {
            @Override public TimeCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createTimeCS(code);
            }
            @Override public TimeCS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return csFactory(factory).createTimeCS(code);
            }
    };

    public static final AuthorityFactoryProxy<CoordinateSystemAxis> AXIS =
        new AuthorityFactoryProxy<CoordinateSystemAxis>(CoordinateSystemAxis.class) {
            @Override public CoordinateSystemAxis create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCoordinateSystemAxis(code);
            }
            @Override public CoordinateSystemAxis createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return csFactory(factory).createCoordinateSystemAxis(code);
            }
    };

    @SuppressWarnings("rawtypes")
    public static final AuthorityFactoryProxy<Unit> UNIT =
        new AuthorityFactoryProxy<Unit>(Unit.class) {
            @Override public Unit<?> create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createUnit(code);
            }
            @Override public Unit createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return csFactory(factory).createUnit(code);
            }
    };

    public static final AuthorityFactoryProxy<CoordinateReferenceSystem> CRS =
        new AuthorityFactoryProxy<CoordinateReferenceSystem>(CoordinateReferenceSystem.class) {
            @Override public CoordinateReferenceSystem create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCoordinateReferenceSystem(code);
            }
            @Override public CoordinateReferenceSystem createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return crsFactory(factory).createCoordinateReferenceSystem(code);
            }
    };

    public static final AuthorityFactoryProxy<CompoundCRS> COMPOUND_CRS =
        new AuthorityFactoryProxy<CompoundCRS>(CompoundCRS.class) {
            @Override public CompoundCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCompoundCRS(code);
            }
            @Override public CompoundCRS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return crsFactory(factory).createCompoundCRS(code);
            }
    };

    public static final AuthorityFactoryProxy<DerivedCRS> DERIVED_CRS =
        new AuthorityFactoryProxy<DerivedCRS>(DerivedCRS.class) {
            @Override public DerivedCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createDerivedCRS(code);
            }
            @Override public DerivedCRS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return crsFactory(factory).createDerivedCRS(code);
            }
    };

    public static final AuthorityFactoryProxy<EngineeringCRS> ENGINEERING_CRS =
        new AuthorityFactoryProxy<EngineeringCRS>(EngineeringCRS.class) {
            @Override public EngineeringCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createEngineeringCRS(code);
            }
            @Override public EngineeringCRS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return crsFactory(factory).createEngineeringCRS(code);
            }
    };

    public static final AuthorityFactoryProxy<GeographicCRS> GEOGRAPHIC_CRS =
        new AuthorityFactoryProxy<GeographicCRS>(GeographicCRS.class) {
            @Override public GeographicCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createGeographicCRS(code);
            }
            @Override public GeographicCRS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return crsFactory(factory).createGeographicCRS(code);
            }
    };

    public static final AuthorityFactoryProxy<GeocentricCRS> GEOCENTRIC_CRS =
        new AuthorityFactoryProxy<GeocentricCRS>(GeocentricCRS.class) {
            @Override public GeocentricCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createGeocentricCRS(code);
            }
            @Override public GeocentricCRS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return crsFactory(factory).createGeocentricCRS(code);
            }
    };

    public static final AuthorityFactoryProxy<ImageCRS> IMAGE_CRS =
        new AuthorityFactoryProxy<ImageCRS>(ImageCRS.class) {
            @Override public ImageCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createImageCRS(code);
            }
            @Override public ImageCRS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return crsFactory(factory).createImageCRS(code);
            }
    };

    public static final AuthorityFactoryProxy<ProjectedCRS> PROJECTED_CRS =
        new AuthorityFactoryProxy<ProjectedCRS>(ProjectedCRS.class) {
            @Override public ProjectedCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createProjectedCRS(code);
            }
            @Override public ProjectedCRS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return crsFactory(factory).createProjectedCRS(code);
            }
    };

    public static final AuthorityFactoryProxy<TemporalCRS> TEMPORAL_CRS =
        new AuthorityFactoryProxy<TemporalCRS>(TemporalCRS.class) {
            @Override public TemporalCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createTemporalCRS(code);
            }
            @Override public TemporalCRS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return crsFactory(factory).createTemporalCRS(code);
            }
    };

    public static final AuthorityFactoryProxy<VerticalCRS> VERTICAL_CRS =
        new AuthorityFactoryProxy<VerticalCRS>(VerticalCRS.class) {
            @Override public VerticalCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createVerticalCRS(code);
            }
            @Override public VerticalCRS createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return crsFactory(factory).createVerticalCRS(code);
            }
    };

    @SuppressWarnings("rawtypes")
    public static final AuthorityFactoryProxy<ParameterDescriptor> PARAMETER =
        new AuthorityFactoryProxy<ParameterDescriptor>(ParameterDescriptor.class) {
            @Override public ParameterDescriptor<?> create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createParameterDescriptor(code);
            }
            @Override public ParameterDescriptor createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return geotkFactory(factory).createParameterDescriptor(code);
            }
    };

    public static final AuthorityFactoryProxy<OperationMethod> METHOD =
        new AuthorityFactoryProxy<OperationMethod>(OperationMethod.class) {
            @Override public OperationMethod create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createOperationMethod(code);
            }
            @Override public OperationMethod createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return opFactory(factory).createOperationMethod(code);
            }
    };

    public static final AuthorityFactoryProxy<CoordinateOperation> OPERATION =
        new AuthorityFactoryProxy<CoordinateOperation>(CoordinateOperation.class) {
            @Override public CoordinateOperation create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCoordinateOperation(code);
            }
            @Override public CoordinateOperation createFromAPI(AuthorityFactory factory, String code) throws FactoryException {
                return opFactory(factory).createCoordinateOperation(code);
            }
    };

    /**
     * Returns the instance for the given type. The {@code type} argument can be a GeoAPI interface
     * or some implementation class like {@link org.geotoolkit.referencing.crs.DefaultProjectedCRS}.
     * This method returns the most specific proxy for the given type.
     *
     * @param  type The GeoAPI or implementation class.
     * @return The most specific proxy for the given {@code type}.
     * @throws IllegalArgumentException if the type doesn't implement a valid interface.
     */
    @SuppressWarnings("unchecked")
    public static <T> AuthorityFactoryProxy<? super T> getInstance(final Class<T> type)
            throws IllegalArgumentException
    {
        for (final AuthorityFactoryProxy<?> proxy : PROXIES) {
            if (proxy.type.isAssignableFrom(type)) {
                return (AuthorityFactoryProxy<? super T>) proxy;
            }
        }
        throw new IllegalArgumentException(Errors.format(
                Errors.Keys.ILLEGAL_CLASS_2, type, IdentifiedObject.class));
    }

    /**
     * The types of proxies. The most specific types must appear first in this list.
     * Must be declared after all the above constants.
     */
    static final AuthorityFactoryProxy<?>[] PROXIES = new AuthorityFactoryProxy<?>[] {
        OPERATION,
        METHOD,
        PARAMETER,
        PROJECTED_CRS,
        GEOGRAPHIC_CRS,
        GEOCENTRIC_CRS,
        IMAGE_CRS,
        DERIVED_CRS,
        VERTICAL_CRS,
        TEMPORAL_CRS,
        ENGINEERING_CRS,
        COMPOUND_CRS,
        CRS,
        AXIS,
        CARTESIAN_CS,
        ELLIPSOIDAL_CS,
        SPHERICAL_CS,
        CYLINDRICAL_CS,
        POLAR_CS,
        VERTICAL_CS,
        TIME_CS,
        COORDINATE_SYSTEM,
        PRIME_MERIDIAN,
        ELLIPSOID,
        GEODETIC_DATUM,
        IMAGE_DATUM,
        VERTICAL_DATUM,
        TEMPORAL_DATUM,
        ENGINEERING_DATUM,
        DATUM,
        EXTENT,
        UNIT,
        OBJECT
    };
}
