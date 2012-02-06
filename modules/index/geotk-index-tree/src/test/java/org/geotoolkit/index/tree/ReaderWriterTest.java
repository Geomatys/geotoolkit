/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.io.TreeReader;
import org.geotoolkit.index.tree.io.TreeWriter;
import org.geotoolkit.util.ArgumentChecks;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Create test suite to test Tree writer and reader. 
 * 
 * @author Rémi Marechal (Geomatys).
 */
public class ReaderWriterTest {

    private Tree treeRef, treeTest;
    private final File fil = new File("tree.bin");
    private final List<Shape> lData = new ArrayList<Shape>();

    public ReaderWriterTest() {
        fil.deleteOnExit();
        for (int j = -120; j <= 120; j += 4) {
            for (int i = -200; i <= 200; i += 4) {
                lData.add(new Ellipse2D.Double(i, j, 1, 1));
            }
        }
    }

    /**
     * Test suite on (Basic) R-Tree.
     * 
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    @Test
    public void basicRTreeTest() throws IOException, ClassNotFoundException {
        setBasicRTree();
        TreeWriter.write(treeRef, fil);
        TreeReader.read(treeTest, fil);
        testTree();
    }

    /**
     * Test suite on R*Tree.
     * 
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    @Test
    public void starRTreeTest() throws IOException, ClassNotFoundException {
        setStarRTree();
        TreeWriter.write(treeRef, fil);
        TreeReader.read(treeTest, fil);
        testTree();
    }

    /**
     * Test suite on Hilbert R-Tree.
     * 
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    @Test
    public void hilbertRTreeTest() throws IOException, ClassNotFoundException {
        setHilbertRTree();
        TreeWriter.write(treeRef, fil);
        TreeReader.read(treeTest, fil);
        testTree();
    }

    @Test
    public void multiTest() throws IOException, ClassNotFoundException {
        final TreeWriter treeW = new TreeWriter();
        final TreeReader treeR = new TreeReader();

        setBasicRTree();
        treeW.setOutput(fil);
        treeW.write(treeRef);
        treeW.dispose();
        treeW.reset();
        treeR.setInput(fil);
        treeR.read(treeTest);
        treeR.dispose();
        treeR.reset();
        testTree();

        setStarRTree();
        treeW.setOutput(fil);
        treeW.write(treeRef);
        treeW.dispose();
        treeW.reset();
        treeR.setInput(fil);
        treeR.read(treeTest);
        treeR.dispose();
        treeR.reset();
        testTree();

        setHilbertRTree();
        treeW.setOutput(fil);
        treeW.write(treeRef);
        treeW.dispose();
        treeW.reset();
        treeR.setInput(fil);
        treeR.read(treeTest);
        treeR.dispose();
        treeR.reset();
        testTree();
    }

    /**
     * Affect (Basic) R-Tree on two tree test.
     */
    private void setBasicRTree() {
        treeRef = TreeFactory.createBasicRTree2D(SplitCase.LINEAR, 4);
        treeTest = TreeFactory.createBasicRTree2D(SplitCase.LINEAR, 4);
        insert();
    }

    /**
     * Affect R*Tree on two tree test.
     */
    private void setStarRTree() {
        treeRef = TreeFactory.createStarRTree2D(4);
        treeTest = TreeFactory.createStarRTree2D(4);
        insert();
    }

    /**
     * Affect Hilbert RTree on two tree test.
     */
    private void setHilbertRTree() {
        treeRef = TreeFactory.createHilbertRTree2D(4, 2);
        treeTest = TreeFactory.createHilbertRTree2D(4, 2);
        insert();
    }

    /**
     * Shuffle entries data list and insert in treeRef.
     */
    private void insert() {
        ArgumentChecks.ensureNonNull("insert : lData", lData);
        Collections.shuffle(lData);
        for (Shape shape : lData) {
            treeRef.insert(shape);
        }
    }

    /**
     * Test suite to compare two RTree.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: Test criterion are : - same node number.
     *                                    - trees contains same leafs.
     *                                    - trees contains same entries.</strong> 
     * </font></blockquote>
     * 
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    private void testTree() throws IOException, ClassNotFoundException {
        ArgumentChecks.ensureNonNull("testTree : treeRef", treeRef);
        ArgumentChecks.ensureNonNull("testTree : treeTest", treeTest);
        final List<Shape> listSearchTreeRef = new ArrayList<Shape>();
        final List<Shape> listSearchTreeTest = new ArrayList<Shape>();
        treeRef.search(((Node2D)treeRef.getRoot()).getBoundary(), listSearchTreeRef);
        treeTest.search(((Node2D)treeTest.getRoot()).getBoundary(), listSearchTreeTest);
        assertTrue(compareList(listSearchTreeRef, listSearchTreeTest));
        assertTrue(countAllNode(treeRef) == countAllNode(treeTest));
        assertTrue(compareListLeaf(getAllLeaf(treeRef), getAllLeaf(treeTest)));
    }

    /**
     * Find and enumerate all tree node.
     * 
     * @param tree
     * @return tree node number.
     */
    private int countAllNode(final Tree tree) {
        ArgumentChecks.ensureNonNull("countAllNode : tree", tree);
        int count = 0;
        countNode((Node)tree.getRoot(), count);
        return count;
    }

