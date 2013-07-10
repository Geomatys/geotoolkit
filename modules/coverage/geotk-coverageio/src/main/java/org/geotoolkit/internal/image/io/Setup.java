/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.image.io;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Properties;
import javax.imageio.spi.IIORegistry;

import org.geotoolkit.internal.SetupService;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.io.plugin.WorldFileImageWriter;


/**
 * Performs initialization and shutdown of the {@code geotk-coverageio} module.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @see org.geotoolkit.lang.Setup
 *
 * @since 3.10
 * @module
 */
public final class Setup implements SetupService {
    /**
     * Registers the world-file readers and writers.
     */
    @Override
    public void initialize(final Properties properties, final boolean reinit) {
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        WorldFileImageReader.Spi.registerDefaults(registry);
        WorldFileImageWriter.Spi.registerDefaults(registry);
        if (reinit) {
            IIORegistry.getDefaultInstance().registerApplicationClasspathSpis();
        }
    }

    /**
     * Unregisters the world-file readers and writers, then unregister all Geotk plugins.
     * Note that we should not need to unregister explicitly the world-file plugins since
     * they should be removed by the loop just after, but we do that anyway as a matter of
     * principle.
     */
    @Override
    public void shutdown() {
        final IIORegistry registry = IIORegistry.getDefaultInstance();
        WorldFileImageReader.Spi.unregisterDefaults(registry);
        WorldFileImageWriter.Spi.unregisterDefaults(registry);
        final List<Object> toRemove = new ArrayList<>();
        final Iterator<Class<?>> categories = registry.getCategories();
        while (categories.hasNext()) {
            final Class<?> category = categories.next();
            final Iterator<?> it = registry.getServiceProviders(category, false);
            while (it.hasNext()) {
                final Object spi = it.next();
                if (spi.getClass().getName().startsWith("org.geotoolkit.")) {
                    toRemove.add(spi);
                }
            }
        }
        for (final Object spi : toRemove) {
            registry.deregisterServiceProvider(spi);
        }
    }
}
