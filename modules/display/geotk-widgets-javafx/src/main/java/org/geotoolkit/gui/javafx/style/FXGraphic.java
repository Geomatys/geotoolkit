/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2015, Geomatys
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

package org.geotoolkit.gui.javafx.style;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.gui.javafx.util.FXDeleteTableColumn;
import org.geotoolkit.gui.javafx.util.FXEditTableColumn;
import org.geotoolkit.gui.javafx.util.FXMoveDownTableColumn;
import org.geotoolkit.gui.javafx.util.FXMoveUpTableColumn;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXGraphic extends FXStyleElementController<Graphic>{

    @FXML protected FXNumberExpression uiSize;
    @FXML protected FXNumberExpression uiOpacity;
    @FXML protected FXNumberExpression uiRotation;
    @FXML protected FXAnchorPoint uiAnchor;
    @FXML protected FXDisplacement uiDisplacement;
    @FXML protected Button uiAddMark;
    @FXML protected Button uiAddExternal;
    @FXML protected TableView<GraphicalSymbol> uiTable;
    
    @FXML
    void addMark(ActionEvent event) {
        final GraphicalSymbol mark = getStyleFactory().mark(
                    getFilterFactory().literal("circle"),
                    StyleConstants.DEFAULT_FILL,
                    StyleConstants.DEFAULT_STROKE);
        uiTable.getItems().add(mark);
    }

    @FXML
    void addExternal(ActionEvent event) {
        try {
            final GraphicalSymbol external = getStyleFactory().externalGraphic(new URL("file:/..."), "image/png");
            uiTable.getItems().add(external);
        } catch (MalformedURLException ex) {
            //won't happen
            throw new RuntimeException(ex.getMessage(),ex);
        }
    }
    
    @Override
    public Class<Graphic> getEditedClass() {
        return Graphic.class;
    }

    @Override
    public Graphic newValue() {
        return getStyleFactory().graphic();
    }

    private void resetValue(){
        if(updating) return;
        value.set(getStyleFactory().graphic(
                new ArrayList<GraphicalSymbol>(uiTable.getItems()), 
                uiOpacity.valueProperty().get(), 
                uiSize.valueProperty().get(), 
                uiRotation.valueProperty().get(), 
                uiAnchor.valueProperty().get(), 
                uiDisplacement.valueProperty().get()));
    }
    
    @Override
    public void initialize() {
        super.initialize();
        FXUtilities.hideTableHeader(uiTable);
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            resetValue();
        };

        uiOpacity.getEditor().getSpinner().setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1, 1, 0.1));
        uiSize.getEditor().getSpinner().setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 16, 1));
        
        uiSize.valueProperty().addListener(changeListener);
        uiOpacity.valueProperty().addListener(changeListener);
        uiRotation.valueProperty().addListener(changeListener);
        uiAnchor.valueProperty().addListener(changeListener);
        uiDisplacement.valueProperty().addListener(changeListener);
        
        uiTable.setItems(FXCollections.observableArrayList());
        
        final TableColumn<GraphicalSymbol, GraphicalSymbol> previewCol = new TableColumn<>();
        previewCol.setEditable(true);
        previewCol.setPrefWidth(60);
        previewCol.setMinWidth(40);
        previewCol.setCellValueFactory((CellDataFeatures<GraphicalSymbol, GraphicalSymbol> param) -> new SimpleObjectProperty<>((GraphicalSymbol) param.getValue()));
        previewCol.setCellFactory((TableColumn<GraphicalSymbol, GraphicalSymbol> p) -> new GlyphButton());
        
        
        uiTable.getColumns().add(previewCol);
        uiTable.getColumns().add(new FXEditTableColumn((Object o) -> openGraphicalSymbolEditor((GraphicalSymbol)o)));
        uiTable.getColumns().add(new FXMoveUpTableColumn());
        uiTable.getColumns().add(new FXMoveDownTableColumn());
        uiTable.getColumns().add(new FXDeleteTableColumn(false));
        uiTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        uiTable.setTableMenuButtonVisible(false);


        FXStyleElementController.configureAdvancedProperty(uiAnchor);
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiSize.setLayer(layer);
        uiOpacity.setLayer(layer);
        uiRotation.setLayer(layer);
        uiAnchor.setLayer(layer);
        uiDisplacement.setLayer(layer);
    }
    
    @Override
    protected void updateEditor(Graphic graphic) {
        uiSize.valueProperty().setValue(graphic.getSize());
        uiOpacity.valueProperty().setValue(graphic.getOpacity());
        uiRotation.valueProperty().setValue(graphic.getRotation());
        uiAnchor.valueProperty().setValue(graphic.getAnchorPoint());
        uiDisplacement.valueProperty().setValue(graphic.getDisplacement());
        uiTable.getItems().clear();
        uiTable.getItems().addAll(graphic.graphicalSymbols());
    }
        
    private void openGraphicalSymbolEditor(final GraphicalSymbol graphicalSymbol){
        final FXStyleElementController graphicalSymbolEditor = FXStyleElementEditor.findEditor(graphicalSymbol);
        graphicalSymbolEditor.valueProperty().setValue(graphicalSymbol);
        graphicalSymbolEditor.setLayer(layer);
        uiTable.getSelectionModel().select(graphicalSymbol);
                
        graphicalSymbolEditor.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                uiTable.getItems().replaceAll((GraphicalSymbol t) -> {
                    if(t==oldValue) return (GraphicalSymbol) newValue;
                    else return t;
                });
                resetValue();
            }
        });

        final DialogPane pane = new DialogPane();
        pane.setContent(graphicalSymbolEditor);
        pane.getButtonTypes().add(ButtonType.CLOSE);

        final Dialog dialog = new Dialog();
        dialog.setDialogPane(pane);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(true);
        dialog.show();

    }
    
    
    private class GlyphButton extends TableCell<GraphicalSymbol, GraphicalSymbol> {

        @Override
        protected void updateItem(GraphicalSymbol item, boolean empty) {
            super.updateItem(item, empty);
            
            updateGlyph(item);

            setText("");
            if (item instanceof Mark) {
                final Mark m = (Mark) item;
                setText(m.getWellKnownName().toString());
            } else if (item instanceof ExternalGraphic) {
                final ExternalGraphic m = (ExternalGraphic) item;
                final OnlineResource res = m.getOnlineResource();
                if(res != null && res.getLinkage() != null){
                    setText(String.valueOf(res.getName()));
                }
            }
        }
        
        private void updateGlyph(final GraphicalSymbol graphicalSymbol){
            if(graphicalSymbol==null){
                setGraphic(null);
                return;
            }

            final Graphic graphic = GO2Utilities.STYLE_FACTORY.graphic(
                        Collections.singletonList(graphicalSymbol),
                        StyleConstants.DEFAULT_GRAPHIC_OPACITY,
                        GO2Utilities.FILTER_FACTORY.literal(16),
                        StyleConstants.DEFAULT_GRAPHIC_ROTATION,
                        StyleConstants.DEFAULT_ANCHOR_POINT,
                        StyleConstants.DEFAULT_DISPLACEMENT);
            final PointSymbolizer ps = GO2Utilities.STYLE_FACTORY.pointSymbolizer(graphic, null);
            final BufferedImage img = DefaultGlyphService.create(ps, new Dimension(18, 18), null);
            setGraphic(new ImageView(SwingFXUtils.toFXImage(img, null)));
        }
    }

}
