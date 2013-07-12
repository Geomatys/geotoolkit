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


package org.geotoolkit.wmts.xml;

import java.util.Collections;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.xml.XML;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public final class WMTSMarshallerPool {

    private static final MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool(JAXBContext.newInstance(
                 "org.geotoolkit.wmts.xml.v100:" +
                 "org.geotoolkit.gml.xml.v311:" +
                 "org.apache.sis.internal.jaxb.geometry:" +
                 "org.geotoolkit.ows.xml.v110"),
                    Collections.singletonMap(XML.DEFAULT_NAMESPACE, "http://www.opengis.net/wmts/1.0"));
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private WMTSMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
