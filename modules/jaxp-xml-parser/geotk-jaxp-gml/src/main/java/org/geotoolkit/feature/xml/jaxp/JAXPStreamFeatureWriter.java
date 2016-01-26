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

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.op.AliasOperation;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryType;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.type.PropertyType;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.XmlFeatureWriter;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.gml.JTStoGeometry;
import org.geotoolkit.gml.xml.AbstractCurve;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.AbstractSurface;
import org.geotoolkit.gml.xml.CurveProperty;
import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.geotoolkit.gml.xml.MultiCurve;
import org.geotoolkit.gml.xml.MultiSurface;
import org.geotoolkit.gml.xml.SurfaceProperty;
import org.geotoolkit.gml.xml.v321.AbstractGeometryType;
import org.geotoolkit.gml.xml.v321.AbstractSolidType;
import org.geotoolkit.gml.xml.v321.GeometryPropertyType;
import org.geotoolkit.gml.xml.v321.MultiGeometryType;
import org.geotoolkit.gml.xml.v321.MultiPointType;
import org.geotoolkit.gml.xml.v321.MultiSolidType;
import org.geotoolkit.gml.xml.v321.PointPropertyType;
import org.geotoolkit.gml.xml.v321.PointType;
import org.geotoolkit.gml.xml.v321.SolidPropertyType;
import org.geotoolkit.internal.jaxb.JTSWrapperMarshallerPool;
import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.xml.StaxStreamWriter;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.w3c.dom.Document;


