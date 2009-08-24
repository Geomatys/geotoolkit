/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.factory;

import javax.measure.unit.Unit;

import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.FactoryException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.metadata.extent.Extent;
import org.geotoolkit.resources.Errors;


/**
 * Delegates object creations to one of the {@code create} methods in a backing
 * {@link AbstractAuthorityFactory}.
 * <p>
 * This class is similar to {@link AuthorityFactoryProxy} except that it requires the factory
 * to be specifically an instance of {@link AbstractAuthorityFactory}. This restriction makes
 * the implementation easier because we don't need to deal with the different kind of factories
 * ({@link CRSAuthorityFactory}, {@link CSAuthorityFactory}, {@link DatumAuthorityFactory},
 * <cite>etc.</cite>), which allow us to declare static final constants and pass the factory
 * in parameter. By contrast, {@link AuthorityFactoryProxy} needs to keep the factory as a
 * field because the type may vary.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
abstract class AbstractAuthorityFactoryProxy<T> {
    /**
     * The type of objects to be created.
     */
    public final Class<T> type;

    /**
     * Creates a new proxy for objects of the given type.
     */
    private AbstractAuthorityFactoryProxy(final Class<T> type) {
        this.type = type;
    }

    /**
     * Creates the object for the given code.
     *
     * @param  factory The factory to use for creating the object.
     * @param  code    The code for which to create an object.
     * @return The object created from the given code.
     * @throws FactoryException If an error occured while creating the object.
     */
    public abstract T create(AbstractAuthorityFactory factory, String code) throws FactoryException;

    /**
     * The proxy for the {@link AbstractAuthorityFactory#createObject} method.
     */
    public static final AbstractAuthorityFactoryProxy<IdentifiedObject> OBJECT =
        new AbstractAuthorityFactoryProxy<IdentifiedObject>(IdentifiedObject.class) {
            @Override public IdentifiedObject create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createObject(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<Datum> DATUM =
        new AbstractAuthorityFactoryProxy<Datum>(Datum.class) {
            @Override public Datum create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createDatum(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<EngineeringDatum> ENGINEERING_DATUM =
        new AbstractAuthorityFactoryProxy<EngineeringDatum>(EngineeringDatum.class) {
            @Override public EngineeringDatum create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createEngineeringDatum(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<ImageDatum> IMAGE_DATUM =
        new AbstractAuthorityFactoryProxy<ImageDatum>(ImageDatum.class) {
            @Override public ImageDatum create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createImageDatum(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<VerticalDatum> VERTICAL_DATUM =
        new AbstractAuthorityFactoryProxy<VerticalDatum>(VerticalDatum.class) {
            @Override public VerticalDatum create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createVerticalDatum(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<TemporalDatum> TEMPORAL_DATUM =
        new AbstractAuthorityFactoryProxy<TemporalDatum>(TemporalDatum.class) {
            @Override public TemporalDatum create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createTemporalDatum(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<GeodeticDatum> GEODETIC_DATUM =
        new AbstractAuthorityFactoryProxy<GeodeticDatum>(GeodeticDatum.class) {
            @Override public GeodeticDatum create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createGeodeticDatum(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<Ellipsoid> ELLIPSOID =
        new AbstractAuthorityFactoryProxy<Ellipsoid>(Ellipsoid.class) {
            @Override public Ellipsoid create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createEllipsoid(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<PrimeMeridian> PRIME_MERIDIAN =
        new AbstractAuthorityFactoryProxy<PrimeMeridian>(PrimeMeridian.class) {
            @Override public PrimeMeridian create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createPrimeMeridian(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<Extent> EXTENT =
        new AbstractAuthorityFactoryProxy<Extent>(Extent.class) {
            @Override public Extent create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createExtent(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<CoordinateSystem> COORDINATE_SYSTEM =
        new AbstractAuthorityFactoryProxy<CoordinateSystem>(CoordinateSystem.class) {
            @Override public CoordinateSystem create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCoordinateSystem(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<CartesianCS> CARTESIAN_CS =
        new AbstractAuthorityFactoryProxy<CartesianCS>(CartesianCS.class) {
            @Override public CartesianCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCartesianCS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<PolarCS> POLAR_CS =
        new AbstractAuthorityFactoryProxy<PolarCS>(PolarCS.class) {
            @Override public PolarCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createPolarCS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<CylindricalCS> CYLINDRICAL_CS =
        new AbstractAuthorityFactoryProxy<CylindricalCS>(CylindricalCS.class) {
            @Override public CylindricalCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCylindricalCS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<SphericalCS> SPHERICAL_CS =
        new AbstractAuthorityFactoryProxy<SphericalCS>(SphericalCS.class) {
            @Override public SphericalCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createSphericalCS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<EllipsoidalCS> ELLIPSOIDAL_CS =
        new AbstractAuthorityFactoryProxy<EllipsoidalCS>(EllipsoidalCS.class) {
            @Override public EllipsoidalCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createEllipsoidalCS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<VerticalCS> VERTICAL_CS =
        new AbstractAuthorityFactoryProxy<VerticalCS>(VerticalCS.class) {
            @Override public VerticalCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createVerticalCS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<TimeCS> TIME_CS =
        new AbstractAuthorityFactoryProxy<TimeCS>(TimeCS.class) {
            @Override public TimeCS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createTimeCS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<CoordinateSystemAxis> AXIS =
        new AbstractAuthorityFactoryProxy<CoordinateSystemAxis>(CoordinateSystemAxis.class) {
            @Override public CoordinateSystemAxis create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCoordinateSystemAxis(code);
            }
    };

    @SuppressWarnings("rawtypes")
    public static final AbstractAuthorityFactoryProxy<Unit> UNIT =
        new AbstractAuthorityFactoryProxy<Unit>(Unit.class) {
            @Override public Unit<?> create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createUnit(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<CoordinateReferenceSystem> CRS =
        new AbstractAuthorityFactoryProxy<CoordinateReferenceSystem>(CoordinateReferenceSystem.class) {
            @Override public CoordinateReferenceSystem create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCoordinateReferenceSystem(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<CompoundCRS> COMPOUND_CRS =
        new AbstractAuthorityFactoryProxy<CompoundCRS>(CompoundCRS.class) {
            @Override public CompoundCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCompoundCRS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<DerivedCRS> DERIVED_CRS =
        new AbstractAuthorityFactoryProxy<DerivedCRS>(DerivedCRS.class) {
            @Override public DerivedCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createDerivedCRS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<EngineeringCRS> ENGINEERING_CRS =
        new AbstractAuthorityFactoryProxy<EngineeringCRS>(EngineeringCRS.class) {
            @Override public EngineeringCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createEngineeringCRS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<GeographicCRS> GEOGRAPHIC_CRS =
        new AbstractAuthorityFactoryProxy<GeographicCRS>(GeographicCRS.class) {
            @Override public GeographicCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createGeographicCRS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<GeocentricCRS> GEOCENTRIC_CRS =
        new AbstractAuthorityFactoryProxy<GeocentricCRS>(GeocentricCRS.class) {
            @Override public GeocentricCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createGeocentricCRS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<ImageCRS> IMAGE_CRS =
        new AbstractAuthorityFactoryProxy<ImageCRS>(ImageCRS.class) {
            @Override public ImageCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createImageCRS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<ProjectedCRS> PROJECTED_CRS =
        new AbstractAuthorityFactoryProxy<ProjectedCRS>(ProjectedCRS.class) {
            @Override public ProjectedCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createProjectedCRS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<TemporalCRS> TEMPORAL_CRS =
        new AbstractAuthorityFactoryProxy<TemporalCRS>(TemporalCRS.class) {
            @Override public TemporalCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createTemporalCRS(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<VerticalCRS> VERTICAL_CRS =
        new AbstractAuthorityFactoryProxy<VerticalCRS>(VerticalCRS.class) {
            @Override public VerticalCRS create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createVerticalCRS(code);
            }
    };

    @SuppressWarnings("rawtypes")
    public static final AbstractAuthorityFactoryProxy<ParameterDescriptor> PARAMETER =
        new AbstractAuthorityFactoryProxy<ParameterDescriptor>(ParameterDescriptor.class) {
            @Override public ParameterDescriptor<?> create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createParameterDescriptor(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<OperationMethod> METHOD =
        new AbstractAuthorityFactoryProxy<OperationMethod>(OperationMethod.class) {
            @Override public OperationMethod create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createOperationMethod(code);
            }
    };

    public static final AbstractAuthorityFactoryProxy<CoordinateOperation> OPERATION =
        new AbstractAuthorityFactoryProxy<CoordinateOperation>(CoordinateOperation.class) {
            @Override public CoordinateOperation create(AbstractAuthorityFactory factory, String code) throws FactoryException {
                return factory.createCoordinateOperation(code);
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
    public static <T> AbstractAuthorityFactoryProxy<? super T> getInstance(final Class<T> type)
            throws IllegalArgumentException
    {
        for (final AbstractAuthorityFactoryProxy<?> proxy : PROXIES) {
            if (proxy.type.isAssignableFrom(type)) {
                return (AbstractAuthorityFactoryProxy<? super T>) proxy;
            }
        }
        throw new IllegalArgumentException(Errors.format(
                Errors.Keys.ILLEGAL_CLASS_$2, type, IdentifiedObject.class));
    }

    /**
     * The types of proxies. The most specific types must appear first in this list.
     * Must be declared after all the above constants.
     */
    static final AbstractAuthorityFactoryProxy<?>[] PROXIES = new AbstractAuthorityFactoryProxy<?>[] {
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
