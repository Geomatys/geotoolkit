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

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Modality;
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
    
    public FXEditAction(FXMap map) {
        super(map,GeotkFX.getString(FXEditAction.class,"edit"),GeotkFX.getString(FXEditAction.class,"edit"),ICON);
    }

    @Override
    public void accept(ActionEvent event) {
        if (map != null) {
            final GraphicContainer container = map.getCanvas().getContainer();
            if (container instanceof ContextContainer2D) {
                final Dialog dialog = new Dialog();
                dialog.initModality(Modality.NONE);
                final DialogPane pane = new DialogPane();
                pane.setMaxHeight(Double.MAX_VALUE);
                pane.setMaxWidth(Double.MAX_VALUE);
                dialog.setDialogPane(pane);

                final FXToolBox toolbox = new FXToolBox(map);
                toolbox.getTools().add(new CreatePointTool());
                toolbox.getTools().add(new CreatePointTool());
                toolbox.getTools().add(new CreatePointTool());
                toolbox.getTools().add(new CreatePointTool());
                toolbox.getTools().add(new CreatePointTool());
                toolbox.getTools().add(new CreatePointTool());
                toolbox.getTools().add(new CreatePointTool());
                toolbox.getTools().add(new CreatePointTool());
                toolbox.getTools().add(new CreatePointTool());
                toolbox.getTools().add(new CreatePointTool());
                toolbox.getTools().add(new CreatePointTool());
                toolbox.getTools().add(new CreatePointTool());
                pane.setContent(toolbox);
                pane.setMaxHeight(Double.MAX_VALUE);
                pane.setMaxWidth(Double.MAX_VALUE);

                dialog.setResizable(true);
                dialog.show();
            }
        }
    }
    
}
