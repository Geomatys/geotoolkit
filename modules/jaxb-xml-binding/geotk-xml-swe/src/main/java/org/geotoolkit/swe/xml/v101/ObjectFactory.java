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
package org.geotoolkit.swe.xml.v101;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 *
 * @version $Id:
 * @author Guilhem Legal
 * @module pending
 */
@XmlRegistry
public class ObjectFactory {
    
    private static final QName _AbstractDataRecord_QNAME    = new QName("http://www.opengis.net/swe/1.0.1", "AbstractDataRecord");
    private static final QName _SimpleDataRecord_QNAME      = new QName("http://www.opengis.net/swe/1.0.1", "SimpleDataRecord");
    private static final QName _Item_QNAME                  = new QName("http://www.opengis.net/swe/1.0.1", "Item");
    private static final QName _CompositePhenomenon_QNAME   = new QName("http://www.opengis.net/swe/1.0.1", "CompositePhenomenon");
    private static final QName _Phenomenon_QNAME            = new QName("http://www.opengis.net/swe/1.0.1", "Phenomenon");
    private static final QName _CompoundPhenomenon_QNAME    = new QName("http://www.opengis.net/swe/1.0.1", "CompoundPhenomenon");
    private static final QName _TextBlock_QNAME             = new QName("http://www.opengis.net/swe/1.0.1", "TextBlock");
    private static final QName _Encoding_QNAME              = new QName("http://www.opengis.net/swe/1.0.1", "Encoding");
    private static final QName _AbstractDataComponent_QNAME = new QName("http://www.opengis.net/swe/1.0.1", "AbstractDataComponent");
    private static final QName _Time_QNAME                  = new QName("http://www.opengis.net/swe/1.0.1", "Time");
    private static final QName _Quantity_QNAME              = new QName("http://www.opengis.net/swe/1.0.1", "Quantity");
    private static final QName _Boolean_QNAME               = new QName("http://www.opengis.net/swe/1.0.1", "Boolean");
    private static final QName _DataArray_QNAME             = new QName("http://www.opengis.net/swe/1.0.1", "DataArray");
    private static final QName _ConditionalValue_QNAME      = new QName("http://www.opengis.net/swe/1.0.1", "ConditionalValue");
    private static final QName _Envelope_QNAME              = new QName("http://www.opengis.net/swe/1.0.1", "Envelope");
    private static final QName _GeoLocationArea_QNAME       = new QName("http://www.opengis.net/swe/1.0.1", "GeoLocationArea");
    private static final QName _Vector_QNAME                = new QName("http://www.opengis.net/swe/1.0.1", "Vector");
    private static final QName _Position_QNAME              = new QName("http://www.opengis.net/swe/1.0.1", "Position");
    private static final QName _AbstractDataArray_QNAME     = new QName("http://www.opengis.net/swe/1.0.1", "AbstractDataArray");
    private static final QName _DataBlockDefinition_QNAME   = new QName("http://www.opengis.net/swe/1.0.1", "DataBlockDefinition");
    private static final QName _SquareMatrix_QNAME          = new QName("http://www.opengis.net/swe/1.0.1", "SquareMatrix");
    private static final QName _XMLBlock_QNAME              = new QName("http://www.opengis.net/swe/1.0.1", "XMLBlock");
    private static final QName _PhenomenonSeries_QNAME      = new QName("http://www.opengis.net/swe/1.0.1", "PhenomenonSeries");
    private static final QName _DataStreamDefinition_QNAME  = new QName("http://www.opengis.net/swe/1.0.1", "DataStreamDefinition");
    private static final QName _MultiplexStreamFormat_QNAME = new QName("http://www.opengis.net/swe/1.0.1", "MultiplexedStreamFormat");
    private static final QName _ConstrainedPhenomenon_QNAME = new QName("http://www.opengis.net/swe/1.0.1", "ConstrainedPhenomenon");
    private static final QName _AllowedTokenValueList_QNAME = new QName("http://www.opengis.net/swe/1.0.1", "valueList");
    private static final QName _AllowedTimesInterval_QNAME  = new QName("http://www.opengis.net/swe/1.0.1", "interval");
    private static final QName _DataRecord_QNAME            = new QName("http://www.opengis.net/swe/1.0.1", "DataRecord");
    /**
     *
     */
    public ObjectFactory() {
    }
    
