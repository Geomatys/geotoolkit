/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Geomatys
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

package org.geotoolkit.gml.xml;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public final class GMLMarshallerPool {

    private static final MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool(createJAXBContext(
                    "org.geotoolkit.gml.xml.v311:" +
                    "org.geotoolkit.gml.xml.v321",
                    GMLMarshallerPool.class.getClassLoader()), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a configuration problem.
        }
    }

    private GMLMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }

    /**
     * Creates a JAXB context for the given package names.
     * This method is strictly internal to Geotk and shall not be invoked by client code.
     *
     * @param  packages The colon-separated list of packages.
     * @param  loader   The class loader to use.
     * @return The JAXB context for the given packages.
     * @throws JAXBException If the JAXB context can not be created.
     */
    public static JAXBContext createJAXBContext(final String packages, final ClassLoader loader) throws JAXBException {
        final Map<String,?> properties = properties();
        try {
            return JAXBContext.newInstance(packages, loader, properties);
        } catch (JAXBException e) {
            /*
             * The JAXB implementation bundled in JDK6+ has "internal" in the package names and property names.
             * But the JAXB implementation used by Glassfish does not have the "internal" part. If we have not
             * been able to instantiate the context with the internal implementation, try with the endorsed one.
             */
            return JAXBContext.newInstance(packages, loader, toEndorsed(properties));
        }
    }

    /**
     * Creates a JAXB context for the given classes.
     * This method is strictly internal to Geotk and shall not be invoked by client code.
     *
     * @param  classes The classes.
     * @return The JAXB context for the given packages.
     * @throws JAXBException If the JAXB context can not be created.
     */
    public static JAXBContext createJAXBContext(final Class<?>... classes) throws JAXBException {
        final Map<String,?> properties = properties();
        try {
            return JAXBContext.newInstance(classes, properties);
        } catch (JAXBException e) {
            return JAXBContext.newInstance(classes, toEndorsed(properties));
        }
    }

    /**
     * Returns the vendor-specific properties to give to the JDK internal JAXB implementation.
     */
    private static Map<String,?> properties() {
        /*
         * The SIS and the Geotk classes have the same XML type name in the "http://www.opengis.net/gml" namespace.
         * This is because SIS provides an incomplete implementation, which shall be replaced by the Geotk one until
         * we ported the temporal module to SIS. So we try to use the vendor-specific 'subclassReplacements' property
         * for replacing SIS classes by the Geotk ones.
         */
        final Map<Class<?>, Class<?>> subclassReplacements = new HashMap<>(4);
        subclassReplacements.put(org.apache.sis.internal.jaxb.gml.TimeInstant.class, org.geotoolkit.gml.xml.v311.TimeInstantType.class);
        subclassReplacements.put(org.apache.sis.internal.jaxb.gml.TimePeriod.class,  org.geotoolkit.gml.xml.v311.TimePeriodType.class);
        return Collections.singletonMap("com.sun.xml.internal.bind.subclassReplacements", subclassReplacements);
    }

    /**
     * Converts the given JDK internal JAXB properties of the endorsed JAXB properties.
     */
    private static Map<String,?> toEndorsed(final Map<String,?> properties) {
        return Collections.singletonMap("com.sun.xml.bind.subclassReplacements", properties.values().iterator().next());
    }
}
