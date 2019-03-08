/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
import java.util.List;
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
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 * Export selected layer.
 *
 * The menu item is active when a single coverage layer is selected.
 * The output format is limited to Geotiff.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ExportCoverageItem extends TreeMenuItem {

    private static final Image ICON = SwingFXUtils.toFXImage(
            IconBuilder.createImage(FontAwesomeIcons.ICON_DOWNLOAD, 16, FontAwesomeIcons.DEFAULT_COLOR), null);

    private WeakReference<TreeItem> itemRef;

    public ExportCoverageItem() {
        menuItem = new Menu(GeotkFX.getString(ExportFeatureSetItem.class,"export"));
        menuItem.setGraphic(new ImageView(ICON));
        ((Menu)menuItem).getItems().add(new ExportSub());
    }

    @Override
    public MenuItem init(List<? extends TreeItem> selection) {
        boolean valid = uniqueAndType(selection,CoverageMapLayer.class);
        if(valid && selection.get(0).getParent()!=null){
            itemRef = new WeakReference<>(selection.get(0));
            return menuItem;
        }
        return null;
    }

    private class ExportSub extends MenuItem{

        public ExportSub() {
            super("Geotiff");


            setOnAction(new EventHandler<javafx.event.ActionEvent>() {
                @Override
                public void handle(javafx.event.ActionEvent event) {
                    if(itemRef == null) return;
                    final TreeItem ti = itemRef.get();
                    if(ti == null) return;
                    final CoverageMapLayer layer = (CoverageMapLayer) ti.getValue();

                    final DirectoryChooser chooser = new DirectoryChooser();
                    chooser.setTitle(GeotkFX.getString(ExportFeatureSetItem.class, "folder"));
                    final File folder = chooser.showDialog(null);
                    final GridCoverageResource base = layer.getResource();

                    if (folder != null) {

                        GridCoverageReader reader = null;
                        ImageCoverageWriter writer = null;
                        try {
                            final FeatureType baseType = base.getType();
                            final GenericName baseName = baseType.getName();

                            reader = base.acquireReader();
                            final GridCoverage coverage = reader.read(null);
                            base.recycle(reader);
                            reader = null;


                            final GridCoverageWriteParam writeParam = new GridCoverageWriteParam();
                            writeParam.setFormatName("geotiff");

                            writer = new ImageCoverageWriter();
                            writer.setOutput(folder.toPath().resolve(baseName+".tiff"));
                            writer.write(coverage, writeParam);

                        } catch (DataStoreException ex) {
                            Loggers.DATA.log(Level.WARNING, ex.getMessage(),ex);
                            final Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                            alert.showAndWait();
                        } finally {
                            if (reader != null) {
                                try {
                                    reader.dispose();
                                } catch (DataStoreException ex) {
                                    Loggers.DATA.log(Level.WARNING, ex.getMessage(),ex);
                                    final Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                                    alert.showAndWait();
                                }
                            }
                            if (writer != null) {
                                try {
                                    writer.dispose();
                                } catch (DataStoreException ex) {
                                    Loggers.DATA.log(Level.WARNING, ex.getMessage(),ex);
                                    final Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                                    alert.showAndWait();
                                }
                            }
                        }

                    }
                }
            });

        }

    }

}
