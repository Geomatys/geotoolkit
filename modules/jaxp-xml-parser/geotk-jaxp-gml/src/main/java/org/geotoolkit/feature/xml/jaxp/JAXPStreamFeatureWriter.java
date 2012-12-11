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
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
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
import org.geotoolkit.gml.JTStoGeometry;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.geotoolkit.internal.jaxb.JTSWrapperMarshallerPool;
import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.xml.StaxStreamWriter;
import org.opengis.feature.ComplexAttribute;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;


/**
 * Handles writing process of features using JAXP. The {@link #dispose()} method MUST be
 * called in order to release some resources.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXPStreamFeatureWriter extends StaxStreamWriter implements XmlFeatureWriter {

    /**
     * The pool of marshallers used for marshalling geometries.
     */
    @Deprecated
    private static final MarshallerPool GML_31_POOL = JTSWrapperMarshallerPool.getInstance();

    private static final MarshallerPool GML_32_POOL = GMLMarshallerPool.getInstance();

    /**
     * Object factory to build a geometry.
     */
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static final org.geotoolkit.gml.xml.v321.ObjectFactory GML32_FACTORY = new org.geotoolkit.gml.xml.v321.ObjectFactory();

    protected String schemaLocation;

    private final String gmlVersion;

    private final String wfsVersion;
    
    private final String wfsNamespace;

    private final String gmlNamespace;

    public JAXPStreamFeatureWriter() {
        this("3.1.1", "1.1.0", null);
    }

    public JAXPStreamFeatureWriter(final String gmlVersion, final String wfsVersion, final Map<String, String> schemaLocations)  {
        this.gmlVersion = gmlVersion;
        this.wfsVersion = wfsVersion;
        if (schemaLocations != null && schemaLocations.size() > 0) {
            final StringBuilder sb = new StringBuilder();
            for (Entry<String, String> entry : schemaLocations.entrySet()) {
                sb.append(entry.getKey()).append(' ').append(entry.getValue()).append(' ');
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1); //remove last ' '
            }
            schemaLocation = sb.toString();
        }
        if ("2.0.0".equals(wfsVersion)) {
            wfsNamespace = "http://www.opengis.net/wfs/2.0";
        } else {
            wfsNamespace = "http://www.opengis.net/wfs";
        }
        if ("3.2.1".equals(gmlVersion)) {
            gmlNamespace = "http://www.opengis.net/gml/3.2";
        } else {
            gmlNamespace = Namespaces.GML;
        }
    }

    public JAXPStreamFeatureWriter(final Map<String, String> schemaLocations)  {
         this("3.1.1", "1.1.0", schemaLocations);
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final Object candidate, final Object output) throws IOException,
                                               XMLStreamException, DataStoreException
    {
        setOutput(output);
        if (candidate instanceof ComplexAttribute) {
            writeFeature((ComplexAttribute) candidate,true);
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
    private void writeFeature(final ComplexAttribute feature, final boolean root) throws XMLStreamException {

        //the root element of the xml document (type of the feature)
        final ComplexType type = feature.getType();
        final Name typeName    = type.getName();
        final String namespace = typeName.getNamespaceURI();
        final String localPart = typeName.getLocalPart();
        final Identifier featureId = feature.getIdentifier();
        if (namespace != null && !namespace.isEmpty()) {
            final Prefix prefix = getPrefix(namespace);
            writer.writeStartElement(prefix.prefix, localPart, namespace);
            if (featureId != null) {
                writer.writeAttribute("gml", gmlNamespace, "id", (String)featureId.getID());
            }
            if (prefix.unknow && !root) {
                writer.writeNamespace(prefix.prefix, namespace);
            }
            if (root) {
                writer.writeNamespace("gml", gmlNamespace);
                if (!namespace.equals(gmlNamespace)) {
                    writer.writeNamespace(prefix.prefix, namespace);
                }
            }
        } else {
            writer.writeStartElement(localPart);
            if (featureId != null) {
                writer.writeAttribute("gml", gmlNamespace, "id", (String)featureId.getID());
            }
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

                if (a instanceof ComplexAttribute) {
                    if (namespaceProperty != null) {
                        writer.writeStartElement(namespaceProperty, nameProperty);
                    } else {
                        writer.writeStartElement(nameProperty);
                    }
                    writeFeature((ComplexAttribute)a, false);
                    writer.writeEndElement();

                } else if (valueA instanceof Collection && !(typeA instanceof GeometryType)) {
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
                    final Map<?,?> map = (Map)valueA;
                    for (Entry<?,?> entry : map.entrySet()) {
                        if (namespaceProperty != null) {
                            writer.writeStartElement(namespaceProperty, nameProperty);
                        } else {
                            writer.writeStartElement(nameProperty);
                        }
                        final Object key = entry.getKey();
                        if (key != null) {
                            writer.writeAttribute("name", (String)key);
                        }
                        writer.writeCharacters(Utils.getStringValue(entry.getValue()));
                        writer.writeEndElement();
                    }

                } else if (!(typeA instanceof GeometryType)) {
                    String value = Utils.getStringValue(valueA);
                    if (value != null || (value == null && !a.isNillable())) {

                        if ((nameProperty.equals("name") || nameProperty.equals("description")) && !gmlNamespace.equals(namespaceProperty)) {
                            namespaceProperty = gmlNamespace;
                            LOGGER.finer("the property name and description of a feature must have the GML namespace");
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
                        final CoordinateReferenceSystem crs = ((GeometryType)typeA).getCoordinateReferenceSystem();
                        final JAXBElement element;
                        final MarshallerPool POOL;
                        if ("3.1.1".equals(gmlVersion)) {
                            final Geometry isoGeometry = JTSUtils.toISO((com.vividsolutions.jts.geom.Geometry) valueA, crs);
                            element = OBJECT_FACTORY.buildAnyGeometry(isoGeometry);
                            POOL = GML_31_POOL;
                        } else if ("3.2.1".equals(gmlVersion)) {
                            AbstractGeometry gmlGeometry = null;
                            try {
                                gmlGeometry = JTStoGeometry.toGML(gmlVersion, (com.vividsolutions.jts.geom.Geometry) valueA,  crs);
                            } catch (FactoryException ex) {
                                LOGGER.log(Level.WARNING, "Factory exception when transforming JTS geometry to GML binding", ex);
                            }
                            element = GML32_FACTORY.buildAnyGeometry(gmlGeometry);
                            POOL = GML_32_POOL;
                        } else {
                            throw new IllegalArgumentException("Unexpected GML version:" + gmlVersion);
                        }
                        Marshaller marshaller = null;
                        try {
                            marshaller = POOL.acquireMarshaller();
                            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
                            marshaller.marshal(element, writer);
                        } catch (JAXBException ex) {
                            LOGGER.log(Level.WARNING, "JAXB Exception while marshalling the iso geometry: " + ex.getMessage(), ex);
                        } finally {
                            if (marshaller != null) {
                                POOL.release(marshaller);
                            }
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
        writer.writeStartElement("wfs", "FeatureCollection", wfsNamespace);
        String collectionID = "";
        if (featureCollection.getID() != null) {
            collectionID = featureCollection.getID();
        }
        writer.writeAttribute("gml", gmlNamespace, "id", collectionID);
        writer.writeNamespace("gml", gmlNamespace);
        writer.writeNamespace("wfs", wfsNamespace);
        if (schemaLocation != null && !schemaLocation.equals("")) {
            writer.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", schemaLocation);
        }

        FeatureType type = featureCollection.getFeatureType();
        if (type != null && type.getName() != null) {
            String namespace = type.getName().getNamespaceURI();
            if (namespace != null && !(namespace.equals(Namespaces.GML) || namespace.equals("http://www.opengis.net/gml/3.2")) && !namespace.isEmpty()) {
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

                if ("2.0.0".equals(wfsVersion)) {
                    writer.writeStartElement("wfs", "member", wfsNamespace);
                } else {
                    writer.writeStartElement("gml", "featureMember", gmlNamespace);
                }
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

    private void writeBounds(final Envelope bounds, final XMLStreamWriter streamWriter) throws XMLStreamException {
        if (bounds != null) {

            String srsName = null;
            if (bounds.getCoordinateReferenceSystem() != null) {
                try {
                    srsName = IdentifiedObjects.lookupIdentifier(Citations.URN_OGC, bounds.getCoordinateReferenceSystem(), true);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }
            streamWriter.writeStartElement("gml", "boundedBy", gmlNamespace);
            streamWriter.writeStartElement("gml", "Envelope", gmlNamespace);
            if (srsName != null) {
                streamWriter.writeAttribute("srsName", srsName);
            } else {
                streamWriter.writeAttribute("srsName", "");
            }

            // lower corner
            streamWriter.writeStartElement("gml", "lowerCorner", gmlNamespace);
            String lowValue = bounds.getLowerCorner().getOrdinate(0) + " " + bounds.getLowerCorner().getOrdinate(1);
            streamWriter.writeCharacters(lowValue);
            streamWriter.writeEndElement();

            // upper corner
            streamWriter.writeStartElement("gml", "upperCorner", gmlNamespace);
            String uppValue = bounds.getUpperCorner().getOrdinate(0) + " " + bounds.getUpperCorner().getOrdinate(1);
            streamWriter.writeCharacters(uppValue);
            streamWriter.writeEndElement();

            streamWriter.writeEndElement();
            streamWriter.writeEndElement();
        }
    }
}
