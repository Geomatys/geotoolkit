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
package org.geotoolkit.gui.javafx.contexttree.menu;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.geotoolkit.data.session.Session;
import org.opengis.util.GenericName;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.FactoryMetadata;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Geometry;

/**
 * Export selected layer in the context tree.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ExportItem extends TreeMenuItem {

    private static final Image ICON = SwingFXUtils.toFXImage(
            IconBuilder.createImage(FontAwesomeIcons.ICON_DOWNLOAD, 16, FontAwesomeIcons.DEFAULT_COLOR), null);

    private final Map<FileChooser.ExtensionFilter,FileFeatureStoreFactory> index = new HashMap<>();
    private WeakReference<TreeItem> itemRef;

    public ExportItem() {

        menuItem = new Menu(GeotkFX.getString(this,"export"));
        menuItem.setGraphic(new ImageView(ICON));

        //select file factories which support writing
        final Set<FileFeatureStoreFactory> factories = DataStores.getAvailableFactories(FileFeatureStoreFactory.class);
        for(FileFeatureStoreFactory ff : factories){
            final FactoryMetadata metadata = ff.getMetadata();
            if(metadata.supportStoreCreation() && metadata.supportStoreWriting() && metadata.supportedGeometryTypes().length>0){
                final String[] exts = ff.getFileExtensions();
                final String name = ff.getDisplayName().toString();
                final FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(name, exts);
                index.put(filter, ff);

                ((Menu)menuItem).getItems().add(new ExportSub(ff));
            }
        }

    }

    @Override
    public MenuItem init(List<? extends TreeItem> selection) {
        boolean valid = uniqueAndType(selection,FeatureMapLayer.class) && !index.isEmpty();
        if(valid && selection.get(0).getParent()!=null){
            final FeatureMapLayer layer = (FeatureMapLayer) (selection.get(0)).getValue();
            itemRef = new WeakReference<>(selection.get(0));
            return menuItem;
        }
        return null;
    }

    private class ExportSub extends MenuItem{

        private final FileFeatureStoreFactory factory;

        public ExportSub(FileFeatureStoreFactory factory) {
            super(factory.getDisplayName().toString());
            this.factory = factory;


            setOnAction(new EventHandler<javafx.event.ActionEvent>() {
                @Override
                public void handle(javafx.event.ActionEvent event) {
                    if(itemRef == null) return;
                    final TreeItem ti = itemRef.get();
                    if(ti == null) return;
                    final FeatureMapLayer layer = (FeatureMapLayer) ti.getValue();

                    final DirectoryChooser chooser = new DirectoryChooser();
                    chooser.setTitle(GeotkFX.getString(ExportItem.class, "folder"));
                    File folder = chooser.showDialog(null);

                    if(folder!=null){
                        try {
                            final FeatureCollection baseCol = layer.getCollection();
                            final FeatureType baseType = baseCol.getType();
                            final GenericName baseName = baseType.getName();

                            final FactoryMetadata metadata = factory.getMetadata();
                            final Class<Geometry>[] supportedGeometryTypes = metadata.supportedGeometryTypes();

                            //detect if we need one or multiple types.
                            final FeatureCollection[] cols;
                            final AttributeType<?> geomAtt = FeatureExt.castOrUnwrap(FeatureExt.getDefaultGeometry(baseType))
                                    .orElseThrow(() -> new IllegalArgumentException("No geometric property found in layer " + layer.getName()));
                            if(ArraysExt.contains(supportedGeometryTypes,geomAtt.getValueClass()) ){
                                cols = new FeatureCollection[]{baseCol};
                            }else{
                                //split the feature collection in sub geometry types
                                cols = FeatureStoreUtilities.decomposeByGeometryType(baseCol, supportedGeometryTypes);
                            }

                            for(FeatureCollection col : cols){

                                final FeatureType inType = col.getType();
                                final String inTypeName = inType.getName().tip().toString();

                                //output file path
                                final File file= new File(folder, inTypeName+factory.getFileExtensions()[0]);

                                //create output store
                                final FeatureStore store = factory.createDataStore(file.toURI());

                                //create output type
                                store.createFeatureType(inType);
                                final FeatureType outType = store.getFeatureType(inTypeName);
                                final GenericName outName = outType.getName();

                                //write datas
                                final Session session = store.createSession(false);
                                session.addFeatures(outName.toString(), col);

                                //close store
                                store.close();
                            }

                        } catch (DataStoreException ex) {
                            Loggers.DATA.log(Level.WARNING, ex.getMessage(),ex);
                            final Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                            alert.showAndWait();
                        }

                    }
                }
            });


        }




    }

}
