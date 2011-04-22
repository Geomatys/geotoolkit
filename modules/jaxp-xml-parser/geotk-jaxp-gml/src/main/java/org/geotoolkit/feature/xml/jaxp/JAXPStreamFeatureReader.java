/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.feature.xml.jaxb.JAXBEventHandler;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiCurve;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.internal.jaxb.LineStringPosListType;
import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.geotoolkit.internal.jaxb.PolygonType;
import org.geotoolkit.util.Converters;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.xml.StaxStreamReader;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;

import static javax.xml.stream.events.XMLEvent.*;


/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JAXPStreamFeatureReader extends StaxStreamReader implements XmlFeatureReader {

    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY,LenientFeatureFactory.class));

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml.jaxp");
    private static MarshallerPool marshallpool;

    static {
        try {
            marshallpool = new MarshallerPool(ObjectFactory.class);
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, "JAXB Exception while initalizing the marshaller pool", ex);
        }
    }
    protected List<FeatureType> featureTypes;
    protected final Unmarshaller unmarshaller;

    public JAXPStreamFeatureReader(final FeatureType featureType) throws JAXBException {
        this.featureTypes = Arrays.asList(featureType);
        this.unmarshaller = marshallpool.acquireUnmarshaller();

    }

    public JAXPStreamFeatureReader(final List<FeatureType> featureTypes) throws JAXBException {
        this.featureTypes = featureTypes;
        this.unmarshaller = marshallpool.acquireUnmarshaller();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setFeatureType(final FeatureType featureType) {
        this.featureTypes = Arrays.asList(featureType);
    }

    @Override
    public void dispose() {
        marshallpool.release(unmarshaller);
    }

    @Override
    public Object read(final Object xml) throws IOException, XMLStreamException  {
        setInput(xml);
        return read();
    }

    /**
     * Start to read An object from the XML datasource.
     * @return A feature or featureCollection described in the XML stream.
     */
    private Object read() throws XMLStreamException {
        while (reader.hasNext()) {
            final int event = reader.getEventType();

            //we are looking for the root mark
            if (event == START_ELEMENT) {

                final Name name  = Utils.getNameFromQname(reader.getName());
                final String id  = reader.getAttributeValue(0);
                final StringBuilder expectedFeatureType = new StringBuilder();

                if (name.getLocalPart().equals("FeatureCollection")) {
                    return readFeatureCollection(id);

                } else if (name.getLocalPart().equals("Transaction")) {
                    return extractFeatureFromTransaction();

                } else {
                    for (FeatureType ft : featureTypes) {
                        if (ft.getName().equals(name)) {
                            return readFeature(id, ft);
                        }
                        expectedFeatureType.append(ft.getName()).append('\n');
                    }
                }

                throw new IllegalArgumentException("The xml does not describe the same type of feature: \n " +
                                                   "Expected: " + expectedFeatureType.toString() + '\n' +
                                                   "But was: "  + name);
            }
            reader.next();
        }
        return null;
    }

    private Object readFeatureCollection(final String id) throws XMLStreamException {
        FeatureCollection collection = null;
        while (reader.hasNext()) {
            int event = reader.next();


            //we are looking for the root mark
            if (event == START_ELEMENT) {
                final Name name = Utils.getNameFromQname(reader.getName());

                String fid = null;
                if (reader.getAttributeCount() > 0) {
                    fid = reader.getAttributeValue(0);
                }

                if (name.getLocalPart().equals("featureMember") || name.getLocalPart().equals("featureMembers")) {
                    continue;

                } else if (name.getLocalPart().equals("boundedBy")) {
                    while (reader.hasNext()) {
                        event = reader.next();
                        if (event == START_ELEMENT) {
                            break;
                        }
                    }
                    String srsName = null;
                    if (reader.getAttributeCount() > 0) {
                        srsName = reader.getAttributeValue(0);
                    }
                    final JTSEnvelope2D bounds = readBounds(srsName);

                } else {
                    boolean find = false;
                    StringBuilder expectedFeatureType = new StringBuilder();
                    for (FeatureType ft : featureTypes) {
                        if (ft.getName().equals(name)) {
                            if (collection == null) {
                                collection = DataUtilities.collection(id, ft);
                            }
                            collection.add(readFeature(fid, ft));
                            find = true;
                        }
                        expectedFeatureType.append(ft.getName()).append('\n');
                    }

                    if (!find) {
                        throw new IllegalArgumentException("The xml does not describe the same type of feature: \n "
                                + "Expected: " + expectedFeatureType.toString() + '\n'
                                + "But was: " + name);
                    }
                }
            }
        }
        return collection;
    }

    private Feature readFeature(final String id, final FeatureType featureType) throws XMLStreamException {
        final Map<Name,Property> properties = new LinkedHashMap<Name, Property>();

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == START_ELEMENT) {
                final Name propName = Utils.getNameFromQname(reader.getName());

                // we skip the boundedby attribute if it's present
                if ("boundedBy".equals(propName.getLocalPart())) {
                    toTagEnd("boundedBy");
                    continue;
                }

                final String nameAttribute = reader.getAttributeValue(null, "name");
                final PropertyDescriptor pdesc = featureType.getDescriptor(propName.getLocalPart());

                if (pdesc == null){
                    throw new IllegalArgumentException("Unexpected attribute:" + propName + " not found in :\n" + featureType);
                }

                if (pdesc instanceof GeometryDescriptor) {
                    event = reader.next();
                    while (event != START_ELEMENT) {
                        event = reader.next();
                    }

                    try {
                        unmarshaller.setEventHandler(new JAXBEventHandler());
                        final JTSGeometry isoGeom;
                        final Object geometry = ((JAXBElement) unmarshaller.unmarshal(reader)).getValue();
                        if (geometry instanceof JTSGeometry) {
                            isoGeom = (JTSGeometry) geometry;
                            if (isoGeom instanceof JTSMultiCurve) {
                                ((JTSMultiCurve)isoGeom).applyCRSonChild();
                            }
                        } else if (geometry instanceof PolygonType) {
                            isoGeom = ((PolygonType)geometry).getJTSPolygon();
                        } else if (geometry instanceof LineStringPosListType) {
                            isoGeom = ((LineStringPosListType)geometry).getJTSLineString();
                        } else {
                            throw new IllegalArgumentException("unexpected geometry type:" + geometry);
                        }
                        properties.put(propName,FF.createAttribute(isoGeom.getJTSGeometry(), (AttributeDescriptor)pdesc, null));
                        
                    } catch (JAXBException ex) {
                        String msg = ex.getMessage();
                        if (msg == null && ex.getLinkedException() != null) {
                            msg = ex.getLinkedException().getMessage();
                        }
                        throw new IllegalArgumentException("JAXB exception while reading the feature geometry: " + msg, ex);
                    }

                } else {
                    final String content = reader.getElementText();
                    final Class propertyType = pdesc.getType().getBinding();
                    final Property prevProp = properties.get(propName);
                    final Object previous = (prevProp == null) ? null : prevProp.getValue();

                    if (previous == null && nameAttribute != null) {
                        final Map<String, Object> map = new LinkedHashMap<String, Object>();
                        map.put(nameAttribute, content);
                        properties.put(propName, FF.createAttribute(map, (AttributeDescriptor)pdesc, null));

                    } else if (previous == null && List.class.equals(propertyType)) {
                        final List<String> list = new ArrayList<String>();
                        list.add(content);
                        properties.put(propName, FF.createAttribute(list, (AttributeDescriptor)pdesc, null));

                    } else if (previous == null) {
                        properties.put(propName, FF.createAttribute(Converters.convert(content, propertyType),
                                (AttributeDescriptor)pdesc, null));

                    } else if (previous instanceof Map && nameAttribute != null) {
                        ((Map) previous).put(nameAttribute, content);

                    } else if (previous instanceof Map && nameAttribute == null) {
                        LOGGER.severe("unable to reader a composite attribute no name has been found");

                    } else if (previous instanceof Collection) {
                        ((Collection) previous).add(content);

                    } else {
                        final List multipleValue = new ArrayList();
                        multipleValue.add(previous);
                        multipleValue.add(Converters.convert(content, propertyType));
                        properties.put(propName, FF.createAttribute(multipleValue, (AttributeDescriptor)pdesc, null));
                    }

                }

            } else if (event == END_ELEMENT) {
                final QName q = reader.getName();
                if (q.getLocalPart().equals("featureMember")) {
                    break;
                } else if (Utils.getNameFromQname(q).equals(featureType.getName())) {
                    break;
                }
            }
        }

        return FF.createFeature(properties.values(), featureType, id);
    }

    private Object extractFeatureFromTransaction() throws XMLStreamException {
        final List<Feature> features = new ArrayList<Feature>();
        boolean insert = false;
        while (reader.hasNext()) {
            int event = reader.next();

            if (event == END_ELEMENT) {
                Name name  = Utils.getNameFromQname(reader.getName());
                if (name.getLocalPart().equals("Insert")) {
                    insert = false;
                }


            //we are looking for the root mark
            } else if (event == START_ELEMENT) {
                Name name  = Utils.getNameFromQname(reader.getName());

                if (name.getLocalPart().equals("Insert")) {
                    insert = true;
                    continue;

                } else if (insert) {

                    if (name.getLocalPart().equals("FeatureCollection")) {
                        return readFeatureCollection("");
                    }
                    boolean find = false;
                    StringBuilder expectedFeatureType = new StringBuilder();
                    for (FeatureType ft : featureTypes) {
                        if (ft.getName().equals(name)) {
                            features.add(readFeature("", ft));
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
    }

    @Override
    public Map<String, String> extractNamespace(final String xml) {
        try {
            final XMLInputFactory XMLfactory = XMLInputFactory.newInstance();
            XMLfactory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);

            final XMLStreamReader streamReader = XMLfactory.createXMLStreamReader(new StringReader(xml));
            final Map<String, String> namespaceMapping = new LinkedHashMap<String, String>();
            while (streamReader.hasNext()) {
                int event = streamReader.next();
                if (event == START_ELEMENT) {
                    for (int i = 0; i < streamReader.getNamespaceCount(); i++) {
                        namespaceMapping.put(streamReader.getNamespacePrefix(i), streamReader.getNamespaceURI(i));
                    }
                }
            }
            return namespaceMapping;
        } catch (XMLStreamException ex) {
            LOGGER.severe("XMl stream exception while extracting namespace: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Extract An envelope from the BoundedBy XML mark of a feature collection.
     *
     * @param srsName The extracted CRS identifier.
     *
     * @return An envelope of the collection bounds.
     * @throws XMLStreamException
     */
    private JTSEnvelope2D readBounds(final String srsName) throws XMLStreamException {
       JTSEnvelope2D bounds = null;
       while (reader.hasNext()) {
            int event = reader.next();
            if (event == END_ELEMENT) {
                QName endElement = reader.getName();
                if (endElement.getLocalPart().equals("boundedBy")) {
                    return null;
                }
            }

       }
        return bounds;
    }
}
