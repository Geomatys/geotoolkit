/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.fes._2 package.
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

    private final static QName _AbstractProjectionClause_QNAME = new QName("http://www.opengis.net/fes/2.0", "AbstractProjectionClause");
    private final static QName _PropertyIsGreaterThan_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsGreaterThan");
    private final static QName _TContains_QNAME = new QName("http://www.opengis.net/fes/2.0", "TContains");
    private final static QName _Meets_QNAME = new QName("http://www.opengis.net/fes/2.0", "Meets");
    private final static QName _PropertyIsNotEqualTo_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsNotEqualTo");
    private final static QName _SpatialOps_QNAME = new QName("http://www.opengis.net/fes/2.0", "spatialOps");
    private final static QName _PropertyIsLessThanOrEqualTo_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsLessThanOrEqualTo");
    private final static QName _LogicOps_QNAME = new QName("http://www.opengis.net/fes/2.0", "logicOps");
    private final static QName _Id_QNAME = new QName("http://www.opengis.net/fes/2.0", "_Id");
    private final static QName _AbstractQueryExpression_QNAME = new QName("http://www.opengis.net/fes/2.0", "AbstractQueryExpression");
    private final static QName _Contains_QNAME = new QName("http://www.opengis.net/fes/2.0", "Contains");
    private final static QName _PropertyIsLike_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsLike");
    private final static QName _PropertyIsEqualTo_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsEqualTo");
    private final static QName _Begins_QNAME = new QName("http://www.opengis.net/fes/2.0", "Begins");
    private final static QName _During_QNAME = new QName("http://www.opengis.net/fes/2.0", "During");
    private final static QName _ValueReference_QNAME = new QName("http://www.opengis.net/fes/2.0", "ValueReference");
    private final static QName _BBOX_QNAME = new QName("http://www.opengis.net/fes/2.0", "BBOX");
    private final static QName _SortBy_QNAME = new QName("http://www.opengis.net/fes/2.0", "SortBy");
    private final static QName _Not_QNAME = new QName("http://www.opengis.net/fes/2.0", "Not");
    private final static QName _Beyond_QNAME = new QName("http://www.opengis.net/fes/2.0", "Beyond");
    private final static QName _DWithin_QNAME = new QName("http://www.opengis.net/fes/2.0", "DWithin");
    private final static QName _Expression_QNAME = new QName("http://www.opengis.net/fes/2.0", "expression");
    private final static QName _EndedBy_QNAME = new QName("http://www.opengis.net/fes/2.0", "EndedBy");
    private final static QName _Within_QNAME = new QName("http://www.opengis.net/fes/2.0", "Within");
    private final static QName _Or_QNAME = new QName("http://www.opengis.net/fes/2.0", "Or");
    private final static QName _PropertyIsBetween_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsBetween");
    private final static QName _PropertyIsNull_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsNull");
    private final static QName _And_QNAME = new QName("http://www.opengis.net/fes/2.0", "And");
    private final static QName _Touches_QNAME = new QName("http://www.opengis.net/fes/2.0", "Touches");
    private final static QName _PropertyIsNil_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsNil");
    private final static QName _Literal_QNAME = new QName("http://www.opengis.net/fes/2.0", "Literal");
    private final static QName _Ends_QNAME = new QName("http://www.opengis.net/fes/2.0", "Ends");
    private final static QName _TOverlaps_QNAME = new QName("http://www.opengis.net/fes/2.0", "TOverlaps");
    private final static QName _ComparisonOps_QNAME = new QName("http://www.opengis.net/fes/2.0", "comparisonOps");
    private final static QName _ResourceId_QNAME = new QName("http://www.opengis.net/fes/2.0", "ResourceId");
    private final static QName _MetBy_QNAME = new QName("http://www.opengis.net/fes/2.0", "MetBy");
    private final static QName _Equals_QNAME = new QName("http://www.opengis.net/fes/2.0", "Equals");
    private final static QName _AbstractAdhocQueryExpression_QNAME = new QName("http://www.opengis.net/fes/2.0", "AbstractAdhocQueryExpression");
    private final static QName _Function_QNAME = new QName("http://www.opengis.net/fes/2.0", "Function");
    private final static QName _Disjoint_QNAME = new QName("http://www.opengis.net/fes/2.0", "Disjoint");
    private final static QName _TemporalOps_QNAME = new QName("http://www.opengis.net/fes/2.0", "temporalOps");
    private final static QName _Overlaps_QNAME = new QName("http://www.opengis.net/fes/2.0", "Overlaps");
    private final static QName _OverlappedBy_QNAME = new QName("http://www.opengis.net/fes/2.0", "OverlappedBy");
    private final static QName _BegunBy_QNAME = new QName("http://www.opengis.net/fes/2.0", "BegunBy");
    private final static QName _PropertyIsLessThan_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsLessThan");
    private final static QName _Crosses_QNAME = new QName("http://www.opengis.net/fes/2.0", "Crosses");
    private final static QName _PropertyIsGreaterThanOrEqualTo_QNAME = new QName("http://www.opengis.net/fes/2.0", "PropertyIsGreaterThanOrEqualTo");
    private final static QName _Before_QNAME = new QName("http://www.opengis.net/fes/2.0", "Before");
    private final static QName _After_QNAME = new QName("http://www.opengis.net/fes/2.0", "After");
    private final static QName _Filter_QNAME = new QName("http://www.opengis.net/fes/2.0", "Filter");
    private final static QName _Intersects_QNAME = new QName("http://www.opengis.net/fes/2.0", "Intersects");
    private final static QName _ExtensionOps_QNAME = new QName("http://www.opengis.net/fes/2.0", "extensionOps");
    private final static QName _AnyInteracts_QNAME = new QName("http://www.opengis.net/fes/2.0", "AnyInteracts");
    private final static QName _AbstractSortingClause_QNAME = new QName("http://www.opengis.net/fes/2.0", "AbstractSortingClause");
    private final static QName _AbstractSelectionClause_QNAME = new QName("http://www.opengis.net/fes/2.0", "AbstractSelectionClause");
    private final static QName _TEquals_QNAME = new QName("http://www.opengis.net/fes/2.0", "TEquals");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.fes._2
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ArgumentsType }
     */
    public ArgumentsType createArgumentsType() {
        return new ArgumentsType();
    }

    /**
     * Create an instance of {@link ResourceIdentifierType }
     */
    public ResourceIdentifierType createResourceIdentifierType() {
        return new ResourceIdentifierType();
    }

    /**
     * Create an instance of {@link UpperBoundaryType }
     */
    public UpperBoundaryType createUpperBoundaryType() {
        return new UpperBoundaryType();
    }

    /**
     * Create an instance of {@link GeometryOperandsType.GeometryOperand }
     */
    public GeometryOperandsType.GeometryOperand createGeometryOperandsTypeGeometryOperand() {
        return new GeometryOperandsType.GeometryOperand();
    }

    /**
     * Create an instance of {@link FilterType }
     */
    public FilterType createFilterType() {
        return new FilterType();
    }

    /**
     * Create an instance of {@link FunctionType }
     */
    public FunctionType createFunctionType() {
        return new FunctionType();
    }

    /**
     * Create an instance of {@link TemporalOperatorType }
     */
    public TemporalOperatorType createTemporalOperatorType() {
        return new TemporalOperatorType();
    }

    /**
     * Create an instance of {@link ComparisonOperatorsType }
     */
    public ComparisonOperatorsType createComparisonOperatorsType() {
        return new ComparisonOperatorsType();
    }

    /**
     * Create an instance of {@link BinaryLogicOpType }
     *

    public BinaryLogicOpType createBinaryLogicOpType() {
        return new BinaryLogicOpType();
    }*/

    /**
     * Create an instance of {@link TemporalCapabilitiesType }
     */
    public TemporalCapabilitiesType createTemporalCapabilitiesType() {
        return new TemporalCapabilitiesType();
    }

    /**
     * Create an instance of {@link FilterCapabilities }
     */
    public FilterCapabilities createFilterCapabilities() {
        return new FilterCapabilities();
    }

    /**
     * Create an instance of {@link LowerBoundaryType }
     */
    public LowerBoundaryType createLowerBoundaryType() {
        return new LowerBoundaryType();
    }

    /**
     * Create an instance of {@link PropertyIsLikeType }
     */
    public PropertyIsLikeType createPropertyIsLikeType() {
        return new PropertyIsLikeType();
    }

    /**
     * Create an instance of {@link TemporalOperandsType }
     */
    public TemporalOperandsType createTemporalOperandsType() {
        return new TemporalOperandsType();
    }

    /**
     * Create an instance of {@link ArgumentType }
     */
    public ArgumentType createArgumentType() {
        return new ArgumentType();
    }

    /**
     * Create an instance of {@link TemporalOperandsType.TemporalOperand }
     */
    public TemporalOperandsType.TemporalOperand createTemporalOperandsTypeTemporalOperand() {
        return new TemporalOperandsType.TemporalOperand();
    }

    /**
     * Create an instance of {@link LiteralType }
     */
    public LiteralType createLiteralType() {
        return new LiteralType();
    }

    /**
     * Create an instance of {@link BinarySpatialOpType }
     *

    public BinarySpatialOpType createBinarySpatialOpType() {
        return new BinarySpatialOpType();
    }*/

    /**
     * Create an instance of {@link ExtensionOperatorType }
     */
    public ExtensionOperatorType createExtensionOperatorType() {
        return new ExtensionOperatorType();
    }

    /**
     * Create an instance of {@link PropertyIsBetweenType }
     */
    public PropertyIsBetweenType createPropertyIsBetweenType() {
        return new PropertyIsBetweenType();
    }

    /**
     * Create an instance of {@link AvailableFunctionsType }
     */
    public AvailableFunctionsType createAvailableFunctionsType() {
        return new AvailableFunctionsType();
    }

    /**
     * Create an instance of {@link AvailableFunctionType }
     */
    public AvailableFunctionType createAvailableFunctionType() {
        return new AvailableFunctionType();
    }

    /**
     * Create an instance of {@link ResourceIdType }
     */
    public ResourceIdType createResourceIdType() {
        return new ResourceIdType();
    }

    /**
     * Create an instance of {@link AdditionalOperatorsType }
     */
    public AdditionalOperatorsType createAdditionalOperatorsType() {
        return new AdditionalOperatorsType();
    }

    /**
     * Create an instance of {@link BinaryComparisonOpType }
     */
    public BinaryComparisonOpType createBinaryComparisonOpType() {
        return new BinaryComparisonOpType();
    }

    /**
     * Create an instance of {@link SortByType }
     */
    public SortByType createSortByType() {
        return new SortByType();
    }

    /**
     * Create an instance of {@link SpatialOperatorsType }
     */
    public SpatialOperatorsType createSpatialOperatorsType() {
        return new SpatialOperatorsType();
    }

    /**
     * Create an instance of {@link PropertyIsNilType }
     */
    public PropertyIsNilType createPropertyIsNilType() {
        return new PropertyIsNilType();
    }

    /**
     * Create an instance of {@link ScalarCapabilitiesType }
     */
    public ScalarCapabilitiesType createScalarCapabilitiesType() {
        return new ScalarCapabilitiesType();
    }

    /**
     * Create an instance of {@link SpatialCapabilitiesType }
     */
    public SpatialCapabilitiesType createSpatialCapabilitiesType() {
        return new SpatialCapabilitiesType();
    }

    /**
     * Create an instance of {@link SortPropertyType }
     */
    public SortPropertyType createSortPropertyType() {
        return new SortPropertyType();
    }

    /**
     * Create an instance of {@link GeometryOperandsType }
     */
    public GeometryOperandsType createGeometryOperandsType() {
        return new GeometryOperandsType();
    }

    /**
     * Create an instance of {@link BinaryTemporalOpType }
     */
    public BinaryTemporalOpType createBinaryTemporalOpType() {
        return new BinaryTemporalOpType();
    }

    /**
     * Create an instance of {@link ExtendedCapabilitiesType }
     */
    public ExtendedCapabilitiesType createExtendedCapabilitiesType() {
        return new ExtendedCapabilitiesType();
    }

    /**
     * Create an instance of {@link PropertyIsNullType }
     */
    public PropertyIsNullType createPropertyIsNullType() {
        return new PropertyIsNullType();
    }

    /**
     * Create an instance of {@link BBOXType }
     */
    public BBOXType createBBOXType() {
        return new BBOXType();
    }

    /**
     * Create an instance of {@link IdCapabilitiesType }
     */
    public IdCapabilitiesType createIdCapabilitiesType() {
        return new IdCapabilitiesType();
    }

    /**
     * Create an instance of {@link DistanceBufferType }
     *

    public DistanceBufferType createDistanceBufferType() {
        return new DistanceBufferType();
    } */

    /**
     * Create an instance of {@link TemporalOperatorsType }
     */
    public TemporalOperatorsType createTemporalOperatorsType() {
        return new TemporalOperatorsType();
    }

    /**
     * Create an instance of {@link ConformanceType }
     */
    public ConformanceType createConformanceType() {
        return new ConformanceType();
    }

    /**
     * Create an instance of {@link MeasureType }
     */
    public MeasureType createMeasureType() {
        return new MeasureType();
    }

    /**
     * Create an instance of {@link LogicalOperators }
     */
    public LogicalOperators createLogicalOperators() {
        return new LogicalOperators();
    }

    /**
     * Create an instance of {@link ComparisonOperatorType }
     */
    public ComparisonOperatorType createComparisonOperatorType() {
        return new ComparisonOperatorType();
    }

    /**
     * Create an instance of {@link UnaryLogicOpType }
     *

    public UnaryLogicOpType createUnaryLogicOpType() {
        return new UnaryLogicOpType();
    } */

    /**
     * Create an instance of {@link SpatialOperatorType }
     */
    public SpatialOperatorType createSpatialOperatorType() {
        return new SpatialOperatorType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "AbstractProjectionClause")
    public JAXBElement<Object> createAbstractProjectionClause(Object value) {
        return new JAXBElement<Object>(_AbstractProjectionClause_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "PropertyIsGreaterThan", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsGreaterThanType> createPropertyIsGreaterThan(PropertyIsGreaterThanType value) {
        return new JAXBElement<PropertyIsGreaterThanType>(_PropertyIsGreaterThan_QNAME, PropertyIsGreaterThanType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "TContains", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeContainsType> createTContains(TimeContainsType value) {
        return new JAXBElement<TimeContainsType>(_TContains_QNAME, TimeContainsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Meets", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeMeetsType> createMeets(TimeMeetsType value) {
        return new JAXBElement<TimeMeetsType>(_Meets_QNAME, TimeMeetsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "PropertyIsNotEqualTo", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsNotEqualToType> createPropertyIsNotEqualTo(PropertyIsNotEqualToType value) {
        return new JAXBElement<PropertyIsNotEqualToType>(_PropertyIsNotEqualTo_QNAME, PropertyIsNotEqualToType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpatialOpsType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "spatialOps")
    public JAXBElement<SpatialOpsType> createSpatialOps(SpatialOpsType value) {
        return new JAXBElement<SpatialOpsType>(_SpatialOps_QNAME, SpatialOpsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "PropertyIsLessThanOrEqualTo", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsLessThanOrEqualToType> createPropertyIsLessThanOrEqualTo(PropertyIsLessThanOrEqualToType value) {
        return new JAXBElement<PropertyIsLessThanOrEqualToType>(_PropertyIsLessThanOrEqualTo_QNAME, PropertyIsLessThanOrEqualToType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LogicOpsType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "logicOps")
    public JAXBElement<LogicOpsType> createLogicOps(LogicOpsType value) {
        return new JAXBElement<LogicOpsType>(_LogicOps_QNAME, LogicOpsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractIdType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "_Id")
    public JAXBElement<AbstractIdType> createId(AbstractIdType value) {
        return new JAXBElement<AbstractIdType>(_Id_QNAME, AbstractIdType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractQueryExpressionType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "AbstractQueryExpression")
    public JAXBElement<AbstractQueryExpressionType> createAbstractQueryExpression(AbstractQueryExpressionType value) {
        return new JAXBElement<AbstractQueryExpressionType>(_AbstractQueryExpression_QNAME, AbstractQueryExpressionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Contains", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "spatialOps")
    public JAXBElement<ContainsType> createContains(ContainsType value) {
        return new JAXBElement<ContainsType>(_Contains_QNAME, ContainsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyIsLikeType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "PropertyIsLike", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsLikeType> createPropertyIsLike(PropertyIsLikeType value) {
        return new JAXBElement<PropertyIsLikeType>(_PropertyIsLike_QNAME, PropertyIsLikeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "PropertyIsEqualTo", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsEqualToType> createPropertyIsEqualTo(PropertyIsEqualToType value) {
        return new JAXBElement<PropertyIsEqualToType>(_PropertyIsEqualTo_QNAME, PropertyIsEqualToType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Begins", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeBeginsType> createBegins(TimeBeginsType value) {
        return new JAXBElement<TimeBeginsType>(_Begins_QNAME, TimeBeginsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "During", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeDuringType> createDuring(TimeDuringType value) {
        return new JAXBElement<TimeDuringType>(_During_QNAME, TimeDuringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "ValueReference", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "expression")
    public JAXBElement<String> createValueReference(String value) {
        return new JAXBElement<String>(_ValueReference_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BBOXType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "BBOX", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "spatialOps")
    public JAXBElement<BBOXType> createBBOX(BBOXType value) {
        return new JAXBElement<BBOXType>(_BBOX_QNAME, BBOXType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SortByType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "SortBy", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "AbstractSortingClause")
    public JAXBElement<SortByType> createSortBy(SortByType value) {
        return new JAXBElement<SortByType>(_SortBy_QNAME, SortByType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnaryLogicOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Not", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "logicOps")
    public JAXBElement<NotType> createNot(NotType value) {
        return new JAXBElement<NotType>(_Not_QNAME, NotType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Beyond", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "spatialOps")
    public JAXBElement<BeyondType> createBeyond(BeyondType value) {
        return new JAXBElement<BeyondType>(_Beyond_QNAME, BeyondType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "DWithin", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "spatialOps")
    public JAXBElement<DWithinType> createDWithin(DWithinType value) {
        return new JAXBElement<DWithinType>(_DWithin_QNAME, DWithinType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "expression")
    public JAXBElement<Object> createExpression(Object value) {
        return new JAXBElement<Object>(_Expression_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "EndedBy", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeEndedByType> createEndedBy(TimeEndedByType value) {
        return new JAXBElement<TimeEndedByType>(_EndedBy_QNAME, TimeEndedByType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Within", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "spatialOps")
    public JAXBElement<WithinType> createWithin(WithinType value) {
        return new JAXBElement<WithinType>(_Within_QNAME, WithinType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryLogicOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Or", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "logicOps")
    public JAXBElement<OrType> createOr(OrType value) {
        return new JAXBElement<OrType>(_Or_QNAME, OrType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyIsBetweenType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "PropertyIsBetween", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsBetweenType> createPropertyIsBetween(PropertyIsBetweenType value) {
        return new JAXBElement<PropertyIsBetweenType>(_PropertyIsBetween_QNAME, PropertyIsBetweenType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyIsNullType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "PropertyIsNull", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsNullType> createPropertyIsNull(PropertyIsNullType value) {
        return new JAXBElement<PropertyIsNullType>(_PropertyIsNull_QNAME, PropertyIsNullType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryLogicOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "And", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "logicOps")
    public JAXBElement<AndType> createAnd(AndType value) {
        return new JAXBElement<AndType>(_And_QNAME, AndType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Touches", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "spatialOps")
    public JAXBElement<TouchesType> createTouches(TouchesType value) {
        return new JAXBElement<TouchesType>(_Touches_QNAME, TouchesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyIsNilType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "PropertyIsNil", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsNilType> createPropertyIsNil(PropertyIsNilType value) {
        return new JAXBElement<PropertyIsNilType>(_PropertyIsNil_QNAME, PropertyIsNilType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LiteralType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Literal", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "expression")
    public JAXBElement<LiteralType> createLiteral(LiteralType value) {
        return new JAXBElement<LiteralType>(_Literal_QNAME, LiteralType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Ends", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeEndsType> createEnds(TimeEndsType value) {
        return new JAXBElement<TimeEndsType>(_Ends_QNAME, TimeEndsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "TOverlaps", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeOverlapsType> createTOverlaps(TimeOverlapsType value) {
        return new JAXBElement<TimeOverlapsType>(_TOverlaps_QNAME, TimeOverlapsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComparisonOpsType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "comparisonOps")
    public JAXBElement<ComparisonOpsType> createComparisonOps(ComparisonOpsType value) {
        return new JAXBElement<ComparisonOpsType>(_ComparisonOps_QNAME, ComparisonOpsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResourceIdType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "ResourceId", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "_Id")
    public JAXBElement<ResourceIdType> createResourceId(ResourceIdType value) {
        return new JAXBElement<ResourceIdType>(_ResourceId_QNAME, ResourceIdType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "MetBy", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeMetByType> createMetBy(TimeMetByType value) {
        return new JAXBElement<TimeMetByType>(_MetBy_QNAME, TimeMetByType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Equals", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "spatialOps")
    public JAXBElement<EqualsType> createEquals(EqualsType value) {
        return new JAXBElement<EqualsType>(_Equals_QNAME, EqualsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractAdhocQueryExpressionType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "AbstractAdhocQueryExpression", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "AbstractQueryExpression")
    public JAXBElement<AbstractAdhocQueryExpressionType> createAbstractAdhocQueryExpression(AbstractAdhocQueryExpressionType value) {
        return new JAXBElement<AbstractAdhocQueryExpressionType>(_AbstractAdhocQueryExpression_QNAME, AbstractAdhocQueryExpressionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FunctionType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Function", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "expression")
    public JAXBElement<FunctionType> createFunction(FunctionType value) {
        return new JAXBElement<FunctionType>(_Function_QNAME, FunctionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Disjoint", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "spatialOps")
    public JAXBElement<DisjointType> createDisjoint(DisjointType value) {
        return new JAXBElement<DisjointType>(_Disjoint_QNAME, DisjointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemporalOpsType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "temporalOps")
    public JAXBElement<TemporalOpsType> createTemporalOps(TemporalOpsType value) {
        return new JAXBElement<TemporalOpsType>(_TemporalOps_QNAME, TemporalOpsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Overlaps", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "spatialOps")
    public JAXBElement<OverlapsType> createOverlaps(OverlapsType value) {
        return new JAXBElement<OverlapsType>(_Overlaps_QNAME, OverlapsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "OverlappedBy", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeOverlappedByType> createOverlappedBy(TimeOverlappedByType value) {
        return new JAXBElement<TimeOverlappedByType>(_OverlappedBy_QNAME, TimeOverlappedByType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "BegunBy", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeBegunByType> createBegunBy(TimeBegunByType value) {
        return new JAXBElement<TimeBegunByType>(_BegunBy_QNAME, TimeBegunByType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "PropertyIsLessThan", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsLessThanType> createPropertyIsLessThan(PropertyIsLessThanType value) {
        return new JAXBElement<PropertyIsLessThanType>(_PropertyIsLessThan_QNAME, PropertyIsLessThanType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Crosses", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "spatialOps")
    public JAXBElement<CrossesType> createCrosses(CrossesType value) {
        return new JAXBElement<CrossesType>(_Crosses_QNAME, CrossesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "PropertyIsGreaterThanOrEqualTo", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsGreaterThanOrEqualToType> createPropertyIsGreaterThanOrEqualTo(PropertyIsGreaterThanOrEqualToType value) {
        return new JAXBElement<PropertyIsGreaterThanOrEqualToType>(_PropertyIsGreaterThanOrEqualTo_QNAME, PropertyIsGreaterThanOrEqualToType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Before", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeBeforeType> createBefore(TimeBeforeType value) {
        return new JAXBElement<TimeBeforeType>(_Before_QNAME, TimeBeforeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "After", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeAfterType> createAfter(TimeAfterType value) {
        return new JAXBElement<TimeAfterType>(_After_QNAME, TimeAfterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FilterType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Filter", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "AbstractSelectionClause")
    public JAXBElement<FilterType> createFilter(FilterType value) {
        return new JAXBElement<FilterType>(_Filter_QNAME, FilterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "Intersects", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "spatialOps")
    public JAXBElement<IntersectsType> createIntersects(IntersectsType value) {
        return new JAXBElement<IntersectsType>(_Intersects_QNAME, IntersectsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtensionOpsType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "extensionOps")
    public JAXBElement<ExtensionOpsType> createExtensionOps(ExtensionOpsType value) {
        return new JAXBElement<ExtensionOpsType>(_ExtensionOps_QNAME, ExtensionOpsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "AnyInteracts", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeAnyInteractsType> createAnyInteracts(TimeAnyInteractsType value) {
        return new JAXBElement<TimeAnyInteractsType>(_AnyInteracts_QNAME, TimeAnyInteractsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "AbstractSortingClause")
    public JAXBElement<Object> createAbstractSortingClause(Object value) {
        return new JAXBElement<Object>(_AbstractSortingClause_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "AbstractSelectionClause")
    public JAXBElement<Object> createAbstractSelectionClause(Object value) {
        return new JAXBElement<Object>(_AbstractSelectionClause_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/fes/2.0", name = "TEquals", substitutionHeadNamespace = "http://www.opengis.net/fes/2.0", substitutionHeadName = "temporalOps")
    public JAXBElement<TimeEqualsType> createTEquals(TimeEqualsType value) {
        return new JAXBElement<TimeEqualsType>(_TEquals_QNAME, TimeEqualsType.class, null, value);
    }
}
