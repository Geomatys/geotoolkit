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
import org.geotoolkit.internal.GeotkFXBundle;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLayerStylesPane extends FXPropertyPane{
    
    private final BorderPane parts = new BorderPane();
    private final BorderPane left = new BorderPane();
    private final ListView views = new ListView();
    private final Button apply = new Button(GeotkFXBundle.getString(this,"apply"));
    private final Button revert = new Button(GeotkFXBundle.getString(this,"revert"));
    
    private final FXLayerStylePane[] editors;
    
    private FXLayerStylePane currentEditor = null;
    
    private MapLayer candidate;
    
    public FXLayerStylesPane(FXLayerStylePane ... styleEditors) {
        this.editors = styleEditors;
        
        apply.setGraphic(new ImageView(SwingFXUtils.toFXImage(
                IconBuilder.createImage(FontAwesomeIcons.ICON_CHECK,16,FontAwesomeIcons.DEFAULT_COLOR),null)));
        revert.setGraphic(new ImageView(SwingFXUtils.toFXImage(
                IconBuilder.createImage(FontAwesomeIcons.ICON_ROTATE_LEFT_ALIAS,16,FontAwesomeIcons.DEFAULT_COLOR),null)));
        
        final HBox vbox = new HBox(10, apply, revert);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setAlignment(Pos.CENTER);
        
        final ScrollPane scroll = new ScrollPane(views);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        scroll.setMinSize(300, 250);
        left.setCenter(scroll);
        left.setBottom(vbox);
        
        parts.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setCenter(parts);        
        parts.setLeft(left);
        
        //build index
        final LinkedHashMap<String,List<FXLayerStylePane>> index = new LinkedHashMap<>();
        for(FXLayerStylePane st : styleEditors){
            List<FXLayerStylePane> lst = index.get(st.getCategory());
            if(lst==null){
                lst = new ArrayList<>();
                index.put(st.getCategory(), lst);
            }
            lst.add(st);
        }
        final ObservableList lst = FXCollections.observableArrayList();
        for(Entry<String,List<FXLayerStylePane>> entry : index.entrySet()){
            lst.add(entry.getKey());
            lst.addAll(entry.getValue());
        }
        views.setItems(lst);
        
                
        //listen to list selection
        views.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        views.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                final Object item = views.getSelectionModel().getSelectedItem();
                if(item instanceof FXLayerStylePane){
                    parts.setCenter(null);
                    currentEditor = (FXLayerStylePane) item;
                    parts.setCenter(currentEditor);
                }
            }
        });
        
        //select first editor by default
        if(styleEditors.length>0){
            views.getSelectionModel().selectFirst();
        }
        
        views.setCellFactory(new Callback() {

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
        return GeotkFXBundle.getString(this,"style");
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
    
    static class EditorCell extends ListCell{
        
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
                }else if(item instanceof FXLayerStylePane){
                    setText(((FXLayerStylePane)item).getTitle());
                }
            }
        }

    }
    
}
