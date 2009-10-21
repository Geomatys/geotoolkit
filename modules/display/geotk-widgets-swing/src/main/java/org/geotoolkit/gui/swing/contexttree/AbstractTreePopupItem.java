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

import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author eclesia
 * @module pending
 */
public abstract class AbstractTreePopupItem extends JMenuItem implements TreePopupItem{

    protected JContextTree tree = null;

    public AbstractTreePopupItem() {
    }

    public AbstractTreePopupItem(String str) {
        super(str);
    }

    @Override
    public void setTree(JContextTree tree) {
        this.tree = tree;
    }

    @Override
    public JContextTree getTree() {
        return tree;
    }

    protected static boolean uniqueAndType(TreePath[] selection,Class C) {
        if (selection != null && selection.length == 1){
            TreePath path = selection[0];
            if( path != null && path.getLastPathComponent() != null && path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                return  C.isInstance(node.getUserObject()) ;
            }
        }
        return false;

    }

}
