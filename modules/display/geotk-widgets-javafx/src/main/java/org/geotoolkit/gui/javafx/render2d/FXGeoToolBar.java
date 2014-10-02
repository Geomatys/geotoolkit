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

package org.geotoolkit.gui.javafx.render2d;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import org.controlsfx.control.action.ActionUtils;
import org.geotoolkit.gui.javafx.render2d.tool.FXMesureAreaAction;
import org.geotoolkit.gui.javafx.render2d.tool.FXMesureLengthAction;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXGeoToolBar extends ToolBar {
    
    private static final String LEFT   = "buttongroup-left";
    private static final String CENTER = "buttongroup-center";
    private static final String RIGHT  = "buttongroup-right";
    
    public FXGeoToolBar(FXMap map) {
        getStylesheets().add("/fr/sym/buttonbar.css");
        
        final Button butLength = new FXMesureLengthAction(map).createButton(ActionUtils.ActionTextBehavior.HIDE);
        final Button butArea = new FXMesureAreaAction(map).createButton(ActionUtils.ActionTextBehavior.HIDE);
        butLength.getStyleClass().add(LEFT);
        butArea.getStyleClass().add(RIGHT);
        final HBox hboxAction = new HBox(butLength,butArea);
    
        getItems().add(hboxAction);
        
    }
    
    
}
