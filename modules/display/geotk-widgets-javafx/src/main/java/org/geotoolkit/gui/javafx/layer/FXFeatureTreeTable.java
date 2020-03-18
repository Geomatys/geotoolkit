/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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

import java.util.logging.Level;
import java.util.stream.Stream;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.gui.javafx.util.FXNumberSpinner;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXFeatureTreeTable extends FXPropertyPane{

    protected final FXNumberSpinner spinner = new FXNumberSpinner();
    protected final Label label1 = new Label();
    protected final Label label2 = new Label();
    protected final FXFeatureViewer treetable = new FXFeatureViewer();
    private final CheckBox opVisible = new CheckBox("Operations");
    private final CheckBox nullVisible = new CheckBox("Null values");
    private final CheckBox fullNamesVisible = new CheckBox("Full names");
    private boolean loadAll = false;

    protected FeatureMapLayer layer;


    public FXFeatureTreeTable() {
        final ScrollPane scroll = new ScrollPane(treetable);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        scroll.setPrefSize(600, 400);
        scroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setCenter(scroll);

        spinner.getSpinner().setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));

        treetable.operationVisibleProperty().bindBidirectional(opVisible.selectedProperty());
        treetable.nullVisibleProperty().bindBidirectional(nullVisible.selectedProperty());
        treetable.fullNameVisibleProperty().bindBidirectional(fullNamesVisible.selectedProperty());

        final HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(label1);
        box.getChildren().add(spinner);
        box.getChildren().add(label2);
        box.getChildren().add(opVisible);
        box.getChildren().add(nullVisible);
        box.getChildren().add(fullNamesVisible);

        setTop(box);

        spinner.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (layer != null) {
                    final FeatureSet col = layer.getResource();
                    try (Stream<Feature> stream = col.features(false).skip(newValue.longValue())) {
                        Feature feature = stream.findFirst().orElse(null);
                        treetable.setFeature(feature);
                    } catch (DataStoreException ex) {
                        Loggers.DATA.log(Level.WARNING, ex.getMessage(),ex);
                    }
                }
            }
        });

    }

    @Override
    public String getTitle() {
        return "Features tree-table";
    }

    public String getCategory(){
        return "";
    }

    public boolean isLoadAll() {
        return loadAll;
    }

    public void setLoadAll(boolean loadAll) {
        this.loadAll = loadAll;
    }

    public void setEditable(boolean editable){
        treetable.setEditable(editable);
    }

    public boolean isEditable(){
        return treetable.isEditable();
    }

    @Override
    public boolean init(Object candidate){
        if(!(candidate instanceof FeatureMapLayer)) return false;
        layer = (FeatureMapLayer) candidate;
        final FeatureSet col = layer.getResource();
        try (Stream<Feature> stream = col.features(false)) {
            stream.findFirst().ifPresent(treetable::setFeature);
        } catch (DataStoreException ex) {
            Loggers.DATA.log(Level.WARNING, ex.getMessage(),ex);
        }

        try (Stream<Feature> stream = col.features(false)) {
            long count = stream.count();
            label1.setText("Feature number ");
            label2.setText(" / "+count);
        } catch (DataStoreException ex) {
            Loggers.DATA.log(Level.WARNING, ex.getMessage(),ex);
        }


        return true;
    }


}
