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
import org.geotoolkit.internal.tree.TreeAccess;
import java.io.IOException;
import static org.geotoolkit.index.tree.TreeUtilities.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Classes;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.internal.tree.TreeAccessFile;
import org.geotoolkit.internal.tree.TreeAccessMemory;

/**
 * Default implementation Node use in Tree.<br/><br/>
 * 
 * In Tree, Node architecture is organize like a chained list.<br/><br/>
 * 
 * &nbsp;N1 (root)<br/>
 * &nbsp;&nbsp;/-&gt;N2---------------------------&gt;N3----------------------------&gt;N4<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/--&gt;N5--&gt;N6--&gt;N7&nbsp;&nbsp;
 * &nbsp;&nbsp;&nbsp;/--&gt;N8--&gt;N9--&gt;N10&nbsp;&nbsp;&nbsp;&nbsp;
 * &nbsp;/--&gt;N11--&gt;N12--&gt;N13<br/><br/>
 * 
 * Note : Neither Node has an identifier equal to 0.<br/>
 * zero is reserved to significate end of chained list or no parent like tree root Node.
 *
 * @author Martin Desruisseaux (Geomatys).
 * @author Remi Marechal (Geomatys).
 */
public class Node {
    
    /**
     * Single Node identifier.
     */
    protected final int nodeId;
    
    /**
     * Node boundary.
     */
    protected double[] boundary;
    
    /**
     * Identifier of parent Node.<br/>
     * If Node have no parent like tree trunk (see {@link AbstractTree#root) the value is 0.
     */
    protected int parentId;
    
    /**
     * Identifier of sibling Node.<br/>
     * Note : all sibling Node have same parent identifiers.<br/>
     * When it s the last sibling Node of the current chained list Tree level the value is zero. 
     */
    protected int siblingId;
    
    /**
     * There are 2 cases : <br/>
     * if Node is a data (see {@link Node#isData()) childID is the tree identifier of a data.<br/>
     * else childID is the identifier of the first children from Node chained list architecture.
     */
    protected int childId;// < 0 si ce nest pas un FileNode
    
    /**
     * Number of children.
     */
    protected int childCount;
    
    /**
     * Object which store Node attributs on a file define by user (see {@link TreeAccessFile}) 
     * or in memory (see{@link TreeAccessMemory).
     */
    protected TreeAccess tAF;
    
    /**
     * {@code Byte} which use to test some properties.<br/>
     * first bit is at 1 if Node is a leaf.(see {@link TreeUtilities#IS_LEAF).<br/>
     * second bit is at 2 if Node is a data.(see {@link TreeUtilities#IS_DATA).<br/>
     * third bit is at 1 if Node is a cell.(see {@link TreeUtilities#IS_CELL).<br/>
     * fourth bit is at 1 if Node is other.(see {@link TreeUtilities#IS_OTHER).
     */
    protected byte properties;

    /**
     * Create a Node adapted for standard Tree implementation.
     * 
     * @param tAF Object which store Node attributs.
     * @param nodeId invariable single integer Node identifier.
     * @param boundary double table which represent boundary Node coordinates.
     * @param properties define type of Node. see ({@link Node#properties}).
     * @param parentId identifier of parent Node Tree architecture.
     * @param siblingId identifier of sibling Node.
     * @param childId if Node is a data it is the identifier of the data which is 
     * store in this tree (see {@link Node#childId}) else it is the first child of this Node. 
     * @see TreeAccess
     */
    public Node(final TreeAccess tAF, final int nodeId, final double[] boundary, final byte properties, final int parentId, final int siblingId, final int childId) {
        this.tAF        = tAF;
        this.nodeId     = nodeId;
        this.boundary   = boundary;
        this.parentId   = parentId;
        this.siblingId  = siblingId;
        this.childId    = childId;
        this.childCount = (childId != 0) ? 1 : 0;
        this.properties = properties;
    }

    /**
     * Return invariable single Node identifier.
     * 
     * @return invariable single Node identifier.
     * @see Node#nodeId.
     */
    public int getNodeId() {
        return nodeId;
    }
    
     /**
     * Return invariable single Node identifier from its parent Node.
     * 
     * @return invariable single Node identifier from its parent Node.
     * @see Node#parentId.
     */
    public int getParentId() {
        return parentId;
    }
    
