/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.gui.swing.etl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import org.netbeans.api.visual.anchor.AnchorShape;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ChainShapes implements AnchorShape{

    public static final int SIZE = 5;
    public static final Shape SHP_ANCHOR_INPUT;
    public static final Shape SHP_ANCHOR_OUTPUT;
    public static final AffineTransform trs = new AffineTransform();


    static{
        SHP_ANCHOR_INPUT = new java.awt.geom.Ellipse2D.Double(-0.5, -0.5, 1, 1);
        SHP_ANCHOR_OUTPUT = new java.awt.geom.Ellipse2D.Double(-0.5, -0.5, 1, 1);
        trs.scale(SIZE, SIZE);
    }

    public static final AnchorShape ANCHOR_INPUT = new ChainShapes(SHP_ANCHOR_INPUT,-0.1);
    public static final AnchorShape ANCHOR_OUTPUT = new ChainShapes(SHP_ANCHOR_OUTPUT,+0.1);

    private final Shape shape;
    private final double tx;

    private ChainShapes(Shape shape, double tx){
        this.shape = shape;
        this.tx=tx;
    }

    @Override
    public boolean isLineOriented() {
        return false;
    }

    @Override
    public int getRadius() {
        return SIZE;
    }

    @Override
    public double getCutDistance() {
        return SIZE/2;
    }

    @Override
    public void paint(Graphics2D g, boolean bln) {
        g = (Graphics2D) g.create();

        AffineTransform t = new AffineTransform(trs);
        t.translate(tx, 0);

        final Shape shp = t.createTransformedShape(shape);

        g.setPaint(Color.DARK_GRAY.brighter());
        g.fill(shp);

        g.setStroke(new BasicStroke(1));
        g.setPaint(Color.BLACK);
        g.draw(shp);

    }

}
