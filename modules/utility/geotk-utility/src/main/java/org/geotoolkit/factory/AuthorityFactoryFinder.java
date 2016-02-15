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
 */
package org.geotoolkit.factory;

import java.util.Set;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;
import org.opengis.util.FactoryException;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.factory.MultiAuthoritiesFactory;



/**
 * Defines static methods used to access the application's {@linkplain AuthorityFactory authority
 * factory} implementations. This class provide access to the following services:
 * <p>
 * <ul>
 *   <li><b>Referencing</b></li><ul>
 *     <li>{@link CoordinateOperationAuthorityFactory}</li>
 *     <li>{@link CRSAuthorityFactory}</li>
 *     <li>{@link CSAuthorityFactory}</li>
 *     <li>{@link DatumAuthorityFactory}</li>
 *   </ul>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @module
 *
 * @deprecated Will be replaced by a more standard dependency injection mechanism.
 */
@Deprecated
public final class AuthorityFactoryFinder extends FactoryFinder {
    /**
     * Do not allow instantiation of this class.
     */
    private AuthorityFactoryFinder() {
    }

    /**
     * Returns the names of all currently registered authorities.
     *
     * @return The set of all currently registered authorities.
     */
    public static Set<String> getAuthorityNames() {
        try {
            return ((MultiAuthoritiesFactory) CRS.getAuthorityFactory(null)).getCodeSpaces();
        } catch (FactoryException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns the first implementation of a factory matching the specified hints. If no
     * implementation matches, a new one is created if possible or an exception is thrown
     * otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     *
     * @param  category  The authority factory type.
     * @param  authority The desired authority (e.g. "EPSG").
     * @param  hints     An optional map of hints, or {@code null} for the default ones.
     * @param  key       The hint key to use for searching an implementation.
     * @return The first authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         specified interface.
     */
    private static <T extends AuthorityFactory> T getAuthorityFactory(
            final Class<T> category, final String authority, Hints hints, final Hints.ClassKey key)
            throws FactoryRegistryException
    {
        hints = mergeSystemHints(hints);
        final Object factory = hints.get(key);
        if (category.isInstance(factory)) {
            return (T) factory;
        }
        try {
            return ((MultiAuthoritiesFactory) CRS.getAuthorityFactory(null)).getAuthorityFactory(category, authority, null);
        } catch (FactoryException e) {
            throw new FactoryRegistryException(e.getMessage(), e);
        }
    }

    /**
     * Returns the first implementation of {@link DatumAuthorityFactory} matching the specified
     * hints. If no implementation matches, a new one is created if possible or an exception is
     * thrown otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     *
     * @param  authority The desired authority (e.g. "EPSG").
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first datum authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link DatumAuthorityFactory} interface.
     *
     * @category Referencing
     */
    public static DatumAuthorityFactory getDatumAuthorityFactory(final String authority, final Hints hints)
            throws FactoryRegistryException
    {
        return getAuthorityFactory(DatumAuthorityFactory.class, authority, hints, Hints.DATUM_AUTHORITY_FACTORY);
    }

    /**
     * Returns the first implementation of {@link CSAuthorityFactory} matching the specified
     * hints. If no implementation matches, a new one is created if possible or an exception is
     * thrown otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     * <p>
     * Hints that may be understood includes
     * {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER FORCE_LONGITUDE_FIRST_AXIS_ORDER},
     * {@link Hints#FORCE_STANDARD_AXIS_UNITS        FORCE_STANDARD_AXIS_UNITS} and
     * {@link Hints#FORCE_STANDARD_AXIS_DIRECTIONS   FORCE_STANDARD_AXIS_DIRECTIONS} and
     * {@link Hints#VERSION                          VERSION}.
     *
     * @param  authority The desired authority (e.g. "EPSG").
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first coordinate system authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CSAuthorityFactory} interface.
     *
     * @category Referencing
     */
    public static CSAuthorityFactory getCSAuthorityFactory(final String authority, final Hints hints)
            throws FactoryRegistryException
    {
        return getAuthorityFactory(CSAuthorityFactory.class, authority, hints, Hints.CS_AUTHORITY_FACTORY);
    }

    /**
     * Returns the first implementation of {@link CRSAuthorityFactory} matching the specified
     * hints. If no implementation matches, a new one is created if possible or an exception is
     * thrown otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     * <p>
     * Hints that may be understood includes
     * {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER FORCE_LONGITUDE_FIRST_AXIS_ORDER},
     * {@link Hints#FORCE_STANDARD_AXIS_UNITS        FORCE_STANDARD_AXIS_UNITS},
     * {@link Hints#FORCE_STANDARD_AXIS_DIRECTIONS   FORCE_STANDARD_AXIS_DIRECTIONS} and
     * {@link Hints#VERSION                          VERSION}.
     * <p>
     * <b>TIP:</b> The EPSG official factory and the EPSG extensions (additional CRS provided by
     * ESRI and others) are two distinct factories. Call to {@code getCRSAuthorityFactory("EPSG",
     * null)} returns only one of those, usually the official EPSG factory. If the union of those
     * two factories is wanted, then a chain of fallbacks is wanted. Consider using something like:
     *
     * {@preformat java
     *     FallbackAuthorityFactory.create(CRSAuthorityFactory.class, getCRSAuthorityFactories(hints));
     * }
     *
     * @param  authority The desired authority (e.g. "EPSG").
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first coordinate reference system authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CRSAuthorityFactory} interface.
     *
     * @see org.geotoolkit.referencing.factory.FallbackAuthorityFactory#create(Class, java.util.Collection)
     * @category Referencing
     */
    public static CRSAuthorityFactory getCRSAuthorityFactory(final String authority, final Hints hints)
            throws FactoryRegistryException
    {
        return getAuthorityFactory(CRSAuthorityFactory.class, authority, hints, Hints.CRS_AUTHORITY_FACTORY);
    }

    /**
     * Returns the first implementation of {@link CoordinateOperationAuthorityFactory} matching
     * the specified hints. If no implementation matches, a new one is created if possible or an
     * exception is thrown otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     *
     * @param  authority The desired authority (e.g. "EPSG").
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @return The first coordinate operation authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CoordinateOperationAuthorityFactory} interface.
     *
     * @category Referencing
     */
    public static CoordinateOperationAuthorityFactory getCoordinateOperationAuthorityFactory(
            final String authority, final Hints hints) throws FactoryRegistryException
    {
        return getAuthorityFactory(CoordinateOperationAuthorityFactory.class, authority, hints,
                Hints.COORDINATE_OPERATION_AUTHORITY_FACTORY);
    }
}
