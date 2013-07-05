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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D.Double;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
import org.opengis.filter.expression.Expression;

public class WellKnownMarkFactory implements MarkFactory {

    /** The logger for the rendering module. */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.rendering.style");

    public static final GeneralPath CROSS;
    public static final Shape STAR;
    public static final Shape TRIANGLE;
    public static final GeneralPath ARROW;
    public static final Shape X;
    public static final GeneralPath HATCH;
    public static final Shape SQUARE;
    public static final Shape CIRCLE;

    static {
        CROSS = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        CROSS.moveTo(0.5f, 0.125f);
        CROSS.lineTo(0.125f, 0.125f);
        CROSS.lineTo(0.125f, 0.5f);
        CROSS.lineTo(-0.125f, 0.5f);
        CROSS.lineTo(-0.125f, 0.125f);
        CROSS.lineTo(-0.5f, 0.125f);
        CROSS.lineTo(-0.5f, -0.125f);
        CROSS.lineTo(-0.125f, -0.125f);
        CROSS.lineTo(-0.125f, -0.5f);
        CROSS.lineTo(0.125f, -0.5f);
        CROSS.lineTo(0.125f, -0.125f);
        CROSS.lineTo(0.5f, -0.125f);
        CROSS.lineTo(0.5f, 0.125f);

        AffineTransform at = new AffineTransform();
        at.rotate(Math.PI / 4.0);
        X = CROSS.createTransformedShape(at);
        
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        float angle = (float) (-Math.PI / 2.0);
        float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);
        float dist = 0.5f;
        path.moveTo( dist*cos , dist*sin);
        for(int i=0; i<10; i++){
            angle += Math.PI / 5.0;
            dist = (i%2 !=0) ? 0.5f : 0.25f ;
            sin = (float) Math.sin(angle);
            cos = (float) Math.cos(angle);
            path.lineTo(dist*cos , dist*sin);
        }
        at = new AffineTransform();
        at.translate(0, -path.getBounds2D().getCenterY());
        STAR = path.createTransformedShape(at);


        path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        angle = (float) (-Math.PI / 2.0);
        sin = (float) Math.sin(angle);
        cos = (float) Math.cos(angle);
        dist = 0.5f;
        path.moveTo(dist*cos , dist*sin);
        for(int i=0; i<3; i++){
            angle += 2f/3f * Math.PI ;
            sin = (float) Math.sin(angle);
            cos = (float) Math.cos(angle);
            path.lineTo(dist*cos , dist*sin);
        }
        at = new AffineTransform();
        at.translate(0, -path.getBounds2D().getCenterY());
        TRIANGLE = path.createTransformedShape(at);

        ARROW = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        ARROW.moveTo(0f, -.5f);
        ARROW.lineTo(.5f, 0f);
        ARROW.lineTo(0f, .5f);
        ARROW.lineTo(0f, .1f);
        ARROW.lineTo(-.5f, .1f);
        ARROW.lineTo(-.5f, -.1f);
        ARROW.lineTo(0f, -.1f);
        ARROW.lineTo(0f, -.5f);

        HATCH = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        HATCH.moveTo(.55f,.57f);
        HATCH.lineTo(.52f,.57f);
        HATCH.lineTo(-.57f,-.52f);
        HATCH.lineTo(-.57f,-.57f);
        HATCH.lineTo(-.52f, -.57f);
        HATCH.lineTo(.57f, .52f);
        HATCH.lineTo(.57f,.57f);
                
        HATCH.moveTo(.57f,-.49f);
        HATCH.lineTo(.49f, -.57f);
        HATCH.lineTo(.57f,-.57f);
        HATCH.lineTo(.57f,-.49f);
                
        HATCH.moveTo(-.57f,.5f);
        HATCH.lineTo(-.5f, .57f);
        HATCH.lineTo(-.57f,.57f);
        HATCH.lineTo(-.57f,.5f);
        
        SQUARE = new Double(-.5, -.5, 1., 1.);
        
        CIRCLE = new java.awt.geom.Ellipse2D.Double(-.5, -.5, 1., 1.);
    }
    
    @Override
    public Shape getShape(final Graphics2D graphics, final Expression symbolUrl, final Object candidate) throws Exception {
        
        if(symbolUrl == null){
            return null;
        }
        
        final String wellKnownName = symbolUrl.evaluate(candidate, String.class);
        
        if ("cross".equalsIgnoreCase(wellKnownName)) {
            return CROSS;
        }else if ("circle".equalsIgnoreCase(wellKnownName)) {
            return CIRCLE;
        }else if ("triangle".equalsIgnoreCase(wellKnownName)) {
            return TRIANGLE;
        }else if ("X".equalsIgnoreCase(wellKnownName)) {
            return X;
        }else if ("star".equalsIgnoreCase(wellKnownName)) {
            return STAR;
        }else if ("arrow".equalsIgnoreCase(wellKnownName)) {
            return ARROW;
        }else if ("hatch".equalsIgnoreCase(wellKnownName)) {
            return HATCH;
        }else if ("square".equalsIgnoreCase(wellKnownName)) {
            return SQUARE;
        }

        LOGGER.finer("Could not find the symbol, returning null");
        return null;
    }

}
