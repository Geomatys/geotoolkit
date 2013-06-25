/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

/**
 * Create appropriate {@code Node} to R-Tree.
 *
 * @author Rémi Maréchal (Geomatys)
 */
public final class DefaultNodeFactory implements NodeFactory {

    /**
     * Adapted default {@code Node} factory to R-Tree.
     */
    public static final NodeFactory INSTANCE = new DefaultNodeFactory();
    
    private DefaultNodeFactory(){}

    /**
     * {@inheritDoc }
     */
    @Override
    public Node createNode(Tree tree, Node parent, double[] lowerCorner, double[] upperCorner, Node[] children, Object[] objects, double[][] objectsCoordinates) {
        return new DefaultNode(tree, parent, lowerCorner, upperCorner, children, objects, objectsCoordinates);
    }
}
