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

import org.geotoolkit.index.tree.hilbert.iterator.HilbertIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Classes;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.index.tree.DefaultNode;
import org.geotoolkit.index.tree.DefaultTreeUtils;
import static org.geotoolkit.index.tree.DefaultTreeUtils.contains;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getEnvelopeMin;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMedian;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMinimum;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getSpan;
import org.geotoolkit.index.tree.Node;
import static org.geotoolkit.index.tree.Node.PROP_HILBERT_ORDER;
import static org.geotoolkit.index.tree.Node.PROP_ISLEAF;
import org.geotoolkit.index.tree.Tree;

/**
 * Sub class of {@link DefaultNode} adapted for {@link HilbertRTree} tree.
 *
 * @author Remi Marechal (Geomatys).
 */
public class HilbertNode extends DefaultNode {

    private static final double LN2 = 0.6931471805599453;
    
    public HilbertNode(Tree tree) throws IOException {
        super(tree);
    }

    public HilbertNode(Tree tree, Node parent, double[] lowerCorner, double[] upperCorner, 
            Node[] children, Object[] objects, double[][] coordinates) throws IOException {
        super(tree, parent, lowerCorner, upperCorner, children, null, null);
        setUserProperty(PROP_ISLEAF, false);
        setUserProperty(PROP_HILBERT_ORDER, 0);
        if (objects != null) {
            assert coordinates != null : "create node : coordinates should not be null.";
            assert (children == null) : "createNode : children should be null.";
            final int len = coordinates.length;
            assert (len == objects.length) : "createNode : coordinates and objects should have same length.";
            // create a Hilbert leaf
            final double[] bound = getEnvelopeMin(coordinates);
            final int dim = bound.length >> 1;
            // next work lost of dimension.
//            int diment = dim;
//            for (int d = 0; d < dim; d++) if (getSpan(bound, d) <= 1E-12) diment--;
//            final Node result = new HilbertNode(tree, parent, getLowerCorner(bound), getUpperCorner(bound), null, null, null);
            final int maxElts = tree.getMaxElements();
            
            final int hOrder = (len <= maxElts) ? 0 : (int)((Math.log(len-1)-Math.log(maxElts))/(dim*LN2)) + 1;
            setUserProperty(PROP_ISLEAF, true);
            setUserProperty(PROP_HILBERT_ORDER, hOrder);
            final int nbCell = hOrder == 0 ? 1 : 2 << (dim * hOrder - 1);
            this.children = new DefaultNode[nbCell];
            for (int i = 0; i < nbCell; i++) {
                this.children[i] = new DefaultNode(tree, this, null, null, null, null, null);
            }
            this.countChild = nbCell;
            for (int i = 0; i < len; i++) {
                setBound(bound);
                addElement(objects[i], coordinates[i]);
            }
            
            //case where splitting method lost a dimension and overmuch elements for n-1 dimension.
//////            if (hOrder > ((HilbertRTree)tree).getHilbertOrder()) {
//////                createBasicHL(result, 0, bound.clone());//a voir si on peut pas kill le clone()
//////                for (int i = 0; i < len; i++) {
//////                    insertNode(result, objects[i], coordinates[i]);
//////                }
//////            } else {
//////                result.setUserProperty(PROP_ISLEAF, true);
//////                createBasicHL(result, hOrder, bound.clone());
//////                for (int i = 0; i < len; i++) {
//////                    result.setBound(bound);
//////                    chooseSubtree(result, coordinates[i]).addElement(objects[i], coordinates[i]);
//////                }
//////            }
        }
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void addElement(Object object, double... coordinate) throws IOException {
        final int dimension = coordinate.length >> 1;
        assert tree.getCrs().getCoordinateSystem().getDimension() == (dimension) : "dimension between coordinates and tree crs should be same.";
        final Node parent = getParent();
        if (parent != null && parent.isLeaf()) {
            super.addElement(object, coordinate);
            getParent().setBound(null);
        } else {
            assert isLeaf() : "addElement only in leaf.";
            // en cas de index = -1 on increase l'ordre de hilbert pour que ca paraisse invisible a l'ajout auto split !!!! 
            final int index = getAppropriateCellIndex(coordinate);
            if (index != -1) {
                children[index].addElement(object, coordinate);
                setBound(null);
            } else {
                // on augment l'ordre de hilbert
                int currentOrder = (Integer) getUserProperty(PROP_HILBERT_ORDER);
                assert currentOrder++ < ((HilbertRTree)tree).getHilbertOrder() : "impossible to increase node hilbert order";
                setUserProperty(PROP_HILBERT_ORDER, currentOrder);
                final int nbCell = currentOrder == 0 ? 1 : 2 << (dimension * currentOrder - 1);
                final int childCount = getChildCount();
                int nbrElts = 0;
                for (int i = 0; i < childCount; i++) {
                    nbrElts += getChild(i).getCoordsCount();
                }
                nbrElts++;
                final double[][] coordinates = new double[nbrElts][];
                final Object[] objects = new Object[nbrElts];
                int currentPos = 0;
                for (int i = 0; i < childCount; i++) {
                    final Node cuCell = getChild(i);
                    final int len = cuCell.getCoordsCount();
                    System.arraycopy(cuCell.getCoordinates(), 0, coordinates, currentPos, len);
                    System.arraycopy(cuCell.getObjects(), 0, objects, currentPos, len);
                    currentPos += len;
                    cuCell.clear();
                }
                clear();
                coordinates[nbrElts - 1] = coordinate;
                objects[nbrElts - 1] = object;
                final double[] bound = getEnvelopeMin(coordinates);
                children = new DefaultNode[nbCell];
                for (int i = 0; i < nbCell; i++) {
                    this.children[i] = new DefaultNode(tree, this, null, null, null, null, null);
                }
                this.countChild = nbCell;
                for (int i = 0; i < nbrElts; i++) {
                    setBound(bound);
                    addElement(objects[i], coordinates[i]);// normaly found all index
                }
            }
        }
    }
    
    /**
     * {@inheritDoc }. 
     */
    @Override
    public boolean isFull() throws IOException {
        if (isLeaf()) {
            for (int i = 0, s = getChildCount(); i < s; i++) {
                if (!getChild(i).isFull()) {
                    return false;
                }
            }
            return ((Integer) getUserProperty(PROP_HILBERT_ORDER)) == ((HilbertRTree)tree).getHilbertOrder();
        } else {
            return getChildCount() >= tree.getMaxElements();//normaly unused
        }
    }
    

    /**
     * Return the appropriate table index of Hilbert cell within {@link #children} table.
     * 
     * @param coordinate boundary of element which will be insert.
     * @return Return the appropriate table index of Hilbert cell else return -1 if all cell are full.
     */
    private int getAppropriateCellIndex(double... coordinate) throws IOException {
        if ((Integer) getUserProperty(PROP_HILBERT_ORDER) < 1) {//only one cell.
            return (children[0].isFull()) ? -1 : 0;
        }
        final int index = getHVOfEntry(coordinate);
        return findCell(index);
    }
    
    /**
     * To answer Hilbert criterion and to avoid call split method, in some case
     * we constrain tree leaf to choose another cell to insert Entry.<br/>
     * Return -1 if all cells are full.
     *
     * @param index of subnode which is normally chosen.
     * @param ptEntryCentroid subnode chosen centroid.
     * @throws IllegalArgumentException if method call by none leaf {@code Node}.
     * @throws IllegalArgumentException if index is out of required limit.
     * @throws IllegalStateException if no another cell is find.
     * @return index of another subnode.
     */
    private int findCell(int index) throws IOException {
        if (!isLeaf()) throw new IllegalArgumentException("impossible to find another leaf in Node which isn't LEAF tree");
        final int siz   = getChildCount();
        assert (index < siz) : "wrong index in findAnotherCell"; 
        boolean oneTime = false;
        int indexTemp1  = index;
        for (int i = index; i < siz; i++) {
            if (!getChild(i).isFull()) {
                return i;
            }
            if (i == siz - 1) {
                if (oneTime) return - 1;//all cells are full 
                oneTime = true;
                i = -1;
            }
        }
        return indexTemp1;
    }
    
    /**
     * {@inheritDoc }. 
     */
    @Override
    protected double[] calculateBounds() throws IOException {
        double[] boundary = null;    
        final int s = getChildCount();
        for(int i = 0; i < s; i++) {
            final Node cuCell = getChild(i);
            if (!cuCell.isEmpty()) {
                if (boundary == null) {
                    boundary = cuCell.getBoundary().clone();
                } else {
                    DefaultTreeUtils.add(boundary, getChild(i).getBoundary());
                }
            }
        }
        return boundary;
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        String strparent =  (parent == null)?"null":String.valueOf(parent.hashCode());
        return Trees.toString(Classes.getShortClassName(this)+" : "+this.hashCode()+" parent : "+strparent
                + " leaf : "+isLeaf()+ " userPropLeaf : "+(Boolean)getUserProperty(PROP_ISLEAF), Arrays.asList(children));
    }
    
    /**
     * Find {@code DirectPosition} Hilbert coordinate from this Node.
     *
     * @param pt {@code DirectPosition}
     * @throws IllegalArgumentException if parameter "dPt" is out of this node
     * boundary.
     * @throws IllegalArgumentException if parameter dPt is null.
     * @return int[] table of length 3 which contains 3 coordinates.
     */
    private static int[] getHilbCoord(final double[] point, final double[] envelope, final int hilbertOrder) {
        ArgumentChecks.ensureNonNull("DirectPosition dPt : ", point);
        if (!contains(envelope, point)) {
            throw new IllegalArgumentException("Point is out of this node boundary");
        }
        
        final double div  = 2 << hilbertOrder - 1;
        List<Integer> lInt = new ArrayList<Integer>();

        for(int d = 0, dim = envelope.length/2; d < dim; d++){
            final double span = getSpan(envelope, d);
            if (span <= 1E-9) continue;
            final double currentDiv = span/div;
            int val = (int) (Math.abs(point[d] - getMinimum(envelope, d)) / currentDiv);
            if (val == div) val--;
            lInt.add(val);
        }
        final int[] result = new int[lInt.size()];
        int i = 0;
        for (Integer val : lInt) result[i++] = val;
        return result;
    }
    
    /**
     * Find Hilbert order of an entry from candidate.
     *
     * @param candidate entry 's hilbert value from it.
     * @param objectBoundary which we looking for its Hilbert order.
     * @throws IllegalArgumentException if parameter "entry" is out of this node
     * boundary.
     * @throws IllegalArgumentException if entry is null.
     * @return integer the entry Hilbert order.
     */
    private int getHVOfEntry(double[] objectBoundary) throws IOException {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null entry", objectBoundary);
        final double[] ptCE = getMedian(objectBoundary);
        final double[] bound = getBoundary().clone();
        DefaultTreeUtils.add(bound, objectBoundary);
        final int order = (Integer) getUserProperty(PROP_HILBERT_ORDER);
        if (! contains(bound, ptCE)) throw new IllegalArgumentException("entry is out of this node boundary");

        int[] hCoord = getHilbCoord(ptCE, bound, order);
        final int spaceHDim = hCoord.length;

        if (spaceHDim == 1) return hCoord[0];

        final HilbertIterator hIt = new HilbertIterator(order, spaceHDim);
        int hilberValue = 0;
        while (hIt.hasNext()) {
            final int[] currentCoords = hIt.next();
            assert hilberValue < getChildCount() : "getHVOfEntry : hilbert value out of bound.";
            if (Arrays.equals(hCoord, currentCoords)) return hilberValue;
            hilberValue++;
        }
        throw new IllegalArgumentException("should never throw");
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean checkInternal() throws IOException {
        if (isEmpty()) {
            //: "Node should never be empty.";
            throw new IllegalStateException("Candidate is empty.");
            
        }
        if (getTree().getRoot() != this) {
            if (getParent() == null) {
                throw new IllegalStateException("Node should not have a null parent.");
            }
        }
        if (isLeaf()) {
            final double[] tempboundary = getBound();
            final int countC = getChildCount();
            if (countC <= 0) {
                throw new IllegalStateException("Candidate leaf count should be > 0.");
            }
            
            if (tempboundary != null) {
                double[] boundCell = null;
                double[] boundElements = null;
                for (int i = 0; i < countC; i++) {
                    final Node cuCell = getChild(i);
                    if (!cuCell.isEmpty()) {
                        final double[] cuCellBoundary = cuCell.getBoundary();
                        if (boundCell == null) {
                            boundCell = cuCellBoundary.clone();
                        } else {
                            DefaultTreeUtils.add(boundCell, cuCellBoundary);
                        }
                        double[] boundCoord = null;
                        //work on coordinates
                        for (int ic = 0, sc = cuCell.getCoordsCount(); ic < sc; ic++) {
                            if (boundCoord == null) {
                                boundCoord = cuCell.getCoordinate(ic).clone();
                            } else {
                                DefaultTreeUtils.add(boundCoord, cuCell.getCoordinate(ic));
                            }
                            if (boundElements == null) {
                                boundElements = cuCell.getCoordinate(ic).clone();
                            } else {
                                DefaultTreeUtils.add(boundElements, cuCell.getCoordinate(ic));
                            }
                        }
                        if (!Arrays.equals(cuCellBoundary, boundCoord)) {
                            throw new IllegalStateException("Internal leaf cell boundary don't consistent with its stored elements boundary.");
                        }
                    }
                }
                if (!Arrays.equals(tempboundary, boundElements)) {
                    throw new IllegalStateException("Leaf boundary don't consistent with its stored elements boundary.");
                }
                if (!Arrays.equals(tempboundary, boundCell)) {
                    throw new IllegalStateException("Leaf boundary don't consistent with its stored cells boundary.");
                }
            }
        } else {
            final int countChilds = getChildCount();
            if (countChilds <= 0) {
                return false;
            }
            for (int i = 0; i < countChilds; i++) {
                final Node child  = getChild(i);
                if (child.getParent() != this) {
                    throw new IllegalStateException("Candidate Node child has a wrong parent.");
                }
            }
            final double[] bound = getBound();
            if (bound != null) {
                final double[] tempBound = getChild(0).getBoundary().clone();
                for (int i = 1; i < countChilds; i++) {
                    DefaultTreeUtils.add(tempBound, getChild(i).getBoundary());
                }
                if (!Arrays.equals(tempBound, bound)) {
                    throw new IllegalStateException("Candidate Node boundary should be consistent.");
                }
            }
        }
        return true;
    }
}
