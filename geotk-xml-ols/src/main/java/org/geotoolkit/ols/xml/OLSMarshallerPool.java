/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ols.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 * Shared JAXB marshaller pool for OGC OpenLS (OLS) 1.2.0 XML bindings.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class OLSMarshallerPool {

    private static final MarshallerPool INSTANCE;
    static {
        try {
            INSTANCE = new MarshallerPool(JAXBContext.newInstance(
                      "org.geotoolkit.ols.xml.v121:"
                    + "org.geotoolkit.gml.xml.v311"), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private OLSMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return INSTANCE;
    }
}
