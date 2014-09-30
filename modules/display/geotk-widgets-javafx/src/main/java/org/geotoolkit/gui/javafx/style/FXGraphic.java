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

package org.geotoolkit.gui.javafx.style;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.gui.javafx.util.ButtonTableCell;
import org.geotoolkit.internal.GeotkFX;
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
public class FXGraphic extends FXStyleElementController<FXGraphic, Graphic>{

    @FXML
    protected FXNumberExpression uiSize;
    @FXML
    protected FXNumberExpression uiOpacity;
    @FXML
    protected FXNumberExpression uiRotation;
    @FXML
    protected FXAnchorPoint uiAnchor;
    @FXML
    protected FXDisplacement uiDisplacement;
    @FXML
    protected TableView<GraphicalSymbol> uiTable;
    @FXML
    protected Button uiAddExternal;
    @FXML
    protected Button uiAddMark;
    
    @FXML
    void addMark(ActionEvent event) {
        final GraphicalSymbol m = getStyleFactory().mark(
                    getFilterFactory().literal("circle"),
                    StyleConstants.DEFAULT_FILL,
                    StyleConstants.DEFAULT_STROKE);
        uiTable.getItems().add(m);
    }

    @FXML
    void addExternal(ActionEvent event) {
        try {
            final GraphicalSymbol m = getStyleFactory().externalGraphic(new URL("file:/..."), "image/png");
            uiTable.getItems().add(m);
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

    private void rebuildValue(){
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
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            rebuildValue();
        };
        
        uiOpacity.getNumberField().minValueProperty().set(0);
        uiOpacity.getNumberField().maxValueProperty().set(1);
        uiSize.getNumberField().minValueProperty().set(0);
        
        uiSize.valueProperty().addListener(changeListener);
        uiOpacity.valueProperty().addListener(changeListener);
        uiRotation.valueProperty().addListener(changeListener);
        uiAnchor.valueProperty().addListener(changeListener);
        uiDisplacement.valueProperty().addListener(changeListener);
        uiTable.getItems().addListener((ListChangeListener.Change<? extends GraphicalSymbol> c) -> {
            rebuildValue();
        });
        
        uiTable.setItems(FXCollections.observableArrayList());
        
        final TableColumn<GraphicalSymbol,GraphicalSymbol> previewCol = new TableColumn<>();
        previewCol.setEditable(true);
        previewCol.setPrefWidth(60);
        previewCol.setMinWidth(40);
        previewCol.setCellValueFactory((CellDataFeatures<GraphicalSymbol, GraphicalSymbol> param) -> new SimpleObjectProperty<>((GraphicalSymbol)param.getValue()));
        previewCol.setCellFactory((TableColumn<GraphicalSymbol, GraphicalSymbol> p) -> new GlyphButton());
        
        final TableColumn<GraphicalSymbol,GraphicalSymbol> deleteCol = new TableColumn<>();
        deleteCol.setEditable(true);
        deleteCol.setPrefWidth(30);
        deleteCol.setMinWidth(30);
        deleteCol.setMaxWidth(30);
        deleteCol.setCellValueFactory((CellDataFeatures<GraphicalSymbol, GraphicalSymbol> param) -> new SimpleObjectProperty<>((GraphicalSymbol)param.getValue()));
        deleteCol.setCellFactory((TableColumn<GraphicalSymbol, GraphicalSymbol> p) -> new DeleteButton());
        
        
        uiTable.getColumns().add(previewCol);
        uiTable.getColumns().add(deleteCol);
        uiTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        uiTable.setTableMenuButtonVisible(false);
        
    }
    
    @Override
    protected void updateEditor(Graphic styleElement) {
        uiSize.valueProperty().setValue(styleElement.getSize());
        uiOpacity.valueProperty().setValue(styleElement.getOpacity());
        uiRotation.valueProperty().setValue(styleElement.getRotation());
        uiAnchor.valueProperty().setValue(styleElement.getAnchorPoint());
        uiDisplacement.valueProperty().setValue(styleElement.getDisplacement());
        uiTable.getItems().clear();
        uiTable.getItems().addAll(styleElement.graphicalSymbols());
    }
    
    private static class GlyphButton extends ButtonTableCell<GraphicalSymbol, GraphicalSymbol>{

        public GlyphButton() {
            super(false, null,
                  (GraphicalSymbol t) -> t instanceof GraphicalSymbol,
                  (GraphicalSymbol t) -> openEditor(t));
        }

        @Override
        protected void updateItem(GraphicalSymbol item, boolean empty) {
            super.updateItem(item, empty);
            
            if(item instanceof GraphicalSymbol){
                final Graphic gra = GO2Utilities.STYLE_FACTORY.graphic(
                        Collections.singletonList(item),
                        StyleConstants.DEFAULT_GRAPHIC_OPACITY,
                        GO2Utilities.FILTER_FACTORY.literal(16),
                        StyleConstants.DEFAULT_GRAPHIC_ROTATION,
                        StyleConstants.DEFAULT_ANCHOR_POINT,
                        StyleConstants.DEFAULT_DISPLACEMENT);
                final PointSymbolizer ps = GO2Utilities.STYLE_FACTORY.pointSymbolizer(gra, null);
                final BufferedImage img = DefaultGlyphService.create(ps, new Dimension(18, 18), null);
                button.setGraphic(new ImageView(SwingFXUtils.toFXImage(img,null)));
                
                if (item instanceof Mark) {
                    final Mark m = (Mark) item;
                    button.setText(m.getWellKnownName().toString());
                } else if (item instanceof ExternalGraphic) {
                    final ExternalGraphic m = (ExternalGraphic) item;
                    final OnlineResource res = m.getOnlineResource();
                    if(res != null && res.getLinkage() != null){
                        button.setText(res.getName());
                    }else{
                        button.setText("");
                    }
                }
            }
            
        }
        
        private static void openEditor(GraphicalSymbol t){
            System.out.println("todo");
        }
    }

    private class DeleteButton extends ButtonTableCell<GraphicalSymbol, GraphicalSymbol>{

        public DeleteButton() {
            super(false, new ImageView(GeotkFX.ICON_DELETE),
                   //JavaFX bug : do not use lambda here : java.lang.VerifyError: Bad type on operand stack->invokedynamic
                  (GraphicalSymbol t) -> t instanceof GraphicalSymbol, new Consumer<GraphicalSymbol>() {
                public void accept(GraphicalSymbol t) {
                    uiTable.getItems().remove(t);
                }
            });
        }
    }
    
    
}
