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

package org.geotoolkit.gui.swing.navigator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.measure.unit.NonSI;
import javax.swing.SwingConstants;
import org.geotoolkit.display.axis.Graduation;
import org.geotoolkit.display.axis.NumberGraduation;
import org.geotoolkit.display.axis.TickIterator;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DoubleRenderer implements NavigatorRenderer<Double>{

    @Override
    public int getGraduationHeight() {
        return 20;
    }

    @Override
    public void render(NavigatorModel<Double> model, Graphics2D g, Rectangle area) {
        g = (Graphics2D) g.create();
        g.setClip(area);
//        g.setColor(Color.LIGHT_GRAY);
//        g.fill(area);

        g.setColor(Color.BLACK);

        final int extent;
        final int orientation = model.getOrientation();
        final boolean horizontal = orientation == SwingConstants.NORTH || orientation == SwingConstants.SOUTH;
        final boolean flipText = orientation == SwingConstants.NORTH || orientation == SwingConstants.WEST;

        
        if(horizontal){
            extent = area.width;
        }else{
            extent = area.height;
        }


        final RenderingHints tickHint = new RenderingHints(null);
        tickHint.put(Graduation.VISUAL_AXIS_LENGTH, extent);
        tickHint.put(Graduation.VISUAL_TICK_SPACING, 200);

        double start = model.getValueAt(0);
        double end = model.getValueAt(extent);

        final NumberGraduation graduationX = new NumberGraduation(null);
        graduationX.setRange(start, end, NonSI.PIXEL);
        TickIterator tickIte = graduationX.getTickIterator(tickHint, null);

        if(orientation == SwingConstants.NORTH){

        }else if(orientation == SwingConstants.SOUTH){

        }else if(orientation == SwingConstants.WEST){
            while(!tickIte.isDone()){
                tickIte.next();
                final String label = tickIte.currentLabel();
                final int d = (int)tickIte.currentPosition();

                g.drawLine(0, d, getGraduationHeight(), d);
                g.drawString(label, 2, d-2);

            }

        }else if(orientation == SwingConstants.EAST){

        }


//        while(!tickIte.isDone()){
//            tickIte.next();
//            final String label = tickIte.currentLabel();
//            final double d = tickIte.currentPosition();
//
//            g.drawLine( (int)d, -getGraduationHeight(), (int)d, getGraduationHeight());
//            g.drawString(label, (int)d+2, -getGraduationHeight());
//
////            final ArrayList<Coordinate> lineCoords = new ArrayList<Coordinate>();
////            final double maxY = gridBounds.getMaximum(1);
////            for(double k= gridBounds.getMinimum(1); k<maxY; k+=gridResolution[1]){
////                lineCoords.add(new Coordinate(d, k));
////            }
////            lineCoords.add(new Coordinate(d, maxY));
////
////            Geometry ls = fact.createLineString(lineCoords.toArray(new Coordinate[lineCoords.size()]));
////            ls = JTS.transform(ls, gridToObj);
////
////            if(ls == null) continue;
////
////            final Geometry geom = ls.intersection(bounds);
////            final DefaultProjectedGeometry pg = new DefaultProjectedGeometry(geom);
////            pg.setObjToDisplay(objToDisp);
////
////            final LinearLabelDescriptor desc;
////            if(tickIte.isMajorTick()){
////                desc = new DefaultLinearLabelDescriptor(
////                    label, template.getMainLabelFont(), template.getMainLabelPaint(),
////                    template.getMainHaloWidth(), template.getMainHaloPaint(),
////                    0, 10, 3,
////                    false, false, false,
////                    pg);
////            }else{
////                desc = new DefaultLinearLabelDescriptor(
////                    label, template.getLabelFont(), template.getLabelPaint(),
////                    template.getHaloWidth(), template.getHaloPaint(),
////                    0, 10, 3,
////                    false, false, false,
////                    pg);
////            }
////            layer.labels().add(desc);
////
////            if(tickIte.isMajorTick()){
////                g.setPaint(template.getMainLinePaint());
////                g.setStroke(template.getMainLineStroke());
////            }else{
////                g.setPaint(template.getLinePaint());
////                g.setStroke(template.getLineStroke());
////            }
////
////            g.draw(pg.getDisplayShape());
//        }





//        for(int i=0; i<extent; i+=10){
//            final int d = model.getValueAt(i).intValue();
//
//            g.drawLine(d, 0, d, getGraduationHeight());
//            g.drawString(String.valueOf(d), d+2, getGraduationHeight());
//
//        }

        g.dispose();
    }

}
