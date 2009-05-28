/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display2d.ext.northarrow;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import java.awt.RenderingHints;
import java.awt.Shape;
import org.geotoolkit.display.exception.PortrayalException;


/**
 * Utility class to render north arrow using a provided template.
 *
 * @author Johann Sorel (Geomatys)
 */
public class J2DNorthArrowUtilities {

    private static J2DNorthArrowUtilities INSTANCE = null;

    private J2DNorthArrowUtilities(){
    }

    /**
     * Paint a north arrow using Java2D.
     *
     * @param rotation : map rotation, in radians
     * @param g2d : Graphics2D used to render
     * @param bounds : Rectangle where the scale must be painted
     * @param template  : north arrow template
     * @throws org.geotools.display.exception.PortrayalException
     */
    public void paintNorthArrow(final float rotation,
                              final Graphics2D g2d,
                              final Rectangle bounds,
                              final NorthArrowTemplate template) throws PortrayalException{

        final Image img = template.getImage(bounds.getSize());

        if(img == null) return;

        final Shape oldClip = g2d.getClip();
        g2d.setClip(bounds);

        g2d.translate(bounds.getCenterX(), bounds.getCenterY());
        g2d.rotate(rotation);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(img, -img.getWidth(null)/2, -img.getHeight(null)/2, null);


        g2d.rotate(-rotation);
        g2d.translate(-bounds.getCenterX(), -bounds.getCenterY());
        g2d.setClip(oldClip);

    }

    /**
     * Get the default instance, singleton.
     */
    public static final J2DNorthArrowUtilities getInstance(){
        if(INSTANCE == null){
            INSTANCE = new J2DNorthArrowUtilities();
        }
        return INSTANCE;
    }

}
