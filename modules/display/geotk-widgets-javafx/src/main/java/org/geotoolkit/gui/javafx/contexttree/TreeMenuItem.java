/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.gui.javafx.contexttree;

import java.util.List;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TreeMenuItem {
    
    protected MenuItem menuItem;

    public TreeMenuItem() {
    }

    public TreeMenuItem(MenuItem item) {
        this.menuItem = item;
    }
    
    /**
     * 
     * @param selectedItems currently selected items.
     * @return MenuItem if valid for current selection. null if not valid
     */
    public MenuItem init(List<? extends TreeItem> selectedItems){
        return menuItem;
    }
    
    /**
     * 
     * @param selection
     * @param type
     * @return true if selection is unique and the selected item is instance of type. 
     */
    protected static boolean uniqueAndType(final List<? extends TreeItem> selection, final Class type) {
        if (selection != null && selection.size() == 1){
            final TreeItem selectedItem = selection.get(0);
            if(selectedItem==null) return false;
            return type.isInstance(selectedItem.getValue());
        }
        return false;
    }
            
}
