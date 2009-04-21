/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display.shape;

import java.awt.*;
import javax.swing.*;
import static org.geotoolkit.display.shape.ShapeTest.*;


/**
 * Display a Java2D shape and tests inclusion or intersection of small rectangles.
 * Run from the command line (no argument needed) for testing the appearance of the
 * {@link Arrow2D} shape.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 */
@SuppressWarnings("serial")
final class ShapeViewer extends JPanel {
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
     * One of {@link #NONE}, {@link #CONTAINS_POINT}, etc.
     */
    private final int method;

    /**
     * Creates a viewer for the given shape.
     */
    private ShapeViewer(final Shape shape, final int method) {
        this.shape  = shape;
        this.method = method;
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
     */
    private static JPanel createPanel(final Shape shape) {
        final JPanel pane = new JPanel(new GridLayout(2,2));
        for (int i=0; i<4; i++) {
            final JPanel inside = new JPanel(new BorderLayout());
            final JLabel label = new JLabel(LABELS[i], JLabel.CENTER);
            label.setForeground(Color.WHITE);
            inside.add(label, BorderLayout.NORTH);
            inside.add(new ShapeViewer(shape, i), BorderLayout.CENTER);
            inside.setBackground(Color.BLACK);
            pane.add(inside);
        }
        return pane;
    }

    /**
     * Display the arrow for testing purpose.
     */
    public static void main(final String[] args) {
        final Shape shape  = new Arrow2D(SHAPE_X, SHAPE_Y, SHAPE_WIDTH, SHAPE_HEIGHT);
        final JPanel panel = createPanel(shape);
        final JFrame frame = new JFrame("Shape viewer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.setSize(2*(TEST_AREA_WIDTH + 10), 2*(TEST_AREA_HEIGHT + 10));
        frame.setVisible(true);
    }
}
