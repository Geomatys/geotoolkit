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

package org.geotoolkit.gui.javafx.style;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.filter.FilterFactory2;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @param <T> Edited object class
 */
public abstract class FXStyleElementController<T> extends BorderPane {

    protected static final BooleanProperty ADVANCED_MODE = new SimpleBooleanProperty(false);

    protected final SimpleObjectProperty<T> value = new SimpleObjectProperty<T>(){
        @Override
        public void set(T newValue) {
            //We do not update value when the editor is in update mode
            if(updating) return;
            super.set(newValue);
        }
    };
    protected MapLayer layer = null;
    protected volatile boolean updating = false;

    public FXStyleElementController() {
        this(true);
    }

    public FXStyleElementController(boolean loadFxml) {
        if(loadFxml){
            try{
                GeotkFX.loadJRXML(this,this.getClass());
            }catch(Throwable ex){
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Called by FXMLLoader after creating controller.
     */
    public void initialize(){        
        value.addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                updating = true;
                updateEditor((T)newValue);
                updating = false;
            }
        });
    }

    public abstract Class<T> getEditedClass();
    
    public MapLayer getLayer() {
        return layer;
    }

    public void setLayer(MapLayer layer) {
        this.layer = layer;
    }
    
    /**
     * 
     * @return Property being edited.
     */
    public ObjectProperty<T> valueProperty(){
        return value;
    }
    
    /**
     * Panels may have 2 modes, simple or advanced.
     * All properties are not important, we divide them between simple and advanced.
     * This a static property used by all javafx panels.
     *
     * @return true if in advanced mode
     */
    public BooleanProperty advancedModeProperty(){
        return ADVANCED_MODE;
    }

    /**
     *
     * @param advanced true to display advanced properties
     */
    public void setAdvancedMode(boolean advanced){
        ADVANCED_MODE.set(advanced);
    }

    /**
     *
     * @return true if in advanced mode
     */
    public boolean isAdvancedMode(){
        return ADVANCED_MODE.get();
    }

    /**
     * Create a value object editable by this controller.
     * @return a new instance of the edited class of this editor
     */
    public abstract T newValue();
    
    /**
     * Called when to value property changes.
     * @param styleElement  edited object
     */
    protected abstract void updateEditor(T styleElement);
        
    protected synchronized static FilterFactory2 getFilterFactory(){
        return GeotkFX.getFilterFactory();
    }
    
    protected synchronized static MutableStyleFactory getStyleFactory(){
        return GeotkFX.getStyleFactory();
    }

    public static void configureAdvancedProperty(Node ... nodes){
        for(Node n : nodes){
            n.visibleProperty().bind(ADVANCED_MODE);
            n.managedProperty().bind(ADVANCED_MODE);
        }
    }

}
