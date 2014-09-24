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

import java.io.IOException;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.filter.FilterFactory2;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class FXStyleElementController<E extends FXStyleElementController<E,T>,T> extends BorderPane {
        
    private static final String BUNDLE_PATH = "org/geotoolkit/gui/javafx/internal/Bundle";
    private static final String CSS_PATH = "/org/geotoolkit/gui/javafx/style/StyleEditor.css";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_PATH);
    private static FilterFactory2 FF;
    private static MutableStyleFactory SF;
        
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
        final Class thisClass = this.getClass();
        final String fxmlpath = "/"+thisClass.getName().replace('.', '/')+".fxml";
        final FXMLLoader loader = new FXMLLoader(thisClass.getResource(fxmlpath));
        loader.setResources(BUNDLE);
        loader.setController(this);
        loader.setRoot(this);
        //in special environement like osgi or other, we must use the proper class loaders
        //not necessarly the one who loaded the FXMLLoader class
        loader.setClassLoader(thisClass.getClassLoader());
        try {
            loader.load();
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        getStylesheets().add(CSS_PATH);
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
     * Create a value object editable by this controller.
     * @return 
     */
    public abstract T newValue();
    
    /**
     * Called when to value property changes.
     * @param styleElement 
     */
    protected abstract void updateEditor(T styleElement);
        
    protected synchronized static FilterFactory2 getFilterFactory(){
        if(FF==null)FF = (FilterFactory2) FactoryFinder.getFilterFactory(null);
        return FF;
    }
    
    protected synchronized static MutableStyleFactory getStyleFactory(){
        if(SF==null)SF = (MutableStyleFactory) FactoryFinder.getStyleFactory(null);
        return SF;
    }
    
}
