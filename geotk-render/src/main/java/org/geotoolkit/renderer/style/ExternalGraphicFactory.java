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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Collection;

/**
 * Symbol handler for an external symbolizers.
 * @module
 */
public interface ExternalGraphicFactory {

    /**
     * Turns the specified URI into an Image.
     * The <code>size</code> parameter defines the size of the image (so that
     * vector based symbols can be drawn at the specified size directly), or may
     * be zero or negative if the size was not specified (in that case the "natural" size of
     * the image will be used, which is the size in pixels for raster images, and
     * 16 for any format that does not have a specific size, according to the SLD spec).<br>
     * <code>null</code> will be returned if this factory cannot handle the
     * provided uri.
     */
    BufferedImage getImage(URI uri, String mime, Float size, RenderingHints hints) throws Exception;

    /**
     * Render in vector quality if possible.
     * @see #getImage(java.net.URI, java.lang.String, java.lang.Float, java.awt.RenderingHints)
     */
    void renderImage(URI uri, String mime, Float size, Graphics2D g,
            Point2D center,RenderingHints hints) throws Exception;

    /**
     * The mime types supported by this factory.
     */
    Collection<String> getSupportedMimeTypes();

}
