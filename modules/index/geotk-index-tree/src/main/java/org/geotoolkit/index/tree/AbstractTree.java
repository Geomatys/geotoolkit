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
package org.geotoolkit.index.tree;

import java.io.IOException;
import java.util.Arrays;
import org.geotoolkit.index.tree.calculator.*;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Classes;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.access.TreeAccess;
import org.geotoolkit.index.tree.hilbert.FileHilbertNode;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.index.tree.mapper.TreeElementMapper;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Create an abstract Tree.
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public abstract class AbstractTree<E> implements Tree<E> {

    protected TreeAccess treeAccess;
    private Node root;
    private final int nbMaxElement;
    protected CoordinateReferenceSystem crs;
    protected Calculator calculator;
    protected int eltCompteur;
    protected final TreeElementMapper<E> treeEltMap;
    protected int treeIdentifier;
    
    // search
    protected int currentLength;
    protected int currentPosition;
    protected int[] tabSearch;

    //dfebug
//    protected int countadjust;
    
    /**
     * To create an R-Tree use {@linkplain TreeFactory}.
     */
    protected AbstractTree(TreeAccess treeAccess, CoordinateReferenceSystem crs, TreeElementMapper<E> treeEltMap) {
        ArgumentChecks.ensureNonNull("Create Tree : CRS", crs);
        ArgumentChecks.ensureNonNull("Create TreeAccess : treeAccess", treeAccess);
        ArgumentChecks.ensureNonNull("Create TreeElementMapper : treeEltMap", treeEltMap);
        this.treeAccess = treeAccess;
        this.treeEltMap = treeEltMap;
        this.calculator = new CalculatorND();
        this.nbMaxElement = treeAccess.getMaxElementPerCells();
        this.eltCompteur = treeAccess.getEltNumber();
        ArgumentChecks.ensureBetween("Create Tree : maxElements", 2, Integer.MAX_VALUE, nbMaxElement);
        this.crs = crs;
        //debug
//        this.countadjust = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] searchID(Envelope regionSearch) throws StoreIndexException {
        return searchID(DefaultTreeUtils.getCoords(regionSearch));
    }
    
    public abstract int[] searchID(double[] regionSearch) throws StoreIndexException ;
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(E object) throws IllegalArgumentException , StoreIndexException{
        try {
            ArgumentChecks.ensureNonNull("insert : object", object);
            final Envelope env = treeEltMap.getEnvelope(object);
            if (!CRS.equalsIgnoreMetadata(crs, env.getCoordinateReferenceSystem()))
                throw new IllegalArgumentException("During insertion element should have same CoordinateReferenceSystem as Tree.");
            final double[] coordinates = DefaultTreeUtils.getCoords(env);
            for (double d : coordinates)
                if (Double.isNaN(d))
                    throw new IllegalArgumentException("coordinates contain at least one NAN value");
            treeEltMap.setTreeIdentifier(object, treeIdentifier);
            insert(treeIdentifier, coordinates);
            treeIdentifier++;
        } catch (IOException ex) {
            throw new StoreIndexException(ex);
        }
    }
    
    /**
     * {@inheritDoc}
     */
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
    
    protected abstract Node nodeInsert(Node candidate, Object object, double ...coordinates) throws IOException;
    
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
    protected Node chooseSubtree(final Node candidate, final double... coordinates) throws IOException {
        ArgumentChecks.ensureNonNull("chooseSubtree : candidate", candidate);
        ArgumentChecks.ensureNonNull("chooseSubtree : coordinates", coordinates);
//        assert candidate.checkInternal() : "chooseSubtree : candidate not conform";
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
     * Split a overflow {@code Node} in accordance with R-Tree properties.
     *
     * @param candidate {@code Node} to Split.
     * @throws IllegalArgumentException if candidate is null.
     * @throws IllegalArgumentException if candidate elements number is lesser 2.
     * @return {@code Node} List which contains two {@code Node} (split result of candidate).
     */
    protected Node[] splitNode(final Node candidate) throws IllegalArgumentException, IOException {
        ArgumentChecks.ensureNonNull("splitNode : candidate", candidate);
        assert candidate.checkInternal() : "splitNode : begin.";
//        int childNumber = (candidate.isLeaf())?((FileHilbertNode)candidate).getDataCount():candidate.getChildCount();
//        if (childNumber < 2) 
//            throw new IllegalArgumentException("not enought elements within " + candidate + " to split.");
        
        final Node[] children = candidate.getChildren();
//        assert childNumber == children.length : "SplitNode : childnumber should be same as children length value.";
        
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
//        countadjust += (treeAccess.getCountAdjust()-counta);
        return new Node[]{result1, result2};
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
    protected int defineSplitAxis(final Node[] children) throws IOException {
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
    public boolean remove(E object) throws StoreIndexException {
        try {
            ArgumentChecks.ensureNonNull("insert : object", object);
            final Envelope env = treeEltMap.getEnvelope(object);
            if (!CRS.equalsIgnoreMetadata(crs, env.getCoordinateReferenceSystem()))
                throw new IllegalArgumentException("During insertion element should have same CoordinateReferenceSystem as Tree.");
            final double[] coordinates = DefaultTreeUtils.getCoords(env);
            for (double d : coordinates)
                if (Double.isNaN(d))
                    throw new IllegalArgumentException("coordinates contain at least one NAN value");
            final int treeID = treeEltMap.getTreeIdentifier(object);
            return remove(treeID, coordinates);
        } catch (IOException ex) {
            throw new StoreIndexException(ex);
        }
    }
    
    public boolean remove(Object object, double... coordinates) throws StoreIndexException {
        ArgumentChecks.ensureNonNull("remove : object", object);
        ArgumentChecks.ensureNonNull("remove : coordinates", coordinates);
        final Node root = getRoot();
        if (root != null) {
            try {
                final boolean removed = removeNode(root, object, coordinates);
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
    protected boolean removeNode(final Node candidate, final Object object, final double... coordinate) throws IllegalArgumentException, StoreIndexException, IOException{
        ArgumentChecks.ensureNonNull("removeNode : Node candidate", candidate);
        ArgumentChecks.ensureNonNull("removeNode : Object object", object);
        ArgumentChecks.ensureNonNull("removeNode : double[] coordinate", coordinate);
        if(intersects(candidate.getBoundary(), coordinate, true)){
            if (candidate.isLeaf()) {
                boolean removed = candidate.removeData(object, coordinate);
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
     * Condense made, travel up from leaf to tree trunk (root Node).
     *
     * @param candidate {@code Node} to begin condense.
     * @throws IllegalArgumentException if candidate is null.
     */
    protected abstract void trim(final Node candidate) throws IllegalArgumentException, IOException, StoreIndexException ;
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxElements() {
        return this.nbMaxElement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getRoot() {
        return this.root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRoot(Node root) throws StoreIndexException{
        this.root = root;
        if (root == null) {
            try {
               treeAccess.rewind();
            } catch (IOException ex) {
                throw new StoreIndexException("Impossible to rewind treeAccess during setRoot(null).", ex);
            }
            treeIdentifier = 1;
            eltCompteur = 0;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CoordinateReferenceSystem getCrs(){
        return crs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Calculator getCalculator() {
        return this.calculator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() throws StoreIndexException {
        setRoot(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElementsNumber() {
        return eltCompteur;
    }

    /**
     * {@inheritDoc}
     */
    public void setElementsNumber(int value) {
        this.eltCompteur = value;
    }

    @Override
    public TreeElementMapper getTreeElementMapper() {
        return treeEltMap;
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

    public TreeAccess getTreeAccess() {
        return treeAccess;
    }    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getExtent() throws StoreIndexException {
        final Node node = getRoot();
        return (node == null) ? null : node.getBoundary().clone();
    }
    
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
    public Node createNode(TreeAccess tA, double[] boundary, byte properties, int parentId, int siblingId, int childId) throws IllegalArgumentException {
        return tA.createNode(boundary, properties, parentId, siblingId, childId);
    }
}
