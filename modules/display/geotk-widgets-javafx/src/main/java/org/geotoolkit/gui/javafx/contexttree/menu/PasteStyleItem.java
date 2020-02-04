/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyle;

/**
 * Paste layer style item for ContextTree
 *
 * @author Johann Sorel (Geomatys)
 */
public class PasteStyleItem extends ActionMenuItem{

    private final CopyStyleItem copyItem;
    private List<WeakReference<TreeItem>> itemRefs;

    /**
     * Paste layer style item for ContextTree
     */
    public PasteStyleItem(CopyStyleItem item){
        super(GeotkFX.getString(PasteStyleItem.class,"paste"), null);
        copyItem = item;
    }

    @Override
    public MenuItem init(List<? extends TreeItem> selection) {
        if(selection.isEmpty()) return null;

        boolean valid = true;
        itemRefs = new ArrayList<>();
        for(TreeItem<? extends TreeItem> ti : selection){
            if(ti==null) continue;
            valid &= MapLayer.class.isInstance(ti.getValue());
            if(!valid) return null;
            itemRefs.add(new WeakReference<>(ti));
        }

        if(itemRefs.isEmpty()) return null;

        return menuItem;
    }

    @Override
    protected void handle(ActionEvent event) {
        if(itemRefs == null) return;
        for(WeakReference<TreeItem> itemRef : itemRefs){
            TreeItem path = itemRef.get();
            if(path == null) continue;
            if(path.getParent() == null) continue;
            final MapLayer candidate = (MapLayer) path.getValue();
            final MutableStyle style = copyItem.getStyle();
            if (style != null) candidate.setStyle(style);
        }
    }

}
