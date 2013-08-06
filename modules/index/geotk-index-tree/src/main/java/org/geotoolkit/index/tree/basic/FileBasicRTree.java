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
package org.geotoolkit.index.tree.basic;

import java.io.File;
import java.io.IOException;
import org.geotoolkit.index.tree.access.TreeAccessFile;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.index.tree.mapper.TreeElementMapper;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * BasicRTree implementation which store all Nodes from Tree and others informations,
 * on hard disk at specified emplacement define by user. 
 * 
 * @author Remi Marechal(Geomatys).
 */
public class FileBasicRTree<E> extends BasicRTree<E> {

    /**
     * Number to identify tree file.
     */
    private final static int BASIC_NUMBER      = 188047901;
    private final static double VERSION_NUMBER = 0.1;

    /**
     * 
     * @param outPut File which contain path where Tree information will be stored.
     * @param maxElements 
     * @param crs
     * @param choice
     * @param treeEltMap
     * @throws StoreIndexException
     * @throws IOException 
     */
    public FileBasicRTree(final File outPut, final int maxElements, final CoordinateReferenceSystem crs,
            final SplitCase choice, final TreeElementMapper<E> treeEltMap) throws StoreIndexException, IOException {
        super(new TreeAccessFile(outPut, BASIC_NUMBER, VERSION_NUMBER, maxElements, crs), choice, treeEltMap);
    }
    
    /**
     * 
     * @param outPut
     * @param maxElements
     * @param crs
     * @param choice
     * @param treeEltMap
     * @param byteBufferLength length in Byte unit of the buffer which read and write all Tree Node on hard disk by TreeAccess object.
     * @throws StoreIndexException
     * @throws IOException 
     */
    public FileBasicRTree(final File outPut, final int maxElements, final CoordinateReferenceSystem crs, 
            final SplitCase choice, final TreeElementMapper<E> treeEltMap, final int byteBufferLength) throws StoreIndexException, IOException {
        super(new TreeAccessFile(outPut, BASIC_NUMBER, VERSION_NUMBER, maxElements, crs, byteBufferLength), choice, treeEltMap);
    }
    
    /**
     * 
     * @param input
     * @param choice
     * @param treeEltMap
     * @throws IOException
     * @throws StoreIndexException
     * @throws ClassNotFoundException 
     */
    public FileBasicRTree(final File input, final SplitCase choice, final TreeElementMapper<E> treeEltMap) throws IOException, StoreIndexException, ClassNotFoundException {
        super(new TreeAccessFile(input, BASIC_NUMBER, VERSION_NUMBER), choice, treeEltMap);
    } 
    
    /**
     * 
     * @param input
     * @param choice
     * @param treeEltMap
     * @param byteBufferLength length in Byte unit of the buffer which read and write all Tree Node on hard disk by TreeAccess object.
     * @throws IOException
     * @throws StoreIndexException
     * @throws ClassNotFoundException 
     */
    public FileBasicRTree(final File input, final SplitCase choice, final TreeElementMapper<E> treeEltMap, 
            final int byteBufferLength) throws IOException, StoreIndexException, ClassNotFoundException {
        super(new TreeAccessFile(input, BASIC_NUMBER, VERSION_NUMBER), choice, treeEltMap);
    } 
}
