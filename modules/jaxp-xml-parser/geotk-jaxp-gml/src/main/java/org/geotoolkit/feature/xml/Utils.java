/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2015, Geomatys
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
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;
import net.iharder.Base64;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.xsd.xml.v2001.Import;
import org.geotoolkit.xsd.xml.v2001.Include;
import org.geotoolkit.xsd.xml.v2001.Schema;
import org.geotoolkit.xsd.xml.v2001.XSDMarshallerPool;
import org.geotoolkit.feature.type.ComplexType;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.type.PropertyType;
import org.apache.sis.util.logging.Logging;

/**
 *
 * @module pending
 * @author Guilhem Legal (Geomatys)
 */
public class Utils {

    /**
     * This named is used for complex simple element to indicate the real node value opposed to attribute values
     * Tested cases :
     * "",".","$value",":value","#value" conflict with xpath qname constraint
     */
    public static final String VALUE_PROPERTY_NAME = "_value";
    /**
     * This named is used for element of type xsd:any.
     */
    public static final String ANY_PROPERTY_NAME = "_any";

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.feature.xml");

    private static final DateFormat timestampFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'Z'");

    private static final String GML_311_NAMESPACE = "http://www.opengis.net/gml";
    private static final String GML_321_NAMESPACE = "http://www.opengis.net/gml/3.2";
    public static final Set<GenericName> GML_FEATURE_TYPES;
    public static final Set<GenericName> GML_STANDARD_OBJECT_PROPERTIES;
    public static final Set<GenericName> GML_ABSTRACT_FEATURE_PROPERTIES;
    static{
        timestampFormatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));

        GML_FEATURE_TYPES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new GenericName[]{
            //3.1.1
            //3.1.1
        NamesExt.create(GML_311_NAMESPACE, "AbstractFeatureType"), NamesExt.create(GML_311_NAMESPACE, "AbstractFeatureCollection"), NamesExt.create(GML_311_NAMESPACE, "FeatureCollection"), NamesExt.create(GML_311_NAMESPACE, "AbstractCoverage"), NamesExt.create(GML_311_NAMESPACE, "AbstractContinuousCoverage"), NamesExt.create(GML_311_NAMESPACE, "AbstractDiscreteCoverage"), NamesExt.create(GML_311_NAMESPACE, "Observation"), NamesExt.create(GML_311_NAMESPACE, "DirectedObservation"), NamesExt.create(GML_311_NAMESPACE, "DirectedObservationAtDistance"), NamesExt.create(GML_311_NAMESPACE, "MultiPointCoverage"), NamesExt.create(GML_311_NAMESPACE, "MultiCurveCoverage"), NamesExt.create(GML_311_NAMESPACE, "MultiSurfaceCoverage"), NamesExt.create(GML_311_NAMESPACE, "MultiSolidCoverage"), NamesExt.create(GML_311_NAMESPACE, "GridCoverage"), NamesExt.create(GML_311_NAMESPACE, "_FeatureCollection"), NamesExt.create(GML_311_NAMESPACE, "_Coverage"), NamesExt.create(GML_311_NAMESPACE, "_ContinuousCoverage"), NamesExt.create(GML_311_NAMESPACE, "_DiscreteCoverage"), //3.2.1
NamesExt.create(GML_321_NAMESPACE, "AbstractFeatureType"), NamesExt.create(GML_321_NAMESPACE, "AbstractFeatureCollection"), NamesExt.create(GML_321_NAMESPACE, "FeatureCollection"), NamesExt.create(GML_321_NAMESPACE, "AbstractCoverage"), NamesExt.create(GML_321_NAMESPACE, "AbstractContinuousCoverage"), NamesExt.create(GML_321_NAMESPACE, "AbstractDiscreteCoverage"), NamesExt.create(GML_321_NAMESPACE, "Observation"), NamesExt.create(GML_321_NAMESPACE, "DirectedObservation"), NamesExt.create(GML_321_NAMESPACE, "DirectedObservationAtDistance"), NamesExt.create(GML_321_NAMESPACE, "DynamicFeatureCollection"), NamesExt.create(GML_321_NAMESPACE, "DynamicFeature"), NamesExt.create(GML_321_NAMESPACE, "DiscreteCoverage"), NamesExt.create(GML_321_NAMESPACE, "MultiPointCoverage"), NamesExt.create(GML_321_NAMESPACE, "MultiCurveCoverage"), NamesExt.create(GML_321_NAMESPACE, "MultiSurfaceCoverage"), NamesExt.create(GML_321_NAMESPACE, "MultiSolidCoverage"), NamesExt.create(GML_321_NAMESPACE, "GridCoverage"), NamesExt.create(GML_321_NAMESPACE, "RectifiedGridCoverage")
        })));

        GML_STANDARD_OBJECT_PROPERTIES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new GenericName[]{
            //3.1.1
            //3.1.1
        NamesExt.create(GML_311_NAMESPACE, "metaDataProperty"), NamesExt.create(GML_311_NAMESPACE, "description"), NamesExt.create(GML_311_NAMESPACE, "name"), NamesExt.create(GML_311_NAMESPACE, "csName"), //substitution group of name
NamesExt.create(GML_311_NAMESPACE, "srsName"), //substitution group of name
NamesExt.create(GML_311_NAMESPACE, "datumName"), //substitution group of name
NamesExt.create(GML_311_NAMESPACE, "meridianName"), //substitution group of name
NamesExt.create(GML_311_NAMESPACE, "ellipsoidName"), //substitution group of name
NamesExt.create(GML_311_NAMESPACE, "coordinateOperationName"), //substitution group of name
NamesExt.create(GML_311_NAMESPACE, "methodName"), //substitution group of name
NamesExt.create(GML_311_NAMESPACE, "parameterName"), //substitution group of name
NamesExt.create(GML_311_NAMESPACE, "groupName"), //substitution group of name
        //3.2.1
NamesExt.create(GML_321_NAMESPACE, "metaDataProperty"), NamesExt.create(GML_321_NAMESPACE, "description"), NamesExt.create(GML_321_NAMESPACE, "descriptionReference"), NamesExt.create(GML_321_NAMESPACE, "name"), NamesExt.create(GML_321_NAMESPACE, "csName"), //substitution group of name
NamesExt.create(GML_321_NAMESPACE, "srsName"), //substitution group of name
NamesExt.create(GML_321_NAMESPACE, "datumName"), //substitution group of name
NamesExt.create(GML_321_NAMESPACE, "meridianName"), //substitution group of name
NamesExt.create(GML_321_NAMESPACE, "ellipsoidName"), //substitution group of name
NamesExt.create(GML_321_NAMESPACE, "coordinateOperationName"), //substitution group of name
NamesExt.create(GML_321_NAMESPACE, "methodName"), //substitution group of name
NamesExt.create(GML_321_NAMESPACE, "parameterName"), //substitution group of name
NamesExt.create(GML_321_NAMESPACE, "groupName"), //substitution group of name
NamesExt.create(GML_321_NAMESPACE, "identifier")})));

        GML_ABSTRACT_FEATURE_PROPERTIES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new GenericName[]{
            //3.1.1
            //3.1.1
        NamesExt.create(GML_311_NAMESPACE, "@id"), NamesExt.create(GML_311_NAMESPACE, "boundedBy"), NamesExt.create(GML_311_NAMESPACE, "location"), NamesExt.create(GML_311_NAMESPACE, "priorityLocation"), //substitution group of location
        //3.2.1
NamesExt.create(GML_321_NAMESPACE, "@id"), NamesExt.create(GML_321_NAMESPACE, "boundedBy"), NamesExt.create(GML_321_NAMESPACE, "location"), NamesExt.create(GML_321_NAMESPACE, "priorityLocation") //substitution group of location
        })));

    }

    private Utils() {}

    /**
     * Return a Name from a QName.
     *
     * @param qname a XML QName.
     * @return a Types Name.
     */
    public static GenericName getNameFromQname(final QName qname) {
        GenericName name;
        if (qname.getNamespaceURI() == null || qname.getNamespaceURI().isEmpty()) {
            name = NamesExt.create(qname.getLocalPart());
        } else {
            name = NamesExt.create(qname);
        }
        return name;
    }

    /**
     * Return A QName from a Name.
     *
     * @param name a Types name.
     * @return A XML QName.
     */
    public static QName getQnameFromName(final GenericName name) {
        QName qname;
        final String ns = NamesExt.getNamespace(name);
        if (ns == null || ns.isEmpty()) {
            qname = new QName(name.tip().toString());
        } else {
            qname = new QName(ns, name.tip().toString());
        }
        return qname;
    }

    private static final Map<String, Class> CLASS_BINDING = new HashMap<>();
    private static final Set<String> GEOMETRIC_NAME = new HashSet<>();
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
        CLASS_BINDING.put("base64Binary",byte[].class);
        CLASS_BINDING.put("language", String.class);
        CLASS_BINDING.put("IDREF",    String.class);
        CLASS_BINDING.put("normalizedString",String.class); //TODO String value of the xml should be trimmed
        CLASS_BINDING.put("NMTOKEN",  String.class);

        CLASS_BINDING.put("nonNegativeInteger", Integer.class);
        CLASS_BINDING.put("positiveInteger",    Integer.class);
        CLASS_BINDING.put("integerList",        Integer.class);
        CLASS_BINDING.put("time",               Date.class);
        CLASS_BINDING.put("duration",           String.class);
        CLASS_BINDING.put("CalDate",            String.class); //TODO should be date
        CLASS_BINDING.put("anyType",            String.class);
        CLASS_BINDING.put("ID",                 String.class);
        CLASS_BINDING.put("StringOrRefType",    String.class);
        CLASS_BINDING.put("token",              String.class);
        CLASS_BINDING.put("NCName",             String.class);
        CLASS_BINDING.put("TimePositionUnion",  String.class);
        CLASS_BINDING.put("Name",               String.class);


        // GML geometry types
        CLASS_BINDING.put("AbstractGeometry",              Geometry.class);
        CLASS_BINDING.put("AbstractGeometryType",          Geometry.class);
        CLASS_BINDING.put("AbstractGeometryTypeCollection",Geometry.class);
        CLASS_BINDING.put("GeometryPropertyType",          Geometry.class);
        CLASS_BINDING.put("MultiPoint",                    MultiPoint.class);
        CLASS_BINDING.put("MultiPointType",                MultiPoint.class);
        CLASS_BINDING.put("MultiPointPropertyType",        MultiPoint.class);
        CLASS_BINDING.put("Point",                         Point.class);
        CLASS_BINDING.put("PointType",                     Point.class);
        CLASS_BINDING.put("PointPropertyType",             Point.class);
        CLASS_BINDING.put("Curve",                         LineString.class);
        CLASS_BINDING.put("CurveType",                     LineString.class);
        CLASS_BINDING.put("CurvePropertyType",             LineString.class);
        CLASS_BINDING.put("MultiGeometry",                 GeometryCollection.class);
        CLASS_BINDING.put("MultiGeometryType",             GeometryCollection.class);
        CLASS_BINDING.put("MultiGeometryPropertyType",     GeometryCollection.class);
        CLASS_BINDING.put("CompositeCurve",                MultiLineString.class);
        CLASS_BINDING.put("CompositeCurveType",            MultiLineString.class);
        CLASS_BINDING.put("CompositeCurvePropertyType",    MultiLineString.class);
        CLASS_BINDING.put("MultiLineString",               MultiLineString.class);
        CLASS_BINDING.put("MultiLineStringType",           MultiLineString.class);
        CLASS_BINDING.put("MultiLineStringPropertyType",   MultiLineString.class);
        CLASS_BINDING.put("MultiCurve",                    MultiLineString.class);
        CLASS_BINDING.put("MultiCurveType",                MultiLineString.class);
        CLASS_BINDING.put("MultiCurvePropertyType",        MultiLineString.class);
        CLASS_BINDING.put("Envelope",                      Envelope.class);
        CLASS_BINDING.put("EnvelopeType",                  Envelope.class);
        CLASS_BINDING.put("EnvelopePropertyType",          Envelope.class);
        CLASS_BINDING.put("PolyHedralSurface",             MultiPolygon.class);
        CLASS_BINDING.put("PolyHedralSurfaceType",         MultiPolygon.class);
        CLASS_BINDING.put("PolyHedralSurfacePropertyType", MultiPolygon.class);
        CLASS_BINDING.put("MultiSurfacePropertyType",      MultiPolygon.class);
        CLASS_BINDING.put("MultiPolygon",                  MultiPolygon.class);
        CLASS_BINDING.put("MultiPolygonType",              MultiPolygon.class);
        CLASS_BINDING.put("MultiPolygonPropertyType",      MultiPolygon.class);
        CLASS_BINDING.put("SurfaceType",                   Polygon.class);
        CLASS_BINDING.put("SurfacePropertyType",           Polygon.class);
        CLASS_BINDING.put("Polygon",                       Polygon.class);
        CLASS_BINDING.put("PolygonType",                   Polygon.class);
        CLASS_BINDING.put("PolygonPropertyType",           Polygon.class);
        CLASS_BINDING.put("Ring",                          LinearRing.class);
        CLASS_BINDING.put("RingType",                      LinearRing.class);
        CLASS_BINDING.put("RingPropertyType",              LinearRing.class);
        CLASS_BINDING.put("LinearRing",                    LinearRing.class);
        CLASS_BINDING.put("LinearRingType",                LinearRing.class);
        CLASS_BINDING.put("LinearRingPropertyType",        LinearRing.class);

        for(Entry<String,Class> entry : CLASS_BINDING.entrySet()){
            if(Geometry.class.isAssignableFrom(entry.getValue())){
                GEOMETRIC_NAME.add(entry.getKey());
            }
        }
    }

    public static boolean isGeometricType(final QName elementType) {
        if (elementType != null && elementType.getNamespaceURI().contains(GML_311_NAMESPACE)) {
            return GEOMETRIC_NAME.contains(elementType.getLocalPart());
        }
        return false;
    }

    public static boolean isGeometricType(final GenericName elementType) {
        if (elementType != null && NamesExt.getNamespace(elementType)!=null && NamesExt.getNamespace(elementType).contains(GML_311_NAMESPACE)) {
            return GEOMETRIC_NAME.contains(elementType.tip().toString());
        }
        return false;
    }

    public static boolean isPrimitiveType(final QName elementType) {
        if (elementType != null) {
            return CLASS_BINDING.containsKey(elementType.getLocalPart());
        }
        return false;
    }

    /**
     * Return a primitive Class from the specified XML QName (extracted from an xsd file).
     *
     * @param name A XML QName.
     * @return A Class.
     */
    public static Class getTypeFromQName(final QName name) {
        if (name != null) {
            final Class result = CLASS_BINDING.get(name.getLocalPart());
            if (result == null) {
                throw new IllegalArgumentException("unexpected type:" + name);
            }
            return result;
        }
        return null;
    }

    /**
     * Return a primitive Class from the specified XML QName (extracted from an xsd file).
     *
     * @param name A XML QName.
     * @return A Class.
     */
    public static boolean existPrimitiveType(final String name) {
        if (name != null) {
            return CLASS_BINDING.get(name) != null;
        }
        return false;
    }

    private static final Map<Class, QName> NAME_BINDING = new HashMap<Class, QName>();
    static {

        // Special case when we get a Collection or Map we return String => TODO
         NAME_BINDING.put(List.class,          new QName("http://www.w3.org/2001/XMLSchema", "string"));
         NAME_BINDING.put(Map.class,           new QName("http://www.w3.org/2001/XMLSchema", "string"));
         NAME_BINDING.put(Collection.class,    new QName("http://www.w3.org/2001/XMLSchema", "string"));

         NAME_BINDING.put(String.class,        new QName("http://www.w3.org/2001/XMLSchema", "string"));
         NAME_BINDING.put(String[].class,      new QName("http://www.w3.org/2001/XMLSchema", "string"));
         NAME_BINDING.put(Float.class,         new QName("http://www.w3.org/2001/XMLSchema", "float"));
         NAME_BINDING.put(Float[].class,       new QName("http://www.w3.org/2001/XMLSchema", "float"));
         NAME_BINDING.put(Long.class,          new QName("http://www.w3.org/2001/XMLSchema", "long"));
         NAME_BINDING.put(Long[].class,        new QName("http://www.w3.org/2001/XMLSchema", "long"));
         NAME_BINDING.put(Integer.class,       new QName("http://www.w3.org/2001/XMLSchema", "integer"));
         NAME_BINDING.put(Integer[].class,     new QName("http://www.w3.org/2001/XMLSchema", "integer"));
         NAME_BINDING.put(Double.class,        new QName("http://www.w3.org/2001/XMLSchema", "double"));
         NAME_BINDING.put(Double[].class,      new QName("http://www.w3.org/2001/XMLSchema", "double"));
         NAME_BINDING.put(Date.class,          new QName("http://www.w3.org/2001/XMLSchema", "date"));
         NAME_BINDING.put(Date[].class,        new QName("http://www.w3.org/2001/XMLSchema", "date"));
         NAME_BINDING.put(java.sql.Date.class, new QName("http://www.w3.org/2001/XMLSchema", "date"));
         NAME_BINDING.put(java.sql.Date[].class, new QName("http://www.w3.org/2001/XMLSchema", "date"));
         NAME_BINDING.put(Timestamp.class,     new QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
         NAME_BINDING.put(Timestamp[].class,   new QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
         NAME_BINDING.put(Boolean.class,       new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
         NAME_BINDING.put(Boolean[].class,     new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
         NAME_BINDING.put(BigDecimal.class,    new QName("http://www.w3.org/2001/XMLSchema", "decimal"));
         NAME_BINDING.put(BigDecimal[].class,  new QName("http://www.w3.org/2001/XMLSchema", "decimal"));
         NAME_BINDING.put(Short.class,         new QName("http://www.w3.org/2001/XMLSchema", "short"));
         NAME_BINDING.put(Short[].class,       new QName("http://www.w3.org/2001/XMLSchema", "short"));
         NAME_BINDING.put(int.class,           new QName("http://www.w3.org/2001/XMLSchema", "int"));
         NAME_BINDING.put(int[].class,         new QName("http://www.w3.org/2001/XMLSchema", "int"));
         NAME_BINDING.put(QName.class,         new QName("http://www.w3.org/2001/XMLSchema", "QName"));
         NAME_BINDING.put(QName[].class,       new QName("http://www.w3.org/2001/XMLSchema", "QName"));
         NAME_BINDING.put(URI.class,           new QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
         NAME_BINDING.put(URI[].class,         new QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
         NAME_BINDING.put(URL.class,           new QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
         NAME_BINDING.put(Byte.class,          new QName("http://www.w3.org/2001/XMLSchema", "byte"));
         NAME_BINDING.put(byte[].class,        new QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));

    }

    private static final Map<Class, QName> GEOMETRY_NAME_BINDING_311 = new HashMap<Class, QName>();
    static {

        GEOMETRY_NAME_BINDING_311.put(MultiPoint.class,         new QName(GML_311_NAMESPACE, "MultiPoint"));
        GEOMETRY_NAME_BINDING_311.put(Point.class,              new QName(GML_311_NAMESPACE, "Point"));
        GEOMETRY_NAME_BINDING_311.put(LineString.class,         new QName(GML_311_NAMESPACE, "Curve"));
        GEOMETRY_NAME_BINDING_311.put(GeometryCollection.class, new QName(GML_311_NAMESPACE, "MultiGeometry"));
        GEOMETRY_NAME_BINDING_311.put(MultiLineString.class,    new QName(GML_311_NAMESPACE, "CompositeCurve"));
        GEOMETRY_NAME_BINDING_311.put(Envelope.class,           new QName(GML_311_NAMESPACE, "Envelope"));
        GEOMETRY_NAME_BINDING_311.put(MultiPolygon.class,       new QName(GML_311_NAMESPACE, "MultiPolygon"));
        GEOMETRY_NAME_BINDING_311.put(Polygon.class,            new QName(GML_311_NAMESPACE, "Polygon"));
        GEOMETRY_NAME_BINDING_311.put(LinearRing.class,         new QName(GML_311_NAMESPACE, "Ring"));
    }

    private static final Map<Class, QName> GEOMETRY_NAME_BINDING_321 = new HashMap<Class, QName>();
    static {

        GEOMETRY_NAME_BINDING_321.put(MultiPoint.class,         new QName(GML_321_NAMESPACE, "MultiPointType"));
        GEOMETRY_NAME_BINDING_321.put(Point.class,              new QName(GML_321_NAMESPACE, "PointType"));
        GEOMETRY_NAME_BINDING_321.put(LineString.class,         new QName(GML_321_NAMESPACE, "CurveType"));
        GEOMETRY_NAME_BINDING_321.put(GeometryCollection.class, new QName(GML_321_NAMESPACE, "MultiGeometryType"));
        GEOMETRY_NAME_BINDING_321.put(MultiLineString.class,    new QName(GML_321_NAMESPACE, "CompositeCurveType"));
        GEOMETRY_NAME_BINDING_321.put(Envelope.class,           new QName(GML_321_NAMESPACE, "EnvelopeTypr"));
        GEOMETRY_NAME_BINDING_321.put(MultiPolygon.class,       new QName(GML_321_NAMESPACE, "MultiPolygonType"));
        GEOMETRY_NAME_BINDING_321.put(Polygon.class,            new QName(GML_321_NAMESPACE, "PolygonType"));
        GEOMETRY_NAME_BINDING_321.put(LinearRing.class,         new QName(GML_321_NAMESPACE, "RingType"));
    }
    /**
     * Return a QName intended to be used in a xsd XML file fro mthe specified class.
     *
     * @param type A prmitive type Class.
     * @param gmlVersion
     * @return A QName describing the class.
     */
    public static QName getQNameFromType(final PropertyType type, final String gmlVersion) {
        if (type instanceof ComplexType) {
            return new QName(NamesExt.getNamespace(type.getName()), getNameWithTypeSuffix(type.getName().tip().toString()));
        } else {
            final Class binding = type.getBinding();
            if (binding != null) {
                final QName result;
                if (Geometry.class.isAssignableFrom(binding)) {
                    if ("3.2.1".equals(gmlVersion)) {
                        result = GEOMETRY_NAME_BINDING_321.get(binding);
                    } else {
                        result = GEOMETRY_NAME_BINDING_311.get(binding);
                    }
                    if (result == null) {
                        if ("3.2.1".equals(gmlVersion)) {
                            return new QName(GML_321_NAMESPACE, "GeometryPropertyType");
                        } else {
                            return new QName(GML_311_NAMESPACE, "GeometryPropertyType");
                        }
                    }
                // maybe we can find a better way to handle Enum. for now we set a String value
                } else if (binding.isEnum()){
                    result = new QName("http://www.w3.org/2001/XMLSchema", "string");

                } else if (binding.equals(Object.class)) {
                  if ("3.2.1".equals(gmlVersion)) {
                        result = new QName(GML_321_NAMESPACE, "AbstractObject");
                    } else {
                        result = new QName(GML_311_NAMESPACE, "_Object");
                    }
                } else {
                    result = NAME_BINDING.get(binding);
                }
                if (result == null) {
                    throw new IllegalArgumentException("unexpected type:" + binding);
                }
                return result;
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
    public static String getEventTypeString(final int eventType) {
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
    public static String getStringValue(final Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Timestamp) {
            return timestampFormatter.format(new Date(((Timestamp)obj).getTime()));
        } else if (obj instanceof java.sql.Date) {
            // sql date does not have
            return  obj.toString() + 'Z';
        } else if (obj instanceof java.util.Date) {
            return dateFormatter.format((java.util.Date) obj);
        } else if (obj instanceof Number || obj instanceof Boolean || obj instanceof URI) {
            return obj.toString();
        } else if (obj instanceof byte[]) {
            return Base64.encodeBytes((byte[])obj);
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
    public static List<QName> getQNameListFromNameSet(final Collection<GenericName> typeNames) {
        final List<QName> result = new ArrayList<QName>(typeNames.size());
        for (GenericName typeName : typeNames) {
            result.add(Utils.getQnameFromName(typeName));
        }
        return result;
    }

    /**
     * Return a List of Name from a Set Of QName.
     * @param typeNames
     * @return
     */
    public static List<GenericName> getNameListFromQNameSet(final Collection<QName> typeNames) {
        final List<GenericName> result = new ArrayList<GenericName>(typeNames.size());
        for (QName typeName : typeNames) {
            result.add(Utils.getNameFromQname(typeName));
        }
        return result;
    }

    public static URL resolveURL(URL base, String location) throws MalformedURLException, URISyntaxException{
        //try an url
        if (location.startsWith("http://") || location.startsWith("https://")) {
            return new URL(location);
        }

        //try to file
        File f = new File(location);
        if(f.exists()){
            return f.toURI().toURL();
        }

        //try a jar resource
        final URL jarUrl = Utils.class.getResource(location);
        if(jarUrl!=null){
            return jarUrl;
        }

        //try to resolve a relative path
        if(base!=null){
            f = new File(base.toURI());
            if(f.exists()){
                if(f.isFile()){
                    f = f.getParentFile();
                }
                f = new File(f, location);
                if(f.exists()){
                    return f.toURI().toURL();
                }
            }
        }

        throw new MalformedURLException("Could not resolve xsd location : "+location);
    }

    /**
     * Retrieve an XSD schema from a http location
     * @param location
     * @return
     */
    public static Schema getDistantSchema(final String location) {

        URL schemaUrl = null;
        try {
            //search in the jar files if we have it
            if(location.startsWith("http://schemas.opengis.net/")){
                String localUrl = location.replace("http://schemas.opengis.net/", "/org/geotoolkit/xsd/");
                schemaUrl = Utils.class.getResource(localUrl);
            }

            if(schemaUrl==null){
                schemaUrl = resolveURL(null, location);
            }
        } catch (MalformedURLException | URISyntaxException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            return null;
        }

        try {
            LOGGER.log(Level.FINE, "retrieving:{0}", location);
            final Unmarshaller u = XSDMarshallerPool.getInstance().acquireUnmarshaller();
            final Object obj = u.unmarshal(schemaUrl.openStream());
            XSDMarshallerPool.getInstance().recycle(u);
            if (obj instanceof Schema) {
                return (Schema) obj;
            } else {
                LOGGER.log(Level.WARNING, "Bad content for imported schema:{0}", location);
            }

        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "IO exception trying to retrieve imported schema:" + location, ex);
        } catch (JAXBException ex) {
            LOGGER.log(Level.WARNING, "JAXB exception while reading imported schema:" + location, ex);
        }

        LOGGER.log(Level.WARNING, "Schema ressource not found:" + location);
        return null;
    }

    /**
     * Return the location a the Import/include element.
     *
     * if the location is local, the base location will be added to the result.
     *
     * @param baseLocation
     * @param attr
     * @return
     */
    public static String getIncludedLocation(final String baseLocation, final Object attr) {
        final String schemaLocation;
        if (attr instanceof Import) {
             schemaLocation = ((Import)attr).getSchemaLocation();
         } else if (attr instanceof Include) {
             schemaLocation = ((Include)attr).getSchemaLocation();
         } else {
             return null;
         }
         if (schemaLocation != null  && baseLocation != null && baseLocation.startsWith("http://") && !schemaLocation.startsWith("http://")) {
             if(schemaLocation.startsWith("./")){
                 return baseLocation + schemaLocation.substring(2);
             }else{
                return baseLocation + schemaLocation;
             }
        } else {
            return schemaLocation;
        }
    }

    public static String getNameWithTypeSuffix(String name){
        if(name.endsWith("Type")){
            return name;
        }else{
            return name+="Type";
        }
    }

    public static String getNameWithoutTypeSuffix(String name){
        if(name.endsWith("Type")){
            return name.substring(0,name.length()-4);
        }else{
            return name;
        }
    }

    public static Set<String> listAllSubNamespaces(PropertyType type, String namespace){
        final Set<String> ns = new HashSet<>();
        final Set<GenericName> visited = new HashSet<>();
        listAllSubNamespaces(type, ns, visited, namespace);
        return ns;
    }

    private static void listAllSubNamespaces(PropertyType type, Set<String> ns, Set<GenericName> visited, final String namespace){
        final GenericName name = type.getName();
        if(visited.contains(name)){
            //avoid cyclic loops
            return;
        }
        visited.add(name);
        String typeUri = NamesExt.getNamespace(type.getName());
        //if(nsuri!=null) ns.add(nsuri);

        if(type instanceof ComplexType){
            final ComplexType ct = (ComplexType) type;
            for(PropertyDescriptor pd : ct.getDescriptors()){
                if (typeUri.equals(namespace)) {
                    String nsuri = NamesExt.getNamespace(pd.getName());
                    if(nsuri!=null) ns.add(nsuri);
                }
                listAllSubNamespaces(pd.getType(), ns, visited, namespace);
            }
        }
    }

    public static Set<String> listAllNamespaces(PropertyType type){
        final Set<String> ns = new HashSet<>();
        final Set<GenericName> visited = new HashSet<>();
        listAllNamespaces(type, ns, visited);
        return ns;
    }

    private static void listAllNamespaces(PropertyType type, Set<String> ns, Set<GenericName> visited){
        final GenericName name = type.getName();
        if(visited.contains(name)){
            //avoid cyclic loops
            return;
        }
        visited.add(name);
        String nsuri = NamesExt.getNamespace(type.getName());
        if(nsuri!=null) ns.add(nsuri);

        if(type instanceof ComplexType){
            final ComplexType ct = (ComplexType) type;
            for(PropertyDescriptor pd : ct.getDescriptors()){
                nsuri = NamesExt.getNamespace(pd.getName());
                if(nsuri!=null) ns.add(nsuri);
                listAllNamespaces(pd.getType(), ns, visited);
            }
        }
    }

}
