/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.test.image;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import javax.swing.*;

import static java.lang.StrictMath.*;


/**
 * A panel showing an image.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 */
@SuppressWarnings("serial")
final strictfp class ImagePanel extends JPanel {
    /**
     * The image to show.
     */
    final RenderedImage image;

    /**
     * Creates a viewer for the given image.
     */
    ImagePanel(final RenderedImage image) {
        this.image = image;
        setPreferredSize(new Dimension(max(300, image.getWidth()), max(30, image.getHeight())));
    }

    /**
     * Paints the image.
     */
    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        final Graphics2D gr = (Graphics2D) graphics;
        gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        final double width  = image.getWidth();
        final double height = image.getHeight();
        final double scale  = min(getWidth() / width, getHeight() / height);
        final AffineTransform gridToPanel = new AffineTransform(
                scale, 0, 0, scale,
                0.5*(getWidth()  - scale*width),
                0.5*(getHeight() - scale*height));
        gr.drawRenderedImage(image, gridToPanel);
    }
}