    /**
     * Create an instance of {@link Boolean }
     * 
     */
    public BooleanType createBooleanType() {
        return new BooleanType();
    }

    
    /**
     * Create an instance of {@link Time }
     * 
     */
    public TimeType createTimeType() {
        return new TimeType();
    }

    /**
     * Create an instance of {@link Quantity }
     * 
     */
    public QuantityType createQuantityType() {
        return new QuantityType();
    }
    
    /**
     * Create an instance of {@link UomPropertyType }
     * 
     */
    public UomPropertyType createUomPropertyType() {
        return new UomPropertyType();
    }

    /**
     * Create an instance of {@link AbstractDataComponentType }
     * 
     */
    public AbstractDataComponentType createAbstractDataComponentType() {
        return new AbstractDataComponentType();
    }
    
    /**
     * Create an instance of {@link AbstractDataComponentType }
     * 
     */
    public AbstractDataRecordType createAbstractDataRecordType() {
        return new AbstractDataRecordType();
    }
    
    /**
     * Create an instance of {@link AbstractEncodingType }
     * 
     */
    public AbstractEncodingType createAbstractEncodingType() {
        return new AbstractEncodingType();
    }
    
    /**
     * Create an instance of {@link AnyResultType }
     * 
     */
    public AnyResultType createAnyResultType() {
        return new AnyResultType();
    }
    
    /**
     * Create an instance of {@link DataBlockDefinitionType }
     * 
     */
    public DataBlockDefinitionType createDataBlockDefinitionType() {
        return new DataBlockDefinitionType();
    }
    
    /**
     * Create an instance of {@link TextBlockType }
     * 
     */
    public TextBlockType createTextBlockType() {
        return new TextBlockType();
    }
    
     /**
     * Create an instance of {@link SimpleDataRecordType }
     * 
     */
    public SimpleDataRecordType createSimpleDataRecordType() {
        return new SimpleDataRecordType();
    }
    
    /**
     * Create an instance of {@link AnyScalarType }
     * 
     */
    public AnyScalarPropertyType createAnyScalarEntry() {
        return new AnyScalarPropertyType();
    }
    
    /**
     * Create an instance of {@link TimeGeometricPrimitivePropertyType }
     * 
     */
    public TimeGeometricPrimitivePropertyType createTimeGeometricPrimitivePropertyType() {
        return new TimeGeometricPrimitivePropertyType();
    }
     
    /**
     * Create an instance of {@link CompositePhenomenonType }
     * 
     */
    public CompositePhenomenonType createCompositePhenomenonType() {
        return new CompositePhenomenonType();
    }
    
     /**
     * Create an instance of {@link CompositePhenomenonType }
     * 
     */
    public PhenomenonType createPhenomenonType() {
        return new PhenomenonType();
    }
    
    /**
     * Create an instance of {@link DataArrayPropertyType }
     * 
     */
    public DataArrayPropertyType createDataArrayPropertyType() {
        return new DataArrayPropertyType();
    }
    
    /**
     * Create an instance of {@link DataArrayPropertyType }
     * 
     */
    public DataArrayType createDataArrayType() {
        return new DataArrayType();
    }

    /**
     * Create an instance of {@link SquareMatrixType }
     *
     */
    public SquareMatrixType createSquareMatrixType() {
        return new SquareMatrixType();
    }

    /**
     * Create an instance of {@link DataBlockDefinitionPropertyType }
     *
     */
    public DataBlockDefinitionPropertyType createDataBlockDefinitionPropertyType() {
        return new DataBlockDefinitionPropertyType();
    }

    /**
     * Create an instance of {@link ConstrainedPhenomenonType }
     *
     */
    public ConstrainedPhenomenonType createConstrainedPhenomenonType() {
        return new ConstrainedPhenomenonType();
    }

    /**
     * Create an instance of {@link AnyDataPropertyType }
     *
     */
    public AnyDataPropertyType createAnyDataPropertyType() {
        return new AnyDataPropertyType();
    }

