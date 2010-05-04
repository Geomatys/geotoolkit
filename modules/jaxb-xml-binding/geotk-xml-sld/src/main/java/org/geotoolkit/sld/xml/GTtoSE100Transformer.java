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

import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.v100.BinaryComparisonOpType;
import org.geotoolkit.ogc.xml.v100.BinaryLogicOpType;
import org.geotoolkit.ogc.xml.v100.BinaryOperatorType;
import org.geotoolkit.ogc.xml.v100.ComparisonOpsType;
import org.geotoolkit.ogc.xml.v100.FilterType;
import org.geotoolkit.ogc.xml.v100.FunctionType;
import org.geotoolkit.ogc.xml.v100.LiteralType;
import org.geotoolkit.ogc.xml.v100.LogicOpsType;
import org.geotoolkit.ogc.xml.v100.LowerBoundaryType;
import org.geotoolkit.ogc.xml.v100.PropertyIsBetweenType;
import org.geotoolkit.ogc.xml.v100.PropertyIsLikeType;
import org.geotoolkit.ogc.xml.v100.PropertyIsNullType;
import org.geotoolkit.ogc.xml.v100.PropertyNameType;
import org.geotoolkit.ogc.xml.v100.SpatialOpsType;
import org.geotoolkit.ogc.xml.v100.UnaryLogicOpType;
import org.geotoolkit.ogc.xml.v100.UpperBoundaryType;
import org.geotoolkit.sld.xml.v100.CssParameter;
import org.geotoolkit.sld.xml.v100.ParameterValueType;
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
public class GTtoSE100Transformer implements StyleVisitor{

    private static final String GENERIC_ANY = "generic:any";
    private static final String GENERIC_POINT = "generic:point";
    private static final String GENERIC_LINE = "generic:line";
    private static final String GENERIC_POLYGON = "generic:polygon";
    private static final String GENERIC_TEXT = "generic:text";
    private static final String GENERIC_RASTER = "generic:raster";
    
    private static final String VERSION = "1.0.0";
    
    private final org.geotoolkit.sld.xml.v100.ObjectFactory sld_factory_v100;
    private final org.geotoolkit.ogc.xml.v100.ObjectFactory ogc_factory;
    
    public GTtoSE100Transformer(){
        this.sld_factory_v100 = new org.geotoolkit.sld.xml.v100.ObjectFactory();
        this.ogc_factory = new org.geotoolkit.ogc.xml.v100.ObjectFactory();
    }

