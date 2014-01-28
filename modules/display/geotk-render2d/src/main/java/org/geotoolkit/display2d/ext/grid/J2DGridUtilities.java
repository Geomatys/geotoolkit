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

package org.geotoolkit.display2d.ext.grid;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.display.axis.Graduation;
import org.geotoolkit.display.axis.NumberGraduation;
import org.geotoolkit.display.axis.TickIterator;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.style.labeling.DefaultLabelLayer;
import org.geotoolkit.display2d.style.labeling.DefaultLinearLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.LabelLayer;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;
import org.geotoolkit.display2d.style.labeling.LinearLabelDescriptor;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * Utility class to render grid on J2DCanvas.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class J2DGridUtilities {

    private static final double MIN = 1e-6;

    private J2DGridUtilities() {
    }

    public static void paint(final RenderingContext2D context, final GridTemplate template){

        CoordinateReferenceSystem gridCRS = template.getCRS();
        //use context crs if gridcrs is not defined
        if(gridCRS == null) gridCRS = context.getObjectiveCRS();

        final Graphics2D g = context.getGraphics();
        context.switchToDisplayCRS();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);

        final Shape shp = context.getCanvasObjectiveShape();
        final List<Coordinate> coords = new ArrayList<Coordinate>();
        final PathIterator ite = shp.getPathIterator(new AffineTransform());

        final double[] vals = new double[3];
        while(!ite.isDone()){
            ite.currentSegment(vals);
            coords.add( new Coordinate(vals[0],vals[1]));
            ite.next();
        }

        final GeometryFactory fact = new GeometryFactory();
        final LinearRing ring = fact.createLinearRing(coords.toArray(new Coordinate[coords.size()]));
        final Polygon bounds = fact.createPolygon(ring, new LinearRing[0]);

        final LabelRenderer renderer = context.getLabelRenderer(true);
        final LabelLayer layer = new DefaultLabelLayer(false, true);

        final RenderingHints tickHint = new RenderingHints(null);
        tickHint.put(Graduation.VISUAL_AXIS_LENGTH, context.getCanvasDisplayBounds().width);
        tickHint.put(Graduation.VISUAL_TICK_SPACING, 200);

        //number of point by line
        final int nbPoint = 20;

        final CoordinateReferenceSystem objectiveCRS = context.getObjectiveCRS2D();
        try{
            //reduce grid bounds to validity area
            Envelope gridBounds = CRS.transform(context.getCanvasObjectiveBounds2D(), gridCRS);

            if(Math.abs(gridBounds.getSpan(0)) < MIN || Math.abs(gridBounds.getSpan(1)) < MIN ){
                return;
            }


            Envelope validity = CRS.getEnvelope(gridCRS);
            if(validity != null){
                GeneralEnvelope env = new GeneralEnvelope(gridBounds);
                env.intersect(validity);
                gridBounds = env;
            }


            final MathTransform gridToObj = CRS.findMathTransform(gridCRS, objectiveCRS, true);
            final MathTransform objToGrid = gridToObj.inverse();

            //grid on X axis ---------------------------------------------------

            final NumberGraduation graduationX = new NumberGraduation(null);
            graduationX.setRange(gridBounds.getMinimum(0), gridBounds.getMaximum(0),
                    gridBounds.getCoordinateReferenceSystem().getCoordinateSystem().getAxis(0).getUnit());

            TickIterator tickIte = graduationX.getTickIterator(tickHint, null);

            while(!tickIte.isDone()){
                tickIte.next();
                final String label = tickIte.currentLabel();
                final double d = tickIte.currentPosition();
                if(d>gridBounds.getMaximum(0))continue;

                final ArrayList<Coordinate> lineCoords = new ArrayList<Coordinate>();
                final double maxY = gridBounds.getMaximum(1);
                final double step = gridBounds.getSpan(1)/nbPoint;
                for(double k=Math.nextUp(gridBounds.getMinimum(1)); k<maxY; k+=step){
                    lineCoords.add(new Coordinate(d, k));
                }
                lineCoords.add(new Coordinate(d, Math.nextAfter(maxY,Double.NEGATIVE_INFINITY)));

                Geometry geom = fact.createLineString(lineCoords.toArray(new Coordinate[lineCoords.size()]));
                if(geom == null) continue;

                final StatelessContextParams params = new StatelessContextParams(null, null);
                final ProjectedGeometry pg = new ProjectedGeometry(params);
                params.update(context);
                pg.setDataGeometry(geom, gridCRS);

                //draw line
                if(tickIte.isMajorTick()){
                    g.setPaint(template.getMainLinePaint());
                    g.setStroke(template.getMainLineStroke());
                }else{
                    g.setPaint(template.getLinePaint());
                    g.setStroke(template.getLineStroke());
                }
                for(Shape ds : pg.getDisplayShape()) g.draw(ds);
                

                //clip geometry to avoid text outside visible area
                geom = JTS.transform(geom, gridToObj);
                if(geom == null) continue;
                geom = geom.intersection(bounds);
                pg.setDataGeometry(geom, objectiveCRS);

                //draw text
                final LinearLabelDescriptor desc;
                if(tickIte.isMajorTick()){
                    desc = new DefaultLinearLabelDescriptor(
                        label, template.getMainLabelFont(), template.getMainLabelPaint(),
                        template.getMainHaloWidth(), template.getMainHaloPaint(),
                        0, 10, 3,
                        false, false, false,
                        pg);
                }else{
                    desc = new DefaultLinearLabelDescriptor(
                        label, template.getLabelFont(), template.getLabelPaint(),
                        template.getHaloWidth(), template.getHaloPaint(),
                        0, 10, 3,
                        false, false, false,
                        pg);
                }
                layer.labels().add(desc);


            }

            //grid on Y axis ---------------------------------------------------

            final NumberGraduation graduationY = new NumberGraduation(null);
            graduationY.setRange(gridBounds.getMinimum(1), gridBounds.getMaximum(1),
                    gridBounds.getCoordinateReferenceSystem().getCoordinateSystem().getAxis(1).getUnit());

            tickIte = graduationY.getTickIterator(tickHint, null);

            while(!tickIte.isDone()){
                tickIte.next();
                final String label = tickIte.currentLabel();
                final double d = tickIte.currentPosition();
                if(d>gridBounds.getMaximum(1))continue;

                final ArrayList<Coordinate> lineCoords = new ArrayList<Coordinate>();
                final double maxX = gridBounds.getMaximum(0);
                final double step = gridBounds.getSpan(0)/nbPoint;
                for(double k= Math.nextUp(gridBounds.getMinimum(0)); k<maxX; k+=step){
                    lineCoords.add(new Coordinate(k, d));
                }
                lineCoords.add(new Coordinate(Math.nextAfter(maxX,Double.NEGATIVE_INFINITY), d));

                Geometry geom = fact.createLineString(lineCoords.toArray(new Coordinate[lineCoords.size()]));
                final StatelessContextParams params = new StatelessContextParams(null, null);
                final ProjectedGeometry pg = new ProjectedGeometry(params);
                params.update(context);
                pg.setDataGeometry(geom, gridCRS);

                //draw line
                if(tickIte.isMajorTick()){
                    g.setPaint(template.getMainLinePaint());
                    g.setStroke(template.getMainLineStroke());
                }else{
                    g.setPaint(template.getLinePaint());
                    g.setStroke(template.getLineStroke());
                }
                for(Shape ds : pg.getDisplayShape()) g.draw(ds);

                //clip geometry to avoid text outside visible area
                geom = JTS.transform(geom, gridToObj);
                if(geom == null) continue;
                geom = geom.intersection(bounds);
                pg.setDataGeometry(geom, objectiveCRS);

                //draw text
                final LinearLabelDescriptor desc;
                if(tickIte.isMajorTick()){
                    desc = new DefaultLinearLabelDescriptor(
                        label, template.getMainLabelFont(), template.getMainLabelPaint(),
                        template.getMainHaloWidth(), template.getMainHaloPaint(),
                        0, 10, 3,
                        false, false, false,
                        pg);
                }else{
                    desc = new DefaultLinearLabelDescriptor(
                        label, template.getLabelFont(), template.getLabelPaint(),
                        template.getHaloWidth(), template.getHaloPaint(),
                        0, 10, 3,
                        false, false, false,
                        pg);
                }

                layer.labels().add(desc);
            }


        }catch(Exception ex){
            ex.printStackTrace();
        }

        renderer.portrayImmidiately(layer);
    }


}
