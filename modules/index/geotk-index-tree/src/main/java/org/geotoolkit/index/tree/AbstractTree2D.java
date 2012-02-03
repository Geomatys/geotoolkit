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

import java.awt.Shape;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.Classes;

/**Create a "generic" 2D Tree.
 *
 * @author Rémi Marechal (Geomatys).
 * @author Johann Sorel  (Geomatys).
 */
public abstract class AbstractTree2D implements Tree<Shape> {

    private Node2D root;
    private final int maxElements;

    /**Create 2 Dimension Tree
     * 
     * @param maxElements max value permit for each tree cells.
     * @throws IllegalArgumentException if maxElements is out of required limits.
     */
    public AbstractTree2D(int maxElements) {
        ArgumentChecks.ensureStrictlyPositive("Create Tree : maxElements", maxElements);
        this.maxElements = maxElements;
    }

    /**
     * @return max elements permitted by tree cells. 
     */
    public int getMaxElements() {
        return maxElements;
    }

    /**
     * {@inheritDoc}
     */
    public Node2D getRoot() {
        return root;
    }

    /**Affect a new tree trunk.
     * 
     * @param root 
     */
    public void setRoot(Node2D root) {
        this.root = root;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + "\n" + getRoot();
    }
}
