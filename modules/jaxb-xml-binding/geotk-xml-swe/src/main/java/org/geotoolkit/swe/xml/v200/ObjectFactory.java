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

package org.geotoolkit.swe.xml.v200;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.swe._2 package.
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

    private final static QName _AbstractEncoding_QNAME = new QName("http://www.opengis.net/swe/2.0", "AbstractEncoding");
    private final static QName _AbstractDataComponent_QNAME = new QName("http://www.opengis.net/swe/2.0", "AbstractDataComponent");
    private final static QName _BinaryEncoding_QNAME = new QName("http://www.opengis.net/swe/2.0", "BinaryEncoding");
    private final static QName _AbstractSWEIdentifiable_QNAME = new QName("http://www.opengis.net/swe/2.0", "AbstractSWEIdentifiable");
    private final static QName _Count_QNAME = new QName("http://www.opengis.net/swe/2.0", "Count");
    private final static QName _CategoryRange_QNAME = new QName("http://www.opengis.net/swe/2.0", "CategoryRange");
    private final static QName _AbstractSWE_QNAME = new QName("http://www.opengis.net/swe/2.0", "AbstractSWE");
    private final static QName _Category_QNAME = new QName("http://www.opengis.net/swe/2.0", "Category");
    private final static QName _TextEncoding_QNAME = new QName("http://www.opengis.net/swe/2.0", "TextEncoding");
    private final static QName _Vector_QNAME = new QName("http://www.opengis.net/swe/2.0", "Vector");
    private final static QName _QuantityRange_QNAME = new QName("http://www.opengis.net/swe/2.0", "QuantityRange");
    private final static QName _NilValues_QNAME = new QName("http://www.opengis.net/swe/2.0", "NilValues");
    private final static QName _DataArray_QNAME = new QName("http://www.opengis.net/swe/2.0", "DataArray");
    private final static QName _Matrix_QNAME = new QName("http://www.opengis.net/swe/2.0", "Matrix");
    private final static QName _DataStream_QNAME = new QName("http://www.opengis.net/swe/2.0", "DataStream");
    private final static QName _AllowedTokens_QNAME = new QName("http://www.opengis.net/swe/2.0", "AllowedTokens");
    private final static QName _CountRange_QNAME = new QName("http://www.opengis.net/swe/2.0", "CountRange");
    private final static QName _Boolean_QNAME = new QName("http://www.opengis.net/swe/2.0", "Boolean");
    private final static QName _TimeRange_QNAME = new QName("http://www.opengis.net/swe/2.0", "TimeRange");
    private final static QName _Time_QNAME = new QName("http://www.opengis.net/swe/2.0", "Time");
    private final static QName _Component_QNAME = new QName("http://www.opengis.net/swe/2.0", "Component");
    private final static QName _Text_QNAME = new QName("http://www.opengis.net/swe/2.0", "Text");
    private final static QName _Quantity_QNAME = new QName("http://www.opengis.net/swe/2.0", "Quantity");
    private final static QName _Block_QNAME = new QName("http://www.opengis.net/swe/2.0", "Block");
    private final static QName _DataChoice_QNAME = new QName("http://www.opengis.net/swe/2.0", "DataChoice");
    private final static QName _AllowedValues_QNAME = new QName("http://www.opengis.net/swe/2.0", "AllowedValues");
    private final static QName _DataRecord_QNAME = new QName("http://www.opengis.net/swe/2.0", "DataRecord");
    private final static QName _AbstractSimpleComponent_QNAME = new QName("http://www.opengis.net/swe/2.0", "AbstractSimpleComponent");
    private final static QName _AllowedTimes_QNAME = new QName("http://www.opengis.net/swe/2.0", "AllowedTimes");
    private final static QName _XMLEncoding_QNAME = new QName("http://www.opengis.net/swe/2.0", "XMLEncoding");
    private final static QName _AllowedTimesTypeValue_QNAME = new QName("http://www.opengis.net/swe/2.0", "value");
    private final static QName _AllowedTimesTypeInterval_QNAME = new QName("http://www.opengis.net/swe/2.0", "interval");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.swe._2
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link QuantityPropertyType }
     *
     */
    public QuantityPropertyType createQuantityPropertyType() {
        return new QuantityPropertyType();
    }

    /**
     * Create an instance of {@link AllowedTokensPropertyType }
     *
     */
    public AllowedTokensPropertyType createAllowedTokensPropertyType() {
        return new AllowedTokensPropertyType();
    }

    /**
     * Create an instance of {@link DataChoiceType }
     *
     */
    public DataChoiceType createDataChoiceType() {
        return new DataChoiceType();
    }

    /**
     * Create an instance of {@link BooleanPropertyType }
     *
     */
    public BooleanPropertyType createBooleanPropertyType() {
        return new BooleanPropertyType();
    }

    /**
     * Create an instance of {@link TimePropertyType }
     *
     */
    public TimePropertyType createTimePropertyType() {
        return new TimePropertyType();
    }

    /**
     * Create an instance of {@link NilValuesPropertyType }
     *
     */
    public NilValuesPropertyType createNilValuesPropertyType() {
        return new NilValuesPropertyType();
    }

    /**
     * Create an instance of {@link Reference }
     *
     */
    public Reference createReference() {
        return new Reference();
    }

    /**
     * Create an instance of {@link CountPropertyType }
     *
     */
    public CountPropertyType createCountPropertyType() {
        return new CountPropertyType();
    }

    /**
     * Create an instance of {@link AnyRangePropertyType }
     *
     */
    public AnyRangePropertyType createAnyRangePropertyType() {
        return new AnyRangePropertyType();
    }

    /**
     * Create an instance of {@link TextPropertyType }
     *
     */
    public TextPropertyType createTextPropertyType() {
        return new TextPropertyType();
    }

    /**
     * Create an instance of {@link CountType }
     *
     */
    public CountType createCountType() {
        return new CountType();
    }

    /**
     * Create an instance of {@link QuantityType }
     *
     */
    public QuantityType createQuantityType() {
        return new QuantityType();
    }

    /**
     * Create an instance of {@link DataStreamType.ElementCount }
     *
     */
    public DataStreamType.ElementCount createDataStreamTypeElementCount() {
        return new DataStreamType.ElementCount();
    }

    /**
     * Create an instance of {@link TimeRangeType }
     *
     */
    public TimeRangeType createTimeRangeType() {
        return new TimeRangeType();
    }

    /**
     * Create an instance of {@link MatrixType }
     *
     */
    public MatrixType createMatrixType() {
        return new MatrixType();
    }

    /**
     * Create an instance of {@link TextEncodingType }
     *
     */
    public TextEncodingType createTextEncodingType() {
        return new TextEncodingType();
    }

    /**
     * Create an instance of {@link AllowedValuesPropertyByValueType }
     *
     */
    public AllowedValuesPropertyByValueType createAllowedValuesPropertyByValueType() {
        return new AllowedValuesPropertyByValueType();
    }

    /**
     * Create an instance of {@link DataRecordType.Field }
     *
     */
    public Field createDataRecordTypeField() {
        return new Field();
    }

    /**
     * Create an instance of {@link AllowedValuesPropertyType }
     *
     */
    public AllowedValuesPropertyType createAllowedValuesPropertyType() {
        return new AllowedValuesPropertyType();
    }

    /**
     * Create an instance of {@link CategoryRangePropertyType }
     *
     */
    public CategoryRangePropertyType createCategoryRangePropertyType() {
        return new CategoryRangePropertyType();
    }

    /**
     * Create an instance of {@link TextEncodingPropertyType }
     *
     */
    public TextEncodingPropertyType createTextEncodingPropertyType() {
        return new TextEncodingPropertyType();
    }

    /**
     * Create an instance of {@link DataArrayType.ElementType }
     *
     */
    public DataArrayType.ElementType createDataArrayTypeElementType() {
        return new DataArrayType.ElementType();
    }

    /**
     * Create an instance of {@link CountRangePropertyType }
     *
     */
    public CountRangePropertyType createCountRangePropertyType() {
        return new CountRangePropertyType();
    }

    /**
     * Create an instance of {@link QualityPropertyType }
     *
     */
    public QualityPropertyType createQualityPropertyType() {
        return new QualityPropertyType();
    }

    /**
     * Create an instance of {@link QuantityRangeType }
     *
     */
    public QuantityRangeType createQuantityRangeType() {
        return new QuantityRangeType();
    }

    /**
     * Create an instance of {@link DataStreamType.Encoding }
     *
     */
    public DataStreamType.Encoding createDataStreamTypeEncoding() {
        return new DataStreamType.Encoding();
    }

    /**
     * Create an instance of {@link TimeRangePropertyType }
     *
     */
    public TimeRangePropertyType createTimeRangePropertyType() {
        return new TimeRangePropertyType();
    }

    /**
     * Create an instance of {@link BinaryEncodingType.Member }
     *
     */
    public BinaryEncodingType.Member createBinaryEncodingTypeMember() {
        return new BinaryEncodingType.Member();
    }

    /**
     * Create an instance of {@link DataChoiceType.Item }
     *
     */
    public DataChoiceType.Item createDataChoiceTypeItem() {
        return new DataChoiceType.Item();
    }

    /**
     * Create an instance of {@link EncodedValuesPropertyType }
     *
     */
    public EncodedValuesPropertyType createEncodedValuesPropertyType() {
        return new EncodedValuesPropertyType();
    }

    /**
     * Create an instance of {@link DataChoicePropertyByValueType }
     *
     */
    public DataChoicePropertyByValueType createDataChoicePropertyByValueType() {
        return new DataChoicePropertyByValueType();
    }

    /**
     * Create an instance of {@link BinaryEncodingPropertyType }
     *
     */
    public BinaryEncodingPropertyType createBinaryEncodingPropertyType() {
        return new BinaryEncodingPropertyType();
    }

    /**
     * Create an instance of {@link AbstractSimpleComponentPropertyType }
     *
     */
    public AbstractSimpleComponentPropertyType createAbstractSimpleComponentPropertyType() {
        return new AbstractSimpleComponentPropertyType();
    }

    /**
     * Create an instance of {@link AllowedTokensType }
     *
     */
    public AllowedTokensType createAllowedTokensType() {
        return new AllowedTokensType();
    }

    /**
     * Create an instance of {@link CountRangeType }
     *
     */
    public CountRangeType createCountRangeType() {
        return new CountRangeType();
    }

    /**
     * Create an instance of {@link ComponentType }
     *
     */
    public ComponentType createComponentType() {
        return new ComponentType();
    }

    /**
     * Create an instance of {@link AllowedValuesType }
     *
     */
    public AllowedValuesType createAllowedValuesType() {
        return new AllowedValuesType();
    }

    /**
     * Create an instance of {@link DataArrayPropertyType }
     *
     */
    public DataArrayPropertyType createDataArrayPropertyType() {
        return new DataArrayPropertyType();
    }

    /**
     * Create an instance of {@link VectorPropertyByValueType }
     *
     */
    public VectorPropertyByValueType createVectorPropertyByValueType() {
        return new VectorPropertyByValueType();
    }

    /**
     * Create an instance of {@link BooleanType }
     *
     */
    public BooleanType createBooleanType() {
        return new BooleanType();
    }

    /**
     * Create an instance of {@link QuantityRangePropertyType }
     *
     */
    public QuantityRangePropertyType createQuantityRangePropertyType() {
        return new QuantityRangePropertyType();
    }

    /**
     * Create an instance of {@link DataArrayType.Encoding }
     *
     */
    public DataArrayType.Encoding createDataArrayTypeEncoding() {
        return new DataArrayType.Encoding();
    }

    /**
     * Create an instance of {@link CategoryRangeType }
     *
     */
    public CategoryRangeType createCategoryRangeType() {
        return new CategoryRangeType();
    }

    /**
     * Create an instance of {@link ComponentOrBlockPropertyType }
     *
     */
    public ComponentOrBlockPropertyType createComponentOrBlockPropertyType() {
        return new ComponentOrBlockPropertyType();
    }

    /**
     * Create an instance of {@link XMLEncodingType }
     *
     */
    public XMLEncodingType createXMLEncodingType() {
        return new XMLEncodingType();
    }

    /**
     * Create an instance of {@link AbstractDataComponentPropertyType }
     *
     */
    public AbstractDataComponentPropertyType createAbstractDataComponentPropertyType() {
        return new AbstractDataComponentPropertyType();
    }

    /**
     * Create an instance of {@link DataArrayPropertyByValueType }
     *
     */
    public DataArrayPropertyByValueType createDataArrayPropertyByValueType() {
        return new DataArrayPropertyByValueType();
    }

    /**
     * Create an instance of {@link VectorPropertyType }
     *
     */
    public VectorPropertyType createVectorPropertyType() {
        return new VectorPropertyType();
    }

    /**
     * Create an instance of {@link AnyScalarPropertyType }
     *
     */
    public AnyScalarPropertyType createAnyScalarPropertyType() {
        return new AnyScalarPropertyType();
    }

    /**
     * Create an instance of {@link DataStreamPropertyByValueType }
     *
     */
    public DataStreamPropertyByValueType createDataStreamPropertyByValueType() {
        return new DataStreamPropertyByValueType();
    }

    /**
     * Create an instance of {@link CategoryType }
     *
     */
    public CategoryType createCategoryType() {
        return new CategoryType();
    }

    /**
     * Create an instance of {@link DataRecordType }
     *
     */
    public DataRecordType createDataRecordType() {
        return new DataRecordType();
    }

    /**
     * Create an instance of {@link AllowedTimesPropertyByValueType }
     *
     */
    public AllowedTimesPropertyByValueType createAllowedTimesPropertyByValueType() {
        return new AllowedTimesPropertyByValueType();
    }

    /**
     * Create an instance of {@link BlockType }
     *
     */
    public BlockType createBlockType() {
        return new BlockType();
    }

    /**
     * Create an instance of {@link AbstractEncodingPropertyType }
     *
     */
    public AbstractEncodingPropertyType createAbstractEncodingPropertyType() {
        return new AbstractEncodingPropertyType();
    }

    /**
     * Create an instance of {@link AllowedTimesType }
     *
     */
    public AllowedTimesType createAllowedTimesType() {
        return new AllowedTimesType();
    }

    /**
     * Create an instance of {@link ComponentPropertyByValueType }
     *
     */
    public ComponentPropertyByValueType createComponentPropertyByValueType() {
        return new ComponentPropertyByValueType();
    }

    /**
     * Create an instance of {@link UnitReference }
     *
     */
    public UnitReference createUnitReference() {
        return new UnitReference();
    }

    /**
     * Create an instance of {@link NilValue }
     *
     */
    public NilValue createNilValue() {
        return new NilValue();
    }

    /**
     * Create an instance of {@link MatrixPropertyType }
     *
     */
    public MatrixPropertyType createMatrixPropertyType() {
        return new MatrixPropertyType();
    }

    /**
     * Create an instance of {@link AbstractSWEIdentifiableType }
     *
     */
    public AbstractSWEIdentifiableType createAbstractSWEIdentifiableType() {
        return new AbstractSWEIdentifiableType();
    }

    /**
     * Create an instance of {@link VectorType.Coordinate }
     *
     */
    public VectorType.Coordinate createVectorTypeCoordinate() {
        return new VectorType.Coordinate();
    }

    /**
     * Create an instance of {@link BinaryEncodingType }
     *
     */
    public BinaryEncodingType createBinaryEncodingType() {
        return new BinaryEncodingType();
    }

    /**
     * Create an instance of {@link AllowedTimesPropertyType }
     *
     */
    public AllowedTimesPropertyType createAllowedTimesPropertyType() {
        return new AllowedTimesPropertyType();
    }

    /**
     * Create an instance of {@link AllowedTokensPropertyByValueType }
     *
     */
    public AllowedTokensPropertyByValueType createAllowedTokensPropertyByValueType() {
        return new AllowedTokensPropertyByValueType();
    }

    /**
     * Create an instance of {@link BlockPropertyType }
     *
     */
    public BlockPropertyType createBlockPropertyType() {
        return new BlockPropertyType();
    }

    /**
     * Create an instance of {@link DataRecordPropertyType }
     *
     */
    public DataRecordPropertyType createDataRecordPropertyType() {
        return new DataRecordPropertyType();
    }

    /**
     * Create an instance of {@link DataStreamType }
     *
     */
    public DataStreamType createDataStreamType() {
        return new DataStreamType();
    }

    /**
     * Create an instance of {@link TimeType }
     *
     */
    public TimeType createTimeType() {
        return new TimeType();
    }

    /**
     * Create an instance of {@link DataArrayType }
     *
     */
    public DataArrayType createDataArrayType() {
        return new DataArrayType();
    }

    /**
     * Create an instance of {@link MatrixPropertyByValueType }
     *
     */
    public MatrixPropertyByValueType createMatrixPropertyByValueType() {
        return new MatrixPropertyByValueType();
    }

    /**
     * Create an instance of {@link CategoryPropertyType }
     *
     */
    public CategoryPropertyType createCategoryPropertyType() {
        return new CategoryPropertyType();
    }

    /**
     * Create an instance of {@link ComponentPropertyType }
     *
     */
    public ComponentPropertyType createComponentPropertyType() {
        return new ComponentPropertyType();
    }

    /**
     * Create an instance of {@link XMLEncodingPropertyByValueType }
     *
     */
    public XMLEncodingPropertyByValueType createXMLEncodingPropertyByValueType() {
        return new XMLEncodingPropertyByValueType();
    }

    /**
     * Create an instance of {@link NilValuesType }
     *
     */
    public NilValuesType createNilValuesType() {
        return new NilValuesType();
    }

    /**
     * Create an instance of {@link TextType }
     *
     */
    public TextType createTextType() {
        return new TextType();
    }

    /**
     * Create an instance of {@link TextEncodingPropertyByValueType }
     *
     */
    public TextEncodingPropertyByValueType createTextEncodingPropertyByValueType() {
        return new TextEncodingPropertyByValueType();
    }

    /**
     * Create an instance of {@link DataStreamType.ElementType }
     *
     */
    public DataStreamType.ElementType createDataStreamTypeElementType() {
        return new DataStreamType.ElementType();
    }

    /**
     * Create an instance of {@link AnyNumericalPropertyType }
     *
     */
    public AnyNumericalPropertyType createAnyNumericalPropertyType() {
        return new AnyNumericalPropertyType();
    }

    /**
     * Create an instance of {@link BlockPropertyByValueType }
     *
     */
    public BlockPropertyByValueType createBlockPropertyByValueType() {
        return new BlockPropertyByValueType();
    }

    /**
     * Create an instance of {@link BinaryEncodingPropertyByValueType }
     *
     */
    public BinaryEncodingPropertyByValueType createBinaryEncodingPropertyByValueType() {
        return new BinaryEncodingPropertyByValueType();
    }

    /**
     * Create an instance of {@link AbstractSWEType }
     *
     */
    public AbstractSWEType createAbstractSWEType() {
        return new AbstractSWEType();
    }

    /**
     * Create an instance of {@link DataChoiceType.ChoiceValue }
     *
     */
    public DataChoiceType.ChoiceValue createDataChoiceTypeChoiceValue() {
        return new DataChoiceType.ChoiceValue();
    }

    /**
     * Create an instance of {@link XMLEncodingPropertyType }
     *
     */
    public XMLEncodingPropertyType createXMLEncodingPropertyType() {
        return new XMLEncodingPropertyType();
    }

    /**
     * Create an instance of {@link DataRecordPropertyByValueType }
     *
     */
    public DataRecordPropertyByValueType createDataRecordPropertyByValueType() {
        return new DataRecordPropertyByValueType();
    }

    /**
     * Create an instance of {@link DataChoicePropertyType }
     *
     */
    public DataChoicePropertyType createDataChoicePropertyType() {
        return new DataChoicePropertyType();
    }

    /**
     * Create an instance of {@link VectorType }
     *
     */
    public VectorType createVectorType() {
        return new VectorType();
    }

    /**
     * Create an instance of {@link DataStreamPropertyType }
     *
     */
    public DataStreamPropertyType createDataStreamPropertyType() {
        return new DataStreamPropertyType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractEncodingType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "AbstractEncoding", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<AbstractEncodingType> createAbstractEncoding(AbstractEncodingType value) {
        return new JAXBElement<AbstractEncodingType>(_AbstractEncoding_QNAME, AbstractEncodingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDataComponentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "AbstractDataComponent", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWEIdentifiable")
    public JAXBElement<AbstractDataComponentType> createAbstractDataComponent(AbstractDataComponentType value) {
        return new JAXBElement<AbstractDataComponentType>(_AbstractDataComponent_QNAME, AbstractDataComponentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryEncodingType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "BinaryEncoding", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractEncoding")
    public JAXBElement<BinaryEncodingType> createBinaryEncoding(BinaryEncodingType value) {
        return new JAXBElement<BinaryEncodingType>(_BinaryEncoding_QNAME, BinaryEncodingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractSWEIdentifiableType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "AbstractSWEIdentifiable", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<AbstractSWEIdentifiableType> createAbstractSWEIdentifiable(AbstractSWEIdentifiableType value) {
        return new JAXBElement<AbstractSWEIdentifiableType>(_AbstractSWEIdentifiable_QNAME, AbstractSWEIdentifiableType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "Count", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSimpleComponent")
    public JAXBElement<CountType> createCount(CountType value) {
        return new JAXBElement<CountType>(_Count_QNAME, CountType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CategoryRangeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "CategoryRange", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSimpleComponent")
    public JAXBElement<CategoryRangeType> createCategoryRange(CategoryRangeType value) {
        return new JAXBElement<CategoryRangeType>(_CategoryRange_QNAME, CategoryRangeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractSWEType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "AbstractSWE")
    public JAXBElement<AbstractSWEType> createAbstractSWE(AbstractSWEType value) {
        return new JAXBElement<AbstractSWEType>(_AbstractSWE_QNAME, AbstractSWEType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CategoryType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "Category", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSimpleComponent")
    public JAXBElement<CategoryType> createCategory(CategoryType value) {
        return new JAXBElement<CategoryType>(_Category_QNAME, CategoryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TextEncodingType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "TextEncoding", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractEncoding")
    public JAXBElement<TextEncodingType> createTextEncoding(TextEncodingType value) {
        return new JAXBElement<TextEncodingType>(_TextEncoding_QNAME, TextEncodingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VectorType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "Vector", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractDataComponent")
    public JAXBElement<VectorType> createVector(VectorType value) {
        return new JAXBElement<VectorType>(_Vector_QNAME, VectorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QuantityRangeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "QuantityRange", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSimpleComponent")
    public JAXBElement<QuantityRangeType> createQuantityRange(QuantityRangeType value) {
        return new JAXBElement<QuantityRangeType>(_QuantityRange_QNAME, QuantityRangeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NilValuesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "NilValues", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<NilValuesType> createNilValues(NilValuesType value) {
        return new JAXBElement<NilValuesType>(_NilValues_QNAME, NilValuesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "DataArray", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractDataComponent")
    public JAXBElement<DataArrayType> createDataArray(DataArrayType value) {
        return new JAXBElement<DataArrayType>(_DataArray_QNAME, DataArrayType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MatrixType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "Matrix", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "DataArray")
    public JAXBElement<MatrixType> createMatrix(MatrixType value) {
        return new JAXBElement<MatrixType>(_Matrix_QNAME, MatrixType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataStreamType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "DataStream", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWEIdentifiable")
    public JAXBElement<DataStreamType> createDataStream(DataStreamType value) {
        return new JAXBElement<DataStreamType>(_DataStream_QNAME, DataStreamType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AllowedTokensType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "AllowedTokens", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<AllowedTokensType> createAllowedTokens(AllowedTokensType value) {
        return new JAXBElement<AllowedTokensType>(_AllowedTokens_QNAME, AllowedTokensType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountRangeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "CountRange", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSimpleComponent")
    public JAXBElement<CountRangeType> createCountRange(CountRangeType value) {
        return new JAXBElement<CountRangeType>(_CountRange_QNAME, CountRangeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BooleanType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "Boolean", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSimpleComponent")
    public JAXBElement<BooleanType> createBoolean(BooleanType value) {
        return new JAXBElement<BooleanType>(_Boolean_QNAME, BooleanType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeRangeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "TimeRange", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSimpleComponent")
    public JAXBElement<TimeRangeType> createTimeRange(TimeRangeType value) {
        return new JAXBElement<TimeRangeType>(_TimeRange_QNAME, TimeRangeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "Time", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSimpleComponent")
    public JAXBElement<TimeType> createTime(TimeType value) {
        return new JAXBElement<TimeType>(_Time_QNAME, TimeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComponentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "Component", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<ComponentType> createComponent(ComponentType value) {
        return new JAXBElement<ComponentType>(_Component_QNAME, ComponentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TextType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "Text", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSimpleComponent")
    public JAXBElement<TextType> createText(TextType value) {
        return new JAXBElement<TextType>(_Text_QNAME, TextType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QuantityType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "Quantity", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSimpleComponent")
    public JAXBElement<QuantityType> createQuantity(QuantityType value) {
        return new JAXBElement<QuantityType>(_Quantity_QNAME, QuantityType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BlockType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "Block", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<BlockType> createBlock(BlockType value) {
        return new JAXBElement<BlockType>(_Block_QNAME, BlockType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataChoiceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "DataChoice", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractDataComponent")
    public JAXBElement<DataChoiceType> createDataChoice(DataChoiceType value) {
        return new JAXBElement<DataChoiceType>(_DataChoice_QNAME, DataChoiceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AllowedValuesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "AllowedValues", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<AllowedValuesType> createAllowedValues(AllowedValuesType value) {
        return new JAXBElement<AllowedValuesType>(_AllowedValues_QNAME, AllowedValuesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "DataRecord", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractDataComponent")
    public JAXBElement<DataRecordType> createDataRecord(DataRecordType value) {
        return new JAXBElement<DataRecordType>(_DataRecord_QNAME, DataRecordType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractSimpleComponentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "AbstractSimpleComponent", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractDataComponent")
    public JAXBElement<AbstractSimpleComponentType> createAbstractSimpleComponent(AbstractSimpleComponentType value) {
        return new JAXBElement<AbstractSimpleComponentType>(_AbstractSimpleComponent_QNAME, AbstractSimpleComponentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AllowedTimesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "AllowedTimes", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractSWE")
    public JAXBElement<AllowedTimesType> createAllowedTimes(AllowedTimesType value) {
        return new JAXBElement<AllowedTimesType>(_AllowedTimes_QNAME, AllowedTimesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLEncodingType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "XMLEncoding", substitutionHeadNamespace = "http://www.opengis.net/swe/2.0", substitutionHeadName = "AbstractEncoding")
    public JAXBElement<XMLEncodingType> createXMLEncoding(XMLEncodingType value) {
        return new JAXBElement<XMLEncodingType>(_XMLEncoding_QNAME, XMLEncodingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "value", scope = AllowedTimesType.class)
    public JAXBElement<List<String>> createAllowedTimesTypeValue(List<String> value) {
        return new JAXBElement<List<String>>(_AllowedTimesTypeValue_QNAME, ((Class) List.class), AllowedTimesType.class, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "interval", scope = AllowedTimesType.class)
    public JAXBElement<List<String>> createAllowedTimesTypeInterval(List<String> value) {
        return new JAXBElement<List<String>>(_AllowedTimesTypeInterval_QNAME, ((Class) List.class), AllowedTimesType.class, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/swe/2.0", name = "interval", scope = AllowedValuesType.class)
    public JAXBElement<List<Double>> createAllowedValuesTypeInterval(List<Double> value) {
        return new JAXBElement<List<Double>>(_AllowedTimesTypeInterval_QNAME, ((Class) List.class), AllowedValuesType.class, ((List<Double> ) value));
    }

}
