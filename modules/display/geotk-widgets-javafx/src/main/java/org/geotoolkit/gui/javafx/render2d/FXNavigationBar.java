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
package org.geotoolkit.gui.javafx.render2d;

import org.geotoolkit.gui.javafx.render2d.navigation.FXPanAction;
import org.geotoolkit.gui.javafx.render2d.navigation.FXZoomAllAction;
import org.geotoolkit.gui.javafx.render2d.navigation.FXZoomInAction;
import org.geotoolkit.gui.javafx.render2d.navigation.FXZoomNextAction;
import org.geotoolkit.gui.javafx.render2d.navigation.FXZoomOutAction;
import org.geotoolkit.gui.javafx.render2d.navigation.FXZoomPreviousAction;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import org.controlsfx.control.action.ActionUtils;
import org.geotoolkit.gui.javafx.render2d.navigation.FXRepaintAction;
import org.geotoolkit.gui.javafx.render2d.navigation.FXZoomToAction;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXNavigationBar extends ToolBar {

    private static final String LEFT   = "buttongroup-left";
    private static final String CENTER = "buttongroup-center";
    private static final String RIGHT  = "buttongroup-right";
    
    public FXNavigationBar(FXMap map) {
        getStylesheets().add("/org/geotoolkit/gui/javafx/buttonbar.css");
        
        final Button butAll = new FXZoomAllAction(map).createButton(ActionUtils.ActionTextBehavior.HIDE);
        final ToggleButton butIn = new FXZoomInAction(map).createToggleButton(ActionUtils.ActionTextBehavior.HIDE);
        final ToggleButton butOut = new FXZoomOutAction(map).createToggleButton(ActionUtils.ActionTextBehavior.HIDE);
        final ToggleButton butPan = new FXPanAction(map,false).createToggleButton(ActionUtils.ActionTextBehavior.HIDE);
        final Button butAt = new FXZoomToAction(map).createButton(ActionUtils.ActionTextBehavior.HIDE);
        final Button butRepaint = new FXRepaintAction(map).createButton(ActionUtils.ActionTextBehavior.HIDE);
        final Button butPrevious = new FXZoomPreviousAction(map).createButton(ActionUtils.ActionTextBehavior.HIDE);
        final Button butNext = new FXZoomNextAction(map).createButton(ActionUtils.ActionTextBehavior.HIDE);
        
        butAll.getStyleClass().add(LEFT);    
        butIn.getStyleClass().add(CENTER);
        butOut.getStyleClass().add(CENTER);
        butPan.getStyleClass().add(CENTER);  
        butAt.getStyleClass().add(CENTER);
        butRepaint.getStyleClass().add(CENTER);
        butPrevious.getStyleClass().add(CENTER);
        butNext.getStyleClass().add(RIGHT); 
        
        final HBox hboxHandler = new HBox(
                butAll, butIn, butOut, butPan, 
                butAt, butRepaint, butPrevious, butNext);
        
        getItems().add(hboxHandler);
        
    }
    
    
}
