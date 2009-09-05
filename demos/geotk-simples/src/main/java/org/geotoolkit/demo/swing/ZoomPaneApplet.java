/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.geotoolkit.demo.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import javax.swing.JApplet;

import org.geotoolkit.gui.swing.ZoomPane;


/**
 * Display a {@link ZoomPane} in an applet. This demo drawn simple geometric shapes.
 * The purpose is only to illustrate the zoom capabilities.
 */
@SuppressWarnings("serial")
public class ZoomPaneApplet extends JApplet {
    /**
     * Initialization method that will be called after the applet is loaded into
     * the browser. This method creates a new applet with a simple {@link ZoomPane}.
     */
    @Override
    public void init() {
        add(new SimpleZoomPane());
    }

    /**
     * Our subclass of {@link ZoomPane} which drawn some simple geometric shapes.
     */
    private static class SimpleZoomPane extends ZoomPane {
        /**
         * The rectangle which will be drawn on the zoom pane.
         */
        private final Rectangle rect = new Rectangle(100, 200, 100, 93);

        /**
         * A triangle to be drawn inside the above rectangle.
         */
        private final Polygon poly = new Polygon(new int[] {125,175,150}, new int[] {225,225,268}, 3);

        /**
         * Creates a new zoom pane which allow uniform scales,
         * rotations, translations and a few extra actions.
         */
        public SimpleZoomPane() {
            super(UNIFORM_SCALE | ROTATE | TRANSLATE_X | TRANSLATE_Y | RESET | DEFAULT_ZOOM);
        }

        /**
         * Returns the zoom pane bounds in logical coordinate. In our demo, the rectangle
         * is also the bounds since the triandle is fully contained inside that rectangle.
         */
        @Override
        public Rectangle2D getArea() {
            return (Rectangle) rect.clone();
        }

        /**
         * Paints the rectangle and the triangle inside it.
         */
        @Override
        protected void paintComponent(final Graphics2D graphics) {
            graphics.transform(zoom);
            graphics.setColor(Color.RED);
            graphics.fill(poly);
            graphics.setColor(Color.BLUE);
            graphics.draw(poly);
            graphics.draw(rect);
        }
    }
}
