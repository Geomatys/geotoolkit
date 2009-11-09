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
package org.geotoolkit.sld.xml;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.geotoolkit.display2d.ext.pattern.PatternSymbolizer;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.ogc.xml.v110.AndType;
import org.geotoolkit.ogc.xml.v110.BinaryComparisonOpType;
import org.geotoolkit.ogc.xml.v110.BinaryLogicOpType;
import org.geotoolkit.ogc.xml.v110.BinaryOperatorType;
import org.geotoolkit.ogc.xml.v110.ComparisonOpsType;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.ogc.xml.v110.FunctionType;
import org.geotoolkit.ogc.xml.v110.LiteralType;
import org.geotoolkit.ogc.xml.v110.LogicOpsType;
import org.geotoolkit.ogc.xml.v110.LowerBoundaryType;
import org.geotoolkit.ogc.xml.v110.NotType;
import org.geotoolkit.ogc.xml.v110.OrType;
import org.geotoolkit.ogc.xml.v110.PropertyIsBetweenType;
import org.geotoolkit.ogc.xml.v110.PropertyIsEqualToType;
import org.geotoolkit.ogc.xml.v110.PropertyIsGreaterThanOrEqualToType;
import org.geotoolkit.ogc.xml.v110.PropertyIsGreaterThanType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLessThanOrEqualToType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLessThanType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLikeType;
import org.geotoolkit.ogc.xml.v110.PropertyIsNotEqualToType;
import org.geotoolkit.ogc.xml.v110.PropertyIsNullType;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.ogc.xml.v110.SpatialOpsType;
import org.geotoolkit.ogc.xml.v110.UnaryLogicOpType;
import org.geotoolkit.ogc.xml.v110.UpperBoundaryType;
import org.geotoolkit.se.xml.v110.AnchorPointType;
import org.geotoolkit.se.xml.v110.CategorizeType;
import org.geotoolkit.se.xml.v110.ChannelSelectionType;
import org.geotoolkit.se.xml.v110.ColorMapType;
import org.geotoolkit.se.xml.v110.ColorReplacementType;
import org.geotoolkit.se.xml.v110.ContrastEnhancementType;
import org.geotoolkit.se.xml.v110.CoverageStyleType;
import org.geotoolkit.se.xml.v110.DescriptionType;
import org.geotoolkit.se.xml.v110.DisplacementType;
import org.geotoolkit.se.xml.v110.ExternalGraphicType;
import org.geotoolkit.se.xml.v110.FeatureTypeStyleType;
import org.geotoolkit.se.xml.v110.FillType;
import org.geotoolkit.se.xml.v110.FontType;
import org.geotoolkit.se.xml.v110.GeometryType;
import org.geotoolkit.se.xml.v110.GraphicFillType;
import org.geotoolkit.se.xml.v110.GraphicStrokeType;
import org.geotoolkit.se.xml.v110.GraphicType;
import org.geotoolkit.se.xml.v110.HaloType;
import org.geotoolkit.se.xml.v110.ImageOutlineType;
import org.geotoolkit.se.xml.v110.InterpolateType;
import org.geotoolkit.se.xml.v110.InterpolationPointType;
import org.geotoolkit.se.xml.v110.LabelPlacementType;
import org.geotoolkit.se.xml.v110.LegendGraphicType;
import org.geotoolkit.se.xml.v110.LinePlacementType;
import org.geotoolkit.se.xml.v110.LineSymbolizerType;
import org.geotoolkit.se.xml.v110.MarkType;
import org.geotoolkit.se.xml.v110.MethodType;
import org.geotoolkit.se.xml.v110.ModeType;
import org.geotoolkit.se.xml.v110.OnlineResourceType;
import org.geotoolkit.se.xml.v110.ParameterValueType;
import org.geotoolkit.se.xml.v110.PointPlacementType;
import org.geotoolkit.se.xml.v110.PointSymbolizerType;
import org.geotoolkit.se.xml.v110.PolygonSymbolizerType;
import org.geotoolkit.se.xml.v110.RasterSymbolizerType;
import org.geotoolkit.se.xml.v110.RecodeType;
import org.geotoolkit.se.xml.v110.RuleType;
import org.geotoolkit.se.xml.v110.ShadedReliefType;
import org.geotoolkit.se.xml.v110.StrokeType;
import org.geotoolkit.se.xml.v110.SvgParameterType;
import org.geotoolkit.se.xml.v110.TextSymbolizerType;
import org.geotoolkit.se.xml.v110.ThreshholdsBelongToType;

import org.geotoolkit.se.xml.vext.ColorItemType;
import org.geotoolkit.se.xml.vext.PatternSymbolizerType;
import org.geotoolkit.se.xml.vext.RangeType;
import org.geotoolkit.se.xml.vext.RecolorType;
import org.geotoolkit.style.function.Categorize;
import org.geotoolkit.style.function.ColorItem;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.geotoolkit.style.function.Recode;
import org.geotoolkit.style.function.RecodeFunction;
import org.geotoolkit.style.function.RecolorFunction;
import org.geotoolkit.style.function.ThreshholdsBelongTo;

