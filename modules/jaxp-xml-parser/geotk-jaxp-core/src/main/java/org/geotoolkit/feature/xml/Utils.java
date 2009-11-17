/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.feature.xml;

import com.vividsolutions.jts.geom.Geometry;
import java.util.Date;
import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;
import org.geotoolkit.feature.DefaultName;
import org.opengis.feature.type.Name;

/**
 *
 * @module pending
 * @author Guilhem Legal (Geomatys)
 */
public class Utils {

    private Utils() {}

    public static Name getNameFromQname(QName qname) {
        Name name;
        if (qname.getNamespaceURI() == null || "".equals(qname.getNamespaceURI())) {
            name = new DefaultName(qname.getLocalPart());
        } else {
            name = new DefaultName(qname);
        }
        return name;
    }

    public static QName getQnameFromName(Name name) {
        QName qname;
        if (name.getNamespaceURI() == null || "".equals(name.getNamespaceURI())) {
            qname = new QName(name.getLocalPart());
        } else {
            qname = new QName(name.getNamespaceURI(), name.getLocalPart());
        }
        return qname;
    }

    public static Class getTypeFromQName(QName name) {
        if (name != null) {
            if ("long".equals(name.getLocalPart())) {
                return Long.class;
            } else if ("integer".equals(name.getLocalPart())) {
                return Integer.class;
            } else if ("string".equals(name.getLocalPart())) {
                return String.class;
            } else if ("date".equals(name.getLocalPart())) {
                return Date.class;
            } else if ("double".equals(name.getLocalPart())) {
                return Double.class;
            } else if ("GeometryPropertyType".equals(name.getLocalPart())) {
                return Geometry.class;
            } else {
                throw new IllegalArgumentException("unexpected type:" + name);
            }
        }
        return null;
    }

    public static QName getQNameFromType(Class binding) {
        if (binding != null) {
            if (Long.class.equals(binding)) {
                return new QName("http://www.w3.org/2001/XMLSchema", "long");
            } else if (Integer.class.equals(binding)) {
                return new QName("http://www.w3.org/2001/XMLSchema", "integer");
            } else if (String.class.equals(binding)) {
                return new QName("http://www.w3.org/2001/XMLSchema", "string");
            } else if (Double.class.equals(binding)) {
                return new QName("http://www.w3.org/2001/XMLSchema", "double");
            } else if (Date.class.equals(binding)) {
                return new QName("http://www.w3.org/2001/XMLSchema", "date");
            } else if (Geometry.class.isAssignableFrom(binding)) {
                return new QName("http://www.opengis.net/gml", "GeometryPropertyType");
            } else {
                throw new IllegalArgumentException("unexpected type:" + binding);
            }
        }
        return null;
    }

    public final static String getEventTypeString(int eventType) {
        switch (eventType) {
            case XMLEvent.START_DOCUMENT:
                return "START_DOCUMENT";
            case XMLEvent.END_DOCUMENT:
                return "END_DOCUMENT";
            case XMLEvent.START_ELEMENT:
                return "START_ELEMENT";
            case XMLEvent.END_ELEMENT:
                return "END_ELEMENT";
            case XMLEvent.PROCESSING_INSTRUCTION:
                return "PROCESSING_INSTRUCTION";
            case XMLEvent.CHARACTERS:
                return "CHARACTERS";
            case XMLEvent.COMMENT:
                return "COMMENT";

        }
        return "UNKNOWN_EVENT_TYPE";
    }
}
