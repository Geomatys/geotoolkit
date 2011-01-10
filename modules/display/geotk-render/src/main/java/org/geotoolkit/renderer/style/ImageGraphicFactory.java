/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.renderer.style;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Collection;
import javax.imageio.ImageIO;

import org.geotoolkit.util.collection.UnmodifiableArrayList;

/**
 * External graphic factory accepting an Expression that can be evaluated to a
 * URL pointing to a image file. The <code>format</code> must be one of the
 * mime types supported by the current JDK.
 * 
 * @author Andrea Aime - TOPP
 * @author Johann Sorel (Geomatys)
 * 
 * @module pending
 */
public class ImageGraphicFactory implements ExternalGraphicFactory {

    /** Current way to load images */
    private ImageLoader imageLoader = new ImageLoader();

    /** Holds the of graphic formats supported by the current jdk */
    private static Collection<String> supportedGraphicFormats = UnmodifiableArrayList.wrap(ImageIO.getReaderMIMETypes());

    @Override
    public BufferedImage getImage(final URI location, final String format, final Float size, final RenderingHints hints) throws Exception {
        // check we do support the format
        if (!supportedGraphicFormats.contains(format.toLowerCase()))
            return null;

        // evaluate the location as a URL
        if (location == null)
            throw new IllegalArgumentException("URI must not be null");

        // imageLoader is not thread safe
        BufferedImage image;
        synchronized (imageLoader) {
            image = imageLoader.get(location.toURL(), false);
        }
        
        // if scaling is needed, perform it
        if(size > 0 && image.getHeight() != size) {
            final double dsize = (double) size;
            final double scaleY = dsize / image.getHeight(); // >1 if you're magnifying
            final double scaleX =  scaleY; // keep aspect ratio!
            final AffineTransform scaleTx = AffineTransform.getScaleInstance(scaleX,scaleY);
            final AffineTransformOp ato = new AffineTransformOp(scaleTx, AffineTransformOp.TYPE_BILINEAR);
            image = ato.filter(image, null);
        }
        
        return image;
    }
    
    /**
     * Returs the set of mime types supported by this factory
     * @return Collection<String>
     */
    @Override
    public Collection<String> getSupportedMimeTypes() {
        return supportedGraphicFormats;
    }

    @Override
    public void renderImage(final URI uri, final String mime, final Float size, final Graphics2D g,
            final Point2D center, final RenderingHints hints) throws Exception {
        final BufferedImage img = getImage(uri, mime, size, hints);

        final float dispX = img.getWidth()/2;
        final float dispY = img.getHeight()/2;

        g.translate(-dispX-center.getX(),-dispY-center.getY());
        g.drawImage(img, null, 0, 0);
        g.translate(dispX+center.getX(),dispY+center.getY());
    }

}
