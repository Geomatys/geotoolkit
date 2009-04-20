/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2009, Geotools Project Managment Committee (PMC)
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
package org.geotoolkit.display2d.container;


import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display2d.style.GO2Utilities;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;

import org.opengis.filter.expression.Expression;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.AnchorPoint;
import org.opengis.style.Displacement;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.SemanticType;
import org.opengis.style.Stroke;
import org.opengis.style.Style;

/**
 * This is the general usecase of a renderer, this renderer is made to work
 * with MapContext objects, each MapContext describing MapLayers and related coverages or
 * features.ContextContainer2D
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract  class ContextContainer2D extends AbstractContainer2D{

    public static final Style DEFAULT_SELECTION_STYLE;

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
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(mark);
        final Expression opacity = GO2Utilities.FILTER_FACTORY.literal(1d);
        final Expression expSize = GO2Utilities.FILTER_FACTORY.literal(14);
        final Expression expRotation = GO2Utilities.FILTER_FACTORY.literal(0);
        final AnchorPoint anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        final Displacement disp = GO2Utilities.STYLE_FACTORY.displacement(0, 0);
        final Graphic graphic = GO2Utilities.STYLE_FACTORY.graphic(symbols, opacity, expSize, expRotation, anchor,disp);

        final LineSymbolizer lineSymbol = GO2Utilities.STYLE_FACTORY.lineSymbolizer(selectionStroke, null);
        final PolygonSymbolizer polygonSymbol = GO2Utilities.STYLE_FACTORY.polygonSymbolizer(selectionStroke, selectionFill, null);
        final PointSymbolizer pointSymbol = GO2Utilities.STYLE_FACTORY.pointSymbolizer(graphic, null);

        ftsline.rules().add(GO2Utilities.STYLE_FACTORY.rule(lineSymbol));
        ftspoint.rules().add(GO2Utilities.STYLE_FACTORY.rule(pointSymbol));
        ftspolygon.rules().add(GO2Utilities.STYLE_FACTORY.rule(polygonSymbol));

        style.featureTypeStyles().add(ftsline);
        style.featureTypeStyles().add(ftspoint);
        style.featureTypeStyles().add(ftspolygon);

        DEFAULT_SELECTION_STYLE = style;
    }


    /**
     * CreContextContainer2D with no particular hints.
     */
    protected ContextContainer2D(final ReferencedCanvas2D canvas){
        super(canvas);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Rectangle2D getGraphicsEnvelope2D() {
        CoordinateReferenceSystem crs = getCanvas().getObjectiveCRS();
        try {
            ReferencedEnvelope env = getContext().getBounds();

            if( CRS.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(),crs) ){
                GeneralEnvelope genv = new GeneralEnvelope(env);
                return genv.toRectangle2D();
            }else{
                GeneralEnvelope genv = (GeneralEnvelope) CRS.transform(env, crs);
                return genv.toRectangle2D();
            }

        } catch (IOException ex) {
            Logger.getLogger(ContextContainer2D.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformException ex) {
            Logger.getLogger(ContextContainer2D.class.getName()).log(Level.SEVERE, null, ex);
        }

        return XRectangle2D.INFINITY;
    }

    /**
     * Returns an envelope that completly encloses all {@linkplain ReferencedGraphic#getEnvelope
     * graphic envelopes} managed by this canvas. Note that there is no guarantee that the returned
     * envelope is the smallest bounding box that encloses the canvas, only that the canvas lies
     * entirely within the indicated envelope.
     * <p>
     * This envelope is different from
     * {@link org.geotools.display.canvas.map.DefaultMapState#getEnvelope}, since the later returns
     * an envelope that encloses only the <em>visible</em> canvas area and is scale-dependent. This
     * {@code ReferencedCanvas.getEnvelope()} method is scale-independent. Both envelopes are equal
     * if the scale is choosen in such a way that all graphics fit exactly in the canvas visible
     * area.
     *
     * @return The envelope for this canvas in terms of {@linkplain #getObjectiveCRS objective CRS}.
     *
     * @see org.geotools.display.canvas.map.DefaultMapState#getEnvelope
     * @see ReferencedCanvas2D#getEnvelope2D
     */
    @Override
    public GeneralEnvelope getGraphicsEnvelope(){
        CoordinateReferenceSystem crs = getCanvas().getObjectiveCRS();
        try {
            ReferencedEnvelope env = getContext().getBounds();

            if( CRS.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(),crs) ){
                return new GeneralEnvelope(env);
            }else{
                return (GeneralEnvelope) CRS.transform(env, crs);
            }

        } catch (IOException ex) {
            Logger.getLogger(ContextContainer2D.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformException ex) {
            Logger.getLogger(ContextContainer2D.class.getName()).log(Level.SEVERE, null, ex);
        }

        GeneralEnvelope genv = new GeneralEnvelope(crs);
        genv.setToInfinite();

        return genv;
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void updateObjectiveCRS(final CoordinateReferenceSystem crs)
            throws TransformException{
    }

    /**
     * Set the mapcontext to render.
     * this will remove all previous graphics builded with the context.
     * <b>Caution</b> this should not remove graphics unrelated to the context.
     *
     * @param context : Mapcontext to render
     */
    public abstract void setContext(MapContext context);

    /**
     * Returns the currently renderered map context
     *
     * @return Mapcontext or null
     */
    public abstract MapContext getContext();

}
