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
import org.geotoolkit.index.tree.access.TreeAccess;
import java.io.IOException;
import static org.geotoolkit.index.tree.TreeUtils.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Classes;
import org.geotoolkit.gui.swing.tree.Trees;

/**
 * Default implementation Node use in Tree.
 *
 * @author Martin Desruisseaux (Geomatys).
 * @author Remi Marechal (Geomatys).
 */
public class Node {
    
    protected final int nodeId;
    protected double[] boundary;
    protected int parentId;
    protected int siblingId;
    protected int childId;// < 0 si ce nest pas un FileNode
    protected int childCount;
    protected TreeAccess tAF;
    
    /**
     * 
     */
    protected byte properties;

    public Node(TreeAccess tAF, int nodeId, double[] boundary, byte properties, int parentId, int siblingId, int childId) {
        this.tAF        = tAF;
        this.nodeId     = nodeId;
        this.boundary   = boundary;
        this.parentId   = parentId;
        this.siblingId  = siblingId;
        this.childId    = childId;
        this.childCount = (childId != 0) ? 1 : 0;
        this.properties = properties;
    }

    public int getNodeId() {
        return nodeId;
    }
    
    public int getParentId() {
        return parentId;
    }

    public int getSiblingId() {
        return siblingId;
    }

    public int getChildId() {
        return childId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setSiblingId(int siblingId) {
        this.siblingId = siblingId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }
    
    public byte getProperties() {
        return properties;
    }

    public void setProperties(byte properties) {
        this.properties = properties;
    }
    
    /**
     * <blockquote><font size=-1>
     * <strong>NOTE: if boundary is null, method re-compute all subnode boundary.</strong>
     * </font></blockquote>
     * @return boundary.
     */
    public double[] getBoundary() {
        return boundary;
    }

    public void setBoundary(double[] boundary) {
        this.boundary = boundary;
    }

    public TreeAccess getTreeAccess() {
        return tAF;
    }
    
    /**
     * A leaf is a {@code Node} at extremity of {@code Tree} which contains only entries.
     *
     * @return true if it is a leaf else false (branch).
     */
    public boolean isLeaf() {
        return (properties &  IS_LEAF) !=  0;
    }
    
    /**
     * A leaf is a {@code Node} at extremity of {@code Tree} which contains only entries.
     *
     * @return true if it is a leaf else false (branch).
     */
    public boolean isData() {
        return (properties &  IS_DATA) !=  0;
    }

    public boolean isEmpty(){
        return childCount == 0;
    }
    
    public void addChildren(Node[] nodes) throws IOException {
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

    public Node getChild() throws IOException {
        return tAF.readNode(childId);
    }

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

    public boolean removeChild(Node node) throws IOException {
        boolean found = false;
        if (childCount == 1) {
            if (node.getNodeId() == getChildId()) {
                childCount--;
                setChildId(0);
//                boundary = null;
                tAF.writeNode(this);
                found = true;
            }
        } else {
            
            if (((Integer) getChildId()) == node.getNodeId()) {
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
//                assert found : "removechild : child not found";
                if (found) {
                    childCount--;
                    tAF.writeNode(this);
                }
            }
        }
        if (found) tAF.removeNode(node);
        return found;
    }
    
    public boolean removeData(Object object, double ...coordinates) throws IOException {
        if (!((properties & 5) != 0))// test isleaf or iscell
            throw new IllegalStateException("You should not call removeData() method on a no leaf or cell Node.");
        if (isEmpty()) return false;
        final Node[] children = getChildren();
        final int l = children.length;
        assert childCount == l;
        int index = -1;
        for (int i = 0; i < l; i++) {
            final Node currentData = children[i];
            if (((Integer)object) == -currentData.getChildId()
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

    public void removeChildren(Node[] nodes) throws IOException {
        for (Node nod : nodes) {
            removeChild(nod);
        }
    }
    
    public void clear() {
        boundary = null;
        childId = 0;
        childCount = 0;
    }

    public int getChildCount() {
        return childCount;
    }
    
    /**
     * Only use for assertion.
     * @return 
     */
    public void setChildCount(final int value) {
        childCount = value;
    }

    public void addChild(Node node) throws IOException {
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

    @Override
    public boolean equals(Object obj) {
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
