/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.csw.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each Java content interface and Java element interface 
 * generated in the org.constellation.cat.csw.v200 package. 
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding of schema 
 * type definitions, element declarations and model groups.  
 * Factory methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Capabilities_QNAME           = new QName("http://www.opengis.net/cat/csw", "Capabilities");
    private final static QName _GetRecordById_QNAME          = new QName("http://www.opengis.net/cat/csw", "GetRecordById");
    private final static QName _AbstractRecord_QNAME         = new QName("http://www.opengis.net/cat/csw", "AbstractRecord");
    private final static QName _GetDomainResponse_QNAME      = new QName("http://www.opengis.net/cat/csw", "GetDomainResponse");
    private final static QName _GetDomain_QNAME              = new QName("http://www.opengis.net/cat/csw", "GetDomain");
    private final static QName _DescribeRecordResponse_QNAME = new QName("http://www.opengis.net/cat/csw", "DescribeRecordResponse");
    private final static QName _GetRecordByIdResponse_QNAME  = new QName("http://www.opengis.net/cat/csw", "GetRecordByIdResponse");
    private final static QName _SummaryRecord_QNAME          = new QName("http://www.opengis.net/cat/csw", "SummaryRecord");
    private final static QName _Query_QNAME                  = new QName("http://www.opengis.net/cat/csw", "Query");
    private final static QName _Record_QNAME                 = new QName("http://www.opengis.net/cat/csw", "Record");
    private final static QName _BriefRecord_QNAME            = new QName("http://www.opengis.net/cat/csw", "BriefRecord");
    private final static QName _GetCapabilities_QNAME        = new QName("http://www.opengis.net/cat/csw", "GetCapabilities");
    private final static QName _DCMIRecord_QNAME             = new QName("http://www.opengis.net/cat/csw", "DCMIRecord");
    private final static QName _ElementSetName_QNAME         = new QName("http://www.opengis.net/cat/csw", "ElementSetName");
    private final static QName _Constraint_QNAME             = new QName("http://www.opengis.net/cat/csw", "Constraint");
    private final static QName _DescribeRecord_QNAME         = new QName("http://www.opengis.net/cat/csw", "DescribeRecord");
    private final static QName _GetRecords_QNAME             = new QName("http://www.opengis.net/cat/csw", "GetRecords");
    private final static QName _Acknowledgement_QNAME        = new QName("http://www.opengis.net/cat/csw", "Acknowledgement");
    private final static QName _AbstractQuery_QNAME          = new QName("http://www.opengis.net/cat/csw", "AbstractQuery");
    private final static QName _GetRecordsResponse_QNAME     = new QName("http://www.opengis.net/cat/csw", "GetRecordsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.constellation.cat.csw.v200
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetDomainResponseType }
     * 
     */
    public GetDomainResponseType createGetDomainResponseType() {
        return new GetDomainResponseType();
    }

    /**
     * Create an instance of {@link GetRecordsResponseType }
     * 
     */
    public GetRecordsResponseType createGetRecordsResponseType() {
        return new GetRecordsResponseType();
    }

    /**
     * Create an instance of {@link GetRecordByIdResponseType }
     * 
     */
    public GetRecordByIdResponseType createGetRecordByIdResponseType() {
        return new GetRecordByIdResponseType();
    }

    /**
     * Create an instance of {@link RequestStatusType }
     * 
     */
    public RequestStatusType createRequestStatusType() {
        return new RequestStatusType();
    }

    /**
     * Create an instance of {@link DescribeRecordType }
     * 
     */
    public DescribeRecordType createDescribeRecordType() {
        return new DescribeRecordType();
    }

    /**
     * Create an instance of {@link DomainValuesType }
     * 
     */
    public DomainValuesType createDomainValuesType() {
        return new DomainValuesType();
    }

    /**
     * Create an instance of {@link RangeOfValuesType }
     * 
     */
    public RangeOfValuesType createRangeOfValuesType() {
        return new RangeOfValuesType();
    }

    /**
     * Create an instance of {@link CapabilitiesType }
     * 
     */
    public CapabilitiesType createCapabilitiesType() {
        return new CapabilitiesType();
    }

    /**
     * Create an instance of {@link QueryConstraintType }
     * 
     */
    public QueryConstraintType createQueryConstraintType() {
        return new QueryConstraintType();
    }

    /**
     * Create an instance of {@link GetRecordByIdType }
     * 
     */
    public GetRecordByIdType createGetRecordByIdType() {
        return new GetRecordByIdType();
    }

    /**
     * Create an instance of {@link SearchResultsType }
     * 
     */
    public SearchResultsType createSearchResultsType() {
        return new SearchResultsType();
    }

    /**
     * Create an instance of {@link ConceptualSchemeType }
     * 
     */
    public ConceptualSchemeType createConceptualSchemeType() {
        return new ConceptualSchemeType();
    }

    /**
     * Create an instance of {@link EchoedRequestType }
     * 
     */
    public EchoedRequestType createEchoedRequestType() {
        return new EchoedRequestType();
    }

    /**
     * Create an instance of {@link SchemaComponentType }
     * 
     */
    public SchemaComponentType createSchemaComponentType() {
        return new SchemaComponentType();
    }

    /**
     * Create an instance of {@link QueryType }
     * 
     */
    public QueryType createQueryType() {
        return new QueryType();
    }

    /**
     * Create an instance of {@link ListOfValuesType }
     * 
     */
    public ListOfValuesType createListOfValuesType() {
        return new ListOfValuesType();
    }

    /**
     * Create an instance of {@link TypeNameType }
     * 
     */
    public TypeNameType createTypeNameType() {
        return new TypeNameType();
    }

    /**
     * Create an instance of {@link SummaryRecordType }
     * 
     */
    public SummaryRecordType createSummaryRecordType() {
        return new SummaryRecordType();
    }

    /**
     * Create an instance of {@link BriefRecordType }
     * 
     */
    public BriefRecordType createBriefRecordType() {
        return new BriefRecordType();
    }

    /**
     * Create an instance of {@link GetDomainType }
     * 
     */
    public GetDomainType createGetDomainType() {
        return new GetDomainType();
    }

    /**
     * Create an instance of {@link RecordType }
     * 
     */
    public RecordType createRecordType() {
        return new RecordType();
    }

    /**
     * Create an instance of {@link AcknowledgementType }
     * 
     */
    public AcknowledgementType createAcknowledgementType() {
        return new AcknowledgementType();
    }

    /**
     * Create an instance of {@link DCMIRecordType }
     * 
     */
    public DCMIRecordType createDCMIRecordType() {
        return new DCMIRecordType();
    }

    /**
     * Create an instance of {@link ElementSetNameType }
     * 
     */
    public ElementSetNameType createElementSetNameType() {
        return new ElementSetNameType();
    }

    /**
     * Create an instance of {@link GetRecordsType }
     * 
     */
    public GetRecordsType createGetRecordsType() {
        return new GetRecordsType();
    }

    /**
     * Create an instance of {@link GetCapabilitiesType }
     * 
     */
    public GetCapabilitiesType createGetCapabilitiesType() {
        return new GetCapabilitiesType();
    }

    /**
     * Create an instance of {@link DistributedSearchType }
     * 
     */
    public DistributedSearchType createDistributedSearchType() {
        return new DistributedSearchType();
    }

    /**
     * Create an instance of {@link DescribeRecordResponseType }
     * 
     */
    public DescribeRecordResponseType createDescribeRecordResponseType() {
        return new DescribeRecordResponseType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CapabilitiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "Capabilities")
    public JAXBElement<CapabilitiesType> createCapabilities(CapabilitiesType value) {
        return new JAXBElement<CapabilitiesType>(_Capabilities_QNAME, CapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordByIdType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "GetRecordById")
    public JAXBElement<GetRecordByIdType> createGetRecordById(GetRecordByIdType value) {
        return new JAXBElement<GetRecordByIdType>(_GetRecordById_QNAME, GetRecordByIdType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "AbstractRecord")
    public JAXBElement<AbstractRecordType> createAbstractRecord(AbstractRecordType value) {
        return new JAXBElement<AbstractRecordType>(_AbstractRecord_QNAME, AbstractRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDomainResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "GetDomainResponse")
    public JAXBElement<GetDomainResponseType> createGetDomainResponse(GetDomainResponseType value) {
        return new JAXBElement<GetDomainResponseType>(_GetDomainResponse_QNAME, GetDomainResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDomainType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "GetDomain")
    public JAXBElement<GetDomainType> createGetDomain(GetDomainType value) {
        return new JAXBElement<GetDomainType>(_GetDomain_QNAME, GetDomainType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeRecordResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "DescribeRecordResponse")
    public JAXBElement<DescribeRecordResponseType> createDescribeRecordResponse(DescribeRecordResponseType value) {
        return new JAXBElement<DescribeRecordResponseType>(_DescribeRecordResponse_QNAME, DescribeRecordResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordByIdResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "GetRecordByIdResponse")
    public JAXBElement<GetRecordByIdResponseType> createGetRecordByIdResponse(GetRecordByIdResponseType value) {
        return new JAXBElement<GetRecordByIdResponseType>(_GetRecordByIdResponse_QNAME, GetRecordByIdResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SummaryRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "SummaryRecord", substitutionHeadNamespace = "http://www.opengis.net/cat/csw", substitutionHeadName = "AbstractRecord")
    public JAXBElement<SummaryRecordType> createSummaryRecord(SummaryRecordType value) {
        return new JAXBElement<SummaryRecordType>(_SummaryRecord_QNAME, SummaryRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryType }{@code >}}
     * 
    
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "Query", substitutionHeadNamespace = "http://www.opengis.net/cat/csw", substitutionHeadName = "AbstractQuery")
    public JAXBElement<QueryType> createQuery(QueryType value) {
        return new JAXBElement<QueryType>(_Query_QNAME, QueryType.class, null, value);
    } */

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "Record", substitutionHeadNamespace = "http://www.opengis.net/cat/csw", substitutionHeadName = "AbstractRecord")
    public JAXBElement<RecordType> createRecord(RecordType value) {
        return new JAXBElement<RecordType>(_Record_QNAME, RecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BriefRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "BriefRecord", substitutionHeadNamespace = "http://www.opengis.net/cat/csw", substitutionHeadName = "AbstractRecord")
    public JAXBElement<BriefRecordType> createBriefRecord(BriefRecordType value) {
        return new JAXBElement<BriefRecordType>(_BriefRecord_QNAME, BriefRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "GetCapabilities")
    public JAXBElement<GetCapabilitiesType> createGetCapabilities(GetCapabilitiesType value) {
        return new JAXBElement<GetCapabilitiesType>(_GetCapabilities_QNAME, GetCapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DCMIRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "DCMIRecord", substitutionHeadNamespace = "http://www.opengis.net/cat/csw", substitutionHeadName = "AbstractRecord")
    public JAXBElement<DCMIRecordType> createDCMIRecord(DCMIRecordType value) {
        return new JAXBElement<DCMIRecordType>(_DCMIRecord_QNAME, DCMIRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementSetNameType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "ElementSetName", defaultValue = "summary")
    public JAXBElement<ElementSetNameType> createElementSetName(ElementSetNameType value) {
        return new JAXBElement<ElementSetNameType>(_ElementSetName_QNAME, ElementSetNameType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryConstraintType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "Constraint")
    public JAXBElement<QueryConstraintType> createConstraint(QueryConstraintType value) {
        return new JAXBElement<QueryConstraintType>(_Constraint_QNAME, QueryConstraintType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "DescribeRecord")
    public JAXBElement<DescribeRecordType> createDescribeRecord(DescribeRecordType value) {
        return new JAXBElement<DescribeRecordType>(_DescribeRecord_QNAME, DescribeRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "GetRecords")
    public JAXBElement<GetRecordsType> createGetRecords(GetRecordsType value) {
        return new JAXBElement<GetRecordsType>(_GetRecords_QNAME, GetRecordsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AcknowledgementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "Acknowledgement")
    public JAXBElement<AcknowledgementType> createAcknowledgement(AcknowledgementType value) {
        return new JAXBElement<AcknowledgementType>(_Acknowledgement_QNAME, AcknowledgementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractQueryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "AbstractQuery")
    public JAXBElement<AbstractQueryType> createAbstractQuery(AbstractQueryType value) {
        return new JAXBElement<AbstractQueryType>(_AbstractQuery_QNAME, AbstractQueryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw", name = "GetRecordsResponse")
    public JAXBElement<GetRecordsResponseType> createGetRecordsResponse(GetRecordsResponseType value) {
        return new JAXBElement<GetRecordsResponseType>(_GetRecordsResponse_QNAME, GetRecordsResponseType.class, null, value);
    }

}
