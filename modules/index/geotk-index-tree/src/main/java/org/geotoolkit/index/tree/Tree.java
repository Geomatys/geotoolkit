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

import java.util.List;

/**
 * Define a generic Tree.
 *
 * @author Rémi Maréchal       (Geomatys).
 * @author Yohann Sorel        (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public interface Tree<A, B> {

    /**
     * Find some {@code Entry} which intersect regionSearch parameter 
     * and add them into result {@code List} parameter.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: if no result found, the list passed in parameter is unchanged.</strong> 
     * </font></blockquote>
     * 
     * @param regionSearch Define the region to find Shape within tree.
     * @param result List of Entr(y)(ies).
     */
    void search(B regionSearch, List<B> result);

    /**
     * Insert a {@code Entry} into Rtree.
     * 
     * @param Entry to insert into tree.
     */
    void insert(B entry);

    /**
     * Find a {@code Entry} into the tree and delete it.
     * 
     * @param Entry to delete.
     */
    void delete(B entry);

    /**
     * @return max number authorized by tree cells.
     */
    int getMaxElements();

    /**
     * @return tree trunk.
     */
    A getRoot();
    
    /**
     * Affect a new root {@Node}.
     * 
     * @param root new root.
     */
    void setRoot(A root);
    
    /**
     * Create a node in accordance with this RTree properties.
     * 
     * @param tree pointer on Tree.
     * @param parent pointer on parent {@code Node2D}.
     * @param children sub {@code Node2D}.
     * @param entries {@code List<Shape>} to add in this node. 
     * @return 
     */
    A createNode(Tree tree, A parent, List<A> listChildren, List<B> listEntries);
}
