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
package org.geotoolkit.wps.xml;

import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WPSMarshallerPool {

    private static MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool(JAXBContext.newInstance(
                      "org.geotoolkit.wps.xml.v100:"
                    + "org.geotoolkit.gml.xml.v311:"
                    + "org.geotoolkit.ows.xml.v110:"
                    + "org.apache.sis.internal.jaxb.geometry:"
                    + "org.geotoolkit.mathml.xml"), null);
        } catch (JAXBException ex) {
            Logging.getLogger(WPSMarshallerPool.class).log(Level.SEVERE, null, ex);
        }
    }
    private WPSMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
