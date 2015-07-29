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
package org.geotoolkit.gui.javafx.action;

import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;

/**
 * Action to rollback a feature maplayer session.
 * <br/>
 * The action is diabled when there are no changes in the session.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class RollbackAction extends Action implements Consumer<ActionEvent>, StorageListener<StorageEvent, StorageEvent> {

    private final ObjectProperty<FeatureMapLayer> layerProperty = new SimpleObjectProperty<>();
    private final StorageListener.Weak weakListener;

    public RollbackAction() {
        super(GeotkFX.getString(CommitAction.class,"text"));
        setGraphic(new ImageView(GeotkFX.ICON_UNDO));
        weakListener = new StorageListener.Weak(this);

        //listen to storage state
        layerProperty.addListener(new ChangeListener<FeatureMapLayer>() {
            @Override
            public void changed(ObservableValue<? extends FeatureMapLayer> observable, FeatureMapLayer oldValue, FeatureMapLayer newValue) {
                if(oldValue!=null) weakListener.unregisterSource(oldValue.getCollection().getSession());
                if(newValue!=null) weakListener.registerSource(newValue.getCollection().getSession());
                contentChanged(null);
            }
        });
        setEventHandler(this);
        contentChanged(null);
    }

    public RollbackAction(FeatureMapLayer layer) {
        this();
        layerProperty.setValue(layer);
    }

    public FeatureMapLayer getLayer() {
        return layerProperty.get();
    }

    public void setLayer(FeatureMapLayer layer){
        this.layerProperty.set(layer);
    }

    public ObjectProperty<FeatureMapLayer> layerProperty(){
        return layerProperty;
    }

    @Override
    public void accept(ActionEvent t) {
        final FeatureMapLayer layer = getLayer();
        if(layer==null) return;
        layer.getCollection().getSession().rollback();
    }

    @Override
    public void structureChanged(StorageEvent event) {
    }

    @Override
    public void contentChanged(StorageEvent event) {
        final FeatureMapLayer layer = getLayer();
        setDisabled(layer==null || !layer.getCollection().getSession().hasPendingChanges());
    }

    public Button createButton(ActionUtils.ActionTextBehavior behavior){
        return ActionUtils.createButton(this, behavior);
    }
    
}
