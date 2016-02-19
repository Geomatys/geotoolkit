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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.geotoolkit.index.tree.AbstractTreeTest;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.StoreIndexException;
import static org.junit.Assert.assertTrue;
import static org.geotoolkit.internal.tree.TreeUtilities.add;

/**
 * Class to override some methods appropriate to HilbertRTree use.
 *
 * @author Remi Marechal (Geomatys)
 */
abstract class HilbertTest extends AbstractTreeTest {

    /**
     * Create a generic {@link HilbertRTree} test suite with {@link CoordinateReferenceSystem} define by user.
     *
     * @param crs
     */
    protected HilbertTest(final CoordinateReferenceSystem crs) throws IOException {
        super(crs);
    }

    /**
     * Create a generic {@link HilbertRTree} test suite.
     *
     * @param tree HilbertRTree which will be test.
     */
    protected HilbertTest(final HilbertRTree tree) throws IOException {
        super(tree);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void checkNode(Node node, List<double[]> listRef) throws StoreIndexException, IOException {
        final double[] nodeBoundary = node.getBoundary();
        double[] subNodeBound = null;
        if (node.isLeaf()) {
            int cellSibl = node.getChildId();
            while (cellSibl != 0) {
                final HilbertNode currentCell = (HilbertNode) tAF.readNode(cellSibl);
                assertTrue(currentCell.isCell());
                if (!currentCell.isEmpty()) {
                    final double[] currentCellBound = currentCell.getBoundary();
                    double[] subDataBound = null;
                    int dataSibl = currentCell.getChildId();
                    while (dataSibl != 0) {
                        final HilbertNode currentData = (HilbertNode) tAF.readNode(dataSibl);
                        if (subDataBound == null) {
                            subDataBound = currentData.getBoundary().clone();
                        } else {
                            subDataBound = add(subDataBound, currentData.getBoundary());
                        }
                        final int currentValue = - currentData.getChildId();
                        final int listId = currentValue -1;
                        assertTrue("bad ID = "+(currentValue)
                                +" expected : "+Arrays.toString(listRef.get(listId))
                                +" found : "+Arrays.toString(currentData.getBoundary()), Arrays.equals(currentData.getBoundary(), listRef.get(listId)));
                        dataSibl = currentData.getSiblingId();
                    }
                    assertTrue("boundary Cell should have a boundary equals from its data boundary sum : "
                            + "cell boundary = "+Arrays.toString(currentCellBound)
                            +" data boundary sum = "+Arrays.toString(subDataBound), Arrays.equals(currentCellBound, subDataBound));
                    if (subNodeBound == null) {
                        subNodeBound = currentCellBound.clone();
                    } else {
                        subNodeBound = add(subNodeBound, currentCellBound);
                    }
                }
                cellSibl = currentCell.getSiblingId();
            }
        } else {
            int sibl = node.getChildId();
            while (sibl != 0) {
                final Node currentChild = tAF.readNode(sibl);
                if (subNodeBound == null) {
                    subNodeBound = currentChild.getBoundary().clone();
                } else {
                    subNodeBound = add(subNodeBound, currentChild.getBoundary());
                }
                checkNode(currentChild, listRef);
                sibl = currentChild.getSiblingId();
            }
        }
        assertTrue("HilbertNode should have a boundary equals from its sub-Nodes boundary sum : "
                +" HilbertNode boundary = "+Arrays.toString(nodeBoundary)
                +"sub-nodes sum = "+Arrays.toString(subNodeBound), Arrays.equals(nodeBoundary, subNodeBound));
    }
}
