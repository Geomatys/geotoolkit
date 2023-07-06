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

import java.util.Map;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.wps.xml.v200.WPSMarshaller;
import org.geotoolkit.wps.xml.v200.WPSUnmarshaller;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public final class WPSMarshallerPool {

    public static final String WPS_1_0_NAMESPACE = "http://www.opengis.net/wps/1.0.0";
    public static final String WPS_2_0_NAMESPACE = "http://www.opengis.net/wps/2.0";

    public static final String OWS_1_1_NAMESPACE = "http://www.opengis.net/ows/1.1";
    public static final String OWS_2_0_NAMESPACE = "http://www.opengis.net/ows/2.0";

    private static final MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPoolProxy(TypeRegistration.getSharedContext(), null);
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
            final Marshaller marshaller = super.createMarshaller();
            marshaller.setProperty("org.glassfish.jaxb.characterEscapeHandler", new NoCharacterEscapeHandler());
            return new WPSMarshaller(marshaller);
        }

        @Override
        protected Unmarshaller createUnmarshaller() throws JAXBException {
            return new WPSUnmarshaller(super.createUnmarshaller());
        }
    }

    private WPSMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
