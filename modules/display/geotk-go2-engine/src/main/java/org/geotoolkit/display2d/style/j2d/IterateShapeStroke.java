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

package org.geotoolkit.display2d.style.j2d;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class IterateShapeStroke implements Stroke {

    private final GeneralPath result = new GeneralPath();
    private final Point2D pt = new Point2D.Double();
    private final float initialGap;
    private final float gap;
    private final Shape motif;

    public IterateShapeStroke(float initialGap, float gap, Shape motif) {
        this.initialGap = initialGap;
        this.gap = gap;
        this.motif = motif;
    }

    @Override
    public Shape createStrokedShape(Shape shape) {
        final PathIterator it = new FlatteningPathIterator(shape.getPathIterator(null), 1d);
        final PathWalker walker = new DefaultPathWalker(it);

        walker.walk(initialGap);
        while(!walker.isFinished()){
            //paint the motif --------------------------------------------------
            walker.getPosition(pt);
            final float angle = walker.getRotation();
            
            final AffineTransform trs = new AffineTransform();
            trs.translate(pt.getX(), pt.getY());
            trs.rotate(angle);
            result.append(trs.createTransformedShape(motif), false);

            //walk over the gap ------------------------------------------------
            walker.walk(gap);
        }

        return result;
    }


    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static Shape resize(Shape shape, float size){
        shape = anchor(shape, 0.5f);
        final Rectangle2D bounds = shape.getBounds2D();

        if(bounds.getWidth() != size){
            final float scale = (float) (size/bounds.getWidth());
            AffineTransform t = new AffineTransform();
            t.scale(scale, scale);
            shape = t.createTransformedShape(shape);
        }

        return shape;
    }

    public static Shape anchor(Shape shape, float anchorY){
        final Rectangle2D bounds = shape.getBounds2D();
        AffineTransform t = new AffineTransform();
        t.setToTranslation(-bounds.getCenterX(), -bounds.getMinY() - bounds.getHeight()*anchorY);
        return  t.createTransformedShape(shape);
    }

}
