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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;
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
 * @module
 */
public final class DynamicSymbolFactoryFinder {
    /** The logger for the filter module. */
    protected static final Logger LOGGER = org.apache.sis.util.logging.Logging
            .getLogger("org.geotoolkit.renderer.style");

    private static final List<MarkFactory> MARK_FACTORIES = new ArrayList<>();
    private static final List<ExternalGraphicFactory> EXT_FACTORIES = new ArrayList<>();
    static {
        ServiceLoader.load(MarkFactory.class).iterator().forEachRemaining(MARK_FACTORIES::add);
        ServiceLoader.load(ExternalGraphicFactory.class).iterator().forEachRemaining(EXT_FACTORIES::add);
    }

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
    public static BufferedImage getImage(final URI uri, final String mime, final Float size, final RenderingHints hints) throws Exception{
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
     * @see org.geotoolkit.renderer.style.ExternalGraphicFactory#getImage(java.net.URI, java.lang.String, java.lang.Float, RenderingHints)
     */
    public static void renderImage(final URI uri, final String mime, final Float size, final Graphics2D g,
            final Point2D center,final RenderingHints hints) throws Exception{
        final Iterator<ExternalGraphicFactory> ite = getExternalGraphicFactories();
        while(ite.hasNext()){
            final ExternalGraphicFactory factory = ite.next();
            if(factory.getSupportedMimeTypes().contains(mime)){
                 factory.renderImage(uri, mime, size, g, center, hints);
                 return;
            }
        }
    }

    /**
     * Finds all implementations of {@link MarkFactory} which have registered
     * using the services mechanism.
     *
     * @return An iterator over all discovered datastores which have registered
     *         factories, and whose available method returns true.
     */
    public static synchronized Iterator<MarkFactory> getMarkFactories() {
        return MARK_FACTORIES.iterator();
    }

    /**
     * Finds all implementations of {@link ExternalGraphicFactory} which have
     * registered using the services mechanism.
     *
     * @return An iterator over all discovered datastores which have registered
     *         factories, and whose available method returns true.
     */
    public static synchronized Iterator<ExternalGraphicFactory> getExternalGraphicFactories() {
        return EXT_FACTORIES.iterator();
    }

}
