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
package org.geotoolkit.gui.javafx.contexttree;

import java.util.Collections;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMapContextTree extends BorderPane {

    private static final double BORDER_DELTA = 4.0;

    private static final DataFormat MAPITEM_FORMAT = new DataFormat("contextItem");

    /**
     * Flag indicating that a node as been dragged to the bottom of target node.
     */
    public static final int DRAGGED_BELOW = -1;
    /**
     * Indicates source node has been dragged into target node.
     */
    public static final int DRAGGED_INTO = 0;
    /**
     * Indicates that source node as been dragged to the upper bound of target
     * node.
     */
    public static final int DRAGGED_ABOVE = 1;

    /**
     * CSS Style class applied when a node is dragged on the top border of
     * another one.
     */
    public static final String ABOVE_CSS = "dragged-above";

    /**
     * CSS Style class applied when a node is dragged on the bottom border of
     * another one.
     */
    public static final String BELOW_CSS = "dragged-below";

    /**
     * CSS Style class applied when a node is dragged over another one.
     */
    public static final String OVER_CSS = "dragged-over";

    private final ObservableList<Object> menuItems = FXCollections.observableArrayList();
    private final TreeTableView<MapItem> treetable = new TreeTableView();
    private final ObjectProperty<MapContext> itemProperty = new SimpleObjectProperty<>();

    public FXMapContextTree() {
        this(null);
    }

    public FXMapContextTree(MapContext item) {
        setCenter(treetable);

        //configure treetable
        treetable.getColumns().add(new MapItemNameColumn());
        treetable.getColumns().add(new MapItemGlyphColumn());
        treetable.getColumns().add(new MapItemVisibleColumn());
        treetable.setTableMenuButtonVisible(false);
        treetable.setEditable(true);
        treetable.setContextMenu(new ContextMenu());
        treetable.setPlaceholder(new Label(""));

        treetable.setRowFactory(new Callback<TreeTableView<MapItem>, TreeTableRow<MapItem>>() {
            @Override
            public TreeTableRow<MapItem> call(TreeTableView<MapItem> param) {
                final TreeTableRow row = new TreeTableRow();
                initDragAndDrop(row);
                return row;
            }
        });
        treetable.getStylesheets().add("org/geotoolkit/gui/javafx/parameter/parameters.css");

        //this will cause the column width to fit the view area
        treetable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        final ContextMenu menu = new ContextMenu();
        treetable.setContextMenu(menu);

        //update context menu based on selected items
        treetable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treetable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                final ObservableList items = menu.getItems();
                items.clear();
                final List<? extends TreeItem> selection = FXUtilities.getSelectionItems(treetable);
                for (int i = 0, n = menuItems.size(); i < n; i++) {
                    final Object candidate = menuItems.get(i);
                    if (candidate instanceof TreeMenuItem) {
                        final MenuItem mc = ((TreeMenuItem) candidate).init(selection);
                        if (mc != null)
                            items.add(mc);
                    } else if (candidate instanceof SeparatorMenuItem) {
                        //special case, we don't want any separator at the start or end
                        //or 2 succesive separators
                        if (i == 0 || i == n - 1 || items.isEmpty())
                            continue;

                        if (items.get(items.size() - 1) instanceof SeparatorMenuItem) {
                            continue;
                        }
                        items.add((SeparatorMenuItem) candidate);

                    } else if (candidate instanceof MenuItem) {
                        items.add((MenuItem) candidate);
                    }
                }

                //special case, we don't want any separator at the start or end
                if (!items.isEmpty()) {
                    if (items.get(0) instanceof SeparatorMenuItem) {
                        items.remove(0);
                    }
                    if (!items.isEmpty()) {
                        final int idx = items.size() - 1;
                        if (items.get(idx) instanceof SeparatorMenuItem) {
                            items.remove(idx);
                        }
                    }
                }

            }
        });

        treetable.setShowRoot(true);
        itemProperty.addListener(new ChangeListener<MapItem>() {
            @Override
            public void changed(ObservableValue<? extends MapItem> observable, MapItem oldValue, MapItem newValue) {
                if (newValue == null) {
                    treetable.setRoot(null);
                } else {
                    treetable.setRoot(new TreeMapItem(newValue));
                }
            }
        });

        setMapItem(item);
    }

    /**
     * Configure ability to move a row into the tree view.
     *
     * @param row to configure.
     */
    private void initDragAndDrop(final TreeTableRow<MapItem> row) {
        row.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                final int selection = treetable.getSelectionModel().getSelectedIndex();
                final Dragboard db = treetable.startDragAndDrop(TransferMode.MOVE);
                db.setContent(Collections.singletonMap(MAPITEM_FORMAT, selection));
            }
        });

        row.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getDragboard().hasContent(MAPITEM_FORMAT)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }

                // Does not impact empty row style.
                if (row.isEmpty() || row.getItem() == null) {
                    return;
                }

                row.getStyleClass().removeAll(ABOVE_CSS, BELOW_CSS, OVER_CSS);
                switch (getDragPosition(event.getPickResult())) {
                    case DRAGGED_ABOVE:
                        row.getStyleClass().add(ABOVE_CSS);
                        break;
                    case DRAGGED_BELOW:
                        row.getStyleClass().add(BELOW_CSS);
                        break;
                    default:
                        row.getStyleClass().add(OVER_CSS);
                }
            }
        });

        row.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                row.getStyleClass().removeAll(ABOVE_CSS, BELOW_CSS, OVER_CSS);

            }
        });

        row.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                final Dragboard db = event.getDragboard();
                boolean success = false;

                conditions:
                if (db.hasContent(MAPITEM_FORMAT)) {
                    final int index = (Integer) db.getContent(MAPITEM_FORMAT);
                    if (index >= 0) {
                        final MapItem root = treetable.getRoot() == null ? null : treetable.getRoot().getValue();
                        if (root == null)
                            break conditions;

                        ObservableList<TreeItem<MapItem>> movedRows = treetable.getSelectionModel().getSelectedItems();
                        final TreeItem<MapItem> targetRow = row.getTreeItem();

                        // Prevent moving a row in itself
                        if (targetRow != null) {
                            movedRows = movedRows.filtered(toMove -> !FXUtilities.isParent(toMove, targetRow));
                        }
                        if (movedRows.isEmpty())
                            break conditions;

                        final MapItem targetItem = row.getItem();
                        final MapItem targetParent = (targetRow == null || targetRow.getParent() == null ? null : targetRow.getParent().getValue());

                        final int dragPosition = getDragPosition(event.getPickResult());
                        for (final TreeItem<MapItem> movedRow : movedRows) {
                            final MapItem movedItem = movedRow.getValue();
                            final MapItem movedParent = (movedRow.getParent() == null ? null : movedRow.getParent().getValue());
                            // Root or null item dragged. Cannot move them.
                            if (movedItem == null || movedParent == null) {
                                continue;
                            }

                            movedParent.items().remove(movedItem);
                            if (targetItem == null) {
                                // Insert in empty row, should be at end of the tree.
                                root.items().add(0, movedItem);
                            } else if (targetParent == null) {
                                // Add directly on root.
                                root.items().add(movedItem);
                            } else {
                                switch (dragPosition) {
                                    case DRAGGED_ABOVE:
                                        targetParent.items().add(targetParent.items().indexOf(targetItem) + 1, movedItem);
                                        break;
                                    case DRAGGED_BELOW:
                                        targetParent.items().add(targetParent.items().indexOf(targetItem), movedItem);
                                        break;
                                    default:
                                        if (targetItem instanceof MapLayer) {
                                            //insert as sibling
                                            final int insertIndex = targetParent.items().indexOf(targetItem);
                                            targetParent.items().add(insertIndex, movedItem);
                                        } else {
                                            //insert as children
                                            targetItem.items().add(movedItem);
                                        }
                                }
                            }
                        }
                    }
                    success = true;
                }

                // Clear selection to avoid random index selection on tree update.
                treetable.getSelectionModel().clearSelection();

                event.setDropCompleted(success);
            }
        });
    }

    /**
     * Analyze if cursor is positionned on the upper or lower bound of target
     * node.
     *
     * @param pick Result of proceed picking.
     * @return {@link #DRAGGED_ABOVE} if picking is located on upper target
     * bound, {@link #DRAGGED_BELOW} if it's on lower bound. Otherwise,
     * {@link #DRAGGED_INTO} is sent back.
     */
    private static int getDragPosition(final PickResult pick) {
        final Bounds targetBounds = pick.getIntersectedNode().getBoundsInLocal();
        final Point3D intersectedPoint = pick.getIntersectedPoint();
        if (intersectedPoint.getY() <= targetBounds.getMinY() + BORDER_DELTA) {
            return DRAGGED_ABOVE;
        } else if (intersectedPoint.getY() >= targetBounds.getMaxY() - BORDER_DELTA) {
            return DRAGGED_BELOW;
        } else {
            return DRAGGED_INTO;
        }
    }

    public TreeTableView getTreetable() {
        return treetable;
    }

    /**
     * This list can contain MenuItem of TreeMenuItem.
     *
     * @return ObservableList of contextual menu items.
     */
    public ObservableList<Object> getMenuItems() {
        return menuItems;
    }

    public ObjectProperty<MapContext> mapItemProperty() {
        return itemProperty;
    }

    public MapContext getMapItem() {
        return itemProperty.get();
    }

    public void setMapItem(MapContext mapItem) {
        itemProperty.set(mapItem);
    }

}
