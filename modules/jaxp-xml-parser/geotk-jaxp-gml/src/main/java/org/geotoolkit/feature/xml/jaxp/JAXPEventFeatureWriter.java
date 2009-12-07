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
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.collection.FeatureIterator;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.xml.Namespaces;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.FactoryException;

/**
 *
 * @module pending
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXPEventFeatureWriter extends JAXPFeatureWriter {

    
    public JAXPEventFeatureWriter() throws JAXBException {
         super();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String write(SimpleFeature feature) {
        if (feature != null) {
            try {
                XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
                StringWriter sw = new StringWriter();
                XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(sw);

                // the XML header
                eventWriter.add(new StartDocumentEvent("UTF-8", "1.0"));

                writeFeature(feature, eventWriter, true);

                // we close the stream
                eventWriter.flush();
                eventWriter.close();

                return sw.toString();

            } catch (XMLStreamException ex) {
                LOGGER.severe("XMl stream exception while writing the feature: " + ex.getMessage());
            }
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write(SimpleFeature feature, Writer writer) {
        if (feature != null) {
            try {
                XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
                XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(writer);

                // the XML header
                eventWriter.add(new StartDocumentEvent("UTF-8", "1.0"));

                writeFeature(feature, eventWriter, true);

                // we close the stream
                eventWriter.flush();
                eventWriter.close();

            } catch (XMLStreamException ex) {
                LOGGER.severe("XMl stream exception while writing the feature: " + ex.getMessage());
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write(SimpleFeature feature, OutputStream out) {
        if (feature != null) {
            try {
                XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
                XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(out);

                // the XML header
                eventWriter.add(new StartDocumentEvent("UTF-8", "1.0"));

                writeFeature(feature, eventWriter,true);

                // we close the stream
                eventWriter.flush();
                eventWriter.close();

            } catch (XMLStreamException ex) {
                LOGGER.severe("XMl stream exception while writing the feature: " + ex.getMessage());
            }
        }
    }

    private void writeFeature(SimpleFeature feature, XMLEventWriter eventWriter, boolean root) {

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
                if (!namespace.equals("http://www.opengis.net/gml")) {
                    NamespaceImpl namespaceEvent2 = new NamespaceImpl(prefix, namespace);
                    eventWriter.add(namespaceEvent2);

                }
            }

            //the simple nodes (attributes of the feature)
            for (Property a : feature.getProperties()) {
                if (!(a.getType() instanceof GeometryType)) {
                    if (a.getName() != null)  {
                        QName property;
                        if (a.getName().getNamespaceURI() != null) {
                            prefix   = Namespaces.getPreferredPrefix(a.getName().getNamespaceURI(), "");
                            property = new QName(a.getName().getNamespaceURI(), a.getName().getLocalPart(), prefix);
                        } else {
                            property = new QName(a.getName().getNamespaceURI(), a.getName().getLocalPart());
                        }
                        eventWriter.add(new StartElementEvent(property));
                        eventWriter.add(new CharacterEvent(Utils.getStringValue(a.getValue())));
                        eventWriter.add(new EndElementEvent(property));
                    } else {
                        LOGGER.severe("the propertyName is null for property:" + a);
                    }
                    
                // we add the geometry
                } else {
                    Name geometryName = a.getName();
                    QName geomQname;
                    if (geometryName.getNamespaceURI() != null) {
                        prefix = Namespaces.getPreferredPrefix(geometryName.getNamespaceURI(), "");
                        geomQname = new QName(geometryName.getNamespaceURI(), geometryName.getLocalPart(), prefix);
                    } else {
                        geomQname = new QName(geometryName.getNamespaceURI(), geometryName.getLocalPart());
                    }
                    eventWriter.add(new StartElementEvent(geomQname));
                    Geometry isoGeometry = JTSUtils.toISO((com.vividsolutions.jts.geom.Geometry) a.getValue(), feature.getFeatureType().getCoordinateReferenceSystem());
                    try {
                        marshaller.marshal(factory.buildAnyGeometry(isoGeometry), eventWriter);
                    } catch (JAXBException ex) {
                        LOGGER.severe("JAXB Exception while marshalling the iso geometry: " + ex.getMessage());
                    }
                    eventWriter.add(new EndElementEvent(geomQname));
                }
            }

            eventWriter.add(new EndElementEvent(rootName));
        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write(FeatureCollection fc, Writer writer) {
        try {

            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter eventWriter     = outputFactory.createXMLEventWriter(writer);

            writeFeatureCollection(fc, eventWriter);

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write(FeatureCollection fc, OutputStream out) {
        try {

            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLEventWriter eventWriter     = outputFactory.createXMLEventWriter(out);

            writeFeatureCollection(fc, eventWriter);

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String write(FeatureCollection featureCollection) {
        try {

            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            StringWriter sw                = new StringWriter();
            XMLEventWriter eventWriter     = outputFactory.createXMLEventWriter(sw);
            eventWriter.setDefaultNamespace("");
            writeFeatureCollection(featureCollection, eventWriter);

            return sw.toString();
        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
        return null;
    }

    private void writeFeatureCollection(FeatureCollection featureCollection, XMLEventWriter eventWriter) {
        try {
            
            // the XML header
            eventWriter.add(new StartDocumentEvent("UTF-8", "1.0"));

            
            // the root Element
            QName root = new QName("http://www.opengis.net/gml", "FeatureCollection", "wfs");
            StartElementEvent ste = new StartElementEvent(root);
            eventWriter.add(ste);
            eventWriter.add(new AttributeImpl("gml", "http://www.opengis.net/gml", "id", featureCollection.getID(), null));

            NamespaceImpl namespaceEvent = new NamespaceImpl("gml", "http://www.opengis.net/gml");
            eventWriter.add(namespaceEvent);
            NamespaceImpl namespaceEvent2 = new NamespaceImpl("wfs", "http://www.opengis.net/wfs");
            eventWriter.add(namespaceEvent2);

            FeatureType type = featureCollection.getSchema();
            String namespace = type.getName().getNamespaceURI();
            if (!namespace.equals("http://www.opengis.net/gml")) {
                String prefix    = Namespaces.getPreferredPrefix(namespace, null);
                NamespaceImpl namespaceEvent3 = new NamespaceImpl(prefix, namespace);
                eventWriter.add(namespaceEvent3);

            }

            /*
             * The boundedby part
             */
            writeBounds(featureCollection.getBounds(), eventWriter);

            // we write each feature member of the collection
            QName memberName = new QName("http://www.opengis.net/gml", "featureMember", "gml");
            FeatureIterator iterator =featureCollection.features();
            while (iterator.hasNext()) {
                final SimpleFeature f = (SimpleFeature) iterator.next();

                eventWriter.add(new StartElementEvent(memberName));
                writeFeature(f, eventWriter, false);
                eventWriter.add(new EndElementEvent(memberName));
            }

            eventWriter.add(new EndElementEvent(root));
            // we close the stream
            iterator.close();
            eventWriter.flush();
            eventWriter.close();
            
        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
    }

    private void writeBounds(JTSEnvelope2D bounds, XMLEventWriter eventWriter) throws XMLStreamException {
        if (bounds != null) {
            String srsName = null;
            if (bounds.getCoordinateReferenceSystem() != null) {
                try {
                    srsName = CRS.lookupIdentifier(Citations.URN_OGC, bounds.getCoordinateReferenceSystem(), true);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
            QName bounded = new QName("http://www.opengis.net/gml", "boundedBy", "gml");
            eventWriter.add(new StartElementEvent(bounded));
            QName env     = new QName("http://www.opengis.net/gml", "Envelope", "gml");
            eventWriter.add(new StartElementEvent(env));
            if (srsName != null) {
                eventWriter.add(new AttributeImpl("srsName", srsName));
            } else {
               eventWriter.add(new AttributeImpl("srsName", ""));
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
            eventWriter.add(new EndElementEvent(bounded));
        }
    }
}