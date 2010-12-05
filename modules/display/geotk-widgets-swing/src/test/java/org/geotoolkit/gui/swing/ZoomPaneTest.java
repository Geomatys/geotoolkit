/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import static java.awt.Color.*;

import org.geotoolkit.test.gui.SwingTestBase;


/**
 * Tests the {@link ZoomPane}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.01
 *
 * @since 2.0
 */
public final class ZoomPaneTest extends SwingTestBase<ZoomPane> {
    /**
     * Constructs the test case.
     */
    public ZoomPaneTest() {
        super(ZoomPane.class);
    }

    /**
     * Creates the widget.
     */
    @Override
    @SuppressWarnings("serial")
    protected ZoomPane create(final int index) {
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
        return pane;
    }
}
