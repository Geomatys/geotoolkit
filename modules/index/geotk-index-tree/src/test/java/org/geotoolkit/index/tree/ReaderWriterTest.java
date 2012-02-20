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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.io.TreeReader;
import org.geotoolkit.index.tree.io.TreeWriter;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.geotoolkit.util.ArgumentChecks;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.referencing.operation.TransformException;

/**
 * Create test suite to test Tree writer and reader. 
 * 
 * @author Rémi Marechal (Geomatys).
 */
public class ReaderWriterTest {

    private Tree treeRef, treeTest;
    private final File fil = new File("tree.bin");
    private final List<GeneralEnvelope> lData = new ArrayList<GeneralEnvelope>();

    public ReaderWriterTest() {
        fil.deleteOnExit();
        for (int j = -120; j <= 120; j += 4) {
            for (int i = -200; i <= 200; i += 4) {
                final GeneralEnvelope ge = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_3D);
                ge.setEnvelope(i, j, 20, i, j, 20);
                lData.add(ge);
            }
        }
    }

//    /**
//     * Test suite on (Basic) R-Tree.
//     * 
//     * @throws IOException
//     * @throws ClassNotFoundException 
//     */
//    @Test
//    public void basicRTreeTest() throws IOException, ClassNotFoundException, TransformException {
//        setBasicRTree();
//        TreeWriter.write(treeRef, fil);
//        TreeReader.read(treeTest, fil);
//        testTree();
//    }

    /**
     * Test suite on R*Tree.
     * 
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    @Test
    public void starRTreeTest() throws IOException, ClassNotFoundException, TransformException {
        setStarRTree();
        TreeWriter.write(treeRef, fil);
        TreeReader.read(treeTest, fil);
        testTree();
    }

//    /**
//     * Test suite on Hilbert R-Tree.
//     * 
//     * @throws IOException
//     * @throws ClassNotFoundException 
//     */
//    @Test
//    public void hilbertRTreeTest() throws IOException, ClassNotFoundException, TransformException {
//        setHilbertRTree();
//        TreeWriter.write(treeRef, fil);
//        TreeReader.read(treeTest, fil);
//        testTree();
//    }

    @Test
    public void multiTest() throws IOException, ClassNotFoundException, TransformException {
        final TreeWriter treeW = new TreeWriter();
        final TreeReader treeR = new TreeReader();

//        setBasicRTree();
//        treeW.setOutput(fil);
//        treeW.write(treeRef);
//        treeW.dispose();
//        treeW.reset();
//        treeR.setInput(fil);
//        treeR.read(treeTest);
//        treeR.dispose();
//        treeR.reset();
//        testTree();

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

//        setHilbertRTree();
//        treeW.setOutput(fil);
//        treeW.write(treeRef);
//        treeW.dispose();
//        treeW.reset();
//        treeR.setInput(fil);
//        treeR.read(treeTest);
//        treeR.dispose();
//        treeR.reset();
//        testTree();
    }

    /**
     * Affect (Basic) R-Tree on two tree test.
     */
    private void setBasicRTree() throws TransformException {
        treeRef = TreeFactory.createBasicRTree2D(SplitCase.LINEAR, 4);
        treeTest = TreeFactory.createBasicRTree2D(SplitCase.LINEAR, 4);
        insert();
    }

    /**
     * Affect R*Tree on two tree test.
     */
    private void setStarRTree() throws TransformException {
        treeRef = TreeFactory.createStarRTree(4, DefaultEngineeringCRS.CARTESIAN_3D);
        treeTest = TreeFactory.createStarRTree(4, DefaultEngineeringCRS.CARTESIAN_3D);
        insert();
    }

    /**
     * Affect Hilbert RTree on two tree test.
     */
    private void setHilbertRTree() throws TransformException {
        treeRef = TreeFactory.createHilbertRTree2D(4, 2);
        treeTest = TreeFactory.createHilbertRTree2D(4, 2);
        insert();
    }

    /**
     * Shuffle entries data list and insert in treeRef.
     */
    private void insert() throws TransformException {
        ArgumentChecks.ensureNonNull("insert : lData", lData);
        Collections.shuffle(lData);
        for (GeneralEnvelope shape : lData) {
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
    private void testTree() throws IOException, ClassNotFoundException, TransformException {
        ArgumentChecks.ensureNonNull("testTree : treeRef", treeRef);
        ArgumentChecks.ensureNonNull("testTree : treeTest", treeTest);
        final List<GeneralEnvelope> listSearchTreeRef = new ArrayList<GeneralEnvelope>();
        final List<GeneralEnvelope> listSearchTreeTest = new ArrayList<GeneralEnvelope>();
        treeRef.search(((DefaultNode)treeRef.getRoot()).getBoundary(), listSearchTreeRef);
        treeTest.search(((DefaultNode)treeTest.getRoot()).getBoundary(), listSearchTreeTest);
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
    private List<DefaultNode> getAllLeaf(final Tree tree) {
        ArgumentChecks.ensureNonNull("getAllLeaf : tree", tree);
        final List<DefaultNode> listLeaf = new ArrayList<DefaultNode>();
        getLeaf((DefaultNode)tree.getRoot(), listLeaf);
        return listLeaf;
    }

    /**
     * Check if {@code node} passed in parameter is a leaf.
     * if it is true node is added in parameter {@code listLeaf}.
     * 
     * @param node to study
     * @param listLeaf 
     */
    private void getLeaf(final DefaultNode node, final List<DefaultNode> listLeaf) {
        ArgumentChecks.ensureNonNull("getLeaf : node", node);
        ArgumentChecks.ensureNonNull("getLeaf : listLeaf", listLeaf);
        if (node.isLeaf()) {
            listLeaf.add(node);
        }
        for (DefaultNode nod : node.getChildren()) {
            getLeaf(nod, listLeaf);
        }
    }

    /**
     * Compare 2 {@code DefaultNode} lists.
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
    private boolean compareListLeaf(final List<DefaultNode> listTreeRef, final List<DefaultNode> listTreeTest) {
        ArgumentChecks.ensureNonNull("compareListLeaf : listTreeRef", listTreeRef);
        ArgumentChecks.ensureNonNull("compareListLeaf : listTreeTest", listTreeTest);

        if (listTreeRef.isEmpty() && listTreeTest.isEmpty()) {
            return true;
        }

        if (listTreeRef.size() != listTreeTest.size()) {
            return false;
        }
        boolean test = false;
        for (DefaultNode nod : listTreeRef) {
            for (DefaultNode no : listTreeTest) {
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
     * Test suite to compare two "leaf" ({@DefaultNode}).
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
    private boolean compareLeaf(final DefaultNode nodeA, final DefaultNode nodeB) {
        ArgumentChecks.ensureNonNull("compareLeaf : nodeA", nodeA);
        ArgumentChecks.ensureNonNull("compareLeaf : nodeB", nodeB);
        if (!nodeA.isLeaf() || !nodeB.isLeaf()) {
            throw new IllegalArgumentException("compareLeaf : you must compare two leaf");
        }

        if (!nodeA.getBoundary().equals(nodeB.getBoundary(), 1E-9, false)) {
            return false;
        }
        final List<GeneralEnvelope> listA = new ArrayList<GeneralEnvelope>();
        final List<GeneralEnvelope> listB = new ArrayList<GeneralEnvelope>();

        final List<DefaultNode> lupA = (List<DefaultNode>) nodeA.getUserProperty("cells");
        final List<DefaultNode> lupB = (List<DefaultNode>) nodeB.getUserProperty("cells");

        if (lupA != null && !lupA.isEmpty()) {
            for (DefaultNode nod : lupA) {
                listA.addAll(nod.getEntries());
            }
        }

        if (lupB != null && !lupB.isEmpty()) {
            for (DefaultNode nod : lupB) {
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
    protected boolean compareList(final List<GeneralEnvelope> listA, final List<GeneralEnvelope> listB) {
        ArgumentChecks.ensureNonNull("compareList : listA", listA);
        ArgumentChecks.ensureNonNull("compareList : listB", listB);

        if (listA.size() != listB.size()) {
            return false;
        }

        if (listA.isEmpty() && listB.isEmpty()) {
            return true;
        }

        boolean shapequals = false;
        for (GeneralEnvelope shs : listA) {
            for (GeneralEnvelope shr : listB) {
                if (shs.equals(shr, 1E-9, false)) {
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
