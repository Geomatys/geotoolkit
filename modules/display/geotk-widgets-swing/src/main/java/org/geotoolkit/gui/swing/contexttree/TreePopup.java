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

import java.awt.Component;
import java.awt.Point;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.contexttree.popup.SeparatorItem;
import org.geotoolkit.gui.swing.contexttree.popup.TitledSeparatorItem;
import org.geotoolkit.gui.swing.contexttree.popup.TreePopupItem;

/**
 * TreePopup item.
 *
 * @author Johann Sorel
 */
final class TreePopup extends JPopupMenu {

    private final JContextTreePopup manager;
    private final TreeTable treetable;

    TreePopup(TreeTable treetable, JContextTreePopup manager) {
        this.manager = manager;
        this.treetable = treetable;
    }

    /**
     * will not be set visible if nothing is in the popup
     * 
     * @param view 
     */
    @Override
    public void setVisible(boolean view) {

        if (view) {
            removeAll();

            TreePath[] selection = {};

            if (treetable != null) {

                Point location = treetable.getMousePosition();
                if (location != null) {
                    TreePath path = treetable.getPathForLocation(location.x, location.y);

                    if (path == null) {
                        treetable.getTreeSelectionModel().clearSelection();
                    } else {
                        treetable.getTreeSelectionModel().addSelectionPath(path);
                    }
                } else {
                    treetable.getTreeSelectionModel().clearSelection();
                }

                selection = treetable.getTreeSelectionModel().getSelectionPaths();
                
            }
            
            if(selection == null){
                selection = new TreePath[0];
            }

            for (TreePopupItem control : manager.controls) {
                if (control.isValid(selection)) {
                    add(control.getComponent(selection));
                }
            }
            removeLastSeparators();

            if (getComponentCount() > 0) {
                super.setVisible(view);
            }
        } else {
            super.setVisible(view);
        }


    }

    private void removeLastSeparators() {
        if (getComponentCount() > 0) {
            while (getComponent(getComponentCount() - 1) instanceof TitledSeparatorItem) {
                remove(getComponentCount() - 1);
            }
        }
    }

    @Override
    public Component add(Component menuItem) {

        if (getComponentCount() > 0) {
            if( getComponent(getComponentCount() - 1) instanceof SeparatorItem && menuItem instanceof SeparatorItem){
                return menuItem;
            } else if (!(getComponent(getComponentCount() - 1) instanceof TitledSeparatorItem && menuItem instanceof TitledSeparatorItem)) {
                return super.add(menuItem);
            } else {
                remove(getComponentCount() - 1);
                return super.add(menuItem);
            }
        }

        return super.add(menuItem);


    }
}
