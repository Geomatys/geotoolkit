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
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.geotoolkit.data.DefaultFeatureCollection;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.geotoolkit.util.Converters;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * @module pending
 * @author Guilhem Legal (Geomatys)
 */
public class JAXPEventFeatureReader extends JAXPFeatureReader {

    public JAXPEventFeatureReader(FeatureType featureType) throws JAXBException {
         super(featureType);
    }

    public JAXPEventFeatureReader(List<FeatureType> featureTypes) throws JAXBException {
        super(featureTypes);
    }
    /**
     * {@inheritDoc }
     */
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

    /**
     * {@inheritDoc }
     */
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

    /**
     * {@inheritDoc }
     */
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


    /**
     * Start to read An object from the XML datasource.
     *
     * @param eventReader The XML event reader.
     * @return A feature or featureCollection described in the XML stream.
     */
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
                    StringBuilder expectedFeatureType = new StringBuilder();

                    if (name.getLocalPart().equals("FeatureCollection")) {
                        return readFeatureCollection(eventReader, id.getValue());

                    } else {
                        for (FeatureType ft : featureTypes) {
                            if (ft.getName().equals(name)) {
                                return readFeature(eventReader, id.getValue(), ft);
                            }
                            expectedFeatureType.append(ft.getName()).append('\n');
                        }
                        throw new IllegalArgumentException("The xml does not describte the same type of feature: \n " +
                                                           "Expected: " + expectedFeatureType.toString()      + '\n'  +
                                                           "But was: "  + name);
                    }
                }
            }
        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while reading the feature: " + ex.getMessage(), ex);
        } 
        return null;
    }

    /**
     * Read a simpleFeature from the XML stream.
     *
     * @param eventReader The XML event reader.
     * @param id the extracted id of the feature.
     * @return A simpleFeature object.
     */
    private SimpleFeature readFeature(XMLEventReader eventReader, String id, FeatureType featureType) {
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder((SimpleFeatureType) featureType);
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
                            JTSGeometry isoGeom = (JTSGeometry) ((JAXBElement)unmarshaller.unmarshal(eventReader)).getValue();
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

    /**
     * Read a Feature collection from the XML stream.
     *
     * @param eventReader The XML event reader.
     * @param id The extract id of the feature collection
     * @return A feature Collection.
     */
    private FeatureCollection readFeatureCollection(XMLEventReader eventReader, String id) {
        FeatureCollection collection = null;
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
                        

                    }  else {
                        boolean find = false;
                        StringBuilder expectedFeatureType = new StringBuilder();
                        for (FeatureType ft : featureTypes) {
                            if (ft.getName().equals(name)) {
                                if (collection == null) {
                                    collection = new DefaultFeatureCollection(id, ft, SimpleFeature.class);
                                }
                                collection.add(readFeature(eventReader, fid.getValue(), ft));
                                find = true;
                            }
                            expectedFeatureType.append(ft.getName()).append('\n');
                        }

                        if (!find) {
                            throw new IllegalArgumentException("The xml does not describe the same type of feature: \n " +
                                                               "Expected: " + expectedFeatureType.toString()     + '\n'  +
                                                               "But was: "  + name);
                        }
                    }
                }
             }
             return collection;

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while reading the feature: " + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Extract An envelope from the BoundedBy XML mark of a feature collection.
     * 
     * @param eventReader The XML event reader.
     * @param srsName The extracted CRS identifier.
     *
     * @return An envelope of the collection bounds.
     * @throws XMLStreamException
     */
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
        return bounds;
    }
}