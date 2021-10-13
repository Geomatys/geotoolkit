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
import java.util.Collections;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.internal.xml.LegacyNamespaces;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.xml.XML;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public final class GMLMarshallerPool {

    private static final MarshallerPool instance;
    static {
        final Map<String, Object> properties = Collections.singletonMap
                (XML.METADATA_VERSION, LegacyNamespaces.VERSION_2007);
        try {
            instance = new MarshallerPool(createJAXBContext(
                    "org.geotoolkit.gml.xml.v311:" +
                    "org.geotoolkit.gml.xml.v321",
                    GMLMarshallerPool.class.getClassLoader()), properties);
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
        return JAXBContext.newInstance(packages, loader, properties());
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
        return JAXBContext.newInstance(classes, properties());
    }

    /**
     * Returns the vendor-specific properties to give to the JDK internal JAXB implementation.
     */
    private static Map<String,?> properties() {
        /*
         * A previous implementation was setting the vendor-specific "com.sun.xml.bind.subclassReplacements"
         * property here. This has been removed, but we keep the mechanism in case we need to reinsert some
         * properties later.
         */
        return Collections.emptyMap();
    }
}
