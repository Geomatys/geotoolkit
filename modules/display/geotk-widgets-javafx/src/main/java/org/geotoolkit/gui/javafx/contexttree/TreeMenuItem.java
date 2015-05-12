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

import java.util.ArrayList;
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
    
    /**
     * Get all values of queried types from input items.
     * 
     * Note : Input items are not browsed recursively, we don't check their children.
     * @param <T> Type of object to return.
     * @param selection The list of tree items we want to extract value from.
     * @param type Class of output values.
     * @return All values of wanted types we have found in input selection parameter.
     */
    protected static <T> List<T>  getSelection(final List<? extends TreeItem> selection, final Class<T> type) {
        final ArrayList<T> result = new ArrayList<>();
        if (selection != null && !selection.isEmpty()) {
            Object tmpValue;
            for (final TreeItem item : selection) {
                if (item == null) continue;
                tmpValue = item.getValue();
                if (tmpValue != null && type.isAssignableFrom(tmpValue.getClass())) {
                    result.add((T)tmpValue);
                }
            }
        }
        return result;
    }
            
}
