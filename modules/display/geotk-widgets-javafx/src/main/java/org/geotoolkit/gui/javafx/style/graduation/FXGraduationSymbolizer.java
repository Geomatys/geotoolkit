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
package org.geotoolkit.gui.javafx.style.graduation;

import java.util.List;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import org.geotoolkit.display2d.ext.graduation.GraduationSymbolizer;
import org.geotoolkit.display2d.ext.graduation.GraduationSymbolizer.Graduation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.geotoolkit.gui.javafx.style.FXStyleElementController;
import org.geotoolkit.gui.javafx.util.ButtonTableCell;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXGraduationSymbolizer extends FXStyleElementController<GraduationSymbolizer> {

    @FXML
    private TableView<Graduation> uiTable;
    @FXML
    private SplitPane uiSplit;

    private FXGraduation uiGraduation;
    private boolean updatingSubEditor = false;

    @FXML
    void addGraduation(ActionEvent event) {
        final Graduation graduation = new Graduation();
        uiTable.getItems().add(graduation);
    }

    @Override
    public Class<GraduationSymbolizer> getEditedClass() {
        return GraduationSymbolizer.class;
    }

    @Override
    public GraduationSymbolizer newValue() {
        return new GraduationSymbolizer();
    }

    @Override
    public void initialize() {
        super.initialize();

        uiGraduation = new FXGraduation();
        uiGraduation.visibleProperty().bind(uiTable.getSelectionModel().selectedItemProperty().isNotNull());
        uiGraduation.managedProperty().bind(uiGraduation.visibleProperty());

        final ScrollPane scrollPane = new ScrollPane(uiGraduation);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        uiSplit.getItems().add(scrollPane);

        uiTable.setEditable(true);
        uiTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        uiTable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Graduation>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Graduation> c) {
                if(updatingSubEditor) return;
                final Graduation graduation = uiTable.getSelectionModel().getSelectedItem();
                if(graduation!=null){
                    uiGraduation.valueProperty().set(graduation);
                }
            }
        });
        FXUtilities.hideTableHeader(uiTable);

        uiGraduation.valueProperty().addListener(new ChangeListener<Graduation>() {
            @Override
            public void changed(ObservableValue<? extends Graduation> observable, Graduation oldValue, Graduation newValue) {
                if(updating) return;
                final List<TablePosition> cells = uiTable.getSelectionModel().getSelectedCells();
                final int index = cells.isEmpty() ? -1 : cells.get(0).getRow();
                if(index>=0 && newValue!=null){
                    updatingSubEditor = true;
                    uiTable.getItems().set(index, newValue);
                    uiTable.getSelectionModel().clearAndSelect(index);
                    updatingSubEditor = false;
                }
            }
        });

        final TableColumn<Graduation,String> nameCol = new TableColumn<>();
        nameCol.setEditable(false);
        nameCol.setMinWidth(100);
        nameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Graduation, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Graduation, String> param) {
                final Graduation graduation = param.getValue();
                return new SimpleObjectProperty<>(String.valueOf(graduation.getStep()));
            }
        });
        final TableColumn<Graduation,Graduation> deleteCol = new TableColumn<>();
        deleteCol.setEditable(true);
        deleteCol.setPrefWidth(30);
        deleteCol.setMinWidth(30);
        deleteCol.setMaxWidth(30);
        deleteCol.setCellValueFactory((TableColumn.CellDataFeatures<Graduation, Graduation> param) -> new SimpleObjectProperty<>((Graduation)param.getValue()));
        deleteCol.setCellFactory((TableColumn<Graduation, Graduation> p) -> new DeleteButton());


        uiTable.getColumns().add(nameCol);
        uiTable.getColumns().add(deleteCol);
        uiTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        uiTable.setTableMenuButtonVisible(false);
    }

    @Override
    protected void updateEditor(GraduationSymbolizer styleElement) {
        uiTable.getSelectionModel().clearSelection();
        uiTable.setItems(FXCollections.observableArrayList(styleElement.getGraduations()));
        uiTable.getItems().addListener(new ListChangeListener<Graduation>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Graduation> c) {
                if(updating) return;
                final GraduationSymbolizer symbolizer = new GraduationSymbolizer();
                symbolizer.getGraduations().addAll(uiTable.getItems());
                value.set(symbolizer);
            }
        });
    }

    private class DeleteButton extends ButtonTableCell<Graduation, Graduation>{

        public DeleteButton() {
            super(false, new ImageView(GeotkFX.ICON_DELETE), null,
                   //JavaFX bug : do not use lambda here : java.lang.VerifyError: Bad type on operand stack->invokedynamic
                  new Function<Graduation,Graduation>() {
                    public Graduation apply(Graduation t) {
                        uiTable.getItems().remove(t);
                        return t;
                    }
                });
        }
    }

}
