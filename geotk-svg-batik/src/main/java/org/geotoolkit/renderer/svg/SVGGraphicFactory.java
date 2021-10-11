/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.renderer.svg;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.logging.Level;
import org.geotoolkit.renderer.style.ExternalGraphicFactory;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.util.logging.Logging;

/**
 * Factory capable to read SVG files.
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

        InputStream stream = null;
        try{
            //try distant url
            stream = uri.toURL().openStream();
        }catch(Exception ex){
            //try class loader
            try{
                stream = SVGGraphicFactory.class.getResourceAsStream(uri.toString());
            }catch(Exception e){
                Logging.getLogger("org.geotoolkit.renderer.svg").log(Level.WARNING, e.getMessage(), e);
            }
        }

        if(stream != null){
            try{
                return (BufferedImage) SvgUtils.read(stream, dim, hints);
            }finally{
                stream.close();
            }
        }
        return null;
    }

    @Override
    public void renderImage(final URI uri, final String mime, Float size, final Graphics2D g,
            final Point2D center, final RenderingHints hints) throws Exception {

        if(size == null || Float.isNaN(size)){
            size = 12f;
        }
        final float rsize = size/2f;

        g.translate(center.getX()-rsize,center.getY()-rsize);
        SvgUtils.render(uri, new Point2D.Float(size, size), g, hints);
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
