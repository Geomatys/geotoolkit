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
package org.geotoolkit.index.tree.hilbert;

import java.io.File;
import java.io.IOException;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link HilbertRTree} implementation which store all Nodes from Tree and others informations,
 * on hard drive at specified emplacement define by user. 
 *
 * @author Remi Marechal (Geomatys).
 */
public final class FileHilbertRTree<E> extends HilbertRTree<E> {
    /**
     * Number to identify tree file.
     */
    private final static int HILBERT_NUMBER    = 69669745;
    private final static double VERSION_NUMBER = 0.1;

    /**
     * Create a new {@link HilbertRTree} implementation which store Tree architecture on hard drive.<br/><br/>
     * 
     * Note : the default buffer length which read and write on hard drive  is 4096 {@code Byte}.
     * 
     * @param outPut File which contain path where Tree information will be stored.
     * @param maxElements maximum children value permit per Node.
     * @param hilbertOrder maximum hilbert order value permit for each tree leaf.
     * @param crs Tree {@link CoordinateReferenceSystem}.
     * @param treeEltMap object which store tree identifier and data.
     * @throws StoreIndexException
     * @throws IOException 
     * @see HilbertRTree
     * @see TreeElementMapper
     */
    public FileHilbertRTree(final File outPut, final int maxElements, final int hilbertOrder, 
            final CoordinateReferenceSystem crs, final TreeElementMapper<E> treeEltMap) throws StoreIndexException, IOException {
        super(new HilbertTreeAccessFile(outPut, HILBERT_NUMBER, VERSION_NUMBER, maxElements, hilbertOrder, crs), treeEltMap);
    }
    
    /**
     * Create a new {@link HilbertRTree} implementation which store Tree architecture on hard drive.<br/><br/>
     * 
     * Note : in this implementation user may choose length of buffer which read and write on hard drive.<br/>
     * User may increase reading and writing performance in function of the owned memory.
     * 
     * @param outPut File which contain path where Tree information will be stored.
     * @param maxElements maximum children value permit per Node.
     * @param hilbertOrder maximum hilbert order value permit for each tree leaf.
     * @param crs Tree {@link CoordinateReferenceSystem}.
     * @param treeEltMap object which store tree identifier and data.
     * @param byteBufferLength length in Byte unit of the buffer which read and write all Tree Node on hard disk by TreeAccess object.
     * @throws StoreIndexException
     * @throws IOException 
     * @see HilbertRTree
     * @see TreeElementMapper
     */
    public FileHilbertRTree(final File outPut, final int maxElements, final int hilbertOrder, final int bytebufferLength,
            final CoordinateReferenceSystem crs, final TreeElementMapper<E> treeEltMap) throws StoreIndexException, IOException {
        super(new HilbertTreeAccessFile(outPut, HILBERT_NUMBER, VERSION_NUMBER, maxElements, hilbertOrder, crs, bytebufferLength), treeEltMap);
    }
    
    /**
     * Create a {@link HilbertRTree} implementation from an already filled file which contain {@link HilbertRTree} architecture.<br/><br/>
     * 
     * Note : the default buffer length which read and write on hard drive is 4096 {@code Byte}.
     * 
     * @param input File already filled by old {@link HilbertRTree} implementation.
     * @param treeEltMap object which store tree identifier and data.
     * @throws IOException if problem during head reading from already filled file.
     * @throws StoreIndexException if file isn't already filled by {@link HilbertRTree} implementation.
     * @throws ClassNotFoundException if file doesn't exist.
     * @see HilbertRTree
     * @see TreeElementMapper
     */
    public FileHilbertRTree(final File input, final TreeElementMapper<E> treeEltMap) throws StoreIndexException, IOException, ClassNotFoundException {
        super(new HilbertTreeAccessFile(input, HILBERT_NUMBER, VERSION_NUMBER), treeEltMap);
    }
    
    /**
     * Create a {@link HilbertRTree} implementation from an already filled file which contain {@link HilbertRTree} architecture.<br/><br/>
     * 
     * Note : in this implementation user may choose length of buffer which read and write on hard drive.<br/>
     * User may increase reading and writing performance in function of the owned memory.
     * 
     * @param input File already filled by old {@link HilbertRTree} implementation.
     * @param treeEltMap object which store tree identifier and data.
     * @param byteBufferLength length in Byte unit of the buffer which read and write all Tree Node on hard disk by TreeAccess object.
     * @throws IOException if problem during head reading from already filled file.
     * @throws StoreIndexException if file isn't already filled by {@link HilbertRTree} implementation.
     * @throws ClassNotFoundException if file doesn't exist.
     * @see HilbertRTree
     * @see TreeElementMapper
     */
    public FileHilbertRTree(final File input, final TreeElementMapper<E> treeEltMap, final int bytebufferLength) throws StoreIndexException, IOException, ClassNotFoundException {
        super(new HilbertTreeAccessFile(input, HILBERT_NUMBER, VERSION_NUMBER, bytebufferLength), treeEltMap);
    }
}
