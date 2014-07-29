/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.gui.javafx.chooser;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.gui.javafx.parameter.FXParameterEditor;
import org.geotoolkit.gui.javafx.parameter.FXValueEditor;
import org.geotoolkit.gui.javafx.util.FXOptionDialog;
import org.geotoolkit.internal.GeotkFXBundle;
import org.geotoolkit.map.MapLayer;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXFeatureStoreChooser extends SplitPane {

    private static final Logger LOGGER = Logging.getLogger(FXFeatureStoreChooser.class);

    private static final Comparator<FeatureStoreFactory> SORTER = new Comparator<FeatureStoreFactory>() {
        @Override
        public int compare(FeatureStoreFactory o1, FeatureStoreFactory o2) {
            return o1.getDisplayName().toString().compareTo(o2.getDisplayName().toString());
        }
    };
    
    private final ListView<FeatureStoreFactory> factoryView = new ListView<>();
    private final FXLayerChooser layerChooser = new FXLayerChooser();
    private final FXParameterEditor paramEditor = new FXParameterEditor();
    private final ScrollPane listScroll = new ScrollPane(factoryView);
    private final Button connectButton = new Button(GeotkFXBundle.getString(FXFeatureStoreChooser.class,"connect"));
    private final Label infoLabel = new Label();
        
    public FXFeatureStoreChooser() {
        
        final ObservableList<FeatureStoreFactory> factories = FXCollections.observableArrayList(FeatureStoreFinder.getAvailableFactories(null));
        Collections.sort(factories, SORTER);

        factoryView.setItems(factories);
        factoryView.setCellFactory(new Callback<ListView<FeatureStoreFactory>, ListCell<FeatureStoreFactory>>() {
            @Override
            public ListCell<FeatureStoreFactory> call(ListView<FeatureStoreFactory> param) {
                final ListCell<FeatureStoreFactory> cell = new TextFieldListCell<>(new StringConverter<FeatureStoreFactory>() {

                    @Override
                    public String toString(FeatureStoreFactory object) {
                        return object.getDisplayName().toString();
                    }

                    @Override
                    public FeatureStoreFactory fromString(String string) {
                        throw new UnsupportedOperationException("Not supported.");
                    }
                });
                return cell;
            }
        });
        
        listScroll.setFitToHeight(true);
        listScroll.setFitToWidth(true);        
        
        final BorderPane hpane = new BorderPane(infoLabel, null, connectButton, null, null);        
        hpane.setPadding(new Insets(8, 8, 8, 8));
        final BorderPane vpane = new BorderPane(paramEditor, null, null, hpane, null);
        
        getItems().add(listScroll);
        getItems().add(vpane);
        getItems().add(layerChooser);
        
        factoryView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        factoryView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<FeatureStoreFactory>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends FeatureStoreFactory> c) {
                final FeatureStoreFactory factory = factoryView.getSelectionModel().getSelectedItem();
                if(factory==null) return;
                final ParameterValueGroup param = factory.getParametersDescriptor().createValue();
                paramEditor.setParameter(param);
            }
        });
        
        
        connectButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FeatureStore store = null;
                try {
                    layerChooser.setSource(null);
                    store = getFeatureStore();
                    layerChooser.setSource(store);
                    infoLabel.setText(GeotkFXBundle.getString(FXFeatureStoreChooser.class,"ok"));
                } catch (DataStoreException ex) {
                    infoLabel.setText(""+ex.getMessage());
                    LOGGER.log(Level.WARNING, ex.getMessage(),ex);
                }
            }
        });
        
    }
    
    private void setLayerSelectionVisible(boolean layerVisible) {
        layerChooser.setVisible(layerVisible);
    }
    
    
    private FeatureStore getFeatureStore() throws DataStoreException {
        final FeatureStoreFactory factory = factoryView.getSelectionModel().getSelectedItem();

        if(factory == null){
            return null;
        }

        final ParameterValueGroup param = (ParameterValueGroup) paramEditor.getParameter();
        return factory.open(param);
    }
    
    private List<MapLayer> getSelectedLayers() throws DataStoreException {
        return layerChooser.getLayers();
    }
    
    
    /**
     * Display a modal dialog.
     *
     * @return
     * @throws DataStoreException
     */
    public static List<FeatureStore> showDialog(Node parent) throws DataStoreException{
        return showDialog(parent,Collections.EMPTY_LIST);
    }

    /**
     * Display a modal dialog.
     *
     * @param editors : additional FeatureOutline editors
     * @return
     * @throws DataStoreException
     */
    public static List<FeatureStore> showDialog(Node parent, List<FXValueEditor> editors) throws DataStoreException{
        return showDialog(parent,editors, false);
    }

    /**
     * Display a modal dialog choosing layers.
     *
     * @param editors : additional FeatureOutline editors
     * @return
     * @throws DataStoreException
     */
    public static List<MapLayer> showLayerDialog(Node parent, List<FXValueEditor> editors) throws DataStoreException{
        return showDialog(parent,editors, true);
    }

    private static List showDialog(Node parent, List<FXValueEditor> editors, boolean layerVisible) throws DataStoreException{
        final FXFeatureStoreChooser chooser = new FXFeatureStoreChooser();
        if(editors != null){
            chooser.paramEditor.setAvailableEditors(editors);
        }
        chooser.setLayerSelectionVisible(layerVisible);
        
        
        final boolean res = FXOptionDialog.showOkCancel(parent, chooser, "", true);

        if (res) {
            if(layerVisible){
                return chooser.getSelectedLayers();
            }else{
                final FeatureStore store = chooser.getFeatureStore();
                if(store == null){
                    return Collections.EMPTY_LIST;
                }else{
                    return Collections.singletonList(store);
                }
            }
        } else {
            return Collections.EMPTY_LIST;
        }

    }

}
