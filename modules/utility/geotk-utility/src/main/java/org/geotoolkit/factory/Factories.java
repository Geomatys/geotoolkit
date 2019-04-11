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
import java.awt.RenderingHints; // For javadoc
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.factory.MultiAuthoritiesFactory;

import org.geotoolkit.lang.Configuration;
import org.geotoolkit.lang.Static;
import org.geotoolkit.internal.Listeners;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;
import org.opengis.util.FactoryException;


/**
 * Static methods relative to the factories. There are many aspects in the way Geotk manages
 * factories on a system-wide basis:
 *
 * <ul>
 *   <li><p><b>Default settings:</b> They are handled as the default hint values set on a system-wide
 *   basis by {@link Hints#getSystemDefault Hints.get}/{@link Hints#putSystemDefault put}/{@link
 *   Hints#removeSystemDefault removeSystemDefault} methods. The default values can be provided
 *   in application code.</p></li>
 *
 *   <li><p><b>Integration plugins:</b> If hosting Geotk in a alternate plugin system such as
 *   Spring or OSGi, application may needs to register additional "Factory Iterators" for Geotk
 *   to search using the {@link #addFactoryIteratorProvider addFactoryIteratorProvider} method.</p></li>
 * </ul>
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
     * Object to inform about hints changes.
     * We use the Swing utility listener list since it is lightweight and thread-safe.
     * Note that it doesn't involve any dependency to the remaining of Swing library.
     * (Note: this assumption may change with JDK8 modularization)
     */
    private static ChangeListener[] listeners;

    /**
     * Do not allow instantiation of this class.
     */
    private Factories() {
    }

    /**
     * Adds the specified listener to the list of objects to inform when a
     * system-wide configuration changed. The methods which may fire a
     * {@linkplain ChangeEvent change event} are:
     * <p>
     * <ul>
     *   <li>{@link Hints#putSystemDefault(RenderingHints.Key, Object)}</li>
     *   <li>{@link Hints#removeSystemDefault(RenderingHints.Key)}</li>
     *   <li>{@link #addFactoryIteratorProvider(FactoryIteratorProvider)}</li>
     *   <li>{@link #removeFactoryIteratorProvider(FactoryIteratorProvider)}</li>
     * </ul>
     *
     * @param listener The listener to add.
     */
    public static synchronized void addChangeListener(final ChangeListener listener) {
        listeners = Listeners.addListener(listener, listeners);
    }

    /**
     * Removes the specified listener from the list of objects to inform when a system-wide
     * configuration changed.
     *
     * @param listener The listener to remove.
     */
    public static synchronized void removeChangeListener(final ChangeListener listener) {
        listeners = Listeners.removeListener(listener, listeners);
    }

    /**
     * Informs every listeners that a system-wide configuration changed.
     * This method is invoked by the static methods that are annotated
     * with {@link Configuration}. Users should not need to invoke this
     * method themselves.
     *
     * @param source The source of this event.
     */
    static void fireConfigurationChanged(final Class<?> source) {
        final ChangeListener[] list;
        synchronized (Factories.class) {
            list = listeners;
        }
        Listeners.fireChanged(source, list);
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
     * Geotoolkit.org library - one for each web application - then all image formats like
     * {@link org.geotoolkit.image.io.plugin.NetcdfImageReader} will be registered twice,
     * because the JVM will find two {@code NetcdfImageReader.Spi} classes loaded by two
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
     * @return The first coordinate reference system authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@link CRSAuthorityFactory} interface.
     *
     * @see org.geotoolkit.referencing.factory.FallbackAuthorityFactory#create(Class, java.util.Collection)
     * @category Referencing
     */
    public static CRSAuthorityFactory getCRSAuthorityFactory(final String authority)
            throws FactoryRegistryException
    {
        return getAuthorityFactory(CRSAuthorityFactory.class, authority, Hints.CRS_AUTHORITY_FACTORY);
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
        return getAuthorityFactory(CoordinateOperationAuthorityFactory.class, authority,
                Hints.COORDINATE_OPERATION_AUTHORITY_FACTORY);
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
     * @param  key       The hint key to use for searching an implementation.
     * @return The first authority factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         specified interface.
     */
    private static <T extends AuthorityFactory> T getAuthorityFactory(
            final Class<T> category, final String authority, final Hints.ClassKey key)
            throws FactoryRegistryException
    {
        try {
            return ((MultiAuthoritiesFactory) CRS.getAuthorityFactory(null)).getAuthorityFactory(category, authority, null);
        } catch (FactoryException e) {
            throw new FactoryRegistryException(e.getMessage(), e);
        }
    }
}
