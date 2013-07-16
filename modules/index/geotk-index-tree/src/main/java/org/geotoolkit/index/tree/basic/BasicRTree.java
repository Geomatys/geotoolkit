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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.geotoolkit.index.tree.AbstractTree;
import org.geotoolkit.index.tree.DefaultNodeFactory;
import org.geotoolkit.index.tree.DefaultTreeUtils;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.calculator.Calculator;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.NodeFactory;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.geotoolkit.index.tree.DefaultNode;
import static org.geotoolkit.index.tree.basic.SplitCase.LINEAR;
import static org.geotoolkit.index.tree.basic.SplitCase.QUADRATIC;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.index.tree.io.TreeElementMapper;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Create R-Tree (Basic)
 *
 * @author Rémi Marechal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class BasicRTree extends AbstractTree {

    private SplitCase choice;

    /**
     * Create a Basic R-Tree using default node factory.
     *
     * @param maxElements           : max elements number within each tree Node.
     * @param crs                   : associate coordinate system.
     * @param choice                : made to split "linear" or "quadratic".
     * @return Basic RTree.
     */
    public BasicRTree(final int maxElements, CoordinateReferenceSystem crs, final SplitCase choice, TreeElementMapper treeEltMap) {
        this(maxElements, crs, choice, DefaultNodeFactory.INSTANCE, treeEltMap);
    }

    /**
     * Create a Basic R-Tree.
     *
     * @param maxElements           : max elements number within each tree Node.
     * @param crs                   : associate coordinate system.
     * @param choice                : made to split "linear" or "quadratic".
     * @param nodefactory           : made to create tree {@code Node}.
     * @return Basic RTree.
     */
    @Deprecated
    public BasicRTree(final int maxElements, CoordinateReferenceSystem crs, final SplitCase choice, NodeFactory nodefactory, TreeElementMapper treeEltMap) {
        super(maxElements, crs, nodefactory, treeEltMap);
        this.choice = choice;
    }
        
    /**
     * {@inheritDoc }.
     */
    @Override
    public int[] searchID(double[] regionSearch) throws StoreIndexException {
        ArgumentChecks.ensureNonNull("search : region search", regionSearch);
        final Node root = getRoot();
        if (root != null && !root.isEmpty()) try {
            currentLength   = 100;
            currentPosition = 0;
            tabSearch       = new int[currentLength];
            nodeSearch(root, regionSearch);
            return Arrays.copyOf(tabSearch, currentPosition);
        } catch (IOException ex) {
            throw new StoreIndexException(ex);
        }
        return null;
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public void insert(Object object, double... coordinates) throws IllegalArgumentException, StoreIndexException {
        ArgumentChecks.ensureNonNull("insert : object", object);
        ArgumentChecks.ensureNonNull("insert : coordinates", coordinates);
//        super.insert(object, coordinates);
        super.eltCompteur++;
        final Node root       = getRoot();
        try {
            if (root == null || root.isEmpty()) {
                setRoot(createNode(this, null, null, new Object[]{object}, new double[][]{coordinates}));
            } else {
                nodeInsert(root, object, coordinates);
            }
        } catch (IOException ex) {
            throw new StoreIndexException(ex);
        }
    }
    
//    /**
//     * {@inheritDoc }.
//     */
//    @Override
//    public boolean delete(Object object, double... coordinates) throws IllegalArgumentException, StoreIndexException {
//        ArgumentChecks.ensureNonNull("delete : object", object);
//        ArgumentChecks.ensureNonNull("delete : coordinates", coordinates);
//        final Node root = getRoot();
//        if (root != null) try {
//            return deleteNode(root, object, coordinates);
//        } catch (IOException ex) {
//            throw new StoreIndexException(ex);
//        }
//        return false;
//    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean remove(Object object, double... coordinates) throws IllegalArgumentException, StoreIndexException {
        ArgumentChecks.ensureNonNull("remove : object", object);
        ArgumentChecks.ensureNonNull("remove : coordinates", coordinates);
        final Node root = getRoot();
        if (root != null) try {
            return removeNode(root, object, coordinates);
        } catch (IOException ex) {
            throw new StoreIndexException(ex);
        }
        return false;
    }
    
    /**
     * @return split case chosen to split.
     */
    public SplitCase getSplitCase() {
        return this.choice;
    }

    /**
     * Find all {@code Envelope} which intersect regionSearch parameter in {@code Node}.
     *
     * @param candidate current Node
     * @param regionSearch area of search.
     * @param result {@code List} where is add search resulting.
     */
    private void nodeSearch(final Node candidate, double... regionSearch) throws IOException{
        final double[] bound = candidate.getBoundary();
        if (bound != null) {
            if (regionSearch == null) {
                if (candidate.isLeaf()) {
                    for (int i = 0, l = candidate.getObjectCount(); i < l; i++) {// avec les count pour eviter les pointeurs null
                        if (currentPosition == currentLength) {
                            currentLength = currentLength << 1;
                            final int[] tabTemp = tabSearch;
                            tabSearch = new int[currentLength];
                            System.arraycopy(tabTemp, 0, tabSearch, 0, currentPosition);
                        }
                        tabSearch[currentPosition++] = (int) candidate.getObject(i);
                    }
                } else {
                    for (int i = 0, l = candidate.getChildCount(); i < l; i++) {//avec les counts pour eviter les pointeurs null
                        nodeSearch(candidate.getChild(i), null);
                    }
                }
            } else {
                final double[] rS = regionSearch.clone();
                if (contains(rS, bound, true)) {
                    nodeSearch(candidate, null);
                } else if (intersects(rS, bound, true)) {
                    if (candidate.isLeaf()) {
                        final int nbrElts = candidate.getCoordsCount();
                        //paranoiac assert
                        assert (nbrElts == candidate.getObjectCount()) : "search node : coordinate and object number should be similary";
                        for (int i = 0; i < nbrElts; i++) {
                            if (intersects(rS, candidate.getCoordinate(i), true)) {//a revoir pour passer un tableau a get coords et eviter la creation d'un nouveau
                                if (currentPosition == currentLength) {
                                    currentLength = currentLength << 1;
                                    final int[] tabTemp = tabSearch;
                                    tabSearch = new int[currentLength];
                                    System.arraycopy(tabTemp, 0, tabSearch, 0, currentPosition);
                                }
                                tabSearch[currentPosition++] = (int) candidate.getObject(i);
                            }
                        }
                    } else {
                        for (int i = 0, l = candidate.getChildCount(); i < l; i++) {
                            nodeSearch(candidate.getChild(i), regionSearch);
                        }
                    }
                }
            }
        }
    }
    
//    /**Find all {@code Envelope} which intersect regionSearch parameter in {@code Node}.
//     *
//     * @param candidate current Node
//     * @param regionSearch area of search.
//     * @param result {@code List} where is add search resulting.
//     */
//    private static TreeVisitorResult nodeSearch(final Node candidate, final TreeVisitor visitor, double... regionSearch) throws IOException{
//        final TreeVisitorResult tvr = visitor.filter(candidate);
//        if (isTerminate(tvr)) return tvr;
//        final double[] bound = candidate.getBoundary();
//        if (bound != null) {
//            if (regionSearch == null) {
//                if (candidate.isLeaf()) {
//                    for (int i = 0, l = candidate.getObjectCount(); i < l; i++) {// avec les count pour eviter les pointeurs null
//                        final TreeVisitorResult tvrTemp = visitor.visit(candidate.getObject(i));
//                        if (isTerminate(tvrTemp))   return tvrTemp;
//                        if (isSkipSibling(tvrTemp)) break;
//                    }
//                } else {
//                    if (!isSkipSubTree(tvr)) {
//                        for (int i = 0, l = candidate.getChildCount(); i < l; i++) {//avec les counts pour eviter les pointeurs null
//                            final TreeVisitorResult tvrTemp = nodeSearch(candidate.getChild(i), visitor, null);//filter
//                            if (isTerminate(tvrTemp))   return tvrTemp;
//                            if (isSkipSibling(tvrTemp)) break;
//                        }
//                    }
//                }
//            } else {
//                final double[] rS = regionSearch.clone();
//                if (contains(rS, bound, true)) {
//                    nodeSearch(candidate, visitor, null);
//                } else if (intersects(rS, bound, true)) {
//                    if (candidate.isLeaf()) {
//                        final int nbrElts = candidate.getCoordsCount();
//                        //paranoiac assert
//                        assert (nbrElts == candidate.getObjectCount()) : "search node : coordinate and object number should be similary";
//                        for (int i = 0, l = candidate.getCoordsCount(); i<l; i++) {
//                            TreeVisitorResult tvrTemp = null;
//                            if (intersects(rS, candidate.getCoordinate(i), true)) {//a revoir pour passer un tableau a get coords et eviter la creation d'un nouveau
//                                tvrTemp = visitor.visit(candidate.getObject(i));
//                            }
//                            if (tvrTemp != null) {
//                                if (isTerminate(tvrTemp))   return tvrTemp;
//                                if (isSkipSibling(tvrTemp)) break;
//                            }
//                        }
//                    } else {
//                        if (!isSkipSubTree(tvr)) {
//                            for (int i = 0, l = candidate.getChildCount(); i < l; i++) {
//                                final TreeVisitorResult tvrTemp = nodeSearch(candidate.getChild(i), visitor, regionSearch);
//                                if (isTerminate(tvrTemp))   return tvrTemp;
//                                if (isSkipSibling(tvrTemp)) break;
//                            }
//                        }
//                    }
//                }
//            }
//            return tvr;
//        }
//        return TreeVisitorResult.TERMINATE;
//    }

    /**
     * Insert new {@code Envelope} in branch and re-organize {@code Node} if it's necessary.
     *
     * <blockquote><font size=-1>
     * <strong>NOTE: insertion is in accordance with R*Tree properties.</strong>
     * </font></blockquote>
     *
     * @param candidate where to insert entry.
     * @param entry to add.
     * @throws IllegalArgumentException if {@code Node} candidate is null.
     * @throws IllegalArgumentException if {@code Envelope} entry is null.
     */
    private static void nodeInsert(final Node candidate, final Object object, double... coordinates) throws IllegalArgumentException, IOException{
        assert candidate.checkInternal() : "nodeInsert : begin : candidate not conform";
        if (candidate.isLeaf()) {
            assert candidate.getCoordsCount() <= candidate.getTree().getMaxElements() : "too of element before insertion in leaf";
            candidate.addElement(object, coordinates);
            assert candidate.getCoordsCount() <= candidate.getTree().getMaxElements() +1 : "too of element after insertion in leaf";
        } else {
            //paranoiac assert
            assert (candidate.getChildCount() != 0) : "nodeInsert : candidate isn't leaf and is empty";
            assert candidate.getChildCount() <= candidate.getTree().getMaxElements() : "too of element before insertion in Node";
            nodeInsert(chooseSubtree(candidate, coordinates), object, coordinates);
            assert candidate.getCoordsCount() <= candidate.getTree().getMaxElements() +1 : "too of element after insertion in Node";
            candidate.setBound(null);
        }
        
        if (!candidate.isLeaf()) {
            assert candidate.checkInternal() : "nodeInsert : just before split candidate not conform";
            int size = candidate.getChildCount();
            if (candidate.getChild(0).isLeaf()) {
                final int countCandidate = countElementsRecursively(candidate, 0);
                for (int i = 0; i<size-1; i++) {
                    for (int j = i+1; j<size; j++) {
                        final Node nodeA = candidate.getChild(i);
                        final Node nodeB = candidate.getChild(j);
                        if (intersects(nodeA.getBoundary(), nodeB.getBoundary(), false)
                                && nodeA.isLeaf() && nodeB.isLeaf()
                                && nodeA.getCoordsCount() > 1 && nodeB.getCoordsCount() > 1){
                            assert nodeA.getParent() == candidate && nodeB.getParent() == candidate:"before branchgrafting parent pointer";
                            branchGrafting(nodeA, nodeB);
                        assert nodeA.getParent() == candidate && nodeB.getParent() == candidate:"branchgrafting change parent pointer";
                        }
                    }
                }
                assert (countCandidate == countElementsRecursively(candidate, 0)) :"branch grafting eat elements";
            }

            for (int i = 0; i < size; i++) {
                if (DefaultTreeUtils.countElements(candidate.getChild(i)) > candidate.getTree().getMaxElements()) {
                    final int countCandidate = countElementsRecursively(candidate, 0);
                    final Node child = candidate.removeChild(i);
                    final List<Node> l = splitNode(child);
                    final Node l0 = l.get(0);
                    final Node l1 = l.get(1);
                    l0.setParent(candidate);
                    l1.setParent(candidate);
                    candidate.addChild(l0);
                    candidate.addChild(l1);
                    assert (countCandidate == countElementsRecursively(candidate, 0)) : "splitnode eat elements";
                }
            }
            assert candidate.checkInternal() : "nodeInsert : just after split candidate not conform";
        }

        if (candidate.getParent() == null) {
            if (DefaultTreeUtils.countElements(candidate) > candidate.getTree().getMaxElements()) {
                final int countCandidate = countElementsRecursively(candidate, 0);
                List<Node> l = splitNode(candidate);
                final Node l0 = l.get(0);
                final Node l1 = l.get(1);
                l0.setParent(candidate);
                l1.setParent(candidate);
                candidate.clear();
                candidate.addChild(l0);
                candidate.addChild(l1);
                assert (countCandidate == countElementsRecursively(candidate, 0)) : "splitnode with no parent eat elements";
            }
        }
        assert candidate.checkInternal() : "nodeInsert : at end candidate not conform";
    }

    /**
     * Exchange some entry(ies) between two nodes in aim to find best form with lesser overlaps.
     * Also branchGrafting will be able to avoid splitting node.
     *
     * @param nodeA Node
     * @param nodeB Node
     * @throws IllegalArgumentException if nodeA or nodeB are null.
     * @throws IllegalArgumentException if nodeA and nodeB have different "parent".
     * @throws IllegalArgumentException if nodeA or nodeB are not tree leaf.
     * @throws IllegalArgumentException if nodeA or nodeB, and their subnodes, don't contains some {@code Entry}.
     */
    private static void branchGrafting(final Node nodeA, final Node nodeB ) throws IllegalArgumentException, IOException {
        if(!nodeA.isLeaf() || !nodeB.isLeaf()) throw new IllegalArgumentException("branchGrafting : not leaf");
        assert nodeA.getParent() == nodeB.getParent() : "branchGrafting : NodeA and NodeB should have same parent.";
        assert nodeA.getParent().checkInternal()      : "branchGrafting : nodeA and B parent not conform.";
        assert nodeA.checkInternal()                  : "branchGrafting : at begin candidate not conform";
        assert nodeB.checkInternal()                  : "branchGrafting : at begin candidate not conform";
        final int nodeACount = nodeA.getCoordsCount();
        final int nodeBCount = nodeB.getCoordsCount();
        List<double[]> listCoords = new ArrayList<double[]>(nodeACount + nodeBCount);
        List<Object> listObjects = new ArrayList<Object>(nodeACount + nodeBCount);
        for (int i = 0; i < nodeACount; i++) {
            listCoords.add(nodeA.getCoordinate(i));
            listObjects.add(nodeA.getObject(i));
        }
        for (int i = 0; i < nodeBCount; i++) {
            listCoords.add(nodeB.getCoordinate(i));
            listObjects.add(nodeB.getObject(i));
        }
        if(listCoords.isEmpty()) throw new IllegalArgumentException("branchGrafting : empty list");
        final AbstractTree tree = (AbstractTree)nodeA.getTree();
        final int maxEltsPermit = tree.getMaxElements();
        final int size = listCoords.size();
        final double[] globalE = getEnvelopeMin(listCoords);
        final int dim = globalE.length >> 1;//decal bit
        double lengthDimRef = -1;
        int indexSplit = -1;
        //split along the longest span
        for(int i = 0; i < dim; i++) {
            double lengthDimTemp = getSpan(globalE, i);
            if(lengthDimTemp>lengthDimRef) {
                lengthDimRef = lengthDimTemp;
                indexSplit = i;
            }
        }
        assert indexSplit != -1 : "BranchGrafting : indexSplit not find"+indexSplit;
        final Calculator calc = tree.getCalculator();
        calc.sortList(indexSplit, true, listCoords, listObjects);
        double[] envB;
        final double[] envA = listCoords.get(0).clone();
        double overLapsRef = Double.POSITIVE_INFINITY;
        int index =-1;
        final int size04 = (int) Math.max(size * 0.4, 1);
        for (int cut = size04; cut < size-size04; cut++) {
            for (int i = 1; i<cut; i++) {
                add(envA, listCoords.get(i));
            }
            envB = listCoords.get(cut).clone();
            for (int i = cut + 1; i < size; i++) {
                add(envB, listCoords.get(i));
            }
            double overLapsTemp = calc.getOverlaps(envA, envB);
            if (overLapsTemp < overLapsRef) {
                overLapsRef = overLapsTemp;
                index = cut;
            }
        }
        
        //index not wrong a split is better.
        if (index > maxEltsPermit || (size-index) > maxEltsPermit) return;
        
        nodeA.clear();
        nodeB.clear();
        for (int i = 0; i < index; i++) {
            nodeA.addElement(listObjects.get(i), listCoords.get(i));
        }
        for (int i = index; i < size; i++) {
            nodeB.addElement(listObjects.get(i), listCoords.get(i));
        }
        assert nodeA.getParent() == nodeB.getParent() : "branchGrafting : NodeA and NodeB should have same parent.";
        assert nodeA.getParent().checkInternal()      : "branchGrafting : nodeA and B parent not conform.";
        assert nodeA.checkInternal()                  : "branchGrafting : at end candidate not conform";
        assert nodeB.checkInternal()                  : "branchGrafting : at end candidate not conform";
    }
    
    /**
     * Split a overflow {@code Node} in accordance with R-Tree properties.
     *
     * @param candidate {@code Node} to Split.
     * @throws IllegalArgumentException if candidate is null.
     * @throws IllegalArgumentException if candidate elements number is lesser 2.
     * @return {@code Node} List which contains two {@code Node} (split result of candidate).
     */
    private static List<Node> splitNode(final Node candidate) throws IllegalArgumentException, IOException {
        ArgumentChecks.ensureNonNull("splitNode : candidate", candidate);
        if (DefaultTreeUtils.countElements(candidate) < 2) 
            throw new IllegalArgumentException("not enought elements within " + candidate + " to split.");
        assert candidate.checkInternal() : "splitNode : at begin candidate not conform";
        final int countCandidate = countElementsRecursively(candidate, 0);
        final Tree tree = candidate.getTree();
        final Calculator calc = tree.getCalculator();
        final int maxElmnts = tree.getMaxElements();
        boolean leaf = candidate.isLeaf();
        
        Object s1 = null;
        Object s2 = null;
        if (leaf) {
            assert(candidate.getChildCount() == 0):"candidate should have no child";
            assert (candidate.getChildren() == null) :"candidate children should be null";
        } else {
            assert(candidate.getCoordsCount() == 0) :"candidate should have no coord or objects";
        }
        
        Object[] objects = null;
        Object[] coordOrNode;
        if (leaf) {
            final int cCount = candidate.getCoordsCount();
            coordOrNode = new double[cCount][];
            System.arraycopy(candidate.getCoordinates(), 0, coordOrNode, 0, cCount);
            
            objects = new Object[cCount];
            System.arraycopy(candidate.getObjects(), 0, objects, 0, cCount);
        } else {
            final int chCount = candidate.getChildCount();
            coordOrNode = new Node[chCount];
            System.arraycopy(candidate.getChildren(), 0, coordOrNode, 0, chCount);
        }
        
        Object leafO1 = null;
        Object leafO2 = null;
        
        double refValue = Double.NEGATIVE_INFINITY;
        double tempValue;
        int index1 = 0;
        int index2 = 0;
        
        final int countCoords = candidate.getCoordsCount();
        assert (countCoords == candidate.getObjectCount()) :"splitNode : coordinate and object table should have same length.";
        int length = (leaf) ? candidate.getCoordsCount() : candidate.getChildCount();
        switch (((BasicRTree) tree).getSplitCase()) {
            case LINEAR: {
                for (int i = 0; i < length - 1; i++) {
                    for (int j = i + 1; j < length; j++) {
                        tempValue = (leaf) ? calc.getDistanceEnvelope((double[]) coordOrNode[i], (double[])coordOrNode[j])
                                           : calc.getDistanceEnvelope(((Node) coordOrNode[i]).getBoundary(), ((Node) coordOrNode[j]).getBoundary());
                        if (tempValue > refValue) {
                            s1     = coordOrNode[i];
                            s2     = coordOrNode[j];
                            if (leaf) {
                                leafO1 = objects[i];
                                leafO2 = objects[j];
                            }
                            index1 = i;
                            index2 = j;
                            refValue = tempValue;
                        }
                    }
                }
            }
            break;

            case QUADRATIC: {
                double[] rectGlobal, bound1, bound2;
                for (int i = 0; i < length - 1; i++) {
                    for (int j = i + 1; j < length; j++) {
                        if (leaf) {
                            bound1 = (double[]) coordOrNode[i];
                            bound2 = (double[]) coordOrNode[j];
                        } else {
                            bound1 = ((Node) coordOrNode[i]).getBoundary();
                            bound2 = ((Node) coordOrNode[j]).getBoundary();
                        }
                        rectGlobal = bound1.clone();
                        add(rectGlobal, bound2);
                        tempValue  = calc.getSpace(rectGlobal) - calc.getSpace(bound1) - calc.getSpace(bound2);
                        if (tempValue > refValue) {
                            s1     = coordOrNode[i];
                            s2     = coordOrNode[j];
                            if (leaf) {
                                leafO1 = objects[i];
                                leafO2 = objects[j];
                            }
                            index1 = i;
                            index2 = j;
                            refValue = tempValue;
                        }
                    }
                }
            }
            break;
        }
        
        assert (s1 != null && s2 != null) : "s1 || s2 == null";
        if (leaf) assert (leafO1 != null && leafO2 != null) :"leaf01 || leaf02 == null";
        
        int maxid = Math.max(index1, index2);
        System.arraycopy(coordOrNode, maxid+1, coordOrNode, maxid, coordOrNode.length-maxid-1);
        coordOrNode[coordOrNode.length-1] = null;
        
        int minid = Math.min(index1, index2);
        System.arraycopy(coordOrNode, minid+1, coordOrNode, minid, coordOrNode.length-minid-1);
        coordOrNode[coordOrNode.length-1] = null;
        length -= 2;
        
        if (leaf) {
            System.arraycopy(objects, maxid+1, objects, maxid, objects.length-maxid-1);
            objects[objects.length-1] = null;
            System.arraycopy(objects, minid+1, objects, minid, objects.length-minid-1);
            objects[objects.length-1] = null;
        }
        double[] r1Temp, r2Temp;
        Node result1, result2;
        double demimaxE = maxElmnts / 3.0;
        demimaxE = Math.max(demimaxE, 1);
        
        //result1 attributs
        Node[] result1Children;
        int r1ChCount = 0;
        double[][] result1Coordinates;
        int r1EltCount = 0;
        Object[] result1Object;
        
        //result2 attributs
        Node[] result2Children;
        int r2ChCount = 0;
        double[][] result2Coordinates;
        int r2EltCount = 0;
        Object[] result2Object;
        
        //initialize
        if (leaf) {
            result1Children = null;
            result1Coordinates = new double[length+1][];
            result1Object = new Object[length+1];
            result2Children = null;
            result2Coordinates = new double[length +1][];
            result2Object = new Object[length+1];
            //add s1 s2
            result1Coordinates[r1EltCount] = (double[])s1;
            result1Object[r1EltCount++] = leafO1;
            result2Coordinates[r2EltCount] = (double[])s2;
            result2Object[r2EltCount++] = leafO2;
        } else {
            result1Children = new Node[length+1];
            result1Coordinates = null;
            result1Object = null;
            result2Children = new Node[length+1];
            result2Coordinates = null;
            result2Object = null;
            //add s1 s2
            result1Children[r1ChCount++] = (Node)s1;
            result2Children[r2ChCount++] = (Node)s2;
        }

        for (int i = 0, s = length; i < s; i++) {
            Object ent = coordOrNode[i];
            if (leaf) {
                r1Temp = ((double[])s1).clone();
                add(r1Temp, (double[]) ent);
                r2Temp = ((double[])s2).clone();
                add(r2Temp, (double[]) ent);
            } else {
                r1Temp = ((Node)s1).getBoundary().clone();
                add(r1Temp, ((Node)ent).getBoundary());
                r2Temp = ((Node)s2).getBoundary().clone();
                add(r2Temp, ((Node)ent).getBoundary());
            }

            double area1 = calc.getSpace(r1Temp);
            double area2 = calc.getSpace(r2Temp);
            int r1nbE = (leaf) ? r1EltCount : r1ChCount;
            int r2nbE = (leaf) ? r2EltCount : r2ChCount;
            if (area1 < area2) {
                if (r2nbE <= demimaxE && r1nbE > demimaxE) {
                    if (leaf) {
                        result2Coordinates[r2EltCount] = (double[])ent;
                        result2Object[r2EltCount++] = objects[i];
                    }else{
                        result2Children[r2ChCount++] = (Node)ent;
                    }
                } else {
                    if (leaf) {
                        result1Coordinates[r1EltCount] = (double[])ent;
                        result1Object[r1EltCount++] = objects[i];
                    }else{
                        result1Children[r1ChCount++] = (Node)ent;
                    }
                }
            } else if (area1 == area2) {
                if (r1nbE < r2nbE) {
                    if (leaf) {
                        result1Coordinates[r1EltCount] = (double[])ent;
                        result1Object[r1EltCount++] = objects[i];
                    }else{
                        result1Children[r1ChCount++] = (Node)ent;
                    }
                } else {
                    if (leaf) {
                        result2Coordinates[r2EltCount] = (double[])ent;
                        result2Object[r2EltCount++] = objects[i];
                    }else{
                        result2Children[r2ChCount++] = (Node)ent;
                    }
                }
            } else {
                if (r1nbE <= demimaxE && r2nbE > demimaxE) {
                    if (leaf) {
                        result1Coordinates[r1EltCount] = (double[])ent;
                        result1Object[r1EltCount++] = objects[i];
                    }else{
                        result1Children[r1ChCount++] = (Node)ent;
                    }
                } else {
                    if (leaf) {
                        result2Coordinates[r2EltCount] = (double[])ent;
                        result2Object[r2EltCount++] = objects[i];
                    }else{
                        result2Children[r2ChCount++] = (Node)ent;
                    }
                }
            }
        }
        
//        if (r1EltCount > candidate.getTree().getMaxElements() || r2EltCount > candidate.getTree().getMaxElements()) {
//            System.out.println("");
//        }
        
        if (leaf) {
            result1Object = Arrays.copyOf(result1Object, r1EltCount);
            result1Coordinates = Arrays.copyOf(result1Coordinates, r1EltCount);
            result1 = tree.createNode(tree, null, null, result1Object, result1Coordinates);
            
            result2Object = Arrays.copyOf(result2Object, r2EltCount);
            result2Coordinates = Arrays.copyOf(result2Coordinates, r2EltCount);
            result2 = tree.createNode(tree, null, null, result2Object, result2Coordinates);
            assert (countCandidate == (countElementsRecursively(result1, 0)+countElementsRecursively(result2, 0))) : "sum splitnode is leaf";
        } else {
            if (r1ChCount == 1) {
                result1 = result1Children[0];
                result1.setParent(null);
                //paranoiac set parent
                if (!result1.isLeaf()) {
                    final int nbChild = result1.getChildCount();
                    for (int i = 0; i < nbChild; i++) {
                        result1.getChild(i).setParent(result1);
                    }
                }
            } else {
                result1Children = Arrays.copyOf(result1Children, r1ChCount);
                result1 = tree.createNode(tree, null, result1Children, null, null);
            }
            if (r2ChCount == 1) {
                result2 = result2Children[0];
                result2.setParent(null);
                //paranoiac set parent
                if (!result2.isLeaf()) {
                    final int nbChild = result2.getChildCount();
                    for (int i = 0; i < nbChild; i++) {
                        result2.getChild(i).setParent(result2);
                    }
                }
            } else {
                result2Children = Arrays.copyOf(result2Children, r2ChCount);
                result2 = tree.createNode(tree, null, result2Children, null, null);
            }
            
            assert (countCandidate == (countElementsRecursively(result1, 0)+countElementsRecursively(result2, 0))) : "sum splitnode is node";
        }
        return UnmodifiableArrayList.wrap(new Node[] {result1, result2});
    }

    /**
     * Travel {@code Tree}, find {@code Entry} if it exist and delete it.
     *
     * <blockquote><font size=-1>
     * <strong>NOTE: Moreover {@code Tree} is condensate after a deletion to stay conform about R-Tree properties.</strong>
     * </font></blockquote>
     *
     * @param candidate {@code Node}  where to delete.
     * @param entry {@code Envelope} to delete.
     * @throws IllegalArgumentException if candidate or entry is null.
     * @return true if entry is find and deleted else false.
     */
    private static boolean deleteNode(final Node candidate, final Object object, final double... coordinate) throws IllegalArgumentException, StoreIndexException, IOException {
        ArgumentChecks.ensureNonNull("DeleteNode : Node candidate", candidate);
        ArgumentChecks.ensureNonNull("DeleteNode : Object candidate", object);
        ArgumentChecks.ensureNonNull("DeleteNode : double[] coordinate", coordinate);
        assert candidate.checkInternal() : "deleteNode : candidate not conform";
        if (intersects(candidate.getBoundary(), coordinate, true)) {
            if (candidate.isLeaf()) {
                final int countElts = candidate.getObjectCount();
                assert (countElts == candidate.getCoordsCount()) :"removeNode : coordinate and object table should have same length.";
                boolean removed = false;
                for (int i = countElts - 1; i >= 0; i--) {
                    if (Arrays.equals(candidate.getCoordinate(i), coordinate)) {
                        if (candidate.getObject(i).equals(object)
                         /*&& Arrays.equals(candidate.getCoordinate(i), coordinate)*/) {
                            removed = true;
                            candidate.removeCoordinate(i);
                            candidate.removeObject(i);
                            break;
                        } 
                    } 
                }
                if (removed) {
                    final AbstractTree tree = ((AbstractTree)candidate.getTree());
                    tree.setElementsNumber(tree.getElementsNumber()-1);
                    trim(candidate);
                    return true;
                }
            } else {
                final int countChild = candidate.getChildCount();
                for (int i = 0; i < countChild; i++) {
                    final boolean removed = deleteNode(candidate.getChild(i), object, coordinate);
                    if (removed) return true;
                }
            }
            assert candidate.checkInternal() : "deleteNode : after delete and trim candidate not conform";
        }
        return false;
    }
    
    /**
     * Travel {@code Tree}, find {@code Entry} if it exist and delete it from reference.
     *
     * <blockquote><font size=-1>
     * <strong>NOTE: Moreover {@code Tree} is condensate after a deletion to stay conform about R-Tree properties.</strong>
     * </font></blockquote>
     *
     * @param candidate {@code Node}  where to delete.
     * @param entry {@code Envelope} to delete.
     * @throws IllegalArgumentException if candidate or entry is null.
     * @return true if entry is find and deleted else false.
     */
    private static boolean removeNode(final Node candidate, final Object object, final double... coordinate) throws IllegalArgumentException, StoreIndexException, IOException{
        ArgumentChecks.ensureNonNull("removeNode : Node candidate", candidate);
        ArgumentChecks.ensureNonNull("removeNode : Object object", object);
        ArgumentChecks.ensureNonNull("removeNode : double[] coordinate", coordinate);
        if(intersects(candidate.getBoundary(), coordinate, true)){
            if (candidate.isLeaf()) {
                final int countElts = candidate.getObjectCount();
                assert (countElts == candidate.getCoordsCount()) :"removeNode : coordinate and object table should have same length.";
                boolean removed = false;
                for (int i = countElts - 1; i >= 0; i--) {
                    if (candidate.getObject(i).equals(object)// a modifier avec les integer
                     && Arrays.equals(candidate.getCoordinate(i), coordinate)) {
                        removed = true;
                        candidate.removeCoordinate(i);
                        candidate.removeObject(i);
                        break;
                    }
                }
                if (removed) {
                    final AbstractTree tree = ((AbstractTree)candidate.getTree());
                    tree.setElementsNumber(tree.getElementsNumber()-1);
                    trim(candidate);
                    return true;
                }
            } else {
                for (int i = 0, s = candidate.getChildCount(); i < s; i++) {
                    final boolean removed = removeNode(candidate.getChild(i), object, coordinate);
                    if (removed) return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Condense R-Tree.
     *
     * Condense made, travel up from leaf to tree trunk.
     *
     * @param candidate {@code Node} to begin condense.
     * @throws IllegalArgumentException if candidate is null.
     */
    private static void trim(final Node candidate) throws IllegalArgumentException, IOException, StoreIndexException {
        ArgumentChecks.ensureNonNull("trim : Node candidate", candidate);
        List<double[]> reinsertListCoords = null;
        List<Object> reinsertListObjects = null;
        final AbstractTree tree = ((AbstractTree)candidate.getTree());
        if (!candidate.isLeaf()) {
            for(int i = candidate.getChildCount() - 1; i >= 0; i--) {
                final Node currentChild = candidate.getChild(i);
                // empty child
                if (currentChild.isEmpty()) {
                    candidate.removeChild(i);
                } else { // check another conditions
                    if (currentChild.isLeaf()) {
                        // paranoiac assert
                        final int countCoords = currentChild.getCoordsCount();
                        assert (countCoords == currentChild.getObjectCount()) :"trim : candidate child should have coord and object with same length.";
                        // stored elements number < max elements / 3
                        if (countCoords <= tree.getMaxElements() / 3) {
                            if (reinsertListCoords == null) {
                                reinsertListCoords = new LinkedList<double[]>();
                                reinsertListObjects = new LinkedList<Object>();
                            }
                            for (int ic = 0; ic < countCoords; ic++) {
                                reinsertListCoords.add(currentChild.getCoordinate(ic));
                                reinsertListObjects.add(currentChild.getObject(ic));
                            }
                            tree.setElementsNumber(tree.getElementsNumber() - countCoords);
                            currentChild.clear();
                            candidate.removeChild(i);
                        }
                    } else {
                        // child have a single sub-child
                        if (currentChild.getChildCount() == 1) {
                            final Node reinsertNode = currentChild.removeChild(0);
                            currentChild.clear();
                            candidate.removeChild(i);
                            reinsertNode.setParent(candidate);
                            candidate.addChild(reinsertNode);
                        }
                    }
                }
            }
        }
        if (candidate.getParent() != null) trim (candidate.getParent());
        if (reinsertListCoords != null) {
            assert (reinsertListObjects != null) : "trim : listObjects should not be null.";
            final int reSize = reinsertListCoords.size();
            assert (reSize == reinsertListObjects.size()) :"reinsertLists should have same size";
            for (int i = 0; i < reSize; i++) {
                tree.insert(reinsertListObjects.get(i), reinsertListCoords.get(i));
            }
        } else {
            assert (reinsertListObjects == null) : "trim : listObjects should be null.";
        }
    }
    
    /**
     * Find appropriate {@code Node} to insert {@code Envelope} entry.
     * To define appropriate Node, criterion are :
     *      - require minimum area enlargement to cover shape.
     *      - or put into {@code Node} with lesser elements number in case of area equals.
     *
     * @param children List of {@code Node}.
     * @param entry {@code Envelope} to add.
     * @throws IllegalArgumentException if children or entry are null.
     * @throws IllegalArgumentException if children is empty.
     * @return {@code Node} which is appropriate to contain shape.
     */
    private static Node chooseSubtree(final Node candidate, final double... coordinates) throws IOException {
        ArgumentChecks.ensureNonNull("chooseSubtree : candidate", candidate);
        ArgumentChecks.ensureNonNull("chooseSubtree : coordinates", coordinates);
        assert candidate.checkInternal() : "chooseSubtree : candidate not conform";
        final Calculator calc = candidate.getTree().getCalculator();
        final int childCount = candidate.getChildCount();
        if (childCount == 0) throw new IllegalArgumentException("chooseSubtree : children is empty");
        if (childCount == 1) return candidate.getChild(0);
        Node n = candidate.getChild(0);
        for (int i = 0; i < childCount; i++) {
            final Node nod = candidate.getChild(i);
            if (contains(nod.getBoundary(), coordinates, true)) return nod;
        }
        
        double[] addBound = n.getBoundary().clone();
        for(int i = 1; i < childCount; i++) {
            add(addBound, candidate.getChild(i).getBoundary());
        }
        double area = calc.getSpace(addBound);
        double nbElmt = DefaultTreeUtils.countElements(n);
        double areaTemp;
        for (int i = 0; i < childCount; i++) {
            final Node dn = candidate.getChild(i);
            final double[] rnod = dn.getBoundary().clone();
            add(rnod, coordinates);
            final int nbe = DefaultTreeUtils.countElements(dn);
            areaTemp = calc.getEnlargement(dn.getBoundary(), rnod);
            if (areaTemp < area) {
                n = dn;
                area = areaTemp;
                nbElmt = nbe;
            } else if (areaTemp== area) {
                if (nbe < nbElmt) {
                    n = dn;
                    area = areaTemp;
                    nbElmt = nbe;
                }
            }
        }
        assert candidate.checkInternal() : "chooseSubtree : candidate not conform";
        return n;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node createNode(Tree tree, Node parent, Node[] children, Object[] objects, double[][] coordinates) throws IOException {
        if (coordinates != null && ((coordinates[0].length % 2) != 0)) 
            throw new IllegalArgumentException("coordinate dimension is not correct");
        
        double[] bound = null;
        if (children != null) {
            assert (objects == null && coordinates == null) : "impossible to create node which contain Node and elements.";
            bound = children[0].getBoundary().clone();
            for (int i = 1, l = children.length; i < l; i++) {
                add(bound, children[i].getBoundary());
            }
        } else if (objects != null && coordinates != null) {
            assert (objects.length == coordinates.length) : "BasicRTree.createNode : object and coordinates tables should have same length.";
            bound = coordinates[0].clone();
            for (int i = 1, l = coordinates.length; i < l; i++) {
                add(bound, coordinates[i]);
            }
        }
        if (bound == null) return new DefaultNode(tree, parent, null, null, children, objects, coordinates);
        final int dim = bound.length >> 1;
        final double[] dp1Coords = new double[dim];
        final double[] dp2Coords = new double[dim];
        System.arraycopy(bound, 0, dp1Coords, 0, dim);
        System.arraycopy(bound, dim, dp2Coords, 0, dim);
        return new DefaultNode(tree, parent, null, null,children, objects, coordinates);
    }

    
}
