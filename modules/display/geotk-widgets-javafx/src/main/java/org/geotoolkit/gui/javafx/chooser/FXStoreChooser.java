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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
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
import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataSet;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.db.AbstractJDBCFeatureStoreFactory;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.parameter.FXParameterEditor;
import org.geotoolkit.gui.javafx.util.FXOptionDialog;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.metadata.MetadataUtilities;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.style.RandomStyleBuilder;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStoreChooser extends BorderPane {

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
            if (candidate instanceof DataStoreProvider) {
                return ((DataStoreProvider)candidate).getShortName();
            } else {
                return "";
            }
        }

        private int getPriority(Object o){

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
    private final ListView<Object> providerView = new ListView<>();
    private final FXResourceChooser resourceChooser = new FXResourceChooser();
    private final Label placeholder = new Label(" . . . ");
    private final FXParameterEditor paramEditor = new FXParameterEditor();
    private final ScrollPane listScroll = new ScrollPane(providerView);
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

        ObservableList providers = FXCollections.observableArrayList(factoriesLst);
        Collections.sort(providers, SORTER);
        if (factoryFilter != null) {
            providers = providers.filtered(factoryFilter);
        }

        providerView.setItems(providers);
        providerView.setCellFactory((ListView<Object> param) -> new FactoryCell());
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

        providerView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        providerView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Object>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends Object> c) {
                final Object factory = providerView.getSelectionModel().getSelectedItem();

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

        connectButton.setOnAction((ActionEvent event) -> { connect(); });

    }

    private void connect() {
        try {
            resourceChooser.setResource(null);
            DataStore store = getStore();
            resourceChooser.setResource(store);
            accordion.setExpandedPane(paneResource);

            Collection<DataSet> preselected = org.geotoolkit.storage.DataStores.flatten(store, true, DataSet.class);
            resourceChooser.setSelected(new ArrayList<>(preselected));

        } catch (DataStoreException ex) {
            infoLabel.setText("Error "+ex.getMessage());
            Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(),ex);
        }
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
        final Object factory = providerView.getSelectionModel().getSelectedItem();
        final ParameterValueGroup param = (ParameterValueGroup) paramEditor.getParameter();

        if (factory instanceof DataStoreProvider) {
            return ((DataStoreProvider)factory).open(param);
        } else {
            return null;
        }
    }

    private void setStore(DataStore store) {
        providerView.getSelectionModel().clearAndSelect(providerView.getItems().indexOf(store.getProvider()));
        paramEditor.setParameter(store.getOpenParameters().get());
        connect();
    }

    private List<MapItem> getSelectedLayers() throws DataStoreException {
        final List<Resource> selected = resourceChooser.getSelected();
        final List<MapItem> layers = new ArrayList<>();
        for (Resource selection : selected) {
            MapItem item = toMapItem(selection);
            if (item != null) {
                layers.add(item);
            }
        }
        return layers;
    }

    private static MapItem toMapItem(Resource resource) throws DataStoreException {
        if (resource instanceof GridCoverageResource) {
            final GridCoverageResource ref = (GridCoverageResource) resource;
            final MapLayer layer = MapBuilder.createCoverageLayer(ref);
            ref.getIdentifier().ifPresent((id) -> layer.setName(id.tip().toString()));
            return layer;
        } else if (resource instanceof FeatureSet) {
            final FeatureSet fs = (FeatureSet) resource;
            final MapLayer layer = MapBuilder.createFeatureLayer(fs);
            layer.setStyle(RandomStyleBuilder.createRandomVectorStyle(fs.getType()));
            return layer;
        } else if (resource instanceof Aggregate) {
            final Aggregate fs = (Aggregate) resource;
            final MapItem item = MapBuilder.createItem();
            fs.getIdentifier().ifPresent(new Consumer<GenericName>() {
                @Override
                public void accept(GenericName id) {
                    final String name = id.tip().toString();
                    item.setName(name);
                    item.setDescription(new DefaultDescription(
                            new SimpleInternationalString(name),
                            new SimpleInternationalString(id.toString())));
                }
            });
            if (item.getName() == null) {
                String metaIdd = MetadataUtilities.getIdentifier(fs.getMetadata());
                if (metaIdd != null) {
                    item.setName(metaIdd);
                    item.setDescription(new DefaultDescription(
                            new SimpleInternationalString(metaIdd),
                            new SimpleInternationalString(metaIdd)));
                }
            }


            for (Resource r : fs.components()) {
                MapItem i = toMapItem(r);
                if (i != null) item.items().add(i);
            }
            return item;
        }
        return null;
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
        final List lst = showDialog(parent, null, predicate, false);
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
    public static List<MapItem> showLayerDialog(Node parent, Predicate predicate) throws DataStoreException{
        return showDialog(parent, null, predicate, true);
    }

    public static List<MapItem> showLayerDialog(Node parent, DataStore store, Predicate predicate) throws DataStoreException {
        return showDialog(parent, store, predicate, true);
    }

    private static List showDialog(Node parent, DataStore store, Predicate predicate, boolean layerVisible) throws DataStoreException{
        final FXStoreChooser chooser = new FXStoreChooser(predicate);
        chooser.decorateProperty().set(true);
        chooser.setLayerSelectionVisible(layerVisible);

        if (store != null) {
            chooser.setStore(store);
        }


        final boolean res = FXOptionDialog.showOkCancel(parent, chooser, "", true);

        if (res) {
            if (layerVisible) {
                return chooser.getSelectedLayers();
            } else {
                store = chooser.getStore();
                if(store == null){
                    return Collections.EMPTY_LIST;
                } else {
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
            setStyle("-fx-font-family: 'monospaced';");
            setText(null);
            setGraphic(null);
            if (!empty && item != null) {
                if (item instanceof DataStoreProvider) {
                    final DataStoreProvider provider = (DataStoreProvider) item;
                    final StoreMetadata metadata = provider.getClass().getAnnotation(StoreMetadata.class);

                    final StringBuilder sb = new StringBuilder();

                    String name = provider.getShortName();
                    sb.append(name.toUpperCase());

                    if (metadata != null) {
                        final Capability[] capabilities = metadata.capabilities();

                        sb.append(' ');
                        sb.append('[');
                        sb.append(ArraysExt.contains(capabilities, Capability.READ) ? 'R' : '-');
                        sb.append('/');
                        sb.append(ArraysExt.contains(capabilities, Capability.WRITE) ? 'W' : '-');
                        sb.append('/');
                        sb.append(ArraysExt.contains(capabilities, Capability.CREATE) ? 'C' : '-');
                        sb.append(']');

                    } else {
                        sb.append(" [?/?/?]");
                    }

                    setText(sb.toString());
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

        if (candidate != null) {
            final StoreMetadata metadata = candidate.getClass().getAnnotation(StoreMetadata.class);
            if (metadata != null) {
                Class<? extends Resource>[] resourceTypes = metadata.resourceTypes().clone();
                Arrays.sort(resourceTypes, (Class<? extends Resource> o1, Class<? extends Resource> o2) -> o1.getName().compareTo(o2.getName()));
                String text = null;
                for (Class c : resourceTypes) {
                    if (c.isAssignableFrom(FeatureSet.class)) {
                        text += FontAwesomeIcons.ICON_VECTOR_SQUARE + "   ";
                    } else if (c.isAssignableFrom(GridCoverageResource.class)) {
                        text += FontAwesomeIcons.ICON_IMAGE + "   ";
                    } else if (c.isAssignableFrom(MultiResolutionResource.class)) {
                        text += FontAwesomeIcons.ICON_TH + "   ";
                    }
                }
                if (text != null) {

                    BufferedImage icon = IconBuilder.createImage(text, null, FontAwesomeIcons.DISABLE_COLOR, IconBuilder.FONT.get().deriveFont(24f), null, null, 2, false, true);

                    return SwingFXUtils.toFXImage(icon,null);
                }
            }
        } else {
            return ICON_OTHER;
        }


        if(candidate instanceof AbstractJDBCFeatureStoreFactory){
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
