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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.ext.DefaultBackgroundTemplate;
import org.geotoolkit.display2d.ext.legend.DefaultLegendService;
import org.geotoolkit.display2d.ext.legend.DefaultLegendTemplate;
import org.geotoolkit.map.LayerListener;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.collection.CollectionChangeEvent;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class StyleMapItem extends TreeItem implements LayerListener {

    //generate a template for the legend
    private static final DefaultLegendTemplate legendTemplate = new DefaultLegendTemplate(
            new DefaultBackgroundTemplate( //legend background
                    new BasicStroke(0), //stroke
                    Color.BLACK, //stroke paint
                    new Color(0, 0, 0, 0), // fill paint
                    new Insets(2, 2, 2, 2), //border margins
                    0 //round border
            ),
            1, //gap between legend elements
            null, //glyph size, we can let it to null for the legend to use the best size
            new Font("Serial", Font.PLAIN, 10), //Font used for style rules
            false, // show layer names
            new Font("Serial", Font.BOLD, 12), //Font used for layer names
            false // display only visible layers
    );

    private final BorderPane pane = new BorderPane();
    private final ImageView view = new ImageView();
    private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();

    public StyleMapItem(MapLayer item) {
        super(item.getStyle());
        pane.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        pane.setPrefSize(BorderPane.USE_COMPUTED_SIZE, BorderPane.USE_COMPUTED_SIZE);
        pane.setCenter(view);
        /** listen to style change */
        item.addLayerListener(new LayerListener.Weak(item, this));
        styleChange(item, null);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public void itemChange(CollectionChangeEvent<MapItem> event) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void styleChange(MapLayer source, EventObject event) {
        try {
            imageProperty.set(SwingFXUtils.toFXImage(DefaultLegendService.portray(legendTemplate, source, null),null));
        } catch (PortrayalException ex) {
            Logger.getLogger(StyleMapItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ObjectProperty<Image> imageProperty() {
        return imageProperty;
    }

}
