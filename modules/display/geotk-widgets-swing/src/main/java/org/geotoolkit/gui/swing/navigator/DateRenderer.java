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
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
        return 100;
    }

    @Override
    public void render(JNavigator model, Graphics2D g, Rectangle area) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//        height = area.height;
//        width = area.width;
//        limit1 = (int) (height * topHeight);
//        limit2 = height - (int) (height * lastHeight);
//        zoom = model.getScale();
//        this.model = model;
//
//        final Double d = model.getValueAt(width/2);
//        centralDate = new Date(d.longValue());
//        centralGregorian.setTime(centralDate);
//        System.out.println("--> " +centralDate);
//
//        System.out.println(model.getValueAt(0));
//        System.out.println(model.getValueAt(width));
//
//        paintYears(g2, height, width, limit1, limit2);
//        paintMonths(g2, height, width, limit1, limit2);
//        paintDays(g2, height, width, limit1, limit2);

    }


    private void paintYears(Graphics2D g2, int height, int width, int limit1, int limit2) {
        
        g2.setColor(Clast);
        g2.fillRect(0, limit2, width, height - limit2);

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(Cseparator);
        g2.drawLine(0, limit2, width, limit2);

        //draw a gradiant
        GradientPaint mask;
        mask = new GradientPaint(
                0, 0, new Color(1.0f, 1.0f, 1.0f, 1f),
                0, height, new Color(1.0f, 1.0f, 1.0f, 0.0f));
        g2.setPaint(mask);
        g2.fillRect(0, limit2, width, height - limit2);


        Calendar calendar = new GregorianCalendar();
        calendar.setTime(centralDate);


//        final Calendar from = new GregorianCalendar();
//        from.setTimeInMillis(model.getValueAt(0).longValue());
//        final Calendar to = new GregorianCalendar();
//        to.setTimeInMillis(model.getValueAt(width).longValue());
//
//        //draw the years
//        g2.setColor(Ccenter);
//        g2.setFont(new Font("Serif", Font.BOLD, 12));
//
//        for(int year=from.get(Calendar.YEAR),max=to.get(Calendar.YEAR); year<=max; year++){
//            calendar.set(year, 0, 1, 1, 1);
//            int x = (int) model.getPosition(Double.valueOf(calendar.getTimeInMillis()));
//            if(x<0)x=0;
//
////            if (x >= 0) {
//                g2.setColor(Ccenter);
//                g2.drawLine(x, limit2 + 1, x, height);
//                paintString(g2, String.valueOf(calendar.get(Calendar.YEAR)), Ccenter, x + 5, height - 5);
////            } else if ((pxYear + x) > 40) {
////                paintString(g2, String.valueOf(calendar.get(Calendar.YEAR)), Ccenter, 5, height - 5);
////            }
//        }
//
//
////        int nbYears = (int) (((float) width / 2f) / pxYear) + 1;
////
////        //draw the years
////        g2.setColor(Ccenter);
////        g2.setFont(new Font("Serif", Font.BOLD, 12));
////        calendar.set(centralGregorian.get(Calendar.YEAR), 0, 1, 1, 1);
////        calendar.add(Calendar.YEAR, -nbYears);
////
////        for (int y = -nbYears; y <= nbYears; y++, calendar.add(Calendar.YEAR, 1)) {
////            int x = (int) model.getPosition(Double.valueOf(calendar.getTimeInMillis()));
////
////            if (x >= 0) {
////                g2.setColor(Ccenter);
////                g2.drawLine(x, limit2 + 1, x, height);
////                paintString(g2, String.valueOf(calendar.get(Calendar.YEAR)), Ccenter, x + 5, height - 5);
////            } else if ((pxYear + x) > 40) {
////                paintString(g2, String.valueOf(calendar.get(Calendar.YEAR)), Ccenter, 5, height - 5);
////            }
////        }
    }

    private void paintMonths(Graphics2D g2, int height, int width, int limit1, int limit2) {
        float pxMonth = ((float) width) / ((float)zoom) / 12f;

        g2.setColor(Ccenter);
        g2.fillRect(0, limit1, width, limit2 - limit1);


        //draw a gradiant
        GradientPaint mask;
        mask = new GradientPaint(
                0, limit1, new Color(1.0f, 1.0f, 1.0f, 0.1f),
                0, limit2, new Color(Ccenter.getRed(), Ccenter.getGreen(), Ccenter.getBlue(), 255));
        g2.setPaint(mask);
        g2.fillRect(0, limit1, width, limit2 - limit1);

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(Cseparator);
        g2.drawLine(0, limit1, width, limit1);


        Calendar calendar = new GregorianCalendar();
        calendar.setTime(centralDate);

//        if (pxMonth > 28) {
//            int nbMonth = (int) (((float) width / 2f) / pxMonth) + 1;
//
//            //draw the months
//            g2.setColor(Ctop);
//            g2.setFont(new Font("Serif", Font.PLAIN, 12));
//            calendar.set(centralGregorian.get(Calendar.YEAR), centralGregorian.get(Calendar.MONTH) + 1, 1, 1, 1);
//            calendar.add(Calendar.MONTH, -nbMonth);
//
//            for (int y = -nbMonth; y <= nbMonth; y++, calendar.add(Calendar.MONTH, 1)) {
//                int x = (int) model.getPosition(Double.valueOf(calendar.getTimeInMillis()));
//
//                if (x >= 0) {
//                    g2.setColor(Ctop);
//                    g2.drawLine(x, limit1 + 1, x, limit2 - 1);
//
//                    if (pxMonth < 60) {
//                        paintString(g2, calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()), Ctop, x + 5, limit2 - 5);
//                    } else if (pxMonth >= 60) {
//                        paintString(g2, calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()), Ctop, x + 5, limit2 - 5);
//                    }
//                } else if ((pxMonth + x) >= 60) {
//                    paintString(g2, calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()), Ctop, 5, limit2 - 5);
//                } else if ((pxMonth + x) > 30) {
//                    paintString(g2, calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()), Ctop, 5, limit2 - 5);
//                }
//            }
//        }
    }

    private void paintDays(Graphics2D g2, int height, int width, int limit1, int limit2) {
        float pxDay = ((float) width) / ((float)zoom) / 365f;

        g2.setColor(Ctop);
        g2.fillRect(0, 0, width, limit1);


        //draw a gradiant
        GradientPaint mask;
        mask = new GradientPaint(
                0, 0, new Color(1.0f, 1.0f, 1.0f, 0.8f),
                0, height, new Color(1.0f, 1.0f, 1.0f, 0.0f));
        g2.setPaint(mask);
        g2.fillRect(0, 0, width, limit1);


        Calendar calendar = new GregorianCalendar();
        calendar.setTime(centralDate);

//        if (pxDay > 22) {
//            int nbDays = (int) (((float) width / 2f) / pxDay) + 1;
//
//            //draw the days
//            g2.setColor(Color.WHITE);
//            g2.setFont(new Font("Serif", Font.PLAIN, 12));
//            calendar.set(centralGregorian.get(Calendar.YEAR), centralGregorian.get(Calendar.MONTH), centralGregorian.get(Calendar.DAY_OF_MONTH), 1, 1);
//            calendar.add(Calendar.DAY_OF_MONTH, -nbDays);
//
//            for (int y = -nbDays; y <= nbDays; y++, calendar.add(Calendar.DAY_OF_MONTH, 1)) {
//                int x = (int) model.getPosition(Double.valueOf(calendar.getTimeInMillis()));
//
//                if (x >= 0) {
//                    g2.setColor(Color.WHITE);
//                    g2.drawLine(x, 0, x, limit1 - 1);
//
//                    if (pxDay < 60) {
//                        paintString(g2, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), Color.WHITE, x + 5, limit1 - 5);
//                    } else if (pxDay >= 60) {
//                        paintString(g2, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), Color.WHITE, x + 5, limit1 - 5);
//                    }
//                } else if ((pxDay + x) >= 60) {
//                    paintString(g2, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), Color.WHITE, 5, limit1 - 5);
//                } else if ((pxDay + x) > 30) {
//                    paintString(g2, String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), Color.WHITE, 5, limit1 - 5);
//                }
//            }
//        }


    }

    private void paintString(Graphics2D g2, String txt, Color color, int x, int y) {
        g2.setColor(color.darker().darker());
        g2.drawString(txt, x + 1, y + 1);
        g2.setColor(color.brighter());
        g2.drawString(txt, x, y);
    }


}
