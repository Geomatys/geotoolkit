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
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.index.tree.AbstractTree;
import static org.geotoolkit.index.tree.TreeUtilities.*;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.internal.tree.TreeAccess;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapper;

/**
 *
 * @author Remi Marechal (Geomatys).
 */
abstract class HilbertRTree<E> extends AbstractTree<E> {
        
    protected HilbertRTree(final TreeAccess treeAccess, final TreeElementMapper treeEltMap) throws StoreIndexException {
        super(treeAccess, treeAccess.getCRS(), treeEltMap);
        ArgumentChecks.ensureNonNull("Create AbstractBasicRTree : treeAF", treeAccess);
        ArgumentChecks.ensureNonNull("Create AbstractBasicRTree : CRS", crs);
        super.setRoot(treeAccess.getRoot());
        treeIdentifier = treeAccess.getTreeIdentifier();
    }
    
    @Override
    protected Node nodeInsert(Node candidate, int identifier, double... coordinates) throws IOException{
        assert candidate instanceof Node;
        Node fileCandidate = (Node) candidate;
        assert !fileCandidate.isData() : "nodeInsert : candidate should never be data type.";
        /**
         * During travel down recursively candidate parent may be modified.<br/>
         * When travel up recursively if candidate should be modified, get
         * new candidate object updated from sub-method.
         */
        Node subCandidateParent = null;
        if (fileCandidate.isLeaf()) {
            if (fileCandidate.isFull()) {
                Node[] lSp = splitNode(fileCandidate);
                if (lSp != null) {

                    Node lsp0 = (Node) lSp[0];
                    Node lsp1 = (Node) lSp[1];
                    assert lsp0.checkInternal() : "insertNode : just after split.lsp0";
                    assert lsp1.checkInternal() : "insertNode : just after split.lsp1";
                    
                    if (fileCandidate.getParentId() != 0) {
                        Node parentCandidate = treeAccess.readNode(fileCandidate.getParentId());
                        final int lsp0Id = lsp0.getNodeId();
                        final int lsp1Id = lsp1.getNodeId();
                        /**
                         * Add in candidate temporary to force to add element in one of splitted Node
                         * else algorithm can choose another node of lspo and lsp1, from parent children.<br/>
                         * That is not wrong behavior because after split, an another Node from candidate parent children,
                         * may be choosen and the split become caducous.
                         */
                        lsp0.setParentId(fileCandidate.getNodeId());
                        lsp1.setParentId(fileCandidate.getNodeId());
                        fileCandidate.clear();
                        fileCandidate.setProperties(IS_OTHER);
                        ((HilbertNode)fileCandidate).setCurrentHilbertOrder(0);
                        fileCandidate.setParentId(0);
                        fileCandidate.addChild(lsp0);
                        fileCandidate.addChild(lsp1);
                        nodeInsert(fileCandidate, identifier, coordinates);
                        assert fileCandidate.checkInternal() : "insertNode : split with parent not null.";
                        candidate.clear();
                        
                        lsp0 = treeAccess.readNode(lsp0Id);
                        lsp1 = treeAccess.readNode(lsp1Id);
                        
                        assert lsp0.checkInternal() : "insertNode : split with parent not null.lsp0";
                        assert lsp1.checkInternal() : "insertNode : split with parent not null.lsp1";
                        parentCandidate.removeChild(fileCandidate);
                        lsp0.setParentId(parentCandidate.getNodeId());
                        lsp0.setSiblingId(0);
                        parentCandidate.addChild(lsp0);
                        lsp1.setParentId(parentCandidate.getNodeId());
                        lsp1.setSiblingId(0);
                        parentCandidate.addChild(lsp1);
                        return parentCandidate;
                    } else {
                        candidate.clear();
                        fileCandidate.setProperties(IS_OTHER);
                        ((HilbertNode)fileCandidate).setCurrentHilbertOrder(0);
                        lsp0.setParentId(fileCandidate.getNodeId());
                        lsp1.setParentId(fileCandidate.getNodeId());
                        candidate.addChild(lsp0);
                        candidate.addChild(lsp1);
                        nodeInsert(candidate, identifier, coordinates);
                    }
                } else {
                    throw new IllegalStateException("Normaly split leaf never null");
                }
            } else {
                assert candidate.checkInternal() : "insertNode : leaf not full just before insert candidate not conform.";
                fileCandidate.addChild(createNode(treeAccess, coordinates, IS_DATA, fileCandidate.getNodeId(), 0, -identifier));
                assert candidate.checkInternal() : "insertNode : leaf not full just after insert candidate not conform.";
            }
        } else {
            assert fileCandidate.checkInternal() : "nodeInsert : Node before insert.";
            subCandidateParent = (Node)nodeInsert(chooseSubtree(fileCandidate, coordinates), identifier, coordinates);
            add(fileCandidate.getBoundary(), coordinates);
            
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
                if (candidateParentID == 0) { 
                    // on clear le candidate
                    assert fileCandidate.getSiblingId() == 0 : "nodeInsert : split root : root should not have sibling.";
                    fileCandidate.clear();
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
//                } else if (currentChild.isLeaf() 
//                     && ((FileHilbertNode)currentChild).getDataCount() <= getMaxElements() / 3) {// other condition
//                    if (reinsertListCoords == null) {
//                        reinsertListCoords  = new ArrayList<double[]>();
//                        reinsertListObjects = new ArrayList<Object>();
//                    }
//                    int cuCellSibl = currentChild.getChildId();
//                    while (cuCellSibl != 0) {
//                        final FileNode currentCell = treeAccess.readNode(cuCellSibl);
//                        int cuDataSibl = currentCell.getChildId();
//                        while (cuDataSibl != 0) {
//                            final FileNode currentData = treeAccess.readNode(cuDataSibl);
//                            reinsertListCoords.add(currentData.getBoundary());// risk de .clone a voir
//                            reinsertListObjects.add(-currentData.getChildId());
//                            setElementsNumber(getElementsNumber()-1);
//                            cuDataSibl = currentData.getSiblingId();
//                            currentChild.removeChild(currentData);
//                        }
////                            reinsertListCoords.add(currentCell.getBoundary());// risk de .clone a voir
////                            reinsertListObjects.add(-currentCell.getChildId());
//                        cuCellSibl = currentCell.getSiblingId();
////                        currentChild.removeChild(currentCell);
////                        setElementsNumber(getElementsNumber()-1);
//                    }
//                    candidate.removeChild(currentChild);
                } else {
                    if (candiBound == null) {
                        candiBound = currentChild.getBoundary().clone();
                    } else {
                        add(candiBound, currentChild.getBoundary());
                    }
                    // child own a single sub-child and its not a leaf.
                    if (!currentChild.isLeaf() && currentChild.getChildCount() == 1) {
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
