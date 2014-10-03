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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;

/**
 * Change layer opacity for ContextTree
 *
 * @author Johann Sorel (Geomatys)
 */
public class OpacityItem extends TreeMenuItem{
    
    private WeakReference<TreeItem> itemRef;
    private final Slider slider = new Slider(0.0, 1.0, 1);

    public OpacityItem(){
        
        slider.setOrientation(Orientation.HORIZONTAL);
        slider.setTooltip(new Tooltip(GeotkFX.getString(this,"opacity")));
        slider.setMin(0.0);
        slider.setMax(1.0);
        slider.setMajorTickUnit(0.5);
        slider.setMinorTickCount(3);
        slider.showTickLabelsProperty().set(true);
        slider.showTickMarksProperty().set(true);
        
        item = new CustomMenuItem(slider, false);
        
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(itemRef!=null){
                    final TreeItem ti = itemRef.get();
                    if(ti!=null){
                        final MapLayer layer = (MapLayer) ti.getValue();
                        layer.setOpacity(slider.getValue());
                    }
                }
            }
        });
        
    }

    @Override
    public MenuItem init(List<? extends TreeItem> selection) {
        boolean valid = uniqueAndType(selection,MapLayer.class);
        if(valid && selection.get(0).getParent()!=null){
            final MapLayer layer = (MapLayer) (selection.get(0)).getValue();
            slider.setValue(layer.getOpacity());
            itemRef = new WeakReference<>(selection.get(0));
            return item;
        }
        return null;
    }

}
