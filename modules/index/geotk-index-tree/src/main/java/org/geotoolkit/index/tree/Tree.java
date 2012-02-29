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
import org.geotoolkit.index.tree.calculator.Calculator;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.operation.TransformException;

/**
 * Define a generic Tree.
 *
 * @author Rémi Maréchal       (Geomatys).
 * @author Johann Sorel        (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public interface Tree<N> {

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
    void search(Envelope regionSearch, List<Envelope> result) throws TransformException;

    /**
     * Insert a {@code Entry} into Rtree.
     * 
     * @param Entry to insert into tree.
     * @throws MismatchedReferenceSystemException if entry CRS is different from tree CRS 
     */
    void insert(Envelope entry)throws MismatchedReferenceSystemException;

    /**
     * Find a {@code Entry} into the tree and delete it.
     * 
     * @param Entry to delete.
     * @throws MismatchedReferenceSystemException if entry CRS is different from tree CRS 
     */
    void delete(Envelope entry)throws MismatchedReferenceSystemException;

    /**
     * @return max number authorized by tree cells.
     */
    int getMaxElements();

    /**
     * @return tree trunk.
     */
    N getRoot();
    
    /**
     * Affect a new root {@Node}.
     * 
     * @param root new root.
     */
    void setRoot(N root);
    
    /**
     * @return Calculator to effectuate Tree compute.
     */
    Calculator getCalculator();
    
    /**Create a node in accordance with this RTree properties.
     *  
     * @param tree pointer on Tree.
     * @param parent pointer on parent {@code Node}.
     * @param children sub {@code Node}.
     * @param entries entries {@code List} to add in this node. 
     * @param coordinates lower upper bounding box coordinates table. 
     * @return appropriate Node from tree.
     */
    N createNode(Tree tree, N parent, List<N> listChildren, List<Envelope> listEntries, double ...coordinates);
}
