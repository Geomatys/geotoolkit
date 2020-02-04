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
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleUtilities;

/**
 * Copy layer style item for ContextTree
 *
 * @author Johann Sorel (Geomatys)
 */
public class CopyStyleItem extends ActionMenuItem{

    private MutableStyle copiedStyle;
    private WeakReference<TreeItem> itemRef;

    /**
     * Copy layer style item for ContextTree
     */
    public CopyStyleItem(){
        super(GeotkFX.getString(CopyStyleItem.class,"copy"), null);
    }

    public MutableStyle getStyle() {
        if (copiedStyle == null) {
            return null;
        } else {
            return StyleUtilities.copy(copiedStyle);
        }
    }

    @Override
    public MenuItem init(List<? extends TreeItem> selection) {
        if(selection.isEmpty()) return null;

        if (uniqueAndType(selection, MapLayer.class)) {
            itemRef = new WeakReference<>(selection.get(0));
            return menuItem;
        }

        return null;
    }

    @Override
    protected void handle(ActionEvent event) {
        if (itemRef == null) return;
        final TreeItem treeItem = itemRef.get();
        if (treeItem == null) return;
        final MapLayer candidate = (MapLayer) treeItem.getValue();
        copiedStyle = candidate.getStyle();
    }

}
