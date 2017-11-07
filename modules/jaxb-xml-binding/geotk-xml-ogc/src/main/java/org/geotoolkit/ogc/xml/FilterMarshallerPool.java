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

package org.geotoolkit.ogc.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;
import org.opengis.filter.Filter;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public final class FilterMarshallerPool {

    private static final MarshallerPool DEFAULT;
    static {
        try {
            DEFAULT = new MarshallerPool(JAXBContext.newInstance(
                    "org.geotoolkit.ogc.xml.v110:"           +
                    "org.geotoolkit.ogc.xml.v200:"           +
                    "org.apache.sis.internal.jaxb.geometry:" +
                    "org.geotoolkit.gml.xml.v311:"           +
                    "org.geotoolkit.gml.xml.v321"), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private static final MarshallerPool V100;
    static {
        try {
            V100 = new MarshallerPool(JAXBContext.newInstance(
                    "org.geotoolkit.ogc.xml.v100:"           +
                    "org.apache.sis.internal.jaxb.geometry:" +
                    "org.geotoolkit.gml.xml.v212:"
            ), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private FilterMarshallerPool() {}

    public static MarshallerPool getInstance(FilterVersion version) {
        switch (version) {
            case V100: return V100;
            default: return DEFAULT;
        }
    }

    public static MarshallerPool getInstance() {
        return DEFAULT;
    }

    public static XMLFilter transform(final Filter source, final FilterVersion outputVersion) {
        switch (outputVersion) {
            case V100: return V100_CONVERTER.apply(source);
            case V110: return V110_CONVERTER.apply(source);
            case V200: return V200_CONVERTER.apply(source);
            default: throw new UnsupportedOperationException("Version not supported yet: "+outputVersion);
        }
    }

    public static JAXBElement<?> toJAXBElement(final Filter source, final FilterVersion outputVersion) {
        switch (outputVersion) {
            case V100: return V100_CONVERTER.visit(source);
            case V110: return V110_CONVERTER.visit(source);
            case V200: return V200_CONVERTER.visit(source);
            default: throw new UnsupportedOperationException("Version not supported yet: "+outputVersion);
        }
    }

    static final FilterToOGC200Converter V200_CONVERTER = new FilterToOGC200Converter();
    static final FilterToOGC110Converter V110_CONVERTER = new FilterToOGC110Converter();
    static final FilterToOGC100Converter V100_CONVERTER = new FilterToOGC100Converter();
}
