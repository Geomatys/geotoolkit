/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.render2d.edition;

import java.util.Iterator;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXMapAction;
import org.geotoolkit.internal.GeotkFX;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FXEditAction extends FXMapAction {
    public static final Image ICON = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_PENCIL, 16, FontAwesomeIcons.DEFAULT_COLOR), null);

    private Stage dialog;

    public FXEditAction(FXMap map) {
        super(map,GeotkFX.getString(FXEditAction.class,"edit"),GeotkFX.getString(FXEditAction.class,"edit"),ICON);
    }

    @Override
    public void accept(ActionEvent event) {
        if (map != null) {
            if(dialog!=null){
                //close the dialog
                dialog.close();
                dialog = null;
                return;
            }

            final GraphicContainer container = map.getCanvas().getContainer();
            if (container instanceof ContextContainer2D) {
                dialog = new Stage();
                dialog.setAlwaysOnTop(true);
                dialog.initModality(Modality.NONE);
                dialog.initStyle(StageStyle.UTILITY);
                dialog.setTitle(GeotkFX.getString(FXEditAction.class,"edit"));

                final FXToolBox toolbox = new FXToolBox(map);
                toolbox.setMaxHeight(Double.MAX_VALUE);
                toolbox.setMaxWidth(Double.MAX_VALUE);
                final Iterator<EditionTool.Spi> ite = EditionHelper.getToolSpis();
                while(ite.hasNext()){
                    toolbox.getTools().add(ite.next());
                }

                final BorderPane pane = new BorderPane(toolbox);
                pane.setPadding(new Insets(10, 10, 10, 10));

                final Scene scene = new Scene(pane);

                dialog.setOnCloseRequest((WindowEvent evt) -> dialog = null);
                dialog.setScene(scene);
                dialog.setResizable(true);
                dialog.setWidth(300);
                dialog.setHeight(450);
                dialog.show();
            }
        }
    }
    
}
