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

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.sis.measure.Units;
import org.geotoolkit.ogc.xml.OGC110toGTTransformer;
import org.geotoolkit.se.xml.v110.AnchorPointType;
import org.geotoolkit.se.xml.v110.CategorizeType;
import org.geotoolkit.se.xml.v110.ChangeCaseType;
import org.geotoolkit.se.xml.v110.ChannelSelectionType;
import org.geotoolkit.se.xml.v110.ColorMapType;
import org.geotoolkit.se.xml.v110.ColorReplacementType;
import org.geotoolkit.se.xml.v110.ConcatenateType;
import org.geotoolkit.se.xml.v110.ContrastEnhancementType;
import org.geotoolkit.se.xml.v110.CoverageStyleType;
import org.geotoolkit.se.xml.v110.DescriptionType;
import org.geotoolkit.se.xml.v110.DisplacementType;
import org.geotoolkit.se.xml.v110.ExternalGraphicType;
import org.geotoolkit.se.xml.v110.FeatureTypeStyleType;
import org.geotoolkit.se.xml.v110.FillType;
import org.geotoolkit.se.xml.v110.FontType;
import org.geotoolkit.se.xml.v110.FormatDateType;
import org.geotoolkit.se.xml.v110.FormatNumberType;
import org.geotoolkit.se.xml.v110.GeometryType;
import org.geotoolkit.se.xml.v110.GraphicFillType;
import org.geotoolkit.se.xml.v110.GraphicStrokeType;
import org.geotoolkit.se.xml.v110.GraphicType;
import org.geotoolkit.se.xml.v110.HaloType;
import org.geotoolkit.se.xml.v110.ImageOutlineType;
import org.geotoolkit.se.xml.v110.InlineContentType;
import org.geotoolkit.se.xml.v110.InterpolateType;
import org.geotoolkit.se.xml.v110.InterpolationPointType;
import org.geotoolkit.se.xml.v110.LabelPlacementType;
import org.geotoolkit.se.xml.v110.LegendGraphicType;
import org.geotoolkit.se.xml.v110.LinePlacementType;
import org.geotoolkit.se.xml.v110.LineSymbolizerType;
import org.geotoolkit.se.xml.v110.MapItemType;
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
import org.geotoolkit.se.xml.v110.StringLengthType;
import org.geotoolkit.se.xml.v110.StringPositionType;
import org.geotoolkit.se.xml.v110.StrokeType;
import org.geotoolkit.se.xml.v110.SubstringType;
import org.geotoolkit.se.xml.v110.SvgParameterType;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.geotoolkit.se.xml.v110.TextSymbolizerType;
import org.geotoolkit.se.xml.v110.ThreshholdsBelongToType;
import org.geotoolkit.se.xml.v110.TrimType;
import org.geotoolkit.se.xml.vext.ColorItemType;
import org.geotoolkit.se.xml.vext.JenksType;
import org.geotoolkit.se.xml.vext.RangeType;
import org.geotoolkit.se.xml.vext.RecolorType;
import org.geotoolkit.style.DefaultColorReplacement;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.function.Categorize;
import org.geotoolkit.style.function.ColorItem;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Jenks;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.geotoolkit.style.function.RecolorFunction;
import org.geotoolkit.style.function.ThreshholdsBelongTo;
import org.geotoolkit.util.NamesExt;
import org.opengis.filter.Expression;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Literal;
import org.opengis.filter.ValueReference;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.style.AnchorPoint;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ColorReplacement;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;
import org.opengis.style.Description;
import org.opengis.style.Displacement;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.ExternalMark;
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
import org.opengis.style.SelectedChannelType;
import org.opengis.style.SemanticType;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;
import org.opengis.util.FactoryException;

