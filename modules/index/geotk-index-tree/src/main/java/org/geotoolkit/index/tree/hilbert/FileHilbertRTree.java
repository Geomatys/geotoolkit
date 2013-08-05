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
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.index.tree.mapper.TreeElementMapper;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Remi Marechal (Geomatys).
 */
public class FileHilbertRTree<E> extends HilbertRTree<E> {
    /**
     * Number to identify tree file.
     */
    private final static int HILBERT_NUMBER    = 69669745;
    private final static double VERSION_NUMBER = 0.1;

    public FileHilbertRTree(final File outPut, final int maxElements, final int hilbertOrder, 
            final CoordinateReferenceSystem crs, TreeElementMapper treeEltMap) throws StoreIndexException, IOException {
        super(new HilbertTreeAccessFile(outPut, HILBERT_NUMBER, VERSION_NUMBER, maxElements, hilbertOrder, crs), treeEltMap);
    }
    
    public FileHilbertRTree(final File input, final TreeElementMapper treeEltMap) throws StoreIndexException, IOException, ClassNotFoundException {
        super(new HilbertTreeAccessFile(input, HILBERT_NUMBER, VERSION_NUMBER), treeEltMap);
    }
}
