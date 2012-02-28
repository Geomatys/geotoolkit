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
package org.geotoolkit.index.tree.hilbert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.index.tree.*;
import org.geotoolkit.index.tree.calculator.Calculator;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.util.converter.Classes;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Create Hilbert RTree.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class HilbertRTree extends DefaultAbstractTree {
    
    
    private static final String PROP_LEAF = "isleaf";

    int hilbertOrder;
    private static final double LN2 = 0.6931471805599453;
    /**
     * Create Hilbert RTree.
     *
     * @param maxElements max elements number authorized
     * @param hilbertOrder max order value.
     * @throws IllegalArgumentException if maxElements <= 0.
     * @throws IllegalArgumentException if hilbertOrder <= 0.
     */
    public HilbertRTree(int nbMaxElement, int hilbertOrder, CoordinateReferenceSystem crs, Calculator calculator) {
        super(nbMaxElement, crs, calculator);
        ArgumentChecks.ensureStrictlyPositive("impossible to create Hilbert Rtree with order <= 0", hilbertOrder);
        this.hilbertOrder = hilbertOrder;
        setRoot(null);
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
    public void search(final GeneralEnvelope regionSearch, final List<Envelope> result) throws TransformException {
        final CoordinateReferenceSystem entryCRS = regionSearch.getCoordinateReferenceSystem();
        if (!entryCRS.equals(crs)) {
            regionSearch.setEnvelope(Envelopes.transform(regionSearch, crs));
        }
        final DefaultNode root = getRoot();
        if (!root.isEmpty() && root != null) {
            searchHilbertNode(root, regionSearch, result);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(final Envelope entry) throws MismatchedReferenceSystemException {
        if(!CRS.equalsIgnoreMetadata(crs, entry.getCoordinateReferenceSystem())){
            throw new MismatchedReferenceSystemException();
        }
        final DefaultNode root = getRoot();
        final int dim = entry.getDimension();
        final double[] coords = new double[2 * dim];
        System.arraycopy(entry.getLowerCorner().getCoordinate(), 0, coords, 0, dim);
        System.arraycopy(entry.getUpperCorner().getCoordinate(), 0, coords, dim, dim);
        if (root == null || root.isEmpty()) {
            setRoot(createNode(this, null, null, UnmodifiableArrayList.wrap(entry), coords));
        } else {
            insertNode(root, entry);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final Envelope entry) throws MismatchedReferenceSystemException {
        if(!CRS.equalsIgnoreMetadata(crs, entry.getCoordinateReferenceSystem())){
            throw new MismatchedReferenceSystemException();
        }
        deleteHilbertNode(getRoot(), entry);
    }

    /**
     * Find all {@code GeneralEnvelope} (entries) which intersect regionSearch
     * parameter.
     *
     * @param regionSearch area of search.
     * @param result {@code List} where is add search resulting.
     */
    public static void searchHilbertNode(final DefaultNode candidate, final GeneralEnvelope regionSearch, final List<Envelope> result) {
        if (regionSearch.intersects(candidate.getBoundary(), true)) {
            if (candidate.isLeaf()) {
//                final List<DefaultNode> lN = (List<DefaultNode>) candidate.getUserProperty("cells");
                final List<DefaultNode> lN = candidate.getChildren();
                for (DefaultNode n2d : lN.toArray(new DefaultNode[lN.size()])) {
                    if (!n2d.isEmpty()) {
                        if (n2d.getBoundary().intersects(regionSearch, true)) {
                            for (GeneralEnvelope sh : n2d.getEntries().toArray(new GeneralEnvelope[n2d.getEntries().size()])) {
                                if (sh.intersects(regionSearch, true)) {
                                    result.add(sh);
                                }
                            }
                        }
                    }
                }
            } else {
                for (DefaultNode nod : candidate.getChildren()) {
                    searchHilbertNode(nod, regionSearch, result);
                }
            }
        }
    }

    /**
     * Insert entry in {@code DefaultNode} in accordance with R-Tree properties.
     *
     * @param candidate {@code DefaultNode} where user want insert data.
     * @param entry to insert.
     * @throws IllegalArgumentException if candidate or entry are null.
     */
    public static void insertNode(final DefaultNode candidate, final Envelope entry) {
        ArgumentChecks.ensureNonNull("impossible to insert a null entry", entry);
        if (candidate.isFull()) {
            List<DefaultNode> lSp = splitNode(candidate);
            if (lSp != null) {
                final DefaultNode lsp0 = lSp.get(0);
                final DefaultNode lsp1 = lSp.get(1);
                ((List<DirectPosition>) candidate.getUserProperty("centroids")).clear();
                candidate.getChildren().clear();
                candidate.setUserProperty("isleaf", false);
                candidate.setUserProperty("hilbertOrder", 0);
                lsp0.setParent(candidate);
                lsp1.setParent(candidate);
                candidate.getChildren().add(lSp.get(0));
                candidate.getChildren().add(lSp.get(1));
            }
        }
        if (candidate.isLeaf()) {
            if ((!candidate.getBoundary().contains(entry, true))) {
                List<Envelope> lS = new ArrayList<Envelope>();
                searchHilbertNode(candidate, candidate.getBoundary(), lS);
                lS.add(entry);
                GeneralEnvelope envelope = DefaultTreeUtils.getEnveloppeMin(lS);
                candidate.getTree().getCalculator().createBasicHL(candidate, (Integer) candidate.getUserProperty("hilbertOrder"), envelope);
                for (Envelope sh : lS) {
                    candidate.setBound(envelope);
                    chooseSubtree(candidate, entry).getEntries().add(sh);
                }
            } else {
                chooseSubtree(candidate, entry).getEntries().add(entry);
                
            }
        } else {
            insertNode(chooseSubtree(candidate, entry), entry);
//            candidate.setBound(null);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if this {@code Node} contains lesser two
     * subnode.
     * @throws IllegalArgumentException if this {@code Node} doesn't contains {@code Entry}.
     */
    public static List<DefaultNode> splitNode(final DefaultNode candidate) {
        boolean cleaf = candidate.isLeaf();
        int cHO = (Integer) candidate.getUserProperty("hilbertOrder");
        final Calculator calc = candidate.getTree().getCalculator();
        if (candidate.getChildren().size() < 2 && !cleaf) {
            throw new IllegalStateException("impossible to split node with lesser two subnode");
        }
        if (cleaf && (cHO < ((HilbertRTree) candidate.getTree()).getHilbertOrder())) {

            final List<Envelope> lS = new ArrayList<Envelope>();
            searchHilbertNode(candidate, candidate.getBoundary(), lS);
            if (lS.isEmpty()) {
                throw new IllegalStateException("impossible to increase Hilbert order of a empty Node");
            }
            final GeneralEnvelope boundT = DefaultTreeUtils.getEnveloppeMin(lS);
            calc.createBasicHL(candidate, cHO + 1, boundT);
            for (Envelope sh : lS) {
                candidate.setBound(boundT);
                chooseSubtree(candidate, sh).getEntries().add(sh);
            }
            return null;
        } else {
            final List<DefaultNode> lS = hilbertNodeSplit(candidate);
            return lS;
        }
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
    private static int defineSplitAxis(final DefaultNode candidate) {
        ArgumentChecks.ensureNonNull("defineSplitAxis : ", candidate);
        final boolean isLeaf = candidate.isLeaf();
        List eltList;
        if (isLeaf) {
//            final List<DefaultNode> ldf = ((List<DefaultNode>) candidate.getUserProperty("cells"));
            final List<DefaultNode> ldf = candidate.getChildren();
            eltList = new ArrayList();
            for (DefaultNode dn : ldf) {
                eltList.addAll(dn.getEntries());
            }
        } else {
            eltList = candidate.getChildren();
        }
        Calculator calc = candidate.getTree().getCalculator();
        final int dim = candidate.getBoundary().getDimension();
        final int size = eltList.size();
        final double size04 = size * 0.4;
        final int demiSize = (int) ((size04 >= 1) ? size04 : 1);
        final List splitListA = new ArrayList();
        final List splitListB = new ArrayList();
        GeneralEnvelope gESPLA, gESPLB;
        double bulkTemp, bulkRef = -1;
        int index = 0;
        int cut2;
        Comparator comp;
        for (int indOrg = 0; indOrg < dim; indOrg++) {
            bulkTemp = 0;
            for (int left_or_right = 0; left_or_right < 2; left_or_right++) {
                if (isLeaf) {
                    if (left_or_right == 0) {
                        comp = calc.sortFrom(indOrg, true, false);
                    } else {
                        comp = calc.sortFrom(indOrg, false, false);
                    }
                } else {
                    if (left_or_right == 0) {
                        comp = calc.sortFrom(indOrg, true, true);
                    } else {
                        comp = calc.sortFrom(indOrg, false, true);
                    }
                }
                Collections.sort(eltList, comp);
                for (int cut = demiSize, sdem = size - demiSize; cut <= sdem; cut++) {
                    splitListA.clear();
                    splitListB.clear();
                    for (int i = 0; i < cut; i++) {
                        splitListA.add(eltList.get(i));
                    }
                    for (int j = cut; j < size; j++) {
                        splitListB.add(eltList.get(j));
                    }
                    cut2 = size - cut;
                    if (isLeaf) {
                        gESPLA = new GeneralEnvelope((GeneralEnvelope) splitListA.get(0));
                        gESPLB = new GeneralEnvelope((GeneralEnvelope) splitListB.get(0));
                        for (int i = 1; i < cut; i++) {
                            gESPLA.add((GeneralEnvelope) splitListA.get(i));
                        }
                        for (int i = 1; i < cut2; i++) {
                            gESPLB.add((GeneralEnvelope) splitListB.get(i));
                        }
                    } else {
                        gESPLA = new GeneralEnvelope(((DefaultNode) splitListA.get(0)).getBoundary());
                        gESPLB = new GeneralEnvelope(((DefaultNode) splitListB.get(0)).getBoundary());
                        for (int i = 1; i < cut; i++) {
                            gESPLA.add(((DefaultNode) splitListA.get(i)).getBoundary());
                        }
                        for (int i = 1; i < cut2; i++) {
                            gESPLB.add(((DefaultNode) splitListB.get(i)).getBoundary());
                        }
                    }
                    bulkTemp += calc.getEdge(gESPLA);
                    bulkTemp += calc.getEdge(gESPLB);
                }
            }
            if (indOrg == 0) {
                bulkRef = bulkTemp;
                index = indOrg;
            } else {
                if (bulkTemp < bulkRef) {
                    bulkRef = bulkTemp;
                    index = indOrg;
                }
            }
        }
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
     * @return Two appropriate {@code DefaultNode} in List in accordance with
     * R*Tree split properties.
     */
    private static List<DefaultNode> hilbertNodeSplit(final DefaultNode candidate) {

        final int splitIndex = defineSplitAxis(candidate);
        final boolean isLeaf = candidate.isLeaf();
        final Tree tree = candidate.getTree();
        final Calculator calc = tree.getCalculator();
        List eltList;
        if (isLeaf) {
//            final List<DefaultNode> ldf = ((List<DefaultNode>) candidate.getUserProperty("cells"));
            final List<DefaultNode> ldf = candidate.getChildren();
            eltList = new ArrayList();
            for (DefaultNode dn : ldf) {
                eltList.addAll(dn.getEntries());
            }
        } else {
            eltList = candidate.getChildren();
        }
        final int size = eltList.size();
        final double size04 = size * 0.4;
        final int demiSize = (int) ((size04 >= 1) ? size04 : 1);
        final List splitListA = new ArrayList();
        final List splitListB = new ArrayList();
        final List<CoupleGE> listCGE = new ArrayList<CoupleGE>();
        GeneralEnvelope gESPLA, gESPLB;
        double bulkTemp;
        double bulkRef = -1;
        CoupleGE coupleGE;
        int index = 0;
        int lower_or_upper = 0;
        int cut2;
        Comparator comp;
        for (int lu = 0; lu < 2; lu++) {
            if (isLeaf) {
                if (lu == 0) {
                    comp = calc.sortFrom(splitIndex, true, false);
                } else {
                    comp = calc.sortFrom(splitIndex, false, false);
                }
            } else {
                if (lu == 0) {
                    comp = calc.sortFrom(splitIndex, true, true);
                } else {
                    comp = calc.sortFrom(splitIndex, false, true);
                }
            }
            Collections.sort(eltList, comp);

            for (int cut = demiSize; cut <= size - demiSize; cut++) {
                for (int i = 0; i < cut; i++) {
                    splitListA.add(eltList.get(i));
                }
                for (int j = cut; j < size; j++) {
                    splitListB.add(eltList.get(j));
                }
                cut2 = size - cut;
                if (isLeaf) {
                    gESPLA = new GeneralEnvelope((GeneralEnvelope) splitListA.get(0));
                    gESPLB = new GeneralEnvelope((GeneralEnvelope) splitListB.get(0));
                    for (int i = 1; i < cut; i++) {
                        gESPLA.add((GeneralEnvelope) splitListA.get(i));
                    }
                    for (int i = 1; i < cut2; i++) {
                        gESPLB.add((GeneralEnvelope) splitListB.get(i));
                    }
                } else {
                    gESPLA = new GeneralEnvelope(((DefaultNode) splitListA.get(0)).getBoundary());
                    gESPLB = new GeneralEnvelope(((DefaultNode) splitListB.get(0)).getBoundary());
                    for (int i = 1; i < cut; i++) {
                        gESPLA.add(((DefaultNode) splitListA.get(i)).getBoundary());
                    }
                    for (int i = 1; i < cut2; i++) {
                        gESPLB.add(((DefaultNode) splitListB.get(i)).getBoundary());
                    }
                }
                coupleGE = new CoupleGE(gESPLA, gESPLB, calc);
                bulkTemp = coupleGE.getOverlaps();
                if (bulkTemp < bulkRef || bulkRef == -1) {
                    bulkRef = bulkTemp;
                    index = cut;
                    lower_or_upper = lu;
                } else if (bulkTemp == 0) {
                    coupleGE.setUserProperty("cut", cut);
                    coupleGE.setUserProperty("lower_or_upper", lu);
                    listCGE.add(coupleGE);
                }
                splitListA.clear();
                splitListB.clear();
            }
        }

        if (!listCGE.isEmpty()) {
            double areaRef = -1;
            double areaTemp;
            for (CoupleGE cge : listCGE) {
                areaTemp = cge.getEdge();
                if (areaTemp < areaRef || areaRef == -1) {
                    areaRef = areaTemp;
                    index = (Integer) cge.getUserProperty("cut");
                    lower_or_upper = (Integer) cge.getUserProperty("lower_or_upper");
                }
            }
        }

        if (isLeaf) {
            if (lower_or_upper == 0) {
                comp = calc.sortFrom(splitIndex, true, false);
            } else {
                comp = calc.sortFrom(splitIndex, false, false);
            }
        } else {
            if (lower_or_upper == 0) {
                comp = calc.sortFrom(splitIndex, true, true);
            } else {
                comp = calc.sortFrom(splitIndex, false, true);
            }
        }
        Collections.sort(eltList, comp);

        for (int i = 0; i < index; i++) {
            splitListA.add(eltList.get(i));
        }
        for (int i = index; i < size; i++) {
            splitListB.add(eltList.get(i));
        }

        if (isLeaf) {
            return UnmodifiableArrayList.wrap((DefaultNode) tree.createNode(tree, null, null, splitListA),
                    (DefaultNode) tree.createNode(tree, null, null, splitListB));
        } else {
            final DefaultNode resultA = (DefaultNode) ((splitListA.size() == 1) ? splitListA.get(0) : tree.createNode(tree, null, splitListA, null));
            final DefaultNode resultB = (DefaultNode) ((splitListB.size() == 1) ? splitListB.get(0) : tree.createNode(tree, null, splitListB, null));
            return UnmodifiableArrayList.wrap(resultA, resultB);
        }
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
    public static DefaultNode chooseSubtree(final DefaultNode candidate, final Envelope entry) {
        ArgumentChecks.ensureNonNull("impossible to choose subtree with entry null", entry);

        if (candidate.isLeaf() && candidate.isFull()) {
            throw new IllegalStateException("impossible to choose subtree in overflow node");
        }
        Calculator calc = candidate.getTree().getCalculator();
        if (candidate.isLeaf()) {
            if ((Integer) candidate.getUserProperty("hilbertOrder") < 1) {
//                return ((List<DefaultNode>) candidate.getUserProperty("cells")).get(0);
                return candidate.getChildren().get(0);
            }
            int index;
            index = calc.getHVOfEntry(candidate, entry);
//            for (DefaultNode nod : (List<DefaultNode>) candidate.getUserProperty("cells")) {
            for (DefaultNode nod : candidate.getChildren()) {
                if (index <= ((Integer) (nod.getUserProperty("hilbertValue"))) && !nod.isFull()) {
                    return nod;
                }
            }
//            return ((List<DefaultNode>) candidate.getUserProperty("cells")).get(findAnotherCell(index, candidate));
            return candidate.getChildren().get(findAnotherCell(index, candidate));
        } else {
            final List<DefaultNode> childrenList = candidate.getChildren();
            final int size = childrenList.size();
            if (childrenList.get(0).isLeaf()) {
                final List<DefaultNode> listOverZero = new ArrayList<DefaultNode>();
                double overlapsRef = -1;
                int index = -1;
                double overlapsTemp = 0;
                for (int i = 0; i < size; i++) {
                    final GeneralEnvelope gnTemp = new GeneralEnvelope(childrenList.get(i).getBoundary());
                    gnTemp.add(entry);
                    for (int j = 0; j < size; j++) {
                        if (i != j) {
                            final GeneralEnvelope gET = childrenList.get(j).getBoundary();
                            overlapsTemp += calc.getOverlaps(gnTemp, gET);
                        }
                    }
                    if (overlapsTemp == 0) {
                        listOverZero.add(childrenList.get(i));
                    } else {
                        if ((overlapsTemp < overlapsRef) || overlapsRef == -1) {
                            overlapsRef = overlapsTemp;
                            index = i;
                        } else if (overlapsTemp == overlapsRef) {
                            if (DefaultTreeUtils.countElements(childrenList.get(i)) < DefaultTreeUtils.countElements(childrenList.get(index))) {
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
                    for (int i = 0, s = listOverZero.size(); i < s; i++) {
                        final GeneralEnvelope gE = new GeneralEnvelope(listOverZero.get(i).getBoundary());
                        gE.add(entry);
                        areaTemp = calc.getEdge(gE);
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
                return childrenList.get(index);
            }

            for (DefaultNode no : childrenList) {
                final GeneralEnvelope ge = no.getBoundary();
                if (ge.contains(entry, true)) {
                    return no;
                }
            }

            double enlargRef = -1;
            int indexEnlarg = -1;
            for (int i = 0, s = childrenList.size(); i < s; i++) {
                final DefaultNode n3d = childrenList.get(i);
                final GeneralEnvelope gEN = n3d.getBoundary();
                final GeneralEnvelope GE = new GeneralEnvelope(gEN);
                GE.add(entry);
                double enlargTemp = calc.getEnlargement(gEN, GE);
                if (enlargTemp < enlargRef || enlargRef == -1) {
                    enlargRef = enlargTemp;
                    indexEnlarg = i;
                }
            }
            return childrenList.get(indexEnlarg);
        }
    }

    /**
     * To answer Hilbert criterion and to avoid call split method, in some case
     * we constrain tree leaf to choose another cell to insert Entry.
     *
     * @param index of subnode which is normally chosen.
     * @param ptEntryCentroid subnode chosen centroid.
     * @throws IllegalArgumentException if method call by none leaf {@code Node}.
     * @throws IllegalArgumentException if index is out of required limit.
     * @throws IllegalStateException if no another cell is find.
     * @return index of another subnode.
     */
    private static int findAnotherCell(int index, final DefaultNode candidate) {
        if (!candidate.isLeaf()) {
            throw new IllegalArgumentException("impossible to find another leaf in Node which isn't LEAF tree");
        }
        ArgumentChecks.ensureBetween("index to find another leaf is out of required limit",
                0, (int) Math.pow(2, ((Integer)  candidate.getUserProperty("hilbertOrder") * 2)), index);
//        final List<DefaultNode> listCells = (List<DefaultNode>) candidate.getUserProperty("cells");
        final List<DefaultNode> listCells = candidate.getChildren();
        int siz = listCells.size();
        boolean oneTime = false;
        int indexTemp1 = index;
        for (int i = index; i < siz; i++) {
            if (!listCells.get(i).isFull()) {
                indexTemp1 = i;
                break;
            }
            if (i == siz - 1) {
                if (oneTime) {
                    throw new IllegalStateException("will be able to split");
                }
                oneTime = true;
                i = -1;
            }
        }
        return indexTemp1;
    }

    /**
     * Travel down {@code Tree}, find {@code GeneralEnvelope} entry if it exist
     * and delete it.
     *
     * <blockquote><font size=-1> <strong>NOTE: Moreover {@code Tree} is
     * condensate after a deletion to stay conform about R-Tree
     * properties.</strong> </font></blockquote>
     *
     * @param candidate {@code DefaultNode} where to delete.
     * @param entry {@code GeneralEnvelope} to delete.
     * @throws IllegalArgumentException if candidate or entry is null.
     * @return true if entry is find and deleted else false.
     */
    private static void deleteHilbertNode(final DefaultNode candidate, final Envelope entry) {
        ArgumentChecks.ensureNonNull("deleteHilbertNode Node2D candidate : ", candidate);
        ArgumentChecks.ensureNonNull("deleteHilbertNode Shape entry : ", entry);
        if (candidate.getBoundary().intersects(entry, true)) {
            if (candidate.isLeaf()) {
                boolean removed = false;
//                final List<DefaultNode> lN = (List<DefaultNode>) candidate.getUserProperty("cells");
                final List<DefaultNode> lN = candidate.getChildren();
                for (DefaultNode nod : lN) {
                    if (nod.getEntries().remove(entry)) {
                        removed = true;
                    }
                }
                if (removed) {
                    candidate.setBound(null);
                    trim(candidate);
                }
            } else {
                for (DefaultNode nod : candidate.getChildren().toArray(new DefaultNode[candidate.getChildren().size()])) {
                    deleteHilbertNode(nod, entry);
                }
            }
        }
    }

    /**
     * Method which permit to condense R-Tree. Condense made begin by leaf and
     * travel up to tree trunk.
     *
     * @param candidate {@code DefaultNode} to begin condense.
     */
    public static void trim(final DefaultNode candidate) {

        if (!candidate.isLeaf()) {
            final List<DefaultNode> children = candidate.getChildren();
            for (int i = children.size() - 1; i >= 0; i--) {
                final DefaultNode child = children.get(i);
                if (child.isEmpty()) {
                    children.remove(i);
                } else if (child.getChildren().size() == 1 && !child.isLeaf()) {
                    children.remove(i);
                    for (DefaultNode dn : child.getChildren()) {
                        dn.setParent(candidate);
                    }
                    children.addAll(child.getChildren());
                }
            }

            final HilbertRTree tree = (HilbertRTree) candidate.getTree();
            final Calculator calc = tree.getCalculator();
            final List<Envelope> lS = new ArrayList<Envelope>();
            searchHilbertNode(candidate, candidate.getBoundary(), lS);
            
            if (lS.size() <= tree.getMaxElements() * Math.pow(2, tree.getHilbertOrder() * 2) && !lS.isEmpty()) {
                
                final GeneralEnvelope bound = DefaultTreeUtils.getEnveloppeMin(lS);
                calc.createBasicHL(candidate, tree.getHilbertOrder(), bound);
                candidate.setUserProperty("isleaf", true);
                for (Envelope entry : lS) {
                    candidate.setBound(bound);
                    chooseSubtree(candidate, entry).getEntries().add(entry);
                }
            }
        }

        if (candidate.getParent() != null) {
            trim((DefaultNode)candidate.getParent());
        }
    }

    /**
     * Create appropriate sub-node to HilbertRTree leaf.
     *
     * @param tree pointer on Tree.
     * @param parent pointer on parent {@code Node}.
     * @param children sub {@code Node}.
     * @param entries entries {@code List} to add in this node.
     * @param coordinates lower upper bounding box coordinates table.
     * @return appropriate Node from tree.
     * @return
     */
    public static DefaultNode createCell(final Tree tree, final DefaultNode parent, final DirectPosition centroid, final int hilbertValue, final List<Envelope> entries, double... coordinates) {
        final DefaultNode cell = new DefaultNode(tree, parent, centroid, centroid, null, entries);
        cell.setUserProperty("hilbertValue", hilbertValue);
        cell.setUserProperty("centroid", centroid);
        return cell;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DefaultNode createNode(Tree tree, DefaultNode parent, List<DefaultNode> listChildren, List<Envelope> listEntries, double... coordinates) {
        final int ddim = coordinates.length;
        assert (ddim % 2) == 0 : "coordinate dimension is not correct";
        DefaultNode result;
        if (ddim == 0) {
            result = new DefaultNode(tree, parent, null, null, listChildren, null);
//            return new HilbertNode(tree, parent, null, null, listChildren, listEntries);
        }else{
            final int dim = coordinates.length / 2;
            final double[] dp1Coords = new double[dim];
            final double[] dp2Coords = new double[dim];
            System.arraycopy(coordinates, 0, dp1Coords, 0, dim);
            System.arraycopy(coordinates, dim, dp2Coords, 0, dim);

            final DirectPosition dp1 = new GeneralDirectPosition(crs);
            final DirectPosition dp2 = new GeneralDirectPosition(crs);
            for (int i = 0; i < dim; i++) {
                dp1.setOrdinate(i, dp1Coords[i]);
                dp2.setOrdinate(i, dp2Coords[i]);
            }
            result = new DefaultNode(tree, parent, dp1, dp2, listChildren, null);
        }
        
        result.setUserProperty("isleaf", false);
        if (listEntries != null && !listEntries.isEmpty()) {
            final int diment = listEntries.get(0).getDimension();
            final int size = listEntries.size();
            final int maxElts = tree.getMaxElements();
            final int hOrder = (size<=maxElts) ? 0 : (int)((Math.log(size-1)-Math.log(maxElts))/(diment*Math.log(2))) + 1;
            assert hOrder<=hilbertOrder : "too much elements to stock in tree leaf";
            result.setUserProperty("isleaf", true);
            result.setUserProperty("centroids", new ArrayList<DirectPosition>());
//            setUserProperty("cells", new ArrayList<DefaultNode>());
            final GeneralEnvelope bound = DefaultTreeUtils.getEnveloppeMin(listEntries);
            getCalculator().createBasicHL(result, hOrder, bound);
            for (Envelope ent : listEntries) {
                result.setBound(bound);
                chooseSubtree(result, ent).getEntries().add(ent);
            }
        }
        return result;
    }
}
