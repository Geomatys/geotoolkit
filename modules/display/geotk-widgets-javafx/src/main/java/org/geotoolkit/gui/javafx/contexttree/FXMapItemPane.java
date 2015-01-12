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

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.map.ItemListener;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.collection.CollectionChangeEvent;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMapItemPane extends BorderPane{
    
    public static final Image ICON_VISIBLE = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_EYE, 16, Color.DARK_GRAY),null);
    public static final Image ICON_UNVISIBLE = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_EYE_SLASH, 16, Color.LIGHT_GRAY),null);
    public static final Image ICON_LEGEND_UP = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_ARROW_CIRCLE_UP, 16, Color.DARK_GRAY),null);
    public static final Image ICON_LEGEND_DOWN = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_ARROW_CIRCLE_DOWN, 16, Color.DARK_GRAY),null);
    public static final Image ICON_FILTER = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_FILTER, 16, Color.DARK_GRAY),null);
    
    
    private final MapItem item;
    private final FlowPane flow = new FlowPane();
    private final TextField uiName = new TextField();
    private final Button viewButton = new Button();
    private final Button filterButton = new Button("",new ImageView(ICON_FILTER));
    private final Button legendButton = new Button("",new ImageView(ICON_LEGEND_UP));
    private final ImageView legend = new ImageView();
    
    public FXMapItemPane(MapItem item) {
        this.item = item;
        
        //viewButton.getStyleClass().remove("button");
        viewButton.setStyle("-fx-background-color: transparent;");
        filterButton.setStyle("-fx-background-color: transparent;");
        legendButton.setStyle("-fx-background-color: transparent;");
        
        flow.getChildren().add(uiName);
        flow.getChildren().add(viewButton);
        viewButton.setGraphic(new ImageView(item.isVisible() ? ICON_VISIBLE : ICON_UNVISIBLE));
        
        
        uiName.setText(item.getName());        
        
        
        //build sub elements
        if(!(item instanceof MapLayer)){
            final VBox vbox = new VBox();
            for(MapItem mi : item.items()){
                vbox.getChildren().add(new FXMapItemPane(mi));
            }
            setCenter(vbox);
        }else{
            flow.getChildren().add(filterButton);
            flow.getChildren().add(legendButton);
            setCenter(legend);
        }
        
        setTop(flow);
        
        item.addItemListener(new ItemListener() {
            @Override
            public void itemChange(CollectionChangeEvent<MapItem> event) {}
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                final String property = evt.getPropertyName();
                if(MapItem.VISIBILITY_PROPERTY.equals(property)){
                    viewButton.setGraphic(new ImageView(item.isVisible() ? ICON_VISIBLE : ICON_UNVISIBLE));
                }
            }
        });
        
        viewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                item.setVisible(!item.isVisible());
            }
        });
        
    }

    public MapItem getMapItem() {
        return item;
    }
    
    
    
}
