
package org.geotoolkit.se.xml;

/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.opengis.util.InternationalString;


/**
 * JAXB adapter for XML {@code <InternationalString>} element mapped to {@link String}.
 *
 * @author glegal (Geomatys)
 * @version 3.17
 *
 * @since 2.5
 * @module
 */
public final class DirectStringAdapter extends XmlAdapter<String, InternationalString> {

    @Override
    public InternationalString unmarshal(String v) throws Exception {
        return new SimpleInternationalString(v);
    }

    @Override
    public String marshal(InternationalString v) throws Exception {
        return v.toString();
    }
}
