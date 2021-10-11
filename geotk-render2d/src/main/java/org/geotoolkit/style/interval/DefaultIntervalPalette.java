/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2015 Geomatys
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

package org.geotoolkit.style.interval;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultIntervalPalette implements IntervalPalette{

    private final int[] ARGB;
    private final double[] fractions;

    public DefaultIntervalPalette(final Color[] colors) {
        this(null,colors);
    }

    public DefaultIntervalPalette(double[] fractions, final Color[] colors) {
        if(fractions == null){
            fractions = new double[colors.length];
            for(int i=0;i<colors.length;i++){
                fractions[i] = ((double)i)/ (colors.length-1);
            }
        }

        this.ARGB = new int[colors.length];

        for(int i=0;i<colors.length;i++){
            ARGB[i] = colors[i].getRGB();
        }

        this.fractions = fractions;
    }

    public int[] getARGB() {
        return ARGB;
    }

    @Override
    public List<Entry<Double, Color>> getSteps() {
        final List<Entry<Double, Color>> steps = new ArrayList<Entry<Double, Color>>();
        for(int i=0;i<fractions.length;i++){
            steps.add(new SimpleImmutableEntry<Double, Color>(fractions[i], new Color(ARGB[i])) );
        }
        return steps;
    }

    @Override
    public void render(final Graphics2D g, final Rectangle rectangle, boolean interpolate) {

        final float[] fractions = new float[ARGB.length];
        final Color[] colors = new Color[ARGB.length];

        for(int i=0;i<ARGB.length;i++){
            fractions[i] = (float)i/(ARGB.length-1);
            colors[i] = new Color(ARGB[i]);
        }

        if(interpolate){
            final MultipleGradientPaint.CycleMethod cycleMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
            final LinearGradientPaint paint = new LinearGradientPaint(
                new Point2D.Double(rectangle.getMinX(),rectangle.getMinY()),
                new Point2D.Double(rectangle.getMaxX(),rectangle.getMinY()),
                fractions,
                colors,
                cycleMethod
            );

            g.setPaint(paint);
            g.fill(rectangle);

            g.setColor(Color.WHITE);
            final Font font = new Font("Dialog", Font.BOLD, 13);
            final FontMetrics fm = g.getFontMetrics(font);
            final String text = NumberFormat.getNumberInstance().format(this.fractions[0])
                    +"..."+NumberFormat.getNumberInstance().format(this.fractions[this.fractions.length-1]);
            final Rectangle2D rect = fm.getStringBounds(text, g);
            g.drawString(text,
                    (float)( (rectangle.getWidth()-rect.getWidth())/2),
                    (float)(rectangle.getHeight() - (rectangle.getHeight()-rect.getHeight())/2) );
        }else{
            double step = rectangle.getWidth()/colors.length;
            double start = rectangle.getX();
            for(int i=0;i<colors.length;i++){
                g.setColor(colors[i]);
                g.fill(new Rectangle2D.Double(start, rectangle.getY(), step, rectangle.getHeight()));
                start += step;
            }
        }

    }

    @Override
    public Color interpolate(final double d) {

        float lastStep = -1;
        int lastColor = -1;
        for(int k=0;k<ARGB.length;k++){
            final int currentColor = ARGB[k];
            double kd = k;
            double total = ARGB.length-1;
            final float currentStep = (float) (kd / total);

            //first element, dont interpolate colors
            if(k == 0){
                lastColor = currentColor;
                lastStep = 0;
            }

            if(d>currentStep){
                lastStep = currentStep;
                lastColor = currentColor;
                continue;
            }

            if(d == currentStep){
                return new Color(currentColor);
            }

            final float stepInterval  = currentStep - lastStep;
            final int lastAlpha     = (lastColor>>>24) & 0xFF;
            final int lastRed       = (lastColor>>>16) & 0xFF;
            final int lastGreen     = (lastColor>>> 8) & 0xFF;
            final int lastBlue      = (lastColor>>> 0) & 0xFF;
            final int alphaInterval = ((currentColor>>>24) & 0xFF) - lastAlpha;
            final int redInterval   = ((currentColor>>>16) & 0xFF) - lastRed;
            final int greenInterval = ((currentColor>>> 8) & 0xFF) - lastGreen;
            final int blueInterval  = ((currentColor>>> 0) & 0xFF) - lastBlue;

            //calculate interpolated color
            final float relativePosition = (float) (d - lastStep);
            final double pourcent = (double)( (double)relativePosition / (double)stepInterval);
            int a = lastAlpha + (int)(pourcent*alphaInterval);
            int r = lastRed   + (int)(pourcent*redInterval);
            int g = lastGreen + (int)(pourcent*greenInterval);
            int b = lastBlue  + (int)(pourcent*blueInterval);
            a <<= 24;
            r <<= 16;
            g <<=  8;
            b <<=  0;
            return new Color(a|r|g|b);
        }

        return new Color(ARGB[ARGB.length-1]);
    }


}
