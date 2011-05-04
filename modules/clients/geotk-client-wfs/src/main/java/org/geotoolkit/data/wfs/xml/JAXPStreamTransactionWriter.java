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

package org.geotoolkit.data.wfs.xml;

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.wfs.Delete;
import org.geotoolkit.data.wfs.IdentifierGenerationOption;
import org.geotoolkit.data.wfs.Insert;
import org.geotoolkit.data.wfs.Native;
import org.geotoolkit.data.wfs.ReleaseAction;
import org.geotoolkit.data.wfs.TransactionElement;
import org.geotoolkit.data.wfs.TransactionRequest;
import org.geotoolkit.data.wfs.Update;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.internal.jaxb.JTSWrapperMarshallerPool;
import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.sld.xml.XMLUtilities;
import org.geotoolkit.xml.MarshallerPool;

import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JAXPStreamTransactionWriter {

    public static final String GML_NAMESPACE = "http://www.opengis.net/gml";
    public static final String GML_PREFIX = "gml";

    public static final String OGC_NAMESPACE = "http://www.opengis.net/ogc";
    public static final String OGC_PREFIX = "ogc";

    public static final String WFS_NAMESPACE = "http://www.opengis.net/wfs";
    public static final String WFS_PREFIX = "wfs";

    public static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String XSI_PREFIX = "xsi";

    public static final String XS_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    public static final String XS_PREFIX = "xs";


    private static final String TAG_TRANSACTION = "Transaction";
    private static final String TAG_INSERT = "Insert";
    private static final String TAG_UPDATE = "Update";
    private static final String TAG_DELETE = "Delete";
    private static final String TAG_NATIVE = "Native";
    private static final String TAG_LOCKID = "LockId";
    private static final String TAG_PROPERTY = "Property";
    private static final String TAG_NAME = "Name";
    private static final String TAG_VALUE = "Value";
    private static final String TAG_FILTER = "Filter";
    
    private static final String PROP_SERVICE = "service";
    private static final String PROP_VERSION = "version";
    private static final String PROP_RELEASEACTION = "releaseAction";
    private static final String PROP_VENDORID = "vendorId";
    private static final String PROP_SAFETOIGNORE = "safeToIgnore";
    private static final String PROP_HANDLE = "handle";
    private static final String PROP_TYPENAME = "typeName";
    private static final String PROP_SRSNAME = "srsName";
    private static final String PROP_INPUTFORMAT = "inputFormat";
    private static final String PROP_IDGEN = "idgen";
    private static final String PROP_TYPE = "type";

    private static final String TYPE_STRING = XS_PREFIX+":string";
    private static final String TYPE_DECIMAL = XS_PREFIX+":decimal";
    private static final String TYPE_INTEGER = XS_PREFIX+":int";
    private static final String TYPE_DATE = XS_PREFIX+":date";

    private final AtomicInteger inc = new AtomicInteger();

    private static final MarshallerPool POOL = JTSWrapperMarshallerPool.getInstance();
    
    public void write(final OutputStream out, final TransactionRequest request) 
            throws XMLStreamException, FactoryException, JAXBException, DataStoreException, IOException{
        final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        final XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(out);

        // the XML header
        streamWriter.writeStartDocument("UTF-8", "1.0");
        
        //set the namespaces
        streamWriter.setDefaultNamespace(WFS_NAMESPACE);
        streamWriter.setPrefix(GML_PREFIX, GML_NAMESPACE);
        streamWriter.setPrefix(OGC_PREFIX, OGC_NAMESPACE);
        streamWriter.setPrefix(WFS_PREFIX, WFS_NAMESPACE);
        streamWriter.setPrefix(XSI_PREFIX, XSI_NAMESPACE);
        streamWriter.setPrefix(XS_PREFIX, XS_NAMESPACE);

        //write the request
        write(streamWriter, request);

        streamWriter.writeEndDocument();

        streamWriter.flush();
        streamWriter.close();
    }

    private void write(final XMLStreamWriter writer, final TransactionRequest request) 
            throws XMLStreamException, FactoryException, JAXBException, DataStoreException, IOException{
        writer.writeStartElement(WFS_PREFIX, TAG_TRANSACTION, WFS_NAMESPACE);
        writer.writeAttribute(PROP_SERVICE, "WFS");
        writer.writeAttribute(PROP_VERSION, "1.1.0");

        //write the namespaces
        writer.writeAttribute("xmlns:"+GML_PREFIX, GML_NAMESPACE);
        writer.writeAttribute("xmlns:"+OGC_PREFIX, OGC_NAMESPACE);
        writer.writeAttribute("xmlns:"+WFS_PREFIX, WFS_NAMESPACE);
        writer.writeAttribute("xmlns:"+XSI_PREFIX, XSI_NAMESPACE);
        writer.writeAttribute("xmlns:"+XS_PREFIX, XS_NAMESPACE);


        //write action if there is one------------------------------------------
        final ReleaseAction action = request.getReleaseAction();
        if(action != null){
            writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_RELEASEACTION, action.name());
        }

        //write lock if there is one--------------------------------------------
        final String lockId = request.getLockId();
        if(lockId != null){
            writer.writeStartElement(WFS_PREFIX, TAG_LOCKID, WFS_NAMESPACE);
            writer.writeCharacters(lockId);
            writer.writeEndElement();
        }

        //write the transaction elements----------------------------------------
        for(TransactionElement t : request.elements()){
            if(t instanceof Insert){
                write(writer,(Insert)t);
            }else if(t instanceof Update){
                write(writer,(Update)t);
            }else if(t instanceof Delete){
                write(writer,(Delete)t);
            }else if(t instanceof Native){
                write(writer,(Native)t);
            }
        }

        writer.writeEndElement();
    }


