/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2013, Geomatys
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
package org.geotoolkit.display2d.container;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.portrayal.MapLayers;
import org.geotoolkit.display.container.DefaultGraphicContainer;
import org.geotoolkit.display.container.MapContextContainer;
import org.geotoolkit.display.primitive.SceneNode;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.Expression;
import org.opengis.style.AnchorPoint;
import org.opengis.style.Displacement;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;
import org.opengis.style.SemanticType;
import org.opengis.style.Stroke;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;

/**
 * This is the general use case of a renderer, this renderer is made to work
 * with MapContext objects, each MapContext describing MapLayers and related coverages or
 * features.ContextContainer2D
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ContextContainer2D extends DefaultGraphicContainer implements MapContextContainer {

    public static final String CONTEXT_PROPERTY = "context";

    public static final Style DEFAULT_SELECTION_STYLE;
    public static final Symbolizer DEFAULT_LINE_SELECTION_SYMBOL;
    public static final Symbolizer DEFAULT_POINT_SELECTION_SYMBOL;
    public static final Symbolizer DEFAULT_POLYGON_SELECTION_SYMBOL;

    static{
        final MutableStyle style = GO2Utilities.STYLE_FACTORY.style();
        final MutableFeatureTypeStyle ftspoint = GO2Utilities.STYLE_FACTORY.featureTypeStyle();
        final MutableFeatureTypeStyle ftsline = GO2Utilities.STYLE_FACTORY.featureTypeStyle();
        final MutableFeatureTypeStyle ftspolygon = GO2Utilities.STYLE_FACTORY.featureTypeStyle();

        ftsline.semanticTypeIdentifiers().add(SemanticType.LINE);
        ftspoint.semanticTypeIdentifiers().add(SemanticType.POINT);
        ftspolygon.semanticTypeIdentifiers().add(SemanticType.POLYGON);

        final Expression selectionColor = GO2Utilities.STYLE_FACTORY.literal(Color.GREEN);
        final Stroke selectionStroke = GO2Utilities.STYLE_FACTORY.stroke(selectionColor, GO2Utilities.FILTER_FACTORY.literal(2));
        final Fill selectionFill = GO2Utilities.STYLE_FACTORY.fill(selectionColor, GO2Utilities.FILTER_FACTORY.literal(0.6f));
        final Expression wkn = StyleConstants.MARK_CIRCLE;
        final Mark mark = GO2Utilities.STYLE_FACTORY.mark(wkn, selectionFill, selectionStroke);
        final List<GraphicalSymbol> symbols = new ArrayList<>();
        symbols.add(mark);
        final Expression opacity = GO2Utilities.FILTER_FACTORY.literal(1d);
        final Expression expSize = GO2Utilities.FILTER_FACTORY.literal(14);
        final Expression expRotation = GO2Utilities.FILTER_FACTORY.literal(0);
        final AnchorPoint anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        final Displacement disp = GO2Utilities.STYLE_FACTORY.displacement(0, 0);
        final Graphic graphic = GO2Utilities.STYLE_FACTORY.graphic(symbols, opacity, expSize, expRotation, anchor,disp);

        DEFAULT_LINE_SELECTION_SYMBOL = GO2Utilities.STYLE_FACTORY.lineSymbolizer(selectionStroke, null);
        DEFAULT_POLYGON_SELECTION_SYMBOL = GO2Utilities.STYLE_FACTORY.polygonSymbolizer(selectionStroke, selectionFill, null);
        DEFAULT_POINT_SELECTION_SYMBOL = GO2Utilities.STYLE_FACTORY.pointSymbolizer(graphic, null);

        ftsline.rules().add(GO2Utilities.STYLE_FACTORY.rule(DEFAULT_LINE_SELECTION_SYMBOL));
        ftspoint.rules().add(GO2Utilities.STYLE_FACTORY.rule(DEFAULT_POINT_SELECTION_SYMBOL));
        ftspolygon.rules().add(GO2Utilities.STYLE_FACTORY.rule(DEFAULT_POLYGON_SELECTION_SYMBOL));

        style.featureTypeStyles().add(ftsline);
        style.featureTypeStyles().add(ftspoint);
        style.featureTypeStyles().add(ftspolygon);

        DEFAULT_SELECTION_STYLE = style;
    }


    private GraphicJ2D contextGraphic = null;
    private MapLayers context = null;

    /**
     * CreContextContainer2D with no particular hints.
     */
    public ContextContainer2D(final J2DCanvas canvas){
        super(canvas, new SceneNode(canvas));
    }

    @Override
    public J2DCanvas getCanvas() {
        return (J2DCanvas)super.getCanvas();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        final SceneNode root = getRoot();
        if(root!=null){
            root.dispose();
        }
    }

    /**
     * Set the mapcontext to render.
     * this will remove all previous graphics builded with the context.
     * <b>Caution</b> this should not remove graphics unrelated to the context.
     *
     * @param context : MapContext to render
     */
    @Override
    public void setContext(MapLayers context){

        if(this.context != null && context != null){
            if(this.context.equals(context)){
                //same context
                return;
            }
        }

        //dispose previous context graphic
        if(contextGraphic!=null){
            getRoot().getChildren().remove(contextGraphic);
            contextGraphic.dispose();
        }

        final MapLayers oldcontext = this.context;
        this.context = context;

        if(this.context != null){
            //create the new graphics
            contextGraphic = new MapItemJ2D(getCanvas(), context, true);
            getRoot().getChildren().add(contextGraphic);
        }

        firePropertyChange(CONTEXT_PROPERTY, oldcontext, this.context);
    }

    /**
     * Returns the currently rendered map context
     *
     * @return MapContext or null
     */
    @Override
    public MapLayers getContext(){
        return context;
    }

}
