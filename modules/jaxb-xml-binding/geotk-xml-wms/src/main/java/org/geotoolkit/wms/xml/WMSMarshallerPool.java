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

package org.geotoolkit.wms.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.xml.XML;

/**
 *
 * @author guilhem
 */
public class WMSMarshallerPool {

    /**
     * we separte the v 1.3.0 instance in order to marshall with no prefix (QGIS issue)
     */
    private static MarshallerPool instancev130;
    static {
        try {
            final Map<String, String> properties = new HashMap<String, String>();
            properties.put(XML.DEFAULT_NAMESPACE, "http://www.opengis.net/wms");
            instancev130 = new MarshallerPool(JAXBContext.newInstance(
                                          "org.geotoolkit.ogc.xml.exception:" +
                                          "org.geotoolkit.wms.xml.v130:" +
                                          "org.geotoolkit.sld.xml.v110:" +
                                          "org.geotoolkit.inspire.xml.vs:" +
                                          "org.apache.sis.internal.jaxb.geometry"), properties);
        } catch (JAXBException ex) {
            Logger.getLogger(WMSMarshallerPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool(JAXBContext.newInstance(
                    "org.geotoolkit.ogc.xml.exception:" +
                    "org.geotoolkit.wms.xml.v111:" +
                    "org.geotoolkit.wms.xml.v130:" +
                    "org.geotoolkit.sld.xml.v110:" +
                    "org.geotoolkit.inspire.xml.vs:" +
                    "org.apache.sis.internal.jaxb.geometry"), null);
        } catch (JAXBException ex) {
            Logger.getLogger(WMSMarshallerPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private WMSMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }

    public static MarshallerPool getInstance130() {
        return instancev130;
    }
}
