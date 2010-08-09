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

import javax.xml.bind.JAXBException;
import org.geotoolkit.xml.MarshallerPool;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FilterMarshallerPool {

    private static MarshallerPool instance;
    
    private FilterMarshallerPool() {}

    public static MarshallerPool getInstance() throws JAXBException {
        if (instance == null) {
            instance = new MarshallerPool("org.geotoolkit.ogc.xml.v110:" +
                                         "org.geotoolkit.internal.jaxb.geometry:" +
                                         "org.geotoolkit.gml.xml.v311");
        }
        return instance;
    }
}
