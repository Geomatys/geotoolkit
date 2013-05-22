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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.gui.swing.tree.Trees;
import static org.geotoolkit.index.tree.Node.PROP_ISLEAF;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.collection.NotifiedCheckedList;
import org.apache.sis.util.Classes;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**Create a Node adapting with Euclidean dimensions datas.
 *
 * @author Rémi Marechal (Geomatys)
 * @author Johann Sorel  (Geomatys)
 */
public class DefaultNode extends Node {

    private final List<Node> children = new NotifiedCheckedList<Node>(Node.class) {

        @Override
        protected void notifyAdd(Node e, int i) {
            setBound(null);
        }

        @Override
        protected void notifyAdd(Collection<? extends Node> clctn, NumberRange<Integer> nr) {
            setBound(null);
        }

        @Override
        protected void notifyRemove(Node e, int i) {
            setBound(null);
        }

        @Override
        protected void notifyRemove(Collection<? extends Node> clctn, NumberRange<Integer> nr) {
            setBound(null);
        }

        @Override
        protected void notifyChange(Node oldItem, Node newItem, int index) {
            setBound(null);
        }
    };

    private final List<Envelope> entries = new NotifiedCheckedList<Envelope>(Envelope.class) {
        @Override
        protected void notifyAdd(Envelope e, int i) {
            setBound(null);
        }
        @Override
        protected void notifyAdd(Collection<? extends Envelope> clctn, NumberRange<Integer> nr) {
            setBound(null);
        }
        @Override
        protected void notifyRemove(Envelope e, int i) {
            setBound(null);
        }
        @Override
        protected void notifyRemove(Collection<? extends Envelope> clctn, NumberRange<Integer> nr) {
            setBound(null);
        }

        @Override
        protected void notifyChange(Envelope oldItem, Envelope newItem, int index) {
            setBound(null);
        }
    };

    private Map<String, Object> userProperties;
    private GeneralEnvelope boundary;
    
    /**Create an empty {@code DefaultNode}.
     *
     * @param tree
     */
    public DefaultNode(final Tree tree) {
        this(tree, null, null, null, null, null);
    }

    /**Create {@code DefaultNode}.
     *
     * @param tree pointer on {@code Tree}.
     * @param parent pointer on {@code Node} parent.
     * @param children subNode.
     * @param entries data(s) to add.
     * @throws IllegalArgumentException if tree pointer is null.
     */
    public DefaultNode(final Tree tree, final Node parent, final DirectPosition lowerCorner, final DirectPosition upperCorner, final List<Node> children, final List<Envelope> entries) {
        super(tree);
        this.parent = parent;
        if(children!=null){
            for(Node n3d : children){
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
    public void setBound(Envelope bound){
        if(boundary == bound){
            return;
        }
        boundary = (bound == null) ? null : new GeneralEnvelope(bound);
        
        if(parent != null){
            parent.setBound(null);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Envelope getBound(){
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
     * {@inheritDoc}
     */
    @Override
    public List<Node> getChildren() {
        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Envelope> getEntries() {
        return entries;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final Collection col = new ArrayList(entries);
        col.addAll(children);
        String strparent =  (parent == null)?"null":String.valueOf(parent.hashCode());
        return Trees.toString(Classes.getShortClassName(this)+" : "+this.hashCode()+" parent : "+strparent
                + " leaf : "+isLeaf()+ " userPropLeaf : "+(Boolean)getUserProperty(PROP_ISLEAF), Collections.EMPTY_LIST);
    }
}
