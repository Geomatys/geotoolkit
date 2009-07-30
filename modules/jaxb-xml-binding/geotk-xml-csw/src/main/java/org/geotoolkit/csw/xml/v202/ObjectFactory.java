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
package org.geotoolkit.csw.xml.v202;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each Java content interface and Java element interface 
 * generated in the org.constellation.cat.csw.v202 package. 
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content. 
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding of schema 
 * type definitions, element declarations and model groups.  
 * Factory methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _ElementSetName_QNAME         = new QName("http://www.opengis.net/cat/csw/2.0.2", "ElementSetName");
    private static final QName _Harvest_QNAME                = new QName("http://www.opengis.net/cat/csw/2.0.2", "Harvest");
    private static final QName _GetDomain_QNAME              = new QName("http://www.opengis.net/cat/csw/2.0.2", "GetDomain");
    private static final QName _Query_QNAME                  = new QName("http://www.opengis.net/cat/csw/2.0.2", "Query");
    private static final QName _Record_QNAME                 = new QName("http://www.opengis.net/cat/csw/2.0.2", "Record");
    private static final QName _RecordProperty_QNAME         = new QName("http://www.opengis.net/cat/csw/2.0.2", "RecordProperty");
    private static final QName _GetRecordByIdResponse_QNAME  = new QName("http://www.opengis.net/cat/csw/2.0.2", "GetRecordByIdResponse");
    private static final QName _GetDomainResponse_QNAME      = new QName("http://www.opengis.net/cat/csw/2.0.2", "GetDomainResponse");
    private static final QName _BriefRecord_QNAME            = new QName("http://www.opengis.net/cat/csw/2.0.2", "BriefRecord");
    private static final QName _AbstractQuery_QNAME          = new QName("http://www.opengis.net/cat/csw/2.0.2", "AbstractQuery");
    private static final QName _DescribeRecordResponse_QNAME = new QName("http://www.opengis.net/cat/csw/2.0.2", "DescribeRecordResponse");
    private static final QName _GetCapabilities_QNAME        = new QName("http://www.opengis.net/cat/csw/2.0.2", "GetCapabilities");
    private static final QName _Transaction_QNAME            = new QName("http://www.opengis.net/cat/csw/2.0.2", "Transaction");
    private static final QName _Constraint_QNAME             = new QName("http://www.opengis.net/cat/csw/2.0.2", "Constraint");
    private static final QName _SummaryRecord_QNAME          = new QName("http://www.opengis.net/cat/csw/2.0.2", "SummaryRecord");
    private static final QName _DCMIRecord_QNAME             = new QName("http://www.opengis.net/cat/csw/2.0.2", "DCMIRecord");
    private static final QName _GetRecordById_QNAME          = new QName("http://www.opengis.net/cat/csw/2.0.2", "GetRecordById");
    private static final QName _GetRecords_QNAME             = new QName("http://www.opengis.net/cat/csw/2.0.2", "GetRecords");
    private static final QName _Capabilities_QNAME           = new QName("http://www.opengis.net/cat/csw/2.0.2", "Capabilities");
    private static final QName _GetRecordsResponse_QNAME     = new QName("http://www.opengis.net/cat/csw/2.0.2", "GetRecordsResponse");
    private static final QName _HarvestResponse_QNAME        = new QName("http://www.opengis.net/cat/csw/2.0.2", "HarvestResponse");
    private static final QName _AbstractRecord_QNAME         = new QName("http://www.opengis.net/cat/csw/2.0.2", "AbstractRecord");
    private static final QName _Acknowledgement_QNAME        = new QName("http://www.opengis.net/cat/csw/2.0.2", "Acknowledgement");
    private static final QName _DescribeRecord_QNAME         = new QName("http://www.opengis.net/cat/csw/2.0.2", "DescribeRecord");
    private static final QName _TransactionResponse_QNAME    = new QName("http://www.opengis.net/cat/csw/2.0.2", "TransactionResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.constellation.cat.csw.v202
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RangeOfValuesType }
     * 
     */
    public RangeOfValuesType createRangeOfValuesType() {
        return new RangeOfValuesType();
    }

    /**
     * Create an instance of {@link ConceptualSchemeType }
     * 
     */
    public ConceptualSchemeType createConceptualSchemeType() {
        return new ConceptualSchemeType();
    }

    /**
     * Create an instance of {@link GetRecordByIdResponseType }
     * 
     */
    public GetRecordByIdResponseType createGetRecordByIdResponseType() {
        return new GetRecordByIdResponseType();
    }

    /**
     * Create an instance of {@link TransactionType }
     * 
     */
    public TransactionType createTransactionType() {
        return new TransactionType();
    }

    /**
     * Create an instance of {@link EmptyType }
     * 
     */
    public EmptyType createEmptyType() {
        return new EmptyType();
    }

    /**
     * Create an instance of {@link InsertResultType }
     * 
     */
    public InsertResultType createInsertResultType() {
        return new InsertResultType();
    }

    /**
     * Create an instance of {@link QueryType }
     * 
     */
    public QueryType createQueryType() {
        return new QueryType();
    }

    /**
     * Create an instance of {@link InsertType }
     * 
     */
    public InsertType createInsertType() {
        return new InsertType();
    }

    /**
     * Create an instance of {@link DescribeRecordType }
     * 
     */
    public DescribeRecordType createDescribeRecordType() {
        return new DescribeRecordType();
    }

    /**
     * Create an instance of {@link DeleteType }
     * 
     */
    public DeleteType createDeleteType() {
        return new DeleteType();
    }

    /**
     * Create an instance of {@link SummaryRecordType }
     * 
     */
    public SummaryRecordType createSummaryRecordType() {
        return new SummaryRecordType();
    }

    /**
     * Create an instance of {@link DCMIRecordType }
     * 
     */
    public DCMIRecordType createDCMIRecordType() {
        return new DCMIRecordType();
    }

    /**
     * Create an instance of {@link RecordPropertyType }
     * 
     */
    public RecordPropertyType createRecordPropertyType() {
        return new RecordPropertyType();
    }

    /**
     * Create an instance of {@link HarvestType }
     * 
     */
    public HarvestType createHarvestType() {
        return new HarvestType();
    }

    /**
     * Create an instance of {@link ElementSetNameType }
     * 
     */
    public ElementSetNameType createElementSetNameType() {
        return new ElementSetNameType();
    }

    /**
     * Create an instance of {@link DescribeRecordResponseType }
     * 
     */
    public DescribeRecordResponseType createDescribeRecordResponseType() {
        return new DescribeRecordResponseType();
    }

    /**
     * Create an instance of {@link AcknowledgementType }
     * 
     */
    public AcknowledgementType createAcknowledgementType() {
        return new AcknowledgementType();
    }

    /**
     * Create an instance of {@link HarvestResponseType }
     * 
     */
    public HarvestResponseType createHarvestResponseType() {
        return new HarvestResponseType();
    }

    /**
     * Create an instance of {@link DistributedSearchType }
     * 
     */
    public DistributedSearchType createDistributedSearchType() {
        return new DistributedSearchType();
    }

    /**
     * Create an instance of {@link CapabilitiesType }
     * 
     */
    public Capabilities createCapabilities() {
        return new Capabilities();
    }

    /**
     * Create an instance of {@link QueryConstraintType }
     * 
     */
    public QueryConstraintType createQueryConstraintType() {
        return new QueryConstraintType();
    }
    
    /**
     * Create an instance of {@link GetDomainType }
     * 
     */
    public GetDomainType createGetDomainType() {
        return new GetDomainType();
    }

    /**
     * Create an instance of {@link RequestStatusType }
     * 
     */
    public RequestStatusType createRequestStatusType() {
        return new RequestStatusType();
    }

    /**
     * Create an instance of {@link TransactionResponseType }
     * 
     */
    public TransactionResponseType createTransactionResponseType() {
        return new TransactionResponseType();
    }

    /**
     * Create an instance of {@link SchemaComponentType }
     * 
     */
    public SchemaComponentType createSchemaComponentType() {
        return new SchemaComponentType();
    }

    /**
     * Create an instance of {@link SearchResultsType }
     * 
     */
    public SearchResultsType createSearchResultsType() {
        return new SearchResultsType();
    }

    /**
     * Create an instance of {@link GetRecordsType }
     * 
     */
    public GetRecordsType createGetRecordsType() {
        return new GetRecordsType();
    }

    /**
     * Create an instance of {@link DomainValuesType }
     * 
     */
    public DomainValuesType createDomainValuesType() {
        return new DomainValuesType();
    }

    /**
     * Create an instance of {@link BriefRecordType }
     * 
     */
    public BriefRecordType createBriefRecordType() {
        return new BriefRecordType();
    }

    /**
     * Create an instance of {@link TransactionSummaryType }
     * 
     */
    public TransactionSummaryType createTransactionSummaryType() {
        return new TransactionSummaryType();
    }

    /**
     * Create an instance of {@link GetRecordByIdType }
     * 
     */
    public GetRecordByIdType createGetRecordByIdType() {
        return new GetRecordByIdType();
    }

    /**
     * Create an instance of {@link GetDomainResponseType }
     * 
     */
    public GetDomainResponseType createGetDomainResponseType() {
        return new GetDomainResponseType();
    }

    /**
     * Create an instance of {@link RecordType }
     * 
     */
    public RecordType createRecordType() {
        return new RecordType();
    }

    /**
     * Create an instance of {@link ListOfValuesType }
     * 
     */
    public ListOfValuesType createListOfValuesType() {
        return new ListOfValuesType();
    }

    /**
     * Create an instance of {@link EchoedRequestType }
     * 
     */
    public EchoedRequestType createEchoedRequestType() {
        return new EchoedRequestType();
    }

    /**
     * Create an instance of {@link UpdateType }
     * 
     */
    public UpdateType createUpdateType() {
        return new UpdateType();
    }

    /**
     * Create an instance of {@link GetRecordsResponseType }
     * 
     */
    public GetRecordsResponseType createGetRecordsResponseType() {
        return new GetRecordsResponseType();
    }

    /**
     * Create an instance of {@link GetCapabilitiesType }
     * 
     */
    public GetCapabilitiesType createGetCapabilitiesType() {
        return new GetCapabilitiesType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementSetNameType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "ElementSetName", defaultValue = "summary")
    public JAXBElement<ElementSetNameType> createElementSetName(ElementSetNameType value) {
        return new JAXBElement<ElementSetNameType>(_ElementSetName_QNAME, ElementSetNameType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HarvestType }{@code >}}
     * 
     
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "Harvest")
    public JAXBElement<HarvestType> createHarvest(HarvestType value) {
        return new JAXBElement<HarvestType>(_Harvest_QNAME, HarvestType.class, null, value);
    }
     
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDomainType }{@code >}}
     * 
     
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "GetDomain")
    public JAXBElement<GetDomainType> createGetDomain(GetDomainType value) {
        return new JAXBElement<GetDomainType>(_GetDomain_QNAME, GetDomainType.class, null, value);
    }*/

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryType }{@code >}}
     * 
     
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "Query", substitutionHeadNamespace = "http://www.opengis.net/cat/csw/2.0.2", substitutionHeadName = "AbstractQuery")
    public JAXBElement<QueryType> createQuery(QueryType value) {
        return new JAXBElement<QueryType>(_Query_QNAME, QueryType.class, null, value);
    }*/

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "Record", substitutionHeadNamespace = "http://www.opengis.net/cat/csw/2.0.2", substitutionHeadName = "AbstractRecord")
    public JAXBElement<RecordType> createRecord(RecordType value) {
        return new JAXBElement<RecordType>(_Record_QNAME, RecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecordPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "RecordProperty")
    public JAXBElement<RecordPropertyType> createRecordProperty(RecordPropertyType value) {
        return new JAXBElement<RecordPropertyType>(_RecordProperty_QNAME, RecordPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordByIdResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "GetRecordByIdResponse")
    public JAXBElement<GetRecordByIdResponseType> createGetRecordByIdResponse(GetRecordByIdResponseType value) {
        return new JAXBElement<GetRecordByIdResponseType>(_GetRecordByIdResponse_QNAME, GetRecordByIdResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDomainResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "GetDomainResponse")
    public JAXBElement<GetDomainResponseType> createGetDomainResponse(GetDomainResponseType value) {
        return new JAXBElement<GetDomainResponseType>(_GetDomainResponse_QNAME, GetDomainResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BriefRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "BriefRecord", substitutionHeadNamespace = "http://www.opengis.net/cat/csw/2.0.2", substitutionHeadName = "AbstractRecord")
    public JAXBElement<BriefRecordType> createBriefRecord(BriefRecordType value) {
        return new JAXBElement<BriefRecordType>(_BriefRecord_QNAME, BriefRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractQueryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "AbstractQuery")
    public JAXBElement<AbstractQueryType> createAbstractQuery(AbstractQueryType value) {
        return new JAXBElement<AbstractQueryType>(_AbstractQuery_QNAME, AbstractQueryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeRecordResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "DescribeRecordResponse")
    public JAXBElement<DescribeRecordResponseType> createDescribeRecordResponse(DescribeRecordResponseType value) {
        return new JAXBElement<DescribeRecordResponseType>(_DescribeRecordResponse_QNAME, DescribeRecordResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}}
     * 
    
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "GetCapabilities")
    public JAXBElement<GetCapabilities> createGetCapabilities(GetCapabilities value) {
        return new JAXBElement<GetCapabilities>(_GetCapabilities_QNAME, GetCapabilities.class, null, value);
    } */

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionType }{@code >}}
     * 
     
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "Transaction")
    public JAXBElement<TransactionType> createTransaction(TransactionType value) {
        return new JAXBElement<TransactionType>(_Transaction_QNAME, TransactionType.class, null, value);
    }*/

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryConstraintType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "Constraint")
    public JAXBElement<QueryConstraintType> createConstraint(QueryConstraintType value) {
        return new JAXBElement<QueryConstraintType>(_Constraint_QNAME, QueryConstraintType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SummaryRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "SummaryRecord", substitutionHeadNamespace = "http://www.opengis.net/cat/csw/2.0.2", substitutionHeadName = "AbstractRecord")
    public JAXBElement<SummaryRecordType> createSummaryRecord(SummaryRecordType value) {
        return new JAXBElement<SummaryRecordType>(_SummaryRecord_QNAME, SummaryRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DCMIRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "DCMIRecord", substitutionHeadNamespace = "http://www.opengis.net/cat/csw/2.0.2", substitutionHeadName = "AbstractRecord")
    public JAXBElement<DCMIRecordType> createDCMIRecord(DCMIRecordType value) {
        return new JAXBElement<DCMIRecordType>(_DCMIRecord_QNAME, DCMIRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordByIdType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "GetRecordById")
    public JAXBElement<GetRecordByIdType> createGetRecordById(GetRecordByIdType value) {
        return new JAXBElement<GetRecordByIdType>(_GetRecordById_QNAME, GetRecordByIdType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsType }{@code >}}
     * 
     
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "GetRecords")
    public JAXBElement<GetRecordsType> createGetRecords(GetRecordsType value) {
        return new JAXBElement<GetRecordsType>(_GetRecords_QNAME, GetRecordsType.class, null, value);
    }*/

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CapabilitiesType }{@code >}}
     * 
     
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "Capabilities")
    public JAXBElement<Capabilities> createCapabilities(Capabilities value) {
        return new JAXBElement<Capabilities>(_Capabilities_QNAME, Capabilities.class, null, value);
    }*/

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "GetRecordsResponse")
    public JAXBElement<GetRecordsResponseType> createGetRecordsResponse(GetRecordsResponseType value) {
        return new JAXBElement<GetRecordsResponseType>(_GetRecordsResponse_QNAME, GetRecordsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HarvestResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "HarvestResponse")
    public JAXBElement<HarvestResponseType> createHarvestResponse(HarvestResponseType value) {
        return new JAXBElement<HarvestResponseType>(_HarvestResponse_QNAME, HarvestResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "AbstractRecord")
    public JAXBElement<AbstractRecordType> createAbstractRecord(AbstractRecordType value) {
        return new JAXBElement<AbstractRecordType>(_AbstractRecord_QNAME, AbstractRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AcknowledgementType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "Acknowledgement")
    public JAXBElement<AcknowledgementType> createAcknowledgement(AcknowledgementType value) {
        return new JAXBElement<AcknowledgementType>(_Acknowledgement_QNAME, AcknowledgementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "DescribeRecord")
    public JAXBElement<DescribeRecordType> createDescribeRecord(DescribeRecordType value) {
        return new JAXBElement<DescribeRecordType>(_DescribeRecord_QNAME, DescribeRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "TransactionResponse")
    public JAXBElement<TransactionResponseType> createTransactionResponse(TransactionResponseType value) {
        return new JAXBElement<TransactionResponseType>(_TransactionResponse_QNAME, TransactionResponseType.class, null, value);
    }

}
