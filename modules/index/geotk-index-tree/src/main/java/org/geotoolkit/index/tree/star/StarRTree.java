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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.index.tree.AbstractTree;
import static org.geotoolkit.index.tree.TreeUtilities.*;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.access.TreeAccess;
import org.geotoolkit.index.tree.calculator.Calculator;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.index.tree.mapper.TreeElementMapper;

/**
 * Tree implementation.
 * R* Tree.
 *
 * @author Remi Marechal (Geomatys).
 */
public abstract class StarRTree<E> extends AbstractTree<E> {
    
    /**
     * In accordance with R*Tree properties.
     * To avoid unnecessary split permit to
     * reinsert some elements just one time.
     */
    boolean insertAgain = true;
    
    private final LinkedList<Object> listObjects  = new LinkedList<Object>();
    private final LinkedList<double[]> listCoords = new LinkedList<double[]>(); 
    
    boolean travelUpBeforeInsertAgain = false;
    
    
    public StarRTree(final TreeAccess treeAccess, final TreeElementMapper treeEltMap) throws StoreIndexException {
        super(treeAccess, treeAccess.getCRS(), treeEltMap);
        ArgumentChecks.ensureNonNull("Create AbstractBasicRTree : treeAF", treeAccess);
        ArgumentChecks.ensureNonNull("Create AbstractBasicRTree : CRS", crs);
        super.setRoot(treeAccess.getRoot());
        treeIdentifier = treeAccess.getTreeIdentifier();
    }
    
    
    /**
     * Get statement from re-insert state.
     *
     * @return true if it's permit to re-insert else false.
     */
    private boolean getIA() {
        return insertAgain;
    }

    /**
     * Affect statement to permit or not, re-insertion.
     * 
     * @param insertAgain
     */
    private void setIA(boolean insertAgain) {
        this.insertAgain = insertAgain;
    }
    
    @Override
    public void insert(Object object, double... coordinates) throws IllegalArgumentException, StoreIndexException {
//        super.insert(object, coordinates);
        try {
            eltCompteur++;
            Node root = getRoot();
            if (root == null || root.isEmpty()) {
                root = createNode(treeAccess, null, IS_LEAF, 0, 0, 0);
                root.addChild(createNode(treeAccess, coordinates, IS_DATA, 1, 0, -((Integer)object)));
                setRoot(root);
            } else {
                travelUpBeforeInsertAgain = false;
                final Node newRoot = nodeInsert(root, object, coordinates);
                if (newRoot != null) {
                    setRoot(newRoot);
                    treeAccess.writeNode((Node)newRoot);
                }
                /**
                 * Insert again. Property named Tree re-balancing. 
                 */
                if (travelUpBeforeInsertAgain) {
                    travelUpBeforeInsertAgain = false;
                    // insert again
                    final int siz = listCoords.size();
                    assert (siz == listObjects.size()) : "getElementAtMore33Percent : nodeInsert : lists should have same size.";
                    setIA(false);
                    final int nbrElt = getElementsNumber();
                    final int treeIdent = treeIdentifier; // gere quand root == null
                    for (int i = 0; i < siz; i++) {
                        assert remove(listObjects.get(i), listCoords.get(i));
                    }
                    for (int i = 0; i< siz; i++) {
                        insert(listObjects.get(i), listCoords.get(i));
                    }
                    setIA(true);
                    assert nbrElt == getElementsNumber() : "During Insert again element number within tree should not change.";
                    treeIdentifier = treeIdent;
                }
            }
        } catch (IOException ex) {
            throw new StoreIndexException(this.getClass().getName()+"Tree.insert(), impossible to add element.", ex);
        }
    }
    
