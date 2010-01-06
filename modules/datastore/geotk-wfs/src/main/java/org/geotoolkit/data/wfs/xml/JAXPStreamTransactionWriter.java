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

import java.io.OutputStream;
import java.util.Map.Entry;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotoolkit.data.DataStoreException;
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
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.sld.xml.XMLUtilities;

import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JAXPStreamTransactionWriter {

    private static final String GML_NAMESPACE = "http://www.opengis.net/gml";
    private static final String GML_PREFIX = "gml";

    private static final String OGC_NAMESPACE = "http://www.opengis.net/ogc";
    private static final String OGC_PREFIX = "ogc";

    private static final String WFS_NAMESPACE = "http://www.opengis.net/wfs";
    private static final String WFS_PREFIX = "wfs";

    private static final String TAG_TRANSACTION = "Transaction";
    private static final String TAG_INSERT = "Insert";
    private static final String TAG_UPDATE = "Update";
    private static final String TAG_DELETE = "Delete";
    private static final String TAG_NATIVE = "Native";
    private static final String TAG_LOCKID = "LockId";
    private static final String TAG_PROPERTY = "Property";
    private static final String TAG_NAME = "Name";
    private static final String TAG_VALUE = "Value";
    
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

    public void write(OutputStream out, TransactionRequest request) 
            throws XMLStreamException, FactoryException, JAXBException, DataStoreException{
        final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        final XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(out);

        // the XML header
        streamWriter.writeStartDocument("UTF-8", "1.0");
        
        //set the namespaces
        streamWriter.setDefaultNamespace(WFS_NAMESPACE);
        streamWriter.setPrefix(GML_PREFIX, GML_NAMESPACE);
        streamWriter.setPrefix(OGC_PREFIX, OGC_NAMESPACE);
        streamWriter.setPrefix(WFS_PREFIX, WFS_NAMESPACE);

        write(streamWriter, request);

        streamWriter.flush();
        streamWriter.close();
    }

    private void write(XMLStreamWriter writer, TransactionRequest request) 
            throws XMLStreamException, FactoryException, JAXBException, DataStoreException{
        writer.writeStartElement(WFS_PREFIX, TAG_TRANSACTION, WFS_NAMESPACE);
        writer.writeAttribute(PROP_SERVICE, "WFS");
        writer.writeAttribute(PROP_VERSION, "1.1.0");

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
    private void write(XMLStreamWriter writer, Insert element)
            throws XMLStreamException, FactoryException, JAXBException, DataStoreException{
        writer.writeStartElement(WFS_PREFIX, TAG_INSERT, WFS_NAMESPACE);

        //write features--------------------------------------------------------
        final FeatureCollection col = element.getFeatures();
        final JAXPStreamFeatureWriter fw = new JAXPStreamFeatureWriter();
        fw.writeFeatureCollection(col,writer);
        
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
    private void write(XMLStreamWriter writer, Update element)
            throws XMLStreamException, FactoryException, JAXBException{
        writer.writeStartElement(WFS_PREFIX, TAG_UPDATE, WFS_NAMESPACE);

        //write typename--------------------------------------------------------
        final String typeName = element.getTypeName();
        writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_TYPENAME, typeName);

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
            XMLUtilities util = new XMLUtilities();
            util.writeFilter(writer, element.getFilter(), org.geotoolkit.sld.xml.Specification.Filter.V_1_1_0);
        }

        //write properties------------------------------------------------------
        for(final Entry<Name,Object> entry : element.updates().entrySet()){
            writer.writeStartElement(WFS_PREFIX, TAG_PROPERTY, WFS_NAMESPACE);

            //write name
            writer.writeStartElement(WFS_PREFIX, TAG_NAME, WFS_NAMESPACE);
            writer.writeCharacters(entry.getKey().getLocalPart());
            writer.writeEndElement();

            //write value
            if(entry.getValue() != null){
                //todo must handle geometry differently
                writer.writeStartElement(WFS_PREFIX, TAG_VALUE, WFS_NAMESPACE);
                writer.writeCharacters(entry.getValue().toString());
                writer.writeEndElement();
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
    private void write(XMLStreamWriter writer, Delete element) throws XMLStreamException, JAXBException{
        writer.writeStartElement(WFS_PREFIX, TAG_DELETE, WFS_NAMESPACE);

        //write typename--------------------------------------------------------
        final String typeName = element.getTypeName();
        writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_TYPENAME, typeName);

        //write handle----------------------------------------------------------
        final String handle = element.getHandle();
        if(handle != null){
            writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_HANDLE, handle);
        }

        //write filter ---------------------------------------------------------
        XMLUtilities util = new XMLUtilities();
        util.writeFilter(writer, element.getFilter(), org.geotoolkit.sld.xml.Specification.Filter.V_1_1_0);
        
        writer.writeEndElement();
    }

//    <xsd:element name="Native" type="wfs:NativeType"/>
//    <xsd:complexType name="NativeType">
//       <xsd:attribute name="vendorId" type="xsd:string" use="required"/>
//       <xsd:attribute name="safeToIgnore" type="xsd:boolean" use="required"/>
//    </xsd:complexType>
    private void write(XMLStreamWriter writer, Native element) throws XMLStreamException{
        writer.writeStartElement(WFS_PREFIX, TAG_NATIVE, WFS_NAMESPACE);

        writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_VENDORID, element.getVendorId());
        writer.writeAttribute(WFS_PREFIX, WFS_NAMESPACE, PROP_SAFETOIGNORE, Boolean.valueOf(element.isSafeToIgnore()).toString());

        writer.writeEndElement();
    }

}
