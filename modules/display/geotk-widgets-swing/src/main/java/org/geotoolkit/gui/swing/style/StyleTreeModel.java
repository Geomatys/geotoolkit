/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Level;
import javax.swing.tree.DefaultTreeModel;

import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.sld.MutableLayerStyle;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.style.FeatureTypeStyleListener;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.RuleListener;
import org.geotoolkit.style.StyleListener;
import org.geotoolkit.style.StyleUtilities;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.type.Name;
import org.opengis.style.SemanticType;

import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class StyleTreeModel<T> extends DefaultTreeModel implements StyleListener, FeatureTypeStyleListener, RuleListener {

    private final StyleListener.Weak weakStyleListener          = new StyleListener.Weak(null, this);
    private final FeatureTypeStyleListener.Weak weakFTSListener = new FeatureTypeStyleListener.Weak(null, this);
    private final RuleListener.Weak weakRuleListener            = new RuleListener.Weak(null, this);
    private T styleElement = null;

    public StyleTreeModel(){
        super(null);
    }
    
    /**
     * create a StyleTreeModel
     * @param style , can't be null
     */
    public StyleTreeModel(final T style) {
        super(new DefaultMutableTreeNode());
        if (style == null) {
            throw new NullPointerException("Style can't be null");
        }
        setStyleElement(style);
    }

    /**
     * Set the model Style
     * @param style , can't be null
     */
    public void setStyleElement(final T style) {
        if (style == null) {
            throw new NullPointerException("Style can't be null");
        }

        weakStyleListener.dispose();
        weakFTSListener.dispose();
        weakRuleListener.dispose();

        this.styleElement = style;

        if(this.styleElement != null){
            if(style instanceof MutableLayerStyle){
                weakStyleListener.registerSource((MutableLayerStyle)style);
            }else if(style instanceof MutableFeatureTypeStyle){
                weakFTSListener.registerSource((MutableFeatureTypeStyle)style);
            }else if(style instanceof MutableRule){
                weakRuleListener.registerSource((MutableRule)style);
            }
        }

        setRoot(parse(style));
    }
    
    /**
     * 
     * @return Style
     */
    public T getStyleElement() {
        return styleElement;
    }


    //--------------- style events ---------------------------------------------

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
    }

    @Override
    public void featureTypeNameChange(final CollectionChangeEvent<Name> event) {
    }

    @Override
    public void semanticTypeChange(final CollectionChangeEvent<SemanticType> event) {
    }

    @Override
    public void featureTypeStyleChange(final CollectionChangeEvent<MutableFeatureTypeStyle> event) {
        styleElementChange(event);
    }
    
    @Override
    public void ruleChange(final CollectionChangeEvent<MutableRule> event) {
        styleElementChange(event);
    }

    @Override
    public void symbolizerChange(final CollectionChangeEvent<Symbolizer> event) {
        styleElementChange(event);
    }

    private void styleElementChange(final CollectionChangeEvent event) {
        final int type = event.getType();
        final DefaultMutableTreeNode parent = search(event.getSource());

        if(parent == null){
            //some elements like Semantics in Feature type style might not
            //be visible as tree nodes. we ignore them.
            return;
        }

        final NumberRange range = event.getRange();
        if(range == null){
            //event is vague, reload the entire node
            final Object stylElement = parent.getUserObject();
            final DefaultMutableTreeNode node = parse(stylElement);

            final DefaultMutableTreeNode sp = (DefaultMutableTreeNode) parent.getParent();
            if(sp != null){
                final int i = sp.getIndex(parent);
                removeNodeFromParent(parent);
                insertNodeInto(node, sp, i);
            }else{
                //it's the root node
                setRoot(node);
            }
            return;
        }

        if(type == CollectionChangeEvent.ITEM_ADDED){
            int index = (int) range.getMinimum();
            for(Object added : event.getItems()){
                final DefaultMutableTreeNode child = parse(added);

                if(child != null){
                    insertNodeInto(child, parent, index);
                    index++;
                }
            }
        }else if(type == CollectionChangeEvent.ITEM_REMOVED){
            for(Object ele : event.getItems()){
                final DefaultMutableTreeNode child = search(parent,ele);
                if(child != null){
                    removeNodeFromParent(child);
                }
            }
        }else if(type == CollectionChangeEvent.ITEM_CHANGED){
            for(Object ele : event.getItems()){
                final DefaultMutableTreeNode child = search(parent,ele);
                final EventObject subEvent = event.getChangeEvent();
                if(subEvent instanceof CollectionChangeEvent){
                    styleElementChange((CollectionChangeEvent) subEvent);
                }else{
                    nodeChanged(child);
                }
            }
        }
    }


    //---------------------using nodes------------------------------------------
    

    public boolean isDeletable(final DefaultMutableTreeNode node){
        final Object removeObject = node.getUserObject();

        return     removeObject instanceof MutableFeatureTypeStyle
                || removeObject instanceof MutableRule
                || removeObject instanceof Symbolizer;
    }

    /**
     * delete a node and his related style object
     * @param node
     * @return false if not removed
     */
    public boolean deleteNode(final DefaultMutableTreeNode node) {
        final Object removeObject = node.getUserObject();

        final boolean removed;
        if(removeObject instanceof MutableStyle){
            removed = false;
        }else if(removeObject instanceof MutableFeatureTypeStyle){
            final DefaultMutableTreeNode styleNode = (DefaultMutableTreeNode)node.getParent();
            final MutableStyle style = (MutableStyle) styleNode.getUserObject();
            removed = style.featureTypeStyles().remove((MutableFeatureTypeStyle)removeObject);
        }else if (removeObject instanceof MutableRule){
            final DefaultMutableTreeNode ftsnode = (DefaultMutableTreeNode)node.getParent();
            final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) ftsnode.getUserObject();
            removed = fts.rules().remove((MutableRule)removeObject);
        }else if (removeObject instanceof Symbolizer){
            final DefaultMutableTreeNode rulenode = (DefaultMutableTreeNode)node.getParent();
            final MutableRule rule = (MutableRule) rulenode.getUserObject();
            removed = rule.symbolizers().remove((Symbolizer)removeObject);
        }else{
            removed = false;
        }

        return removed;
    }
    /**
     * move an existing node
     * @param movedNode :node to move
     * @param targetNode
     * @return DefaultMutableTreeNode or null if node could not be moved
     */
    public DefaultMutableTreeNode moveNode(final DefaultMutableTreeNode movedNode, final DefaultMutableTreeNode targetNode) {
        Object movedObj = movedNode.getUserObject();
        DefaultMutableTreeNode parentMovedNode = (DefaultMutableTreeNode) movedNode.getParent();
        Object parentMovedObj = parentMovedNode.getUserObject();

        Object targetObj = targetNode.getUserObject();


        DefaultMutableTreeNode copy = null;



        if (targetObj instanceof MutableFeatureTypeStyle && movedObj instanceof MutableFeatureTypeStyle) {
            final DefaultMutableTreeNode targetParentNode = (DefaultMutableTreeNode)targetNode.getParent();
            MutableFeatureTypeStyle targetFTS = (MutableFeatureTypeStyle) targetObj;
            int targetIndex = indexof((MutableStyle)targetParentNode.getUserObject(),targetFTS);

            if (parentMovedNode == targetParentNode) {
                copy = moveAt(movedNode, (MutableFeatureTypeStyle)movedObj, targetIndex);
            }

        } else if (targetObj instanceof MutableFeatureTypeStyle && movedObj instanceof MutableRule) {

            if (parentMovedNode == targetNode) {
            } else if(parentMovedNode.getChildCount() == 1){
                MutableRule rule = (MutableRule) movedObj;
                copy = insert(targetNode, rule);
            } else{
                remove(parentMovedNode, (MutableRule) movedObj);
                copy = insert(targetNode, (MutableRule) movedObj);
            }


        } else if (targetObj instanceof MutableRule && movedObj instanceof MutableRule) {

            DefaultMutableTreeNode targetParentNode = (DefaultMutableTreeNode)targetNode.getParent();
            MutableRule targetRule = (MutableRule) targetObj;
            int targetIndex = indexof((MutableFeatureTypeStyle)targetParentNode.getUserObject(),targetRule);

            if (parentMovedNode == targetParentNode) {
                copy = moveAt(movedNode, (MutableRule)movedObj, targetIndex);
            } else if(parentMovedNode.getChildCount() == 1){
                MutableRule rule = (MutableRule) movedObj;
                MutableFeatureTypeStyle parentfts = (MutableFeatureTypeStyle) targetParentNode.getUserObject();
                copy = insertAt(targetParentNode, rule, targetIndex );
            } else{
                remove(parentMovedNode, (MutableRule) movedObj);
                copy = insertAt(targetParentNode, (MutableRule) movedObj,targetIndex);
            }

        } else if (targetObj instanceof MutableRule && movedObj instanceof Symbolizer) {

            if (parentMovedNode == targetNode) {
            } else if(parentMovedNode.getChildCount() == 1){
                Symbolizer symbol = (Symbolizer) movedObj;
                copy = insert(targetNode, symbol);
            } else{
                remove(parentMovedNode, (Symbolizer) movedObj);
                copy = insert(targetNode, (Symbolizer) movedObj);
            }

        } else if (targetObj instanceof Symbolizer && movedObj instanceof Symbolizer) {

            DefaultMutableTreeNode targetParentNode = (DefaultMutableTreeNode)targetNode.getParent();
            Symbolizer targetSymbol = (Symbolizer) targetObj;
            int targetIndex = indexof((MutableRule)targetParentNode.getUserObject(),targetSymbol);

            if (parentMovedNode == targetParentNode) {
                copy = moveAt(movedNode, (Symbolizer)movedObj, targetIndex);
            } else if(parentMovedNode.getChildCount() == 1){
                Symbolizer symbol = (Symbolizer) movedObj;
                MutableRule parentRule = (MutableRule) targetParentNode.getUserObject();
                copy = insertAt(targetParentNode, symbol, targetIndex );
            } else{
                remove(parentMovedNode, (Symbolizer) movedObj);
                copy = insertAt(targetParentNode, (Symbolizer) movedObj,targetIndex);
            }

        }

        return copy;
    }
    /**
     * duplicate a node
     * @param node
     * @return DefaultMutableTreeNode or null if node could not be duplicate
     */
    public void duplicateNode(final DefaultMutableTreeNode node) {
        final Object obj = node.getUserObject();
        final DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode) node.getParent();
        final Object parentobj = parentnode.getUserObject();

        if (obj instanceof MutableFeatureTypeStyle) {
            final MutableFeatureTypeStyle fts = StyleUtilities.copy((MutableFeatureTypeStyle) obj);
            final int index = indexof((MutableStyle) parentobj, (MutableFeatureTypeStyle) obj) + 1;
            ((MutableStyle) parentobj).featureTypeStyles().add(index, fts);
        } else if (obj instanceof MutableRule) {
            final MutableRule rule = StyleUtilities.copy((MutableRule) obj);
            final int index = indexof((MutableFeatureTypeStyle) parentobj, (MutableRule) obj) + 1;
            ((MutableFeatureTypeStyle) parentobj).rules().add(index, rule);
        } else if (obj instanceof Symbolizer) {
            //no need to copy symbolizer, they are immutable
            final Symbolizer symbol = (Symbolizer) obj;
            final int index = indexof((MutableRule) parentobj, (Symbolizer) obj) + 1;
            ((MutableRule) parentobj).symbolizers().add(index, symbol);
        }

    }

    //-------------------utilities----------------------------------------------
    private DefaultMutableTreeNode search(final Object userObject){
        return search(getRoot(), userObject);
    }

    private DefaultMutableTreeNode search(final DefaultMutableTreeNode node, final Object userObject){
        if(node.getUserObject() == userObject){
            //we are searching for the exact same object. more than just equal
            return node;
        }
        for(int i=0,n=node.getChildCount();i<n;i++){
            final DefaultMutableTreeNode candidate = search((DefaultMutableTreeNode) node.getChildAt(i), userObject);
            if(candidate != null){
                return candidate;
            }
        }
        return null;
    }


    private int indexof(final MutableStyle style, final MutableFeatureTypeStyle fts) {
        return style.featureTypeStyles().indexOf(fts);
    }

    private int indexof(final MutableFeatureTypeStyle fts, final MutableRule rule) {
        return fts.rules().indexOf(rule);
    }

    private int indexof(final MutableRule rule, final Symbolizer symbol) {
        return rule.symbolizers().indexOf(symbol);
    }

    private DefaultMutableTreeNode insert(final DefaultMutableTreeNode parentNode, final MutableRule rule) {
        final DefaultMutableTreeNode rulenode = parse(rule);
        final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) parentNode.getUserObject();
        fts.rules().add(rule);
        insertNodeInto(rulenode, parentNode, indexof(fts, rule));
        return rulenode;
    }

    private DefaultMutableTreeNode insert(final DefaultMutableTreeNode parentNode, final Symbolizer symbol) {
        final DefaultMutableTreeNode symbolNode = new DefaultMutableTreeNode(symbol);
        final MutableRule rule = (MutableRule) parentNode.getUserObject();
        rule.symbolizers().add(symbol);
        insertNodeInto(symbolNode, parentNode, indexof(rule, symbol));
        return symbolNode;
    }

    private DefaultMutableTreeNode insertAt(final DefaultMutableTreeNode parentNode, final MutableRule rule, final int index) {
        final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) parentNode.getUserObject();
        fts.rules().add(index, rule);
        final DefaultMutableTreeNode node = parse(rule);
        insertNodeInto(node, parentNode, index);
        return node;
    }

    private DefaultMutableTreeNode insertAt(final DefaultMutableTreeNode parentNode, final Symbolizer symbol, final int index) {
        final MutableRule rule = (MutableRule) parentNode.getUserObject();
        rule.symbolizers().add(index, symbol);
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(symbol);
        insertNodeInto(node, parentNode, index);
        return node;
    }

    private DefaultMutableTreeNode moveAt(final DefaultMutableTreeNode ftsnode, final MutableFeatureTypeStyle fts, final int target) {
        final DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode)ftsnode.getParent();
        final MutableStyle style = (MutableStyle) parentnode.getUserObject();
        final int origine = indexof(style,fts);

        if (origine != target) {
            final List<MutableFeatureTypeStyle> ntypes = style.featureTypeStyles();

            ntypes.remove(fts);
            removeNodeFromParent(ftsnode);
                       
            ntypes.add(target, fts);
            insertNodeInto(ftsnode, getRoot(), target);
        }

        return ftsnode;
    }
    
    private DefaultMutableTreeNode moveAt(final DefaultMutableTreeNode rulenode, final MutableRule rule, final int target) {
        final DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode)rulenode.getParent();
        final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) parentnode.getUserObject();
        final int origine = indexof(fts,rule);

        if (origine != target) {
            final List<MutableRule> nrules = fts.rules();

            nrules.remove(rule);
            removeNodeFromParent(rulenode);
            
            nrules.add(target, rule);
            insertNodeInto(rulenode, parentnode, target);
        }

        return rulenode;
    }
    
    private DefaultMutableTreeNode moveAt(final DefaultMutableTreeNode symbolnode, final Symbolizer symbol, final int target) {
        final DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode)symbolnode.getParent();
        final MutableRule rule = (MutableRule) ((DefaultMutableTreeNode)symbolnode.getParent()).getUserObject();
        final int origine = indexof(rule,symbol);

        if (origine != target) {
            final List<Symbolizer> nsymbols = rule.symbolizers();

            nsymbols.remove(symbol);
            removeNodeFromParent(symbolnode);
                
            nsymbols.add(target, symbol);
            insertNodeInto(symbolnode, parentnode, target);
        }

        return symbolnode;        
    }
        
    private void remove(final DefaultMutableTreeNode parentNode, final MutableRule rule){
        final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) parentNode.getUserObject();
        final DefaultMutableTreeNode ruleNode = (DefaultMutableTreeNode) parentNode.getChildAt(indexof(fts, rule));
        fts.rules().remove(rule);
        removeNodeFromParent(ruleNode);        
    }
    
    private void remove(final DefaultMutableTreeNode parentNode, final Symbolizer symbol){
        final MutableRule rule = (MutableRule) parentNode.getUserObject();
        final DefaultMutableTreeNode symbolNode = (DefaultMutableTreeNode) parentNode.getChildAt(indexof(rule, symbol));
        rule.symbolizers().remove(symbol);
        removeNodeFromParent(symbolNode);        
    }
    
    //--------------------override----------------------------------------------
    @Override
    public DefaultMutableTreeNode getRoot() {
        return (DefaultMutableTreeNode) super.getRoot();
    }


    ////////////////////////////////////////////////////////////////////////////
    // STYLE PARSING ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private static DefaultMutableTreeNode parse(final Object obj){
        if(obj instanceof MutableStyle){
            return parse((MutableStyle)obj);
        }else if(obj instanceof MutableFeatureTypeStyle){
            return parse((MutableFeatureTypeStyle)obj);
        }else if(obj instanceof MutableRule){
            return parse((MutableRule)obj);
        }else if(obj instanceof Symbolizer){
            return parse((Symbolizer)obj);
        }else{
            Logging.getLogger(StyleTreeModel.class).log(Level.WARNING, "Unexpected type : " + obj);
            return null;
        }
    }

    private static DefaultMutableTreeNode parse(final MutableStyle style) {
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(style);
        for (MutableFeatureTypeStyle fts : style.featureTypeStyles()) {
            node.add(parse(fts));
        }
        return node;
    }

    private static DefaultMutableTreeNode parse(final MutableFeatureTypeStyle fts) {
        final DefaultMutableTreeNode ftsnode = new DefaultMutableTreeNode(fts);
        for (MutableRule rule : fts.rules()) {
            ftsnode.add(parse(rule));
        }
        return ftsnode;
    }

    private static DefaultMutableTreeNode parse(final MutableRule rule) {
        final DefaultMutableTreeNode rulenode = new DefaultMutableTreeNode(rule);
        for (Symbolizer symb : rule.symbolizers()) {
            rulenode.add(parse(symb));
        }
        return rulenode;
    }

    private static DefaultMutableTreeNode parse(final Symbolizer symbolizer) {
        return new DefaultMutableTreeNode(symbolizer);
    }

}
