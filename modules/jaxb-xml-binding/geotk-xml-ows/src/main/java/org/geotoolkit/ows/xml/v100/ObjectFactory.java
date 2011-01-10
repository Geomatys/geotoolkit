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
package org.geotoolkit.ows.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.ows package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * @module pending
 */
@XmlRegistry
public class ObjectFactory {

    public static final QName _GetCapabilities_QNAME = new QName("http://www.opengis.net/ows", "GetCapabilities");
    public static final QName _Language_QNAME = new QName("http://www.opengis.net/ows", "Language");
    public static final QName _PositionName_QNAME = new QName("http://www.opengis.net/ows", "PositionName");
    public static final QName _Keywords_QNAME = new QName("http://www.opengis.net/ows", "Keywords");
    public static final QName _ContactInfo_QNAME = new QName("http://www.opengis.net/ows", "ContactInfo");
    public static final QName _AbstractMetaData_QNAME = new QName("http://www.opengis.net/ows", "AbstractMetaData");
    public static final QName _WGS84BoundingBox_QNAME = new QName("http://www.opengis.net/ows", "WGS84BoundingBox");
    public static final QName _ExtendedCapabilities_QNAME = new QName("http://www.opengis.net/ows", "ExtendedCapabilities");
    public static final QName _Abstract_QNAME = new QName("http://www.opengis.net/ows", "Abstract");
    public static final QName _PointOfContact_QNAME = new QName("http://www.opengis.net/ows", "PointOfContact");
    public static final QName _Title_QNAME = new QName("http://www.opengis.net/ows", "Title");
    public static final QName _OrganisationName_QNAME = new QName("http://www.opengis.net/ows", "OrganisationName");
    public static final QName _Role_QNAME = new QName("http://www.opengis.net/ows", "Role");
    public static final QName _Fees_QNAME = new QName("http://www.opengis.net/ows", "Fees");
    public static final QName _IndividualName_QNAME = new QName("http://www.opengis.net/ows", "IndividualName");
    public static final QName _Exception_QNAME = new QName("http://www.opengis.net/ows", "Exception");
    public static final QName _SupportedCRS_QNAME = new QName("http://www.opengis.net/ows", "SupportedCRS");
    public static final QName _OutputFormat_QNAME = new QName("http://www.opengis.net/ows", "OutputFormat");
    public static final QName _AvailableCRS_QNAME = new QName("http://www.opengis.net/ows", "AvailableCRS");
    public static final QName _AccessConstraints_QNAME = new QName("http://www.opengis.net/ows", "AccessConstraints");
    public static final QName _Metadata_QNAME = new QName("http://www.opengis.net/ows", "Metadata");
    public static final QName _Identifier_QNAME = new QName("http://www.opengis.net/ows", "Identifier");
    public static final QName _BoundingBox_QNAME = new QName("http://www.opengis.net/ows", "BoundingBox");
    public static final QName _HTTPGet_QNAME = new QName("http://www.opengis.net/ows", "Get");
    public static final QName _HTTPPost_QNAME = new QName("http://www.opengis.net/ows", "Post");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.ows
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DescriptionType }
     * 
     */
    public DescriptionType createDescriptionType() {
        return new DescriptionType();
    }

    /**
     * Create an instance of {@link ExceptionReport }
     * 
     */
    public ExceptionReport createExceptionReport() {
        return new ExceptionReport();
    }

    /**
     * Create an instance of {@link Operation }
     * 
     */
    public Operation createOperation() {
        return new Operation();
    }

    /**
     * Create an instance of {@link OnlineResourceType }
     * 
     */
    public OnlineResourceType createOnlineResourceType() {
        return new OnlineResourceType();
    }

    /**
     * Create an instance of {@link ResponsiblePartySubsetType }
     * 
     */
    public ResponsiblePartySubsetType createResponsiblePartySubsetType() {
        return new ResponsiblePartySubsetType();
    }

    /**
     * Create an instance of {@link MetadataType }
     * 
     */
    public MetadataType createMetadataType() {
        return new MetadataType();
    }

    /**
     * Create an instance of {@link BoundingBoxType }
     * 
     */
    public BoundingBoxType createBoundingBoxType() {
        return new BoundingBoxType();
    }

    /**
     * Create an instance of {@link WGS84BoundingBoxType }
     * 
     */
    public WGS84BoundingBoxType createWGS84BoundingBoxType() {
        return new WGS84BoundingBoxType();
    }

    /**
     * Create an instance of {@link HTTP }
     * 
     */
    public HTTP createHTTP() {
        return new HTTP();
    }

    /**
     * Create an instance of {@link ResponsiblePartyType }
     * 
     */
    public ResponsiblePartyType createResponsiblePartyType() {
        return new ResponsiblePartyType();
    }

    /**
     * Create an instance of {@link DomainType }
     * 
     */
    public DomainType createDomainType() {
        return new DomainType();
    }

    /**
     * Create an instance of {@link ExceptionType }
     * 
     */
    public ExceptionType createExceptionType() {
        return new ExceptionType();
    }

    /**
     * Create an instance of {@link ServiceProvider }
     * 
     */
    public ServiceProvider createServiceProvider() {
        return new ServiceProvider();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link GetCapabilitiesType }
     * 
     */
    public GetCapabilitiesType createGetCapabilitiesType() {
        return new GetCapabilitiesType();
    }

    /**
     * Create an instance of {@link ServiceIdentification }
     * 
     */
    public ServiceIdentification createServiceIdentification() {
        return new ServiceIdentification();
    }

    /**
     * Create an instance of {@link OperationsMetadata }
     * 
     */
    public OperationsMetadata createOperationsMetadata() {
        return new OperationsMetadata();
    }

    /**
     * Create an instance of {@link DCP }
     * 
     */
    public DCP createDCP() {
        return new DCP();
    }

    /**
     * Create an instance of {@link SectionsType }
     * 
     */
    public SectionsType createSectionsType() {
        return new SectionsType();
    }

    /**
     * Create an instance of {@link AcceptFormatsType }
     * 
     */
    public AcceptFormatsType createAcceptFormatsType() {
        return new AcceptFormatsType();
    }

    /**
     * Create an instance of {@link TelephoneType }
     * 
     */
    public TelephoneType createTelephoneType() {
        return new TelephoneType();
    }

    /**
     * Create an instance of {@link RequestMethodType }
     * 
     */
    public RequestMethodType createRequestMethodType() {
        return new RequestMethodType();
    }

    /**
     * Create an instance of {@link IdentificationType }
     * 
     */
    public IdentificationType createIdentificationType() {
        return new IdentificationType();
    }

    /**
     * Create an instance of {@link ContactType }
     * 
     */
    public ContactType createContactType() {
        return new ContactType();
    }

    /**
     * Create an instance of {@link CapabilitiesBaseType }
     * 
     */
    public CapabilitiesBaseType createCapabilitiesBaseType() {
        return new CapabilitiesBaseType();
    }

    /**
     * Create an instance of {@link AcceptVersionsType }
     * 
     */
    public AcceptVersionsType createAcceptVersionsType() {
        return new AcceptVersionsType();
    }

    /**
     * Create an instance of {@link CodeType }
     * 
     */
    public CodeType createCodeType() {
        return new CodeType();
    }

    /**
     * Create an instance of {@link KeywordsType }
     * 
     */
    public KeywordsType createKeywordsType() {
        return new KeywordsType();
    }

    /**
     * Create an instance of {@link KeywordsType }
     *
     */
    public AbstractExtendedCapabilitiesType createAbstractExtendedCapabilitiesType() {
        return new AbstractExtendedCapabilitiesType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "GetCapabilities")
    public JAXBElement<GetCapabilitiesType> createGetCapabilities(final GetCapabilitiesType value) {
        return new JAXBElement<GetCapabilitiesType>(_GetCapabilities_QNAME, GetCapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Language")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLanguage(final String value) {
        return new JAXBElement<String>(_Language_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "PositionName")
    public JAXBElement<String> createPositionName(final String value) {
        return new JAXBElement<String>(_PositionName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeywordsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Keywords")
    public JAXBElement<KeywordsType> createKeywords(final KeywordsType value) {
        return new JAXBElement<KeywordsType>(_Keywords_QNAME, KeywordsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContactType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "ContactInfo")
    public JAXBElement<ContactType> createContactInfo(final ContactType value) {
        return new JAXBElement<ContactType>(_ContactInfo_QNAME, ContactType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "AbstractMetaData")
    public JAXBElement<Object> createAbstractMetaData(final Object value) {
        return new JAXBElement<Object>(_AbstractMetaData_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WGS84BoundingBoxType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "WGS84BoundingBox", substitutionHeadNamespace = "http://www.opengis.net/ows", substitutionHeadName = "BoundingBox")
    public JAXBElement<WGS84BoundingBoxType> createWGS84BoundingBox(final WGS84BoundingBoxType value) {
        return new JAXBElement<WGS84BoundingBoxType>(_WGS84BoundingBox_QNAME, WGS84BoundingBoxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "ExtendedCapabilities")
    public JAXBElement<Object> createExtendedCapabilities(final Object value) {
        return new JAXBElement<Object>(_ExtendedCapabilities_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Abstract")
    public JAXBElement<String> createAbstract(final String value) {
        return new JAXBElement<String>(_Abstract_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponsiblePartyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "PointOfContact")
    public JAXBElement<ResponsiblePartyType> createPointOfContact(final ResponsiblePartyType value) {
        return new JAXBElement<ResponsiblePartyType>(_PointOfContact_QNAME, ResponsiblePartyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Title")
    public JAXBElement<String> createTitle(final String value) {
        return new JAXBElement<String>(_Title_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "OrganisationName")
    public JAXBElement<String> createOrganisationName(final String value) {
        return new JAXBElement<String>(_OrganisationName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Role")
    public JAXBElement<CodeType> createRole(final CodeType value) {
        return new JAXBElement<CodeType>(_Role_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Fees")
    public JAXBElement<String> createFees(final String value) {
        return new JAXBElement<String>(_Fees_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "IndividualName")
    public JAXBElement<String> createIndividualName(final String value) {
        return new JAXBElement<String>(_IndividualName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExceptionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Exception")
    public JAXBElement<ExceptionType> createException(final ExceptionType value) {
        return new JAXBElement<ExceptionType>(_Exception_QNAME, ExceptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "SupportedCRS", substitutionHeadNamespace = "http://www.opengis.net/ows", substitutionHeadName = "AvailableCRS")
    public JAXBElement<String> createSupportedCRS(final String value) {
        return new JAXBElement<String>(_SupportedCRS_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "OutputFormat")
    public JAXBElement<String> createOutputFormat(final String value) {
        return new JAXBElement<String>(_OutputFormat_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "AvailableCRS")
    public JAXBElement<String> createAvailableCRS(final String value) {
        return new JAXBElement<String>(_AvailableCRS_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "AccessConstraints")
    public JAXBElement<String> createAccessConstraints(final String value) {
        return new JAXBElement<String>(_AccessConstraints_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetadataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Metadata")
    public JAXBElement<MetadataType> createMetadata(final MetadataType value) {
        return new JAXBElement<MetadataType>(_Metadata_QNAME, MetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Identifier")
    public JAXBElement<CodeType> createIdentifier(final CodeType value) {
        return new JAXBElement<CodeType>(_Identifier_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BoundingBoxType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "BoundingBox")
    public JAXBElement<BoundingBoxType> createBoundingBox(final BoundingBoxType value) {
        return new JAXBElement<BoundingBoxType>(_BoundingBox_QNAME, BoundingBoxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Get", scope = HTTP.class)
    public JAXBElement<RequestMethodType> createHTTPGet(final RequestMethodType value) {
        return new JAXBElement<RequestMethodType>(_HTTPGet_QNAME, RequestMethodType.class, HTTP.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ows", name = "Post", scope = HTTP.class)
    public JAXBElement<RequestMethodType> createHTTPPost(final RequestMethodType value) {
        return new JAXBElement<RequestMethodType>(_HTTPPost_QNAME, RequestMethodType.class, HTTP.class, value);
    }

}
