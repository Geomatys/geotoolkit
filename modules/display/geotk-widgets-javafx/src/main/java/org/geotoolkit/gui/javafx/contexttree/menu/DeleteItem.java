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
package org.geotoolkit.gui.javafx.contexttree.menu;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapItem;

/**
 * Delete item for ContextTree
 *
 * @author Johann Sorel (Geomatys)
 */
public class DeleteItem extends ActionMenuItem{

    private List<WeakReference<TreeItem>> itemRefs;

    /**
     * delete item for contexttree
     */
    public DeleteItem(){
        super(GeotkFX.getString(DeleteItem.class,"delete"), GeotkFX.ICON_DELETE);
    }

    @Override
    public MenuItem init(List<? extends TreeItem> selection) {
        if(selection.isEmpty()) return null;

        boolean valid = true;
        itemRefs = new ArrayList<>();
        for(TreeItem<? extends TreeItem> ti : selection){
            if(ti==null) continue;
            valid &= MapItem.class.isInstance(ti.getValue());
            if(!valid) return null;
            itemRefs.add(new WeakReference<>(ti));
        }

        if(itemRefs.isEmpty()) return null;

        return menuItem;
    }

    @Override
    protected void handle(ActionEvent event) {
        if(itemRefs == null) return;
        new Thread(){
            @Override
            public void run() {
                for(WeakReference<TreeItem> itemRef : itemRefs){
                    TreeItem path = itemRef.get();
                    if(path == null) continue;
                    if(path.getParent() == null) continue;
                    final MapItem parent = (MapItem) path.getParent().getValue();
                    final MapItem candidate = (MapItem) path.getValue();
                    parent.items().remove(candidate);
                }
            }
        }.start();
    }

}
