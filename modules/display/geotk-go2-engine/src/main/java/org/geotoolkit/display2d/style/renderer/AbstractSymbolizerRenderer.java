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
package org.geotoolkit.display2d.style.renderer;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display.shape.TransformedShape;
import org.geotoolkit.renderer.style.WellKnownMarkFactory;
import org.geotoolkit.style.StyleConstants;

import org.opengis.filter.expression.Expression;
import org.opengis.style.Fill;
import org.opengis.style.Mark;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;

/**
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractSymbolizerRenderer<S extends Symbolizer, C extends CachedSymbolizer<S>> implements SymbolizerRenderer<S, C>{

    protected static final Shape LINE;
    protected static final Shape POLYGON;
    protected static final Point2D POINT;
    protected static final Shape TEXT;

    static{
        //LINE -----------------------------------------------------------------
        final float x2Points[] = {0,    0.4f,   0.6f,   1f};
        final float y2Points[] = {0.2f, 0.6f,   0.4f,   0.8f};
        final GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x2Points.length);

        polyline.moveTo (x2Points[0], y2Points[0]);
        for (int index = 1; index < x2Points.length; index++) {
                 polyline.lineTo(x2Points[index], y2Points[index]);
        }
        LINE = polyline;

        //POLYGON --------------------------------------------------------------
        final float x1Points[] = {0.2f,     0.4f,   1f,     1f,     0.2f};
        final float y1Points[] = {1f,       0.4f,   0.2f,   1f,     1f};
        final GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x1Points.length);

        polygon.moveTo(x1Points[0], y1Points[0]);
        for (int index = 1; index < x1Points.length; index++) {
                polygon.lineTo(x1Points[index], y1Points[index]);
        }
        POLYGON = polygon;

        //POINT ----------------------------------------------------------------
        POINT = new Point2D.Float(0.5f,0.5f);

        //TEXT -----------------------------------------------------------------
        final float xtPoints[] = {0.1f,     0.3f,   0.2f,   0.2f};
        final float ytPoints[] = {0.6f,     0.6f,   0.6f,   0.9f};
        final GeneralPath textLine = new GeneralPath(GeneralPath.WIND_EVEN_ODD, xtPoints.length);

        textLine.moveTo (xtPoints[0], ytPoints[0]);
        for (int index = 1; index < xtPoints.length; index++) {
                 textLine.lineTo(xtPoints[index], ytPoints[index]);
        }
        TEXT = textLine;
    }

    protected void renderGraphic(final Mark mark, final float size, final Graphics2D target){
        final Expression wkn = mark.getWellKnownName();

        final Shape shape;

        if(StyleConstants.MARK_CIRCLE.equals(wkn)){
            shape = WellKnownMarkFactory.CIRCLE;
        }else if(StyleConstants.MARK_CROSS.equals(wkn)){
            shape = WellKnownMarkFactory.CROSS;
        }else if(StyleConstants.MARK_SQUARE.equals(wkn)){
            shape = WellKnownMarkFactory.SQUARE;
        }else if(StyleConstants.MARK_STAR.equals(wkn)){
            shape = WellKnownMarkFactory.STAR;
        }else if(StyleConstants.MARK_TRIANGLE.equals(wkn)){
            shape = WellKnownMarkFactory.TRIANGLE;
        }else if(StyleConstants.MARK_X.equals(wkn)){
            shape = WellKnownMarkFactory.X;
        }else{
            shape = null;
        }

        if(shape != null){
            final TransformedShape trs = new TransformedShape();
            trs.setOriginalShape(shape);
            trs.scale(size, size);
            renderFill(trs, mark.getFill(), target);
            renderStroke(trs, mark.getStroke(), SI.METER, target);
        }

    }
    
    protected void renderStroke(final Shape shape, final Stroke stroke, final Unit uom, final Graphics2D target){
        final Expression expColor = stroke.getColor();
        final Expression expOpa = stroke.getOpacity();
        final Expression expCap = stroke.getLineCap();
        final Expression expJoin = stroke.getLineJoin();
        final Expression expWidth = stroke.getWidth();

        Paint color;
        final float width;
        final float opacity;
        final int cap;
        final int join;
        final float[] dashes;

        if(GO2Utilities.isStatic(expColor)){
            color = expColor.evaluate(null, Color.class);
        }else{
            color = Color.RED;
        }

        if(color == null){
            color = Color.RED;
        }

        if(GO2Utilities.isStatic(expOpa)){
            opacity = expOpa.evaluate(null, Number.class).floatValue();
        }else{
            opacity = 0.6f;
        }

        if(GO2Utilities.isStatic(expCap)){
            if(StyleConstants.STROKE_CAP_ROUND.equals(expCap)){
                cap = BasicStroke.CAP_ROUND;
            }else if(StyleConstants.STROKE_CAP_SQUARE.equals(expCap)){
                cap = BasicStroke.CAP_SQUARE;
            }else {
                cap = BasicStroke.CAP_BUTT;
            }
        }else{
            cap = BasicStroke.CAP_BUTT;
        }

        if(GO2Utilities.isStatic(expJoin)){
            if(StyleConstants.STROKE_JOIN_ROUND.equals(expJoin)){
                join = BasicStroke.JOIN_ROUND;
            }else if(StyleConstants.STROKE_JOIN_MITRE.equals(expJoin)){
                join = BasicStroke.JOIN_MITER;
            }else {
                join = BasicStroke.JOIN_BEVEL;
            }
        }else{
            join = BasicStroke.JOIN_BEVEL;
        }

        if(NonSI.PIXEL.equals(uom) && GO2Utilities.isStatic(expWidth)){
            width = expWidth.evaluate(null, Number.class).floatValue();

            if(stroke.getDashArray() != null && stroke.getDashArray().length >0){
                dashes = stroke.getDashArray();
            }else{
                dashes = null;
            }

        }else{
            width = 1f;
            dashes = null;
        }

        target.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        target.setPaint(color);

        if(dashes != null){
            target.setStroke(new BasicStroke(width, cap, join,1,dashes,0));
        }else{
            target.setStroke(new BasicStroke(width, cap, join));
        }

        target.draw(shape);
    }

    protected void renderFill(final Shape shape, final Fill fill, final Graphics2D target){
        final Expression expColor = fill.getColor();
        final Expression expOpa = fill.getOpacity();

        Paint color;
        final float opacity;

        if(GO2Utilities.isStatic(expColor)){
            color = expColor.evaluate(null, Color.class);
        }else{
            color = Color.RED;
        }

        if(color == null){
            color = Color.RED;
        }

        if(GO2Utilities.isStatic(expOpa)){
            opacity = expOpa.evaluate(null, Number.class).floatValue();
        }else{
            opacity = 0.6f;
        }

        target.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        target.setPaint(color);

        target.fill(shape);
    }

}
