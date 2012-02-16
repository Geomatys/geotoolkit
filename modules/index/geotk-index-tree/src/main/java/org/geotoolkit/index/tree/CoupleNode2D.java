/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.index.tree;

import java.awt.geom.Rectangle2D;
import org.geotoolkit.util.ArgumentChecks;

/**Create a {@code CoupleNode2D}.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class CoupleNode2D implements Couple<Node2D> {

    private final Node2D node1;
    private final Node2D node2;

    /**Create a couple of two {@code Node2D}.
     * 
     * @param node1
     * @param node2 
     * @throws IllegalArgumentException if node1 or node2 are null.
     */
    public CoupleNode2D(final Node2D node1, final Node2D node2) {
        ArgumentChecks.ensureNonNull("create couplenode2D : node1", node1);
        ArgumentChecks.ensureNonNull("create couplenode2D : node2", node2);
        this.node1 = node1;
        this.node2 = node2;
    }

    /**
     * @return node1.
     */
    @Override
    public Node2D getObject1() {
        return node1;
    }

    /**
     * @return node2.
     */
    @Override
    public Node2D getObject2() {
        return node2;
    }

    /**
     * @return sum of two Node2D boundary.
     */
    @Override
    public double getPerimeter() {
        final Rectangle2D rectO1 = getObject1().getBoundary().getBounds2D();
        final Rectangle2D rectO2 = getObject2().getBoundary().getBounds2D();
        return 2 * (rectO1.getWidth() + rectO1.getHeight() + rectO2.getWidth() + rectO2.getHeight());
    }

    /**
     * @return true if the two Node2D intersect them else false.
     */
    @Override
    public boolean intersect() {
        return getObject1().getBoundary().intersects(getObject2().getBoundary().getBounds2D());
    }

    /**
     * @return  Euclidean distance between two Node2D centroids.
     */
    @Override
    public double getDistance() {
        return TreeUtils.getDistanceBetweenTwoBound2D(getObject1().getBoundary().getBounds2D(), getObject2().getBoundary().getBounds2D());
    }

    /**
     * {@inheritDoc}. 
     */
    @Override
    public double getOverlaps() {
        final Rectangle2D over = getObject1().getBoundary().getBounds2D().createIntersection(getObject2().getBoundary().getBounds2D());
        return over.getWidth() * over.getHeight();
    }
    
    /**
     * {@inheritDoc}. 
     */
    @Override
    public double getArea() {
        final Rectangle2D rectO1 = getObject1().getBoundary().getBounds2D();
        final Rectangle2D rectO2 = getObject2().getBoundary().getBounds2D();
        final double areaSom = rectO1.getWidth()*rectO1.getHeight()+rectO2.getWidth()*rectO2.getHeight();
        if(rectO1.intersects(rectO2)){
            return areaSom-getOverlaps();
        }
        return areaSom;
    }
}
