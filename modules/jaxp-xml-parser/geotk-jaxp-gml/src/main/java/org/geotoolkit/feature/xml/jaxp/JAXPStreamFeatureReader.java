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
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.DefaultSimpleFeature;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.internal.jaxb.ObjectFactory;
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
public class JAXPStreamFeatureReader implements XmlFeatureReader {

    private static final Logger LOGGER = Logger.getLogger("jaxp");
    
    private static MarshallerPool pool;

    private FeatureType featureType;

    public JAXPStreamFeatureReader(FeatureType featureType) throws JAXBException {
         // for GML geometries unmarshall
        pool = new MarshallerPool(ObjectFactory.class);
        this.featureType = featureType;
    }

    @Override
    public Object read(String xml)  {
        try {
            XMLInputFactory XMLfactory = XMLInputFactory.newInstance();
            XMLfactory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);

            XMLStreamReader streamReader = XMLfactory.createXMLStreamReader(new StringReader(xml));
            return read(streamReader);
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

            XMLStreamReader streamReader = XMLfactory.createXMLStreamReader(in);
            return read(streamReader);
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

            XMLStreamReader streamReader = XMLfactory.createXMLStreamReader(reader);
            return read(streamReader);
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream initializing the event Reader: " + ex.getMessage());
        }
        return null;
    }

    private Object read(XMLStreamReader streamReader) {
        try {
            while (streamReader.hasNext()) {
                int event = streamReader.next();
               

                //we are looking for the root mark
                if (event == XMLEvent.START_ELEMENT) {
                    QName q                 = streamReader.getName();
                    Name name;
                    if (q.getNamespaceURI() == null || "".equals(q.getNamespaceURI())) {
                        name                = new DefaultName(q.getLocalPart());
                    } else {
                        name                = new DefaultName(q);
                    }
                    if (name.getLocalPart().equals("FeatureCollection")) {
                        // TODO

                    } else if (featureType.getName().equals(name)) {
                        return readFeature(streamReader);
                    } else {
                        throw new IllegalArgumentException("the xml does not describte the same type of feature");
                    }
                }
            }
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while reading the feature: " + ex.getMessage());
        }
        return null;
    }

    public SimpleFeature readFeature(XMLStreamReader streamReader) {
        try {
            int nbAttribute         = 0;
            List<Object> values     = new ArrayList<Object>();

            while (streamReader.hasNext()) {
                int event = streamReader.next();

                if (event == XMLEvent.START_ELEMENT) {
                    nbAttribute++;
                    QName q                 = streamReader.getName();

                    if (!q.getLocalPart().equals("the_geom")) {
                        int contentEvent = streamReader.next();
                        if (contentEvent == XMLEvent.CHARACTERS) {
                            String content =streamReader.getText();
                            LOGGER.info("find value:" + content + " for attribute :" + q.getLocalPart());
                            PropertyDescriptor pdesc = featureType.getDescriptor(q.getLocalPart());
                            if (pdesc != null) {
                                Class propertyType       = pdesc.getType().getBinding();
                                values.add(castValue(content, propertyType));
                            } else {
                                throw new IllegalArgumentException("unexpected attribute:" + q.getLocalPart());
                            }
                        } else {
                            LOGGER.severe("unexpected event");
                        }

                    } else {
                        event = streamReader.next();
                        while (event != XMLEvent.START_ELEMENT) {
                            event = streamReader.next();
                        }
                        
                        try {
                            Unmarshaller un     = pool.acquireUnmarshaller();
                            JTSGeometry isoGeom = (JTSGeometry) ((JAXBElement)un.unmarshal(streamReader)).getValue();
                            Geometry jtsGeom = isoGeom.getJTSGeometry();
                            values.add(jtsGeom);
                        } catch (JAXBException ex) {
                            LOGGER.severe("JAXB exception while reading the feature geometry: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }

                }
            }

            int ordinal   = 1;
            FeatureId id  = new DefaultFeatureId(featureType.getName().getLocalPart() + '.' + ordinal);
            DefaultSimpleFeature simpleFeature = new DefaultSimpleFeature(values, (SimpleFeatureType)featureType, id);

            return simpleFeature;
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while reading the feature: " + ex.getMessage());
        }
        return null;
    }

    private Object castValue(String data, Class propertyType) {
        if (propertyType.equals(String.class)) {
            return data;
        } else if (propertyType.equals(Integer.class)) {
            return Integer.parseInt(data);
        } else if (propertyType.equals(Double.class)) {
            return Double.parseDouble(data);
        } else if (propertyType.equals(Long.class)) {
            return Long.parseLong(data);
        } else {
            LOGGER.severe("unexpected type:" + propertyType);
        }
        return null;
    }

    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }
}
