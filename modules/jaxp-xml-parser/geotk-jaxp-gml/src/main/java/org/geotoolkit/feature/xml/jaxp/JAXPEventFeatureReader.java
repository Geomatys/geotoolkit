/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.feature.xml.jaxp;


import com.sun.xml.internal.stream.events.EndElementEvent;
import com.vividsolutions.jts.geom.Geometry;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


import org.geotoolkit.data.FeatureCollectionUtilities;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.DefaultSimpleFeature;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.geotoolkit.util.Converters;
import org.geotoolkit.xml.MarshallerPool;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.identity.FeatureId;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXPEventFeatureReader implements XmlFeatureReader {

    private static final Logger LOGGER = Logger.getLogger("jaxp");

    private static MarshallerPool marshallpool;

    private FeatureType featureType ;

    private  SimpleFeatureBuilder builder;

    public JAXPEventFeatureReader(FeatureType featureType) throws JAXBException {
         // for GML geometries marshall
         marshallpool = new MarshallerPool(ObjectFactory.class);

         builder = new SimpleFeatureBuilder((SimpleFeatureType) featureType);
         this.featureType = featureType;

    }

    @Override
    public Object read(String xml)  {
        try {
            XMLInputFactory XMLfactory = XMLInputFactory.newInstance();
            XMLfactory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);

            XMLEventReader eventReader = XMLfactory.createXMLEventReader(new StringReader(xml));
            return read(eventReader);
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream initializing the event Reader: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public Object read(InputStream in) {
        try {
            XMLInputFactory XMLfactory = XMLInputFactory.newInstance();
            XMLfactory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);

            XMLEventReader eventReader = XMLfactory.createXMLEventReader(in);
            return read(eventReader);
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream initializing the event Reader: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public Object read(Reader reader) {
        try {
            XMLInputFactory XMLfactory = XMLInputFactory.newInstance();
            XMLfactory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);

            XMLEventReader eventReader = XMLfactory.createXMLEventReader(reader);
            return read(eventReader);
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream initializing the event Reader: " + ex.getMessage());
        }
        return null;
    }


    private Object read(XMLEventReader eventReader) {
        try {
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                //LOGGER.info(event + "");

                //we are looking for the root mark
                if (event.isStartElement()) {
                    StartElement startEvent = event.asStartElement();
                    Name name               = getNameFromQname(startEvent.getName());
                    
                    if (name.getLocalPart().equals("FeatureCollection")) {
                        return readFeatureCollection(eventReader);

                    } else if (featureType.getName().equals(name)) {
                        return readFeature(eventReader);
                    } else {
                        throw new IllegalArgumentException("The xml does not describte the same type of feature: \n " +
                                                           "Expected: " + featureType.getName() + '\n'                  +
                                                           "But was: "  + name);
                    }
                }
            }
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while reading the feature: " + ex.getMessage());
        } 
        return null;
    }

    public SimpleFeature readFeature(XMLEventReader eventReader) {
        builder.reset();
        try {
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startEvent = event.asStartElement();
                    QName q                 = startEvent.getName();

                    if (!q.getLocalPart().equals("the_geom")) {
                        XMLEvent contentEvent = eventReader.nextEvent();
                        if (contentEvent.isCharacters()) {
                            Characters content = contentEvent.asCharacters();
                            LOGGER.info("find value:" + content.getData() + " for attribute :" + q.getLocalPart());
                            PropertyDescriptor pdesc = featureType.getDescriptor(q.getLocalPart());
                            if (pdesc != null) {
                                Class propertyType       = pdesc.getType().getBinding();
                                builder.set(q.getLocalPart(), Converters.convert(content.getData(), propertyType));
                                
                            } else {
                                throw new IllegalArgumentException("unexpected attribute:" + q.getLocalPart());
                            }
                        
                        } else {
                            LOGGER.severe("unexpected event");
                        }

                    } else {
                        eventReader.next();
                        try {
                            Unmarshaller un     = marshallpool.acquireUnmarshaller();
                            JTSGeometry isoGeom = (JTSGeometry) ((JAXBElement)un.unmarshal(eventReader)).getValue();
                            System.out.println("isogeom:" + isoGeom.getClass());
                            Geometry jtsGeom    = isoGeom.getJTSGeometry();
                            builder.set("the_geom", jtsGeom);
                        } catch (JAXBException ex) {
                            LOGGER.severe("JAXB exception while reading the feature geometry: " + ex.getMessage());
                        }
                    }

                } else if (event.isEndElement()) {
                    EndElement endEvent = event.asEndElement();
                    QName q             = endEvent.getName();
                    if (q.getLocalPart().equals("featureMember")) {
                        break;
                    }
                }
            }

            int ordinal   = 1;
            return builder.buildFeature(featureType.getName().getLocalPart() + '.' + ordinal);


        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while reading the feature: " + ex.getMessage());
        }
        return null;
    }

    private FeatureCollection readFeatureCollection(XMLEventReader eventReader) {
        FeatureCollection collection = FeatureCollectionUtilities.createCollection(null, (SimpleFeatureType) featureType);
        try {
             while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    StartElement startEvent = event.asStartElement();
                    Name name               = getNameFromQname(startEvent.getName());
                    System.out.println("name:" + name);
                    if (name.getLocalPart().equals("featureMember")) {
                        continue;

                    } else if (featureType.getName().equals(name)) {
                        SimpleFeature feature = readFeature(eventReader);
                        collection.add(feature);
                    } else {
                        throw new IllegalArgumentException("The xml does not describte the same type of feature: \n " +
                                                           "Expected: " + featureType.getName() + '\n'                  +
                                                           "But was: "  + name);
                    }
                }
             }
             return collection;

        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while reading the feature: " + ex.getMessage());
        }
        return null;
    }


    private Name getNameFromQname(QName qname) {
        Name name;
        if (qname.getNamespaceURI() == null || "".equals(qname.getNamespaceURI())) {
            name = new DefaultName(qname.getLocalPart());
        } else {
            name = new DefaultName(qname);
        }
        return name;
    }
    
}