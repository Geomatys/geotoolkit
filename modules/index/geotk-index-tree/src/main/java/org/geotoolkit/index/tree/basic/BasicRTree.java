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
import java.util.List;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.index.tree.AbstractTree;
import static org.geotoolkit.index.tree.TreeUtilities.*;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.internal.tree.TreeAccess;
import static org.geotoolkit.index.tree.basic.SplitCase.*;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapper;

/**
 * BasicRTree : Tree implementation.<br/><br/>
 * 
 * It's a Tree implementation with a faster insertion and remove action, 
 * but search is lesser fast than other Trees.<br/>
 * If stored datas are often updated, which mean more insertions or removes action, 
 * it's a Tree implementation which respond to this criteria.<br/><br/>
 * 
 * Note : In this RTree version it exist two made to split a Node, named : LINEAR and QUADRATIC.<br/>
 * For more informations see {@link SplitCase} javadoc.
 *
 * @author Remi Marechal (Geomatys).
 * @see SplitCase.
 */
abstract class BasicRTree<E> extends AbstractTree<E> {
    
    /**
     * Split made choice.
     */
    private final SplitCase choice;
    
    /**
     * Create a Basic RTree implementation.
     * 
     * @param treeAccess object in which all Tree information are stored.
     * @param choice split made choice.
     * @param treeEltMap object in which data and tree identifier are stored.
     * @throws StoreIndexException 
     * @see TreeAccess
     * @see SplitCase
     * @see TreeElementMapper
     */
    protected BasicRTree(final TreeAccess treeAccess, final TreeElementMapper treeEltMap) throws StoreIndexException {
        super(treeAccess, treeAccess.getCRS(), treeEltMap);
        ArgumentChecks.ensureNonNull("Create AbstractBasicRTree : treeAF", treeAccess);
        ArgumentChecks.ensureNonNull("Create AbstractBasicRTree : CRS", crs);
        this.choice    = treeAccess.getSplitMade();
        ArgumentChecks.ensureNonNull("Create AbstractBasicRTree : SplitCase choice", choice);
        super.setRoot(treeAccess.getRoot());
        treeIdentifier = treeAccess.getTreeIdentifier();
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    protected Node nodeInsert(Node candidate, int identifier, double... coordinates) throws IOException {
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
            fileCandidate.addChild(createNode(treeAccess, coordinates, IS_DATA, fileCandidate.getNodeId(), 0, -identifier));
            assert fileCandidate.checkInternal() : "nodeInsert : leaf after add.";
        } else {
            assert fileCandidate.checkInternal() : "nodeInsert : Node before insert.";
            subCandidateParent = (Node)nodeInsert(chooseSubtree(fileCandidate, coordinates), identifier, coordinates);
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
        assert fileCandidate.checkInternal() : "nodeInsert : after insert.";
        
        if (fileCandidate.getChildCount() > getMaxElements()) {
            assert fileCandidate.checkInternal() : "nodeInsert : before Branch grafting.";
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
//            /******************************************************************/
            assert fileCandidate.checkInternal() : "nodeInsert : after Branch grafting.";
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
        /**
         * If last travel up (on Root Node) and currently candidate was changed,
         * return currently Node else return null.
         */
        return (subCandidateParent != null && fileCandidate.getParentId() == 0) ? fileCandidate : null;
    }
    
    /**
     * Split a overflow {@code Node} in accordance with R-Tree properties.
     *
     * @param candidate {@code Node} to Split.
     * @throws IllegalArgumentException if candidate is null.
     * @throws IllegalArgumentException if candidate elements number is lesser 2.
     * @return {@code Node} List which contains two {@code Node} (split result of candidate).
     */
    @Override
    protected Node[] splitNode(final Node candidate) throws IllegalArgumentException, IOException {
        ArgumentChecks.ensureNonNull("splitNode : candidate", candidate);
        assert candidate.checkInternal() : "splitNode : begin.";
        int childNumber = candidate.getChildCount();
        if (childNumber < 2) 
            throw new IllegalArgumentException("not enought elements within " + candidate + " to split.");
        final int maxElmnts   = getMaxElements();
        
        final Node[] children = candidate.getChildren();
        assert childNumber == children.length : "SplitNode : childnumber should be same as children length value.";
        
        final byte candidateProperties = candidate.getProperties();
        
        Node s1 = null;
        Node s2 = null;
        
        double refValue = Double.NEGATIVE_INFINITY;
        double tempValue;
        int index1 = 0;
        int index2 = 0;
        
        switch (choice) {
            /**
             * Find the two further Nodes.
             */
            case LINEAR: {
                for (int i = 0; i < childNumber - 1; i++) {
                    for (int j = i + 1; j < childNumber; j++) {
                        tempValue = calculator.getDistanceEnvelope(children[i].getBoundary(), children[j].getBoundary());
                        if (tempValue > refValue) {
                            s1     = children[i];
                            s2     = children[j];
                            index1 = i;
                            index2 = j;
                            refValue = tempValue;
                        }
                    }
                }
            }
            break;

            /**
             * Find the two which create as much dead space as possible.<br/>
             * With dead space mean the most empty area between them.
             */
            case QUADRATIC: {
                double[] rectGlobal, bound1, bound2;
                for (int i = 0; i < childNumber - 1; i++) {
                    for (int j = i + 1; j < childNumber; j++) {
                        bound1 = children[i].getBoundary();
                        bound2 = children[j].getBoundary();
                        
                        rectGlobal = bound1.clone();
                        add(rectGlobal, bound2);
                        tempValue  = calculator.getSpace(rectGlobal) - calculator.getSpace(bound1) - calculator.getSpace(bound2);
                        if (tempValue > refValue) {
                            s1     = children[i];
                            s2     = children[j];
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
        
        final int maxid = Math.max(index1, index2);
        System.arraycopy(children, maxid+1, children, maxid, children.length-maxid-1);
        children[children.length-1] = null;
        
        final int minid = Math.min(index1, index2);
        System.arraycopy(children, minid+1, children, minid, children.length-minid-1);
        children[children.length-1] = null;
        childNumber -= 2;
        
        double[] r1Temp, r2Temp;
        double demimaxE = maxElmnts / 3.0;
        demimaxE = Math.max(demimaxE, 1);
        
        //result1 attributs
        int r1ChCount = 0;
        Node[] result1Children = new Node[childNumber+1];
        
        //result2 attributs
        int r2ChCount = 0;
        Node[] result2Children = new Node[childNumber+1];
        
        //add s1 s2
        result1Children[r1ChCount++] = s1;
        result2Children[r2ChCount++] = s2;

        for (int i = 0, s = childNumber; i < s; i++) {
            Node currentFileNode = children[i];
            r1Temp = s1.getBoundary().clone();
            add(r1Temp, currentFileNode.getBoundary());
            r2Temp = s2.getBoundary().clone();
            add(r2Temp, currentFileNode.getBoundary());
            
            final double area1 = calculator.getSpace(r1Temp);
            final double area2 = calculator.getSpace(r2Temp);
            
            if (area1 < area2) {
                if (r2ChCount <= demimaxE && r1ChCount > demimaxE) {
                    result2Children[r2ChCount++] = currentFileNode;
                } else {
                    result1Children[r1ChCount++] = currentFileNode;
                }
            } else if (area1 == area2) {
                if (r1ChCount < r2ChCount) {
                    result1Children[r1ChCount++] = currentFileNode;
                } else {
                    result2Children[r2ChCount++] = currentFileNode;
                }
            } else {
                if (r1ChCount <= demimaxE && r2ChCount > demimaxE) {
                    result1Children[r1ChCount++] = currentFileNode;
                } else {
                    result2Children[r2ChCount++] = currentFileNode;
                }
            }
        }
        result1Children = Arrays.copyOf(result1Children, r1ChCount);
        result2Children = Arrays.copyOf(result2Children, r2ChCount);
                
        final Node result1, result2;
        final boolean isLeaf = candidate.isLeaf();
        if (!isLeaf && r1ChCount == 1) {
            result1 = result1Children[0];
            ((Node)result1).setSiblingId(0);
        } else {
            result1 = createNode(treeAccess, null, candidateProperties, 0, 0, 0);
            result1.addChildren(result1Children);
        }
        if (!isLeaf && r2ChCount == 1) {
            result2 = result2Children[0];
            ((Node)result2).setSiblingId(0);
        } else {
            result2 = createNode(treeAccess, null, candidateProperties, 0, 0, 0);
            result2.addChildren(result2Children);
        }
        // check result
        assert result1.checkInternal() : "splitNode : result1.";
        assert result2.checkInternal() : "splitNode : result2.";
        
        return new Node[]{result1, result2};
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
            for (int i = 1; i < cut; i++) {
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
    @Override
    protected boolean removeNode(final Node candidate, final int identifier, final double... coordinate) throws  StoreIndexException, IOException {
        ArgumentChecks.ensureNonNull("removeNode : Node candidate", candidate);
        ArgumentChecks.ensureNonNull("removeNode : Object object", identifier);
        ArgumentChecks.ensureNonNull("removeNode : double[] coordinate", coordinate);
        if(intersects(candidate.getBoundary(), coordinate, true)){
            if (candidate.isLeaf()) {
                boolean removed = candidate.removeData(identifier, coordinate);
                if (removed) {
                    setElementsNumber(getElementsNumber()-1);
                    trim(candidate);
                    return true;
                }
            } else {
                int sibl = candidate.getChildId();
                while (sibl != 0) {
                    final Node currentChild = treeAccess.readNode(sibl);
                    final boolean removed = removeNode(currentChild, identifier, coordinate);
                    if (removed) return true;
                    sibl = currentChild.getSiblingId();
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
    @Override
    protected void trim(final Node candidate) throws IllegalArgumentException, IOException, StoreIndexException {
        ArgumentChecks.ensureNonNull("trim : Node candidate", candidate);
        List<double[]> reinsertListCoords = null;
        List<Integer> reinsertListObjects = null;
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
                        reinsertListObjects = new ArrayList<Integer>();
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
                insert((int)reinsertListObjects.get(i), reinsertListCoords.get(i));
            }
        } else {
            assert (reinsertListObjects == null) : "trim : listObjects should be null.";
        }
    }
}