import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.type.Name;
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.style.AnchorPoint;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ColorReplacement;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;
import org.opengis.style.Description;
import org.opengis.style.Displacement;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.ExternalMark;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Fill;
import org.opengis.style.Font;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicFill;
import org.opengis.style.GraphicLegend;
import org.opengis.style.GraphicStroke;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Halo;
import org.opengis.style.LabelPlacement;
import org.opengis.style.LinePlacement;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.PointPlacement;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.SelectedChannelType;
import org.opengis.style.SemanticType;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Stroke;
import org.opengis.style.Style;
import org.opengis.style.StyleVisitor;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GTtoSE110Transformer implements StyleVisitor{

    private static final String GENERIC_ANY = "generic:any";
    private static final String GENERIC_POINT = "generic:point";
    private static final String GENERIC_LINE = "generic:line";
    private static final String GENERIC_POLYGON = "generic:polygon";
    private static final String GENERIC_TEXT = "generic:text";
    private static final String GENERIC_RASTER = "generic:raster";
    
    private static final String VERSION = "1.1.0";
    
    private final org.geotoolkit.sld.xml.v110.ObjectFactory sld_factory_v110;
    private final org.geotoolkit.se.xml.v110.ObjectFactory se_factory;
    private final org.geotoolkit.ogc.xml.v110.ObjectFactory ogc_factory;
    
    public GTtoSE110Transformer(){
        this.sld_factory_v110 = new org.geotoolkit.sld.xml.v110.ObjectFactory();
        this.se_factory = new org.geotoolkit.se.xml.v110.ObjectFactory();
        this.ogc_factory = new org.geotoolkit.ogc.xml.v110.ObjectFactory();
    }

    public JAXBElement<?> extract(Expression exp){
        JAXBElement<?> jax = null;
        
        if(exp instanceof Function){
            final Function function = (Function) exp;
            final FunctionType ft = ogc_factory.createFunctionType();
            ft.setName(function.getName());
            for(final Expression ex : function.getParameters()){
                ft.getExpression().add( extract(ex) );
            }
            jax = ogc_factory.createFunction(ft);
        }else if(exp instanceof Multiply){
            final Multiply multiply = (Multiply)exp;
            final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
            bot.getExpression().add(  extract(multiply.getExpression1()) );
            bot.getExpression().add(  extract(multiply.getExpression2()) );
            jax = ogc_factory.createMul(bot);
        }else if(exp instanceof Literal){
            final LiteralType literal = ogc_factory.createLiteralType();
            literal.setContent( ((Literal)exp).getValue().toString());
            jax = ogc_factory.createLiteral(literal);
        }else if(exp instanceof Add){
            final Add add = (Add)exp;
            final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
            bot.getExpression().add(  extract(add.getExpression1()) );
            bot.getExpression().add(  extract(add.getExpression2()) );
            jax = ogc_factory.createAdd(bot);
        }else if(exp instanceof Divide){
            final Divide divide = (Divide)exp;
            final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
            bot.getExpression().add(  extract(divide.getExpression1()) );
            bot.getExpression().add(  extract(divide.getExpression2()) );
            jax = ogc_factory.createDiv(bot);
        }else if(exp instanceof Subtract){
            final Subtract substract = (Subtract)exp;
            final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
            bot.getExpression().add(  extract(substract.getExpression1()) );
            bot.getExpression().add(  extract(substract.getExpression2()) );
            jax = ogc_factory.createSub(bot);
        }else if(exp instanceof PropertyName){
            final PropertyNameType literal = ogc_factory.createPropertyNameType();
            literal.setContent( ((PropertyName)exp).getPropertyName() );
            jax = ogc_factory.createPropertyName(literal);
        }else if(exp instanceof NilExpression){
            //DO nothing on NILL expression
        }else{
            throw new IllegalArgumentException("Unknowed expression element :" + exp);
        }
        
        //        JAXBElementBinaryOperatorType> 
//                JAXBElementMapItemType> 
//                JAXBElementBinaryOperatorType> 
//                JAXBElementLiteralType> 
//                JAXBElementInterpolateType> 
//                JAXBElementConcatenateType> 
//                JAXBElementChangeCaseType> 
//                JAXBElementPropertyNameType> 
//                JAXBElementTrimType> 
//                JAXBElementBinaryOperatorType> 
//                JAXBElementnet.opengis.ogc.FunctionType> 
//                JAXBElementFormatDateType> 
//                JAXBElementCategorizeType> 
//                JAXBElementBinaryOperatorType> 
//                JAXBElementExpressionType> 
//                JAXBElementInterpolationPointType> 
//                JAXBElementStringLengthType> 
//                JAXBElementRecodeType> String 
//                JAXBElementnet.opengis.se.FunctionType> 
//                JAXBElementFormatNumberType> 
//                JAXBElementSubstringType> 
//                JAXBElementStringPositionType>
        
        
        return jax;
    }
        
    /**
     * Transform a GT Expression in a jaxb parameter value type.
     */
    public ParameterValueType visitExpression(final Expression exp) {
        
        final JAXBElement<?> ele = extract(exp);
        if (ele == null) {
            return null;
        } else {
            final ParameterValueType param = se_factory.createParameterValueType();
            param.getContent().add(extract(exp));
            return param;
        }
        
    }
    
    /**
     * Transform an expression or float array in a scg parameter.
     */
    public SvgParameterType visitSVG(final Object obj, final String value){
        SvgParameterType svg = se_factory.createSvgParameterType();
        svg.setName(value);
        
        if(obj instanceof Expression){
            final Expression exp = (Expression) obj;
            final JAXBElement<?> ele = extract(exp);
            if(ele == null){
                svg = null;
            }else{
                svg.getContent().add(ele);
            }
        }else if(obj instanceof float[]){
            final float[] dashes = (float[]) obj;
            final StringBuilder sb = new StringBuilder();
            for(final float f : dashes){
                sb.append(f);
                sb.append(' ');
            }
            svg.getContent().add(sb.toString().trim());
        }else{
            throw new IllegalArgumentException("Unknowed CSS parameter jaxb structure :" + obj);
        }
                
        return svg;
    }
    
    /**
     * Transform a geometrie name in a geometrytype.
     */
    public GeometryType visitGeometryType(String str){
        final GeometryType geo = se_factory.createGeometryType();
        final PropertyNameType value = ogc_factory.createPropertyNameType();
        if(str == null)str= "";
        value.setContent(str);
        geo.setPropertyName(value);
        return geo;
    }
   
    /**
     * Transform a Feature name in a QName.
     */
    public QName visitName(Name name){
        return new QName(name.getNamespaceURI(), name.getLocalPart());
    }
        
    public JAXBElement<?> visitFilter(Filter filter){
                        
        if(filter.equals(Filter.INCLUDE)){
            return null;
        }if(filter.equals(Filter.EXCLUDE)){
            return null;
        }
                
        if(filter instanceof PropertyIsBetween){
            final PropertyIsBetween pib = (PropertyIsBetween) filter;
            final LowerBoundaryType lbt = ogc_factory.createLowerBoundaryType();
            lbt.setExpression(extract(pib.getLowerBoundary()));
            final UpperBoundaryType ubt = ogc_factory.createUpperBoundaryType();
            ubt.setExpression(extract(pib.getUpperBoundary()));
            final PropertyIsBetweenType bot = new PropertyIsBetweenType(extract(pib.getExpression()), lbt, ubt);
            return ogc_factory.createPropertyIsBetween(bot);
        }else if(filter instanceof PropertyIsEqualTo){
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final BinaryComparisonOpType bot = ogc_factory.createBinaryComparisonOpType();
            bot.getExpression().add( extract(pit.getExpression1()));
            bot.getExpression().add( extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsEqualTo(new PropertyIsEqualToType(bot.getLiteral(),
                    new PropertyNameType(bot.getPropertyName()), bot.getMatchCase()));
        }else if(filter instanceof PropertyIsGreaterThan){
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final BinaryComparisonOpType bot = ogc_factory.createBinaryComparisonOpType();
            bot.getExpression().add( extract(pit.getExpression1()));
            bot.getExpression().add( extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsGreaterThan(new PropertyIsGreaterThanType(bot.getLiteral(),
                    new PropertyNameType(bot.getPropertyName()), bot.getMatchCase()));
        }else if(filter instanceof PropertyIsGreaterThanOrEqualTo){
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final BinaryComparisonOpType bot = ogc_factory.createBinaryComparisonOpType();
            bot.getExpression().add( extract(pit.getExpression1()));
            bot.getExpression().add( extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsGreaterThanOrEqualTo(new PropertyIsGreaterThanOrEqualToType(bot.getLiteral(),
                    new PropertyNameType(bot.getPropertyName()), bot.getMatchCase()));
        }else if(filter instanceof PropertyIsLessThan){
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final BinaryComparisonOpType bot = ogc_factory.createBinaryComparisonOpType();
            bot.getExpression().add( extract(pit.getExpression1()));
            bot.getExpression().add( extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsLessThan(new PropertyIsLessThanType(bot.getLiteral(),
                    new PropertyNameType(bot.getPropertyName()), bot.getMatchCase()));
        }else if(filter instanceof PropertyIsLessThanOrEqualTo){
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final BinaryComparisonOpType bot = ogc_factory.createBinaryComparisonOpType();
            bot.getExpression().add( extract(pit.getExpression1()));
            bot.getExpression().add( extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsLessThanOrEqualTo(new PropertyIsLessThanOrEqualToType(bot.getLiteral(),
                    new PropertyNameType(bot.getPropertyName()), bot.getMatchCase()));
        }else if(filter instanceof PropertyIsLike){
            final PropertyIsLike pis = (PropertyIsLike) filter;
            final PropertyIsLikeType bot = ogc_factory.createPropertyIsLikeType();
            bot.setEscapeChar(pis.getEscape());
            final LiteralType lt = ogc_factory.createLiteralType();
            lt.setContent(pis.getLiteral());
            bot.setLiteral( lt.getStringValue() );
            final PropertyNameType pnt = (PropertyNameType) extract(pis.getExpression()).getValue();
            bot.setPropertyName(pnt);
            bot.setSingleChar(pis.getSingleChar());
            bot.setWildCard(pis.getWildCard());
            return ogc_factory.createPropertyIsLike(bot);
        }else if(filter instanceof PropertyIsNotEqualTo){
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final BinaryComparisonOpType bot = ogc_factory.createBinaryComparisonOpType();
            bot.getExpression().add( extract(pit.getExpression1()));
            bot.getExpression().add( extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsNotEqualTo(new PropertyIsNotEqualToType(bot.getLiteral(),
                    new PropertyNameType(bot.getPropertyName()), bot.getMatchCase()));
        }else if(filter instanceof PropertyIsNull){
            final PropertyIsNull pis = (PropertyIsNull) filter;
            final PropertyIsNullType bot = ogc_factory.createPropertyIsNullType();
            final Object obj = extract(pis.getExpression()).getValue();
            bot.setPropertyName((PropertyNameType) obj);
            
            return ogc_factory.createPropertyIsNull(bot);
        }else if(filter instanceof And){
            final And and = (And) filter;
            final BinaryLogicOpType lot = ogc_factory.createBinaryLogicOpType();
            for(final Filter f : and.getChildren()){
                lot.getLogicOps().add((JAXBElement<? extends LogicOpsType>) visitFilter(f));
            }
            return ogc_factory.createAnd(new AndType(lot.getLogicOps().get(0).getValue(),
                                                     lot.getLogicOps().get(1).getValue()));
        }else if(filter instanceof Or){
            final Or or = (Or) filter;
            final BinaryLogicOpType lot = ogc_factory.createBinaryLogicOpType();
            for(final Filter f : or.getChildren()){
                lot.getLogicOps().add((JAXBElement<? extends LogicOpsType>) visitFilter(f));
            }
            return ogc_factory.createOr(new OrType(lot.getLogicOps().get(0).getValue(),
                                                   lot.getLogicOps().get(1).getValue()));
        }else if(filter instanceof Not){
            final Not not = (Not) filter;
            final UnaryLogicOpType lot = ogc_factory.createUnaryLogicOpType();
            final JAXBElement<?> sf = visitFilter(not.getFilter());
            
            if (sf.getValue() instanceof ComparisonOpsType){
                lot.setComparisonOps((JAXBElement<? extends ComparisonOpsType>) sf);
                return ogc_factory.createNot(new NotType(lot.getComparisonOps().getValue()));
            }
            if(sf.getValue() instanceof LogicOpsType){
                lot.setLogicOps((JAXBElement<? extends LogicOpsType>) sf);
                return ogc_factory.createNot(new NotType(lot.getLogicOps().getValue()));
            }
            if(sf.getValue() instanceof SpatialOpsType){
                lot.setSpatialOps((JAXBElement<? extends SpatialOpsType>) sf);
                return ogc_factory.createNot(new NotType(lot.getSpatialOps().getValue()));
            }
            //should not happen
            throw new IllegalArgumentException("invalide filter element : " + sf);
        }else if(filter instanceof FeatureId){
            
        }else if(filter instanceof BBOX){
            
        }else if(filter instanceof Beyond){
            
        }else if(filter instanceof Contains){
            
        }else if(filter instanceof Crosses){
            
        }else if(filter instanceof DWithin){
            
        }else if(filter instanceof Disjoint){
            
        }else if(filter instanceof Equals){
            
        }else if(filter instanceof Intersects){
            
        }else if(filter instanceof Overlaps){
            
        }else if(filter instanceof Touches){
            
        }else if(filter instanceof Within){
            
        }
        
        throw new IllegalArgumentException("Unknowed filter element : " + filter +" class :" + filter.getClass());
    }
    
    public FilterType visit(Filter filter) {
        final FilterType ft = ogc_factory.createFilterType();
        final JAXBElement<?> sf = visitFilter(filter);

        if(sf == null){
            return null;
        }else if (sf.getValue() instanceof ComparisonOpsType) {
            ft.setComparisonOps((JAXBElement<? extends ComparisonOpsType>) sf);
        } else if (sf.getValue() instanceof LogicOpsType) {
            ft.setLogicOps((JAXBElement<? extends LogicOpsType>) sf);
        } else if (sf.getValue() instanceof SpatialOpsType) {
            ft.setSpatialOps((JAXBElement<? extends SpatialOpsType>) sf);
        } else {
            //should not happen
            throw new IllegalArgumentException("invalide filter element : " + sf);
        }
        return ft;
    }
    
    /**
     * Transform a Unit to the corresponding SLD string.
     */
    public String visitUOM(Unit<Length> uom) {
        if(uom == null) return null;
        
        if(uom.equals(SI.METER) || uom.equals(SI.METRE)){
            return "http://www.opengeospatial.org/se/units/metre";
        }else if(uom.equals(NonSI.FOOT) ){
            return "http://www.opengeospatial.org/se/units/foot";
        }else{
            return "http://www.opengeospatial.org/se/units/pixel";
        }
    }
    
    /**
     * Transform a GT Style in Jaxb UserStyle
     */
    @Override
    public org.geotoolkit.sld.xml.v110.UserStyle visit(Style style, Object data) {
        final org.geotoolkit.sld.xml.v110.UserStyle userStyle = sld_factory_v110.createUserStyle();
        userStyle.setName(style.getName());
        userStyle.setDescription(visit(style.getDescription(), null));
        userStyle.setIsDefault(style.isDefault());
        
        for(final FeatureTypeStyle fts : style.featureTypeStyles()){
            userStyle.getFeatureTypeStyleOrCoverageStyleOrOnlineResource().add(visit(fts,null));
        }
        
        return userStyle;
    }

    /**
     * Transform a GT FTS in Jaxb FeatureTypeStyle or CoveragaStyle or OnlineResource.
     */
    @Override
    public Object visit(FeatureTypeStyle fts, Object data) {
        if(fts.getOnlineResource() != null){
            //we store only the online resource
            return visit(fts.getOnlineResource(), null);
        } else {
            Object obj = null;
            
            //try to figure out if we have here a coverage FTS or not
            boolean isCoverage = false;
            if(fts.semanticTypeIdentifiers().contains(SemanticType.RASTER)){
                isCoverage = true;
            }else if(fts.semanticTypeIdentifiers().contains(SemanticType.ANY) || fts.semanticTypeIdentifiers().isEmpty()){
                if( fts.getFeatureInstanceIDs() == null || fts.getFeatureInstanceIDs().getIdentifiers().isEmpty()){
                    
                    //try to find a coverage style
                    ruleLoop :
                    for(final Rule r : fts.rules()){
                        for(final Symbolizer s : r.symbolizers()){
                            if(s instanceof RasterSymbolizer){
                               isCoverage = true;
                               break ruleLoop;
                            }
                        }
                    }
                }else{
                    isCoverage = false;
                }
            }else{
                isCoverage = false;
            }
            
            //create the sld FTS
            if(isCoverage){
                //coverage type
                final CoverageStyleType cst = se_factory.createCoverageStyleType();
                
                if(!fts.featureTypeNames().isEmpty()){
                    cst.setCoverageName(fts.featureTypeNames().iterator().next().toString());
                }
                
                cst.setDescription(visit(fts.getDescription(), null));
                cst.setName(fts.getName());
                
                for(final SemanticType semantic : fts.semanticTypeIdentifiers()){
                    
                    if(SemanticType.ANY.equals(semantic)){
                        cst.getSemanticTypeIdentifier().add(GENERIC_ANY);
                    }else if(SemanticType.POINT.equals(semantic)){
                        cst.getSemanticTypeIdentifier().add(GENERIC_POINT);
                    }else if(SemanticType.LINE.equals(semantic)){
                        cst.getSemanticTypeIdentifier().add(GENERIC_LINE);
                    }else if(SemanticType.POLYGON.equals(semantic)){
                        cst.getSemanticTypeIdentifier().add(GENERIC_POLYGON);
                    }else if(SemanticType.TEXT.equals(semantic)){
                        cst.getSemanticTypeIdentifier().add(GENERIC_TEXT);
                    }else if(SemanticType.RASTER.equals(semantic)){
                        cst.getSemanticTypeIdentifier().add(GENERIC_RASTER);
                    }else{
                        cst.getSemanticTypeIdentifier().add(semantic.identifier());
                    }
                    
                }
                
                for(final Rule rule : fts.rules()){
                    cst.getRuleOrOnlineResource().add(visit(rule,null));
                }
                
                obj = cst;
            }else{
                //feature type
                final FeatureTypeStyleType ftst = se_factory.createFeatureTypeStyleType();
                
                if(!fts.featureTypeNames().isEmpty()){
                    final Name name = fts.featureTypeNames().iterator().next();
                    final String pre = name.getNamespaceURI();
                    final String sep = name.getSeparator();
                    final String local = name.getLocalPart();
                    ftst.setFeatureTypeName(new QName(pre+sep, local));
                }
                
                ftst.setDescription(visit(fts.getDescription(), null));
                ftst.setName(fts.getName());
                
                for(final SemanticType semantic : fts.semanticTypeIdentifiers()){
                    
                    if(SemanticType.ANY.equals(semantic)){
                        ftst.getSemanticTypeIdentifier().add(GENERIC_ANY);
                    }else if(SemanticType.POINT.equals(semantic)){
                        ftst.getSemanticTypeIdentifier().add(GENERIC_POINT);
                    }else if(SemanticType.LINE.equals(semantic)){
                        ftst.getSemanticTypeIdentifier().add(GENERIC_LINE);
                    }else if(SemanticType.POLYGON.equals(semantic)){
                        ftst.getSemanticTypeIdentifier().add(GENERIC_POLYGON);
                    }else if(SemanticType.TEXT.equals(semantic)){
                        ftst.getSemanticTypeIdentifier().add(GENERIC_TEXT);
                    }else if(SemanticType.RASTER.equals(semantic)){
                        ftst.getSemanticTypeIdentifier().add(GENERIC_RASTER);
                    }else{
                        ftst.getSemanticTypeIdentifier().add(semantic.identifier());
                    }
                    
                }
                
                for(final Rule rule : fts.rules()){
                    ftst.getRuleOrOnlineResource().add(visit(rule,null));
                }
                
                obj = ftst;
            }
            
            return obj;
        }
    }

    /**
     * Transform a GT rule in jaxb rule or OnlineResource
     */
    @Override
    public Object visit(Rule rule, Object data) {
        if(rule.getOnlineResource() != null){
            //we store only the online resource
            return visit(rule.getOnlineResource(), null);
        }
        
        final RuleType rt = se_factory.createRuleType();
        rt.setName(rule.getName());
        rt.setDescription(visit(rule.getDescription(),null));
        
        if(rule.isElseFilter()){
            rt.setElseFilter(se_factory.createElseFilterType());
        }else if(rule.getFilter() != null){
            rt.setFilter( visit(rule.getFilter()) );
        }
        
        if(rule.getLegend() != null){
            rt.setLegendGraphic(visit(rule.getLegend(),null));
        }
        
        rt.setMaxScaleDenominator(rule.getMaxScaleDenominator());
        rt.setMinScaleDenominator(rule.getMinScaleDenominator());
        
        for(final Symbolizer symbol : rule.symbolizers()){
            if(symbol instanceof LineSymbolizer){
                rt.getSymbolizer().add( visit((LineSymbolizer)symbol,null));
            }else if(symbol instanceof PolygonSymbolizer){
                rt.getSymbolizer().add( visit((PolygonSymbolizer)symbol,null));
            }else if(symbol instanceof PointSymbolizer){
                rt.getSymbolizer().add( visit((PointSymbolizer)symbol,null));
            }else if(symbol instanceof RasterSymbolizer){
                rt.getSymbolizer().add( visit((RasterSymbolizer)symbol,null));
            }else if(symbol instanceof TextSymbolizer){
                rt.getSymbolizer().add( visit((TextSymbolizer)symbol,null));
            }else if(symbol instanceof PatternSymbolizer){
                rt.getSymbolizer().add( visit((PatternSymbolizer)symbol,null));
            }else if(symbol instanceof ExtensionSymbolizer){
                //TODO provide jaxb binding for extension symbolizers
//                rt.getSymbolizer().add( visit((ExtensionSymbolizer)symbol,null));
            }
        }
        
        return rt;
    }

    /**
     * Transform a GT point symbol in jaxb point symbol.
     */
    @Override
    public JAXBElement<PointSymbolizerType> visit(PointSymbolizer point, Object data) {
        final PointSymbolizerType pst = se_factory.createPointSymbolizerType();
        pst.setName( point.getName() );
        pst.setDescription( visit(point.getDescription(),null) );
        pst.setUom( visitUOM(point.getUnitOfMeasure()));
        pst.setGeometry( visitGeometryType(point.getGeometryPropertyName() ) );
        
        if(point.getGraphic() != null){
            pst.setGraphic( visit(point.getGraphic(),null) );
        }
        return se_factory.createPointSymbolizer(pst);
    }

    /**
     * Transform a GT line symbol in jaxb line symbol.
     */
    @Override
    public JAXBElement<LineSymbolizerType> visit(LineSymbolizer line, Object data) {
        final LineSymbolizerType lst = se_factory.createLineSymbolizerType();
        lst.setName( line.getName() );
        lst.setDescription( visit(line.getDescription(),null) );
        lst.setUom( visitUOM(line.getUnitOfMeasure()));
        lst.setGeometry( visitGeometryType(line.getGeometryPropertyName() ) );
        
        if(line.getStroke() != null){
            lst.setStroke( visit(line.getStroke(),null) );
        }
        lst.setPerpendicularOffset( visitExpression(line.getPerpendicularOffset()) );
        return se_factory.createLineSymbolizer(lst);
    }

    /**
     * Transform a GT polygon symbol in a jaxb version.
     */
    @Override
    public JAXBElement<PolygonSymbolizerType> visit(PolygonSymbolizer polygon, Object data) {
        final PolygonSymbolizerType pst = se_factory.createPolygonSymbolizerType();
        pst.setName( polygon.getName() );
        pst.setDescription( visit(polygon.getDescription(),null) );
        pst.setUom( visitUOM(polygon.getUnitOfMeasure()));
        pst.setGeometry( visitGeometryType(polygon.getGeometryPropertyName() ) );
        
        if(polygon.getDisplacement() != null){
            pst.setDisplacement( visit(polygon.getDisplacement(), null) );
        }
        
        if(polygon.getFill() != null){
            pst.setFill( visit(polygon.getFill(),null) );
        }
        
        pst.setPerpendicularOffset( visitExpression(polygon.getPerpendicularOffset()) );
        
        if(polygon.getStroke() != null){
            pst.setStroke( visit(polygon.getStroke(),null) );
        }
        
        return se_factory.createPolygonSymbolizer(pst);
    }

    /**
     * Transform a GT text symbol in jaxb symbol.
     */
    @Override
    public JAXBElement<TextSymbolizerType> visit(TextSymbolizer text, Object data) {
        final TextSymbolizerType tst = se_factory.createTextSymbolizerType();
        tst.setName( text.getName() );
        tst.setDescription( visit(text.getDescription(),null) );
        tst.setUom( visitUOM(text.getUnitOfMeasure()));
        tst.setGeometry( visitGeometryType(text.getGeometryPropertyName() ) );
        
        if(text.getHalo() != null){
            tst.setHalo( visit(text.getHalo(), null) );
        }
        
        if(text.getFont() != null){
            tst.setFont( visit(text.getFont(),null) );
        }
        
        tst.setLabel( visitExpression(text.getLabel()) );
        
        if(text.getLabelPlacement() != null){
            tst.setLabelPlacement( visit(text.getLabelPlacement(), null) );
        }
        
        if(text.getFill() != null){
            tst.setFill( visit(text.getFill(),null) );
        }
        
        return se_factory.createTextSymbolizer(tst);
    }

    /**
     * Transform a GT raster symbolizer in jaxb raster symbolizer.
     */
    @Override
    public JAXBElement<RasterSymbolizerType> visit(RasterSymbolizer raster, Object data) {
        final RasterSymbolizerType tst = se_factory.createRasterSymbolizerType();
        tst.setName( raster.getName() );
        tst.setDescription( visit(raster.getDescription(),null) );
        tst.setUom( visitUOM(raster.getUnitOfMeasure()));
        tst.setGeometry( visitGeometryType(raster.getGeometryPropertyName() ) );
        
        if(raster.getChannelSelection() != null){
            tst.setChannelSelection( visit(raster.getChannelSelection(),null) );
        }
        
        if(raster.getColorMap() != null){
            tst.setColorMap( visit(raster.getColorMap(), null) );
        }
        
        if(raster.getContrastEnhancement() != null){
            tst.setContrastEnhancement( visit(raster.getContrastEnhancement(), null) );
        }
        
        if(raster.getImageOutline() != null){
            final ImageOutlineType iot = se_factory.createImageOutlineType();
            if(raster.getImageOutline() instanceof LineSymbolizer){
                final LineSymbolizer ls = (LineSymbolizer) raster.getImageOutline();
                iot.setLineSymbolizer( visit(ls, null).getValue() );
                tst.setImageOutline(iot);
            }else if(raster.getImageOutline() instanceof PolygonSymbolizer){
                final PolygonSymbolizer ps = (PolygonSymbolizer)raster.getImageOutline();
                iot.setPolygonSymbolizer( visit(ps,null).getValue() );
                tst.setImageOutline(iot);
            }            
        }
        
        tst.setOpacity( visitExpression(raster.getOpacity()) );
        
        if(raster.getOverlapBehavior() != null){
            tst.setOverlapBehavior( visit(raster.getOverlapBehavior(), null) );
        }
        
        if(raster.getShadedRelief() != null){
            tst.setShadedRelief( visit(raster.getShadedRelief(), null) );
        }
        
        return se_factory.createRasterSymbolizer(tst);
    }

    /**
     * Transform a GT raster symbolizer in jaxb raster symbolizer.
     */
    public JAXBElement<PatternSymbolizerType> visit(PatternSymbolizer pattern, Object data) {
        final PatternSymbolizerType tst = se_factory.createPatternSymbolizerType();
        tst.setName( pattern.getName() );
        tst.setDescription( visit(pattern.getDescription(),null) );
        tst.setUom( visitUOM(pattern.getUnitOfMeasure()));

        if(pattern.getChannel() != null){
            tst.setChannel( visitExpression(pattern.getChannel()) );
        }
        
        if(ThreshholdsBelongTo.PRECEDING == pattern.getBelongTo()){
            tst.setThreshholdsBelongTo(ThreshholdsBelongToType.PRECEDING);
        }else{
            tst.setThreshholdsBelongTo(ThreshholdsBelongToType.SUCCEEDING);
        }

        Map<Expression,List<Symbolizer>> ranges = pattern.getRanges();
        for(Map.Entry<Expression,List<Symbolizer>> entry : ranges.entrySet()){
            tst.getRange().add(visitRange(entry.getKey(), entry.getValue()));
        }

        return se_factory.createPatternSymbolizer(tst);
    }

    public JAXBElement<RangeType> visitRange(Expression thredhold, List<Symbolizer> symbols){
        final RangeType type = se_factory.createRangeType();

        if(thredhold != null){
            type.setThreshold(visitExpression(thredhold));
        }

        for(final Symbolizer symbol : symbols){
            if(symbol instanceof LineSymbolizer){
                type.getSymbolizer().add( visit((LineSymbolizer)symbol,null));
            }else if(symbol instanceof PolygonSymbolizer){
                type.getSymbolizer().add( visit((PolygonSymbolizer)symbol,null));
            }else if(symbol instanceof PointSymbolizer){
                type.getSymbolizer().add( visit((PointSymbolizer)symbol,null));
            }else if(symbol instanceof RasterSymbolizer){
                type.getSymbolizer().add( visit((RasterSymbolizer)symbol,null));
            }else if(symbol instanceof TextSymbolizer){
                type.getSymbolizer().add( visit((TextSymbolizer)symbol,null));
            }else if(symbol instanceof PatternSymbolizer){
                type.getSymbolizer().add( visit((PatternSymbolizer)symbol,null));
            }else if(symbol instanceof ExtensionSymbolizer){
                //TODO provide jaxb binding for extension symbolizers
//                rt.getSymbolizer().add( visit((ExtensionSymbolizer)symbol,null));
            }
        }
        
        return se_factory.createRange(type);
    }

    @Override
    public Object visit(ExtensionSymbolizer ext, Object data){
        return null;
    }

    /**
     * transform a GT description in jaxb description.
     */
    @Override
    public DescriptionType visit(Description description, Object data) {
        final DescriptionType dt = se_factory.createDescriptionType();
        if(description != null){
            if(description.getTitle() != null)    dt.setTitle(description.getTitle().toString());
            if(description.getAbstract() != null) dt.setAbstract(description.getAbstract().toString());
        }
        return dt;
    }

    /**
     * Transform a GT displacement in jaxb displacement.
     */
    @Override
    public DisplacementType visit(Displacement displacement, Object data) {
        final DisplacementType disp = se_factory.createDisplacementType();
        disp.setDisplacementX( visitExpression(displacement.getDisplacementX()) );
        disp.setDisplacementY( visitExpression(displacement.getDisplacementY()) );
        return disp;
    }

    /**
     * Transform a GT fill in jaxb fill.
     */
    @Override
    public FillType visit(Fill fill, Object data) {
        final FillType ft = se_factory.createFillType();
        
        if(fill.getGraphicFill() != null){
            ft.setGraphicFill( visit(fill.getGraphicFill(),null) );
        }
        
        final List<SvgParameterType> svgs = ft.getSvgParameter();
        svgs.add( visitSVG(fill.getColor(), SEJAXBStatics.FILL) );
        svgs.add( visitSVG(fill.getOpacity(), SEJAXBStatics.FILL_OPACITY) );
                
        return ft;
    }

    /**
     * Transform a GT Font in jaxb font.
     */
    @Override
    public FontType visit(Font font, Object data) {
        final FontType ft = se_factory.createFontType();
        
        final List<SvgParameterType> svgs = ft.getSvgParameter();
        for(final Expression exp : font.getFamily() ){
            svgs.add( visitSVG(exp, SEJAXBStatics.FONT_FAMILY) );
        }
        
        svgs.add( visitSVG(font.getSize(), SEJAXBStatics.FONT_SIZE) );
        svgs.add( visitSVG(font.getStyle(), SEJAXBStatics.FONT_STYLE) );
        svgs.add( visitSVG(font.getWeight(), SEJAXBStatics.FONT_WEIGHT) );
        
        return ft;
    }

    /**
     * Transform a GT stroke in jaxb stroke.
     */
    @Override
    public StrokeType visit(Stroke stroke, Object data) {
        final StrokeType st = se_factory.createStrokeType();
        
        if(stroke.getGraphicFill() != null){
            st.setGraphicFill( visit(stroke.getGraphicFill(),null) );
        }else if(stroke.getGraphicStroke() != null){
            st.setGraphicStroke( visit(stroke.getGraphicStroke(),null) );
        }
                
        final List<SvgParameterType> svgs = st.getSvgParameter();
        svgs.add( visitSVG(stroke.getColor(), SEJAXBStatics.STROKE) );
        if(stroke.getDashArray() != null){
            svgs.add( visitSVG(stroke.getDashArray(), SEJAXBStatics.STROKE_DASHARRAY) );
        }
        svgs.add( visitSVG(stroke.getDashOffset(), SEJAXBStatics.STROKE_DASHOFFSET) );
        svgs.add( visitSVG(stroke.getLineCap(), SEJAXBStatics.STROKE_LINECAP) );
        svgs.add( visitSVG(stroke.getLineJoin(), SEJAXBStatics.STROKE_LINEJOIN) );
        svgs.add( visitSVG(stroke.getOpacity(), SEJAXBStatics.STROKE_OPACITY) );
        svgs.add( visitSVG(stroke.getWidth(), SEJAXBStatics.STROKE_WIDTH) );
        
        return st;
    }

    /**
     * transform a GT graphic in jaxb graphic
     */
    @Override
    public GraphicType visit(Graphic graphic, Object data) {
        final GraphicType gt = se_factory.createGraphicType();
        gt.setAnchorPoint( visit(graphic.getAnchorPoint(),null) );
        for(final GraphicalSymbol gs : graphic.graphicalSymbols()){
            if(gs instanceof Mark){
                final Mark mark = (Mark) gs;
                gt.getExternalGraphicOrMark().add( visit(mark,null) );
            }else if(gs instanceof ExternalMark){
                final ExternalMark ext = (ExternalMark) gs;
                gt.getExternalGraphicOrMark().add( visit(ext,null) );
            }else if(gs instanceof ExternalGraphic){
                final ExternalGraphic ext = (ExternalGraphic) gs;
                gt.getExternalGraphicOrMark().add( visit(ext,null));
            }
        }
        
        gt.setDisplacement( visit(graphic.getDisplacement(),null) );
        gt.setOpacity( visitExpression(graphic.getOpacity()) );
        gt.setRotation( visitExpression(graphic.getRotation()));
        gt.setSize( visitExpression(graphic.getSize()));
        return gt;
    }

    /**
     * Transform a GT graphic fill in jaxb graphic fill.
     */
    @Override
    public GraphicFillType visit(GraphicFill graphicFill, Object data) {
        final GraphicFillType gft = se_factory.createGraphicFillType();
        gft.setGraphic( visit((Graphic)graphicFill,null) );
        return gft;
    }

    /**
     * Transform a GT graphic stroke in jaxb graphic stroke.
     */
    @Override
    public GraphicStrokeType visit(GraphicStroke graphicStroke, Object data) {
        final GraphicStrokeType gst = se_factory.createGraphicStrokeType();
        gst.setGraphic( visit((Graphic)graphicStroke,null) );
        gst.setGap( visitExpression(graphicStroke.getGap()) );
        gst.setInitialGap( visitExpression(graphicStroke.getInitialGap()) );
        return gst;
    }

    @Override
    public MarkType visit(Mark mark, Object data) {
        final MarkType mt = se_factory.createMarkType();
        mt.setFill( visit(mark.getFill(),null) );
        mt.setStroke( visit(mark.getStroke(),null) );
        
        if(mark.getExternalMark() != null){
            mt.setOnlineResource( visit(mark.getExternalMark().getOnlineResource(),null) );
            mt.setFormat(mark.getExternalMark().getFormat());
            mt.setMarkIndex( new BigInteger( String.valueOf(mark.getExternalMark().getMarkIndex())) );
            
            //TODO insert the inline icone
//            mt.setInlineContent(mark.getExternalMark().getInlineContent());
            
        }else{
            mt.setWellKnownName(mark.getWellKnownName().toString());
        }
        
        return mt;
    }

    /**
     * Not usable for SLD, See visit(Mark) method.
     */
    @Override
    public Object visit(ExternalMark externalMark, Object data) {
        return null;
    }

    @Override
    public ExternalGraphicType visit(ExternalGraphic externalGraphic, Object data) {
        final ExternalGraphicType egt = se_factory.createExternalGraphicType();
        egt.setFormat(externalGraphic.getFormat());
        
        System.out.println(externalGraphic.getOnlineResource());
        System.out.println(visit(externalGraphic.getOnlineResource(), null));
        
        if(externalGraphic.getInlineContent() != null){
            //TODO insert inline image
        }
        
        if(externalGraphic.getOnlineResource() != null){
            egt.setOnlineResource(  visit(externalGraphic.getOnlineResource(), null) );
        }
        
        for(final ColorReplacement cr : externalGraphic.getColorReplacements()){
            egt.getColorReplacement().add(visit(cr, data));
        }
        
        return egt;
    }

    /**
     * transform a GT point placement in jaxb point placement.
     */
    @Override
    public PointPlacementType visit(PointPlacement pointPlacement, Object data) {
        final PointPlacementType ppt = se_factory.createPointPlacementType();
        ppt.setAnchorPoint( visit(pointPlacement.getAnchorPoint(), null) );
        ppt.setDisplacement( visit(pointPlacement.getDisplacement(), null) );
        ppt.setRotation( visitExpression(pointPlacement.getRotation()) );
        return ppt;
    }

    /**
     * transform a GT anchor point in jaxb anchor point.
     */
    @Override
    public AnchorPointType visit(AnchorPoint anchorPoint, Object data) {
        final AnchorPointType apt = se_factory.createAnchorPointType();
        apt.setAnchorPointX( visitExpression(anchorPoint.getAnchorPointX()) );
        apt.setAnchorPointY( visitExpression(anchorPoint.getAnchorPointY()) );
        return apt;
    }

    /**
     * transform a GT lineplacement in jaxb line placement.
     */
    @Override
    public LinePlacementType visit(LinePlacement linePlacement, Object data) {
        final LinePlacementType lpt = se_factory.createLinePlacementType();
        lpt.setGap( visitExpression(linePlacement.getGap()) );
        lpt.setGeneralizeLine( linePlacement.isGeneralizeLine() );
        lpt.setInitialGap( visitExpression(linePlacement.getInitialGap()) );
        lpt.setIsAligned( linePlacement.IsAligned() );
        lpt.setIsRepeated( linePlacement.isRepeated() );
        lpt.setPerpendicularOffset( visitExpression(linePlacement.getPerpendicularOffset()) );        
        return lpt;
    }

    /**
     * Transform a GT label placement in jaxb label placement.
     * @return
     */
    public LabelPlacementType visit(LabelPlacement labelPlacement, Object data) {
        final LabelPlacementType lpt = se_factory.createLabelPlacementType();
        if(labelPlacement instanceof LinePlacement){
            final LinePlacement lp = (LinePlacement) labelPlacement;
            lpt.setLinePlacement( visit(lp, null) );
        }else if(labelPlacement instanceof PointPlacement){
            final PointPlacement pp = (PointPlacement) labelPlacement;
            lpt.setPointPlacement( visit(pp, null) );
        }
        return lpt;
    }

    /**
     * Transform a GT graphicLegend in jaxb graphic legend
     */
    @Override
    public LegendGraphicType visit(GraphicLegend graphicLegend, Object data) {
        final LegendGraphicType lgt = se_factory.createLegendGraphicType();
        lgt.setGraphic( visit((Graphic)graphicLegend,null) );
        return lgt;
    }

    /**
     * Transform a GT onlineResource in jaxb online resource.
     */
    public org.geotoolkit.se.xml.v110.OnlineResourceType visit(OnlineResource onlineResource, Object data) {
        final OnlineResourceType ort = se_factory.createOnlineResourceType();
        try {
            ort.setHref(onlineResource.getLinkage().toURL().toString());
        } catch (MalformedURLException ex) {
            Logging.getLogger(GTtoSE110Transformer.class).log(Level.SEVERE, null, ex);
        }
        return ort;
    }

    /**
     * transform a GT halo in a jaxb halo.
     */
    @Override
    public HaloType visit(Halo halo, Object data) {
        final HaloType ht = se_factory.createHaloType();
        ht.setFill( visit(halo.getFill(),null) );
        ht.setRadius( visitExpression(halo.getRadius()) );
        return ht;
    }

    @Override
    public ColorMapType visit(ColorMap colorMap, Object data) {
//TODO Fix that when better undestanding raster functions.
        final org.geotoolkit.se.xml.v110.ColorMapType cmt = se_factory.createColorMapType();
        
        final Function fct = colorMap.getFunction();
        if(fct instanceof Categorize){
            cmt.setCategorize(visit((Categorize)fct));
        }else if(fct instanceof Interpolate){
            cmt.setInterpolate(visit((Interpolate)fct));
        }
        
        return cmt;
    }
    
    public CategorizeType visit(Categorize categorize){
        final CategorizeType type = se_factory.createCategorizeType();
        type.setFallbackValue(categorize.getFallbackValue().getValue().toString());
        type.setLookupValue(visitExpression(categorize.getLookupValue()));
        
        if(ThreshholdsBelongTo.PRECEDING == categorize.getBelongTo()){
            type.setThreshholdsBelongTo(ThreshholdsBelongToType.PRECEDING);
        }else{
            type.setThreshholdsBelongTo(ThreshholdsBelongToType.SUCCEEDING);
        }
        
        final Map<Expression,Expression> steps = categorize.getThresholds();
        final Iterator<Expression> ite = steps.keySet().iterator();
        type.setValue(visitExpression(ite.next()));
        
        final List<JAXBElement<ParameterValueType>> elements = type.getThresholdAndTValue();
        elements.clear();
        while(ite.hasNext()){
            final Expression key = ite.next();
            final Expression val = steps.get(key);
            elements.add( se_factory.createDateValue(visitExpression(key)) );
            elements.add( se_factory.createDateValue(visitExpression(val)) );
        }
        
        return type;
    }
    
    public InterpolateType visit(Interpolate interpolate){
        final InterpolateType type = se_factory.createInterpolateType();
        type.setFallbackValue(interpolate.getFallbackValue().getValue().toString());
        type.setLookupValue(visitExpression(interpolate.getLookupValue()));
        
        if(interpolate.getMethod() == Method.COLOR){
            type.setMethod(MethodType.COLOR);
        }else{
            type.setMethod(MethodType.NUMERIC);
        }
        
        final Mode mode = interpolate.getMode();
        if(mode == Mode.COSINE){
            type.setMode(ModeType.COSINE);
        }else if( mode == Mode.CUBIC){
            type.setMode(ModeType.CUBIC);
        }else{
            type.setMode(ModeType.LINEAR);
        }
        
        final List<InterpolationPointType> points = type.getInterpolationPoint();
        points.clear();
        for(final InterpolationPoint ip : interpolate.getInterpolationPoints()){
            final InterpolationPointType point = se_factory.createInterpolationPointType();
            point.setData(ip.getData());
            point.setValue(visitExpression(ip.getValue()));
            points.add(point);
        }
        
        return type;
    }
    
    @Override
    public ColorReplacementType visit(ColorReplacement colorReplacement, Object data) {
        final ColorReplacementType crt = se_factory.createColorReplacementType();
        final Function fct = colorReplacement.getRecoding();

        if(fct instanceof RecolorFunction){
            final RecolorFunction rf = (RecolorFunction) fct;
            crt.setRecolor(visit(rf));
        }

//        if(fct instanceof RecodeFunction){
//            final RecodeFunction recode = (RecodeFunction) fct;
//            final RecodeType rt = se_factory.createRecodeType();
//
//            for(final Expression exp : recode.getParameters()){
//                final MapItemType mit = se_factory.createMapItemType();
//                mit.setValue(visitExpression(exp));
//                rt.getMapItem().add(mit);
//            }
//
//            rt.setLookupValue(visitExpression(FactoryFinder.getFilterFactory(null).literal(RecodeFunction.RASTER_DATA)));
//            crt.setRecode(rt);
//        }
        return crt;
    }

    public RecolorType visit(RecolorFunction fct){
        RecolorType rt = new RecolorType();

        for(ColorItem item : fct.getColorItems()){
            final ColorItemType cit = new ColorItemType();
            final Literal data = item.getSourceColor();
            final Literal value = item.getTargetColor();
            cit.setData(visitExpression(data));
            cit.setValue(visitExpression(value));
            rt.getColorItem().add(cit);
        }

        return rt;
    }


    /**
     * Transform a GT constrast enchancement in jaxb constrast enchancement
     */
    @Override
    public ContrastEnhancementType visit(ContrastEnhancement contrastEnhancement, Object data) {
        final ContrastEnhancementType cet = se_factory.createContrastEnhancementType();
        cet.setGammaValue(contrastEnhancement.getGammaValue().evaluate(null, Double.class));
        
        final ContrastMethod cm = contrastEnhancement.getMethod();
        if(ContrastMethod.HISTOGRAM.equals(cm)){
            cet.setHistogram(se_factory.createHistogramType());
        }else if(ContrastMethod.NORMALIZE.equals(cm)){
            cet.setNormalize(se_factory.createNormalizeType());
        }
        
        return cet;
    }

    /**
     * Transform a GT channel selection in jaxb channel selection.
     */
    @Override
    public ChannelSelectionType visit(ChannelSelection channelSelection, Object data) {
        final ChannelSelectionType cst = se_factory.createChannelSelectionType();
        
        if(channelSelection.getRGBChannels() != null){
            SelectedChannelType[] scts = channelSelection.getRGBChannels();
            cst.setRedChannel( visit(scts[0], null) );
            cst.setGreenChannel( visit(scts[1], null) );
            cst.setBlueChannel( visit(scts[2], null) );
            
        }else if(channelSelection.getGrayChannel() != null){
            cst.setGrayChannel( visit(channelSelection.getGrayChannel(), null) );
        }
        
        return cst;
    }

    /**
     * transform a GT overlap in xml string representation.
     */
    public String visit(OverlapBehavior overlapBehavior, Object data) {
        switch(overlapBehavior){
            case AVERAGE : return SEJAXBStatics.OVERLAP_AVERAGE;
            case EARLIEST_ON_TOP : return SEJAXBStatics.OVERLAP_EARLIEST_ON_TOP;
            case LATEST_ON_TOP : return SEJAXBStatics.OVERLAP_LATEST_ON_TOP;
            case RANDOM : return SEJAXBStatics.OVERLAP_RANDOM;
            default : return null;
        }
    }

    /**
     * transform a GT channel type in jaxb channel type.
     */
    @Override
    public org.geotoolkit.se.xml.v110.SelectedChannelType visit(SelectedChannelType selectChannelType, Object data) {
        final org.geotoolkit.se.xml.v110.SelectedChannelType sct = se_factory.createSelectedChannelType();
        sct.setContrastEnhancement( visit(selectChannelType.getContrastEnhancement(), null) );
        sct.setSourceChannelName( selectChannelType.getChannelName() );
        return sct;
    }

    /**
     * Transform a GT shaded relief in jaxb shaded relief.
     */
    @Override
    public ShadedReliefType visit(ShadedRelief shadedRelief, Object data) {
        final ShadedReliefType srt = se_factory.createShadedReliefType();
        srt.setBrightnessOnly(shadedRelief.isBrightnessOnly());
        srt.setReliefFactor(shadedRelief.getReliefFactor().evaluate(null, Double.class));
        return srt;
    }
    
}
