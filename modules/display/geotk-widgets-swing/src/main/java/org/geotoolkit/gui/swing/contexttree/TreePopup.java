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

import org.geotoolkit.gui.swing.contexttree.menu.SeparatorItem;
import org.geotoolkit.gui.swing.contexttree.menu.TitledSeparatorItem;

/**
 * TreePopup item.
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
final class TreePopup extends JPopupMenu {

    private final JContextTree tree;

    TreePopup(JContextTree tree) {
        this.tree = tree;
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

            if (tree != null) {

                Point location = tree.getMousePosition();
                if (location != null) {
                    TreePath path = tree.getRealTree().getPathForLocation(location.x, location.y);

                    selection = new TreePath[]{path};
                }

                
            }
            
            if(selection == null){
                selection = new TreePath[0];
            }

            for (TreePopupItem control : tree.controls()) {
                control.setTree(tree);
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

    /**
     * {@inheritDoc }
     */
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
