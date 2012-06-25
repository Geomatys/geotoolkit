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

import com.vividsolutions.jts.geom.Geometry;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
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
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.JTSGeometry;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.JTSMultiCurve;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.internal.jaxb.JTSWrapperMarshallerPool;
import org.geotoolkit.internal.jaxb.LineStringPosListType;
import org.geotoolkit.internal.jaxb.PolygonType;
import org.geotoolkit.util.Converters;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.xml.Namespaces;
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
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.opengis.util.FactoryException;


/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JAXPStreamFeatureReader extends StaxStreamReader implements XmlFeatureReader {

    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY,LenientFeatureFactory.class));

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml.jaxp");
    
    protected List<FeatureType> featureTypes;

    public static final String READ_EMBEDDED_FEATURE_TYPE = "readEmbeddedFeatureType";
    
    public static final String BINDING_PACKAGE = "bindingPackage";
    
    public JAXPStreamFeatureReader() {
        this(new ArrayList<FeatureType>());
    }
    
    public JAXPStreamFeatureReader(final FeatureType featureType) {
        this(Arrays.asList(featureType));
    }

    public JAXPStreamFeatureReader(final List<FeatureType> featureTypes) {
        this.featureTypes = featureTypes;
        this.properties.put(READ_EMBEDDED_FEATURE_TYPE, false);
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
        // do nothing
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

                // we search an embedded featureType description
                String schemaLocation = reader.getAttributeValue(Namespaces.XSI, "schemaLocation");
                if (isReadEmbeddedFeatureType() && schemaLocation != null) {
                    final JAXBFeatureTypeReader featureTypeReader = new JAXBFeatureTypeReader();
                    schemaLocation = schemaLocation.trim();
                    final String[] urls = schemaLocation.split(" ");
                    for (int i = 0; i < urls.length; i++) {
                        final String namespace = urls[i];
                        if (!(namespace.equalsIgnoreCase("http://www.opengis.net/gml") || namespace.equalsIgnoreCase("http://www.opengis.net/wfs")) && i + 1 < urls.length) {
                            final String fturl = urls[i + 1];
                            try {
                                final URL url = new URL(fturl);
                                List<FeatureType> fts = (List<FeatureType>) featureTypeReader.read(url.openStream());
                                for (FeatureType ft : fts) {
                                    if (!featureTypes.contains(ft)) {
                                        featureTypes.add(ft);
                                    }
                                }
                            } catch (MalformedURLException ex) {
                                LOGGER.log(Level.WARNING, null, ex);
                            } catch (IOException ex) {
                                LOGGER.log(Level.WARNING, null, ex);
                            } catch (JAXBException ex) {
                                LOGGER.log(Level.WARNING, null, ex);
                            }
                            i = i + 2;
                        } else if(namespace.equalsIgnoreCase("http://www.opengis.net/gml") || namespace.equalsIgnoreCase("http://www.opengis.net/wfs")) {
                            i++;
                        }
                    }
                }
                
                final Name name  = Utils.getNameFromQname(reader.getName());
                final String id  = reader.getAttributeValue(Namespaces.GML, "id");
                final StringBuilder expectedFeatureType = new StringBuilder();

                if (name.getLocalPart().equals("FeatureCollection")) {
                    final Object coll = readFeatureCollection(id);
                    if (coll == null) {
                        if (featureTypes.size() == 1) {
                            return DataUtilities.collection(id, featureTypes.get(0));
                        } else {
                            return DataUtilities.collection(id, null);
                        }
                    }
                    return coll;
                        
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
                    if (fid == null) {
                        LOGGER.info("Missing feature id : generating a random one");
                        fid = UUID.randomUUID().toString();
                    }
                    
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
        final Map<Name,Property> namedProperties = new LinkedHashMap<Name, Property>();

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
                    final MarshallerPool pool = getPool();
                    Unmarshaller unmarshaller = null;
                    try {
                        unmarshaller = pool.acquireUnmarshaller();
                        unmarshaller.setEventHandler(new JAXBEventHandler());
                        final Geometry jtsGeom;
                        final Object geometry = ((JAXBElement) unmarshaller.unmarshal(reader)).getValue();
                        if (geometry instanceof JTSGeometry) {
                            final JTSGeometry isoGeom = (JTSGeometry) geometry;
                            if (isoGeom instanceof JTSMultiCurve) {
                                ((JTSMultiCurve)isoGeom).applyCRSonChild();
                            }
                            jtsGeom = isoGeom.getJTSGeometry();
                        } else if (geometry instanceof PolygonType) {
                            jtsGeom = ((PolygonType)geometry).getJTSPolygon().getJTSGeometry();
                        } else if (geometry instanceof LineStringPosListType) {
                            jtsGeom = ((LineStringPosListType)geometry).getJTSLineString().getJTSGeometry();
                        } else if (geometry instanceof AbstractGeometry) {
                            try {
                                jtsGeom = GeometrytoJTS.toJTS((AbstractGeometry) geometry);
                            } catch (FactoryException ex) {
                                throw new XMLStreamException("Factory Exception while transforming GML object to JTS", ex);
                            }
                        } else {
                            throw new IllegalArgumentException("unexpected geometry type:" + geometry);
                        }
                        namedProperties.put(propName,FF.createAttribute(jtsGeom, (AttributeDescriptor)pdesc, null));
                        
                    } catch (JAXBException ex) {
                        String msg = ex.getMessage();
                        if (msg == null && ex.getLinkedException() != null) {
                            msg = ex.getLinkedException().getMessage();
                        }
                        throw new IllegalArgumentException("JAXB exception while reading the feature geometry: " + msg, ex);
                    } finally {
                        if (unmarshaller != null) {
                            pool.release(unmarshaller);
                        }
                    }

                } else {
                    final String content = reader.getElementText();
                    final Class propertyType = pdesc.getType().getBinding();
                    final Property prevProp = namedProperties.get(propName);
                    final Object previous = (prevProp == null) ? null : prevProp.getValue();

                    if (previous == null && nameAttribute != null) {
                        final Map<String, Object> map = new LinkedHashMap<String, Object>();
                        map.put(nameAttribute, content);
                        namedProperties.put(propName, FF.createAttribute(map, (AttributeDescriptor)pdesc, null));

                    } else if (previous == null && List.class.equals(propertyType)) {
                        final List<String> list = new ArrayList<String>();
                        list.add(content);
                        namedProperties.put(propName, FF.createAttribute(list, (AttributeDescriptor)pdesc, null));

                    } else if (previous == null) {
                        namedProperties.put(propName, FF.createAttribute(Converters.convert(content, propertyType),
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
                        namedProperties.put(propName, FF.createAttribute(multipleValue, (AttributeDescriptor)pdesc, null));
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

        return FF.createFeature(namedProperties.values(), featureType, id);
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
            LOGGER.log(Level.SEVERE, "XMl stream exception while extracting namespace: {0}", ex.getMessage());
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

    /**
     * Return a MarshallerPool depending on the property BINDING_PACKAGE.
     * 
     * accepted values : "JTSWrapper" or null (default). => JTSWrapperMarshallerPool
     *                   "GML"                           => GMLMarshallerPool
     */
    private MarshallerPool getPool() {
        final String bindingPackage = (String) properties.get(BINDING_PACKAGE);
        if (bindingPackage == null || "JTSWrapper".equals(bindingPackage)) {
            return JTSWrapperMarshallerPool.getInstance();
        } else if ("GML".equals(bindingPackage)) {
            return GMLMarshallerPool.getInstance();
        } else {
            throw new IllegalArgumentException("Unexpected property value for BINDING_PACKAGE:" + bindingPackage);
        }
    }
    
    /**
     * @deprecated use getProperty(READ_EMBEDDED_FEATURE_TYPE)
     */
    @Deprecated
    public boolean isReadEmbeddedFeatureType() {
        return (Boolean) this.properties.get(READ_EMBEDDED_FEATURE_TYPE);
    }

    /**
     * * @deprecated use getProperties().put(READ_EMBEDDED_FEATURE_TYPE)
     */
    @Deprecated
    public void setReadEmbeddedFeatureType(boolean readEmbeddedFeatureType) {
        this.properties.put(READ_EMBEDDED_FEATURE_TYPE, readEmbeddedFeatureType);
    }
}
