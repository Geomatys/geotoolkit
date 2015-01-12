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

package org.geotoolkit.gui.javafx.layer.style;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import org.geotoolkit.display2d.ext.graduation.GraduationSymbolizer;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.gui.javafx.layer.FXLayerStylePane;
import org.geotoolkit.gui.javafx.style.FXStyleElementController;
import org.geotoolkit.gui.javafx.style.FXStyleElementEditor;
import org.geotoolkit.gui.javafx.util.ButtonTableCell;
import org.geotoolkit.gui.javafx.util.FXDeleteTableColumn;
import org.geotoolkit.gui.javafx.util.FXMoveDownTableColumn;
import org.geotoolkit.gui.javafx.util.FXMoveUpTableColumn;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStyleSimplePane extends FXLayerStylePane {
    
    @FXML private ImageView uiPreview;
    @FXML private TableView<Symbolizer> uiTable;
    @FXML private BorderPane uiSymbolizerPane;
    @FXML private ComboBox<FXStyleElementController> uiChoice;
    
    private FXStyleElementController editor = null;
    private MapLayer layer;
    //keep track of where the rule was to avoid rewriting the complete style
    private MutableRule rule;
    
    public FXStyleSimplePane() {
        GeotkFX.loadJRXML(this);
    }

    @Override
    public String getTitle() {
        return GeotkFX.getString(this, "title");
    }
    
    @Override
    public String getCategory() {
        return GeotkFX.getString(this, "category");
    }
    
    @FXML
    void addSymbol(ActionEvent event) {
        final FXStyleElementController styleController = uiChoice.getSelectionModel().getSelectedItem();        
        final Symbolizer symbolizer = (Symbolizer) styleController.newValue();
        uiTable.getItems().add(symbolizer);
    }
    
    /**
     * Called by FXMLLoader after creating controller.
     */
    public void initialize(){
        final List<FXStyleElementController> editors = FXStyleElementEditor.findEditorsForType(Symbolizer.class);
        uiChoice.setItems(FXCollections.observableArrayList(editors));
        uiChoice.setButtonCell(new SymbolizerCell());
        uiChoice.setCellFactory((ListView<FXStyleElementController> param) -> new SymbolizerCell());
        
        final TableColumn<Symbolizer,Symbolizer> previewCol = new TableColumn<>();
        previewCol.setMinWidth(40);
        previewCol.setEditable(false);
        previewCol.setCellValueFactory((TableColumn.CellDataFeatures<Symbolizer, Symbolizer> param) -> new SimpleObjectProperty<>((Symbolizer)param.getValue()));
        previewCol.setCellFactory((TableColumn<Symbolizer, Symbolizer> p) -> new GlyphButton());
        
        uiTable.getColumns().add(previewCol);
        uiTable.getColumns().add(new FXMoveUpTableColumn());
        uiTable.getColumns().add(new FXMoveDownTableColumn());
        uiTable.getColumns().add(new FXDeleteTableColumn(false));
        uiTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        uiTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        uiTable.setTableMenuButtonVisible(false);
        
        //change symbolizer editor visible
        uiTable.getSelectionModel().getSelectedCells().addListener(new ListChangeListener<TablePosition>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends TablePosition> c) {
                uiSymbolizerPane.setCenter(null);
                
                for(final TablePosition tablePosition : uiTable.getSelectionModel().getSelectedCells()){
                    
                    final Symbolizer symbol = uiTable.getItems().get(tablePosition.getRow());
                    editor = FXStyleElementEditor.findEditor(symbol);
                    if(editor != null){
                        editor.setLayer(layer);
                        editor.valueProperty().setValue(symbol);

                        //listen to editor change
                        editor.valueProperty().addListener(new ChangeListener() {
                            @Override
                            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                                //apply editor
                                final int index = uiTable.getSelectionModel().getSelectedIndex();
                                if(index>=0){
                                    uiTable.getItems().set(index, (Symbolizer)editor.valueProperty().get());
                                }
                            }
                        });
                        uiSymbolizerPane.setCenter(editor);
                    }
                }
                
                final Dimension dim = new Dimension(120, 120);
                final BufferedImage imge = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
                DefaultGlyphService.render(rule, new Rectangle(dim), imge.createGraphics(), null);
                uiPreview.setImage(SwingFXUtils.toFXImage(imge, null));
            }
        });
        
    }
    
    @Override
    public boolean init(Object candidate) {
        if(!(candidate instanceof MapLayer)) return false;
        
        this.layer = (MapLayer) candidate;
        
        rule = null;        
        for(final FeatureTypeStyle typeStyle : layer.getStyle().featureTypeStyles()){
            for(final Rule rule : typeStyle.rules()){
                parse((MutableRule)rule);
                break; //we only retrieve the first rule.
            }
        }
        
        return true;
    }
    
    private void parse(final MutableRule rule) {

        //listen to rule change from other style editors
        if(this.rule!=rule){
            this.rule = rule;
            uiTable.setItems(FXCollections.observableList(rule.symbolizers()));
        }
    }
    
    @Override
    public MutableStyle getMutableStyle() {
        return layer.getStyle();
    }
    
    private static int identityIndex(Object obj, List lst){
        for(int i=0,n=lst.size();i<n;i++){
            if(lst.get(i)==obj)return i;
        }
        return -1;
    }
    
    private static class GlyphButton extends ButtonTableCell<Symbolizer, Symbolizer>{

        public GlyphButton() {
            super(false, null,
                  (Symbolizer t) -> t instanceof Symbolizer, new Function<Symbolizer, Symbolizer>() {

                public Symbolizer apply(Symbolizer t) {
                    return t;
                }
            });
        }

        @Override
        protected void updateItem(Symbolizer item, boolean empty) {
            super.updateItem(item, empty);
            
            if(item instanceof Symbolizer){
                final BufferedImage img = DefaultGlyphService.create(item, new Dimension(24, 24), null);
                button.setGraphic(new ImageView(SwingFXUtils.toFXImage(img,null)));
                button.setText(item.getName());
            }
            
        }
    }
        
