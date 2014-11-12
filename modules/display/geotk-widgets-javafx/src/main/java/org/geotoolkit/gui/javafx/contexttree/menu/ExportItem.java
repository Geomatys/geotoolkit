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
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.map.FeatureMapLayer;

/**
 * Export selected layer in the context tree.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ExportItem extends TreeMenuItem {

    private static final Image ICON = SwingFXUtils.toFXImage(
            IconBuilder.createImage(FontAwesomeIcons.ICON_DOWNLOAD, 16, FontAwesomeIcons.DEFAULT_COLOR), null);
    
    private final boolean hasExportAvailable;
    private WeakReference<TreeItem> itemRef;
    
    public ExportItem() {
        
        final Set<FileFeatureStoreFactory> factories = FeatureStoreFinder.getAvailableFactories(FileFeatureStoreFactory.class);
        final Map<FileChooser.ExtensionFilter,FileFeatureStoreFactory> index = new HashMap<>();
        
        for(FileFeatureStoreFactory ff : factories){
            final String[] exts = ff.getFileExtensions();
            final String name = ff.getDisplayName().toString();
            final FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(name, exts);
            index.put(filter, ff);
        }
        hasExportAvailable = !index.isEmpty();
        
        
        item = new MenuItem(GeotkFX.getString(this,"export"));
        item.setGraphic(new ImageView(ICON));

        item.setOnAction(new EventHandler<javafx.event.ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent event) {
                if(itemRef == null) return;                
                final TreeItem ti = itemRef.get();
                if(ti == null) return;
                final FeatureMapLayer layer = (FeatureMapLayer) ti.getValue();
                                
                final FileChooser chooser = new FileChooser();
                chooser.getExtensionFilters().addAll(index.keySet());
                chooser.setSelectedExtensionFilter(index.keySet().iterator().next());
                File file = chooser.showSaveDialog(null);
                
                if(file!=null){
                    //get factory
                    final FileChooser.ExtensionFilter filter = chooser.getSelectedExtensionFilter();
                    final FileFeatureStoreFactory factory = index.get(filter);
                    
                    try {
                        //ensure extension is valid
                        file = (File) IOUtilities.changeExtension(file, filter.getExtensions().get(0));
                    
                        //create output store
                        FeatureType type = layer.getCollection().getFeatureType();
                        Name name = type.getName();
                        
                        final FeatureStore store = factory.createDataStore(file.toURI().toURL());
                        
                        //create output type
                        store.createFeatureType(DefaultName.valueOf(name.getLocalPart()), type);
                        type = store.getFeatureType(name.getLocalPart());
                        name = type.getName();
                        
                        //write datas
                        final Session session = store.createSession(false);
                        session.addFeatures(name, layer.getCollection());
                        
                        //close store
                        store.close();
                        
                    } catch (MalformedURLException | DataStoreException ex) {
                        Loggers.DATA.log(Level.WARNING, ex.getMessage(),ex);
                        final Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                        alert.showAndWait();
                    }
                    
                }
            }
        });
    }

    @Override
    public MenuItem init(List<? extends TreeItem> selection) {
        boolean valid = uniqueAndType(selection,FeatureMapLayer.class);
        if(valid && selection.get(0).getParent()!=null){
            final FeatureMapLayer layer = (FeatureMapLayer) (selection.get(0)).getValue();
            itemRef = new WeakReference<>(selection.get(0));
            return item;
        }
        return null;
    }
    
    
    
}
