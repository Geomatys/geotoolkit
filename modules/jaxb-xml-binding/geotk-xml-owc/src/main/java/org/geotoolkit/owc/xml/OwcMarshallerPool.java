/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.owc.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class OwcMarshallerPool {
    
    public static final org.w3._2005.atom.ObjectFactory ATOM_FACTORY = new org.w3._2005.atom.ObjectFactory();
    public static final org.geotoolkit.owc.xml.v10.ObjectFactory OWC_FACTORY = new org.geotoolkit.owc.xml.v10.ObjectFactory();
    public static final org.geotoolkit.georss.xml.v100.ObjectFactory GEORSS_FACTORY = new org.geotoolkit.georss.xml.v100.ObjectFactory();
    
    private static final MarshallerPool POOL;
    static {
        try {
            final JAXBContext jaxbCtxt = JAXBContext.newInstance(
                       "org.geotoolkit.owc.xml.v10"
                    + ":org.w3._2005.atom"
                    + ":org.geotoolkit.georss.xml.v100"
                    + ":org.geotoolkit.gml.xml.v311"
                    + ":org.geotoolkit.sld.xml.v110"
                    + ":org.apache.sis.internal.jaxb.geometry"
                    + ":org.geotoolkit.wms.xml.v130"
                    + ":org.geotoolkit.owc.gtkext");
            POOL = new MarshallerPool(jaxbCtxt, null);
        } catch (JAXBException ex) {
            // Should never happen, unless we have a build configuration problem.
            throw new AssertionError(ex); 
        }
    }

    public static MarshallerPool getPool() throws JAXBException{
        return POOL;
    }
    
}
