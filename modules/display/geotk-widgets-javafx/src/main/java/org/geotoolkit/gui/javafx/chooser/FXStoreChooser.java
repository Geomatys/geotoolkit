/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2018, Geomatys
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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.client.ClientFactory;
import org.geotoolkit.db.AbstractJDBCFeatureStoreFactory;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.parameter.FXParameterEditor;
import org.geotoolkit.gui.javafx.util.FXOptionDialog;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.style.RandomStyleBuilder;
import org.opengis.parameter.ParameterValueGroup;
import org.geotoolkit.storage.coverage.GridCoverageResource;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStoreChooser extends BorderPane {

    public static final Predicate CLIENTFACTORY_ONLY = (Object t) -> t instanceof ClientFactory;

    static final Comparator<Object> SORTER = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            //sort by type first
            final int o1p = getPriority(o1);
            final int o2p = getPriority(o2);

            if(o1p == o2p){
                final String o1Name = getText(o1);
                final String o2Name = getText(o2);
                return o1Name.compareTo(o2Name);
            }else{
                return Integer.compare(o1p, o2p);
            }
        }

        private String getText(Object candidate){
            if(candidate instanceof DataStoreFactory){
                return ((DataStoreFactory)candidate).getDisplayName().toString();
            }else if(candidate instanceof DataStoreProvider){
                return ((DataStoreProvider)candidate).getShortName();
            }else if(candidate instanceof ClientFactory){
                return ((ClientFactory)candidate).getDisplayName().toString();
            }else{
                return "";
            }
        }

        private int getPriority(Object o){

            if (o instanceof ClientFactory) {
                return 4;
            }else if(o instanceof AbstractJDBCFeatureStoreFactory){
                return 3;
            }

            ResourceType[] types = new ResourceType[0];
            if (o instanceof DataStoreProvider) {
                types = org.geotoolkit.storage.DataStores.getResourceTypes((DataStoreProvider) o);
            }

            if (ArraysExt.contains(types, ResourceType.VECTOR)){
                return 1;
            } else if ((ArraysExt.contains(types, ResourceType.COVERAGE)
                    | ArraysExt.contains(types, ResourceType.GRID)
                    | ArraysExt.contains(types, ResourceType.PYRAMID))
                    ){
                return 2;
            } else {
                return 5;
            }

//            if(o instanceof FileFeatureStoreFactory){
//                return 1;
//            }else if((ArraysExt.contains(types, ResourceType.COVERAGE)
//                    | ArraysExt.contains(types, ResourceType.GRID)
//                    | ArraysExt.contains(types, ResourceType.PYRAMID))
//                    && !(o instanceof ClientFactory)){
//                return 2;
//            }else if(o instanceof AbstractFolderFeatureStoreFactory){
//                return 3;
//            }else if(o instanceof AbstractJDBCFeatureStoreFactory){
//                return 4;
//            }else if(o instanceof ClientFactory){
//                return 6;
//            }else{
//                return 5;
//            }
        }

    };

    private final Accordion accordion = new Accordion();
    private final ListView<Object> factoryView = new ListView<>();
    private final FXResourceChooser resourceChooser = new FXResourceChooser();
    private final Label placeholder = new Label(" . . . ");
    private final FXParameterEditor paramEditor = new FXParameterEditor();
    private final ScrollPane listScroll = new ScrollPane(factoryView);
    private final Button connectButton = new Button(GeotkFX.getString(FXStoreChooser.class,"apply"));
    private final Label infoLabel = new Label();
    private final BooleanProperty decorateProperty = new SimpleBooleanProperty(false);
    private final TitledPane paneResource;

    public FXStoreChooser() {
        this(null);
    }

    public FXStoreChooser(Predicate factoryFilter) {

        final Set factoriesLst = new HashSet();
        factoriesLst.addAll(DataStores.providers());

        ObservableList factories = FXCollections.observableArrayList(factoriesLst);
        Collections.sort(factories, SORTER);
        if(factoryFilter!=null){
            factories = factories.filtered(factoryFilter);
        }

        factoryView.setItems(factories);
        factoryView.setCellFactory((ListView<Object> param) -> new FactoryCell());
        listScroll.setFitToHeight(true);
        listScroll.setFitToWidth(true);

        //hide the tree table header
        FXUtilities.hideTableHeader(paramEditor.getTreetable());

        final BorderPane hpane = new BorderPane(infoLabel, null, connectButton, null, null);
        hpane.setPadding(new Insets(6, 6, 6, 6));
        final BorderPane vpane = new BorderPane(paramEditor, null, null, hpane, null);
        vpane.setPadding(Insets.EMPTY);

        final TitledPane paneFactory = new TitledPane(GeotkFX.getString(FXStoreChooser.class,"factory"), listScroll);
        paneFactory.setFont(Font.font(paneFactory.getFont().getFamily(), FontWeight.BOLD, paneFactory.getFont().getSize()));
        final TitledPane paneConfig = new TitledPane(GeotkFX.getString(FXStoreChooser.class,"config"), vpane);
        paneResource = new TitledPane(GeotkFX.getString(FXStoreChooser.class,"resource"), placeholder);
        paneResource.setContent(resourceChooser);

        accordion.getPanes().add(paneFactory);
        accordion.getPanes().add(paneConfig);
        accordion.getPanes().add(paneResource);
        accordion.setPrefSize(500, 500);
        accordion.setExpandedPane(paneFactory);

        setCenter(accordion);

        factoryView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        factoryView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Object>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends Object> c) {
                final Object factory = factoryView.getSelectionModel().getSelectedItem();

                final ParameterValueGroup param;
                if (factory instanceof DataStoreProvider) {
                    param = ((DataStoreProvider)factory).getOpenParameters().createValue();
                } else {
                    return;
                }
                paramEditor.setParameter(param);
                accordion.setExpandedPane(paneConfig);
            }
        });

        connectButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                try {
                    resourceChooser.setResource(null);
                    DataStore store = getStore();
                    resourceChooser.setResource(store);
                    accordion.setExpandedPane(paneResource);

                } catch (DataStoreException ex) {
                    infoLabel.setText("Error "+ex.getMessage());
                    Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(),ex);
                }
            }
        });

    }

    public BooleanProperty decorateProperty(){
        return decorateProperty;
    }

    private void setLayerSelectionVisible(boolean layerVisible) {
        resourceChooser.setVisible(layerVisible);
    }

    /**
     *
     * @return FeatureStore, CoverageStore or Client
     * @throws DataStoreException if store creation failed
     */
    private DataStore getStore() throws DataStoreException {
        final Object factory = factoryView.getSelectionModel().getSelectedItem();
        final ParameterValueGroup param = (ParameterValueGroup) paramEditor.getParameter();

        if (factory instanceof DataStoreProvider) {
            return ((DataStoreProvider)factory).open(param);
        } else {
            return null;
        }
    }

    private List<MapLayer> getSelectedLayers() throws DataStoreException {
        final List<Resource> selected = resourceChooser.getSelected();
        final List<MapLayer> layers = new ArrayList<>();
        for (Resource selection : selected) {
            if (selection instanceof GridCoverageResource) {
                final GridCoverageResource ref = (GridCoverageResource) selection;
                final MapLayer layer = MapBuilder.createCoverageLayer(ref);
                layer.setName(ref.getIdentifier().tip().toString());
                layers.add(layer);
            } else if (selection instanceof FeatureSet) {
                final FeatureSet fs = (FeatureSet) selection;
                final MapLayer layer = MapBuilder.createFeatureLayer(fs);
                layer.setStyle(RandomStyleBuilder.createRandomVectorStyle(fs.getType()));
                layers.add(layer);
            }
        }
        return layers;
    }


    /**
     * Display a modal dialog.
     *
     * @param parent parent widget, can be null
     * @return FeatureStore, CoverageStore or Client
     * @throws DataStoreException if store creation failed
     */
    public static Object showDialog(Node parent) throws DataStoreException{
        return showDialog(parent, null);
    }

    /**
     * Display a modal dialog.
     *
     * @param parent Parent region over which dialog will be displayed.
     * @param predicate factory filter
     * @return FeatureStore, CoverageStore or Client
     * @throws DataStoreException if store creation failed
     */
    public static Object showDialog(Node parent, Predicate predicate) throws DataStoreException{
        final List lst = showDialog(parent, predicate, false);
        if(lst.isEmpty()){
            return null;
        }else{
            return lst.get(0);
        }
    }

    /**
     * Display a modal dialog choosing layers.
     *
     * @param parent Parent region over which dialog will be displayed.
     * @param predicate factory filter
     * @return created map layers.
     * @throws DataStoreException if store creation failed
     */
    public static List<MapLayer> showLayerDialog(Node parent, Predicate predicate) throws DataStoreException{
        return showDialog(parent, predicate, true);
    }

    private static List showDialog(Node parent, Predicate predicate, boolean layerVisible) throws DataStoreException{
        final FXStoreChooser chooser = new FXStoreChooser(predicate);
        chooser.decorateProperty().set(true);
        chooser.setLayerSelectionVisible(layerVisible);

        final boolean res = FXOptionDialog.showOkCancel(parent, chooser, "", true);

        if (res) {
            if(layerVisible){
                return chooser.getSelectedLayers();
            }else{
                final Object store = chooser.getStore();
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


    public static class FactoryCell extends ListCell{

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            setText(null);
            setGraphic(null);
            if(!empty && item!=null){
                if(item instanceof DataStoreFactory){
                    setText(((DataStoreFactory)item).getDisplayName().toString());
                }else if(item instanceof DataStoreProvider){
                    setText(((DataStoreProvider)item).getShortName());
                }else if(item instanceof ClientFactory){
                    setText(((ClientFactory)item).getDisplayName().toString());
                }
                setGraphic(new ImageView(findIcon(item)));
            }
        }
    }

    private static final Image EMPTY_24 = SwingFXUtils.toFXImage(new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB),null);
    private static final Image ICON_SERVER = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_GLOBE, 24, FontAwesomeIcons.DISABLE_COLOR),null);
    private static final Image ICON_DATABASE = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_DATABASE, 24, FontAwesomeIcons.DISABLE_COLOR),null);
    private static final Image ICON_VECTOR = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_VECTOR_SQUARE, 24, FontAwesomeIcons.DISABLE_COLOR),null);
    private static final Image ICON_COVERAGE = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_IMAGE, 24, FontAwesomeIcons.DISABLE_COLOR),null);
    private static final Image ICON_FOLDER = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_FOLDER, 24, FontAwesomeIcons.DISABLE_COLOR),null);
    private static final Image ICON_FILE = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_FILE, 24, FontAwesomeIcons.DISABLE_COLOR),null);
    private static final Image ICON_OTHER = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_ATLAS, 24, FontAwesomeIcons.DISABLE_COLOR),null);
    private static final Image ICON_PRODUCT = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_ATLAS, 24, FontAwesomeIcons.DISABLE_COLOR),null);

    private static Image findIcon(Object candidate){


        if (candidate instanceof ClientFactory) {
            return ICON_SERVER;
        }else if(candidate instanceof AbstractJDBCFeatureStoreFactory){
            return ICON_DATABASE;
        }

        ResourceType[] types = new ResourceType[0];
        if (candidate instanceof DataStoreProvider) {
            types = org.geotoolkit.storage.DataStores.getResourceTypes((DataStoreProvider) candidate);
        }

        if (ArraysExt.contains(types, ResourceType.VECTOR)){
            return ICON_VECTOR;
        } else if ((ArraysExt.contains(types, ResourceType.COVERAGE)
                | ArraysExt.contains(types, ResourceType.GRID)
                | ArraysExt.contains(types, ResourceType.PYRAMID))
                ){
            return ICON_COVERAGE;
        } else {
            return ICON_OTHER;
        }



//        ResourceType[] types = new ResourceType[0];
//        if (candidate instanceof DataStoreProvider) {
//            types = org.geotoolkit.storage.DataStores.getResourceTypes((DataStoreProvider) candidate);
//        }
//
//        Image icon = EMPTY_24;
//        if(candidate instanceof AbstractFolderFeatureStoreFactory){
//            icon = ICON_FOLDER;
//        }else if(candidate instanceof FileFeatureStoreFactory){
//            icon = ICON_FILE;
//        }else if(candidate instanceof ClientFactory){
//            icon = ICON_SERVER;
//        }else if(candidate instanceof AbstractJDBCFeatureStoreFactory){
//            icon = ICON_DATABASE;
//        }else if(ArraysExt.contains(types, ResourceType.COVERAGE) | ArraysExt.contains(types, ResourceType.GRID) | ArraysExt.contains(types, ResourceType.PYRAMID)){
//            icon = ICON_COVERAGE;
//        }else if(ArraysExt.contains(types, ResourceType.VECTOR)){
//            icon = ICON_VECTOR;
//        }else if(candidate instanceof DataStoreProvider){
//            icon = ICON_OTHER;
//        }
//
//        return icon;
    }

}
