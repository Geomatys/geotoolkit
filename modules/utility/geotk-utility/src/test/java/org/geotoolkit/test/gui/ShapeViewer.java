/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.test.gui;

import java.awt.*;
import javax.swing.*;
import static org.geotoolkit.test.gui.ShapeTestBase.*;


/**
 * Display a Java2D shape and tests inclusion or intersection of small rectangles.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 */
@SuppressWarnings("serial")
final strictfp class ShapeViewer extends JPanel {
    /**
     * Enumeration of the methods to test.
     */
    private static final int NONE=0, CONTAINS_POINT=1, CONTAINS_RECTANGLE=2, INTERSECTS=3;
    private static final String[] LABELS = new String[4];
    static {
        LABELS[NONE]               = "none";
        LABELS[CONTAINS_POINT]     = "contains(Point)";
        LABELS[CONTAINS_RECTANGLE] = "contains(Rectangle)";
        LABELS[INTERSECTS]         = "intersects(Rectangle)";
    }

    /**
     * The shape to draw.
     */
    private final Shape shape;

    /**
     * The shape to use as a reference, or {@code null} if none.
     */
    private final Shape reference;

    /**
     * One of {@link #NONE}, {@link #CONTAINS_POINT}, etc.
     */
    private final int method;

    /**
     * Creates a viewer for the given shape.
     */
    private ShapeViewer(final Shape shape, final Shape reference, final int method) {
        this.shape     = shape;
        this.reference = reference;
        this.method    = method;
    }

    /**
     * Paints the shape.
     */
    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;
        final Color oldColor = g.getColor();
        g.setColor(Color.BLUE);
        g.fill(shape);
        if (reference != null) {
            g.setColor(Color.YELLOW);
            g.draw(reference);
        }
        final Rectangle test = new Rectangle(TEST_SAMPLING_WIDTH, TEST_SAMPLING_HEIGHT);
        for (test.y=0; test.y<TEST_AREA_HEIGHT; test.y+=TEST_INTERVAL_Y) {
            for (test.x=0; test.x<TEST_AREA_WIDTH; test.x+=TEST_INTERVAL_X) {
                final boolean inside;
                switch (method) {
                    case NONE: {
                        continue; // We are wasting a bit of CPU, but this is just a test...
                    }
                    case CONTAINS_POINT: {
                        inside = shape.contains(test.getCenterX(), test.getCenterY());
                        break;
                    }
                    case CONTAINS_RECTANGLE: {
                        inside = shape.contains(test);
                        break;
                    }
                    case INTERSECTS: {
                        inside = shape.intersects(test);
                        break;
                    }
                    default: {
                        throw new AssertionError(method);
                    }
                }
                g.setColor(inside ? Color.GREEN : Color.RED);
                g.fill(test);
            }
        }
        g.setColor(oldColor);
    }

    /**
     * Creates a panel for the given shape. The panel will contains many view of the same
     * shape, but testing different methods (contains, intersects, etc.). The views are
     * organized on a grid.
     *
     * @param shape The shape to show.
     * @param reference The shape to use as a reference, or {@code null} if none.
     * @param withSamples {@code true} if the panel should contain sample points for
     *        {@code contains} and {@code intersects} methods, or {@code false} for
     *        displaying the shape alone.
     */
    static JPanel createPanel(final Shape shape, final Shape reference, final boolean withSamples) {
        final int numPerRow = withSamples ? 2 : 1;
        final int numPanels = numPerRow * numPerRow;
        final JPanel pane = new JPanel(new GridLayout(numPerRow, numPerRow));
        for (int i=0; i<numPanels; i++) {
            final JPanel inside = new JPanel(new BorderLayout());
            final JLabel label = new JLabel(LABELS[i], JLabel.CENTER);
            label.setForeground(Color.WHITE);
            inside.add(label, BorderLayout.NORTH);
            inside.add(new ShapeViewer(shape, reference, i), BorderLayout.CENTER);
            inside.setBackground(Color.BLACK);
            pane.add(inside);
        }
        Dimension size = new Dimension(TEST_AREA_WIDTH + 10, TEST_AREA_HEIGHT + 10);
        size.width  *= 2;
        size.height *= 2;
        pane.setPreferredSize(size);
        return pane;
    }
}
