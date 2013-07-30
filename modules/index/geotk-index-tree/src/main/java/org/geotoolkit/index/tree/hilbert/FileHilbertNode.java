/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Classes;
import org.geotoolkit.gui.swing.tree.Trees;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.FileNode;
import org.geotoolkit.index.tree.Node;
import static org.geotoolkit.index.tree.Node.PROP_HILBERT_ORDER;
import org.geotoolkit.index.tree.hilbert.iterator.HilbertIterator;

/**
 *
 * @author rmarechal
 */
public class FileHilbertNode extends FileNode {

    private Node[] children;
    private int dimension;
    private final double[] boundTemp;
    private final List<Node> data = new ArrayList<Node>();
    private int dataCount;
    private int currentHilbertOrder;
    

    public FileHilbertNode(HilbertTreeAccessFile tAF, int nodeId, double[] boundary, byte properties, int parentId, int siblingId, int childId) throws IOException {
        super(tAF, nodeId, boundary, properties, parentId, siblingId, childId);
        dimension = tAF.getCRS().getCoordinateSystem().getDimension();
        boundTemp = new double[dimension << 1];
        Arrays.fill(boundTemp, Double.NaN);
        dataCount = 0;
        currentHilbertOrder = 0;
        
        if ((boundary == null || ArraysExt.hasNaN(boundary)) && ((properties & IS_LEAF) != 0)) { 
            super.addChild(tAF.createNode(boundTemp, (byte) IS_CELL, this.getNodeId(), 0, 0));
        }
    }

    private boolean isInternalyFull() throws IOException {
        int sibl = getChildId();
        while (sibl != 0) {
            final FileHilbertNode fhn = (FileHilbertNode) tAF.readNode(sibl);
            if (!fhn.isFull()) {
                return false;
            }
            sibl = fhn.getSiblingId();
        }
        return true;
    }

    public int getCurrentHilbertOrder() {
        return currentHilbertOrder;
    }

    public void setCurrentHilbertOrder(int currentHilbertOrder) {
        this.currentHilbertOrder = currentHilbertOrder;
    }

