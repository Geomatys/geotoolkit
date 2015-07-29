/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

import java.util.List;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.controlsfx.control.action.ActionUtils;
import org.geotoolkit.gui.javafx.action.RollbackAction;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;
import org.geotoolkit.map.FeatureMapLayer;

/**
 * Map rollback action to context tree menu item.
 *
 * @author Johann Sorel (Geomatys)
 */
public class RollbackItem extends TreeMenuItem {

    public RollbackItem() {}

    @Override
    public MenuItem init(List<? extends TreeItem> selectedItems) {
        if(uniqueAndType(selectedItems, FeatureMapLayer.class)){
            final FeatureMapLayer layer = (FeatureMapLayer) selectedItems.get(0).getValue();
            return ActionUtils.createMenuItem(new RollbackAction(layer));
        }
        return null;
    }


}
