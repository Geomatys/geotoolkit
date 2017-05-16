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

import java.util.ArrayList;
import java.util.List;
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
 * @author Alexis Manin (Geomatys)
 */
public class OpacityItem extends TreeMenuItem {

    private final Slider slider = new Slider(0.0, 1.0, 1);
    private List<MapLayer> selectedLayers = new ArrayList<>();

    public OpacityItem() {
        slider.setOrientation(Orientation.HORIZONTAL);
        slider.setTooltip(new Tooltip(GeotkFX.getString(this,"opacity")));
        slider.setMin(0.0);
        slider.setMax(1.0);
        slider.setMajorTickUnit(0.5);
        slider.setMinorTickCount(3);
        slider.showTickLabelsProperty().set(true);
        slider.showTickMarksProperty().set(true);

        menuItem = new CustomMenuItem(slider, false);

        slider.valueProperty().addListener(this::updateOpacity);
    }

    @Override
    public MenuItem init(List<? extends TreeItem> selection) {
        selectedLayers = getSelection(selection, MapLayer.class);
        // We do not allow opacity update if there is something which is not a layer in the selection.
        if (selectedLayers.isEmpty() || selectedLayers.size() < selection.size()) {
            return null;
        } else {
            slider.setValue(selectedLayers.get(0).getOpacity());
            return menuItem;
        }
    }

    /**
     * Update opacity of currently selected layers using {@link #slider}.
     * @param sliderProperty
     * @param oldOpacity The previous slider opacity.
     * @param newOpacity The new opacity value
     */
    private void updateOpacity(ObservableValue<? extends Number> sliderProperty, Number oldOpacity, Number newOpacity) {
        final double opacity = newOpacity.doubleValue();
        for (final MapLayer layer : selectedLayers) {
            layer.setOpacity(opacity);
        }
    }
}
