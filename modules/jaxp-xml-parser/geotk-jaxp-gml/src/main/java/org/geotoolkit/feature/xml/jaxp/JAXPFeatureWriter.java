/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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


package org.geotoolkit.feature.xml.jaxp;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.geotoolkit.feature.xml.XmlFeatureWriter;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.geotoolkit.xml.StaxStreamWriter;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public abstract class JAXPFeatureWriter extends StaxStreamWriter implements XmlFeatureWriter {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml.jaxp");

    protected String schemaLocation;

    private static MarshallerPool pool;
    static {
        try {
            Map<String, String> properties = new HashMap<String, String>();
            properties.put(Marshaller.JAXB_FRAGMENT, "true");
            properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, "false");
            pool = new MarshallerPool(properties, ObjectFactory.class);
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, "JAXB Exception while initalizing the marshaller pool", ex);
        }
    }

    protected static ObjectFactory factory = new ObjectFactory();

    protected final Marshaller marshaller;

    public static final String GML_NAMESPACE = "http://www.opengis.net/gml";
    
    public JAXPFeatureWriter() throws JAXBException {
         marshaller = pool.acquireMarshaller();
         marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
    }

    public JAXPFeatureWriter(Map<String, String> schemaLocations) throws JAXBException {
         marshaller = pool.acquireMarshaller();
         marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
         if (schemaLocations != null && schemaLocations.size() > 0) {
             schemaLocation = "";
             for (String s : schemaLocations.keySet()) {
                 schemaLocation = schemaLocation + s + " " + schemaLocations.get(s) + " ";
             }
             schemaLocation = schemaLocation.substring(0, schemaLocation.length() - 1);
         }
    }
    
    @Override
    public void dispose() {
        pool.release(marshaller);
    }

}
