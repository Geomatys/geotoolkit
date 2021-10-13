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
package org.geotoolkit.index.tree.star;

import java.io.IOException;
import java.nio.file.Path;
import org.geotoolkit.internal.tree.TreeAccessFile;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.internal.tree.TreeUtilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link StarRTree} implementation which store all Nodes from Tree and others informations,
 * on hard drive at specified emplacement define by user.
 *
 * @author Rémi Marechal (Geomatys).
 * @see StarRTree
 */
public class FileStarRTree<E> extends StarRTree<E> {

    /**
     * Create a new {@link StarRTree} implementation which store Tree architecture into stream at {@link Path} location.<br><br>
     *
     * Note : the default buffer length which read and write on hard drive  is 4096 {@code Byte}.
     *
     * @param outPut File which contain path where Tree information will be stored.
     * @param maxElements maximum children value permit per Node.
     * @param crs Tree {@link CoordinateReferenceSystem}.
     * @param treeEltMap object which store tree identifier and data.
     * @throws StoreIndexException
     * @throws IOException if problem during head file writing.
     * @see StarRTree
     * @see TreeElementMapper
     */
    public FileStarRTree(final Path outPut, final int maxElements, final CoordinateReferenceSystem crs, final TreeElementMapper<E> treeEltMap) throws StoreIndexException, IOException {
        super(new TreeAccessFile(outPut, TreeUtilities.STAR_NUMBER, TreeUtilities.VERSION_NUMBER, maxElements, crs), treeEltMap);
    }

    /**
     * Create a new {@link StarRTree} implementation which store Tree architecture into stream at {@link Path} location.<br><br>
     *
     * Note : in this implementation user may choose length of buffer which read and write on hard drive.<br>
     * User may increase reading and writing performance in function of the owned memory.
     *
     * @param outPut File which contain path where Tree information will be stored.
     * @param maxElements maximum children value permit per Node.
     * @param crs Tree {@link CoordinateReferenceSystem}.
     * @param treeEltMap object which store tree identifier and data.
     * @param byteBufferLength length in Byte unit of the buffer which read and write all Tree Node on hard disk by TreeAccess object.
     * @throws StoreIndexException
     * @throws IOException
     * @see StarRTree
     * @see TreeElementMapper
     */
    public FileStarRTree(final Path outPut, final int maxElements, final CoordinateReferenceSystem crs, final TreeElementMapper<E> treeEltMap, final int byteBufferLength) throws StoreIndexException, IOException {
        super(new TreeAccessFile(outPut, TreeUtilities.STAR_NUMBER, TreeUtilities.VERSION_NUMBER, maxElements, crs), treeEltMap);
    }

    /**
     * Open a {@link StarRTree} implementation from an already filled file which contain {@link StarRTree} architecture.<br><br>
     *
     * Note : the default buffer length which read and write on hard drive is 4096 {@code Byte}.
     *
     * @param input File already filled by old {@link StarRTree} implementation.
     * @param treeEltMap object which store tree identifier and data.
     * @throws IOException if problem during head reading from already filled file.
     * @throws StoreIndexException if file isn't already filled by {@link StarRTree} implementation.
     * @see StarRTree
     * @see TreeElementMapper
     */
    public FileStarRTree(final Path input, final TreeElementMapper<E> treeEltMap) throws IOException, StoreIndexException {
        super(new TreeAccessFile(input, TreeUtilities.STAR_NUMBER, TreeUtilities.VERSION_NUMBER), treeEltMap);
    }

    /**
     * Open a {@link StarRTree} implementation from an already filled file which contain {@link StarRTree} architecture.<br><br>
     *
     * Note : in this implementation user may choose length of buffer which read and write on hard drive.<br>
     * User may increase reading and writing performance in function of the owned memory.
     *
     * @param input File already filled by old {@link StarRTree} implementation.
     * @param treeEltMap object which store tree identifier and data.
     * @param byteBufferLength length in Byte unit of the buffer which read and write all Tree Node on hard disk by TreeAccess object.
     * @throws IOException if problem during head reading from already filled file.
     * @throws StoreIndexException if file isn't already filled by {@link StarRTree} implementation.
     * @see StarRTree
     * @see TreeElementMapper
     */
    public FileStarRTree(final Path input, final TreeElementMapper<E> treeEltMap, final int byteBufferLength) throws IOException, StoreIndexException {
        super(new TreeAccessFile(input, TreeUtilities.STAR_NUMBER, TreeUtilities.VERSION_NUMBER), treeEltMap);
    }
}
