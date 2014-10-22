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
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.controlsfx.control.action.Action;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;
import org.geotoolkit.gui.javafx.layer.FXFeatureTable;
import org.geotoolkit.gui.javafx.layer.FXLayerStylesPane;
import org.geotoolkit.gui.javafx.layer.FXPropertiesPane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleAdvancedPane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleClassifRangePane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleClassifSinglePane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleColorMapPane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleSimplePane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleXMLPane;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.util.FXDialog;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;

/**
 * MapLayer properties panel
 *
 * @author Johann Sorel (Geomatys)
 */
public class LayerPropertiesItem extends TreeMenuItem{
    
    private final FXMap map;
    private WeakReference<TreeItem> itemRef;
    
    /**
     * delete item for contexttree
     */
    public LayerPropertiesItem(FXMap map){
        this.map = map;
        
        item = new MenuItem(GeotkFX.getString(this,"properties"));
        //item.setGraphic(new ImageView(ICON));
        item.setOnAction(new EventHandler<javafx.event.ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent event) {
                if(itemRef == null) return;
                TreeItem path = itemRef.get();
                if(path == null) return;
                final MapLayer candidate = (MapLayer) path.getValue();
                                
                final FXPropertiesPane panel = new FXPropertiesPane(
                        candidate,
//                        new FXLayerStructure(),
                        new FXFeatureTable(),
                        new FXLayerStylesPane(
                                new FXStyleSimplePane(),
                                new FXStyleColorMapPane(),
                                new FXStyleClassifSinglePane(),
                                new FXStyleClassifRangePane(),
                                new FXStyleAdvancedPane(),
                                new FXStyleXMLPane()
                        )
                );
                
                final FXDialog dialog = new FXDialog();
                dialog.setTitle(GeotkFX.getString(LayerPropertiesItem.this,"properties"));
                dialog.setContent(panel);
                dialog.getActions().add(new CloseAction(dialog));
                dialog.setModal(false);
                dialog.setVisible(map,true);
                
            }
        });
    }

    @Override
    public MenuItem init(List<? extends TreeItem> selection) {
        final boolean valid = uniqueAndType(selection,MapLayer.class);
        if(valid){
            itemRef = new WeakReference<>(selection.get(0));
            return item;
        }
        return null;
    }

    public static final class CloseAction extends Action {
        
        public CloseAction(final FXDialog dialog) {
            super(GeotkFX.getString(LayerPropertiesItem.class, "close"), (ActionEvent t) -> {
                dialog.setVisible(null,false);
            });
        }
        
    }
    
    
}