    /**
     * Affect a new parent identifier.
     * 
     * @param parentId identifier of new parent.
     */
    public void setParentId(final int parentId) {
        this.parentId = parentId;
    }

    /**
     * Return invariable single Node identifier of its sibling Node.
     * 
     * @return invariable single Node identifier from its sibling Node.
     * @see Node#siblingId.
     */
    public int getSiblingId() {
        return siblingId;
    }
    
    /**
     * Affect a new sibling identifier.
     * 
     * @param siblingId identifier of new sibling.
     */
    public void setSiblingId(final int siblingId) {
        this.siblingId = siblingId;
    }

    /**
     * Return invariable single Node identifier of its first children or data tree identifier value.
     * 
     * @return invariable single Node identifier of its first children or data tree identifier value.
     * @see Node#childId.
     */
    public int getChildId() {
        return childId;
    }

    /**
     * Affect a new child identifier.
     * 
     * @param childId identifier of new child.
     */
    public void setChildId(final int childId) {
        this.childId = childId;
    }
    
    /**
     * Return {@code Byte} which contains type of Node.
     * 
     * @return {@code Byte} which contains type of Node.
     * @see Node#properties
     */
    public byte getProperties() {
        return properties;
    }

    /**
     * Affect a new type on this Node.
     * 
     * @param properties newest type.
     */
    public void setProperties(final byte properties) {
        this.properties = properties;
    }
    
    /**
     * Return boundary of this Node.
     * 
     * @return boundary of this Node.
     */
    public double[] getBoundary() {
        return boundary;
    }

    /**
     * Affect a new boundary on this Node.
     * 
     * @param boundary newest boundary.
     */
    public void setBoundary(final double[] boundary) {
        this.boundary = boundary;
    }

    /**
     * Return TreeAccess pointer.
     * 
     * @return TreeAccess pointer.
     * @see TreeAccess
     */
    public TreeAccess getTreeAccess() {
        return tAF;
    }
    
    /**
     * A leaf is a {@code Node} which contains only some data Node.
     *
     * @return true if it is a leaf else false (branch).
     * @see Node#properties
     */
    public boolean isLeaf() {
        return (properties &  IS_LEAF) !=  0;
    }
    
    /**
     * A leaf is a {@code Node} at extremity of {@code Tree} 
     * which contains a single data tree identifier.
     *
     * @return true if it is a leaf else false (branch).
     * @see Node#properties
     */
    public boolean isData() {
        return (properties &  IS_DATA) !=  0;
    }

    /**
     * Return true if Node don't contains children else false.
     * 
     * @return true if Node don't contains children else false.
     */
    public boolean isEmpty() {
        return childCount == 0;
    }
    
    /**
     * Add some children Node.
     * 
     * @param nodes children Node which will be added.
     * @throws IOException if problem during Node writing from {@link TreeAccessFile}.
     * @see TreeAccessFile#writeNode(org.geotoolkit.index.tree.Node) 
     */
    public void addChildren(final Node[] nodes) throws IOException {
        for(Node fnod : nodes) {
            // all elements should be distinct.
            fnod.setSiblingId(0);
            // connect child at other children (its sibling).
            childCount++;
            final int nextSibling = getChildId();
            setChildId(fnod.getNodeId());
            fnod.setParentId(getNodeId());
            fnod.setSiblingId(nextSibling);
            if (boundary == null) {
                boundary = fnod.getBoundary().clone();
            } else {
                add(boundary, fnod.getBoundary());
            }
            tAF.writeNode(fnod);
        }
        tAF.writeNode(this);
    }

    /**
     * Return all children from this Node.<br/><br/>
     * 
     * Note : if is leaf all children returned are data type else other type.
     * 
     * @return all children from this Node.
     * @throws IOException if problem during Node reading from {@link TreeAccessFile}. 
     * @see TreeAccessFile#readNode(int) 
     */
    public Node[] getChildren() throws IOException {
        final Node[] children = new Node[childCount];
        int sibl = getChildId();
        int id = 0;
        while (sibl != 0) {
            final Node cuN = tAF.readNode(sibl);
            children[id++] = cuN;
            sibl = cuN.getSiblingId();
        }
        assert id == childCount : "FileNode : getChildren : childCound and child number doesn't match.";
        return children;
    }