    /**
     * Create an instance of {@link DataStreamDefinitionType }
     *
     */
    public DataStreamDefinitionType createDataStreamDefinitionType() {
        return new DataStreamDefinitionType();
    }

    /**
     * Create an instance of {@link MultiplexedStreamFormatPropertyType }
     *
     */
    public MultiplexedStreamFormatPropertyType createMultiplexedStreamFormatPropertyType() {
        return new MultiplexedStreamFormatPropertyType();
    }

    /**
     * Create an instance of {@link MultiplexedStreamFormatType }
     *
     */
    public MultiplexedStreamFormatType createMultiplexedStreamFormatType() {
        return new MultiplexedStreamFormatType();
    }
    
    /**
     * Create an instance of {@link XMLBlockType }
     *
     */
    public XMLBlockType createXMLBlockType() {
        return new XMLBlockType();
    }

    /**
     * Create an instance of {@link PhenomenonSeriesType }
     *
     */
    public PhenomenonSeriesType createPhenomenonSeriesType() {
        return new PhenomenonSeriesType();
    }
    
    /**
     * Create an instance of {@link BinaryBlock.Member }
     *
     */
    public BinaryBlock.Member createBinaryBlockMember() {
        return new BinaryBlock.Member();
    }

    /**
     * Create an instance of {@link StandardFormat }
     *
     */
    public StandardFormat createStandardFormat() {
        return new StandardFormat();
    }

    /**
     * Create an instance of {@link BinaryBlock.Member.Component }
     *
     */
    public BinaryBlock.Member.Component createBinaryBlockMemberComponent() {
        return new BinaryBlock.Member.Component();
    }

    /**
     * Create an instance of {@link BinaryBlock }
     *
     */
    public BinaryBlock createBinaryBlock() {
        return new BinaryBlock();
    }

    /**
     * Create an instance of {@link BinaryBlock.Member.Block }
     *
     */
    public BinaryBlock.Member.Block createBinaryBlockMemberBlock() {
        return new BinaryBlock.Member.Block();
    }
    
    /**
     * Create an instance of {@link BlockEncodingPropertyType }
     *
     */
    public BlockEncodingPropertyType createBlockEncodingPropertyType() {
        return new BlockEncodingPropertyType();
    }

    /**
     * Create an instance of {@link DataValuePropertyType }
     *
     */
    public DataValuePropertyType createDataValuePropertyType() {
        return new DataValuePropertyType();
    }

    /**
     * Create an instance of {@link AbstractMatrixType }
     *
     */
    public AbstractMatrixType createAbstractMatrixType() {
        return new AbstractMatrixType();
    }

    /**
     * Create an instance of {@link QuantityPropertyType }
     *
     */
    public QuantityPropertyType createQuantityPropertyType() {
        return new QuantityPropertyType();
    }

    /**
     * Create an instance of {@link DataComponentPropertyType }
     *
     */
    public DataComponentPropertyType createDataComponentPropertyType() {
        return new DataComponentPropertyType();
    }
    
    /**
     * Create an instance of {@link AbstractConditionalType.Condition }
     *
     */
    public AbstractConditionalType.Condition createAbstractConditionalTypeCondition() {
        return new AbstractConditionalType.Condition();
    }

    /**
     * Create an instance of {@link QuantityRange }
     *
     */
    public QuantityRange createQuantityRange() {
        return new QuantityRange();
    }

    /**
     * Create an instance of {@link TimeRange }
     *
     */
    public TimeRange createTimeRange() {
        return new TimeRange();
    }

    /**
     * Create an instance of {@link ConditionalValueType.Data }
     *
     */
    public ConditionalValueType.Data createConditionalValueTypeData() {
        return new ConditionalValueType.Data();
    }

    /**
     * Create an instance of {@link Text }
     *
     */
    public Text createText() {
        return new Text();
    }

    /**
     * Create an instance of {@link CountRange }
     *
     */
    public CountRange createCountRange() {
        return new CountRange();
    }

    /**
     * Create an instance of {@link Count }
     *
     */
    public Count createCount() {
        return new Count();
    }
    
