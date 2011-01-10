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
package org.geotoolkit.wcs.xml.v111;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.geotoolkit.ows.xml.v110.ReferenceGroupType;

/**
 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.wcs.v111
 * @author Guilhem Legal
 * @module pending
 */
@XmlRegistry
public class ObjectFactory {
    
    private static final QName _CoverageSummaryTypeSupportedCRS_QNAME    = new QName("http://www.opengis.net/wcs/1.1.1", "SupportedCRS");
    private static final QName _CoverageSummaryTypeSupportedFormat_QNAME = new QName("http://www.opengis.net/wcs/1.1.1", "SupportedFormat");
    private static final QName _CoverageSummary_QNAME                    = new QName("http://www.opengis.net/wcs/1.1.1", "CoverageSummary");
    private static final QName _SpatialDomain_QNAME                      = new QName("http://www.opengis.net/wcs/1.1.1", "spatialDomain");
    private static final QName _GridCRS_QNAME                            = new QName("http://www.opengis.net/wcs/1.1.1", "GridCRS");
    private static final QName _InterpolationMethod_QNAME                = new QName("http://www.opengis.net/wcs/1.1.1", "interpolationMethod");
    private static final QName _Identifier_QNAME                         = new QName("http://www.opengis.net/wcs/1.1.1", "Identifier");
    private static final QName _GridBaseCRS_QNAME                        = new QName("http://www.opengis.net/wcs/1.1.1", "GridBaseCRS");
    private static final QName _TemporalSubset_QNAME                     = new QName("http://www.opengis.net/wcs/1.1.1", "TemporalSubset");
    private static final QName _GridCS_QNAME                             = new QName("http://www.opengis.net/wcs/1.1.1", "GridCS");
    private static final QName _Coverages_QNAME                          = new QName("http://www.opengis.net/wcs/1.1.1", "Coverages");
    private static final QName _GridOffsets_QNAME                        = new QName("http://www.opengis.net/wcs/1.1.1", "GridOffsets");
    private static final QName _GridType_QNAME                           = new QName("http://www.opengis.net/wcs/1.1.1", "GridType");
    private static final QName _TemporalDomain_QNAME                     = new QName("http://www.opengis.net/wcs/1.1.1", "TemporalDomain");
    private static final QName _Coverage_QNAME                           = new QName("http://www.opengis.net/wcs/1.1.1", "Coverage");
    private static final QName _GridOrigin_QNAME                         = new QName("http://www.opengis.net/wcs/1.1.1", "GridOrigin");
 
    /**
     * Create an instance of {@link Contents }
     * 
     */
    public Contents createContents() {
        return new Contents();
    }
    
    /**
     * Create an instance of {@link DescribeCoverage }
     * 
     */
    public DescribeCoverageType createDescribeCoverage() {
        return new DescribeCoverageType();
    }
    
    /**
     * Create an instance of {@link Capabilities }
     * 
     */
    public Capabilities createCapabilities() {
        return new Capabilities();
    }

    /**
     * Create an instance of {@link CoverageSummaryType }
     * 
     */
    public CoverageSummaryType createCoverageSummaryType() {
        return new CoverageSummaryType();
    }
    
    /**
     * Create an instance of {@link SpatialDomainType }
     * 
     */
    public SpatialDomainType createSpatialDomainType() {
        return new SpatialDomainType();
    }
    
    /**
     * Create an instance of {@link CoverageDomainType }
     * 
     */
    public CoverageDomainType createCoverageDomainType() {
        return new CoverageDomainType();
    }


    /**
     * Create an instance of {@link CoverageDescriptionType }
     * 
     */
    public CoverageDescriptionType createCoverageDescriptionType() {
        return new CoverageDescriptionType();
    }
    
    /**
     * Create an instance of {@link GridCrsType }
     * 
     */
    public GridCrsType createGridCrsType() {
        return new GridCrsType();
    }
    
    /**
     * Create an instance of {@link ImageCRSRefType }
     * 
     */
    public ImageCRSRefType createImageCRSRefType() {
        return new ImageCRSRefType();
    }

    /**
     * Create an instance of {@link TimeSequenceType }
     * 
     */
    public TimeSequenceType createTimeSequenceType() {
        return new TimeSequenceType();
    }
    
    /**
     * Create an instance of {@link InterpolationMethodType }
     * 
     */
    public InterpolationMethodType createInterpolationMethodType() {
        return new InterpolationMethodType();
    }
    
    /**
     * Create an instance of {@link InterpolationMethodBaseType }
     * 
     */
    public InterpolationMethodBaseType createInterpolationMethodBaseType() {
        return new InterpolationMethodBaseType();
    }
    
    /**
     * Create an instance of {@link FieldType }
     * 
     */
    public FieldType createFieldType() {
        return new FieldType();
    }
    
    /**
     * Create an instance of {@link InterpolationMethods }
     * 
     */
    public InterpolationMethods createInterpolationMethods() {
        return new InterpolationMethods();
    }
         
    /**
     * Create an instance of {@link TimePeriodType }
     * 
     */
    public TimePeriodType createTimePeriodType() {
        return new TimePeriodType();
    }
    
    /**
     * Create an instance of {@link RangeType }
     * 
     */
    public RangeType createRangeType() {
        return new RangeType();
    }
    /**
     * Create an instance of {@link AxisType }
     * 
     */
    public AxisType createAxisType() {
        return new AxisType();
    }