    /**
     * Remove specified child Node.<br/><br/>
     * 
     * Return true if child Node was found and should be removed else false.
     * 
     * @param node Node which will be removed.
     * @return true if child Node was found and should be removed else false.
     * @throws IOException if problem during Node writing from {@link TreeAccessFile}.
     * @see TreeAccessFile#writeNode(org.geotoolkit.index.tree.Node) 
     */
    public boolean removeChild(final Node node) throws IOException {
        boolean found = false;
        if (childCount == 1) {
            if (node.getNodeId() == getChildId()) {
                childCount--;
                setChildId(0);
                tAF.writeNode(this);
                found = true;
            }
        } else {
            
            if (getChildId() == node.getNodeId()) {
                setChildId(node.getSiblingId());
                childCount--;
                final Node[] children = getChildren();
                boundary = children[0].getBoundary().clone();
                for (int i = 1, l = children.length; i < l; i++) {
                    add(boundary, children[i].getBoundary());
                }
                tAF.writeNode(this);
                found = true;
            } else {
                // connect sibling with its next sibling.
                Node precChild = tAF.readNode(getChildId());
                boundary = precChild.getBoundary().clone();
                int sibl = precChild.getSiblingId();
                
                while (sibl != 0) {
                    if (sibl == node.getNodeId()) {
                        sibl = node.getSiblingId();
                        found = true;
                        // accrocher les voisins
                        precChild.setSiblingId(sibl);
                        tAF.writeNode(precChild);
                    } else {
                        precChild = tAF.readNode(sibl);
                        sibl = precChild.getSiblingId();
                        add(boundary, precChild.getBoundary());
                    }
                }
                if (found) {
                    childCount--;
                    tAF.writeNode(this);
                }
            }
        }
        if (found) tAF.removeNode(node);
        return found;
    }
    
    /**
     * Remove specified data.<br/><br/>
     * 
     * Return true if data was found and should be removed else false.
     * 
     * @param identifier tree identifier.
     * @param coordinates data boundary
     * @return true if data was found and should be removed else false.
     * @throws IOException if problem during Node writing from {@link TreeAccessFile}.
     * @see TreeAccessFile#writeNode(org.geotoolkit.index.tree.Node) 
     */
    public boolean removeData(final int identifier, final double ...coordinates) throws IOException {
        if (!((properties & 5) != 0))// test isleaf or iscell
            throw new IllegalStateException("You should not call removeData() method on a no leaf or cell Node.");
        if (isEmpty()) return false;
        final Node[] children = getChildren();
        final int l = children.length;
        assert childCount == l;
        int index = -1;
        for (int i = 0; i < l; i++) {
            final Node currentData = children[i];
            if (identifier == -currentData.getChildId()
               && Arrays.equals(currentData.getBoundary(), coordinates)) {
                index = i;
                break;
            }
        }
        if (index == -1) return false;
        childCount--;
        tAF.removeNode(children[index]);
        if (index == 0) {
            if (l == 1) {
                setChildId(0);
                boundary = null;
            } else {
                setChildId(children[1].getNodeId());
                boundary = children[1].getBoundary().clone();
                for (int i = 2; i < l; i++) {
                    add(boundary, children[i].getBoundary());
                }
            }
        } else {
            children[index-1].setSiblingId(children[index].getSiblingId());
            tAF.writeNode(children[index-1]);
            boundary = children[0].getBoundary().clone();
            for (int i = 1; i < l; i++) {
                if (i != index) add(boundary, children[i].getBoundary());
            }
        }
        tAF.writeNode(this);
        return true;
    }
    
    /**
     * Initialize Node.
     */
    public void clear() {
        boundary   = null;
        childId    = 0;
        childCount = 0;
    }

    /**
     * Return children number.
     * 
     * @return children number.
     */
    public int getChildCount() {
        return childCount;
    }
    
    /**
     * Affect a new children number value.
     * 
     * @param value new children number value.
     * @see TreeAccessFile#readNode(int) 
     */
    public void setChildCount(final int value) {
        childCount = value;
    }

