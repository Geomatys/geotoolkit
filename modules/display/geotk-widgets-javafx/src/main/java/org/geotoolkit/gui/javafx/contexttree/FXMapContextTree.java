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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.gui.javafx.util.ToggleButtonTreeTableCell;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapItem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMapContextTree extends BorderPane{

    private final TreeTableView treetable = new TreeTableView();
    private final ScrollPane scroll = new ScrollPane(treetable);
    private MapItem mapItem;
    
    
    public FXMapContextTree() {
        this(null);
    }
    
    public FXMapContextTree(MapItem item){    
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        setCenter(scroll);
        
        //configure treetable
        final TreeTableColumn treeCol = new TreeTableColumn(MessageBundle.getString("contexttreetable_layers"));
        treeCol.setCellValueFactory(param -> FXUtilities.beanProperty(((CellDataFeatures)param).getValue().getValue(), "name", String.class));     
        treeCol.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        treeCol.setEditable(true);
        treeCol.setPrefWidth(200);
        treeCol.setMinWidth(120);
        
                
        final TreeTableColumn visibleCol = new TreeTableColumn("");
        visibleCol.setCellValueFactory(param -> FXUtilities.beanProperty(((CellDataFeatures)param).getValue().getValue(), "visible", Boolean.class));     
        visibleCol.setEditable(true);
        visibleCol.setPrefWidth(26);
        visibleCol.setMinWidth(26);
        visibleCol.setMaxWidth(26);
        
       
        visibleCol.setCellFactory(new Callback() {

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
        
        
        treetable.getColumns().add(treeCol);
        treetable.getColumns().add(visibleCol);
        treetable.setEditable(true);
        
        
        
        setMapItem(item);
    }

    public MapItem getMapItem() {
        return mapItem;
    }

    public void setMapItem(MapItem mapItem) {
        if(this.mapItem == mapItem) return;
        this.mapItem = mapItem;
        
        if(mapItem==null){
            treetable.setRoot(null);
        }else{
            treetable.setRoot(new TreeMapItem(mapItem));
        }
        
        treetable.setShowRoot(true);
    }
    
    
    
}
