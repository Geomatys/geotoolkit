/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.kml.xml;

import java.util.Collections;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class KMLMarshallerPool {

    private static final MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool(createJAXBContext(
                    "org.geotoolkit.ogc.xml.exception:" +
                    "org.geotoolkit.kml.xml.v220:" +
                    "org.geotoolkit.xal.xml.v20:" +
                    "org.geotoolkit.atom.xml:",
                    KMLMarshallerPool.class.getClassLoader()), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private KMLMarshallerPool() {}

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
        final Map<String,?> properties = Collections.emptyMap();
        return JAXBContext.newInstance(packages, loader, properties);
    }
}
