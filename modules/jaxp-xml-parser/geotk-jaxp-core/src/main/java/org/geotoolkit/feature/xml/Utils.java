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

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Logger;
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

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml");
    
    private Utils() {}

    /**
     * Return a Name from a QName.
     *
     * @param qname a XML QName.
     * @return a GeoAPI Name.
     */
    public static Name getNameFromQname(QName qname) {
        Name name;
        if (qname.getNamespaceURI() == null || "".equals(qname.getNamespaceURI())) {
            name = new DefaultName(qname.getLocalPart());
        } else {
            name = new DefaultName(qname);
        }
        return name;
    }

    /**
     * Return A QName from a Name.
     *
     * @param name a GeoAPI name.
     * @return A XML QName.
     */
    public static QName getQnameFromName(Name name) {
        QName qname;
        if (name.getNamespaceURI() == null || "".equals(name.getNamespaceURI())) {
            qname = new QName(name.getLocalPart());
        } else {
            qname = new QName(name.getNamespaceURI(), name.getLocalPart());
        }
        return qname;
    }

    /**
     * Return a primitive Class from the specified XML QName (extracted from an xsd file).
     *
     * @param name A XML QName.
     * @return A Class.
     */
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
            } else if ("MultiPoint".equals(name.getLocalPart())) {
                return MultiPoint.class;
            } else if ("Point".equals(name.getLocalPart())) {
                return Point.class;
            } else if ("Curve".equals(name.getLocalPart())) {
                return LineString.class;
            } else if ("MultiGeometry".equals(name.getLocalPart())) {
                return GeometryCollection.class;
            } else if ("CompositeCurve".equals(name.getLocalPart())) {
                return MultiLineString.class;
            } else if ("Envelope".equals(name.getLocalPart())) {
                return Envelope.class;
            } else if ("PolyHedralSurface".equals(name.getLocalPart())) {
                return MultiPolygon.class;
            } else if ("Polygon".equals(name.getLocalPart())) {
                return Polygon.class;
            } else if ("Ring".equals(name.getLocalPart())) {
                return LinearRing.class;

            } else {
                throw new IllegalArgumentException("unexpected type:" + name);
            }
        }
        return null;
    }

    /**
     * Return a QName intended to be used in a xsd XML file fro mthe specified class.
     *
     * @param binding A prmitive type Class.
     * @return A QName describing the class.
     */
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

                if (MultiPoint.class.equals(binding)) {
                    return new QName("http://www.opengis.net/gml", "MultiPoint");
                } else if (Point.class.equals(binding)) {
                    return new QName("http://www.opengis.net/gml", "Point");
                } else if (LineString.class.equals(binding)) {
                    return new QName("http://www.opengis.net/gml", "Curve");
                } else if (GeometryCollection.class.equals(binding)) {
                    return new QName("http://www.opengis.net/gml", "MultiGeometry");
                } else if (MultiLineString.class.equals(binding)) {
                    return new QName("http://www.opengis.net/gml", "CompositeCurve");
                } else if (Envelope.class.equals(binding)) {
                    return new QName("http://www.opengis.net/gml", "Envelope");
                } else if (MultiPolygon.class.equals(binding)) {
                    return new QName("http://www.opengis.net/gml", "MultiGeometry");
                } else if (Polygon.class.equals(binding)) {
                    return new QName("http://www.opengis.net/gml", "Polygon");
                } else if (LinearRing.class.equals(binding)) {
                    return new QName("http://www.opengis.net/gml", "Ring");
                } else {
                    return new QName("http://www.opengis.net/gml", "GeometryPropertyType");
                }
            } else {
                throw new IllegalArgumentException("unexpected type:" + binding);
            }
        }
        return null;
    }

    /**
     * Return an String representation of an Event Type.
     *
     * @param eventType An XMLEvent type.
     * @return A string representation or "UNKNOWN_EVENT_TYPE" if the integer does not correspound to an XMLEvent type.
     */
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

    /**
     * Return a String representation of an Object.
     * Accepted types are : - Integer, Long, String
     * Else it return null.
     *
     * @param obj A primitive object
     * @return A String representation of the Object.
     */
    public static String getStringValue(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Timestamp) {
            String tValue  =((Timestamp) obj).toString();
            return tValue.replace(" ", "T");
        } else if (obj instanceof Integer || obj instanceof Long || obj instanceof Double || obj instanceof Boolean) {
            return obj + "";

        } else if (obj != null) {
            LOGGER.warning("unexpected type:" + obj.getClass());
        }
        return null;
    }
}
