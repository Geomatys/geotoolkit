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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.geotoolkit.temporal.object.TemporalConstants;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DateRenderer implements NavigatorRenderer{

    private Date centralDate = new Date();
    private GregorianCalendar centralGregorian = new GregorianCalendar();
    private final float topHeight = 0.60f;
    private final float lastHeight = 0.20f;
    private final Color Cseparator = Color.WHITE;
    private final Color Cbase = Color.GRAY;
    private final Color Ctop;
    private final Color Ccenter;
    private final Color Clast;
    private int height = 0;
    private int width = 0;
    private int limit1 = 0;
    private int limit2 = 0;
    private double zoom = 1f;
    private NavigatorModel model = null;

    public DateRenderer() {
        Ctop = Cbase.brighter();
        Ccenter = Cbase;
        Clast = Cbase.darker();
    }

    @Override
    public int getGraduationHeight() {
        return 20;
    }

    @Override
    public void render(final JNavigator nav, final Graphics2D g2, final Rectangle area) {
        this.model = nav.getModel();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        height = area.height;
        width = area.width;
        limit1 = (int) (height * topHeight);
        limit2 = height - (int) (height * lastHeight);
        zoom = model.getScale();

        final Double d = model.getDimensionValueAt(width/2);
        centralDate = new Date(d.longValue());
        centralGregorian.setTime(centralDate);
//        System.out.println("--> " +centralDate);
//
//        System.out.println(model.getDimensionValueAt(0));
//        System.out.println(model.getDimensionValueAt(width));

        paintYears(g2, height, width, limit1, limit2);
        paintMonths(g2, height, width, limit1, limit2);
        paintDays(g2, height, width, limit1, limit2);

    }

    private void paintYears(final Graphics2D g2, final int height, final int width, final int limit1, final int limit2) {
        
        g2.setColor(Clast);
        g2.fillRect(0, limit2, width, height - limit2);

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(Cseparator);
        g2.drawLine(0, limit2, width, limit2);

        //draw a gradiant-------------------------------------------------------
        final GradientPaint mask = new GradientPaint(
                0, 0, new Color(1.0f, 1.0f, 1.0f, 1f),
                0, height, new Color(1.0f, 1.0f, 1.0f, 0.0f));
        g2.setPaint(mask);
        g2.fillRect(0, limit2, width, height - limit2);


        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(centralDate);
        final Calendar from = new GregorianCalendar();
        from.setTimeInMillis((long) model.getDimensionValueAt(0));
        final Calendar to = new GregorianCalendar();
        to.setTimeInMillis((long) model.getDimensionValueAt(width));


        //draw the years--------------------------------------------------------
        final FontMetrics fm = g2.getFontMetrics(g2.getFont());
        g2.setColor(Ccenter);
        g2.setFont(new Font("Serif", Font.BOLD, 12));


        for(int year=from.get(Calendar.YEAR),max=to.get(Calendar.YEAR); year<=max; year++){
            calendar.set(year, 0, 1, 1, 1);
            calendar.set(Calendar.MILLISECOND, 1);
            
            final String strYear = String.valueOf(calendar.get(Calendar.YEAR));
            int x = (int) model.getGraphicValueAt(calendar.getTimeInMillis());
            if(x<0)x=0;

            g2.setColor(Ccenter);
            g2.drawLine(x, limit2 + 1, x, height);

            //see if we have enough space before the next year
            calendar.set(year+1, 0, 1, 1, 1);
            final int nextX = (int) model.getGraphicValueAt(calendar.getTimeInMillis());
            final double strWidth = fm.getStringBounds(strYear, g2).getWidth();
            if(x+strWidth+5 > nextX){
                //not enough space
                continue;
            }

            paintString(g2, strYear, Ccenter, x + 5, height - 5);
        }

    }

    private void paintMonths(final Graphics2D g2, final int height, final int width, final int limit1, final int limit2) {
        
        g2.setColor(Ccenter);
        g2.fillRect(0, limit1, width, limit2 - limit1);

        //draw a gradiant-------------------------------------------------------
        final GradientPaint mask = new GradientPaint(
                0, limit1, new Color(1.0f, 1.0f, 1.0f, 0.1f),
                0, limit2, new Color(Ccenter.getRed(), Ccenter.getGreen(), Ccenter.getBlue(), 255));
        g2.setPaint(mask);
        g2.fillRect(0, limit1, width, limit2 - limit1);

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(Cseparator);
        g2.drawLine(0, limit1, width, limit1);


        final Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis((long) model.getDimensionValueAt(0));

        final double pxMonth = model.getScale() * TemporalConstants.MONTH_MS;
        final int nbMonth = (int) (width / pxMonth);


        if (pxMonth > 3) {

            //draw the months
            g2.setColor(Ctop);
            g2.setFont(new Font("Serif", Font.PLAIN, 12));
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, 1, 1, 1, 1);
            calendar.set(Calendar.MILLISECOND, 1);
            calendar.add(Calendar.MONTH, -1);

            for (int y=0; y<=nbMonth+1; y++, calendar.add(Calendar.MONTH, 1)) {
                int x = (int) model.getGraphicValueAt(calendar.getTimeInMillis());

                g2.setColor(Ctop);
                g2.drawLine(x, limit1 + 1, x, limit2 - 1);

                if (x >= 0) {
                    if (pxMonth < 60 && pxMonth > 28) {
                        paintString(g2, calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()), Ctop, x + 5, limit2 - 5);
                    } else if (pxMonth >= 60) {
                        paintString(g2, calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()), Ctop, x + 5, limit2 - 5);
                    }
                } else if ((pxMonth + x) >= 60) {
                    paintString(g2, calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()), Ctop, 5, limit2 - 5);
                } else if ((pxMonth + x) > 30) {
                    paintString(g2, calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()), Ctop, 5, limit2 - 5);
                }
            }
        }
    }

    private void paintDays(final Graphics2D g2, final int height, final int width, final int limit1, final int limit2) {
        
        g2.setColor(Ctop);
        g2.fillRect(0, 0, width, limit1);


        //draw a gradiant-------------------------------------------------------
        final GradientPaint mask = new GradientPaint(
                0, 0, new Color(1.0f, 1.0f, 1.0f, 0.8f),
                0, height, new Color(1.0f, 1.0f, 1.0f, 0.0f));
        g2.setPaint(mask);
        g2.fillRect(0, 0, width, limit1);


        final Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis((long) model.getDimensionValueAt(0));

        final double pxDay = model.getScale() * TemporalConstants.DAY_MS;
        final int nbDay = (int) (width / pxDay);

        if (pxDay > 3) {

            //draw the days
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Serif", Font.PLAIN, 12));
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH)+1, 1, 1, 1);
            calendar.set(Calendar.MILLISECOND, 1);
            calendar.add(Calendar.MONTH, -1);
            calendar.add(Calendar.DAY_OF_YEAR, -1);

            for (int y=0; y<=nbDay+1; y++, calendar.add(Calendar.DAY_OF_YEAR, 1)) {
                int x = (int) model.getGraphicValueAt(calendar.getTimeInMillis());

                g2.setColor(Color.WHITE);
                g2.drawLine(x, 0, x, limit1 - 1);

                if (x >= 0) {
                    if (pxDay < 60 && pxDay > 22) {
                        paintString(g2, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), Color.WHITE, x + 5, limit1 - 5);
                    } else if (pxDay >= 60) {
                        paintString(g2, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), Color.WHITE, x + 5, limit1 - 5);
                    }
                } else if ((pxDay + x) >= 60) {
                    paintString(g2, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), Color.WHITE, 5, limit1 - 5);
                } else if ((pxDay + x) > 30) {
                    paintString(g2, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), Color.WHITE, 5, limit1 - 5);
                }


            }
        }
        
    }

    private void paintString(final Graphics2D g2, final String txt, final Color color, final int x, final int y) {
        g2.setColor(color.darker().darker());
        g2.drawString(txt, x + 1, y + 1);
        g2.setColor(color.brighter());
        g2.drawString(txt, x, y);
    }


}
