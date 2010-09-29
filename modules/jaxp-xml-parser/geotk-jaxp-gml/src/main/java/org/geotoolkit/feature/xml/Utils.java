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
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
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

    private static final DateFormat timestampFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static{
        timestampFormatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }

    private static final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'Z'");
    static{
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }

    private Utils() {}

    /**
     * Return a Name from a QName.
     *
     * @param qname a XML QName.
     * @return a GeoAPI Name.
     */
    public static Name getNameFromQname(QName qname) {
        Name name;
        if (qname.getNamespaceURI() == null || qname.getNamespaceURI().isEmpty()) {
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

    private static final Map<String, Class> CLASS_BINDING = new HashMap<String, Class>();
    static {
        CLASS_BINDING.put("long",     Long.class);
        CLASS_BINDING.put("integer",  Integer.class);
        CLASS_BINDING.put("int",      int.class);
        CLASS_BINDING.put("QName",    QName.class);
        CLASS_BINDING.put("anyURI",   URI.class);
        CLASS_BINDING.put("byte",     Byte.class);
        CLASS_BINDING.put("string",   String.class);
        CLASS_BINDING.put("decimal",  BigDecimal.class);
        CLASS_BINDING.put("short",    Short.class);
        CLASS_BINDING.put("boolean",  Boolean.class);
        CLASS_BINDING.put("dateTime", Timestamp.class);
        CLASS_BINDING.put("date",     Date.class);
        CLASS_BINDING.put("double",   Double.class);
        CLASS_BINDING.put("float",    Float.class);

        // GML geometry types
        CLASS_BINDING.put("GeometryPropertyType",          Geometry.class);
        CLASS_BINDING.put("MultiPoint",                    MultiPoint.class);
        CLASS_BINDING.put("MultiPointPropertyType",        MultiPoint.class);
        CLASS_BINDING.put("Point",                         Point.class);
        CLASS_BINDING.put("PointPropertyType",             Point.class);
        CLASS_BINDING.put("Curve",                         LineString.class);
        CLASS_BINDING.put("CurvePropertyType",             LineString.class);
        CLASS_BINDING.put("MultiGeometry",                 GeometryCollection.class);
        CLASS_BINDING.put("MultiGeometryPropertyType",     GeometryCollection.class);
        CLASS_BINDING.put("CompositeCurve",                MultiLineString.class);
        CLASS_BINDING.put("CompositeCurvePropertyType",    MultiLineString.class);
        CLASS_BINDING.put("MultiLineStringPropertyType",   MultiLineString.class);
        CLASS_BINDING.put("MultiLineString",               MultiLineString.class);
        CLASS_BINDING.put("Envelope",                      Envelope.class);
        CLASS_BINDING.put("EnvelopePropertyType",          Envelope.class);
        CLASS_BINDING.put("PolyHedralSurface",             MultiPolygon.class);
        CLASS_BINDING.put("PolyHedralSurfacePropertyType", MultiPolygon.class);
        CLASS_BINDING.put("MultiSurfacePropertyType",      MultiPolygon.class);
        CLASS_BINDING.put("MultiPolygonPropertyType",      MultiPolygon.class);
        CLASS_BINDING.put("Polygon",                       Polygon.class);
        CLASS_BINDING.put("PolygonPropertyType",           Polygon.class);
        CLASS_BINDING.put("Ring",                          LinearRing.class);
        CLASS_BINDING.put("RingPropertyType",              LinearRing.class);
        CLASS_BINDING.put("LinearRing",                    LinearRing.class);
        CLASS_BINDING.put("LinearRingPropertyType",        LinearRing.class);
    }
    /**
     * Return a primitive Class from the specified XML QName (extracted from an xsd file).
     *
     * @param name A XML QName.
     * @return A Class.
     */
    public static Class getTypeFromQName(QName name) {
        if (name != null) {
            final Class result = CLASS_BINDING.get(name.getLocalPart());
            if (result == null) {
                throw new IllegalArgumentException("unexpected type:" + name);
            }
            return result;
        }
        return null;
    }

    private static final Map<Class, QName> NAME_BINDING = new HashMap<Class, QName>();
    static {
       
        // Special case when we get a List or Map we return String => TODO
        NAME_BINDING.put(List.class,          new QName("http://www.w3.org/2001/XMLSchema", "string"));
        NAME_BINDING.put(Map.class,           new QName("http://www.w3.org/2001/XMLSchema", "string"));
        NAME_BINDING.put(String.class,        new QName("http://www.w3.org/2001/XMLSchema", "string"));
        NAME_BINDING.put(Long.class,          new QName("http://www.w3.org/2001/XMLSchema", "long"));
        NAME_BINDING.put(Integer.class,       new QName("http://www.w3.org/2001/XMLSchema", "integer"));
        NAME_BINDING.put(Double.class,        new QName("http://www.w3.org/2001/XMLSchema", "double"));
        NAME_BINDING.put(Date.class,          new QName("http://www.w3.org/2001/XMLSchema", "date"));
        NAME_BINDING.put(java.sql.Date.class, new QName("http://www.w3.org/2001/XMLSchema", "date"));
        NAME_BINDING.put(Timestamp.class,     new QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        NAME_BINDING.put(Boolean.class,       new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        NAME_BINDING.put(BigDecimal.class,    new QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        NAME_BINDING.put(Short.class,         new QName("http://www.w3.org/2001/XMLSchema", "short"));
        NAME_BINDING.put(int.class,           new QName("http://www.w3.org/2001/XMLSchema", "int"));
        NAME_BINDING.put(QName.class,         new QName("http://www.w3.org/2001/XMLSchema", "QName"));
        NAME_BINDING.put(URI.class,           new QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        NAME_BINDING.put(Byte.class,          new QName("http://www.w3.org/2001/XMLSchema", "byte"));

    }
    private static final Map<Class, QName> GEOMETRY_NAME_BINDING = new HashMap<Class, QName>();
    static {
       
        GEOMETRY_NAME_BINDING.put(MultiPoint.class,         new QName("http://www.opengis.net/gml", "MultiPoint"));
        GEOMETRY_NAME_BINDING.put(Point.class,              new QName("http://www.opengis.net/gml", "Point"));
        GEOMETRY_NAME_BINDING.put(LineString.class,         new QName("http://www.opengis.net/gml", "Curve"));
        GEOMETRY_NAME_BINDING.put(GeometryCollection.class, new QName("http://www.opengis.net/gml", "MultiGeometry"));
        GEOMETRY_NAME_BINDING.put(MultiLineString.class,    new QName("http://www.opengis.net/gml", "CompositeCurve"));
        GEOMETRY_NAME_BINDING.put(Envelope.class,           new QName("http://www.opengis.net/gml", "Envelope"));
        GEOMETRY_NAME_BINDING.put(MultiPolygon.class,       new QName("http://www.opengis.net/gml", "MultiGeometry"));
        GEOMETRY_NAME_BINDING.put(Polygon.class,            new QName("http://www.opengis.net/gml", "Polygon"));
        GEOMETRY_NAME_BINDING.put(LinearRing.class,         new QName("http://www.opengis.net/gml", "Ring"));
    }
    /**
     * Return a QName intended to be used in a xsd XML file fro mthe specified class.
     *
     * @param binding A prmitive type Class.
     * @return A QName describing the class.
     */
    public static QName getQNameFromType(Class binding) {
        if (binding != null) {
            final QName result;
             if (Geometry.class.isAssignableFrom(binding)) {
                result = GEOMETRY_NAME_BINDING.get(binding);
                if (result == null) {
                    return new QName("http://www.opengis.net/gml", "GeometryPropertyType");
                }
            } else {
                result = NAME_BINDING.get(binding);
            }
            if (result == null) {
                throw new IllegalArgumentException("unexpected type:" + binding);
            }
            return result;
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
            return timestampFormatter.format(new Date(((Timestamp)obj).getTime()));
        } else if (obj instanceof java.sql.Date) {
            // sql date does not have
            return  obj.toString() + 'Z';
        } else if (obj instanceof java.util.Date) {
            return dateFormatter.format((java.util.Date) obj);
        } else if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        } else if (obj != null) {
            LOGGER.log(Level.WARNING, "Unhandled type :" + obj.getClass(),new IllegalArgumentException());
        }
        return null;
    }

    /**
     * Return a List of QName from a Set Of Name.
     * @param typeNames
     * @return
     */
    public static List<QName> getQNameListFromNameSet(Collection<Name> typeNames) {
        final List<QName> result = new ArrayList<QName>(typeNames.size());
        for (Name typeName : typeNames) {
            result.add(Utils.getQnameFromName(typeName));
        }
        return result;
    }

    /**
     * Return a List of Name from a Set Of QName.
     * @param typeNames
     * @return
     */
    public static List<Name> getNameListFromQNameSet(Collection<QName> typeNames) {
        final List<Name> result = new ArrayList<Name>(typeNames.size());
        for (QName typeName : typeNames) {
            result.add(Utils.getNameFromQname(typeName));
        }
        return result;
    }
}
