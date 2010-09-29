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

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.swing.Icon;
import javax.xml.bind.JAXBElement;

import org.geotoolkit.feature.DefaultName;

import org.geotoolkit.ogc.xml.OGC100toGTTransformer;

import org.geotoolkit.sld.xml.v100.CssParameter;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.util.StringUtilities;

import org.geotoolkit.util.logging.Logging;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
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
import org.opengis.util.InternationalString;

/**
 * Transform old SLD v1.0.0 symbology in GT classes.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SE100toGTTransformer extends OGC100toGTTransformer {

    private static final String GENERIC_ANY = "generic:any";
    private static final String GENERIC_POINT = "generic:point";
    private static final String GENERIC_LINE = "generic:line";
    private static final String GENERIC_POLYGON = "generic:polygon";
    private static final String GENERIC_TEXT = "generic:text";
    private static final String GENERIC_RASTER = "generic:raster";

    protected final MutableStyleFactory styleFactory;

    public SE100toGTTransformer(FilterFactory2 filterFactory, MutableStyleFactory styleFactory) {
        super(filterFactory);
        this.styleFactory = styleFactory;
    }

    public OnlineResource visitOnlineResource(org.geotoolkit.sld.xml.v100.OnlineResource ort) {
        if(ort == null){
            return null;
        }

        URI uri = null;
        try {
            uri = new URI(ort.getHref());
        } catch (URISyntaxException ex) {
            Logging.getLogger(SLD110toGTTransformer.class).log(Level.WARNING, null, ex);
        }

        if (uri != null) {
            return styleFactory.onlineResource(uri);
        }

        return null;
    }

    public String visitGeom(org.geotoolkit.sld.xml.v100.Geometry geometry) {
        if(geometry == null || geometry.getPropertyName() == null || geometry.getPropertyName().getContent().trim().isEmpty()) return null;
        return geometry.getPropertyName().getContent();
    }

    public Object visitSVG(org.geotoolkit.sld.xml.v100.CssParameter css) {
        
//        JAXBElementFunctionType> 
//        String 
//        JAXBElementExpressionType> 
//        JAXBElementLiteralType> 
//        JAXBElementBinaryOperatorType> 
//        JAXBElementBinaryOperatorType> 
//        JAXBElementBinaryOperatorType> 
//        JAXBElementPropertyNameType> 
//        JAXBElementBinaryOperatorType>
        
        if(SEJAXBStatics.STROKE_DASHARRAY.equalsIgnoreCase(css.getName()) ){

            final List<Serializable> content = css.getContent();
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

        final List<Serializable> sers = css.getContent();
        if (sers.size() > 1) {
            for (Object obj : css.getContent()) {
                if (obj instanceof String) {
                    obj = StringUtilities.clean((String) obj);
                    if (((String)obj).isEmpty()) {
                        continue;
                    }
                    return filterFactory.literal(obj);
                } else if (obj instanceof JAXBElement) {
                    return visitExpression((JAXBElement<?>) obj);
                }
            }
        } else if (sers.size() == 1) {
            Object obj = css.getContent().get(0);
            if (obj instanceof String) {
                obj = StringUtilities.clean((String) obj);
                return filterFactory.literal(obj);
            } else if (obj instanceof JAXBElement) {
                return visitExpression((JAXBElement<?>) obj);
            }
        }

        return null;
    }

    public Unit visitUOM(String uom) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Transform a parametervaluetype in Expression.
     */
    public Expression visitExpression(org.geotoolkit.sld.xml.v100.ParameterValueType param) {
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

        Expression result = Expression.NIL;

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

    //Style, FTS and Rule-------------------------------------------------------
    /**
     * Transform a SLD v1.0 userstyle in GT style.
     */
    public MutableStyle visitUserStyle(org.geotoolkit.sld.xml.v100.UserStyle us) {
        if(us == null){
            return null;
        }else{
            final MutableStyle mls = styleFactory.style();
            mls.setName(us.getName());
            final InternationalString title = (us.getTitle() == null) ? null : new SimpleInternationalString(us.getTitle());
            final InternationalString abs = (us.getAbstract() == null) ? null : new SimpleInternationalString(us.getAbstract());
            mls.setDescription(styleFactory.description(title, abs));
            final Boolean def = us.isIsDefault();
            mls.setDefault( (def != null)? def : false);

            final List<org.geotoolkit.sld.xml.v100.FeatureTypeStyle> ftss = us.getFeatureTypeStyle();

            for(final org.geotoolkit.sld.xml.v100.FeatureTypeStyle obj : ftss){
                final MutableFeatureTypeStyle fts = visitFTS(obj);
                mls.featureTypeStyles().add(fts);
            }

            return mls;
        }

    }

    /**
     * Transform a SLD v1.0 FeatureTypeStyle or CoverageStyle in GT FTS.
     */
    public MutableFeatureTypeStyle visitFTS(org.geotoolkit.sld.xml.v100.FeatureTypeStyle obj){
        if(obj == null) return null;        
    
        final MutableFeatureTypeStyle fts = styleFactory.featureTypeStyle();
        final org.geotoolkit.sld.xml.v100.FeatureTypeStyle ftst = (org.geotoolkit.sld.xml.v100.FeatureTypeStyle) obj;

        fts.setName(ftst.getName());
        final InternationalString title = (ftst.getTitle() == null) ? null : new SimpleInternationalString(ftst.getTitle());
        final InternationalString abs = (ftst.getAbstract() == null) ? null : new SimpleInternationalString(ftst.getAbstract());
        fts.setDescription(styleFactory.description(title, abs));
        fts.semanticTypeIdentifiers().addAll(visitSemantics(ftst.getSemanticTypeIdentifier()));
        
        if(ftst.getFeatureTypeName() != null){
            fts.featureTypeNames().add(new DefaultName(ftst.getFeatureTypeName()));
        }
        
        if(ftst.getRule() == null || ftst.getRule().isEmpty()){
        }else{
            for(org.geotoolkit.sld.xml.v100.Rule rt : ftst.getRule()){
                fts.rules().add(visitRule(rt) );
            }
        }

        return fts;
    }

    /**
     * Transform SLD v1.0 semantics in GT semantics.
     */
    public Collection<? extends SemanticType> visitSemantics(List<String> strs){

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
     * Trasnform SLD v1.0 rule in GT Rule.
     */
    public MutableRule visitRule(org.geotoolkit.sld.xml.v100.Rule rt) {

        final MutableRule rule = styleFactory.rule();

        rule.setName(rt.getName());
        final InternationalString title = (rt.getTitle() == null) ? null : new SimpleInternationalString(rt.getTitle());
        final InternationalString abs = (rt.getAbstract() == null) ? null : new SimpleInternationalString(rt.getAbstract());
        rule.setDescription(styleFactory.description(title, abs));
        rule.setElseFilter(rt.getElseFilter() != null);
        rule.setFilter(visitFilter(rt.getFilter()));
        rule.setLegendGraphic(visitLegend(rt.getLegendGraphic()));
        rule.setMaxScaleDenominator((rt.getMaxScaleDenominator() == null) ? Double.MAX_VALUE : rt.getMaxScaleDenominator());
        rule.setMinScaleDenominator((rt.getMinScaleDenominator() == null) ? 0 : rt.getMinScaleDenominator());
        
        if(rt.getSymbolizer() == null || rt.getSymbolizer().isEmpty()){
            
        }else{
            
            for(final JAXBElement<? extends org.geotoolkit.sld.xml.v100.SymbolizerType> jax : rt.getSymbolizer()) {
                final org.geotoolkit.sld.xml.v100.SymbolizerType st = jax.getValue();

                if (st == null) {
                    continue;
                }
                if (st instanceof org.geotoolkit.sld.xml.v100.PointSymbolizer) {
                    final org.geotoolkit.sld.xml.v100.PointSymbolizer pst = (org.geotoolkit.sld.xml.v100.PointSymbolizer) st;
                    rule.symbolizers().add(visit(pst));
                } else if (st instanceof org.geotoolkit.sld.xml.v100.LineSymbolizer) {
                    final org.geotoolkit.sld.xml.v100.LineSymbolizer pst = (org.geotoolkit.sld.xml.v100.LineSymbolizer) st;
                    rule.symbolizers().add(visit(pst));
                } else if (st instanceof org.geotoolkit.sld.xml.v100.PolygonSymbolizer) {
                    final org.geotoolkit.sld.xml.v100.PolygonSymbolizer pst = (org.geotoolkit.sld.xml.v100.PolygonSymbolizer) st;
                    rule.symbolizers().add(visit(pst));
                } else if (st instanceof org.geotoolkit.sld.xml.v100.TextSymbolizer) {
                    final org.geotoolkit.sld.xml.v100.TextSymbolizer pst = (org.geotoolkit.sld.xml.v100.TextSymbolizer) st;
                    rule.symbolizers().add(visit(pst));
                } else if (st instanceof org.geotoolkit.sld.xml.v100.RasterSymbolizer) {
                    final org.geotoolkit.sld.xml.v100.RasterSymbolizer pst = (org.geotoolkit.sld.xml.v100.RasterSymbolizer) st;
                    rule.symbolizers().add(visit(pst));
                }
            }


            
        }

        return rule;
    }


    //Symbolizers---------------------------------------------------------------
    /**
     * Transform a SLD v1.0 point symbolizer in GT point symbolizer.
     */
    public PointSymbolizer visit(org.geotoolkit.sld.xml.v100.PointSymbolizer pst) {
        if(pst == null) return null;

        final Graphic graphic = (pst.getGraphic() == null) ? styleFactory.graphic() : visit(pst.getGraphic());
        final Unit uom = NonSI.PIXEL;
        final String geom = visitGeom( pst.getGeometry());
        final String name = null;
        final Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        return styleFactory.pointSymbolizer(name,geom,desc,uom,graphic);
    }

    /**
     * Transform a SLD v1.0 line symbolizer in GT line symbolizer.
     */
    public LineSymbolizer visit(org.geotoolkit.sld.xml.v100.LineSymbolizer lst) {
        if(lst == null) return null;

        final Stroke stroke = visit(lst.getStroke());
        final Expression offset = filterFactory.literal(0);
        final Unit uom = NonSI.PIXEL;
        final String geom = visitGeom( lst.getGeometry());
        final String name = null;
        final Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        return styleFactory.lineSymbolizer(name,geom,desc,uom,stroke, offset);
    }

    /**
     * Transform a SLD v1.0 polygon symbolizer in GT polygon symbolizer.
     */
    public PolygonSymbolizer visit(org.geotoolkit.sld.xml.v100.PolygonSymbolizer pst) {
        if(pst == null) return null;

        final Stroke stroke = visit(pst.getStroke());
        final Fill fill = visit(pst.getFill());
        final Displacement disp = StyleConstants.DEFAULT_DISPLACEMENT;
        final Expression offset = filterFactory.literal(0);
        final Unit uom = NonSI.PIXEL;
        final String geom = visitGeom( pst.getGeometry());
        final String name = null;
        final Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        return styleFactory.polygonSymbolizer(name,geom,desc,uom,stroke, fill, disp, offset);
    }

    /**
     * Transform a SLD v1.0 raster symbolizer in GT raster symbolizer.
     */
    public RasterSymbolizer visit(org.geotoolkit.sld.xml.v100.RasterSymbolizer rst) {
        if(rst == null) return null;

        final Expression opacity = (rst.getOpacity() == null) ? filterFactory.literal(1) : visitExpression(rst.getOpacity());
        final ChannelSelection selection = visit(rst.getChannelSelection());
        final OverlapBehavior overlap = visitOverLap(rst.getOverlapBehavior());
        final ColorMap colorMap = visit(rst.getColorMap());
        final ContrastEnhancement enchance = visit(rst.getContrastEnhancement());
        final ShadedRelief relief = visit(rst.getShadedRelief());
        final Symbolizer outline = visit(rst.getImageOutline());
        final Unit uom = NonSI.PIXEL;
        final String geom = visitGeom( rst.getGeometry());
        final String name = "";
        final Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        if(selection == null) return null;

        return styleFactory.rasterSymbolizer(name,geom,desc,uom,opacity, selection, overlap, colorMap, enchance, relief, outline);
    }

    /**
     * Transform a SLD v1.0 text symbolizer in GT text symbolizer.
     */
    public TextSymbolizer visit(org.geotoolkit.sld.xml.v100.TextSymbolizer tst) {
        if(tst == null) return null;

        final Expression label = visitExpression(tst.getLabel());
        final Font font = (tst.getFont() == null) ? styleFactory.font() : visit(tst.getFont());
        final LabelPlacement placement = (tst.getLabelPlacement() == null) ? styleFactory.pointPlacement() : visit(tst.getLabelPlacement());
        final Halo halo = visit(tst.getHalo());
        final Fill fill = visit(tst.getFill());
        final Unit uom = NonSI.PIXEL;
        final String geom = visitGeom( tst.getGeometry());
        final String name = null;
        final Description desc = StyleConstants.DEFAULT_DESCRIPTION;
        
        if(label == null) return null;
        
        return styleFactory.textSymbolizer(name,geom,desc,uom,label, font, placement, halo, fill);
    }


    //Sub elements -------------------------------------------------------------
    /**
     * Transform a SLD v1.0 legend in GT legend.
     */
    public GraphicLegend visitLegend(org.geotoolkit.sld.xml.v100.LegendGraphic legendGraphic) {
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
     * Transform a SLD v1.0 graphic in GT graphic.
     */
    private Graphic visit(org.geotoolkit.sld.xml.v100.Graphic graphic) {
        if(graphic == null) return null;

        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();

        for(final Object obj : graphic.getExternalGraphicOrMark()){
            if(obj instanceof org.geotoolkit.sld.xml.v100.Mark){
                symbols.add( visit((org.geotoolkit.sld.xml.v100.Mark)obj));
            }else if(obj instanceof org.geotoolkit.sld.xml.v100.ExternalGraphic){
                symbols.add( visit((org.geotoolkit.sld.xml.v100.ExternalGraphic)obj));
            }
        }

        final Expression opacity = visitExpression(graphic.getOpacity());
        final Expression size = visitExpression(graphic.getSize());
        final Expression rotation = visitExpression(graphic.getRotation());
        final AnchorPoint anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        final Displacement disp = StyleConstants.DEFAULT_DISPLACEMENT;

        return styleFactory.graphic(symbols, opacity, size, rotation, anchor, disp);
    }

    /**
     *  Transform a SLD v1.0 stroke in GT stroke.
     */
    private Stroke visit(org.geotoolkit.sld.xml.v100.Stroke strk) {
        if(strk == null) return null;

        final GraphicFill fill = visit(strk.getGraphicFill());
        final GraphicStroke stroke = visit(strk.getGraphicStroke());
        Expression color = Expression.NIL;
        Expression opacity = Expression.NIL;
        Expression width = Expression.NIL;
        Expression join = Expression.NIL;
        Expression cap = Expression.NIL;
        float[] dashes = null;
        Expression offset = Expression.NIL;        
        
        final List<CssParameter> params = strk.getCssParameter();
        for(final CssParameter svg : params){
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
     *  Transform a SLD v1.0 fill in GT fill.
     */
    private Fill visit(org.geotoolkit.sld.xml.v100.Fill fl) {
        if(fl == null) return null;

        final GraphicFill fill = visit(fl.getGraphicFill());
        Expression color = Expression.NIL;
        Expression opacity = Expression.NIL;
        
        final List<CssParameter> params = fl.getCssParameter();
        for(final CssParameter svg : params){
            if(SEJAXBStatics.FILL.equalsIgnoreCase(svg.getName())){
                color = (Expression)visitSVG(svg);
            }else if(SEJAXBStatics.FILL_OPACITY.equalsIgnoreCase(svg.getName())){
                opacity = (Expression)visitSVG(svg);
            }
        }

        return styleFactory.fill(fill, color, opacity);
    }

    /**
     *  Transform a SLD v1.0 displacement in GT displacement.
     */
    private Displacement visit(org.geotoolkit.sld.xml.v100.Displacement displacement) {
        if(displacement == null) return null;
        
        final Expression x = visitExpression(displacement.getDisplacementX());
        final Expression y = visitExpression(displacement.getDisplacementY());
        
        return styleFactory.displacement(x, y);
    }

    /**
     *  Transform a SLD v1.0 overlap in GT overlap.
     */
    private OverlapBehavior visitOverLap(org.geotoolkit.sld.xml.v100.OverlapBehavior overlapBehavior) {
        if(overlapBehavior == null) return OverlapBehavior.LATEST_ON_TOP;
        
        if(overlapBehavior.getAVERAGE() != null){
            return OverlapBehavior.AVERAGE;
        }else if(overlapBehavior.getEARLIESTONTOP() != null){
            return OverlapBehavior.EARLIEST_ON_TOP;
        }else if(overlapBehavior.getLATESTONTOP() != null){
            return OverlapBehavior.LATEST_ON_TOP;
        }else if(overlapBehavior.getRANDOM() != null){
            return OverlapBehavior.RANDOM;
        }else{
            return OverlapBehavior.RANDOM;
        }
    }

    /**
     *  Transform a SLD v1.0 channelselection in GT channel selection
     */
    private ChannelSelection visit(org.geotoolkit.sld.xml.v100.ChannelSelection channelSelection) {
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

    private ColorMap visit(org.geotoolkit.sld.xml.v100.ColorMap colorMap) {
        if(colorMap == null) return null;
        
        final Function function = null;
//        if(colorMap.getCategorize() != null){
//            function = visit(colorMap.getCategorize());
//        }else if(colorMap.getInterpolate() != null){
//            function = visit(colorMap.getInterpolate());
//        }
        
        colorMap.getColorMapEntry();
        
        return styleFactory.colorMap(function);
    }

    /**
     *  Transform a SLD v1.0 contrastEnchancement in GT contrastEnchancement.
     */
    private ContrastEnhancement visit(org.geotoolkit.sld.xml.v100.ContrastEnhancement contrastEnhancement) {
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
     *  Transform a SLD v1.0 outline in GT outline.
     */
    private Symbolizer visit(org.geotoolkit.sld.xml.v100.ImageOutline imageOutline) {
        if(imageOutline == null) return null;
        
        if(imageOutline.getLineSymbolizer() != null){
            return visit(imageOutline.getLineSymbolizer());
        }else if(imageOutline.getPolygonSymbolizer() != null){
            return visit(imageOutline.getPolygonSymbolizer());
        }
        
        return null;
    }

    /**
     *  Transform a SLD v1.0 shadedRelief in GT shadedRelief.
     */
    private ShadedRelief visit(org.geotoolkit.sld.xml.v100.ShadedRelief shadedRelief) {
        if(shadedRelief == null) return null;
        
        final boolean bright = shadedRelief.isBrightnessOnly();
        final Expression relief = filterFactory.literal(shadedRelief.getReliefFactor());
        
        return styleFactory.shadedRelief(relief,bright);
    }

    /**
     *  Transform a SLD v1.0 font in GT font.
     */
    private Font visit(org.geotoolkit.sld.xml.v100.Font font) {
        if(font == null) return null;
        
        final List<Expression> family = new ArrayList<Expression>();
        Expression style = Expression.NIL;
        Expression weight = Expression.NIL;
        Expression size = Expression.NIL;
        
        final List<CssParameter> params = font.getCssParameter();
        for(final CssParameter svg : params){
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
     *  Transform a SLD v1.0 halo in GT halo.
     */
    private Halo visit(org.geotoolkit.sld.xml.v100.Halo halo) {
        if(halo == null) return null;
        
        final Fill fill = visit(halo.getFill());
        final Expression radius = visitExpression(halo.getRadius());
        
        return styleFactory.halo(fill, radius);
    }

    /**
     *  Transform a SLD v1.0 label placement in GT label placement.
     */
    private LabelPlacement visit(org.geotoolkit.sld.xml.v100.LabelPlacement labelPlacement) {
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
     *  Transform a SLD v1.0 anchor in GT anchor.
     */
    private AnchorPoint visit(org.geotoolkit.sld.xml.v100.AnchorPoint anchorPoint) {
        if(anchorPoint == null) return null;
        
        final Expression x = visitExpression(anchorPoint.getAnchorPointX());
        final Expression y = visitExpression(anchorPoint.getAnchorPointY());
        
        return styleFactory.anchorPoint(x, y);
    }

    /**
     *  Transform a SLD v1.0 mark in GT mark.
     */
    private Mark visit(org.geotoolkit.sld.xml.v100.Mark markType) {
        if(markType == null) return null;
        
        final Expression wkn = filterFactory.literal(markType.getWellKnownName());
        final Fill fill = visit(markType.getFill());
        final Stroke stroke = visit(markType.getStroke());
        
        return styleFactory.mark(wkn, fill, stroke);
    }

    private ExternalGraphic visit(org.geotoolkit.sld.xml.v100.ExternalGraphic externalGraphicType) {
        if(externalGraphicType == null) return null;
        
        final OnlineResource resource = visitOnlineResource(externalGraphicType.getOnlineResource());
        final Icon icon = null;
        final String format = externalGraphicType.getFormat();
        final Collection<ColorReplacement> replaces = Collections.emptyList();
        
        if (resource != null){
            return styleFactory.externalGraphic(resource, format, replaces);
        } else if (icon != null){
            return styleFactory.externalGraphic(icon, replaces);
        } else {
            return null;
        }
    }

    /**
     *  Transform a SLD v1.0 graphic fill in GT graphic fill.
     */
    private GraphicFill visit(org.geotoolkit.sld.xml.v100.GraphicFill graphicFill) {
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
     *  Transform a SLD v1.0 graphic stroke in GT graphic stroke.
     */
    private GraphicStroke visit(org.geotoolkit.sld.xml.v100.GraphicStroke graphicStroke) {
        if(graphicStroke == null || graphicStroke.getGraphic() == null){
            return null;
        }

        final Graphic graphic = visit(graphicStroke.getGraphic());
        if(graphic != null){
            final Expression gap = filterFactory.literal(0);
            final Expression initialGap = filterFactory.literal(0);
            
            return styleFactory.graphicStroke(graphic,gap,initialGap);
        }
        
        return null;
    }
        
    /**
     * Transform a SLD v1.0 selected channel in GT selected channel.
     */
    private SelectedChannelType visit(org.geotoolkit.sld.xml.v100.SelectedChannelType channel) {
        if(channel == null) return null;
        
        final String name = channel.getSourceChannelName();
        final ContrastEnhancement enchance = (channel.getContrastEnhancement() == null) ?
            StyleConstants.DEFAULT_CONTRAST_ENHANCEMENT : visit(channel.getContrastEnhancement());
        
        return styleFactory.selectedChannelType(name, enchance);
    }
    
    /**
     *  Transform a SLD v1.0 lineplacement in GT line placement.
     */
    private LinePlacement visit(org.geotoolkit.sld.xml.v100.LinePlacement linePlacement) {
        if(linePlacement == null) return null;
        
        final Expression offset = visitExpression(linePlacement.getPerpendicularOffset());
        final Expression initial = filterFactory.literal(0);
        final Expression gap = filterFactory.literal(0);
        final boolean repeated = false;
        final boolean aligned = false;
        final boolean generalize = false;
        
        return styleFactory.linePlacement(offset, initial, gap, repeated, aligned, generalize);
    }

    /**
     *  Transform a SLD v1.0 pointplacement in GT point placement.
     */
    private PointPlacement visit(org.geotoolkit.sld.xml.v100.PointPlacement pointPlacement) {
        if(pointPlacement == null) return null;
        
        final AnchorPoint anchor = visit(pointPlacement.getAnchorPoint());
        final Displacement disp = visit(pointPlacement.getDisplacement());
        final Expression rotation = visitExpression(pointPlacement.getRotation());
        
        return styleFactory.pointPlacement(anchor, disp, rotation);
    }

}
