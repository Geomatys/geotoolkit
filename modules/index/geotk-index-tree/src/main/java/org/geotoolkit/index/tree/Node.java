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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.Envelope;

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
    public static final String PROP_CENTROID = "centroid";
    public static final String PROP_CENTROIDS = "centroids";
            
    protected Node parent;
    protected final Tree tree;

    public Node(Tree tree) {
        ArgumentChecks.ensureNonNull("tree", tree);
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
    public abstract void setBound(Envelope bound);

    /**<blockquote><font size=-1>
     * <strong>NOTE: Null value can be return.</strong>
     * </font></blockquote>
     *
     * @return {@code Node} boundary without re-computing sub-node boundary.
     */
    public abstract Envelope getBound();

    /**Affect a new {@code Node} parent.
     *
     * @param parent {@code Node} parent pointer.
     */
    public void setParent(Node parent){
        this.parent = parent;
    }

    /**
     * @return subNodes.
     */
    public abstract List<Node> getChildren();
    
    /**
     * @return entries.
     */
    public abstract List<Envelope> getEntries();
    
    /**A leaf is a {@code Node} at extremity of {@code Tree} which contains only entries.
     *
     * @return true if it is a leaf else false (branch).
     */
    public boolean isLeaf() {
        final Object userPropIsLeaf = getUserProperty(PROP_ISLEAF);
        if(userPropIsLeaf != null){
            return (Boolean)userPropIsLeaf;
        }
        return getChildren().isEmpty();
    }

    /**
     * @return true if {@code Node} contains nothing else false.
     */
    public boolean isEmpty() {
        final Object userPropIsLeaf = getUserProperty(PROP_ISLEAF);
        if(userPropIsLeaf != null && ((Boolean)userPropIsLeaf)){
            for(Node cell : getChildren()){
                if(!cell.isEmpty()){
                    return false;
                }
            }
            return true;
        }
        return (getChildren().isEmpty() && getEntries().isEmpty());
    }

    /**
     * @return true if node elements number equals or overflow max elements
     *         number autorized by {@code Tree} else false.
     */
    public boolean isFull() {
        final Object userPropIsLeaf = getUserProperty(PROP_ISLEAF);
        if(userPropIsLeaf != null && ((Boolean)userPropIsLeaf)){
            for(Node cell : getChildren()){
                if(!cell.isFull()){
                    return false;
                }
            }
            return true;
        }
        return (getChildren().size()+getEntries().size())>=getTree().getMaxElements();
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
    public Envelope getBoundary() {
        GeneralEnvelope env = (GeneralEnvelope) getBound();
        if(env != null){
            return env;
        }
        env = calculateBounds();
        if(!env.isNull()){
            setBound(env);
        }
        return env;
    }
    
    /**
     * Compute {@code Node} boundary from stocked sub-node or entries .
     */
    private GeneralEnvelope calculateBounds(){
        GeneralEnvelope boundary = null;        
        for(Envelope ent2D : getEntries()){
            boundary = addBound(boundary,ent2D);
        }
        for(Node n2D : getChildren()){
            if(!n2D.isEmpty()){
                boundary = addBound(boundary,n2D.getBoundary());
            }
        }
        if(boundary == null){
            boundary = new GeneralEnvelope(tree.getCrs());
            boundary.setToNull();
        }
        return boundary;
    }

    /**Update boundary size from {@code Envelope}.
     *
     * @param env {@code Node} or entry boundary.
     */
    private static GeneralEnvelope addBound(GeneralEnvelope boundary, final Envelope env){
        if(boundary==null){
            boundary = new GeneralEnvelope(env);
        }else{
            boundary.add(env);
        }
        return boundary;
    }
    
}
