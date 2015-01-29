/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2015, Geomatys
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

package org.geotoolkit.gui.javafx.layer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLayerStylesPane extends FXPropertyPane{
    
    private final BorderPane leftPane = new BorderPane();
    private final ListView listView = new ListView();

    private final LinkedHashMap<String, List<FXLayerStylePane>> indexByCategory = new LinkedHashMap<>();    
    private FXLayerStylePane currentEditor = null;    
    private MapLayer candidate;
    
    public FXLayerStylesPane(FXLayerStylePane ... styleEditors) {
        getStylesheets().add(GeotkFX.CSS_PATH);
        
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        
        final ScrollPane scroll = new ScrollPane(listView);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        scroll.setMinSize(300, 250);
        leftPane.setCenter(scroll);
        setLeft(leftPane);      
        
        //build index
        for(final FXLayerStylePane styleEditor : styleEditors){
            List<FXLayerStylePane> editorOfCategory = indexByCategory.get(styleEditor.getCategory());
            if(editorOfCategory==null){
                editorOfCategory = new ArrayList<>();
                indexByCategory.put(styleEditor.getCategory(), editorOfCategory);
            }
            editorOfCategory.add(styleEditor);
        }
        
        final ObservableList itemCollection = FXCollections.observableArrayList();
        for(final Entry<String, List<FXLayerStylePane>> entry : indexByCategory.entrySet()){
            itemCollection.add(entry.getKey());
            itemCollection.addAll(entry.getValue());
        }
        listView.setItems(itemCollection);
        
                
        //listen to list selection
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                final Object item = listView.getSelectionModel().getSelectedItem();
                if(item instanceof FXLayerStylePane){
                    setCenter(null);
                    currentEditor = (FXLayerStylePane) item;
                    setCenter(currentEditor);
                }
            }
        });
        
        //select first editor by default
        if(styleEditors.length>0){
            listView.getSelectionModel().selectFirst();
        }
        
        listView.setCellFactory(new Callback() {

            @Override
            public Object call(Object param) {
                return new EditorCell();
            }
        });
                
    }

    @Override
    public String getTitle() {
        return GeotkFX.getString(this,"style");
    }

    @Override
    public boolean init(Object candidate) {
        if(!(candidate instanceof MapLayer)) return false;
        this.candidate = (MapLayer) candidate;

        //update list values
        final ObservableList itemCollection = FXCollections.observableArrayList();
        for(final Entry<String, List<FXLayerStylePane>> entry : indexByCategory.entrySet()){
            boolean hasValues = false;
            final List<FXLayerStylePane> valids = new ArrayList<>();
            for(FXLayerStylePane editor : entry.getValue()){
                if(editor.init(candidate)){
                    hasValues = true;
                    valids.add(editor);
                }
            }

            if(hasValues){
                itemCollection.add(entry.getKey());
                itemCollection.addAll(valids);
            }
        }
        listView.setItems(itemCollection);

        return true;
    }
    
    /**
     * Controls the style of cells.
     */
    static private class EditorCell extends ListCell {
        
        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            setText(null);
            setGraphic(null);
            getStyleClass().remove("property-group-title");
            if(!empty && item!=null){
                if(item instanceof String){
                    getStyleClass().add("property-group-title");
                    setText((String)item);
                } else if(item instanceof FXLayerStylePane){
                    setText(((FXLayerStylePane)item).getTitle());
                }
            }
        }

    }
    
}
