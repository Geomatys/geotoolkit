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
    
    protected MenuItem item;

    public TreeMenuItem() {
    }

    public TreeMenuItem(MenuItem item) {
        this.item = item;
    }
    
    /**
     * 
     * @param selectedItems currently seelcted items.
     * @return Menu Item if valid for current selection. null if not valid
     */
    public MenuItem init(List<? extends TreeItem> selectedItems){
        return item;
    }
    
    protected static boolean uniqueAndType(final List<? extends TreeItem> selection, final Class C) {
        if (selection != null && selection.size() == 1){
            final TreeItem ca = selection.get(0);
            return C.isInstance(ca.getValue());
        }
        return false;
    }
            
}
