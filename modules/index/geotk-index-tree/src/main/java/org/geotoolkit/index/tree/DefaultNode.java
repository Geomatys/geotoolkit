/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.collection.NotifiedCheckedList;
import org.geotoolkit.util.converter.Classes;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**Create a Node adapting with Euclidean dimensions datas.
 * 
 * @author Rémi Marechal (Geomatys)
 * @author Johann Sorel  (Geomatys)
 */
public class DefaultNode extends AbstractNode<DefaultNode, GeneralEnvelope> {
    
    protected GeneralEnvelope boundary;
    private DefaultNode parent;
    private final Tree tree;
    
    private final List<DefaultNode> children = new CrossList<DefaultNode>(DefaultNode.class) {

        @Override
        protected void notifyAdd(DefaultNode e, int i) {
            super.notifyAdd(e, i);
            clearBounds();
        }

        @Override
        protected void notifyAdd(Collection<? extends DefaultNode> clctn, NumberRange<Integer> nr) {
            super.notifyAdd(clctn, nr);
            clearBounds();
        }

        @Override
        protected void notifyRemove(DefaultNode e, int i) {
            super.notifyRemove(e, i);
            clearBounds();
        }

        @Override
        protected void notifyRemove(Collection<? extends DefaultNode> clctn, NumberRange<Integer> nr) {
            super.notifyRemove(clctn, nr);
            clearBounds();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            clearBounds();
        }
    };
    
    private final List<Envelope> entries = new NotifiedCheckedList<Envelope>(Envelope.class) {
        @Override
        protected void notifyAdd(Envelope e, int i) {
            clearBounds();
        }
        @Override
        protected void notifyAdd(Collection<? extends Envelope> clctn, NumberRange<Integer> nr) {
            clearBounds();
        }
        @Override
        protected void notifyRemove(Envelope e, int i) {
            clearBounds();
        }
        @Override
        protected void notifyRemove(Collection<? extends Envelope> clctn, NumberRange<Integer> nr) {
            clearBounds();
        }
    };
    
    /**Create an empty {@code Node3D}.
     * 
     * @param tree 
     */
    public DefaultNode(final Tree tree) {
        this(tree, null, null, null, null, null);
    }
    
    /**Create {@code Node3D}.
     * 
     * @param tree pointer on {@code Tree}.
     * @param parent pointer on {@code Node3D} parent.
     * @param children subNode.
     * @param entries data(s) to add.
     * @throws IllegalArgumentException if tree pointer is null.
     */
    public DefaultNode(final Tree tree, final DefaultNode parent, final DirectPosition lowerCorner, final DirectPosition upperCorner, final List<DefaultNode> children, final List<Envelope> entries) {
        ArgumentChecks.ensureNonNull("tree", tree);
        this.tree = tree;
        this.parent = parent;
        if(children!=null){
            for(DefaultNode n3d : children){
                n3d.setParent(this);
            }
            this.children.addAll(children);
        }
        if(entries!=null)this.entries.addAll(entries);
        if(lowerCorner != null && upperCorner != null){
            if(lowerCorner.getDimension() != upperCorner.getDimension()){
                throw new IllegalArgumentException("DefaultNode constructor : envelope corners are not in same dimension");
            }
            this.boundary = new GeneralEnvelope(new GeneralDirectPosition(lowerCorner), new GeneralDirectPosition(upperCorner));
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public List<DefaultNode> getChildren() {
        return children;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isLeaf() {
        return getChildren().isEmpty();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isEmpty() {
        return (getChildren().isEmpty() && getEntries().isEmpty());
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isFull() {
        return (getChildren().size()+getEntries().size())>=getTree().getMaxElements();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public GeneralEnvelope getBoundary() {
        if(boundary==null){
            calculateBounds();
        }
        return boundary;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public List<Envelope> getEntries() {
        return entries;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public DefaultNode getParent() {
        return parent;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public Tree getTree() {
        return tree;
    }
    
    /**
     * Affect a {@code null} on boundary.
     */
    protected final void clearBounds() {
        boundary=null;
        fireCollectionEvent();
    }
    
    /**
     * Compute {@code Node2D} boundary. 
     */
    protected void calculateBounds(){
        for(Envelope ent2D : getEntries()){
            addBound(ent2D);
        }
        for(DefaultNode n2D : getChildren()){
            addBound(n2D.getBoundary());
        }
    }

    /**Update boundary size from {@code GeneralEnvelope}.
     * 
     * @param env {@code Node} or entry boundary.
     */
    protected void addBound(final Envelope env){
        if(boundary==null){
            boundary = new GeneralEnvelope(env);
        }else{
            boundary.add(env);
        }
    }
    
    /**Affect a new parent pointer.
     * 
     * @param parent 
     */
    public void setParent(final DefaultNode parent){
        this.parent = parent;
    }
    
    /**
     * {@inheritDoc}. 
     */
    @Override
    public String toString() {
        final Collection col = new ArrayList(entries);
        col.addAll(children);
        String strparent =  (parent == null)?"null":String.valueOf(parent.hashCode()); 
        return Trees.toString(Classes.getShortClassName(this)+" : "+this.hashCode()+" parent : "+strparent, col);
    }
}
