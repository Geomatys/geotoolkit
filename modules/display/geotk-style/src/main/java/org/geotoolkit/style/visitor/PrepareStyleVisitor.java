/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.style.visitor;

import java.util.List;
import javax.measure.unit.Unit;
import org.geotoolkit.filter.visitor.PrepareFilterVisitor;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.feature.type.ComplexType;
import org.opengis.filter.expression.Expression;
import org.opengis.style.AnchorPoint;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ColorReplacement;
import org.opengis.style.ContrastEnhancement;
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
import org.opengis.style.Halo;
import org.opengis.style.LabelPlacement;
import org.opengis.style.LinePlacement;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.PointPlacement;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.SelectedChannelType;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Stroke;
import org.opengis.style.Style;
import org.opengis.style.StyleVisitor;
import org.opengis.style.TextSymbolizer;

/**
 * Copy and simplify operations
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PrepareStyleVisitor extends PrepareFilterVisitor implements StyleVisitor{
    
    private static final MutableStyleFactory SF = new DefaultStyleFactory();
            
    public PrepareStyleVisitor(final Class clazz,final ComplexType expectedType){
        super(clazz, expectedType);
    }

    @Override
    public Object visit(Style style, Object o) {
        //TODO
        return style;
    }

    @Override
    public Object visit(FeatureTypeStyle fts, Object o) {
        //TODO
        return fts;
    }

    @Override
    public Object visit(Rule rule, Object o) {
        //TODO
        return rule;
    }

    @Override
    public Object visit(PointSymbolizer ps, Object o) {
        final Graphic orig = ps.getGraphic();
        if(orig == null){
            return ps;
        }
        
        final Graphic opt = (Graphic)orig.accept(this, o);
        if(opt == orig){
            return ps;
        }        
        
        //recreate symbolizer
        return SF.pointSymbolizer(
                ps.getName(), 
                ps.getGeometryPropertyName(), 
                ps.getDescription(), 
                ps.getUnitOfMeasure(), 
                opt);
    }

    @Override
    public Object visit(LineSymbolizer ls, Object o) {
        Expression offset = ls.getPerpendicularOffset();
        Stroke stroke = ls.getStroke();
        
        if(offset != null){
            offset = (Expression) offset.accept(this, o);
        }        
        if(stroke != null){
            stroke = (Stroke)stroke.accept(this, o);
        }
        
        //recreate symbolizer
        return SF.lineSymbolizer(
                ls.getName(), 
                ls.getGeometryPropertyName(), 
                ls.getDescription(), 
                ls.getUnitOfMeasure(), 
                stroke,
                offset);
    }

    @Override
    public Object visit(PolygonSymbolizer ps, Object o) {
        Displacement disp = ps.getDisplacement();
        Fill fill = ps.getFill();
        Expression offset = ps.getPerpendicularOffset();
        Stroke stroke = ps.getStroke();
        
        if(disp != null){
            disp = (Displacement) disp.accept(this, o);
        }
        if(fill != null){
            fill = (Fill) fill.accept(this, o);
        }
        if(offset != null){
            offset = (Expression) offset.accept(this, o);
        }
        if(stroke != null){
            stroke = (Stroke)stroke.accept(this, o);
        }
        
        //recreate symbolizer
        return SF.polygonSymbolizer(ps.getName(),
                ps.getGeometryPropertyName(), 
                ps.getDescription(), 
                ps.getUnitOfMeasure(), 
                stroke, fill, disp, offset);
    }

    @Override
    public Object visit(TextSymbolizer ts, Object o) {
        Fill fill = ts.getFill();
        Font font = ts.getFont();
        Halo halo = ts.getHalo();
        Expression label = ts.getLabel();
        LabelPlacement place = ts.getLabelPlacement();
        
        if(fill != null){
            fill = (Fill)fill.accept(this, o);
        }
        if(font != null){
            font = (Font)font.accept(this, o);
        }
        if(halo != null){
            halo = (Halo)halo.accept(this, o);
        }
        if(label != null){
            label = (Expression)label.accept(this, o);
        }
        if(place != null){
            place = (LabelPlacement)place.accept(this, o);
        }
        
        //recreate symbolizer
        return SF.textSymbolizer(ts.getName(), 
                ts.getGeometryPropertyName(), 
                ts.getDescription(), 
                ts.getUnitOfMeasure(), 
                label, font, place, halo, fill);
    }

    @Override
    public Object visit(RasterSymbolizer rs, Object o) {
        //nothing to optimize here
        return rs;
    }

    @Override
    public Object visit(ExtensionSymbolizer es, Object o) {
        return es;
    }

    @Override
    public Object visit(Description d, Object o) {
        return d;
    }

    @Override
    public Object visit(Displacement d, Object o) {        
        return SF.displacement(
                (Expression)d.getDisplacementX().accept(this, o), 
                (Expression)d.getDisplacementY().accept(this, o));
    }

    @Override
    public Object visit(Fill fill, Object o) {
        Expression color = fill.getColor();
        GraphicFill gra = fill.getGraphicFill();
        Expression opacity = fill.getOpacity();
        
        if(color != null){
            color = (Expression)color.accept(this, o);
        }
        if(gra != null){
            gra = (GraphicFill)gra.accept(this, o);
        }
        if(opacity != null){
            opacity = (Expression)opacity.accept(this, o);
        }
        
        return SF.fill(gra, color, opacity);
    }

    @Override
    public Object visit(Font font, Object o) {
        Expression size = font.getSize();
        Expression style = font.getStyle();
        Expression weight = font.getWeight();
        
        if(size != null){
            size = (Expression)size.accept(this, o);
        }
        if(style != null){
            style = (Expression)style.accept(this, o);
        }
        if(weight != null){
            weight = (Expression)weight.accept(this, o);
        }
        
        return SF.font(font.getFamily(), style, weight, size);        
    }

    @Override
    public Object visit(Stroke stroke, Object o) {
        Expression color = stroke.getColor();
        Expression offset = stroke.getDashOffset();
        GraphicFill grafill = stroke.getGraphicFill();
        GraphicStroke grastroke = stroke.getGraphicStroke();
        Expression cap = stroke.getLineCap();
        Expression join = stroke.getLineJoin();
        Expression opacity = stroke.getOpacity();
        Expression width = stroke.getWidth();
        
        if(color != null){
            color = (Expression)color.accept(this, o);
        }
        if(offset != null){
            offset = (Expression)offset.accept(this, o);
        }
        if(grafill != null){
            grafill = (GraphicFill)grafill.accept(this, o);
        }
        if(grastroke != null){
            grastroke = (GraphicStroke)grastroke.accept(this, o);
        }
        if(cap != null){
            cap = (Expression)cap.accept(this, o);
        }
        if(join != null){
            join = (Expression)join.accept(this, o);
        }
        if(opacity != null){
            opacity = (Expression)opacity.accept(this, o);
        }
        if(width != null){
            width = (Expression)width.accept(this, o);
        }
        
        if(grafill != null){
            return SF.stroke(grafill, color, opacity, width, join, cap, stroke.getDashArray(), offset);
        }else if(grastroke != null){
            return SF.stroke(grastroke, color, opacity, width, join, cap, stroke.getDashArray(), offset);
        }else{
            return SF.stroke(color, opacity, width, join, cap, stroke.getDashArray(), offset);
        }
    }

    @Override
    public Object visit(Graphic grphc, Object o) {
        //TODO
        return grphc;
    }

    @Override
    public Object visit(GraphicFill gf, Object o) {
        //TODO
        return gf;
    }

    @Override
    public Object visit(GraphicStroke gs, Object o) {
        //TODO
        return gs;
    }

    @Override
    public Object visit(Mark mark, Object o) {
        //TODO
        return mark;
    }

    @Override
    public Object visit(ExternalMark em, Object o) {
        //TODO
        return em;
    }

    @Override
    public Object visit(ExternalGraphic eg, Object o) {
        //TODO
        return eg;
    }

    @Override
    public Object visit(PointPlacement pp, Object o) {
        //TODO
        return pp;
    }

    @Override
    public Object visit(AnchorPoint ap, Object o) {
        //TODO
        return ap;
    }

    @Override
    public Object visit(LinePlacement lp, Object o) {
        //TODO
        return lp;
    }

    @Override
    public Object visit(GraphicLegend gl, Object o) {
        return gl;
    }

    @Override
    public Object visit(Halo halo, Object o) {
        //TODO
        return halo;
    }

    @Override
    public Object visit(ColorMap cm, Object o) {
        return cm;
    }

    @Override
    public Object visit(ColorReplacement cr, Object o) {
        return cr;
    }

    @Override
    public Object visit(ContrastEnhancement ce, Object o) {
        return ce;
    }

    @Override
    public Object visit(ChannelSelection cs, Object o) {
        return cs;
    }

    @Override
    public Object visit(SelectedChannelType sct, Object o) {
        return sct;
    }

    @Override
    public Object visit(ShadedRelief sr, Object o) {
        return sr;
    }
    
}