//    private static class SymbolizersList extends ModifiableObservableListBase<Symbolizer> implements RuleListener{
//        
//        private final MutableRule rule;
//        
//        public SymbolizersList(MutableRule rule) {
//            this.rule = rule;
//            rule.addListener(this);
//        }
//        
//        @Override
//        public Symbolizer get(int index) {
//            return rule.symbolizers().get(index);
//        }
//
//        @Override
//        public int size() {
//            return rule.symbolizers().size();
//        }
//
//        @Override
//        protected void doAdd(int index, Symbolizer element) {
//            rule.symbolizers().add(index, element);
//        }
//
//        @Override
//        protected Symbolizer doSet(int index, Symbolizer element) {
//            return rule.symbolizers().set(index, element);
//        }
//
//        @Override
//        protected Symbolizer doRemove(int index) {
//            return rule.symbolizers().remove(index);
//        }
//        
//        @Override
//        public void symbolizerChange(CollectionChangeEvent<Symbolizer> event) {
//            final int type = event.getType();
//            final int min = (int) event.getRange().getMinDouble();
//            final int max = (int) event.getRange().getMaxDouble();
//            if(type==CollectionChangeEvent.ITEM_ADDED){
//                fireChange(new NonIterableChange.SimpleAddChange<>(min,max,this));
//            }else if(type==CollectionChangeEvent.ITEM_REMOVED){
//                fireChange(new NonIterableChange.GenericAddRemoveChange<>(min,max,new ArrayList(event.getItems()),this));
//            }else if(type==CollectionChangeEvent.ITEM_CHANGED){
//                fireChange(new NonIterableChange.SimpleUpdateChange<>(min,max,this));
//            }
//        }
//
//        @Override
//        public void propertyChange(PropertyChangeEvent evt) {
//        }
//
//    }
    
    private static class SymbolizerCell extends ListCell<FXStyleElementController>{
        @Override
        protected void updateItem(FXStyleElementController item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setGraphic(null);
                setText("");
            } else {
                final Symbolizer symb = (Symbolizer) item.newValue();
                final Dimension dim = DefaultGlyphService.glyphPreferredSize(symb, null, null);
                final BufferedImage imge = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
                DefaultGlyphService.render(symb, new Rectangle(dim),imge.createGraphics(),null);
                setGraphic(new ImageView(SwingFXUtils.toFXImage(imge, null)));
                if(symb instanceof PointSymbolizer){
                    setText("Point");
                }else if(symb instanceof LineSymbolizer){
                    setText("Line");
                }else if(symb instanceof PolygonSymbolizer){
                    setText("Polygon");
                }else if(symb instanceof TextSymbolizer){
                    setText("Text");
                }else if(symb instanceof RasterSymbolizer){
                    setText("Raster");
                }else if(symb instanceof GraduationSymbolizer){
                    setText("Graduation");
                }else{
                    setText(symb.getClass().getSimpleName());
                }
                setContentDisplay(ContentDisplay.LEFT);
                setTextAlignment(TextAlignment.CENTER);
            }
        }
    }
}

