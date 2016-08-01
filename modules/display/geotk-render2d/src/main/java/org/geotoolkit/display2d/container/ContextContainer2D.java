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
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.container.DefaultGraphicContainer;
import org.geotoolkit.display.primitive.SceneNode;
import org.geotoolkit.display.container.MapContextContainer;
import org.geotoolkit.display2d.container.statefull.RootSceneNode;
import org.geotoolkit.display2d.container.statefull.StatefullMapItemJ2D;
import org.geotoolkit.display2d.container.stateless.StatelessMapItemJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.opengis.display.canvas.CanvasState;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
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
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.util.Utilities;

/**
 * This is the general use case of a renderer, this renderer is made to work
 * with MapContext objects, each MapContext describing MapLayers and related coverages or
 * features.ContextContainer2D
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ContextContainer2D extends DefaultGraphicContainer implements MapContextContainer {

    public static final String CONTEXT_PROPERTY = "context";

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.display2d.container");

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
    private final boolean statefull;
    private MapContext context = null;

    /**
     * CreContextContainer2D with no particular hints.
     */
    public ContextContainer2D(final J2DCanvas canvas, final boolean statefull){
        super(canvas, statefull ? new RootSceneNode(canvas) : new SceneNode(canvas));
        this.statefull = statefull;
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
     * {@inheritDoc }
     */
    public Rectangle2D getGraphicsEnvelope2D() {
        CoordinateReferenceSystem crs = getCanvas().getObjectiveCRS();
        try {
            final MapContext context = getContext();
            if(context != null){
                Envelope env = context.getBounds(true);
                if( Utilities.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(),crs) ){
                    org.geotoolkit.geometry.GeneralEnvelope genv = new org.geotoolkit.geometry.GeneralEnvelope(env);
                    return genv.toRectangle2D();
                }else{
                    org.geotoolkit.geometry.GeneralEnvelope genv = new org.geotoolkit.geometry.GeneralEnvelope(Envelopes.transform(env, crs));
                    return genv.toRectangle2D();
                }
            }

        } catch (IOException | TransformException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        return XRectangle2D.INFINITY;
    }

    /**
     * Returns an envelope that completely encloses all {@linkplain ReferencedGraphic#getEnvelope()
     * graphic envelopes} managed by this canvas. Note that there is no guarantee that the returned
     * envelope is the smallest bounding box that encloses the canvas, only that the canvas lies
     * entirely within the indicated envelope.
     * <p>
     * This envelope is different from
     * {@link CanvasState#getCenter() }, since the later returns
     * an envelope that encloses only the <em>visible</em> canvas area and is scale-dependent. This
     * {@code ReferencedCanvas.getEnvelope()} method is scale-independent. Both envelopes are equal
     * if the scale is chosen in such a way that all graphics fit exactly in the canvas visible
     * area.
     *
     * @return The envelope for this canvas in terms of {@linkplain AbstractCanvas#getObjectiveCRS() objective CRS}.
     */
    public GeneralEnvelope getGraphicsEnvelope(){
        CoordinateReferenceSystem crs = getCanvas().getObjectiveCRS();
        try {
            crs = CRSUtilities.getCRS2D(crs);
            final MapContext context = getContext();
            if(context != null){
                Envelope env = context.getBounds(true);

                if(env != null){
                    if ( Utilities.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(),crs) ) {
                        return new GeneralEnvelope(env);
                    } else {
                        return (GeneralEnvelope) Envelopes.transform(env, crs);
                    }
                }
            }

        } catch (IOException | TransformException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        GeneralEnvelope genv = new GeneralEnvelope(crs);
        genv.setToInfinite();

        return genv;
    }

    /**
     * Set the mapcontext to render.
     * this will remove all previous graphics builded with the context.
     * <b>Caution</b> this should not remove graphics unrelated to the context.
     *
     * @param context : MapContext to render
     */
    @Override
    public void setContext(MapContext context){

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

        final MapContext oldcontext = this.context;
        this.context = context;

        if(this.context != null){
            //create the new graphics
            if(statefull){
                contextGraphic = new StatefullMapItemJ2D(getCanvas(), context, true);
            }else{
                contextGraphic = new StatelessMapItemJ2D(getCanvas(), context, true);
            }

            getRoot().getChildren().add(contextGraphic);
        }

        firePropertyChange(CONTEXT_PROPERTY, oldcontext, this.context);
    }

    /**
     * Returns the currently renderered map context
     *
     * @return MapContext or null
     */
    @Override
    public MapContext getContext(){
        return context;
    }

}
