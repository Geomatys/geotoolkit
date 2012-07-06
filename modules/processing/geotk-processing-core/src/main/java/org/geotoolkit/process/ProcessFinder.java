/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
 */
package org.geotoolkit.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.spi.ServiceRegistry;
import org.opengis.metadata.Identifier;
import org.opengis.util.NoSuchIdentifierException;

/**
 * Utility class to find Process factories and descriptors.
 * At he fist ProcessFinder method call, he finder will use ServiceRegistry to lookup all ProcessingRegistry
 * in jar files and he cache this list of ProcessingRegistry.
 *
 * To update the list, call {@link #scanForPlugins()} method.
 *
 * Usage :
 * <code>
 *  //get all factories
 *  Iterator&lt;ProcessingRegistry&gt; registries = ProcessFinder.getProcessFactories();
 *
 *  //get a factory from her name
 *  ProcessingRegistry registry = ProcessFinder.getProcessFactory("myFactoryName");
 *
 *  //get a ProcessDescriptor from specific factory
 *  ProcessingRegistry registry = ProcessFinder.getProcessDescriptor("myFactoryName", "processDescriptorName");
 *
 *  //search a ProcessDescriptor without knowing registry name.
 *  ProcessingRegistry registry = ProcessFinder.getProcessDescriptor(null, "processDescriptorName");
 *
 *  //Rescan jars with ServiceRegistry to get the last ProcessRegistries
 *  ProcessFinder.scanForPlugins();
 * </code>
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys) @moduel pending
 */
public final class ProcessFinder {

    private static List<ProcessingRegistry> REGISTRIES = null;

    private ProcessFinder() {
    }

    /**
     * @return Iterator of all available ProcessFactory.
     */
    public static synchronized Iterator<ProcessingRegistry> getProcessFactories() {
        if (REGISTRIES == null) {
            final Iterator<ProcessingRegistry> ite = ServiceRegistry.lookupProviders(ProcessingRegistry.class);
            REGISTRIES = new ArrayList<ProcessingRegistry>();
            while (ite.hasNext()) {
                REGISTRIES.add(ite.next());
            }
        }
        return REGISTRIES.iterator();
    }

    /**
     * Return the factory for the given authority code.
     *
     * @param authorityCode registry name
     * @return ProcessingRegistry or null if not found
     */
    public static synchronized ProcessingRegistry getProcessFactory(final String authorityCode) {
        final Iterator<ProcessingRegistry> ite = getProcessFactories();
        while (ite.hasNext()) {
            final ProcessingRegistry candidate = ite.next();
            for (final Identifier id : candidate.getIdentification().getCitation().getIdentifiers()) {
                if (id.getCode().equalsIgnoreCase(authorityCode)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Search for a Process descriptor in the given authority and the given name.
     *
     * @param authority registry name
     * @param processName process descriptor name
     * @return ProcessDescriptor
     * @throws IllegalArgumentException if description could not be found
     */
    public static synchronized ProcessDescriptor getProcessDescriptor(String authority, final String processName) throws NoSuchIdentifierException {

        if (authority != null && authority.trim().isEmpty()) {
            authority = null;
        }

        if (authority != null) {
            final ProcessingRegistry factory = getProcessFactory(authority);
            if (factory != null) {
                return factory.getDescriptor(processName);
            } else {
                throw new NoSuchIdentifierException("No processing registry for given code.", authority);
            }
        }

        //try all factories
        final Iterator<ProcessingRegistry> ite = getProcessFactories();
        while (ite.hasNext()) {
            final ProcessingRegistry factory = ite.next();
            try {
                return factory.getDescriptor(processName);
            } catch (NoSuchIdentifierException ex) {
            }
        }

        throw new NoSuchIdentifierException("No process for given code.", processName);
    }

    /**
     * Research all ProcessingRegistries at next ProcessFinder call.
     */
    public static void scanForPlugins() {
        REGISTRIES = null;
    }

    /**
     * Return the factory for the given authority code.
     *
     * @param authorityCode registry name
     * @return ProcessingRegistry or null if not found
     */
    public static ProcessingRegistry getProcessFactory(final Iterator<? extends ProcessingRegistry> factories, final String authorityCode) {
        while (factories.hasNext()) {
            final ProcessingRegistry candidate = factories.next();
            for (final Identifier id : candidate.getIdentification().getCitation().getIdentifiers()) {
                if (id.getCode().equalsIgnoreCase(authorityCode)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Search for a Process descriptor in the given authority and the given name.
     *
     * @param authority registry name
     * @param processName process descriptor name
     * @return ProcessDescriptor
     * @throws IllegalArgumentException if description could not be found
     */
    public static ProcessDescriptor getProcessDescriptor(final Iterator<? extends ProcessingRegistry> factories, String authority, final String processName)
            throws NoSuchIdentifierException {

        if (authority != null && authority.trim().isEmpty()) {
            authority = null;
        }

        if (authority != null) {
            final ProcessingRegistry factory = getProcessFactory(factories, authority);
            if (factory != null) {
                return factory.getDescriptor(processName);
            } else {
                throw new NoSuchIdentifierException("No processing registry for given code.", authority);
            }
        }

        //try all factories
        while (factories.hasNext()) {
            final ProcessingRegistry factory = factories.next();
            try {
                return factory.getDescriptor(processName);
            } catch (NoSuchIdentifierException ex) {
            }
        }

        throw new NoSuchIdentifierException("No process for given code.", processName);
    }
}
