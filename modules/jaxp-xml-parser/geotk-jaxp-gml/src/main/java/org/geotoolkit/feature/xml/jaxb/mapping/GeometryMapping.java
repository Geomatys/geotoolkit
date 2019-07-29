/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.feature.xml.jaxb.mapping;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.feature.xml.GMLConvention;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import static org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader.BINDING_PACKAGE;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiCurve;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.geotoolkit.internal.jaxb.JTSWrapperMarshallerPool;
import org.geotoolkit.internal.jaxb.LineStringPosListType;
import org.geotoolkit.internal.jaxb.PolygonType;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.xsd.xml.v2001.Annotated;
import org.geotoolkit.xsd.xml.v2001.ComplexType;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.PropertyType;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeometryMapping implements XSDMapping {

    private static Map<String,Class> CLASS_BINDING = new HashMap<>();
    static {

        CLASS_BINDING.put("_Geometry",                     Geometry.class);
        CLASS_BINDING.put("AbstractGeometry",              Geometry.class);
        CLASS_BINDING.put("AbstractGeometryType",          Geometry.class);
        CLASS_BINDING.put("AbstractGeometryTypeCollection",Geometry.class);
        CLASS_BINDING.put("MultiPoint",                    MultiPoint.class);
        CLASS_BINDING.put("MultiPointType",                MultiPoint.class);
        CLASS_BINDING.put("Point",                         Point.class);
        CLASS_BINDING.put("PointType",                     Point.class);
        CLASS_BINDING.put("Curve",                         LineString.class);
        CLASS_BINDING.put("CurveType",                     LineString.class);
        CLASS_BINDING.put("AbstractCurveType",             LineString.class);
        CLASS_BINDING.put("LineStringType",                LineString.class);
        CLASS_BINDING.put("MultiGeometry",                 GeometryCollection.class);
        CLASS_BINDING.put("MultiGeometryType",             GeometryCollection.class);
        CLASS_BINDING.put("CompositeCurve",                MultiLineString.class);
        CLASS_BINDING.put("CompositeCurveType",            MultiLineString.class);
        CLASS_BINDING.put("MultiLineString",               MultiLineString.class);
        CLASS_BINDING.put("MultiLineStringType",           MultiLineString.class);
        CLASS_BINDING.put("MultiCurve",                    MultiLineString.class);
        CLASS_BINDING.put("MultiCurveType",                MultiLineString.class);
        CLASS_BINDING.put("Envelope",                      Envelope.class);
        CLASS_BINDING.put("EnvelopeType",                  Envelope.class);
        CLASS_BINDING.put("PolyHedralSurface",             MultiPolygon.class);
        CLASS_BINDING.put("PolyHedralSurfaceType",         MultiPolygon.class);
        CLASS_BINDING.put("MultiPolygon",                  MultiPolygon.class);
        CLASS_BINDING.put("MultiPolygonType",              MultiPolygon.class);
        CLASS_BINDING.put("SurfaceType",                   Polygon.class);
        CLASS_BINDING.put("Polygon",                       Polygon.class);
        CLASS_BINDING.put("PolygonType",                   Polygon.class);
        CLASS_BINDING.put("Ring",                          LinearRing.class);
        CLASS_BINDING.put("RingType",                      LinearRing.class);
        CLASS_BINDING.put("LinearRing",                    LinearRing.class);
        CLASS_BINDING.put("LinearRingType",                LinearRing.class);

        //GML 2.1.2
        CLASS_BINDING.put("LineStringPropertyType",        LineString.class);

        //GML 3+
        CLASS_BINDING.put("GeometryPropertyType",          Geometry.class);
        CLASS_BINDING.put("MultiPointPropertyType",        MultiPoint.class);
        CLASS_BINDING.put("PointPropertyType",             Point.class);
        CLASS_BINDING.put("CurvePropertyType",             MultiLineString.class);
        CLASS_BINDING.put("MultiGeometryPropertyType",     GeometryCollection.class);
        CLASS_BINDING.put("CompositeCurvePropertyType",    MultiLineString.class);
        CLASS_BINDING.put("MultiLineStringPropertyType",   MultiLineString.class);
        CLASS_BINDING.put("MultiCurvePropertyType",        MultiLineString.class);
        CLASS_BINDING.put("EnvelopePropertyType",          Envelope.class);
        CLASS_BINDING.put("PolyHedralSurfacePropertyType", MultiPolygon.class);
        CLASS_BINDING.put("MultiSurfacePropertyType",      MultiPolygon.class);
        CLASS_BINDING.put("MultiPolygonPropertyType",      MultiPolygon.class);
        CLASS_BINDING.put("SurfacePropertyType",           Polygon.class);
        CLASS_BINDING.put("PolygonPropertyType",           Polygon.class);
        CLASS_BINDING.put("RingPropertyType",              LinearRing.class);
        CLASS_BINDING.put("LinearRingPropertyType",        LinearRing.class);
    }

    private final ComplexType xsdType;
    private final PropertyType propertyType;
    private final boolean longitudeFirst;
    private final MarshallerPool pool;
    private final boolean decorated;

    public GeometryMapping(ComplexType xsdType, PropertyType propertyType, MarshallerPool pool, boolean longitudeFirst, boolean decorated) {
        this.xsdType = xsdType;
        this.propertyType = propertyType;
        this.pool = pool;
        this.longitudeFirst = longitudeFirst;
        this.decorated = decorated;
    }

    @Override
    public IdentifiedType getType() {
        return propertyType;
    }

    public boolean isDecorated() {
        return decorated;
    }

    @Override
    public void readValue(XMLStreamReader reader, GenericName propName, Feature feature) throws XMLStreamException {

        final String localName = reader.getLocalName();

        if (decorated) {
            //check if we are dealing with a link href
            String link = reader.getAttributeValue(GMLConvention.XLINK_NAMESPACE, "href");
            if (link != null) {
                toTagEnd(reader, localName);
                Attribute attribute = (Attribute) feature.getProperty(propName.toString());
                AttributeType<String> charType = (AttributeType) attribute.getType().characteristics().get(GMLConvention.XLINK_HREF.tip().toString());
                Attribute<String> charValue = charType.newInstance();
                charValue.setValue(link);
                attribute.characteristics().put(GMLConvention.XLINK_HREF.tip().toString(), charValue);
                return;
            }
        }

        boolean skipCurrent = decorated;
        int event;
        Object value;

        //backward compatible with incorrect old writings
        final String propertyName = propertyType.getName().tip().toString();
        if (propertyName.equals(localName)) {
            skipCurrent = true;
        }

        if (skipCurrent) {
            event = reader.next();
        } else {
            event = reader.getEventType();
        }
        while (event != START_ELEMENT) {
            if (event == END_ELEMENT) {
                return;
            }
            event = reader.next();
        }

        try {
            Unmarshaller unmarshaller = pool.acquireUnmarshaller();
            final Geometry jtsGeom;
            final Object geometry = ((JAXBElement) unmarshaller.unmarshal(reader)).getValue();
            if (geometry instanceof JTSGeometry) {
                final JTSGeometry isoGeom = (JTSGeometry) geometry;
                if (isoGeom instanceof JTSMultiCurve) {
                    ((JTSMultiCurve)isoGeom).applyCRSonChild();
                }
                jtsGeom = isoGeom.getJTSGeometry();
            } else if (geometry instanceof PolygonType) {
                final PolygonType polygon = ((PolygonType)geometry);
                jtsGeom = polygon.getJTSPolygon().getJTSGeometry();
                if (polygon.getCoordinateReferenceSystem() != null) {
                    JTS.setCRS(jtsGeom, polygon.getCoordinateReferenceSystem());
                }
            } else if (geometry instanceof LineStringPosListType) {
                final JTSLineString line = ((LineStringPosListType)geometry).getJTSLineString();
                jtsGeom = line.getJTSGeometry();
                if (line.getCoordinateReferenceSystem() != null) {
                    JTS.setCRS(jtsGeom, line.getCoordinateReferenceSystem());
                }
            } else if (geometry instanceof AbstractGeometry) {
                try {
                    jtsGeom = GeometrytoJTS.toJTS((AbstractGeometry) geometry, longitudeFirst);
                } catch (FactoryException ex) {
                    throw new XMLStreamException("Factory Exception while transforming GML object to JTS", ex);
                }
            } else {
                throw new IllegalArgumentException("unexpected geometry type:" + geometry);
            }
            value = jtsGeom;

            pool.recycle(unmarshaller);
        } catch (JAXBException ex) {
            String msg = ex.getMessage();
            if (msg == null && ex.getLinkedException() != null) {
                msg = ex.getLinkedException().getMessage();
            }
            throw new IllegalArgumentException("JAXB exception while reading the feature geometry: " + msg, ex);
        }

        JAXPStreamFeatureReader.setValue(feature, propertyType, propName, null, value);
    }

    @Override
    public void writeValue(XMLStreamWriter writer, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Iterator on the reader until it reaches the end of the given tag name.
     * @param tagName tag name to search
     * @throws XMLStreamException
     */
    private void toTagEnd(XMLStreamReader reader, final String tagName) throws XMLStreamException{
        while (reader.hasNext()) {
            if (END_ELEMENT == reader.next() &&
               tagName.equalsIgnoreCase(reader.getLocalName()))
               return;
        }
        throw new XMLStreamException("Error in xml file, Could not find end of tag "+tagName+" .");
    }

    @Override
    public void close() {
    }


    /**
     * Require a MarshallerPool depending on the property BINDING_PACKAGE.
     *
     * accepted values : "JTSWrapper" or null (default). => JTSWrapperMarshallerPool
     *                   "GML"      (default).                     => GMLMarshallerPool
     */
    public static final class Spi implements XSDMapping.Spi {

        @Override
        public XSDMapping create(GenericName name, JAXBFeatureTypeReader stack, Annotated xsdObject) {

            //check if we are dealing with a geometry type
            if (!(xsdObject instanceof ComplexType)) return null;

            final ComplexType ct = (ComplexType) xsdObject;
            final String namespace = NamesExt.getNamespace(name);

            if (!(GMLConvention.GML_311_NAMESPACE.equals(namespace)
                || GMLConvention.GML_321_NAMESPACE.equals(namespace))) return null;

//            final ComplexContent complexContent = ct.getComplexContent();
//            if (complexContent == null) return null;
//            final ExtensionType extension = complexContent.getExtension();
//            if (extension == null) return null;
//            final QName base = extension.getBase();
//            if (base == null) return null;

            Class type = CLASS_BINDING.get(name.tip().toString());
            if (type == null) return null;

            //extract longitude first parameter
            final Object bool = stack.getProperty(JAXPStreamFeatureReader.LONGITUDE_FIRST);
            final boolean longitudeFirst = (bool == null) || Boolean.TRUE.equals(bool);

            //extract marshaller parameter
            Object bindingPackage = stack.getProperties().get(BINDING_PACKAGE);

            MarshallerPool pool;
            if (bindingPackage instanceof String) {
                if ("JTSWrapper".equals(bindingPackage)) {
                    pool = JTSWrapperMarshallerPool.getInstance();
                } else if ("GML".equals(bindingPackage)) {
                    pool = GMLMarshallerPool.getInstance();
                } else {
                    throw new IllegalArgumentException("Unexpected property value for BINDING_PACKAGE:" + bindingPackage);
                }
            } else if (bindingPackage instanceof MarshallerPool) {
                pool = (MarshallerPool) bindingPackage;
            } else {
                pool = GMLMarshallerPool.getInstance();
            }

            final boolean decorated = GMLConvention.isDecoratedProperty(name.tip().toString());

            final AttributeTypeBuilder atb = new FeatureTypeBuilder().addAttribute(type);

            if (decorated) {
                //add xlink href characteristic
                atb.addCharacteristic(String.class).setName(GMLConvention.XLINK_HREF);
            }

            return new GeometryMapping(ct, atb.build(), pool, longitudeFirst, decorated);
        }

        @Override
        public float getPriority() {
            return 100;
        }
    }

}