    @Override
    protected Node nodeInsert(Node candidate, Object object, double... coordinates) throws IOException {
        assert candidate instanceof Node;
        Node fileCandidate = (Node) candidate;
        assert !fileCandidate.isData() : "nodeInsert : candidate should never be data type.";
        /**
         * During travel down recursively candidate parent may be modified.
         * When travel up recursively if candidate should be modified, get
         * new candidate object updated from sub-method.
         */
        Node subCandidateParent = null;
        if (fileCandidate.isLeaf()) {
            assert fileCandidate.checkInternal() : "nodeInsert : leaf before add.";
            fileCandidate.addChild(createNode(treeAccess, coordinates, IS_DATA, fileCandidate.getNodeId(), 0, -((Integer)object)));
            assert fileCandidate.checkInternal() : "nodeInsert : leaf after add.";
        } else {
            assert fileCandidate.checkInternal() : "nodeInsert : Node before insert.";
            subCandidateParent = (Node)nodeInsert(chooseSubtree(fileCandidate, coordinates), object, coordinates);
            add(fileCandidate.getBoundary(), coordinates);
        }
        
        /**
         * Currently candidate was modified from precedently sub-Insert() call.
         * Affect candidate object with new candidate from sub-Insert method.
         */
        if (subCandidateParent != null) {
            fileCandidate = subCandidateParent;
        }
        treeAccess.writeNode(fileCandidate); 
        if (travelUpBeforeInsertAgain) return null;
        assert fileCandidate.checkInternal() : "nodeInsert : after insert.";
        
        if (fileCandidate.getChildCount() > getMaxElements()) {
            /******************************** 33 % *****************************/
            if (getIA() && fileCandidate.isLeaf()) {
                listObjects.clear();
                listCoords.clear();
                getElementAtMore33PerCent(candidate, listObjects, listCoords);
                travelUpBeforeInsertAgain = true;
                return null;
            }
            assert fileCandidate.checkInternal() : "nodeInsert : after insert again element a 33% distance.";
            /*******************************************************************/
            
//            /*********************** Branch grafting **************************/
//            if (fileCandidate.isLeaf() && fileCandidate.getParentId() != 0) {
//                /**
//                 * We search to, travel candidate sibling from candidate parent first child, to last child.
//                 */
//                final FileNode candidateParent = tAF.readNode(fileCandidate.getParentId());
//                int sibl = candidateParent.getChildId();
//                final double[] candidateBound = fileCandidate.getBoundary();
//                while (sibl != 0) {
//                    if (sibl != fileCandidate.getNodeId()) {
//                        final FileNode cuSibling = tAF.readNode(sibl);
//                        assert cuSibling.checkInternal() : "candidate sibling.";
//                        if (intersects(candidateBound, cuSibling.getBoundary(), true
//                         && cuSibling.getChildCount() > 1)) {
//                            branchGrafting(fileCandidate, cuSibling);
//                            assert cuSibling.checkInternal() : "candidate sibling after branch grafting.";
//                            assert fileCandidate.checkInternal() : "candidate after branch grafting.";
//                            if (fileCandidate.getChildCount() <= tree.getMaxElements()) return null;
//                        }
//                        sibl = cuSibling.getSiblingId();
//                    } else {
//                        sibl = fileCandidate.getSiblingId();
//                    }
//                }
//            }
//            assert fileCandidate.checkInternal() : "nodeInsert : after Branch grafting.";
//            /******************************************************************/
            
            if (fileCandidate.getChildCount() > getMaxElements()) {
                // split
                final Node[] splitTable = splitNode(fileCandidate);
                final Node split1 = (Node)splitTable[0];
                final Node split2 = (Node)splitTable[1];

                final int candidateParentID = fileCandidate.getParentId();
                if (candidateParentID == 0) { // on est sur le noeud root
                    // on clear le candidate
                    assert fileCandidate.getSiblingId() == 0 : "nodeInsert : split root : root should not have sibling.";
                    fileCandidate.clear();
                    fileCandidate.setProperties(IS_OTHER);
                    fileCandidate.addChild(split1);
                    fileCandidate.addChild(split2);     
                    assert split1.checkInternal() : "nodeInsert : split1.";
                    assert split2.checkInternal() : "nodeInsert : split2.";
                    assert fileCandidate.checkInternal() : "nodeInsert : split root.";
                } else {
                    final Node parent = treeAccess.readNode(candidateParentID);
                    parent.removeChild(fileCandidate);
                    parent.addChild(split1);
                    parent.addChild(split2);
                    assert split1.checkInternal() : "nodeInsert : split1.";
                    assert split2.checkInternal() : "nodeInsert : split2.";
                    assert parent.checkInternal() : "nodeInsert : split node.";
                    /**
                     * Candidate parent is modified, return it to re-affect appropriate parent object in up-Insert().
                     */
                    return parent;
                }
            }
        }
        /**
         * If last travel up (on Root Node) and currently candidate was changed,
         * return currently Node else return null;
         */
        return (subCandidateParent != null && fileCandidate.getParentId() == 0) ? fileCandidate : null;
    }
    
