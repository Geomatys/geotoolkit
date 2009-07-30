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
     * Create an instance of {@link AbstractDataComponentEntry }
     * 
     */
    public AbstractDataComponentEntry createAbstractDataComponentEntry() {
        return new AbstractDataComponentEntry();
    }
    
    /**
     * Create an instance of {@link AbstractDataComponentEntry }
     * 
     */
    public AbstractDataRecordEntry createAbstractDataRecordEntry() {
        return new AbstractDataRecordEntry();
    }
    
    /**
     * Create an instance of {@link AbstractEncodingEntry }
     * 
     */
    public AbstractEncodingEntry createAbstractEncodingEntry() {
        return new AbstractEncodingEntry();
    }
    
    /**
     * Create an instance of {@link AnyResultEntry }
     * 
     */
    public AnyResultEntry createAnyResultEntry() {
        return new AnyResultEntry();
    }
    
    /**
     * Create an instance of {@link DataBlockDefinitionEntry }
     * 
     */
    public DataBlockDefinitionEntry createDataBlockDefinitionEntry() {
        return new DataBlockDefinitionEntry();
    }
    
    /**
     * Create an instance of {@link TextBlockEntry }
     * 
     */
    public TextBlockEntry createTextBlockEntry() {
        return new TextBlockEntry();
    }
    
     /**
     * Create an instance of {@link SimpleDataRecordEntry }
     * 
     */
    public SimpleDataRecordEntry createSimpleDataRecordEntry() {
        return new SimpleDataRecordEntry();
    }
    
    /**
     * Create an instance of {@link AnyScalarEntry }
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
     * Create an instance of {@link CompositePhenomenonEntry }
     * 
     */
    public CompositePhenomenonEntry createCompositePhenomenonEntry() {
        return new CompositePhenomenonEntry();
    }
    
     /**
     * Create an instance of {@link CompositePhenomenonEntry }
     * 
     */
    public PhenomenonEntry createPhenomenonEntry() {
        return new PhenomenonEntry();
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
    public DataArrayEntry createDataArrayEntry() {
        return new DataArrayEntry();
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
    public AbstractDataArrayEntry.ElementCount createAbstractDataArrayTypeElementCount() {
        return new AbstractDataArrayEntry.ElementCount();
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
    public JAXBElement<DataRecordType> createDataRecord(DataRecordType value) {
        return new JAXBElement<DataRecordType>(_DataRecord_QNAME, DataRecordType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConstrainedPhenomenonType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "ConstrainedPhenomenon", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "Phenomenon")
    public JAXBElement<ConstrainedPhenomenonType> createConstrainedPhenomenon(ConstrainedPhenomenonType value) {
        return new JAXBElement<ConstrainedPhenomenonType>(_ConstrainedPhenomenon_QNAME, ConstrainedPhenomenonType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataStreamDefinitionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "DataStreamDefinition")
    public JAXBElement<DataStreamDefinitionType> createDataStreamDefinition(DataStreamDefinitionType value) {
        return new JAXBElement<DataStreamDefinitionType>(_DataStreamDefinition_QNAME, DataStreamDefinitionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiplexedStreamFormatType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "MultiplexedStreamFormat")
    public JAXBElement<MultiplexedStreamFormatType> createMultiplexedStreamFormat(MultiplexedStreamFormatType value) {
        return new JAXBElement<MultiplexedStreamFormatType>(_MultiplexStreamFormat_QNAME, MultiplexedStreamFormatType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PhenomenonSeriesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "PhenomenonSeries", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "CompoundPhenomenon")
    public JAXBElement<PhenomenonSeriesType> createPhenomenonSeries(PhenomenonSeriesType value) {
        return new JAXBElement<PhenomenonSeriesType>(_PhenomenonSeries_QNAME, PhenomenonSeriesType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLBlockType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "XMLBlock")
    public JAXBElement<XMLBlockType> createXMLBlock(XMLBlockType value) {
        return new JAXBElement<XMLBlockType>(_XMLBlock_QNAME, XMLBlockType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Envelope", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<EnvelopeType> createEnvelope(EnvelopeType value) {
        return new JAXBElement<EnvelopeType>(_Envelope_QNAME, EnvelopeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConditionalValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "ConditionalValue", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<ConditionalValueType> createConditionalValue(ConditionalValueType value) {
        return new JAXBElement<ConditionalValueType>(_ConditionalValue_QNAME, ConditionalValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeoLocationArea }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "GeoLocationArea", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<GeoLocationArea> createGeoLocationArea(GeoLocationArea value) {
        return new JAXBElement<GeoLocationArea>(_GeoLocationArea_QNAME, GeoLocationArea.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VectorType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Vector", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<VectorType> createVector(VectorType value) {
        return new JAXBElement<VectorType>(_Vector_QNAME, VectorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PositionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Position", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<PositionType> createPosition(PositionType value) {
        return new JAXBElement<PositionType>(_Position_QNAME, PositionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SquareMatrixType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "SquareMatrix", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataArray")
    public JAXBElement<SquareMatrixType> createSquareMatrix(SquareMatrixType value) {
        return new JAXBElement<SquareMatrixType>(_SquareMatrix_QNAME, SquareMatrixType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompoundPhenomenonType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "CompoundPhenomenon", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "Phenomenon")
    public JAXBElement<CompoundPhenomenonEntry> createCompoundPhenomenon(CompoundPhenomenonEntry value) {
        return new JAXBElement<CompoundPhenomenonEntry>(_CompoundPhenomenon_QNAME, CompoundPhenomenonEntry.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PhenomenonType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Phenomenon", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "Definition")
    public JAXBElement<PhenomenonEntry> createPhenomenon(PhenomenonEntry value) {
        return new JAXBElement<PhenomenonEntry>(_Phenomenon_QNAME, PhenomenonEntry.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CompositePhenomenonType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "CompositePhenomenon", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "CompoundPhenomenon")
    public JAXBElement<CompositePhenomenonEntry> createCompositePhenomenon(CompositePhenomenonEntry value) {
        return new JAXBElement<CompositePhenomenonEntry>(_CompositePhenomenon_QNAME, CompositePhenomenonEntry.class, null, value);
    }
    
    /**
     * 
     * 
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleDataRecordType }{@code >}}
     */
     
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "SimpleDataRecord", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataRecord")
    public JAXBElement<SimpleDataRecordEntry> createSimpleDataRecord(SimpleDataRecordEntry value) {
        return new JAXBElement<SimpleDataRecordEntry>(_SimpleDataRecord_QNAME, SimpleDataRecordEntry.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDataRecordType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "AbstractDataRecord")
    public JAXBElement<AbstractDataRecordEntry> createAbstractDataRecord(AbstractDataRecordEntry value) {
        return new JAXBElement<AbstractDataRecordEntry>(_AbstractDataRecord_QNAME, AbstractDataRecordEntry.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Item")
    public JAXBElement<Object> createItem(Object value) {
        return new JAXBElement<Object>(_Item_QNAME, Object.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "TextBlock", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "Encoding")
    public JAXBElement<TextBlockEntry> createTextBlock(TextBlockEntry value) {
        return new JAXBElement<TextBlockEntry>(_TextBlock_QNAME, TextBlockEntry.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Encoding")
    public JAXBElement<AbstractEncodingEntry> createAbstractEncoding(AbstractEncodingEntry value) {
        return new JAXBElement<AbstractEncodingEntry>(_Encoding_QNAME, AbstractEncodingEntry.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "AbstractDataComponent")
    public JAXBElement<AbstractDataComponentEntry> createAbstractDataComponent(AbstractDataComponentEntry value) {
        return new JAXBElement<AbstractDataComponentEntry>(_AbstractDataComponent_QNAME, AbstractDataComponentEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Time", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataComponent")
    public JAXBElement<TimeType> createTime(TimeType value) {
        return new JAXBElement<TimeType>(_Time_QNAME, TimeType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Quantity", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataComponent")
    public JAXBElement<QuantityType> createQuantity(QuantityType value) {
        return new JAXBElement<QuantityType>(_Quantity_QNAME, QuantityType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "Boolean", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataComponent")
    public JAXBElement<BooleanType> createBoolean(BooleanType value) {
        return new JAXBElement<BooleanType>(_Boolean_QNAME, BooleanType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "DataArray", substitutionHeadNamespace = "http://www.opengis.net/swe/1.0.1", substitutionHeadName = "AbstractDataArray")
    public JAXBElement<DataArrayEntry> createDataArray(DataArrayEntry value) {
        return new JAXBElement<DataArrayEntry>(_DataArray_QNAME, DataArrayEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDataArrayType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "AbstractDataArray")
    public JAXBElement<AbstractDataArrayEntry> createAbstractDataArray(AbstractDataArrayEntry value) {
        return new JAXBElement<AbstractDataArrayEntry>(_AbstractDataArray_QNAME, AbstractDataArrayEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "valueList", scope = AllowedTokens.class)
    public JAXBElement<List<String>> createAllowedTokensValueList(List<String> value) {
        return new JAXBElement<List<String>>(_AllowedTokenValueList_QNAME, ((Class) List.class), AllowedTokens.class, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "valueList", scope = AllowedTimes.class)
    public JAXBElement<List<String>> createAllowedTimesValueList(List<String> value) {
        return new JAXBElement<List<String>>(_AllowedTokenValueList_QNAME, ((Class) List.class), AllowedTimes.class, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "interval", scope = AllowedTimes.class)
    public JAXBElement<List<String>> createAllowedTimesInterval(List<String> value) {
        return new JAXBElement<List<String>>(_AllowedTimesInterval_QNAME, ((Class) List.class), AllowedTimes.class, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "valueList", scope = AllowedValues.class)
    public JAXBElement<List<Double>> createAllowedValuesValueList(List<Double> value) {
        return new JAXBElement<List<Double>>(_AllowedTokenValueList_QNAME, ((Class) List.class), AllowedValues.class, ((List<Double> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "interval", scope = AllowedValues.class)
    public JAXBElement<List<Double>> createAllowedValuesInterval(List<Double> value) {
        return new JAXBElement<List<Double>>(_AllowedTimesInterval_QNAME, ((Class) List.class), AllowedValues.class, ((List<Double> ) value));
    }
    
    /**
     * TODO see if we need that code block
     *
     * Create an instance of {@link JAXBElement }{@code <}{@link DataBlockDefinitionType }{@code >}}
     *

    @XmlElementDecl(namespace = "http://www.opengis.net/swe/1.0.1", name = "DataBlockDefinition")
    public JAXBElement<DataBlockDefinitionEntry> createDataBlockDefinition(DataBlockDefinitionEntry value) {
        return new JAXBElement<DataBlockDefinitionEntry>(_DataBlockDefinition_QNAME, DataBlockDefinitionEntry.class, null, value);
    }
     */

}
