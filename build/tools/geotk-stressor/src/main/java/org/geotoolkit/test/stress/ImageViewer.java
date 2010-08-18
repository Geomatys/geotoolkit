/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test.stress;

import java.util.List;
import java.awt.Window;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.geom.AffineTransform;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * Displays an image.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 */
@SuppressWarnings("serial")
final class ImageViewer extends JPanel {
    /**
     * The image to display.
     */
    private RenderedImage image;

    /**
     * Creates a new, initially empty, panel.
     */
    private ImageViewer() {
    }

    /**
     * Sets the image to display. This method can be invoked from any thread.
     */
    public void setImage(final RenderedImage image) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                ImageViewer.this.image = image;
                repaint();
            }
        });
    }

    /**
     * Paints the image.
     */
    @Override
    protected void paintComponent(final Graphics gr) {
        final int cx = getWidth();
        final int cy = getHeight();
        super.paintComponent(gr);
        if (image != null) {
            final Graphics2D graphics = (Graphics2D) gr;
            final double width  = image.getWidth();
            final double height = image.getHeight();
            double scale = Math.min(cx / width, cy / height);
            // Round the scale to integer number for faster rendering.
            if (scale < 1) {
                scale = 1 / Math.rint(1 / scale);
            } else {
                scale = 1;
            }
            final AffineTransform tr = AffineTransform.getTranslateInstance(
                    Math.rint(0.5 * (cx - (width  * scale))),
                    Math.rint(0.5 * (cy - (height * scale))));
            tr.scale(scale, scale);
            tr.translate(-image.getMinX(), -image.getMinY());
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            graphics.drawRenderedImage(image, tr);
        }
        gr.drawRect(0, 0, cx, cy);
    }

    /**
     * Shows all the given stressors.
     * Closing the window will stop the stressor.
     */
    public static Window show(final List<? extends Stressor> stressors) {
        final JFrame frame = new JFrame("Stressor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        final int n  = stressors.size();
        final int nx = (int) Math.round(Math.sqrt(n));
        final int ny = (n + (nx-1)) / nx;
        frame.setLayout(new GridLayout(ny, nx, 2, 2));
        for (int i=0; i<nx*ny; i++) {
            final ImageViewer viewer = new ImageViewer();
            frame.add(viewer);
            if (i < n) {
                stressors.get(i).viewer = viewer;
            }
        }
        frame.setVisible(true);
        return frame;
    }
}
