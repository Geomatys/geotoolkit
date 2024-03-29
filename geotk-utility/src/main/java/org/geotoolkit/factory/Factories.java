/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Iterator;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.factory.MultiAuthoritiesFactory;

import org.geotoolkit.lang.Static;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;
import org.opengis.util.FactoryException;


/**
 * Static methods relative to the factories.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.20
 *
 * @since 2.4
 * @module
 *
 * @deprecated Will be replaced by a more standard dependency injection mechanism.
 */
@Deprecated
public final class Factories extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private Factories() {
    }

    /**
     * Returns an iterator giving precedence to classes loaded by the given class loader or one
     * of its parents/children. This method is used as a safety when there is a risk that many
     * copies of the same library (for example in a web container) register the same JDK service.
     *
     * {@section Example with Image I/O}
     * The {@code geotk-coverageio} module defines new {@link javax.imageio.ImageReader}
     * implementations, which are automatically discovered by the standard JDK through the
     * {@code META-INF/services/} mechanism. If a web container contains two copies of the
     * Geotoolkit.org library - one for each web application - then all image formats will be registered twice,
     * because the JVM will find two {@code Spi} classes loaded by two
     * different class loaders.
     * <p>
     * The service provider instance returned by {@link javax.imageio.spi.IIORegistry} may
     * be somewhat random in the above scenario. This leads to subtle and hard-to-identify
     * bugs. This method reduces the risk by giving precedence to SPI classes loaded by the
     * same class loader than the application. However users are still encouraged to load,
     * for each running JVM, only one copy of the Geotoolkit.org library to be shared by all
     * applications.
     *
     * @param  <T> The type of elements in the iterator.
     * @param  classLoader The desired class loader, or {@code null} for the bootstrap class loader.
     * @param  iterator The iterator to wrap.
     * @return An iterator giving precedences to classes loaded by the given class loader or
     *         one of its parents/children.
     *
     * @since 3.20
     */
    public static <T> Iterator<T> orderForClassLoader(final ClassLoader classLoader, final Iterator<T> iterator) {
        if (classLoader == null || iterator == null || (iterator instanceof OrderedIterator<?> &&
                ((OrderedIterator<?>) iterator).classLoader == classLoader))
        {
            return iterator;
        }
        return new OrderedIterator<>(classLoader, iterator);
    }

    /**
     * Returns the first implementation of {@link CRSAuthorityFactory} matching the specified
     * hints. If no implementation matches, a new one is created if possible or an exception is
     * thrown otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
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
     * @return The first coordinate reference system authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CRSAuthorityFactory} interface.
     *
     * @category Referencing
     */
    public static CRSAuthorityFactory getCRSAuthorityFactory(final String authority)
            throws FactoryRegistryException
    {
        return getAuthorityFactory(CRSAuthorityFactory.class, authority);
    }

    /**
     * Returns the first implementation of {@link CoordinateOperationAuthorityFactory} matching
     * the specified hints. If no implementation matches, a new one is created if possible or an
     * exception is thrown otherwise. If more than one implementation is registered and an
     * {@linkplain #setVendorOrdering ordering is set}, then the preferred
     * implementation is returned. Otherwise an arbitrary one is selected.
     *
     * @param  authority The desired authority (e.g. "EPSG").
     * @return The first coordinate operation authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CoordinateOperationAuthorityFactory} interface.
     *
     * @category Referencing
     */
    public static CoordinateOperationAuthorityFactory getCoordinateOperationAuthorityFactory(
            final String authority) throws FactoryRegistryException
    {
        return getAuthorityFactory(CoordinateOperationAuthorityFactory.class, authority);
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
     * @return The first authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         specified interface.
     */
    private static <T extends AuthorityFactory> T getAuthorityFactory(
            final Class<T> category, final String authority)
            throws FactoryRegistryException
    {
        try {
            return ((MultiAuthoritiesFactory) CRS.getAuthorityFactory(null)).getAuthorityFactory(category, authority, null);
        } catch (FactoryException e) {
            throw new FactoryRegistryException(e.getMessage(), e);
        }
    }
}
