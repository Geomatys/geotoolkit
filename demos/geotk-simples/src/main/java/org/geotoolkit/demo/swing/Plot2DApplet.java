/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.geotoolkit.demo.swing;

import java.util.Random;
import javax.swing.JApplet;

import org.geotoolkit.gui.swing.Plot2D;


/**
 * Display a {@link Plot2D} in an applet.
 */
@SuppressWarnings("serial")
public class Plot2DApplet extends JApplet {
    /**
     * Initialization method that will be called after the applet is loaded into the browser.
     * This method creates a new applet showing a {@link Plot2D} with random data.
     */
    @Override
    public void init() {
        final Random random = new Random();
        final Plot2D plot = new Plot2D(true, false);
        plot.addXAxis("Some x values");
        plot.addYAxis("Some y values");
        for (int j=0; j<2; j++) {
            final int length = 800;
            final float[] x = new float[length];
            final float[] y = new float[length];
            for (int i=0; i<length; i++) {
                x[i] = i / 10f;
                y[i] = (float) random.nextGaussian();
                if (i != 0) {
                    y[i] += y[i-1];
                }
            }
            plot.addSeries("Random values", null, x, y);
        }
        add(plot.createScrollPane());
    }
}
