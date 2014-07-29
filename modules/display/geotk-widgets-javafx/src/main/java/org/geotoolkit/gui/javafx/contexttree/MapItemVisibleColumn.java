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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.util.Callback;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.gui.javafx.util.ToggleButtonTreeTableCell;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapItemVisibleColumn extends TreeTableColumn{

    public MapItemVisibleColumn() {
        setCellValueFactory(param -> FXUtilities.beanProperty(((CellDataFeatures)param).getValue().getValue(), "visible", Boolean.class));     
        setEditable(true);
        setPrefWidth(26);
        setMinWidth(26);
        setMaxWidth(26);
                        
        setCellFactory(new Callback() {
            @Override
            public Object call(Object param) {
                final ToggleButtonTreeTableCell tg = new ToggleButtonTreeTableCell();
                final ToggleButton tb = tg.getToggleButton();
                tb.setBorder(Border.EMPTY);
                tb.setFont(FXUtilities.FONTAWESOME);
                tb.setText(FontAwesomeIcons.ICON_EYE);
                tb.setBackground(Background.EMPTY);
                tb.setPadding(Insets.EMPTY);
                tb.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        tb.setText(newValue ?FontAwesomeIcons.ICON_EYE : FontAwesomeIcons.ICON_EYE_SLASH);
                    }
                });
                return tg;
            }
        });
    }
    
}
