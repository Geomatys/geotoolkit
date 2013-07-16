///*
// *    Geotoolkit.org - An Open Source Java GIS Toolkit
// *    http://www.geotoolkit.org
// *
// *    (C) 2009-2012, Geomatys
// *
// *    This library is free software; you can redistribute it and/or
// *    modify it under the terms of the GNU Lesser General Public
// *    License as published by the Free Software Foundation;
// *    version 2.1 of the License.
// *
// *    This library is distributed in the hope that it will be useful,
// *    but WITHOUT ANY WARRANTY; without even the implied warranty of
// *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// *    Lesser General Public License for more details.
// */
//package org.geotoolkit.index.tree.io;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import org.apache.sis.geometry.GeneralEnvelope;
//import org.geotoolkit.index.tree.basic.BasicRTree;
//import org.geotoolkit.index.tree.basic.SplitCase;
//import org.geotoolkit.index.tree.hilbert.HilbertRTree;
//import org.geotoolkit.index.tree.star.StarRTree;
//import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
//import org.apache.sis.util.ArgumentChecks;
//import org.geotoolkit.index.tree.Node;
//import org.geotoolkit.index.tree.TestedEnvelope3D;
//import org.geotoolkit.index.tree.Tree;
//import static org.junit.Assert.assertTrue;
//import org.junit.Test;
//import org.opengis.geometry.Envelope;
//import org.opengis.referencing.operation.TransformException;
//
///**
// * Create test suite to test Tree writer and reader.
// *
// * @author Rémi Marechal (Geomatys).
// */
//public class ReaderWriterTest {
//
//    private Tree treeRef, treeTest;
//    private final File fil = new File("tree.bin");
//    private final List<GeneralEnvelope> lData = new ArrayList<GeneralEnvelope>();
//
//    public ReaderWriterTest() {
//        fil.deleteOnExit();
//        for (int j = -120; j <= 120; j += 4) {
//            for (int i = -200; i <= 200; i += 4) {
//                final TestedEnvelope3D ge = new TestedEnvelope3D(DefaultEngineeringCRS.CARTESIAN_3D);
//                ge.setEnvelope(i, j, 20, i, j, 20);
//                lData.add(ge);
//            }
//        }
//    }
//
//    /**
//     * Test suite on (Basic) R-Tree.
//     *
//     * @throws IOException
//     * @throws ClassNotFoundException
//     */
//    @Test
//    public void basicRTreeTest() throws StoreIndexException, IOException, TransformException, ClassNotFoundException{
//        setBasicRTree();
//        TreeWriter.write(treeRef, fil);
//        TreeReader.read(treeTest, fil);
//        testTree();
//    }
//
//    /**
//     * Test suite on R*Tree.
//     *
//     * @throws IOException
//     * @throws ClassNotFoundException
//     */
//    @Test
//    public void starRTreeTest() throws StoreIndexException, IOException, ClassNotFoundException, TransformException {
//        setStarRTree();
//        TreeWriter.write(treeRef, fil);
//        TreeReader.read(treeTest, fil);
//        testTree();
//    }
//
//    /**
//     * Test suite on Hilbert R-Tree.
//     *
//     * @throws IOException
//     * @throws ClassNotFoundException
//     */
//    @Test
//    public void hilbertRTreeTest() throws StoreIndexException, IOException, ClassNotFoundException, TransformException {
//        setHilbertRTree();
//        TreeWriter.write(treeRef, fil);
//        TreeReader.read(treeTest, fil);
//        testTree();
//    }
//
//    @Test
//    public void multiTest() throws StoreIndexException, IOException, ClassNotFoundException, TransformException {
//        final TreeWriter treeW = new TreeWriter();
//        final TreeReader treeR = new TreeReader();
//
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
//
//        setStarRTree();
//        treeW.setOutput(fil);
//        treeW.write(treeRef);
//        treeW.dispose();
//        treeW.reset();
//        treeR.setInput(fil);
//        treeR.read(treeTest);
//        treeR.dispose();
//        treeR.reset();
//        testTree();
//
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
//    }
//
//    /**
//     * Affect (Basic) R-Tree on two tree test.
//     */
//    private void setBasicRTree() throws StoreIndexException, IOException {
//        treeRef  = new BasicRTree(4, DefaultEngineeringCRS.CARTESIAN_3D, SplitCase.LINEAR);
//        treeTest = new BasicRTree(4, DefaultEngineeringCRS.CARTESIAN_3D, SplitCase.LINEAR);
//        insert();
//    }
//
//    /**
//     * Affect R*Tree on two tree test.
//     */
//    private void setStarRTree() throws StoreIndexException, IOException {
//        treeRef  = new StarRTree(4, DefaultEngineeringCRS.CARTESIAN_3D);
//        treeTest = new StarRTree(4, DefaultEngineeringCRS.CARTESIAN_3D);
//        insert();
//    }
//
//    /**
//     * Affect Hilbert RTree on two tree test.
//     */
//    private void setHilbertRTree() throws StoreIndexException, IOException{
//        treeRef  = new HilbertRTree(4, 2, DefaultEngineeringCRS.CARTESIAN_3D);
//        treeTest = new HilbertRTree(4, 2, DefaultEngineeringCRS.CARTESIAN_3D);
//        insert();
//    }
//
//    /**
//     * Shuffle entries data list and insert in treeRef.
//     */
//    private void insert() throws StoreIndexException, IOException {
//        ArgumentChecks.ensureNonNull("insert : lData", lData);
//        Collections.shuffle(lData);
//        for (GeneralEnvelope shape : lData) {
//            treeRef.insert(shape);
//        }
//    }
//
//    /**
//     * Test suite to compare two RTree.
//     *
//     * <blockquote><font size=-1> <strong>NOTE: Test criterion are : - same node
//     * number. - trees contains same leafs. - trees contains same
//     * entries.</strong> </font></blockquote>
//     *
//     * @throws IOException
//     * @throws ClassNotFoundException
//     */
//    private void testTree() throws StoreIndexException, IOException {
//        ArgumentChecks.ensureNonNull("testTree : treeRef", treeRef);
//        ArgumentChecks.ensureNonNull("testTree : treeTest", treeTest);
//        final List listSearchTreeRef = new ArrayList<Envelope>();
//        final List listSearchTreeTest = new ArrayList<Envelope>();
//        treeRef.search(((Node) treeRef.getRoot()).getBoundary(), new DefaultTreeVisitor(listSearchTreeRef));
//        treeTest.search(((Node) treeTest.getRoot()).getBoundary(), new DefaultTreeVisitor(listSearchTreeTest));
//        assertTrue(compareList(listSearchTreeRef, listSearchTreeTest));
//        assertTrue(countAllNode(treeRef) == countAllNode(treeTest));
//        assertTrue(compareListLeaf(getAllLeaf(treeRef), getAllLeaf(treeTest)));
//    }
//
//    /**
//     * Find and enumerate all tree node.
//     *
//     * @param tree
//     * @return tree node number.
//     */
//    private int countAllNode(final Tree tree) {
//        ArgumentChecks.ensureNonNull("countAllNode : tree", tree);
//        int count = 0;
//        countNode((Node) tree.getRoot(), count);
//        return count;
//    }
//
//    /**
//     * Increment count for each node parameter.
//     *
//     * @param node
//     * @param count
//     */
//    private void countNode(final Node node, int count) {
//        ArgumentChecks.ensureNonNull("countNode : node", node);
//        count++;
//        for (int i = 0, s = node.getChildCount(); i < s; i++) {
//            countNode(node.getChild(i), count);
//        }
//    }
//
//    /**
//     * Find all tree Leaf
//     *
//     * @param tree
//     * @return leaf list.
//     */
//    private List<Node> getAllLeaf(final Tree tree) throws IOException {
//        ArgumentChecks.ensureNonNull("getAllLeaf : tree", tree);
//        final List<Node> listLeaf = new ArrayList<Node>();
//        getLeaf((Node) tree.getRoot(), listLeaf);
//        return listLeaf;
//    }
//
//    /**
//     * Check if {@code node} passed in parameter is a leaf. if it is true node
//     * is added in parameter {@code listLeaf}.
//     *
//     * @param node to study
//     * @param listLeaf
//     */
//    private void getLeaf(final Node node, final List<Node> listLeaf) throws IOException {
//        ArgumentChecks.ensureNonNull("getLeaf : node", node);
//        ArgumentChecks.ensureNonNull("getLeaf : listLeaf", listLeaf);
//        if (node.isLeaf()) {
//            listLeaf.add(node);
//        } else {
//            for (int i = 0, s = node.getChildCount(); i < s; i++) {
//                getLeaf(node.getChild(i), listLeaf);
//            }
//        }
//    }
//
//    /**
//     * Compare 2 {@code AbstractNode} lists.
//     *
//     * <blockquote><font size=-1> <strong>NOTE: return {@code true} if
//     * listTreeRef and listTreeTest are empty.</strong> </font></blockquote>
//     *
//     * @param listTreeRef
//     * @param listTreeTest
//     * @throws IllegalArgumentException if listTreeRef or listTreeTest is null.
//     * @return true if listTreeRef contains same elements from listTreeTest.
//     */
//    private boolean compareListLeaf(final List<Node> listTreeRef, final List<Node> listTreeTest) throws IOException {
//        ArgumentChecks.ensureNonNull("compareListLeaf : listTreeRef", listTreeRef);
//        ArgumentChecks.ensureNonNull("compareListLeaf : listTreeTest", listTreeTest);
//
//        if (listTreeRef.isEmpty() && listTreeTest.isEmpty()) return true;
//        if (listTreeRef.size() != listTreeTest.size()) return false;
//
//        boolean test = false;
//        for (Node nod : listTreeRef) {
//            for (Node no : listTreeTest) {
//                if (compareLeaf(nod, no)) {
//                    test = true;
//                    break;
//                }
//            }
//            if (!test) return false;
//            test = false;
//        }
//        return true;
//    }
//
//    /**
//     * Test suite to compare two "leaf" ({@AbstractNode}).
//     *
//     * <blockquote><font size=-1> <strong>NOTE: Test based on this criterion : -
//     * same boundary. - same entries (Shape) number within each of them. - they
//     * contain same entries. Moreover entries order is not compare.</strong>
//     * </font></blockquote>
//     *
//     * @param nodeA
//     * @param nodeB
//     * @return true if 3 assertion are verified else false.
//     */
//    private boolean compareLeaf(final Node nodeA, final Node nodeB) throws IOException {
//        ArgumentChecks.ensureNonNull("compareLeaf : nodeA", nodeA);
//        ArgumentChecks.ensureNonNull("compareLeaf : nodeB", nodeB);
//
//        if (!nodeA.isLeaf() || !nodeB.isLeaf()) throw new IllegalArgumentException("compareLeaf : you must compare two leaf");
//        if (!Arrays.equals(nodeA.getBoundary(), nodeB.getBoundary())) return false;
//
//        final List listObjA = new ArrayList();
//        final List listCoordsA = new ArrayList();
//        final List listObjB = new ArrayList();
//        final List listCoordsB = new ArrayList();
//        
//        for (int i = 0, s = nodeA.getChildCount(); i < s; i++) {
//            final Node cuCell = nodeA.getChild(i);
//            if (!cuCell.isEmpty()) {
//                listCoordsA.addAll(Arrays.asList(Arrays.copyOf(cuCell.getCoordinates(), cuCell.getCoordsCount())));
//                listObjA.addAll(Arrays.asList(Arrays.copyOf(cuCell.getObjects(), cuCell.getObjectCount())));
//            }
//        }
//        for (int i = 0, s = nodeB.getChildCount(); i < s; i++) {
//            final Node cuCell = nodeB.getChild(i);
//            if (!cuCell.isEmpty()) {
//                listCoordsB.addAll(Arrays.asList(Arrays.copyOf(cuCell.getCoordinates(), cuCell.getCoordsCount())));
//                listObjB.addAll(Arrays.asList(Arrays.copyOf(cuCell.getObjects(), cuCell.getObjectCount())));
//            }
//        }
//        final int nodeACoorCount = nodeA.getCoordsCount();
//        if (nodeACoorCount != 0) {
//            listCoordsA.addAll(Arrays.asList(Arrays.copyOf(nodeA.getCoordinates(), nodeACoorCount)));
//            listObjA.addAll(Arrays.asList(Arrays.copyOf(nodeA.getObjects(), nodeACoorCount)));
//        }
//        final int nodeBCoordCount = nodeB.getCoordsCount();
//        if (nodeBCoordCount != 0) {
//            listCoordsB.addAll(Arrays.asList(Arrays.copyOf(nodeB.getCoordinates(), nodeBCoordCount)));
//            listObjB.addAll(Arrays.asList(Arrays.copyOf(nodeB.getObjects(), nodeBCoordCount)));
//        }   
//        return compareList(listCoordsA, listCoordsB) && compareList(listObjA, listObjB);
//    }
//
//    /**
//     * Compare 2 lists elements.
//     *
//     * <blockquote><font size=-1> <strong>NOTE: return {@code true} if listA and
//     * listB are empty.</strong> </font></blockquote>
//     *
//     * @param listA
//     * @param listB
//     * @throws IllegalArgumentException if listA or ListB is null.
//     * @return true if listA contains same elements from listB.
//     */
//    protected boolean compareList(final List listA, final List listB) {
//        ArgumentChecks.ensureNonNull("compareList : listA", listA);
//        ArgumentChecks.ensureNonNull("compareList : listB", listB);
//
//        if (listA.size() != listB.size()) return false;
//        if (listA.isEmpty() && listB.isEmpty()) return true;
//
//        boolean shapequals = false;
//        for (Object objA : listA) {
//            for (Object objB : listB) {
//                if (objB instanceof Envelope && objA instanceof Envelope) {
//                    final GeneralEnvelope envA = new GeneralEnvelope((Envelope)objA);
//                    if (envA.equals((Envelope)objB, 1E-9, false)) {
//                        shapequals = true;
//                    }
//                } else if (objB instanceof double[] && objA instanceof double[]) {
//                    if (Arrays.equals((double[])objA, (double[])objB)) {
//                        shapequals = true;
//                    }
//                } else {
//                    throw new IllegalArgumentException("you should compare object of type : double[] or Envelope.");
//                }
//            }
//            if (!shapequals) return false;
//            shapequals = false;
//        }
//        return true;
//    }
//}
