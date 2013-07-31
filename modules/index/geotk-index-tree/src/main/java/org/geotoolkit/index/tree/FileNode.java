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
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Classes;
import org.geotoolkit.gui.swing.tree.Trees;

/**
 *
 * @author rmarechal
 */
public class FileNode extends Node {
    
    protected final int nodeId;
    protected double[] boundary;
    protected int parentId;
    protected int siblingId;
    protected int childId;// < 0 si ce nest pas un FileNode
    protected int childCount;
    protected TreeAccess tAF;
    
//    private Map<String, Object> userProperties;
    
    protected byte properties;

    public FileNode(TreeAccess tAF, int nodeId, double[] boundary, byte properties, int parentId, int siblingId, int childId) {
        super(null);// en attente
        this.tAF        = tAF;
        this.nodeId     = nodeId;
        this.boundary   = boundary;
        this.parentId   = parentId;
        this.siblingId  = siblingId;
        this.childId    = childId;
        this.childCount = (childId != 0) ? 1 : 0;
        this.properties = properties;
//        setUserProperty(PROP_ISLEAF, isLeaf);
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

        /**
         * @param key
         * @return user property for given key
         */
        @Override
        public Object getUserProperty(final String key) {
            return null;
        }
    
        /**
         * Add user property with key access.
         *
         * @param key
         * @param value Object will be stocked.
         */
        @Override
        public void setUserProperty(final String key, final Object value) {
            
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
    @Override
    public double[] getBoundary() {
        return boundary;
    }
    
    @Override
    public void setBound(double[] bound) {
        boundary = bound;
    }

    /**
     * A leaf is a {@code Node} at extremity of {@code Tree} which contains only entries.
     *
     * @return true if it is a leaf else false (branch).
     */
    @Override
    public boolean isLeaf() {
        return (properties &  IS_LEAF) !=  0;
    }
    
    /**
     * A leaf is a {@code Node} at extremity of {@code Tree} which contains only entries.
     *
     * @return true if it is a leaf else false (branch).
     */
    public boolean isData() {
//        return childId < 0;
        return (properties &  IS_DATA) !=  0;
    }

    @Override
    public boolean isEmpty(){
        return childCount == 0;
    }
    
    @Override
    public void addChildren(Node[] nodes) throws IOException {
        for(Node fnod : nodes) {
            final FileNode fNod = ((FileNode)fnod);
            // all elements should be distinct.
            ((FileNode)fnod).setSiblingId(0);
//            addChild(fnod);
            /*****************************************************/
            // connect child at other children (its sibling).
            childCount++;
            final int nextSibling = getChildId();
            setChildId(fNod.getNodeId());
            fNod.setParentId(getNodeId());
            fNod.setSiblingId(nextSibling);
            if (boundary == null) {
                boundary = fNod.getBoundary().clone();
            }
            add(boundary, fNod.getBoundary());
//            tAF.writeNode(this);
//            assert Arrays.equals(node.getBoundary(), assertBound);
            tAF.writeNode(fNod);
            
        }
        tAF.writeNode(this);
    }

    public FileNode getChild() throws IOException {
        return tAF.readNode(childId);
    }

    @Override
    public Node[] getChildren() throws IOException {
        final FileNode[] children = new FileNode[childCount];
        int sibl = getChildId();
        int id = 0;
        while (sibl != 0) {
            final FileNode cuN = tAF.readNode(sibl);
            children[id++] = cuN;
            sibl = cuN.getSiblingId();
        }
        assert id == childCount : "FileNode : getChildren : childCound and child number doesn't match.";
        return children;
    }

    public boolean removeChild(FileNode node) throws IOException {
        boolean found = false;
        if (childCount == 1) {
            if (node.getNodeId() == getChildId()) {
                childCount--;
                setChildId(0);
//                boundary = null;
                tAF.writeNode(this);
                found = true;
            }
//            childCount--;
//            assert node.getNodeId() == getChildId() : "child ID should be same as node Id";
//            setChildId(0);
//            tAF.writeNode(this);
//            found = true;
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
                FileNode precChild = tAF.readNode(getChildId());
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
        if (found) tAF.deleteNode(node);
        return found;
    }

    public void removeChildren(FileNode[] nodes) throws IOException {
        for (FileNode nod : nodes) {
            removeChild(nod);
        }
    }
    
    @Override
    public void clear() {
        boundary = null;
        childId = 0;
        childCount = 0;
    }

    @Override
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

    @Override
    public void addChild(Node node) throws IOException {
        assert node instanceof FileNode;
        final FileNode nod = (FileNode)node;
        final double[] assertBound = node.getBoundary().clone();
        assert nod.getSiblingId() == 0 : "future added element should be distinct from others.";
        // connect child at other children (its sibling).
        childCount++;
        final int nextSibling = getChildId();
        setChildId(nod.getNodeId());
        nod.setParentId(getNodeId());
        nod.setSiblingId(nextSibling);
        if (boundary == null || ArraysExt.hasNaN(boundary)) {
            boundary = node.getBoundary().clone();
        }
        add(boundary, node.getBoundary());
        tAF.writeNode(this);
        assert Arrays.equals(node.getBoundary(), assertBound);
        tAF.writeNode(nod);
    }
    
    @Override
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
                final FileNode cuChild = tAF.readNode(sibl);
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

    @Override
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
            Logger.getLogger(FileNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileNode)) return false;
        final FileNode objNode = (FileNode) obj;
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

    @Override
    public double[] getBound() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node getChild(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node removeChild(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node[] removeChildren() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getObjectCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getObject(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] getObjects() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addElement(Object object, double... coordinate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addElements(Object[] objects, double[][] coordinates) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object removeObject(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object[] removeObjects() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCoordsCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] getCoordinate(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[][] getCoordinates() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] removeCoordinate(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[][] removeCoordinates() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
