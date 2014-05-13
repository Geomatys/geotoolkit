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

import com.sun.prism.paint.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
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
        
        assertEquals(Color.RED.getIntArgbPre(), image.getRGB(2, 2));
        assertEquals(Color.GREEN.getIntArgbPre(), image.getRGB(18, 2));
        assertEquals(Color.BLUE.getIntArgbPre(), image.getRGB(2, 18));
        assertEquals(Color.BLACK.getIntArgbPre(), image.getRGB(18, 18));
        
    }
    
    @Test
    public void testRenderingPosition() throws URISyntaxException, Exception{
        
        final SVGGraphicFactory factory = new SVGGraphicFactory();
        final URI uri = SVGTest.class.getResource("/org/geotoolkit/svg/test.svg").toURI();
        
        final BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        factory.renderImage(uri, "image/svg", 20f, image.createGraphics(), new Point2D.Double(10, 10), null);
        
        assertEquals(Color.RED.getIntArgbPre(), image.getRGB(2, 2));
        assertEquals(Color.GREEN.getIntArgbPre(), image.getRGB(18, 2));
        assertEquals(Color.BLUE.getIntArgbPre(), image.getRGB(2, 18));
        assertEquals(Color.BLACK.getIntArgbPre(), image.getRGB(18, 18));
        
    }
    
}
