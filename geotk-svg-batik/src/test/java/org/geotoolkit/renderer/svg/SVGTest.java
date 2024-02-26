/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.renderer.svg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.geotoolkit.display.shape.DoubleDimension2D;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SVGTest {

    @Test
    public void testRenderingImage() throws URISyntaxException, Exception{

        final SVGGraphicFactory factory = new SVGGraphicFactory();
        final URI uri = SVGTest.class.getResource("/org/geotoolkit/svg/test.svg").toURI();

        final BufferedImage image = factory.getImage(uri, "image/svg", 20f, null);

        assertEquals(Color.RED.getRGB(), image.getRGB(2, 2));
        assertEquals(Color.GREEN.getRGB(), image.getRGB(18, 2));
        assertEquals(Color.BLUE.getRGB(), image.getRGB(2, 18));
        assertEquals(Color.BLACK.getRGB(), image.getRGB(18, 18));

    }

    @Test
    public void testRenderingPosition() throws URISyntaxException, Exception{

        final SVGGraphicFactory factory = new SVGGraphicFactory();
        final URI uri = SVGTest.class.getResource("/org/geotoolkit/svg/test.svg").toURI();

        final BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        factory.renderImage(uri, "image/svg", 20f, image.createGraphics(), new Point2D.Double(10, 10), null);

        assertEquals(Color.RED.getRGB(), image.getRGB(2, 2));
        assertEquals(Color.GREEN.getRGB(), image.getRGB(18, 2));
        assertEquals(Color.BLUE.getRGB(), image.getRGB(2, 18));
        assertEquals(Color.BLACK.getRGB(), image.getRGB(18, 18));

    }

    /**
     * Test replacing a stylesheet.
     */
    @Test
    public void testCSSSubstitute() throws IOException, Exception {

        try (final BatikSVG svg = new BatikSVG(SVGTest.class.getResource("/org/geotoolkit/svg/doc.svg").toString())){
            //normal rendering
            final BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = img.createGraphics();
            svg.paint(g);
            g.dispose();

            assertEquals(Color.RED.getRGB(), img.getRGB(1, 1));
            assertEquals(Color.GREEN.getRGB(), img.getRGB(60, 1));
        }

        try (final BatikSVG svg = new BatikSVG(SVGTest.class.getResource("/org/geotoolkit/svg/doc.svg").toString())){
            //replace css content
            svg.setStyleSheetContent(".squarestyle1 {fill:#0000FF}\n.squarestyle2 {fill:#FF0000}", false);
            final BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = img.createGraphics();
            svg.paint(g);
            g.dispose();

            assertEquals(Color.BLUE.getRGB(), img.getRGB(1, 1));
            assertEquals(Color.RED.getRGB(), img.getRGB(60, 1));
        }

        try (final BatikSVG svg = new BatikSVG(SVGTest.class.getResource("/org/geotoolkit/svg/doc.svg").toString())){
            //inherit css content
            svg.setStyleSheetContent(".squarestyle2 {fill:#0000FF}", true);
            final BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = img.createGraphics();
            svg.paint(g);
            g.dispose();

            assertEquals(Color.RED.getRGB(), img.getRGB(1, 1));
            assertEquals(Color.BLUE.getRGB(), img.getRGB(60, 1));
        }

        try (final BatikSVG svg = new BatikSVG(SVGTest.class.getResource("/org/geotoolkit/svg/doc.svg").toString())){
            //replace css path
            svg.setStyleSheetPath("style2.css");
            final BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = img.createGraphics();
            svg.paint(g);
            g.dispose();

            assertEquals(Color.BLUE.getRGB(), img.getRGB(1, 1));
            assertEquals(Color.RED.getRGB(), img.getRGB(60, 1));
        }
    }

    /**
     * Test unit conversions in document sizes.
     */
    @Test
    public void testDocumentSizes() throws IOException {
        try (final BatikSVG svg = new BatikSVG(SVGTest.class.getResource("/org/geotoolkit/svg/doc.svg").toString())){
            //20mm converted to pixels
            svg.setPixelToMm(0.26f); // ~96dpi
            DoubleDimension2D docSize = svg.getDocumentSize();
            assertEquals(20.0 / 0.26f, docSize.width, 0.00001);
            assertEquals(20.0 / 0.26f, docSize.height, 0.00001);

            //check viewbox
            Rectangle2D.Double viewBox = svg.getViewBox();
            assertEquals(new Rectangle2D.Double(-10,-10,20,20), viewBox);
        }
    }

}
