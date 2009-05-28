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
package org.geotoolkit.gui.swing.contexttree.popup;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.contexttree.ContextTreeNode;
import org.geotoolkit.gui.swing.propertyedit.JPropertyDialog;
import org.geotoolkit.gui.swing.propertyedit.LayerFeaturePropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;


/**
 * Default popup control for property page of MapLayer, use for JContextTreePopup
 * 
 * @author Johann Sorel
 */
public class LayerFeatureItem extends JMenuItem implements TreePopupItem{
    
    private MapLayer layer;
    
    /** 
     * Creates a new instance of DefaultContextPropertyPop 
     */
    public LayerFeatureItem() {
        super( MessageBundle.getString("contexttreetable_feature_table")  );
        init();
    }
        
    private void init(){
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<PropertyPane> lst = new ArrayList<PropertyPane>();
                lst.add(new LayerFeaturePropertyPanel());
                JPropertyDialog.showDialog(lst, layer);
                
            }
        }
        );
    }
    
    @Override
    public boolean isValid(TreePath[] selection) {
        if (selection.length == 1) {
            ContextTreeNode node = (ContextTreeNode) selection[0].getLastPathComponent();            
            return ( node.getUserObject() instanceof MapLayer ) ;
        }
        return false;
        
    }

    @Override
    public Component getComponent(TreePath[] selection) {
        ContextTreeNode node = (ContextTreeNode) selection[0].getLastPathComponent();  
        layer = (MapLayer) node.getUserObject() ;
        return this;
    }
    
}
