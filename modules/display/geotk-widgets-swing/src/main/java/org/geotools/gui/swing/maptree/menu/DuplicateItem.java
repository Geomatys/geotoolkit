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
package org.geotools.gui.swing.maptree.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.geotools.gui.swing.contexttree.JContextTree;
import org.geotools.gui.swing.resource.IconBundle;
import org.geotools.gui.swing.maptree.TreePopupItem;
import org.geotools.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;

/**
 * Duplicate item for treetable.
 * 
 * @author Johann Sorel
 */
public class DuplicateItem implements TreePopupItem{

    private JMenuItem duplicateitem = null;
    private final JContextTree tree;
    
    /**
     * create new instance
     * @param tree
     */
    public DuplicateItem(final JContextTree tree){
        this.tree = tree;
        
        duplicateitem = new JMenuItem( MessageBundle.getString("contexttreetable_duplicate") );
        duplicateitem.setIcon( IconBundle.getInstance().getIcon("16_duplicate") );
        duplicateitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        
        duplicateitem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tree.duplicateSelection();
            }
        });
    }
    
    @Override
    public boolean isValid(TreePath[] selection) {
        if (selection.length == 1 && selection[0].getLastPathComponent() instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selection[0].getLastPathComponent();
            return ( node.getUserObject() instanceof MapLayer ) ;
        }
        return false;
    }

    @Override
    public Component getComponent(TreePath[] selection) {
        duplicateitem.setEnabled(tree.canDuplicateSelection());
        return duplicateitem;
    }

}
