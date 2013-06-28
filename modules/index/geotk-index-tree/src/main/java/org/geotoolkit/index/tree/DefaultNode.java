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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.gui.swing.tree.Trees;
import static org.geotoolkit.index.tree.Node.PROP_ISLEAF;
import org.apache.sis.util.Classes;
import org.geotoolkit.index.tree.hilbert.HilbertNode;
import org.geotoolkit.util.logging.Logging;
import org.opengis.geometry.Envelope;

/**
 * Create a Node adapting with Euclidean dimensions datas.
 *
 * @author Rémi Marechal (Geomatys)
 * @author Johann Sorel  (Geomatys)
 */
public class DefaultNode extends Node {

    private Map<String, Object> userProperties;
    protected double[] boundary = null;
    
    // children
    protected int countChild;
    protected Node[] children;
    
    //elements
    private int countObjects;
    private Object[] objects;
    private int countCoords;
    private double[][] coordinates;
    
    private static final Logger LOGGER = Logging.getLogger(DefaultNode.class);
    
    /**
     * Create an empty {@code DefaultNode}.
     *
     * @param tree
     */
    public DefaultNode(final Tree tree) {
        this(tree, null, null, null, null, null, null);
    }

    /**Create {@code DefaultNode}.
     *
     * @param tree pointer on {@code Tree}.
     * @param parent pointer on {@code Node} parent.
     * @param children subNode.
     * @param objects data(s) to add.
     * @throws IllegalArgumentException if tree pointer is null.
     */
    public DefaultNode(final Tree tree, final Node parent, final double[] lowerCorner, final double[] upperCorner, 
            final Node[] children, final Object[] objects, final double[][] coordinates) {
        super(tree);
        this.parent = parent;
        final int maxSizePermit = tree.getMaxElements();
        if (children != null && children.length > maxSizePermit) 
            throw new IllegalArgumentException("Node number is taller than max element size permit per Node");
        if (objects != null && objects.length > maxSizePermit) 
            throw new IllegalArgumentException("Elements number is taller than max element size permit per Node");
        if (objects != null) {
            assert (coordinates != null) : "impossible to create node with only entries. You should set them coordinates";
            assert (coordinates.length == objects.length) : "entries and coordinates should have same lenght";
        }
        if (children != null && objects != null && (children.length + objects.length) > maxSizePermit)
            throw new IllegalArgumentException("Sum of Node and Entries number is taller than max element size permit per Node");
        
        //init children
        countChild = 0;
        
        if (children != null) {
            this.children = new Node[maxSizePermit << 1];//normaly we accept just only one element of overflow.
            for(int i = 0, l = children.length; i < l; i++) {
                final Node n3d = children[i];
                if (n3d != null) {
                    n3d.setParent(this);
                    addChild(n3d);
                }
            }
        }
                
        //init elements
        countObjects    = 0;
        countCoords     = 0;
        
        if (objects != null) {
            this.objects    = new Object[maxSizePermit << 1];
            this.coordinates = new double[maxSizePermit << 1][];
            for(int i = 0, l = objects.length; i < l; i++) {
                final Object elt = objects[i];
                if (elt != null) {
                    addElement(elt, coordinates[i]);
                }
            }
        }
        
        //init boundary
        if(lowerCorner != null && upperCorner != null){
            final int length = lowerCorner.length;
            if(length != upperCorner.length){
                throw new IllegalArgumentException("DefaultNode constructor : envelope corners are not in same dimension");
            }
            this.boundary = new double[length << 1];
            System.arraycopy(lowerCorner, 0, boundary, 0, length);
            System.arraycopy(upperCorner, 0, boundary, length, length);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setBound(double[] bound) {
        if(boundary == bound){
            return;
        }
        boundary = (bound == null) ? null : bound.clone(); 
        if(parent != null){
            parent.setBound(null);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getBound() {
        return this.boundary;
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final List toString = (isLeaf()) ? Arrays.asList(objects):Arrays.asList(children);
        String strparent =  (parent == null)?"null":String.valueOf(parent.hashCode());
        return Trees.toString(Classes.getShortClassName(this)+" : "+this.hashCode()+" parent : "+strparent
                + " leaf : "+isLeaf()+ " userPropLeaf : "+(Boolean)getUserProperty(PROP_ISLEAF), toString);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(Node node) {
        if (objects != null || coordinates != null)
            throw new IllegalStateException("You can't add children in a leaf.");
//        assert !node.isEmpty() : "add empty node is useless."; // temporary unable to Reader Writer test
        if (children == null) children = new Node[tree.getMaxElements() << 1];
        children[countChild++] = node;
        node.setParent(this);
        setBound(null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addChildren(Node[] nodes) {
        if (nodes.length > tree.getMaxElements())
            throw new IllegalArgumentException("Node number is taller than max element size permit per Node");
        for(int i = 0, l = nodes.length; i < l; i++) {
            final Node n3d = nodes[i];
            if (n3d != null) {
                n3d.setParent(this);
                addChild(n3d);
            }
        }
        setBound(null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Node removeChild(int i) {
        ArgumentChecks.ensureBetween("remove child index ", 0, countChild, i);
        final int length = children.length;
        final Node result = children[i];
        //concatenate node table
        System.arraycopy(children, i+1, children, (i), length - (i+1));
        children[length - 1] = null;
        countChild --;
        setBound(null);
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Node[] removeChildren() {
        Node[] childs = children;
        children = null;
        countChild = 0;
        setBound(null);
        return childs;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Node getChild(int i) {
        ArgumentChecks.ensureBetween("get child index ", 0, countChild, i);
        return children[i];
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Node[] getChildren() {
        return children;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getChildCount() {
        return countChild;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getObjectCount() {
        return countObjects;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addElement(Object object, double... coordinate) {
        if (children != null) 
            throw new IllegalStateException("You can't add element in a Node which isn't leaf.");
        if (objects == null) objects = new Object[tree.getMaxElements() << 1];
        if (coordinates == null) coordinates = new double[tree.getMaxElements() << 1][];
        objects[countObjects++] = object;
        coordinates[countCoords++] = coordinate;
        setBound(null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addElements(Object[] objects, double[][] coordinates) {
        ArgumentChecks.ensureNonNull("Objects", objects);
        ArgumentChecks.ensureNonNull("coordinates", coordinates);
        final int l = objects.length;
        assert (l == coordinates.length) : "addElements : objects and coordinates tables should have same lenght";
        if (l > (objects.length - countObjects))
            throw new IllegalArgumentException("You try to insert more elements than remaining place in elements table");
        
//        System.arraycopy(objects, 0, elements, countElements, l);
//        System.arraycopy(coordinates, 0, this.coordinates, countChild, l);
//        countElements += l;
//        countCoords += l;
        for (int i = 0; i < l; i++) {
            if (objects[i] != null) addElement(objects[i], coordinates[i]);
        }
        setBound(null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double[][] getCoordinates() {
        return coordinates;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getObjects() {
        return objects;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getCoordinate(int i) {
        ArgumentChecks.ensureBetween("get coordinate index ", 0, countCoords, i);
        return coordinates[i];
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getObject(int i) {
        ArgumentChecks.ensureBetween("get element index ", 0, countObjects, i);
        return objects[i];
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object removeObject(int i) {
        ArgumentChecks.ensureBetween("remove element index ", 0, countObjects, i);
        
        if (i >= countObjects) return null;
        final Object elt = objects[i];
        final int l = objects.length;
        System.arraycopy(objects, i+1, objects, i, l - (i + 1));
        objects[l - 1] = null;
        countObjects--;
        setBound(null);
        return elt;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double[] removeCoordinate(int i) {
        ArgumentChecks.ensureBetween("remove coordinate index ", 0, countCoords, i);
        
        if (i >= countCoords) return null;
        final int l = coordinates.length;
        final double[] coords = coordinates[i];
        System.arraycopy(coordinates, i+1, coordinates, i, l-(i+1));
        coordinates[l - 1] = null;
        countCoords--;
        setBound(null);
        
        return coords;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getCoordsCount() {
        return countCoords;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] removeObjects() {
        final Object[] result = objects;
        objects = null;
        countObjects = 0;
        setBound(null);
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double[][] removeCoordinates() {
        final double[][] coords = coordinates;
        coordinates = null;
        countCoords = 0;
        setBound(null);
        return coords;
    }

    @Override
    public void clear() {
        setBound(null);
        countChild   = 0;
        countCoords  = 0;
        countObjects = 0;
        children     = null;
        coordinates  = null;
        objects      = null;
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public boolean checkInternal() { 
        if (isEmpty()) {
            //: "Node should never be empty.";
            LOGGER.log(Level.WARNING, "candidate is empty");
            throw new IllegalStateException("Candidate is empty.");
        }
        if (getTree().getRoot() != this) {
            if (getParent() == null) {
                throw new IllegalStateException("Node should not have a null parent.");
            }
        }
        if (isLeaf()) {
            final double[] tempboundary = getBound();
            final int countC = getCoordsCount();
            if (countC <= 0 || countC != getObjectCount()) {
                throw new IllegalStateException("Candidate leaf counts should be consistent.");
            }
            if (tempboundary != null) {
                double[] bound = getCoordinate(0).clone();
                for (int i = 1; i < countC; i++) {
                    DefaultTreeUtils.add(bound, getCoordinate(i));
                }
                if (!Arrays.equals(bound, tempboundary)) {
                    throw new IllegalStateException("Candidate leaf boundary should be consistent.");
                }
            }
            
        } else {
            final int countChilds = getChildCount();
            if (countChilds <= 0) {
                return false;
            }
            final double[] bound = getBound();
            for (int i = 0; i < countChilds; i++) {
                final Node child  = getChild(i);
                if (child.getParent() != this) {
                    throw new IllegalStateException("Candidate Node child has a wrong parent.");
                }
            }
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
