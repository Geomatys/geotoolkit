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
package org.geotoolkit.gui.javafx.layer;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.Selector;
import org.geotoolkit.gui.javafx.feature.FXFeatureTypeEditor;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.feature.FeatureType;


/**
 * Feature type edition panel.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXFeatureTypePane extends FXPropertyPane {

    private final FXFeatureTypeEditor editor = new FXFeatureTypeEditor(null);
    private final CheckBox editable = new CheckBox(GeotkFX.getString(FXFeatureTypePane.class, "edit"));
    private final Button apply = new Button(GeotkFX.getString(FXFeatureTypePane.class, "apply"));
    private FeatureMapLayer layer;

    public FXFeatureTypePane() {

        editor.editableProperty().bind(editable.selectedProperty());
        editor.disableProperty().bind(editable.selectedProperty().not());
        apply.setOnAction(this::apply);

        final GridPane toppane = new GridPane();
        toppane.setHgap(10);
        toppane.setVgap(10);
        toppane.setPadding(new Insets(10, 10, 10, 10));
        toppane.getRowConstraints().add(new RowConstraints(USE_COMPUTED_SIZE,USE_COMPUTED_SIZE,USE_COMPUTED_SIZE,Priority.NEVER,VPos.CENTER,false));
        toppane.getColumnConstraints().add(new ColumnConstraints(USE_COMPUTED_SIZE,USE_COMPUTED_SIZE,USE_COMPUTED_SIZE,Priority.NEVER,HPos.LEFT,false));
        toppane.getColumnConstraints().add(new ColumnConstraints(USE_COMPUTED_SIZE,USE_COMPUTED_SIZE,USE_COMPUTED_SIZE,Priority.NEVER,HPos.LEFT,false));
        toppane.add(editable, 0, 0);
        toppane.add(apply, 1, 0);

        setTop(toppane);
        setCenter(editor);
    }

    public boolean canHandle(Object target) {
        return target instanceof FeatureMapLayer;
    }

    private void apply(ActionEvent event){
        final FeatureType ft = editor.getFeatureType();
        final Selector source = (Selector) layer.getCollection().getSource();
        final FeatureStore store = source.getSession().getFeatureStore();
        try {
            store.updateFeatureType(ft);
        } catch (DataStoreException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getTitle() {
        return GeotkFX.getString(FXFeatureTypePane.class, "title");
    }

    @Override
    public boolean init(Object target) {
        if(!(target instanceof FeatureMapLayer)){
            return false;
        }

        this.layer = (FeatureMapLayer) target;
        final FeatureType featureType = layer.getCollection().getFeatureType();
        editor.setFeatureType(featureType);

        return true;
    }


}
