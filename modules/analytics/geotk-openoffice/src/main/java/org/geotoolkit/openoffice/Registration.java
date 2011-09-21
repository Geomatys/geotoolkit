/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.openoffice;

import com.sun.star.lang.XSingleServiceFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.comp.loader.FactoryHelper;
import com.sun.star.registry.XRegistryKey;


/**
 * The registration of all formulas provided in this package.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.09 (derived from 2.2)
 * @module
 */
public final class Registration {
    /**
     * Do not allow instantiation of this class.
     */
    private Registration() {
    }

    /**
     * Returns a factory for creating the service.
     * This method is called by the {@code com.sun.star.comp.loader.JavaLoader}; do not rename!
     *
     * @param   implementation The name of the implementation for which a service is desired.
     * @param   factories      The service manager to be used if needed.
     * @param   registry       The registry key
     * @return  A factory for creating the component.
     */
    public static XSingleServiceFactory __getServiceFactory(
            final String               implementation,
            final XMultiServiceFactory factories,
            final XRegistryKey         registry)
    {
        if (implementation.equals(org.geotoolkit.openoffice.geoapi.Referencing.class.getName())) {
            return FactoryHelper.getServiceFactory(org.geotoolkit.openoffice.geoapi.Referencing.class,
                    org.geotoolkit.openoffice.geoapi.Referencing.__serviceName, factories, registry);
        }
        if (implementation.equals(Referencing.class.getName())) {
            return FactoryHelper.getServiceFactory(Referencing.class, Referencing.__serviceName, factories, registry);
        }
        if (implementation.equals(Nature.class.getName())) {
            return FactoryHelper.getServiceFactory(Nature.class, Nature.__serviceName, factories, registry);
        }
        return null;
    }

    /**
     * Writes the service information into the given registry key.
     * This method is called by the {@code com.sun.star.comp.loader.JavaLoader}; do not rename!
     *
     * @param  registry     The registry key.
     * @return {@code true} if the operation succeeded.
     */
    public static boolean __writeRegistryServiceInfo(final XRegistryKey registry) {
        return register(org.geotoolkit.openoffice.geoapi.Referencing.class,
                        org.geotoolkit.openoffice.geoapi.Referencing.__serviceName, registry)
            && register(Referencing.class, Referencing.__serviceName, registry)
            && register(Nature     .class, Nature.     __serviceName, registry);
    }

    /**
     * Helper method for the above {@link #__writeRegistryServiceInfo} method.
     */
    private static boolean register(final Class<? extends Formulas> classe,
            final String serviceName, final XRegistryKey registry)
    {
        final String cn = classe.getName();
        return FactoryHelper.writeRegistryServiceInfo(cn, serviceName, registry)
            && FactoryHelper.writeRegistryServiceInfo(cn, Formulas.ADDIN_SERVICE, registry);
    }
}
