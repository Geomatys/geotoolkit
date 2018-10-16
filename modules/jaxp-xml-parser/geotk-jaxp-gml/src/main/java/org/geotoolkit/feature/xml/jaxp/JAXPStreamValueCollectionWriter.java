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
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.data.FeatureStoreUtilities;
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
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Attribute;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
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
    public void write(final Object candidate, final Object output) throws IOException, XMLStreamException, DataStoreException {
        write(candidate, output, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final Object candidate, final Object output, final Integer nbMatched) throws IOException, XMLStreamException, DataStoreException {
        setOutput(output);
        FeatureCollection collection;
        if (candidate instanceof Feature) {
           collection =  FeatureStoreUtilities.collection((Feature)candidate);
        } else if (candidate instanceof FeatureCollection) {
            collection = (FeatureCollection) candidate;
        } else {
            throw new IllegalArgumentException("The given object is not a Feature or a" +
                    " FeatureCollection: "+ candidate);
        }
        writeValueCollection(collection, nbMatched);
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
                writeValue(value);
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
                writeValue(entry.getValue());
                writer.writeEndElement();
            }

        } else if (valueA != null && valueA.getClass().isArray()) {
            final int length = Array.getLength(valueA);
            for (int i = 0; i < length; i++){
                writer.writeStartElement("wfs", "member", WFS_NAMESPACE);
                final Object value = Array.get(valueA, i);
                final String textValue;
                if (value != null && value.getClass().isArray()) { // matrix
                    final StringBuilder sb = new StringBuilder();
                    final int length2 = Array.getLength(value);
                    for (int j = 0; j < length2; j++) {
                        final Object subValue = Array.get(value, j);
                        sb.append(Utils.getStringValue(subValue)).append(" ");
                    }
                    textValue = sb.toString();
                } else {
                    textValue = Utils.getStringValue(value);
                }
                writer.writeCharacters(textValue);
                writer.writeEndElement();

            }

         } else if (valueA instanceof org.locationtech.jts.geom.Geometry) {
            writer.writeStartElement("wfs", "member", WFS_NAMESPACE);

            AbstractGeometry gmlGeometry = null;
            try {
                gmlGeometry = JTStoGeometry.toGML("3.2.1", (org.locationtech.jts.geom.Geometry) valueA,  FeatureExt.getCRS(type));
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, "Factory exception when transforming JTS geometry to GML binding", ex);
            }
            final JAXBElement element = GML32_FACTORY.buildAnyGeometry(gmlGeometry);

            try {
                final Marshaller marshaller;
                marshaller = GML_32_POOL.acquireMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
                marshal(marshaller, element);
                GML_32_POOL.recycle(marshaller);
            } catch (JAXBException ex) {
                LOGGER.log(Level.WARNING, "JAXB Exception while marshalling the iso geometry: " + ex.getMessage(), ex);
            }
            writer.writeEndElement();


        } else {
            String value = Utils.getStringValue(valueA);
            if (value != null) {
                writer.writeStartElement("wfs", "member", WFS_NAMESPACE);
                writeValue(value);
                writer.writeEndElement();
            }
        }
    }

    private void writeValue(final Object value) throws XMLStreamException {
        if (value instanceof Feature) {
            final JAXPStreamFeatureWriter featureWriter = new JAXPStreamFeatureWriter("3.2.1", "2.0.0", null);
            try {
                featureWriter.write(value, getWriter());
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            } catch (DataStoreException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        } else if (value instanceof Attribute) {
            final Attribute att = (Attribute) value;
            final Object attValue = att.getValue();
            if (attValue instanceof Collection) {
                for (Object o : (Collection) attValue) {
                    writer.writeStartElement(NamesExt.getNamespace(att.getName()), att.getName().tip().toString());
                    writer.writeCharacters(Utils.getStringValue(o));
                    writer.writeEndElement();
                }
            } else {
                writer.writeStartElement(NamesExt.getNamespace(att.getName()), att.getName().tip().toString());
                writer.writeCharacters(Utils.getStringValue(attValue));
                writer.writeEndElement();
            }
        } else {
            writer.writeCharacters(Utils.getStringValue(value));
        }
    }
    /**
     *
     * @param featureCollection
     * @param writer
     * @param fragment : true if we write in a stream, dont write start and end elements
     * @throws DataStoreException
     */
    public void writeValueCollection(final FeatureCollection featureCollection, final Integer nbMatched) throws DataStoreException, XMLStreamException {

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

        /*
         * Other WFS value collection attribute
         */
        writer.writeAttribute("numberReturned", Integer.toString(featureCollection.size()));
        if (nbMatched != null) {
            writer.writeAttribute("numberMatched", Integer.toString(nbMatched));
        }


        FeatureType type = featureCollection.getType();
        if (type != null && type.getName() != null) {
            String namespace = NamesExt.getNamespace(type.getName());
            if (namespace != null && !(namespace.equals("http://www.opengis.net/gml") || namespace.equals("http://www.opengis.net/gml/3.2"))) {
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