//   <xsd:element name="Insert" type="wfs:InsertElementType"/>
//   <xsd:complexType name="InsertElementType">
//        <xsd:choice>
//            <xsd:element ref="gml:_FeatureCollection" />
//            <xsd:sequence>
//               <xsd:element ref="gml:_Feature" maxOccurs="unbounded"/>
//            </xsd:sequence>
//        </xsd:choice>
//        <xsd:attribute name="idgen"
//                     type="wfs:IdentifierGenerationOptionType"
//                     use="optional" default="GenerateNew"/>
//     <xsd:attribute name="handle" type="xsd:string" use="optional"/>
//     <xsd:attribute name="inputFormat" type="xsd:string"
//                     use="optional" default="text/xml; subversion=gml/3.1.1"/>
//     <xsd:attribute name="srsName" type="xsd:anyURI" use="optional"/>
//  </xsd:complexType>
//  <xsd:simpleType name="IdentifierGenerationOptionType">
//     <xsd:restriction base="xsd:string">
//        <xsd:enumeration value="UseExisting"/>
//        <xsd:enumeration value="ReplaceDuplicate"/>
//        <xsd:enumeration value="GenerateNew"/>
//     </xsd:restriction>
//  </xsd:simpleType>
    private void write(final XMLStreamWriter writer, final Insert element)
            throws XMLStreamException, FactoryException, JAXBException, DataStoreException, IOException{
        writer.writeStartElement(WFS_PREFIX, TAG_INSERT, WFS_NAMESPACE);

        //write id gen----------------------------------------------------------
        final IdentifierGenerationOption opt = element.getIdentifierGenerationOption();
        if(opt != null){
            writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_IDGEN, opt.value());
        }

        //write handle----------------------------------------------------------
        final String handle = element.getHandle();
        if(handle != null){
            writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_HANDLE, handle);
        }

        //write format----------------------------------------------------------
        final String format = element.getInputFormat();
        if(format != null){
            writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_INPUTFORMAT, format);
        }

        //write crs-------------------------------------------------------------
        final CoordinateReferenceSystem crs = element.getCoordinateReferenceSystem();
        if(crs != null){
            final String id = CRS.lookupIdentifier(Citations.URN_OGC, crs, true);
            writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_SRSNAME, id);
        }

        //write features--------------------------------------------------------
        final FeatureCollection col = element.getFeatures();
        final JAXPStreamFeatureWriter fw = new JAXPStreamFeatureWriter();
        fw.setOutput(writer);
        fw.writeFeatureCollection(col,true);
        
        writer.writeEndElement();
    }