    public int getDataCount() {
        return dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    @Override
    public void addChildren(Node[] nodes) throws IOException {
        for (Node nod : nodes) {
            final FileNode fnod = (FileNode) nod;
            fnod.setSiblingId(0);
            addChild(fnod);
        }
    }
    
    
    
    @Override
    public void addChild(Node node) throws IOException {
        final FileNode fnod = (FileNode)node;
        if (isLeaf()) {
            // si c pas une feuille sa veux dire que c la premiere insertion
            // alors on la fait devenir feuille 
            assert fnod.isData() : "future added child should be data.";
            final double[] nodeBound = node.getBoundary().clone();
            add(getBoundary(), nodeBound);
            children = super.getChildren();
            final int index = getAppropriateCellIndex(nodeBound);
            // la feuille est elle full ??
            if (index == -1) {
                // increase hilbert order
                assert currentHilbertOrder++ < ((HilbertTreeAccessFile)tAF).getHilbertOrder() : "impossible to increase node hilbert order";
                // on recupere tout les elements contenu dans cette feuille.
                data.clear();
                for (Node cnod : children) {
                    final FileNode fcnod = (FileNode)cnod;
                    int dataSibl = fcnod.getChildId();
                    while (dataSibl != 0) {
                        final FileHilbertNode currentData = (FileHilbertNode) tAF.readNode(dataSibl);
                        dataSibl = currentData.getSiblingId();
                        currentData.setSiblingId(0);// become distinc
                        data.add(currentData);
                    }
//                    super.removeChild(fnod);
                    tAF.deleteNode(fcnod);
                }
                clear();
                // on creer les cells null
                final int nbCell = currentHilbertOrder == 0 ? 1 : 2 << (dimension * currentHilbertOrder - 1);
                for (int i = 0; i < nbCell; i++) {
                    super.addChild(tAF.createNode(boundTemp, IS_CELL, this.getNodeId(), 0, 0));
                }
                data.add(node);
                for (Node dat : data) { //problem ici
                    addChild(dat);
                }
            } else {
                children[index].addChild(node);
                dataCount++;
                double[] cuChildBound = children[index].getBoundary();
                if (boundary == null || ArraysExt.hasNaN(boundary)) {
                    boundary = cuChildBound.clone();
                } else {
                    add(boundary, cuChildBound);
                }
                tAF.writeNode(this);
            }
        } else {
            super.addChild(node); 
        }
    }
    
    public boolean isCell() {
        return (properties & IS_CELL) != 0;
    }

    @Override
    public void clear() {
        super.clear(); 
        dataCount = 0;
    }
    
    

    @Override
    public Node[] getChildren() throws IOException {
        final Node[] superChilds = super.getChildren();
        if (!isLeaf()) return superChilds;
        final Node[] dataChilds = new Node[dataCount];
        int dcID = 0;
        for (Node sc : superChilds) {
            final Node[] currentDataTab = sc.getChildren();
            final int cuDTLength = currentDataTab.length;
            System.arraycopy(currentDataTab, 0, dataChilds, dcID, cuDTLength);
            dcID += cuDTLength;
        }
        assert dcID == dataCount : "FileHilbertNode : getChildren : dataCount and data number doesn't match.";
        return dataChilds;
    }

    @Override
    public boolean checkInternal() throws IOException {
        if (isLeaf()) {
            if (isEmpty()) return true;
            double[] superBound = null;
            Node[] superChilds = super.getChildren();
            int nbrData = 0;
            for (Node nod : superChilds) {
                final FileNode currSC = (FileNode)nod;
                if (currSC.getParentId() != nodeId)
                    throw new IllegalStateException("cell parent ID should be equals to this.nodeID.");
                if (!currSC.isEmpty()) {
                    final double[] currBound = currSC.getBoundary().clone();
                    final int currNID = currSC.getNodeId();
                    if (superBound == null) {
                        superBound = currBound;
                    } else {
                        add(superBound, currBound);
                    }
                    double[] dataBound = null;
                    Node[] dataChilds = currSC.getChildren();
                    for (Node dat : dataChilds) {
                        FileNode fdat = (FileNode)dat;
                        if (dataBound == null) {
                            dataBound = fdat.getBoundary().clone();
                        } else {
                            add(dataBound, fdat.getBoundary());
                        }
                        if (fdat.getParentId() != currNID)
                            throw new IllegalStateException("data parent ID should be equals to its parent cell ID.");
                        nbrData++;
                    }
                    if (!Arrays.equals(dataBound, currBound))
                        throw new IllegalStateException("add data boundary should have same boundary than its cell boundary. expected : "+Arrays.toString(dataBound)+"  found : "+Arrays.toString(currBound));
                }
            }
            if (!Arrays.equals(superBound, getBoundary()))
                throw new IllegalStateException("add cells boundary should have same boundary than this boundary. expected : "+Arrays.toString(getBoundary())+"  found : "+Arrays.toString(superBound));
            if (dataCount != nbrData)
                throw new IllegalStateException("data number should be same than leaf data count. expected : "+nbrData+"  found : "+dataCount);
            return true;
        } else {
            return super.checkInternal();
        }
    }

    @Override
    public boolean isEmpty() {
        if (isLeaf()) return dataCount == 0;
        return super.isEmpty();
    }
   
    @Override
    public boolean isFull() throws IOException {
        if (isLeaf()) {
            return isInternalyFull() && currentHilbertOrder == ((HilbertTreeAccessFile)tAF).getHilbertOrder();
        } else {
            return super.isFull();
        }
    }
    
    /**
     * Return the appropriate table index of Hilbert cell within {@link #children} table.
     * 
     * @param coordinate boundary of element which will be insert.
     * @return Return the appropriate table index of Hilbert cell else return -1 if all cell are full.
     */
    private int getAppropriateCellIndex(double... coordinate) throws IOException {
        if (currentHilbertOrder < 1) {//only one cell.
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
            if (!children[i].isFull()) {
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
        add(bound, objectBoundary);
        final int order = currentHilbertOrder;
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
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final List toString;
        try {
            if (!isData()) {
                toString = Arrays.asList(super.getChildren());
                String strparent =  (getParentId() == 0) ? "null" : (""+getParentId());
                final String strHilbertLeaf = (isLeaf()) 
                        ? " hilbert Order : "+getCurrentHilbertOrder() +" children number : "+getChildCount()+" data number : "+getDataCount()
                        : " children number : "+getChildCount();
                return Trees.toString(Classes.getShortClassName(this)+" parent : "+strparent+" : ID : "+getNodeId()
                    + " leaf : "+isLeaf()+" sibling : "+getSiblingId()+" child "+getChildId()+strHilbertLeaf+"  "+Arrays.toString(getBoundary()), toString);
            } else {
                return Classes.getShortClassName(this)+"Data : parent : "+getParentId()+" ID : "+getNodeId()+" sibling : "+getSiblingId()+" value : "+(-getChildId())+" bound : "+Arrays.toString(getBoundary());
            }
        } catch (IOException ex) {
            Logger.getLogger(FileNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    
}
