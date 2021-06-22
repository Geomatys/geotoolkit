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
import javax.measure.Unit;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.v100.PropertyNameType;
import org.geotoolkit.sld.xml.v100.CssParameter;
import org.geotoolkit.sld.xml.v100.ParameterValueType;
import org.apache.sis.util.logging.Logging;
import org.opengis.filter.Expression;
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
import org.opengis.util.GenericName;
import org.apache.sis.measure.Units;
import org.geotoolkit.ogc.xml.FilterToOGC100Converter;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GTtoSE100Transformer extends FilterToOGC100Converter implements StyleVisitor{

    private static final String GENERIC_ANY = "generic:any";
    private static final String GENERIC_POINT = "generic:point";
    private static final String GENERIC_LINE = "generic:line";
    private static final String GENERIC_POLYGON = "generic:polygon";
    private static final String GENERIC_TEXT = "generic:text";
    private static final String GENERIC_RASTER = "generic:raster";

    private final org.geotoolkit.sld.xml.v100.ObjectFactory sld_factory_v100;

    public GTtoSE100Transformer(){
        this.sld_factory_v100 = new org.geotoolkit.sld.xml.v100.ObjectFactory();
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
    public QName visitName(final GenericName name){
        return new QName(name.scope().isGlobal() ? null : name.scope().name().toString(), name.toString());
    }

    /**
     * Transform a Unit to the corresponding SLD string.
     */
    public String visitUOM(final Unit<Length> uom) {
        if(uom.equals(Units.METRE)){
            return "http://www.opengeospatial.org/se/units/metre";
        }else if(uom.equals(Units.FOOT) ){
            return "http://www.opengeospatial.org/se/units/foot";
        }else{
            return "http://www.opengeospatial.org/se/units/pixel";
        }
    }

    /**
     * Transform a GT Style in Jaxb UserStyle
     */
    @Override
    public org.geotoolkit.sld.xml.v100.UserStyle visit(final Style style, final Object data) {
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
    public org.geotoolkit.sld.xml.v100.FeatureTypeStyle visit(final FeatureTypeStyle fts, final Object data) {

        //normally we should try to figure out if we have here a coverage FTS or not
        //no need, SLD 1.0.0 only have feature tag

        final org.geotoolkit.sld.xml.v100.FeatureTypeStyle ftst = sld_factory_v100.createFeatureTypeStyle();

        ftst.setName(fts.getName());

        if (!fts.featureTypeNames().isEmpty()) {
            final GenericName name = fts.featureTypeNames().iterator().next();
            final String local = name.toString();
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
    public org.geotoolkit.sld.xml.v100.Rule visit(final Rule rule, final Object data) {

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
            rt.setFilter(apply(rule.getFilter()));
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
    public JAXBElement<org.geotoolkit.sld.xml.v100.PointSymbolizer> visit(final PointSymbolizer point, final Object data) {
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
    public JAXBElement<org.geotoolkit.sld.xml.v100.LineSymbolizer> visit(final LineSymbolizer line, final Object data) {
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
    public JAXBElement<org.geotoolkit.sld.xml.v100.PolygonSymbolizer> visit(final PolygonSymbolizer polygon, final Object data) {
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
    public JAXBElement<org.geotoolkit.sld.xml.v100.TextSymbolizer> visit(final TextSymbolizer text, final Object data) {
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
    public JAXBElement<org.geotoolkit.sld.xml.v100.RasterSymbolizer> visit(final RasterSymbolizer raster, final Object data) {
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
    public Object visit(final ExtensionSymbolizer extension, final Object data) {
        // extended symbolizers are not supported in XML
        return null;
    }

    /**
     * transform a GT description in jaxb description.
     */
    @Override
    public Object visit(final Description description, final Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store description.");
    }

    /**
     * Transform a GT displacement in jaxb displacement.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.Displacement visit(final Displacement displacement, final Object data) {
        final org.geotoolkit.sld.xml.v100.Displacement disp = sld_factory_v100.createDisplacement();
        disp.setDisplacementX( visitExpression(displacement.getDisplacementX()) );
        disp.setDisplacementY( visitExpression(displacement.getDisplacementY()) );
        return disp;
    }

    /**
     * Transform a GT fill in jaxb fill.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.Fill visit(final Fill fill, final Object data) {
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
    public org.geotoolkit.sld.xml.v100.Font visit(final Font font, final Object data) {
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
    public org.geotoolkit.sld.xml.v100.Stroke visit(final Stroke stroke, final Object data) {
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
    public org.geotoolkit.sld.xml.v100.Graphic visit(final Graphic graphic, final Object data) {
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
    public org.geotoolkit.sld.xml.v100.GraphicFill visit(final GraphicFill graphicFill, final Object data) {
        final org.geotoolkit.sld.xml.v100.GraphicFill gft = sld_factory_v100.createGraphicFill();
        gft.setGraphic( visit((Graphic)graphicFill,null) );
        return gft;
    }

    /**
     * Transform a GT graphic stroke in jaxb graphic stroke.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.GraphicStroke visit(final GraphicStroke graphicStroke, final Object data) {
        final org.geotoolkit.sld.xml.v100.GraphicStroke gst = sld_factory_v100.createGraphicStroke();
        gst.setGraphic( visit((Graphic)graphicStroke,null) );
        return gst;
    }

    /**
     * Transform a GT Mark in jaxb Mark.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.Mark visit(final Mark mark, final Object data) {
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
    public Object visit(final ExternalMark externalMark, final Object data) {
        return null;
    }

    /**
     * Transform a GT external graphic in jaxb externla graphic.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.ExternalGraphic visit(final ExternalGraphic externalGraphic, final Object data) {
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
    public org.geotoolkit.sld.xml.v100.PointPlacement visit(final PointPlacement pointPlacement, final Object data) {
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
    public org.geotoolkit.sld.xml.v100.AnchorPoint visit(final AnchorPoint anchorPoint, final Object data) {
        final org.geotoolkit.sld.xml.v100.AnchorPoint apt = sld_factory_v100.createAnchorPoint();
        apt.setAnchorPointX( visitExpression(anchorPoint.getAnchorPointX()) );
        apt.setAnchorPointY( visitExpression(anchorPoint.getAnchorPointY()) );
        return apt;
    }

    /**
     * transform a GT lineplacement in jaxb line placement.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.LinePlacement visit(final LinePlacement linePlacement, final Object data) {
        final org.geotoolkit.sld.xml.v100.LinePlacement lpt = sld_factory_v100.createLinePlacement();
        lpt.setPerpendicularOffset( visitExpression(linePlacement.getPerpendicularOffset()) );
        return lpt;
    }

    /**
     * Transform a GT label placement in jaxb label placement.
     */
    public org.geotoolkit.sld.xml.v100.LabelPlacement visit(final LabelPlacement labelPlacement, final Object data) {
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
    public org.geotoolkit.sld.xml.v100.LegendGraphic visit(final GraphicLegend graphicLegend, final Object data) {
        final org.geotoolkit.sld.xml.v100.LegendGraphic lgt = sld_factory_v100.createLegendGraphic();
        lgt.setGraphic( visit((Graphic)graphicLegend,null) );
        return lgt;
    }

    /**
     * Transform a GT onlineResource in jaxb online resource.
     */
    public org.geotoolkit.sld.xml.v100.OnlineResource visit(final OnlineResource or, final Object data){
        final org.geotoolkit.sld.xml.v100.OnlineResource ort = sld_factory_v100.createOnlineResource();
        try {
            ort.setHref(or.getLinkage().toURL().toString());
        } catch (MalformedURLException ex) {
            Logging.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
        }
        return ort;
    }

    /**
     * transform a GT halo in a jaxb halo.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.Halo visit(final Halo halo, final Object data) {
        final org.geotoolkit.sld.xml.v100.Halo ht = sld_factory_v100.createHalo();
        ht.setFill( visit(halo.getFill(),null) );
        ht.setRadius( visitExpression(halo.getRadius()) );
        return ht;
    }

    @Override
    public org.geotoolkit.sld.xml.v100.ColorMap visit(final ColorMap colorMap, final Object data) {
        //TODO Fix that when better undestanding raster functions.
        final org.geotoolkit.sld.xml.v100.ColorMap cmt = sld_factory_v100.createColorMap();
        cmt.getColorMapEntry();

        return cmt;
    }

    @Override
    public Object visit(final ColorReplacement colorReplacement, final Object data) {
        throw new UnsupportedOperationException("SLD v1.0.0 doesnt have a xml tag to store color replacements.");
    }

    /**
     * Transform a GT constrast enchancement in jaxb constrast enchancement
     */
    @Override
    public org.geotoolkit.sld.xml.v100.ContrastEnhancement visit(final ContrastEnhancement contrastEnhancement, final Object data) {
        final org.geotoolkit.sld.xml.v100.ContrastEnhancement cet = sld_factory_v100.createContrastEnhancement();
        final Number gamma = (Number) contrastEnhancement.getGammaValue().apply(null);
        cet.setGammaValue(gamma != null ? gamma.doubleValue() : null);

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
    public org.geotoolkit.sld.xml.v100.ChannelSelection visit(final ChannelSelection channelSelection, final Object data) {
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
    public org.geotoolkit.sld.xml.v100.OverlapBehavior visit(final OverlapBehavior overlapBehavior, final Object data) {
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
    public org.geotoolkit.sld.xml.v100.SelectedChannelType visit(final SelectedChannelType selectChannelType, final Object data) {
        final org.geotoolkit.sld.xml.v100.SelectedChannelType sct = sld_factory_v100.createSelectedChannelType();
        sct.setContrastEnhancement( visit(selectChannelType.getContrastEnhancement(), null) );
        sct.setSourceChannelName( selectChannelType.getChannelName() );
        return sct;
    }

    /**
     * Transform a GT shaded relief in jaxb shaded relief.
     */
    @Override
    public org.geotoolkit.sld.xml.v100.ShadedRelief visit(final ShadedRelief shadedRelief, final Object data) {
        final org.geotoolkit.sld.xml.v100.ShadedRelief srt = sld_factory_v100.createShadedRelief();
        srt.setBrightnessOnly(shadedRelief.isBrightnessOnly());
        final Number rf = (Number) shadedRelief.getReliefFactor().apply(null);
        srt.setReliefFactor(rf != null ? rf.doubleValue() : null);
        return srt;
    }

}