//    <xsd:element name="Update" type="wfs:UpdateElementType"/>
//    <xsd:complexType name="UpdateElementType">
//       <xsd:sequence>
//          <xsd:element ref="wfs:Property" maxOccurs="unbounded"/>
//          <xsd:element ref="ogc:Filter" minOccurs="0" maxOccurs="1"/>
//       </xsd:sequence>
//       <xsd:attribute name="handle" type="xsd:string" use="optional"/>
//       <xsd:attribute name="typeName" type="xsd:QName" use="required"/>
//       <xsd:attribute name="inputFormat" type="xsd:string"
//                       use="optional" default="text/xml; subversion=gml/3.1.1"/>
//       <xsd:attribute name="srsName" type="xsd:anyURI" use="optional"/>
//    </xsd:complexType>
//    <xsd:element name="Property" type="wfs:PropertyType"/>
//    <xsd:complexType name="PropertyType">
//       <xsd:sequence>
//          <xsd:element name="Name" type="xsd:QName"/>
//          <xsd:element name="Value" minOccurs="0"/>
//       </xsd:sequence>
//    </xsd:complexType>
    private void write(final XMLStreamWriter writer, final Update element)
            throws XMLStreamException, FactoryException, JAXBException{
        writer.writeStartElement(WFS_PREFIX, TAG_UPDATE, WFS_NAMESPACE);

        //write typename--------------------------------------------------------
        final Name typeName = element.getTypeName();
        final String prefix = "geons"+inc.incrementAndGet();
        writer.writeAttribute("xmlns:"+prefix, typeName.getNamespaceURI());
        writer.writeAttribute(PROP_TYPENAME, prefix+":"+typeName.getLocalPart());

        //write crs-------------------------------------------------------------
        final CoordinateReferenceSystem crs = element.getCoordinateReferenceSystem();
        if(crs != null){
            final String id = CRS.lookupIdentifier(Citations.URN_OGC, crs, true);
            writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_SRSNAME, id);
        }

        //write format----------------------------------------------------------
        final String format = element.getInputFormat();
        if(format != null){
            writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_INPUTFORMAT, format);
        }

        //write handle----------------------------------------------------------
        final String handle = element.getHandle();
        if(handle != null){
            writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_HANDLE, handle);
        }

        //write filter ---------------------------------------------------------
        final Filter filter = element.getFilter();
        if(filter != null){
            final XMLUtilities util = new XMLUtilities();
            final Marshaller marshaller = XMLUtilities.getJaxbContext110().acquireMarshaller();
            marshaller.setProperty(marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            final Object jaxbelement = util.getTransformerXMLv110().visit(filter);
            try {
                marshaller.marshal(jaxbelement, writer);
            } finally {
                 XMLUtilities.getJaxbContext110().release(marshaller);
            }
            writer.flush();
        }

        //write properties------------------------------------------------------
        for(final Entry<PropertyDescriptor,Object> entry : element.updates().entrySet()){
            writer.writeStartElement(WFS_PREFIX, TAG_PROPERTY, WFS_NAMESPACE);

            //write namespace
            final Name name = entry.getKey().getName();
            String pref = writer.getNamespaceContext().getPrefix(name.getNamespaceURI());
            if(pref == null){
                pref = "geons"+inc.incrementAndGet();
                writer.writeAttribute("xmlns:"+pref, name.getNamespaceURI());
            }
            

            //write name
            writer.writeStartElement(WFS_PREFIX, TAG_NAME, WFS_NAMESPACE);
            writer.writeCharacters(pref+":"+name.getLocalPart());
            writer.writeEndElement();

            //write value
            Object value = entry.getValue();
            if(value != null){
                //todo must handle geometry differently

                if(value instanceof Geometry){
                    final GeometryDescriptor desc = (GeometryDescriptor) entry.getKey();
                    value = JTSUtils.toISO( (Geometry)value, desc.getCoordinateReferenceSystem());
                    Marshaller marshaller = null;
                    try {
                        marshaller = POOL.acquireMarshaller();
                        marshaller.setProperty(marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                        marshaller.marshal(new ObjectFactory().createValue(value), writer);
                    } finally {
                        if (marshaller != null) {
                            POOL.release(marshaller);
                        }
                    }
                    
                }else if(value instanceof org.opengis.geometry.Geometry){
                    Marshaller marshaller = null;
                    try {
                        marshaller = POOL.acquireMarshaller();
                        marshaller.setProperty(marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                        marshaller.marshal(new ObjectFactory().createValue(value), writer);
                    } finally {
                        if (marshaller != null) {
                            POOL.release(marshaller);
                        }
                    }
                }else{
                    writer.writeStartElement(WFS_PREFIX, TAG_VALUE, WFS_NAMESPACE);
                    writer.writeAttribute(XSI_PREFIX, XSI_NAMESPACE, PROP_TYPE, bestType(value));
                    writer.writeCharacters(value.toString());
                    writer.writeEndElement();
                }
            }

            writer.writeEndElement();
        }

        writer.writeEndElement();
    }

//    <xsd:element name="Delete" type="wfs:DeleteElementType"/>
//    <xsd:complexType name="DeleteElementType">
//       <xsd:sequence>
//          <xsd:element ref="ogc:Filter" minOccurs="1" maxOccurs="1"/>
//       </xsd:sequence>
//       <xsd:attribute name="handle" type="xsd:string" use="optional"/>
//       <xsd:attribute name="typeName" type="xsd:QName" use="required"/>
//    </xsd:complexType>
    private void write(final XMLStreamWriter writer, final Delete element) throws XMLStreamException, JAXBException{
        writer.writeStartElement(WFS_PREFIX, TAG_DELETE, WFS_NAMESPACE);

        //write typename--------------------------------------------------------
        final Name typeName = element.getTypeName();
        final String prefix = "geons"+inc.incrementAndGet();
        writer.writeAttribute("xmlns:"+prefix, typeName.getNamespaceURI());
        writer.writeAttribute(PROP_TYPENAME, prefix+":"+typeName.getLocalPart());

        //write handle----------------------------------------------------------
        final String handle = element.getHandle();
        if(handle != null){
            writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_HANDLE, handle);
        }

        //write filter ---------------------------------------------------------
        final XMLUtilities util = new XMLUtilities();
        final Marshaller marshaller = util.getJaxbContext110().acquireMarshaller();
        marshaller.setProperty(marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        final Object jaxbelement = util.getTransformerXMLv110().visit(element.getFilter());
        try {
            marshaller.marshal(jaxbelement, writer);
        } finally {
            util.getJaxbContext110().release(marshaller);
        }
            
        writer.writeEndElement();
    }

//    <xsd:element name="Native" type="wfs:NativeType"/>
//    <xsd:complexType name="NativeType">
//       <xsd:attribute name="vendorId" type="xsd:string" use="required"/>
//       <xsd:attribute name="safeToIgnore" type="xsd:boolean" use="required"/>
//    </xsd:complexType>
    private void write(final XMLStreamWriter writer, final Native element) throws XMLStreamException{
        writer.writeStartElement(WFS_PREFIX, TAG_NATIVE, WFS_NAMESPACE);

        writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_VENDORID, element.getVendorId());
        writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_SAFETOIGNORE, Boolean.valueOf(element.isSafeToIgnore()).toString());

        writer.writeEndElement();
    }

    private static String bestType(final Object candidate){

        if(candidate instanceof String){
            return TYPE_STRING;
        }else if(candidate instanceof Integer){
            return TYPE_INTEGER;
        }else if(candidate instanceof Float || candidate instanceof Double){
            return TYPE_DECIMAL;
        }else if(candidate instanceof org.opengis.geometry.Geometry){
            //is that correct ?
            return GML_PREFIX+':'+"Geometry";
        }else{
            throw new IllegalArgumentException("Unexpected attribut type : "+ candidate.getClass());
        }

        
    }

}
