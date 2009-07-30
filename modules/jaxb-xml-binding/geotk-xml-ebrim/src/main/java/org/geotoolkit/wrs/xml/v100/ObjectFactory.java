/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wrs.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.v202.Capabilities;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.cat.wrs._1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _RecordId_QNAME        = new QName("http://www.opengis.net/cat/wrs/1.0", "RecordId");
    private static final QName _Capabilities_QNAME    = new QName("http://www.opengis.net/cat/wrs/1.0", "Capabilities");
    private static final QName _ExtrinsicObject_QNAME = new QName("http://www.opengis.net/cat/wrs/1.0", "ExtrinsicObject");
    private static final QName _AnyValue_QNAME        = new QName("http://www.opengis.net/cat/wrs/1.0", "AnyValue");
    private static final QName _ValueList_QNAME       = new QName("http://www.opengis.net/cat/wrs/1.0", "ValueList");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.cat.wrs._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AnyValueType }
     * 
     */
    public AnyValueType createAnyValueType() {
        return new AnyValueType();
    }

    /**
     * Create an instance of {@link SimpleLinkType }
     * 
     */
    public SimpleLinkType createSimpleLinkType() {
        return new SimpleLinkType();
    }

    /**
     * Create an instance of {@link RecordIdType }
     * 
     */
    public RecordIdType createRecordIdType() {
        return new RecordIdType();
    }

    /**
     * Create an instance of {@link ValueListType }
     * 
     */
    public ValueListType createValueListType() {
        return new ValueListType();
    }

    /**
     * Create an instance of {@link ExtrinsicObjectType }
     * 
     */
    public ExtrinsicObjectType createExtrinsicObjectType() {
        return new ExtrinsicObjectType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecordIdType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/wrs/1.0", name = "RecordId", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "_Id")
    public JAXBElement<RecordIdType> createRecordId(RecordIdType value) {
        return new JAXBElement<RecordIdType>(_RecordId_QNAME, RecordIdType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CapabilitiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/wrs/1.0", name = "Capabilities")
    public JAXBElement<Capabilities> createCapabilities(Capabilities value) {
        return new JAXBElement<Capabilities>(_Capabilities_QNAME, Capabilities.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtrinsicObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/wrs/1.0", name = "ExtrinsicObject", substitutionHeadNamespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", substitutionHeadName = "ExtrinsicObject")
    public JAXBElement<ExtrinsicObjectType> createExtrinsicObject(ExtrinsicObjectType value) {
        return new JAXBElement<ExtrinsicObjectType>(_ExtrinsicObject_QNAME, ExtrinsicObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AnyValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/wrs/1.0", name = "AnyValue")
    public JAXBElement<AnyValueType> createAnyValue(AnyValueType value) {
        return new JAXBElement<AnyValueType>(_AnyValue_QNAME, AnyValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValueListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/wrs/1.0", name = "ValueList", substitutionHeadNamespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", substitutionHeadName = "ValueList")
    public JAXBElement<ValueListType> createValueList(ValueListType value) {
        return new JAXBElement<ValueListType>(_ValueList_QNAME, ValueListType.class, null, value);
    }

}
