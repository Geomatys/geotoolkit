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
package org.geotoolkit.internal.image;

import java.util.List;
import java.util.Iterator;
import java.util.Properties;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.RegistryElementDescriptor;
import java.awt.image.renderable.RenderedImageFactory;

import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.internal.SetupService;


/**
 * Performs initialization and shutdown of the {@code geotk-coverage} module.
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
     * The product name under which Geotk JAI operations are registered.
     */
    public static final String PRODUCT_NAME = "org.geotoolkit";

    private boolean initialized;

    /**
     * Set the ordering of image reader and writers.
     */
    @Override
    public void initialize(final Properties properties, final boolean reinit) {
        Registry.setDefaultCodecPreferences();
        if (reinit) {
            Registry.registerGeotoolkitServices(JAI.getDefaultInstance().getOperationRegistry());
            initialized = true;
        }
    }

    /**
     * Unregisters all Geotk JAI operations.
     */
    @Override
    public void shutdown() {
        if (initialized) {
            final OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
            for (final String mode : registry.getRegistryModes()) {
                @SuppressWarnings("unchecked")
                final List<RegistryElementDescriptor> descriptors = registry.getDescriptors(mode);
                for (final RegistryElementDescriptor descriptor : descriptors) {
                    final String operationName = descriptor.getName();
                    if (operationName.startsWith("org.geotoolkit.")) {
                        @SuppressWarnings("unchecked")
                        final Iterator<RenderedImageFactory> rif = RIFRegistry.getIterator(registry, operationName);
                        while (rif.hasNext()) {
                            RIFRegistry.unregister(registry, operationName, PRODUCT_NAME, rif.next());
                        }
                        registry.unregisterDescriptor(descriptor);
                    }
                }
            }
        }
    }
}
