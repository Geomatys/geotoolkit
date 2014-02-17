/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.wfs.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.wfs package. 
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

    private final static QName _Native_QNAME = new QName("http://www.opengis.net/wfs", "Native");
    private final static QName _GetFeature_QNAME = new QName("http://www.opengis.net/wfs", "GetFeature");
    private final static QName _SUCCESS_QNAME = new QName("http://www.opengis.net/wfs", "SUCCESS");
    private final static QName _LockId_QNAME = new QName("http://www.opengis.net/wfs", "LockId");
    private final static QName _WFSTransactionResponse_QNAME = new QName("http://www.opengis.net/wfs", "WFS_TransactionResponse");
    private final static QName _Property_QNAME = new QName("http://www.opengis.net/wfs", "Property");
    private final static QName _Insert_QNAME = new QName("http://www.opengis.net/wfs", "Insert");
    private final static QName _DescribeFeatureType_QNAME = new QName("http://www.opengis.net/wfs", "DescribeFeatureType");
    private final static QName _WFSLockFeatureResponse_QNAME = new QName("http://www.opengis.net/wfs", "WFS_LockFeatureResponse");
    private final static QName _GetFeatureWithLock_QNAME = new QName("http://www.opengis.net/wfs", "GetFeatureWithLock");
    private final static QName _LockFeature_QNAME = new QName("http://www.opengis.net/wfs", "LockFeature");
    private final static QName _FAILED_QNAME = new QName("http://www.opengis.net/wfs", "FAILED");
    private final static QName _PARTIAL_QNAME = new QName("http://www.opengis.net/wfs", "PARTIAL");
    private final static QName _Delete_QNAME = new QName("http://www.opengis.net/wfs", "Delete");
    private final static QName _FeatureCollection_QNAME = new QName("http://www.opengis.net/wfs", "FeatureCollection");
    private final static QName _GetCapabilities_QNAME = new QName("http://www.opengis.net/wfs", "GetCapabilities");
    private final static QName _Transaction_QNAME = new QName("http://www.opengis.net/wfs", "Transaction");
    private final static QName _Update_QNAME = new QName("http://www.opengis.net/wfs", "Update");
    private final static QName _Query_QNAME = new QName("http://www.opengis.net/wfs", "Query");
    private static final QName _WFSCapabilities_QNAME = new QName("http://www.opengis.net/wfs", "WFS_Capabilities");
    private final static QName _RequestTypeDescribeFeatureType_QNAME = new QName("http://www.opengis.net/wfs", "DescribeFeatureType");
    private final static QName _RequestTypeGetFeature_QNAME = new QName("http://www.opengis.net/wfs", "GetFeature");
    private final static QName _RequestTypeLockFeature_QNAME = new QName("http://www.opengis.net/wfs", "LockFeature");
    private final static QName _RequestTypeGetCapabilities_QNAME = new QName("http://www.opengis.net/wfs", "GetCapabilities");
    private final static QName _RequestTypeTransaction_QNAME = new QName("http://www.opengis.net/wfs", "Transaction");
    private final static QName _RequestTypeGetFeatureWithLock_QNAME = new QName("http://www.opengis.net/wfs", "GetFeatureWithLock");
    private final static QName _OnlineResource_QNAME = new QName("http://www.opengis.net/wfs", "OnlineResource");
    private final static QName _Keywords_QNAME = new QName("http://www.opengis.net/wfs", "Keywords");
    private final static QName _SRS_QNAME = new QName("http://www.opengis.net/wfs", "SRS");
    private final static QName _Abstract_QNAME = new QName("http://www.opengis.net/wfs", "Abstract");
    private final static QName _VendorSpecificCapabilities_QNAME = new QName("http://www.opengis.net/wfs", "VendorSpecificCapabilities");
    private final static QName _XMLSCHEMA_QNAME = new QName("http://www.opengis.net/wfs", "XMLSCHEMA");
    private final static QName _Fees_QNAME = new QName("http://www.opengis.net/wfs", "Fees");
    private final static QName _GML2_QNAME = new QName("http://www.opengis.net/wfs", "GML2");
    private final static QName _Title_QNAME = new QName("http://www.opengis.net/wfs", "Title");
    private final static QName _Lock_QNAME = new QName("http://www.opengis.net/wfs", "Lock");
    private final static QName _AccessConstraints_QNAME = new QName("http://www.opengis.net/wfs", "AccessConstraints");
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.wfs
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FeaturesLockedType }
     * 
     */
    public FeaturesLockedType createFeaturesLockedType() {
        return new FeaturesLockedType();
    }

    /**
     * Create an instance of {@link DeleteElementType }
     * 
     */
    public DeleteElementType createDeleteElementType() {
        return new DeleteElementType();
    }

    /**
     * Create an instance of {@link EmptyType }
     * 
     */
    public EmptyType createEmptyType() {
        return new EmptyType();
    }

    /**
     * Create an instance of {@link GetFeatureType }
     * 
     */
    public GetFeatureType createGetFeatureType() {
        return new GetFeatureType();
    }

    /**
     * Create an instance of {@link PropertyType }
     * 
     */
    public PropertyType createPropertyType() {
        return new PropertyType();
    }

    /**
     * Create an instance of {@link WFSTransactionResponseType }
     * 
     */
    public WFSTransactionResponseType createWFSTransactionResponseType() {
        return new WFSTransactionResponseType();
    }

    /**
     * Create an instance of {@link GetCapabilitiesType }
     * 
     */
    public GetCapabilitiesType createGetCapabilitiesType() {
        return new GetCapabilitiesType();
    }

    /**
     * Create an instance of {@link NativeType }
     * 
     */
    public NativeType createNativeType() {
        return new NativeType();
    }

    /**
     * Create an instance of {@link InsertResultType }
     * 
     */
    public InsertResultType createInsertResultType() {
        return new InsertResultType();
    }

    /**
     * Create an instance of {@link TransactionResultType }
     * 
     */
    public TransactionResultType createTransactionResultType() {
        return new TransactionResultType();
    }

    /**
     * Create an instance of {@link DescribeFeatureTypeType }
     * 
     */
    public DescribeFeatureTypeType createDescribeFeatureTypeType() {
        return new DescribeFeatureTypeType();
    }

    /**
     * Create an instance of {@link LockType }
     * 
     */
    public LockType createLockType() {
        return new LockType();
    }

    /**
     * Create an instance of {@link FeaturesNotLockedType }
     * 
     */
    public FeaturesNotLockedType createFeaturesNotLockedType() {
        return new FeaturesNotLockedType();
    }

    /**
     * Create an instance of {@link StatusType }
     * 
     */
    public StatusType createStatusType() {
        return new StatusType();
    }

    /**
     * Create an instance of {@link FeatureCollectionType }
     * 
     */
    public FeatureCollectionType createFeatureCollectionType() {
        return new FeatureCollectionType();
    }

    /**
     * Create an instance of {@link GetFeatureWithLockType }
     * 
     */
    public GetFeatureWithLockType createGetFeatureWithLockType() {
        return new GetFeatureWithLockType();
    }

    /**
     * Create an instance of {@link UpdateElementType }
     * 
     */
    public UpdateElementType createUpdateElementType() {
        return new UpdateElementType();
    }

    /**
     * Create an instance of {@link QueryType }
     * 
     */
    public QueryType createQueryType() {
        return new QueryType();
    }

    /**
     * Create an instance of {@link WFSLockFeatureResponseType }
     * 
     */
    public WFSLockFeatureResponseType createWFSLockFeatureResponseType() {
        return new WFSLockFeatureResponseType();
    }

    /**
     * Create an instance of {@link InsertElementType }
     * 
     */
    public InsertElementType createInsertElementType() {
        return new InsertElementType();
    }

    /**
     * Create an instance of {@link TransactionType }
     * 
     */
    public TransactionType createTransactionType() {
        return new TransactionType();
    }

    /**
     * Create an instance of {@link LockFeatureType }
     * 
     */
    public LockFeatureType createLockFeatureType() {
        return new LockFeatureType();
    }
    
    /**
     * Create an instance of {@link WFSCapabilitiesType }
     * 
     */
    public WFSCapabilitiesType createWFSCapabilitiesType() {
        return new WFSCapabilitiesType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NativeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Native")
    public JAXBElement<NativeType> createNative(NativeType value) {
        return new JAXBElement<NativeType>(_Native_QNAME, NativeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFeatureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "GetFeature")
    public JAXBElement<GetFeatureType> createGetFeature(GetFeatureType value) {
        return new JAXBElement<GetFeatureType>(_GetFeature_QNAME, GetFeatureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmptyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "SUCCESS")
    public JAXBElement<EmptyType> createSUCCESS(EmptyType value) {
        return new JAXBElement<EmptyType>(_SUCCESS_QNAME, EmptyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "LockId")
    public JAXBElement<String> createLockId(String value) {
        return new JAXBElement<String>(_LockId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WFSTransactionResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "WFS_TransactionResponse")
    public JAXBElement<WFSTransactionResponseType> createWFSTransactionResponse(WFSTransactionResponseType value) {
        return new JAXBElement<WFSTransactionResponseType>(_WFSTransactionResponse_QNAME, WFSTransactionResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Property")
    public JAXBElement<PropertyType> createProperty(PropertyType value) {
        return new JAXBElement<PropertyType>(_Property_QNAME, PropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertElementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Insert")
    public JAXBElement<Object> createInsert(Object value) {
        return new JAXBElement<Object>(_Insert_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeFeatureTypeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "DescribeFeatureType")
    public JAXBElement<DescribeFeatureTypeType> createDescribeFeatureType(DescribeFeatureTypeType value) {
        return new JAXBElement<DescribeFeatureTypeType>(_DescribeFeatureType_QNAME, DescribeFeatureTypeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WFSLockFeatureResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "WFS_LockFeatureResponse")
    public JAXBElement<WFSLockFeatureResponseType> createWFSLockFeatureResponse(WFSLockFeatureResponseType value) {
        return new JAXBElement<WFSLockFeatureResponseType>(_WFSLockFeatureResponse_QNAME, WFSLockFeatureResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFeatureWithLockType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "GetFeatureWithLock")
    public JAXBElement<GetFeatureWithLockType> createGetFeatureWithLock(GetFeatureWithLockType value) {
        return new JAXBElement<GetFeatureWithLockType>(_GetFeatureWithLock_QNAME, GetFeatureWithLockType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LockFeatureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "LockFeature")
    public JAXBElement<LockFeatureType> createLockFeature(LockFeatureType value) {
        return new JAXBElement<LockFeatureType>(_LockFeature_QNAME, LockFeatureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmptyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "FAILED")
    public JAXBElement<EmptyType> createFAILED(EmptyType value) {
        return new JAXBElement<EmptyType>(_FAILED_QNAME, EmptyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmptyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "PARTIAL")
    public JAXBElement<EmptyType> createPARTIAL(EmptyType value) {
        return new JAXBElement<EmptyType>(_PARTIAL_QNAME, EmptyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmptyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Delete")
    public JAXBElement<Object> createDelete(Object value) {
        return new JAXBElement<Object>(_Delete_QNAME, Object.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeatureCollectionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "FeatureCollection", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeatureCollection")
    public JAXBElement<FeatureCollectionType> createFeatureCollection(FeatureCollectionType value) {
        return new JAXBElement<FeatureCollectionType>(_FeatureCollection_QNAME, FeatureCollectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "GetCapabilities")
    public JAXBElement<GetCapabilitiesType> createGetCapabilities(GetCapabilitiesType value) {
        return new JAXBElement<GetCapabilitiesType>(_GetCapabilities_QNAME, GetCapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Transaction")
    public JAXBElement<TransactionType> createTransaction(TransactionType value) {
        return new JAXBElement<TransactionType>(_Transaction_QNAME, TransactionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmptyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Update")
    public JAXBElement<Object> createUpdate(Object value) {
        return new JAXBElement<Object>(_Update_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Query")
    public JAXBElement<Object> createQuery(Object value) {
        return new JAXBElement<Object>(_Query_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WFSCapabilitiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "WFS_Capabilities")
    public JAXBElement<WFSCapabilitiesType> createWFSCapabilities(final WFSCapabilitiesType value) {
        return new JAXBElement<WFSCapabilitiesType>(_WFSCapabilities_QNAME, WFSCapabilitiesType.class, null, value);
    }
    
     /**
     * Create an instance of {@link SchemaDescriptionLanguageType }
     * 
     */
    public SchemaDescriptionLanguageType createSchemaDescriptionLanguageType() {
        return new SchemaDescriptionLanguageType();
    }

    /**
     * Create an instance of {@link MetadataURLType }
     * 
     */
    public MetadataURLType createMetadataURLType() {
        return new MetadataURLType();
    }

    /**
     * Create an instance of {@link OperationsType }
     * 
     */
    public OperationsType createOperationsType() {
        return new OperationsType();
    }

    /**
     * Create an instance of {@link RequestType }
     * 
     */
    public RequestType createRequestType() {
        return new RequestType();
    }

    /**
     * Create an instance of {@link PostType }
     * 
     */
    public PostType createPostType() {
        return new PostType();
    }

    /**
     * Create an instance of {@link HTTPType }
     * 
     */
    public HTTPType createHTTPType() {
        return new HTTPType();
    }

    /**
     * Create an instance of {@link ServiceType }
     * 
     */
    public ServiceType createServiceType() {
        return new ServiceType();
    }

    /**
     * Create an instance of {@link FeatureTypeListType }
     * 
     */
    public FeatureTypeListType createFeatureTypeListType() {
        return new FeatureTypeListType();
    }

    /**
     * Create an instance of {@link GetFeatureTypeType }
     * 
     */
    public GetFeatureTypeType createGetFeatureTypeType() {
        return new GetFeatureTypeType();
    }

    /**
     * Create an instance of {@link LockFeatureTypeType }
     * 
     */
    public LockFeatureTypeType createLockFeatureTypeType() {
        return new LockFeatureTypeType();
    }

    /**
     * Create an instance of {@link CapabilityType }
     * 
     */
    public CapabilityType createCapabilityType() {
        return new CapabilityType();
    }

    /**
     * Create an instance of {@link GetType }
     * 
     */
    public GetType createGetType() {
        return new GetType();
    }

    /**
     * Create an instance of {@link ResultFormatType }
     * 
     */
    public ResultFormatType createResultFormatType() {
        return new ResultFormatType();
    }

    /**
     * Create an instance of {@link FeatureTypeType }
     * 
     */
    public FeatureTypeType createFeatureTypeType() {
        return new FeatureTypeType();
    }

    /**
     * Create an instance of {@link LatLongBoundingBoxType }
     * 
     */
    public LatLongBoundingBoxType createLatLongBoundingBoxType() {
        return new LatLongBoundingBoxType();
    }

    /**
     * Create an instance of {@link DCPTypeType }
     * 
     */
    public DCPTypeType createDCPTypeType() {
        return new DCPTypeType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeFeatureTypeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "DescribeFeatureType", scope = RequestType.class)
    public JAXBElement<DescribeFeatureTypeType> createRequestTypeDescribeFeatureType(DescribeFeatureTypeType value) {
        return new JAXBElement<DescribeFeatureTypeType>(_RequestTypeDescribeFeatureType_QNAME, DescribeFeatureTypeType.class, RequestType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFeatureTypeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "GetFeature", scope = RequestType.class)
    public JAXBElement<GetFeatureTypeType> createRequestTypeGetFeature(GetFeatureTypeType value) {
        return new JAXBElement<GetFeatureTypeType>(_RequestTypeGetFeature_QNAME, GetFeatureTypeType.class, RequestType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LockFeatureTypeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "LockFeature", scope = RequestType.class)
    public JAXBElement<LockFeatureTypeType> createRequestTypeLockFeature(LockFeatureTypeType value) {
        return new JAXBElement<LockFeatureTypeType>(_RequestTypeLockFeature_QNAME, LockFeatureTypeType.class, RequestType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "GetCapabilities", scope = RequestType.class)
    public JAXBElement<GetCapabilitiesType> createRequestTypeGetCapabilities(GetCapabilitiesType value) {
        return new JAXBElement<GetCapabilitiesType>(_RequestTypeGetCapabilities_QNAME, GetCapabilitiesType.class, RequestType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Transaction", scope = RequestType.class)
    public JAXBElement<TransactionType> createRequestTypeTransaction(TransactionType value) {
        return new JAXBElement<TransactionType>(_RequestTypeTransaction_QNAME, TransactionType.class, RequestType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFeatureTypeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "GetFeatureWithLock", scope = RequestType.class)
    public JAXBElement<GetFeatureTypeType> createRequestTypeGetFeatureWithLock(GetFeatureTypeType value) {
        return new JAXBElement<GetFeatureTypeType>(_RequestTypeGetFeatureWithLock_QNAME, GetFeatureTypeType.class, RequestType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "OnlineResource")
    public JAXBElement<Object> createOnlineResource(Object value) {
        return new JAXBElement<Object>(_OnlineResource_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Keywords")
    public JAXBElement<String> createKeywords(String value) {
        return new JAXBElement<String>(_Keywords_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "SRS")
    public JAXBElement<String> createSRS(String value) {
        return new JAXBElement<String>(_SRS_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Abstract")
    public JAXBElement<String> createAbstract(String value) {
        return new JAXBElement<String>(_Abstract_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "VendorSpecificCapabilities")
    public JAXBElement<String> createVendorSpecificCapabilities(String value) {
        return new JAXBElement<String>(_VendorSpecificCapabilities_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmptyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "XMLSCHEMA")
    public JAXBElement<EmptyType> createXMLSCHEMA(EmptyType value) {
        return new JAXBElement<EmptyType>(_XMLSCHEMA_QNAME, EmptyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Fees")
    public JAXBElement<String> createFees(String value) {
        return new JAXBElement<String>(_Fees_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmptyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "GML2")
    public JAXBElement<EmptyType> createGML2(EmptyType value) {
        return new JAXBElement<EmptyType>(_GML2_QNAME, EmptyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Title")
    public JAXBElement<String> createTitle(String value) {
        return new JAXBElement<String>(_Title_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmptyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "Lock")
    public JAXBElement<EmptyType> createLock(EmptyType value) {
        return new JAXBElement<EmptyType>(_Lock_QNAME, EmptyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wfs", name = "AccessConstraints")
    public JAXBElement<String> createAccessConstraints(String value) {
        return new JAXBElement<String>(_AccessConstraints_QNAME, String.class, null, value);
    }

}
