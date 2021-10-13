/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2014, Geomatys
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
import org.opengis.filter.Filter;
import org.opengis.filter.ResourceId;
import org.opengis.filter.Expression;
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
 * Expression visitor that returns a list of all Feature attributs requiered by this style.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ListingPropertyVisitor extends org.geotoolkit.filter.visitor.ListingPropertyVisitor implements StyleVisitor{

    public static final ListingPropertyVisitor VISITOR = new ListingPropertyVisitor();

    @Override
    public Object visit(final Style style, Object data) {
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
    public Object visit(final FeatureTypeStyle featureTypeStyle, Object data) {
        final ResourceId ids = featureTypeStyle.getFeatureInstanceIDs();
        if(ids != null){
            visit(ids, (Collection<String>) data);
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
    public Object visit(final Rule rule, Object data) {
        final Filter filter = rule.getFilter();
        if(filter != null){
            visit(filter, (Collection<String>) data);
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

    private void visitGeomName(Symbolizer symbolizer, Object data){
        final Expression exp = symbolizer.getGeometry();
        if(exp!=null){
            visit(exp, (Collection<String>) data);
        }
    }

    @Override
    public Object visit(final PointSymbolizer pointSymbolizer, Object data) {
        visitGeomName(pointSymbolizer, data);

        final Graphic gra = pointSymbolizer.getGraphic();
        if(gra != null){
            data = gra.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(final LineSymbolizer lineSymbolizer, Object data) {
        visitGeomName(lineSymbolizer, data);

        final Expression offset = lineSymbolizer.getPerpendicularOffset();
        if(offset != null){
            visit(offset, (Collection<String>) data);
        }
        final Stroke stroke = lineSymbolizer.getStroke();
        if(stroke != null){
            data = stroke.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(final PolygonSymbolizer polygonSymbolizer, Object data) {
        visitGeomName(polygonSymbolizer, data);

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
            visit(offset, (Collection<String>) data);
        }
        final Stroke stroke = polygonSymbolizer.getStroke();
        if(stroke != null){
            data = stroke.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(final TextSymbolizer textSymbolizer, Object data) {
        visitGeomName(textSymbolizer, data);

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
            visit(label, (Collection<String>) data);
        }
        final LabelPlacement place = textSymbolizer.getLabelPlacement();
        if(place != null){
            data = place.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(final RasterSymbolizer rasterSymbolizer, Object data) {
        visitGeomName(rasterSymbolizer, data);

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
            visit(opa, (Collection<String>) data);
        }
        final ShadedRelief shade = rasterSymbolizer.getShadedRelief();
        if(shade != null){
            data = shade.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(final ExtensionSymbolizer extension, Object data) {
        visitGeomName(extension, data);

        final Map<String,Expression> exps = extension.getParameters();
        if(exps != null){
            for(Expression exp : exps.values()){
                visit(exp, (Collection<String>) data);
            }
        }
        return data;
    }

    @Override
    public Object visit(final Description description, final Object data) {
        return data;
    }

    @Override
    public Object visit(final Displacement displacement, Object data) {
        final Expression x = displacement.getDisplacementX();
        if(x != null){
            visit(x, (Collection<String>) data);
        }
        final Expression y = displacement.getDisplacementY();
        if(y != null){
            visit(y, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final Fill fill, Object data) {
        final Expression color = fill.getColor();
        if(color != null){
            visit(color, (Collection<String>) data);
        }
        final GraphicFill gf = fill.getGraphicFill();
        if(gf != null){
            data = gf.accept(this, data);
        }
        final Expression opa = fill.getOpacity();
        if(opa != null){
            visit(opa, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final Font font, Object data) {
        final List<Expression> families = font.getFamily();
        if(families != null){
            for(Expression family : families){
                visit(family, (Collection<String>) data);
            }
        }

        final Expression size = font.getSize();
        if(size != null){
            visit(size, (Collection<String>) data);
        }
        final Expression style = font.getStyle();
        if(style != null){
            visit(style, (Collection<String>) data);
        }
        final Expression weight = font.getWeight();
        if(weight != null){
            visit(weight, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final Stroke stroke, Object data) {
        final Expression color = stroke.getColor();
        if(color != null){
            visit(color, (Collection<String>) data);
        }

        final Expression offset = stroke.getDashOffset();
        if(offset != null){
            visit(offset, (Collection<String>) data);
        }

        final GraphicFill gf = stroke.getGraphicFill();
        if(gf != null){
            data = visit(gf, data);
        }

        final GraphicStroke gs = stroke.getGraphicStroke();
        if(gs != null){
            data = visit(gs, data);
        }

        final Expression lc = stroke.getLineCap();
        if(lc != null){
            visit(lc, (Collection<String>) data);
        }

        final Expression lj = stroke.getLineJoin();
        if(lj != null){
            visit(lj, (Collection<String>) data);
        }

        final Expression opa = stroke.getOpacity();
        if(opa != null){
            visit(opa, (Collection<String>) data);
        }

        final Expression width = stroke.getWidth();
        if(width != null){
            visit(width, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final Graphic graphic, Object data) {
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
            visit(opa, (Collection<String>) data);
        }
        final Expression rot = graphic.getRotation();
        if(rot != null){
            visit(rot, (Collection<String>) data);
        }
        final Expression size = graphic.getSize();
        if(size != null){
            visit(size, (Collection<String>) data);
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
    public Object visit(final GraphicFill graphicFill, final Object data) {
        return visit((Graphic)graphicFill, data);
    }

    @Override
    public Object visit(final GraphicStroke graphicStroke, Object data) {
        data = visit((Graphic)graphicStroke, data);
        final Expression gap = graphicStroke.getGap();
        if(gap != null){
            visit(gap, (Collection<String>) data);
        }
        final Expression igap = graphicStroke.getInitialGap();
        if(igap != null){
            visit(igap, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final Mark mark, Object data) {
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
            visit(wkn, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final ExternalMark externalMark, final Object data) {
        return data;
    }

    @Override
    public Object visit(final ExternalGraphic externalGraphic, Object data) {
        final Collection<ColorReplacement> replaces = externalGraphic.getColorReplacements();
        if(replaces != null){
            for(ColorReplacement r : replaces){
                data = r.accept(this, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(final PointPlacement pointPlacement, Object data) {
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
            visit(rot, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final AnchorPoint anchorPoint, Object data) {
        final Expression x = anchorPoint.getAnchorPointX();
        if(x != null){
            visit(x, (Collection<String>) data);
        }

        final Expression y = anchorPoint.getAnchorPointY();
        if(y != null){
            visit(y, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final LinePlacement linePlacement, Object data) {
        final Expression gap = linePlacement.getGap();
        if(gap != null){
            visit(gap, (Collection<String>) data);
        }
        final Expression igap = linePlacement.getInitialGap();
        if(igap != null){
            visit(igap, (Collection<String>) data);
        }
        final Expression offset = linePlacement.getPerpendicularOffset();
        if(offset != null){
            visit(offset, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final GraphicLegend graphicLegend, final Object data) {
        return visit((Graphic)graphicLegend, data);
    }

    @Override
    public Object visit(final Halo halo, Object data) {
        final Fill fill = halo.getFill();
        if(fill != null){
            data = fill.accept(this, data);
        }
        final Expression radius = halo.getRadius();
        if(radius != null){
            visit(radius, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final ColorMap colorMap, Object data) {
        final Expression fct = colorMap.getFunction();
        if(fct != null){
            visit(fct, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final ColorReplacement colorReplacement, Object data) {
        final Expression fct = colorReplacement.getRecoding();
        if(fct != null){
            visit(fct, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final ContrastEnhancement contrastEnhancement, Object data) {
        final Expression gamma = contrastEnhancement.getGammaValue();
        if(gamma != null){
            visit(gamma, (Collection<String>) data);
        }
        return data;
    }

    @Override
    public Object visit(final ChannelSelection channelSelection, Object data) {
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
    public Object visit(final SelectedChannelType selectChannelType, Object data) {
        final ContrastEnhancement enc = selectChannelType.getContrastEnhancement();
        if(enc != null){
            data = enc.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(final ShadedRelief shadedRelief, Object data) {
        final Expression exp = shadedRelief.getReliefFactor();
        if(exp != null){
            visit(exp, (Collection<String>) data);
        }
        return data;
    }
}
