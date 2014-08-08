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

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.Closeable;
import java.io.IOException;

/**
 * Define a generic Tree.
 *
 * @author Rémi Maréchal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public interface Tree<E> extends Closeable {

    /**
     * Find all {@code Integer} tree identifiers, from each stored datas which intersect {@code regionSearch} parameter.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: if no result found, an empty table is return.</strong>
     * </font></blockquote>
     * 
     * @param regionSearch Define area of search.
     * @return integer table which contain all tree identifier from selected data.
     * @throws StoreIndexException if pblem during search on stored file.
     * @see AbstractTree#treeIdentifier
     */
    int[] searchID(final Envelope regionSearch) throws StoreIndexException;
    
    /**
     * Find all {@code Integer} tree identifiers, from each stored datas which 
     * intersect {@code regionSearch} parameter and return an appropriate {@code Iterator} to travel them.
     * 
     * @param regionSearch Define area of search.
     * @return Iterator on each tree identifier search results.
     * @throws StoreIndexException if regionSearch own NaN coordinates value or during reading first result. 
     */
    TreeIdentifierIterator search(final Envelope regionSearch) throws StoreIndexException;
    
    /**
     * Insert an Object into Rtree.
     *
     * @param object
     * @throws StoreIndexException if problem during reading writing element on file. 
     */
    void insert(final E object) throws StoreIndexException;
    
    /**
     * Find an object define by user and remove it from RTree. 
     * 
     * @param object which will be removed.
     * @return true if object as been correctly remove else false.
     * @throws StoreIndexException if impossible to found its treeIdentifier from TreeElementMapper object.
     * @see TreeElementMapper#getTreeIdentifier(java.lang.Object) 
     */
    boolean remove(final E object) throws StoreIndexException;

    boolean remove(final int entry, Envelope entryEnvelope) throws StoreIndexException;

    /**
     * flush all streams use to store RTree on hard drive.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: Method has no impact if RTree is not an FileRTree instance.</strong>
     * </font></blockquote>
     * 
     * @throws StoreIndexException if problem during buffer writing.
     */
    void flush() throws StoreIndexException;

    /**
     * Return {@link TreeElementMapper} use to store inserted data and their tree identifiers.
     * 
     * @return {@link TreeElementMapper} use to store inserted data and their tree identifiers.
     */
    TreeElementMapper<E> getTreeElementMapper();
    
    /**
     * @return maximum element number authorized by tree cells.
     */
    int getMaxElements();

    /**
     * Return Tree trunk {@link Node}.<br/>
     * May return {@code null} if Tree is empty.
     * 
     * @return Tree trunk.
     */
    Node getRoot();
    
    /**
     * Affect a new root {@code Node}.
     *
     * @param root new root.
     */
    void setRoot(final Node root) throws StoreIndexException;

    /**
     * @return associate crs.
     */
    CoordinateReferenceSystem getCrs();

    /**
     * Clear tree (tree root Node become null).
     */
    void clear() throws StoreIndexException;

    /**
     * Return elements number within tree.
     */
    int getElementsNumber();

    /**
     * <blockquote><font size=-1>
     * <strong>NOTE: return {@code null} if tree root node is null.</strong>
     * </font></blockquote>
     *
     * @return all tree data set boundary.
     */
    double[] getExtent() throws StoreIndexException;
    
    /**
     * Close all streams use to store RTree on hard drive.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: Method has no impact if RTree is not an FileRTree instance.</strong>
     * </font></blockquote>
     * 
     * @throws StoreIndexException if problem during close stream.
     */
    void close() throws IOException;
   
    /**
     * Return true if {@link Tree} has already been closed else false.
     * 
     * @return true if {@link Tree} has already been closed else false.
     */
    boolean isClosed();
}
