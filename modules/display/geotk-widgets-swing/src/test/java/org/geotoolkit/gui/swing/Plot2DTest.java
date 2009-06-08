/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.gui.swing;

import java.util.Random;
import org.geotoolkit.test.Depend;
import org.junit.*;


/**
 * Tests the {@link Plot2D}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 */
@Depend(ZoomPaneTest.class)
public final class Plot2DTest extends WidgetTestCase {
    /**
     * Constructs the test case.
     */
    public Plot2DTest() {
        super(Plot2D.class);
    }

    /**
     * Creates the widget. If {@link #displayEnabled} is {@code true}, then the widget is shown.
     */
    @Test
    public void display() {
        final Random random = new Random();
        Plot2D test = new Plot2D(true, false);
        test.addXAxis("Some x values");
        test.addYAxis("Some y values");
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
            test.addSeries("Random values", null, x, y);
        }
        component = test.createScrollPane();
        show("Plot2D");
    }
}
