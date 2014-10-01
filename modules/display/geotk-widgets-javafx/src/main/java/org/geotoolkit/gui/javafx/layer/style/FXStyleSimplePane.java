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

import com.sun.javafx.collections.NonIterableChange;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ModifiableObservableListBase;
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
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.gui.javafx.layer.FXLayerStylePane;
import org.geotoolkit.gui.javafx.style.FXStyleElementController;
import org.geotoolkit.gui.javafx.style.FXStyleElementEditor;
import org.geotoolkit.gui.javafx.util.ButtonTableCell;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.RuleListener;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStyleSimplePane extends FXLayerStylePane {
    
    @FXML
    private ImageView uiPreview;
    @FXML
    private TableView<Symbolizer> uiTable;
    @FXML
    private BorderPane uiSymbolizerPane;
    @FXML
    private ComboBox<FXStyleElementController> uiChoice;
    
    private FXStyleElementController editor = null;
    private MapLayer layer;
    //keep track of where the rule was to avoid rewriting the complete style
    private MutableRule rule;
    
    public FXStyleSimplePane() {
        GeotkFX.loadJRXML(this);
    }

    @Override
    public String getTitle() {
        return GeotkFX.getString(this,"title");
    }
    
    @Override
    public String getCategory() {
        return GeotkFX.getString(this,"category");
    }
    
    @FXML
    void addSymbol(ActionEvent event) {
        final FXStyleElementController control = uiChoice.getSelectionModel().getSelectedItem();        
        final Symbolizer symbolizer = (Symbolizer) control.newValue();
        rule.symbolizers().add(symbolizer);
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
        previewCol.setPrefWidth(60);
        previewCol.setMinWidth(40);
        previewCol.setEditable(false);
        previewCol.setCellValueFactory((TableColumn.CellDataFeatures<Symbolizer, Symbolizer> param) -> new SimpleObjectProperty<>((Symbolizer)param.getValue()));
        previewCol.setCellFactory((TableColumn<Symbolizer, Symbolizer> p) -> new GlyphButton());
        
        final TableColumn<Symbolizer,Symbolizer> moveUpCol = new TableColumn<>();
        moveUpCol.setEditable(true);
        moveUpCol.setPrefWidth(30);
        moveUpCol.setMinWidth(30);
        moveUpCol.setMaxWidth(30);
        moveUpCol.setCellValueFactory((TableColumn.CellDataFeatures<Symbolizer, Symbolizer> param) -> new SimpleObjectProperty<>((Symbolizer)param.getValue()));
        moveUpCol.setCellFactory((TableColumn<Symbolizer, Symbolizer> p) -> new MoveUpButton());
        
        final TableColumn<Symbolizer,Symbolizer> moveDownCol = new TableColumn<>();
        moveDownCol.setEditable(true);
        moveDownCol.setPrefWidth(30);
        moveDownCol.setMinWidth(30);
        moveDownCol.setMaxWidth(30);
        moveDownCol.setCellValueFactory((TableColumn.CellDataFeatures<Symbolizer, Symbolizer> param) -> new SimpleObjectProperty<>((Symbolizer)param.getValue()));
        moveDownCol.setCellFactory((TableColumn<Symbolizer, Symbolizer> p) -> new MoveDownButton());
        
        final TableColumn<Symbolizer,Symbolizer> deleteCol = new TableColumn<>();
        deleteCol.setEditable(true);
        deleteCol.setPrefWidth(30);
        deleteCol.setMinWidth(30);
        deleteCol.setMaxWidth(30);
        deleteCol.setCellValueFactory((TableColumn.CellDataFeatures<Symbolizer, Symbolizer> param) -> new SimpleObjectProperty<>((Symbolizer)param.getValue()));
        deleteCol.setCellFactory((TableColumn<Symbolizer, Symbolizer> p) -> new DeleteButton());
        
        
        uiTable.getColumns().add(previewCol);
        uiTable.getColumns().add(moveUpCol);
        uiTable.getColumns().add(moveDownCol);
        uiTable.getColumns().add(deleteCol);
        uiTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        uiTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        uiTable.setTableMenuButtonVisible(false);
        
        //update preview on events
        uiTable.getItems().addListener(new ListChangeListener<Symbolizer>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Symbolizer> c) {
                final Dimension dim = new Dimension(120, 120);
                final BufferedImage imge = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
                DefaultGlyphService.render(rule, new Rectangle(dim),imge.createGraphics(),null);
                uiPreview.setImage(SwingFXUtils.toFXImage(imge, null));
            }
        });
        
        //change symbolizer editor visible
        uiTable.getSelectionModel().getSelectedCells().addListener(new ListChangeListener<TablePosition>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends TablePosition> c) {
                uiSymbolizerPane.setCenter(null);
                for(Object i : uiTable.getSelectionModel().getSelectedCells()){
                    final TablePosition ttp = (TablePosition) i;                    
                    final Symbolizer symbol = uiTable.getItems().get(ttp.getRow());
                    editor = FXStyleElementEditor.findEditor(symbol);
                    if(editor != null){
                        editor.setLayer(layer);
                        editor.valueProperty().setValue(symbol);

                        //listen to editor change
                        editor.valueProperty().addListener(new ChangeListener() {
                            @Override
                            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                                //applyEditor(editorPath);
                            }
                        });
                        uiSymbolizerPane.setCenter(editor);
                    }
                }
            }
        });
        
    }
    
    @Override
    public boolean init(Object candidate) {
        if(!(candidate instanceof MapLayer)) return false;
        
        this.layer = (MapLayer) candidate;
        
        rule = null;        
        for(final FeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
            for(final Rule rule : fts.rules()){
                parse((MutableRule)rule);
                break; //we only retrieve the first rule.
            }
        }
        
        return true;
    }
    
    private void parse(final MutableRule rule) {

        //listen to rule change from other syle editors
        if(this.rule!=rule){
            this.rule = rule;
            uiTable.setItems(new SymbolizersList(rule));
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
                  (Symbolizer t) -> t instanceof Symbolizer, 
                  (Symbolizer t) -> {openEditor(t);return t;}
            );
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
        
        private static void openEditor(Symbolizer t){
        }
    }

    private class DeleteButton extends ButtonTableCell<Symbolizer, Symbolizer>{

        public DeleteButton() {
            super(false, new ImageView(GeotkFX.ICON_DELETE),
                   //JavaFX bug : do not use lambda here : java.lang.VerifyError: Bad type on operand stack->invokedynamic
                  (Symbolizer t) -> t instanceof Symbolizer, new Function<Symbolizer,Symbolizer>() {
                public Symbolizer apply(Symbolizer t) {
                    uiTable.getItems().remove(t);
                    return t;
                }
            });
        }
    }
    
    private class MoveUpButton extends ButtonTableCell<Symbolizer, Symbolizer>{

        public MoveUpButton() {
            super(false, new ImageView(GeotkFX.ICON_MOVEUP),
                   //JavaFX bug : do not use lambda here : java.lang.VerifyError: Bad type on operand stack->invokedynamic
                  (Symbolizer t) -> t instanceof Symbolizer, new Function<Symbolizer,Symbolizer>() {
                public Symbolizer apply(Symbolizer t) {
                    int index = identityIndex(t, uiTable.getItems());
                    if(index>0){
                        uiTable.getItems().remove(index);
                        index--;
                        uiTable.getItems().add(index, t);
                    }
                    return t;
                }
            });
        }
    }
    
    private class MoveDownButton extends ButtonTableCell<Symbolizer, Symbolizer>{

        public MoveDownButton() {
            super(false, new ImageView(GeotkFX.ICON_MOVEDOWN),
                   //JavaFX bug : do not use lambda here : java.lang.VerifyError: Bad type on operand stack->invokedynamic
                  (Symbolizer t) -> t instanceof Symbolizer, new Function<Symbolizer,Symbolizer>() {
                public Symbolizer apply(Symbolizer t) {
                    int index = identityIndex(t, uiTable.getItems());
                    if(index>=0 && index<uiTable.getItems().size()-1){
                        uiTable.getItems().remove(index);
                        index++;
                        uiTable.getItems().add(index, t);
                    }
                    return t;
                }
            });
        }
    }
    
    private static class SymbolizersList extends ModifiableObservableListBase<Symbolizer> implements RuleListener{
        
        private final MutableRule rule;
        
        public SymbolizersList(MutableRule rule) {
            this.rule = rule;
            rule.addListener(this);
        }
        
        @Override
        public Symbolizer get(int index) {
            return rule.symbolizers().get(index);
        }

        @Override
        public int size() {
            return rule.symbolizers().size();
        }

        @Override
        protected void doAdd(int index, Symbolizer element) {
            rule.symbolizers().add(index, element);
        }

        @Override
        protected Symbolizer doSet(int index, Symbolizer element) {
            return rule.symbolizers().set(index, element);
        }

        @Override
        protected Symbolizer doRemove(int index) {
            return rule.symbolizers().remove(index);
        }
        
        @Override
        public void symbolizerChange(CollectionChangeEvent<Symbolizer> event) {
            final int type = event.getType();
            final int min = (int) event.getRange().getMinDouble();
            final int max = (int) event.getRange().getMaxDouble();
            if(type==CollectionChangeEvent.ITEM_ADDED){
                fireChange(new NonIterableChange.SimpleAddChange<>(min,max,this));
            }else if(type==CollectionChangeEvent.ITEM_REMOVED){
                fireChange(new NonIterableChange.GenericAddRemoveChange<>(min,max,new ArrayList(event.getItems()),this));
            }else if(type==CollectionChangeEvent.ITEM_CHANGED){
                fireChange(new NonIterableChange.SimpleUpdateChange<>(min,max,this));
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        }

    }
    
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
                setText(symb.getName());
                setContentDisplay(ContentDisplay.LEFT);
                setTextAlignment(TextAlignment.CENTER);
            }
        }
    }
    
}

