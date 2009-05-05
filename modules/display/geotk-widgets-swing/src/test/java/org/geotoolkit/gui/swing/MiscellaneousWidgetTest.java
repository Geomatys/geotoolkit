/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.util.Locale;
import java.util.Random;

import javax.media.jai.operator.AddConstDescriptor;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.GradientMagnitudeDescriptor;
import javax.media.jai.operator.MultiplyConstDescriptor;

import org.geotoolkit.gui.swing.image.*;
import org.geotoolkit.gui.swing.referencing.*;
import org.geotoolkit.internal.image.ColorUtilities;
import org.geotoolkit.measure.AngleFormat;

import org.junit.*;

import static java.awt.Color.*;


/**
 * Tests a set of widgets.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.0
 */
public class MiscellaneousWidgetTest extends WidgetTestCase {
    /**
     * Constructs the test case.
     */
    public MiscellaneousWidgetTest() {
        super(About.class);
    }

    /**
     * Sets whatever widgets should be show or not. The default implementation
     * set {@link #displayEnabled} to {@code false}. You can modify this setting
     * and set it to {@code true} for displaying every widgets.
     */
    @Before
    public void initDisplayState() {
        displayEnabled = false;
    }

    /**
     * Tests the {@link About} dialog.
     */
    @Test
    public void testAbout() {
        final About test = new About();
        component = test;
        show();
    }

    /**
     * Tests the {@link DisjointLists}.
     */
    @Test
    public void testDisjointLists() {
        final DisjointLists test = new DisjointLists();
        test.addElements(Locale.getAvailableLocales());
        component = test;
        show();
    }

    /**
     * Tests the {@link FormatChooser}.
     */
    @Test
    public void testFormatChooser() {
        final FormatChooser test = new FormatChooser(new AngleFormat());
        component = test;
        show();
    }

    /**
     * Tests the {@link CoordinateChooser}.
     */
    @Test
    public void testCoordinateChooser() {
        final CoordinateChooser test = new CoordinateChooser();
        component = test;
        show();
    }

    /**
     * Tests the {@link ImageFileChooser}.
     */
    @Test
    public void testImageFileChooser() {
        final ImageFileChooser test = new ImageFileChooser("png");
        test.setDialogType(ImageFileChooser.OPEN_DIALOG);
        component = test;
        show();
    }

    /**
     * Tests the {@link MosaicChooser}.
     */
    @Test
    public void testMosaicChooser() {
        final MosaicChooser test = new MosaicChooser();
        component = test;
        show();
    }

    /**
     * Tests the {@link KernelEditor}.
     */
    @Test
    public void testKernelEditor() {
        final KernelEditor test = new KernelEditor();
        test.addDefaultKernels();
        component = test;
        show();
    }

    /**
     * Tests the {@link GradientKernelEditor}.
     */
    @Test
    public void testGradientKernelEditor() {
        final GradientKernelEditor test = new GradientKernelEditor();
        test.addDefaultKernels();
        component = test;
        show();
    }

    /**
     * Tests the {@link ColorRamp}.
     */
    @Test
    public void testColorRamp() {
        final ColorRamp test = new ColorRamp();
        final int[] ARGB = new int[256];
        ColorUtilities.expand(new Color[] {RED, ORANGE, YELLOW, CYAN}, ARGB, 0, ARGB.length);
        test.setColors(ColorUtilities.getIndexColorModel(ARGB));
        component = test;
        show();
    }

    /**
     * Tests the {@link Plot2D}.
     */
    @Test
    public void testPlot2D() {
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

    /**
     * Tests the {@link ZoomPane}.
     */
    @Test
    @SuppressWarnings("serial")
    public void testZoomPane() {
        final Rectangle rect = new Rectangle(100,200,100,93);
        final Polygon   poly = new Polygon(new int[] {125,175,150}, new int[] {225,225,268}, 3);
        final ZoomPane  pane = new ZoomPane(
                ZoomPane.UNIFORM_SCALE | ZoomPane.ROTATE      |
                ZoomPane.TRANSLATE_X   | ZoomPane.TRANSLATE_Y |
                ZoomPane.RESET         | ZoomPane.DEFAULT_ZOOM)
        {
            @Override public Rectangle2D getArea() {
                return rect;
            }

            @Override protected void paintComponent(final Graphics2D graphics) {
                graphics.transform(zoom);
                graphics.setColor(RED);
                graphics.fill(poly);
                graphics.setColor(BLUE);
                graphics.draw(poly);
                graphics.draw(rect);
            }
        };
        pane.setPaintingWhileAdjusting(true);
        component = pane;
        show("ZoomPane");
    }

    /**
     * Tests the {@link OperationTreeBrowser}.
     */
    @Test
    public void testOperationTree() {
        RenderedImage image;
        final Float size = new Float(200);
        final Byte value = new Byte((byte)10);
        image = ConstantDescriptor.create(size,size, new Byte[]{value}, null);
        image = MultiplyConstDescriptor.create(image, new double[] {2}, null);
        image = GradientMagnitudeDescriptor.create(image, null, null, null);
        image = AddConstDescriptor.create(image, new double[] {35}, null);
        component = new OperationTreeBrowser(image);
        show();
    }
}
