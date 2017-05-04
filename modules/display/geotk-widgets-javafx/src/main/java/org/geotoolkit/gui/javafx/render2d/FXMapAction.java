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

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class FXMapAction extends Action implements Consumer<ActionEvent>{

    protected FXMap map;

    public FXMapAction() {
        this((String)null);
    }

    public FXMapAction(String text) {
        this(null,text,null,null);
    }

    public FXMapAction(FXMap map) {
        super((String)null);
        setMap(map);
    }

    public FXMapAction(FXMap map, String shortText, String longText, Image graphic) {
        super(shortText);
        setLongText(longText);
        if(graphic!=null) graphicProperty().set(new ImageView(graphic));
        setMap(map);
        setEventHandler(this);

    }

    public FXMap getMap() {
        return map;
    }

    public void setMap(FXMap map) {
        this.map = map;
        disabledProperty().set(map==null);
    }

    public Button createButton(ActionUtils.ActionTextBehavior behavior){
        return ActionUtils.createButton(this, behavior);
    }

    public MenuButton createMenuButton(ActionUtils.ActionTextBehavior behavior){
        return ActionUtils.createMenuButton(this, behavior);
    }

    public MenuItem createMenuItem(){
        return ActionUtils.createMenuItem(this);
    }

    public ToggleButton createToggleButton(ActionUtils.ActionTextBehavior behavior){
        return ActionUtils.createToggleButton(this,behavior);
    }

}
