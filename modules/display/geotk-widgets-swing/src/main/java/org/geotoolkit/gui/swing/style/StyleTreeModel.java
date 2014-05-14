/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.gui.swing.style;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.geotoolkit.style.FeatureTypeStyleListener;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.RuleListener;
import org.geotoolkit.style.StyleListener;
import org.geotoolkit.style.StyleUtilities;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.opengis.feature.type.Name;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.SemanticType;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class StyleTreeModel implements TreeModel, StyleListener, FeatureTypeStyleListener, RuleListener {

    // style elements listeners
    private final StyleListener.Weak weakStyleListener          = new StyleListener.Weak(null, this);
    private final FeatureTypeStyleListener.Weak weakFTSListener = new FeatureTypeStyleListener.Weak(null, this);
    private final RuleListener.Weak weakRuleListener            = new RuleListener.Weak(null, this);

    private final EventListenerList listeners = new EventListenerList();
    private Object root;

    public StyleTreeModel(Object root) {
        setRoot(root);
    }
    
    /**
     * Set the model Style
     * @param style , can't be null
     */
    public void setRoot(final Object style) {
        weakStyleListener.dispose();
        weakFTSListener.dispose();
        weakRuleListener.dispose();

        this.root = style;

        if(this.root != null){
            if(style instanceof MutableStyle){
                weakStyleListener.registerSource((MutableStyle)style);
            }else if(style instanceof MutableFeatureTypeStyle){
                weakFTSListener.registerSource((MutableFeatureTypeStyle)style);
            }else if(style instanceof MutableRule){
                weakRuleListener.registerSource((MutableRule)style);
            }
            fireStructureChanged(new TreeModelEvent(this, new TreePath(root)));
        }
    }
    
    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if(parent instanceof Style){
            return ((Style)parent).featureTypeStyles().get(index);
        }else if(parent instanceof FeatureTypeStyle){
            return ((FeatureTypeStyle)parent).rules().get(index);
        }else if(parent instanceof Rule){
            return ((Rule)parent).symbolizers().get(index);
        }else{
            return -1;
        }
    }
    
    public void removeChild(Object parent, Object child) {
        if(parent instanceof Style){
            ((Style)parent).featureTypeStyles().remove(child);
        }else if(parent instanceof FeatureTypeStyle){
            ((FeatureTypeStyle)parent).rules().remove(child);
        }else if(parent instanceof Rule){
            ((Rule)parent).symbolizers().remove(child);
        }
    }

    /**
     * duplicate a node
     */
    public void duplicateNode(Object parent, Object child) {

        //style events will refresh the style
        if (child instanceof MutableFeatureTypeStyle) {
            final MutableFeatureTypeStyle fts = StyleUtilities.copy((MutableFeatureTypeStyle) child);
            final int index = getIndexOfChild((MutableStyle) parent, (MutableFeatureTypeStyle) child) + 1;
            ((MutableStyle) parent).featureTypeStyles().add(index, fts);
        } else if (child instanceof MutableRule) {
            final MutableRule rule = StyleUtilities.copy((MutableRule) child);
            final int index = getIndexOfChild((MutableFeatureTypeStyle) parent, (MutableRule) child) + 1;
            ((MutableFeatureTypeStyle) parent).rules().add(index, rule);
        } else if (child instanceof Symbolizer) {
            //no need to copy symbolizer, they are immutable
            final Symbolizer symbol = (Symbolizer) child;
            final int index = getIndexOfChild((MutableRule) parent, (Symbolizer) child) + 1;
            ((MutableRule) parent).symbolizers().add(index, symbol);
        }

    }
    
    @Override
    public int getChildCount(Object parent) {
        if(parent instanceof Style){
            return ((Style)parent).featureTypeStyles().size();
        }else if(parent instanceof FeatureTypeStyle){
            return ((FeatureTypeStyle)parent).rules().size();
        }else if(parent instanceof Rule){
            return ((Rule)parent).symbolizers().size();
        }else{
            return 0;
        }
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof Symbolizer;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if(parent instanceof Style){
            return ((Style)parent).featureTypeStyles().indexOf(child);
        }else if(parent instanceof FeatureTypeStyle){
            return ((FeatureTypeStyle)parent).rules().indexOf(child);
        }else if(parent instanceof Rule){
            return ((Rule)parent).symbolizers().indexOf(child);
        }else{
            return -1;
        }
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(TreeModelListener.class, l);
    }

    // style element events
    @Override
    public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event) {
        fireEvent(new TreePath(root), event);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        fireEvent(new TreePath(root), event);
    }

    @Override
    public void ruleChange(CollectionChangeEvent<MutableRule> event) {
        fireEvent(new TreePath(root), event);
    }

    @Override
    public void featureTypeNameChange(CollectionChangeEvent<Name> event) {
        fireEvent(new TreePath(root), event);
    }

    @Override
    public void semanticTypeChange(CollectionChangeEvent<SemanticType> event) {
    }

    @Override
    public void symbolizerChange(CollectionChangeEvent<Symbolizer> event) {
        fireEvent(new TreePath(root), event);
    }
        
    private void fireNodesChanged(TreeModelEvent event){
        final TreeModelListener[] lsts = listeners.getListeners(TreeModelListener.class);
        for(TreeModelListener lst : lsts){
            lst.treeNodesChanged(event);
        }
    }
    
    private void fireNodesInserted(TreeModelEvent event){
        final TreeModelListener[] lsts = listeners.getListeners(TreeModelListener.class);
        for(TreeModelListener lst : lsts){
            lst.treeNodesInserted(event);
        }
    }
    
    private void fireNodesRemoved(TreeModelEvent event){
        final TreeModelListener[] lsts = listeners.getListeners(TreeModelListener.class);
        for(TreeModelListener lst : lsts){
            lst.treeNodesRemoved(event);
        }
    }
    
    private void fireStructureChanged(TreeModelEvent event){
        final TreeModelListener[] lsts = listeners.getListeners(TreeModelListener.class);
        for(TreeModelListener lst : lsts){
            lst.treeStructureChanged(event);
        }
    }
    
    private void fireEvent(TreePath path, EventObject event){
        
        if(event instanceof CollectionChangeEvent){
            final CollectionChangeEvent cevent = (CollectionChangeEvent) event;
            final int type = cevent.getType();

            //we might can events on featuretype names or semantics
            //do not propagante those
            final Collection col = cevent.getItems();
            if(col==null || col.isEmpty()) return;
            final Object candidate = col.iterator().next();
            if(!(candidate instanceof Rule 
              || candidate instanceof Symbolizer 
              || candidate instanceof FeatureTypeStyle)){
                return;
            }
            
            if(type == CollectionChangeEvent.ITEM_ADDED){
                final Object[] objs = cevent.getItems().toArray();
                final int[] indices = new int[objs.length];
                indices[0] = (int) cevent.getRange().getMinDouble();
                for(int i=1;i<indices.length;i++){
                    indices[i] = indices[i-1]+1;
                }
                final TreeModelEvent te = new TreeModelEvent(this, path, indices, objs);
                fireNodesInserted(te);
            }else if(type == CollectionChangeEvent.ITEM_REMOVED){
                final Object[] objs = cevent.getItems().toArray();
                final int[] indices = new int[objs.length];
                indices[0] = (int) cevent.getRange().getMinDouble();
                for(int i=1;i<indices.length;i++){
                    indices[i] = indices[i-1]+1;
                }
                final TreeModelEvent te = new TreeModelEvent(this, path, indices, objs);
                fireNodesRemoved(te);
            }else if(type == CollectionChangeEvent.ITEM_CHANGED){
                if(cevent.getChangeEvent()!=null){
                    //children event
                    final Object[] objs = cevent.getItems().toArray();
                    path = path.pathByAddingChild(objs[0]);
                    fireEvent(path, cevent.getChangeEvent());
                }else{
                    //changed the object at given index
                    final Object[] objs = cevent.getItems().toArray();
                    final int[] indices = new int[objs.length];
                    indices[0] = (int) cevent.getRange().getMinDouble();
                    for(int i=1;i<indices.length;i++){
                        indices[i] = indices[i-1]+1;
                    }
                    final TreeModelEvent te = new TreeModelEvent(this, path, indices, objs);
                    fireNodesRemoved(te);
                    
                    //the change event contain the old element
                    final Object parent = path.getLastPathComponent();
                    if(parent instanceof Style){
                        final List lst = ((Style)parent).featureTypeStyles();
                        for(int i=0;i<indices.length;i++)objs[i]=lst.get(indices[i]);
                    }else if(parent instanceof FeatureTypeStyle){
                        final List lst = ((FeatureTypeStyle)parent).rules();
                        for(int i=0;i<indices.length;i++)objs[i]=lst.get(indices[i]);
                    }else if(parent instanceof Rule){
                        final List lst = ((Rule)parent).symbolizers();
                        for(int i=0;i<indices.length;i++)objs[i]=lst.get(indices[i]);
                    }
                    fireNodesInserted(te);
                }
            }
        }else if(event instanceof PropertyChangeEvent){
            final TreePath parentPath = path.getParentPath();
            if(parentPath==null){
                //root changed
                fireNodesChanged(new TreeModelEvent(this, path));
            }else{
                //child node changed
                final Object[] objs = {path.getLastPathComponent()};
                final Object parent = parentPath.getLastPathComponent();
                final int[] indices = {getIndexOfChild(parent, objs[0])};
                fireNodesChanged(new TreeModelEvent(this, parentPath,indices,objs));
            }
        }
        
    }
    
}
