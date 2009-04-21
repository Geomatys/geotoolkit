/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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
import java.util.Collection;
import java.io.Writer;
import java.io.IOException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.lang.Static;


/**
 * Static methods relative to the factories. There are many aspects in the way Geotoolkit manages
 * factories on a system-wide basis:
 *
 * <ul>
 *   <li><p><b>Default settings:</b> They are handled as the default hint values set on a system-wide
 *   basis by {@link Hints#getSystemDefault Hints.get}/{@link Hints#putSystemDefault put}/{@link
 *   Hints#removeSystemDefault removeSystemDefault} methods. The default values can be provided
 *   in application code.</p></li>
 *
 *   <li><p><b>Integration plugins:</b> If hosting Geotoolkit in a alternate plugin system such as
 *   Spring or OSGi, application may needs to register additional "Factory Iterators" for Geotoolkit
 *   to search using the {@link #addFactoryIteratorProvider addFactoryIteratorProvider} method.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @author Jody Garnett (Refractions)
 * @version 3.0
 *
 * @since 2.4
 * @module
 */
@Static
public final class Factories {
    /**
     * Object to inform about hints changes.
     * We use the Swing utility listener list since it is lightweight and thread-safe.
     * Note that it doesn't involve any dependency to the remaining of Swing library.
     */
    private static final EventListenerList LISTENERS = new EventListenerList();

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
     *   <li>{@link Hints#putSystemDefault}</li>
     *   <li>{@link Hints#removeSystemDefault}</li>
     *   <li>{@link #addFactoryIteratorProvider}</li>
     *   <li>{@link #removeFactoryIteratorProvider}</li>
     * </ul>
     *
     * @param listener The listener to add.
     */
    public static void addChangeListener(final ChangeListener listener) {
        synchronized (LISTENERS) {
            removeChangeListener(listener); // Ensure singleton.
            LISTENERS.add(ChangeListener.class, listener);
        }
    }

    /**
     * Removes the specified listener from the list of objects to inform when a system-wide
     * configuration changed.
     *
     * @param listener The listener to remove.
     */
    public static void removeChangeListener(final ChangeListener listener) {
        synchronized (LISTENERS) {
            LISTENERS.remove(ChangeListener.class, listener);
        }
    }

    /**
     * Informs every listeners that a system-wide configuration changed.
     * This method is invoked by the static methods that are annotated
     * with {@link Configuration}. Users should not need to invoke this
     * method themself.
     *
     * @param source The source of this event.
     */
    static void fireConfigurationChanged(final Class<?> source) {
        final ChangeEvent event = new ChangeEvent(source);
        final Object[] listeners = LISTENERS.getListenerList();
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i] == ChangeListener.class) {
                ((ChangeListener) listeners[i+1]).stateChanged(event);
            }
        }
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
     * every listeners registered with {@link #addChangeListener}.
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
     * If the given provider was found, then this method notifies
     * every listeners registered with {@link #addChangeListener}.
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
     * @throws IOException if an error occurs while writting to {@code out}.
     *
     * @see FactoryFinder#listProviders
     *
     * @since 3.0
     */
    public static void listProviders(final Collection<FactoryRegistry> registries,
            final Writer out, final Locale locale) throws IOException
    {
        new FactoryPrinter(registries).list(out, locale);
    }
}
