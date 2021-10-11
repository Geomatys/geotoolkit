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
package org.geotoolkit.wcs.xml.v100;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.geotoolkit.ows.xml.v110.ReferenceGroupType;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.wcs._1_1 package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 * @module
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _DomainSet_QNAME = new QName("http://www.opengis.net/wcs", "domainSet");
    private static final QName _CoverageOffering_QNAME = new QName("http://www.opengis.net/wcs", "CoverageOffering");
    private static final QName _SpatialDomain_QNAME = new QName("http://www.opengis.net/wcs", "spatialDomain");
    private static final QName _RangeSet_QNAME = new QName("http://www.opengis.net/wcs", "RangeSet");
    private static final QName _SupportedCRSs_QNAME = new QName("http://www.opengis.net/wcs", "supportedCRSs");
    private static final QName _SupportedFormats_QNAME = new QName("http://www.opengis.net/wcs", "supportedFormats");
    private static final QName _SupportedInterpolations_QNAME = new QName("http://www.opengis.net/wcs", "supportedInterpolations");
    private static final QName _GridBaseCRS_QNAME = new QName("http://www.opengis.net/wcs", "GridBaseCRS");
    private static final QName _TemporalSubset_QNAME = new QName("http://www.opengis.net/wcs", "TemporalSubset");
    private static final QName _GridCS_QNAME = new QName("http://www.opengis.net/wcs", "GridCS");
    private static final QName _GridOffsets_QNAME = new QName("http://www.opengis.net/wcs", "GridOffsets");
    private static final QName _GridType_QNAME = new QName("http://www.opengis.net/wcs", "GridType");
    private static final QName _TemporalDomain_QNAME = new QName("http://www.opengis.net/wcs", "temporalDomain");
    private static final QName _Coverage_QNAME = new QName("http://www.opengis.net/wcs", "Coverage");
    private static final QName _Identifier_QNAME = new QName("http://www.opengis.net/wcs", "Identifier");
    private static final QName _GridOrigin_QNAME = new QName("http://www.opengis.net/wcs", "GridOrigin");
    private static final QName _MetadataLink_QNAME = new QName("http://www.opengis.net/wcs", "metadataLink");
    private static final QName _Description_QNAME = new QName("http://www.opengis.net/wcs", "description");
    private static final QName _Label_QNAME = new QName("http://www.opengis.net/wcs", "label");
    private static final QName _TimeSequence_QNAME = new QName("http://www.opengis.net/wcs", "TimeSequence");
    private static final QName _TimePeriod_QNAME = new QName("http://www.opengis.net/wcs", "timePeriod");
    private static final QName _WCSCapabilities_QNAME = new QName("http://www.opengis.net/wcs", "WCS_Capabilities");
    private static final QName _LonLatEnvelope_QNAME = new QName("http://www.opengis.net/wcs", "lonLatEnvelope");
    private static final QName _CoverageOfferingBrief_QNAME = new QName("http://www.opengis.net/wcs", "CoverageOfferingBrief");
    private static final QName _Name_QNAME = new QName("http://www.opengis.net/wcs", "name");
    private static final QName _Capability_QNAME = new QName("http://www.opengis.net/wcs", "Capability");
    private static final QName _Service_QNAME = new QName("http://www.opengis.net/wcs", "Service");
    private static final QName _AbstractDescriptionTypeLabel_QNAME = new QName("http://www.opengis.net/wcs", "label");
    private static final QName _ResponsiblePartyTypePositionName_QNAME = new QName("http://www.opengis.net/wcs", "positionName");
    private static final QName _ResponsiblePartyTypeContactInfo_QNAME = new QName("http://www.opengis.net/wcs", "contactInfo");
    private static final QName _ResponsiblePartyTypeOrganisationName_QNAME = new QName("http://www.opengis.net/wcs", "organisationName");
    private static final QName _ResponsiblePartyTypeIndividualName_QNAME = new QName("http://www.opengis.net/wcs", "individualName");
    private static final QName _SpatialSubset_QNAME = new QName("http://www.opengis.net/wcs", "spatialSubset");
    private static final QName _Interval_QNAME = new QName("http://www.opengis.net/wcs", "interval");
    private static final QName _SingleValue_QNAME = new QName("http://www.opengis.net/wcs", "singleValue");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.wcs.v100
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TimeSequenceType }
     *
     */
    public TimeSequenceType createTimeSequenceType() {
        return new TimeSequenceType();
    }

   /**
     * Create an instance of {@link TimePeriodType }
     *
     */
    public TimePeriodType createTimePeriodType() {
        return new TimePeriodType();
    }

   /**
     * Create an instance of {@link SpatialDomainType }
     *
     */
    public SpatialDomainType createSpatialDomainType() {
        return new SpatialDomainType();
    }

    /**
     * Create an instance of {@link Request.DescribeCoverage }
     *
     */
    public Request.DescribeCoverage createWCSCapabilityTypeRequestDescribeCoverage() {
        return new Request.DescribeCoverage();
    }

    /**
     * Create an instance of {@link DCPTypeType.HTTP }
     *
     */
    public DCPTypeType.HTTP createDCPTypeTypeHTTP() {
        return new DCPTypeType.HTTP();
    }

    /**
     * Create an instance of {@link MetadataLinkType }
     *
     */
    public MetadataLinkType createMetadataLinkType() {
        return new MetadataLinkType();
    }

    /**
     * Create an instance of {@link Keywords }
     *
     */
    public Keywords createKeywords() {
        return new Keywords();
    }

    /**
     * Create an instance of {@link ServiceType }
     *
     */
    public ServiceType createServiceType() {
        return new ServiceType();
    }

    /**
     * Create an instance of {@link LonLatEnvelopeBaseType }
     *
     */
    public LonLatEnvelopeBaseType createLonLatEnvelopeBaseType() {
        return new LonLatEnvelopeBaseType();
    }

    /**
     * Create an instance of {@link WCSCapabilitiesType }
     *
     */
    public WCSCapabilitiesType createWCSCapabilitiesType() {
        return new WCSCapabilitiesType();
    }

    /**
     * Create an instance of {@link DCPTypeType }
     *
     */
    public DCPTypeType createDCPTypeType() {
        return new DCPTypeType();
    }

    /**
     * Create an instance of {@link OnlineResourceType }
     *
     */
    public OnlineResourceType createOnlineResourceType() {
        return new OnlineResourceType();
    }

    /**
     * Create an instance of {@link DCPTypeType.HTTP.Get }
     *
     */
    public DCPTypeType.HTTP.Get createDCPTypeTypeHTTPGet() {
        return new DCPTypeType.HTTP.Get();
    }

    /**
     * Create an instance of {@link Request }
     *
     */
    public Request createWCSCapabilityTypeRequest() {
        return new Request();
    }

    /**
     * Create an instance of {@link Request.GetCoverage }
     *
     */
    public Request.GetCoverage createWCSCapabilityTypeRequestGetCoverage() {
        return new Request.GetCoverage();
    }

    /**
     * Create an instance of {@link ContentMetadata }
     *
     */
    public ContentMetadata createContentMetadata() {
        return new ContentMetadata();
    }

    /**
     * Create an instance of {@link Request.GetCapabilities }
     *
     */
    public Request.GetCapabilities createWCSCapabilityTypeRequestGetCapabilities() {
        return new Request.GetCapabilities();
    }

    /**
     * Create an instance of {@link DCPTypeType.HTTP.Post }
     *
     */
    public DCPTypeType.HTTP.Post createDCPTypeTypeHTTPPost() {
        return new DCPTypeType.HTTP.Post();
    }

    /**
     * Create an instance of {@link LonLatEnvelopeType }
     *
     */
    public LonLatEnvelopeType createLonLatEnvelopeType() {
        return new LonLatEnvelopeType();
    }

    /**
     * Create an instance of {@link WCSCapabilityType.Exception }
     *
     */
    public WCSCapabilityType.Exception createWCSCapabilityTypeException() {
        return new WCSCapabilityType.Exception();
    }

    /**
     * Create an instance of {@link WCSCapabilityType }
     *
     */
    public WCSCapabilityType createWCSCapabilityType() {
        return new WCSCapabilityType();
    }

    /**
     * Create an instance of {@link MetadataAssociationType }
     *
     */
    public MetadataAssociationType createMetadataAssociationType() {
        return new MetadataAssociationType();
    }

    /**
     * Create an instance of {@link CoverageOfferingBriefType }
     *
     */
    public CoverageOfferingBriefType createCoverageOfferingBriefType() {
        return new CoverageOfferingBriefType();
    }

    /**
     * Create an instance of {@link WCSCapabilityType.VendorSpecificCapabilities }
     *
     */
    public WCSCapabilityType.VendorSpecificCapabilities createWCSCapabilityTypeVendorSpecificCapabilities() {
        return new WCSCapabilityType.VendorSpecificCapabilities();
    }

    /**
     * Create an instance of {@link CoverageDescription }
     *
     */
    public CoverageDescription createCoverageDescription() {
        return new CoverageDescription();
    }

    /**
     * Create an instance of {@link CoverageOfferingType }
     *
     */
    public CoverageOfferingType createCoverageOfferingType() {
        return new CoverageOfferingType();
    }

    /**
     * Create an instance of {@link DomainSetType }
     *
     */
    public DomainSetType createDomainSetType() {
        return new DomainSetType();
    }

    /**
     * Create an instance of {@link RangeSetType }
     *
     */
    public RangeSetType createRangeSetType() {
        return new RangeSetType();
    }

    /**
     * Create an instance of {@link SupportedCRSsType }
     *
     */
    public SupportedCRSsType createSupportedCRSsType() {
        return new SupportedCRSsType();
    }

    /**
     * Create an instance of {@link SupportedFormatsType }
     *
     */
    public SupportedFormatsType createSupportedFormatsType() {
        return new SupportedFormatsType();
    }

    /**
     * Create an instance of {@link SupportedInterpolationsType }
     *
     */
    public SupportedInterpolationsType createSupportedInterpolationsType() {
        return new SupportedInterpolationsType();
    }

    /**
     * Create an instance of {@link ResponsiblePartyType }
     *
     */
    public ResponsiblePartyType createResponsiblePartyType() {
        return new ResponsiblePartyType();
    }

    /**
     * Create an instance of {@link ContactType }
     *
     */
    public ContactType createContactType() {
        return new ContactType();
    }

    /**
     * Create an instance of {@link TelephoneType }
     *
     */
    public TelephoneType createTelephoneType() {
        return new TelephoneType();
    }

    /**
     * Create an instance of {@link AddressType }
     *
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link AddressType }
     *
     */
    public RangeSet createRangeSet() {
        return new RangeSet();
    }

    /**
     * Create an instance of {@link net.opengis.wcs.GetCapabilities }
     *
     */
    public GetCapabilitiesType createGetCapabilities() {
        return new GetCapabilitiesType();
    }

     /**
     * Create an instance of {@link net.opengis.wcs.GetCoverage }
     *
     */
    public GetCoverageType createGetCoverage() {
        return new GetCoverageType();
    }

    /**
     * Create an instance of {@link net.opengis.wcs.DescribeCoverage }
     *
     */
    public DescribeCoverageType createDescribeCoverage() {
        return new DescribeCoverageType();
    }

    /**
     * Create an instance of {@link DomainSubsetType }
     *
     */
    public DomainSubsetType createDomainSubsetType() {
        return new DomainSubsetType();
    }

    /**
     * Create an instance of {@link SpatialSubsetType }
     *
     */
    public SpatialSubsetType createSpatialSubsetType() {
        return new SpatialSubsetType();
    }

    /**
     * Create an instance of {@link OutputType }
     *
     */
    public OutputType createOutputType() {
        return new OutputType();
    }

     /**
     * Create an instance of {@link RangeSubsetType.AxisSubset }
     *
     */
    public RangeSubsetType.AxisSubset createRangeSubsetTypeAxisSubset() {
        return new RangeSubsetType.AxisSubset();
    }

    /**
     * Create an instance of {@link RangeSubsetType }
     *
     */
    public RangeSubsetType createRangeSubsetType() {
        return new RangeSubsetType();
    }

    /**
     * Create an instance of {@link ValueEnumBaseType }
     *
     */
    public ValueEnumBaseType createValueEnumBaseType() {
        return new ValueEnumBaseType();
    }

    /**
     * Create an instance of {@link IntervalType }
     *
     */
    public IntervalType createIntervalType() {
        return new IntervalType();
    }

    /**
     * Create an instance of {@link ValueRangeType }
     *
     */
    public ValueRangeType createValueRangeType() {
        return new ValueRangeType();
    }

    /**
     * Create an instance of {@link TypedLiteralType }
     *
     */
    public TypedLiteralType createTypedLiteralType() {
        return new TypedLiteralType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntervalType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "interval")
    public JAXBElement<IntervalType> createInterval(final IntervalType value) {
        return new JAXBElement<IntervalType>(_Interval_QNAME, IntervalType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypedLiteralType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "singleValue")
    public JAXBElement<TypedLiteralType> createSingleValue(final TypedLiteralType value) {
        return new JAXBElement<TypedLiteralType>(_SingleValue_QNAME, TypedLiteralType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeSequenceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "temporalSubset")
    public JAXBElement<TimeSequenceType> createTemporalSubset(final TimeSequenceType value) {
        return new JAXBElement<TimeSequenceType>(_TemporalSubset_QNAME, TimeSequenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpatialSubsetType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "spatialSubset")
    public JAXBElement<SpatialSubsetType> createSpatialSubset(final SpatialSubsetType value) {
        return new JAXBElement<SpatialSubsetType>(_SpatialSubset_QNAME, SpatialSubsetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "positionName", scope = ResponsiblePartyType.class)
    public JAXBElement<String> createResponsiblePartyTypePositionName(final String value) {
        return new JAXBElement<String>(_ResponsiblePartyTypePositionName_QNAME, String.class, ResponsiblePartyType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContactType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "contactInfo", scope = ResponsiblePartyType.class)
    public JAXBElement<ContactType> createResponsiblePartyTypeContactInfo(final ContactType value) {
        return new JAXBElement<ContactType>(_ResponsiblePartyTypeContactInfo_QNAME, ContactType.class, ResponsiblePartyType.class, value);
    }

     /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "organisationName", scope = ResponsiblePartyType.class)
    public JAXBElement<String> createResponsiblePartyTypeOrganisationName(final String value) {
        return new JAXBElement<String>(_ResponsiblePartyTypeOrganisationName_QNAME, String.class, ResponsiblePartyType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "individualName", scope = ResponsiblePartyType.class)
    public JAXBElement<String> createResponsiblePartyTypeIndividualName(final String value) {
        return new JAXBElement<String>(_ResponsiblePartyTypeIndividualName_QNAME, String.class, ResponsiblePartyType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SupportedInterpolationsType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "supportedInterpolations")
    public JAXBElement<SupportedInterpolationsType> createSupportedInterpolations(final SupportedInterpolationsType value) {
        return new JAXBElement<SupportedInterpolationsType>(_SupportedInterpolations_QNAME, SupportedInterpolationsType.class, null, value);
    }


     /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SupportedFormatsType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "supportedFormats")
    public JAXBElement<SupportedFormatsType> createSupportedFormats(final SupportedFormatsType value) {
        return new JAXBElement<SupportedFormatsType>(_SupportedFormats_QNAME, SupportedFormatsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SupportedCRSsType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "supportedCRSs")
    public JAXBElement<SupportedCRSsType> createSupportedCRSs(final SupportedCRSsType value) {
        return new JAXBElement<SupportedCRSsType>(_SupportedCRSs_QNAME, SupportedCRSsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RangeSetType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "RangeSet", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<RangeSetType> createRangeSet(final RangeSetType value) {
        return new JAXBElement<RangeSetType>(_RangeSet_QNAME, RangeSetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpatialDomainType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "spatialDomain")
    public JAXBElement<SpatialDomainType> createSpatialDomain(final SpatialDomainType value) {
        return new JAXBElement<SpatialDomainType>(_SpatialDomain_QNAME, SpatialDomainType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoverageOfferingType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "CoverageOffering", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<CoverageOfferingType> createCoverageOffering(final CoverageOfferingType value) {
        return new JAXBElement<CoverageOfferingType>(_CoverageOffering_QNAME, CoverageOfferingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "domainSet")
    public JAXBElement<DomainSetType> createDomainSet(final DomainSetType value) {
        return new JAXBElement<DomainSetType>(_DomainSet_QNAME, DomainSetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetadataLinkType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "metadataLink", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "metaDataProperty")
    public JAXBElement<MetadataLinkType> createMetadataLink(final MetadataLinkType value) {
        return new JAXBElement<MetadataLinkType>(_MetadataLink_QNAME, MetadataLinkType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "description")
    public JAXBElement<String> createDescription(final String value) {
        return new JAXBElement<String>(_Description_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "label")
    public JAXBElement<String> createLabel(final String value) {
        return new JAXBElement<String>(_Label_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeSequenceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "TimeSequence")
    public JAXBElement<TimeSequenceType> createTimeSequence(final TimeSequenceType value) {
        return new JAXBElement<TimeSequenceType>(_TimeSequence_QNAME, TimeSequenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimePeriodType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "timePeriod")
    public JAXBElement<TimePeriodType> createTimePeriod(final TimePeriodType value) {
        return new JAXBElement<TimePeriodType>(_TimePeriod_QNAME, TimePeriodType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WCSCapabilitiesType }{@code >}}
     *

    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "WCS_Capabilities")
    public JAXBElement<WCSCapabilitiesType> createWCSCapabilities(final WCSCapabilitiesType value) {
        return new JAXBElement<WCSCapabilitiesType>(_WCSCapabilities_QNAME, WCSCapabilitiesType.class, null, value);
    }*/

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LonLatEnvelopeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "lonLatEnvelope")
    public JAXBElement<LonLatEnvelopeType> createLonLatEnvelope(final LonLatEnvelopeType value) {
        return new JAXBElement<LonLatEnvelopeType>(_LonLatEnvelope_QNAME, LonLatEnvelopeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoverageOfferingBriefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "CoverageOfferingBrief", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<CoverageOfferingBriefType> createCoverageOfferingBrief(final CoverageOfferingBriefType value) {
        return new JAXBElement<CoverageOfferingBriefType>(_CoverageOfferingBrief_QNAME, CoverageOfferingBriefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "name")
    public JAXBElement<String> createName(final String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WCSCapabilityType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "Capability")
    public JAXBElement<WCSCapabilityType> createCapability(final WCSCapabilityType value) {
        return new JAXBElement<WCSCapabilityType>(_Capability_QNAME, WCSCapabilityType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "Service", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<ServiceType> createService(final ServiceType value) {
        return new JAXBElement<ServiceType>(_Service_QNAME, ServiceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "label", scope = AbstractDescriptionType.class)
    public JAXBElement<String> createAbstractDescriptionTypeLabel(final String value) {
        return new JAXBElement<String>(_AbstractDescriptionTypeLabel_QNAME, String.class, AbstractDescriptionType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "GridBaseCRS")
    public JAXBElement<String> createGridBaseCRS(final String value) {
        return new JAXBElement<String>(_GridBaseCRS_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeSequenceType }{@code >}}
     *

    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "TemporalSubset")
    public JAXBElement<TimeSequenceType> createTemporalSubset(TimeSequenceType value) {
        return new JAXBElement<TimeSequenceType>(_TemporalSubset_QNAME, TimeSequenceType.class, null, value);
    }*/

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "GridCS", defaultValue = "urn:ogc:def:cs:OGC:0.0:Grid2dSquareCS")
    public JAXBElement<String> createGridCS(final String value) {
        return new JAXBElement<String>(_GridCS_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "GridOffsets")
    public JAXBElement<List<Double>> createGridOffsets(final List<Double> value) {
        return new JAXBElement<List<Double>>(_GridOffsets_QNAME, ((Class) List.class), null, ((List<Double> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "GridType", defaultValue = "urn:ogc:def:method:WCS:1.1:2dSimpleGrid")
    public JAXBElement<String> createGridType(final String value) {
        return new JAXBElement<String>(_GridType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeSequenceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "temporalDomain")
    public JAXBElement<TimeSequenceType> createTemporalDomain(final TimeSequenceType value) {
        return new JAXBElement<TimeSequenceType>(_TemporalDomain_QNAME, TimeSequenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceGroupType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "Coverage", substitutionHeadNamespace = "http://www.opengis.net/ows/1.1", substitutionHeadName = "ReferenceGroup")
    public JAXBElement<ReferenceGroupType> createCoverage(final ReferenceGroupType value) {
        return new JAXBElement<ReferenceGroupType>(_Coverage_QNAME, ReferenceGroupType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "Identifier")
    public JAXBElement<String> createIdentifier(final String value) {
        return new JAXBElement<String>(_Identifier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "GridOrigin", defaultValue = "0 0")
    public JAXBElement<List<Double>> createGridOrigin(final List<Double> value) {
        return new JAXBElement<List<Double>>(_GridOrigin_QNAME, ((Class) List.class), null, ((List<Double> ) value));
    }

}
