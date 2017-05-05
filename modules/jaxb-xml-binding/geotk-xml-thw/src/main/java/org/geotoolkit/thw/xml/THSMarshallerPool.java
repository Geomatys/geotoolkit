/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

package org.geotoolkit.thw.xml;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author Guilhem Legal (Geomatys)
 *
 * @since 2.2.3
 */
public class THSMarshallerPool {

    private static MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool(JAXBContext.newInstance(org.geotoolkit.thw.xml.ObjectFactory.class,
                                          org.geotoolkit.ows.xml.v110.ObjectFactory.class,
                                          org.geotoolkit.skos.xml.ObjectFactory.class,
                                          org.geotoolkit.gml.xml.v311.ObjectFactory.class,
                                          org.apache.sis.internal.jaxb.geometry.ObjectFactory.class),
                                          properties());
        } catch (JAXBException ex) {
            Logger.getLogger(THSMarshallerPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private THSMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
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
