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
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
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
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.feature.xml.GMLConvention;
import org.geotoolkit.xml.StaxStreamWriter;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Property;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
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
        if (candidate instanceof Feature) {
            writeFeature((Feature) candidate,true);
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
    private void writeFeature(final Feature feature, final boolean root) throws XMLStreamException {
        //reset geometry id increment
        gidInc = 0;

        //the root element of the xml document (type of the feature)
        final FeatureType type = feature.getType();
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

    private static String getId(Feature att, String fallback){
        final Identifier attId;
        try {
            attId = FeatureExt.getId(att);
        } catch(PropertyNotFoundException ex) {
            return fallback;
        }
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
    private boolean writeAttributeProperties(final Feature feature) throws XMLStreamException {

        final FeatureType type = feature.getType();

        boolean nil = false;

        //write properties in the type order
        for(final PropertyType desc : type.getProperties(true)){
            if(AttributeConvention.contains(desc.getName())) continue;
            if(!isAttributeProperty(desc.getName())) continue;

            if(desc.getName().tip().toString().equals("@id")) {
                //gml id has already been written
                continue;
            }

            Object value = feature.getPropertyValue(desc.getName().toString());
            final GenericName nameA = desc.getName();
            String nameProperty = nameA.tip().toString();
            String namespaceProperty = NamesExt.getNamespace(nameA);

            //remove the @
            nameProperty = nameProperty.substring(1);

            nil |= "nil".equals(nameProperty) && Boolean.TRUE.equals(value);
            if(value instanceof Boolean) {
                value = (Boolean)value ? "1" : "0";
            }

            String valueStr = Utils.getStringValue(value);
            if (valueStr != null) {
                if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                    writer.writeAttribute(namespaceProperty, nameProperty, valueStr);
                } else {
                    writer.writeAttribute(nameProperty, valueStr);
                }
            }
        }

        return nil;
    }

    /**
     * Test if the feature is nill.
     *
     * @return null if the object is not nill
     *         Boolean.TRUE if the object is nill without reason
     *         String if the object is nill with a reason
     */
    private static Object isNill(Feature feature){
        try {
            if(Boolean.TRUE.equals(feature.getPropertyValue("@nil"))){
                try {
                    Object reason = feature.getPropertyValue("@nilReason");
                    if (reason!=null) {
                       return Utils.getStringValue(reason);
                    }
                }catch(PropertyNotFoundException ex){
                }
                return true;
            }
        } catch(PropertyNotFoundException ex) {
            return null;
        }
        return null;
    }

    private void writeComplexProperties(final Feature feature, String id) throws XMLStreamException {

        final boolean isNil = writeAttributeProperties(feature);
        if(isNil) return;

        //write properties in the type order
        for(PropertyType pt : feature.getType().getProperties(true)){
            final Object value = feature.getPropertyValue(pt.getName().toString());
            final Collection<Object> values;
            if(value instanceof Collection){
                values = (Collection<Object>) value;
            }else{
                values = Collections.singleton(value);
            }

            if(!values.isEmpty()){
                for (Object a : values) {
                    writeProperty(feature,pt,a,id);
                }
            }else if(Utils.isNillable(pt)){
                //we must have at least one tag with nil=1
                final GenericName nameA = pt.getName();
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
    }

    private void writeCharacteristics(Attribute att) throws XMLStreamException {
        final Iterator<Attribute> ite = att.characteristics().values().iterator();
        while (ite.hasNext()) {
            final Attribute chara = ite.next();
            final GenericName name = chara.getName();
            final String namespace = NamesExt.getNamespace(name);
            String localPart = name.tip().toString();
            if(localPart.startsWith("@")){
                //remove the @
                localPart = localPart.substring(1);
            }
            Object value = chara.getValue();
            if(value instanceof Boolean) {
                value = (Boolean)value ? "1" : "0";
            }
            if (value!=null) {
                writer.writeAttribute(namespace, localPart,ObjectConverters.convert(value, String.class));
            }
        }
    }

    private void writeProperty(Feature parent, PropertyType typeA, Object valueA, String id) throws XMLStreamException{
        final FeatureType parentType = parent.getType();
        final GenericName nameA = typeA.getName();
        final String nameProperty = nameA.tip().toString();
        String namespaceProperty = NamesExt.getNamespace(nameA);
        final boolean hasChars = typeA instanceof AttributeType && !((AttributeType)typeA).characteristics().isEmpty();

        if(isAttributeProperty(nameA)) return;

        //TODO : search for link operation which match
//        if(!isSubstitute){
//            for(PropertyDescriptor desc : parentType.getDescriptors()){
//                final PropertyType pt = desc.getType();
//                if(pt instanceof Link && ((AliasOperation)pt).getRefName().equals(nameA)){
//                    //possible substitute group
//                    Property p = parent.getProperty(desc.getName());
//                    if(p!=null){
//                        final PropertyType originalType = parent.getType().getDescriptor(a.getName()).getType();
//                        if(p.getType().equals(originalType)){
//                            //substitute has exactly the same type
//                            //we favorite the non alias type
//                            break;
//                        }
//
//                        //valid substitute, we write it instead of the current property
//                        writeProperty(parent, parent.getType(),p, true, id);
//                        return;
//                    }
//                }
//            }
//        }



        if (typeA instanceof FeatureAssociationRole && valueA instanceof Feature) {
            final FeatureAssociationRole far = (FeatureAssociationRole) typeA;
            final Feature ca = (Feature)valueA;

            //write feature
            if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                writer.writeStartElement(namespaceProperty, nameProperty);
            } else {
                writer.writeStartElement(nameProperty);
            }

            //nill case
            final Object isNil = isNill(ca);
            if (isNil != null) {
                writeAttributeProperties(ca);
                writer.writeEndElement();
                return;
            }

            /*
            Note : the GML 3.2 identifier element is this only one which does not
            follow the OGC 'PropertyType' pattern and is not encapsulated.
            Note : if more cases are found, a more generic approach should be used.
            */
            boolean encapsulate = !"identifier".equals(ca.getType().getName().tip().toString());

            if (encapsulate) {
                //we need to encapsulate type
                final FeatureType valueType = far.getValueType();
                final String encapName = Utils.getNameWithoutTypeSuffix(valueType.getName().tip().toString());
                if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                    writer.writeStartElement(namespaceProperty, encapName);
                } else {
                    writer.writeStartElement(encapName);
                }

                writeComplexProperties(ca, getId(ca, id));

                //close encapsulation
                writer.writeEndElement();
            } else {

                writeComplexProperties(ca, getId(ca, id));
            }



            writer.writeEndElement();

        } else if (valueA instanceof Collection && !(AttributeConvention.isGeometryAttribute(typeA))) {
            for (Object value : (Collection)valueA) {
                if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                    writer.writeStartElement(namespaceProperty, nameProperty);
                } else {
                    writer.writeStartElement(nameProperty);
                }
                if(hasChars) writeCharacteristics((Attribute) parent.getProperty(nameA.toString()));
                writer.writeCharacters(Utils.getStringValue(value));
                writer.writeEndElement();
            }

        } else if (valueA != null && valueA.getClass().isArray() && !(AttributeConvention.isGeometryAttribute(typeA))) {
            final int length = Array.getLength(valueA);
            for (int i = 0; i < length; i++){
                if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                    writer.writeStartElement(namespaceProperty, nameProperty);
                } else {
                    writer.writeStartElement(nameProperty);
                }
                if(hasChars) writeCharacteristics((Attribute) parent.getProperty(nameA.toString()));
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

        } else if (valueA instanceof Map && !(AttributeConvention.isGeometryAttribute(typeA))) {
            final Map<?,?> map = (Map)valueA;
            for (Entry<?,?> entry : map.entrySet()) {
                if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                    writer.writeStartElement(namespaceProperty, nameProperty);
                } else {
                    writer.writeStartElement(nameProperty);
                }
                if(hasChars) writeCharacteristics((Attribute) parent.getProperty(nameA.toString()));
                final Object key = entry.getKey();
                if (key != null) {
                    writer.writeAttribute("name", (String)key);
                }
                writer.writeCharacters(Utils.getStringValue(entry.getValue()));
                writer.writeEndElement();
            }

        } else if (!(AttributeConvention.isGeometryAttribute(typeA))) {

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

                if (valueA instanceof Feature || value != null) {
                    if (namespaceProperty != null && !namespaceProperty.isEmpty()) {
                        writer.writeStartElement(namespaceProperty, nameProperty);
                    } else {
                        writer.writeStartElement(nameProperty);
                    }
                    if(hasChars) writeCharacteristics((Attribute) parent.getProperty(nameA.toString()));

                    if(valueA instanceof Feature){
                        //some types, like Observation & Measurement have Object types which can be
                        //properties again, we ensure to write then as proper xml tags
                        final Feature prop = (Feature) valueA;
                        final GenericName propName = prop.getType().getName();
                        final String namespaceURI = NamesExt.getNamespace(propName);
                        final String localPart = Utils.getNameWithoutTypeSuffix(propName.tip().toString());
                        if (namespaceURI != null && !namespaceURI.isEmpty()) {
                            writer.writeStartElement(namespaceURI, localPart);
                        } else {
                            writer.writeStartElement(localPart);
                        }
                        writeComplexProperties(prop, getId(prop, id));
                        writer.writeEndElement();
                    }else if (value != null) {
                        writer.writeCharacters(value);
                    }
                    writer.writeEndElement();
                }else if(value==null && Utils.isNillable(typeA)){
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

                final CoordinateReferenceSystem crs = FeatureExt.getCRS(typeA);
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

}
