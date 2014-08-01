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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.util.Builder;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStyleElementController<E extends FXStyleElementController<E,T>,T> extends BorderPane implements Builder<E> {
        
    private static final String PATH = "org/geotoolkit/gui/javafx/internal/Bundle";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PATH);
    
    @FXML
    protected Node fxmlRoot;
    
    protected final SimpleObjectProperty<T> value = new SimpleObjectProperty<>();
    protected MapLayer layer = null;
    
    /**
     * Called by FXMLLoader after creating controller.
     */
    public void initialize(){
        if(fxmlRoot==null){
            throw new IllegalArgumentException("Root node is not set, fix "+getClass().getName()+".fxml , root pane must have id : fxmlRoot");
        }
        this.setCenter(fxmlRoot);
        
        value.addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                updateEditor((T)newValue);
            }
        });
    }

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
    
    protected void updateEditor(T styleElement){
        
    }
    
    /**
     * Call after construction, to attach the visual nodes from FXML.
     * 
     * @return this
     */
    @Override
    public final E build() {
        try{
            final Class thisClass = this.getClass();
            final String fxmlpath = "/"+thisClass.getName().replace('.', '/')+".fxml";
            final FXMLLoader loader = new FXMLLoader(
                    thisClass.getResource(fxmlpath), 
                    BUNDLE, new JavaFXBuilderFactory(this.getClass().getClassLoader(),true),
                    (Class<?> param) -> this);
            //in special environement like osgi or other, we must use the proper class loaders
            //not necessarly the one who loaded the FXMLLoader class
            loader.setClassLoader(thisClass.getClassLoader());
            try {
                loader.load();
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
        return (E)this;
    }
        
    
}
