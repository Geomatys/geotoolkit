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
package org.geotoolkit.renderer.style;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

/**
 * Well Known Mark factory.
 *
 * @author Johann Sorel (Geomatys)
 */
public class WKMMarkFactory extends MarkFactory {

    public static final Shape SQUARE;
    public static final Shape CIRCLE;
    public static final Shape TRIANGLE;
    public static final Shape STAR;
    public static final Shape CROSS;
    public static final Shape X;


    static {
        SQUARE = new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0);
        CIRCLE = new Ellipse2D.Double(-0.5, -0.5, 1.0, 1.0);


        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        double angle = (-Math.PI / 2.0);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double dist = 0.5;
        path.moveTo(dist*cos , dist*sin);
        for(int i=0; i<3; i++){
            angle += 2.0/3.0 * Math.PI ;
            sin = Math.sin(angle);
            cos = Math.cos(angle);
            path.lineTo(dist*cos , dist*sin);
        }
        AffineTransform at = new AffineTransform();
        at.translate(0, -path.getBounds2D().getCenterY());
        TRIANGLE = path.createTransformedShape(at);


        path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        angle =(-Math.PI / 2.0);
        sin = Math.sin(angle);
        cos = Math.cos(angle);
        dist = 0.5;
        path.moveTo( dist*cos , dist*sin);
        for(int i=0; i<10; i++){
            angle += Math.PI / 5.0;
            dist = (i%2 !=0) ? 0.5 : 0.25 ;
            sin = Math.sin(angle);
            cos = Math.cos(angle);
            path.lineTo(dist*cos , dist*sin);
        }
        at = new AffineTransform();
        at.translate(0, -path.getBounds2D().getCenterY());
        STAR = path.createTransformedShape(at);


        path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        path.moveTo( 0.5,   0.125);
        path.lineTo( 0.125, 0.125);
        path.lineTo( 0.125, 0.5);
        path.lineTo(-0.125, 0.5);
        path.lineTo(-0.125, 0.125);
        path.lineTo(-0.5,   0.125);
        path.lineTo(-0.5,  -0.125);
        path.lineTo(-0.125,-0.125);
        path.lineTo(-0.125,-0.5);
        path.lineTo( 0.125,-0.5);
        path.lineTo( 0.125,-0.125);
        path.lineTo( 0.5,  -0.125);
        path.lineTo( 0.5,   0.125);
        CROSS = path;


        at = new AffineTransform();
        at.rotate(Math.PI / 4.0);
        X = path.createTransformedShape(at);

    }

    @Override
    public Shape evaluateShape(String format, Object markRef, int markIndex) {

        if(!(markRef instanceof String)){
            return null;
        }

        final String wellKnownName = (String) markRef;

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
        }else if ("square".equalsIgnoreCase(wellKnownName)) {
            return SQUARE;
        }

        return null;
    }

}
