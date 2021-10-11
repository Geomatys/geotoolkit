/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
 * This object factory exclude CSW Record from the context.
 *
 * TODO see if its possible to make an inheritance between this and regular object factory.
 *
 * @module
 * @author Guilhem Legal (Geomatys)
 */
@XmlRegistry
public class LimitedObjectFactory {

    private static final QName _ElementSetName_QNAME         = new QName("http://www.opengis.net/cat/csw/2.0.2", "ElementSetName");
    private static final QName _RecordProperty_QNAME         = new QName("http://www.opengis.net/cat/csw/2.0.2", "RecordProperty");
    private static final QName _GetRecordByIdResponse_QNAME  = new QName("http://www.opengis.net/cat/csw/2.0.2", "GetRecordByIdResponse");
    private static final QName _GetDomainResponse_QNAME      = new QName("http://www.opengis.net/cat/csw/2.0.2", "GetDomainResponse");
    private static final QName _AbstractQuery_QNAME          = new QName("http://www.opengis.net/cat/csw/2.0.2", "AbstractQuery");
    private static final QName _DescribeRecordResponse_QNAME = new QName("http://www.opengis.net/cat/csw/2.0.2", "DescribeRecordResponse");
    private static final QName _Constraint_QNAME             = new QName("http://www.opengis.net/cat/csw/2.0.2", "Constraint");
    private static final QName _GetRecordById_QNAME          = new QName("http://www.opengis.net/cat/csw/2.0.2", "GetRecordById");
    private static final QName _GetRecordsResponse_QNAME     = new QName("http://www.opengis.net/cat/csw/2.0.2", "GetRecordsResponse");
    private static final QName _HarvestResponse_QNAME        = new QName("http://www.opengis.net/cat/csw/2.0.2", "HarvestResponse");
    private static final QName _Acknowledgement_QNAME        = new QName("http://www.opengis.net/cat/csw/2.0.2", "Acknowledgement");
    private static final QName _DescribeRecord_QNAME         = new QName("http://www.opengis.net/cat/csw/2.0.2", "DescribeRecord");
    private static final QName _TransactionResponse_QNAME    = new QName("http://www.opengis.net/cat/csw/2.0.2", "TransactionResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.constellation.cat.csw.v202
     *
     */
    public LimitedObjectFactory() {
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
    public JAXBElement<ElementSetNameType> createElementSetName(final ElementSetNameType value) {
        return new JAXBElement<>(_ElementSetName_QNAME, ElementSetNameType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecordPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "RecordProperty")
    public JAXBElement<RecordPropertyType> createRecordProperty(final RecordPropertyType value) {
        return new JAXBElement<>(_RecordProperty_QNAME, RecordPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordByIdResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "GetRecordByIdResponse")
    public JAXBElement<GetRecordByIdResponseType> createGetRecordByIdResponse(final GetRecordByIdResponseType value) {
        return new JAXBElement<>(_GetRecordByIdResponse_QNAME, GetRecordByIdResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDomainResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "GetDomainResponse")
    public JAXBElement<GetDomainResponseType> createGetDomainResponse(final GetDomainResponseType value) {
        return new JAXBElement<>(_GetDomainResponse_QNAME, GetDomainResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractQueryType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "AbstractQuery")
    public JAXBElement<AbstractQueryType> createAbstractQuery(final AbstractQueryType value) {
        return new JAXBElement<>(_AbstractQuery_QNAME, AbstractQueryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeRecordResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "DescribeRecordResponse")
    public JAXBElement<DescribeRecordResponseType> createDescribeRecordResponse(final DescribeRecordResponseType value) {
        return new JAXBElement<>(_DescribeRecordResponse_QNAME, DescribeRecordResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QueryConstraintType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "Constraint")
    public JAXBElement<QueryConstraintType> createConstraint(final QueryConstraintType value) {
        return new JAXBElement<>(_Constraint_QNAME, QueryConstraintType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordByIdType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "GetRecordById")
    public JAXBElement<GetRecordByIdType> createGetRecordById(final GetRecordByIdType value) {
        return new JAXBElement<>(_GetRecordById_QNAME, GetRecordByIdType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRecordsResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "GetRecordsResponse")
    public JAXBElement<GetRecordsResponseType> createGetRecordsResponse(final GetRecordsResponseType value) {
        return new JAXBElement<>(_GetRecordsResponse_QNAME, GetRecordsResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HarvestResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "HarvestResponse")
    public JAXBElement<HarvestResponseType> createHarvestResponse(final HarvestResponseType value) {
        return new JAXBElement<>(_HarvestResponse_QNAME, HarvestResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AcknowledgementType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "Acknowledgement")
    public JAXBElement<AcknowledgementType> createAcknowledgement(final AcknowledgementType value) {
        return new JAXBElement<>(_Acknowledgement_QNAME, AcknowledgementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeRecordType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "DescribeRecord")
    public JAXBElement<DescribeRecordType> createDescribeRecord(final DescribeRecordType value) {
        return new JAXBElement<>(_DescribeRecord_QNAME, DescribeRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/cat/csw/2.0.2", name = "TransactionResponse")
    public JAXBElement<TransactionResponseType> createTransactionResponse(final TransactionResponseType value) {
        return new JAXBElement<>(_TransactionResponse_QNAME, TransactionResponseType.class, null, value);
    }

}