    /**
     * Create an instance of {@link AbstractConditionalType }
     *
     */
    public AbstractConditionalType createAbstractConditionalType() {
        return new AbstractConditionalType();
    }

    /**
     * Create an instance of {@link ConditionalValueType }
     *
     */
    public ConditionalValueType createConditionalValueType() {
        return new ConditionalValueType();
    }

    /**
     * Create an instance of {@link EnvelopePropertyType }
     *
     */
    public EnvelopePropertyType createEnvelopePropertyType() {
        return new EnvelopePropertyType();
    }

    /**
     * Create an instance of {@link VectorType.Coordinate }
     *
     */
    public CoordinateType createCoordinateType() {
        return new CoordinateType();
    }

    /**
     * Create an instance of {@link PositionType }
     *
     */
    public PositionType createPositionType() {
        return new PositionType();
    }

    /**
     * Create an instance of {@link TimeRangePropertyType }
     *
     */
    public TimeRangePropertyType createTimeRangePropertyType() {
        return new TimeRangePropertyType();
    }

    /**
     * Create an instance of {@link VectorType }
     *
     */
    public VectorType createVectorType() {
        return new VectorType();
    }

    /**
     * Create an instance of {@link GeoLocationArea }
     *
     */
    public GeoLocationArea createGeoLocationArea() {
        return new GeoLocationArea();
    }

    /**
     * Create an instance of {@link EnvelopeType }
     *
     */
    public EnvelopeType createEnvelopeType() {
        return new EnvelopeType();
    }

     /**
     * Create an instance of {@link AbstractDataArrayType.ElementCount }
     *
     */
    public ElementCount createElementCount() {
        return new ElementCount();
    }

    /**
     * Create an instance of {@link PhenomenonPropertyType }
     *
     */
    public PhenomenonPropertyType createPhenomenonPropertyType() {
        return new PhenomenonPropertyType();
    }

    /**
     * Create an instance of {@link ObservableProperty }
     *
     */
    public ObservableProperty createObservableProperty() {
        return new ObservableProperty();
    }

    /**
     * Create an instance of {@link CodeSpacePropertyType }
     *
     */
    public CodeSpacePropertyType createCodeSpacePropertyType() {
        return new CodeSpacePropertyType();
    }

    /**
     * Create an instance of {@link Category }
     *
     */
    public Category createCategory() {
        return new Category();
    }

    /**
     * Create an instance of {@link VectorOrSquareMatrixPropertyType }
     *
     */
    public VectorOrSquareMatrixPropertyType createVectorOrSquareMatrixPropertyType() {
        return new VectorOrSquareMatrixPropertyType();
    }

    /**
     * Create an instance of {@link AllowedValuesPropertyType }
     *
     */
    public AllowedValuesPropertyType createAllowedValuesPropertyType() {
        return new AllowedValuesPropertyType();
    }

    /**
     * Create an instance of {@link AllowedTokensPropertyType }
     *
     */
    public AllowedTokensPropertyType createAllowedTokensPropertyType() {
        return new AllowedTokensPropertyType();
    }

    /**
     * Create an instance of {@link TimePropertyType }
     *
     */
    public TimePropertyType createTimePropertyType() {
        return new TimePropertyType();
    }

    /**
     * Create an instance of {@link QualityPropertyType }
     *
     */
    public QualityPropertyType createQualityPropertyType() {
        return new QualityPropertyType();
    }

    /**
     * Create an instance of {@link AllowedTimesPropertyType }
     *
     */
    public AllowedTimesPropertyType createAllowedTimesPropertyType() {
        return new AllowedTimesPropertyType();
    }

    /**
     * Create an instance of {@link AllowedTokens }
     *
     */
    public AllowedTokens createAllowedTokens() {
        return new AllowedTokens();
    }

    /**
     * Create an instance of {@link AllowedValues }
     *
     */
    public AllowedValues createAllowedValues() {
        return new AllowedValues();
    }

    /**
     * Create an instance of {@link AllowedTimes }
     *
     */
    public AllowedTimes createAllowedTimes() {
        return new AllowedTimes();
    }

