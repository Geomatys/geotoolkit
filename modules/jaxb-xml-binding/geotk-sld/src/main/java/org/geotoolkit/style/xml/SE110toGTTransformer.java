/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.style.xml;

import java.awt.Color;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.swing.Icon;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.geotoolkit.util.SimpleInternationalString;

import org.geotools.feature.NameImpl;
import org.geotoolkit.internal.jaxb.v110.se.AnchorPointType;
import org.geotoolkit.internal.jaxb.v110.se.CategorizeType;
import org.geotoolkit.internal.jaxb.v110.se.ChannelSelectionType;
import org.geotoolkit.internal.jaxb.v110.se.ColorMapType;
import org.geotoolkit.internal.jaxb.v110.se.ContrastEnhancementType;
import org.geotoolkit.internal.jaxb.v110.se.CoverageStyleType;
import org.geotoolkit.internal.jaxb.v110.se.DescriptionType;
import org.geotoolkit.internal.jaxb.v110.se.DisplacementType;
import org.geotoolkit.internal.jaxb.v110.se.ExternalGraphicType;
import org.geotoolkit.internal.jaxb.v110.se.FeatureTypeStyleType;
import org.geotoolkit.internal.jaxb.v110.se.FillType;
import org.geotoolkit.internal.jaxb.v110.se.FontType;
import org.geotoolkit.internal.jaxb.v110.se.GeometryType;
import org.geotoolkit.internal.jaxb.v110.se.GraphicFillType;
import org.geotoolkit.internal.jaxb.v110.se.GraphicStrokeType;
import org.geotoolkit.internal.jaxb.v110.se.GraphicType;
import org.geotoolkit.internal.jaxb.v110.se.HaloType;
import org.geotoolkit.internal.jaxb.v110.se.ImageOutlineType;
import org.geotoolkit.internal.jaxb.v110.se.InterpolateType;
import org.geotoolkit.internal.jaxb.v110.se.InterpolationPointType;
import org.geotoolkit.internal.jaxb.v110.se.LabelPlacementType;
import org.geotoolkit.internal.jaxb.v110.se.LegendGraphicType;
import org.geotoolkit.internal.jaxb.v110.se.LinePlacementType;
import org.geotoolkit.internal.jaxb.v110.se.LineSymbolizerType;
import org.geotoolkit.internal.jaxb.v110.se.MarkType;
import org.geotoolkit.internal.jaxb.v110.se.MethodType;
import org.geotoolkit.internal.jaxb.v110.se.ModeType;
import org.geotoolkit.internal.jaxb.v110.se.OnlineResourceType;
import org.geotoolkit.internal.jaxb.v110.se.ParameterValueType;
import org.geotoolkit.internal.jaxb.v110.se.PointPlacementType;
import org.geotoolkit.internal.jaxb.v110.se.PointSymbolizerType;
import org.geotoolkit.internal.jaxb.v110.se.PolygonSymbolizerType;
import org.geotoolkit.internal.jaxb.v110.se.RasterSymbolizerType;
import org.geotoolkit.internal.jaxb.v110.se.RuleType;
import org.geotoolkit.internal.jaxb.v110.se.ShadedReliefType;
import org.geotoolkit.internal.jaxb.v110.se.StrokeType;
import org.geotoolkit.internal.jaxb.v110.se.SvgParameterType;
import org.geotoolkit.internal.jaxb.v110.se.SymbolizerType;
import org.geotoolkit.internal.jaxb.v110.se.TextSymbolizerType;
import org.geotoolkit.internal.jaxb.v110.se.ThreshholdsBelongToType;

import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.function.Categorize;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.geotoolkit.style.function.ThreshholdsBelongTo;

import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.metadata.citation.OnLineResource;
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

