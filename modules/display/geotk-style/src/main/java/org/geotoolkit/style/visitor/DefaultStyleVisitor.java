/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.geotoolkit.filter.visitor.DefaultFilterVisitor;

import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
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
import org.opengis.style.GraphicalSymbol;
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
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

/**
 * Abstract implementation of StyleVisitor that simply walks the data structure.
 * <p>
 * This class implements the full StyleVisitor interface and will visit every Style member of a
 * Style object. This class performs no actions and is not intended to be used directly, instead
 * extend it and override the methods for the Style type you are interested in. Remember to call the
 * super method if you want to ensure that the entire style tree is still visited.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class DefaultStyleVisitor extends DefaultFilterVisitor implements StyleVisitor{

    @Override
    public Object visit(Style style, Object data) {
        final List<? extends FeatureTypeStyle> ftss = style.featureTypeStyles();
        if(ftss != null){
            for(FeatureTypeStyle fts : ftss){
                data = fts.accept(this, data);
            }
        }
        final Symbolizer def = style.getDefaultSpecification();
        if(def != null){
            data = def.accept(this, data);
        }

        return data;
    }

    @Override
    public Object visit(FeatureTypeStyle featureTypeStyle, Object data) {
        final Id ids = featureTypeStyle.getFeatureInstanceIDs();
        if(ids != null){
            data = ids.accept(this, data);
        }
        final List<? extends Rule> rules = featureTypeStyle.rules();
        if(rules != null){
            for(Rule r : rules){
                data = r.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(Rule rule, Object data) {
        final Filter filter = rule.getFilter();
        if(filter != null){
            data = filter.accept(this, data);
        }
        final GraphicLegend legend = rule.getLegend();
        if(legend != null){
            data = legend.accept(this, data);
        }
        final List<? extends Symbolizer> symbols = rule.symbolizers();
        if(symbols != null){
            for(Symbolizer symbol : symbols){
                data = symbol.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(PointSymbolizer pointSymbolizer, Object data) {
        final Graphic gra = pointSymbolizer.getGraphic();
        if(gra != null){
            data = gra.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(LineSymbolizer lineSymbolizer, Object data) {
        final Expression offset = lineSymbolizer.getPerpendicularOffset();
        if(offset != null){
            data = offset.accept(this, data);
        }
        final Stroke stroke = lineSymbolizer.getStroke();
        if(stroke != null){
            data = stroke.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(PolygonSymbolizer polygonSymbolizer, Object data) {
        final Displacement disp = polygonSymbolizer.getDisplacement();
        if(disp != null){
            data = disp.accept(this, data);
        }
        final Fill fill = polygonSymbolizer.getFill();
        if(fill != null){
            data = fill.accept(this, data);
        }
        final Expression offset = polygonSymbolizer.getPerpendicularOffset();
        if(offset != null){
            data = offset.accept(this, data);
        }
        final Stroke stroke = polygonSymbolizer.getStroke();
        if(stroke != null){
            data = stroke.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(TextSymbolizer textSymbolizer, Object data) {
        final Fill fill = textSymbolizer.getFill();
        if(fill != null){
            data = fill.accept(this, data);
        }
        final Font font = textSymbolizer.getFont();
        if(font != null){
            data = font.accept(this, data);
        }
        final Halo halo = textSymbolizer.getHalo();
        if(halo != null){
            data = halo.accept(this, data);
        }
        final Expression label = textSymbolizer.getLabel();
        if(label != null){
            data = label.accept(this, data);
        }
        final LabelPlacement place = textSymbolizer.getLabelPlacement();
        if(place != null){
            data = place.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(RasterSymbolizer rasterSymbolizer, Object data) {
        final ChannelSelection cs = rasterSymbolizer.getChannelSelection();
        if(cs != null){
            data = cs.accept(this, data);
        }
        final ColorMap cm = rasterSymbolizer.getColorMap();
        if(cm != null){
            data = cm.accept(this, data);
        }
        final ContrastEnhancement ce = rasterSymbolizer.getContrastEnhancement();
        if(ce != null){
            data = ce.accept(this, data);
        }
        final Symbolizer sym = rasterSymbolizer.getImageOutline();
        if(sym != null){
            data = sym.accept(this, data);
        }
        final Expression opa = rasterSymbolizer.getOpacity();
        if(opa != null){
            data = opa.accept(this, data);
        }
        final ShadedRelief shade = rasterSymbolizer.getShadedRelief();
        if(shade != null){
            data = shade.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(ExtensionSymbolizer extension, Object data) {
        final Map<String,Expression> exps = extension.getParameters();
        if(exps != null){
            for(Expression exp : exps.values()){
                data = exp.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(Description description, Object data) {
        return data;
    }

    @Override
    public Object visit(Displacement displacement, Object data) {
        final Expression x = displacement.getDisplacementX();
        if(x != null){
            data = x.accept(this, data);
        }
        final Expression y = displacement.getDisplacementY();
        if(y != null){
            data = y.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(Fill fill, Object data) {
        final Expression color = fill.getColor();
        if(color != null){
            data = color.accept(this, data);
        }
        final GraphicFill gf = fill.getGraphicFill();
        if(gf != null){
            data = gf.accept(this, data);
        }
        final Expression opa = fill.getOpacity();
        if(opa != null){
            data = opa.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(Font font, Object data) {
        final List<Expression> families = font.getFamily();
        if(families != null){
            for(Expression family : families){
                data = family.accept(this, data);
            }
        }

        final Expression size = font.getSize();
        if(size != null){
            data = size.accept(this, data);
        }
        final Expression style = font.getStyle();
        if(style != null){
            data = style.accept(this, data);
        }
        final Expression weight = font.getWeight();
        if(weight != null){
            data = weight.accept(this, data);
        }

        return data;
    }

    @Override
    public Object visit(Stroke stroke, Object data) {
        final Expression color = stroke.getColor();
        if(color != null){
            data = color.accept(this, data);
        }

        final Expression offset = stroke.getDashOffset();
        if(offset != null){
            data = offset.accept(this, data);
        }

        final GraphicFill gf = stroke.getGraphicFill();
        if(gf != null){
            data = gf.accept(this, data);
        }

        final GraphicStroke gs = stroke.getGraphicStroke();
        if(gs != null){
            data = gs.accept(this, data);
        }

        final Expression lc = stroke.getLineCap();
        if(lc != null){
            data = lc.accept(this, data);
        }

        final Expression lj = stroke.getLineJoin();
        if(lj != null){
            data = lj.accept(this, data);
        }

        final Expression opa = stroke.getOpacity();
        if(opa != null){
            data = opa.accept(this, data);
        }

        final Expression width = stroke.getWidth();
        if(width != null){
            data = width.accept(this, data);
        }

        return data;
    }

    @Override
    public Object visit(Graphic graphic, Object data) {
        final AnchorPoint ac = graphic.getAnchorPoint();
        if(ac != null){
            data = ac.accept(this, data);
        }
        final Displacement disp = graphic.getDisplacement();
        if(disp != null){
            data = disp.accept(this, data);
        }
        final Expression opa = graphic.getOpacity();
        if(opa != null){
            data = opa.accept(this, data);
        }
        final Expression rot = graphic.getRotation();
        if(rot != null){
            data = rot.accept(this, data);
        }
        final Expression size = graphic.getSize();
        if(size != null){
            data = size.accept(this, data);
        }
        final List<GraphicalSymbol> symbols = graphic.graphicalSymbols();
        if(symbols != null){
            for(GraphicalSymbol gs : symbols){
                if(gs instanceof Mark){
                    data = ((Mark)gs).accept(this, data);
                }else if(gs instanceof ExternalGraphic){
                    data = ((ExternalGraphic)gs).accept(this, data);
                }
            }
        }

        return data;
    }

    @Override
    public Object visit(GraphicFill graphicFill, Object data) {
        return visit((Graphic)graphicFill, data);
    }

    @Override
    public Object visit(GraphicStroke graphicStroke, Object data) {
        data = visit((Graphic)graphicStroke, data);
        final Expression gap = graphicStroke.getGap();
        if(gap != null){
            data = gap.accept(this, data);
        }
        final Expression igap = graphicStroke.getInitialGap();
        if(igap != null){
            data = igap.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(Mark mark, Object data) {
        final ExternalMark em = mark.getExternalMark();
        if(em != null){
            data = em.accept(this, data);
        }
        final Fill fill = mark.getFill();
        if(fill != null){
            data = fill.accept(this, data);
        }
        final Stroke stroke = mark.getStroke();
        if(stroke != null){
            data = stroke.accept(this, data);
        }
        final Expression wkn = mark.getWellKnownName();
        if(wkn != null){
            data = wkn.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(ExternalMark externalMark, Object data) {
        return data;
    }

    @Override
    public Object visit(ExternalGraphic externalGraphic, Object data) {
        final Collection<ColorReplacement> replaces = externalGraphic.getColorReplacements();
        if(replaces != null){
            for(ColorReplacement r : replaces){
                data = r.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(PointPlacement pointPlacement, Object data) {
        final AnchorPoint ap = pointPlacement.getAnchorPoint();
        if(ap != null){
            data = ap.accept(this, data);
        }
        final Displacement disp = pointPlacement.getDisplacement();
        if(disp != null){
            data = disp.accept(this, data);
        }
        final Expression rot = pointPlacement.getRotation();
        if(rot != null){
            data = rot.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(AnchorPoint anchorPoint, Object data) {
        final Expression x = anchorPoint.getAnchorPointX();
        if(x != null){
            data = x.accept(this, data);
        }

        final Expression y = anchorPoint.getAnchorPointY();
        if(y != null){
            data = y.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(LinePlacement linePlacement, Object data) {
        final Expression gap = linePlacement.getGap();
        if(gap != null){
            data = gap.accept(this, data);
        }
        final Expression igap = linePlacement.getInitialGap();
        if(igap != null){
            data = igap.accept(this, data);
        }
        final Expression offset = linePlacement.getPerpendicularOffset();
        if(offset != null){
            data = offset.accept(this, data);
        }

        return data;
    }

    @Override
    public Object visit(GraphicLegend graphicLegend, Object data) {
        return visit((Graphic)graphicLegend, data);
    }

    @Override
    public Object visit(Halo halo, Object data) {
        final Fill fill = halo.getFill();
        if(fill != null){
            data = fill.accept(this, data);
        }

        final Expression radius = halo.getRadius();
        if(radius != null){
            data = radius.accept(this, data);
        }

        return data;
    }

    @Override
    public Object visit(ColorMap colorMap, Object data) {
        final Function fct = colorMap.getFunction();
        if(fct != null){
            data = fct.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(ColorReplacement colorReplacement, Object data) {
        final Function fct = colorReplacement.getRecoding();
        if(fct != null){
            data = fct.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(ContrastEnhancement contrastEnhancement, Object data) {
        final Expression gamma = contrastEnhancement.getGammaValue();
        if(gamma != null){
            data = gamma.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(ChannelSelection channelSelection, Object data) {
        final SelectedChannelType sct = channelSelection.getGrayChannel();
        if(sct != null){
            data = sct.accept(this, data);
        }

        final SelectedChannelType[] scts = channelSelection.getRGBChannels();
        if(scts != null){
            for(SelectedChannelType sc : scts){
                if(sc != null){
                    data = sc.accept(this, data);
                }
            }
        }

        return data;
    }

    @Override
    public Object visit(SelectedChannelType selectChannelType, Object data) {
        final ContrastEnhancement enc = selectChannelType.getContrastEnhancement();
        if(enc != null){
            data = enc.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(ShadedRelief shadedRelief, Object data) {
        final Expression exp = shadedRelief.getReliefFactor();
        if(exp != null){
            data = exp.accept(this, data);
        }
        return data;
    }

}