    /**
     * Create an instance of {@link AvailableKeys }
     * 
     */
    public AvailableKeys createAvailableKeys() {
        return new AvailableKeys();
    }

    /**
     * Create an instance of {@link CoverageDescriptions }
     * 
     */
    public CoverageDescriptions createCoverageDescriptions() {
        return new CoverageDescriptions();
    }

    /**
     * Create an instance of {@link GetCapabilities }
     * 
     */
    public GetCapabilitiesType createGetCapabilities() {
        return new GetCapabilitiesType();
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterpolationMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs", name = "interpolationMethod")
    public JAXBElement<InterpolationMethodType> createInterpolationMethod(final InterpolationMethodType value) {
        return new JAXBElement<InterpolationMethodType>(_InterpolationMethod_QNAME, InterpolationMethodType.class, null, value);
    }

    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GridCrsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "GridCRS")
    public JAXBElement<GridCrsType> createGridCRS(final GridCrsType value) {
        return new JAXBElement<GridCrsType>(_GridCRS_QNAME, GridCrsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpatialDomainType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "spatialDomain")
    public JAXBElement<SpatialDomainType> createSpatialDomain(final SpatialDomainType value) {
        return new JAXBElement<SpatialDomainType>(_SpatialDomain_QNAME, SpatialDomainType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "SupportedCRS", scope = CoverageSummaryType.class)
    public JAXBElement<String> createCoverageSummaryTypeSupportedCRS(final String value) {
        return new JAXBElement<String>(_CoverageSummaryTypeSupportedCRS_QNAME, String.class, CoverageSummaryType.class, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "SupportedFormat", scope = CoverageSummaryType.class)
    public JAXBElement<String> createCoverageSummaryTypeSupportedFormat(final String value) {
        return new JAXBElement<String>(_CoverageSummaryTypeSupportedFormat_QNAME, String.class, CoverageSummaryType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoverageSummaryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "CoverageSummary")
    public JAXBElement<CoverageSummaryType> createCoverageSummary(final CoverageSummaryType value) {
        return new JAXBElement<CoverageSummaryType>(_CoverageSummary_QNAME, CoverageSummaryType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "Identifier")
    public JAXBElement<String> createIdentifier(final String value) {
        return new JAXBElement<String>(_Identifier_QNAME, String.class, null, value);
    }
    
    /**
     * Create an instance of {@link RangeSubsetType.FieldSubset }
     * 
     */
    public RangeSubsetType.FieldSubset createRangeSubsetTypeFieldSubset() {
        return new RangeSubsetType.FieldSubset();
    }

    /**
     * Create an instance of {@link AxisSubset }
     * 
     */
    public AxisSubset createAxisSubset() {
        return new AxisSubset();
    }

    /**
     * Create an instance of {@link OutputType }
     * 
     */
    public OutputType createOutputType() {
        return new OutputType();
    }

    /**
     * Create an instance of {@link RangeSubsetType }
     * 
     */
    public RangeSubsetType createRangeSubsetType() {
        return new RangeSubsetType();
    }

    /**
     * Create an instance of {@link DomainSubsetType }
     * 
     */
    public DomainSubsetType createDomainSubsetType() {
        return new DomainSubsetType();
    }

    /**
     * Create an instance of {@link GetCoverage }
     * 
     */
    public GetCoverageType createGetCoverage() {
        return new GetCoverageType();
    }

    

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "GridBaseCRS")
    public JAXBElement<String> createGridBaseCRS(final String value) {
        return new JAXBElement<String>(_GridBaseCRS_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeSequenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "TemporalSubset")
    public JAXBElement<TimeSequenceType> createTemporalSubset(final TimeSequenceType value) {
        return new JAXBElement<TimeSequenceType>(_TemporalSubset_QNAME, TimeSequenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "GridCS", defaultValue = "urn:ogc:def:cs:OGC:0.0:Grid2dSquareCS")
    public JAXBElement<String> createGridCS(final String value) {
        return new JAXBElement<String>(_GridCS_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "GridOffsets")
    public JAXBElement<List<Double>> createGridOffsets(final List<Double> value) {
        return new JAXBElement<List<Double>>(_GridOffsets_QNAME, ((Class) List.class), null, ((List<Double> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "GridType", defaultValue = "urn:ogc:def:method:WCS:1.1:2dSimpleGrid")
    public JAXBElement<String> createGridType(final String value) {
        return new JAXBElement<String>(_GridType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeSequenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "TemporalDomain")
    public JAXBElement<TimeSequenceType> createTemporalDomain(final TimeSequenceType value) {
        return new JAXBElement<TimeSequenceType>(_TemporalDomain_QNAME, TimeSequenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceGroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "Coverage", substitutionHeadNamespace = "http://www.opengis.net/ows/1.1", substitutionHeadName = "ReferenceGroup")
    public JAXBElement<ReferenceGroupType> createCoverage(final ReferenceGroupType value) {
        return new JAXBElement<ReferenceGroupType>(_Coverage_QNAME, ReferenceGroupType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/wcs/1.1.1", name = "GridOrigin", defaultValue = "0 0")
    public JAXBElement<List<Double>> createGridOrigin(final List<Double> value) {
        return new JAXBElement<List<Double>>(_GridOrigin_QNAME, ((Class) List.class), null, ((List<Double> ) value));
    }

}
