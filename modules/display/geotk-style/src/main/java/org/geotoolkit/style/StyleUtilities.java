/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010 Geomatys
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

package org.geotoolkit.style;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.geotoolkit.lang.Static;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.MutableUserLayer;

import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.sld.Layer;
import org.opengis.sld.StyledLayerDescriptor;
import org.opengis.sld.UserLayer;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.Stroke;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

/**
 * Convenient methods to transform a style in tree.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class StyleUtilities extends Static {

    private static final MutableStyleFactory SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                                            new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    private static final MutableSLDFactory SLDF = new DefaultSLDFactory();

    private StyleUtilities(){}

    public static MutableStyledLayerDescriptor copy(final StyledLayerDescriptor sld){
        final MutableStyledLayerDescriptor copy = SLDF.createSLD();
        copy.setName(sld.getName());
        copy.setVersion(sld.getVersion());
        copy.setDescription(sld.getDescription());
        
        for(Layer layer : sld.layers()){
            if(layer instanceof UserLayer){
                copy.layers().add(copy((UserLayer)layer));
            }else{
                //copy.layers().add(layer);
            }
        }
        
        return copy;
    }
    
    public static MutableUserLayer copy(final UserLayer layer){
        final MutableUserLayer copy = SLDF.createUserLayer();
        copy.setName(layer.getName());
        //copy.setConstraints(layer.getConstraints());
        copy.setDescription(layer.getDescription());
        copy.setSource(layer.getSource());
        
        for(Style style : layer.styles()){
            copy.styles().add(copy(style));
        }
        
        return copy;
    }
    
    public static MutableStyle copy(final Style style){
        return copy(style,1d);
    }

    public static MutableStyle copy(final Style style, final double opacity){
        final MutableStyle copy = SF.style();
        copy.setDefault(style.isDefault());
        copy.setDefaultSpecification(style.getDefaultSpecification());
        copy.setDescription(style.getDescription());
        copy.setName(style.getName());

        for(FeatureTypeStyle fts : style.featureTypeStyles()){
            copy.featureTypeStyles().add(copy(fts,opacity));
        }

        return copy;
    }

    public static MutableFeatureTypeStyle copy(final FeatureTypeStyle fts){
        return copy(fts,1d);
    }

    public static MutableFeatureTypeStyle copy(final FeatureTypeStyle fts, final double opacity){
        final MutableFeatureTypeStyle copy = SF.featureTypeStyle();
        copy.semanticTypeIdentifiers().addAll(fts.semanticTypeIdentifiers());
        copy.setDescription(fts.getDescription());
        copy.setFeatureInstanceIDs(fts.getFeatureInstanceIDs());
        copy.setName(fts.getName());
        copy.setOnlineResource(fts.getOnlineResource());

        for(Rule r : fts.rules()){
            copy.rules().add(copy(r,opacity));
        }

        return copy;
    }

    public static MutableRule copy(final Rule rule){
        return copy(rule,1d);
    }

    public static MutableRule copy(final Rule rule, final double opacity){
        final MutableRule copy = SF.rule();
        copy.setDescription(rule.getDescription());
        copy.setElseFilter(rule.isElseFilter());
        copy.setFilter(rule.getFilter());
        copy.setLegendGraphic(rule.getLegend());
        copy.setMaxScaleDenominator(rule.getMaxScaleDenominator());
        copy.setMinScaleDenominator(rule.getMinScaleDenominator());
        copy.setName(rule.getName());
        copy.setOnlineResource(rule.getOnlineResource());

        for(Symbolizer symbol : rule.symbolizers()){
            copy.symbolizers().add(copy(symbol,opacity));
        }
        return copy;
    }

    public static Symbolizer copy(final Symbolizer symbol, final double opacity){
        if(opacity == 1){
            //no need to modify the symbol
            return symbol;
        }

        if(symbol instanceof PointSymbolizer){
            final PointSymbolizer ps = (PointSymbolizer) symbol;
            final Graphic gra = ps.getGraphic();
            return SF.pointSymbolizer(
                    ps.getName(),
                    ps.getGeometryPropertyName(),
                    ps.getDescription(),
                    ps.getUnitOfMeasure(),
                    SF.graphic(
                        gra.graphicalSymbols(),
                        correctOpacity(gra.getOpacity(),opacity),
                        gra.getSize(),
                        gra.getRotation(),
                        gra.getAnchorPoint(),
                        gra.getDisplacement())
                    );
        }else if(symbol instanceof LineSymbolizer){
            final LineSymbolizer ps = (LineSymbolizer) symbol;
            return SF.lineSymbolizer(
                    ps.getName(),
                    ps.getGeometryPropertyName(),
                    ps.getDescription(),
                    ps.getUnitOfMeasure(),
                    correctOpacity(ps.getStroke(), opacity),
                    ps.getPerpendicularOffset());
        }else if(symbol instanceof PolygonSymbolizer){
            final PolygonSymbolizer ps = (PolygonSymbolizer) symbol;
            final Stroke str = ps.getStroke();
            return SF.polygonSymbolizer(
                    ps.getName(),
                    ps.getGeometryPropertyName(),
                    ps.getDescription(),
                    ps.getUnitOfMeasure(),
                    correctOpacity(ps.getStroke(), opacity),
                    correctOpacity(ps.getFill(), opacity),
                    ps.getDisplacement(),
                    ps.getPerpendicularOffset());
        }else if(symbol instanceof TextSymbolizer){
            final TextSymbolizer ps = (TextSymbolizer) symbol;
            return SF.textSymbolizer(
                    ps.getName(),
                    ps.getGeometryPropertyName(),
                    ps.getDescription(),
                    ps.getUnitOfMeasure(),
                    ps.getLabel(),
                    ps.getFont(),
                    ps.getLabelPlacement(),
                    SF.halo(
                        correctOpacity(ps.getHalo().getFill(), opacity),
                        ps.getHalo().getRadius()),
                    correctOpacity(ps.getFill(), opacity));
        }else if(symbol instanceof RasterSymbolizer){
            final RasterSymbolizer ps = (RasterSymbolizer) symbol;
            return SF.rasterSymbolizer(
                    ps.getName(),
                    ps.getGeometryPropertyName(),
                    ps.getDescription(),
                    ps.getUnitOfMeasure(),
                    correctOpacity(ps.getOpacity(),opacity),
                    ps.getChannelSelection(),
                    ps.getOverlapBehavior(),
                    ps.getColorMap(),
                    ps.getContrastEnhancement(),
                    ps.getShadedRelief(),
                    ps.getImageOutline());
        }else{
            return symbol;
        }

    }

    private static Fill correctOpacity(final Fill fl, final double opacity){
        return SF.fill(
            fl.getGraphicFill(),
            fl.getColor(),
            correctOpacity(fl.getOpacity(),opacity));
    }

    private static Stroke correctOpacity(final Stroke str, final double opacity){
        return SF.stroke(
            str.getGraphicFill(),
            str.getColor(),
            correctOpacity(str.getOpacity(),opacity),
            str.getWidth(),
            str.getLineJoin(),
            str.getLineCap(),
            str.getDashArray(),
            str.getDashOffset());
    }

    private static Expression correctOpacity(final Expression exp, final double opacity){
        return FF.multiply(exp, FF.literal(opacity));
    }

    public static MutableTreeNode asTreeNode(final MutableStyle element){
        return new StyleNode(element);
    }

    public static MutableTreeNode asTreeNode(final MutableFeatureTypeStyle element){
        return new FTSNode(element);
    }

    public static MutableTreeNode asTreeNode(final MutableRule element){
        return new RuleNode(element);
    }

    public static MutableTreeNode asTreeNode(final Symbolizer element){
        return new SymbolNode(element);
    }

    private static class StyleNode extends DefaultMutableTreeNode{

        private MutableStyle element;

        public StyleNode(final MutableStyle element){
            super(element);
            this.element = element;
            //todo must add listener mecanism
            for(MutableFeatureTypeStyle fts : element.featureTypeStyles()){
                add(asTreeNode(fts));
            }

        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Style : ").append(element.getClass().getSimpleName()).append(' ');
            sb.append(element.getName()).append(element.getDescription());
            return sb.toString();
        }

    }

    private static class FTSNode extends DefaultMutableTreeNode{

        private MutableFeatureTypeStyle element;

        public FTSNode(final MutableFeatureTypeStyle element){
            super(element);
            this.element = element;
            //todo must add listener mecanism
            for(MutableRule rule : element.rules()){
                add(asTreeNode(rule));
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("FTS : ").append(element.getClass().getSimpleName()).append(' ');
            sb.append(element.getName()).append(element.getDescription());
            return sb.toString();
        }

    }

    private static class RuleNode extends DefaultMutableTreeNode{

        private MutableRule element;

        public RuleNode(final MutableRule element){
            super(element);
            this.element = element;
            //todo must add listener mecanism
            for(Symbolizer symbol : element.symbolizers()){
                add(asTreeNode(symbol));
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Rule : ").append(element.getClass().getSimpleName()).append(' ');
            sb.append(element.getName()).append(element.getDescription());
            return sb.toString();
        }

    }

    private static class SymbolNode extends DefaultMutableTreeNode{

        private Symbolizer element;

        public SymbolNode(final Symbolizer element){
            super(element);
            this.element = element;
            //todo must add listener mecanism
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Symbol : ").append(element.getClass().getSimpleName()).append(' ');
            sb.append(element.getName()).append(element.getDescription());
            return sb.toString();
        }

    }

}
