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

package org.geotoolkit.gui.javafx.layer.style;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.apache.sis.cql.CQLException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.gui.javafx.filter.FXCQLEditor;
import org.geotoolkit.gui.javafx.layer.FXLayerStylePane;
import org.geotoolkit.gui.javafx.layer.FXPropertyPane;
import org.geotoolkit.gui.javafx.style.FXPaletteCell;
import org.geotoolkit.gui.javafx.util.ButtonTableCell;
import org.geotoolkit.gui.javafx.util.FXDeleteTableColumn;
import org.geotoolkit.gui.javafx.util.FXNumberSpinner;
import org.geotoolkit.image.palette.PaletteFactory;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.interval.DefaultIntervalPalette;
import org.geotoolkit.style.interval.IntervalPalette;
import org.geotoolkit.style.interval.IntervalStyleBuilder;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;
import org.opengis.style.Description;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStyleClassifRangePane extends FXLayerStylePane {

    private static final PaletteFactory PF = PaletteFactory.getDefault();
    private static final List<IntervalPalette> PALETTES;
    private static final Dimension GLYPH_DIMENSION = new Dimension(30, 20);

    static{
        PALETTES = new ArrayList<>();
        final Set<String> paletteNames = PF.getAvailableNames();

        for (String palName : paletteNames) {
            try {
                PALETTES.add(new DefaultIntervalPalette(PF.getColors(palName)));
            } catch (IOException ex) {
                Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    @FXML private ComboBox<PropertyName> uiProperty;
    @FXML private ComboBox<IntervalStyleBuilder.METHOD> uiMethod;
    @FXML private ComboBox<PropertyName> uiNormalize;
    @FXML private SplitMenuButton uiTemplate;
    @FXML private FXNumberSpinner uiClasses;
    @FXML private ComboBox<Object> uiPalette;
    @FXML private TableView<MutableRule> uiTable;
    @FXML private Button uiCombineFilter;
    //this is the target style element where we must generate the rules
    //it can be a MutableStyle or a MutableFeatureTypeStyle
    private Object targetStyleElement;
    private Filter combineFilter = Filter.INCLUDE;

    private final IntervalStyleBuilder analyze = new IntervalStyleBuilder();
    private FeatureMapLayer layer;

    public FXStyleClassifRangePane() {
        GeotkFX.loadJRXML(this,FXStyleClassifRangePane.class);
    }

    @FXML
    private void propertyChange(ActionEvent event) {
        analyze.setClassification(uiProperty.getSelectionModel().getSelectedItem());
        updateNormalizeList();
    }

    @FXML
    private void normalizeChange(ActionEvent event) {
        final PropertyName prop = uiNormalize.getSelectionModel().getSelectedItem();
        analyze.setNormalize(prop);
    }

    @FXML
    private void methodChange(ActionEvent event) {
        analyze.setMethod(uiMethod.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void editTemplate(ActionEvent event) {
        final Symbolizer template = FXPropertyPane.showSymbolizerDialog(this, analyze.getTemplate(), layer);
        analyze.setTemplate(template);
        updateTemplateGlyph();
    }

    @FXML
    private void generate(ActionEvent event) {
        analyze.setClassification(uiProperty.getValue());
        analyze.setMethod(uiMethod.getValue());
        analyze.setNbClasses(uiClasses.valueProperty().get().intValue());
        analyze.setNormalize(uiNormalize.getValue());
        uiTable.getItems().setAll(analyze.generateRules((IntervalPalette) uiPalette.getSelectionModel().getSelectedItem(),combineFilter));
    }

    @FXML
    private void editCombineFilter(ActionEvent event) {
        try {
            Filter f = FXCQLEditor.showFilterDialog(this, layer, combineFilter);
            if(f!=null){
                combineFilter = f;
                uiCombineFilter.setTooltip(new Tooltip(CQL.write(combineFilter)));
            }
        } catch (CQLException | DataStoreException ex) {
            Loggers.JAVAFX.log(Level.INFO, ex.getMessage(),ex);
        }
    }

    @FXML
    private void invertValues(ActionEvent event) {

        final List<MutableRule> rules = new ArrayList<>(uiTable.getItems());
        final Symbolizer[] symbols = new Symbolizer[rules.size()];

        for(int i=0;i<rules.size();i++){
            symbols[rules.size()-1-i] = rules.get(i).symbolizers().get(0);
        }

        for(int i=0;i<rules.size();i++){
            rules.get(i).symbolizers().clear();
            rules.get(i).symbolizers().add(symbols[i]);
        }

        uiTable.getItems().clear();
        uiTable.getItems().setAll(rules);
    }

    @FXML
    private void removeAll(ActionEvent event) {
        uiTable.getItems().clear();
    }

    @FXML
    private void apply(ActionEvent event) {
        if(layer==null) return;

        if(targetStyleElement instanceof MutableStyle){
            final List<MutableFeatureTypeStyle> ftss = ((MutableStyle)targetStyleElement).featureTypeStyles();
            final MutableFeatureTypeStyle fts;
            if(ftss.isEmpty()){
                fts = GeotkFX.getStyleFactory().featureTypeStyle();
                layer.getStyle().featureTypeStyles().add(fts);
            }else{
                fts = ftss.get(0);
            }

            fts.rules().clear();
            fts.rules().addAll(uiTable.getItems());
        }else if(targetStyleElement instanceof MutableFeatureTypeStyle){
            final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) targetStyleElement;
            fts.rules().clear();
            fts.rules().addAll(uiTable.getItems());
        }
    }

    @Override
    public String getTitle() {
        return GeotkFX.getString(this,"title");
    }

    @Override
    public String getCategory() {
        return GeotkFX.getString(this,"category");
    }

    /**
     * Called by FXMLLoader after creating controller.
     */
    public void initialize(){
        uiPalette.setItems(FXCollections.observableArrayList(PALETTES));
        uiPalette.setCellFactory((ListView<Object> param) -> new FXPaletteCell());
        uiPalette.setButtonCell((new FXPaletteCell()));
        uiPalette.setEditable(false);
        uiPalette.getSelectionModel().selectFirst();

        uiProperty.setCellFactory((ListView<PropertyName> param) -> new FXPropertyCell());
        uiProperty.setButtonCell((new FXPropertyCell()));
        uiProperty.setEditable(false);

        uiNormalize.setCellFactory((ListView<PropertyName> param) -> new FXPropertyCell());
        uiNormalize.setButtonCell((new FXPropertyCell()));
        uiNormalize.setEditable(false);

        uiMethod.setEditable(false);
        uiMethod.getItems().clear();
        uiMethod.getItems().add(IntervalStyleBuilder.METHOD.EL);
        uiMethod.getItems().add(IntervalStyleBuilder.METHOD.QANTILE);
        uiMethod.getItems().add(IntervalStyleBuilder.METHOD.MANUAL);
        uiClasses.getSpinner().setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0, 1));
        uiClasses.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            analyze.setNbClasses(uiClasses.valueProperty().get().intValue());
        });

        uiTable.setItems(FXCollections.observableArrayList());
        uiTable.getColumns().add(new GlyphColumn());
        uiTable.getColumns().add(new NameColumn());
        uiTable.getColumns().add(new FilterColumn());
        uiTable.getColumns().add(new FXDeleteTableColumn(false));

        //this will cause the column width to fit the view area
        uiTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        final MenuItem miPoint = new MenuItem(GeotkFX.getString(FXStyleClassifRangePane.class, "pointTemplate"));
        miPoint.setOnAction((ActionEvent event) -> {
            analyze.setTemplate(IntervalStyleBuilder.createPointTemplate());
            updateTemplateGlyph();
        });
        final MenuItem miLine = new MenuItem(GeotkFX.getString(FXStyleClassifRangePane.class, "lineTemplate"));
        miLine.setOnAction((ActionEvent event) -> {
            analyze.setTemplate(IntervalStyleBuilder.createLineTemplate());
            updateTemplateGlyph();
        });
        final MenuItem miPolygon = new MenuItem(GeotkFX.getString(FXStyleClassifRangePane.class, "polygonTemplate"));
        miPolygon.setOnAction((ActionEvent event) -> {
            analyze.setTemplate(IntervalStyleBuilder.createPolygonTemplate());
            updateTemplateGlyph();
        });
        uiTemplate.getItems().clear();
        uiTemplate.getItems().add(miPoint);
        uiTemplate.getItems().add(miLine);
        uiTemplate.getItems().add(miPolygon);

        uiCombineFilter.setGraphic(new ImageView(GeotkFX.ICON_FILTER));
    }

    @Override
    public boolean init(MapLayer candidate, Object styleElement) {
        if(!(candidate instanceof FeatureMapLayer)) return false;

        if(styleElement==null) styleElement = candidate.getStyle();
        this.targetStyleElement = styleElement;

        this.layer = (FeatureMapLayer) candidate;
        analyze.setLayer(layer);

        uiTable.getItems().clear();

        if(styleElement instanceof MutableStyle){
            if(analyze.isIntervalStyle((MutableStyle)styleElement)){
                uiTable.getItems().addAll(layer.getStyle().featureTypeStyles().get(0).rules());
            }
        }else if(styleElement instanceof MutableFeatureTypeStyle){
            if(analyze.isIntervalStyle((MutableFeatureTypeStyle)styleElement)){
                uiTable.getItems().addAll( ((MutableFeatureTypeStyle)styleElement).rules());
            }
        }

        final List<PropertyName> props = analyze.getProperties();
        uiProperty.setItems(FXCollections.observableArrayList(props));
        uiProperty.getSelectionModel().selectFirst();

        updateNormalizeList();
        updateTemplateGlyph();

        uiMethod.getSelectionModel().select(analyze.getMethod());
        uiClasses.valueProperty().set(analyze.getNbClasses());

        return true;
    }

    private void updateNormalizeList(){

        final PropertyName oldSelected = uiNormalize.getSelectionModel().getSelectedItem();

        final List<PropertyName> lstnormalize = new ArrayList<>();
        lstnormalize.add(analyze.noValue);
        lstnormalize.addAll(analyze.getProperties());
        lstnormalize.remove(uiProperty.getSelectionModel().getSelectedItem());
        uiNormalize.setItems(FXCollections.observableList(lstnormalize));

        if(oldSelected != null){
            uiNormalize.getSelectionModel().select(oldSelected);
        }
        if(uiNormalize.getSelectionModel().getSelectedItem() == null){
            uiNormalize.getSelectionModel().select(analyze.noValue);
        }

    }

    private void updateTemplateGlyph(){
        final Symbolizer template = analyze.getTemplate();
        if(template == null){
            uiTemplate.setGraphic(null);
            uiTemplate.setText("...");
        }else{
            final BufferedImage img = new BufferedImage(30, 20, BufferedImage.TYPE_INT_ARGB);
            DefaultGlyphService.render(template, new Rectangle(GLYPH_DIMENSION), img.createGraphics(),null);
            uiTemplate.setGraphic(new ImageView(SwingFXUtils.toFXImage(img, null)));
            uiTemplate.setText("");
        }
    }

    @Override
    public MutableStyle getMutableStyle() {
        final MutableStyle style = GeotkFX.getStyleFactory().style();
        final MutableFeatureTypeStyle fts = GeotkFX.getStyleFactory().featureTypeStyle();
        style.featureTypeStyles().add(fts);
        fts.rules().addAll(uiTable.getItems());
        return style;
    }


    private static final class FXPropertyCell extends ListCell<PropertyName>{

        @Override
        protected void updateItem(PropertyName item, boolean empty) {
            super.updateItem(item, empty);
            if(item instanceof PropertyName){
                setText(item.getPropertyName());
            }else{
                setText("");
            }
        }
    }

    private final class GlyphColumn extends TableColumn<MutableRule,Symbolizer>{

        public GlyphColumn() {
            super();
            setMinWidth(36);
            setMaxWidth(36);
            setEditable(true);
            setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MutableRule, Symbolizer>, ObservableValue<Symbolizer>>() {
                @Override
                public ObservableValue<Symbolizer> call(TableColumn.CellDataFeatures<MutableRule, Symbolizer> param) {
                    return new SimpleObjectProperty<>(param.getValue().symbolizers().get(0));
                }
            });
            setCellFactory(new Callback<TableColumn<MutableRule, Symbolizer>, TableCell<MutableRule, Symbolizer>>() {
                @Override
                public TableCell<MutableRule, Symbolizer> call(TableColumn<MutableRule, Symbolizer> param) {
                    return new ButtonTableCell<MutableRule,Symbolizer>(false,null,null,new Function<Symbolizer,Symbolizer>() {
                        @Override
                        public Symbolizer apply(Symbolizer t) {
                            return FXPropertyPane.showSymbolizerDialog(null, t, layer);
                        }
                    }){
                        @Override
                        protected void updateItem(Symbolizer item, boolean empty) {
                            super.updateItem(item, empty);
                            if(item!=null){
                                button.setText(null);
                                final BufferedImage img = new BufferedImage(30, 20, BufferedImage.TYPE_INT_ARGB);
                                DefaultGlyphService.render(item, new Rectangle(GLYPH_DIMENSION), img.createGraphics(),null);
                                button.setGraphic(new ImageView(SwingFXUtils.toFXImage(img, null)));
                            }
                        }
                    };
                }
            });
            addEventHandler((EventType)TableColumn.editCommitEvent(), new EventHandler<Event>() {
                @Override
                public void handle(Event evt) {
                    final TableColumn.CellEditEvent<MutableRule, Symbolizer> event = (TableColumn.CellEditEvent<MutableRule, Symbolizer>) evt;
                    //BUG : next line raise a nullpointer, bug in javafx ?
                    final MutableRule rule = event.getRowValue();
//                    final MutableRule rule = uiTable.getItems().get(event.getTablePosition().getRow());
                    rule.symbolizers().set(0, event.getNewValue());
                }
            });
        }
    }

    private static final class NameColumn extends TableColumn<MutableRule,String>{

        public NameColumn() {
            super(GeotkFX.getString(FXStyleClassifSinglePane.class, "name"));
            setMinWidth(80);
            setEditable(true);
            setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MutableRule, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<MutableRule, String> param) {
                    final Description desc = param.getValue().getDescription();
                    return new SimpleStringProperty(String.valueOf(desc.getTitle()));
                }
            });
            setCellFactory(TextFieldTableCell.forTableColumn());
            setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<MutableRule, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<MutableRule, String> event) {
                    final MutableRule rule = event.getRowValue();
                    final Description desc = GeotkFX.getStyleFactory().description(
                            new SimpleInternationalString(event.getNewValue()),
                            rule.getDescription().getAbstract());
                    rule.setDescription(desc);
                }
            });
        }
    }

    private final class FilterColumn extends TableColumn<MutableRule,Filter>{

        public FilterColumn() {
            super(GeotkFX.getString(FXStyleClassifSinglePane.class, "filter"));
            setMinWidth(80);
            setEditable(true);
            setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MutableRule, Filter>, ObservableValue<Filter>>() {
                @Override
                public ObservableValue<Filter> call(TableColumn.CellDataFeatures<MutableRule, Filter> param) {
                    return new SimpleObjectProperty<>(param.getValue().getFilter());
                }
            });
            setCellFactory(new Callback<TableColumn<MutableRule, Filter>, TableCell<MutableRule, Filter>>() {
                @Override
                public TableCell<MutableRule, Filter> call(TableColumn<MutableRule, Filter> param) {
                    return new ButtonTableCell<MutableRule,Filter>(false,null,null,new Function<Filter,Filter>() {
                        @Override
                        public Filter apply(Filter t) {
                            try{
                                return FXCQLEditor.showFilterDialog(null, layer, t);
                            }catch(CQLException | DataStoreException ex){
                                ex.printStackTrace();
                            }
                            return t;
                        }
                    }){

                        @Override
                        protected void updateItem(Filter item, boolean empty) {
                            super.updateItem(item, empty);
                            if(item!=null){
                                button.setText(CQL.write(item));
                            }
                        }
                    };
                }
            });
            setOnEditCommit((TableColumn.CellEditEvent<MutableRule, Filter> event) -> {
                event.getRowValue().setFilter(event.getNewValue());
            });
        }
    }

}
