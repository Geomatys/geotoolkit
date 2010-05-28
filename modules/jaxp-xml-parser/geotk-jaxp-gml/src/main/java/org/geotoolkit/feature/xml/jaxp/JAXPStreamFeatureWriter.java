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
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.xml.StaxStreamWriter;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.FactoryException;


/**
 * Handles writing process of features using JAXP. The {@link #dispose()} method MUST be
 * called in order to release some resources.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXPStreamFeatureWriter extends StaxStreamWriter implements XmlFeatureWriter {
    /**
     * Logger for this writer.
     */
    protected static final Logger LOGGER = Logging.getLogger(JAXPStreamFeatureWriter.class);

    /**
     * The pool of marshallers used for marshalling geometries.
     */
    private static MarshallerPool pool;
    static {
        try {
            final Map<String, String> properties = new HashMap<String, String>();
            properties.put(Marshaller.JAXB_FRAGMENT, "true");
            properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, "false");
            pool = new MarshallerPool(properties, ObjectFactory.class);
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, "JAXB Exception while initalizing the marshaller pool", ex);
        }
    }

    /**
     * Object factory to build a geometry.
     */
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    /**
     * The marshaller used by the feature writer. This marshaller must be released to the
     * {@link #pool} when closing the feature writer in calling {@link #dispose}.
     */
    protected final Marshaller marshaller;

    protected String schemaLocation;

    private int lastUnknowPrefix = 0;

    private final Map<String, String> unknowNamespaces = new HashMap<String, String>();


    public JAXPStreamFeatureWriter() throws JAXBException {
         marshaller = pool.acquireMarshaller();
         marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
    }

    public JAXPStreamFeatureWriter(final Map<String, String> schemaLocations) throws JAXBException {
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

    /**
     * Dispose the allocated resources. <strong>Must</strong> be called when closing the feautre writer.
     *
     * @throws IOException
     * @throws XMLStreamException
     */
    @Override
    public void dispose() throws IOException, XMLStreamException{
        super.dispose();
        pool.release(marshaller);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final Object candidate, final Object output) throws IOException,
                                               XMLStreamException, DataStoreException
    {
        setOutput(output);
        if (candidate instanceof Feature) {
            writeFeature((Feature) candidate,true);
        } else if (candidate instanceof FeatureCollection) {
            writeFeatureCollection((FeatureCollection) candidate,false);
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
    private void writeFeature(final Feature feature, final boolean root) throws XMLStreamException {

        //the root element of the xml document (type of the feature)
        final FeatureType type = feature.getType();
        final Name typeName = type.getName();
        final String namespace = typeName.getNamespaceURI();
        final String localPart = typeName.getLocalPart();
        if (namespace != null) {
            final Prefix prefix = getPrefix(namespace);
            writer.writeStartElement(prefix.prefix, localPart, namespace);
            writer.writeAttribute("gml", Namespaces.GML, "id", feature.getIdentifier().getID());
            if (prefix.unknow && !root) {
                writer.writeNamespace(prefix.prefix, namespace);
            }
            if (root) {
                writer.writeNamespace("gml", Namespaces.GML);
                if (!namespace.equals(Namespaces.GML)) {
                    writer.writeNamespace(prefix.prefix, namespace);
                }
            }
        } else {
            writer.writeStartElement(localPart);
            writer.writeAttribute("gml", Namespaces.GML, "id", feature.getIdentifier().getID());
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
                        if (namespaceProperty != null) {
                            writer.writeStartElement(namespaceProperty, nameProperty);
                        } else {
                            writer.writeStartElement(nameProperty);
                        }
                        writer.writeCharacters(Utils.getStringValue(value));
                        writer.writeEndElement();
                    }

                } else if (valueA instanceof Map && !(typeA instanceof GeometryType)) {
                    final Map map = (Map)valueA;
                    for (Object key : map.keySet()) {
                        if (namespaceProperty != null) {
                            writer.writeStartElement(namespaceProperty, nameProperty);
                        } else {
                            writer.writeStartElement(nameProperty);
                        }
                        if (key != null) {
                            writer.writeAttribute("name", (String)key);
                        }
                        writer.writeCharacters(Utils.getStringValue(map.get(key)));
                        writer.writeEndElement();
                    }

                } else if (!(typeA instanceof GeometryType)) {
                    String value = Utils.getStringValue(valueA);
                    if (value != null || (value == null && !a.isNillable())) {

                        if ((nameProperty.equals("name") || nameProperty.equals("description")) && !Namespaces.GML.equals(namespaceProperty)) {
                            namespaceProperty = Namespaces.GML;
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

                    if (valueA != null) {
                        if (namespaceProperty != null) {
                            writer.writeStartElement(namespaceProperty, nameProperty);
                        } else {
                            writer.writeStartElement(nameProperty);
                        }
                        Geometry isoGeometry = JTSUtils.toISO((com.vividsolutions.jts.geom.Geometry) valueA, type.getCoordinateReferenceSystem());
                        try {
                            marshaller.marshal(OBJECT_FACTORY.buildAnyGeometry(isoGeometry), writer);
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
    public void writeFeatureCollection(final FeatureCollection featureCollection, final boolean fragment)
                                                            throws DataStoreException, XMLStreamException
    {
        
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
        writer.writeAttribute("gml", Namespaces.GML, "id", collectionID);
        writer.writeNamespace("gml", Namespaces.GML);
        writer.writeNamespace("wfs", "http://www.opengis.net/wfs");
        if (schemaLocation != null && !schemaLocation.equals("")) {
            writer.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", schemaLocation);
        }

        FeatureType type = featureCollection.getFeatureType();
        if (type != null && type.getName() != null) {
            String namespace = type.getName().getNamespaceURI();
            if (namespace != null && !namespace.equals(Namespaces.GML)) {
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

                writer.writeStartElement("gml", "featureMember", Namespaces.GML);
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

            String srsName = null;
            if (bounds.getCoordinateReferenceSystem() != null) {
                try {
                    srsName = CRS.lookupIdentifier(Citations.URN_OGC, bounds.getCoordinateReferenceSystem(), true);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }
            streamWriter.writeStartElement("gml", "boundedBy", Namespaces.GML);
            streamWriter.writeStartElement("gml", "Envelope", Namespaces.GML);
            if (srsName != null) {
                streamWriter.writeAttribute("srsName", srsName);
            } else {
                streamWriter.writeAttribute("srsName", "");
            }

            // lower corner
            streamWriter.writeStartElement("gml", "lowerCorner", Namespaces.GML);
            String lowValue = bounds.getLowerCorner().getOrdinate(0) + " " + bounds.getLowerCorner().getOrdinate(1);
            streamWriter.writeCharacters(lowValue);
            streamWriter.writeEndElement();

            // upper corner
            streamWriter.writeStartElement("gml", "upperCorner", Namespaces.GML);
            String uppValue = bounds.getUpperCorner().getOrdinate(0) + " " + bounds.getUpperCorner().getOrdinate(1);
            streamWriter.writeCharacters(uppValue);
            streamWriter.writeEndElement();

            streamWriter.writeEndElement();
            streamWriter.writeEndElement();
        }
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

        public Prefix(boolean unknow, String prefix) {
            this.prefix = prefix;
            this.unknow = unknow;
        }
    }
}
