/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2015, Geomatys
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
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
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

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
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
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.xml.Namespaces;
import org.geotoolkit.xml.StaxStreamReader;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureFactory;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;

import static javax.xml.stream.events.XMLEvent.*;
import net.iharder.Base64;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.PropertyType;
import org.opengis.util.FactoryException;
import org.apache.sis.util.Numbers;


/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JAXPStreamFeatureReader extends StaxStreamReader implements XmlFeatureReader {

    private static final FeatureFactory FF = FeatureFactory.LENIENT;

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml.jaxp");

    protected List<FeatureType> featureTypes;

    public static final String READ_EMBEDDED_FEATURE_TYPE = "readEmbeddedFeatureType";

    public static final String SKIP_UNEXPECTED_PROPERTY_TAGS = "skipUnexpectedPropertyTags";

    public static final String BINDING_PACKAGE = "bindingPackage";

    /**
     * GML namespace for this class.
     */
    private static final String GML = "http://www.opengis.net/gml";

    private URL base = null;

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

    @Override
    public void setInput(Object input) throws IOException, XMLStreamException {
        super.setInput(input);
        if(input instanceof URL){
            base = (URL) input;
        }else if(input instanceof URI){
            base =((URI)input).toURL();
        }else if(input instanceof File){
            base = ((File)input).toURI().toURL();
        }else{
            base = null;
        }
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
                                final URL url = Utils.resolveURL(base, fturl);
                                List<FeatureType> fts = (List<FeatureType>) featureTypeReader.read(url.openStream());
                                for (FeatureType ft : fts) {
                                    if (!featureTypes.contains(ft)) {
                                        featureTypes.add(ft);
                                    }
                                }
                            } catch (MalformedURLException | URISyntaxException ex) {
                                LOGGER.log(Level.WARNING, null, ex);
                            } catch (IOException | JAXBException ex) {
                                LOGGER.log(Level.WARNING, null, ex);
                            }
                            i = i + 2;
                        } else if(namespace.equalsIgnoreCase("http://www.opengis.net/gml") || namespace.equalsIgnoreCase("http://www.opengis.net/wfs")) {
                            i++;
                        }
                    }
                }

                final Name name  = Utils.getNameFromQname(reader.getName());
                final String id  = reader.getAttributeValue(GML, "id");
                final StringBuilder expectedFeatureType = new StringBuilder();

                if (name.getLocalPart().equals("FeatureCollection")) {
                    final Object coll = readFeatureCollection(id);
                    if (coll == null) {
                        if (featureTypes.size() == 1) {
                            return FeatureStoreUtilities.collection(id, featureTypes.get(0));
                        } else {
                            return FeatureStoreUtilities.collection(id, null);
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
                                collection = FeatureStoreUtilities.collection(id, ft);
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

    private ComplexAttribute readFeature(final String id, final ComplexType featureType) throws XMLStreamException {
        return readFeature(id, featureType, featureType.getName());
    }

    private ComplexAttribute readFeature(final String id, final ComplexType featureType, final Name tagName) throws XMLStreamException {

        /*
         * We create a map and a collection because we can encounter two cases :
         * - The case featureType defines a property with max occur > 1.
         * - The case featureType defines a property with max occur = 1, and its
         * value instance of collection or map.
         * We store all encountered name with its linked property in the map, so
         * at each value parsed, we can add it in the existing property if its
         * value is a list or map. The collection is the final property store,
         * we add the all the created properties in it (so we can put multiple
         * properties with the same name).
         */
        final Map<Name,Property> namedProperties = new LinkedHashMap<>();
        final Collection<Property> propertyContainer = new ArrayList<>();

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
                    if (Boolean.TRUE.equals(this.properties.get(SKIP_UNEXPECTED_PROPERTY_TAGS))) {
                        toTagEnd(propName.getLocalPart());
                        continue;
                    } else {
                        throw new IllegalArgumentException("Unexpected attribute:" + propName + " not found in :\n" + featureType);
                    }
                }
                final PropertyType propertyType = pdesc.getType();

                if (pdesc instanceof GeometryDescriptor) {
                    event = reader.next();
                    while (event != START_ELEMENT) {
                        event = reader.next();
                    }
                    final MarshallerPool pool = getPool();
                    try {
                        final Unmarshaller unmarshaller;
                        unmarshaller = pool.acquireUnmarshaller();
                        unmarshaller.setEventHandler(new JAXBEventHandler());
                        final Geometry jtsGeom;
                        final Object geometry = ((JAXBElement) unmarshaller.unmarshal(reader)).getValue();
                        pool.recycle(unmarshaller);
                        if (geometry instanceof JTSGeometry) {
                            final JTSGeometry isoGeom = (JTSGeometry) geometry;
                            if (isoGeom instanceof JTSMultiCurve) {
                                ((JTSMultiCurve)isoGeom).applyCRSonChild();
                            }
                            jtsGeom = isoGeom.getJTSGeometry();
                        } else if (geometry instanceof PolygonType) {
                            final PolygonType polygon = ((PolygonType)geometry);
                            jtsGeom = polygon.getJTSPolygon().getJTSGeometry();
                            if(polygon.getCoordinateReferenceSystem() != null) {
                                JTS.setCRS(jtsGeom, polygon.getCoordinateReferenceSystem());
                            }
                        } else if (geometry instanceof LineStringPosListType) {
                            final JTSLineString line = ((LineStringPosListType)geometry).getJTSLineString();
                            jtsGeom = line.getJTSGeometry();
                            if(line.getCoordinateReferenceSystem() != null) {
                                JTS.setCRS(jtsGeom, line.getCoordinateReferenceSystem());
                            }
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
                        propertyContainer.add(namedProperties.get(propName));
                    } catch (JAXBException ex) {
                        String msg = ex.getMessage();
                        if (msg == null && ex.getLinkedException() != null) {
                            msg = ex.getLinkedException().getMessage();
                        }
                        throw new IllegalArgumentException("JAXB exception while reading the feature geometry: " + msg, ex);
                    }

                } else if (propertyType instanceof ComplexType) {

                    final ComplexAttribute catt = readFeature(null, (ComplexType) propertyType, propName);
                    if (pdesc.getMaxOccurs() > 1) {
                        propertyContainer.add(FF.createComplexAttribute(catt.getProperties(), (AttributeDescriptor) pdesc, null));
                    } else {
                        if (namedProperties.containsKey(propName)) {
                            Property complexProp = namedProperties.get(propName);
                            if (complexProp.getValue() instanceof List) {
                                ((List) complexProp.getValue()).add(FF.createComplexAttribute(catt.getProperties(), (AttributeDescriptor) pdesc, null));
                            } else if (complexProp.getValue() instanceof Map) {
                                if (nameAttribute != null) {
                                    ((Map) complexProp.getValue()).put(nameAttribute, FF.createComplexAttribute(catt.getProperties(), (AttributeDescriptor) pdesc, null));
                                } else {
                                    LOGGER.severe("unable to read a composite attribute : no name has been found");
                                }
                            }
                        } else {
                            namedProperties.put(propName, FF.createComplexAttribute(catt.getProperties(), (AttributeDescriptor) pdesc, null));
                            propertyContainer.add(namedProperties.get(propName));
                        }
                    }

                    /**
                     * TODO : Change to adopt the same behavior than in complexAttribute case :
                     * Check if the parameter is multi occurence : if ot is, we just create a new property that
                     * we'll add in property container. Otherwise, if a previous value already exists, we must throw an
                     * exception (or set value as a list, but I don't think it's a safe behavior).
                     */
                } else {
                    final String content = reader.getElementText();
                    final Class typeBinding = propertyType.getBinding();
                    final Property prevProp = namedProperties.get(propName);
                    final Object previous = (prevProp == null) ? null : prevProp.getValue();

                    if (previous == null && nameAttribute != null) {
                        final Map<String, Object> map = new LinkedHashMap<String, Object>();
                        map.put(nameAttribute, content);
                        namedProperties.put(propName, FF.createAttribute(map, (AttributeDescriptor)pdesc, null));
                        propertyContainer.add(namedProperties.get(propName));

                    } else if (previous == null && List.class.equals(typeBinding)) {
                        final List<String> list = new ArrayList<String>();
                        list.add(content);
                        namedProperties.put(propName, FF.createAttribute(list, (AttributeDescriptor)pdesc, null));
                        propertyContainer.add(namedProperties.get(propName));

                    } else if (previous == null) {
                        namedProperties.put(propName, FF.createAttribute(readValue(content, propertyType),
                            (AttributeDescriptor)pdesc, null));
                        propertyContainer.add(namedProperties.get(propName));

                    } else if (previous instanceof Map && nameAttribute != null) {
                        ((Map) previous).put(nameAttribute, content);

                    } else if (previous instanceof Map && nameAttribute == null) {
                        LOGGER.severe("unable to read a composite attribute : no name has been found");

                    } else if (previous instanceof Collection) {
                        ((Collection) previous).add(content);

                    } else {
                        final List multipleValue = new ArrayList();
                        multipleValue.add(previous);
                        multipleValue.add(readValue(content, propertyType));
                        namedProperties.put(propName, FF.createAttribute(multipleValue, (AttributeDescriptor)pdesc, null));
                        propertyContainer.remove(prevProp);
                        propertyContainer.add(namedProperties.get(propName));
                    }

                }

            } else if (event == END_ELEMENT) {
                final QName q = reader.getName();
                if (q.getLocalPart().equals("featureMember")) {
                    break;
                } else if (Utils.getNameFromQname(q).equals(tagName)) {
                    break;
                }
            }
        }

        if (featureType instanceof SimpleFeatureType) {
            //we must ensure we have the rigth number of properties
            final SimpleFeatureBuilder sfb = new SimpleFeatureBuilder((SimpleFeatureType)featureType);
            for(Property p : namedProperties.values()){
                sfb.set(p.getName(), p.getValue());
            }
            return sfb.buildFeature(id);
        } else if (featureType instanceof FeatureType) {
            return FF.createFeature(propertyContainer, (FeatureType)featureType, id);
        } else {
            return FF.createComplexAttribute(propertyContainer, (ComplexType)featureType, null);
        }
    }

    public Object readValue(final String content, final PropertyType type){
        Object value = content;
        if(type.getBinding() == byte[].class && content != null){
            try {
                value = Base64.decode(content);
            } catch (IOException ex) {
                LOGGER.log(Level.INFO, "Failed to parser binary64 : "+ex.getMessage(),ex);
            }
        }else{
            value = ObjectConverters.convert(value, Numbers.primitiveToWrapper(type.getBinding()));
        }
        return value;
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
                            features.add((Feature)readFeature("", ft));
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