/**
 * Transform a SE v1.1.0 symbology in GT classes.
 *
 * @author Johann Sorel (Geomatys)
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
    protected final XMLUtilities xmlUtilities = new XMLUtilities();

    public SE110toGTTransformer(FilterFactory2 filterFactory,MutableStyleFactory styleFactory){
        super(filterFactory);
        this.styleFactory = styleFactory;
    }

    public Description visitDescription(final DescriptionType dt) {
        if (dt == null) {
            return StyleConstants.DEFAULT_DESCRIPTION;
        } else {
            return styleFactory.description(
                (dt.getTitle() == null) ? null : new SimpleInternationalString(dt.getTitle()),
                (dt.getAbstract() == null) ? null : new SimpleInternationalString(dt.getAbstract()));
        }

    }

    public OnLineResource visitOnlineResource(final org.geotoolkit.internal.jaxb.v110.se.OnlineResourceType ort) {
        URI uri = null;
        try {
            uri = new URI(ort.getHref());
        } catch (URISyntaxException ex) {
            Logger.getLogger(SLD110toGTTransformer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (uri != null) {
            return styleFactory.onlineResource(uri);
        }

        return null;
    }

    public String visitGeom(final GeometryType geometry) {
        if(geometry == null || geometry.getPropertyName() == null || geometry.getPropertyName().getContent().trim().isEmpty()) return null;
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
        
        if(JAXBStatics.STROKE_DASHARRAY.equalsIgnoreCase(svg.getName()) ){
            //its a float array
            float[] values = null;
            
            return values;
        }else if(svg.getContent().size() >= 1 && svg.getContent().get(0).getClass().equals(String.class)){
            //it's a basic String, handle it as a literal
            return filterFactory.literal(svg.getContent().get(0).toString());
        }else if(svg.getContent().size() >= 1 && svg.getContent().get(0) instanceof JAXBElement<?>){
            return visitExpression( (JAXBElement<?>)svg.getContent().get(0) );
        }
        
        return null;
    }

    public Unit<Length> visitUOM(final String uom) {
        if(uom == null) return NonSI.PIXEL;
        
        if(UOM_METRE.equalsIgnoreCase(uom)){
            return SI.METER;
        }else if(UOM_FOOT.equalsIgnoreCase(uom)){
            return NonSI.FOOT;
        }else if(UOM_PIXEL.equalsIgnoreCase(uom)){
            return NonSI.PIXEL;
        }else{
            return NonSI.PIXEL;
        }        
        
    }


    //Style, FTS and Rule-------------------------------------------------------

    /**
     * Transform a SLD v1.1 userstyle in GT style.
     */
    public MutableStyle visitUserStyle(org.geotoolkit.internal.jaxb.v110.sld.UserStyle us) {
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
    public MutableFeatureTypeStyle visitFTS(Object obj){
        if(obj == null) return null;

        if(obj instanceof OnlineResourceType){
            OnlineResourceType ort = (OnlineResourceType) obj;
            OnLineResource or = visitOnlineResource(ort);
            if(or != null){
                try{
                    MutableFeatureTypeStyle fts = xmlUtilities.readFeatureTypeStyle(or, Specification.SymbologyEncoding.V_1_1_0);
                    return fts;
                }catch(JAXBException ex){
                    Logger.getLogger(SE110toGTTransformer.class.getName()).log(Level.WARNING, null, ex);
                }
                return null;
            }
        }else if(obj instanceof CoverageStyleType){
            MutableFeatureTypeStyle fts = styleFactory.featureTypeStyle();
            CoverageStyleType cst = (CoverageStyleType) obj;

            fts.setName(cst.getName());
            fts.setDescription( visitDescription(cst.getDescription()));
            fts.semanticTypeIdentifiers().addAll( visitSemantics(cst.getSemanticTypeIdentifier()));
            
            if(cst.getCoverageName() != null){
                fts.featureTypeNames().add(new NameImpl(cst.getCoverageName()));
            }
            
            if(cst.getRuleOrOnlineResource() == null || cst.getRuleOrOnlineResource().isEmpty()){
            }else{
                for(Object objRule : cst.getRuleOrOnlineResource()){
                    fts.rules().add(visitRule(objRule) );
                }
            }
            
            return fts;
        }else if(obj instanceof FeatureTypeStyleType){
            MutableFeatureTypeStyle fts = styleFactory.featureTypeStyle();
            FeatureTypeStyleType ftst = (FeatureTypeStyleType) obj;

            fts.setName(ftst.getName());
            fts.setDescription( visitDescription(ftst.getDescription()));
            fts.semanticTypeIdentifiers().addAll( visitSemantics(ftst.getSemanticTypeIdentifier()));
            
            if(ftst.getFeatureTypeName() != null){
                fts.featureTypeNames().add(new NameImpl(ftst.getFeatureTypeName()));
            }
            
            if(ftst.getRuleOrOnlineResource() == null || ftst.getRuleOrOnlineResource().isEmpty()){
            }else{
                for(Object objRule : ftst.getRuleOrOnlineResource()){
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
    public Collection<? extends SemanticType> visitSemantics(List<String> strs){

        if(strs == null || strs.isEmpty()){
            return Collections.emptyList();
        }

        Collection<SemanticType> semantics = new ArrayList<SemanticType>();
        for(String str : strs){
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
    public MutableRule visitRule(Object objRule) {
        if(objRule instanceof OnlineResourceType){
            OnlineResourceType ortRule = (OnlineResourceType) objRule;
            return visitRule(ortRule);
        } else if (objRule instanceof RuleType) {
            RuleType rt = (RuleType) objRule;
            return visitRule(rt);
        }
        return null;
    }
    
    /**
     * Trasnform SLD v1.1 rule in GT Rule.
     */
    public MutableRule visitRule(OnlineResourceType ort) {
        OnLineResource or = visitOnlineResource(ort);
        if(or != null) {
            try{
                MutableRule rule = xmlUtilities.readRule(or, Specification.SymbologyEncoding.V_1_1_0);
                return rule;
            } catch (JAXBException ex) {
                Logger.getLogger(SE110toGTTransformer.class.getName()).log(Level.WARNING, null, ex);
            }
            return null;
        }
        return null;
    }
    
    /**
     * Trasnform SLD v1.1 rule in GT Rule.
     */
    public MutableRule visitRule(org.geotoolkit.internal.jaxb.v110.se.RuleType rt) {

        MutableRule rule = styleFactory.rule();

        rule.setName(rt.getName());
        rule.setDescription( visitDescription(rt.getDescription()) );
        rule.setElseFilter(rt.getElseFilter() != null);
        rule.setFilter(visitFilter(rt.getFilter()));
        rule.setLegendGraphic(visitLegend(rt.getLegendGraphic()));
        rule.setMaxScaleDenominator((rt.getMaxScaleDenominator() == null) ? Double.MAX_VALUE : rt.getMaxScaleDenominator());
        rule.setMinScaleDenominator((rt.getMinScaleDenominator() == null) ? 0 : rt.getMinScaleDenominator());
        
        if(rt.getSymbolizer() == null || rt.getSymbolizer().isEmpty()){
            
        }else{
            
            for(JAXBElement<? extends org.geotoolkit.internal.jaxb.v110.se.SymbolizerType> jax : rt.getSymbolizer()) {
                org.geotoolkit.internal.jaxb.v110.se.SymbolizerType st = jax.getValue();

                if (st == null) {
                    continue;
                }
                if (st instanceof org.geotoolkit.internal.jaxb.v110.se.PointSymbolizerType) {
                    org.geotoolkit.internal.jaxb.v110.se.PointSymbolizerType pst = (org.geotoolkit.internal.jaxb.v110.se.PointSymbolizerType) st;
                    rule.symbolizers().add(visit(pst));
                } else if (st instanceof org.geotoolkit.internal.jaxb.v110.se.LineSymbolizerType) {
                    org.geotoolkit.internal.jaxb.v110.se.LineSymbolizerType pst = (org.geotoolkit.internal.jaxb.v110.se.LineSymbolizerType) st;
                    rule.symbolizers().add(visit(pst));
                } else if (st instanceof org.geotoolkit.internal.jaxb.v110.se.PolygonSymbolizerType) {
                    org.geotoolkit.internal.jaxb.v110.se.PolygonSymbolizerType pst = (org.geotoolkit.internal.jaxb.v110.se.PolygonSymbolizerType) st;
                    rule.symbolizers().add(visit(pst));
                } else if (st instanceof org.geotoolkit.internal.jaxb.v110.se.TextSymbolizerType) {
                    org.geotoolkit.internal.jaxb.v110.se.TextSymbolizerType pst = (org.geotoolkit.internal.jaxb.v110.se.TextSymbolizerType) st;
                    rule.symbolizers().add(visit(pst));
                } else if (st instanceof org.geotoolkit.internal.jaxb.v110.se.RasterSymbolizerType) {
                    org.geotoolkit.internal.jaxb.v110.se.RasterSymbolizerType pst = (org.geotoolkit.internal.jaxb.v110.se.RasterSymbolizerType) st;
                    rule.symbolizers().add(visit(pst));
                }
            }


            
        }

        return rule;
    }



    //Symbolizers---------------------------------------------------------------
    /**
     * Transform a SLD v1.1 symbolizers in GT Symbolizers.
     */
    public Collection<? extends Symbolizer> visitSymbolizers(List<JAXBElement<? extends SymbolizerType>> objs){
        if( objs == null || objs.isEmpty()){
            return Collections.emptyList();
        }

        Collection<Symbolizer> rs = new ArrayList<Symbolizer>();
        for(JAXBElement<? extends SymbolizerType> jax : objs){
            SymbolizerType st = jax.getValue();

            if(st == null) continue;

            if(st instanceof PointSymbolizerType){
                PointSymbolizerType pst = (PointSymbolizerType) st;
                rs.add( visit(pst));
            }else if(st instanceof LineSymbolizerType){
                LineSymbolizerType pst = (LineSymbolizerType) st;
                rs.add( visit(pst));
            }else if(st instanceof PolygonSymbolizerType){
                PolygonSymbolizerType pst = (PolygonSymbolizerType) st;
                rs.add( visit(pst));
            }else if(st instanceof TextSymbolizerType){
                TextSymbolizerType pst = (TextSymbolizerType) st;
                rs.add( visit(pst));
            }else if(st instanceof RasterSymbolizerType){
                RasterSymbolizerType pst = (RasterSymbolizerType) st;
                rs.add( visit(pst));
            }
        }

        return rs;
    }

    /**
     * Transform a SLD v1.1 point symbolizer in GT point symbolizer.
     */
    public PointSymbolizer visit(PointSymbolizerType pst) {
        if(pst == null) return null;

        Graphic graphic = (pst.getGraphic() == null) ? styleFactory.graphic() : visit(pst.getGraphic());
        Unit uom = visitUOM(pst.getUom());
        String geom = visitGeom( pst.getGeometry());
        String name = pst.getName();
        Description desc = visitDescription(pst.getDescription());

        return styleFactory.pointSymbolizer(name,geom,desc,uom,graphic);
    }

    /**
     * Transform a SLD v1.1 line symbolizer in GT line symbolizer.
     */
    public LineSymbolizer visit(LineSymbolizerType lst) {
        if(lst == null) return null;

        Stroke stroke = (lst.getStroke() == null)? styleFactory.stroke() : visit(lst.getStroke());
        Expression offset = (lst.getPerpendicularOffset() == null) ? filterFactory.literal(0) : visitExpression(lst.getPerpendicularOffset());
        Unit uom = visitUOM(lst.getUom());
        String geom = visitGeom( lst.getGeometry());
        String name = lst.getName();
        Description desc = visitDescription(lst.getDescription());

        return styleFactory.lineSymbolizer(name,geom,desc,uom,stroke, offset);
    }

    /**
     * Transform a SLD v1.1 polygon symbolizer in GT polygon symbolizer.
     */
    public PolygonSymbolizer visit(PolygonSymbolizerType pst) {
        if(pst == null) return null;

        Stroke stroke = (pst.getStroke() == null)? styleFactory.stroke() : visit(pst.getStroke());
        Fill fill = (pst.getFill() == null)? styleFactory.fill() : visit(pst.getFill());
        Displacement disp = (pst.getDisplacement() == null)? styleFactory.displacement(0, 0) : visit(pst.getDisplacement());
        Expression offset = (pst.getPerpendicularOffset() == null) ? filterFactory.literal(0) : visitExpression(pst.getPerpendicularOffset());
        Unit uom = visitUOM(pst.getUom());
        String geom = visitGeom( pst.getGeometry());
        String name = pst.getName();
        Description desc = visitDescription(pst.getDescription());

        return styleFactory.polygonSymbolizer(name,geom,desc,uom,stroke, fill, disp, offset);
    }

    /**
     * Transform a SLD v1.1 raster symbolizer in GT raster symbolizer.
     */
    public RasterSymbolizer visit(RasterSymbolizerType rst) {
        if(rst == null) return null;

        Expression opacity = (rst.getOpacity() == null) ? filterFactory.literal(1) : visitExpression(rst.getOpacity());
        ChannelSelection selection = visit(rst.getChannelSelection());
        OverlapBehavior overlap = visitOverLap(rst.getOverlapBehavior());
        ColorMap colorMap = visit(rst.getColorMap());
        ContrastEnhancement enchance = visit(rst.getContrastEnhancement());
        ShadedRelief relief = visit(rst.getShadedRelief());
        Symbolizer outline = visit(rst.getImageOutline());
        Unit uom = visitUOM(rst.getUom());
        String geom = visitGeom( rst.getGeometry());
        String name = rst.getName();
        Description desc = visitDescription(rst.getDescription());

        if(selection == null) return null;
        
        return styleFactory.rasterSymbolizer(name,geom,desc,uom,opacity, selection, overlap, colorMap, enchance, relief, outline);
    }

    /**
     * Transform a SLD v1.1 text symbolizer in GT text symbolizer.
     */
    public TextSymbolizer visit(TextSymbolizerType tst) {
        if(tst == null) return null;

        Expression label = visitExpression(tst.getLabel());
        Font font = (tst.getFont() == null) ? styleFactory.font() : visit(tst.getFont());
        LabelPlacement placement = (tst.getLabelPlacement() == null) ? styleFactory.pointPlacement() : visit(tst.getLabelPlacement());
        Halo halo = (tst.getHalo() == null)? styleFactory.halo(Color.WHITE, 0) : visit(tst.getHalo());
        Fill fill = (tst.getFill() == null)? styleFactory.fill() : visit(tst.getFill());
        Unit uom = visitUOM(tst.getUom());
        String geom = visitGeom( tst.getGeometry());
        String name = tst.getName();
        Description desc = visitDescription(tst.getDescription());

        if(label == null) return null;
        
        return styleFactory.textSymbolizer(name,geom,desc,uom,label, font, placement, halo, fill);
    }


    //Sub elements -------------------------------------------------------------
    /**
     * Transform a SLD v1.1 legend in GT legend.
     */
    public GraphicLegend visitLegend(LegendGraphicType legendGraphic) {
        if(legendGraphic == null || legendGraphic.getGraphic() == null){
            return null;
        }

        Graphic graphic = visit(legendGraphic.getGraphic());
        if(graphic != null){
            GraphicLegend legend = styleFactory.graphicLegend(graphic);
            return legend;
        }

        return null;
    }

    /**
     * Transform a SLD v1.1 graphic in GT graphic.
     */
    private Graphic visit(GraphicType graphic) {
        if(graphic == null) return null;

        List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();

        for(Object obj : graphic.getExternalGraphicOrMark()){
            if(obj instanceof MarkType){
                symbols.add( visit((MarkType)obj));
            }else if(obj instanceof ExternalGraphicType){
                symbols.add( visit((ExternalGraphicType)obj));
            }
        }

        Expression opacity = visitExpression(graphic.getOpacity());
        Expression size = visitExpression(graphic.getSize());
        Expression rotation = visitExpression(graphic.getRotation());
        AnchorPoint anchor = visit(graphic.getAnchorPoint());
        Displacement disp = visit(graphic.getDisplacement());

        return styleFactory.graphic(symbols, opacity, size, rotation, anchor, disp);
    }

    /**
     *  Transform a SLD v1.1 stroke in GT stroke.
     */
    private Stroke visit(StrokeType strk) {
        if(strk == null) return null;

        GraphicFill fill = visit(strk.getGraphicFill());
        GraphicStroke stroke = visit(strk.getGraphicStroke());
        Expression color = Expression.NIL;
        Expression opacity = Expression.NIL;
        Expression width = Expression.NIL;
        Expression join = Expression.NIL;
        Expression cap = Expression.NIL;
        float[] dashes = null;
        Expression offset = Expression.NIL;

        List<SvgParameterType> params = strk.getSvgParameter();
        for(SvgParameterType svg : params){
            if(JAXBStatics.STROKE.equalsIgnoreCase(svg.getName())){
                color = (Expression)visitSVG(svg);
            }else if(JAXBStatics.STROKE_OPACITY.equalsIgnoreCase(svg.getName())){
                opacity = (Expression)visitSVG(svg);
            }else if(JAXBStatics.STROKE_WIDTH.equalsIgnoreCase(svg.getName())){
                width = (Expression)visitSVG(svg);
            }else if(JAXBStatics.STROKE_LINEJOIN.equalsIgnoreCase(svg.getName())){
                join = (Expression)visitSVG(svg);
            }else if(JAXBStatics.STROKE_LINECAP.equalsIgnoreCase(svg.getName())){
                cap = (Expression)visitSVG(svg);
            }else if(JAXBStatics.STROKE_DASHARRAY.equalsIgnoreCase(svg.getName())){
                dashes = (float[])visitSVG(svg);
            }else if(JAXBStatics.STROKE_DASHOFFSET.equalsIgnoreCase(svg.getName())){
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
    private Fill visit(FillType fl) {
        if(fl == null) return null;

        GraphicFill fill = visit(fl.getGraphicFill());
        Expression color = Expression.NIL;
        Expression opacity = Expression.NIL;
        
        List<SvgParameterType> params = fl.getSvgParameter();
        for(SvgParameterType svg : params){
            if(JAXBStatics.FILL.equalsIgnoreCase(svg.getName())){
                color = (Expression)visitSVG(svg);
            }else if(JAXBStatics.FILL_OPACITY.equalsIgnoreCase(svg.getName())){
                opacity = (Expression)visitSVG(svg);
            }
        }

        return styleFactory.fill(fill, color, opacity);
    }

    /**
     *  Transform a SLD v1.1 displacement in GT displacement.
     */
    private Displacement visit(DisplacementType displacement) {
        if(displacement == null) return null;
        
        Expression x = visitExpression(displacement.getDisplacementX());
        Expression y = visitExpression(displacement.getDisplacementY());
        
        return styleFactory.displacement(x, y);
    }

    /**
     *  Transform a SLD v1.1 overlap in GT overlap.
     */
    private OverlapBehavior visitOverLap(String overlapBehavior) {
        if(JAXBStatics.OVERLAP_AVERAGE.equalsIgnoreCase(overlapBehavior)){
            return OverlapBehavior.AVERAGE;
        }else if(JAXBStatics.OVERLAP_EARLIEST_ON_TOP.equalsIgnoreCase(overlapBehavior)){
            return OverlapBehavior.EARLIEST_ON_TOP;
        }else if(JAXBStatics.OVERLAP_LATEST_ON_TOP.equalsIgnoreCase(overlapBehavior)){
            return OverlapBehavior.LATEST_ON_TOP;
        }else if(JAXBStatics.OVERLAP_RANDOM.equalsIgnoreCase(overlapBehavior)){
            return OverlapBehavior.RANDOM;
        }else{
            return OverlapBehavior.RANDOM;
        }
    }

    /**
     *  Transform a SLD v1.1 channelselection in GT channel selection
     */
    private ChannelSelection visit(ChannelSelectionType channelSelection) {
        if(channelSelection == null) return null;
        
        if(channelSelection.getGrayChannel() != null){
            SelectedChannelType sct = visit(channelSelection.getGrayChannel());
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
    private ColorMap visit(ColorMapType colorMap) {
        if(colorMap == null) return null;
        
        Function function = null;
        if(colorMap.getCategorize() != null){
            function = visit(colorMap.getCategorize());
        }else if(colorMap.getInterpolate() != null){
            function = visit(colorMap.getInterpolate());
        }
        
        return styleFactory.colorMap(function);
    }

    /**
     *  Transform a SLD v1.1 contrastEnchancement in GT contrastEnchancement.
     */
    private ContrastEnhancement visit(ContrastEnhancementType contrastEnhancement) {
        if(contrastEnhancement == null) return null;
        
        Expression gamma = filterFactory.literal(contrastEnhancement.getGammaValue());
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
    private Symbolizer visit(ImageOutlineType imageOutline) {
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
    private ShadedRelief visit(ShadedReliefType shadedRelief) {
        if(shadedRelief == null) return null;
        
        boolean bright = shadedRelief.isBrightnessOnly();
        Expression relief = filterFactory.literal(shadedRelief.getReliefFactor());
        
        return styleFactory.shadedRelief(relief,bright);
    }

    /**
     *  Transform a SLD v1.1 font in GT font.
     */
    private Font visit(FontType font) {
        if(font == null) return null;
        
        List<Expression> family = new ArrayList<Expression>();
        Expression style = Expression.NIL;
        Expression weight = Expression.NIL;
        Expression size = Expression.NIL;
        
        List<SvgParameterType> params = font.getSvgParameter();
        for(SvgParameterType svg : params){
            if(JAXBStatics.FONT_FAMILY.equalsIgnoreCase(svg.getName())){
                family.add( (Expression)visitSVG(svg) );
            }else if(JAXBStatics.FONT_STYLE.equalsIgnoreCase(svg.getName())){
                style = (Expression)visitSVG(svg);
            }else if(JAXBStatics.FONT_WEIGHT.equalsIgnoreCase(svg.getName())){
                weight = (Expression)visitSVG(svg);
            }else if(JAXBStatics.FONT_SIZE.equalsIgnoreCase(svg.getName())){
                size = (Expression)visitSVG(svg);
            }
        }
        
        return styleFactory.font(family, style, weight, size);
    }

    /**
     *  Transform a SLD v1.1 halo in GT halo.
     */
    private Halo visit(HaloType halo) {
        if(halo == null) return null;
        
        Fill fill = visit(halo.getFill());
        Expression radius = visitExpression(halo.getRadius());
        
        return styleFactory.halo(fill, radius);
    }

    /**
     *  Transform a SLD v1.1 label placement in GT label placement.
     */
    private LabelPlacement visit(LabelPlacementType labelPlacement) {
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
    private AnchorPoint visit(AnchorPointType anchorPoint) {
        if(anchorPoint == null) return null;
        
        Expression x = visitExpression(anchorPoint.getAnchorPointX());
        Expression y = visitExpression(anchorPoint.getAnchorPointY());
        
        return styleFactory.anchorPoint(x, y);
    }

    private Mark visit(MarkType markType) {
        if(markType == null) return null;
        
        Expression wkn = filterFactory.literal(markType.getWellKnownName());
        ExternalMark external = null;
//        ExternalMark = visit(markType.get)
        Fill fill = visit(markType.getFill());
        Stroke stroke = visit(markType.getStroke());
        
        return styleFactory.mark(wkn, fill, stroke);
    }

    private ExternalGraphic visit(ExternalGraphicType externalGraphicType) {
        if(externalGraphicType == null) return null;
                
        OnLineResource resource = visitOnlineResource(externalGraphicType.getOnlineResource());
        Icon icon = null;
        String format = externalGraphicType.getFormat();
        Collection<ColorReplacement> replaces = Collections.emptyList();
        
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
    private GraphicFill visit(GraphicFillType graphicFill) {
        if(graphicFill == null || graphicFill.getGraphic() == null){
            return null;
        }

        Graphic graphic = visit(graphicFill.getGraphic());
        if(graphic != null){
            GraphicFill fill = styleFactory.graphicFill(graphic);
            return fill;
        }
        
        return null;
    }

    /**
     *  Transform a SLD v1.1 graphic stroke in GT graphic stroke.
     */
    private GraphicStroke visit(GraphicStrokeType graphicStroke) {
        if(graphicStroke == null || graphicStroke.getGraphic() == null){
            return null;
        }

        Graphic graphic = visit(graphicStroke.getGraphic());
        if(graphic != null){
            Expression gap = visitExpression(graphicStroke.getGap());
            Expression initialGap = visitExpression(graphicStroke.getInitialGap());
            
            GraphicStroke stroke = styleFactory.graphicStroke(graphic,gap,initialGap);
            return stroke;
        }
        
        return null;
    }
        
    /**
     * Transform a SLD v1.1 selected channel in GT selected channel.
     */
    private SelectedChannelType visit(org.geotoolkit.internal.jaxb.v110.se.SelectedChannelType channel) {
        if(channel == null) return null;
        
        String name = channel.getSourceChannelName();
        ContrastEnhancement enchance = (channel.getContrastEnhancement() == null) ? 
            StyleConstants.DEFAULT_CONTRAST_ENHANCEMENT : visit(channel.getContrastEnhancement());
        
        return styleFactory.selectedChannelType(name, enchance);
    }
    
    /**
     *  Transform a SLD v1.1 categorize function in GT categorize function.
     */
    private Categorize visit(final CategorizeType categorize) {
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
        
        final Map<Expression,Expression> values = new HashMap<Expression,Expression>();
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
    private Interpolate visit(final InterpolateType interpolate) {
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
                    visitExpression(ip.getValue()),
                    ip.getData())
            );
        }
        
        return styleFactory.interpolateFunction(lookup,values,method,mode,fallback);
    }

    /**
     *  Transform a SLD v1.1 lineplacement in GT line placement.
     */
    private LinePlacement visit(LinePlacementType linePlacement) {
        if(linePlacement == null) return null;
        
        Expression offset = visitExpression(linePlacement.getPerpendicularOffset());
        Expression initial = visitExpression(linePlacement.getInitialGap());
        Expression gap = visitExpression(linePlacement.getGap());
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
    private PointPlacement visit(PointPlacementType pointPlacement) {
        if(pointPlacement == null) return null;
        
        AnchorPoint anchor = visit(pointPlacement.getAnchorPoint());
        Displacement disp = visit(pointPlacement.getDisplacement());
        Expression rotation = visitExpression(pointPlacement.getRotation());
        
        return styleFactory.pointPlacement(anchor, disp, rotation);
    }

}
