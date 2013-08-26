/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display.primitive;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Objects;
import javax.swing.event.EventListenerList;
import org.apache.sis.measure.NumberRange;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.apache.sis.util.Classes;
import org.geotoolkit.display.DisplayElement;
import org.geotoolkit.display.SceneVisitor;
import org.geotoolkit.display.canvas.Canvas;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.util.collection.CollectionChangeListener;
import org.geotoolkit.util.collection.NotifiedCheckedList;
import org.opengis.display.primitive.Graphic;

/**
 * A scene node is an element in the graphic container.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class SceneNode extends DisplayElement implements Graphic {
    
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain SceneNode#getParent node parent} changed.
     */
    public static final String PARENT_KEY = "parent";
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain SceneNode#getVisible node visibility} changed.
     */
    public static final String VISIBLE_KEY = "visible";
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain SceneNode#getName node name} changed.
     */
    public static final String NAME_KEY = "name";
        
    private final List<SceneNode> children;
    protected SceneNode parent;
    protected boolean visible = true;
    protected String name = "";
    /**
     * The canvas that own this scene node, or {@code null} if none.
     */
    protected final Canvas canvas;
    /**
     * Used to listen to children events and push them back to the root.
     */
    private CollectionChangeListener childListener;
    
    /**
     * Create a default scene node which allows children.
     */
    public SceneNode(Canvas canvas) {
        this(canvas, true);
    }

    /**
     * 
     * @param allowChildren indicate this node allows children.
     */
    public SceneNode(final Canvas canvas, final boolean allowChildren) {
        ensureNonNull("canvas", canvas);
        this.canvas = canvas;
        if(!allowChildren){
            children = Collections.EMPTY_LIST;
        }else{
            childListener = new CollectionChangeListener() {
                @Override
                public void collectionChange(CollectionChangeEvent event) {
                    
                }
            };
            
            children = new NotifiedCheckedList<SceneNode>(SceneNode.class, 0) {
                @Override
                protected void notifyAdd(SceneNode item, int index) {
                    //remove node from previous parent if any
                    SceneNode parent = item.getParent();
                    if(parent != null){
                        parent.getChildren().remove(item);
                    }
                    //set this as parent
                    item.setParent(SceneNode.this);
                    item.addChildrenListener(childListener);
                    
                    //fire event
                    fireChildrenChange(CollectionChangeEvent.ITEM_ADDED,
                        Collections.singleton(item), NumberRange.create(index, true, index, true));
                }

                @Override
                protected void notifyAdd(Collection<? extends SceneNode> items, NumberRange<Integer> range) {
                    for(SceneNode item : items){
                        //remove node from previous parent if any
                        SceneNode parent = item.getParent();
                        if(parent != null){
                            parent.getChildren().remove(item);
                        }
                        //set this as parent
                        item.setParent(SceneNode.this);
                        item.addChildrenListener(childListener);
                    }
                    
                    //fire event
                    fireChildrenChange(CollectionChangeEvent.ITEM_ADDED,items, range);
                }

                @Override
                protected void notifyChange(SceneNode oldItem, SceneNode newItem, int index) {
                    //remove parent in old item
                    oldItem.setParent(null);
                    oldItem.removeChildrenListener(childListener);
                    
                    //remove node from previous parent if any
                    final SceneNode parent = newItem.getParent();
                    if(parent != null){
                        parent.getChildren().remove(newItem);
                    }
                    //set this as parent
                    newItem.setParent(SceneNode.this);
                    newItem.addChildrenListener(childListener);
                    
                    //fire event
                    fireChildrenChange(CollectionChangeEvent.ITEM_CHANGED,
                        Collections.singleton(newItem), NumberRange.create(index, true, index, true));
                }

                @Override
                protected void notifyRemove(SceneNode item, int index) {
                    //remove parent on item
                    item.setParent(null);
                    item.removeChildrenListener(childListener);
                    
                    //fire event
                    fireChildrenChange(CollectionChangeEvent.ITEM_REMOVED,
                        Collections.singleton(item), NumberRange.create(index, true, index, true));
                }

                @Override
                protected void notifyRemove(Collection<? extends SceneNode> items, NumberRange<Integer> range) {
                    for(SceneNode item : items){
                        //remove parent on item
                        item.setParent(null);
                        item.removeChildrenListener(childListener);
                    }
                    
                    //fire event
                    fireChildrenChange(CollectionChangeEvent.ITEM_REMOVED,items, range);
                }
            };
        }
    }

    /**
     * If this display object is contained in a canvas, returns the canvas that own it.
     * Otherwise, returns {@code null}.
     *
     * @return Canvas, The canvas that this graphic listen to.
     */
    public Canvas getCanvas() {
        return canvas;
    }
    
    /**
     * Get the parent scene node.
     * @return SceneNode , can be null
     */
    public SceneNode getParent() {
        return parent;
    }
    
    /**
     * Set this node parent.
     * @param parent , can be null
     */
    private void setParent(SceneNode parent){
        if (Objects.equals(name, this.name)) return;
        final SceneNode old = this.parent;
        this.parent = parent;
        firePropertyChange(PARENT_KEY, old, parent);
    }
    
    /**
     * Get scene node children, modifiable list.
     * 
     * @return List, never null, can be empty
     */
    public List<SceneNode> getChildren(){
        return children;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible == this.visible) return;
        this.visible = visible;
        firePropertyChange(VISIBLE_KEY, !visible, visible);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (Objects.equals(name, this.name)) return;
        final String old = this.name;
        this.name = name;
        firePropertyChange(NAME_KEY, old, name);
    }

    @Override
    public String toString() {
        return Trees.toString(name+" ("+Classes.getShortClassName(this)+")", children);
    }

    @Override
    public void dispose() {
        for(SceneNode child : getChildren()){
            child.dispose();
        }
    }
    
    public void addChildrenListener(CollectionChangeListener listener){
        getListenerList(true).add(CollectionChangeListener.class, listener);
    }
    
    public void removeChildrenListener(CollectionChangeListener listener){
        getListenerList(true).remove(CollectionChangeListener.class, listener);
    }
    
    /**
     * Accepts a visitor.
     */
    public Object accept(SceneVisitor visitor, Object extraData){
        return visitor.visit(this, extraData);
    }
    
    protected void fireChildrenChange(final int type, final SceneNode item, final NumberRange<Integer> range, final EventObject orig) {
        final EventListenerList lst = getListenerList(false);
        if(lst==null) return;
        
        final CollectionChangeListener[] lists = lst.getListeners(CollectionChangeListener.class);
        if(lists.length==0) return;
        
        final CollectionChangeEvent<SceneNode> event = new CollectionChangeEvent<>(this, item, type, range, orig);
        for (CollectionChangeListener listener : lists){
            listener.collectionChange(event);
        }

    }

    protected void fireChildrenChange(final int type, final Collection<? extends SceneNode> item, final NumberRange<Integer> range){
        final EventListenerList lst = getListenerList(false);
        if(lst==null) return;
        
        final CollectionChangeListener[] lists = lst.getListeners(CollectionChangeListener.class);
        if(lists.length==0) return;
        
        final CollectionChangeEvent<SceneNode> event = new CollectionChangeEvent<>(this,item,type,range, null);
        for (CollectionChangeListener listener : lists){
            listener.collectionChange(event);
        }
    }
    
}
