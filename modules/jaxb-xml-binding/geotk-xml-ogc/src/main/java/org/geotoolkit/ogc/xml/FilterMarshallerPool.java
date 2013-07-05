/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.ogc.xml;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FilterMarshallerPool {

    private static MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool(JAXBContext.newInstance(
                    "org.geotoolkit.ogc.xml.v110:"           +
                    "org.geotoolkit.ogc.xml.v200:"           +
                    "org.apache.sis.internal.jaxb.geometry:" +
                    "org.geotoolkit.gml.xml.v311"), null);
        } catch (JAXBException ex) {
            Logger.getLogger(FilterMarshallerPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private FilterMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
