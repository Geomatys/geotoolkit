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
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.contexttree.JContextTree;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 * delete item for treetable
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class DeleteItem implements TreePopupItem{

    private JMenuItem deleteitem = null;
    private JContextTree tree = null;
    
    /**
     * delete item for jcontexttreepopup
     * @param tree
     */
    public DeleteItem(final JContextTree tree){
        this.tree = tree;
        
        deleteitem = new JMenuItem( MessageBundle.getString("contexttreetable_delete") );
        deleteitem.setIcon( IconBundle.getInstance().getIcon("16_delete") );
        deleteitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
        
        deleteitem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tree.deleteSelection();
            }
        });
    }
    
    @Override
    public boolean isValid(TreePath[] selection) {
        return tree.selectionContainOnlyContexts() || tree.selectionContainOnlyLayers() ;
    }

    @Override
    public Component getComponent(TreePath[] selection) {
        deleteitem.setEnabled(tree.canDeleteSelection());        
        return deleteitem;
    }

}