/**
 * Transform a SE v1.1.0 symbology in GT classes.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class SE110toGTTransformer extends OGC110toGTTransformer {

    private static final String GENERIC_ANY = "generic:any";
    private static final String GENERIC_POINT = "generic:point";
    private static final String GENERIC_LINE = "generic:line";
    private static final String GENERIC_POLYGON = "generic:polygon";
    private static final String GENERIC_TEXT = "generic:text";
    private static final String GENERIC_RASTER = "generic:raster";
    private static final String UOM_METRE = "http://www.opengeospatial.org/se/units/metre";
    private static final String UOM_FOOT = "http://www.opengeospatial.org/se/units/foot";
    private static final String UOM_PIXEL = "http://www.opengeospatial.org/se/units/pixel";

    protected final MutableStyleFactory styleFactory;
    protected final StyleXmlIO xmlUtilities = new StyleXmlIO();

    public SE110toGTTransformer(final FilterFactory filterFactory,final MutableStyleFactory styleFactory){
        super(filterFactory);
        this.styleFactory = styleFactory;
    }

    public SE110toGTTransformer(final FilterFactory filterFactory,final MutableStyleFactory styleFactory, final Map<String, String> namespaceMapping){
        super(filterFactory, namespaceMapping);
        this.styleFactory = styleFactory;
    }

    public Description visitDescription(final DescriptionType dt) {
        if (dt == null) {
            return StyleConstants.DEFAULT_DESCRIPTION;
        } else {
            return styleFactory.description(
                (dt.getTitle() == null) ? null : dt.getTitle(),
                (dt.getAbstract() == null) ? null : dt.getAbstract());
        }

    }

    public OnlineResource visitOnlineResource(final org.geotoolkit.se.xml.v110.OnlineResourceType ort) {
        URI uri = null;
        try {
            uri = new URI(ort.getHref());
        } catch (URISyntaxException ex) {
            Logger.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
        }

        if (uri != null) {
            return styleFactory.onlineResource(uri);
        }

        return null;
    }

    public String visitGeom(final GeometryType geometry) {
        if(geometry == null || geometry.getPropertyName() == null || geometry.getPropertyName().getContent() == null || geometry.getPropertyName().getContent().trim().isEmpty()) return null;
        return geometry.getPropertyName().getContent();
    }

    public Object visitSVG(final SvgParameterType svg) {

//        JAXBElementFunctionType>
//        String
//        JAXBElementExpressionType>
//        JAXBElementLiteralType>
//        JAXBElementBinaryOperatorType>
//        JAXBElementBinaryOperatorType>
//        JAXBElementBinaryOperatorType>
//        JAXBElementPropertyNameType>
//        JAXBElementBinaryOperatorType>

        if(SEJAXBStatics.STROKE_DASHARRAY.equalsIgnoreCase(svg.getName()) ){

            final List<Serializable> content = svg.getContent();
            Object value = null;
            for(final Serializable obj : content){
                if(obj instanceof String && !obj.toString().trim().isEmpty()){
                    value = obj.toString();
                }else if(obj instanceof JAXBElement<?>){
                    value = visitExpression( (JAXBElement<?>)obj );
                }
            }

            if(value != null){
                //its a float array
                final float[] values = new float[]{0,0};
                final String[] parts = value.toString().split(" ");

                for(int i=0;i < parts.length && i<2 ;i++){
                    try{
                        final Float f = Float.valueOf(parts[i]);
                        values[i] = f.floatValue();
                    }catch(NumberFormatException ne){}
                }

                return values;
            }else{
                return null;
            }

        }

        final List<Serializable> content = svg.getContent();
        for(final Serializable obj : content){
            if(obj instanceof String && !obj.toString().trim().isEmpty()){
                return filterFactory.literal(obj.toString());
            }else if(obj instanceof JAXBElement<?>){
                return visitExpression( (JAXBElement<?>)obj );
            }
        }

        if(!content.isEmpty()){
            //we arrived here with finding a real value but the content is not empty
            //so it's an empty string value
            return filterFactory.literal("");
        }

        return null;
    }

    public Unit<Length> visitUOM(final String uom) {
        if (uom == null) return Units.POINT;

        if (UOM_METRE.equalsIgnoreCase(uom)) {
            return Units.METRE;
        } else if(UOM_FOOT.equalsIgnoreCase(uom)) {
            return Units.FOOT;
        } else if(UOM_PIXEL.equalsIgnoreCase(uom)) {
            return Units.POINT;
        } else try {
            final Unit<?> unit = Units.valueOf(uom);
            if (unit == null || Units.PIXEL.equals(unit)) return Units.POINT;
            else return unit.asType(Length.class);
        } catch (Exception e) {
            final Logger logger = Logger.getLogger("org.geotoolkit.sld.xml");
            logger.warning("Input unit cannot be parsed. Defaulting to 'point' unit. More details available in debug logs");
            logger.log(Level.FINE, e, () -> "Cannot parse unit: "+uom);
            return Units.POINT;
        }
    }

    /**
     * Transform a JaxBelement in Expression.
     */
    @Override
    public Expression visitExpression(final JAXBElement<?> jax){
        //Added in SE1.1-----
//        JAXBElementMapItemType>
//        JAXBElementInterpolateType>
//        JAXBElementConcatenateType>
//        JAXBElementChangeCaseType>
//        JAXBElementTrimType>
//        JAXBElementFormatDateType>
//        JAXBElementCategorizeType>
//        JAXBElementInterpolationPointType>
//        JAXBElementStringLengthType>
//        JAXBElementRecodeType>
//        JAXBElementnet.opengis.se.FunctionType>
//        JAXBElementFormatNumberType>
//        JAXBElementSubstringType>
//        JAXBElementStringPositionType>

        try {
            return super.visitExpression(jax);
        } catch(IllegalArgumentException ex) {
            final String expName = jax.getName().toString();
            final Object obj = jax.getValue();

            if(obj instanceof MapItemType){
                throw new IllegalArgumentException("Not supported yet : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
            }else if(obj instanceof InterpolateType){
                return visit((InterpolateType) obj);
            }else if(obj instanceof ConcatenateType){
                 throw new IllegalArgumentException("Not supported yet : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
            }else if(obj instanceof ChangeCaseType){
                 throw new IllegalArgumentException("Not supported yet : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
            }else if(obj instanceof TrimType){
                 throw new IllegalArgumentException("Not supported yet : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
            }else if(obj instanceof FormatDateType){
                 throw new IllegalArgumentException("Not supported yet : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
            }else if(obj instanceof CategorizeType){
                return visit((CategorizeType) obj);
            }else if(obj instanceof InterpolationPointType){
                 throw new IllegalArgumentException("Not supported yet : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
            }else if(obj instanceof StringLengthType){
                 throw new IllegalArgumentException("Not supported yet : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
            }else if(obj instanceof RecodeType){
                 throw new IllegalArgumentException("Not supported yet : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
            }else if(obj instanceof FormatNumberType){
                 throw new IllegalArgumentException("Not supported yet : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
            }else if(obj instanceof SubstringType){
                 throw new IllegalArgumentException("Not supported yet : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
            }else if(obj instanceof StringPositionType){
                 throw new IllegalArgumentException("Not supported yet : Name > " + expName +"  JAXB > " + jax + " OBJECT >" + obj);
            }

            throw new IllegalArgumentException(String.format(
                    "Unknown expression: Name=%s ; Value-type=%s",
                    expName, obj == null ? null : obj.getClass().getCanonicalName()), ex);
        }
    }

    /**
     * Transform a parametervaluetype in Expression.
     */
    public Expression visitExpression(final org.geotoolkit.se.xml.v110.ParameterValueType param) {
        if(param == null) return null;

//        Objects of the following type(s) are allowed in the list
//        JAXBElementFunctionType> ---NS
//        String ---k
//        JAXBElementExpressionType> ---k
//        JAXBElementLiteralType> ---k
//        JAXBElementBinaryOperatorType> ---k
//        JAXBElementBinaryOperatorType> ---k
//        JAXBElementBinaryOperatorType> ---k
//        JAXBElementPropertyNameType> ---k
//        JAXBElementBinaryOperatorType> ---k

        Expression result = null;

        final List<Serializable> sers = param.getContent();
        if (sers.size() == 1) {
            final Serializable ser = sers.get(0);
            if (ser instanceof String) {
                result = filterFactory.literal((String) ser);
            } else if (ser instanceof JAXBElement<?>) {
                final JAXBElement<?> jax = (JAXBElement<?>) ser;
                result = visitExpression(jax);
            }
        } else {
            for (final Serializable ser : sers) {
                if (ser instanceof JAXBElement<?>) {
                    final JAXBElement<?> jax = (JAXBElement<?>) ser;
                    result = visitExpression(jax);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * If expression is an empty property name, null is returned.
     * @param exp
     * @return
     */
    private static Expression notEmpty(Expression exp){
        if(exp instanceof ValueReference && ((ValueReference)exp).getXPath().trim().isEmpty()){
            return null;
        }
        return exp;
    }


    //Style, FTS and Rule-------------------------------------------------------

    /**
     * Transform a SLD v1.1 userstyle in GT style.
     */
    public MutableStyle visitUserStyle(final org.geotoolkit.sld.xml.v110.UserStyle us) throws FactoryException {
        if(us == null){
            return null;
        }else{
            final MutableStyle mls = styleFactory.style();
            mls.setName(us.getName());
            mls.setDescription(visitDescription(us.getDescription()));
            final Boolean def = us.isIsDefault();
            mls.setDefault( (def != null)? def : false);

            final List<Object> ftss = us.getFeatureTypeStyleOrCoverageStyleOrOnlineResource();

            for(final Object obj : ftss){
                MutableFeatureTypeStyle fts = visitFTS(obj);
                mls.featureTypeStyles().add(fts);
            }

            return mls;
        }

    }

    /**
     * Transform a SLD v1.1 FeatureTypeStyle or CoverageStyle in GT FTS.
     */
    public MutableFeatureTypeStyle visitFTS(final Object obj) throws FactoryException{
        if(obj == null) return null;

        if(obj instanceof OnlineResourceType){
            final  OnlineResourceType ort = (OnlineResourceType) obj;
            final OnlineResource or = visitOnlineResource(ort);
            if(or != null){
                try{
                    return xmlUtilities.readFeatureTypeStyle(or, Specification.SymbologyEncoding.V_1_1_0);
                }catch(JAXBException ex){
                    Logger.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
                }
                return null;
            }
        }else if(obj instanceof CoverageStyleType){
            final MutableFeatureTypeStyle fts = styleFactory.featureTypeStyle();
            final CoverageStyleType cst = (CoverageStyleType) obj;

            fts.setName(cst.getName());
            fts.setDescription( visitDescription(cst.getDescription()));
            fts.semanticTypeIdentifiers().addAll( visitSemantics(cst.getSemanticTypeIdentifier()));

            if(cst.getCoverageName() != null){
                fts.featureTypeNames().add(NamesExt.create(cst.getCoverageName()));
            }

            if(cst.getRuleOrOnlineResource() == null || cst.getRuleOrOnlineResource().isEmpty()){
            }else{
                for(Object objRule : cst.getRuleOrOnlineResource()){
                    fts.rules().add(visitRule(objRule) );
                }
            }

            return fts;
        }else if(obj instanceof FeatureTypeStyleType){
            final MutableFeatureTypeStyle fts = styleFactory.featureTypeStyle();
            final FeatureTypeStyleType ftst = (FeatureTypeStyleType) obj;

            fts.setName(ftst.getName());
            fts.setDescription( visitDescription(ftst.getDescription()));
            fts.semanticTypeIdentifiers().addAll( visitSemantics(ftst.getSemanticTypeIdentifier()));

            if(ftst.getFeatureTypeName() != null){
                fts.featureTypeNames().add(NamesExt.create(ftst.getFeatureTypeName()));
            }

            if(ftst.getRuleOrOnlineResource() == null || ftst.getRuleOrOnlineResource().isEmpty()){
            }else{
                for(final Object objRule : ftst.getRuleOrOnlineResource()){
                    fts.rules().add(visitRule(objRule) );
                }
            }

            return fts;
        }

        return null;
    }

    /**
     * Transform SLD v1.1 semantics in GT semantics.
     */
    public Collection<? extends SemanticType> visitSemantics(final List<String> strs){

        if(strs == null || strs.isEmpty()){
            return Collections.emptyList();
        }

        final Collection<SemanticType> semantics = new ArrayList<SemanticType>();
        for(final String str : strs){
            if(GENERIC_ANY.equalsIgnoreCase(str)){
                semantics.add( SemanticType.ANY );
            }else if(GENERIC_POINT.equalsIgnoreCase(str)){
                semantics.add( SemanticType.POINT );
            }else if(GENERIC_LINE.equalsIgnoreCase(str)){
                semantics.add( SemanticType.LINE );
            }else if(GENERIC_POLYGON.equalsIgnoreCase(str)){
                semantics.add( SemanticType.POLYGON );
            }else if(GENERIC_TEXT.equalsIgnoreCase(str)){
                semantics.add( SemanticType.TEXT );
            }else if(GENERIC_RASTER.equalsIgnoreCase(str)){
                semantics.add( SemanticType.RASTER );
            }else{
                semantics.add( SemanticType.valueOf(str) );
            }
        }

        return semantics;
    }

    /**
     * Trasnform SLD v1.1 rule in GT Rule.
     */
    public MutableRule visitRule(final Object objRule) throws FactoryException {
        if(objRule instanceof OnlineResourceType){
            final OnlineResourceType ortRule = (OnlineResourceType) objRule;
            return visitRule(ortRule);
        } else if (objRule instanceof RuleType) {
            final RuleType rt = (RuleType) objRule;
            return visitRule(rt);
        }
        return null;
    }

    /**
     * Trasnform SLD v1.1 rule in GT Rule.
     */
    public MutableRule visitRule(final OnlineResourceType ort) throws FactoryException {
        final OnlineResource or = visitOnlineResource(ort);
        if(or != null) {
            try{
                return xmlUtilities.readRule(or, Specification.SymbologyEncoding.V_1_1_0);
            } catch (JAXBException ex) {
                Logger.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
            }
            return null;
        }
        return null;
    }

    /**
     * Trasnform SLD v1.1 rule in GT Rule.
     */
    public MutableRule visitRule(final org.geotoolkit.se.xml.v110.RuleType rt)
            throws FactoryException {

        final MutableRule rule = styleFactory.rule();

        rule.setName(rt.getName());
        rule.setDescription( visitDescription(rt.getDescription()) );
        rule.setElseFilter(rt.getElseFilter() != null);
        rule.setFilter(visitFilter(rt.getFilter()));
        rule.setLegendGraphic(visitLegend(rt.getLegendGraphic()));
        rule.setMaxScaleDenominator((rt.getMaxScaleDenominator() == null) ? Double.MAX_VALUE : rt.getMaxScaleDenominator());
        rule.setMinScaleDenominator((rt.getMinScaleDenominator() == null) ? 0 : rt.getMinScaleDenominator());

        if(rt.getSymbolizer() == null || rt.getSymbolizer().isEmpty()){

        }else{

            for(final JAXBElement<?> jax : rt.getSymbolizer()) {
                final Object st = jax.getValue();

                if (st == null) {
                    continue;
                }else if(st instanceof SymbolizerType){
                    rule.symbolizers().add(visit((SymbolizerType)st));
                }else if(st instanceof Symbolizer){
                    rule.symbolizers().add((Symbolizer)st);
                }


            }

        }

        return rule;
    }



    //Symbolizers---------------------------------------------------------------
    /**
     * Transform a SLD v1.1 symbolizers in GT Symbolizers.
     */
    public Collection<? extends Symbolizer> visitSymbolizers(final List<JAXBElement<? extends SymbolizerType>> objs){
        if( objs == null || objs.isEmpty()){
            return Collections.emptyList();
        }

        final Collection<Symbolizer> rs = new ArrayList<Symbolizer>();
        for(JAXBElement<? extends SymbolizerType> jax : objs){
            final SymbolizerType st = jax.getValue();
            if(st == null) continue;
            rs.add( visit(st));
        }

        return rs;
    }

    public Symbolizer visit(final SymbolizerType st) {

        if (st instanceof PointSymbolizerType) {
            final PointSymbolizerType pst = (PointSymbolizerType) st;
            return visit(pst);
        } else if (st instanceof LineSymbolizerType) {
            final LineSymbolizerType pst = (LineSymbolizerType) st;
            return visit(pst);
        } else if (st instanceof PolygonSymbolizerType) {
            final PolygonSymbolizerType pst = (PolygonSymbolizerType) st;
            return visit(pst);
        } else if (st instanceof TextSymbolizerType) {
            final TextSymbolizerType pst = (TextSymbolizerType) st;
            return visit(pst);
        } else if (st instanceof RasterSymbolizerType) {
            final RasterSymbolizerType pst = (RasterSymbolizerType) st;
            return visit(pst);
        } else if(st instanceof Symbolizer){
            //jaxbelement is a conform opengis symbolizer
            //this element is an extension symbolizer
            return (Symbolizer) st;
        }

        throw new IllegalArgumentException("Unknowned Symbolizer : " + st.getClass().toString());
    }

    /**
     * Transform a SLD v1.1 point symbolizer in GT point symbolizer.
     */
    public PointSymbolizer visit(final PointSymbolizerType pst) {
        if(pst == null) return null;

        final Graphic graphic = (pst.getGraphic() == null) ? styleFactory.graphic() : visit(pst.getGraphic());
        final Unit uom = visitUOM(pst.getUom());
        final Expression geom = notEmpty(visitExpression(pst.getGeometry()));
        final String name = pst.getName();
        final Description desc = visitDescription(pst.getDescription());

        return styleFactory.pointSymbolizer(name,geom,desc,uom,graphic);
    }

    /**
     * Transform a SLD v1.1 line symbolizer in GT line symbolizer.
     */
    public LineSymbolizer visit(final LineSymbolizerType lst) {
        if(lst == null) return null;

        final Stroke stroke = visit(lst.getStroke());
        final Expression offset = (lst.getPerpendicularOffset() == null) ? filterFactory.literal(0) : visitExpression(lst.getPerpendicularOffset());
        final Unit uom = visitUOM(lst.getUom());
        final Expression geom = notEmpty(visitExpression( lst.getGeometry()));
        final String name = lst.getName();
        final Description desc = visitDescription(lst.getDescription());

        return styleFactory.lineSymbolizer(name,geom,desc,uom,stroke, offset);
    }

    /**
     * Transform a SLD v1.1 polygon symbolizer in GT polygon symbolizer.
     */
    public PolygonSymbolizer visit(final PolygonSymbolizerType pst) {
        if(pst == null) return null;

        final Stroke stroke = visit(pst.getStroke());
        final Fill fill = visit(pst.getFill());
        final Displacement disp = (pst.getDisplacement() == null)? styleFactory.displacement(0, 0) : visit(pst.getDisplacement());
        final Expression offset = (pst.getPerpendicularOffset() == null) ? filterFactory.literal(0) : visitExpression(pst.getPerpendicularOffset());
        final Unit uom = visitUOM(pst.getUom());
        final Expression geom = notEmpty(visitExpression( pst.getGeometry()));
        final String name = pst.getName();
        final Description desc = visitDescription(pst.getDescription());

        return styleFactory.polygonSymbolizer(name,geom,desc,uom,stroke, fill, disp, offset);
    }

    /**
     * Transform a SLD v1.1 raster symbolizer in GT raster symbolizer.
     */
    public RasterSymbolizer visit(final RasterSymbolizerType rst) {
        if(rst == null) return null;

        final Expression opacity = (rst.getOpacity() == null) ? filterFactory.literal(1) : visitExpression(rst.getOpacity());
        final ChannelSelection selection = visit(rst.getChannelSelection());
        final OverlapBehavior overlap = visitOverLap(rst.getOverlapBehavior());
        final ColorMap colorMap = visit(rst.getColorMap());
        final ContrastEnhancement enchance = visit(rst.getContrastEnhancement());
        final ShadedRelief relief = visit(rst.getShadedRelief());
        final Symbolizer outline = visit(rst.getImageOutline());
        final Unit uom = visitUOM(rst.getUom());
        final Expression geom = notEmpty(visitExpression( rst.getGeometry()));
        final String name = rst.getName();
        final Description desc = visitDescription(rst.getDescription());

        return styleFactory.rasterSymbolizer(name,geom,desc,uom,opacity, selection, overlap, colorMap, enchance, relief, outline);
    }

    /**
     * Transform a SLD v1.1 text symbolizer in GT text symbolizer.
     */
    public TextSymbolizer visit(final TextSymbolizerType tst) {
        if(tst == null) return null;

        final Expression label = visitExpression(tst.getLabel());
        final Font font = (tst.getFont() == null) ? styleFactory.font() : visit(tst.getFont());
        final LabelPlacement placement = (tst.getLabelPlacement() == null) ? styleFactory.pointPlacement() : visit(tst.getLabelPlacement());
        final Halo halo = (tst.getHalo() == null)? styleFactory.halo(Color.WHITE, 0) : visit(tst.getHalo());
        final Fill fill = (tst.getFill() == null)? styleFactory.fill() : visit(tst.getFill());
        final Unit uom = visitUOM(tst.getUom());
        final Expression geom = notEmpty(visitExpression( tst.getGeometry()));
        final String name = tst.getName();
        final Description desc = visitDescription(tst.getDescription());

        if(label == null) return null;

        return styleFactory.textSymbolizer(name,geom,desc,uom,label, font, placement, halo, fill);
    }

    //Sub elements -------------------------------------------------------------

    public Map<Expression,List<Symbolizer>> visitRanges(final List<JAXBElement<RangeType>> types){
        final Map<Expression,List<Symbolizer>> ranges = new LinkedHashMap<Expression, List<Symbolizer>>();

        for(final JAXBElement<RangeType> type : types){
            final RangeType rt = type.getValue();

            final Expression exp = visitExpression(rt.getThreshold());
            final List<Symbolizer> symbols = new ArrayList<Symbolizer>();

            for(final JAXBElement<? extends SymbolizerType> jst : rt.getSymbolizer()){
                final SymbolizerType st = jst.getValue();
                if(st == null) continue;

                symbols.add(visit(st));
            }

            ranges.put(exp, symbols);
        }

        return ranges;
    }

    /**
     * Transform a SLD v1.1 legend in GT legend.
     */
    public GraphicLegend visitLegend(final LegendGraphicType legendGraphic) {
        if(legendGraphic == null || legendGraphic.getGraphic() == null){
            return null;
        }

        final Graphic graphic = visit(legendGraphic.getGraphic());
        if(graphic != null){
            return styleFactory.graphicLegend(graphic);
        }

        return null;
    }

    /**
     * Transform a SLD v1.1 graphic in GT graphic.
     */
    public Graphic visit(final GraphicType graphic) {
        if(graphic == null) return null;

        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();

        for(final Object obj : graphic.getExternalGraphicOrMark()){
            if(obj instanceof MarkType){
                symbols.add( visit((MarkType)obj));
            }else if(obj instanceof ExternalGraphicType){
                symbols.add( visit((ExternalGraphicType)obj));
            }
        }

        final Expression opacity = visitExpression(graphic.getOpacity());
        final Expression size = visitExpression(graphic.getSize());
        final Expression rotation = visitExpression(graphic.getRotation());
        final AnchorPoint anchor = visit(graphic.getAnchorPoint());
        final Displacement disp = visit(graphic.getDisplacement());

        return styleFactory.graphic(symbols, opacity, size, rotation, anchor, disp);
    }

    /**
     *  Transform a SLD v1.1 stroke in GT stroke.
     */
    public Stroke visit(final StrokeType strk) {
        if(strk == null) return null;

        final GraphicFill fill = visit(strk.getGraphicFill());
        final GraphicStroke stroke = visit(strk.getGraphicStroke());
        Expression color   = null;
        Expression opacity = null;
        Expression width   = null;
        Expression join    = null;
        Expression cap     = null;
        float[]    dashes  = null;
        Expression offset  = null;

        final List<SvgParameterType> params = strk.getSvgParameter();
        for(final SvgParameterType svg : params){
            if(SEJAXBStatics.STROKE.equalsIgnoreCase(svg.getName())){
                color = (Expression)visitSVG(svg);
            }else if(SEJAXBStatics.STROKE_OPACITY.equalsIgnoreCase(svg.getName())){
                opacity = (Expression)visitSVG(svg);
            }else if(SEJAXBStatics.STROKE_WIDTH.equalsIgnoreCase(svg.getName())){
                width = (Expression)visitSVG(svg);
            }else if(SEJAXBStatics.STROKE_LINEJOIN.equalsIgnoreCase(svg.getName())){
                join = (Expression)visitSVG(svg);
            }else if(SEJAXBStatics.STROKE_LINECAP.equalsIgnoreCase(svg.getName())){
                cap = (Expression)visitSVG(svg);
            }else if(SEJAXBStatics.STROKE_DASHARRAY.equalsIgnoreCase(svg.getName())){
                dashes = (float[])visitSVG(svg);
            }else if(SEJAXBStatics.STROKE_DASHOFFSET.equalsIgnoreCase(svg.getName())){
                offset = (Expression)visitSVG(svg);
            }
        }
        if(fill != null){
            return styleFactory.stroke(fill, color, opacity, width, join, cap, dashes, offset);
        }else if(stroke != null){
            return styleFactory.stroke(stroke, color, opacity, width, join, cap, dashes, offset);
        }else{
            return styleFactory.stroke(color, opacity, width, join, cap, dashes, offset);
        }
    }

    /**
     *  Transform a SLD v1.1 fill in GT fill.
     */
    public Fill visit(final FillType fl) {
        if(fl == null) return null;

        final GraphicFill fill = visit(fl.getGraphicFill());
        Expression color   = null;
        Expression opacity = null;

        final List<SvgParameterType> params = fl.getSvgParameter();
        for(final SvgParameterType svg : params){
            if(SEJAXBStatics.FILL.equalsIgnoreCase(svg.getName())){
                color = (Expression)visitSVG(svg);
            }else if(SEJAXBStatics.FILL_OPACITY.equalsIgnoreCase(svg.getName())){
                opacity = (Expression)visitSVG(svg);
            }
        }

        return styleFactory.fill(fill, color, opacity);
    }

    /**
     *  Transform a SLD v1.1 displacement in GT displacement.
     */
    public Displacement visit(final DisplacementType displacement) {
        if(displacement == null) return null;

        final Expression x = visitExpression(displacement.getDisplacementX());
        final Expression y = visitExpression(displacement.getDisplacementY());

        return styleFactory.displacement(x, y);
    }

    /**
     *  Transform a SLD v1.1 overlap in GT overlap.
     */
    public OverlapBehavior visitOverLap(final String overlapBehavior) {
        if(SEJAXBStatics.OVERLAP_AVERAGE.equalsIgnoreCase(overlapBehavior)){
            return OverlapBehavior.AVERAGE;
        }else if(SEJAXBStatics.OVERLAP_EARLIEST_ON_TOP.equalsIgnoreCase(overlapBehavior)){
            return OverlapBehavior.EARLIEST_ON_TOP;
        }else if(SEJAXBStatics.OVERLAP_LATEST_ON_TOP.equalsIgnoreCase(overlapBehavior)){
            return OverlapBehavior.LATEST_ON_TOP;
        }else if(SEJAXBStatics.OVERLAP_RANDOM.equalsIgnoreCase(overlapBehavior)){
            return OverlapBehavior.RANDOM;
        }else{
            return OverlapBehavior.RANDOM;
        }
    }

    /**
     *  Transform a SLD v1.1 channelselection in GT channel selection
     */
    public ChannelSelection visit(final ChannelSelectionType channelSelection) {
        if(channelSelection == null) return null;

        if(channelSelection.getGrayChannel() != null){
            final SelectedChannelType sct = visit(channelSelection.getGrayChannel());
            return styleFactory.channelSelection(sct);
        }else{
            return styleFactory.channelSelection(
                    visit(channelSelection.getRedChannel()),
                    visit(channelSelection.getGreenChannel()),
                    visit(channelSelection.getBlueChannel()));
        }

    }

    /**
     *  Transform a SLD v1.1 colormap in GT colormap.
     */
    public ColorMap visit(final ColorMapType colorMap) {
        if(colorMap == null) return null;

        Expression function = null;
        if (colorMap.getCategorize() != null) {
            function = visit(colorMap.getCategorize());
        } else if(colorMap.getInterpolate() != null) {
            function = visit(colorMap.getInterpolate());
        } else if (colorMap.getJenks() != null) {
             function = visit(colorMap.getJenks());
        }

        return styleFactory.colorMap(function);
    }

    /**
     *  Transform a SLD v1.1 contrastEnchancement in GT contrastEnchancement.
     */
    public ContrastEnhancement visit(final ContrastEnhancementType contrastEnhancement) {
        if(contrastEnhancement == null) return null;

        final Expression gamma = filterFactory.literal(contrastEnhancement.getGammaValue());
        ContrastMethod type = ContrastMethod.NONE;

        if(contrastEnhancement.getHistogram() != null){
            type = ContrastMethod.HISTOGRAM;
        }else if(contrastEnhancement.getNormalize() != null){
            type = ContrastMethod.NORMALIZE;
        }

        return styleFactory.contrastEnhancement(gamma,type);
    }

    /**
     *  Transform a SLD v1.1 outline in GT outline.
     */
    public Symbolizer visit(final ImageOutlineType imageOutline) {
        if(imageOutline == null) return null;

        if(imageOutline.getLineSymbolizer() != null){
            return visit(imageOutline.getLineSymbolizer());
        }else if(imageOutline.getPolygonSymbolizer() != null){
            return visit(imageOutline.getPolygonSymbolizer());
        }

        return null;
    }

    /**
     *  Transform a SLD v1.1 shadedRelief in GT shadedRelief.
     */
    public ShadedRelief visit(final ShadedReliefType shadedRelief) {
        if(shadedRelief == null) return null;

        final boolean bright = shadedRelief.isBrightnessOnly();
        final Expression relief = filterFactory.literal(shadedRelief.getReliefFactor());

        return styleFactory.shadedRelief(relief,bright);
    }

    /**
     *  Transform a SLD v1.1 font in GT font.
     */
    public Font visit(final FontType font) {
        if(font == null) return null;

        final List<Expression> family = new ArrayList<Expression>();
        Expression style  = null;
        Expression weight = null;
        Expression size   = null;

        final List<SvgParameterType> params = font.getSvgParameter();
        for(final SvgParameterType svg : params){
            if(SEJAXBStatics.FONT_FAMILY.equalsIgnoreCase(svg.getName())){
                family.add( (Expression)visitSVG(svg) );
            }else if(SEJAXBStatics.FONT_STYLE.equalsIgnoreCase(svg.getName())){
                style = (Expression)visitSVG(svg);
            }else if(SEJAXBStatics.FONT_WEIGHT.equalsIgnoreCase(svg.getName())){
                weight = (Expression)visitSVG(svg);
            }else if(SEJAXBStatics.FONT_SIZE.equalsIgnoreCase(svg.getName())){
                size = (Expression)visitSVG(svg);
            }
        }

        return styleFactory.font(family, style, weight, size);
    }

    /**
     *  Transform a SLD v1.1 halo in GT halo.
     */
    public Halo visit(final HaloType halo) {
        if(halo == null) return null;

        final Fill fill = visit(halo.getFill());
        final Expression radius = visitExpression(halo.getRadius());

        return styleFactory.halo(fill, radius);
    }

    /**
     *  Transform a SLD v1.1 label placement in GT label placement.
     */
    public LabelPlacement visit(final LabelPlacementType labelPlacement) {
        if(labelPlacement == null) return null;

        if(labelPlacement.getLinePlacement() != null){
            return visit(labelPlacement.getLinePlacement());
        }else if(labelPlacement.getPointPlacement() != null){
            return visit(labelPlacement.getPointPlacement());
        }else{
            return null;
        }

    }

    /**
     *  Transform a SLD v1.1 anchor in GT anchor.
     */
    public AnchorPoint visit(final AnchorPointType anchorPoint) {
        if(anchorPoint == null) return null;

        final Expression x = visitExpression(anchorPoint.getAnchorPointX());
        final Expression y = visitExpression(anchorPoint.getAnchorPointY());

        return styleFactory.anchorPoint(x, y);
    }

    public Mark visit(final MarkType markType) {
        if(markType == null) return null;

        final Expression wkn = filterFactory.literal(markType.getWellKnownName());
        final ExternalMark external = null;
//        ExternalMark = visit(markType.get)
        final Fill fill = visit(markType.getFill());
        final Stroke stroke = visit(markType.getStroke());

        return styleFactory.mark(wkn, fill, stroke);
    }

    public ExternalGraphic visit(final ExternalGraphicType externalGraphicType) {
        if(externalGraphicType == null) return null;

        OnlineResource resource = null;
        //check online resource
        if(externalGraphicType.getOnlineResource()!=null){
            resource = visitOnlineResource(externalGraphicType.getOnlineResource());
        }
        Icon icon = null;
        //check inline content
        if(externalGraphicType.getInlineContent() != null){
            final InlineContentType ict = externalGraphicType.getInlineContent();
            final List<Object> contents = ict.getContent();
            for(Object obj : contents){
                if(obj instanceof String){
                    try{
                        final byte[] b64 = Base64.getDecoder().decode((String) obj);
                        final ByteArrayInputStream is = new ByteArrayInputStream(b64);
                        final BufferedImage image = ImageIO.read(is);
                        icon = new ImageIcon(image);
                    }catch(IOException ex){
                        Logger.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
                    }
                }
            }
        }

        final String format = externalGraphicType.getFormat();

        //rebuild color replacements
        final Collection<ColorReplacement> replaces = new ArrayList<>();
        for(final ColorReplacementType crt : externalGraphicType.getColorReplacement()){
            final RecodeType rt = crt.getRecode();

            if(rt != null){
                for(final MapItemType mit : rt.getMapItem()){
                    final double d = mit.getData();
                    final Expression val = visitExpression(mit.getValue());
                }
            }

            final RecolorType rc = crt.getRecolor();

            if(rc != null){
                List<ColorItem> items = new ArrayList<ColorItem>();
                for(final ColorItemType mit : rc.getColorItem()){
                    final Literal data = (Literal) visitExpression(mit.getData());
                    final Literal value = (Literal) visitExpression(mit.getValue());
                    items.add(new ColorItem(data, value));
                }
                RecolorFunction recolor = new RecolorFunction(items,null);
                replaces.add(new DefaultColorReplacement(recolor));
            }

        }

        if (resource != null){
            return styleFactory.externalGraphic(resource, format, replaces);
        } else if (icon != null){
            return styleFactory.externalGraphic(icon, replaces);
        } else {
            return null;
        }


    }

    /**
     *  Transform a SLD v1.1 graphic fill in GT graphic fill.
     */
    public GraphicFill visit(final GraphicFillType graphicFill) {
        if(graphicFill == null || graphicFill.getGraphic() == null){
            return null;
        }

        final Graphic graphic = visit(graphicFill.getGraphic());
        if(graphic != null){
            return styleFactory.graphicFill(graphic);
        }

        return null;
    }

    /**
     *  Transform a SLD v1.1 graphic stroke in GT graphic stroke.
     */
    public GraphicStroke visit(final GraphicStrokeType graphicStroke) {
        if(graphicStroke == null || graphicStroke.getGraphic() == null){
            return null;
        }

        final Graphic graphic = visit(graphicStroke.getGraphic());
        if(graphic != null){
            final Expression gap = visitExpression(graphicStroke.getGap());
            final Expression initialGap = visitExpression(graphicStroke.getInitialGap());

            return styleFactory.graphicStroke(graphic,gap,initialGap);
        }

        return null;
    }

    /**
     * Transform a SLD v1.1 selected channel in GT selected channel.
     */
    public SelectedChannelType visit(final org.geotoolkit.se.xml.v110.SelectedChannelType channel) {
        if(channel == null) return null;

        final String name = channel.getSourceChannelName();
        final ContrastEnhancement enchance = (channel.getContrastEnhancement() == null) ?
            StyleConstants.DEFAULT_CONTRAST_ENHANCEMENT : visit(channel.getContrastEnhancement());

        return styleFactory.selectedChannelType(name, enchance);
    }

    /**
     *  Transform a SLD v1.1 categorize function in GT categorize function.
     */
    public Categorize visit(final CategorizeType categorize) {
        if(categorize == null) return null;

        final Literal fallback = filterFactory.literal(categorize.getFallbackValue());
        final Expression lookup = visitExpression(categorize.getLookupValue());
        final Expression value = visitExpression(categorize.getValue());

        final ThreshholdsBelongTo belongs;
        if(ThreshholdsBelongToType.PRECEDING.equals(categorize.getThreshholdsBelongTo()) ){
            belongs = ThreshholdsBelongTo.PRECEDING;
        }else {
            belongs = ThreshholdsBelongTo.SUCCEEDING;
        }

        final Map<Expression,Expression> values = new HashMap<>();
        values.put(StyleConstants.CATEGORIZE_LESS_INFINITY, value);
        final List<JAXBElement<ParameterValueType>> elements = categorize.getThresholdAndTValue();
        for(int i=0, n=elements.size(); i<n;){
            final Expression key = visitExpression(elements.get(i).getValue());
            final Expression val = visitExpression(elements.get(i+1).getValue());
            values.put(key, val);
            i+=2;
        }

        return styleFactory.categorizeFunction(lookup,values,belongs,fallback);
    }

    /**
     *  Transform a SLD v1.1 interpolate function in GT interpolate function.
     */
    public Interpolate visit(final InterpolateType interpolate) {
        if(interpolate == null) return null;

        final Literal fallback = filterFactory.literal(interpolate.getFallbackValue());
        final Expression lookup = visitExpression(interpolate.getLookupValue());

        final Method method;
        if(MethodType.COLOR.equals(interpolate.getMethod()) ){
            method = Method.COLOR;
        }else{
            method = Method.NUMERIC;
        }

        final Mode mode;
        if(ModeType.COSINE.equals(interpolate.getMode()) ){
            mode = Mode.COSINE;
        }else if(ModeType.CUBIC.equals(interpolate.getMode()) ){
            mode = Mode.CUBIC;
        }else {
            mode = Mode.LINEAR;
        }

        final List<InterpolationPoint> values = new ArrayList<InterpolationPoint>();
        for(final InterpolationPointType ip : interpolate.getInterpolationPoint()){
            values.add(
                styleFactory.interpolationPoint(
                    ip.getData(),
                    visitExpression(ip.getValue())
                    )
            );
        }

        return styleFactory.interpolateFunction(lookup,values,method,mode,fallback);
    }

    /**
     *  Transform a SLD v1.1 jenks function in GT jenks function.
     */
    public Jenks visit(final JenksType jenks) {
        if(jenks == null) return null;

        final Literal fallback = filterFactory.literal(jenks.getFallbackValue());
        final Literal classNumber = filterFactory.literal(jenks.getClassNumber());
        final Literal palette = filterFactory.literal(jenks.getPalette());
        final double[] noData = jenks.getNoData();
        final List<Literal> noDataLiteral = new ArrayList<Literal>();
        if (noData != null) {
            for (int i = 0; i < noData.length; i++) {
                noDataLiteral.add(filterFactory.literal(noData[i]));
            }
        }

        return styleFactory.jenksFunction(classNumber, palette, fallback, noDataLiteral);
    }

    /**
     *  Transform a SLD v1.1 lineplacement in GT line placement.
     */
    public LinePlacement visit(final LinePlacementType linePlacement) {
        if(linePlacement == null) return null;

        final Expression offset = visitExpression(linePlacement.getPerpendicularOffset());
        final Expression initial = visitExpression(linePlacement.getInitialGap());
        final Expression gap = visitExpression(linePlacement.getGap());
        Boolean repeated = linePlacement.isIsRepeated();
        Boolean aligned = linePlacement.isIsAligned();
        Boolean generalize = linePlacement.isGeneralizeLine();
        if(repeated == null) repeated = Boolean.FALSE;
        if(aligned == null) aligned = Boolean.FALSE;
        if(generalize == null) generalize = Boolean.FALSE;

        return styleFactory.linePlacement(offset, initial, gap, repeated, aligned, generalize);
    }

    /**
     *  Transform a SLD v1.1 pointplacement in GT point placement.
     */
    public PointPlacement visit(final PointPlacementType pointPlacement) {
        if(pointPlacement == null) return null;

        final AnchorPoint anchor = visit(pointPlacement.getAnchorPoint());
        final Displacement disp = visit(pointPlacement.getDisplacement());
        final Expression rotation = visitExpression(pointPlacement.getRotation());

        return styleFactory.pointPlacement(anchor, disp, rotation);
    }

}
