/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.gui.javafx.util.FXUtilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXResourceChooser extends BorderPane{

    private final TreeTableView<Resource> treeTable = new TreeTableView<>();
    private Resource resource = null;

    public FXResourceChooser() {
        treeTable.getColumns().add(new ResourceNameColumn());
        setCenter(treeTable);
        treeTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treeTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        FXUtilities.hideTableHeader(treeTable);
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
        updateTree();
    }

    public List<Resource> getSelected() {
        final List<Resource> resources = new ArrayList<>();
        final ObservableList<TreeItem<Resource>> selectedItems = treeTable.getSelectionModel().getSelectedItems();
        for (TreeItem<Resource> item : selectedItems) {
            resources.add(item.getValue());
        }
        return resources;
    }

    public void setSelected(List<Resource> resources) {
        TreeItem<Resource> root = treeTable.getRoot();
        if (root == null) return;

        final List<ResourceItem> selected = new ArrayList<>();
        select((ResourceItem) root, resources, selected);
        treeTable.getSelectionModel().clearSelection();
//        treeTable.getSelectionModel().getSelectedItems().setAll(selected);
    }

    private void select(ResourceItem root, List<Resource> resources, List<ResourceItem> selected) {
        if (resources.contains(root.resource)) {
            treeTable.getSelectionModel().select(root);
            selected.add(root);
        }
        for (TreeItem<Resource> r : root.getChildren()) {
            select((ResourceItem) r, resources, selected);
        }
    }

    private void updateTree() {
        if (resource==null) {
            treeTable.setRoot(null);
        } else {
            treeTable.setRoot(new ResourceItem(resource));
        }
    }

    static class ResourceItem extends TreeItem<Resource> {

        private final Resource resource;
        private boolean isFirstTimeChildren = true;

        public ResourceItem(Resource res) {
            super(res);
            this.resource = res;
            setExpanded(true);
        }

        public ObservableList<TreeItem<Resource>> getChildren() {
            if (isFirstTimeChildren) {
                isFirstTimeChildren = false;
                super.getChildren().setAll(buildChildren());
            }
            return super.getChildren();
        }

        @Override
        public boolean isLeaf() {
            return !(resource instanceof Aggregate);
        }

        private List<TreeItem<Resource>> buildChildren() {
            if (resource instanceof Aggregate) {
                final List<TreeItem<Resource>> lst = new ArrayList<>();
                try {
                    for (Resource res : ((Aggregate)resource).components()) {
                        lst.add(new ResourceItem(res));
                    }
                } catch (DataStoreException ex) {
                    ex.printStackTrace();
                }
                return lst;
            }
            return Collections.EMPTY_LIST;
        }

    }

}
