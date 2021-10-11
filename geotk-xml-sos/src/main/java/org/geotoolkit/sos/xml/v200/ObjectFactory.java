/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.sos.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.sos._2 package.
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

    private final static QName _InsertObservationResponse_QNAME = new QName("http://www.opengis.net/sos/2.0", "InsertObservationResponse");
    private final static QName _GetFeatureOfInterestResponse_QNAME = new QName("http://www.opengis.net/sos/2.0", "GetFeatureOfInterestResponse");
    private final static QName _InsertResultTemplate_QNAME = new QName("http://www.opengis.net/sos/2.0", "InsertResultTemplate");
    private final static QName _InsertObservation_QNAME = new QName("http://www.opengis.net/sos/2.0", "InsertObservation");
    private final static QName _SosInsertionMetadata_QNAME = new QName("http://www.opengis.net/sos/2.0", "SosInsertionMetadata");
    private final static QName _InsertionCapabilities_QNAME = new QName("http://www.opengis.net/sos/2.0", "InsertionCapabilities");
    private final static QName _InsertResultTemplateResponse_QNAME = new QName("http://www.opengis.net/sos/2.0", "InsertResultTemplateResponse");
    private final static QName _GetObservationByIdResponse_QNAME = new QName("http://www.opengis.net/sos/2.0", "GetObservationByIdResponse");
    private final static QName _GetObservation_QNAME = new QName("http://www.opengis.net/sos/2.0", "GetObservation");
    private final static QName _GetCapabilities_QNAME = new QName("http://www.opengis.net/sos/2.0", "GetCapabilities");
    private final static QName _Contents_QNAME = new QName("http://www.opengis.net/sos/2.0", "Contents");
    private final static QName _GetObservationResponse_QNAME = new QName("http://www.opengis.net/sos/2.0", "GetObservationResponse");
    private final static QName _GetFeatureOfInterest_QNAME = new QName("http://www.opengis.net/sos/2.0", "GetFeatureOfInterest");
    private final static QName _ResultTemplate_QNAME = new QName("http://www.opengis.net/sos/2.0", "ResultTemplate");
    private final static QName _ObservationOffering_QNAME = new QName("http://www.opengis.net/sos/2.0", "ObservationOffering");
    private final static QName _GetResultTemplateResponse_QNAME = new QName("http://www.opengis.net/sos/2.0", "GetResultTemplateResponse");
    private final static QName _GetResultTemplate_QNAME = new QName("http://www.opengis.net/sos/2.0", "GetResultTemplate");
    private final static QName _GetResultResponse_QNAME = new QName("http://www.opengis.net/sos/2.0", "GetResultResponse");
    private final static QName _GetObservationById_QNAME = new QName("http://www.opengis.net/sos/2.0", "GetObservationById");
    private final static QName _InsertResultResponse_QNAME = new QName("http://www.opengis.net/sos/2.0", "InsertResultResponse");
    private final static QName _InsertResult_QNAME = new QName("http://www.opengis.net/sos/2.0", "InsertResult");
    private final static QName _GetResult_QNAME = new QName("http://www.opengis.net/sos/2.0", "GetResult");
    private final static QName _Capabilities_QNAME = new QName("http://www.opengis.net/sos/2.0", "Capabilities");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.sos._2
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetFeatureOfInterestPropertyType }
     *
     */
    public GetFeatureOfInterestPropertyType createGetFeatureOfInterestPropertyType() {
        return new GetFeatureOfInterestPropertyType();
    }

    /**
     * Create an instance of {@link ObservationOfferingType.ResultTime }
     *
     */
    public ObservationOfferingType.ResultTime createObservationOfferingTypeResultTime() {
        return new ObservationOfferingType.ResultTime();
    }

    /**
     * Create an instance of {@link GetObservationType }
     *
     */
    public GetObservationType createGetObservationType() {
        return new GetObservationType();
    }

    /**
     * Create an instance of {@link ResultTemplateType.ResultEncoding }
     *
     */
    public ResultEncoding createResultTemplateTypeResultEncoding() {
        return new ResultEncoding();
    }

    /**
     * Create an instance of {@link ContentsPropertyType }
     *
     */
    public ContentsPropertyType createContentsPropertyType() {
        return new ContentsPropertyType();
    }

    /**
     * Create an instance of {@link GetCapabilitiesPropertyType }
     *
     */
    public GetCapabilitiesPropertyType createGetCapabilitiesPropertyType() {
        return new GetCapabilitiesPropertyType();
    }

    /**
     * Create an instance of {@link InsertObservationResponsePropertyType }
     *
     */
    public InsertObservationResponsePropertyType createInsertObservationResponsePropertyType() {
        return new InsertObservationResponsePropertyType();
    }

    /**
     * Create an instance of {@link GetResultResponsePropertyType }
     *
     */
    public GetResultResponsePropertyType createGetResultResponsePropertyType() {
        return new GetResultResponsePropertyType();
    }

    /**
     * Create an instance of {@link SosInsertionMetadataType }
     *
     */
    public SosInsertionMetadataType createSosInsertionMetadataType() {
        return new SosInsertionMetadataType();
    }

    /**
     * Create an instance of {@link TemporalFilter }
     *
     */
    public TemporalFilterType createTemporalFilterType() {
        return new TemporalFilterType();
    }

    /**
     * Create an instance of {@link InsertionCapabilitiesType }
     *
     */
    public InsertionCapabilitiesType createInsertionCapabilitiesType() {
        return new InsertionCapabilitiesType();
    }

    /**
     * Create an instance of {@link GetResultTemplateResponseType.ResultStructure }
     *
     */
    public ResultStructure createGetResultTemplateResponseTypeResultStructure() {
        return new ResultStructure();
    }

    /**
     * Create an instance of {@link InsertResultResponsePropertyType }
     *
     */
    public InsertResultResponsePropertyType createInsertResultResponsePropertyType() {
        return new InsertResultResponsePropertyType();
    }

    /**
     * Create an instance of {@link GetResultType }
     *
     */
    public GetResultType createGetResultType() {
        return new GetResultType();
    }

    /**
     * Create an instance of {@link GetResultTemplateResponseType.ResultEncoding }
     *
     */
    public ResultEncoding createGetResultTemplateResponseTypeResultEncoding() {
        return new ResultEncoding();
    }

    /**
     * Create an instance of {@link ResultTemplateType }
     *
     */
    public ResultTemplateType createResultTemplateType() {
        return new ResultTemplateType();
    }

    /**
     * Create an instance of {@link ObservationOfferingPropertyType }
     *
     */
    public ObservationOfferingPropertyType createObservationOfferingPropertyType() {
        return new ObservationOfferingPropertyType();
    }

    /**
     * Create an instance of {@link GetObservationByIdResponsePropertyType }
     *
     */
    public GetObservationByIdResponsePropertyType createGetObservationByIdResponsePropertyType() {
        return new GetObservationByIdResponsePropertyType();
    }

    /**
     * Create an instance of {@link GetResultTemplateResponseType }
     *
     */
    public GetResultTemplateResponseType createGetResultTemplateResponseType() {
        return new GetResultTemplateResponseType();
    }

    /**
     * Create an instance of {@link GetFeatureOfInterestResponsePropertyType }
     *
     */
    public GetFeatureOfInterestResponsePropertyType createGetFeatureOfInterestResponsePropertyType() {
        return new GetFeatureOfInterestResponsePropertyType();
    }

    /**
     * Create an instance of {@link CapabilitiesPropertyType }
     *
     */
    public CapabilitiesPropertyType createCapabilitiesPropertyType() {
        return new CapabilitiesPropertyType();
    }

    /**
     * Create an instance of {@link InsertObservationType.Observation }
     *
     */
    public InsertObservationType.Observation createInsertObservationTypeObservation() {
        return new InsertObservationType.Observation();
    }

    /**
     * Create an instance of {@link SpatialFilter }
     *
     */
    public SpatialFilterType createSpatialFilterType() {
        return new SpatialFilterType();
    }

    /**
     * Create an instance of {@link GetResultResponseType }
     *
     */
    public GetResultResponseType createGetResultResponseType() {
        return new GetResultResponseType();
    }

    /**
     * Create an instance of {@link CapabilitiesType }
     *
     */
    public CapabilitiesType createCapabilitiesType() {
        return new CapabilitiesType();
    }

    /**
     * Create an instance of {@link InsertResultResponseType }
     *
     */
    public InsertResultResponseType createInsertResultResponseType() {
        return new InsertResultResponseType();
    }

    /**
     * Create an instance of {@link GetResultTemplateType }
     *
     */
    public GetResultTemplateType createGetResultTemplateType() {
        return new GetResultTemplateType();
    }

    /**
     * Create an instance of {@link GetObservationByIdPropertyType }
     *
     */
    public GetObservationByIdPropertyType createGetObservationByIdPropertyType() {
        return new GetObservationByIdPropertyType();
    }

    /**
     * Create an instance of {@link GetObservationPropertyType }
     *
     */
    public GetObservationPropertyType createGetObservationPropertyType() {
        return new GetObservationPropertyType();
    }

    /**
     * Create an instance of {@link GetObservationResponseType.ObservationData }
     *
     */
    public GetObservationResponseType.ObservationData createGetObservationResponseTypeObservationData() {
        return new GetObservationResponseType.ObservationData();
    }

    /**
     * Create an instance of {@link InsertResultTemplatePropertyType }
     *
     */
    public InsertResultTemplatePropertyType createInsertResultTemplatePropertyType() {
        return new InsertResultTemplatePropertyType();
    }

    /**
     * Create an instance of {@link InsertResultTemplateType.ProposedTemplate }
     *
     */
    public InsertResultTemplateType.ProposedTemplate createInsertResultTemplateTypeProposedTemplate() {
        return new InsertResultTemplateType.ProposedTemplate();
    }

    /**
     * Create an instance of {@link CapabilitiesType.FilterCapabilities }
     *
     */
    public FilterCapabilities createCapabilitiesTypeFilterCapabilities() {
        return new FilterCapabilities();
    }

    /**
     * Create an instance of {@link InsertionCapabilitiesPropertyType }
     *
     */
    public InsertionCapabilitiesPropertyType createInsertionCapabilitiesPropertyType() {
        return new InsertionCapabilitiesPropertyType();
    }

    /**
     * Create an instance of {@link InsertObservationType }
     *
     */
    public InsertObservationType createInsertObservationType() {
        return new InsertObservationType();
    }

    /**
     * Create an instance of {@link GetFeatureOfInterestType }
     *
     */
    public GetFeatureOfInterestType createGetFeatureOfInterestType() {
        return new GetFeatureOfInterestType();
    }

    /**
     * Create an instance of {@link GetObservationByIdType }
     *
     */
    public GetObservationByIdType createGetObservationByIdType() {
        return new GetObservationByIdType();
    }

    /**
     * Create an instance of {@link GetResultPropertyType }
     *
     */
    public GetResultPropertyType createGetResultPropertyType() {
        return new GetResultPropertyType();
    }

    /**
     * Create an instance of {@link ResultTemplatePropertyType }
     *
     */
    public ResultTemplatePropertyType createResultTemplatePropertyType() {
        return new ResultTemplatePropertyType();
    }

    /**
     * Create an instance of {@link GetObservationResponsePropertyType }
     *
     */
    public GetObservationResponsePropertyType createGetObservationResponsePropertyType() {
        return new GetObservationResponsePropertyType();
    }

    /**
     * Create an instance of {@link GetObservationResponseType }
     *
     */
    public GetObservationResponseType createGetObservationResponseType() {
        return new GetObservationResponseType();
    }

    /**
     * Create an instance of {@link InsertResultPropertyType }
     *
     */
    public InsertResultPropertyType createInsertResultPropertyType() {
        return new InsertResultPropertyType();
    }

    /**
     * Create an instance of {@link GetCapabilitiesType }
     *
     */
    public GetCapabilitiesType createGetCapabilitiesType() {
        return new GetCapabilitiesType();
    }

    /**
     * Create an instance of {@link CapabilitiesType.Contents }
     *
     */
    public CapabilitiesType.Contents createCapabilitiesTypeContents() {
        return new CapabilitiesType.Contents();
    }

    /**
     * Create an instance of {@link ResultTemplateType.ObservationTemplate }
     *
     */
    public ResultTemplateType.ObservationTemplate createResultTemplateTypeObservationTemplate() {
        return new ResultTemplateType.ObservationTemplate();
    }

    /**
     * Create an instance of {@link ResultTemplateType.ResultStructure }
     *
     */
    public ResultStructure createResultTemplateTypeResultStructure() {
        return new ResultStructure();
    }

    /**
     * Create an instance of {@link InsertObservationPropertyType }
     *
     */
    public InsertObservationPropertyType createInsertObservationPropertyType() {
        return new InsertObservationPropertyType();
    }

    /**
     * Create an instance of {@link ObservationOfferingType.ObservedArea }
     *
     */
    public ObservationOfferingType.ObservedArea createObservationOfferingTypeObservedArea() {
        return new ObservationOfferingType.ObservedArea();
    }

    /**
     * Create an instance of {@link GetObservationByIdResponseType.Observation }
     *
     */
    public GetObservationByIdResponseType.Observation createGetObservationByIdResponseTypeObservation() {
        return new GetObservationByIdResponseType.Observation();
    }

    /**
     * Create an instance of {@link ContentsType }
     *
     */
    public ContentsType createContentsType() {
        return new ContentsType();
    }

    /**
     * Create an instance of {@link ObservationOfferingType }
     *
     */
    public ObservationOfferingType createObservationOfferingType() {
        return new ObservationOfferingType();
    }

    /**
     * Create an instance of {@link GetResultTemplatePropertyType }
     *
     */
    public GetResultTemplatePropertyType createGetResultTemplatePropertyType() {
        return new GetResultTemplatePropertyType();
    }

    /**
     * Create an instance of {@link InsertResultTemplateResponseType }
     *
     */
    public InsertResultTemplateResponseType createInsertResultTemplateResponseType() {
        return new InsertResultTemplateResponseType();
    }

    /**
     * Create an instance of {@link GetFeatureOfInterestResponseType }
     *
     */
    public GetFeatureOfInterestResponseType createGetFeatureOfInterestResponseType() {
        return new GetFeatureOfInterestResponseType();
    }

    /**
     * Create an instance of {@link InsertResultTemplateResponsePropertyType }
     *
     */
    public InsertResultTemplateResponsePropertyType createInsertResultTemplateResponsePropertyType() {
        return new InsertResultTemplateResponsePropertyType();
    }

    /**
     * Create an instance of {@link InsertResultTemplateType }
     *
     */
    public InsertResultTemplateType createInsertResultTemplateType() {
        return new InsertResultTemplateType();
    }

    /**
     * Create an instance of {@link GetObservationByIdResponseType }
     *
     */
    public GetObservationByIdResponseType createGetObservationByIdResponseType() {
        return new GetObservationByIdResponseType();
    }

    /**
     * Create an instance of {@link InsertResultType }
     *
     */
    public InsertResultType createInsertResultType() {
        return new InsertResultType();
    }

    /**
     * Create an instance of {@link GetResultTemplateResponsePropertyType }
     *
     */
    public GetResultTemplateResponsePropertyType createGetResultTemplateResponsePropertyType() {
        return new GetResultTemplateResponsePropertyType();
    }

    /**
     * Create an instance of {@link SosInsertionMetadataPropertyType }
     *
     */
    public SosInsertionMetadataPropertyType createSosInsertionMetadataPropertyType() {
        return new SosInsertionMetadataPropertyType();
    }

    /**
     * Create an instance of {@link InsertObservationResponseType }
     *
     */
    public InsertObservationResponseType createInsertObservationResponseType() {
        return new InsertObservationResponseType();
    }

    /**
     * Create an instance of {@link ObservationOfferingType.PhenomenonTime }
     *
     */
    public ObservationOfferingType.PhenomenonTime createObservationOfferingTypePhenomenonTime() {
        return new ObservationOfferingType.PhenomenonTime();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertObservationResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "InsertObservationResponse", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleResponse")
    public JAXBElement<InsertObservationResponseType> createInsertObservationResponse(InsertObservationResponseType value) {
        return new JAXBElement<InsertObservationResponseType>(_InsertObservationResponse_QNAME, InsertObservationResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFeatureOfInterestResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "GetFeatureOfInterestResponse", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleResponse")
    public JAXBElement<GetFeatureOfInterestResponseType> createGetFeatureOfInterestResponse(GetFeatureOfInterestResponseType value) {
        return new JAXBElement<GetFeatureOfInterestResponseType>(_GetFeatureOfInterestResponse_QNAME, GetFeatureOfInterestResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertResultTemplateType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "InsertResultTemplate", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleRequest")
    public JAXBElement<InsertResultTemplateType> createInsertResultTemplate(InsertResultTemplateType value) {
        return new JAXBElement<InsertResultTemplateType>(_InsertResultTemplate_QNAME, InsertResultTemplateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertObservationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "InsertObservation", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleRequest")
    public JAXBElement<InsertObservationType> createInsertObservation(InsertObservationType value) {
        return new JAXBElement<InsertObservationType>(_InsertObservation_QNAME, InsertObservationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SosInsertionMetadataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "SosInsertionMetadata", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "InsertionMetadata")
    public JAXBElement<SosInsertionMetadataType> createSosInsertionMetadata(SosInsertionMetadataType value) {
        return new JAXBElement<SosInsertionMetadataType>(_SosInsertionMetadata_QNAME, SosInsertionMetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertionCapabilitiesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "InsertionCapabilities")
    public JAXBElement<InsertionCapabilitiesType> createInsertionCapabilities(InsertionCapabilitiesType value) {
        return new JAXBElement<InsertionCapabilitiesType>(_InsertionCapabilities_QNAME, InsertionCapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertResultTemplateResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "InsertResultTemplateResponse", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleResponse")
    public JAXBElement<InsertResultTemplateResponseType> createInsertResultTemplateResponse(InsertResultTemplateResponseType value) {
        return new JAXBElement<InsertResultTemplateResponseType>(_InsertResultTemplateResponse_QNAME, InsertResultTemplateResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetObservationByIdResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "GetObservationByIdResponse", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleResponse")
    public JAXBElement<GetObservationByIdResponseType> createGetObservationByIdResponse(GetObservationByIdResponseType value) {
        return new JAXBElement<GetObservationByIdResponseType>(_GetObservationByIdResponse_QNAME, GetObservationByIdResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetObservationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "GetObservation", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleRequest")
    public JAXBElement<GetObservationType> createGetObservation(GetObservationType value) {
        return new JAXBElement<GetObservationType>(_GetObservation_QNAME, GetObservationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "GetCapabilities", substitutionHeadNamespace = "http://www.opengis.net/ows/1.1", substitutionHeadName = "GetCapabilities")
    public JAXBElement<GetCapabilitiesType> createGetCapabilities(GetCapabilitiesType value) {
        return new JAXBElement<GetCapabilitiesType>(_GetCapabilities_QNAME, GetCapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContentsType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "Contents", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "AbstractContents")
    public JAXBElement<ContentsType> createContents(ContentsType value) {
        return new JAXBElement<ContentsType>(_Contents_QNAME, ContentsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetObservationResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "GetObservationResponse", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleResponse")
    public JAXBElement<GetObservationResponseType> createGetObservationResponse(GetObservationResponseType value) {
        return new JAXBElement<GetObservationResponseType>(_GetObservationResponse_QNAME, GetObservationResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFeatureOfInterestType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "GetFeatureOfInterest", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleRequest")
    public JAXBElement<GetFeatureOfInterestType> createGetFeatureOfInterest(GetFeatureOfInterestType value) {
        return new JAXBElement<GetFeatureOfInterestType>(_GetFeatureOfInterest_QNAME, GetFeatureOfInterestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResultTemplateType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "ResultTemplate", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "AbstractSWES")
    public JAXBElement<ResultTemplateType> createResultTemplate(ResultTemplateType value) {
        return new JAXBElement<ResultTemplateType>(_ResultTemplate_QNAME, ResultTemplateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObservationOfferingType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "ObservationOffering", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "AbstractOffering")
    public JAXBElement<ObservationOfferingType> createObservationOffering(ObservationOfferingType value) {
        return new JAXBElement<ObservationOfferingType>(_ObservationOffering_QNAME, ObservationOfferingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResultTemplateResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "GetResultTemplateResponse", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleResponse")
    public JAXBElement<GetResultTemplateResponseType> createGetResultTemplateResponse(GetResultTemplateResponseType value) {
        return new JAXBElement<GetResultTemplateResponseType>(_GetResultTemplateResponse_QNAME, GetResultTemplateResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResultTemplateType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "GetResultTemplate", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleRequest")
    public JAXBElement<GetResultTemplateType> createGetResultTemplate(GetResultTemplateType value) {
        return new JAXBElement<GetResultTemplateType>(_GetResultTemplate_QNAME, GetResultTemplateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResultResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "GetResultResponse", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleResponse")
    public JAXBElement<GetResultResponseType> createGetResultResponse(GetResultResponseType value) {
        return new JAXBElement<GetResultResponseType>(_GetResultResponse_QNAME, GetResultResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetObservationByIdType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "GetObservationById", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleRequest")
    public JAXBElement<GetObservationByIdType> createGetObservationById(GetObservationByIdType value) {
        return new JAXBElement<GetObservationByIdType>(_GetObservationById_QNAME, GetObservationByIdType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertResultResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "InsertResultResponse", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleResponse")
    public JAXBElement<InsertResultResponseType> createInsertResultResponse(InsertResultResponseType value) {
        return new JAXBElement<InsertResultResponseType>(_InsertResultResponse_QNAME, InsertResultResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InsertResultType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "InsertResult", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleRequest")
    public JAXBElement<InsertResultType> createInsertResult(InsertResultType value) {
        return new JAXBElement<InsertResultType>(_InsertResult_QNAME, InsertResultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResultType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "GetResult", substitutionHeadNamespace = "http://www.opengis.net/swes/2.0", substitutionHeadName = "ExtensibleRequest")
    public JAXBElement<GetResultType> createGetResult(GetResultType value) {
        return new JAXBElement<GetResultType>(_GetResult_QNAME, GetResultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CapabilitiesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sos/2.0", name = "Capabilities")
    public JAXBElement<CapabilitiesType> createCapabilities(CapabilitiesType value) {
        return new JAXBElement<CapabilitiesType>(_Capabilities_QNAME, CapabilitiesType.class, null, value);
    }

}
