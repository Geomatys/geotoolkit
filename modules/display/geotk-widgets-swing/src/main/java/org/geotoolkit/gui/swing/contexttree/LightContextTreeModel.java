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
package org.geotoolkit.gui.swing.contexttree;

/**
 * Simplified context tree model
 * 
 * @author Johann Sorel
 */
public class LightContextTreeModel {

    public final ContextTreeModel completeModel;
    
    LightContextTreeModel(ContextTreeModel model){
        this.completeModel = model;
        
    }
    
    public void removeNodeFromParent(ContextTreeNode node){
        completeModel.removeNodeFromParent(node);
    }
       
    public void insetNodeInto(ContextTreeNode child, ContextTreeNode father, int index){
        completeModel.insertNodeInto(child, father, index);
    }
    

    
        
    
    
    
    
}
