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
package org.geotoolkit.swe.xml.v100;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.swe._1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * @module pending
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _TimeInstantGrid_QNAME = new QName("http://www.opengis.net/swe/1.0", "TimeInstantGrid");
    private static final QName _NormalizedCurve_QNAME = new QName("http://www.opengis.net/swe/1.0", "NormalizedCurve");
    private static final QName _ConditionalValue_QNAME = new QName("http://www.opengis.net/swe/1.0", "ConditionalValue");
    private static final QName _TimeAggregate_QNAME = new QName("http://www.opengis.net/swe/1.0", "TimeAggregate");
    private static final QName _TimeGeometricComplex_QNAME = new QName("http://www.opengis.net/swe/1.0", "TimeGeometricComplex");
    private static final QName _XMLBlock_QNAME = new QName("http://www.opengis.net/swe/1.0", "XMLBlock");
    private static final QName _Position_QNAME = new QName("http://www.opengis.net/swe/1.0", "Position");
    private static final QName _ConditionalData_QNAME = new QName("http://www.opengis.net/swe/1.0", "ConditionalData");
    private static final QName _SquareMatrix_QNAME = new QName("http://www.opengis.net/swe/1.0", "SquareMatrix");
    private static final QName _MultiplexedStreamFormat_QNAME = new QName("http://www.opengis.net/swe/1.0", "MultiplexedStreamFormat");
    private static final QName _DataStreamDefinition_QNAME = new QName("http://www.opengis.net/swe/1.0", "DataStreamDefinition");
    private static final QName _AbstractDataRecord_QNAME = new QName("http://www.opengis.net/swe/1.0", "AbstractDataRecord");
    private static final QName _DataArray_QNAME = new QName("http://www.opengis.net/swe/1.0", "DataArray");
    private static final QName _DataRecord_QNAME = new QName("http://www.opengis.net/swe/1.0", "DataRecord");
    private static final QName _Vector_QNAME = new QName("http://www.opengis.net/swe/1.0", "Vector");
    private static final QName _Interval_QNAME = new QName("http://www.opengis.net/swe/1.0", "Interval");
    private static final QName _AbstractDataArray_QNAME = new QName("http://www.opengis.net/swe/1.0", "AbstractDataArray");
    private static final QName _PhenomenonSeries_QNAME = new QName("http://www.opengis.net/swe/1.0", "PhenomenonSeries");
    private static final QName _SimpleDataRecord_QNAME = new QName("http://www.opengis.net/swe/1.0", "SimpleDataRecord");
    private static final QName _Array_QNAME = new QName("http://www.opengis.net/swe/1.0", "Array");
    private static final QName _Item_QNAME = new QName("http://www.opengis.net/swe/1.0", "Item");
    private static final QName _Curve_QNAME = new QName("http://www.opengis.net/swe/1.0", "Curve");
    private static final QName _TypedValueList_QNAME = new QName("http://www.opengis.net/swe/1.0", "TypedValueList");
    private static final QName _CompoundPhenomenon_QNAME = new QName("http://www.opengis.net/swe/1.0", "CompoundPhenomenon");
    private static final QName _ConstrainedPhenomenon_QNAME = new QName("http://www.opengis.net/swe/1.0", "ConstrainedPhenomenon");
    private static final QName _Envelope_QNAME = new QName("http://www.opengis.net/swe/1.0", "Envelope");
    private static final QName _Phenomenon_QNAME = new QName("http://www.opengis.net/swe/1.0", "Phenomenon");
    private static final QName _TypedValue_QNAME = new QName("http://www.opengis.net/swe/1.0", "TypedValue");
    private static final QName _DataBlockDefinition_QNAME = new QName("http://www.opengis.net/swe/1.0", "DataBlockDefinition");
    private static final QName _Record_QNAME = new QName("http://www.opengis.net/swe/1.0", "Record");
    private static final QName _TimeIntervalGrid_QNAME = new QName("http://www.opengis.net/swe/1.0", "TimeIntervalGrid");
    private static final QName _CompositePhenomenon_QNAME = new QName("http://www.opengis.net/swe/1.0", "CompositePhenomenon");
    private static final QName _TimeGrid_QNAME = new QName("http://www.opengis.net/swe/1.0", "TimeGrid");
    private static final QName _GeoLocationArea_QNAME = new QName("http://www.opengis.net/swe/1.0", "GeoLocationArea");
    private static final QName _AllowedTokensValueList_QNAME = new QName("http://www.opengis.net/swe/1.0", "valueList");
    private static final QName _AllowedTimesInterval_QNAME = new QName("http://www.opengis.net/swe/1.0", "interval");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.swe._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BlockEncodingPropertyType }
     * 
     */
    public BlockEncodingPropertyType createBlockEncodingPropertyType() {
        return new BlockEncodingPropertyType();
    }

    /**
     * Create an instance of {@link SquareMatrixType }
     * 
     */
    public SquareMatrixType createSquareMatrixType() {
        return new SquareMatrixType();
    }

    /**
     * Create an instance of {@link AbstractEncodingType }
     * 
     */
    public AbstractEncodingType createAbstractEncodingType() {
        return new AbstractEncodingType();
    }

    /**
     * Create an instance of {@link AbstractMatrixType }
     * 
     */
    public AbstractMatrixType createAbstractMatrixType() {
        return new AbstractMatrixType();
    }

    /**
     * Create an instance of {@link NormalizedCurveType }
     * 
     */
    public NormalizedCurveType createNormalizedCurveType() {
        return new NormalizedCurveType();
    }

    /**
     * Create an instance of {@link ObservableProperty }
     * 
     */
    public ObservableProperty createObservableProperty() {
        return new ObservableProperty();
    }

    /**
     * Create an instance of {@link XMLBlockType }
     * 
     */
    public XMLBlockType createXMLBlockType() {
        return new XMLBlockType();
    }

    /**
     * Create an instance of {@link StandardFormat }
     * 
     */
    public StandardFormat createStandardFormat() {
        return new StandardFormat();
    }

    /**
     * Create an instance of {@link TextBlock }
     * 
     */
    public TextBlock createTextBlock() {
        return new TextBlock();
    }

    /**
     * Create an instance of {@link AbstractConditionalType.Condition }
     * 
     */
    public AbstractConditionalType.Condition createAbstractConditionalTypeCondition() {
        return new AbstractConditionalType.Condition();
    }

    /**
     * Create an instance of {@link TimeRangePropertyType }
     * 
     */
    public TimeRangePropertyType createTimeRangePropertyType() {
        return new TimeRangePropertyType();
    }

    /**
     * Create an instance of {@link AllowedTimesPropertyType }
     * 
     */
    public AllowedTimesPropertyType createAllowedTimesPropertyType() {
        return new AllowedTimesPropertyType();
    }

    /**
     * Create an instance of {@link AllowedValues }
     * 
     */
    public AllowedValues createAllowedValues() {
        return new AllowedValues();
    }

    /**
     * Create an instance of {@link AnyScalarPropertyType }
     * 
     */
    public AnyScalarPropertyType createAnyScalarPropertyType() {
        return new AnyScalarPropertyType();
    }

    /**
     * Create an instance of {@link AbstractConditionalType }
     * 
     */
    public AbstractConditionalType createAbstractConditionalType() {
        return new AbstractConditionalType();
    }

    /**
     * Create an instance of {@link EnvelopeType }
     * 
     */
    public EnvelopeType createEnvelopeType() {
        return new EnvelopeType();
    }

    /**
     * Create an instance of {@link CurveType }
     * 
     */
    public CurveType createCurveType() {
        return new CurveType();
    }

    /**
     * Create an instance of {@link DataArrayType }
     * 
     */
    public DataArrayType createDataArrayType() {
        return new DataArrayType();
    }

    /**
     * Create an instance of {@link CategoryPropertyType }
     * 
     */
    public CategoryPropertyType createCategoryPropertyType() {
        return new CategoryPropertyType();
    }

    /**
     * Create an instance of {@link VectorOrSquareMatrixPropertyType }
     * 
     */
    public VectorOrSquareMatrixPropertyType createVectorOrSquareMatrixPropertyType() {
        return new VectorOrSquareMatrixPropertyType();
    }

    /**
     * Create an instance of {@link Quantity }
     * 
     */
    public QuantityType createQuantity() {
        return new QuantityType();
    }

    /**
     * Create an instance of {@link BinaryBlock.Member }
     * 
     */
    public BinaryBlock.Member createBinaryBlockMember() {
        return new BinaryBlock.Member();
    }

    /**
     * Create an instance of {@link VectorType }
     * 
     */
    public VectorType createVectorType() {
        return new VectorType();
    }

    /**
     * Create an instance of {@link VectorPropertyType }
     * 
     */
    public VectorPropertyType createVectorPropertyType() {
        return new VectorPropertyType();
    }

    /**
     * Create an instance of {@link VectorType.Coordinate }
     * 
     */
    public CoordinateType createCoordinateType() {
        return new CoordinateType();
    }

    /**
     * Create an instance of {@link DataBlockDefinitionPropertyType }
     * 
     */
    public DataBlockDefinitionPropertyType createDataBlockDefinitionPropertyType() {
        return new DataBlockDefinitionPropertyType();
    }

    /**
     * Create an instance of {@link ConditionalDataType }
     * 
     */
    public ConditionalDataType createConditionalDataType() {
        return new ConditionalDataType();
    }

    /**
     * Create an instance of {@link DataStreamDefinitionType }
     * 
     */
    public DataStreamDefinitionType createDataStreamDefinitionType() {
        return new DataStreamDefinitionType();
    }

    /**
     * Create an instance of {@link CountRange }
     * 
     */
    public CountRange createCountRange() {
        return new CountRange();
    }

    /**
     * Create an instance of {@link MultiplexedStreamFormatPropertyType }
     * 
     */
    public MultiplexedStreamFormatPropertyType createMultiplexedStreamFormatPropertyType() {
        return new MultiplexedStreamFormatPropertyType();
    }

    /**
     * Create an instance of {@link AllowedTokensPropertyType }
     * 
     */
    public AllowedTokensPropertyType createAllowedTokensPropertyType() {
        return new AllowedTokensPropertyType();
    }

    /**
     * Create an instance of {@link AbstractDataArrayType.ElementCount }
     * 
     */
    public ElementCount createElementCount() {
        return new ElementCount();
    }

    /**
     * Create an instance of {@link SimpleDataRecordPropertyType }
     * 
     */
    public SimpleDataRecordPropertyType createSimpleDataRecordPropertyType() {
        return new SimpleDataRecordPropertyType();
    }

    /**
     * Create an instance of {@link UomPropertyType }
     * 
     */
    public UomPropertyType createUomPropertyType() {
        return new UomPropertyType();
    }

    /**
     * Create an instance of {@link PositionType }
     * 
     */
    public PositionType createPositionType() {
        return new PositionType();
    }

    /**
     * Create an instance of {@link ConditionalValueType }
     * 
     */
    public ConditionalValueType createConditionalValueType() {
        return new ConditionalValueType();
    }

    /**
     * Create an instance of {@link DataRecordType }
     * 
     */
    public DataRecordType createDataRecordType() {
        return new DataRecordType();
    }

    /**
     * Create an instance of {@link ConditionalValueType.Data }
     * 
     */
    public ConditionalValueType.Data createConditionalValueTypeData() {
        return new ConditionalValueType.Data();
    }

    /**
     * Create an instance of {@link MultiplexedStreamFormatType }
     * 
     */
    public MultiplexedStreamFormatType createMultiplexedStreamFormatType() {
        return new MultiplexedStreamFormatType();
    }

    /**
     * Create an instance of {@link SimpleDataRecordType }
     * 
     */
    public SimpleDataRecordType createSimpleDataRecordType() {
        return new SimpleDataRecordType();
    }

    /**
     * Create an instance of {@link Count }
     * 
     */
    public Count createCount() {
        return new Count();
    }

    /**
     * Create an instance of {@link Time }
     * 
     */
    public TimeType createTime() {
        return new TimeType();
    }

    /**
     * Create an instance of {@link Text }
     * 
     */
    public Text createText() {
        return new Text();
    }

    /**
     * Create an instance of {@link CodeSpacePropertyType }
     * 
     */
    public CodeSpacePropertyType createCodeSpacePropertyType() {
        return new CodeSpacePropertyType();
    }

    /**
     * Create an instance of {@link DataValuePropertyType }
     * 
     */
    public DataValuePropertyType createDataValuePropertyType() {
        return new DataValuePropertyType();
    }

    /**
     * Create an instance of {@link CurvePropertyType }
     * 
     */
    public CurvePropertyType createCurvePropertyType() {
        return new CurvePropertyType();
    }

    /**
     * Create an instance of {@link GeoLocationArea }
     * 
     */
    public GeoLocationArea createGeoLocationArea() {
        return new GeoLocationArea();
    }

    /**
     * Create an instance of {@link TimeRange }
     * 
     */
    public TimeRange createTimeRange() {
        return new TimeRange();
    }

    /**
     * Create an instance of {@link QuantityPropertyType }
     * 
     */
    public QuantityPropertyType createQuantityPropertyType() {
        return new QuantityPropertyType();
    }

    /**
     * Create an instance of {@link BinaryBlock.Member.Component }
     * 
     */
    public BinaryBlock.Member.Component createBinaryBlockMemberComponent() {
        return new BinaryBlock.Member.Component();
    }

    /**
     * Create an instance of {@link QuantityRange }
     * 
     */
    public QuantityRange createQuantityRange() {
        return new QuantityRange();
    }

    /**
     * Create an instance of {@link AllowedTokens }
     * 
     */
    public AllowedTokens createAllowedTokens() {
        return new AllowedTokens();
    }

    /**
     * Create an instance of {@link Boolean }
     * 
     */
    public BooleanType createBoolean() {
        return new BooleanType();
    }

    /**
     * Create an instance of {@link AllowedValuesPropertyType }
     * 
     */
    public AllowedValuesPropertyType createAllowedValuesPropertyType() {
        return new AllowedValuesPropertyType();
    }

    /**
     * Create an instance of {@link DataBlockDefinitionType }
     * 
     */
    public DataBlockDefinitionType createDataBlockDefinitionType() {
        return new DataBlockDefinitionType();
    }

    /**
     * Create an instance of {@link QualityPropertyType }
     * 
     */
    public QualityPropertyType createQualityPropertyType() {
        return new QualityPropertyType();
    }

    /**
     * Create an instance of {@link EnvelopePropertyType }
     * 
     */
    public EnvelopePropertyType createEnvelopePropertyType() {
        return new EnvelopePropertyType();
    }

    /**
     * Create an instance of {@link BinaryBlock }
     * 
     */
    public BinaryBlock createBinaryBlock() {
        return new BinaryBlock();
    }

    /**
     * Create an instance of {@link AllowedTimes }
     * 
     */
    public AllowedTimes createAllowedTimes() {
        return new AllowedTimes();
    }

    /**
     * Create an instance of {@link ConditionalDataType.Case }
     * 
     */
    public ConditionalDataType.Case createConditionalDataTypeCase() {
        return new ConditionalDataType.Case();
    }

    /**
     * Create an instance of {@link BinaryBlock.Member.Block }
     * 
     */
    public BinaryBlock.Member.Block createBinaryBlockMemberBlock() {
        return new BinaryBlock.Member.Block();
    }

    /**
     * Create an instance of {@link Category }
     * 
     */
    public Category createCategory() {
        return new Category();
    }

    /**
     * Create an instance of {@link TimePropertyType }
     * 
     */
    public TimePropertyType createTimePropertyType() {
        return new TimePropertyType();
    }

    /**
     * Create an instance of {@link DataComponentPropertyType }
     * 
     */
    public DataComponentPropertyType createDataComponentPropertyType() {
        return new DataComponentPropertyType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NormalizedCurveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "NormalizedCurve", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<NormalizedCurveType> createNormalizedCurve(NormalizedCurveType value) {
        return new JAXBElement<NormalizedCurveType>(_NormalizedCurve_QNAME, NormalizedCurveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConditionalValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "ConditionalValue", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<ConditionalValueType> createConditionalValue(ConditionalValueType value) {
        return new JAXBElement<ConditionalValueType>(_ConditionalValue_QNAME, ConditionalValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLBlockType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "XMLBlock")
    public JAXBElement<XMLBlockType> createXMLBlock(XMLBlockType value) {
        return new JAXBElement<XMLBlockType>(_XMLBlock_QNAME, XMLBlockType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PositionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "Position", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<PositionType> createPosition(PositionType value) {
        return new JAXBElement<PositionType>(_Position_QNAME, PositionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConditionalDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "ConditionalData", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<ConditionalDataType> createConditionalData(ConditionalDataType value) {
        return new JAXBElement<ConditionalDataType>(_ConditionalData_QNAME, ConditionalDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SquareMatrixType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "SquareMatrix", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0", substitutionHeadName = "AbstractDataArray")
    public JAXBElement<SquareMatrixType> createSquareMatrix(SquareMatrixType value) {
        return new JAXBElement<SquareMatrixType>(_SquareMatrix_QNAME, SquareMatrixType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiplexedStreamFormatType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "MultiplexedStreamFormat")
    public JAXBElement<MultiplexedStreamFormatType> createMultiplexedStreamFormat(MultiplexedStreamFormatType value) {
        return new JAXBElement<MultiplexedStreamFormatType>(_MultiplexedStreamFormat_QNAME, MultiplexedStreamFormatType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataStreamDefinitionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "DataStreamDefinition")
    public JAXBElement<DataStreamDefinitionType> createDataStreamDefinition(DataStreamDefinitionType value) {
        return new JAXBElement<DataStreamDefinitionType>(_DataStreamDefinition_QNAME, DataStreamDefinitionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDataRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "AbstractDataRecord")
    public JAXBElement<AbstractDataRecordType> createAbstractDataRecord(AbstractDataRecordType value) {
        return new JAXBElement<AbstractDataRecordType>(_AbstractDataRecord_QNAME, AbstractDataRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "DataArray", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0", substitutionHeadName = "AbstractDataArray")
    public JAXBElement<DataArrayType> createDataArray(DataArrayType value) {
        return new JAXBElement<DataArrayType>(_DataArray_QNAME, DataArrayType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "DataRecord", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<DataRecordType> createDataRecord(DataRecordType value) {
        return new JAXBElement<DataRecordType>(_DataRecord_QNAME, DataRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VectorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "Vector", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<VectorType> createVector(VectorType value) {
        return new JAXBElement<VectorType>(_Vector_QNAME, VectorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDataArrayType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "AbstractDataArray")
    public JAXBElement<AbstractDataArrayType> createAbstractDataArray(AbstractDataArrayType value) {
        return new JAXBElement<AbstractDataArrayType>(_AbstractDataArray_QNAME, AbstractDataArrayType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleDataRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "SimpleDataRecord", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<SimpleDataRecordType> createSimpleDataRecord(SimpleDataRecordType value) {
        return new JAXBElement<SimpleDataRecordType>(_SimpleDataRecord_QNAME, SimpleDataRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "Item")
    public JAXBElement<Object> createItem(Object value) {
        return new JAXBElement<Object>(_Item_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurveType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "Curve", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0", substitutionHeadName = "AbstractDataArray")
    public JAXBElement<CurveType> createCurve(CurveType value) {
        return new JAXBElement<CurveType>(_Curve_QNAME, CurveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "Envelope", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<EnvelopeType> createEnvelope(EnvelopeType value) {
        return new JAXBElement<EnvelopeType>(_Envelope_QNAME, EnvelopeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataBlockDefinitionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "DataBlockDefinition")
    public JAXBElement<DataBlockDefinitionType> createDataBlockDefinition(DataBlockDefinitionType value) {
        return new JAXBElement<DataBlockDefinitionType>(_DataBlockDefinition_QNAME, DataBlockDefinitionType.class, null, value);
    }

    /**
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeoLocationArea }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "GeoLocationArea", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<GeoLocationArea> createGeoLocationArea(GeoLocationArea value) {
        return new JAXBElement<GeoLocationArea>(_GeoLocationArea_QNAME, GeoLocationArea.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "valueList", scope = AllowedTokens.class)
    public JAXBElement<List<String>> createAllowedTokensValueList(List<String> value) {
        return new JAXBElement<List<String>>(_AllowedTokensValueList_QNAME, ((Class) List.class), AllowedTokens.class, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "interval", scope = AllowedTimes.class)
    public JAXBElement<List<String>> createAllowedTimesInterval(List<String> value) {
        return new JAXBElement<List<String>>(_AllowedTimesInterval_QNAME, ((Class) List.class), AllowedTimes.class, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "valueList", scope = AllowedTimes.class)
    public JAXBElement<List<String>> createAllowedTimesValueList(List<String> value) {
        return new JAXBElement<List<String>>(_AllowedTokensValueList_QNAME, ((Class) List.class), AllowedTimes.class, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "interval", scope = AllowedValues.class)
    public JAXBElement<List<Double>> createAllowedValuesInterval(List<Double> value) {
        return new JAXBElement<List<Double>>(_AllowedTimesInterval_QNAME, ((Class) List.class), AllowedValues.class, ((List<Double> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0", name = "valueList", scope = AllowedValues.class)
    public JAXBElement<List<Double>> createAllowedValuesValueList(List<Double> value) {
        return new JAXBElement<List<Double>>(_AllowedTokensValueList_QNAME, ((Class) List.class), AllowedValues.class, ((List<Double> ) value));
    }

}
