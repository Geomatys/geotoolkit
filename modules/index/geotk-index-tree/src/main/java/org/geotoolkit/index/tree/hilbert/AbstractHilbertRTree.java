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
import org.apache.sis.util.Classes;
import org.geotoolkit.index.tree.AbstractTree;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.access.TreeAccess;
import org.geotoolkit.index.tree.calculator.Calculator;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.index.tree.mapper.TreeElementMapper;

/**
 *
 * @author Remi Marechal (Geomatys).
 */
public class AbstractHilbertRTree<E> extends AbstractTree<E> {
    
//     /**
//     * In accordance with R*Tree properties.
//     * To avoid unnecessary split permit to
//     * reinsert some elements just one time.
//     */
//    boolean insertAgain = true;
//    
//    private final LinkedList<Object> listObjects  = new LinkedList<Object>();
//    private final LinkedList<double[]> listCoords = new LinkedList<double[]>(); 
//    
//    boolean travelUpBeforeInsertAgain = false;
    
    public AbstractHilbertRTree(final TreeAccess treeAccess, final TreeElementMapper treeEltMap) throws StoreIndexException {
        super(treeAccess, treeAccess.getCRS(), treeEltMap);
//        if (!(treeAccess instanceof HilbertTreeAccessFile))
//            throw new IllegalArgumentException("HilbertRTree init : you should specify a HilbertTreeAccess");
        ArgumentChecks.ensureNonNull("Create AbstractBasicRTree : treeAF", treeAccess);
        ArgumentChecks.ensureNonNull("Create AbstractBasicRTree : CRS", crs);
        this.treeAccess = treeAccess;
        super.setRoot(treeAccess.getRoot());
        treeIdentifier = treeAccess.getTreeIdentifier();
    }
    