    /**
     * Add a new child in this Node.<br/><br/>
     * 
     * Added child own a sibling id equal to last child Node identifier.
     * 
     * @param node added child.
     * @throws IOException if problem during Node writing from {@link TreeAccessFile}.
     * @see TreeAccessFile#writeNode(org.geotoolkit.index.tree.Node) 
     */
    public void addChild(final Node node) throws IOException {
        final double[] nodeBoundary = node.getBoundary();
        assert node.getSiblingId() == 0 : "future added element should be distinct from others.";
        // connect child at other children (its sibling).
        childCount++;
        final int nextSibling = getChildId();
        setChildId(node.getNodeId());
        node.setParentId(getNodeId());
        node.setSiblingId(nextSibling);
        if (nodeBoundary != null) {
            if (boundary == null || ArraysExt.hasNaN(boundary)) {
                boundary = nodeBoundary.clone();
            } else {
                add(boundary, nodeBoundary);
            }
        }
        tAF.writeNode(this);
        tAF.writeNode(node);
    }
    
    /**
     * Verify some internal Node properties.<br/><br/>
     * 
     * Return false if a Node properties doesn't match with an expected results else true.
     * 
     * @return false if a Node properties doesn't match with an expected results else true.
     * @throws IOException if problem during Node reading from {@link TreeAccessFile}. 
     * @see TreeAccessFile#readNode(int) 
     */
    public boolean checkInternal() throws IOException {
        if (isEmpty()) return true;
        if (getChildId() < 0) {
            if (!isData()) {
                throw new IllegalStateException("with childID < 0 isData() should return true.");
            }
            if (getChildCount() != 1) {
                throw new IllegalStateException("in data childcount always equals to 1.");
            }
        } else {
            int verifChildCount = 0;
            double[] boundTemp = null;
            int sibl = getChildId();
            while (sibl != 0) {
                final Node cuChild = tAF.readNode(sibl);
                if (cuChild.getParentId() != getNodeId()) {
                    throw new IllegalStateException("Child sibling should have parent ID equals to this.nodeID.");
                }
                if (boundTemp == null) {
                    boundTemp = cuChild.getBoundary().clone();
                } else {
                    add(boundTemp, cuChild.getBoundary());
                }
                verifChildCount++;
                sibl = cuChild.getSiblingId();
            }
            if (isData()) {
                throw new IllegalStateException("with childID > 0 isData() should return false.");
            }
            if (verifChildCount != childCount) {
                throw new IllegalStateException("sibling number and child count should have same value.");
            }
            if (!Arrays.equals(getBoundary(), boundTemp)) {
                throw new IllegalStateException("children boundary adding should be same as this.boundary."+Arrays.toString(boundTemp)+"   "+Arrays.toString(getBoundary()));
            }
        }
        return true;
    }

    /**
     * Return true if children number is higher than maximum elements permit 
     * per Node else false.
     * 
     * @return true if children number is higher than maximum elements permit per Node else false.
     * @throws IOException if problem during reading Node in {@link TreeAccessFile}.
     * But exception only return from {@link HilbertNode} sub-implementation.
     * @see HilbertNode#isFull() 
     */
    public boolean isFull() throws IOException {
        return getChildCount() >= tAF.getMaxElementPerCells();
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final List toString;
        try {
            if (!isData()) {
                toString = Arrays.asList(this.getChildren());
                String strparent =  (getParentId() == 0) ? "null" : (""+getParentId());
                return Trees.toString(Classes.getShortClassName(this)+" parent : "+strparent+" : ID : "+getNodeId()
                    + " leaf : "+isLeaf()+" sibling : "+getSiblingId()+" child "+getChildId()+" children number : "+getChildCount()+Arrays.toString(getBoundary()), toString);
            } else {
                return Classes.getShortClassName(this)+"Data : parent : "+getParentId()+" ID : "+getNodeId()+" sibling : "+getSiblingId()+" value : "+(-getChildId())+" bound : "+Arrays.toString(getBoundary());
            }
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Node)) return false;
        final Node objNode = (Node) obj;
        boolean boundBool;
        final double[] boundThis = getBoundary();
        final double[] objBound = objNode.getBoundary();
        if (boundThis == null || ArraysExt.allEquals(boundThis, Double.NaN)) {
            boundBool = (objBound == null || ArraysExt.allEquals(objBound, Double.NaN));
        } else {
            boundBool = Arrays.equals(objBound, boundThis);
        }
        return objNode.getNodeId() == getNodeId() 
                && boundBool
                && objNode.getParentId() == getParentId() 
                && objNode.getSiblingId() == getSiblingId()
                && objNode.getChildId() == getChildId()
                && objNode.getChildCount() == getChildCount();
    }
}
