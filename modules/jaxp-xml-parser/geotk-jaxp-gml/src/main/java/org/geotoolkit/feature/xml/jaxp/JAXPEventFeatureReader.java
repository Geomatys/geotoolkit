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

package org.geotoolkit.feature.xml.jaxp;


import com.vividsolutions.jts.geom.Geometry;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


import org.geotoolkit.data.FeatureCollectionUtilities;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.geotoolkit.util.Converters;
import org.geotoolkit.xml.MarshallerPool;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * @module pending
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
            LOGGER.log(Level.SEVERE,"XMl stream initializing the event Reader: " + ex.getMessage(), ex);
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
            LOGGER.log(Level.SEVERE,"XMl stream initializing the event Reader: " + ex.getMessage(), ex);
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
            LOGGER.log(Level.SEVERE,"XMl stream initializing the event Reader: " + ex.getMessage(), ex);
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
                    Name name               = Utils.getNameFromQname(startEvent.getName());
                    Attribute id            = startEvent.getAttributeByName(new QName("http://www.opengis.net/gml", "id"));
                    
                    if (name.getLocalPart().equals("FeatureCollection")) {
                        return readFeatureCollection(eventReader, id.getValue());

                    } else if (featureType.getName().equals(name)) {
                        return readFeature(eventReader, id.getValue());
                    } else {
                        throw new IllegalArgumentException("The xml does not describte the same type of feature: \n " +
                                                           "Expected: " + featureType.getName() + '\n'                  +
                                                           "But was: "  + name);
                    }
                }
            }
        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while reading the feature: " + ex.getMessage(), ex);
        } 
        return null;
    }

    private SimpleFeature readFeature(XMLEventReader eventReader, String id) {
        builder.reset();
        String geometryName = featureType.getGeometryDescriptor().getName().getLocalPart();
        try {
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startEvent = event.asStartElement();
                    QName q                 = startEvent.getName();

                    if (!q.getLocalPart().equals(geometryName)) {
                        XMLEvent contentEvent = eventReader.nextEvent();
                        if (contentEvent.isCharacters()) {
                            Characters content = contentEvent.asCharacters();
                            LOGGER.finer("find value:" + content.getData() + " for attribute :" + q.getLocalPart());
                            PropertyDescriptor pdesc = featureType.getDescriptor(Utils.getNameFromQname(q));
                            if (pdesc != null) {
                                Class propertyType       = pdesc.getType().getBinding();
                                builder.set(q.getLocalPart(), Converters.convert(content.getData(), propertyType));
                                
                            } else {
                                StringBuilder exp = new StringBuilder("expected ones are:").append('\n');
                                for (PropertyDescriptor pd : featureType.getDescriptors()) {
                                    exp.append(pd.getName().getLocalPart()).append('\n');
                                }
                                throw new IllegalArgumentException("unexpected attribute:" + q.getLocalPart() + '\n' + exp.toString());

                            }
                        
                        } else {
                            LOGGER.severe("unexpected event:" + Utils.getEventTypeString(contentEvent.getEventType()));
                        }

                    } else {
                        XMLEvent nextEvent = eventReader.peek();
                        if (nextEvent.getEventType() == XMLEvent.CHARACTERS) {
                            eventReader.next();
                        }
                        
                        try {
                            Unmarshaller un     = marshallpool.acquireUnmarshaller();
                            JTSGeometry isoGeom = (JTSGeometry) ((JAXBElement)un.unmarshal(eventReader)).getValue();
                            Geometry jtsGeom    = isoGeom.getJTSGeometry();
                            builder.set(geometryName, jtsGeom);
                        } catch (JAXBException ex) {
                            LOGGER.log(Level.SEVERE, "JAXB exception while reading the feature geometry:" + ex.getMessage(), ex);
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

            return builder.buildFeature(id);


        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while reading the feature: " + ex.getMessage(), ex);
        }
        return null;
    }

    private FeatureCollection readFeatureCollection(XMLEventReader eventReader, String id) {
        FeatureCollection collection = FeatureCollectionUtilities.createCollection(id, (SimpleFeatureType) featureType);
        try {
             while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    StartElement startEvent = event.asStartElement();
                    Name name               = Utils.getNameFromQname(startEvent.getName());
                    Attribute fid           = startEvent.getAttributeByName(new QName("http://www.opengis.net/gml", "id"));

                    if (name.getLocalPart().equals("featureMember")) {
                        continue;
                    
                    } else if (name.getLocalPart().equals("boundedBy")) {
                        Attribute srsNameAtt = startEvent.getAttributeByName(new QName("http://www.opengis.net/gml", "srsName"));
                        String srsName       = null;
                        if (srsNameAtt != null) {
                            srsName = srsNameAtt.getValue();
                        }
                        JTSEnvelope2D bounds = readBounds(eventReader, srsName);
                        

                    } else if (featureType.getName().equals(name)) {
                        SimpleFeature feature = readFeature(eventReader, fid.getValue());
                        collection.add(feature);
                    } else {
                        throw new IllegalArgumentException("The xml does not describe the same type of feature: \n " +
                                                           "Expected: " + featureType.getName() + '\n'                  +
                                                           "But was: "  + name);
                    }
                }
             }
             return collection;

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while reading the feature: " + ex.getMessage(), ex);
        }
        return null;
    }

    private JTSEnvelope2D readBounds(XMLEventReader eventReader, String srsName) throws XMLStreamException {
       JTSEnvelope2D bounds = null;
       while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().getLocalPart().equals("boundedBy")) {
                    return null;
                }
            }

       }
        /*String srsName = null;
        if (bounds.getCoordinateReferenceSystem() != null) {
            try {
                srsName = CRS.lookupIdentifier(bounds.getCoordinateReferenceSystem(), true);
            } catch (FactoryException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        QName bounded = new QName("http://www.opengis.net/gml", "boundedBy", "gml");
        eventWriter.add(new StartElementEvent(bounded));
        QName env     = new QName("http://www.opengis.net/gml", "Envelope", "gml");
        eventWriter.add(new StartElementEvent(env));
        if (srsName != null) {
            eventWriter.add(new AttributeImpl("gml", "http://www.opengis.net/gml", "srsName", srsName, null));
        }

        // lower corner
        QName lowc    = new QName("http://www.opengis.net/gml", "lowerCorner", "gml");
        eventWriter.add(new StartElementEvent(lowc));
        String lowValue = bounds.getLowerCorner().getOrdinate(0) + " " + bounds.getLowerCorner().getOrdinate(1);
        eventWriter.add(new CharacterEvent(lowValue));
        eventWriter.add(new EndElementEvent(lowc));

        // upper corner
        QName uppc    = new QName("http://www.opengis.net/gml", "upperCorner", "gml");
        eventWriter.add(new StartElementEvent(uppc));
        String uppValue = bounds.getUpperCorner().getOrdinate(0) + " " + bounds.getUpperCorner().getOrdinate(1);
        eventWriter.add(new CharacterEvent(uppValue));
        eventWriter.add(new EndElementEvent(uppc));

        eventWriter.add(new EndElementEvent(env));
        eventWriter.add(new EndElementEvent(bounded));*/
        return bounds;
    }
}