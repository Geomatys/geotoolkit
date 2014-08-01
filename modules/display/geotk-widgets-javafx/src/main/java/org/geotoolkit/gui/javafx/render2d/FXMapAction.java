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

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.ActionUtils;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class FXMapAction extends AbstractAction {

    protected final SimpleBooleanProperty selectedProperty = new SimpleBooleanProperty(false);
    
    protected FXMap map;
    
    public FXMapAction() {
        super(null);
    }
    
    public FXMapAction(String text) {
        super(text);
    }
    
    public FXMapAction(FXMap map) {
        super(null);
        setMap(map);
    }
    
    public FXMapAction(FXMap map, String shortText, String longText, Image graphic) {
        super(shortText);
        setLongText(longText);
        if(graphic!=null) graphicProperty().set(new ImageView(graphic));
        setMap(map);
        
    }

    public FXMap getMap() {
        return map;
    }

    public void setMap(FXMap map) {
        this.map = map;
        disabledProperty().set(map==null);
    }

    public SimpleBooleanProperty selectedProperty() {
        return selectedProperty;
    }
    
    public Button createButton(ActionUtils.ActionTextBehavior behavior){
        return ActionUtils.createButton(this, behavior);
    }
    
    public ToggleButton createToggleButton(ActionUtils.ActionTextBehavior behavior){
        final ToggleButton tb = ActionUtils.createToggleButton(this, behavior);
        selectedProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                tb.setSelected(newValue);
            }
        });
        return tb;
    }
    
}
