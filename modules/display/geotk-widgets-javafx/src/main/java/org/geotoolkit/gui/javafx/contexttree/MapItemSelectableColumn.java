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

import javafx.beans.property.SimpleObjectProperty;
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
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapItemSelectableColumn extends TreeTableColumn<Object,Boolean>{

    public MapItemSelectableColumn() {   
        setEditable(true);
        setPrefWidth(26);
        setMinWidth(26);
        setMaxWidth(26);
        
        setCellValueFactory(new Callback<CellDataFeatures<Object, Boolean>, ObservableValue<Boolean>>() {

            @Override
            public ObservableValue<Boolean> call(CellDataFeatures<Object, Boolean> param) {
                final Object candidate = param.getValue().getValue();
                if(candidate instanceof MapLayer){
                    return FXUtilities.beanProperty(((CellDataFeatures)param).getValue().getValue(), "selectable", Boolean.class);
                }else{
                    return new SimpleObjectProperty<>(null);
                }
            }
        });
        
        
        setCellFactory(new Callback() {
            @Override
            public Object call(Object param) {
                final ToggleButtonTreeTableCell tg = new ToggleButtonTreeTableCell(){

                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        final ToggleButton tb = getToggleButton();
                        
                        if(item instanceof Boolean){
                            tb.setVisible(true);
                            tb.setText((Boolean)item ? FontAwesomeIcons.ICON_UNLOCK : FontAwesomeIcons.ICON_LOCK);
                        }else{
                            tb.setVisible(false);
                        }
                        
                    }
                    
                };
                final ToggleButton tb = tg.getToggleButton();
                tb.setBorder(Border.EMPTY);
                tb.setFont(FXUtilities.FONTAWESOME);
                tb.setText(FontAwesomeIcons.ICON_LOCK);
                tb.setBackground(Background.EMPTY);
                tb.setPadding(Insets.EMPTY);
                tb.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        tb.setText(newValue ? FontAwesomeIcons.ICON_UNLOCK : FontAwesomeIcons.ICON_LOCK);
                    }
                });
                return tg;
            }
        });
    }
    
}
