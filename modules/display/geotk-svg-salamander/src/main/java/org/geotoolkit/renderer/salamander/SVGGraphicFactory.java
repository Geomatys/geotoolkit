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

package org.geotoolkit.renderer.salamander;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Collection;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.geotoolkit.renderer.style.ExternalGraphicFactory;

/**
 * Factory capable to read SVG files using Salamander library.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class SVGGraphicFactory implements ExternalGraphicFactory {

    private static final Collection<String> MIME_TYPES = UnmodifiableArrayList.wrap(new String[]{"svg","image/svg"});

    /**
     * {@inheritDoc }
     */
    @Override
    public BufferedImage getImage(final URI uri, final String mime, final Float size, final RenderingHints hints) throws Exception {
        final Dimension dim;
        if(size == null || Float.isNaN(size)){
            dim = new Dimension(12, 12);
        }else{
            dim = new Dimension(size.intValue(), size.intValue());
        }

        final SVGUniverse svgUniverse = new SVGUniverse();
        final SVGDiagram diagram = svgUniverse.getDiagram(uri);
        diagram.setIgnoringClipHeuristic(true);

        final BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = img.createGraphics();
        final Rectangle2D rect = diagram.getViewRect();
        final double scale = Math.min( ((double)dim.width)/rect.getWidth(), ((double)dim.height)/rect.getHeight() );
        final AffineTransform trs = new AffineTransform(scale, 0, 0, scale, -rect.getMinX(), -rect.getMinY());
        g.setTransform(trs);
        diagram.render(g);
        g.dispose();

        return img;
    }

    @Override
    public void renderImage(final URI uri, final String mime, Float size, final Graphics2D g,
            final Point2D center, final RenderingHints hints) throws Exception {

        if(size == null || Float.isNaN(size)){
            size = 12f;
        }
        final float rsize = size/2f;

        g.translate(center.getX()-rsize,center.getY()-rsize);

        //render
        final SVGUniverse svgUniverse = new SVGUniverse();
        final SVGDiagram diagram = svgUniverse.getDiagram(uri);
        diagram.setIgnoringClipHeuristic(true);

        final Rectangle2D bounds = diagram.getViewRect();
        final double scale = Math.min( ((double)size)/bounds.getWidth(), ((double)size)/bounds.getHeight() );
        g.scale(scale, scale);
        try {
            diagram.render(g);
        } finally {
            g.scale(1.0/scale, 1.0/scale);
        }

        g.translate(-center.getX()+rsize,-center.getY()+rsize);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<String> getSupportedMimeTypes() {
        return MIME_TYPES;
    }

}
