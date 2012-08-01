/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.XmlFeatureWriter;
import org.geotoolkit.gml.JTStoGeometry;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.geotoolkit.xml.StaxStreamWriter;
import org.geotoolkit.gml.xml.v321.ObjectFactory;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.xml.Namespaces;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.expression.Expression;
import org.opengis.util.FactoryException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXPStreamValueCollectionWriter extends StaxStreamWriter implements XmlFeatureWriter {

    private static final MarshallerPool GML_32_POOL = GMLMarshallerPool.getInstance();

    /**
     * Object factory to build a geometry.
     */
    private static final ObjectFactory GML32_FACTORY = new ObjectFactory();

    private static final String GML_NAMESPACE = "http://www.opengis.net/gml/3.2";
    private static final String WFS_NAMESPACE = "http://www.opengis.net/wfs/2.0";

    private final String valueReference;

    public JAXPStreamValueCollectionWriter(final String valueReference) {
        this.valueReference = valueReference;
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
        FeatureCollection collection;
        if (candidate instanceof Feature) {
           collection =  DataUtilities.collection((Feature)candidate);
        } else if (candidate instanceof FeatureCollection) {
            collection = (FeatureCollection) candidate;
        } else {
            throw new IllegalArgumentException("The given object is not a Feature or a" +
                    " FeatureCollection: "+ candidate);
        }
        writeValueCollection(collection);
    }

    /**
     * Write the feature into the stream.
     *
     * @param feature The feature
     * @throws XMLStreamException
     */
    private void writeFeature(final Feature feature) throws XMLStreamException {

        final FeatureType type = feature.getType();

        //write properties in the type order
        Expression exp = FactoryFinder.getFilterFactory(null).property(valueReference);
        Object valueA = exp.evaluate(feature);

        if (valueA instanceof Collection) {
            for (Object value : (Collection)valueA) {
                writer.writeStartElement("wfs", "member", WFS_NAMESPACE);
                writer.writeCharacters(Utils.getStringValue(value));
                writer.writeEndElement();
            }

        } else if (valueA instanceof Map) {
            final Map<?,?> map = (Map)valueA;
            for (Map.Entry<?,?> entry : map.entrySet()) {

                writer.writeStartElement("wfs", "member", WFS_NAMESPACE);
                final Object key = entry.getKey();
                if (key != null) {
                    writer.writeAttribute("name", (String)key);
                }
                writer.writeCharacters(Utils.getStringValue(entry.getValue()));
                writer.writeEndElement();
            }

        } else if (valueA instanceof com.vividsolutions.jts.geom.Geometry) {
            writer.writeStartElement("wfs", "member", WFS_NAMESPACE);

            AbstractGeometry gmlGeometry = null;
            try {
                gmlGeometry = JTStoGeometry.toGML("3.2.1", (com.vividsolutions.jts.geom.Geometry) valueA,  type.getCoordinateReferenceSystem());
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, "Factory exception when transforming JTS geometry to GML binding", ex);
            }
            final JAXBElement element = GML32_FACTORY.buildAnyGeometry(gmlGeometry);

            Marshaller marshaller = null;
            try {
                marshaller = GML_32_POOL.acquireMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
                marshaller.marshal(element, writer);
            } catch (JAXBException ex) {
                LOGGER.log(Level.WARNING, "JAXB Exception while marshalling the iso geometry: " + ex.getMessage(), ex);
            } finally {
                if (marshaller != null) {
                    GML_32_POOL.release(marshaller);
                }
            }
            writer.writeEndElement();


        // we add the geometry
        } else {
            String value = Utils.getStringValue(valueA);
            if (value != null) {
                writer.writeStartElement("wfs", "member", WFS_NAMESPACE);
                if (value != null) {
                    writer.writeCharacters(value);
                }
                writer.writeEndElement();
            }
        }
    }

    /**
     *
     * @param featureCollection
     * @param writer
     * @param fragment : true if we write in a stream, dont write start and end elements
     * @throws DataStoreException
     */
    public void writeValueCollection(final FeatureCollection featureCollection) throws DataStoreException, XMLStreamException {

        // the XML header
        writer.writeStartDocument("UTF-8", "1.0");

        // the root Element
        writer.writeStartElement("wfs", "ValueCollection", WFS_NAMESPACE);

        writer.writeNamespace("gml", GML_NAMESPACE);
        writer.writeNamespace("wfs", WFS_NAMESPACE);

        /*if (schemaLocation != null && !schemaLocation.equals("")) {
            writer.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", schemaLocation);
        }*/

        FeatureType type = featureCollection.getFeatureType();
        if (type != null && type.getName() != null) {
            String namespace = type.getName().getNamespaceURI();
            if (namespace != null && !(namespace.equals(Namespaces.GML) || namespace.equals("http://www.opengis.net/gml/3.2"))) {
                Prefix prefix    = getPrefix(namespace);
                writer.writeNamespace(prefix.prefix, namespace);
            }
        }

        // we write each feature member of the collection
        FeatureIterator iterator = featureCollection.iterator();
        try {
            while (iterator.hasNext()) {
                final Feature f = iterator.next();
                writeFeature(f);
            }

        } finally {
            // we close the stream
            iterator.close();
        }

        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
        writer.close();
    }
}
