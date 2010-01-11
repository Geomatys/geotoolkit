/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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


import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.container.AbstractContainer2D;
import org.geotoolkit.display.primitive.ReferencedGraphic;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.logging.Logging;

import org.opengis.display.canvas.CanvasState;
import org.opengis.feature.type.FeatureType;
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

/**
 * This is the general usecase of a renderer, this renderer is made to work
 * with MapContext objects, each MapContext describing MapLayers and related coverages or
 * features.ContextContainer2D
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract  class ContextContainer2D extends AbstractContainer2D{

    private static final Logger LOGGER = Logging.getLogger(ContextContainer2D.class);

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
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
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

//    public static Style getBestSelectionStyle(FeatureType type){
//        if(type == null){
//            return DEFAULT_SELECTION_STYLE;
//        }
//
//        Class clazz = type.getGeometryDescriptor().getType().getBinding();
//
//        if(Point.class.isAssignableFrom(clazz) || MultiPoint.class.isAssignableFrom(clazz)){
//            return GO2Utilities.STYLE_FACTORY.style(DEFAULT_POLYGON_SELECTION_SYMBOL);
//        }else if(LineString.class.isAssignableFrom(clazz) || MultiLineString.class.isAssignableFrom(clazz)){
//            return GO2Utilities.STYLE_FACTORY.style(DEFAULT_POLYGON_SELECTION_SYMBOL);
//        }else if(Polygon.class.isAssignableFrom(clazz) || MultiPolygon.class.isAssignableFrom(clazz)){
//            return GO2Utilities.STYLE_FACTORY.style(DEFAULT_POLYGON_SELECTION_SYMBOL);
//        }
//
//        return DEFAULT_SELECTION_STYLE;
//    }


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
            final MapContext context = getContext();
            if(context != null){
                Envelope env = context.getBounds();
                if( CRS.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(),crs) ){
                    GeneralEnvelope genv = new GeneralEnvelope(env);
                    return genv.toRectangle2D();
                }else{
                    GeneralEnvelope genv = (GeneralEnvelope) CRS.transform(env, crs);
                    return genv.toRectangle2D();
                }
            }

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (TransformException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return XRectangle2D.INFINITY;
    }

    /**
     * Returns an envelope that completly encloses all {@linkplain ReferencedGraphic#getEnvelope()
     * graphic envelopes} managed by this canvas. Note that there is no guarantee that the returned
     * envelope is the smallest bounding box that encloses the canvas, only that the canvas lies
     * entirely within the indicated envelope.
     * <p>
     * This envelope is different from
     * {@link CanvasState#getCenter() }, since the later returns
     * an envelope that encloses only the <em>visible</em> canvas area and is scale-dependent. This
     * {@code ReferencedCanvas.getEnvelope()} method is scale-independent. Both envelopes are equal
     * if the scale is choosen in such a way that all graphics fit exactly in the canvas visible
     * area.
     *
     * @return The envelope for this canvas in terms of {@linkplain AbstractCanvas#getObjectiveCRS() objective CRS}.
     */
    @Override
    public GeneralEnvelope getGraphicsEnvelope(){
        CoordinateReferenceSystem crs = getCanvas().getObjectiveCRS();
        try {

            final MapContext context = getContext();
            if(context != null){
                Envelope env = context.getBounds();

                if(env != null){
                if( CRS.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(),crs) ){
                    return new GeneralEnvelope(env);
                }else{
                    return (GeneralEnvelope) CRS.transform(env, crs);
                }
                }
            }

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (TransformException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
