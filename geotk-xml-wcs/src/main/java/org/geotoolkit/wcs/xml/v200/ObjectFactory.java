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

package org.geotoolkit.wcs.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.wcs._2 package.
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

    private final static QName _DimensionTrim_QNAME = new QName("http://www.opengis.net/wcs/2.0", "DimensionTrim");
    private final static QName _CoverageDescriptions_QNAME = new QName("http://www.opengis.net/wcs/2.0", "CoverageDescriptions");
    private final static QName _CoverageId_QNAME = new QName("http://www.opengis.net/wcs/2.0", "CoverageId");
    private final static QName _CoverageSummary_QNAME = new QName("http://www.opengis.net/wcs/2.0", "CoverageSummary");
    private final static QName _ServiceParameters_QNAME = new QName("http://www.opengis.net/wcs/2.0", "ServiceParameters");
    private final static QName _DescribeCoverage_QNAME = new QName("http://www.opengis.net/wcs/2.0", "DescribeCoverage");
    private final static QName _DimensionSlice_QNAME = new QName("http://www.opengis.net/wcs/2.0", "DimensionSlice");
    private final static QName _CoverageSubtypeParent_QNAME = new QName("http://www.opengis.net/wcs/2.0", "CoverageSubtypeParent");
    private final static QName _GetCoverage_QNAME = new QName("http://www.opengis.net/wcs/2.0", "GetCoverage");
    private final static QName _CoverageSubtype_QNAME = new QName("http://www.opengis.net/wcs/2.0", "CoverageSubtype");
    private final static QName _OfferedCoverage_QNAME = new QName("http://www.opengis.net/wcs/2.0", "OfferedCoverage");
    private final static QName _Extension_QNAME = new QName("http://www.opengis.net/wcs/2.0", "Extension");
    private final static QName _GetCapabilities_QNAME = new QName("http://www.opengis.net/wcs/2.0", "GetCapabilities");
    private final static QName _ServiceMetadata_QNAME = new QName("http://www.opengis.net/wcs/2.0", "ServiceMetadata");
    private final static QName _DimensionSubset_QNAME = new QName("http://www.opengis.net/wcs/2.0", "DimensionSubset");
    private final static QName _Contents_QNAME = new QName("http://www.opengis.net/wcs/2.0", "Contents");
    private final static QName _CoverageDescription_QNAME = new QName("http://www.opengis.net/wcs/2.0", "CoverageDescription");
    private final static QName _Capabilities_QNAME = new QName("http://www.opengis.net/wcs/2.0", "Capabilities");
    private final static QName _CoverageOfferings_QNAME = new QName("http://www.opengis.net/wcs/2.0", "CoverageOfferings");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.wcs._2
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CoverageSubtypeParentType }
     *
     */
    public CoverageSubtypeParentType createCoverageSubtypeParentType() {
        return new CoverageSubtypeParentType();
    }

    /**
     * Create an instance of {@link ServiceParametersType }
     *
     */
    public ServiceParametersType createServiceParametersType() {
        return new ServiceParametersType();
    }

    /**
     * Create an instance of {@link CoverageDescriptionsType }
     *
     */
    public CoverageDescriptionsType createCoverageDescriptionsType() {
        return new CoverageDescriptionsType();
    }

    /**
     * Create an instance of {@link CoverageSummaryType }
     *
     */
    public CoverageSummaryType createCoverageSummaryType() {
        return new CoverageSummaryType();
    }

    /**
     * Create an instance of {@link ServiceMetadataType }
     *
     */
    public ServiceMetadataType createServiceMetadataType() {
        return new ServiceMetadataType();
    }

    /**
     * Create an instance of {@link DescribeCoverageType }
     *
     */
    public DescribeCoverageType createDescribeCoverageType() {
        return new DescribeCoverageType();
    }

    /**
     * Create an instance of {@link CoverageOfferingsType }
     *
     */
    public CoverageOfferingsType createCoverageOfferingsType() {
        return new CoverageOfferingsType();
    }

    /**
     * Create an instance of {@link ContentsType }
     *
     */
    public ContentsType createContentsType() {
        return new ContentsType();
    }

    /**
     * Create an instance of {@link GetCoverageType }
     *
     */
    public GetCoverageType createGetCoverageType() {
        return new GetCoverageType();
    }

    /**
     * Create an instance of {@link DimensionSliceType }
     *
     */
    public DimensionSliceType createDimensionSliceType() {
        return new DimensionSliceType();
    }

    /**
     * Create an instance of {@link GetCapabilitiesType }
     *
     */
    public GetCapabilitiesType createGetCapabilitiesType() {
        return new GetCapabilitiesType();
    }

    /**
     * Create an instance of {@link ExtensionType }
     *
     */
    public ExtensionType createExtensionType() {
        return new ExtensionType();
    }

    /**
     * Create an instance of {@link CoverageDescriptionType }
     *
     */
    public CoverageDescriptionType createCoverageDescriptionType() {
        return new CoverageDescriptionType();
    }

    /**
     * Create an instance of {@link DimensionTrimType }
     *
     */
    public DimensionTrimType createDimensionTrimType() {
        return new DimensionTrimType();
    }

    /**
     * Create an instance of {@link CapabilitiesType }
     *
     */
    public CapabilitiesType createCapabilitiesType() {
        return new CapabilitiesType();
    }

    /**
     * Create an instance of {@link OfferedCoverageType }
     *
     */
    public OfferedCoverageType createOfferedCoverageType() {
        return new OfferedCoverageType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DimensionTrimType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "DimensionTrim", substitutionHeadNamespace = "http://www.opengis.net/wcs/2.0", substitutionHeadName = "DimensionSubset")
    public JAXBElement<DimensionTrimType> createDimensionTrim(DimensionTrimType value) {
        return new JAXBElement<DimensionTrimType>(_DimensionTrim_QNAME, DimensionTrimType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoverageDescriptionsType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "CoverageDescriptions")
    public JAXBElement<CoverageDescriptionsType> createCoverageDescriptions(CoverageDescriptionsType value) {
        return new JAXBElement<CoverageDescriptionsType>(_CoverageDescriptions_QNAME, CoverageDescriptionsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "CoverageId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createCoverageId(String value) {
        return new JAXBElement<String>(_CoverageId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoverageSummaryType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "CoverageSummary")
    public JAXBElement<CoverageSummaryType> createCoverageSummary(CoverageSummaryType value) {
        return new JAXBElement<CoverageSummaryType>(_CoverageSummary_QNAME, CoverageSummaryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceParametersType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "ServiceParameters")
    public JAXBElement<ServiceParametersType> createServiceParameters(ServiceParametersType value) {
        return new JAXBElement<ServiceParametersType>(_ServiceParameters_QNAME, ServiceParametersType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeCoverageType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "DescribeCoverage")
    public JAXBElement<DescribeCoverageType> createDescribeCoverage(DescribeCoverageType value) {
        return new JAXBElement<DescribeCoverageType>(_DescribeCoverage_QNAME, DescribeCoverageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DimensionSliceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "DimensionSlice", substitutionHeadNamespace = "http://www.opengis.net/wcs/2.0", substitutionHeadName = "DimensionSubset")
    public JAXBElement<DimensionSliceType> createDimensionSlice(DimensionSliceType value) {
        return new JAXBElement<DimensionSliceType>(_DimensionSlice_QNAME, DimensionSliceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoverageSubtypeParentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "CoverageSubtypeParent")
    public JAXBElement<CoverageSubtypeParentType> createCoverageSubtypeParent(CoverageSubtypeParentType value) {
        return new JAXBElement<CoverageSubtypeParentType>(_CoverageSubtypeParent_QNAME, CoverageSubtypeParentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCoverageType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "GetCoverage")
    public JAXBElement<GetCoverageType> createGetCoverage(GetCoverageType value) {
        return new JAXBElement<GetCoverageType>(_GetCoverage_QNAME, GetCoverageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "CoverageSubtype")
    public JAXBElement<QName> createCoverageSubtype(QName value) {
        return new JAXBElement<QName>(_CoverageSubtype_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OfferedCoverageType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "OfferedCoverage")
    public JAXBElement<OfferedCoverageType> createOfferedCoverage(OfferedCoverageType value) {
        return new JAXBElement<OfferedCoverageType>(_OfferedCoverage_QNAME, OfferedCoverageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtensionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "Extension")
    public JAXBElement<ExtensionType> createExtension(ExtensionType value) {
        return new JAXBElement<ExtensionType>(_Extension_QNAME, ExtensionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "GetCapabilities")
    public JAXBElement<GetCapabilitiesType> createGetCapabilities(GetCapabilitiesType value) {
        return new JAXBElement<GetCapabilitiesType>(_GetCapabilities_QNAME, GetCapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceMetadataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "ServiceMetadata")
    public JAXBElement<ServiceMetadataType> createServiceMetadata(ServiceMetadataType value) {
        return new JAXBElement<ServiceMetadataType>(_ServiceMetadata_QNAME, ServiceMetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DimensionSubsetType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "DimensionSubset")
    public JAXBElement<DimensionSubsetType> createDimensionSubset(DimensionSubsetType value) {
        return new JAXBElement<DimensionSubsetType>(_DimensionSubset_QNAME, DimensionSubsetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContentsType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "Contents")
    public JAXBElement<ContentsType> createContents(ContentsType value) {
        return new JAXBElement<ContentsType>(_Contents_QNAME, ContentsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoverageDescriptionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "CoverageDescription", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractFeature")
    public JAXBElement<CoverageDescriptionType> createCoverageDescription(CoverageDescriptionType value) {
        return new JAXBElement<CoverageDescriptionType>(_CoverageDescription_QNAME, CoverageDescriptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CapabilitiesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "Capabilities")
    public JAXBElement<CapabilitiesType> createCapabilities(CapabilitiesType value) {
        return new JAXBElement<CapabilitiesType>(_Capabilities_QNAME, CapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoverageOfferingsType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/2.0", name = "CoverageOfferings")
    public JAXBElement<CoverageOfferingsType> createCoverageOfferings(CoverageOfferingsType value) {
        return new JAXBElement<CoverageOfferingsType>(_CoverageOfferings_QNAME, CoverageOfferingsType.class, null, value);
    }

}
