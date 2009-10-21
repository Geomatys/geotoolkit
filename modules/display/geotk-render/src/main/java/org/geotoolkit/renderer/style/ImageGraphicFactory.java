/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;

/**
 * External graphic factory accepting an Expression that can be evaluated to a
 * URL pointing to a image file. The <code>format</code> must be one of the
 * mime types supported by the current JDK.
 * 
 * @author Andrea Aime - TOPP
 * 
 * @module pending
 */
public class ImageGraphicFactory implements ExternalGraphicFactory {

    /** The logger for the rendering module. */
    private static final Logger LOGGER = Logging.getLogger(ImageGraphicFactory.class);

    /** Current way to load images */
    ImageLoader imageLoader = new ImageLoader();

    /** Holds the of graphic formats supported by the current jdk */
    static Set<String> supportedGraphicFormats = new HashSet<String>(Arrays.asList(ImageIO
            .getReaderMIMETypes()));

    public Icon getIcon(Feature feature, Expression url, String format, int size) throws Exception {
        // check we do support the format
        if (!supportedGraphicFormats.contains(format.toLowerCase()))
            return null;

        // evaluate the location as a URL
        URL location = url.evaluate(feature, URL.class);
        if (location == null)
            throw new IllegalArgumentException(
                    "The provided expression cannot be evaluated to a URL");

        // imageLoader is not thread safe
        BufferedImage image;
        synchronized (imageLoader) {
            image = imageLoader.get(location, false);
        }
        
        // if scaling is needed, perform it
        if(size > 0 && image.getHeight() != size) {
            double dsize = (double) size;

            double scaleY = dsize / image.getHeight(); // >1 if you're magnifying
            double scaleX =  scaleY; // keep aspect ratio!

            AffineTransform scaleTx = AffineTransform.getScaleInstance(scaleX,scaleY);  
            AffineTransformOp ato = new AffineTransformOp(scaleTx, AffineTransformOp.TYPE_BILINEAR);
            image = ato.filter(image, null);
        }
        
        return new ImageIcon(image);
    }
    
    /**
     * Returs the set of mime types supported by this factory
     * @return
     */
    public Set<String> getSupportedMimeTypes() {
        return Collections.unmodifiableSet(supportedGraphicFormats);
    }

}
