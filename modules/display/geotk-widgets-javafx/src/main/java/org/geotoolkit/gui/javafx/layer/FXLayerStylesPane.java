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

package org.geotoolkit.gui.javafx.layer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLayerStylesPane extends FXPropertyPane{
    
    private final BorderPane mainPane = new BorderPane();
    private final BorderPane leftPane = new BorderPane();
    private final ListView listView = new ListView();
    private final Button apply = new Button(GeotkFX.getString(this,"apply"));
    private final Button revert = new Button(GeotkFX.getString(this,"revert"));
    
    private final FXLayerStylePane[] editors;
    
    private FXLayerStylePane currentEditor = null;
    
    private MapLayer candidate;
    
    public FXLayerStylesPane(FXLayerStylePane ... styleEditors) {
        this.editors = styleEditors;
        
        mainPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setCenter(mainPane);  
        
        apply.setGraphic(new ImageView(SwingFXUtils.toFXImage(
                IconBuilder.createImage(FontAwesomeIcons.ICON_CHECK, 
                        16, FontAwesomeIcons.DEFAULT_COLOR), null)));
        revert.setGraphic(new ImageView(SwingFXUtils.toFXImage(
                IconBuilder.createImage(FontAwesomeIcons.ICON_ROTATE_LEFT_ALIAS,
                        16, FontAwesomeIcons.DEFAULT_COLOR), null)));
        
        final HBox hbox = new HBox(10, apply, revert);
        hbox.setPadding(new Insets(10, 10, 10, 10));
        hbox.setAlignment(Pos.CENTER);
        
        final ScrollPane scroll = new ScrollPane(listView);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        scroll.setMinSize(300, 250);
        leftPane.setCenter(scroll);
        leftPane.setBottom(hbox);
        mainPane.setLeft(leftPane);      
        
        //build index
        final LinkedHashMap<String, List<FXLayerStylePane>> indexByCategory = new LinkedHashMap<>();
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
            public void onChanged(ListChangeListener.Change c) {
                final Object item = listView.getSelectionModel().getSelectedItem();
                if(item instanceof FXLayerStylePane){
                    mainPane.setCenter(null);
                    currentEditor = (FXLayerStylePane) item;
                    mainPane.setCenter(currentEditor);
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
        
        apply.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(currentEditor==null || candidate==null) return;
                candidate.setStyle(currentEditor.getMutableStyle());
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
        for(FXLayerStylePane editor : editors){
            editor.init(candidate);
        }
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
            if(!empty && item!=null){
                if(item instanceof String){
                    setText((String)item);
                    setFont(Font.font(getFont().getFamily(), FontWeight.BOLD, getFont().getSize()));
                    setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
                    setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
                } else if(item instanceof FXLayerStylePane){
                    setText(((FXLayerStylePane)item).getTitle());
                }
            }
        }

    }
    
}
