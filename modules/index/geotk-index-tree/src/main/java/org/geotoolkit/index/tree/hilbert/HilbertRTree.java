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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.index.tree.*;
import static org.geotoolkit.index.tree.Node.*;
import org.geotoolkit.index.tree.calculator.Calculator;
import org.geotoolkit.index.tree.NodeFactory;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.util.Classes;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.index.tree.io.TreeElementMapper;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Create Hilbert RTree.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class HilbertRTree extends AbstractTree {

    int hilbertOrder;

    /**
     * Create Hilbert R-Tree using default node factory.
     *
     * <blockquote><font size=-1> <strong>
     * NOTE: In HilbertRTree each leaf contains some sub-{@code Node} called cells.
     * {@code Envelope} entries are contains in their cells.
     * Cells number per leaf = 2 ^ (dim*hilbertOrder).
     * Moreother there are maxElements_per_cells 2 ^(dim*hilbertOrder) elements per leaf.
     * </strong> </font></blockquote>
     *
     * @param nbMaxElement          : max elements number within each tree leaf cells.
     * @param hilbertOrder          : max order value.
     * @param crs                   : associate coordinate system.
     * @return Hilbert R-Tree.
     * @throws IllegalArgumentException if maxElements <= 0.
     * @throws IllegalArgumentException if hilbertOrder <= 0.
     */
    public HilbertRTree(int nbMaxElement, int hilbertOrder, CoordinateReferenceSystem crs, TreeElementMapper treeEltMap) {
        this(nbMaxElement, hilbertOrder, crs, DefaultNodeFactory.INSTANCE, treeEltMap);
    }

    /**
     * Create Hilbert R-Tree.
     *
     * <blockquote><font size=-1> <strong>
     * NOTE: In HilbertRTree each leaf contains some sub-{@code Node} called cells.
     * {@code Envelope} entries are contains in their cells.
     * Cells number per leaf = 2 ^ (dim*hilbertOrder).
     * Moreother there are maxElements_per_cells 2 ^(dim*hilbertOrder) elements per leaf.
     * </strong> </font></blockquote>
     *
     * @param nbMaxElement          : max elements number within each tree leaf cells.
     * @param hilbertOrder          : max order value.
     * @param crs                   : associate coordinate system.
     * @param nodefactory           : made to create tree {@code Node}.
     * @return Hilbert R-Tree.
     * @throws IllegalArgumentException if maxElements <= 0.
     * @throws IllegalArgumentException if hilbertOrder <= 0.
     */
    @Deprecated
    public HilbertRTree(int nbMaxElement, int hilbertOrder, CoordinateReferenceSystem crs, NodeFactory nodefactory, TreeElementMapper treeEltMap) {
        super(nbMaxElement, crs, nodefactory, treeEltMap);
        ArgumentChecks.ensureStrictlyPositive("impossible to create Hilbert Rtree with order <= 0", hilbertOrder);
        this.hilbertOrder = hilbertOrder;
    }

    /**
     * @return Max Hilbert order value.
     */
    public int getHilbertOrder() {
        return hilbertOrder;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + "\n" + getRoot();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] searchID(double[] regionSearch) throws StoreIndexException {
        ArgumentChecks.ensureNonNull("search : region search", regionSearch);
        final Node root = this.getRoot();
        if (root != null && !root.isEmpty()) {
            currentLength   = 100;
            currentPosition = 0;
            tabSearch       = new int[currentLength];
            try {
                searchHilbertNode(root, regionSearch);
                return Arrays.copyOf(tabSearch, currentPosition);
            } catch (IOException ex) {
                throw new StoreIndexException(ex);
            }
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(Object object, double... coordinates) throws StoreIndexException {
//        super.insert(object, coordinates);
        super.eltCompteur++;
        final Node root       = getRoot();
        try {
            if (root == null || root.isEmpty()) {
                setRoot(createNode(this, null, null, new Object[]{object}, new double[][]{coordinates}));
            } else {
                insertNode(root, object, coordinates);
            }
        } catch (IOException ex){
            throw new StoreIndexException(ex);
        }
    }

//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public boolean delete(Object object, double... coordinates) throws StoreIndexException {
//        ArgumentChecks.ensureNonNull("remove : object", object);
//        ArgumentChecks.ensureNonNull("remove : coordinates", coordinates);
//        final Node root = getRoot();
//        if (root != null) try {
//            return deleteHilbertNode(root, object, coordinates);
//        } catch (IOException ex) {
//            throw new StoreIndexException(ex);
//        }
//        return false;
//    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object object, double... coordinates) throws StoreIndexException {
        ArgumentChecks.ensureNonNull("remove : object", object);
        ArgumentChecks.ensureNonNull("remove : coordinates", coordinates);
        final Node root = getRoot();
        if (root != null) try {
            return removeHilbertNode(root, object, coordinates);
        } catch (IOException ex) {
            throw new StoreIndexException(ex);
        }
        return false;
    }
    
    /**
     * Find all {@code Envelope} (entries) which intersect regionSearch
     * parameter.
     *
     * @param regionSearch area of search.
     * @param result {@code List} where is add search resulting.
     */
    public void searchHilbertNode(final Node candidate, final double[] regionSearch) throws IOException {
        assert candidate.checkInternal() : "searchHilbertNode : begin candidate not conform.";
        final double[] bound = candidate.getBoundary();
        if (bound != null) {
            if (regionSearch == null) {
                if (candidate.isLeaf()) {
                    for (int i = 0, s = candidate.getChildCount(); i < s; i++) {
                        final Node cuCell = candidate.getChild(i);
                        if (!cuCell.isEmpty()) {
                            for (int j = 0, sc = cuCell.getObjectCount(); j < sc; j++) {
                                if (currentPosition == currentLength) {
                                    currentLength = currentLength << 1;
                                    final int[] tabTemp = tabSearch;
                                    tabSearch = new int[currentLength];
                                    System.arraycopy(tabTemp, 0, tabSearch, 0, currentPosition);
                                }
                                tabSearch[currentPosition++] = (int) cuCell.getObject(j);
                            }
                        }
                    }
                } else {
                    for (int i = 0, s = candidate.getChildCount(); i < s; i++) {
                        searchHilbertNode(candidate.getChild(i), null);
                    }
                }
            } else {
                if (contains(regionSearch, bound, true)) {
                    searchHilbertNode(candidate, null);
                } else if(intersects(regionSearch, bound, true)) {
                    if (candidate.isLeaf()) {
                        for (int i = 0, s = candidate.getChildCount(); i < s; i++) {
                            final Node cuCell = candidate.getChild(i);
                            if (!cuCell.isEmpty()) {
                                if (intersects(regionSearch, cuCell.getBoundary(), true)) {
                                    assert (candidate.getCoordsCount() == candidate.getObjectCount()) : "";
                                    for (int j = 0, sc = cuCell.getCoordsCount(); j < sc; j++) {
                                        final double[] cuCoords = cuCell.getCoordinate(j);
                                        if (intersects(regionSearch, cuCoords, true)) {
                                            if (currentPosition == currentLength) {
                                                currentLength = currentLength << 1;
                                                final int[] tabTemp = tabSearch;
                                                tabSearch = new int[currentLength];
                                                System.arraycopy(tabTemp, 0, tabSearch, 0, currentPosition);
                                            }
                                            tabSearch[currentPosition++] = (int) cuCell.getObject(j);
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        for (int i = 0, s = candidate.getChildCount(); i < s; i++) {
                            searchHilbertNode(candidate.getChild(i), regionSearch);
                        }
                    }
                }
            }
        }
    }
    
    

//    /**
//     * Find all {@code Envelope} (entries) which intersect regionSearch
//     * parameter.
//     *
//     * @param regionSearch area of search.
//     * @param result {@code List} where is add search resulting.
//     */
//    public static TreeVisitorResult searchHilbertNode(final Node candidate, final double[] regionSearch, final TreeVisitor visitor) throws IOException {
//        assert candidate.checkInternal() : "searchHilbertNode : begin candidate not conform.";
//        final TreeVisitorResult tvr = visitor.filter(candidate);
//        if (isTerminate(tvr)) return tvr;
//        final double[] bound = candidate.getBoundary();
//        if (bound != null) {
//            if (regionSearch == null) {
//                if (candidate.isLeaf()) {
//                    for (int i = 0, s = candidate.getChildCount(); i < s; i++) {
//                        final Node cuCell = candidate.getChild(i);
//                        if (!cuCell.isEmpty()) {
//                            for (int j = 0, sc = cuCell.getObjectCount(); j < sc; j++) {
//                                final TreeVisitorResult tvrTemp = visitor.visit(cuCell.getObject(j));
//                                if (isTerminate(tvrTemp))   return tvrTemp;
//                                if (isSkipSibling(tvrTemp)) break;
//                            }
//                        }
//                    }
//                } else {
//                    if (!isSkipSubTree(tvr)) {
//                        for (int i = 0, s = candidate.getChildCount(); i < s; i++) {
//                            final TreeVisitorResult tvrTemp = searchHilbertNode(candidate.getChild(i), null, visitor);
//                            if (isTerminate(tvrTemp))   return tvrTemp;
//                            if (isSkipSibling(tvrTemp)) break;
//                        }
//                    }
//                }
//            } else {
//                if (contains(regionSearch, bound, true)) {
//                    searchHilbertNode(candidate, null, visitor);
//                } else if(intersects(regionSearch, bound, true)) {
//                    if (candidate.isLeaf()) {
//                        for (int i = 0, s = candidate.getChildCount(); i < s; i++) {
//                            final Node cuCell = candidate.getChild(i);
//                            TreeVisitorResult tvrTemp = null;
//                            if (!cuCell.isEmpty()) {
//                                if (intersects(regionSearch, cuCell.getBoundary(), true)) {
//                                    assert (candidate.getCoordsCount() == candidate.getObjectCount()) : "";
//                                    for (int j = 0, sc = cuCell.getCoordsCount(); j < sc; j++) {
//                                        final double[] cuCoords = cuCell.getCoordinate(j);
//                                        if (intersects(regionSearch, cuCoords, true)) {
//                                            tvrTemp = visitor.visit(cuCell.getObject(j));
//                                        }
//                                        if (isTerminate(tvrTemp) && tvrTemp != null)   return tvrTemp;
//                                        if (isSkipSibling(tvrTemp) && tvrTemp != null) break;
//                                    }
//                                }
//                            }
//                            if (isSkipSibling(tvrTemp)) break;
//                        }
//                    }else{
//                        if (!isSkipSubTree(tvr)) {
//                            for (int i = 0, s = candidate.getChildCount(); i < s; i++) {
//                                final TreeVisitorResult tvrTemp = searchHilbertNode(candidate.getChild(i), regionSearch, visitor);
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
     * Insert entry in {@code Node} in accordance with R-Tree properties.
     *
     * @param candidate {@code Node} where user want insert data.
     * @param entry to insert.
     * @throws IllegalArgumentException if candidate or entry are null.
     */
    public static void insertNode(final Node candidate, final Object object, final double... coordinates) throws IllegalArgumentException, IOException{
        ArgumentChecks.ensureNonNull("impossible to insert a null object", object);
        ArgumentChecks.ensureNonNull("impossible to insert a null coordinates", coordinates);
        assert candidate.checkInternal() : "insertNode : begin candidate not conform.";
        if (candidate.isLeaf()) {
            if (candidate.isFull()) {
                List<Node> lSp = hilbertNodeSplit(candidate);
                if (lSp != null) {

                    final Node lsp0      = lSp.get(0);
                    final Node lsp1      = lSp.get(1);
                    Node parentCandidate = candidate.getParent();

                    if (parentCandidate != null) {
                        boolean found = false;
                        for (int i = parentCandidate.getChildCount() - 1; i >= 0; i--) {
                            final Node cuNode = parentCandidate.getChild(i);
                            if (cuNode == candidate) {// found by pointer
                                final Node removedNode = parentCandidate.removeChild(i);
                                removedNode.clear(); 
                                found = true;
                                break;
                            }
                        }
                        assert found : "insertNode : candidate should be found after spliting.";
                        
                        /**
                         * <p>Add in candidate temporary to force to add element in one of splitted Node
                         * else algorithm can choose another node of lspo and lsp1, from parent children.<br/>
                         * That is not wrong behavior because after split an another Node, from candidate parent children,
                         * may be choosen and the split become caduc.</p>
                         */
                        lsp0.setParent(candidate);
                        lsp1.setParent(candidate);
                        candidate.setUserProperty(PROP_ISLEAF, false);
                        candidate.setUserProperty(PROP_HILBERT_ORDER, 0);
                        candidate.addChild(lsp0);
                        candidate.addChild(lsp1);
                        insertNode(candidate, object, coordinates);
                        candidate.clear();
                        
                        lsp0.setParent(parentCandidate);
                        parentCandidate.addChild(lsp0);
                        lsp1.setParent(parentCandidate);
                        parentCandidate.addChild(lsp1);
                    } else {
                        candidate.clear();
                        candidate.setUserProperty(PROP_ISLEAF, false);
                        candidate.setUserProperty(PROP_HILBERT_ORDER, 0);
                        lsp0.setParent(candidate);
                        lsp1.setParent(candidate);
                        candidate.addChild(lsp0);
                        candidate.addChild(lsp1);
                        insertNode(candidate, object, coordinates);
                        assert candidate.getBound() == null: "boundary should be null";
                    }
                } else {
                    throw new IllegalStateException("Normaly split leaf never null");
                }
            } else {
                assert candidate.checkInternal() : "insertNode : leaf not full just before insert candidate not conform.";
                candidate.addElement(object, coordinates);
                candidate.setBound(null);
                assert candidate.checkInternal() : "insertNode : leaf not full just after insert candidate not conform.";
            }
        } else {
            assert candidate.checkInternal() : "insertNode : node just before insert candidate not conform.";
            insertNode(chooseSubtree(candidate, coordinates), object, coordinates);
            assert candidate.checkInternal() : "insertNode : node just after insert candidate not conform.";
            candidate.setBound(null);
            final int maxElt = candidate.getTree().getMaxElements();
            if (candidate.getChildCount() > maxElt) {
                final List<Node> l = hilbertNodeSplit(candidate);
                final Node lsp0 = l.get(0);
                final Node lsp1 = l.get(1);
                final Node parentCandidate = candidate.getParent();
                if (parentCandidate == null) {
                    candidate.clear();
                    candidate.setUserProperty(PROP_ISLEAF, false);
                    candidate.setUserProperty(PROP_HILBERT_ORDER, 0);
                    lsp0.setParent(candidate);
                    lsp1.setParent(candidate);
                    candidate.addChild(lsp0);
                    candidate.addChild(lsp1);
                    assert candidate.checkInternal() : "insertNode : node just after first split candidate not conform.";
                } else {
                    boolean found = false;
                    for (int i = parentCandidate.getChildCount() - 1; i >= 0; i--) {
                        final Node cuNode = parentCandidate.getChild(i);
                        if (cuNode == candidate) {//found by pointer
                            final Node removedNode = parentCandidate.removeChild(i);
                            removedNode.clear();  
                            found = true;
                            break;
                        }
                    }
                    assert found : "insertNode : candidate should be found after spliting.";
                    lsp0.setParent(parentCandidate);
                    parentCandidate.addChild(lsp0);
                    lsp1.setParent(parentCandidate);
                    parentCandidate.addChild(lsp1);
                    assert parentCandidate.checkInternal() : "insertNode : node just after candidate split for parentCandidate not conform.";
                }
            }
        }
        assert candidate.getBound() == null : "node candidate boundary should be null.";
    }
    
    /**
     * Compute and define which axis to split {@code Node} candidate.
     *
     * <blockquote><font size=-1> <strong>NOTE: Define split axis method decides
     * a split axis among all dimensions. The choosen axis is the one with
     * smallest overall perimeter or area (in fonction with dimension size). It
     * work by sorting all entry or {@code Node}, from their left boundary
     * coordinates. Then it considers every divisions of the sorted list that
     * ensure each node is at least 40% full. The algorithm compute perimeters
     * or area of two result {@code Node} from every division. A second pass
     * repeat this process with respect their right boundary coordinates.
     * Finally the overall perimeter or area on one axis is the som of all
     * perimeter or area obtained from the two pass.</strong>
     * </font></blockquote>
     *
     * @throws IllegalArgumentException if candidate is null.
     * @return prefered ordinate index to split.
     */
    private static int defineSplitAxis(final Node candidate) throws IOException {
        ArgumentChecks.ensureNonNull("defineSplitAxis : ", candidate);
        assert candidate.checkInternal() : "defineSplitAxis : begin candidate not conform.";
        final boolean isLeaf = candidate.isLeaf();
        List eltList = new ArrayList();
        final int childCount = candidate.getChildCount();
        if (isLeaf) {
            for (int i = 0; i < childCount; i++) {
                final Node cuCell = candidate.getChild(i);
                for (int j = 0, sc = cuCell.getCoordsCount(); j < sc; j++) {
                    eltList.add(cuCell.getCoordinate(j));
                }
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                eltList.add(candidate.getChild(i));
            }
        }
        final AbstractTree tree = (AbstractTree) candidate.getTree();
        final Calculator calc   = tree.getCalculator();
        final int size          = eltList.size();
        final double size04     = size * 0.4;
        final int demiSize      = (int) ((size04 >= 1) ? size04 : 1);
        final List splitListA   = new ArrayList();
        final List splitListB   = new ArrayList();
        double bulkRef          = Double.POSITIVE_INFINITY;
        int index               = 0;
        double[] gESPLA, gESPLB;
        double bulkTemp;

        final double[] globalEltsArea = getEnvelopeMin(eltList);
        final int dim           = globalEltsArea.length >> 1;

        // if glogaleArea.span(currentDim) == 0 || if all elements have same span
        // value as global area on current ordinate, impossible to split on this axis.
        unappropriateOrdinate :
        for (int indOrg = 0; indOrg < dim; indOrg++) {
            final double globalSpan = getSpan(globalEltsArea, indOrg);
            boolean isSameSpan = true;
            //check if its possible to split on this currently ordinate.
            for (Object elt : eltList) {
                final double[] envElt = (isLeaf) ? (double[])elt : ((Node)elt).getBoundary();
                if (!(Math.abs(getSpan(envElt, indOrg) - globalSpan) <= 1E-9)) {
                    isSameSpan = false;
                    break;
                }
            }
            if (globalSpan <= 1E-9 || isSameSpan) continue unappropriateOrdinate;
            bulkTemp = 0;

            for (int left_or_right = 0; left_or_right < 2; left_or_right++) {
                calc.sortList(indOrg, (left_or_right == 0), eltList, null);
                for (int cut = demiSize, sdem = size - demiSize; cut <= sdem; cut++) {
                    splitListA.clear();
                    splitListB.clear();
                    for (int i = 0; i < cut; i++) {
                        splitListA.add(eltList.get(i));
                    }
                    for (int j = cut; j < size; j++) {
                        splitListB.add(eltList.get(j));
                    }
                    gESPLA = getEnvelopeMin(splitListA);
                    gESPLB = getEnvelopeMin(splitListB);
                    bulkTemp += calc.getEdge(gESPLA);
                    bulkTemp += calc.getEdge(gESPLB);
                }
            }
            if (bulkTemp < bulkRef) {
                bulkRef = bulkTemp;
                index = indOrg;
            }
        }
        assert candidate.checkInternal() : "defineSplitAxis : end candidate not conform.";
        return index;
    }

    /**
     * Compute and define how to split {@code Node} candidate.
     *
     * <blockquote><font size=-1> <strong>NOTE: To choose which {@code Node}
     * couple, split algorithm sorts the entries (for tree leaf), or {@code Node}
     * (for tree branch) in accordance to their lower or upper boundaries on the
     * selected dimension (see defineSplitAxis method) and examines all possible
     * divisions. Two {@code Node} resulting, is the final division which has
     * the minimum overlaps between them.</strong> </font></blockquote>
     *
     * @throws IllegalArgumentException if candidate is null.
     * @return Two appropriate {@code Node} in List in accordance with
     * R*Tree split properties.
     */
    private static List<Node> hilbertNodeSplit(final Node candidate) throws IllegalArgumentException, IOException{
        assert candidate.checkInternal() : "hilbertNodeSplit : begin candidate not conform.";
        final int splitIndex  = defineSplitAxis(candidate);
        final boolean isLeaf  = candidate.isLeaf();
        final Tree tree       = candidate.getTree();
        final Calculator calc = tree.getCalculator();
        List eltList = new ArrayList();
        List<Object> listObjects = null;
        final int count = candidate.getChildCount();
        if (isLeaf) {
            listObjects = new ArrayList<Object>();
            for (int i = 0; i < count; i++) {
                final Node cuCell = candidate.getChild(i);
                for (int j = 0, sc = cuCell.getCoordsCount(); j < sc; j++) {
                    eltList.add(cuCell.getCoordinate(j));
                    listObjects.add(cuCell.getObject(j));
                }
            }
        } else {
            for (int i = 0; i < count; i++) {
                eltList.add(candidate.getChild(i));
            }
        }
        final int size        = eltList.size();
        
        //to find best split combinaison follow list elements from 1/3 th elts to 2/3th elts.
        final double size033  = size * 0.333;
        final int tierSize    = (int) ((size033 >= 1) ? size033 : 1);
        double bulkRef        = Double.POSITIVE_INFINITY;
        final List splitListA = new ArrayList();
        final List splitListB = new ArrayList();
        int index             = 0;
        boolean lower_or_upper    = true;
        double[] gESPLA, gESPLB;
        double bulkTemp;
        
        List<double[]> gESPLAList = new ArrayList<double[]>();
        List<double[]> gESPLBList = new ArrayList<double[]>();
        List<Integer> cutList = new ArrayList<Integer>();
        List<Boolean> luList = new ArrayList<Boolean>();
        
        
        for (int lu = 0; lu < 2; lu++) {
            calc.sortList(splitIndex, (lu == 0), eltList, listObjects);
            for (int cut = tierSize; cut <= size - tierSize; cut++) {
                for (int i = 0; i < cut; i++) {
                    splitListA.add(eltList.get(i));
                }
                for (int j = cut; j < size; j++) {
                    splitListB.add(eltList.get(j));
                }
                gESPLA = getEnvelopeMin(splitListA);
                gESPLB = getEnvelopeMin(splitListB);
                bulkTemp = getOverlapValue(gESPLA, gESPLB);
                if (Double.isNaN(bulkTemp) || bulkTemp == 0) {
                    gESPLAList.add(gESPLA);
                    gESPLBList.add(gESPLB);
                    cutList.add(cut);
                    luList.add(lu == 0);
                } else if (bulkTemp < bulkRef && cutList.isEmpty()) {
                    bulkRef = bulkTemp;
                    index = cut;
                    lower_or_upper = (lu == 0);
                }
                splitListA.clear();
                splitListB.clear();
            }
        }

        if (!cutList.isEmpty()) {
            double areaRef = Double.POSITIVE_INFINITY;
            double areaTemp;
            final int s = cutList.size();
            //paranoiac assert
            assert (s                    == luList.size() 
                    && gESPLAList.size() == gESPLBList.size() 
                    && s                 == gESPLAList.size()) : "splitNode : couple lists haven't got same size";
            for (int i = 0; i < s; i++) {
                areaTemp = calc.getEdge(gESPLAList.get(i)) + calc.getEdge(gESPLBList.get(i));
                if (areaTemp < areaRef) {
                    areaRef = areaTemp;
                    index = cutList.get(i);
                    lower_or_upper = luList.get(i);
                }
            }
        }
        calc.sortList(splitIndex, lower_or_upper, eltList, listObjects);
        Node[] nodeA = null, nodeB = null;
        double[][] coordA = null, coordB = null;
        Object[] objA = null, objB = null;
        
        if (isLeaf) {
            coordA = new double[index][];
            objA = new Object[index];
            for(int i = 0; i < index; i++) {
                coordA[i] = (double[]) eltList.get(i);
                objA[i] = listObjects.get(i);
            }
            coordB = new double[size - index][];
            objB = new Object[size - index];
            int ib = 0;
            for(int i = index; i < size; i++) {
                coordB[ib] = (double[]) eltList.get(i);
                objB[ib++] = listObjects.get(i);
            }
        } else {
            nodeA = new Node[index];
            for(int i = 0; i < index; i++) {
                nodeA[i] = (Node) eltList.get(i);
            }
            nodeB = new Node[size - index];
            int ib = 0;
            for (int i = index; i < size; i++) {
                nodeB[ib++] = (Node) eltList.get(i);
            }
        }

        if (isLeaf) {
            final int maxElts = tree.getMaxElements()*(2<<(tree.getCrs().getCoordinateSystem().getDimension()*((HilbertRTree)tree).getHilbertOrder()-1));
            assert objA.length <= maxElts :"objA length should be lesser than max size permit by tree.";
            assert objB.length <= maxElts :"objB length should be lesser than max size permit by tree.";
            assert coordA.length <= maxElts :"coordA lengèth should be lesser than max size permit by tree.";
            assert coordB.length <= maxElts :"coordB length should be lesser than max size permit by tree.";
        } else {
            final int maxElts = tree.getMaxElements();
            assert nodeA.length <= maxElts :"nodeA length should be lesser than max size permit by tree.";
            assert nodeB.length <= maxElts :"nodeB length should be lesser than max size permit by tree.";
        }
        
        if(isLeaf) return UnmodifiableArrayList.wrap(new Node[] {tree.createNode(tree, null, null, objA, coordA), tree.createNode(tree, null, null, objB, coordB)});
        final Node resultA = (Node) ((nodeA.length == 1) ? nodeA[0] : tree.createNode(tree, null, nodeA, null, null));
        final Node resultB = (Node) ((nodeB.length == 1) ? nodeB[0] : tree.createNode(tree, null, nodeB, null, null));
        return UnmodifiableArrayList.wrap(new Node[] {resultA, resultB});
    }

    /**
     * Find appropriate subnode to insert new entry. Appropriate subnode is
     * chosen to answer HilbertRtree criterion.
     *
     * @param entry to insert.
     * @throws IllegalArgumentException if this subnodes list is empty.
     * @throws IllegalArgumentException if entry is null.
     * @return subnode chosen.
     */
    public static Node chooseSubtree(final Node candidate, final double[] entry) throws IOException {
        ArgumentChecks.ensureNonNull("impossible to choose subtree with entry null", entry);
        assert candidate.checkInternal() : "chooseSubtree : begin candidate not conform.";
        final boolean isLeaf = candidate.isLeaf();
        if (isLeaf && candidate.isFull()){
            throw new IllegalStateException("impossible to choose subtree in overflow node");
        }
        final int size = candidate.getChildCount();
        final double[] entryBound = entry.clone();
        final Calculator calc = candidate.getTree().getCalculator();
        if (isLeaf) {
            throw new IllegalStateException("choose subtree normaly never pass in leaf choose");
        } else {
            if (candidate.getChild(0).isLeaf()) {
                final List<Node> listOverZero = new ArrayList<Node>();
                double overlapsRef = Double.POSITIVE_INFINITY;
                int index = -1;
                double overlapsTemp = 0;
                for (int i = 0; i < size; i++) {
                    final double[] gnTemp = candidate.getChild(i).getBoundary().clone();
                    add(gnTemp, entryBound);
                    for (int j = 0; j < size; j++) {
                        if (i != j) {
                            final double[] gET = candidate.getChild(i).getBoundary();
                            overlapsTemp += calc.getOverlaps(gnTemp, gET);
                        }
                    }
                    if (overlapsTemp == 0) {
                        listOverZero.add(candidate.getChild(i));
                    } else {
                        if ((overlapsTemp < overlapsRef)) {
                            overlapsRef = overlapsTemp;
                            index = i;
                        } else if (overlapsTemp == overlapsRef) {
                            if (countElements(candidate.getChild(i)) < countElements(candidate.getChild(index))) {
                                overlapsRef = overlapsTemp;
                                index = i;
                            }
                        }
                    }
                    overlapsTemp = 0;
                }
                if (!listOverZero.isEmpty()) {
                    double areaRef = Double.POSITIVE_INFINITY;
                    int indexZero  = -1;
                    double areaTemp;
                    for (int i = 0, s = listOverZero.size(); i < s; i++) {
                        final double[] gE = listOverZero.get(i).getBoundary().clone();
                        add(gE, entryBound);
                        areaTemp = calc.getEdge(gE);
                        if (areaTemp < areaRef) {
                            areaRef = areaTemp;
                            indexZero = i;
                        }
                    }
                    assert candidate.checkInternal() : "chooseSubtree : candidate not conform.";
                    return listOverZero.get(indexZero);
                }
                if (index == -1) throw new IllegalStateException("chooseSubTree : no subLeaf find");
                assert candidate.checkInternal() : "chooseSubtree : begin candidate not conform.";
                assert candidate.getChild(index).checkInternal() : "chooseSubtree : choosen child within listOverZero is not conform.";
                return candidate.getChild(index);
            }
            assert candidate.checkInternal() : "chooseSubtree : begin candidate not conform.";
            for (int i = 0; i < size; i++) {
                final Node no = candidate.getChild(i);
                if (contains(no.getBoundary(), entryBound, true)) {
                    assert no.checkInternal() : "chooseSubtree : contains child not conform.";
                    return no;
                }
            }
            assert candidate.checkInternal() : "chooseSubtree : end candidate not conform.";
            double enlargRef = Double.POSITIVE_INFINITY;
            int indexEnlarg  = -1;
            for (int i = 0; i < size; i++) {
                final Node nod = candidate.getChild(i);
                final double[] gEN = nod.getBoundary().clone();
                add(gEN, entryBound);
                double enlargTemp = calc.getEnlargement(gEN, nod.getBoundary());
                if (enlargTemp < enlargRef || enlargRef == -1) {
                    enlargRef = enlargTemp;
                    indexEnlarg = i;
                }
            }
            assert candidate.checkInternal() : "chooseSubtree : end candidate not conform.";
            assert candidate.getChild(indexEnlarg).checkInternal() : "chooseSubtree : choosen child not conform.";
            return candidate.getChild(indexEnlarg);
        }
    }

    /**
     * Travel down {@code Tree}, find {@code Envelope} entry if it exist
     * and delete it.
     *
     * <blockquote><font size=-1> <strong>NOTE: Moreover {@code Tree} is
     * condensate after a deletion to stay conform about R-Tree
     * properties.</strong> </font></blockquote>
     *
     * @param candidate {@code Node} where to delete.
     * @param entry {@code Envelope} to delete.
     * @throws IllegalArgumentException if candidate or entry is null.
     * @return true if entry is find and deleted else false.
     */
    private static boolean deleteHilbertNode(final Node candidate, final Object object, final double... coordinates) throws StoreIndexException, IOException{
        ArgumentChecks.ensureNonNull("deleteHilbertNode Node candidate : ", candidate);
        ArgumentChecks.ensureNonNull("deleteHilbertNode Envelope coordinates : ", coordinates);
        assert candidate.checkInternal() : "chooseSubtree : begin candidate not conform.";
        if (intersects(candidate.getBoundary(), coordinates, true)) {
            if (candidate.isLeaf()) {
                boolean removed = false;
                for (int i = candidate.getChildCount() - 1; i >= 0; i--) {
                    final Node cuCell = candidate.getChild(i);
                    if (!cuCell.isEmpty() && intersects(cuCell.getBoundary(), coordinates, true)) {
                        for (int j = cuCell.getCoordsCount() - 1; j >= 0; j--) {
                            if (Arrays.equals(coordinates, cuCell.getCoordinate(j))
                             && cuCell.getObject(j).equals(object)) {
                                cuCell.removeCoordinate(j);
                                cuCell.removeObject(j);
                                removed = true;
                                break;
                            }
                        }
                    }
                    if (removed) break; // object found 
                }
                if (removed) {
                    final AbstractTree tree = ((AbstractTree)candidate.getTree());
                    tree.setElementsNumber(tree.getElementsNumber()-1);
                    candidate.setBound(null);
                    trim(candidate);
                    return true;
                }
            } else {
                for (int i = candidate.getChildCount() - 1; i >= 0; i--) {
                    final  boolean removed = deleteHilbertNode(candidate.getChild(i), object, coordinates);
                    if (removed) return true;
                }
            }
        }
        return false;
    }
    
     /**
     * Travel down {@code Tree}, find {@code Envelope} entry if it exist
     * and delete it.
     *
     * <blockquote><font size=-1> <strong>NOTE: Moreover {@code Tree} is
     * condensate after a deletion to stay conform about R-Tree
     * properties.</strong> </font></blockquote>
     *
     * @param candidate {@code Node} where to delete.
     * @param entry {@code Envelope} to delete.
     * @throws IllegalArgumentException if candidate or entry is null.
     * @return true if entry is find and deleted else false.
     */
    private static boolean removeHilbertNode(final Node candidate, final Object object, final double... coordinates) throws StoreIndexException, IOException{
        ArgumentChecks.ensureNonNull("deleteHilbertNode Node candidate : ", candidate);
        ArgumentChecks.ensureNonNull("deleteHilbertNode Envelope coordinates : ", coordinates);
        assert candidate.checkInternal() : "chooseSubtree : begin candidate not conform.";
        if (intersects(candidate.getBoundary(), coordinates, true)) {
            if (candidate.isLeaf()) {
                boolean removed = false;
                for (int i = candidate.getChildCount() - 1; i >= 0; i--) {
                    final Node cuCell = candidate.getChild(i);
                    if (!cuCell.isEmpty() && intersects(cuCell.getBoundary(), coordinates, true)) {
                        for (int j = cuCell.getCoordsCount() - 1; j >= 0; j--) {
                            if (Arrays.equals(coordinates, cuCell.getCoordinate(j))
                             && cuCell.getObject(j).equals(object)) {// by reference
                                cuCell.removeCoordinate(j);
                                cuCell.removeObject(j);
                                removed = true;
                                break;
                            }
                        }
                    }
                    if (removed) break; // object found 
                }
                if (removed) {
                    final AbstractTree tree = ((AbstractTree)candidate.getTree());
                    tree.setElementsNumber(tree.getElementsNumber()-1);
                    candidate.setBound(null);
                    trim(candidate);
                    return true;
                }
            } else {
                for (int i = candidate.getChildCount() - 1; i >= 0; i--) {
                    final  boolean removed = deleteHilbertNode(candidate.getChild(i), object, coordinates);
                    if (removed) return true;
                }
            }
        }
        return false;
    }

    /**
     * Method which permit to condense R-Tree. Condense made begin by leaf and
     * travel up to tree trunk.
     *
     * @param candidate {@code Node} to begin condense.
     */
    public static void trim(final Node candidate) throws IllegalArgumentException, IOException, StoreIndexException {
        if (!candidate.isLeaf()) {
            for (int i = candidate.getChildCount() - 1; i >= 0; i--) {
                final Node child = candidate.getChild(i);
                if (child.isEmpty()) {
                    candidate.removeChild(i);
                } else if (child.getChildCount() == 1 && !child.isLeaf()) {
                    final Node subChild = child.removeChild(0);
                    candidate.removeChild(i);
                    subChild.setParent(candidate);
                    candidate.addChild(subChild);
                }
            }

            final HilbertRTree tree = (HilbertRTree) candidate.getTree();
            final int eltsNumber = countEltsInHilbertNode(candidate, 0);
            if (eltsNumber <= tree.getMaxElements() * Math.pow(2, tree.getHilbertOrder() * 2) && eltsNumber != 0) {
                final List<Object> listObjects = new ArrayList<Object>();
                final List<double[]> listCoordinates = new ArrayList<double[]>();
                getElements(candidate, listObjects, listCoordinates);
                final double[] bound = getEnvelopeMin(listCoordinates);
                final Node parent = candidate.getParent();
                if (parent != null) {
                    // work on candidateParent
                    final int parentChildCount = parent.getChildCount();
                    boolean found = false;
                    for (int i = parentChildCount - 1; i >= 0; i--) {
                        if (parent.getChild(i) == candidate) {
                            parent.removeChild(i);
                            found = true;
                            break;
                        }
                    }
                    assert found : "trim : candidate not found from parent child list.";
                    parent.addChild(new HilbertNode(tree, parent, getLowerCorner(bound), getUpperCorner(bound), null, listObjects.toArray(new Object[listObjects.size()]), listCoordinates.toArray(new double[listCoordinates.size()][])));
                } else {
                    //set new root
                    tree.setRoot(new HilbertNode(tree, null, getLowerCorner(bound), getUpperCorner(bound), null, listObjects.toArray(new Object[listObjects.size()]), listCoordinates.toArray(new double[listCoordinates.size()][])));
                }
            }
        }
        if (candidate.getParent() != null) trim(candidate.getParent());
    }
    
    /**
     * Travel tree from stipulate {@code Node} and fill in same time the 2 lists.<br/>
     * Find all elements stored in sub-leaf of this {@code Node}.
     * 
     * @param candidate 
     * @param listObjects all objects stored in candidate Node leafs.
     * @param listCoordinates all objects boundary stored in candidate sub-node leafs.
     */
    private static void getElements(final Node candidate, final List<Object> listObjects, final List<double[]> listCoordinates) throws IOException {
        final int size = candidate.getChildCount();
        if (candidate.isLeaf()) {
            for (int i = 0; i < size; i++) {
                //may contain empty cell
                final Node cuCell = candidate.getChild(i);
                if (!cuCell.isEmpty()) {
                    for (int j = 0, sc = cuCell.getCoordsCount(); j < sc; j++) {
                        listObjects.add(cuCell.getObject(j));
                        listCoordinates.add(cuCell.getCoordinate(j));
                    }
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                getElements(candidate.getChild(i), listObjects, listCoordinates);
            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Node createNode(Tree tree, Node parent, Node[] children, Object[] objects, double[][] coordinates) throws IllegalArgumentException, IOException {
        if (!(tree instanceof HilbertRTree)) {
            throw new IllegalArgumentException("argument tree : "+tree.getClass().getName()+" not adapted to create an Hilbert RTree Node");
        }
        return new HilbertNode(tree, parent, null, null, children, objects, coordinates);
    }
}
