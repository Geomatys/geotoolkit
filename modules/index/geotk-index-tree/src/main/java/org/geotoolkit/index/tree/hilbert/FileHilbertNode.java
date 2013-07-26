/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.index.tree.DefaultTreeUtils;
import static org.geotoolkit.index.tree.DefaultTreeUtils.contains;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMedian;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMinimum;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getSpan;
import org.geotoolkit.index.tree.FileNode;
import org.geotoolkit.index.tree.Node;
import static org.geotoolkit.index.tree.Node.PROP_HILBERT_ORDER;
import static org.geotoolkit.index.tree.Node.PROP_ISLEAF;
import org.geotoolkit.index.tree.access.TreeAccess;
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
    
    /**
     * Properties use only by Hilbert RTree.
     */
    private Map<String, Object> userProperties;
    
    public FileHilbertNode(HilbertTreeAccessFile tAF, int nodeId, double[] boundary, int parentId, int siblingId, int childId) {
        super(tAF, nodeId, boundary, parentId, siblingId, childId);
        setUserProperty(PROP_ISLEAF, false);
        setUserProperty(PROP_HILBERT_ORDER, 0);
        dimension = tAF.getCRS().getCoordinateSystem().getDimension();
        boundTemp = new double[dimension << 1];
        Arrays.fill(boundTemp, Double.NaN);
        dataCount = 0;
    }

    /**
     * @param key
     * @return user property for given key
     */
    @Override
    public Object getUserProperty(final String key) {
        if (userProperties == null) return null;
        return userProperties.get(key);
    }

    /**Add user property with key access.
     *
     * @param key
     * @param value Object will be stocked.
     */
    @Override
    public void setUserProperty(final String key, final Object value) {
        if (userProperties == null) userProperties = new HashMap<String, Object>();
        userProperties.put(key, value);
    }
        
    private boolean isInternalyFull() throws IOException {
        int sibl = getChildId();
        while (sibl != 0) {
            final FileHilbertNode fhn = (FileHilbertNode) tAF.readNode(sibl);
            if (!fhn.isFull()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void addChild(Node node) throws IOException {
        final FileNode fnod = (FileNode)node;
        if (fnod.isData()) {
            // si c pas une feuille sa veux dire que c la premiere insertion
            // alors on la fait devenir feuille 
            if (!isLeaf()) { 
                setUserProperty(PROP_ISLEAF, true); 
                setUserProperty(PROP_HILBERT_ORDER, 0);
                super.addChild(tAF.createNode(boundTemp, this.getNodeId(), 0, 0));
            }
            final double[] nodeBound = node.getBoundary().clone();
            DefaultTreeUtils.add(getBoundary(), nodeBound);
            children = super.getChildren();
            final int index = getAppropriateCellIndex(nodeBound);
            // la feuille est elle full ??
            if (index == -1) {
                // increase hilbert order
                int currentOrder = (int) getUserProperty(PROP_HILBERT_ORDER);
                assert currentOrder++ < ((HilbertTreeAccessFile)tAF).getHilbertOrder() : "impossible to increase node hilbert order";
                // on recupere tout les elements contenu dans cette feuille.
                data.clear();
                for (Node cnod : children) {
                    final FileNode fcnod = (FileNode)cnod;
                    int dataSibl = fcnod.getChildId();
                    while (dataSibl != 0) {
                        data.add(tAF.readNode(dataSibl));
                    }
                    tAF.deleteNode(fcnod);
                }
                // on creer les cells null
                final int nbCell = currentOrder == 0 ? 1 : 2 << (dimension * currentOrder - 1);
                for (int i = 0; i < nbCell; i++) {
                    super.addChild(tAF.createNode(boundTemp, this.getNodeId(), 0, 0));
                }
                for (Node dat : data) {
                    addChild(dat);
                }
            } else {
                children[index].addChild(node);
                dataCount++;
                tAF.writeNode(this);
            }
        } else {
            super.addChild(node); 
        }
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
            System.arraycopy(currentDataTab, 0, dataChilds, dcID+=cuDTLength, cuDTLength);
        }
        return dataChilds;
    }

    @Override
    public int getChildCount() {
        return ((boolean) getUserProperty(PROP_ISLEAF)) ? dataCount : super.getChildCount();
    }
   
    @Override
    public boolean isFull() throws IOException {
        return isInternalyFull() && getUserProperty(PROP_HILBERT_ORDER) == ((HilbertTreeAccessFile) tAF).getHilbertOrder();
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
    
    
    
    
    
}
