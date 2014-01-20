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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import static javax.swing.SwingConstants.EAST;
import static javax.swing.SwingConstants.NORTH;
import static javax.swing.SwingConstants.SOUTH;
import static javax.swing.SwingConstants.WEST;

/**
 * Model renderer displaying dates for JNavigator component.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DateRenderer implements NavigatorRenderer{

    private final List<TimeSubdivision> subdivisions = new ArrayList<>();

    private NavigatorModel model = null;

    public DateRenderer() {
        subdivisions.add(new TimeSubdivision.Year());
        subdivisions.add(new TimeSubdivision.Month());
        subdivisions.add(new TimeSubdivision.Day());
        subdivisions.add(new TimeSubdivision.Hour());
        subdivisions.add(new TimeSubdivision.Quarter());
        subdivisions.add(new TimeSubdivision.Minute());
    }

    @Override
    public int getGraduationHeight() {
        return 42;
    }

    @Override
    public void render(final JNavigator nav, final Graphics2D g2, final Rectangle area) {

        final int extent;
        final int orientation = nav.getOrientation();
        final boolean horizontal = orientation == NORTH || orientation == SOUTH;
        final boolean flipText = orientation == NORTH || orientation == WEST;
        
        //draw the background gradient -----------------------------------------

        int sx = 0;
        int sy = 0;
        int ex = 0;
        int ey = 0;
        switch(orientation){
            case NORTH : sx=0;sy=0;ex=0;ey=area.height;break;
            case SOUTH : sx=0;sy=area.height;ex=0;ey=0;break;
            case EAST : sx=area.width;sy=0;ex=0;ey=0;break;
            case WEST : sx=0;sy=0;ex=area.width;ey=0;break;
        }
        
        
        Color lineColor = Color.GRAY;
        Color textColor = Color.GRAY;

        this.model = nav.getModel();
        final Font monospaced = new Font("Monospaced", Font.PLAIN, 12);
        final FontMetrics fm = g2.getFontMetrics(monospaced);
        final int fontHeight = fm.getHeight();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(monospaced);

        if(horizontal){
            final int compactBandHeight = fm.getHeight() +10;
            final int height = area.height;
            final int width = area.width;

            //draw the two gradient
            final GradientPaint mask1 = new GradientPaint(
                    0, 0, Color.WHITE,
                    0, height-compactBandHeight, Color.LIGHT_GRAY);
            g2.setPaint(mask1);
            g2.fillRect(0, 0, width, height-compactBandHeight);
            final GradientPaint mask2 = new GradientPaint(
                    0, height-compactBandHeight, Color.GRAY,
                    0, height, Color.LIGHT_GRAY);
            g2.setPaint(mask2);
            g2.fillRect(0, height-compactBandHeight, width, compactBandHeight);


            final long beginInterval = (long) model.getDimensionValueAt(0);
            final long endInterval = (long) model.getDimensionValueAt(width);


            final List<TimeSubdivision> compact = new ArrayList<TimeSubdivision>();


            for(int i=0;i<subdivisions.size();i++){
                final TimeSubdivision sub = subdivisions.get(i);
                final double textsize = sub.getTextLength(fm);
                final double scale = sub.getUnitLength();
                final double stepWidth = scale * model.getScale();

                final boolean showLine = stepWidth > 15 ;
                final boolean showText = stepWidth > textsize ;

                if(!showLine){
                    //to narrow to show lines, skip this division and all followings
                    break;
                }

                if(stepWidth > width/3 && i<subdivisions.size()-1){
                    //add to compact group if larg enough and not last subdivision
                    if(!sub.isIntermediate()){
                        compact.add(sub);
                    }
                    continue;
                }

                final long[] steps = sub.getSteps(beginInterval, endInterval);
                for(long step : steps){
                    final double x = model.getGraphicValueAt(step);

                    g2.setColor(lineColor);
                    g2.drawLine((int)x, 0, (int)x, height-compactBandHeight);

                    if(showText){
                        final String text = sub.getText((long)step);
                        g2.setColor(textColor);
                        g2.drawString(text, (float)x+3, height-compactBandHeight-fm.getMaxDescent());
                    }
                }

                //we have draw one type of subdivision, skip others
                break;
            }

            lineColor = Color.BLACK;
            g2.setColor(lineColor);
            g2.drawLine(0, height-compactBandHeight, width, height-compactBandHeight);

            //draw compacts
            textColor = Color.WHITE;
            int x = 5;
            for(int i=0;i<compact.size();i++){
                final TimeSubdivision sub = compact.get(i);

                //draw text
                g2.setColor(textColor);
                final String text = sub.getText(beginInterval)+sub.getUnitText()+" ";
                g2.drawString(text, x, height-fm.getMaxDescent()-3);
                x += fm.stringWidth(text);

                //last element, we draw possible other lines
                if(i==compact.size()-1){

                    final long[] steps = sub.getSteps(beginInterval, endInterval);
                    for(long step : steps){
                        final double lx = model.getGraphicValueAt(step);
                        final String lt = sub.getText(step);

                        g2.setColor(lineColor);
                        g2.drawLine((int)lx, height-compactBandHeight, (int)lx, height);

                        if(lx > x){
                            g2.setColor(textColor);
                            g2.drawString(lt, (float)lx+3, height-fm.getMaxDescent()-3);
                        }

                    }
                }

            }
        }else{
            final int compactBandWidth = fm.stringWidth("2000")+5;
            final int height = area.height;
            final int width = area.width;

            //draw the two gradient
            final GradientPaint mask1 = new GradientPaint(
                    width, 0, Color.WHITE,
                    compactBandWidth, 0, Color.LIGHT_GRAY);
            g2.setPaint(mask1);
            g2.fillRect(compactBandWidth, 0, width-compactBandWidth, height);
            final GradientPaint mask2 = new GradientPaint(
                    0, 0, Color.LIGHT_GRAY,
                    compactBandWidth, 0, Color.GRAY);
            g2.setPaint(mask2);
            g2.fillRect(0, 0, compactBandWidth, height);


            final long beginInterval = (long) model.getDimensionValueAt(0);
            final long endInterval = (long) model.getDimensionValueAt(height);

            final List<TimeSubdivision> compact = new ArrayList<>();

            for(int i=0;i<subdivisions.size();i++){
                final TimeSubdivision sub = subdivisions.get(i);
                final double scale = sub.getUnitLength();
                final double stepWidth = scale * model.getScale();

                final boolean showLine = stepWidth > 15 ;
                final boolean showText = stepWidth > fontHeight ;

                if(!showLine){
                    //to narrow to show lines, skip this division and all followings
                    break;
                }

                if(stepWidth > height/3 && i<subdivisions.size()-1){
                    //add to compact group if large enough and not last subdivision
                    if(!sub.isIntermediate()){
                        compact.add(sub);
                    }
                    continue;
                }

                final long[] steps = sub.getSteps(beginInterval, endInterval);
                for(long step : steps){
                    final double y = model.getGraphicValueAt(step);

                    g2.setColor(lineColor);
                    g2.drawLine(compactBandWidth, (int)y, width, (int)y);

                    if(showText){
                        final String text = sub.getText((long)step);
                        g2.setColor(textColor);
                        g2.drawString(text, compactBandWidth+3, (float)y+fm.getMaxAscent());
                    }
                }

                //we have draw one type of subdivision, skip others
                break;
            }

            lineColor = Color.BLACK;
            g2.setColor(lineColor);
            g2.drawLine(compactBandWidth, 0, compactBandWidth, height);

            //draw compacts
            textColor = Color.WHITE;
            int y = 5;
            for(int i=0;i<compact.size();i++){
                final TimeSubdivision sub = compact.get(i);

                //draw text
                g2.setColor(textColor);
                final String text = sub.getText(beginInterval)+sub.getUnitText();
                g2.drawString(text, 2, y+fm.getAscent());
                y += fontHeight;

                //last element, we draw possible other lines
                if(i==compact.size()-1){

                    final long[] steps = sub.getSteps(beginInterval, endInterval);
                    for(long step : steps){
                        final double ly = model.getGraphicValueAt(step);
                        final String lt = sub.getText(step);

                        g2.setColor(lineColor);
                        g2.drawLine(0,(int)ly, compactBandWidth, (int)ly);

                        if(ly > y){
                            g2.setColor(textColor);
                            g2.drawString(lt, 3, (float)ly+fm.getMaxAscent());
                        }

                    }
                }

            }
        }

    }

}
