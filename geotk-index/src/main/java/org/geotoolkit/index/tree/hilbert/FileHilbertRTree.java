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
import java.nio.file.Path;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.internal.tree.TreeUtilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link HilbertRTree} implementation which store all Nodes from Tree and others informations,
 * on hard drive at specified emplacement define by user.
 *
 * @author Remi Marechal (Geomatys).
 */
public final class FileHilbertRTree<E> extends HilbertRTree<E> {

    /**
     * Create a new {@link HilbertRTree} implementation which store Tree architecture into a file at {@link Path} location.<br/><br/>
     *
     * Note : the default buffer length which read and write on hard drive  is 4096 {@code Byte}.
     *
     * @param outPut File which contain path where Tree information will be stored.
     * @param maxElements maximum children value permit per Node.
     * @param hilbertOrder maximum hilbert order value permit for each tree leaf.
     * @param crs Tree {@link CoordinateReferenceSystem}.
     * @param treeEltMap object which store tree identifier and data.
     * @throws StoreIndexException if problem during root Node affectation.
     * @throws IOException if problem during file head writing.
     * @see HilbertRTree
     * @see TreeElementMapper
     */
    public FileHilbertRTree(final Path outPut, final int maxElements, final int hilbertOrder,
            final CoordinateReferenceSystem crs, final TreeElementMapper<E> treeEltMap) throws StoreIndexException, IOException {
        super(new HilbertTreeAccessFile(outPut, TreeUtilities.HILBERT_NUMBER, TreeUtilities.VERSION_NUMBER, maxElements, hilbertOrder, crs), treeEltMap);
    }

    /**
     * Create a new {@link HilbertRTree} implementation which store Tree architecture into a file at {@link Path} location.<br/><br/>
     *
     * Note : in this implementation user may choose length of buffer which read and write on hard drive.<br/>
     * User may increase reading and writing performance in function of the owned memory.
     *
     * @param outPut path where Tree information will be stored.
     * @param maxElements maximum children value permit per Node.
     * @param hilbertOrder maximum hilbert order value permit for each tree leaf.
     * @param crs Tree {@link CoordinateReferenceSystem}.
     * @param treeEltMap object which store tree identifier and data.
     * @throws StoreIndexException if problem during root Node affectation.
     * @throws IOException if problem during file head writing.
     * @see HilbertRTree
     * @see TreeElementMapper
     */
    public FileHilbertRTree(final Path outPut, final int maxElements, final int hilbertOrder, final int bytebufferLength,
            final CoordinateReferenceSystem crs, final TreeElementMapper<E> treeEltMap) throws StoreIndexException, IOException {
        super(new HilbertTreeAccessFile(outPut, TreeUtilities.HILBERT_NUMBER, TreeUtilities.VERSION_NUMBER, maxElements, hilbertOrder, crs, bytebufferLength), treeEltMap);
    }

    /**
     * Open a {@link HilbertRTree} implementation from an already filled file from {@link Path} location
     * which contain {@link HilbertRTree} architecture.<br/><br/>
     *
     * Note : the default buffer length which read and write on hard drive is 4096 {@code Byte}.
     *
     * @param input File already filled by old {@link HilbertRTree} implementation.
     * @param treeEltMap object which store tree identifier and data.
     * @throws IOException if problem during head reading from already filled file.
     * @throws StoreIndexException if file isn't already filled by {@link HilbertRTree} implementation.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     * @see HilbertRTree
     * @see TreeElementMapper
     */
    public FileHilbertRTree(final Path input, final TreeElementMapper<E> treeEltMap) throws StoreIndexException, IOException, ClassNotFoundException {
        super(new HilbertTreeAccessFile(input, TreeUtilities.HILBERT_NUMBER, TreeUtilities.VERSION_NUMBER), treeEltMap);
    }

    /**
     * Open a {@link HilbertRTree} implementation from an already filled file from {@link Path} location,
     * which contain {@link HilbertRTree} architecture.<br/><br/>
     *
     * Note : in this implementation user may choose length of buffer which read and write on hard drive.<br/>
     * User may increase reading and writing performance in function of the owned memory.
     *
     * @param input File already filled by old {@link HilbertRTree} implementation.
     * @param treeEltMap object which store tree identifier and data.
     * @param bytebufferLength length in Byte unit of the buffer which read and write all Tree Node on hard disk by TreeAccess object.
     * @throws IOException if problem during head reading from already filled file.
     * @throws StoreIndexException if file isn't already filled by {@link HilbertRTree} implementation.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     * @see HilbertRTree
     * @see TreeElementMapper
     */
    public FileHilbertRTree(final Path input, final TreeElementMapper<E> treeEltMap, final int bytebufferLength) throws StoreIndexException, IOException, ClassNotFoundException {
        super(new HilbertTreeAccessFile(input, TreeUtilities.HILBERT_NUMBER, TreeUtilities.VERSION_NUMBER, bytebufferLength), treeEltMap);
    }
}
