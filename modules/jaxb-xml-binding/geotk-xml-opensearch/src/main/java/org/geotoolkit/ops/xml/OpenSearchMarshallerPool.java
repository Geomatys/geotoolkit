/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.ops.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OpenSearchMarshallerPool {

    public static final org.w3._2005.atom.ObjectFactory ATOM_FACTORY = new org.w3._2005.atom.ObjectFactory();
    public static final org.geotoolkit.georss.xml.v100.ObjectFactory GEORSS_FACTORY = new org.geotoolkit.georss.xml.v100.ObjectFactory();

    private static final MarshallerPool POOL;
    static {
        try {
            final List<Class> classes = new ArrayList<>();
            classes.add(org.apache.sis.internal.jaxb.geometry.ObjectFactory.class);
            classes.add(org.geotoolkit.gml.xml.v311.ObjectFactory.class);
            classes.add(org.geotoolkit.georss.xml.v100.ObjectFactory.class);
            classes.add(org.w3._2005.atom.ObjectFactory.class);
            classes.add(org.geotoolkit.eop.xml.v100.ObjectFactory.class);
            classes.add(org.geotoolkit.opt.xml.v100.ObjectFactory.class);
            classes.add(org.geotoolkit.eop.xml.v201.ObjectFactory.class);
            classes.add(org.geotoolkit.opt.xml.v201.ObjectFactory.class);
            classes.add(org.geotoolkit.ops.xml.v110.ObjectFactory.class);
            classes.add(org.geotoolkit.ows.xml.v200.ObjectFactory.class);
            classes.add(org.geotoolkit.observation.xml.v200.ObjectFactory.class);

            // dublin core optional
            try {
                Class dcClass = Class.forName("org.geotoolkit.dublincore.xml.v2.elements.ObjectFactory");
                classes.add(dcClass);
            } catch (ClassNotFoundException ex) {}


            final JAXBContext jaxbCtxt = JAXBContext.newInstance(classes.toArray(new Class[classes.size()]));
            POOL = new MarshallerPool(jaxbCtxt, null);
        } catch (JAXBException ex) {
            // Should never happen, unless we have a build configuration problem.
            throw new AssertionError(ex);
        }
    }

    public static MarshallerPool getInstance() throws JAXBException{
        return POOL;
    }

}
