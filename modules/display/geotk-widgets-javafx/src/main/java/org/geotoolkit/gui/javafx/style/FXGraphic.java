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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.gui.javafx.util.ButtonTableCell;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.StyleListener;
import org.geotoolkit.util.collection.CollectionChangeEvent;
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

    @FXML protected FXNumberExpression uiSize;
    @FXML protected FXNumberExpression uiOpacity;
    @FXML protected FXNumberExpression uiRotation;
    @FXML protected FXAnchorPoint uiAnchor;
    @FXML protected FXDisplacement uiDisplacement;
    @FXML protected Button uiAddMark;
    @FXML protected Button uiAddExternal;
    @FXML protected TableView<GraphicalSymbol> uiTable;
    @FXML protected Pane uiGraphicalSymbol;
    private FXStyleElementController graphicalSymbolEditor = null;
    
    @FXML
    void addMark(ActionEvent event) {
        final GraphicalSymbol mark = getStyleFactory().mark(
                    getFilterFactory().literal("circle"),
                    StyleConstants.DEFAULT_FILL,
                    StyleConstants.DEFAULT_STROKE);
        uiTable.getItems().add(mark);
        rebuildValue();
    }

    @FXML
    void addExternal(ActionEvent event) {
        try {
            final GraphicalSymbol external = getStyleFactory().externalGraphic(new URL("file:/..."), "image/png");
            uiTable.getItems().add(external);
            rebuildValue();
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
        
        
        uiTable.getSelectionModel().getSelectedCells().addListener(new ListChangeListener<TablePosition>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends TablePosition> c) {
//                uiSymbolizerPane.setContent(null);
//                
                for(final TablePosition tablePosition : uiTable.getSelectionModel().getSelectedCells()){
                    
                    final GraphicalSymbol symbol = uiTable.getItems().get(tablePosition.getRow());
//                    editor = FXStyleElementEditor.findEditor(symbol);
                    if(graphicalSymbolEditor != null){
//                        editor.setLayer(layer);
//                        editor.valueProperty().setValue(symbol);
//
//                        //listen to editor change
                        graphicalSymbolEditor.valueProperty().addListener(new ChangeListener() {
                            @Override
                            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                                //apply editor
                                final int index = uiTable.getSelectionModel().getSelectedIndex();
                                System.out.println("Changed ! :) ");
                                if(index>=0){
                                    System.out.println("INDEX : "+index);
                                    System.out.println(((Mark) symbol).getFill().getColor().toString());
                                    System.out.println(((Mark) symbol).getStroke().getColor().toString());
                                    System.out.println(((Mark) graphicalSymbolEditor.valueProperty().get()).getFill().getColor().toString());
                                    System.out.println(((Mark) graphicalSymbolEditor.valueProperty().get()).getStroke().getColor().toString());
                                    uiTable.getItems().set(index, (GraphicalSymbol) graphicalSymbolEditor.valueProperty().get());
                                }
                            }
                        });
//                        uiSymbolizerPane.setContent(editor);
                    }
                }
//                
//                final Dimension dim = new Dimension(120, 120);
//                final BufferedImage imge = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
//                DefaultGlyphService.render(rule, new Rectangle(dim), imge.createGraphics(), null);
//                uiPreview.setImage(SwingFXUtils.toFXImage(imge, null));
            }
        });
        
        
        
        uiTable.setItems(FXCollections.observableArrayList());
        
        final TableColumn<GraphicalSymbol, GraphicalSymbol> previewCol = new TableColumn<>();
        previewCol.setEditable(true);
        previewCol.setPrefWidth(60);
        previewCol.setMinWidth(40);
        previewCol.setCellValueFactory(new Callback<CellDataFeatures<GraphicalSymbol, GraphicalSymbol>, ObservableValue<GraphicalSymbol>>() {

            @Override
            public ObservableValue<GraphicalSymbol> call(CellDataFeatures<GraphicalSymbol, GraphicalSymbol> param) {
                return new SimpleObjectProperty<>((GraphicalSymbol) param.getValue());
            }
        });
        previewCol.setCellFactory(new Callback<TableColumn<GraphicalSymbol, GraphicalSymbol>, TableCell<GraphicalSymbol, GraphicalSymbol>>() {

            @Override
            public GlyphButton call(TableColumn<GraphicalSymbol, GraphicalSymbol> p) {
                return new GlyphButton();
            }
        });
        
        
        
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
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiSize.setLayer(layer);
        uiOpacity.setLayer(layer);
        uiRotation.setLayer(layer);
        uiAnchor.setLayer(layer);
        uiDisplacement.setLayer(layer);
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
        if(styleElement.graphicalSymbols()!=null 
                && styleElement.graphicalSymbols().get(0)!=null){
            openEditor(styleElement.graphicalSymbols().get(0));
        }
    }
        
    private void openEditor(final GraphicalSymbol graphicalSymbol){
        graphicalSymbolEditor = FXStyleElementEditor.findEditor(graphicalSymbol);
        graphicalSymbolEditor.valueProperty().setValue(graphicalSymbol);
        graphicalSymbolEditor.setLayer(layer);
        uiGraphicalSymbol.getChildren().clear();
        uiGraphicalSymbol.getChildren().add(graphicalSymbolEditor);
        graphicalSymbolEditor.valueProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                rebuildValue();
            }
        });
    }
    
    private class GlyphButton extends ButtonTableCell<GraphicalSymbol, GraphicalSymbol>{

        public GlyphButton() {
            super(false, null,
                    (GraphicalSymbol t) -> t instanceof GraphicalSymbol,
                    //JavaFX bug : do not use lambda here : java.lang.VerifyError: Bad type on operand stack->invokedynamic
                    new Function<GraphicalSymbol, GraphicalSymbol>() {
                        @Override
                        public GraphicalSymbol apply(GraphicalSymbol graphicalSymbol) {
                            openEditor(graphicalSymbol);
                            return graphicalSymbol;
                        }
                    });
        }

        @Override
        protected void updateItem(GraphicalSymbol item, boolean empty) {
            super.updateItem(item, empty);
            
            
            layer.getStyle().addListener(new StyleListener() {

                @Override
                public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event) {
                    updateGlyph(item);
                }

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
                
            updateGlyph(item);

            if (item instanceof Mark) {
                final Mark m = (Mark) item;
                button.setText(m.getWellKnownName().toString());
            } else if (item instanceof ExternalGraphic) {
                final ExternalGraphic m = (ExternalGraphic) item;
                final OnlineResource res = m.getOnlineResource();
                if(res != null && res.getLinkage() != null){
                    button.setText(String.valueOf(res.getName()));
                }else{
                    button.setText("");
                }
            }
        }

        private void updateGlyph(final GraphicalSymbol graphicalSymbol){
            System.out.println("UPDATE GLYPH");
            final Graphic graphic = GO2Utilities.STYLE_FACTORY.graphic(
                        Collections.singletonList(graphicalSymbol),
                        StyleConstants.DEFAULT_GRAPHIC_OPACITY,
                        GO2Utilities.FILTER_FACTORY.literal(16),
                        StyleConstants.DEFAULT_GRAPHIC_ROTATION,
                        StyleConstants.DEFAULT_ANCHOR_POINT,
                        StyleConstants.DEFAULT_DISPLACEMENT);
            final PointSymbolizer ps = GO2Utilities.STYLE_FACTORY.pointSymbolizer(graphic, null);
            final BufferedImage img = DefaultGlyphService.create(ps, new Dimension(18, 18), null);
            button.setGraphic(new ImageView(SwingFXUtils.toFXImage(img, null)));
        }
    }

    private class DeleteButton extends ButtonTableCell<GraphicalSymbol, GraphicalSymbol>{

        public DeleteButton() {
            super(false, new ImageView(GeotkFX.ICON_DELETE),
                    //JavaFX bug : do not use lambda here : java.lang.VerifyError: Bad type on operand stack->invokedynamic
                    (GraphicalSymbol t) -> t instanceof GraphicalSymbol,
                    new Function<GraphicalSymbol, GraphicalSymbol>() {
                        @Override
                        public GraphicalSymbol apply(GraphicalSymbol t) {
                            uiTable.getItems().remove(t);
                            return t;
                        }
                    });
        }
    }
}
