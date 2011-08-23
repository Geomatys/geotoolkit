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

package org.geotoolkit.citygml.xml;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.geotoolkit.xml.MarshallerPool;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class CityGMLMarshallerPool {

    private static MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool("org.geotoolkit.internal.jaxb.geometry:" +
                                          "org.geotoolkit.gml.xml.v311:" +
                                          "org.geotoolkit.citygml.xml.v100:" +
                                          "org.geotoolkit.citygml.xml.v100.building:" + 
                                          "org.geotoolkit.citygml.xml.v100.cityfurniture:" + 
                                          "org.geotoolkit.citygml.xml.v100.transportation");
        } catch (JAXBException ex) {
            Logger.getLogger(CityGMLMarshallerPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private CityGMLMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
