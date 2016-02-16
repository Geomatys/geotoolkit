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

import java.util.Locale;
import java.util.Iterator;
import java.util.Collection;
import java.io.Writer;
import java.io.IOException;
import java.awt.RenderingHints; // For javadoc
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geotoolkit.lang.Configuration;
import org.geotoolkit.lang.Static;
import org.geotoolkit.lang.Debug;
import org.geotoolkit.internal.Listeners;


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
     * Adds an alternative way to search for factory implementations. {@link FactoryRegistry} has
     * a default mechanism bundled in it, which uses the content of all {@code META-INF/services}
     * directories found on the classpath. This {@code addFactoryIteratorProvider} method allows
     * to specify additional discovery algorithms. It may be useful in the context of some
     * frameworks that use the <cite>constructor injection</cite> pattern, like the
     * <a href="http://www.springframework.org/">Spring framework</a>.
     * <p>
     * If the given provider was not already registered, then this method notifies
     * every listeners registered with {@link #addChangeListener(ChangeListener)}.
     *
     * @param provider A new provider for factory iterators.
     *
     * @level advanced
     */
    @Configuration
    public static void addFactoryIteratorProvider(final FactoryIteratorProvider provider) {
        if (FactoryIteratorProviders.GLOBAL.addFactoryIteratorProvider(provider)) {
            fireConfigurationChanged(Factories.class);
        }
    }

    /**
     * Removes a provider that was previously {@linkplain #addFactoryIteratorProvider added}.
     * Note that factories already obtained from the specified provider will not be
     * {@linkplain FactoryRegistry#deregisterServiceProvider deregistered} by this method.
     * <p>
     * If the given provider was found, then this method notifies every listeners
     * registered with {@link #addChangeListener(ChangeListener)}.
     *
     * @param provider The provider to remove.
     *
     * @level advanced
     */
    @Configuration
    public static void removeFactoryIteratorProvider(final FactoryIteratorProvider provider) {
        if (FactoryIteratorProviders.GLOBAL.removeFactoryIteratorProvider(provider)) {
            fireConfigurationChanged(Factories.class);
        }
    }

    /**
     * Lists all available factory implementations in a tabular format. For each factory interface,
     * the first implementation listed is the default one. This method provides a way to check the
     * state of a system, usually for debugging purpose.
     *
     * @param  registries Where the factories are registered.
     * @param  out The output stream where to format the list.
     * @param  locale The locale for the list, or {@code null}.
     * @throws IOException if an error occurs while writing to {@code out}.
     *
     * @see FactoryFinder#listProviders
     *
     * @since 3.00
     */
    @Debug
    public static void listProviders(final Collection<FactoryRegistry> registries,
            final Writer out, final Locale locale) throws IOException
    {
        new FactoryPrinter(registries).list(out, locale);
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
}
