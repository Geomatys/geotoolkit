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
package org.geotoolkit.gui.swing.contexttree.popup;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.contexttree.ContextTreeNode;
import org.geotoolkit.gui.swing.contexttree.JContextTree;
import org.geotoolkit.gui.swing.resource.MessageBundle;

import org.geotoolkit.map.MapContext;


/**
 * Default popup control for activation of MapContext, use for JContextTreePopup
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * 
 */
public class ContextActiveItem extends JCheckBoxMenuItem implements TreePopupItem{
    
    
    
    private MapContext context;
    private JContextTree xtree ;
    
    
    /** 
     * Creates a new instance of ContextActiveControl 
     * @param tree 
     */
    public ContextActiveItem(JContextTree tree) {
        this.setText( MessageBundle.getString("contexttreetable_activated")  );
        xtree = tree;
        init();
    }
    
    private void init(){
        
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isSelected()){
                    if(xtree != null && context != null)
                        xtree.setActiveContext(context);
                } else if(xtree != null){
                    xtree.setActiveContext(null);
                }
            }
        });
    }
    
    @Override
    public boolean isValid(TreePath[] selection) {
        if (selection.length == 1) {
            ContextTreeNode node = (ContextTreeNode) selection[0].getLastPathComponent();            
            return ( node.getUserObject() instanceof MapContext ) ;
        }
        return false;
    }

    @Override
    public Component getComponent(TreePath[] selection) {
        ContextTreeNode node = (ContextTreeNode) selection[0].getLastPathComponent();  
        context = (MapContext) node.getUserObject() ;
        this.setSelected( context.equals(xtree.getActiveContext()));
        
        return this;
    }
    
    
}
