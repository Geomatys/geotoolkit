/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.internal.jaxb;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.internal.jaxb.LegacyNamespaces;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.xml.XML;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public final class JTSWrapperMarshallerPool {
    private static final MarshallerPool instance;
    static {
        final Map<String, Object> properties = new HashMap<>();
        properties.put(XML.GML_VERSION, "3.1.1");
        properties.put(XML.METADATA_VERSION, LegacyNamespaces.VERSION_2007);
        try {
            instance = new MarshallerPool(JAXBContext.newInstance(ObjectFactory.class), properties);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private JTSWrapperMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
