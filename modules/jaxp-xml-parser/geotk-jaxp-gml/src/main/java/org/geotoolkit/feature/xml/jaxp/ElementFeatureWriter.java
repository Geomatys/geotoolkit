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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.Collection;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;

import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.internal.jaxb.JTSWrapperMarshallerPool;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.util.FactoryException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ElementFeatureWriter {

    /**
     * Logger for this writer.
     */
    protected static final Logger LOGGER = Logging.getLogger(JAXPStreamFeatureWriter.class);

    /**
     * The pool of marshallers used for marshalling geometries.
     */
    private static final MarshallerPool POOL = JTSWrapperMarshallerPool.getInstance();

    /**
     * Object factory to build a geometry.
     */
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    protected String schemaLocation;

    private int lastUnknowPrefix = 0;

    private final Map<String, String> unknowNamespaces = new HashMap<String, String>();


    public ElementFeatureWriter() {
    }

    public ElementFeatureWriter(final Map<String, String> schemaLocations) {

         if (schemaLocations != null && schemaLocations.size() > 0) {
             final StringBuilder sb = new StringBuilder();
             for (Entry<String,String> entry : schemaLocations.entrySet()) {
                 sb.append(entry.getKey()).append(' ').append(entry.getValue()).append(' ');
             }
             if(sb.length()>0){
                sb.setLength(sb.length()-1); //remove last ' '
             }
             schemaLocation = sb.toString();
         }
    }

    /**
     * {@inheritDoc}
     */
    public Element write(final Object candidate, final boolean fragment) throws IOException, DataStoreException, ParserConfigurationException {

        if (candidate instanceof Feature) {
            return writeFeature((Feature) candidate, null, fragment);
        } else if (candidate instanceof FeatureCollection) {
            return writeFeatureCollection((FeatureCollection) candidate, fragment, true);
        } else {
            throw new IllegalArgumentException("The given object is not a Feature or a" +
                    " FeatureCollection: "+ candidate);
        }
    }

    /**
     * Write the feature into the stream.
     *
     * @param feature The feature
     * @param root
     * @throws XMLStreamException
     */
    public Element writeFeature(final Feature feature,final Document rootDocument, boolean fragment) throws ParserConfigurationException {

        final Document document;
        if (rootDocument == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // then we have to create document-loader:
            factory.setNamespaceAware(false);
            DocumentBuilder loader = factory.newDocumentBuilder();

            // creating a new DOM-document...
            document = loader.newDocument();
        } else {
            document = rootDocument;
        }


        //the root element of the xml document (type of the feature)
        final FeatureType type = feature.getType();
        final Name typeName    = type.getName();
        final String namespace = typeName.getNamespaceURI();
        final String localPart = typeName.getLocalPart();

        final Element rootElement;
        final Prefix prefix;
        if (namespace != null) {
            prefix = getPrefix(namespace);
            rootElement = document.createElementNS(namespace, localPart);
            rootElement.setPrefix(prefix.prefix);

        } else {
            rootElement = document.createElement(localPart);
            prefix = null;
        }
        // if main document set the xmlns
        if (!fragment) {
            rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:gml", "http://www.opengis.net/gml");
        }
        final Attr idAttr = document.createAttributeNS(Namespaces.GML, "id");
        idAttr.setValue(feature.getIdentifier().getID());
        idAttr.setPrefix("gml");
        rootElement.setAttributeNodeNS(idAttr);

        if (rootDocument == null) {
            document.appendChild(rootElement);
        }
        //write properties in the type order
        for(final PropertyDescriptor desc : type.getDescriptors()){
            final Collection<Property> props = feature.getProperties(desc.getName());
            for (Property a : props) {
                final Object valueA = a.getValue();
                final PropertyType typeA = a.getType();
                final Name nameA = a.getName();
                final String nameProperty = nameA.getLocalPart();
                String namespaceProperty = nameA.getNamespaceURI();
                if (valueA instanceof Collection && !(typeA instanceof GeometryType)) {
                    for (Object value : (Collection)valueA) {
                        final Element element;
                        if (namespaceProperty != null) {
                            element = document.createElementNS(namespaceProperty, nameProperty);
                        } else {
                            element = document.createElement(nameProperty);
                        }
                        element.setTextContent(Utils.getStringValue(value));
                        if (prefix != null) {
                            element.setPrefix(prefix.prefix);
                        }
                        rootElement.appendChild(element);
                    }

                } else if (valueA instanceof Map && !(typeA instanceof GeometryType)) {
                    final Map<?,?> map = (Map)valueA;
                    for (Entry<?,?> entry : map.entrySet()) {
                        final Element element;
                        if (namespaceProperty != null) {
                            element = document.createElementNS(namespaceProperty, nameProperty);
                        } else {
                            element = document.createElement(nameProperty);
                        }
                        final Object key = entry.getKey();
                        if (key != null) {
                            element.setAttribute("name", (String)key);
                        }
                        element.setTextContent(Utils.getStringValue(entry.getValue()));
                        if (prefix != null) {
                            element.setPrefix(prefix.prefix);
                        }
                        rootElement.appendChild(element);
                    }

                } else if (!(typeA instanceof GeometryType)) {
                    String value = Utils.getStringValue(valueA);
                    if (value != null || (value == null && !a.isNillable())) {

                        if ((nameProperty.equals("name") || nameProperty.equals("description")) && !Namespaces.GML.equals(namespaceProperty)) {
                            namespaceProperty = Namespaces.GML;
                            LOGGER.warning("the property name and description of a feature must have the GML namespace");
                        }
                        final Element element;
                        if (namespaceProperty != null) {
                            element = document.createElementNS(namespaceProperty, nameProperty);
                        } else {
                            element = document.createElement(nameProperty);
                        }
                        if (value != null) {
                            element.setTextContent(value);
                        }
                        if (prefix != null) {
                            element.setPrefix(prefix.prefix);
                        }
                        rootElement.appendChild(element);
                    }

                // we add the geometry
                } else {

                    if (valueA != null) {
                        final Element element;
                        if (namespaceProperty != null) {
                            element = document.createElementNS(namespaceProperty, nameProperty);
                        } else {
                            element = document.createElement(nameProperty);
                        }
                        if (prefix != null) {
                            element.setPrefix(prefix.prefix);
                        }
                        Geometry isoGeometry = JTSUtils.toISO((com.vividsolutions.jts.geom.Geometry) valueA, type.getCoordinateReferenceSystem());
                        Marshaller marshaller = null;
                        try {
                            marshaller = POOL.acquireMarshaller();
                            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
                            marshaller.marshal(OBJECT_FACTORY.buildAnyGeometry(isoGeometry), element);
                        } catch (JAXBException ex) {
                            LOGGER.log(Level.WARNING, "JAXB Exception while marshalling the iso geometry: " + ex.getMessage(), ex);
                        } finally {
                            if (marshaller != null) {
                                POOL.release(marshaller);
                            }
                        }
                        rootElement.appendChild(element);
                    }
                }
            }
        }

        //writer.writeEndElement();
        return rootElement;
    }

    /**
     *
     * @param featureCollection
     * @param writer
     * @param fragment : true if we write in a stream, dont write start and end elements
     * @throws DataStoreException
     */
    public Element writeFeatureCollection(final FeatureCollection featureCollection, final boolean fragment, final boolean wfs) throws DataStoreException, ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // then we have to create document-loader:
        factory.setNamespaceAware(false);
        DocumentBuilder loader = factory.newDocumentBuilder();

        // creating a new DOM-document...
        Document document = loader.newDocument();

        // the XML header
        if (!fragment) {
            document.setXmlVersion("1.0");
            //writer.writeStartDocument("UTF-8", "1.0");
        }

        // the root Element
        final Element rootElement;
        if (wfs) {
            rootElement = document.createElementNS("http://www.opengis.net/wfs", "FeatureCollection");
            rootElement.setPrefix("wfs");
        } else {
            rootElement = document.createElementNS("http://www.opengis.net/gml", "FeatureCollection");
            rootElement.setPrefix("gml");
        }

        document.appendChild(rootElement);

        String collectionID = "";
        if (featureCollection.getID() != null) {
            collectionID = featureCollection.getID();
        }
        final Attr idAttribute = document.createAttributeNS(Namespaces.GML, "id");
        idAttribute.setValue(collectionID);
        idAttribute.setPrefix("gml");
        rootElement.setAttributeNodeNS(idAttribute);

        if (schemaLocation != null && !schemaLocation.equals("")) {
            rootElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", schemaLocation);
        }

        /*FeatureType type = featureCollection.getFeatureType();
        if (type != null && type.getName() != null) {
            String namespace = type.getName().getNamespaceURI();
            if (namespace != null && !namespace.equals(Namespaces.GML)) {
                Prefix prefix    = getPrefix(namespace);
                writer.writeNamespace(prefix.prefix, namespace);
            }
        }*/
        /*
         * The boundedby part
         */
        final Element boundElement = writeBounds(featureCollection.getEnvelope(), document);
        if (boundElement != null) {
            rootElement.appendChild(boundElement);
        }

        // we write each feature member of the collection
        FeatureIterator iterator = featureCollection.iterator();
        try {
            while (iterator.hasNext()) {
                final Feature f = iterator.next();
                final Element memberElement = document.createElementNS(Namespaces.GML, "featureMember");
                memberElement.setPrefix("gml");
                memberElement.appendChild(writeFeature(f, document, true));
                rootElement.appendChild(memberElement);

            }

        } finally {
            // we close the stream
            iterator.close();
        }
        return rootElement;
    }

    private Element writeBounds(final Envelope bounds, final Document document) {
        if (bounds != null) {

            String srsName = null;
            if (bounds.getCoordinateReferenceSystem() != null) {
                try {
                    srsName = IdentifiedObjects.lookupIdentifier(Citations.URN_OGC, bounds.getCoordinateReferenceSystem(), true);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }
            final Element boundElement = document.createElementNS(Namespaces.GML, "boundedBy");
            boundElement.setPrefix("gml");
            final Element envElement = document.createElementNS(Namespaces.GML, "Envelope");
            envElement.setPrefix("gml");
            if (srsName != null) {
                envElement.setAttribute("srsName", srsName);
            } else {
                envElement.setAttribute("srsName", "");
            }

            // lower corner
            final Element lower = document.createElementNS(Namespaces.GML, "lowerCorner");
            String lowValue = bounds.getLowerCorner().getOrdinate(0) + " " + bounds.getLowerCorner().getOrdinate(1);
            lower.setTextContent(lowValue);
            lower.setPrefix("gml");
            envElement.appendChild(lower);

            // upper corner
            final Element upper = document.createElementNS(Namespaces.GML, "upperCorner");
            String uppValue = bounds.getUpperCorner().getOrdinate(0) + " " + bounds.getUpperCorner().getOrdinate(1);
            upper.setTextContent(uppValue);
            upper.setPrefix("gml");
            envElement.appendChild(upper);

            boundElement.appendChild(envElement);
            return boundElement;
        }
        return null;
    }

    /**
     * Returns the prefix for the given namespace.
     *
     * @param namespace The namespace for which we want the prefix.
     */
    private Prefix getPrefix(final String namespace) {
        String prefix = Namespaces.getPreferredPrefix(namespace, null);
        boolean unknow = false;
        if (prefix == null) {
            prefix = unknowNamespaces.get(namespace);
            if (prefix == null) {
                prefix = "ns" + lastUnknowPrefix;
                lastUnknowPrefix++;
                unknow = true;
                unknowNamespaces.put(namespace, prefix);
            }
        }
        return new Prefix(unknow, prefix);
    }


    /**
     * Inner class for handling prefix and if it is already known.
     */
    private final class Prefix {
        public boolean unknow;
        public String prefix;

        public Prefix(final boolean unknow, final String prefix) {
            this.prefix = prefix;
            this.unknow = unknow;
        }
    }
}
