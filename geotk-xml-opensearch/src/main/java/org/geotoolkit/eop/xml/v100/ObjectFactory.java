/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019
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

package org.geotoolkit.eop.xml.v100;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.v311.CodeListType;
import org.geotoolkit.gml.xml.v311.DirectedObservationAtDistanceType;
import org.geotoolkit.gml.xml.v311.DirectedObservationType;
import org.geotoolkit.gml.xml.v311.MeasureType;
import org.geotoolkit.gml.xml.v311.ObservationType;
import org.geotoolkit.gml.xml.v311.TargetPropertyType;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.geotoolkit.eop.xml.v100 package.
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

    private final static QName _DirectedObservation_QNAME = new QName("http://www.opengis.net/gml", "DirectedObservation");
    private final static QName _DirectedObservationAtDistance_QNAME = new QName("http://www.opengis.net/gml", "DirectedObservationAtDistance");
    private final static QName _Observation_QNAME = new QName("http://www.opengis.net/gml", "Observation");
    private final static QName _Target_QNAME = new QName("http://www.opengis.net/gml", "target");

    /**
     * Create an instance of {@link ObservationType }
     *
     */
    public ObservationType createObservationType() {
        return new ObservationType();
    }

    /*
     * Create an instance of {@link DirectedObservationAtDistanceType }
     *
     */
    public DirectedObservationAtDistanceType createDirectedObservationAtDistanceType() {
        return new DirectedObservationAtDistanceType();
    }

    /**
     * Create an instance of {@link DirectedObservationType }
     *
     */
    public DirectedObservationType createDirectedObservationType() {
        return new DirectedObservationType();
    }

    /**
     * Create an instance of {@link TargetPropertyType }
     *
     */
    public TargetPropertyType createTargetPropertyType() {
        return new TargetPropertyType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObservationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "Observation", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<ObservationType> createObservation(ObservationType value) {
        return new JAXBElement<ObservationType>(_Observation_QNAME, ObservationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectedObservationAtDistanceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "DirectedObservationAtDistance", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "DirectedObservation")
    public JAXBElement<DirectedObservationAtDistanceType> createDirectedObservationAtDistance(DirectedObservationAtDistanceType value) {
        return new JAXBElement<DirectedObservationAtDistanceType>(_DirectedObservationAtDistance_QNAME, DirectedObservationAtDistanceType.class, null, value);
    }

     /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DirectedObservationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "DirectedObservation", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Observation")
    public JAXBElement<DirectedObservationType> createDirectedObservation(DirectedObservationType value) {
        return new JAXBElement<DirectedObservationType>(_DirectedObservation_QNAME, DirectedObservationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TargetPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gml", name = "target")
    public JAXBElement<TargetPropertyType> createTarget(TargetPropertyType value) {
        return new JAXBElement<TargetPropertyType>(_Target_QNAME, TargetPropertyType.class, null, value);
    }


    private final static QName _StatusDetail_QNAME = new QName("http://earth.esa.int/eop", "statusDetail");
    private final static QName _Sensor_QNAME = new QName("http://earth.esa.int/eop", "Sensor");
    private final static QName _SpecificInformation_QNAME = new QName("http://earth.esa.int/eop", "SpecificInformation");
    private final static QName _EarthObservationResult_QNAME = new QName("http://earth.esa.int/eop", "EarthObservationResult");
    private final static QName _Acquisition_QNAME = new QName("http://earth.esa.int/eop", "Acquisition");
    private final static QName _Histograms_QNAME = new QName("http://earth.esa.int/eop", "histograms");
    private final static QName _Instrument_QNAME = new QName("http://earth.esa.int/eop", "Instrument");
    private final static QName _BrowseInformation_QNAME = new QName("http://earth.esa.int/eop", "BrowseInformation");
    private final static QName _ProductInformation_QNAME = new QName("http://earth.esa.int/eop", "ProductInformation");
    private final static QName _MaskFeature_QNAME = new QName("http://earth.esa.int/eop", "MaskFeature");
    private final static QName _EarthObservationMetaData_QNAME = new QName("http://earth.esa.int/eop", "EarthObservationMetaData");
    private final static QName _ImageQualityDegradationQuotationMode_QNAME = new QName("http://earth.esa.int/eop", "imageQualityDegradationQuotationMode");
    private final static QName _Doi_QNAME = new QName("http://earth.esa.int/eop", "doi");
    private final static QName _Status_QNAME = new QName("http://earth.esa.int/eop", "status");
    private final static QName _EarthObservation_QNAME = new QName("http://earth.esa.int/eop", "EarthObservation");
    private final static QName _SubsetOf_QNAME = new QName("http://earth.esa.int/eop", "subsetOf");
    private final static QName _ParentIdentifier_QNAME = new QName("http://earth.esa.int/eop", "parentIdentifier");
    private final static QName _MaskMembers_QNAME = new QName("http://earth.esa.int/eop", "maskMembers");
    private final static QName _MaskInformation_QNAME = new QName("http://earth.esa.int/eop", "MaskInformation");
    private final static QName _LinkedWith_QNAME = new QName("http://earth.esa.int/eop", "linkedWith");
    private final static QName _ArchivingInformation_QNAME = new QName("http://earth.esa.int/eop", "ArchivingInformation");
    private final static QName _ProcessingInformation_QNAME = new QName("http://earth.esa.int/eop", "ProcessingInformation");
    private final static QName _ProductType_QNAME = new QName("http://earth.esa.int/eop", "productType");
    private final static QName _EarthObservationEquipment_QNAME = new QName("http://earth.esa.int/eop", "EarthObservationEquipment");
    private final static QName _AcquisitionType_QNAME = new QName("http://earth.esa.int/eop", "acquisitionType");
    private final static QName _Footprint_QNAME = new QName("http://earth.esa.int/eop", "Footprint");
    private final static QName _Platform_QNAME = new QName("http://earth.esa.int/eop", "Platform");
    private final static QName _Histogram_QNAME = new QName("http://earth.esa.int/eop", "Histogram");
    private final static QName _AcquisitionSubType_QNAME = new QName("http://earth.esa.int/eop", "acquisitionSubType");
    private final static QName _ComposedOf_QNAME = new QName("http://earth.esa.int/eop", "composedOf");
    private final static QName _DownlinkInformation_QNAME = new QName("http://earth.esa.int/eop", "DownlinkInformation");
    private final static QName _Mask_QNAME = new QName("http://earth.esa.int/eop", "Mask");
    private final static QName _Identifier_QNAME = new QName("http://earth.esa.int/eop", "identifier");
    private final static QName _ImageQualityDegradation_QNAME = new QName("http://earth.esa.int/eop", "imageQualityDegradation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.geotoolkit.eop.xml.v100
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AcquisitionType }
     *
     */
    public AcquisitionType createAcquisitionType() {
        return new AcquisitionType();
    }

    /**
     * Create an instance of {@link HistogramType }
     *
     */
    public HistogramType createHistogramType() {
        return new HistogramType();
    }

    /**
     * Create an instance of {@link PlatformType }
     *
     */
    public PlatformType createPlatformType() {
        return new PlatformType();
    }

    /**
     * Create an instance of {@link EarthObservationResultType }
     *
     */
    public EarthObservationResultType createEarthObservationResultType() {
        return new EarthObservationResultType();
    }

    /**
     * Create an instance of {@link EarthObservationPropertyType }
     *
     */
    public EarthObservationPropertyType createEarthObservationPropertyType() {
        return new EarthObservationPropertyType();
    }

    /**
     * Create an instance of {@link BrowseInformationType }
     *
     */
    public BrowseInformationType createBrowseInformationType() {
        return new BrowseInformationType();
    }

    /**
     * Create an instance of {@link EarthObservationType }
     *
     */
    public EarthObservationType createEarthObservationType() {
        return new EarthObservationType();
    }

    /**
     * Create an instance of {@link ArchivingInformationType }
     *
     */
    public ArchivingInformationType createArchivingInformationType() {
        return new ArchivingInformationType();
    }

    /**
     * Create an instance of {@link EarthObservationEquipmentType }
     *
     */
    public EarthObservationEquipmentType createEarthObservationEquipmentType() {
        return new EarthObservationEquipmentType();
    }

    /**
     * Create an instance of {@link MaskMembersPropertyType }
     *
     */
    public MaskMembersPropertyType createMaskMembersPropertyType() {
        return new MaskMembersPropertyType();
    }

    /**
     * Create an instance of {@link MaskFeatureType }
     *
     */
    public MaskFeatureType createMaskFeatureType() {
        return new MaskFeatureType();
    }

    /**
     * Create an instance of {@link FootprintType }
     *
     */
    public FootprintType createFootprintType() {
        return new FootprintType();
    }

    /**
     * Create an instance of {@link HistogramArrayPropertyType }
     *
     */
    public HistogramArrayPropertyType createHistogramArrayPropertyType() {
        return new HistogramArrayPropertyType();
    }

    /**
     * Create an instance of {@link EarthObservationMetaDataType }
     *
     */
    public EarthObservationMetaDataType createEarthObservationMetaDataType() {
        return new EarthObservationMetaDataType();
    }

    /**
     * Create an instance of {@link MaskInformationType }
     *
     */
    public MaskInformationType createMaskInformationType() {
        return new MaskInformationType();
    }

    /**
     * Create an instance of {@link MaskType }
     *
     */
    public MaskType createMaskType() {
        return new MaskType();
    }

    /**
     * Create an instance of {@link ProductInformationType }
     *
     */
    public ProductInformationType createProductInformationType() {
        return new ProductInformationType();
    }

    /**
     * Create an instance of {@link DownlinkInformationType }
     *
     */
    public DownlinkInformationType createDownlinkInformationType() {
        return new DownlinkInformationType();
    }

    /**
     * Create an instance of {@link SpecificInformationType }
     *
     */
    public SpecificInformationType createSpecificInformationType() {
        return new SpecificInformationType();
    }

    /**
     * Create an instance of {@link InstrumentType }
     *
     */
    public InstrumentType createInstrumentType() {
        return new InstrumentType();
    }

    /**
     * Create an instance of {@link ProcessingInformationType }
     *
     */
    public ProcessingInformationType createProcessingInformationType() {
        return new ProcessingInformationType();
    }

    /**
     * Create an instance of {@link SensorType }
     *
     */
    public SensorType createSensorType() {
        return new SensorType();
    }

    /**
     * Create an instance of {@link SpecificInformationArrayPropertyType }
     *
     */
    public SpecificInformationArrayPropertyType createSpecificInformationArrayPropertyType() {
        return new SpecificInformationArrayPropertyType();
    }

    /**
     * Create an instance of {@link AcquisitionPropertyType }
     *
     */
    public AcquisitionPropertyType createAcquisitionPropertyType() {
        return new AcquisitionPropertyType();
    }

    /**
     * Create an instance of {@link BrowseInformationArrayPropertyType }
     *
     */
    public BrowseInformationArrayPropertyType createBrowseInformationArrayPropertyType() {
        return new BrowseInformationArrayPropertyType();
    }

    /**
     * Create an instance of {@link EarthObservationResultPropertyType }
     *
     */
    public EarthObservationResultPropertyType createEarthObservationResultPropertyType() {
        return new EarthObservationResultPropertyType();
    }

    /**
     * Create an instance of {@link ProcessingInformationPropertyType }
     *
     */
    public ProcessingInformationPropertyType createProcessingInformationPropertyType() {
        return new ProcessingInformationPropertyType();
    }

    /**
     * Create an instance of {@link ProductInformationArrayPropertyType }
     *
     */
    public ProductInformationArrayPropertyType createProductInformationArrayPropertyType() {
        return new ProductInformationArrayPropertyType();
    }

    /**
     * Create an instance of {@link ArchivingInformationArrayPropertyType }
     *
     */
    public ArchivingInformationArrayPropertyType createArchivingInformationArrayPropertyType() {
        return new ArchivingInformationArrayPropertyType();
    }

    /**
     * Create an instance of {@link DownlinkInformationArrayPropertyType }
     *
     */
    public DownlinkInformationArrayPropertyType createDownlinkInformationArrayPropertyType() {
        return new DownlinkInformationArrayPropertyType();
    }

    /**
     * Create an instance of {@link CodeWithAuthorityType }
     *
     */
    public CodeWithAuthorityType createCodeWithAuthorityType() {
        return new CodeWithAuthorityType();
    }

    /**
     * Create an instance of {@link MaskInformationArrayPropertyType }
     *
     */
    public MaskInformationArrayPropertyType createMaskInformationArrayPropertyType() {
        return new MaskInformationArrayPropertyType();
    }

    /**
     * Create an instance of {@link PlatformPropertyType }
     *
     */
    public PlatformPropertyType createPlatformPropertyType() {
        return new PlatformPropertyType();
    }

    /**
     * Create an instance of {@link InstrumentPropertyType }
     *
     */
    public InstrumentPropertyType createInstrumentPropertyType() {
        return new InstrumentPropertyType();
    }

    /**
     * Create an instance of {@link EarthObservationEquipmentPropertyType }
     *
     */
    public EarthObservationEquipmentPropertyType createEarthObservationEquipmentPropertyType() {
        return new EarthObservationEquipmentPropertyType();
    }

    /**
     * Create an instance of {@link SensorPropertyType }
     *
     */
    public SensorPropertyType createSensorPropertyType() {
        return new SensorPropertyType();
    }

    /**
     * Create an instance of {@link AcquisitionType.WrsLongitudeGrid }
     *
     */
    public AcquisitionType.WrsLongitudeGrid createAcquisitionTypeWrsLongitudeGrid() {
        return new AcquisitionType.WrsLongitudeGrid();
    }

    /**
     * Create an instance of {@link AcquisitionType.WrsLatitudeGrid }
     *
     */
    public AcquisitionType.WrsLatitudeGrid createAcquisitionTypeWrsLatitudeGrid() {
        return new AcquisitionType.WrsLatitudeGrid();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "statusDetail")
    public JAXBElement<String> createStatusDetail(String value) {
        return new JAXBElement<String>(_StatusDetail_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SensorType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "Sensor")
    public JAXBElement<SensorType> createSensor(SensorType value) {
        return new JAXBElement<SensorType>(_Sensor_QNAME, SensorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpecificInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "SpecificInformation")
    public JAXBElement<SpecificInformationType> createSpecificInformation(SpecificInformationType value) {
        return new JAXBElement<SpecificInformationType>(_SpecificInformation_QNAME, SpecificInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationResultType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "EarthObservationResult", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<EarthObservationResultType> createEarthObservationResult(EarthObservationResultType value) {
        return new JAXBElement<EarthObservationResultType>(_EarthObservationResult_QNAME, EarthObservationResultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AcquisitionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "Acquisition")
    public JAXBElement<AcquisitionType> createAcquisition(AcquisitionType value) {
        return new JAXBElement<AcquisitionType>(_Acquisition_QNAME, AcquisitionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HistogramArrayPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "histograms")
    public JAXBElement<HistogramArrayPropertyType> createHistograms(HistogramArrayPropertyType value) {
        return new JAXBElement<HistogramArrayPropertyType>(_Histograms_QNAME, HistogramArrayPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InstrumentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "Instrument")
    public JAXBElement<InstrumentType> createInstrument(InstrumentType value) {
        return new JAXBElement<InstrumentType>(_Instrument_QNAME, InstrumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BrowseInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "BrowseInformation")
    public JAXBElement<BrowseInformationType> createBrowseInformation(BrowseInformationType value) {
        return new JAXBElement<BrowseInformationType>(_BrowseInformation_QNAME, BrowseInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProductInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "ProductInformation")
    public JAXBElement<ProductInformationType> createProductInformation(ProductInformationType value) {
        return new JAXBElement<ProductInformationType>(_ProductInformation_QNAME, ProductInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MaskFeatureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "MaskFeature", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<MaskFeatureType> createMaskFeature(MaskFeatureType value) {
        return new JAXBElement<MaskFeatureType>(_MaskFeature_QNAME, MaskFeatureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationMetaDataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "EarthObservationMetaData", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "_MetaData")
    public JAXBElement<EarthObservationMetaDataType> createEarthObservationMetaData(EarthObservationMetaDataType value) {
        return new JAXBElement<EarthObservationMetaDataType>(_EarthObservationMetaData_QNAME, EarthObservationMetaDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "imageQualityDegradationQuotationMode")
    public JAXBElement<String> createImageQualityDegradationQuotationMode(String value) {
        return new JAXBElement<String>(_ImageQualityDegradationQuotationMode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "doi")
    public JAXBElement<String> createDoi(String value) {
        return new JAXBElement<String>(_Doi_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "status")
    public JAXBElement<String> createStatus(String value) {
        return new JAXBElement<String>(_Status_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "EarthObservation", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Observation")
    public JAXBElement<EarthObservationType> createEarthObservation(EarthObservationType value) {
        return new JAXBElement<EarthObservationType>(_EarthObservation_QNAME, EarthObservationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "subsetOf")
    public JAXBElement<EarthObservationPropertyType> createSubsetOf(EarthObservationPropertyType value) {
        return new JAXBElement<EarthObservationPropertyType>(_SubsetOf_QNAME, EarthObservationPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "parentIdentifier")
    public JAXBElement<String> createParentIdentifier(String value) {
        return new JAXBElement<String>(_ParentIdentifier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MaskMembersPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "maskMembers")
    public JAXBElement<MaskMembersPropertyType> createMaskMembers(MaskMembersPropertyType value) {
        return new JAXBElement<MaskMembersPropertyType>(_MaskMembers_QNAME, MaskMembersPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MaskInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "MaskInformation")
    public JAXBElement<MaskInformationType> createMaskInformation(MaskInformationType value) {
        return new JAXBElement<MaskInformationType>(_MaskInformation_QNAME, MaskInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "linkedWith")
    public JAXBElement<EarthObservationPropertyType> createLinkedWith(EarthObservationPropertyType value) {
        return new JAXBElement<EarthObservationPropertyType>(_LinkedWith_QNAME, EarthObservationPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArchivingInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "ArchivingInformation")
    public JAXBElement<ArchivingInformationType> createArchivingInformation(ArchivingInformationType value) {
        return new JAXBElement<ArchivingInformationType>(_ArchivingInformation_QNAME, ArchivingInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessingInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "ProcessingInformation")
    public JAXBElement<ProcessingInformationType> createProcessingInformation(ProcessingInformationType value) {
        return new JAXBElement<ProcessingInformationType>(_ProcessingInformation_QNAME, ProcessingInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "productType")
    public JAXBElement<String> createProductType(String value) {
        return new JAXBElement<String>(_ProductType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationEquipmentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "EarthObservationEquipment", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<EarthObservationEquipmentType> createEarthObservationEquipment(EarthObservationEquipmentType value) {
        return new JAXBElement<EarthObservationEquipmentType>(_EarthObservationEquipment_QNAME, EarthObservationEquipmentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "acquisitionType")
    public JAXBElement<String> createAcquisitionType(String value) {
        return new JAXBElement<String>(_AcquisitionType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FootprintType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "Footprint", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<FootprintType> createFootprint(FootprintType value) {
        return new JAXBElement<FootprintType>(_Footprint_QNAME, FootprintType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PlatformType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "Platform")
    public JAXBElement<PlatformType> createPlatform(PlatformType value) {
        return new JAXBElement<PlatformType>(_Platform_QNAME, PlatformType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HistogramType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "Histogram", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractObject")
    public JAXBElement<HistogramType> createHistogram(HistogramType value) {
        return new JAXBElement<HistogramType>(_Histogram_QNAME, HistogramType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeListType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "acquisitionSubType")
    public JAXBElement<CodeListType> createAcquisitionSubType(CodeListType value) {
        return new JAXBElement<CodeListType>(_AcquisitionSubType_QNAME, CodeListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "composedOf")
    public JAXBElement<EarthObservationPropertyType> createComposedOf(EarthObservationPropertyType value) {
        return new JAXBElement<EarthObservationPropertyType>(_ComposedOf_QNAME, EarthObservationPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DownlinkInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "DownlinkInformation")
    public JAXBElement<DownlinkInformationType> createDownlinkInformation(DownlinkInformationType value) {
        return new JAXBElement<DownlinkInformationType>(_DownlinkInformation_QNAME, DownlinkInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MaskType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "Mask", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<MaskType> createMask(MaskType value) {
        return new JAXBElement<MaskType>(_Mask_QNAME, MaskType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "identifier")
    public JAXBElement<String> createIdentifier(String value) {
        return new JAXBElement<String>(_Identifier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeasureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://earth.esa.int/eop", name = "imageQualityDegradation")
    public JAXBElement<MeasureType> createImageQualityDegradation(MeasureType value) {
        return new JAXBElement<MeasureType>(_ImageQualityDegradation_QNAME, MeasureType.class, null, value);
    }

}
