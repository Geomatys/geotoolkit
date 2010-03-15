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

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.xml.Namespaces;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.FactoryException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXPStreamFeatureWriter extends JAXPFeatureWriter {

    private int lastUnknowPrefix = 0;

    private Map<String, String> UnknowNamespaces = new HashMap<String, String>();


    public JAXPStreamFeatureWriter() throws JAXBException {
        super();
    }

    public JAXPStreamFeatureWriter(Map<String, String> schemaLocations) throws JAXBException {
        super(schemaLocations);
    }

     @Override
    public void write(SimpleFeature sf, Writer writer) {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(writer);

            // the XML header
            streamWriter.writeStartDocument("UTF-8", "1.0");

            writeFeature(sf, streamWriter, true);

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

            writeFeature(sf, streamWriter, true);

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

            writeFeature(feature, streamWriter, true);
            
            streamWriter.flush();
            streamWriter.close();
            return sw.toString();

        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while writing the feature: " + ex.getMessage());
        }
        return null;
    }

    private void writeFeature(SimpleFeature feature, XMLStreamWriter streamWriter, boolean root) {
        try {

            //the root element of the xml document (type of the feature)
            FeatureType type = feature.getType();
            String namespace = type.getName().getNamespaceURI();
            String localPart = type.getName().getLocalPart();
            if (namespace != null) {
                Prefix prefix = getPrefix(namespace);
                streamWriter.writeStartElement(prefix.prefix, localPart, namespace);
                streamWriter.writeAttribute("gml", GML_NAMESPACE, "id", feature.getID());
                if (prefix.unknow && !root) {
                    streamWriter.writeNamespace(prefix.prefix, namespace);
                }
                if (root) {
                    streamWriter.writeNamespace("gml", GML_NAMESPACE);
                    if (!namespace.equals(GML_NAMESPACE)) {
                        streamWriter.writeNamespace(prefix.prefix, namespace);
                    }
                }
            } else {
                streamWriter.writeStartElement(localPart);
                streamWriter.writeAttribute("gml", GML_NAMESPACE, "id", feature.getID());
            }
            


            //the simple nodes (attributes of the feature)
            for (Property a : feature.getProperties()) {

                if (a.getValue() instanceof Collection && !(a.getType() instanceof GeometryType)) {
                    String namespaceProperty = a.getName().getNamespaceURI();
                    for (Object value : (Collection)a.getValue()) {
                        if (namespaceProperty != null) {
                            streamWriter.writeStartElement(namespaceProperty, a.getName().getLocalPart());
                        } else {
                            streamWriter.writeStartElement(a.getName().getLocalPart());
                        }
                        streamWriter.writeCharacters(Utils.getStringValue(value));
                        streamWriter.writeEndElement();
                    }

                } else if (a.getValue() instanceof Map && !(a.getType() instanceof GeometryType)) {
                    String namespaceProperty = a.getName().getNamespaceURI();
                    Map map = (Map)a.getValue();
                    for (Object key : map.keySet()) {
                        if (namespaceProperty != null) {
                            streamWriter.writeStartElement(namespaceProperty, a.getName().getLocalPart());
                        } else {
                            streamWriter.writeStartElement(a.getName().getLocalPart());
                        }
                        if (key != null) {
                            streamWriter.writeAttribute("name", (String)key);
                        }
                        streamWriter.writeCharacters(Utils.getStringValue(map.get(key)));
                        streamWriter.writeEndElement();
                    }

                } else if (!(a.getType() instanceof GeometryType)) {
                    String value = Utils.getStringValue(a.getValue());
                    if (value != null || (value == null && !a.isNillable())) {
                        
                        String namespaceProperty = a.getName().getNamespaceURI();
                        String nameProperty      = a.getName().getLocalPart();
                        if ((nameProperty.equals("name") || nameProperty.equals("description")) && !GML_NAMESPACE.equals(namespaceProperty)) {
                            namespaceProperty = GML_NAMESPACE;
                            LOGGER.warning("the property name and description of a feature must have the GML namespace");
                        }
                        if (namespaceProperty != null) {
                            streamWriter.writeStartElement(namespaceProperty, nameProperty);
                        } else {
                            streamWriter.writeStartElement(nameProperty);
                        }
                        if (value != null) {
                            streamWriter.writeCharacters(value);
                        }
                        streamWriter.writeEndElement();
                    }

                // we add the geometry
                } else {
                    
                    if (a.getValue() != null) {
                        Name geometryName        = a.getName();
                        String namespaceProperty = geometryName.getNamespaceURI();
                    
                        if (namespaceProperty != null) {
                            streamWriter.writeStartElement(geometryName.getNamespaceURI(), geometryName.getLocalPart());
                        } else {
                            streamWriter.writeStartElement(geometryName.getLocalPart());
                        }
                        Geometry isoGeometry = JTSUtils.toISO((com.vividsolutions.jts.geom.Geometry) a.getValue(), feature.getFeatureType().getCoordinateReferenceSystem());
                        try {
                            marshaller.marshal(factory.buildAnyGeometry(isoGeometry), streamWriter);
                        } catch (JAXBException ex) {
                            LOGGER.log(Level.SEVERE, "JAXB Exception while marshalling the iso geometry: " + ex.getMessage(), ex);
                        }
                        streamWriter.writeEndElement();
                    }
                }
            }

            
            if (feature.getDefaultGeometry() != null) {

                
            }

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

            writeFeatureCollection(fc, streamWriter,false);

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        } catch (DataStoreException ex) {
            LOGGER.log(Level.SEVERE, "DataStore exception exception while writing the feature: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void write(FeatureCollection fc, OutputStream out) {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter streamWriter    = outputFactory.createXMLStreamWriter(out);

            writeFeatureCollection(fc, streamWriter,false);

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        } catch (DataStoreException ex) {
            LOGGER.log(Level.SEVERE, "DataStore exception exception while writing the feature: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public String write(FeatureCollection featureCollection) {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            StringWriter sw                = new StringWriter();
            XMLStreamWriter streamWriter   = outputFactory.createXMLStreamWriter(sw);

            writeFeatureCollection(featureCollection, streamWriter,false);
            return sw.toString();

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        } catch (DataStoreException ex) {
            LOGGER.log(Level.SEVERE, "DataStore exception exception while writing the feature: " + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     *
     * @param featureCollection
     * @param streamWriter
     * @param fragment : true if we write in a stream, dont write start and end elements
     * @throws DataStoreException
     */
    public void writeFeatureCollection(FeatureCollection featureCollection, XMLStreamWriter streamWriter, boolean fragment) throws DataStoreException {
        try {
            // the XML header
            if(!fragment){
                streamWriter.writeStartDocument("UTF-8", "1.0");
            }

            // the root Element
            streamWriter.writeStartElement("wfs", "FeatureCollection", "http://www.opengis.net/wfs");
            String collectionID = "";
            if (featureCollection.getID() != null) {
                collectionID = featureCollection.getID();
            }
            streamWriter.writeAttribute("gml", GML_NAMESPACE, "id", collectionID);
            streamWriter.writeNamespace("gml", GML_NAMESPACE);
            streamWriter.writeNamespace("wfs", "http://www.opengis.net/wfs");
            if (schemaLocation != null && !schemaLocation.equals("")) {
                streamWriter.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
                streamWriter.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", schemaLocation);
            }

            FeatureType type = featureCollection.getFeatureType();
            if (type != null && type.getName() != null) {
                String namespace = type.getName().getNamespaceURI();
                if (namespace != null && !namespace.equals(GML_NAMESPACE)) {
                    Prefix prefix    = getPrefix(namespace);
                    streamWriter.writeNamespace(prefix.prefix, namespace);
                }
            }
             /*
             * The boundedby part
             */
            writeBounds(featureCollection.getEnvelope(), streamWriter);

            // we write each feature member of the collection
            FeatureIterator iterator =featureCollection.iterator();
            try {
                while (iterator.hasNext()) {
                    final SimpleFeature f = (SimpleFeature) iterator.next();

                    streamWriter.writeStartElement("gml", "featureMember", GML_NAMESPACE);
                    writeFeature(f, streamWriter, false);
                    streamWriter.writeEndElement();
                }

            } finally {
                // we close the stream
                iterator.close();
            }

            streamWriter.writeEndElement();

            if(!fragment){
                streamWriter.writeEndDocument();
            }
            
            streamWriter.flush();

            if(!fragment){
                streamWriter.close();
            }

        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while writing the feature: " + ex.getMessage(), ex);
        }
    }

    private void writeBounds(Envelope bounds, XMLStreamWriter streamWriter) throws XMLStreamException {
        if (bounds != null) {
            String srsName = null;
            if (bounds.getCoordinateReferenceSystem() != null) {
                try {
                    srsName = CRS.lookupIdentifier(Citations.URN_OGC, bounds.getCoordinateReferenceSystem(), true);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
            streamWriter.writeStartElement("gml", "boundedBy", GML_NAMESPACE);
            streamWriter.writeStartElement("gml", "Envelope", GML_NAMESPACE);
            if (srsName != null) {
                streamWriter.writeAttribute("srsName", srsName);
            } else {
                streamWriter.writeAttribute("srsName", "");
            }

            // lower corner
            streamWriter.writeStartElement("gml", "lowerCorner", GML_NAMESPACE);
            String lowValue = bounds.getLowerCorner().getOrdinate(0) + " " + bounds.getLowerCorner().getOrdinate(1);
            streamWriter.writeCharacters(lowValue);
            streamWriter.writeEndElement();

            // upper corner
            streamWriter.writeStartElement("gml", "upperCorner", GML_NAMESPACE);
            String uppValue = bounds.getUpperCorner().getOrdinate(0) + " " + bounds.getUpperCorner().getOrdinate(1);
            streamWriter.writeCharacters(uppValue);
            streamWriter.writeEndElement();

            streamWriter.writeEndElement();
            streamWriter.writeEndElement();
        }
    }

    private Prefix getPrefix(String namespace) {
        String prefix = Namespaces.getPreferredPrefix(namespace, null);
        boolean unknow = false;
        if (prefix == null) {
            prefix = UnknowNamespaces.get(namespace);
            if (prefix == null) {
                prefix = "ns" + lastUnknowPrefix;
                lastUnknowPrefix++;
                unknow = true;
                UnknowNamespaces.put(namespace, prefix);
            }
        }
        return new Prefix(unknow, prefix);
    }

    private class Prefix {
        public boolean unknow;
        public String prefix;

        public Prefix(boolean unknow, String prefix) {
            this.prefix = prefix;
            this.unknow = unknow;
        }
    }
}
