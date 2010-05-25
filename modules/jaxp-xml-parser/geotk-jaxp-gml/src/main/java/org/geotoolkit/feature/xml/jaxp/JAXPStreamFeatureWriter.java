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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.XmlFeatureWriter;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.xml.StaxStreamWriter;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.FactoryException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXPStreamFeatureWriter extends StaxStreamWriter implements XmlFeatureWriter {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml.jaxp");

    protected String schemaLocation;

    private static MarshallerPool pool;
    static {
        try {
            Map<String, String> properties = new HashMap<String, String>();
            properties.put(Marshaller.JAXB_FRAGMENT, "true");
            properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, "false");
            pool = new MarshallerPool(properties, ObjectFactory.class);
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, "JAXB Exception while initalizing the marshaller pool", ex);
        }
    }

    protected static ObjectFactory factory = new ObjectFactory();

    protected final Marshaller marshaller;

    public static final String GML_NAMESPACE = "http://www.opengis.net/gml";

    private int lastUnknowPrefix = 0;

    private Map<String, String> UnknowNamespaces = new HashMap<String, String>();


    public JAXPStreamFeatureWriter() throws JAXBException {
         marshaller = pool.acquireMarshaller();
         marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
    }

    public JAXPStreamFeatureWriter(Map<String, String> schemaLocations) throws JAXBException {
         marshaller = pool.acquireMarshaller();
         marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
         if (schemaLocations != null && schemaLocations.size() > 0) {
             schemaLocation = "";
             for (String s : schemaLocations.keySet()) {
                 schemaLocation = schemaLocation + s + " " + schemaLocations.get(s) + " ";
             }
             schemaLocation = schemaLocation.substring(0, schemaLocation.length() - 1);
         }
    }

    @Override
    public void dispose() {
        pool.release(marshaller);
    }

    @Override
    public void write(Object candidate, Object output) throws IOException, XMLStreamException, DataStoreException{
        setOutput(output);
        if(candidate instanceof Feature){
            writeFeature((Feature) candidate,true);
        }else if(candidate instanceof FeatureCollection){
            writeFeatureCollection((FeatureCollection) candidate,false);
        }
    }
    
    private void writeFeature(Feature feature, boolean root) throws XMLStreamException {

        //the root element of the xml document (type of the feature)
        final FeatureType type = feature.getType();
        final String namespace = type.getName().getNamespaceURI();
        final String localPart = type.getName().getLocalPart();
        if (namespace != null) {
            final Prefix prefix = getPrefix(namespace);
            writer.writeStartElement(prefix.prefix, localPart, namespace);
            writer.writeAttribute("gml", GML_NAMESPACE, "id", feature.getIdentifier().getID());
            if (prefix.unknow && !root) {
                writer.writeNamespace(prefix.prefix, namespace);
            }
            if (root) {
                writer.writeNamespace("gml", GML_NAMESPACE);
                if (!namespace.equals(GML_NAMESPACE)) {
                    writer.writeNamespace(prefix.prefix, namespace);
                }
            }
        } else {
            writer.writeStartElement(localPart);
            writer.writeAttribute("gml", GML_NAMESPACE, "id", feature.getIdentifier().getID());
        }

        //write properties in the type order
        for(final PropertyDescriptor desc : type.getDescriptors()){
            final Collection<Property> props = feature.getProperties(desc.getName());
            for (Property a : props) {

                if (a.getValue() instanceof Collection && !(a.getType() instanceof GeometryType)) {
                    String namespaceProperty = a.getName().getNamespaceURI();
                    for (Object value : (Collection)a.getValue()) {
                        if (namespaceProperty != null) {
                            writer.writeStartElement(namespaceProperty, a.getName().getLocalPart());
                        } else {
                            writer.writeStartElement(a.getName().getLocalPart());
                        }
                        writer.writeCharacters(Utils.getStringValue(value));
                        writer.writeEndElement();
                    }

                } else if (a.getValue() instanceof Map && !(a.getType() instanceof GeometryType)) {
                    String namespaceProperty = a.getName().getNamespaceURI();
                    Map map = (Map)a.getValue();
                    for (Object key : map.keySet()) {
                        if (namespaceProperty != null) {
                            writer.writeStartElement(namespaceProperty, a.getName().getLocalPart());
                        } else {
                            writer.writeStartElement(a.getName().getLocalPart());
                        }
                        if (key != null) {
                            writer.writeAttribute("name", (String)key);
                        }
                        writer.writeCharacters(Utils.getStringValue(map.get(key)));
                        writer.writeEndElement();
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
                            writer.writeStartElement(namespaceProperty, nameProperty);
                        } else {
                            writer.writeStartElement(nameProperty);
                        }
                        if (value != null) {
                            writer.writeCharacters(value);
                        }
                        writer.writeEndElement();
                    }

                // we add the geometry
                } else {

                    if (a.getValue() != null) {
                        Name geometryName        = a.getName();
                        String namespaceProperty = geometryName.getNamespaceURI();

                        if (namespaceProperty != null) {
                            writer.writeStartElement(geometryName.getNamespaceURI(), geometryName.getLocalPart());
                        } else {
                            writer.writeStartElement(geometryName.getLocalPart());
                        }
                        Geometry isoGeometry = JTSUtils.toISO((com.vividsolutions.jts.geom.Geometry) a.getValue(), feature.getType().getCoordinateReferenceSystem());
                        try {
                            marshaller.marshal(factory.buildAnyGeometry(isoGeometry), writer);
                        } catch (JAXBException ex) {
                            LOGGER.log(Level.WARNING, "JAXB Exception while marshalling the iso geometry: " + ex.getMessage(), ex);
                        }
                        writer.writeEndElement();
                    }
                }
            }
        }

        writer.writeEndElement();
    }

    /**
     *
     * @param featureCollection
     * @param writer
     * @param fragment : true if we write in a stream, dont write start and end elements
     * @throws DataStoreException
     */
    public void writeFeatureCollection(FeatureCollection featureCollection, boolean fragment) throws DataStoreException, XMLStreamException {
        
        // the XML header
        if(!fragment){
            writer.writeStartDocument("UTF-8", "1.0");
        }

        // the root Element
        writer.writeStartElement("wfs", "FeatureCollection", "http://www.opengis.net/wfs");
        String collectionID = "";
        if (featureCollection.getID() != null) {
            collectionID = featureCollection.getID();
        }
        writer.writeAttribute("gml", GML_NAMESPACE, "id", collectionID);
        writer.writeNamespace("gml", GML_NAMESPACE);
        writer.writeNamespace("wfs", "http://www.opengis.net/wfs");
        if (schemaLocation != null && !schemaLocation.equals("")) {
            writer.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", schemaLocation);
        }

        FeatureType type = featureCollection.getFeatureType();
        if (type != null && type.getName() != null) {
            String namespace = type.getName().getNamespaceURI();
            if (namespace != null && !namespace.equals(GML_NAMESPACE)) {
                Prefix prefix    = getPrefix(namespace);
                writer.writeNamespace(prefix.prefix, namespace);
            }
        }
         /*
         * The boundedby part
         */
        writeBounds(featureCollection.getEnvelope(), writer);

        // we write each feature member of the collection
        FeatureIterator iterator = featureCollection.iterator();
        try {
            while (iterator.hasNext()) {
                final Feature f = iterator.next();

                writer.writeStartElement("gml", "featureMember", GML_NAMESPACE);
                writeFeature(f, false);
                writer.writeEndElement();
            }

        } finally {
            // we close the stream
            iterator.close();
        }

        writer.writeEndElement();

        if(!fragment){
            writer.writeEndDocument();
        }

        writer.flush();

        if(!fragment){
            writer.close();
        }

    }

    private void writeBounds(Envelope bounds, XMLStreamWriter streamWriter) throws XMLStreamException {
        if (bounds != null) {

            System.out.println("BOUNDS : " + bounds +"   " + bounds.getCoordinateReferenceSystem());

            String srsName = null;
            if (bounds.getCoordinateReferenceSystem() != null) {
                try {
                    srsName = CRS.lookupIdentifier(Citations.URN_OGC, bounds.getCoordinateReferenceSystem(), true);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
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
