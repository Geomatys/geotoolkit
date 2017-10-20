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
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.xml.XML;

import static org.geotoolkit.gml.xml.GMLMarshallerPool.createJAXBContext;


/**
 *
 * @author guilhem
 */
public final class WMSMarshallerPool {

    /**
     * we separate the v 1.0.0 INSTANCE in order to marshall with no prefix. We
     * have to do so, because there's a problem with namespaces in old OGC
     * standards.
     */
    private static final MarshallerPool V_100;
    static {
        try {
            final Map<String, Object> properties = new HashMap<>();
            properties.put(XML.DEFAULT_NAMESPACE, "http://www.opengis.net/wms");
            V_100 = new MarshallerPool(createJAXBContext(
                    "org.geotoolkit.ogc.xml.exception:"
                    + "org.geotoolkit.wms.xml.v100:"
                    + "org.apache.sis.internal.jaxb.geometry",
                    WMSMarshallerPool.class.getClassLoader()), properties) {
                @Override
                public Unmarshaller acquireUnmarshaller() throws JAXBException {
                    final Unmarshaller u = super.acquireUnmarshaller();
                    return new DTDIgnoreUnmarshaller(u);
                }

                @Override
                public void recycle(Unmarshaller unmarshaller) {
                    if (unmarshaller instanceof DTDIgnoreUnmarshaller)
                        unmarshaller = ((DTDIgnoreUnmarshaller) unmarshaller).source;
                    super.recycle(unmarshaller);
                }
            };
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    /**
     * we separate the v 1.3.0 INSTANCE in order to marshall with no prefix (QGIS issue)
     */
    private static final MarshallerPool V_130;
    static {
        try {
            final Map<String, String> properties = new HashMap<>();
            properties.put(XML.DEFAULT_NAMESPACE, "http://www.opengis.net/wms");
            V_130 = new MarshallerPool(createJAXBContext(
                    "org.geotoolkit.ogc.xml.exception:" +
                    "org.geotoolkit.wms.xml.v130:" +
                    "org.geotoolkit.sld.xml.v110:" +
                    "org.geotoolkit.inspire.xml.vs:" +
                    "org.apache.sis.internal.jaxb.geometry",
                    WMSMarshallerPool.class.getClassLoader()), properties);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private static final MarshallerPool DEFAULT;
    static {
        try {
            DEFAULT = new MarshallerPool(createJAXBContext(
                    "org.geotoolkit.ogc.xml.exception:" +
                    "org.geotoolkit.wms.xml.v111:" +
                    "org.geotoolkit.wms.xml.v130:" +
                    "org.geotoolkit.sld.xml.v110:" +
                    "org.geotoolkit.inspire.xml.vs:" +
                    "org.apache.sis.internal.jaxb.geometry",
                    WMSMarshallerPool.class.getClassLoader()), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private WMSMarshallerPool() {}
    public static MarshallerPool getInstance(final WMSVersion version) {
        switch (version) {
            case v100: return V_100;
            case v130: return V_130;
            default: return DEFAULT;
        }
    }

    public static MarshallerPool getInstance() {
        return DEFAULT;
    }

    public static MarshallerPool getInstance130() {
        return V_130;
    }

    public static MarshallerPool getInstance100() {
        return V_100;
    }
}
