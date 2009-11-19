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

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.collection.FeatureIterator;
import org.geotoolkit.feature.xml.XmlFeatureWriter;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.xml.Namespaces;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.Geometry;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXPStreamFeatureWriter implements XmlFeatureWriter {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml.jaxp");
    
    private static MarshallerPool pool;

    private static ObjectFactory factory = new ObjectFactory();

    public JAXPStreamFeatureWriter() throws JAXBException {
         // for GML geometries unmarshall
        pool = new MarshallerPool(ObjectFactory.class);
    }

     @Override
    public void write(SimpleFeature sf, Writer writer) {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(writer);


            // the XML header
            streamWriter.writeStartDocument("UTF-8", "1.0");

            write(sf, streamWriter);

            streamWriter.flush();
            streamWriter.close();

        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while writing the feature: " + ex.getMessage());
        }
    }

    @Override
    public void write(SimpleFeature sf, OutputStream out) {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(out);


            // the XML header
            streamWriter.writeStartDocument("UTF-8", "1.0");

            write(sf, streamWriter);

            streamWriter.flush();
            streamWriter.close();

        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while writing the feature: " + ex.getMessage());
        }
    }

    @Override
    public String write(SimpleFeature feature) {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            StringWriter sw = new StringWriter();
            XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(sw);


            // the XML header
            streamWriter.writeStartDocument("UTF-8", "1.0");

            write(feature, streamWriter);
            
            streamWriter.flush();
            streamWriter.close();
            return sw.toString();

        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while writing the feature: " + ex.getMessage());
        }
        return null;
    }

    private void write(SimpleFeature feature, XMLStreamWriter streamWriter) {
        try {

            //the root element of the xml document (type of the feature)
            FeatureType type = feature.getType();
            String namespace = type.getName().getNamespaceURI();
            String localPart = type.getName().getLocalPart();
            if (namespace != null) {
                String prefix = Namespaces.getPreferredPrefix(namespace, null);
                streamWriter.writeStartElement(prefix, namespace, localPart);
            } else {
                streamWriter.writeStartElement(localPart);
            }


            //the simple nodes (attributes of the feature)
            for (Property a : feature.getProperties()) {
                if (!"the_geom".equals(a.getName().getLocalPart())) {
                    String namespaceProperty = a.getName().getNamespaceURI();
                    if (namespaceProperty != null) {
                        streamWriter.writeStartElement(namespaceProperty, a.getName().getLocalPart());
                    } else {
                        streamWriter.writeStartElement(a.getName().getLocalPart());
                    }
                    streamWriter.writeCharacters(getStringValue(a.getValue()));
                    streamWriter.writeEndElement();
                }
            }

            // we add the geometry
            streamWriter.writeStartElement("the_geom");
            Geometry isoGeometry = JTSUtils.toISO((com.vividsolutions.jts.geom.Geometry) feature.getDefaultGeometry(), feature.getFeatureType().getCoordinateReferenceSystem());

            Marshaller m = null;
            try {
                m = pool.acquireMarshaller();
                m.setProperty(m.JAXB_FRAGMENT, true);
                m.marshal(factory.buildAnyGeometry(isoGeometry), streamWriter);
            } catch (JAXBException ex) {
                LOGGER.severe("JAXB Exception while marshalling the iso geometry: " + ex.getMessage());
            } finally {
                if (m != null)
                    pool.release(m);
            }

            streamWriter.writeEndElement();
            streamWriter.writeEndElement();

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);

        }
    }

    @Override
    public void write(FeatureCollection fc, Writer writer) {
        try {

            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter streamWriter    = outputFactory.createXMLStreamWriter(writer);

            write(fc, streamWriter);

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void write(FeatureCollection fc, OutputStream out) {
        try {

            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter streamWriter    = outputFactory.createXMLStreamWriter(out);

            write(fc, streamWriter);

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
    }

    @Override
    public String write(FeatureCollection featureCollection) {
        try {

            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            StringWriter sw                = new StringWriter();
            XMLStreamWriter streamWriter    = outputFactory.createXMLStreamWriter(sw);

            write(featureCollection, streamWriter);
            return sw.toString();

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
        return null;
    }

    private void write(FeatureCollection featureCollection, XMLStreamWriter streamWriter) {
        try {

            // the XML header
            streamWriter.writeStartDocument("UTF-8", "1.0");

            // the root Element
            streamWriter.writeStartElement("gml", "FeatureCollection", "http://www.opengis.net/gml");


            // we write each feature member of the collection
            FeatureIterator iterator =featureCollection.features();
            while (iterator.hasNext()) {
                final SimpleFeature f = (SimpleFeature) iterator.next();

                streamWriter.writeStartElement("gml", "http://www.opengis.net/gml", "featureMember");
                write(f, streamWriter);
                streamWriter.writeEndElement();
            }

            // we close the stream
            iterator.close();
            streamWriter.flush();
            streamWriter.close();

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
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
        } else if (obj instanceof Integer || obj instanceof Long || obj instanceof Double) {
            return obj + "";

        } else if (obj != null) {
            LOGGER.warning("unexpected type:" + obj.getClass());
        }
        return null;
    }

    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