    @Override
    public int[] searchID(double[] regionSearch) throws StoreIndexException {
        // root node always begin at index 1 because 0 is reserved for no sibling or children.
        final Node root = getRoot();
        if (root != null && !root.isEmpty()) {
            try {
                return treeAccess.search(((Node)root).getNodeId(), regionSearch);
            } catch (IOException ex) {
                throw new StoreIndexException(this.getClass().getName()+" impossible to find stored elements at "
                        +Arrays.toString(regionSearch)+" region search area.", ex);
            }
        }
        return null;
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
                final Node newRoot = nodeInsert(root, object, coordinates);
                if (newRoot != null) {
                    setRoot(newRoot);
                    treeAccess.writeNode((Node)newRoot);
                }
            }
        } catch (IOException ex) {
            throw new StoreIndexException(this.getClass().getName()+"Tree.insert(), impossible to add element.", ex);
        }
    }
    
    private Node nodeInsert(Node candidate, Object object, double... coordinates) throws IOException{
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
            if (fileCandidate.isFull()) {
                Node[] lSp = splitNode(fileCandidate);
                if (lSp != null) {

                    Node lsp0 = (Node) lSp[0];
                    Node lsp1 = (Node) lSp[1];
                    assert lsp0.checkInternal() : "insertNode : just after split.lsp0";
                    assert lsp1.checkInternal() : "insertNode : just after split.lsp1";
                    
                    if (fileCandidate.getParentId() != 0) {
                        Node parentCandidate = treeAccess.readNode(fileCandidate.getParentId());
//                        parentCandidate.removeChild(fileCandidate);
                        final int lsp0Id = lsp0.getNodeId();
                        final int lsp1Id = lsp1.getNodeId();
                        /**
                         * <p>Add in candidate temporary to force to add element in one of splitted Node
                         * else algorithm can choose another node of lspo and lsp1, from parent children.<br/>
                         * That is not wrong behavior because after split an another Node, from candidate parent children,
                         * may be choosen and the split become caduc.</p>
                         */
                        lsp0.setParentId(fileCandidate.getNodeId());
                        lsp1.setParentId(fileCandidate.getNodeId());
                        fileCandidate.clear();
                        fileCandidate.setProperties(IS_OTHER);
                        ((FileHilbertNode)fileCandidate).setCurrentHilbertOrder(0);
                        fileCandidate.setParentId(0);
//                        fileCandidate.setSiblingId(0);
//                        fileCandidate.setUserProperty(PROP_ISLEAF, false);
//                        fileCandidate.setUserProperty(PROP_HILBERT_ORDER, 0);
                        fileCandidate.addChild(lsp0);
                        fileCandidate.addChild(lsp1);
                        nodeInsert(fileCandidate, object, coordinates);
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
//                        candidate.setUserProperty(PROP_ISLEAF, false);
//                        candidate.setUserProperty(PROP_HILBERT_ORDER, 0);
                        fileCandidate.setProperties(IS_OTHER);
                        ((FileHilbertNode)fileCandidate).setCurrentHilbertOrder(0);
                        lsp0.setParentId(fileCandidate.getNodeId());
                        lsp1.setParentId(fileCandidate.getNodeId());
                        candidate.addChild(lsp0);
                        candidate.addChild(lsp1);
                        nodeInsert(candidate, object, coordinates);
//                        assert candidate.getBoundary() == null : "boundary should be null";
                    }
                } else {
                    throw new IllegalStateException("Normaly split leaf never null");
                }
            } else {
                assert candidate.checkInternal() : "insertNode : leaf not full just before insert candidate not conform.";
                fileCandidate.addChild(createNode(treeAccess, coordinates, IS_DATA, fileCandidate.getNodeId(), 0, -((Integer)object)));
                assert candidate.checkInternal() : "insertNode : leaf not full just after insert candidate not conform.";
            }
        } else {
            assert fileCandidate.checkInternal() : "nodeInsert : Node before insert.";
            subCandidateParent = (Node)nodeInsert(chooseSubtree(fileCandidate, coordinates), object, coordinates);
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
                if (candidateParentID == 0) { // on est sur le noeud root
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
     * Split a overflow {@code Node} in accordance with R-Tree properties.
     *
     * @param candidate {@code Node} to Split.
     * @throws IllegalArgumentException if candidate is null.
     * @throws IllegalArgumentException if candidate elements number is lesser 2.
     * @return {@code Node} List which contains two {@code Node} (split result of candidate).
     */
    private Node[] splitNode(final Node candidate) throws IllegalArgumentException, IOException {
        ArgumentChecks.ensureNonNull("splitNode : candidate", candidate);
        assert candidate.checkInternal() : "splitNode : begin.";
        int childNumber = (candidate.isLeaf())?((FileHilbertNode)candidate).getDataCount():candidate.getChildCount();
        if (childNumber < 2) 
            throw new IllegalArgumentException("not enought elements within " + candidate + " to split.");
        
        final Node[] children = candidate.getChildren();
        assert childNumber == children.length : "SplitNode : childnumber should be same as children length value.";
        
        final byte candidateProperties = candidate.getProperties();
        
        final Calculator calc = getCalculator();
        final int splitIndex  = defineSplitAxis(children);
        
        final int size = children.length;
        final double size04 = size * 0.4;
        final int demiSize = (int) ((size04 >= 1) ? size04 : 1);
        double[] unionTabA, unionTabB;
        
        // find a solution where overlaps between 2 groups is the smallest. 
        double bulkTemp;
        double bulkRef = Double.POSITIVE_INFINITY;
        
        // in case where overlaps equal 0 (no overlaps) find the smallest group area.
        double areaTemp;
        double areaRef = Double.POSITIVE_INFINITY;
        
        // solution
        int index = 0;
        boolean lower_or_upper = true;
        
        int cut2;
        //compute with lower and after upper
        for(int lu = 0; lu < 2; lu++) {
            calc.sort(splitIndex, (lu == 0), children);
            for(int cut = demiSize; cut <= size - demiSize; cut++) {
                cut2 = size - cut;
                final Node[] splitTabA = new Node[cut];
                final Node[] splitTabB = new Node[cut2];
                System.arraycopy(children, 0, splitTabA, 0, cut);
                System.arraycopy(children, cut, splitTabB, 0, cut2);
                // bulk computing
                unionTabA = splitTabA[0].getBoundary().clone();
                for (int i = 1; i < cut; i++) {
                    add(unionTabA, splitTabA[i].getBoundary());
                }
                unionTabB = splitTabB[0].getBoundary().clone();
                for (int i = 1; i < cut2; i++) {
                    add(unionTabB, splitTabB[i].getBoundary());
                }
                bulkTemp = calc.getOverlaps(unionTabA, unionTabB);
                if (bulkTemp == 0) {
                    areaTemp = calc.getEdge(unionTabA) + calc.getEdge(unionTabB);
                    if (areaTemp < areaRef) {
                        areaRef        = areaTemp;
                        index          = cut;
                        lower_or_upper = (lu == 0);
                    }
                } else {
                    if (!Double.isInfinite(areaRef)) continue; // a better solution was already found.
                    if (bulkTemp < bulkRef) {
                        bulkRef        = bulkTemp;
                        index          = cut;
                        lower_or_upper = (lu == 0);
                    }
                }
            }
        }
        
        // best organization solution 
        calc.sort(splitIndex, lower_or_upper, children);
        final int lengthResult2 = size - index;
        final Node[] result1Children = new Node[index];
        final Node[] result2Children = new Node[lengthResult2];
                
        final Node result1, result2;
        final boolean isLeaf = candidate.isLeaf();
        if (!isLeaf && index == 1) {
            result1 = children[0];
            ((Node)result1).setSiblingId(0);
        } else {
            result1 = createNode(treeAccess, null, candidateProperties, 0, 0, 0);
            System.arraycopy(children, 0, result1Children, 0, index);
            result1.addChildren(result1Children);
        }
        if (!isLeaf && lengthResult2 == 1) {
            result2 = children[size-1];
            ((Node)result2).setSiblingId(0);
        } else {
            result2 = createNode(treeAccess, null, candidateProperties, 0, 0, 0);
            System.arraycopy(children, index, result2Children, 0, lengthResult2);
            result2.addChildren(result2Children);
        }
        // check result
        assert result1.checkInternal() : "splitNode : result1.";
        assert result2.checkInternal() : "splitNode : result2.";
//        countadjust+=(treeAccess.getCountAdjust()-counta);
        return new Node[]{result1, result2};
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
    private Node chooseSubtree(final Node candidate, final double... coordinates) throws IOException {
        ArgumentChecks.ensureNonNull("chooseSubtree : candidate", candidate);
        ArgumentChecks.ensureNonNull("chooseSubtree : coordinates", coordinates);
//        assert candidate.checkInternal() : "chooseSubtree : candidate not conform";
        if (candidate.isLeaf()) throw new IllegalStateException("jdhvzd");
        final Calculator calc = getCalculator();
        final int childCount = candidate.getChildCount();
        if (childCount == 0) throw new IllegalArgumentException("chooseSubtree : children is empty");
        if (childCount == 1) return treeAccess.readNode(candidate.getChildId());
        final Node[] children = candidate.getChildren();
        assert children.length == childCount : "choose subtree : childcount should have same length as children table.";
        for (Node fNod : children) {
            assert fNod.checkInternal() : "chooseSubTree : test contains.";
            // on pourrai essayer d'equilibré l'arbre si plusieurs contienne une meme donnée la donner a celui qui a le moin d'element
            if (contains(fNod.getBoundary(), coordinates, true)) return fNod;
        }
        Node result = children[0];
        double[] addBound = result.getBoundary().clone();
        for(int i = 1; i < childCount; i++) {
            add(addBound, children[i].getBoundary());
        }
        double area = calc.getSpace(addBound);
        double nbElmt = result.getChildCount();
        double areaTemp;
        for (int i = 0; i < childCount; i++) {
            final Node dn = children[i];
            assert dn.checkInternal() : "chooseSubtree : find subtree.";
            final double[] dnBoundary = dn.getBoundary();
            final double[] rnod = dnBoundary.clone();
            add(rnod, coordinates);
            final int nbe = dn.getChildCount();
            final double[] assertBound = dnBoundary.clone();
            areaTemp = calc.getEnlargement(dnBoundary, rnod);
            assert Arrays.equals(dnBoundary, assertBound);
            if (areaTemp < area) {
                result = dn;
                area = areaTemp;
                nbElmt = nbe;
            } else if (areaTemp == area) {
                if (nbe < nbElmt) {
                    result = dn;
                    area = areaTemp;
                    nbElmt = nbe;
                }
            }
        }
//        assert candidate.checkInternal() : "chooseSubtree : candidate not conform";
        return result;
    }
    
    
    /**
     * Compute and define which axis to split {@code Node} candidate.
     *
     * <blockquote><font size=-1>
     * <strong>NOTE: Define split axis method decides a split axis among all dimensions.
     *               The chosen axis is the one with smallest overall perimeter or area (in function with dimension size).
     *               It work by sorting all entry or {@code Node}, from their left boundary coordinates.
     *               Then it considers every divisions of the sorted list that ensure each node is at least 40% full.
     *               The algorithm compute perimeter or area of two result {@code Node} from every division.
     *               A second pass repeat this process with respect their right boundary coordinates.
     *               Finally the overall perimeter or area on one axis is the sum of all perimeter or area obtained from the two pass.</strong>
     * </font></blockquote>
     *
     * @throws IllegalArgumentException if candidate is null.
     * @return prefered ordinate index to split.
     */
    private int defineSplitAxis(final Node[] children) throws IOException {
        ArgumentChecks.ensureNonNull("candidate : ", children);
        
        final int size = children.length;
        final double[][] childsBound = new double[size][];
        int cbID = 0;
        for (Node nod : children) childsBound[cbID++] = nod.getBoundary();
        
        final Calculator calc = getCalculator();
        final double size04 = size * 0.4;
        final int demiSize = (int) ((size04 >= 1) ? size04 : 1);
        double[][] splitTabA, splitTabB;
        double[] gESPLA, gESPLB;
        double bulkTemp;
        double bulkRef = Double.POSITIVE_INFINITY;
        int index = 0;
        final double[] globalEltsArea = getEnvelopeMin(childsBound);
        final int dim = globalEltsArea.length/2;//decal bit
        
        // if glogaleArea.span(currentDim) == 0 || if all elements have same span
        // value as global area on current ordinate, impossible to split on this axis.
        unappropriateOrdinate :
        for (int indOrg = 0; indOrg < dim; indOrg++) {
            final double globalSpan = getSpan(globalEltsArea, indOrg);
            boolean isSameSpan = true;
            //check if its possible to split on this currently ordinate.
            for (double[] elt : childsBound) {
                if (!(Math.abs(getSpan(elt, indOrg) - globalSpan) <= 1E-9)) {
                    isSameSpan = false;
                    break;
                }
            }
            if (globalSpan <= 1E-9 || isSameSpan) continue unappropriateOrdinate;
            bulkTemp = 0;
            for (int left_or_right = 0; left_or_right < 2; left_or_right++) {
                calc.sort(indOrg, left_or_right == 0, childsBound);
                for (int cut = demiSize, sdem = size - demiSize; cut <= sdem; cut++) {
                    splitTabA = new double[cut][];
                    splitTabB = new double[size - cut][];
                    System.arraycopy(childsBound, 0, splitTabA, 0, cut);
                    System.arraycopy(childsBound, cut, splitTabB, 0, size-cut);
                    gESPLA     = getEnvelopeMin(splitTabA);
                    gESPLB     = getEnvelopeMin(splitTabB);
                    bulkTemp  += calc.getEdge(gESPLA);
                    bulkTemp  += calc.getEdge(gESPLB);
                }
            }
            if(bulkTemp < bulkRef) {
                bulkRef = bulkTemp;
                index = indOrg;
            }
        }
        return index;
    }
    
    @Override
    public boolean remove(Object object, double... coordinates) throws IllegalArgumentException, StoreIndexException {
        ArgumentChecks.ensureNonNull("remove : object", object);
        ArgumentChecks.ensureNonNull("remove : coordinates", coordinates);
        final Node root = getRoot();
        if (root != null) {
            try {
                final boolean removed = removeNode((Node)root, object, coordinates);
                return removed;
            } catch (IOException ex) {
                throw new StoreIndexException(this.getClass().getName()
                        +"impossible to remove object : "+object.toString()
                        +" at coordinates : "+Arrays.toString(coordinates), ex);
            }
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
    private boolean removeNode(final Node candidate, final Object object, final double... coordinate) throws IllegalArgumentException, StoreIndexException, IOException{
        ArgumentChecks.ensureNonNull("removeNode : Node candidate", candidate);
        ArgumentChecks.ensureNonNull("removeNode : Object object", object);
        ArgumentChecks.ensureNonNull("removeNode : double[] coordinate", coordinate);
        if(intersects(candidate.getBoundary(), coordinate, true)){
            if (candidate.isLeaf()) {
                int childSibl = candidate.getChildId();
                boolean removed = false;
                while (childSibl != 0) {
                        final Node currentChild = treeAccess.readNode(childSibl);
                        int datasibl = currentChild.getChildId();
                        while (datasibl != 0) {
                            final Node currentData = treeAccess.readNode(datasibl);
                            if (((Integer)object) == -currentData.getChildId()
                               && Arrays.equals(currentData.getBoundary(), coordinate)) {
                                removed = true;
                                candidate.removeChild(currentData);
                                break;
                            }
                            datasibl = currentData.getSiblingId();
                        }
                        if (removed) break;
                        childSibl = currentChild.getSiblingId();
                }
                if (removed) {
                    setElementsNumber(getElementsNumber()-1);
                    trim(candidate);
                    return true;
                }
            } else {
                int sibl = candidate.getChildId();
                while (sibl != 0) {
                    final Node currentChild = treeAccess.readNode(sibl);
                    final boolean removed = removeNode(currentChild, object, coordinate);
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
    private void trim(final Node candidate) throws IllegalArgumentException, IOException, StoreIndexException {
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
                insert(reinsertListObjects.get(i), reinsertListCoords.get(i));
            }
        } else {
            assert (reinsertListObjects == null) : "trim : listObjects should be null.";
        }
    }

    public Node createNode(TreeAccess tA, double[] boundary, byte properties, int parentId, int siblingId, int childId) throws IllegalArgumentException {
        return tA.createNode(boundary, properties, parentId, siblingId, childId);
    }
    
    public TreeAccess getTreeAccess(){
        return treeAccess;
    }
    
//    /**
//     * Get statement from re-insert state.
//     *
//     * @return true if it's permit to re-insert else false.
//     */
//    private boolean getIA() {
//        return insertAgain;
//    }
//
//    /**
//     * Affect statement to permit or not, re-insertion.
//     * 
//     * @param insertAgain
//     */
//    private void setIA(boolean insertAgain) {
//        this.insertAgain = insertAgain;
//    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final Node root = getRoot();
        final String strRoot = (root == null || root.isEmpty()) ?"null":root.toString();
        return Classes.getShortClassName(this) + "\n" + strRoot;
    }

    @Override
    public Node createNode(Tree tree, Node parent, Node[] children, Object[] objects, double[][] coordinates) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws StoreIndexException {
        try {
            treeAccess.setTreeIdentifier(treeIdentifier);
            treeAccess.setEltNumber(eltCompteur);
            treeAccess.close();
        } catch (IOException ex) {
            throw new StoreIndexException("FileBasicRTree : close(). Impossible to close TreeAccessFile.", ex);
        }
    }
}
