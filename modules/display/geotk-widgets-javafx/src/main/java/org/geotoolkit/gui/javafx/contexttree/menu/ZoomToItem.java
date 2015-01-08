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

import java.awt.geom.NoninvertibleTransformException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.logging.Level;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.opengis.referencing.operation.TransformException;

/**
 * Zoom on item for ContextTree
 *
 * @author Johann Sorel (Geomatys)
 */
public class ZoomToItem extends TreeMenuItem{

    private static final Image ICON = SwingFXUtils.toFXImage(
            IconBuilder.createImage(FontAwesomeIcons.ICON_ARROWS_ALT, 16, FontAwesomeIcons.DEFAULT_COLOR), null);
    
    private final FXMap map;
    private WeakReference<TreeItem> itemRef;

    public ZoomToItem(FXMap map){
        this.map = map;
        menuItem = new MenuItem(GeotkFX.getString(this,"zoomto"));
        menuItem.setGraphic(new ImageView(ICON));

        menuItem.setOnAction(new EventHandler<javafx.event.ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent event) {
                if(itemRef == null) return;
                TreeItem path = itemRef.get();
                if(path == null) return;
                final MapItem parent = (MapItem) path.getParent().getValue();
                final MapItem candidate = (MapItem) path.getValue();
                
                if(map == null) return;
                try {
                    map.getCanvas().setVisibleArea( ((MapLayer)candidate).getBounds());
                } catch (NoninvertibleTransformException | TransformException ex) {
                    Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(),ex);
                }
                
            }
        });
    }

    @Override
    public MenuItem init(List<? extends TreeItem> selection) {
        boolean valid = uniqueAndType(selection,MapLayer.class);
        if(valid && selection.get(0).getParent()!=null){
            itemRef = new WeakReference<>(selection.get(0));
            return menuItem;
        }
        return null;
    }

}
