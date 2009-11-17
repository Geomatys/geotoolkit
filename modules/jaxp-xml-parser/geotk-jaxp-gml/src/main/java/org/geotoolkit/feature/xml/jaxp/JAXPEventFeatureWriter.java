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

import com.sun.xml.internal.stream.events.AttributeImpl;
import com.sun.xml.internal.stream.events.CharacterEvent;
import com.sun.xml.internal.stream.events.EndElementEvent;
import com.sun.xml.internal.stream.events.NamespaceImpl;
import com.sun.xml.internal.stream.events.StartDocumentEvent;
import com.sun.xml.internal.stream.events.StartElementEvent;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
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
import org.opengis.feature.type.GeometryType;
import org.opengis.geometry.Geometry;

/**
 *
 * @module pending
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXPEventFeatureWriter implements XmlFeatureWriter {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml.jaxp");

    private static MarshallerPool pool;

    private static ObjectFactory factory = new ObjectFactory();
    
    public JAXPEventFeatureWriter() throws JAXBException {
         // for GML geometries unmarshall
        pool = new MarshallerPool(ObjectFactory.class);
    }

    @Override
    public String write(SimpleFeature feature) {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            StringWriter sw = new StringWriter();
            XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(sw);

            // the XML header
            eventWriter.add(new StartDocumentEvent("UTF-8", "1.0"));

            write(feature, eventWriter, true);

            // we close the stream
            eventWriter.flush();
            eventWriter.close();
            
            return sw.toString();
            
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while writing the feature: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void write(SimpleFeature feature, Writer writer) {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(writer);

            // the XML header
            eventWriter.add(new StartDocumentEvent("UTF-8", "1.0"));

            write(feature, eventWriter, true);

            // we close the stream
            eventWriter.flush();
            eventWriter.close();

        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while writing the feature: " + ex.getMessage());
        }
    }

    @Override
    public void write(SimpleFeature feature, OutputStream out) {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(out);

            // the XML header
            eventWriter.add(new StartDocumentEvent("UTF-8", "1.0"));

            write(feature, eventWriter,true);

            // we close the stream
            eventWriter.flush();
            eventWriter.close();

        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while writing the feature: " + ex.getMessage());
        }
    }

    private void write(SimpleFeature feature, XMLEventWriter eventWriter, boolean root) {

        try {
        //the root element of the xml document (type of the feature)
            FeatureType type = feature.getType();
            String namespace = type.getName().getNamespaceURI();
            String localPart = type.getName().getLocalPart();
            String prefix    = Namespaces.getPreferredPrefix(namespace, null);
            QName rootName   = new QName(namespace, localPart, prefix);
            StartElementEvent rootEvent = new StartElementEvent(rootName);
            eventWriter.add(rootEvent);
            eventWriter.add(new AttributeImpl("gml", "http://www.opengis.net/gml", "id", feature.getID(), null));


            if (root) {
                NamespaceImpl namespaceEvent = new NamespaceImpl("gml", "http://www.opengis.net/gml");
                eventWriter.add(namespaceEvent);
            }

            //the simple nodes (attributes of the feature)
            String geometryName = null;
            for (Property a : feature.getProperties()) {
                if (!(a.getType() instanceof GeometryType)) {
                    if (a.getName() != null)  {
                        QName property = new QName(a.getName().getNamespaceURI(), a.getName().getLocalPart());
                        eventWriter.add(new StartElementEvent(property));
                        eventWriter.add(new CharacterEvent(getStringValue(a.getValue())));
                        eventWriter.add(new EndElementEvent(property));
                    } else {
                        LOGGER.severe("the propertyName is null for property:" + a);
                    }
                } else {
                    geometryName = a.getName().getLocalPart();
                }
            }

            // we add the geometry
            if (feature.getDefaultGeometry() != null) {
                QName geomQname = new QName(geometryName);
                eventWriter.add(new StartElementEvent(geomQname));
                Geometry isoGeometry = JTSUtils.toISO((com.vividsolutions.jts.geom.Geometry) feature.getDefaultGeometry(), feature.getFeatureType().getCoordinateReferenceSystem());

                Marshaller m = null;
                try {
                    m= pool.acquireMarshaller();
                    m.setProperty(m.JAXB_FRAGMENT, true);
                    m.setProperty(m.JAXB_FORMATTED_OUTPUT, true);
                    m.marshal(factory.buildAnyGeometry(isoGeometry), eventWriter);
                } catch (JAXBException ex) {
                    LOGGER.severe("JAXB Exception while marshalling the iso geometry: " + ex.getMessage());
                } finally {
                    if (m != null)
                        pool.release(m);
                }
                eventWriter.add(new EndElementEvent(geomQname));
            }

            eventWriter.add(new EndElementEvent(rootName));
        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void write(FeatureCollection fc, Writer writer) {
        try {

            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter eventWriter     = outputFactory.createXMLEventWriter(writer);

            write(fc, eventWriter);

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void write(FeatureCollection fc, OutputStream out) {
        try {

            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter eventWriter     = outputFactory.createXMLEventWriter(out);

            write(fc, eventWriter);

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
    }

    @Override
    public String write(FeatureCollection featureCollection) {
        try {

            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            StringWriter sw                = new StringWriter();
            XMLEventWriter eventWriter     = outputFactory.createXMLEventWriter(sw);
            eventWriter.setDefaultNamespace("");
            write(featureCollection, eventWriter);

            return sw.toString();
        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
        return null;
    }

    private void write(FeatureCollection featureCollection, XMLEventWriter eventWriter) {
        try {
            
            // the XML header
            eventWriter.add(new StartDocumentEvent("UTF-8", "1.0"));

            
            // the root Element
            QName root = new QName("http://www.opengis.net/gml", "FeatureCollection", "gml");
            StartElementEvent ste = new StartElementEvent(root);
            eventWriter.add(ste);

            NamespaceImpl namespace = new NamespaceImpl("gml", "http://www.opengis.net/gml");
            eventWriter.add(namespace);

            // we write each feature member of the collection
            QName memberName = new QName("http://www.opengis.net/gml", "featureMember", "gml");
            FeatureIterator iterator =featureCollection.features();
            while (iterator.hasNext()) {
                final SimpleFeature f = (SimpleFeature) iterator.next();

                eventWriter.add(new StartElementEvent(memberName));
                write(f, eventWriter, false);
                eventWriter.add(new EndElementEvent(memberName));
            }

            // we close the stream
            iterator.close();
            eventWriter.flush();
            eventWriter.close();
            
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

}