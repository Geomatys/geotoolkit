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

package org.geotoolkit.eop.xml.v201;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.v321.MultiSurfacePropertyType;
import org.geotoolkit.gml.xml.v321.SurfacePropertyType;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.geotoolkit.eop.xml.v201 package.
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

    private final static QName _EarthObservation_QNAME = new QName("http://www.opengis.net/eop/2.1", "EarthObservation");
    private final static QName _MaskInformation_QNAME = new QName("http://www.opengis.net/eop/2.1", "MaskInformation");
    private final static QName _Footprint_QNAME = new QName("http://www.opengis.net/eop/2.1", "Footprint");
    private final static QName _Mask_QNAME = new QName("http://www.opengis.net/eop/2.1", "Mask");
    private final static QName _Acquisition_QNAME = new QName("http://www.opengis.net/eop/2.1", "Acquisition");
    private final static QName _Instrument_QNAME = new QName("http://www.opengis.net/eop/2.1", "Instrument");
    private final static QName _ParameterInformation_QNAME = new QName("http://www.opengis.net/eop/2.1", "ParameterInformation");
    private final static QName _EarthObservationMetaData_QNAME = new QName("http://www.opengis.net/eop/2.1", "EarthObservationMetaData");
    private final static QName _WavelengthInformation_QNAME = new QName("http://www.opengis.net/eop/2.1", "WavelengthInformation");
    private final static QName _ProcessingInformation_QNAME = new QName("http://www.opengis.net/eop/2.1", "ProcessingInformation");
    private final static QName _ArchivingInformation_QNAME = new QName("http://www.opengis.net/eop/2.1", "ArchivingInformation");
    private final static QName _EarthObservationEquipment_QNAME = new QName("http://www.opengis.net/eop/2.1", "EarthObservationEquipment");
    private final static QName _DownlinkInformation_QNAME = new QName("http://www.opengis.net/eop/2.1", "DownlinkInformation");
    private final static QName _Platform_QNAME = new QName("http://www.opengis.net/eop/2.1", "Platform");
    private final static QName _Histogram_QNAME = new QName("http://www.opengis.net/eop/2.1", "Histogram");
    private final static QName _Sensor_QNAME = new QName("http://www.opengis.net/eop/2.1", "Sensor");
    private final static QName _SpecificInformation_QNAME = new QName("http://www.opengis.net/eop/2.1", "SpecificInformation");
    private final static QName _MaskMember_QNAME = new QName("http://www.opengis.net/eop/2.1", "maskMember");
    private final static QName _EarthObservationResult_QNAME = new QName("http://www.opengis.net/eop/2.1", "EarthObservationResult");
    private final static QName _MultiExtentOf_QNAME = new QName("http://www.opengis.net/eop/2.1", "multiExtentOf");
    private final static QName _ExtentOf_QNAME = new QName("http://www.opengis.net/eop/2.1", "extentOf");
    private final static QName _ProductInformation_QNAME = new QName("http://www.opengis.net/eop/2.1", "ProductInformation");
    private final static QName _BrowseInformation_QNAME = new QName("http://www.opengis.net/eop/2.1", "BrowseInformation");
    private final static QName _MaskFeature_QNAME = new QName("http://www.opengis.net/eop/2.1", "MaskFeature");
    private final static QName _EarthObservationTypeMetaDataProperty_QNAME = new QName("http://www.opengis.net/eop/2.1", "metaDataProperty");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.geotoolkit.eop.xml.v201
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BrowseInformationType }
     *
     */
    public BrowseInformationType createBrowseInformationType() {
        return new BrowseInformationType();
    }

    /**
     * Create an instance of {@link ProductInformationType }
     *
     */
    public ProductInformationType createProductInformationType() {
        return new ProductInformationType();
    }

    /**
     * Create an instance of {@link MaskInformationType }
     *
     */
    public MaskInformationType createMaskInformationType() {
        return new MaskInformationType();
    }

    /**
     * Create an instance of {@link FootprintType }
     *
     */
    public FootprintType createFootprintType() {
        return new FootprintType();
    }

    /**
     * Create an instance of {@link HistogramType }
     *
     */
    public HistogramType createHistogramType() {
        return new HistogramType();
    }

    /**
     * Create an instance of {@link EarthObservationResultType }
     *
     */
    public EarthObservationResultType createEarthObservationResultType() {
        return new EarthObservationResultType();
    }

    /**
     * Create an instance of {@link WavelengthInformationType }
     *
     */
    public WavelengthInformationType createWavelengthInformationType() {
        return new WavelengthInformationType();
    }

    /**
     * Create an instance of {@link PlatformType }
     *
     */
    public PlatformType createPlatformType() {
        return new PlatformType();
    }

    /**
     * Create an instance of {@link EarthObservationMetaDataType }
     *
     */
    public EarthObservationMetaDataType createEarthObservationMetaDataType() {
        return new EarthObservationMetaDataType();
    }

    /**
     * Create an instance of {@link MaskType }
     *
     */
    public MaskType createMaskType() {
        return new MaskType();
    }

    /**
     * Create an instance of {@link DownlinkInformationType }
     *
     */
    public DownlinkInformationType createDownlinkInformationType() {
        return new DownlinkInformationType();
    }

    /**
     * Create an instance of {@link AcquisitionType }
     *
     */
    public AcquisitionType createAcquisitionType() {
        return new AcquisitionType();
    }

    /**
     * Create an instance of {@link ParameterInformationType }
     *
     */
    public ParameterInformationType createParameterInformationType() {
        return new ParameterInformationType();
    }

    /**
     * Create an instance of {@link SpecificInformationType }
     *
     */
    public SpecificInformationType createSpecificInformationType() {
        return new SpecificInformationType();
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
     * Create an instance of {@link InstrumentType }
     *
     */
    public InstrumentType createInstrumentType() {
        return new InstrumentType();
    }

    /**
     * Create an instance of {@link EarthObservationEquipmentType }
     *
     */
    public EarthObservationEquipmentType createEarthObservationEquipmentType() {
        return new EarthObservationEquipmentType();
    }

    /**
     * Create an instance of {@link ProcessingInformationType }
     *
     */
    public ProcessingInformationType createProcessingInformationType() {
        return new ProcessingInformationType();
    }

    /**
     * Create an instance of {@link MaskMemberType }
     *
     */
    public MaskMemberType createMaskMemberType() {
        return new MaskMemberType();
    }

    /**
     * Create an instance of {@link MaskFeatureType }
     *
     */
    public MaskFeatureType createMaskFeatureType() {
        return new MaskFeatureType();
    }

    /**
     * Create an instance of {@link SensorType }
     *
     */
    public SensorType createSensorType() {
        return new SensorType();
    }

    /**
     * Create an instance of {@link FootprintPropertyType }
     *
     */
    public FootprintPropertyType createFootprintPropertyType() {
        return new FootprintPropertyType();
    }

    /**
     * Create an instance of {@link AcquisitionPropertyType }
     *
     */
    public AcquisitionPropertyType createAcquisitionPropertyType() {
        return new AcquisitionPropertyType();
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
     * Create an instance of {@link EarthObservationMetaDataPropertyType }
     *
     */
    public EarthObservationMetaDataPropertyType createEarthObservationMetaDataPropertyType() {
        return new EarthObservationMetaDataPropertyType();
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
     * Create an instance of {@link ArchivingInformationPropertyType }
     *
     */
    public ArchivingInformationPropertyType createArchivingInformationPropertyType() {
        return new ArchivingInformationPropertyType();
    }

    /**
     * Create an instance of {@link MaskInformationPropertyType }
     *
     */
    public MaskInformationPropertyType createMaskInformationPropertyType() {
        return new MaskInformationPropertyType();
    }

    /**
     * Create an instance of {@link HistogramPropertyType }
     *
     */
    public HistogramPropertyType createHistogramPropertyType() {
        return new HistogramPropertyType();
    }

    /**
     * Create an instance of {@link ParameterInformationPropertyType }
     *
     */
    public ParameterInformationPropertyType createParameterInformationPropertyType() {
        return new ParameterInformationPropertyType();
    }

    /**
     * Create an instance of {@link EarthObservationPropertyType }
     *
     */
    public EarthObservationPropertyType createEarthObservationPropertyType() {
        return new EarthObservationPropertyType();
    }

    /**
     * Create an instance of {@link SpecificInformationPropertyType }
     *
     */
    public SpecificInformationPropertyType createSpecificInformationPropertyType() {
        return new SpecificInformationPropertyType();
    }

    /**
     * Create an instance of {@link DownlinkInformationPropertyType }
     *
     */
    public DownlinkInformationPropertyType createDownlinkInformationPropertyType() {
        return new DownlinkInformationPropertyType();
    }

    /**
     * Create an instance of {@link ProductInformationPropertyType }
     *
     */
    public ProductInformationPropertyType createProductInformationPropertyType() {
        return new ProductInformationPropertyType();
    }

    /**
     * Create an instance of {@link WavelengthInformationPropertyType }
     *
     */
    public WavelengthInformationPropertyType createWavelengthInformationPropertyType() {
        return new WavelengthInformationPropertyType();
    }

    /**
     * Create an instance of {@link BrowseInformationPropertyType }
     *
     */
    public BrowseInformationPropertyType createBrowseInformationPropertyType() {
        return new BrowseInformationPropertyType();
    }

    /**
     * Create an instance of {@link SensorPropertyType }
     *
     */
    public SensorPropertyType createSensorPropertyType() {
        return new SensorPropertyType();
    }

    /**
     * Create an instance of {@link BrowseInformationType.FileName }
     *
     */
    public BrowseInformationType.FileName createBrowseInformationTypeFileName() {
        return new BrowseInformationType.FileName();
    }

    /**
     * Create an instance of {@link ProductInformationType.FileName }
     *
     */
    public ProductInformationType.FileName createProductInformationTypeFileName() {
        return new ProductInformationType.FileName();
    }

    /**
     * Create an instance of {@link MaskInformationType.FileName }
     *
     */
    public MaskInformationType.FileName createMaskInformationTypeFileName() {
        return new MaskInformationType.FileName();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "EarthObservation", substitutionHeadNamespace = "http://www.opengis.net/om/2.0", substitutionHeadName = "OM_Observation")
    public JAXBElement<EarthObservationType> createEarthObservation(EarthObservationType value) {
        return new JAXBElement<EarthObservationType>(_EarthObservation_QNAME, EarthObservationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MaskInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "MaskInformation", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<MaskInformationType> createMaskInformation(MaskInformationType value) {
        return new JAXBElement<MaskInformationType>(_MaskInformation_QNAME, MaskInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FootprintType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "Footprint", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractFeature")
    public JAXBElement<FootprintType> createFootprint(FootprintType value) {
        return new JAXBElement<FootprintType>(_Footprint_QNAME, FootprintType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MaskType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "Mask", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractFeature")
    public JAXBElement<MaskType> createMask(MaskType value) {
        return new JAXBElement<MaskType>(_Mask_QNAME, MaskType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AcquisitionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "Acquisition", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<AcquisitionType> createAcquisition(AcquisitionType value) {
        return new JAXBElement<AcquisitionType>(_Acquisition_QNAME, AcquisitionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InstrumentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "Instrument", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<InstrumentType> createInstrument(InstrumentType value) {
        return new JAXBElement<InstrumentType>(_Instrument_QNAME, InstrumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "ParameterInformation", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<ParameterInformationType> createParameterInformation(ParameterInformationType value) {
        return new JAXBElement<ParameterInformationType>(_ParameterInformation_QNAME, ParameterInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationMetaDataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "EarthObservationMetaData", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<EarthObservationMetaDataType> createEarthObservationMetaData(EarthObservationMetaDataType value) {
        return new JAXBElement<EarthObservationMetaDataType>(_EarthObservationMetaData_QNAME, EarthObservationMetaDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WavelengthInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "WavelengthInformation", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<WavelengthInformationType> createWavelengthInformation(WavelengthInformationType value) {
        return new JAXBElement<WavelengthInformationType>(_WavelengthInformation_QNAME, WavelengthInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessingInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "ProcessingInformation", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<ProcessingInformationType> createProcessingInformation(ProcessingInformationType value) {
        return new JAXBElement<ProcessingInformationType>(_ProcessingInformation_QNAME, ProcessingInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArchivingInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "ArchivingInformation", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<ArchivingInformationType> createArchivingInformation(ArchivingInformationType value) {
        return new JAXBElement<ArchivingInformationType>(_ArchivingInformation_QNAME, ArchivingInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationEquipmentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "EarthObservationEquipment", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractFeature")
    public JAXBElement<EarthObservationEquipmentType> createEarthObservationEquipment(EarthObservationEquipmentType value) {
        return new JAXBElement<EarthObservationEquipmentType>(_EarthObservationEquipment_QNAME, EarthObservationEquipmentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DownlinkInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "DownlinkInformation", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<DownlinkInformationType> createDownlinkInformation(DownlinkInformationType value) {
        return new JAXBElement<DownlinkInformationType>(_DownlinkInformation_QNAME, DownlinkInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PlatformType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "Platform", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<PlatformType> createPlatform(PlatformType value) {
        return new JAXBElement<PlatformType>(_Platform_QNAME, PlatformType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HistogramType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "Histogram", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<HistogramType> createHistogram(HistogramType value) {
        return new JAXBElement<HistogramType>(_Histogram_QNAME, HistogramType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SensorType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "Sensor", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<SensorType> createSensor(SensorType value) {
        return new JAXBElement<SensorType>(_Sensor_QNAME, SensorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpecificInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "SpecificInformation", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<SpecificInformationType> createSpecificInformation(SpecificInformationType value) {
        return new JAXBElement<SpecificInformationType>(_SpecificInformation_QNAME, SpecificInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MaskMemberType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "maskMember")
    public JAXBElement<MaskMemberType> createMaskMember(MaskMemberType value) {
        return new JAXBElement<MaskMemberType>(_MaskMember_QNAME, MaskMemberType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationResultType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "EarthObservationResult", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractFeature")
    public JAXBElement<EarthObservationResultType> createEarthObservationResult(EarthObservationResultType value) {
        return new JAXBElement<EarthObservationResultType>(_EarthObservationResult_QNAME, EarthObservationResultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiSurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "multiExtentOf")
    public JAXBElement<MultiSurfacePropertyType> createMultiExtentOf(MultiSurfacePropertyType value) {
        return new JAXBElement<MultiSurfacePropertyType>(_MultiExtentOf_QNAME, MultiSurfacePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SurfacePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "extentOf")
    public JAXBElement<SurfacePropertyType> createExtentOf(SurfacePropertyType value) {
        return new JAXBElement<SurfacePropertyType>(_ExtentOf_QNAME, SurfacePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProductInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "ProductInformation", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<ProductInformationType> createProductInformation(ProductInformationType value) {
        return new JAXBElement<ProductInformationType>(_ProductInformation_QNAME, ProductInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BrowseInformationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "BrowseInformation", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractObject")
    public JAXBElement<BrowseInformationType> createBrowseInformation(BrowseInformationType value) {
        return new JAXBElement<BrowseInformationType>(_BrowseInformation_QNAME, BrowseInformationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MaskFeatureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "MaskFeature", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractFeature")
    public JAXBElement<MaskFeatureType> createMaskFeature(MaskFeatureType value) {
        return new JAXBElement<MaskFeatureType>(_MaskFeature_QNAME, MaskFeatureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarthObservationMetaDataPropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/eop/2.1", name = "metaDataProperty", scope = EarthObservationType.class)
    public JAXBElement<EarthObservationMetaDataPropertyType> createEarthObservationTypeMetaDataProperty(EarthObservationMetaDataPropertyType value) {
        return new JAXBElement<EarthObservationMetaDataPropertyType>(_EarthObservationTypeMetaDataProperty_QNAME, EarthObservationMetaDataPropertyType.class, EarthObservationType.class, value);
    }

}
