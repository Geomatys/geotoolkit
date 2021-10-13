/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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

package org.geotoolkit.csw.xml.v300;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.cat.csw._3 package.
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

    private final static QName _UnHarvestResponse_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "UnHarvestResponse");
    private final static QName _TransactionResponse_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "TransactionResponse");
    private final static QName _Query_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "Query");
    private final static QName _HarvestResponse_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "HarvestResponse");
    private final static QName _UnHarvest_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "UnHarvest");
    private final static QName _FederatedSearchResultBase_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "FederatedSearchResultBase");
    private final static QName _SummaryRecord_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "SummaryRecord");
    private final static QName _Harvest_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "Harvest");
    private final static QName _Source_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "Source");
    private final static QName _Acknowledgement_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "Acknowledgement");
    private final static QName _GetCapabilities_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "GetCapabilities");
    private final static QName _RecordProperty_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "RecordProperty");
    private final static QName _BriefRecord_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "BriefRecord");
    private final static QName _Transaction_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "Transaction");
    private final static QName _DCMIRecord_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "DCMIRecord");
    private final static QName _FederatedException_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "FederatedException");
    private final static QName _FederatedSearchResult_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "FederatedSearchResult");
    private final static QName _Record_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "Record");
    private final static QName _GetRecords_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "GetRecords");
    private final static QName _GetRecordsResponse_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "GetRecordsResponse");
    private final static QName _AbstractRecord_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "AbstractRecord");
    private final static QName _ElementSetName_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "ElementSetName");
    private final static QName _GetRecordById_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "GetRecordById");
    private final static QName _AbstractQuery_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "AbstractQuery");
    private final static QName _Constraint_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "Constraint");
    private final static QName _GetDomain_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "GetDomain");
    private final static QName _Capabilities_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "Capabilities");
    private final static QName _GetDomainResponse_QNAME = new QName("http://www.opengis.net/cat/csw/3.0", "GetDomainResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.cat.csw._3
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ListOfValuesType }
     *
     */
    public ListOfValuesType createListOfValuesType() {
        return new ListOfValuesType();
    }

    /**
     * Create an instance of {@link TemporalExtentType }
     *
     */
    public TemporalExtentType createTemporalExtentType() {
        return new TemporalExtentType();
    }

    /**
     * Create an instance of {@link GetDomainType }
     *
     */
    public GetDomainType createGetDomainType() {
        return new GetDomainType();
    }

    /**
     * Create an instance of {@link FederatedExceptionType }
     *
     */
    public FederatedExceptionType createFederatedExceptionType() {
        return new FederatedExceptionType();
    }

    /**
     * Create an instance of {@link QueryType }
     *
     */
    public QueryType createQueryType() {
        return new QueryType();
    }

    /**
     * Create an instance of {@link QueryConstraintType }
     *
     */
    public QueryConstraintType createQueryConstraintType() {
        return new QueryConstraintType();
    }

    /**
     * Create an instance of {@link GetDomainResponseType }
     *
     */
    public GetDomainResponseType createGetDomainResponseType() {
        return new GetDomainResponseType();
    }

    /**
     * Create an instance of {@link GetRecordsType }
     *
     */
    public GetRecordsType createGetRecordsType() {
        return new GetRecordsType();
    }

    /**
     * Create an instance of {@link UnHarvestResponseType }
     *
     */
    public UnHarvestResponseType createUnHarvestResponseType() {
        return new UnHarvestResponseType();
    }

    /**
     * Create an instance of {@link HarvestType }
     *
     */
    public HarvestType createHarvestType() {
        return new HarvestType();
    }

    /**
     * Create an instance of {@link SourceType }
     *
     */
    public SourceType createSourceType() {
        return new SourceType();
    }

    /**
     * Create an instance of {@link UnHarvestType }
     *
     */
    public UnHarvestType createUnHarvestType() {
        return new UnHarvestType();
    }

    /**
     * Create an instance of {@link AcknowledgementType }
     *
     */
    public AcknowledgementType createAcknowledgementType() {
        return new AcknowledgementType();
    }

    /**
     * Create an instance of {@link TransactionResponseType }
     *
     */
    public TransactionResponseType createTransactionResponseType() {
        return new TransactionResponseType();
    }

    /**
     * Create an instance of {@link FederatedSearchResultType }
     *
     */
    public FederatedSearchResultType createFederatedSearchResultType() {
        return new FederatedSearchResultType();
    }

    /**
     * Create an instance of {@link GetCapabilitiesType }
     *
     */
    public GetCapabilitiesType createGetCapabilitiesType() {
        return new GetCapabilitiesType();
    }

    /**
     * Create an instance of {@link RecordPropertyType }
     *
     */
    public RecordPropertyType createRecordPropertyType() {
        return new RecordPropertyType();
    }

    /**
     * Create an instance of {@link RecordType }
     *
     */
    public RecordType createRecordType() {
        return new RecordType();
    }

    /**
     * Create an instance of {@link ElementSetNameType }
     *
     */
    public ElementSetNameType createElementSetNameType() {
        return new ElementSetNameType();
    }

    /**
     * Create an instance of {@link GetRecordsResponseType }
     *
     */
    public GetRecordsResponseType createGetRecordsResponseType() {
        return new GetRecordsResponseType();
    }

    /**
     * Create an instance of {@link HarvestResponseType }
     *
     */
    public HarvestResponseType createHarvestResponseType() {
        return new HarvestResponseType();
    }

    /**
     * Create an instance of {@link BriefRecordType }
     *
     */
    public BriefRecordType createBriefRecordType() {
        return new BriefRecordType();
    }

    /**
     * Create an instance of {@link TransactionType }
     *
     */
    public TransactionType createTransactionType() {
        return new TransactionType();
    }

    /**
     * Create an instance of {@link SummaryRecordType }
     *
     */
    public SummaryRecordType createSummaryRecordType() {
        return new SummaryRecordType();
    }

    /**
     * Create an instance of {@link CapabilitiesType }
     *
     */
    public CapabilitiesType createCapabilitiesType() {
        return new CapabilitiesType();
    }

    /**
     * Create an instance of {@link DCMIRecordType }
     *
     */
    public DCMIRecordType createDCMIRecordType() {
        return new DCMIRecordType();
    }

    /**
     * Create an instance of {@link GetRecordByIdType }
     *
     */
    public GetRecordByIdType createGetRecordByIdType() {
        return new GetRecordByIdType();
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
     * Create an instance of {@link InsertResultType }
     *
     */
    public InsertResultType createInsertResultType() {
        return new InsertResultType();
    }

    /**
     * Create an instance of {@link SearchResultsType }
     *
     */
    public SearchResultsType createSearchResultsType() {
        return new SearchResultsType();
    }

    /**
     * Create an instance of {@link EchoedRequestType }
     *
     */
    public EchoedRequestType createEchoedRequestType() {
        return new EchoedRequestType();
    }

    /**
     * Create an instance of {@link FederatedCatalogueType }
     *
     */
    public FederatedCatalogueType createFederatedCatalogueType() {
        return new FederatedCatalogueType();
    }

    /**
     * Create an instance of {@link DomainValuesType }
     *
     */
    public DomainValuesType createDomainValuesType() {
        return new DomainValuesType();
    }

    /**
     * Create an instance of {@link EmptyType }
     *
     */
    public EmptyType createEmptyType() {
        return new EmptyType();
    }

    /**
     * Create an instance of {@link TransactionSummaryType }
     *
     */
    public TransactionSummaryType createTransactionSummaryType() {
        return new TransactionSummaryType();
    }

    /**
     * Create an instance of {@link DistributedSearchType }
     *
     */
    public DistributedSearchType createDistributedSearchType() {
        return new DistributedSearchType();
    }

    /**
     * Create an instance of {@link DeleteType }
     *
     */
    public DeleteType createDeleteType() {
        return new DeleteType();
    }

    /**
     * Create an instance of {@link InsertType }
     *
     */
    public InsertType createInsertType() {
        return new InsertType();
    }

    /**
     * Create an instance of {@link UpdateType }
     *
     */
    public UpdateType createUpdateType() {
        return new UpdateType();
    }

    /**
     * Create an instance of {@link RequestStatusType }
     *
     */
    public RequestStatusType createRequestStatusType() {
        return new RequestStatusType();
    }

    /**
     * Create an instance of {@link ListOfValuesType.Value }
     *
     */
    public ListOfValuesType.Value createListOfValuesTypeValue() {
        return new ListOfValuesType.Value();
    }

    /**
     * Create an instance of {@link TemporalExtentType.Begin }
     *
     */
    public TemporalExtentType.Begin createTemporalExtentTypeBegin() {
        return new TemporalExtentType.Begin();
    }

    /**
     * Create an instance of {@link TemporalExtentType.End }
     *
     */
    public TemporalExtentType.End createTemporalExtentTypeEnd() {
        return new TemporalExtentType.End();
    }

    /**
     * Create an instance of {@link GetDomainType.ValueReference }
     *
     */
    public GetDomainType.ValueReference createGetDomainTypeValueReference() {
        return new GetDomainType.ValueReference();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnHarvestResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "UnHarvestResponse")
    public JAXBElement<UnHarvestResponseType> createUnHarvestResponse(UnHarvestResponseType value) {
        return new JAXBElement<UnHarvestResponseType>(_UnHarvestResponse_QNAME, UnHarvestResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "TransactionResponse")
    public JAXBElement<TransactionResponseType> createTransactionResponse(TransactionResponseType value) {
        return new JAXBElement<TransactionResponseType>(_TransactionResponse_QNAME, TransactionResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "Query", substitutionHeadNamespace = "http://www.opengis.net/cat/csw/3.0", substitutionHeadName = "AbstractQuery")
    public JAXBElement<QueryType> createQuery(QueryType value) {
        return new JAXBElement<QueryType>(_Query_QNAME, QueryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HarvestResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "HarvestResponse")
    public JAXBElement<HarvestResponseType> createHarvestResponse(HarvestResponseType value) {
        return new JAXBElement<HarvestResponseType>(_HarvestResponse_QNAME, HarvestResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnHarvestType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "UnHarvest")
    public JAXBElement<UnHarvestType> createUnHarvest(UnHarvestType value) {
        return new JAXBElement<UnHarvestType>(_UnHarvest_QNAME, UnHarvestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FederatedSearchResultBaseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "FederatedSearchResultBase")
    public JAXBElement<FederatedSearchResultBaseType> createFederatedSearchResultBase(FederatedSearchResultBaseType value) {
        return new JAXBElement<FederatedSearchResultBaseType>(_FederatedSearchResultBase_QNAME, FederatedSearchResultBaseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SummaryRecordType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "SummaryRecord", substitutionHeadNamespace = "http://www.opengis.net/cat/csw/3.0", substitutionHeadName = "AbstractRecord")
    public JAXBElement<SummaryRecordType> createSummaryRecord(SummaryRecordType value) {
        return new JAXBElement<SummaryRecordType>(_SummaryRecord_QNAME, SummaryRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HarvestType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "Harvest")
    public JAXBElement<HarvestType> createHarvest(HarvestType value) {
        return new JAXBElement<HarvestType>(_Harvest_QNAME, HarvestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SourceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "Source")
    public JAXBElement<SourceType> createSource(SourceType value) {
        return new JAXBElement<SourceType>(_Source_QNAME, SourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AcknowledgementType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "Acknowledgement")
    public JAXBElement<AcknowledgementType> createAcknowledgement(AcknowledgementType value) {
        return new JAXBElement<AcknowledgementType>(_Acknowledgement_QNAME, AcknowledgementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "GetCapabilities")
    public JAXBElement<GetCapabilitiesType> createGetCapabilities(GetCapabilitiesType value) {
        return new JAXBElement<GetCapabilitiesType>(_GetCapabilities_QNAME, GetCapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecordPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "RecordProperty")
    public JAXBElement<RecordPropertyType> createRecordProperty(RecordPropertyType value) {
        return new JAXBElement<RecordPropertyType>(_RecordProperty_QNAME, RecordPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BriefRecordType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "BriefRecord", substitutionHeadNamespace = "http://www.opengis.net/cat/csw/3.0", substitutionHeadName = "AbstractRecord")
    public JAXBElement<BriefRecordType> createBriefRecord(BriefRecordType value) {
        return new JAXBElement<BriefRecordType>(_BriefRecord_QNAME, BriefRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "Transaction")
    public JAXBElement<TransactionType> createTransaction(TransactionType value) {
        return new JAXBElement<TransactionType>(_Transaction_QNAME, TransactionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DCMIRecordType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "DCMIRecord", substitutionHeadNamespace = "http://www.opengis.net/cat/csw/3.0", substitutionHeadName = "AbstractRecord")
    public JAXBElement<DCMIRecordType> createDCMIRecord(DCMIRecordType value) {
        return new JAXBElement<DCMIRecordType>(_DCMIRecord_QNAME, DCMIRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FederatedExceptionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "FederatedException", substitutionHeadNamespace = "http://www.opengis.net/cat/csw/3.0", substitutionHeadName = "FederatedSearchResultBase")
    public JAXBElement<FederatedExceptionType> createFederatedException(FederatedExceptionType value) {
        return new JAXBElement<FederatedExceptionType>(_FederatedException_QNAME, FederatedExceptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FederatedSearchResultType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "FederatedSearchResult", substitutionHeadNamespace = "http://www.opengis.net/cat/csw/3.0", substitutionHeadName = "FederatedSearchResultBase")
    public JAXBElement<FederatedSearchResultType> createFederatedSearchResult(FederatedSearchResultType value) {
        return new JAXBElement<FederatedSearchResultType>(_FederatedSearchResult_QNAME, FederatedSearchResultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecordType }{@code >}}
     *
     *
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "Record", substitutionHeadNamespace = "http://www.opengis.net/cat/csw/3.0", substitutionHeadName = "AbstractRecord")
    public JAXBElement<RecordType> createRecord(RecordType value) {
        return new JAXBElement<RecordType>(_Record_QNAME, RecordType.class, null, value);
    }*/

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "GetRecords")
    public JAXBElement<GetRecordsType> createGetRecords(GetRecordsType value) {
        return new JAXBElement<GetRecordsType>(_GetRecords_QNAME, GetRecordsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "GetRecordsResponse")
    public JAXBElement<GetRecordsResponseType> createGetRecordsResponse(GetRecordsResponseType value) {
        return new JAXBElement<GetRecordsResponseType>(_GetRecordsResponse_QNAME, GetRecordsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractRecordType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "AbstractRecord")
    public JAXBElement<AbstractRecordType> createAbstractRecord(AbstractRecordType value) {
        return new JAXBElement<AbstractRecordType>(_AbstractRecord_QNAME, AbstractRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementSetNameType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "ElementSetName")
    public JAXBElement<ElementSetNameType> createElementSetName(ElementSetNameType value) {
        return new JAXBElement<ElementSetNameType>(_ElementSetName_QNAME, ElementSetNameType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordByIdType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "GetRecordById")
    public JAXBElement<GetRecordByIdType> createGetRecordById(GetRecordByIdType value) {
        return new JAXBElement<GetRecordByIdType>(_GetRecordById_QNAME, GetRecordByIdType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractQueryType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "AbstractQuery")
    public JAXBElement<AbstractQueryType> createAbstractQuery(AbstractQueryType value) {
        return new JAXBElement<AbstractQueryType>(_AbstractQuery_QNAME, AbstractQueryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryConstraintType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "Constraint")
    public JAXBElement<QueryConstraintType> createConstraint(QueryConstraintType value) {
        return new JAXBElement<QueryConstraintType>(_Constraint_QNAME, QueryConstraintType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDomainType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "GetDomain")
    public JAXBElement<GetDomainType> createGetDomain(GetDomainType value) {
        return new JAXBElement<GetDomainType>(_GetDomain_QNAME, GetDomainType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CapabilitiesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "Capabilities")
    public JAXBElement<CapabilitiesType> createCapabilities(CapabilitiesType value) {
        return new JAXBElement<CapabilitiesType>(_Capabilities_QNAME, CapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDomainResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/3.0", name = "GetDomainResponse")
    public JAXBElement<GetDomainResponseType> createGetDomainResponse(GetDomainResponseType value) {
        return new JAXBElement<GetDomainResponseType>(_GetDomainResponse_QNAME, GetDomainResponseType.class, null, value);
    }

}
