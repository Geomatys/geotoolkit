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
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.*;
import org.geotoolkit.index.tree.calculator.Calculator;
import org.geotoolkit.index.tree.NodeFactory;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.index.tree.mapper.TreeElementMapper;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Create R*Tree.
 *
 * @author Rémi Maréchal (Geomatys)
 * @author Johann Sorel  (Geomatys).
 */
public class StarRTree extends AbstractTree {

    /**
     * In accordance with R*Tree properties.
     * To avoid unnecessary split permit to
     * reinsert some elements just one time.
     */
    boolean insertAgain = true;

    /**
     * Create a R*Tree using default node factory.
     *
     * @param nbMaxElement : max elements number within each tree Node.
     * @param crs          : associate coordinate system.
     * @param nodefactory  : made to create tree {@code Node}.
     * @return R*Tree.
     */
    public StarRTree(int nbMaxElement, CoordinateReferenceSystem crs, TreeElementMapper treeEltMap) {
        this(nbMaxElement, crs, DefaultNodeFactory.INSTANCE, treeEltMap);
    }

    /**
     * Create a R*Tree.
     *
     * @param nbMaxElement : max elements number within each tree Node.
     * @param crs          : associate coordinate system.
     * @param nodefactory  : made to create tree {@code Node}.
     * @return R*Tree.
     */
    @Deprecated
    public StarRTree(int nbMaxElement, CoordinateReferenceSystem crs, NodeFactory nodefactory, TreeElementMapper treeEltMap) {
        super(nbMaxElement, crs, nodefactory, treeEltMap);
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public int[] searchID(double[] regionSearch) throws StoreIndexException {
        ArgumentChecks.ensureNonNull("search : region search", regionSearch);
        final Node root = this.getRoot();
        if (root != null && !root.isEmpty()) {
            try {
                currentLength   = 100;
                currentPosition = 0;
                tabSearch       = new int[currentLength];
                nodeSearch(root, regionSearch);
                return Arrays.copyOf(tabSearch, currentPosition);
            } catch (IOException ex) {
                throw new StoreIndexException(ex);
            }
        }
        return null;
    }
        
    /**
     * {@inheritDoc }.
     */
    @Override
    public void insert(Object object, double... coordinates) throws StoreIndexException {
//        super.insert(object, coordinates);
        super.eltCompteur++;
        final Node root       = getRoot();
        try{
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
//    public boolean delete(Object object, double... coordinates) throws StoreIndexException {
//        ArgumentChecks.ensureNonNull("remove : object", object);
//        ArgumentChecks.ensureNonNull("remove : coordinates", coordinates);
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
    public boolean remove(Object object, double... coordinates) throws StoreIndexException {
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
    
//    /**
//     * Find all {@code Envelope} which intersect regionSearch parameter in {@code Node}.
//     *
//     * @param candidate current Node
//     * @param regionSearch area of search.
//     * @param result {@code List} where is add search resulting.
//     */
//    private static TreeVisitorResult nodeSearch(final Node candidate, final double[] regionSearch, final TreeVisitor visitor) throws IOException {
//        final TreeVisitorResult tvr = visitor.filter(candidate);
//        if (isTerminate(tvr)) return tvr;
//        final double[] bound = candidate.getBoundary();
//        if(bound != null){
//            if(regionSearch == null) {
//                if(candidate.isLeaf()) {
//                    for(int i = 0; i < candidate.getObjectCount(); i++) {
//                        final Object obj = candidate.getObject(i);
//                        final TreeVisitorResult tvrTemp = visitor.visit(obj);
//                        if (isTerminate(tvrTemp)) return tvrTemp;
//                        if (isSkipSibling(tvrTemp)) break;
//                    }
//                } else {
//                    if (!isSkipSubTree(tvr)) {
//                        for (int i = 0, s = candidate.getChildCount(); i < s; i++) {
//                            final TreeVisitorResult tvrTemp = nodeSearch(candidate.getChild(i), null, visitor);
//                            if (isTerminate(tvrTemp)) return tvrTemp;
//                            if (isSkipSibling(tvrTemp)) break;
//                        }
//                    }
//
//                }
//            } else {
//                if(contains(regionSearch, bound, true)) {
//                    nodeSearch(candidate, null, visitor);
//                } else if (intersects(regionSearch, bound, true)) {
//                    if (candidate.isLeaf()) {
//                        for(int i = 0, s = candidate.getCoordsCount(); i < s; i++) {
//                            final double[] coords = candidate.getCoordinate(i);
//                            TreeVisitorResult tvrTemp = null;
//                            if (intersects(regionSearch, coords, true)) {
//                                tvrTemp = visitor.visit(candidate.getObject(i));
//                            }
//                            if(tvrTemp != null){
//                                if (isTerminate(tvrTemp)) return tvrTemp;
//                                if (isSkipSibling(tvrTemp)) break;
//                            }
//                        }
//                    }else{
//                        if(!isSkipSubTree(tvr)){
//                            for(int i = 0, s = candidate.getChildCount(); i < s; i++) {
//                                final TreeVisitorResult tvrTemp = nodeSearch(candidate.getChild(i), regionSearch, visitor);
//                                if (isTerminate(tvrTemp)) return tvrTemp;
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

    /**Insert new {@code Entry} in branch and re-organize {@code Node} if it's necessary.
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
    private static void nodeInsert(final Node candidate, final Object object, final double[] coordinate ) throws StoreIndexException, IOException {
        assert candidate.checkInternal() : "nodeInsert : candidate not conform";
        if (candidate.isLeaf()) {
            candidate.addElement(object, coordinate);
        }else{
            nodeInsert(chooseSubTree(candidate, coordinate), object, coordinate);
            candidate.setBound(null);
        }
        assert candidate.checkInternal() : "nodeInsert : after insert candidate not conform";
        final StarRTree tree = (StarRTree) candidate.getTree();
        final int maxElmts = tree.getMaxElements();
        if (DefaultTreeUtils.countElements(candidate) > maxElmts && tree.getIA()) {
            tree.setIA(false);
            final LinkedList<Object> listObjects  = new LinkedList<Object>();
            final LinkedList<double[]> listCoords = new LinkedList<double[]>();
            getElementAtMore33PerCent(candidate, listObjects, listCoords);
            final int siz = listCoords.size();
            assert (siz == listObjects.size()) :"getElementAtMore33Percent : nodeInsert : lists should have same size.";
            for (int i = 0; i< siz; i++) {
                deleteNode(candidate, listObjects.get(i), listCoords.get(i));
            }
            for (int i = 0; i< siz; i++) {
                tree.insert(listObjects.get(i), listCoords.get(i));
            }
            tree.setIA(true);
        }
        assert candidate.checkInternal() : "nodeInsert : after insert again candidate not conform";
        if (!candidate.isLeaf()) {
            if (candidate.getChild(0).isLeaf()) {
                for (int i = 0, l1 = candidate.getChildCount() - 1; i < l1; i++) {
                    for (int j = i + 1; j < l1 + 1; j++) {
                        final Node nodeA = candidate.getChild(i);
                        final Node nodeB = candidate.getChild(j);
                        if(//new GeneralEnvelope(nodeA.getBoundary()).intersects(nodeB.getBoundary(), false)
                                intersects(nodeA.getBoundary(), nodeB.getBoundary(), false)
                                && nodeA.isLeaf() && nodeB.isLeaf()
                                &&nodeA.getCoordsCount() > 1 && nodeB.getCoordsCount() > 1){
                            branchGrafting(nodeA, nodeB);
                        }
                    }
                }
            }
            for (int i = 0,len = candidate.getChildCount(); i < len; i++) {
                if (DefaultTreeUtils.countElements(candidate.getChild(i)) > candidate.getTree().getMaxElements()) {
                    final Node child = candidate.removeChild(i);
                    final List<Node> l = nodeSplit(child);
                    final Node l0 = l.get(0);
                    final Node l1 = l.get(1);
                    l0.setParent(candidate);
                    l1.setParent(candidate);
                    candidate.addChild(l0);
                    candidate.addChild(l1);
                }
            }
        }
        assert candidate.checkInternal() : "nodeInsert : after split or branch grafting candidate not conform";
        if (candidate.getParent() == null) {
            if (DefaultTreeUtils.countElements(candidate) > candidate.getTree().getMaxElements()) {
                List<Node> l = nodeSplit(candidate);
                final Node l0 = l.get(0);
                final Node l1 = l.get(1);
                l0.setParent(candidate);
                l1.setParent(candidate);
                candidate.clear();
                candidate.addChild(l0);
                candidate.addChild(l1);
            }
        }
        assert candidate.checkInternal() : "nodeInsert : at end candidate not conform";
    }
    
    /**
     * Find appropriate {@code Node} to insert {@code Envelope} entry.
     * <blockquote><font size=-1>
     * <strong>To define appropriate Node, R*Tree criterion are :
     *      - require minimum area enlargement to cover {@code Envelope} entry.
     *      - or put into Node with lesser elements number in case area equals.
     * </strong>
     * </font></blockquote>
     *
     * @param parent Find in its children {@code Node}.
     * @param entry {@code Envelope} to add.
     * @throws IllegalArgumentException if {@code Node} listSubnode is empty.
     * @return {@code Node} which will be appropriate to contain entry.
     */
    private static Node chooseSubTree(final Node parent, final double[] coordinate) throws IOException {
        assert parent.checkInternal() : "chooseSubTree : begin candidate not conform";
        final Node[] childrenList = parent.getChildren();

        if (childrenList == null) {
            throw new IllegalArgumentException("impossible to find subtree from empty list");
        }

        final int size = parent.getChildCount();
        if (size == 1) {
            return childrenList[0];
        }
        final Calculator calc = parent.getTree().getCalculator();
        if (childrenList[0].isLeaf()) {
            final List<Node> listOverZero = new ArrayList<Node>();
            double overlapsRef = -1;
            int index = -1;
            double overlapsTemp = 0;
            for (int i = 0; i < size; i++) {
                final double[] gnTemp = childrenList[i].getBoundary().clone();
                add(gnTemp, coordinate);
                for (int j = 0; j < size; j++) {
                    if (i != j) {
                        overlapsTemp += calc.getOverlaps(gnTemp, childrenList[j].getBoundary());
                    }
                }
                if (overlapsTemp == 0) {
                    listOverZero.add(childrenList[i]);
                } else {
                    if ((overlapsTemp < overlapsRef) || overlapsRef == -1) {
                        overlapsRef = overlapsTemp;
                        index = i;
                    } else if (overlapsTemp == overlapsRef) {
                        if (DefaultTreeUtils.countElements(childrenList[i]) < DefaultTreeUtils.countElements(childrenList[index])) {
                            overlapsRef = overlapsTemp;
                            index = i;
                        }
                    }
                }
                overlapsTemp = 0;
            }
            if (!listOverZero.isEmpty()) {
                double areaRef = -1;
                int indexZero = -1;
                double areaTemp;
                double[] entryBound ;
                for (int i = 0, s = listOverZero.size(); i<s; i++) {
                    entryBound = coordinate.clone();
                    add(entryBound, listOverZero.get(i).getBoundary());
                    areaTemp = calc.getEdge(entryBound);
                    if (areaTemp < areaRef || areaRef == -1) {
                        areaRef = areaTemp;
                        indexZero = i;
                    }
                }
                return listOverZero.get(indexZero);
            }
            if (index == -1) {
                throw new IllegalStateException("chooseSubTree : no subLeaf find");
            }
            return childrenList[index];
        }

        for (int i = 0; i < size; i++) {
            final Node no = childrenList[i];
            if (contains(no.getBoundary(), coordinate, true)) {
                return no;
            }
        }

        double enlargRef = -1;
        int indexEnlarg = -1;
        for(int i = 0; i < size; i++) {
            final Node n3d = childrenList[i];
            final double[] gEN = n3d.getBoundary().clone();
            final double[] GE = gEN.clone();
            add(GE, coordinate);
            double enlargTemp = calc.getEnlargement(gEN, GE);
            if (enlargTemp < enlargRef || enlargRef == -1) {
                enlargRef   = enlargTemp;
                indexEnlarg = i;
            }
        }
        assert parent.checkInternal() : "chooseSubTree : end candidate not conform";
        return childrenList[indexEnlarg];
    }
    
    /**
     * Compute and define how to split {@code Node} candidate.
     *
     * <blockquote><font size=-1>
     * <strong>NOTE: To choose which {@code Node} couple, split algorithm sorts the entries (for tree leaf), or {@code Node} (for tree branch)
     *               in accordance to their lower or upper boundaries on the selected dimension (see defineSplitAxis method) and examines all possible divisions.
     *               Two {@code Node} resulting, is the final division which has the minimum overlaps between them.</strong>
     * </font></blockquote>
     *
     * @return Two appropriate {@code Node} in List in accordance with R*Tree split properties.
     */
    private static List<Node> nodeSplit(final Node candidate) throws IllegalArgumentException, IOException {
        assert candidate.checkInternal() : "nodeSplit : begin candidate not conform";
        final int splitIndex = defineSplitAxis(candidate);
        final boolean isLeaf = candidate.isLeaf();
        final Tree tree = candidate.getTree();
        final Calculator calc = tree.getCalculator();
        List eltList = new ArrayList();
        List<Object> listObjects = null;
        
        if (isLeaf) {
            listObjects = new ArrayList<Object>();
            for (int i = 0, s = candidate.getCoordsCount(); i < s; i++) {
                eltList.add(candidate.getCoordinate(i));
                listObjects.add(candidate.getObject(i));
            }
        } else {
            for (int i = 0, s = candidate.getChildCount(); i < s; i++) {
                eltList.add(candidate.getChild(i));
            }
        }
        final int size = eltList.size();
        final double size04 = size * 0.4;
        final int demiSize = (int) ((size04 >= 1) ? size04 : 1);
        final List splitListA = new ArrayList();
        final List splitListB = new ArrayList();
        double[] gESPLA, gESPLB;
        double bulkTemp;
        double bulkRef = -1;
        int index = 0;
        boolean lower_or_upper = true;
        int cut2;
        
        List<Integer> cutList = new ArrayList<Integer>();
        List<Boolean> luList  = new ArrayList<Boolean>();
        List<double[]> gESPLAList = new ArrayList<double[]>();
        List<double[]> gESPLBList = new ArrayList<double[]>();
        
        //compute with lower and after upper
        for(int lu = 0; lu < 2; lu++) {
            calc.sortList(splitIndex, (lu == 0), eltList, listObjects);
            for(int cut = demiSize; cut <= size - demiSize; cut++) {
                for(int i = 0; i < cut; i++) {
                    splitListA.add(eltList.get(i));
                }
                for(int j = cut; j < size; j++) {
                    splitListB.add(eltList.get(j));
                }
                cut2 = size - cut;
                if (isLeaf) {
                    gESPLA = ((double[]) splitListA.get(0)).clone();
                    gESPLB = ((double[]) splitListB.get(0)).clone();
                    for(int i = 1; i < cut; i++) {
                        add(gESPLA, (double[])splitListA.get(i));
                    }
                    for(int i = 1; i < cut2; i++) {
                        add(gESPLB, (double[])splitListB.get(i));
                    }
                } else {
                    gESPLA = ((Node)splitListA.get(0)).getBoundary().clone();
                    gESPLB = ((Node)splitListB.get(0)).getBoundary().clone();
                    for (int i = 1; i < cut; i++) {
                        add(gESPLA, ((Node)splitListA.get(i)).getBoundary());
                    }
                    for (int i = 1; i < cut2; i++) {
                        add(gESPLB, ((Node)splitListB.get(i)).getBoundary());
                    }
                }
                bulkTemp = calc.getOverlaps(gESPLA, gESPLB);
                //get best index and lower are upper
                if (bulkTemp < bulkRef || bulkRef == -1) {
                    bulkRef = bulkTemp;
                    index = cut;
                    lower_or_upper = (lu == 0);
                }else if (bulkTemp == 0) {
                    cutList.add(cut);
                    luList.add((lu == 0));
                    gESPLAList.add(gESPLA);
                    gESPLBList.add(gESPLB);
                }
                splitListA.clear();
                splitListB.clear();
            }
        }
        
        if (!cutList.isEmpty()) {
            final int s = cutList.size();
            //paranoiac assert
            assert (s                    == luList.size() 
                    && gESPLAList.size() == gESPLBList.size() 
                    && s                 == gESPLAList.size()) : "splitNode : couple lists haven't got same size";
            double areaRef = -1;
            double areaTemp;
            for(int id = 0; id < s; id++) {
                areaTemp = calc.getEdge(gESPLAList.get(id)) + calc.getEdge(gESPLBList.get(id));
                if(areaTemp < areaRef || areaRef == -1) {
                    areaRef = areaTemp;
                    index = cutList.get(id);
                    lower_or_upper = luList.get(id);
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
        
        final int maxElts = tree.getMaxElements();
        if (isLeaf) {
            assert objA.length <= maxElts :"objA length should be lesser than max size permit by tree.";
            assert objB.length <= maxElts :"objB length should be lesser than max size permit by tree.";
            assert coordA.length <= maxElts :"coordA lengèth should be lesser than max size permit by tree.";
            assert coordB.length <= maxElts :"coordB length should be lesser than max size permit by tree.";
        } else {
            assert nodeA.length <= maxElts :"nodeA length should be lesser than max size permit by tree.";
            assert nodeB.length <= maxElts :"nodeB length should be lesser than max size permit by tree.";
        }
        
        if(isLeaf) return UnmodifiableArrayList.wrap(new Node[] {tree.createNode(tree, null, null, objA, coordA), tree.createNode(tree, null, null, objB, coordB)});
        final Node resultA = (Node) ((nodeA.length == 1) ? nodeA[0] : tree.createNode(tree, null, nodeA, null, null));
        final Node resultB = (Node) ((nodeB.length == 1) ? nodeB[0] : tree.createNode(tree, null, nodeB, null, null));
        return UnmodifiableArrayList.wrap(new Node[] {resultA, resultB});
    }

    /**Compute and define which axis to split {@code Node} candidate.
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
    private static int defineSplitAxis(final Node candidate) throws IOException {
        ArgumentChecks.ensureNonNull("candidate : ", candidate);
        assert candidate.checkInternal() : "defineSplitAxis : begin candidate not conform";
        
        final boolean isLeaf = candidate.isLeaf();
        List<double[]> eltList = new ArrayList<double[]>();
        if (isLeaf) {
            final int coordCount = candidate.getCoordsCount();
            for (int i = 0; i < coordCount; i++) {
                eltList.add(candidate.getCoordinate(i));
            }
        } else {
            final int chCount = candidate.getChildCount();
            for (int i = 0; i < chCount; i++) {
                eltList.add(candidate.getChild(i).getBoundary());
            }
        }
        
        final AbstractTree tree = (AbstractTree)candidate.getTree();
        final Calculator calc = tree.getCalculator();
        final int size = eltList.size();
        final double size04 = size * 0.4;
        final int demiSize = (int) ((size04 >= 1)?size04:1);
        final List splitListA = new ArrayList();
        final List splitListB = new ArrayList();
        double[] gESPLA, gESPLB;
        double bulkTemp;
        double bulkRef = Double.POSITIVE_INFINITY;
        int index = 0;
        final double[] globalEltsArea = getEnvelopeMin(eltList);
        final int dim = globalEltsArea.length/2;//decal bit
        
        // if glogaleArea.span(currentDim) == 0 || if all elements have same span
        // value as global area on current ordinate, impossible to split on this axis.
        unappropriateOrdinate :
        for (int indOrg = 0; indOrg < dim; indOrg++) {
            final double globalSpan = getSpan(globalEltsArea, indOrg);
            boolean isSameSpan = true;
            //check if its possible to split on this currently ordinate.
            for (double[] elt : eltList) {
                if (!(Math.abs(getSpan(elt, indOrg) - globalSpan) <= 1E-9)) {
                    isSameSpan = false;
                    break;
                }
            }
            if (globalSpan <= 1E-9 || isSameSpan) continue unappropriateOrdinate;
            bulkTemp = 0;
            for (int left_or_right = 0; left_or_right < 2; left_or_right++) {
                if (left_or_right == 0) calc.sortList(indOrg, true, eltList, null);
                else calc.sortList(indOrg, false, eltList, null);
                for (int cut = demiSize, sdem = size - demiSize; cut <= sdem; cut++) {
                    splitListA.clear();
                    splitListB.clear();
                    for (int i = 0; i < cut; i++)  splitListA.add(eltList.get(i));
                    for (int j = cut; j < size; j++) splitListB.add(eltList.get(j));
                    gESPLA     = getEnvelopeMin(splitListA);
                    gESPLB     = getEnvelopeMin(splitListB);
                    bulkTemp  += calc.getEdge(gESPLA);
                    bulkTemp  += calc.getEdge(gESPLB);
                }
            }
            if(bulkTemp < bulkRef) {
                bulkRef = bulkTemp;
                index = indOrg;
            }
        }
        assert candidate.checkInternal() : "defineSplitAxis : end candidate not conform";
        return index;
    }

    /**
     * Travel {@code Tree}, find {@code Envelope} entry if it exist and delete it.
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
    private static boolean deleteNode(final Node candidate, final Object object, final double... coordinates) throws StoreIndexException, IOException {
        ArgumentChecks.ensureNonNull("DeleteNode : Node candidate", candidate);
        ArgumentChecks.ensureNonNull("DeleteNode : object", object);
        ArgumentChecks.ensureNonNull("DeleteNode : coordinates", coordinates);
        assert candidate.checkInternal() : "deleteNode : begin candidate not conform";
        
        if(intersects(candidate.getBoundary(), coordinates, true)) {
            if(candidate.isLeaf()) {
                boolean removed = false;
                // paranoiac assert
                final int sc = candidate.getCoordsCount();
                assert (sc == candidate.getObjectCount()) :"deleteNode : coordinates and objects stored should have same size.";
                for (int i = sc - 1; i >= 0; i--) {
                    if (Arrays.equals(candidate.getCoordinate(i), coordinates)) { // faire un && logic
                        if (candidate.getObject(i).equals(object)) {
                            removed = true;
                            candidate.removeCoordinate(i);
                            candidate.removeObject(i);
                            break;// found
                        }
                    }
                }
                
                if (removed) {
                    final AbstractTree tree = ((AbstractTree)candidate.getTree());
                    tree.setElementsNumber(tree.getElementsNumber() - 1);
                    trim(candidate);
                    return true;
                }
            } else {
                for(int i = candidate.getChildCount() - 1; i >= 0;i--) {
                    final boolean removed = deleteNode(candidate.getChild(i), object, coordinates);
                    if(removed) return true;
                }
            }
            assert candidate.checkInternal() : "deleteNode : at end after trim candidate not conform";
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
    private static boolean removeNode(final Node candidate, final Object object, final double... coordinates) throws StoreIndexException, IOException{
        ArgumentChecks.ensureNonNull("removeNode : Node candidate", candidate);
        ArgumentChecks.ensureNonNull("removeNode : Node candidate", candidate);
        assert candidate.checkInternal() : "removeNode : begin candidate not conform";
        
        if(intersects(candidate.getBoundary(), coordinates, true)){
            if (candidate.isLeaf()) {
                boolean removed = false;
                final int sc = candidate.getCoordsCount();
                assert (sc == candidate.getObjectCount()) :"removeNode : coordinates and objects stored should have same size.";
                for (int i = sc - 1; i >= 0; i--) {
                    if (Arrays.equals(candidate.getCoordinate(i), coordinates)) { // faire un && logic
                        if (object.equals(candidate.getObject(i))) {
                            candidate.removeCoordinate(i);
                            candidate.removeObject(i);
                            removed = true;
                            break; //found
                        }
                    }
                }
                
                if(removed) {
                    final AbstractTree tree = ((AbstractTree)candidate.getTree());
                    tree.setElementsNumber(tree.getElementsNumber()-1);
                    trim(candidate);
                    return true;
                }
            } else {
                for(int i = candidate.getChildCount() - 1; i >= 0;i--) {
                    final boolean removed = removeNode(candidate.getChild(i), object, coordinates);
                    if(removed) return true;
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
    private static void trim(final Node candidate) throws StoreIndexException, IOException {
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
                            tree.setElementsNumber(tree.getElementsNumber()-countCoords);
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
        if(candidate.getParent() != null) trim (candidate.getParent());
        // re-insert after trim
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
     * Exchange some entry(ies) between two nodes in aim to find best form with lesser overlaps.
     * Also branchGrafting will be able to avoid splitting node.
     *
     * @param nodeA Node
     * @param nodeB Node
     * @throws IllegalArgumentException if nodeA or nodeB are not tree leaf.
     * @throws IllegalArgumentException if nodeA or nodeB, and their sub-nodes, don't contains some {@code Envelope} entry(ies).
     */
    private static void branchGrafting(final Node nodeA, final Node nodeB ) throws IllegalArgumentException, IOException {
        if(!nodeA.isLeaf() || !nodeB.isLeaf()) throw new IllegalArgumentException("branchGrafting : not leaf");
        assert nodeA.getParent() == nodeB.getParent() : "branchGrafting : NodeA and NodeB should have same parent.";
        assert nodeA.getParent().checkInternal()      : "branchGrafting : nodeA and B parent not conform.";
        assert nodeA.checkInternal()                  : "branchGrafting : at begin candidate not conform";
        assert nodeB.checkInternal()                  : "branchGrafting : at begin candidate not conform";
        final int sa = nodeA.getCoordsCount();
        final int sb = nodeB.getCoordsCount();
        List<double[]> listCoords = new LinkedList<double[]>();
        List<Object> listObject = new LinkedList<Object>();
        
        for (int i = 0; i < sa; i++) {
            listCoords.add(nodeA.getCoordinate(i));
            listObject.add(nodeA.getObject(i));
        }
        for (int i = 0; i < sb; i++) {
            listCoords.add(nodeB.getCoordinate(i));
            listObject.add(nodeB.getObject(i));
        }
        
        //paranoiac assert
        final int size = listCoords.size();
        assert (size == listObject.size()) : "branch grafting : listcoords and listobjects should have same size.";
        if (size == 0) throw new IllegalArgumentException("branchGrafting : empty list");
        final AbstractTree tree = (AbstractTree)nodeA.getTree();
        final int maxEltsPermit = tree.getMaxElements();
        final Calculator calc = tree.getCalculator();
        final double[] globalE = getEnvelopeMin(listCoords);
        final int dim = globalE.length/2;//decal bit
        double lengthDimRef = Double.NEGATIVE_INFINITY;
        int indexSplit = -1;
        for (int i = 0; i < dim; i++) {
            double lengthDimTemp = getSpan(globalE, i);
            if (lengthDimTemp > lengthDimRef) {
                lengthDimRef = lengthDimTemp;
                indexSplit = i;
            }
        }
        assert indexSplit != -1 : "BranchGrafting : indexSplit not find"+ indexSplit;
        calc.sortList(indexSplit, true, listCoords, listObject);
        final double[] envA = listCoords.get(0);
        final double[] envB = listCoords.get(0);
        double overLapsRef = -1;
        int index = -1;
        final int size04 = (int)((size * 0.4 >= 1) ? size * 0.4 : 1);
        for(int cut = size04, n = size - size04; cut < n; cut++) {
            final double[] envAC = envA.clone();//system .arraycopy moin couteux
            final double[] envBC = envB.clone();
            for(int i = 1; i < cut; i++) {
                add(envAC, listCoords.get(i));
            }
            for(int i = cut + 1; i < size; i++) {
                add(envBC, listCoords.get(i));
            }
            double overLapsTemp = calc.getOverlaps(envAC, envBC);
            if(overLapsTemp < overLapsRef || overLapsRef == -1){//Double.positiveinfinity
                overLapsRef = overLapsTemp;
                index = cut;
            }
        }
        assert index != -1 : "branchGrafting : index cut out of bound";
        //index not wrong a split is better.
        if (index > maxEltsPermit || (size-index) > maxEltsPermit) return;
        // clear nodes
        nodeA.clear();
        nodeB.clear();
        
        // fill node
        for(int i = 0; i < index; i++) {
            nodeA.addElement(listObject.get(i), listCoords.get(i));
        }
        for(int i = index; i < size; i++) {
            nodeB.addElement(listObject.get(i), listCoords.get(i));
        }
        assert nodeA.getParent() == nodeB.getParent() : "branchGrafting : NodeA and NodeB should have same parent.";
        assert nodeA.getParent().checkInternal()      : "branchGrafting : nodeA and B parent not conform.";
        assert nodeA.checkInternal()                  : "branchGrafting : at end candidate not conform";
        assert nodeB.checkInternal()                  : "branchGrafting : at end candidate not conform";
    }

    /**Get statement from re-insert state.
     *
     * @return true if it's permit to re-insert else false.
     */
    private boolean getIA() {
        return insertAgain;
    }

    /**Affect statement to permit or not, re-insertion.
     * @param insertAgain
     */
    private void setIA(boolean insertAgain) {
        this.insertAgain = insertAgain;
    }
    
    /**
     * Recover lesser 33% largest of {@code Node} candidate within it.
     *
     * @throws IllegalArgumentException if {@code Node} candidate is null.
     * @return all Entry within subNodes at more 33% largest of this {@code Node}.
     */
    private static void getElementAtMore33PerCent(final Node candidate, LinkedList<Object> listObjects, final LinkedList<double[]> listCoords) throws IOException {
        ArgumentChecks.ensureNonNull("getElementAtMore33PerCent : candidate", candidate);
        ArgumentChecks.ensureNonNull("getElementAtMore33PerCent : listObjects", listObjects);
        ArgumentChecks.ensureNonNull("getElementAtMore33PerCent : listCoords", listCoords);
        assert candidate.checkInternal() : "getElementAtMore33PerCent : begin candidate not conform";
        final double[] canBound = candidate.getBoundary();
        final double[] candidateCentroid = getMedian(canBound);
        final Calculator calc = candidate.getTree().getCalculator();
        final double distPermit = calc.getDistancePoint(getLowerCorner(canBound), getUpperCorner(canBound)) / 1.666666666;
        getElementAtMore33PerCent(candidate, candidateCentroid, distPermit, listObjects, listCoords);
        assert candidate.checkInternal() : "getElementAtMore33PerCent : end candidate not conform";
    }
    
    /**
     * Recover lesser 33% largest of {@code Node} candidate within it.
     *
     * @throws IllegalArgumentException if {@code Node} candidate is null.
     * @return all Entry within subNodes at more 33% largest of this {@code Node}.
     */
    private static void getElementAtMore33PerCent(final Node candidate, double[] candidateCentroid, double distancePermit, LinkedList<Object> listObjects, final LinkedList<double[]> listCoords) throws IOException {
        ArgumentChecks.ensureNonNull("getElementAtMore33PerCent : candidateCentroid", candidateCentroid);
        ArgumentChecks.ensureStrictlyPositive("getElementsAtMore33PerCent : distancePermit", distancePermit);
        assert candidate.checkInternal() : "getElementAtMore33PerCent : begin candidate not conform";
        final Calculator calc = candidate.getTree().getCalculator();
        if (candidate.isLeaf()) {
            final int coordCount = candidate.getCoordsCount();
            for (int i = 0; i < coordCount; i++) {
                final double[] cuCoord = candidate.getCoordinate(i);
                if (calc.getDistancePoint(candidateCentroid, getMedian(cuCoord)) >= distancePermit) {
                    listObjects.add(candidate.getObject(i));
                    listCoords.add(candidate.getCoordinate(i));
                }
            }
        } else {
            final int childCount = candidate.getChildCount();
            for (int i = 0; i < childCount; i++) {
                getElementAtMore33PerCent(candidate.getChild(i), candidateCentroid, distancePermit, listObjects, listCoords);
            }
        }
        assert candidate.checkInternal() : "getElementAtMore33PerCent : begin candidate not conform";
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
        return new DefaultNode(tree, parent, dp1Coords, dp2Coords,children, objects, coordinates);
    }
}