    /**
     * Increment count for each node parameter.
     * 
     * @param node
     * @param count 
     */
    private void countNode(final Node node, int count) {
        ArgumentChecks.ensureNonNull("countNode : node", node);
        count++;
        for (Node nod : (List<Node>)node.getChildren()) {
            countNode(nod, count);
        }
    }

    /**
     * Find all tree Leaf
     * 
     * @param tree
     * @return leaf list.
     */
    private List<Node2D> getAllLeaf(final Tree tree) {
        ArgumentChecks.ensureNonNull("getAllLeaf : tree", tree);
        final List<Node2D> listLeaf = new ArrayList<Node2D>();
        getLeaf((Node2D)tree.getRoot(), listLeaf);
        return listLeaf;
    }

    /**
     * Check if {@code node} passed in parameter is a leaf.
     * if it is true node is added in parameter {@code listLeaf}.
     * 
     * @param node to study
     * @param listLeaf 
     */
    private void getLeaf(final Node2D node, final List<Node2D> listLeaf) {
        ArgumentChecks.ensureNonNull("getLeaf : node", node);
        ArgumentChecks.ensureNonNull("getLeaf : listLeaf", listLeaf);
        if (node.isLeaf()) {
            listLeaf.add(node);
        }
        for (Node2D nod : node.getChildren()) {
            getLeaf(nod, listLeaf);
        }
    }

    /**
     * Compare 2 {@code Node2D} lists.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: return {@code true} if listTreeRef and listTreeTest are empty.</strong> 
     * </font></blockquote>
     * 
     * @param listTreeRef
     * @param listTreeTest
     * @throws IllegalArgumentException if listTreeRef or listTreeTest is null.
     * @return true if listTreeRef contains same elements from listTreeTest.
     */
    private boolean compareListLeaf(final List<Node2D> listTreeRef, final List<Node2D> listTreeTest) {
        ArgumentChecks.ensureNonNull("compareListLeaf : listTreeRef", listTreeRef);
        ArgumentChecks.ensureNonNull("compareListLeaf : listTreeTest", listTreeTest);

        if (listTreeRef.isEmpty() && listTreeTest.isEmpty()) {
            return true;
        }

        if (listTreeRef.size() != listTreeTest.size()) {
            return false;
        }
        boolean test = false;
        for (Node2D nod : listTreeRef) {
            for (Node2D no : listTreeTest) {
                if (compareLeaf(nod, no)) {
                    test = true;
                }
            }
            if (!test) {
                return false;
            }
            test = false;
        }
        return true;
    }

    /**
     * Test suite to compare two "leaf" ({@Node2D}).
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: Test based on this criterion : - same boundary.
     *                                              - same entries (Shape) number within each of them.
     *                                              - they contain same entries.
     *                                              Moreover entries order is not compare.</strong> 
     * </font></blockquote>
     * 
     * @param nodeA
     * @param nodeB
     * @return true if 3 assertion are verified else false.
     */
    private boolean compareLeaf(final Node2D nodeA, final Node2D nodeB) {
        ArgumentChecks.ensureNonNull("compareLeaf : nodeA", nodeA);
        ArgumentChecks.ensureNonNull("compareLeaf : nodeB", nodeB);
        if (!nodeA.isLeaf() || !nodeB.isLeaf()) {
            throw new IllegalArgumentException("compareLeaf : you must compare two leaf");
        }

        if (!nodeA.getBoundary().getBounds2D().equals(nodeB.getBoundary().getBounds2D())) {
            return false;
        }
        final List<Shape> listA = new ArrayList<Shape>();
        final List<Shape> listB = new ArrayList<Shape>();

        final List<Node2D> lupA = (List<Node2D>) nodeA.getUserProperty("cells");
        final List<Node2D> lupB = (List<Node2D>) nodeB.getUserProperty("cells");

        if (lupA != null && !lupA.isEmpty()) {
            for (Node2D nod : lupA) {
                listA.addAll(nod.getEntries());
            }
        }

        if (lupB != null && !lupB.isEmpty()) {
            for (Node2D nod : lupB) {
                listB.addAll(nod.getEntries());
            }
        }

        listA.addAll(nodeA.getEntries());
        listB.addAll(nodeB.getEntries());
        return compareList(listA, listB);
    }

    /**
     * Compare 2 lists elements.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: return {@code true} if listA and listB are empty.</strong> 
     * </font></blockquote>
     * 
     * @param listA
     * @param listB
     * @throws IllegalArgumentException if listA or ListB is null.
     * @return true if listA contains same elements from listB.
     */
    protected boolean compareList(final List<Shape> listA, final List<Shape> listB) {
        ArgumentChecks.ensureNonNull("compareList : listA", listA);
        ArgumentChecks.ensureNonNull("compareList : listB", listB);

        if (listA.size() != listB.size()) {
            return false;
        }

        if (listA.isEmpty() && listB.isEmpty()) {
            return true;
        }

        boolean shapequals = false;
        for (Shape shs : listA) {
            for (Shape shr : listB) {
                if (shs.equals(shr)) {
                    shapequals = true;
                }
            }
            if (!shapequals) {
                return false;
            }
            shapequals = false;
        }
        return true;
    }
}
