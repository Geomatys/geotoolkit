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

import com.vividsolutions.jts.geom.Geometry;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import org.geotoolkit.data.DefaultFeatureCollection;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.internal.jaxb.PolygonType;
import org.geotoolkit.util.Converters;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXPStreamFeatureReader extends JAXPFeatureReader {

    public JAXPStreamFeatureReader(FeatureType featureType) throws JAXBException {
        super(featureType);
    }

    public JAXPStreamFeatureReader(List<FeatureType> featureTypes) throws JAXBException {
        super(featureTypes);
    }

    @Override
    public Object read(String xml)  {
        try {
            XMLInputFactory XMLfactory = XMLInputFactory.newInstance();
            XMLfactory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);

            XMLStreamReader streamReader = XMLfactory.createXMLStreamReader(new StringReader(xml));
            return read(streamReader);
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream initializing the stream reader: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public Object read(InputStream in) {
        try {
            XMLInputFactory XMLfactory = XMLInputFactory.newInstance();
            XMLfactory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);

            XMLStreamReader streamReader = XMLfactory.createXMLStreamReader(in);
            return read(streamReader);
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream initializing the stream reader: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public Object read(Reader reader) {
        try {
            XMLInputFactory XMLfactory = XMLInputFactory.newInstance();
            XMLfactory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);

            XMLStreamReader streamReader = XMLfactory.createXMLStreamReader(reader);
            return read(streamReader);
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream initializing the stream reader: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Start to read An object from the XML datasource.
     *
     * @param eventReader The XML event reader.
     * @return A feature or featureCollection described in the XML stream.
     */
    private Object read(XMLStreamReader streamReader) {
        try {
            while (streamReader.hasNext()) {
                int event = streamReader.next();


                //we are looking for the root mark
                if (event == XMLEvent.START_ELEMENT) {
                    
                    Name name  = Utils.getNameFromQname(streamReader.getName());
                    String id  = streamReader.getAttributeValue(0);
                    StringBuilder expectedFeatureType = new StringBuilder();

                    if (name.getLocalPart().equals("FeatureCollection")) {
                        return readFeatureCollection(streamReader, id);

                    } else if (name.getLocalPart().equals("Transaction")) {
                        return extractFeatureFromTransaction(streamReader);

                    } else {
                        for (FeatureType ft : featureTypes) {
                            if (ft.getName().equals(name)) {
                                return readFeature(streamReader, id, ft);
                            }
                            expectedFeatureType.append(name).append('\n');
                        }
                    }

                    throw new IllegalArgumentException("The xml does not describe the same type of feature: \n " +
                                                       "Expected: " + expectedFeatureType.toString() + '\n' +
                                                       "But was: "  + name);
                    
                }
            }
        } catch (XMLStreamException ex) {
            LOGGER.log(Level.SEVERE, "XMl stream exception while reading the feature: " + ex.getMessage(), ex);
        }
        return null;
    }

    private Object readFeatureCollection(XMLStreamReader streamReader, String id) {
        FeatureCollection collection = null;
        try {
            while (streamReader.hasNext()) {
                int event = streamReader.next();
               

                //we are looking for the root mark
                if (event == XMLEvent.START_ELEMENT) {
                    Name name  = Utils.getNameFromQname(streamReader.getName());

                    String fid = null;
                    if (streamReader.getAttributeCount() > 0) {
                        fid = streamReader.getAttributeValue(0);
                    }
                    
                    if (name.getLocalPart().equals("featureMember")) {
                        continue;

                    } else if (name.getLocalPart().equals("boundedBy")) {
                         while (streamReader.hasNext()) {
                            event = streamReader.next();
                            if (event == XMLEvent.START_ELEMENT) break;
                         }
                        String srsName = null;
                        if (streamReader.getAttributeCount() > 0) {
                            srsName = streamReader.getAttributeValue(0);
                        }
                        JTSEnvelope2D bounds = readBounds(streamReader, srsName);

                    } else {
                        boolean find = false;
                        StringBuilder expectedFeatureType = new StringBuilder();
                        for (FeatureType ft : featureTypes) {
                            if (ft.getName().equals(name)) {
                                if (collection == null) {
                                    collection = new DefaultFeatureCollection(id, ft, SimpleFeature.class);
                                }
                                collection.add(readFeature(streamReader, fid, ft));
                                find = true;
                            }
                            expectedFeatureType.append(ft.getName()).append('\n');
                        }
                        
                        if (!find) {
                            throw new IllegalArgumentException("The xml does not describe the same type of feature: \n " +
                                                               "Expected: " + expectedFeatureType.toString()     + '\n'  +
                                                               "But was: "  + name);
                        }
                    }
                }
            }
            return collection;
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while reading the feature: " + ex.getMessage());
        }
        return null;
    }

    private SimpleFeature readFeature(XMLStreamReader streamReader, String id, FeatureType featureType) {
        final SimpleFeatureBuilder builder = new SimpleFeatureBuilder((SimpleFeatureType) featureType);
        //String geometryName = featureType.getGeometryDescriptor().getName().getLocalPart();
        try {
            int nbAttribute            = 0;
            final Map<QName, Object> values  = new HashMap<QName, Object>();


            while (streamReader.hasNext()) {
                int event = streamReader.next();

                if (event == XMLEvent.START_ELEMENT) {
                    nbAttribute++;
                    final QName q              = streamReader.getName();
                    final String nameAttribute = streamReader.getAttributeValue(null, "name");
                    final PropertyDescriptor pdesc = featureType.getDescriptor(Utils.getNameFromQname(q).getLocalPart());

                    if(pdesc == null){
                        final StringBuilder exp = new StringBuilder("expected ones are:").append('\n');
                        for (PropertyDescriptor pd : featureType.getDescriptors()) {
                            exp.append(pd.getName().getLocalPart()).append('\n');
                        }
                        throw new IllegalArgumentException("unexpected attribute:" + q.getLocalPart() + '\n' + exp.toString());
                    }

                    if(pdesc instanceof GeometryDescriptor){
                        event = streamReader.next();
                        while (event != XMLEvent.START_ELEMENT) {
                            event = streamReader.next();
                        }

                        try {
                            JTSGeometry isoGeom;
                            Object geometry = ((JAXBElement) unmarshaller.unmarshal(streamReader)).getValue();
                            if (geometry instanceof JTSGeometry) {
                                isoGeom = (JTSGeometry) geometry;
                            } else if (geometry instanceof PolygonType) {
                                isoGeom = ((PolygonType)geometry).getJTSPolygon();
                            } else {
                                throw new IllegalArgumentException("unexpected geometry type:" + geometry);
                            }
                            Geometry jtsGeom = isoGeom.getJTSGeometry();
                            values.put(q, jtsGeom);
                        } catch (JAXBException ex) {
                            LOGGER.severe("JAXB exception while reading the feature geometry: " + ex.getMessage());
                            ex.printStackTrace();
                        }

                    }else{
                        if (streamReader.next() != XMLEvent.CHARACTERS){
                            LOGGER.severe("unexpected event, was waiting for CHARACTERS event.");
                        } else {
                            final String content = streamReader.getText();

                            final Class propertyType = pdesc.getType().getBinding();
                            final Object previous    = values.get(q);

                            if (previous == null && nameAttribute != null) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put(nameAttribute, Converters.convert(content, propertyType));
                                values.put(q, map);

                            } else if (previous == null) {
                                values.put(q, Converters.convert(content, propertyType));

                            } else if (previous instanceof Map && nameAttribute != null) {
                                ((Map) previous).put(nameAttribute, Converters.convert(content, propertyType));

                            } else if (previous instanceof Map && nameAttribute == null) {
                                LOGGER.severe("unable to reader a composite attribute no name has been found");

                            } else if (previous instanceof Collection) {
                                ((Collection) previous).add(Converters.convert(content, propertyType));

                            } else {
                                List multipleValue = new ArrayList();
                                multipleValue.add(previous);
                                multipleValue.add(Converters.convert(content, propertyType));
                                values.put(q, multipleValue);
                            }
                        }

                    }
                   
                    // we fill  the builder with the properties
                    for (Entry<QName, Object> entry : values.entrySet()) {
                        builder.set(entry.getKey().getLocalPart(), entry.getValue());
                    }
                    

                } else if (event == XMLEvent.END_ELEMENT) {
                    final QName q = streamReader.getName();
                    if (q.getLocalPart().equals("featureMember")) {
                        break;
                    } else if (Utils.getNameFromQname(q).equals(featureType.getName())) {
                        break;
                    }
                }
            }

            return builder.buildFeature(id);

            
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while reading the feature: " + ex.getMessage());
        }
        return null;
    }

    private List<SimpleFeature> extractFeatureFromTransaction(XMLStreamReader streamReader) {
        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        try {
            boolean insert = false;
            while (streamReader.hasNext()) {
                int event = streamReader.next();

                if (event == XMLEvent.END_ELEMENT) {
                    Name name  = Utils.getNameFromQname(streamReader.getName());
                    if (name.getLocalPart().equals("Insert")) {
                        insert = false;
                    }


                //we are looking for the root mark
                } else if (event == XMLEvent.START_ELEMENT) {
                    Name name  = Utils.getNameFromQname(streamReader.getName());

                    if (name.getLocalPart().equals("Insert")) {
                        insert = true;
                        continue;

                    } else if (insert) {

                        boolean find = false;
                        StringBuilder expectedFeatureType = new StringBuilder();
                        for (FeatureType ft : featureTypes) {
                            if (ft.getName().equals(name)) {
                                features.add(readFeature(streamReader, "", ft));
                                find = true;
                            }
                            expectedFeatureType.append(ft.getName()).append('\n');
                        }

                        if (!find) {
                            throw new IllegalArgumentException("The xml does not describe the same type of feature: \n " +
                                                               "Expected: " + expectedFeatureType.toString()     + '\n'  +
                                                               "But was: "  + name);
                        }
                    }
                }
            }
            return features;
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while reading the feature: " + ex.getMessage());
        }
        return null;
    }
    
    /**
     * Extract An envelope from the BoundedBy XML mark of a feature collection.
     *
     * @param eventReader The XML event reader.
     * @param srsName The extracted CRS identifier.
     *
     * @return An envelope of the collection bounds.
     * @throws XMLStreamException
     */
    private JTSEnvelope2D readBounds(XMLStreamReader streamReader, String srsName) throws XMLStreamException {
       JTSEnvelope2D bounds = null;
       while (streamReader.hasNext()) {
            int event = streamReader.next();
            if (event == XMLEvent.END_ELEMENT) {
                QName endElement = streamReader.getName();
                if (endElement.getLocalPart().equals("boundedBy")) {
                    return null;
                }
            }

       }
        return bounds;
    }
}