    /**
     * Recover lesser 33% largest of {@code Node} candidate within it.
     *
     * @throws IllegalArgumentException if {@code Node} candidate is null.
     * @return all Entry within subNodes at more 33% largest of this {@code Node}.
     */
    private void getElementAtMore33PerCent(final Node candidate, LinkedList<Object> listObjects, final LinkedList<double[]> listCoords) throws IOException {
        ArgumentChecks.ensureNonNull("getElementAtMore33PerCent : candidate", candidate);
        ArgumentChecks.ensureNonNull("getElementAtMore33PerCent : listObjects", listObjects);
        ArgumentChecks.ensureNonNull("getElementAtMore33PerCent : listCoords", listCoords);
        assert candidate.checkInternal() : "getElementAtMore33PerCent : begin candidate not conform";
        final double[] canBound = candidate.getBoundary();
        final double[] candidateCentroid = getMedian(canBound);
        final double distPermit = calculator.getDistancePoint(getLowerCorner(canBound), getUpperCorner(canBound)) / 1.666666666;
        getElementAtMore33PerCent((Node)candidate, candidateCentroid, distPermit, listObjects, listCoords);
        assert candidate.checkInternal() : "getElementAtMore33PerCent : end candidate not conform";
    }
    
    /**
     * Recover lesser 33% largest of {@code Node} candidate within it.
     *
     * @throws IllegalArgumentException if {@code Node} candidate is null.
     * @return all Entry within subNodes at more 33% largest of this {@code Node}.
     */
    private void getElementAtMore33PerCent(final Node candidate, double[] candidateCentroid, double distancePermit, LinkedList<Object> listObjects, final LinkedList<double[]> listCoords) throws IOException {
        ArgumentChecks.ensureNonNull("getElementAtMore33PerCent : candidateCentroid", candidateCentroid);
        ArgumentChecks.ensureStrictlyPositive("getElementsAtMore33PerCent : distancePermit", distancePermit);
        assert candidate.checkInternal() : "getElementAtMore33PerCent : begin candidate not conform";
        if (candidate.isLeaf()) {
            int sibl = candidate.getChildId();
            while (sibl != 0) {
                final Node data = treeAccess.readNode(sibl); // voir pour aameliorer algo descendre juska data
                listObjects.add(-data.getChildId());
                listCoords.add(data.getBoundary());
                sibl = data.getSiblingId();
            }
        } else {
            int sibl = candidate.getChildId();
            while (sibl != 0) {
                final Node child = treeAccess.readNode(sibl);
                getElementAtMore33PerCent(child, candidateCentroid, distancePermit, listObjects, listCoords);
                sibl = child.getSiblingId();
            }
        }
        assert candidate.checkInternal() : "getElementAtMore33PerCent : begin candidate not conform";
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
    private void branchGrafting(final Node nodeA, final Node nodeB ) throws IllegalArgumentException, IOException {
        if(!nodeA.isLeaf() || !nodeB.isLeaf()) throw new IllegalArgumentException("branchGrafting : not leaf");
        assert nodeA.getParentId()== nodeB.getParentId(): "branchGrafting : NodeA and NodeB should have same parent.";
        assert treeAccess.readNode(nodeA.getParentId()).checkInternal() : "branchGrafting : nodeA and B parent not conform.";
        assert nodeA.checkInternal()                  : "branchGrafting : at begin candidate not conform";
        assert nodeB.checkInternal()                  : "branchGrafting : at begin candidate not conform";
        final int nodeACount = nodeA.getChildCount();
        final int nodeBCount = nodeB.getChildCount();
        final int size = nodeACount + nodeBCount;
        
        final List<Node> listFN = new ArrayList<Node>(size);
        final Node[] nodeAChildren = nodeA.getChildren();
        final Node[] nodeBChildren = nodeB.getChildren();
        
        final double[] globalE = nodeAChildren[0].getBoundary().clone();
        for (Node nod : nodeAChildren) {
            add(globalE, nod.getBoundary());
            listFN.add(nod);
        }
        for (Node nod : nodeBChildren) {
            add(globalE, nod.getBoundary());
            listFN.add(nod);
        }
        
        if(listFN.isEmpty()) throw new IllegalArgumentException("branchGrafting : empty list");
        final int maxEltsPermit = getMaxElements();
        final int dim           = globalE.length >> 1;
        double lengthDimRef     = -1;
        int indexSplit          = -1;
        //split along the longest span
        for(int i = 0; i < dim; i++) {
            double lengthDimTemp = getSpan(globalE, i);
            if (lengthDimTemp > lengthDimRef) {
                lengthDimRef = lengthDimTemp;
                indexSplit   = i;
            }
        }
        assert indexSplit != -1 : "BranchGrafting : indexSplit not find"+indexSplit;
        calculator.sortList(indexSplit, true, listFN);
        double[] envB, envA;
        double overLapsRef = Double.POSITIVE_INFINITY;
        int index = -1;
        final int size04 = (int) Math.max(size * 0.4, 1);
        for (int cut = size04; cut < size-size04; cut++) {
            envA = listFN.get(0).getBoundary().clone();
            for (int i = 1; i<cut; i++) {
                add(envA, listFN.get(i).getBoundary());
            }
            envB = listFN.get(cut).getBoundary().clone();
            for (int i = cut + 1; i < size; i++) {
                add(envB, listFN.get(i).getBoundary());
            }
            double overLapsTemp = calculator.getOverlaps(envA, envB);
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
            final Node newChild = (Node)listFN.get(i);
            newChild.setSiblingId(0);
            nodeA.addChild(newChild);
        }
        for (int i = index; i < size; i++) {
            final Node newChild = (Node)listFN.get(i);
            newChild.setSiblingId(0);
            nodeB.addChild(newChild);
        }
        assert nodeA.getParentId()== nodeB.getParentId() : "branchGrafting : NodeA and NodeB should have same parent.";
        assert treeAccess.readNode(nodeA.getParentId()).checkInternal()      : "branchGrafting : nodeA and B parent not conform.";
        assert nodeA.checkInternal()                  : "branchGrafting : at end candidate not conform";
        assert nodeB.checkInternal()                  : "branchGrafting : at end candidate not conform";
    }

    /**
     * Condense R-Tree.
     *
     * Condense made, travel up from leaf to tree trunk.
     *
     * @param candidate {@code Node} to begin condense.
     * @throws IllegalArgumentException if candidate is null.
     */
    @Override
    protected void trim(final Node candidate) throws IllegalArgumentException, IOException, StoreIndexException {
        ArgumentChecks.ensureNonNull("trim : Node candidate", candidate);
        List<double[]> reinsertListCoords = null;
        List<Object> reinsertListObjects = null;
            double[] candiBound = null;
        if (candidate.getChildId() != 0 && !candidate.isLeaf()) {
            int sibl = candidate.getChildId();
            while (sibl != 0) {
                final Node currentChild = treeAccess.readNode(sibl);
                // empty child
                if (currentChild.isEmpty()) {
                    candidate.removeChild(currentChild);
                } else if (currentChild.isLeaf() 
                     && currentChild.getChildCount() <= getMaxElements() / 3) {// other condition
                    if (reinsertListCoords == null) {
                        reinsertListCoords  = new ArrayList<double[]>();
                        reinsertListObjects = new ArrayList<Object>();
                    }
                    int cuChildSibl = currentChild.getChildId();
                    while (cuChildSibl != 0) {
                        final Node currentData = treeAccess.readNode(cuChildSibl);
                        reinsertListCoords.add(currentData.getBoundary());// risk de .clone a voir
                        reinsertListObjects.add(-currentData.getChildId());
                        cuChildSibl = currentData.getSiblingId();
                        currentChild.removeChild(currentData);
                        setElementsNumber(getElementsNumber()-1);
                    }
                    candidate.removeChild(currentChild);
                } else {
                    if (candiBound == null) {
                        candiBound = currentChild.getBoundary().clone();
                    } else {
                        add(candiBound, currentChild.getBoundary());
                    }
                    // child own a single sub-child and its not a leaf.
                    if (currentChild.getChildCount() == 1) {
                        assert !currentChild.isLeaf() : "Trim : current child should not be leaf.";
                        final Node cChild = treeAccess.readNode(currentChild.getChildId());
                        assert Arrays.equals(currentChild.getBoundary(), cChild.getBoundary()) : "Node with only one element should have same boundary than its stored element.";
                        candidate.removeChild(currentChild);
                        candidate.addChild(cChild);
                    }
                }
                sibl = currentChild.getSiblingId();
            }
        }
        if (candiBound != null) {
            candidate.setBoundary(candiBound);
             treeAccess.writeNode(candidate);
            assert candidate.checkInternal() : "trim : candidate not conform";
        }
            
        if (candidate.getParentId()!= 0) {
            trim (treeAccess.readNode(candidate.getParentId()));
        } else {
            // generaly root have some changes.
            if (candidate.isEmpty()) {
                setRoot(null);
            } else {
                setRoot(candidate);
            }
        }
        if (reinsertListCoords != null) {
            assert (reinsertListObjects != null) : "trim : listObjects should not be null.";
            final int reSize = reinsertListCoords.size();
            assert (reSize == reinsertListObjects.size()) : "reinsertLists should have same size";
            for (int i = 0; i < reSize; i++) {
                insert(reinsertListObjects.get(i), reinsertListCoords.get(i));
            }
        } else {
            assert (reinsertListObjects == null) : "trim : listObjects should be null.";
        }
    }
}
