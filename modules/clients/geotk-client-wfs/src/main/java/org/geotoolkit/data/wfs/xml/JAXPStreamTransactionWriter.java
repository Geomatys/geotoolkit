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
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotoolkit.feature.xml.Utils;
import org.apache.sis.storage.DataStoreException;
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
import org.geotoolkit.gml.JTStoGeometry;
import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.GeometryPropertyType;
import org.geotoolkit.internal.jaxb.JTSWrapperMarshallerPool;
import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.PropertyType;

import org.opengis.util.GenericName;
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
    private static final String TYPE_BOOLEAN = XS_PREFIX+":boolean";
    private static final String TYPE_DATE = XS_PREFIX+":date";

    private final AtomicInteger inc = new AtomicInteger();

    private static final MarshallerPool POOL = JTSWrapperMarshallerPool.getInstance();
    private static final MarshallerPool GMLPOOL = GMLMarshallerPool.getInstance();

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
            final String id = IdentifiedObjects.lookupIdentifier(Citations.URN_OGC, crs, true);
            writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_SRSNAME, id);
        }

        //write features--------------------------------------------------------
        final FeatureCollection col = element.getFeatures();
        final JAXPStreamFeatureWriter fw = new JAXPStreamFeatureWriter();
        fw.setOutput(writer);
        fw.writeFeatureCollection(col,true, null);

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
        final GenericName typeName = element.getTypeName();
        final String ns = NamesExt.getNamespace(typeName);
        if (ns != null && !ns.isEmpty()) {
            final String prefix = "geons"+inc.incrementAndGet();
            writer.writeAttribute("xmlns:"+prefix, ns);
            writer.writeAttribute(PROP_TYPENAME, prefix + ':' + typeName.tip());
        } else {
            writer.writeAttribute(PROP_TYPENAME, typeName.tip().toString());
        }

        //write crs-------------------------------------------------------------
        final CoordinateReferenceSystem crs = element.getCoordinateReferenceSystem();
        if(crs != null){
            final String id = IdentifiedObjects.lookupIdentifier(Citations.URN_OGC, crs, true);
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
            final StyleXmlIO util = new StyleXmlIO();
            final Marshaller marshaller = StyleXmlIO.getJaxbContext110().acquireMarshaller();
            marshaller.setProperty(marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            final Object jaxbelement = util.getTransformerXMLv110().visit(filter);
            marshaller.marshal(jaxbelement, writer);
            StyleXmlIO.getJaxbContext110().recycle(marshaller);
            writer.flush();
        }

        //write properties------------------------------------------------------
        for(final Entry<PropertyType,Object> entry : element.updates().entrySet()){
            writer.writeStartElement(WFS_PREFIX, TAG_PROPERTY, WFS_NAMESPACE);

            //write namespace
            final GenericName name = entry.getKey().getName();
            final String ns2 = NamesExt.getNamespace(name);
            String pref = writer.getNamespaceContext().getPrefix(ns2);
            if(pref == null && ns2 != null && !ns2.isEmpty()){
                pref = "geons"+inc.incrementAndGet();
                writer.writeAttribute("xmlns:"+pref, ns2);
            }


            //write name
            writer.writeStartElement(WFS_PREFIX, TAG_NAME, WFS_NAMESPACE);
            if (pref != null) {
                writer.writeCharacters(pref + ':' + name.tip());
            } else {
                writer.writeCharacters(name.tip().toString());
            }
            writer.writeEndElement();

            //write value
            final PropertyType propertyType = entry.getKey();
            Object value = entry.getValue();

            if(value != null){
                //todo must handle geometry differently

                if(value instanceof Geometry){
                    value = new GeometryPropertyType((AbstractGeometryType)JTStoGeometry.toGML("3.1.1", (Geometry)value));
                    final Marshaller marshaller = GMLPOOL.acquireMarshaller();
                    marshaller.setProperty(marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                    marshaller.marshal(new ObjectFactory().createValue(value), writer);
                    GMLPOOL.recycle(marshaller);

                }else if(value instanceof org.opengis.geometry.Geometry){
                    final Marshaller marshaller = POOL.acquireMarshaller();
                    marshaller.setProperty(marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                    marshaller.marshal(new ObjectFactory().createValue(value), writer);
                    POOL.recycle(marshaller);
                }else{
                    writer.writeStartElement(WFS_PREFIX, TAG_VALUE, WFS_NAMESPACE);
                    QName qname = Utils.getQNameFromType(propertyType,"");
                    writer.writeAttribute(XSI_PREFIX, XSI_NAMESPACE, PROP_TYPE, qname.getLocalPart());
                    writer.writeCharacters(Utils.getStringValue(value));
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
        final GenericName typeName = element.getTypeName();
        final String ns = NamesExt.getNamespace(typeName);
        if (ns != null && !ns.isEmpty()) {
            final String prefix = "geons"+inc.incrementAndGet();
            writer.writeAttribute("xmlns:"+prefix, ns);
            writer.writeAttribute(PROP_TYPENAME, prefix + ':' + typeName.tip());
        } else {
            writer.writeAttribute(PROP_TYPENAME, typeName.tip().toString());
        }


        //write handle----------------------------------------------------------
        final String handle = element.getHandle();
        if(handle != null){
            writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_HANDLE, handle);
        }

        //write filter ---------------------------------------------------------
        final StyleXmlIO util = new StyleXmlIO();
        final Marshaller marshaller = util.getJaxbContext110().acquireMarshaller();
        marshaller.setProperty(marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        final Object jaxbelement = util.getTransformerXMLv110().visit(element.getFilter());
        marshaller.marshal(jaxbelement, writer);
        util.getJaxbContext110().recycle(marshaller);

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
        }else if(candidate instanceof Boolean){
            return TYPE_BOOLEAN;
        }else if(candidate instanceof Date){
            return TYPE_DATE;
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
