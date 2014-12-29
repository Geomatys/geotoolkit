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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.sld.xml.JAXBSLDUtilities;

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
            final List<Class> classes = new ArrayList<>();
            classes.add(org.apache.sis.internal.jaxb.geometry.ObjectFactory.class);
            classes.add(org.geotoolkit.owc.gtkext.ObjectFactory.class);
            classes.add(org.geotoolkit.wms.xml.v130.ObjectFactory.class);
            classes.add(org.geotoolkit.gml.xml.v311.ObjectFactory.class);
            classes.add(org.geotoolkit.georss.xml.v100.ObjectFactory.class);
            classes.add(org.w3._2005.atom.ObjectFactory.class);
            classes.add(org.geotoolkit.owc.xml.v10.ObjectFactory.class);
            classes.addAll(JAXBSLDUtilities.getSLD110PoolClasses());
                        
            final JAXBContext jaxbCtxt = JAXBContext.newInstance(classes.toArray(new Class[classes.size()]));
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
