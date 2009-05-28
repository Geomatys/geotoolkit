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

import java.util.List;
import javax.swing.tree.DefaultTreeModel;

import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;

import org.geotoolkit.util.RandomStyleFactory;

import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class StyleTreeModel extends DefaultTreeModel {

    
    public StyleTreeModel(){
        super(null);
    }
    
    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final RandomStyleFactory RANDOM_FACTORY = new RandomStyleFactory();
    private MutableStyle style = null;

    /**
     * create a StyleTreeModel
     * @param style , can't be null
     */
    public StyleTreeModel(MutableStyle style) {
        super(new DefaultMutableTreeNode());

        if (style == null) {
            throw new NullPointerException("Style can't be null");
        }
        this.style = style;
        setRoot(parse(style));
    }

    /**
     * Set the model Style
     * @param style , can't be null
     */
    public void setStyle(MutableStyle style) {
        if (style == null) {
            throw new NullPointerException("Style can't be null");
        }
        this.style = style;

        setRoot(parse(style));
    }
    /**
     * 
     * @return Style
     */
    public MutableStyle getStyle() {
        return style;
    }

    //---------------------using nodes------------------------------------------
    
    
    public boolean isDeletable(DefaultMutableTreeNode node){
        
        boolean deletable = false;        
        Object removeObject = node.getUserObject();
        
        if(removeObject instanceof MutableStyle){
            
        }else if(removeObject instanceof MutableFeatureTypeStyle){
            if(!style.featureTypeStyles().isEmpty()){
                deletable = true;
            }
        }else if (removeObject instanceof MutableRule){
            DefaultMutableTreeNode ftsnode = (DefaultMutableTreeNode)node.getParent();
            MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) ftsnode.getUserObject();
            if(!fts.rules().isEmpty()){
                deletable = true;
            }
            
        }else if (removeObject instanceof Symbolizer){
            DefaultMutableTreeNode rulenode = (DefaultMutableTreeNode)node.getParent();
            MutableRule rule = (MutableRule) rulenode.getUserObject();
            if(!rule.symbolizers().isEmpty()){
                deletable = true;
            }
        }
        
        return deletable;        
    }
    
    /**
     * delete a node and his related style object
     * @param node
     * @return false if not removed
     */
    public boolean deleteNode(DefaultMutableTreeNode node) {
        
        boolean removed = false;        
        Object removeObject = node.getUserObject();
        
        if(removeObject instanceof MutableStyle){
            
        }else if(removeObject instanceof MutableFeatureTypeStyle){
            if(!style.featureTypeStyles().isEmpty()){
                remove((MutableFeatureTypeStyle)removeObject);
                removed = true;
            }
        }else if (removeObject instanceof MutableRule){
            DefaultMutableTreeNode ftsnode = (DefaultMutableTreeNode)node.getParent();
            MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) ftsnode.getUserObject();
            if(!fts.rules().isEmpty()){
                remove(ftsnode,(MutableRule)removeObject);
                removed = true;
            }
            
        }else if (removeObject instanceof Symbolizer){
            DefaultMutableTreeNode rulenode = (DefaultMutableTreeNode)node.getParent();
            MutableRule rule = (MutableRule) rulenode.getUserObject();
            if(!rule.symbolizers().isEmpty()){
                remove(rulenode,(Symbolizer)removeObject);
                removed = true;
            }
        }
        
        return removed;
    }
    /**
     * move an existing node
     * @param movedNode :node to move
     * @param targetNode 
     * @return DefaultMutableTreeNode or null if node could not be moved
     */
    public DefaultMutableTreeNode moveNode(DefaultMutableTreeNode movedNode, DefaultMutableTreeNode targetNode) {
        Object movedObj = movedNode.getUserObject();
        DefaultMutableTreeNode parentMovedNode = (DefaultMutableTreeNode) movedNode.getParent();
        Object parentMovedObj = parentMovedNode.getUserObject();
        
        Object targetObj = targetNode.getUserObject();
        

        DefaultMutableTreeNode copy = null;

        

        if (targetObj instanceof MutableFeatureTypeStyle && movedObj instanceof MutableFeatureTypeStyle) {
            copy = moveAt(movedNode,(MutableFeatureTypeStyle) movedObj, indexof(style, (MutableFeatureTypeStyle) targetObj));
            
        } else if (targetObj instanceof MutableFeatureTypeStyle && movedObj instanceof MutableRule) {
                        
            if (parentMovedNode == targetNode) {                
            } else if(parentMovedNode.getChildCount() == 1){
                MutableRule rule = RANDOM_FACTORY.duplicate((MutableRule) movedObj);
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
                MutableRule rule = RANDOM_FACTORY.duplicate((MutableRule) movedObj);                                
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
    public DefaultMutableTreeNode duplicateNode(DefaultMutableTreeNode node) {
        Object obj = node.getUserObject();
        DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode) node.getParent();
        Object parentobj = parentnode.getUserObject();

        DefaultMutableTreeNode copy = null;

        if (obj instanceof MutableFeatureTypeStyle) {
            MutableFeatureTypeStyle fts = RANDOM_FACTORY.duplicate((MutableFeatureTypeStyle) obj);
            int index = indexof(style, (MutableFeatureTypeStyle) obj) + 1;
            copy = insertAt(fts, index);
        } else if (obj instanceof MutableRule) {
            MutableRule rule = RANDOM_FACTORY.duplicate((MutableRule) obj);
            int index = indexof((MutableFeatureTypeStyle) parentobj, (MutableRule) obj) + 1;
            copy = insertAt(parentnode, rule, index);
        } else if (obj instanceof Symbolizer) {
            Symbolizer symbol = (Symbolizer) obj;
            int index = indexof((MutableRule) parentobj, (Symbolizer) obj) + 1;
            copy = insertAt(parentnode, symbol, index);
        }

        return copy;
    }
    
    /**
     * add a new FeatureTypeStyle
     * @return created node
     */
    public DefaultMutableTreeNode newFeatureTypeStyle() {
        MutableFeatureTypeStyle fts = SF.featureTypeStyle(RANDOM_FACTORY.createPointSymbolizer());
        return insert(fts);
    }
    /**
     * add a new rule
     * @param ftsnode 
     * @return created node
     */
    public DefaultMutableTreeNode newRule(DefaultMutableTreeNode ftsnode) {
        MutableRule rule = SF.rule(RANDOM_FACTORY.createPointSymbolizer());
        return insert(ftsnode, rule);
    }
    /**
     * add a new symbolizer
     * @param rulenode 
     * @return created node
     */
    public DefaultMutableTreeNode newPointSymbolizer(DefaultMutableTreeNode rulenode) {
        Symbolizer symbol = RANDOM_FACTORY.createPointSymbolizer();
        return insert(rulenode, symbol);
    }
    /**
     * add a new symbolizer
     * @param rulenode 
     * @return created node
     */
    public DefaultMutableTreeNode newLineSymbolizer(DefaultMutableTreeNode rulenode) {
        Symbolizer symbol = RANDOM_FACTORY.createLineSymbolizer();
        return insert(rulenode, symbol);
    }
    /**
     * add a new symbolizer
     * @param rulenode 
     * @return created node
     */
    public DefaultMutableTreeNode newPolygonSymbolizer(DefaultMutableTreeNode rulenode) {
        Symbolizer symbol = RANDOM_FACTORY.createPolygonSymbolizer();
        return insert(rulenode, symbol);
    }
    /**
     * add a new symbolizer
     * @param rulenode 
     * @return created node
     */
    public DefaultMutableTreeNode newRasterSymbolizer(DefaultMutableTreeNode rulenode) {
        Symbolizer symbol = RANDOM_FACTORY.createRasterSymbolizer();
        return insert(rulenode, symbol);
    }
    
    /**
     * add a new symbolizer
     * @param rulenode 
     * @return created node
     */
    public DefaultMutableTreeNode newTextSymbolizer(DefaultMutableTreeNode rulenode) {
        Symbolizer symbol = SF.textSymbolizer();
        return insert(rulenode, symbol);
    }

    
    //-------------------utilities----------------------------------------------
    private int indexof(MutableStyle style, MutableFeatureTypeStyle fts) {
        return style.featureTypeStyles().indexOf(fts);
    }

    private int indexof(MutableFeatureTypeStyle fts, MutableRule rule) {
        return fts.rules().indexOf(rule);
    }

    private int indexof(MutableRule rule, Symbolizer symbol) {
        return rule.symbolizers().indexOf(symbol);
    }

    private DefaultMutableTreeNode insert(MutableFeatureTypeStyle fts) {
        style.featureTypeStyles().add(fts);
        DefaultMutableTreeNode node = parse(fts);
        insertNodeInto(node, getRoot(), indexof(style, fts));
        return node;
    }

    private DefaultMutableTreeNode insert(DefaultMutableTreeNode parentNode, MutableRule rule) {
        DefaultMutableTreeNode rulenode = parse(rule);

        MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) parentNode.getUserObject();
        fts.rules().add(rule);

        insertNodeInto(rulenode, parentNode, indexof(fts, rule));

        return rulenode;
    }

    private DefaultMutableTreeNode insert(DefaultMutableTreeNode parentNode, Symbolizer symbol) {
        DefaultMutableTreeNode symbolNode = new DefaultMutableTreeNode(symbol);

        MutableRule rule = (MutableRule) parentNode.getUserObject();
        rule.symbolizers().add(symbol);

        insertNodeInto(symbolNode, parentNode, indexof(rule, symbol));

        return symbolNode;
    }

    private DefaultMutableTreeNode insertAt(MutableFeatureTypeStyle fts, int index) {
        style.featureTypeStyles().add(index, fts);
        DefaultMutableTreeNode node = parse(fts);
        insertNodeInto(node, getRoot(), index);
        return node;
    }

    private DefaultMutableTreeNode insertAt(DefaultMutableTreeNode parentNode, MutableRule rule, int index) {
        MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) parentNode.getUserObject();
        fts.rules().add(index, rule);
        DefaultMutableTreeNode node = parse(rule);
        insertNodeInto(node, parentNode, index);
        return node;
    }

    private DefaultMutableTreeNode insertAt(DefaultMutableTreeNode parentNode, Symbolizer symbol, int index) {
        MutableRule rule = (MutableRule) parentNode.getUserObject();
        rule.symbolizers().add(index, symbol);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(symbol);
        insertNodeInto(node, parentNode, index);
        return node;
    }

    private DefaultMutableTreeNode moveAt(DefaultMutableTreeNode ftsnode, MutableFeatureTypeStyle fts, int target) {

        int origine = indexof(style, fts);

        if (origine != target) {
            List<MutableFeatureTypeStyle> ntypes = style.featureTypeStyles();

            ntypes.remove(fts);
            removeNodeFromParent(ftsnode);
                       
            ntypes.add(target, fts);
            insertNodeInto(ftsnode, getRoot(), target);
        }

        return ftsnode;
        
    }
    
    private DefaultMutableTreeNode moveAt(DefaultMutableTreeNode rulenode, MutableRule rule, int target) {

        DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode)rulenode.getParent();
        MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) parentnode.getUserObject();
        
        int origine = indexof(fts,rule);

        if (origine != target) {
            List<MutableRule> nrules = fts.rules();

            nrules.remove(rule);
            removeNodeFromParent(rulenode);
            
            nrules.add(target, rule);
            insertNodeInto(rulenode, parentnode, target);
        }

        return rulenode;
        
    }
    
    private DefaultMutableTreeNode moveAt(DefaultMutableTreeNode symbolnode, Symbolizer symbol, int target) {

        DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode)symbolnode.getParent();
        MutableRule rule = (MutableRule) ((DefaultMutableTreeNode)symbolnode.getParent()).getUserObject();
        
        int origine = indexof(rule,symbol);

        if (origine != target) {
            List<Symbolizer> nsymbols = rule.symbolizers();

            nsymbols.remove(symbol);
            removeNodeFromParent(symbolnode);
                
            nsymbols.add(target, symbol);
            insertNodeInto(symbolnode, parentnode, target);
        }

        return symbolnode;        
    }
    
    private void remove(final MutableFeatureTypeStyle fts){
        DefaultMutableTreeNode ftsNode = (DefaultMutableTreeNode) getRoot().getChildAt(indexof(style, fts));
        style.featureTypeStyles().remove(fts);
        removeNodeFromParent(ftsNode);        
    }
    
    private void remove(DefaultMutableTreeNode parentNode, MutableRule rule){
        MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) parentNode.getUserObject();
        DefaultMutableTreeNode ruleNode = (DefaultMutableTreeNode) parentNode.getChildAt(indexof(fts, rule));
        fts.rules().remove(rule);
        removeNodeFromParent(ruleNode);        
    }
    
    private void remove(DefaultMutableTreeNode parentNode, Symbolizer symbol){
        MutableRule rule = (MutableRule) parentNode.getUserObject();
        DefaultMutableTreeNode symbolNode = (DefaultMutableTreeNode) parentNode.getChildAt(indexof(rule, symbol));
        rule.symbolizers().remove(symbol);
        removeNodeFromParent(symbolNode);        
    }
    
    private DefaultMutableTreeNode parse(MutableStyle style) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(style);

        for (MutableFeatureTypeStyle fts : style.featureTypeStyles()) {
            node.add(parse(fts));
        }
        return node;
    }

    private DefaultMutableTreeNode parse(MutableFeatureTypeStyle fts) {
        DefaultMutableTreeNode ftsnode = new DefaultMutableTreeNode(fts);

        for (MutableRule rule : fts.rules()) {
            ftsnode.add(parse(rule));
        }
        return ftsnode;
    }

    private DefaultMutableTreeNode parse(MutableRule rule) {
        DefaultMutableTreeNode rulenode = new DefaultMutableTreeNode(rule);

        for (Symbolizer symb : rule.symbolizers()) {
            DefaultMutableTreeNode symbnode = new DefaultMutableTreeNode(symb);
            rulenode.add(symbnode);
        }
        return rulenode;
    }

    //--------------------override----------------------------------------------
    @Override
    public DefaultMutableTreeNode getRoot() {
        return (DefaultMutableTreeNode) super.getRoot();
    }
    
}
