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
import org.geotoolkit.index.tree.io.TreeElementMapper;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Remi Marechal(Geomatys).
 */
public class FileBasicRTree<E> extends AbstractBasicRTree<E> {

    /**
     * Number to identify tree file.
     */
    public final static int MAGIC_NUMBER = 188047901;
    private final static double versionNumber = 0.1;

    public FileBasicRTree(final File outPut, final int maxElements, final CoordinateReferenceSystem crs, final SplitCase choice, final TreeElementMapper treeEltMap) throws StoreIndexException, IOException {
        super(new TreeAccessFile(outPut, MAGIC_NUMBER, versionNumber, maxElements, crs), maxElements, crs,choice, treeEltMap);
    }
}
