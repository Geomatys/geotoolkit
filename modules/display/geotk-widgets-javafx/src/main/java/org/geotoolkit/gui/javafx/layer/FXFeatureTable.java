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

package org.geotoolkit.gui.javafx.layer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.FeatureMapLayer;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXFeatureTable extends FXPropertyPane{

    protected final TableView<Feature> table = new TableView<>();
    private boolean loadAll = false;

    protected FeatureMapLayer layer;

    // Bundle management
    /**
     * bundles contains ResourceBundles indexed by table names.
     */
    private final Map<String, ResourceBundle> bundles = new HashMap<>();
    private String bundlePrefix;

    public FXFeatureTable() {
        final ScrollPane scroll = new ScrollPane(table);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        scroll.setPrefSize(600, 400);
        scroll.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setCenter(scroll);

        //listen to table selection
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Feature>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends Feature> c) {
                final Iterator<Feature> ite = table.getSelectionModel().getSelectedItems().iterator();
                final Set<Identifier> set = new HashSet<>();
                while(ite.hasNext()){
                    set.add(FeatureExt.getId(ite.next()));
                }
                final Id selection = GO2Utilities.FILTER_FACTORY.id(set);
                if(layer!=null){
                    layer.setSelectionFilter(selection);
                }

            }
        });

        final Button loadButton = new Button(GeotkFX.getString(FXFeatureTable.class, "loadData"));
        loadButton.setOnAction(this::loadData);
        table.setPlaceholder(loadButton);
    }

    public FXFeatureTable(final Map<String, String> bundleMapping){
        this();
        for(final String key : bundleMapping.keySet()){
            final String bundleBaseName = bundleMapping.get(key);
            try{
                final ResourceBundle bundle = ResourceBundle.getBundle(bundleBaseName);
                bundles.put(key, bundle);
            }
            catch(Exception ex){
                Loggers.JAVAFX.log(Level.INFO, ex.getMessage(),ex);
            }
        }
    }

    public FXFeatureTable(final String bundleBaseNamePrefix){
        this();
        bundlePrefix = bundleBaseNamePrefix;
    }

    @Override
    public String getTitle() {
        return "Features table";
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
        table.setEditable(editable);
    }

    public boolean isEditable(){
        return table.isEditable();
    }

    public boolean init(Object candidate){
        if(!(candidate instanceof FeatureMapLayer)) return false;

        layer = (FeatureMapLayer) candidate;
        final FeatureSet features = layer.getResource();
        try {
            final FeatureType featureType = features.getType();

            table.getColumns().clear();
            for(PropertyType prop : featureType.getProperties(true)){
                final TableColumn<Feature,String> column = new TableColumn<Feature,String>(generateFinalColumnName(prop));
                column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Feature, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Feature, String> param) {
                        final Object val = param.getValue().getPropertyValue(prop.getName().toString());
                        if(val instanceof Geometry){
                            return new SimpleObjectProperty<>("{geometry}");
                        }else{
                            return new SimpleObjectProperty<>(String.valueOf(val));
                        }
                    }
                });
                column.setCellFactory(TextFieldTableCell.forTableColumn());
                table.getColumns().add(column);
            }
        } catch(DataStoreException ex) {
            Loggers.DATA.log(Level.WARNING, ex.getMessage(),ex);
            return true;
        }

        if (loadAll) {
            try (Stream<Feature> stream = features.features(false)) {
                final List<Feature> list = stream.collect(Collectors.toList());
                table.setItems(FXCollections.observableArrayList(list));
            } catch (DataStoreException ex) {
                Loggers.DATA.log(Level.WARNING, ex.getMessage(),ex);
                return true;
            }
        }

        //TODO make a caching version, this is too slow for today use.
        //final ObservableFeatureCollection obsCol = new ObservableFeatureCollection((FeatureCollection<Feature>) col);
        //table.setItems(obsCol);

        return true;
    }

    private String generateFinalColumnName(final PropertyType prop) {
        Map<String, Entry<String, String>> labelInfo;
        try {
            labelInfo = (Map) prop.getDesignation();
        } catch (Exception ex) {
            Loggers.JAVAFX.log(Level.INFO, ex.getMessage(), ex);
            labelInfo = null;
        }

        final String labelName = prop.getName().toString();
        String columnName = labelName;
        String tableName = null;

        // If exists, explore labelInfo to retrive table and column respect to this label.
        if (labelInfo != null) {
            final Entry<String, String> entry = labelInfo.get(labelName);
            if (entry != null) {
                if (entry.getKey() != null) {
                    tableName = entry.getKey();
                } else {
                    tableName = null;
                }
                if (entry.getValue() != null) {
                    columnName = entry.getValue();
                } else {
                    columnName = labelName;
                }
            }
        }

        //If table name is not null, try to found resourcebundle for this table.
        if (tableName != null) {

            // If there isn't resource bundles (or not for the curruen table), try to generate.
            if (bundles.get(tableName) == null) {
                if (bundlePrefix != null) {
                    bundles.put(tableName, ResourceBundle.getBundle(bundlePrefix + tableName));
                }
            }
        }

        final ResourceBundle bundle = bundles.get(tableName);

        String finalColumnName;
        if (labelName == null) {
            finalColumnName = "";
        } else if (bundle == null) {
            if (!labelName.equals(columnName)) {
                finalColumnName = columnName + " as " + labelName;
            } else {
                finalColumnName = columnName;
            }
        } else {
            try {
                if (!labelName.equals(columnName)) {
                    finalColumnName = bundle.getString(columnName) + " as " + labelName;
                } else {
                    finalColumnName = bundle.getString(columnName);
                }
            } catch (MissingResourceException ex) {
                if (!labelName.equals(columnName)) {
                    finalColumnName = columnName + " as " + labelName;
                } else {
                    finalColumnName = columnName;
                }
            }
        }
        return finalColumnName;
    }

    private void loadData(ActionEvent event){
        final FeatureSet col = layer.getResource();
        try (Stream<Feature> stream = col.features(false)) {
            final List<Feature> list = stream.collect(Collectors.toList());
            table.setItems(FXCollections.observableArrayList(list));
        } catch (DataStoreException ex) {
            Loggers.DATA.log(Level.WARNING, ex.getMessage(),ex);
        }
    }

}
