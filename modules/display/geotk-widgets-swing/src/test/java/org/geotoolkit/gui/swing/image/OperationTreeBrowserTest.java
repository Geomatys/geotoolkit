/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.gui.swing.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.media.jai.operator.AddDescriptor;
import javax.media.jai.operator.AddConstDescriptor;
import javax.media.jai.operator.ConstantDescriptor;
import javax.media.jai.operator.GradientMagnitudeDescriptor;
import javax.media.jai.operator.MultiplyConstDescriptor;

import org.geotoolkit.test.gui.SwingTestBase;


/**
 * Tests the {@link OperationTreeBrowser}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.05
 *
 * @since 2.3
 */
public final strictfp class OperationTreeBrowserTest extends SwingTestBase<OperationTreeBrowser> {
    /**
     * The image width and height.
     */
    private static final int WIDTH = 200, HEIGHT = 250;

    /**
     * The value to put in the image of constant pixel values.
     */
    private static final byte VALUE = 40;

    /**
     * Constructs the test case.
     */
    public OperationTreeBrowserTest() {
        super(OperationTreeBrowser.class);
    }

    /**
     * Creates the widget.
     */
    @Override
    protected OperationTreeBrowser create(final int index) {
        RenderedImage image;
        image = ConstantDescriptor.create(Float.valueOf(WIDTH), Float.valueOf(HEIGHT), new Byte[] {VALUE}, null);
        image = AddDescriptor.create(createPictures(), image, null);
        image = MultiplyConstDescriptor.create(image, new double[] {1.5}, null);
        image = GradientMagnitudeDescriptor.create(image, null, null, null);
        image = AddConstDescriptor.create(image, new double[] {35}, null);
        return new OperationTreeBrowser(image);
    }

    /**
     * Returns an image which contains some arbitrary geometric shapes.
     */
    private static RenderedImage createPictures() {
        final BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        final Graphics2D gr = image.createGraphics();
        gr.setColor(Color.GRAY);
        gr.fill(new Ellipse2D.Float(120, 140, 80, 100));
        gr.setColor(Color.DARK_GRAY);
        gr.fill3DRect(40, 50, 100, 120, true);
        gr.dispose();
        return image;
    }
}
