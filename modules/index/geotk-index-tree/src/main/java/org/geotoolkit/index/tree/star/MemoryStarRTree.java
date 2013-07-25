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

import org.geotoolkit.index.tree.access.TreeAccessMemory;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.index.tree.mapper.TreeElementMapper;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Remi Marechal (Geomatys).
 */
public class MemoryStarRTree<E> extends AbstractStarRTree<E>{

    public MemoryStarRTree(int maxElements, CoordinateReferenceSystem crs, TreeElementMapper treeEltMap) throws StoreIndexException {
        super(new TreeAccessMemory(maxElements, crs), treeEltMap);
    }
}
