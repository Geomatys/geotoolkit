/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.gui.javafx.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.ProcessingRegistry;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXProcessRegistryPane extends BorderPane {

    private static final Image ICON_PROCESS  = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_COG,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    private static final Image ICON_REGISTRY = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_COGS,16,FontAwesomeIcons.DEFAULT_COLOR),null);

    private static final Comparator<ProcessingRegistry> REGISTRY_COMPARATOR = new Comparator<ProcessingRegistry>() {
            @Override
            public int compare(ProcessingRegistry o1, ProcessingRegistry o2) {
                String name1 = o1.getIdentification().getCitation().getIdentifiers().iterator().next().getCode();
                String name2 = o2.getIdentification().getCitation().getIdentifiers().iterator().next().getCode();
                return name1.compareTo(name2);
            }
        };

    private static final Comparator<ProcessDescriptor> PROCESS_COMPARATOR = new Comparator<ProcessDescriptor>() {
            @Override
            public int compare(ProcessDescriptor o1, ProcessDescriptor o2) {
                String name1 = o1.getIdentifier().getCode();
                String name2 = o2.getIdentifier().getCode();
                return name1.compareTo(name2);
            }
        };

    private final SimpleObjectProperty<ProcessDescriptor> processProperty = new SimpleObjectProperty<>();
    private final List<ProcessingRegistry> registries = new ArrayList();
    private final TreeView<Object> uiTree;

    public FXProcessRegistryPane() {

        uiTree = new TreeView();
        uiTree.setShowRoot(false);
        uiTree.setCellFactory((TreeView<Object> param) -> new ClickFCell());
        uiTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        uiTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Object>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Object>> observable, TreeItem<Object> oldValue, TreeItem<Object> newValue) {
                if (newValue == null) {
                    processProperty.setValue(null);
                } else {
                    Object obj = newValue.getValue();
                    if (obj instanceof ProcessDescriptor) {
                        processProperty.setValue((ProcessDescriptor) obj);
                    } else {
                        processProperty.setValue(null);
                    }
                }
            }
        });

        ProcessFinder.getProcessFactories().forEachRemaining(registries::add);

        setCenter(uiTree);
    }

    public Property<ProcessDescriptor> valueProperty() {
        return processProperty;
    }

    public void setProcessingRegistry(Collection<? extends ProcessingRegistry> registries) {
        this.registries.clear();
        this.registries.addAll(registries);

        final TreeItem<Object> root = new TreeItem<>("root");
        Collections.sort(this.registries, REGISTRY_COMPARATOR);

        for (ProcessingRegistry registry : registries) {
            final TreeItem fnode = new TreeItem(registry);

            final List<ProcessDescriptor> lst = new ArrayList(registry.getDescriptors());
            Collections.sort(lst, PROCESS_COMPARATOR);
            for (ProcessDescriptor pd : lst) {
                final TreeItem pnode = new TreeItem(pd);
                fnode.getChildren().add(pnode);
            }
            root.getChildren().add(fnode);
            fnode.setExpanded(true);
        }

        uiTree.setRoot(root);
        uiTree.getRoot().setExpanded(true);
    }

    private class ClickFCell extends TreeCell<Object> {

        public ClickFCell() {
        }

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if (item instanceof ProcessingRegistry) {
                ProcessingRegistry registry = (ProcessingRegistry) item;
                setText(registry.getIdentification().getCitation().getIdentifiers().iterator().next().getCode());
                setGraphic(new ImageView(ICON_REGISTRY));
            } else if (item instanceof ProcessDescriptor) {
                final ProcessDescriptor desc = (ProcessDescriptor) item;
                setText(desc.getIdentifier().getCode());
                setGraphic(new ImageView(ICON_PROCESS));
            } else {
                setGraphic(null);
                setText("");
            }
        }

    }
}
