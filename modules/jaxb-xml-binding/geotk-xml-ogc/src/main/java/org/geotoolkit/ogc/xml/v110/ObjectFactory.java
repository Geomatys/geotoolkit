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
package org.geotoolkit.ogc.xml.v110;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.ogc package. 
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

    // Comparison operators
    private final static QName _PropertyIsLessThan_QNAME             = new QName("http://www.opengis.net/ogc", "PropertyIsLessThan");
    private final static QName _PropertyIsGreaterThanOrEqualTo_QNAME = new QName("http://www.opengis.net/ogc", "PropertyIsGreaterThanOrEqualTo");
    private final static QName _PropertyIsNotEqualTo_QNAME           = new QName("http://www.opengis.net/ogc", "PropertyIsNotEqualTo");
    private final static QName _PropertyIsLessThanOrEqualTo_QNAME    = new QName("http://www.opengis.net/ogc", "PropertyIsLessThanOrEqualTo");
    private final static QName _PropertyIsLike_QNAME                 = new QName("http://www.opengis.net/ogc", "PropertyIsLike");
    private final static QName _PropertyIsNull_QNAME                 = new QName("http://www.opengis.net/ogc", "PropertyIsNull");
    private final static QName _PropertyIsBetween_QNAME              = new QName("http://www.opengis.net/ogc", "PropertyIsBetween");
    private final static QName _PropertyIsGreaterThan_QNAME          = new QName("http://www.opengis.net/ogc", "PropertyIsGreaterThan");
    private final static QName _PropertyIsEqualTo_QNAME              = new QName("http://www.opengis.net/ogc", "PropertyIsEqualTo");
    private final static QName _ComparisonOps_QNAME                  = new QName("http://www.opengis.net/ogc", "comparisonOps");
    
    // Spatial Operator
    private final static QName _Intersects_QNAME                     = new QName("http://www.opengis.net/ogc", "Intersects");
    private final static QName _SpatialOps_QNAME                     = new QName("http://www.opengis.net/ogc", "spatialOps");
    private final static QName _Touches_QNAME                        = new QName("http://www.opengis.net/ogc", "Touches");
    private final static QName _Disjoint_QNAME                       = new QName("http://www.opengis.net/ogc", "Disjoint");
    private final static QName _Crosses_QNAME                        = new QName("http://www.opengis.net/ogc", "Crosses");
    private final static QName _Contains_QNAME                       = new QName("http://www.opengis.net/ogc", "Contains");
    private final static QName _Equals_QNAME                         = new QName("http://www.opengis.net/ogc", "Equals");
    private final static QName _Overlaps_QNAME                       = new QName("http://www.opengis.net/ogc", "Overlaps");
    private final static QName _BBOX_QNAME                           = new QName("http://www.opengis.net/ogc", "BBOX");
    private final static QName _Within_QNAME                         = new QName("http://www.opengis.net/ogc", "Within");
    
    // Spatial Distance Operator
    private final static QName _DWithin_QNAME                        = new QName("http://www.opengis.net/ogc", "DWithin");
    private final static QName _Beyond_QNAME                         = new QName("http://www.opengis.net/ogc", "Beyond");
    
    // Logical Operator
    private final static QName _And_QNAME                            = new QName("http://www.opengis.net/ogc", "And");
    private final static QName _Or_QNAME                             = new QName("http://www.opengis.net/ogc", "Or");
    private final static QName _LogicOps_QNAME                       = new QName("http://www.opengis.net/ogc", "logicOps");
    private final static QName _Not_QNAME                            = new QName("http://www.opengis.net/ogc", "Not");
    
    // Temporal Operator
    private final static QName _TOveralps_QNAME                      = new QName("http://www.opengis.net/ogc", "TM_Overalps");
    private final static QName _TEquals_QNAME                        = new QName("http://www.opengis.net/ogc", "TM_Equals");
    private final static QName _TMeets_QNAME                         = new QName("http://www.opengis.net/ogc", "TM_Meets");
    private final static QName _TOverlappedBy_QNAME                  = new QName("http://www.opengis.net/ogc", "TM_OverlappedBy");
    private final static QName _TEndedBy_QNAME                       = new QName("http://www.opengis.net/ogc", "TM_EndedBy");
    private final static QName _TEnds_QNAME                          = new QName("http://www.opengis.net/ogc", "TM_Ends");
    private final static QName _TAfter_QNAME                         = new QName("http://www.opengis.net/ogc", "TM_After");
    private final static QName _TMetBy_QNAME                         = new QName("http://www.opengis.net/ogc", "TM_MetBy");
    private final static QName _TBegins_QNAME                        = new QName("http://www.opengis.net/ogc", "TM_Begins");
    private final static QName _TBefore_QNAME                        = new QName("http://www.opengis.net/ogc", "TM_Before");
    private final static QName _TBegunBy_QNAME                       = new QName("http://www.opengis.net/ogc", "TM_BegunBy");
    private final static QName _TContains_QNAME                      = new QName("http://www.opengis.net/ogc", "TM_Contains");
    private final static QName _TDuring_QNAME                        = new QName("http://www.opengis.net/ogc", "TM_During");
    private final static QName _TemporalOps_QNAME                    = new QName("http://www.opengis.net/ogc", "temporalOps");
    
    private final static QName _Literal_QNAME                        = new QName("http://www.opengis.net/ogc", "Literal");
    private final static QName _PropertyName_QNAME                   = new QName("http://www.opengis.net/ogc", "PropertyName");
    private final static QName _Expression_QNAME                     = new QName("http://www.opengis.net/ogc", "expression");
    private final static QName _Id_QNAME                             = new QName("http://www.opengis.net/ogc", "_Id");
    private final static QName _Add_QNAME                            = new QName("http://www.opengis.net/ogc", "Add");
    private final static QName _Sub_QNAME                            = new QName("http://www.opengis.net/ogc", "Sub");
    private final static QName _Div_QNAME                            = new QName("http://www.opengis.net/ogc", "Div");
    private final static QName _Mul_QNAME                            = new QName("http://www.opengis.net/ogc", "Mul"); 
    private final static QName _FeatureId_QNAME                      = new QName("http://www.opengis.net/ogc", "FeatureId");
    private final static QName _Filter_QNAME                         = new QName("http://www.opengis.net/ogc", "Filter");
    private final static QName _Function_QNAME                       = new QName("http://www.opengis.net/ogc", "Function");
    private final static QName _GmlObjectId_QNAME                    = new QName("http://www.opengis.net/ogc", "GmlObjectId");
    private final static QName _SortBy_QNAME                         = new QName("http://www.opengis.net/ogc", "SortBy");
    
    
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.ogc
     * 
     */
    public ObjectFactory() {
    }


    /**
     * Create an instance of {@link ExistenceCapabilitiesType }
     * 
     */
    public ExistenceCapabilitiesType createExistenceCapabilitiesType() {
        return new ExistenceCapabilitiesType();
    }

    /**
     * Create an instance of {@link PropertyIsNullType }
     * 
     */
    public PropertyIsNullType createPropertyIsNullType() {
        return new PropertyIsNullType();
    }

    /**
     * Create an instance of {@link IdCapabilitiesType }
     * 
     */
    public IdCapabilitiesType createIdCapabilitiesType() {
        return new IdCapabilitiesType();
    }

    /**
     * Create an instance of {@link TemporalCapabilitiesType }
     * 
     */
    public TemporalCapabilitiesType createTemporalCapabilitiesType() {
        return new TemporalCapabilitiesType();
    }

    /**
     * Create an instance of {@link ClassificationOperatorsType }
     * 
     */
    public ClassificationOperatorsType createClassificationOperatorsType() {
        return new ClassificationOperatorsType();
    }

    /**
     * Create an instance of {@link PropertyNameType }
     * 
     */
    public PropertyNameType createPropertyNameType() {
        return new PropertyNameType();
    }

    /**
     * Create an instance of {@link SpatialCapabilitiesType }
     * 
     */
    public SpatialCapabilitiesType createSpatialCapabilitiesType() {
        return new SpatialCapabilitiesType();
    }

 
    /**
     * Create an instance of {@link FID }
     * 
     */
    public FID createFID() {
        return new FID();
    }

    /**
     * Create an instance of {@link SpatialOperatorsType }
     * 
     */
    public SpatialOperatorsType createSpatialOperatorsType() {
        return new SpatialOperatorsType();
    }

    /**
     * Create an instance of {@link DistanceType }
     * 
     */
    public DistanceType createDistanceType() {
        return new DistanceType();
    }

    /**
     * Create an instance of {@link ScalarCapabilitiesType }
     * 
     */
    public ScalarCapabilitiesType createScalarCapabilitiesType() {
        return new ScalarCapabilitiesType();
    }

    /**
     * Create an instance of {@link TemporalOperandsType }
     * 
     */
    public TemporalOperandsType createTemporalOperandsType() {
        return new TemporalOperandsType();
    }

    /**
     * Create an instance of {@link BBOXType }
     * 
     */
    public BBOXType createBBOXType() {
        return new BBOXType();
    }
   
    /**
     * Create an instance of {@link PropertyIsBetweenType }
     * 
     */
    public PropertyIsBetweenType createPropertyIsBetweenType() {
        return new PropertyIsBetweenType();
    }

    /**
     * Create an instance of {@link EID }
     * 
     */
    public EID createEID() {
        return new EID();
    }

    /**
     * Create an instance of {@link LiteralType }
     * 
     */
    public LiteralType createLiteralType() {
        return new LiteralType();
    }

    /**
     * Create an instance of {@link LowerBoundaryType }
     * 
     */
    public LowerBoundaryType createLowerBoundaryType() {
        return new LowerBoundaryType();
    }

   
    /**
     * Create an instance of {@link FunctionNameType }
     * 
     */
    public FunctionNameType createFunctionNameType() {
        return new FunctionNameType();
    }

    /**
     * Create an instance of {@link BinaryComparisonOpType }
     * 
     */
    public BinaryComparisonOpType createBinaryComparisonOpType() {
        return new BinaryComparisonOpType();
    }

    /**
     * Create an instance of {@link SpatialOperatorType }
     * 
     */
    public SpatialOperatorType createSpatialOperatorType() {
        return new SpatialOperatorType();
    }

  
    /**
     * Create an instance of {@link LogicalOperators }
     * 
     */
    public LogicalOperators createLogicalOperators() {
        return new LogicalOperators();
    }

    /**
     * Create an instance of {@link BinaryTemporalOpType }
     * 
     */
    public BinaryTemporalOpType createBinaryTemporalOpType() {
        return new BinaryTemporalOpType();
    }

    /**
     * Create an instance of {@link FunctionsType }
     * 
     */
    public FunctionsType createFunctionsType() {
        return new FunctionsType();
    }

    /**
     * Create an instance of {@link SimpleArithmetic }
     * 
     */
    public SimpleArithmetic createSimpleArithmetic() {
        return new SimpleArithmetic();
    }

    /**
     * Create an instance of {@link DistanceBufferType }
     * 
     */
    public DistanceBufferType createDistanceBufferType() {
        return new DistanceBufferType();
    }

  
    /**
     * Create an instance of {@link FilterCapabilities }
     * 
     */
    public FilterCapabilities createFilterCapabilities() {
        return new FilterCapabilities();
    }

  
    /**
     * Create an instance of {@link BinarySpatialOpType }
     * 
     */
    public BinarySpatialOpType createBinarySpatialOpType() {
        return new BinarySpatialOpType();
    }

    /**
     * Create an instance of {@link TemporalOperatorType }
     * 
     */
    public TemporalOperatorType createTemporalOperatorType() {
        return new TemporalOperatorType();
    }

   
    /**
     * Create an instance of {@link ComparisonOperatorsType }
     * 
     */
    public ComparisonOperatorsType createComparisonOperatorsType() {
        return new ComparisonOperatorsType();
    }

    /**
     * Create an instance of {@link GeometryOperandsType }
     * 
     */
    public GeometryOperandsType createGeometryOperandsType() {
        return new GeometryOperandsType();
    }

    /**
     * Create an instance of {@link PropertyIsLikeType }
     * 
     */
    public PropertyIsLikeType createPropertyIsLikeType() {
        return new PropertyIsLikeType();
    }

    /**
     * Create an instance of {@link UpperBoundaryType }
     * 
     */
    public UpperBoundaryType createUpperBoundaryType() {
        return new UpperBoundaryType();
    }

    /**
     * Create an instance of {@link ArithmeticOperatorsType }
     * 
     */
    public ArithmeticOperatorsType createArithmeticOperatorsType() {
        return new ArithmeticOperatorsType();
    }


    /**
     * Create an instance of {@link FunctionNamesType }
     * 
     */
    public FunctionNamesType createFunctionNamesType() {
        return new FunctionNamesType();
    }

   

    /**
     * Create an instance of {@link TemporalOperatorsType }
     * 
     */
    public TemporalOperatorsType createTemporalOperatorsType() {
        return new TemporalOperatorsType();
    }

    /**
     * Create an instance of {@link ExistenceOperatorsType }
     * 
     */
    public ExistenceOperatorsType createExistenceOperatorsType() {
        return new ExistenceOperatorsType();
    }

    /**
     * Create an instance of {@link ClassificationCapabilitiesType }
     * 
     */
    public ClassificationCapabilitiesType createClassificationCapabilitiesType() {
        return new ClassificationCapabilitiesType();
    }
    
    /**
     * Create an instance of {@link BinaryLogicOpType }
     * 
     */
    public BinaryLogicOpType createBinaryLogicOpType() {
        return new BinaryLogicOpType();
    }
    
    /**
     * Create an instance of {@link BinaryOperatorType }
     * 
     */
    public BinaryOperatorType createBinaryOperatorType() {
        return new BinaryOperatorType();
    }
    
    /**
     * Create an instance of {@link FeatureIdType }
     * 
     */
    public FeatureIdType createFeatureIdType() {
        return new FeatureIdType();
    }
    
    /**
     * Create an instance of {@link FilterType }
     * 
     */
    public FilterType createFilterType() {
        return new FilterType();
    }
    
    /**
     * Create an instance of {@link FunctionType }
     * 
     */
    public FunctionType createFunctionType() {
        return new FunctionType();
    }
    
    /**
     * Create an instance of {@link GmlObjectIdType }
     * 
     */
    public GmlObjectIdType createGmlObjectIdType() {
        return new GmlObjectIdType();
    }
    
    /**
     * Create an instance of {@link SortByType }
     * 
     */
    public SortByType createSortByType() {
        return new SortByType();
    }
    
    /**
     * Create an instance of {@link SortPropertyType }
     * 
     */
    public SortPropertyType createSortPropertyType() {
        return new SortPropertyType();
    }
    
    /**
     * Create an instance of {@link UnaryLogicOpType }
     * 
     */
    public UnaryLogicOpType createUnaryLogicOpType() {
        return new UnaryLogicOpType();
    }
    
    /**
     * Create an instance of {@link AndType }
     * 
     */
    public AndType createAndType() {
        return new AndType();
    }
    
    /**
     * Create an instance of {@link ContainsType }
     * 
     */
    public ContainsType createContainsType() {
        return new ContainsType();
    }
    
    /**
     * Create an instance of {@link CrossesType }
     * 
     */
    public CrossesType createCrossesType() {
        return new CrossesType();
    }
    
    /**
     * Create an instance of {@link DWithinType }
     * 
     */
    public DWithinType createDWithinType() {
        return new DWithinType();
    }
    
    /**
     * Create an instance of {@link DisjointType }
     * 
     */
    public DisjointType createDisjointType() {
        return new DisjointType();
    }
    
    /**
     * Create an instance of {@link EqualsType }
     * 
     */
    public EqualsType createEqualsType() {
        return new EqualsType();
    }
    
    /**
     * Create an instance of {@link IntersectsType }
     * 
     */
    public IntersectsType createIntersectsType() {
        return new IntersectsType();
    }
    
    /**
     * Create an instance of {@link NotType }
     * 
     */
    public NotType createNotType() {
        return new NotType();
    }
    
    /**
     * Create an instance of {@link OrType }
     * 
     */
    public OrType createOrType() {
        return new OrType();
    }
    
    /**
     * Create an instance of {@link PropertyIsEqualToType }
     * 
     */
    public PropertyIsEqualToType createPropertyIsEqualToType() {
        return new PropertyIsEqualToType();
    }
    
    /**
     * Create an instance of {@link PropertyIsNotEqualToType }
     * 
     */
    public PropertyIsNotEqualToType createPropertyIsNotEqualToType() {
        return new PropertyIsNotEqualToType();
    }
    
    /**
     * Create an instance of {@link PropertyIsGreaterThanOrEqualToType }
     * 
     */
    public PropertyIsGreaterThanOrEqualToType createPropertyIsGreaterThanOrEqualToType() {
        return new PropertyIsGreaterThanOrEqualToType();
    }
    
    /**
     * Create an instance of {@link PropertyIsGreaterThanType }
     * 
     */
    public PropertyIsGreaterThanType createPropertyIsGreaterThanType() {
        return new PropertyIsGreaterThanType();
    }
    
    /**
     * Create an instance of {@link PropertyIsLessThanOrEqualToType }
     * 
     */
    public PropertyIsLessThanOrEqualToType createPropertyIsLessThanOrEqualToType() {
        return new PropertyIsLessThanOrEqualToType();
    }
    
    /**
     * Create an instance of {@link TouchesType }
     * 
     */
    public TouchesType createTouchesType() {
        return new TouchesType();
    }
    
    /**
     * Create an instance of {@link WithinType }
     * 
     */
    public WithinType createWithinType() {
        return new WithinType();
    }
    
    /**
     * Create an instance of {@link PropertyIsLessThanType }
     * 
     */
    public PropertyIsLessThanType createPropertyIsLessThanType() {
        return new PropertyIsLessThanType();
    }
    
    /**
     * Create an instance of {@link BeyondType }
     * 
     */
    public BeyondType createBeyondType() {
        return new BeyondType();
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UnaryLogicOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Not", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "logicOps")
    public JAXBElement<NotType> createNot(NotType value) {
        return new JAXBElement<NotType>(_Not_QNAME, NotType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SortByType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "SortBy")
    public JAXBElement<SortByType> createSortBy(SortByType value) {
        return new JAXBElement<SortByType>(_SortBy_QNAME, SortByType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LogicOpsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "logicOps")
    public JAXBElement<LogicOpsType> createLogicOps(LogicOpsType value) {
        return new JAXBElement<LogicOpsType>(_LogicOps_QNAME, LogicOpsType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GmlObjectIdType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "GmlObjectId", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "_Id")
    public JAXBElement<GmlObjectIdType> createGmlObjectId(GmlObjectIdType value) {
        return new JAXBElement<GmlObjectIdType>(_GmlObjectId_QNAME, GmlObjectIdType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FunctionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Function", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "expression")
    public JAXBElement<FunctionType> createFunction(FunctionType value) {
        return new JAXBElement<FunctionType>(_Function_QNAME, FunctionType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FilterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Filter")
    public JAXBElement<FilterType> createFilter(FilterType value) {
        return new JAXBElement<FilterType>(_Filter_QNAME, FilterType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeatureIdType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "FeatureId", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "_Id")
    public JAXBElement<FeatureIdType> createFeatureId(FeatureIdType value) {
        return new JAXBElement<FeatureIdType>(_FeatureId_QNAME, FeatureIdType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Sub", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "expression")
    public JAXBElement<BinaryOperatorType> createSub(BinaryOperatorType value) {
        return new JAXBElement<BinaryOperatorType>(_Sub_QNAME, BinaryOperatorType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Div", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "expression")
    public JAXBElement<BinaryOperatorType> createDiv(BinaryOperatorType value) {
        return new JAXBElement<BinaryOperatorType>(_Div_QNAME, BinaryOperatorType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Mul", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "expression")
    public JAXBElement<BinaryOperatorType> createMul(BinaryOperatorType value) {
        return new JAXBElement<BinaryOperatorType>(_Mul_QNAME, BinaryOperatorType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryOperatorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Add", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "expression")
    public JAXBElement<BinaryOperatorType> createAdd(BinaryOperatorType value) {
        return new JAXBElement<BinaryOperatorType>(_Add_QNAME, BinaryOperatorType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryLogicOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Or", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "logicOps")
    public JAXBElement<OrType> createOr(OrType value) {
        return new JAXBElement<OrType>(_Or_QNAME, OrType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryLogicOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "And", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "logicOps")
    public JAXBElement<AndType> createAnd(AndType value) {
        return new JAXBElement<AndType>(_And_QNAME, AndType.class, null, value);
    }
  
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractIdType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "_Id")
    public JAXBElement<AbstractIdType> createId(AbstractIdType value) {
        return new JAXBElement<AbstractIdType>(_Id_QNAME, AbstractIdType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "PropertyIsLessThan", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsLessThanType> createPropertyIsLessThan(PropertyIsLessThanType value) {
        return new JAXBElement<PropertyIsLessThanType>(_PropertyIsLessThan_QNAME, PropertyIsLessThanType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "PropertyIsGreaterThanOrEqualTo", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsGreaterThanOrEqualToType> createPropertyIsGreaterThanOrEqualTo(PropertyIsGreaterThanOrEqualToType value) {
        return new JAXBElement<PropertyIsGreaterThanOrEqualToType>(_PropertyIsGreaterThanOrEqualTo_QNAME, PropertyIsGreaterThanOrEqualToType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Intersects", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "spatialOps")
    public JAXBElement<IntersectsType> createIntersects(IntersectsType value) {
        return new JAXBElement<IntersectsType>(_Intersects_QNAME, IntersectsType.class, null, value);
    }

     
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpatialOpsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "spatialOps")
    public JAXBElement<SpatialOpsType> createSpatialOps(SpatialOpsType value) {
        return new JAXBElement<SpatialOpsType>(_SpatialOps_QNAME, SpatialOpsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "PropertyIsEqualTo", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsEqualToType> createPropertyIsEqualTo(PropertyIsEqualToType value) {
        return new JAXBElement<PropertyIsEqualToType>(_PropertyIsEqualTo_QNAME, PropertyIsEqualToType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_Overalps", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTOveralps(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TOveralps_QNAME, BinaryTemporalOpType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_Equals", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTEquals(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TEquals_QNAME, BinaryTemporalOpType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Touches", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "spatialOps")
    public JAXBElement<TouchesType> createTouches(TouchesType value) {
        return new JAXBElement<TouchesType>(_Touches_QNAME, TouchesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExpressionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "expression")
    public JAXBElement<ExpressionType> createExpression(ExpressionType value) {
        return new JAXBElement<ExpressionType>(_Expression_QNAME, ExpressionType.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LiteralType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Literal", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "expression")
    public JAXBElement<LiteralType> createLiteral(LiteralType value) {
        return new JAXBElement<LiteralType>(_Literal_QNAME, LiteralType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_Meets", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTMeets(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TMeets_QNAME, BinaryTemporalOpType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_OverlappedBy", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTOverlappedBy(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TOverlappedBy_QNAME, BinaryTemporalOpType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TemporalOpsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "temporalOps")
    public JAXBElement<TemporalOpsType> createTemporalOps(TemporalOpsType value) {
        return new JAXBElement<TemporalOpsType>(_TemporalOps_QNAME, TemporalOpsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_EndedBy", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTEndedBy(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TEndedBy_QNAME, BinaryTemporalOpType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "PropertyIsNotEqualTo", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsNotEqualToType> createPropertyIsNotEqualTo(PropertyIsNotEqualToType value) {
        return new JAXBElement<PropertyIsNotEqualToType>(_PropertyIsNotEqualTo_QNAME, PropertyIsNotEqualToType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "PropertyIsLessThanOrEqualTo", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsLessThanOrEqualToType> createPropertyIsLessThanOrEqualTo(PropertyIsLessThanOrEqualToType value) {
        return new JAXBElement<PropertyIsLessThanOrEqualToType>(_PropertyIsLessThanOrEqualTo_QNAME, PropertyIsLessThanOrEqualToType.class, null, value);
    }

   
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyIsLikeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "PropertyIsLike", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsLikeType> createPropertyIsLike(PropertyIsLikeType value) {
        return new JAXBElement<PropertyIsLikeType>(_PropertyIsLike_QNAME, PropertyIsLikeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "DWithin", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "spatialOps")
    public JAXBElement<DWithinType> createDWithin(DWithinType value) {
        return new JAXBElement<DWithinType>(_DWithin_QNAME, DWithinType.class, null, value);
    }

  
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyIsBetweenType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "PropertyIsBetween", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsBetweenType> createPropertyIsBetween(PropertyIsBetweenType value) {
        return new JAXBElement<PropertyIsBetweenType>(_PropertyIsBetween_QNAME, PropertyIsBetweenType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyNameType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "PropertyName", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "expression")
    public JAXBElement<PropertyNameType> createPropertyName(PropertyNameType value) {
        return new JAXBElement<PropertyNameType>(_PropertyName_QNAME, PropertyNameType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Disjoint", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "spatialOps")
    public JAXBElement<DisjointType> createDisjoint(DisjointType value) {
        return new JAXBElement<DisjointType>(_Disjoint_QNAME, DisjointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Crosses", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "spatialOps")
    public JAXBElement<CrossesType> createCrosses(CrossesType value) {
        return new JAXBElement<CrossesType>(_Crosses_QNAME, CrossesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_Ends", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTEnds(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TEnds_QNAME, BinaryTemporalOpType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Contains", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "spatialOps")
    public JAXBElement<ContainsType> createContains(ContainsType value) {
        return new JAXBElement<ContainsType>(_Contains_QNAME, ContainsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DistanceBufferType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Beyond", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "spatialOps")
    public JAXBElement<BeyondType> createBeyond(BeyondType value) {
        return new JAXBElement<BeyondType>(_Beyond_QNAME, BeyondType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_After", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTAfter(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TAfter_QNAME, BinaryTemporalOpType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComparisonOpsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "comparisonOps")
    public JAXBElement<ComparisonOpsType> createComparisonOps(ComparisonOpsType value) {
        return new JAXBElement<ComparisonOpsType>(_ComparisonOps_QNAME, ComparisonOpsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Equals", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "spatialOps")
    public JAXBElement<EqualsType> createEquals(EqualsType value) {
        return new JAXBElement<EqualsType>(_Equals_QNAME, EqualsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Overlaps", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "spatialOps")
    public JAXBElement<OverlapsType> createOverlaps(OverlapsType value) {
        return new JAXBElement<OverlapsType>(_Overlaps_QNAME, OverlapsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_MetBy", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTMetBy(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TMetBy_QNAME, BinaryTemporalOpType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_Begins", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTBegins(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TBegins_QNAME, BinaryTemporalOpType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_Before", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTBefore(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TBefore_QNAME, BinaryTemporalOpType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryComparisonOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "PropertyIsGreaterThan", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsGreaterThanType> createPropertyIsGreaterThan(PropertyIsGreaterThanType value) {
        return new JAXBElement<PropertyIsGreaterThanType>(_PropertyIsGreaterThan_QNAME, PropertyIsGreaterThanType.class, null, value);
    }

      /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_BegunBy", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTBegunBy(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TBegunBy_QNAME, BinaryTemporalOpType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BBOXType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "BBOX", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "spatialOps")
    public JAXBElement<BBOXType> createBBOX(BBOXType value) {
        return new JAXBElement<BBOXType>(_BBOX_QNAME, BBOXType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_Contains", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTContains(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TContains_QNAME, BinaryTemporalOpType.class, null, value);
    }

    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinaryTemporalOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "TM_During", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "temporalOps")
    public JAXBElement<BinaryTemporalOpType> createTDuring(BinaryTemporalOpType value) {
        return new JAXBElement<BinaryTemporalOpType>(_TDuring_QNAME, BinaryTemporalOpType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BinarySpatialOpType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "Within", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "spatialOps")
    public JAXBElement<WithinType> createWithin(WithinType value) {
        return new JAXBElement<WithinType>(_Within_QNAME, WithinType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PropertyIsNullType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/ogc", name = "PropertyIsNull", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "comparisonOps")
    public JAXBElement<PropertyIsNullType> createPropertyIsNull(PropertyIsNullType value) {
        return new JAXBElement<PropertyIsNullType>(_PropertyIsNull_QNAME, PropertyIsNullType.class, null, value);
    }
    
    
}
