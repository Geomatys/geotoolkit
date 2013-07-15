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
/**
 * Create "generic" Node.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class Node {

    public static final String PROP_ISLEAF = "isleaf";
    public static final String PROP_HILBERT_ORDER = "hilbertOrder";
    public static final String PROP_HILBERT_TABLE = "tabHV";
    public static final String PROP_HILBERT_VALUE = "hilbertValue";
            
    protected Node parent;
    protected final Tree tree;
    
    public Node(Tree tree) {
//        ArgumentChecks.ensureNonNull("tree", tree);
        this.tree = tree;
    }

    /**
     * @param key
     * @return user property for given key
     */
    public abstract Object getUserProperty(final String key);

    /**Add user property with key access.
     *
     * @param key
     * @param value Object will be stocked.
     */
    public abstract void setUserProperty(final String key, final Object value);

    /**
     * Affect a {@code Node} boundary.
     */
    public abstract void setBound(double[] bound);

    /**<blockquote><font size=-1>
     * <strong>NOTE: Null value can be return.</strong>
     * </font></blockquote>
     *
     * @return {@code Node} boundary without re-computing sub-node boundary.
     */
    public abstract double[] getBound();
    
    /**
     * Initialize Node just like after creating.
     */
    public abstract void clear();

    /**Affect a new {@code Node} parent.
     *
     * @param parent {@code Node} parent pointer.
     */
    public void setParent(Node parent){
        this.parent = parent;
    }
    
    /////////// Node ////////////////
    
    /**
     * Return children number within this Node.
     * 
     * @return children number within this Node.
     */
    public abstract int getChildCount();
    
    /**
     * Add a sub-node (node child) in this Node.
     * 
     * @param node will be added.
     */
    public abstract void addChild(Node node) throws IOException;
    
    /**
     * Add some of sug-node.
     * 
     * @param nodes sub-node table.
     */
    public abstract void addChildren(Node[] nodes) throws IOException;
    
    /**
     * Return sub-node (child) at index i.
     * 
     * @param i index of sub-node asked.
     * @return sub-node (child) at index i.
     */
    public abstract Node getChild(int i);
    
    /**
     * Return all sub-nodes within this Node.
     * 
     * @return all sub-nodes within this Node. 
     */
    public abstract Node[] getChildren() throws IOException;
    
    /**
     * Remove sub-node from this Node at i index.
     * 
     * @param i index of sub-node which will be remove.
     * @return sub-node which just be remove.
     */
    public abstract Node removeChild(int i);
    
    /**
     * Remove all sub-node from this Node.
     * 
     * @return removed sub-Node table from this Node.
     */
    public abstract Node[] removeChildren();
    
    
    /////////// Elements ///////////////
    
    /**
     * Return element number within this Node.
     * 
     * @return element number within this Node.
     */
    public abstract int getObjectCount();
    
    /**
     * Return Object at index i within this Node.
     * 
     * @param i index of object which will be returned.
     * @return Object at index i within this Node. 
     */
    public abstract Object getObject(int i);
    
    /**
     * Return all Objects within this Node.
     * 
     * @return all Objects within this Node. 
     */
    public abstract Object[] getObjects();
    
    /**
     * Add element which is composed by an Object with its boundary double table coordinates.
     * 
     * @param object object which will be stored.
     * @param coordinate object coordinates.
     */
    public abstract void addElement(Object object, double... coordinate) throws IOException;//set element et stock
    
    /**
     * Add some of element in this Node.
     * 
     * @param objects some of objects which will be added.
     * @param coordinates some of respective object coordinates.
     * @throws IllegalArgumentException if objects and coordinates table haven't got same length.
     */
    public abstract void addElements(Object[] objects, double[][] coordinates) throws IOException;
    
    /**
     * Remove and return object at index i in this Node.
     * 
     * @param i index of object which will be remove.
     * @return object at index i in this Node.
     */
    public abstract Object removeObject(int i);
    
    /**
     * Return and remove all Objects stored in this Node.
     * 
     * @return all Objects stored in this Node.
     */
    public abstract Object[] removeObjects();
    
    
    ///////////// Coordinates ////////////
    
    /**
     * <p>Return coordinate number stored in this Node.<br/>
     * Normaly equals to {@link #getObjectCount() }.</p>
     * 
     * @return coordinate number stored in this Node.
     */
    public abstract int getCoordsCount();
    
    /**
     * Return coordinate object at i index.
     * 
     * @param i index of coordinates which will be asked.
     * @return coordinate object at i index.
     */
    public abstract double[] getCoordinate(int i);
    
    /**
     * Return all coordinates within this Node.
     * 
     * @return all coordinates within this Node. 
     */
    public abstract double[][] getCoordinates();
    
    /**
     * Return and remove coordinate at i index within this Node.
     * 
     * @param i index of coordinate which will be removed.
     * @return coordinate at i index within this Node.
     */
    public abstract double[] removeCoordinate(int i);
    
    /**
     * Return and remove all coordinates within this Node.
     * 
     * @return all coordinates within this Node.
     */
    public abstract double[][] removeCoordinates();
    
    /**
     * Return {@code true} if Node is consistent from some of rules.<br/>
     * Moreover this method is only call in a {@code assert} commande.
     * 
     * @return {@code true} if Node is consistent from some of rules.
     */
    public abstract boolean checkInternal() throws IOException;
    
    /**
     * A leaf is a {@code Node} at extremity of {@code Tree} which contains only entries.
     *
     * @return true if it is a leaf else false (branch).
     */
    public boolean isLeaf() throws IOException {
        final Object userPropIsLeaf = getUserProperty(PROP_ISLEAF);
        if (userPropIsLeaf != null) {
            return (Boolean)userPropIsLeaf;
        }
        return getChildren() == null;
    }

    /**
     * @return true if {@code Node} contains nothing else false.
     */
    public boolean isEmpty() {
        final Object userPropIsLeaf = getUserProperty(PROP_ISLEAF);
        if(userPropIsLeaf != null && ((Boolean)userPropIsLeaf)){
            for(int i = 0, s = getChildCount(); i < s; i++) {
                if(!getChild(i).isEmpty()){
                    return false;
                }
            }
            return true;
        }
        return (getChildCount() == 0 && getObjectCount() == 0);
    }

    /**
     * @return true if node elements number equals or overflow max elements
     *         number autorized by {@code Tree} else false.
     */
    public boolean isFull() throws IOException {
        final Object userPropIsLeaf = getUserProperty(PROP_ISLEAF);
        if (Boolean.TRUE.equals(userPropIsLeaf)) {
            for(int i = 0, s = getChildCount(); i < s; i++){
                if(!getChild(i).isFull()){
                    return false;
                }
            }
            return true;
        }
        return (getChildCount() + getObjectCount()) >= getTree().getMaxElements();
    }
    
    /**
     * @return {@code Node} parent pointer.
     */
    public Node getParent(){
        return parent;
    }

    /**
     * @return {@code Tree} pointer.
     */
    public Tree getTree(){
        return tree;
    }

    /**
     * <blockquote><font size=-1>
     * <strong>NOTE: if boundary is null, method re-compute all subnode boundary.</strong>
     * </font></blockquote>
     * @return boundary.
     */
    public double[] getBoundary() throws IOException {
        double[] env = getBound();
        if (env != null) {
            return env;
        }
        env = calculateBounds();
        if (env != null) {
            for (double val : env) {
                if (Double.isNaN(val)) {
                    throw new IllegalStateException("getboundary : boundary from sub-node should not contain NAN values.");
                }
            }
            setBound(env);
        } else {
            throw new IllegalStateException("get boundary : boundary from sub - node should not be null.");
        }
        return env;
    }
    
    /**
     * Compute {@code Node} boundary from stocked sub-node or entries .
     */
    protected double[] calculateBounds() throws IOException {
        double[] boundary = null;    
        final int s;
        if (isLeaf()) {
            s = getCoordsCount();
            if (s == 0) throw new IllegalStateException("impossible to compute boundary from empty Leaf.");
            boundary = getCoordinate(0).clone();
            for(int i = 1; i < s; i++) {
                DefaultTreeUtils.add(boundary, getCoordinate(i));
            }
        } else {
            s = getChildCount();
            if (s == 0) throw new IllegalStateException("impossible to compute boundary from empty Node.");
            boundary = getChild(0).getBoundary().clone();
            for(int i = 1; i < s; i++) {
                DefaultTreeUtils.add(boundary, getChild(i).getBoundary());
            }
        }
        return boundary;
    }
}
