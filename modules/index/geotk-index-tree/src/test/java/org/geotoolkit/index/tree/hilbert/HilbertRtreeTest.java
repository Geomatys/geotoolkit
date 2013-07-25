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
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.SpatialTreeTest;
import org.geotoolkit.index.tree.Tree;
import static org.geotoolkit.index.tree.DefaultTreeUtils.add;
import org.geotoolkit.index.tree.io.StoreIndexException;
import static org.junit.Assert.assertTrue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
 abstract class HilbertRtreeTest extends SpatialTreeTest {

    public HilbertRtreeTest(CoordinateReferenceSystem crs) throws StoreIndexException, IOException {
        super(crs);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkBoundaryNode(final Node node) throws IOException {
        double[] globalEnv = null;
        final int size = node.getChildCount();
        if (node.isLeaf()) {
            for (int i = 0; i < size; i++) {
                final Node cuCell = node.getChild(i);
                if (!cuCell.isEmpty()) {
                    for (int j = 0, s = cuCell.getCoordsCount(); j < s; j++) {
                        if (globalEnv == null) {
                            globalEnv = cuCell.getCoordinate(j).clone();
                        } else {
                            add(globalEnv, cuCell.getCoordinate(j));
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                final Node child = node.getChild(i);
                if (globalEnv == null) {
                    globalEnv = child.getBoundary().clone();
                } else {
                    add(globalEnv, child.getBoundary());
                }
            }
        }
        return Arrays.equals(globalEnv, node.getBoundary());
    }

    @Override
    protected boolean checkTreeElts(Tree tree) throws IOException {
        return (tree.getElementsNumber() == countEltsInHilbertNode(tree.getRoot(), 0));
    }
    
    private static int countEltsInHilbertNode(final Node candidate, int count) throws IOException {
        final int size = candidate.getChildCount();
        if (candidate.isLeaf()) {
            for (int i = 0; i < size; i++) {
                final Node cuCell = candidate.getChild(i);
                final int ccount = cuCell.getCoordsCount();
                assert  ccount == cuCell.getObjectCount() : "countEltsInHilbertNode : coord and object length must concord.";
                count += ccount;
            }
        } else {
            for (int i = 0; i < size; i++) {
                count = countEltsInHilbertNode(candidate.getChild(i), count);
            }
        }
        return count;
    }
    
    @Override
    protected boolean checkElementInsertion(final Node candidate, List<double[]> listRef) throws IOException {
        final int siz = candidate.getChildCount();
        if (candidate.isLeaf()) {
            for (int i = 0; i < siz; i++) {
                final Node cuCell = candidate.getChild(i);
                assert (cuCell.getCoordsCount() == cuCell.getObjectCount()) : "coord and object should be same length.";
                for (int j = 0, sc = cuCell.getCoordsCount(); j < sc; j++) {
                    Object cuObj = cuCell.getObject(j);
                    double[] coords = cuCell.getCoordinate(j);
                    assertTrue(Arrays.equals(coords, (double[])cuObj));
                    boolean found = false;
                    for (int il = 0, s = listRef.size(); il < s; il++) {
                        if (cuObj == listRef.get(il)) {
                            if (Arrays.equals(coords, listRef.get(il))) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) return false;
                }
            }
            return true;// we found all.
        } else {
            for (int i = 0; i < siz; i++) {
                boolean check = checkElementInsertion(candidate.getChild(i), listRef);
                if (!check) return false;
            }
            return true;
        }
    }
}