    /**
     * Create an instance of {@link DataRecordType }
     *
     */
    public DataRecordType createDataRecordType() {
        return new DataRecordType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "DataRecord", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<DataRecordType> createDataRecord(final DataRecordType value) {
        return new JAXBElement<DataRecordType>(_DataRecord_QNAME, DataRecordType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConstrainedPhenomenonType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "ConstrainedPhenomenon", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "Phenomenon")
    public JAXBElement<ConstrainedPhenomenonType> createConstrainedPhenomenon(final ConstrainedPhenomenonType value) {
        return new JAXBElement<ConstrainedPhenomenonType>(_ConstrainedPhenomenon_QNAME, ConstrainedPhenomenonType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataStreamDefinitionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "DataStreamDefinition")
    public JAXBElement<DataStreamDefinitionType> createDataStreamDefinition(final DataStreamDefinitionType value) {
        return new JAXBElement<DataStreamDefinitionType>(_DataStreamDefinition_QNAME, DataStreamDefinitionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiplexedStreamFormatType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "MultiplexedStreamFormat")
    public JAXBElement<MultiplexedStreamFormatType> createMultiplexedStreamFormat(final MultiplexedStreamFormatType value) {
        return new JAXBElement<MultiplexedStreamFormatType>(_MultiplexStreamFormat_QNAME, MultiplexedStreamFormatType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PhenomenonSeriesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "PhenomenonSeries", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "CompoundPhenomenon")
    public JAXBElement<PhenomenonSeriesType> createPhenomenonSeries(final PhenomenonSeriesType value) {
        return new JAXBElement<PhenomenonSeriesType>(_PhenomenonSeries_QNAME, PhenomenonSeriesType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLBlockType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "XMLBlock")
    public JAXBElement<XMLBlockType> createXMLBlock(final XMLBlockType value) {
        return new JAXBElement<XMLBlockType>(_XMLBlock_QNAME, XMLBlockType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Envelope", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<EnvelopeType> createEnvelope(final EnvelopeType value) {
        return new JAXBElement<EnvelopeType>(_Envelope_QNAME, EnvelopeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConditionalValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "ConditionalValue", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<ConditionalValueType> createConditionalValue(final ConditionalValueType value) {
        return new JAXBElement<ConditionalValueType>(_ConditionalValue_QNAME, ConditionalValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeoLocationArea }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "GeoLocationArea", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<GeoLocationArea> createGeoLocationArea(final GeoLocationArea value) {
        return new JAXBElement<GeoLocationArea>(_GeoLocationArea_QNAME, GeoLocationArea.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VectorType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Vector", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<VectorType> createVector(final VectorType value) {
        return new JAXBElement<VectorType>(_Vector_QNAME, VectorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PositionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Position", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<PositionType> createPosition(final PositionType value) {
        return new JAXBElement<PositionType>(_Position_QNAME, PositionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SquareMatrixType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "SquareMatrix", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataArray")
    public JAXBElement<SquareMatrixType> createSquareMatrix(final SquareMatrixType value) {
        return new JAXBElement<SquareMatrixType>(_SquareMatrix_QNAME, SquareMatrixType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompoundPhenomenonType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "CompoundPhenomenon", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "Phenomenon")
    public JAXBElement<CompoundPhenomenonType> createCompoundPhenomenon(final CompoundPhenomenonType value) {
        return new JAXBElement<CompoundPhenomenonType>(_CompoundPhenomenon_QNAME, CompoundPhenomenonType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PhenomenonType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Phenomenon", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<PhenomenonType> createPhenomenon(final PhenomenonType value) {
        return new JAXBElement<PhenomenonType>(_Phenomenon_QNAME, PhenomenonType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompositePhenomenonType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "CompositePhenomenon", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "CompoundPhenomenon")
    public JAXBElement<CompositePhenomenonType> createCompositePhenomenon(final CompositePhenomenonType value) {
        return new JAXBElement<CompositePhenomenonType>(_CompositePhenomenon_QNAME, CompositePhenomenonType.class, null, value);
    }
    
    /**
     * 
     * 
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleDataRecordType }{@code >}}
     */
     
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "SimpleDataRecord", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<SimpleDataRecordType> createSimpleDataRecord(final SimpleDataRecordType value) {
        return new JAXBElement<SimpleDataRecordType>(_SimpleDataRecord_QNAME, SimpleDataRecordType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDataRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "AbstractDataRecord")
    public JAXBElement<AbstractDataRecordType> createAbstractDataRecord(final AbstractDataRecordType value) {
        return new JAXBElement<AbstractDataRecordType>(_AbstractDataRecord_QNAME, AbstractDataRecordType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Item")
    public JAXBElement<Object> createItem(final Object value) {
        return new JAXBElement<Object>(_Item_QNAME, Object.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "TextBlock", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "Encoding")
    public JAXBElement<TextBlockType> createTextBlock(final TextBlockType value) {
        return new JAXBElement<TextBlockType>(_TextBlock_QNAME, TextBlockType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Encoding")
    public JAXBElement<AbstractEncodingType> createAbstractEncoding(final AbstractEncodingType value) {
        return new JAXBElement<AbstractEncodingType>(_Encoding_QNAME, AbstractEncodingType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "AbstractDataComponent")
    public JAXBElement<AbstractDataComponentType> createAbstractDataComponent(final AbstractDataComponentType value) {
        return new JAXBElement<AbstractDataComponentType>(_AbstractDataComponent_QNAME, AbstractDataComponentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Time", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataComponent")
    public JAXBElement<TimeType> createTime(final TimeType value) {
        return new JAXBElement<TimeType>(_Time_QNAME, TimeType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Quantity", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataComponent")
    public JAXBElement<QuantityType> createQuantity(final QuantityType value) {
        return new JAXBElement<QuantityType>(_Quantity_QNAME, QuantityType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Boolean", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataComponent")
    public JAXBElement<BooleanType> createBoolean(final BooleanType value) {
        return new JAXBElement<BooleanType>(_Boolean_QNAME, BooleanType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "DataArray", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataArray")
    public JAXBElement<DataArrayType> createDataArray(final DataArrayType value) {
        return new JAXBElement<DataArrayType>(_DataArray_QNAME, DataArrayType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDataArrayType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "AbstractDataArray")
    public JAXBElement<AbstractDataArrayType> createAbstractDataArray(final AbstractDataArrayType value) {
        return new JAXBElement<AbstractDataArrayType>(_AbstractDataArray_QNAME, AbstractDataArrayType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "valueList", scope = AllowedTokens.class)
    public JAXBElement<List<String>> createAllowedTokensValueList(final List<String> value) {
        return new JAXBElement<List<String>>(_AllowedTokenValueList_QNAME, ((Class) List.class), AllowedTokens.class, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "valueList", scope = AllowedTimes.class)
    public JAXBElement<List<String>> createAllowedTimesValueList(final List<String> value) {
        return new JAXBElement<List<String>>(_AllowedTokenValueList_QNAME, ((Class) List.class), AllowedTimes.class, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "interval", scope = AllowedTimes.class)
    public JAXBElement<List<String>> createAllowedTimesInterval(final List<String> value) {
        return new JAXBElement<List<String>>(_AllowedTimesInterval_QNAME, ((Class) List.class), AllowedTimes.class, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "valueList", scope = AllowedValues.class)
    public JAXBElement<List<Double>> createAllowedValuesValueList(final List<Double> value) {
        return new JAXBElement<List<Double>>(_AllowedTokenValueList_QNAME, ((Class) List.class), AllowedValues.class, ((List<Double> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "interval", scope = AllowedValues.class)
    public JAXBElement<List<Double>> createAllowedValuesInterval(final List<Double> value) {
        return new JAXBElement<List<Double>>(_AllowedTimesInterval_QNAME, ((Class) List.class), AllowedValues.class, ((List<Double> ) value));
    }
    
    /**
     * TODO see if we need that code block
     *
     * Create an instance of {@link JAXBElement }{@code <}{@link DataBlockDefinitionType }{@code >}}
     *

    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "DataBlockDefinition")
    public JAXBElement<DataBlockDefinitionType> createDataBlockDefinition(DataBlockDefinitionType value) {
        return new JAXBElement<DataBlockDefinitionType>(_DataBlockDefinition_QNAME, DataBlockDefinitionType.class, null, value);
    }
     */

}
