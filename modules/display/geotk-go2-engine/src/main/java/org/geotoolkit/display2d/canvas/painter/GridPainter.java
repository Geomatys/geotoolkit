/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
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
package org.geotoolkit.display2d.canvas.painter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.DefaultProjectedGeometry;
import org.geotoolkit.display2d.style.labeling.DefaultLabelLayer;
import org.geotoolkit.display2d.style.labeling.DefaultLinearLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.LabelLayer;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;
import org.geotoolkit.display2d.style.labeling.LinearLabelDescriptor;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class GridPainter implements BackgroundPainter{

    private final NumberFormat format = NumberFormat.getNumberInstance();

    private final Paint paint;
    private final Stroke stroke;
    private final Color textPaint;
    private final Color haloPaint;
    private final float haloWidth;
    private final Font textFont;

    public GridPainter(Paint paint, Stroke stroke){
        this.paint = paint;
        this.stroke = stroke;

        textPaint = Color.BLACK;
        haloPaint = Color.WHITE;
        haloWidth = 3f;
        textFont = new Font("Serif", Font.PLAIN,12);
    }

//    @Override
//    public void paint(RenderingContext2D context) {
//        final Graphics2D g = context.getGraphics();
//
//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        final FontRenderContext fontContext = g.getFontRenderContext();
//
//        context.switchToDisplayCRS();
//
//
//
//        Envelope env = CRS.getEnvelope(context.getObjectiveCRS());
//
//        int step = 10;
//
//        AffineTransform trs = context.getObjectiveToDisplay();
//
//        for(double i = env.getMinimum(1); i<=env.getMaximum(1); i +=step){
//            Point2D p1 = trs.transform(new Point2D.Double(env.getMinimum(0), i), null);
//            Point2D p2 = trs.transform(new Point2D.Double(env.getMaximum(0), i), null);
//
//            g.setPaint(paint);
//            g.setStroke(stroke);
//            g.drawLine((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY());
//
//            final String str = String.valueOf(i);
//            final float x = (float)p1.getX();
//            final float y = (float)p1.getY();
//
//
//            final GlyphVector glyph = g.getFont().createGlyphVector(fontContext, str);
//            final Shape shape = glyph.getOutline(x,y);
//            g.setPaint(haloColor);
//            g.setStroke(new BasicStroke(haloWidth*2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
//            g.draw(shape);
//            g.setPaint(TextPaint);
//            g.drawString(str, x,y);
//        }
//
//        for(double i = env.getMinimum(0); i<=env.getMaximum(0); i +=step){
//            Point2D p1 = trs.transform(new Point2D.Double(i, env.getMinimum(1)), null);
//            Point2D p2 = trs.transform(new Point2D.Double(i, env.getMaximum(1)), null);
//
//            g.setPaint(paint);
//            g.setStroke(stroke);
//            g.drawLine((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY());
//
//            final String str = String.valueOf(i);
//            final float x = (float)p1.getX();
//            final float y = (float)p1.getY();
//
//
//            final GlyphVector glyph = g.getFont().createGlyphVector(fontContext, str);
//            final Shape shape = glyph.getOutline(x,y);
//            g.setPaint(haloColor);
//            g.setStroke(new BasicStroke(haloWidth*2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
//            g.draw(shape);
//            g.setPaint(TextPaint);
//            g.drawString(str, x,y);
//        }
//
//    }

    @Override
    public void paint(RenderingContext2D context) {
        final CoordinateReferenceSystem gridCRS = context.getObjectiveCRS();

        final Graphics2D g = context.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final Shape shp = context.getCanvasObjectiveShape();
        final List<Coordinate> coords = new ArrayList<Coordinate>();
        final PathIterator ite = shp.getPathIterator(new AffineTransform());

        final double[] vals = new double[3];
        while(!ite.isDone()){
            ite.currentSegment(vals);
            coords.add( new Coordinate(vals[0],vals[1]));
            ite.next();
        }

        final double[] res = context.getResolution();
        final GeometryFactory fact = new GeometryFactory();
        final LinearRing ring = fact.createLinearRing(coords.toArray(new Coordinate[coords.size()]));
        final Polygon bounds = fact.createPolygon(ring, new LinearRing[0]);


        int step = 10;

        final LabelRenderer renderer = context.getLabelRenderer(true);
        final LabelLayer layer = new DefaultLabelLayer(false, true);

        try{
            final MathTransform trs = CRS.findMathTransform(gridCRS, context.getObjectiveCRS(), true);
            final Envelope gridCRSenv = CRS.getEnvelope(gridCRS);
            final Envelope gridInObjective = CRS.transform(trs, gridCRSenv);
            final MathTransform objToDisp = context.getObjectiveToDisplay();

            for(double i = gridInObjective.getMinimum(1); i<=gridInObjective.getMaximum(1); i +=step){

                final LineString ls = fact.createLineString(new Coordinate[]{
                    new Coordinate(gridInObjective.getMinimum(0), i),
                    new Coordinate(gridInObjective.getMaximum(0), i),
                });

                final Geometry geom = ls.intersection(bounds);
                final DefaultProjectedGeometry pg = new DefaultProjectedGeometry(geom);
                pg.setObjToDisplay(objToDisp);

                final String str = String.valueOf(i);
                final LinearLabelDescriptor desc = new DefaultLinearLabelDescriptor(
                        str, textFont, textPaint,
                        haloWidth, haloPaint,
                        0, 10, 0,
                        false, false, false,
                        pg);
                layer.labels().add(desc);

                g.setPaint(paint);
                g.setStroke(stroke);
                g.draw(pg.getDisplayShape());
            }

            for(double i = gridInObjective.getMinimum(0); i<=gridInObjective.getMaximum(0); i +=step){

                final LineString ls = fact.createLineString(new Coordinate[]{
                    new Coordinate(i, gridInObjective.getMinimum(1)),
                    new Coordinate(i, gridInObjective.getMaximum(1)),
                });

                final Geometry geom = ls.intersection(bounds);
                final DefaultProjectedGeometry pg = new DefaultProjectedGeometry(geom);
                pg.setObjToDisplay(objToDisp);

                final String str = String.valueOf(i);
                final LinearLabelDescriptor desc = new DefaultLinearLabelDescriptor(
                        str, textFont, textPaint,
                        haloWidth, haloPaint,
                        0, 15, 0,
                        false, false, false,
                        pg);
                layer.labels().add(desc);

                g.setPaint(paint);
                g.setStroke(stroke);
                g.draw(pg.getDisplayShape());
            }


        }catch(Exception ex){
            ex.printStackTrace();
        }

        renderer.append(layer);


//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        final FontRenderContext fontContext = g.getFontRenderContext();
//
//        context.switchToDisplayCRS();
//
//
//
//        Envelope env = CRS.getEnvelope(context.getObjectiveCRS());
//
//
//
//        AffineTransform trs = context.getObjectiveToDisplay();
//
//        for(double i = env.getMinimum(1); i<=env.getMaximum(1); i +=step){
//            Point2D p1 = trs.transform(new Point2D.Double(env.getMinimum(0), i), null);
//            Point2D p2 = trs.transform(new Point2D.Double(env.getMaximum(0), i), null);
//
//            g.setPaint(paint);
//            g.setStroke(stroke);
//            g.drawLine((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY());
//
//            final String str = String.valueOf(i);
//            final float x = (float)p1.getX();
//            final float y = (float)p1.getY();
//
//
//            final GlyphVector glyph = g.getFont().createGlyphVector(fontContext, str);
//            final Shape shape = glyph.getOutline(x,y);
//            g.setPaint(haloColor);
//            g.setStroke(new BasicStroke(haloWidth*2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
//            g.draw(shape);
//            g.setPaint(TextPaint);
//            g.drawString(str, x,y);
//        }
//
//        for(double i = env.getMinimum(0); i<=env.getMaximum(0); i +=step){
//            Point2D p1 = trs.transform(new Point2D.Double(i, env.getMinimum(1)), null);
//            Point2D p2 = trs.transform(new Point2D.Double(i, env.getMaximum(1)), null);
//
//            g.setPaint(paint);
//            g.setStroke(stroke);
//            g.drawLine((int)p1.getX(),(int)p1.getY(),(int)p2.getX(),(int)p2.getY());
//
//            final String str = String.valueOf(i);
//            final float x = (float)p1.getX();
//            final float y = (float)p1.getY();
//
//
//            final GlyphVector glyph = g.getFont().createGlyphVector(fontContext, str);
//            final Shape shape = glyph.getOutline(x,y);
//            g.setPaint(haloColor);
//            g.setStroke(new BasicStroke(haloWidth*2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
//            g.draw(shape);
//            g.setPaint(TextPaint);
//            g.drawString(str, x,y);
//        }

    }


}
