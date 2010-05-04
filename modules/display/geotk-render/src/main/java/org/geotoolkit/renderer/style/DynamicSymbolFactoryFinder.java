/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.renderer.style;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import org.geotoolkit.factory.DynamicFactoryRegistry;
import org.geotoolkit.factory.FactoryRegistry;

/**
 * Searches for all available {@link ExternalGraphicFactory} and
 * {@link MarkFactory} implementations.
 * 
 * <p>
 * In addition to implementing this interface dynamic symbol handlers should
 * have a services file:
 * <ul>
 * <li><code>META-INF/services/org.geotoolkit.renderer.style.MarkFactory</code>
 * if the are {@link MarkFactory} instances</li>
 * <li><code>META-INF/services/org.geotoolkit.renderer.style.ExternalGraphicFactory</code>
 * if the are {@link ExternalGraphicFactory} instances</li>
 * </ul>
 * </p>
 * 
 * @module pending
 */
public final class DynamicSymbolFactoryFinder {
    /** The logger for the filter module. */
    protected static final Logger LOGGER = org.geotoolkit.util.logging.Logging
            .getLogger("org.geotoolkit.renderer.style");

    /**
     * The service registry for this manager. Will be initialized only when
     * first needed.
     */
    private static FactoryRegistry registry;

    private DynamicSymbolFactoryFinder() {
    }

    /**
     * @see org.geotoolkit.renderer.style.ExternalGraphicFactory#getImage(java.net.URI, java.lang.String, java.lang.Float, RenderingHints)
     */
    public static BufferedImage getImage(URI uri, String mime, Float size, RenderingHints hints) throws Exception{
        final Iterator<ExternalGraphicFactory> ite = getExternalGraphicFactories();
        while(ite.hasNext()){
            final ExternalGraphicFactory factory = ite.next();
            if(factory.getSupportedMimeTypes().contains(mime)){
                return factory.getImage(uri, mime, size, hints);
            }
        }
        return null;
    }

    /**
     * Finds all implementations of {@link MarkFactory} which have registered
     * using the services mechanism.
     * 
     * @return An iterator over all discovered datastores which have registered
     *         factories, and whose available method returns true.
     */
    public static synchronized Iterator<MarkFactory> getMarkFactories() {
        return getServiceRegistry().getServiceProviders(MarkFactory.class, null, null, null);
    }

    /**
     * Finds all implementations of {@link ExternalGraphicFactory} which have
     * registered using the services mechanism.
     * 
     * @return An iterator over all discovered datastores which have registered
     *         factories, and whose available method returns true.
     */
    public static synchronized Iterator<ExternalGraphicFactory> getExternalGraphicFactories() {
        return getServiceRegistry().getServiceProviders(ExternalGraphicFactory.class, null, null, null);
    }

    /**
     * Returns the service registry. The registry will be created the first time
     * this method is invoked.
     */
    private static FactoryRegistry getServiceRegistry() {
        assert Thread.holdsLock(DynamicSymbolFactoryFinder.class);
        if (registry == null) {
            registry = new DynamicFactoryRegistry(Arrays.asList(new Class<?>[] { MarkFactory.class,
                    ExternalGraphicFactory.class }));
        }
        return registry;
    }

    /**
     * Scans for factory plug-ins on the application class path. This method is
     * needed because the application class path can theoretically change, or
     * additional plug-ins may become available. Rather than re-scanning the
     * classpath on every invocation of the API, the class path is scanned
     * automatically only on the first invocation. Clients can call this method
     * to prompt a re-scan. Thus this method need only be invoked by
     * sophisticated applications which dynamically make new plug-ins available
     * at runtime.
     */
    public static synchronized void scanForPlugins() {
        getServiceRegistry().scanForPlugins();
    }
}