    /**
     * Transform an expression in jaxb element.
     */
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
            literal.getContent().add(exp.toString());
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
            final ParameterValueType param = sld_factory_v100.createParameterValueType();
            param.getContent().add(extract(exp));
            return param;
        }
        
    }
    
    /**
     * Transform an expression or float array in a css parameter.
     */
    public CssParameter visitCSS(final Object obj, final String value){
        CssParameter css = sld_factory_v100.createCssParameter();
        css.setName(value);
        
        if(obj instanceof Expression){
            final Expression exp = (Expression) obj;
            JAXBElement<?> ele = extract(exp);
            if(ele == null){
                css = null;
            }else{
                css.getContent().add(ele);
            }
        }else if(obj instanceof float[]){
            final float[] dashes = (float[]) obj;
            final StringBuilder sb = new StringBuilder();
            for(final float f : dashes){
                sb.append(f);
                sb.append(' ');
            }
            css.getContent().add(sb.toString().trim());
        }else{
            throw new IllegalArgumentException("Unknowed CSS parameter jaxb structure :" + obj);
        }
                
        return css;
    }
    
    /**
     * Transform a geometrie name in a geometrytype.
     */
    public org.geotoolkit.sld.xml.v100.Geometry visitGeometryType(String str){
        final org.geotoolkit.sld.xml.v100.Geometry geo = sld_factory_v100.createGeometry();
        final PropertyNameType value = ogc_factory.createPropertyNameType();
        if(str == null) str = "";
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
            final PropertyIsBetweenType bot = ogc_factory.createPropertyIsBetweenType();
            final LowerBoundaryType lbt = ogc_factory.createLowerBoundaryType();
            lbt.setExpression(extract(pib.getLowerBoundary()));
            final UpperBoundaryType ubt = ogc_factory.createUpperBoundaryType();
            ubt.setExpression(extract(pib.getUpperBoundary()));
            
            bot.setExpression( extract(pib.getExpression()) );
            bot.setLowerBoundary( lbt );
            bot.setUpperBoundary( ubt );
            return ogc_factory.createPropertyIsBetween(bot);
        }else if(filter instanceof PropertyIsEqualTo){
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final BinaryComparisonOpType bot = ogc_factory.createBinaryComparisonOpType();
            bot.getExpression().add( extract(pit.getExpression1()));
            bot.getExpression().add( extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsEqualTo(bot);
        }else if(filter instanceof PropertyIsGreaterThan){
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final BinaryComparisonOpType bot = ogc_factory.createBinaryComparisonOpType();
            bot.getExpression().add( extract(pit.getExpression1()));
            bot.getExpression().add( extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsGreaterThan(bot);
        }else if(filter instanceof PropertyIsGreaterThanOrEqualTo){
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final BinaryComparisonOpType bot = ogc_factory.createBinaryComparisonOpType();
            bot.getExpression().add( extract(pit.getExpression1()));
            bot.getExpression().add( extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsGreaterThanOrEqualTo(bot);
        }else if(filter instanceof PropertyIsLessThan){
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final BinaryComparisonOpType bot = ogc_factory.createBinaryComparisonOpType();
            bot.getExpression().add( extract(pit.getExpression1()));
            bot.getExpression().add( extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsLessThan(bot);
        }else if(filter instanceof PropertyIsLessThanOrEqualTo){
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final BinaryComparisonOpType bot = ogc_factory.createBinaryComparisonOpType();
            bot.getExpression().add( extract(pit.getExpression1()));
            bot.getExpression().add( extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsLessThanOrEqualTo(bot);
        }else if(filter instanceof PropertyIsLike){
            final PropertyIsLike pis = (PropertyIsLike) filter;
            final PropertyIsLikeType bot = ogc_factory.createPropertyIsLikeType();
            bot.setEscape(pis.getEscape());
            final LiteralType lt = ogc_factory.createLiteralType();
            lt.getContent().add(pis.getLiteral());
            bot.setLiteral( lt );
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
            return ogc_factory.createPropertyIsNotEqualTo(bot);
        }else if(filter instanceof PropertyIsNull){
            final PropertyIsNull pis = (PropertyIsNull) filter;
            final PropertyIsNullType bot = ogc_factory.createPropertyIsNullType();
            final Object obj = extract(pis.getExpression()).getValue();
            if(obj instanceof LiteralType){
                bot.setLiteral((LiteralType) obj);
            }else if(obj instanceof PropertyNameType){
                bot.setPropertyName((PropertyNameType) obj);
            }else{
                //should not be possible
                throw new IllegalArgumentException("Invalide expression element : " + obj);
            }
            return ogc_factory.createPropertyIsNull(bot);
        }else if(filter instanceof And){
            final And and = (And) filter;
            final BinaryLogicOpType lot = ogc_factory.createBinaryLogicOpType();
            for(final Filter f : and.getChildren()){
                lot.getComparisonOpsOrSpatialOpsOrLogicOps().add(visitFilter(f));
            }
            return ogc_factory.createAnd(lot);
        }else if(filter instanceof Or){
            final Or or = (Or) filter;
            final BinaryLogicOpType lot = ogc_factory.createBinaryLogicOpType();
            for(final Filter f : or.getChildren()){
                lot.getComparisonOpsOrSpatialOpsOrLogicOps().add(visitFilter(f));
            }
            return ogc_factory.createOr(lot);
        }else if(filter instanceof Not){
            final Not not = (Not) filter;
            final UnaryLogicOpType lot = ogc_factory.createUnaryLogicOpType();
            JAXBElement<?> sf = visitFilter(not.getFilter());
            
            if(sf.getValue() instanceof ComparisonOpsType){
                lot.setComparisonOps((JAXBElement<? extends ComparisonOpsType>) sf);
            }else if(sf.getValue() instanceof LogicOpsType){
                lot.setLogicOps((JAXBElement<? extends LogicOpsType>) sf);
            }else if(sf.getValue() instanceof SpatialOpsType){
                lot.setSpatialOps((JAXBElement<? extends SpatialOpsType>) sf);
            }else{
                //should not happen
                throw new IllegalArgumentException("invalide filter element : " + sf);
            }
            return ogc_factory.createNot(lot);
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
        JAXBElement<?> sf = visitFilter(filter);

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
        if(uom.equals(SI.METRE)){
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
    public org.geotoolkit.sld.xml.v100.UserStyle visit(Style style, Object data) {
        final org.geotoolkit.sld.xml.v100.UserStyle userStyle = sld_factory_v100.createUserStyle();
        userStyle.setName(style.getName());
        if (style.getDescription() != null) {
            if(style.getDescription().getAbstract() != null)
                userStyle.setAbstract(style.getDescription().getAbstract().toString());
            if(style.getDescription().getTitle() != null)
                userStyle.setTitle(style.getDescription().getTitle().toString());
        }
        
        userStyle.setIsDefault(style.isDefault());
        
        for(final FeatureTypeStyle fts : style.featureTypeStyles()){
            userStyle.getFeatureTypeStyle().add(visit(fts,null));
        }
        
        return userStyle;
    }

    /**
     * Transform a GT FTS in Jaxb FeatureTypeStyle or CoveragaStyle or OnlineResource.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.FeatureTypeStyle visit(FeatureTypeStyle fts, Object data) {

        //normally we should try to figure out if we have here a coverage FTS or not
        //no need, SLD 1.0.0 only have feature tag
        
        final org.geotoolkit.sld.xml.v100.FeatureTypeStyle ftst = sld_factory_v100.createFeatureTypeStyle();

        ftst.setName(fts.getName());
        
        if (!fts.featureTypeNames().isEmpty()) {
            final Name name = fts.featureTypeNames().iterator().next();
            final String pre = name.getNamespaceURI();
            final String sep = name.getSeparator();
            final String local = name.getLocalPart();
            ftst.setFeatureTypeName(local);
        }

        if (fts.getDescription() != null) {
            if(fts.getDescription().getAbstract() != null)
                ftst.setAbstract(fts.getDescription().getAbstract().toString());
            if(fts.getDescription().getTitle() != null)
                ftst.setTitle(fts.getDescription().getTitle().toString());
        }
                

        for (final SemanticType semantic : fts.semanticTypeIdentifiers()) {
            
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

        for (final Rule rule : fts.rules()) {
            ftst.getRule().add( visit(rule, null) );
        }

        return ftst;
    }

    /**
     * Transform a GT rule in jaxb rule or OnlineResource
     */
    @Override
    public org.geotoolkit.sld.xml.v100.Rule visit(Rule rule, Object data) {
                
        final org.geotoolkit.sld.xml.v100.Rule rt = sld_factory_v100.createRule();
        rt.setName(rule.getName());
        
        if (rule.getDescription() != null) {
            if(rule.getDescription().getAbstract() != null)
                rt.setAbstract(rule.getDescription().getAbstract().toString());
            if(rule.getDescription().getTitle() != null)
                rt.setTitle(rule.getDescription().getTitle().toString());
        }
        
        if(rule.isElseFilter()){
            rt.setElseFilter(sld_factory_v100.createElseFilter());
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
            }else if(symbol instanceof ExtensionSymbolizer){
                //TODO provide jaxb parsing for unknowned symbolizers
//                rt.getSymbolizer().add( visit((ExtensionSymbolizer)symbol,null));
            }
        }
        
        return rt;
    }

    /**
     * Transform a GT point symbol in jaxb point symbol.
     */
    @Override
    public JAXBElement<org.geotoolkit.sld.xml.v100.PointSymbolizer> visit(PointSymbolizer point, Object data) {
        final org.geotoolkit.sld.xml.v100.PointSymbolizer pst = sld_factory_v100.createPointSymbolizer();
        pst.setGeometry( visitGeometryType(point.getGeometryPropertyName() ) );
        
        if(point.getGraphic() != null){
            pst.setGraphic( visit(point.getGraphic(),null) );
        }
        
        return sld_factory_v100.createPointSymbolizer(pst);
    }

    /**
     * Transform a GT line symbol in jaxb line symbol.
     */
    @Override
    public JAXBElement<org.geotoolkit.sld.xml.v100.LineSymbolizer> visit(LineSymbolizer line, Object data) {
        final org.geotoolkit.sld.xml.v100.LineSymbolizer lst = sld_factory_v100.createLineSymbolizer();
        lst.setGeometry( visitGeometryType(line.getGeometryPropertyName() ) );
        
        if(line.getStroke() != null){
            lst.setStroke( visit(line.getStroke(),null) );
        }
        
        return sld_factory_v100.createLineSymbolizer(lst);
    }

    /**
     * Transform a GT polygon symbol in a jaxb version.
     */
    @Override
    public JAXBElement<org.geotoolkit.sld.xml.v100.PolygonSymbolizer> visit(PolygonSymbolizer polygon, Object data) {
        final org.geotoolkit.sld.xml.v100.PolygonSymbolizer pst = sld_factory_v100.createPolygonSymbolizer();
        pst.setGeometry( visitGeometryType(polygon.getGeometryPropertyName() ) );
                
        if(polygon.getFill() != null){
            pst.setFill( visit(polygon.getFill(),null) );
        }
        
        if(polygon.getStroke() != null){
            pst.setStroke( visit(polygon.getStroke(),null) );
        }
        
        return sld_factory_v100.createPolygonSymbolizer(pst);
    }

    /**
     * Transform a GT text symbol in jaxb symbol.
     */
    @Override
    public JAXBElement<org.geotoolkit.sld.xml.v100.TextSymbolizer> visit(TextSymbolizer text, Object data) {
        final org.geotoolkit.sld.xml.v100.TextSymbolizer tst = sld_factory_v100.createTextSymbolizer();
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
        
        return sld_factory_v100.createTextSymbolizer(tst);
    }

    /**
     * Transform a GT raster symbolizer in jaxb raster symbolizer.
     */
    @Override
    public JAXBElement<org.geotoolkit.sld.xml.v100.RasterSymbolizer> visit(RasterSymbolizer raster, Object data) {
        final org.geotoolkit.sld.xml.v100.RasterSymbolizer tst = sld_factory_v100.createRasterSymbolizer();
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
            final org.geotoolkit.sld.xml.v100.ImageOutline iot = sld_factory_v100.createImageOutline();
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
        
        return sld_factory_v100.createRasterSymbolizer(tst);
    }

    @Override
    public Object visit(ExtensionSymbolizer extension, Object data) {
        // extended symbolizers are not supported in XML
        return null;
    }

    /**
     * transform a GT description in jaxb description.
     */
    @Override
    public Object visit(Description description, Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store description.");
    }

    /**
     * Transform a GT displacement in jaxb displacement.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.Displacement visit(Displacement displacement, Object data) {
        final org.geotoolkit.sld.xml.v100.Displacement disp = sld_factory_v100.createDisplacement();
        disp.setDisplacementX( visitExpression(displacement.getDisplacementX()) );
        disp.setDisplacementY( visitExpression(displacement.getDisplacementY()) );
        return disp;
    }

    /**
     * Transform a GT fill in jaxb fill.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.Fill visit(Fill fill, Object data) {
        final org.geotoolkit.sld.xml.v100.Fill ft = sld_factory_v100.createFill();
        
        if(fill.getGraphicFill() != null){
            ft.setGraphicFill( visit(fill.getGraphicFill(),null) );
        }
        
        final List<CssParameter> svgs = ft.getCssParameter();
        svgs.add( visitCSS(fill.getColor(), SEJAXBStatics.FILL) );
        svgs.add( visitCSS(fill.getOpacity(), SEJAXBStatics.FILL_OPACITY) );
        
        if(svgs.isEmpty() && ft.getGraphicFill() == null){
            return null;
        }
                
        return ft;
    }

    /**
     * Transform a GT Font in jaxb font.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.Font visit(Font font, Object data) {
        final org.geotoolkit.sld.xml.v100.Font ft = sld_factory_v100.createFont();
        
        final List<CssParameter> svgs = ft.getCssParameter();
        for(final Expression exp : font.getFamily() ){
            svgs.add( visitCSS(exp, SEJAXBStatics.FONT_FAMILY) );
        }
        
        svgs.add( visitCSS(font.getSize(), SEJAXBStatics.FONT_SIZE) );
        svgs.add( visitCSS(font.getStyle(), SEJAXBStatics.FONT_STYLE) );
        svgs.add( visitCSS(font.getWeight(), SEJAXBStatics.FONT_WEIGHT) );
        
        return ft;
    }

    /**
     * Transform a GT stroke in jaxb stroke.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.Stroke visit(Stroke stroke, Object data) {
        final org.geotoolkit.sld.xml.v100.Stroke st = sld_factory_v100.createStroke();
        
        if(stroke.getGraphicFill() != null){
            st.setGraphicFill( visit(stroke.getGraphicFill(),null) );
        }else if(stroke.getGraphicStroke() != null){
            st.setGraphicStroke( visit(stroke.getGraphicStroke(),null) );
        }
                
        final List<CssParameter> svgs = st.getCssParameter();
        svgs.add( visitCSS(stroke.getColor(), SEJAXBStatics.STROKE) );
        if(stroke.getDashArray() != null){
            svgs.add( visitCSS(stroke.getDashArray(), SEJAXBStatics.STROKE_DASHARRAY) );
        }
        svgs.add( visitCSS(stroke.getDashOffset(), SEJAXBStatics.STROKE_DASHOFFSET) );
        svgs.add( visitCSS(stroke.getLineCap(), SEJAXBStatics.STROKE_LINECAP) );
        svgs.add( visitCSS(stroke.getLineJoin(), SEJAXBStatics.STROKE_LINEJOIN) );
        svgs.add( visitCSS(stroke.getOpacity(), SEJAXBStatics.STROKE_OPACITY) );
        svgs.add( visitCSS(stroke.getWidth(), SEJAXBStatics.STROKE_WIDTH) );
        
        if(svgs.isEmpty() && st.getGraphicFill() == null && st.getGraphicStroke() == null){
            return null;
        }
        
        
        return st;
    }

    /**
     * transform a GT graphic in jaxb graphic
     */
    @Override
    public org.geotoolkit.sld.xml.v100.Graphic visit(Graphic graphic, Object data) {
        final org.geotoolkit.sld.xml.v100.Graphic gt = sld_factory_v100.createGraphic();
        
        for(final GraphicalSymbol gs : graphic.graphicalSymbols()){
            if(gs instanceof Mark){
                final Mark mark = (Mark) gs;
                gt.getExternalGraphicOrMark().add( visit(mark,null) );
            }else if(gs instanceof ExternalMark){
                final ExternalMark ext = (ExternalMark) gs;
                gt.getExternalGraphicOrMark().add( visit(ext,null) );
            }
        }
        
        gt.setOpacity( visitExpression(graphic.getOpacity()) );
        gt.setRotation( visitExpression(graphic.getRotation()));
        gt.setSize( visitExpression(graphic.getSize()));
        return gt;
    }

    /**
     * Transform a GT graphic fill in jaxb graphic fill.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.GraphicFill visit(GraphicFill graphicFill, Object data) {
        final org.geotoolkit.sld.xml.v100.GraphicFill gft = sld_factory_v100.createGraphicFill();
        gft.setGraphic( visit((Graphic)graphicFill,null) );
        return gft;
    }

    /**
     * Transform a GT graphic stroke in jaxb graphic stroke.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.GraphicStroke visit(GraphicStroke graphicStroke, Object data) {
        final org.geotoolkit.sld.xml.v100.GraphicStroke gst = sld_factory_v100.createGraphicStroke();
        gst.setGraphic( visit((Graphic)graphicStroke,null) );
        return gst;
    }

    /**
     * Transform a GT Mark in jaxb Mark.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.Mark visit(Mark mark, Object data) {
        final org.geotoolkit.sld.xml.v100.Mark mt = sld_factory_v100.createMark();
        mt.setFill( visit(mark.getFill(),null) );
        mt.setStroke( visit(mark.getStroke(),null) );
        mt.setWellKnownName(mark.getWellKnownName().toString());
               
        return mt;
    }

    /**
     * Not usable for SLD, See visit(Mark) method.
     */
    @Override
    public Object visit(ExternalMark externalMark, Object data) {
        return null;
    }

    /**
     * Transform a GT external graphic in jaxb externla graphic.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.ExternalGraphic visit(ExternalGraphic externalGraphic, Object data) {
        final org.geotoolkit.sld.xml.v100.ExternalGraphic egt = sld_factory_v100.createExternalGraphic();
        egt.setFormat(externalGraphic.getFormat());
                
        if(externalGraphic.getOnlineResource() != null){
            egt.setOnlineResource(  visit(externalGraphic.getOnlineResource(), null) );
        }
        
        return egt;
    }

    /**
     * Transform a GT point placement in jaxb point placement.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.PointPlacement visit(PointPlacement pointPlacement, Object data) {
        final org.geotoolkit.sld.xml.v100.PointPlacement ppt = sld_factory_v100.createPointPlacement();
        ppt.setAnchorPoint( visit(pointPlacement.getAnchorPoint(), null) );
        ppt.setDisplacement( visit(pointPlacement.getDisplacement(), null) );
        ppt.setRotation( visitExpression(pointPlacement.getRotation()) );
        return ppt;
    }

    /**
     * transform a GT anchor point in jaxb anchor point.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.AnchorPoint visit(AnchorPoint anchorPoint, Object data) {
        final org.geotoolkit.sld.xml.v100.AnchorPoint apt = sld_factory_v100.createAnchorPoint();
        apt.setAnchorPointX( visitExpression(anchorPoint.getAnchorPointX()) );
        apt.setAnchorPointY( visitExpression(anchorPoint.getAnchorPointY()) );
        return apt;
    }

    /**
     * transform a GT lineplacement in jaxb line placement.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.LinePlacement visit(LinePlacement linePlacement, Object data) {
        final org.geotoolkit.sld.xml.v100.LinePlacement lpt = sld_factory_v100.createLinePlacement();
        lpt.setPerpendicularOffset( visitExpression(linePlacement.getPerpendicularOffset()) );        
        return lpt;
    }

    /**
     * Transform a GT label placement in jaxb label placement.
     */
    public org.geotoolkit.sld.xml.v100.LabelPlacement visit(LabelPlacement labelPlacement, Object data) {
        final org.geotoolkit.sld.xml.v100.LabelPlacement lpt = sld_factory_v100.createLabelPlacement();
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
    public org.geotoolkit.sld.xml.v100.LegendGraphic visit(GraphicLegend graphicLegend, Object data) {
        final org.geotoolkit.sld.xml.v100.LegendGraphic lgt = sld_factory_v100.createLegendGraphic();
        lgt.setGraphic( visit((Graphic)graphicLegend,null) );
        return lgt;
    }

    /**
     * Transform a GT onlineResource in jaxb online resource.
     */
    public org.geotoolkit.sld.xml.v100.OnlineResource visit(OnlineResource or, Object data){
        final org.geotoolkit.sld.xml.v100.OnlineResource ort = sld_factory_v100.createOnlineResource();
        try {
            ort.setHref(or.getLinkage().toURL().toString());
        } catch (MalformedURLException ex) {
            Logging.getLogger(GTtoSE100Transformer.class).log(Level.WARNING, null, ex);
        }
        return ort;
    }

    /**
     * transform a GT halo in a jaxb halo.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.Halo visit(Halo halo, Object data) {
        final org.geotoolkit.sld.xml.v100.Halo ht = sld_factory_v100.createHalo();
        ht.setFill( visit(halo.getFill(),null) );
        ht.setRadius( visitExpression(halo.getRadius()) );
        return ht;
    }

    @Override
    public org.geotoolkit.sld.xml.v100.ColorMap visit(ColorMap colorMap, Object data) {
        //TODO Fix that when better undestanding raster functions.
        final org.geotoolkit.sld.xml.v100.ColorMap cmt = sld_factory_v100.createColorMap();
        cmt.getColorMapEntry();
        
        return cmt;
    }

    @Override
    public Object visit(ColorReplacement colorReplacement, Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store color replacements.");
    }

    /**
     * Transform a GT constrast enchancement in jaxb constrast enchancement
     */
    @Override
    public org.geotoolkit.sld.xml.v100.ContrastEnhancement visit(ContrastEnhancement contrastEnhancement, Object data) {
        final org.geotoolkit.sld.xml.v100.ContrastEnhancement cet = sld_factory_v100.createContrastEnhancement();
        cet.setGammaValue(contrastEnhancement.getGammaValue().evaluate(null, Double.class));
        
        final ContrastMethod cm = contrastEnhancement.getMethod();
        if(ContrastMethod.HISTOGRAM.equals(cm)){
            cet.setHistogram(sld_factory_v100.createHistogram());
        }else if(ContrastMethod.NORMALIZE.equals(cm)){
            cet.setNormalize(sld_factory_v100.createNormalize());
        }
        
        return cet;
    }

    /**
     * Transform a GT channel selection in jaxb channel selection.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.ChannelSelection visit(ChannelSelection channelSelection, Object data) {
        final org.geotoolkit.sld.xml.v100.ChannelSelection cst = sld_factory_v100.createChannelSelection();
        
        if(channelSelection.getRGBChannels() != null){
            final SelectedChannelType[] scts = channelSelection.getRGBChannels();
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
    public org.geotoolkit.sld.xml.v100.OverlapBehavior visit(OverlapBehavior overlapBehavior, Object data) {
        final org.geotoolkit.sld.xml.v100.OverlapBehavior over = sld_factory_v100.createOverlapBehavior();
        switch(overlapBehavior){
            case AVERAGE : over.setAVERAGE(sld_factory_v100.createAVERAGE()); break;
            case EARLIEST_ON_TOP : over.setEARLIESTONTOP(sld_factory_v100.createEARLIESTONTOP()); break;
            case LATEST_ON_TOP : over.setLATESTONTOP(sld_factory_v100.createLATESTONTOP()); break;
            case RANDOM : over.setRANDOM(sld_factory_v100.createRANDOM()); break;   
            default : break;
        }
        
        return over;
    }

    /**
     * transform a GT channel type in jaxb channel type.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.SelectedChannelType visit(SelectedChannelType selectChannelType, Object data) {
        final org.geotoolkit.sld.xml.v100.SelectedChannelType sct = sld_factory_v100.createSelectedChannelType();
        sct.setContrastEnhancement( visit(selectChannelType.getContrastEnhancement(), null) );
        sct.setSourceChannelName( selectChannelType.getChannelName() );
        return sct;
    }

    /**
     * Transform a GT shaded relief in jaxb shaded relief.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.ShadedRelief visit(ShadedRelief shadedRelief, Object data) {
        final org.geotoolkit.sld.xml.v100.ShadedRelief srt = sld_factory_v100.createShadedRelief();
        srt.setBrightnessOnly(shadedRelief.isBrightnessOnly());
        srt.setReliefFactor(shadedRelief.getReliefFactor().evaluate(null, Double.class));
        return srt;
    }

}
