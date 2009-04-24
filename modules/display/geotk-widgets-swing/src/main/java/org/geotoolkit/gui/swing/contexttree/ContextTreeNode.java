/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.contexttree;

import javax.swing.Icon;

import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 * a specific mutabletreenode for jcontexttree
 * 
 * @author Johann Sorel
 */
public abstract class ContextTreeNode extends AbstractMutableTreeTableNode{
    
    protected String tooltip = null;
    
    protected final LightContextTreeModel lightModel;
    
        
    public abstract Object getValue();
    public abstract void setValue(Object obj);
    
    public abstract Icon getIcon();
        
    public abstract boolean isEditable();
    
    /**
     * Creates a new instance of ContextTreeNode
     * @param model model of the tree
     */
    public ContextTreeNode(LightContextTreeModel model) {
        super();
        this.lightModel = model;
    }
            
    /**
     * find if a node is an ancetor of another
     * @param anotherNode the node to compare with
     * @return true is anotherNode is an ancestor of node
     */
    public final boolean isNodeAncestor(ContextTreeNode anotherNode) {
        
        if (anotherNode == null) {
            return false;
        }
        
        TreeTableNode ancestor = this;
        
        do {
            if (ancestor == anotherNode) {
                return true;
            }
        } while((ancestor = ancestor.getParent()) != null);
        
        return false;
        
    }
    
    /**
     * get a object at column
     * @param column number of the column
     * @return object at column
     */
    public final Object getValueAt(int column) {
        
        Object res;
        
        if(column == ContextTreeModel.TREE){
            res = getValue();
//            if(getUserObject() instanceof MapContext)
//                res = ((MapContext)getUserObject()).getTitle();
//            else if(getUserObject() instanceof MapLayer)
//                res = ((MapLayer)getUserObject()).getTitle();
//            else
//                res = "n/a";
        }else{
            if(column <= lightModel.completeModel.getColumnModelCount()){
                res = lightModel.completeModel.getColumnModel(column-1).getValue(getUserObject());
            } else{
                res = "n/a";
            }
        }
        
        return res;        
    }
        
    /**
     * set a new object at specific place
     * @param aValue the new value
     * @param column column number
     */
    @Override
    public final void setValueAt(Object aValue, int column){
        
        if(column == ContextTreeModel.TREE){
            setValue(aValue);
//            if(getUserObject() instanceof MapContext)
//                ((MapContext)getUserObject()).setTitle((String)aValue);
//            else if(getUserObject() instanceof MapLayer)
//                ((MapLayer)getUserObject()).setTitle((String)aValue);
//            
        }else{
            if(column <= lightModel.completeModel.getColumnModelCount())
                lightModel.completeModel.getColumnModel(column-1).setValue(getUserObject(),aValue);
            
        }
        
    }

    /**
     * get the number of columns
     * @return the number of columns
     */
    public final int getColumnCount() {
        return lightModel.completeModel.getColumnCount();
    }

    /**
     * 
     * @param arg0
     * @return
     */
    @Override
    public final boolean isEditable(int arg0) {
        if(arg0 == 0){
            return isEditable();
        }else{
            return super.isEditable(arg0);
        }
    }
    
    public String getToolTip(){
        return tooltip;
    }
    
    public void setToolTip(String tip){
        tooltip = tip;
    }
    
        
}
