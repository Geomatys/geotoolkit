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
 * copy item for treetable
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class CopyItem implements TreePopupItem{

    private JContextTree tree = null;
    private JMenuItem copyitem = null;
    
    /**
     * copy item for jcontexttreepopup
     * @param tree
     */
    public CopyItem(final JContextTree tree){
        this.tree = tree;
        
        copyitem = new JMenuItem(MessageBundle.getString("contexttreetable_copy"));
        copyitem.setIcon( IconBundle.getInstance().getIcon("16_copy") );
        copyitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        
        copyitem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tree.copySelectionInBuffer();
            }
        });
        
    }
    
    @Override
    public boolean isValid(TreePath[] selection) {
        return tree.selectionContainOnlyContexts() || tree.selectionContainOnlyLayers();
    }

    @Override
    public Component getComponent(TreePath[] selection) {
        copyitem.setEnabled( tree.canCopySelection() );
        return copyitem;
    }

}
