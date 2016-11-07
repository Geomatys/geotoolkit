/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wps.xml;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public final class WPSMarshallerPool {

    private static final MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPoolProxy(JAXBContext.newInstance(
                      "org.geotoolkit.wps.xml.v100.ext:"
                    + "org.geotoolkit.wps.xml.v100:"
                    + "org.geotoolkit.wps.xml.v200:"
                    + "org.geotoolkit.gml.xml.v311:"
                    + "org.geotoolkit.ows.xml.v110:"
                    + "org.geotoolkit.ows.xml.v200:"
                    + "org.apache.sis.internal.jaxb.geometry:"
                    + "org.geotoolkit.mathml.xml"), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    /**
     * Proxy class that redefines the acquireMarshaller() method.
     *
     * The aim of this class is to set a CharacterEscapeHandler on all Marshaller
     * instances in order to avoid escaping characters when marshalling datas that
     * should be written inside a CDATA tag.
     */
    private static class MarshallerPoolProxy extends MarshallerPool {

        public MarshallerPoolProxy(JAXBContext context, Map<String, ?> properties) throws JAXBException {
            super(context, properties);
        }

        @Override
        protected Marshaller createMarshaller() throws JAXBException {
            Marshaller marshaller = super.createMarshaller();
            marshaller.setProperty(CharacterEscapeHandler.class.getName(), new NoCharacterEscapeHandler());
            return marshaller;
        }
    }

    private WPSMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