/**
 * Handles writing process of features using JAXP. The {@link #dispose()} method MUST be
 * called in order to release some resources.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXPStreamFeatureWriter extends StaxStreamWriter implements XmlFeatureWriter {

    private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";

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
    private final String wfsLocation;

    private final String gmlNamespace;
    private final String gmlLocation;

    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    //automatic id increment for geometries id
    private int gidInc = 0;

    public JAXPStreamFeatureWriter() {
        this("3.1.1", "1.1.0", null);
    }

    public JAXPStreamFeatureWriter(final String gmlVersion, final String wfsVersion, final Map<String, String> schemaLocations)  {
        this.gmlVersion = gmlVersion;
        this.wfsVersion = wfsVersion;
        if ("2.0.0".equals(wfsVersion)) {
            wfsNamespace = "http://www.opengis.net/wfs/2.0";
            wfsLocation  = "http://schemas.opengis.net/wfs/2.0/wfs.xsd";
        } else {
            wfsNamespace = "http://www.opengis.net/wfs";
            wfsLocation  = "http://schemas.opengis.net/wfs/1.1.0/wfs.xsd";
        }
        if ("3.2.1".equals(gmlVersion)) {
            gmlNamespace = "http://www.opengis.net/gml/3.2";
            gmlLocation  = "http://schemas.opengis.net/gml/3.2.1/gml.xsd";
        } else {
            gmlNamespace = "http://www.opengis.net/gml";
            gmlLocation  = "http://schemas.opengis.net/gml/3.1.1/base/gml.xsd";
        }

        if (schemaLocations != null && schemaLocations.size() > 0) {
            final StringBuilder sb = new StringBuilder();
            for (Entry<String, String> entry : schemaLocations.entrySet()) {
                sb.append(entry.getKey()).append(' ').append(entry.getValue()).append(' ');
            }
            // add wfs schema Location
            sb.append(wfsNamespace).append(' ').append(wfsLocation).append(' ');
            sb.append(gmlNamespace).append(' ').append(gmlLocation).append(' ');
            schemaLocation = sb.toString();
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
    public void write(final Object candidate, final Object output) throws IOException, XMLStreamException, DataStoreException {
        this.write(candidate, output, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final Object candidate, final Object output, final Integer nbMatched) throws IOException, XMLStreamException, DataStoreException {
        setOutput(output);
        if (candidate instanceof ComplexAttribute) {
            writeFeature((ComplexAttribute) candidate,true);
        } else if (candidate instanceof FeatureCollection) {
            writeFeatureCollection((FeatureCollection) candidate,false, nbMatched);
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
        //reset geometry id increment
        gidInc = 0;

        //the root element of the xml document (type of the feature)
        final ComplexType type = feature.getType();
        final GenericName typeName    = type.getName();
        final String namespace = NamesExt.getNamespace(typeName);
        final String localPart = typeName.tip().toString();
        final String gmlid = getId(feature, null);
        if (namespace != null && !namespace.isEmpty()) {
            final Prefix prefix = getPrefix(namespace);
            writer.writeStartElement(prefix.prefix, localPart, namespace);
            if (gmlid != null) {
                writer.writeAttribute("gml", gmlNamespace, "id", gmlid);
            }
            if (prefix.unknow && !root) {
                writer.writeNamespace(prefix.prefix, namespace);
            }
            if (root) {
                writer.writeNamespace("gml", gmlNamespace);
                writer.writeNamespace("xsi", XSI_NAMESPACE);
                if (!namespace.equals(gmlNamespace)) {
                    writer.writeNamespace(prefix.prefix, namespace);
                }
            }
        } else {
            writer.writeStartElement(localPart);
            if (gmlid != null) {
                writer.writeAttribute("gml", gmlNamespace, "id", gmlid);
            }
        }

        writeComplexProperties(feature, gmlid);

        writer.writeEndElement();
        writer.flush();
    }

    private static String getId(ComplexAttribute att, String fallback){
        final Identifier attId = att.getIdentifier();
        if(attId==null) return fallback;
        final Object id = attId.getID();
        if(id==null) return fallback;

        if(id instanceof String){
            if(((String)id).isEmpty()){
                return fallback;
            }else{
                return ((String)id).replace(':', '_');
            }
        }else{
            return String.valueOf(id).replace(':', '_');
        }
    }

    /**
     * Write atribute properties.
     * If we found a nil reason than return is true
     *
     * TODO this is not a perfect way to know if a propery is null.
     * but if we don't declare the property then we don't know the reason either...
     *
     *
     * @param feature
     * @return
     * @throws XMLStreamException
     */
    private boolean writeAttributeProperties(final ComplexAttribute feature) throws XMLStreamException {

        final ComplexType type = feature.getType();

        boolean nil = false;

        //write properties in the type order
        for(final PropertyDescriptor desc : type.getDescriptors()){
//            if(desc instanceof OperationDescriptor) continue;
//
//            final Collection<Property> props;
//            final Property subProp = getSubstitu(feature, desc);
//            if(subProp!=null){
//                props = Arrays.asList(subProp);
//            }else{
//                props = feature.getProperties(desc.getName());
//            }
            final Collection<Property> props = feature.getProperties(desc.getName());
            for (Property a : props) {
                final Object valueA = a.getValue();
                final GenericName nameA = a.getName();
                String nameProperty = nameA.tip().toString();
                String namespaceProperty = NamesExt.getNamespace(nameA);

                if(!isAttributeProperty(nameA)) continue;

                //remove the @
                nameProperty = nameProperty.substring(1);

                if("nilReason".equals(nameProperty)) nil = true;

                String value = Utils.getStringValue(valueA);
                if (value != null) {
                    if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                        writer.writeAttribute(namespaceProperty, nameProperty, value);
                    } else {
                        writer.writeAttribute(nameProperty, value);
                    }
                }
            }
        }

        if(nil){
            //add the xsi:nill attribute
            writer.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil","1");
        }

        return nil;
    }

    private void writeComplexProperties(final ComplexAttribute feature, String id) throws XMLStreamException {

        final ComplexType type = feature.getType();

        if(isPrimitiveType(type)){
            //this type is in reality a single xml element with attributes
            final boolean isNil = writeAttributeProperties(feature);
            if(isNil) return;

            //write the value
            Object value = "";
            for(Property prop : feature.getProperties()){
                if(Utils.VALUE_PROPERTY_NAME.equals(prop.getName().tip().toString())){
                    value = prop.getValue();
                    if (value == null) {
                        value = "";
                    }
                    break;
                }
            }

            writer.writeCharacters(Utils.getStringValue(value));


        }else{
            final boolean isNil = writeAttributeProperties(feature);
            if(isNil) return;

            //keep a list of written properties, some of them might not be described in the type
            final List<Property> allProps = new ArrayList<>(feature.getProperties());


            //write properties in the type order
            for(final PropertyDescriptor desc : type.getDescriptors()){
//                if(desc instanceof OperationDescriptor) continue;
//
//                final Collection<Property> props;
//                final Property subProp = getSubstitu(feature, desc);
//                if(subProp!=null){
//                    props = Arrays.asList(subProp);
//                    allProps.removeAll(feature.getProperties(desc.getName()));
//                }else{
//                    props = feature.getProperties(desc.getName());
//                    allProps.removeAll(props);
//                }

                final Collection<Property> props = feature.getProperties(desc.getName());
                if(!props.isEmpty()){
                    for (Property a : props) {
                        writeProperty(feature,a,false, id);
                        allProps.remove(a);
                    }
                }else if(desc.isNillable()){
                    //we must have at least one tag with nil=1
                    final GenericName nameA = desc.getName();
                    final String namespaceProperty = NamesExt.getNamespace(nameA);
                    final String nameProperty = nameA.tip().toString();
                    if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                        writer.writeStartElement(namespaceProperty, nameProperty);
                    } else {
                        writer.writeStartElement(nameProperty);
                    }
                    writer.writeAttribute("http://www.w3.org/2001/XMLSchema-instance", "nil", "1");
                    writer.writeEndElement();
                }
            }

            //write remaining properties
            for(Property a : allProps){
                writeProperty(feature,a,false, id);
            }

        }
    }

    private void writeProperty(ComplexAttribute parent, Property a, boolean isSubstitute, String id) throws XMLStreamException{
        final ComplexType parentType = parent.getType();
        final Object valueA = a.getValue();
        final PropertyType typeA = a.getType();
        final GenericName nameA = a.getDescriptor().getName();
        final String nameProperty = nameA.tip().toString();
        String namespaceProperty = NamesExt.getNamespace(nameA);

        if(isAttributeProperty(nameA)) return;

        //search for link operation which match
        if(!isSubstitute){
            for(PropertyDescriptor desc : parentType.getDescriptors()){
                final PropertyType pt = desc.getType();
                if(pt instanceof AliasOperation && ((AliasOperation)pt).getRefName().equals(nameA)){
                    //possible substitute group
                    Property p = parent.getProperty(desc.getName());
                    if(p!=null){
                        final PropertyType originalType = parent.getType().getDescriptor(a.getName()).getType();
                        if(p.getType().equals(originalType)){
                            //substitute has exactly the same type
                            //we favorite the non alias type
                            break;
                        }

                        //valid substitute, we write it instead of the current property
                        writeProperty(parent, p, true, id);
                        return;
                    }
                }
            }
        }



        if (a instanceof ComplexAttribute) {
            if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                writer.writeStartElement(namespaceProperty, nameProperty);
            } else {
                writer.writeStartElement(nameProperty);
            }
            final ComplexAttribute ca = (ComplexAttribute) a;
            writeComplexProperties(ca, getId(ca, id));
            writer.writeEndElement();

        } else if (valueA instanceof Collection && !(typeA instanceof GeometryType)) {
            for (Object value : (Collection)valueA) {
                if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                    writer.writeStartElement(namespaceProperty, nameProperty);
                } else {
                    writer.writeStartElement(nameProperty);
                }
                writer.writeCharacters(Utils.getStringValue(value));
                writer.writeEndElement();
            }

        } else if (valueA != null && valueA.getClass().isArray() && !(typeA instanceof GeometryType)) {
            final int length = Array.getLength(valueA);
            for (int i = 0; i < length; i++){
                if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                    writer.writeStartElement(namespaceProperty, nameProperty);
                } else {
                    writer.writeStartElement(nameProperty);
                }
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

        } else if (valueA instanceof Map && !(typeA instanceof GeometryType)) {
            final Map<?,?> map = (Map)valueA;
            for (Entry<?,?> entry : map.entrySet()) {
                if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
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

            if(valueA instanceof Document){
                //special case for xml documents
                final Document doc = (Document) valueA;
                StaxUtils.writeElement(doc.getDocumentElement(), writer, false);

            }else{
                //simple type
                String value = (valueA instanceof Property) ? null : Utils.getStringValue(valueA);

                if ((nameProperty.equals("name") || nameProperty.equals("description")) && !gmlNamespace.equals(namespaceProperty)) {
                    namespaceProperty = gmlNamespace;
                    LOGGER.finer("the property name and description of a feature must have the GML namespace");
                }

                if (valueA instanceof Property || value != null) {
                    if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                        writer.writeStartElement(namespaceProperty, nameProperty);
                    } else {
                        writer.writeStartElement(nameProperty);
                    }

                    if(valueA instanceof Property){
                        //some types, like Observation & Measurement have Object types which can be
                        //properties again, we ensure to write then as proper xml tags
                        final Property prop = (Property) valueA;
                        final GenericName propName = prop.getName();
                        final String namespaceURI = NamesExt.getNamespace(propName);
                        final String localPart = propName.tip().toString();
                        if (namespaceURI != null && !namespaceURI.isEmpty()) {
                            writer.writeStartElement(namespaceURI, localPart);
                        } else {
                            writer.writeStartElement(localPart);
                        }
                        if(prop instanceof ComplexAttribute){
                            final ComplexAttribute ca = (ComplexAttribute)prop;
                            writeComplexProperties(ca, getId(ca, id));
                        }else{
                            value = Utils.getStringValue(prop.getValue());
                            if(value!=null){
                                writer.writeCharacters(value);
                            }
                        }
                        writer.writeEndElement();
                    }else if (value != null) {
                        writer.writeCharacters(value);
                    }
                    writer.writeEndElement();
                }else if(value==null && a.isNillable()){
                    if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                        writer.writeStartElement(namespaceProperty, nameProperty);
                    } else {
                        writer.writeStartElement(nameProperty);
                    }
                    writer.writeAttribute("http://www.w3.org/2001/XMLSchema-instance", "nil", "1");
                    writer.writeEndElement();
                }
            }

        // we add the geometry
        } else {

            if (valueA != null) {
                final boolean descIsType = Utils.isGeometricType(typeA.getName()) && Utils.isGeometricType(nameA);

                if(!descIsType){
                    if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                        writer.writeStartElement(namespaceProperty, nameProperty);
                    } else {
                        writer.writeStartElement(nameProperty);
                    }
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
                    if(gmlGeometry!=null){
                        //id is requiered in version 3.2.1
                        //NOTE we often see gml where the geometry id is the same as the feature
                        // we use the last parent with an id, seems acceptable.
                        final String gid = (id+"_g").replace(':', '_');
                        setId(gmlGeometry, gid);
                    }
                    element = GML32_FACTORY.buildAnyGeometry(gmlGeometry);
                    POOL = GML_32_POOL;
                } else {
                    throw new IllegalArgumentException("Unexpected GML version:" + gmlVersion);
                }
                try {
                    final Marshaller marshaller;
                    marshaller = POOL.acquireMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
                    marshaller.marshal(element, writer);
                    POOL.recycle(marshaller);
                } catch (JAXBException ex) {
                    LOGGER.log(Level.WARNING, "JAXB Exception while marshalling the iso geometry: " + ex.getMessage(), ex);
                }

                if(!descIsType)writer.writeEndElement();
            }
        }
    }

    /**
     *
     * @param gmlGeometry
     * @param id
     * @param inc auto increment value, ids must be unique
     */
    private void setId(AbstractGeometry gmlGeometry, String id){
        if(gmlGeometry.getId()==null || gmlGeometry.getId().isEmpty()){
            //do not override ids if they exist
            gmlGeometry.setId(id+(gidInc));
            gidInc++;
        }

        if(gmlGeometry instanceof MultiCurve){
            for(CurveProperty po : ((MultiCurve)gmlGeometry).getCurveMember()){
                final AbstractCurve child = po.getAbstractCurve();
                if(child instanceof AbstractGeometry){
                    setId((AbstractGeometry) child, id);
                }
            }
        }else if(gmlGeometry instanceof MultiSurface){
            for(SurfaceProperty po : ((MultiSurface)gmlGeometry).getSurfaceMember()){
                final AbstractSurface child = po.getAbstractSurface();
                if(child instanceof AbstractGeometry){
                    setId((AbstractGeometry) child, id);
                }
            }
        }else if(gmlGeometry instanceof MultiGeometryType){
            for(GeometryPropertyType po : ((MultiGeometryType)gmlGeometry).getGeometryMember()){
                final AbstractGeometryType child = po.getAbstractGeometry();
                if(child instanceof AbstractGeometry){
                    setId((AbstractGeometry) child, id);
                }
            }
        }else if(gmlGeometry instanceof MultiSolidType){
            for(SolidPropertyType po : ((MultiSolidType)gmlGeometry).getSolidMember()){
                final AbstractSolidType child = po.getAbstractSolid().getValue();
                if(child instanceof AbstractGeometry){
                    setId((AbstractGeometry) child, id);
                }
            }
        }else if(gmlGeometry instanceof MultiPointType){
            for(PointPropertyType po : ((MultiPointType)gmlGeometry).getPointMember()){
                final PointType child = po.getPoint();
                if(child instanceof AbstractGeometry){
                    setId((AbstractGeometry) child, id);
                }
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
    public void writeFeatureCollection(final FeatureCollection featureCollection, final boolean fragment, final Integer nbMatched)
                                                            throws DataStoreException, XMLStreamException
    {

        // the XML header
        if(!fragment){
            writer.writeStartDocument("UTF-8", "1.0");
        }

        // the root Element
        writer.writeStartElement("wfs", "FeatureCollection", wfsNamespace);

        // id does not appear in WFS 2
        if (!"2.0.0".equals(wfsVersion)) {
            String collectionID = "";
            if (featureCollection.getID() != null) {
                collectionID = featureCollection.getID();
            }
            writer.writeAttribute("gml", gmlNamespace, "id", collectionID);
        }
        // timestamp
        synchronized(FORMATTER) {
            writer.writeAttribute("timeStamp", FORMATTER.format(new Date(System.currentTimeMillis())));
        }

        writer.writeNamespace("gml", gmlNamespace);
        writer.writeNamespace("wfs", wfsNamespace);
        writer.writeNamespace("xsi", XSI_NAMESPACE);
        if (schemaLocation != null && !schemaLocation.equals("")) {
            writer.writeAttribute("xsi", XSI_NAMESPACE, "schemaLocation", schemaLocation);
        }

        /*
         * Other version dependant WFS feature collection attribute
         */
        if ("2.0.0".equals(wfsVersion)) {
            writer.writeAttribute("numberReturned", Integer.toString(featureCollection.size()));
            if (nbMatched != null) {
                writer.writeAttribute("numberMatched", Integer.toString(nbMatched));
            }
        } else {
            writer.writeAttribute("numberOfFeatures", Integer.toString(featureCollection.size()));
        }

        FeatureType type = featureCollection.getFeatureType();
        if (type != null && type.getName() != null) {
            for(String n : Utils.listAllNamespaces(type)){
                if (n != null && !(n.equals("http://www.opengis.net/gml") || n.equals("http://www.opengis.net/gml/3.2")) && !n.isEmpty()) {
                    writer.writeNamespace(getPrefix(n).prefix, n);
                }
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
                    srsName = IdentifiedObjects.lookupURN(bounds.getCoordinateReferenceSystem(), null);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }
            if ("2.0.0".equals(wfsVersion)) {
                streamWriter.writeStartElement("wfs", "boundedBy", wfsNamespace);
            } else {
                streamWriter.writeStartElement("gml", "boundedBy", gmlNamespace);
            }
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


    /**
     *
     * @param name
     * @return true if property is an atribute, starts by a @
     */
    public static boolean isAttributeProperty(GenericName name){
        final String localPart = name.tip().toString();
        return !localPart.isEmpty() && localPart.charAt(0) == '@';
    }

    /**
     * XML can have primitive types with attributes.
     * We decompose them in a complex type with an empty name attribute and @properties.
     *
     * @param type
     * @return
     */
    public static boolean isPrimitiveType(ComplexType type){
        for(PropertyDescriptor desc : type.getDescriptors()){
            if(Utils.VALUE_PROPERTY_NAME.equals(desc.getName().tip().toString())){
                return true;
            }
        }
        return false;
    }

    private static GenericName getSubtituteRefName(PropertyDescriptor prop){
        PropertyType type = prop.getType();
        if(type instanceof AliasOperation){
            return ((AliasOperation)type).getRefName();
        }
        return null;
    }

    private static Property getSubstitu(ComplexAttribute parent, PropertyDescriptor prop){
        final GenericName name = prop.getName();
        for(PropertyDescriptor desc : parent.getType().getDescriptors()){
            if(name.equals(getSubtituteRefName(desc))){
                Property sub = parent.getProperty(desc.getName());
                if(sub!=null) return sub;
            }
        }
        return null;
    }

}
