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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;
import org.controlsfx.control.action.Action;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;
import org.geotoolkit.gui.javafx.layer.FXFeatureTable;
import org.geotoolkit.gui.javafx.layer.FXLayerStructure;
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
        
        menuItem = new MenuItem(GeotkFX.getString(this,"properties"));
        menuItem.setOnAction(new EventHandler<javafx.event.ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent event) {
                if(itemRef == null) return;
                
                final TreeItem path = itemRef.get();
                if(path == null) return;
                
                final MapLayer candidate = (MapLayer) path.getValue();
                                
                final FXPropertiesPane panel = new FXPropertiesPane(
                        candidate,
                        new FXLayerStructure(),
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

                final Dialog dialog = new Dialog();
                dialog.setTitle(GeotkFX.getString(LayerPropertiesItem.this, "properties"));
                final DialogPane pane = new DialogPane();
                pane.setContent(panel);
                pane.getButtonTypes().add(ButtonType.CLOSE);

                dialog.initModality(Modality.NONE);
                dialog.setResizable(true);
                dialog.setDialogPane(pane);
                dialog.resultProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        //TODO add apply revert buttons
                        dialog.close();
                    }
                });

                Platform.runLater(dialog::show);
            }
        });
    }

    @Override
    public MenuItem init(List<? extends TreeItem> selection) {
        final boolean valid = uniqueAndType(selection,MapLayer.class);
        if(valid){
            itemRef = new WeakReference<>(selection.get(0));
            return menuItem;
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
