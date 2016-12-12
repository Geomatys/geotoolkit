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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.opengis.feature.Feature;
import org.geotoolkit.gui.javafx.filter.FXCQLEditor;
import org.geotoolkit.gui.javafx.layer.FXLayerStylePane;
import org.geotoolkit.gui.javafx.layer.FXPropertyPane;
import org.geotoolkit.gui.javafx.style.FXPaletteCell;
import org.geotoolkit.gui.javafx.util.ButtonTableCell;
import org.geotoolkit.gui.javafx.util.FXDeleteTableColumn;
import org.geotoolkit.image.palette.PaletteFactory;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.interval.DefaultIntervalPalette;
import org.geotoolkit.style.interval.DefaultRandomPalette;
import org.geotoolkit.style.interval.IntervalStyleBuilder;
import org.geotoolkit.style.interval.Palette;
import org.geotoolkit.style.interval.RandomPalette;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.style.Description;
import org.opengis.style.Fill;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStyleClassifSinglePane extends FXLayerStylePane {
        
    private static final Dimension GLYPH_DIMENSION = new Dimension(30, 20);
    private static final RandomPalette RANDOM_PALETTE = new DefaultRandomPalette();
    private static final PaletteFactory PF = PaletteFactory.getDefault();
    private static final List<Palette> PALETTES;

    static{
        PALETTES = new ArrayList<>();
        final Set<String> paletteNames = PF.getAvailableNames();

        PALETTES.add(RANDOM_PALETTE);
        for (String palName : paletteNames) {
            if("pastel".equals(palName) || "bright".equals(palName) || "dark".equals(palName))
            try {
                PALETTES.add(new DefaultIntervalPalette(PF.getColors(palName)));
            } catch (IOException ex) {
                Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    @FXML private ComboBox<PropertyName> uiProperty;
    @FXML private CheckBox uiOther;
    @FXML private TableView<MutableRule> uiTable;
    @FXML private ComboBox<Palette> uiPalette;
    @FXML private SplitMenuButton uiTemplate;
    @FXML private Button uiCombineFilter;
    
    private FeatureMapLayer layer;
    private Symbolizer template;
    //this is the target style element where we must generate the rules
    //it can be a MutableStyle or a MutableFeatureTypeStyle
    private Object targetStyleElement;
    private Filter combineFilter = Filter.INCLUDE;
    
    
    public FXStyleClassifSinglePane() {
        GeotkFX.loadJRXML(this,FXStyleClassifSinglePane.class);
    }

    @FXML
    private void editTemplate(ActionEvent event) {
        template = FXPropertyPane.showSymbolizerDialog(this, template, layer);
        updateTemplateGlyph();
    }

    @FXML
    private void generate(ActionEvent event) {
        final ObservableList<MutableRule> rules = create(uiProperty.getSelectionModel().getSelectedItem(), uiOther.isSelected());
        uiTable.setItems(rules);
    }

    @FXML
    private void addValue(ActionEvent event) {
        final TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(GeotkFX.getString(FXStyleClassifSinglePane.class,"newvalue"));
        final Optional<String> str = dialog.showAndWait();
        if(str.get()==null)return;
        final String val = str.get();
        final MutableRule r = createRule(uiProperty.getSelectionModel().getSelectedItem(), val,uiTable.getItems().size());
        uiTable.getItems().add(r);
    }

    @FXML
    private void editCombineFilter(ActionEvent event) {
        try {
            final Filter f = FXCQLEditor.showFilterDialog(this, layer, combineFilter);
            if(f!=null){
                combineFilter = f;
                uiCombineFilter.setTooltip(new Tooltip(CQL.write(combineFilter)));
            }
        } catch (CQLException ex) {
            Loggers.JAVAFX.log(Level.INFO, ex.getMessage(),ex);
        }
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
            MutableFeatureTypeStyle fts;
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
        return GeotkFX.getString(FXStyleClassifSinglePane.class,"title");
    }
    
    @Override
    public String getCategory() {
        return GeotkFX.getString(FXStyleClassifSinglePane.class,"category");
    }
    
    private Class<? extends Symbolizer> getExpectedType(){
        if(template instanceof PointSymbolizer){
            return PointSymbolizer.class;
        }else if(template instanceof LineSymbolizer){
            return LineSymbolizer.class;
        }else if(template instanceof PolygonSymbolizer){
            return PolygonSymbolizer.class;
        }else{
            return PointSymbolizer.class;
        }
    }
    
    /**
     * Called by FXMLLoader after creating controller.
     */
    public void initialize(){
        uiPalette.setItems(FXCollections.observableArrayList(PALETTES));
        uiPalette.setCellFactory((ListView<Palette> param) -> new FXPaletteCell(false));
        uiPalette.setButtonCell((new FXPaletteCell(false)));
        uiPalette.setEditable(false);
        uiProperty.setCellFactory((ListView<PropertyName> param) -> new FXPropertyCell());
        uiProperty.setButtonCell((new FXPropertyCell()));
        uiProperty.setEditable(false);
        
        uiTable.setItems(FXCollections.observableArrayList());
        uiTable.getColumns().add(new GlyphColumn());
        uiTable.getColumns().add(new NameColumn());
        uiTable.getColumns().add(new FilterColumn());
        uiTable.getColumns().add(new FXDeleteTableColumn(false));
        
        //this will cause the column width to fit the view area
        uiTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        final MenuItem miPoint = new MenuItem(GeotkFX.getString(FXStyleClassifSinglePane.class, "pointTemplate"));
        miPoint.setOnAction((ActionEvent event) -> {
            template = IntervalStyleBuilder.createPointTemplate();
            updateTemplateGlyph();
        });
        final MenuItem miLine = new MenuItem(GeotkFX.getString(FXStyleClassifSinglePane.class, "lineTemplate"));
        miLine.setOnAction((ActionEvent event) -> {
            template = IntervalStyleBuilder.createLineTemplate();
            updateTemplateGlyph();
        });
        final MenuItem miPolygon = new MenuItem(GeotkFX.getString(FXStyleClassifSinglePane.class, "polygonTemplate"));
        miPolygon.setOnAction((ActionEvent event) -> {
            template = IntervalStyleBuilder.createPolygonTemplate();
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

        uiOther.setSelected(false);
        uiProperty.setItems(listProperties(layer));
        template = generateTemplate(layer);
        
        uiProperty.getSelectionModel().selectFirst();
        uiPalette.getSelectionModel().selectFirst();
        
        updateTemplateGlyph();
        if(styleElement instanceof MutableStyle){
            tryRebuildExisting((MutableStyle)styleElement);
        }else if(styleElement instanceof MutableFeatureTypeStyle){
            tryRebuildExisting((MutableFeatureTypeStyle)styleElement);
        }
        
        return true;
    }
    
    private void updateTemplateGlyph(){
        if(template==null){
            uiTemplate.setText("...");
            uiTemplate.setGraphic(null);
        }else{
            final BufferedImage img = new BufferedImage(30, 20, BufferedImage.TYPE_INT_ARGB);
            DefaultGlyphService.render(template, new Rectangle(GLYPH_DIMENSION), img.createGraphics(),null);
            uiTemplate.setText("");
            uiTemplate.setGraphic(new ImageView(SwingFXUtils.toFXImage(img, null)));
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
    
    private ObservableList listProperties(FeatureMapLayer layer){
        final ObservableList properties = FXCollections.observableArrayList();
        
        if(layer != null){
            final FeatureType schema = layer.getCollection().getFeatureType();
            for(PropertyType desc : schema.getProperties(true)){
                if(desc instanceof AttributeType){
                    final Class<?> type = ((AttributeType)desc).getValueClass();
                    if(!Geometry.class.isAssignableFrom(type)){
                        properties.add(GeotkFX.getFilterFactory().property(desc.getName().tip().toString()));
                    }
                }
            }
        }
        
        return properties;
    }
    
    private Symbolizer generateTemplate(FeatureMapLayer layer){
        Symbolizer template = null;
        
        if(layer != null){
            final FeatureType schema = layer.getCollection().getFeatureType();

            //find the geometry class for template
            final AttributeType<?> geo = FeatureExt.getDefaultGeometryAttribute(schema);
            final Class<?> geoClass = (geo!=null)?geo.getValueClass():null;
            
            final MutableStyleFactory sf = GeotkFX.getStyleFactory();
            final FilterFactory ff = GeotkFX.getFilterFactory();
            
            if(geoClass!=null && (Polygon.class.isAssignableFrom(geoClass) || MultiPolygon.class.isAssignableFrom(geoClass))){
                final Stroke stroke = sf.stroke(Color.BLACK, 1);
                final Fill fill = sf.fill(Color.BLUE);
                template = sf.polygonSymbolizer(stroke,fill,null);
            }else if(geoClass!=null && (LineString.class.isAssignableFrom(geoClass) || MultiLineString.class.isAssignableFrom(geoClass))){
                final Stroke stroke = sf.stroke(Color.BLUE, 2);
                template = sf.lineSymbolizer(stroke,null);
            }else{
                final Stroke stroke = sf.stroke(Color.BLACK, 1);
                final Fill fill = sf.fill(Color.BLUE);
                final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
                symbols.add(sf.mark(StyleConstants.MARK_CIRCLE, fill, stroke));
                final Graphic gra = sf.graphic(symbols, ff.literal(1), ff.literal(12), ff.literal(0), sf.anchorPoint(), sf.displacement());
                template = sf.pointSymbolizer(gra, null);
            }
        }
        
        return template;
    }
    
    private void tryRebuildExisting(MutableStyle style){
                
        //try to rebuild the previous analyze if it was one
        final List<MutableFeatureTypeStyle> ftss = style.featureTypeStyles();

        if(ftss.size() == 1){
            tryRebuildExisting(ftss.get(0));
        }else{
            tryRebuildExisting((MutableFeatureTypeStyle)null);
        }
        
    }

    private void tryRebuildExisting(MutableFeatureTypeStyle fts){

        final ObservableList<MutableRule> candidates = FXCollections.observableArrayList();
        boolean hasOther = false;
        PropertyName currentProperty = null;

        //try to rebuild the previous analyze if it was one
        final Class<? extends Symbolizer> expectedType = getExpectedType();
        final List<PropertyName> properties = uiProperty.getItems();

        if(fts != null){

            //defensive copy avoid synchronization
            final List<MutableRule> candidateRules = new ArrayList<>(fts.rules());

            for(Rule r : candidateRules){
                //defensive copy avoid synchronization
                final List<? extends Symbolizer> candidateSymbols = new ArrayList<>(r.symbolizers());

                if(candidateSymbols.size() != 1) return;

                final Symbolizer symbol = candidateSymbols.get(0);
                if(expectedType.isInstance(symbol)){

                    if(r.isElseFilter()){
                        //it looks like it's a valid classification "other" rule
                        candidates.add((MutableRule) r);
                        template = symbol;
                        hasOther = true;
                    }else{
                        Filter f = r.getFilter();
                        if(f != null && f instanceof PropertyIsEqualTo){
                            PropertyIsEqualTo equal = (PropertyIsEqualTo) f;
                            Expression exp1 = equal.getExpression1();
                            Expression exp2 = equal.getExpression2();

                            if(exp1 instanceof PropertyName && exp2 instanceof Literal){
                                if(properties.contains(exp1)){
                                    //it looks like it's a valid classification property rule
                                    candidates.add((MutableRule) r);
                                    template = symbol;
                                    currentProperty = (PropertyName) exp1;
                                }else{
                                    //property is not in the schema
                                    return;
                                }
                            }else if(exp2 instanceof PropertyName && exp1 instanceof Literal){
                                if(properties.contains(exp2)){
                                    //it looks like it's a valid classification property rule
                                    candidates.add((MutableRule) r);
                                    template = symbol;
                                    currentProperty = (PropertyName) exp2;
                                }else{
                                    //property is not in the schema
                                    return;
                                }
                            }else{
                                //mismatch analyze structure
                                return;
                            }
                        }
                    }

                }else{
                    return;
                }
            }
        }

        uiOther.setSelected(hasOther);
        uiProperty.getSelectionModel().select(currentProperty);
        uiTable.getItems().setAll(candidates);
    }

    
    private ObservableList<MutableRule> create(PropertyName property, boolean other){
        //search the different values
        final Set<Object> differentValues = new HashSet<Object>();
        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(layer.getCollection().getFeatureType().getName());
        builder.setProperties(new String[]{property.getPropertyName()});
        final Query query = builder.buildQuery();

        FeatureIterator features = null;
        try{
            features = layer.getCollection().subCollection(query).iterator();
            while(features.hasNext()){
                final Feature feature = features.next();
                differentValues.add(property.evaluate(feature));
            }
        }catch(DataStoreException ex){
            ex.printStackTrace();
        }catch(FeatureStoreRuntimeException ex){
            ex.printStackTrace();
        }finally{
            if(features != null){
                features.close();
            }
        }

        final MutableStyleFactory sf = GeotkFX.getStyleFactory();
        final ObservableList<MutableRule> rules = FXCollections.observableArrayList();

        int idx = 0;
        for(Object obj : differentValues){
            MutableRule rule = createRule(property, obj,idx);
            if(combineFilter!=null && !Filter.INCLUDE.equals(combineFilter)){
                rule.setFilter(GO2Utilities.FILTER_FACTORY.and(combineFilter, rule.getFilter()));
            }
            rules.add(rule);
            idx++;
        }

        //generate the other rule if asked
        if(other){
            MutableRule r = sf.rule(createSymbolizer(idx));
            r.setElseFilter(true);
            r.setDescription(sf.description("other", "other"));
            rules.add(r);
        }

        return rules;
    }
    
    protected Symbolizer createSymbolizer(int idx){
        final Palette palette = uiPalette.getValue();
        if(palette instanceof DefaultIntervalPalette){
            final DefaultIntervalPalette ip = (DefaultIntervalPalette) palette;
            final int[] argb = ip.getARGB();
            idx %= argb.length;
            return derivateSymbolizer(template, new Color(argb[idx]));
        }
        return derivateSymbolizer(template, RANDOM_PALETTE.next());
    }
    
    /**
     * Derivate a symbolizer with a new color.
     * @param symbol original symbolizer
     * @param color new color
     * @return derivate symbolizer
     */
    protected Symbolizer derivateSymbolizer(final Symbolizer symbol, final Color color){
        final MutableStyleFactory sf = GeotkFX.getStyleFactory();

        if(symbol instanceof PolygonSymbolizer){
            PolygonSymbolizer ps = (PolygonSymbolizer)symbol;
            Fill fill = sf.fill(sf.literal(color),ps.getFill().getOpacity());
            return sf.polygonSymbolizer(ps.getName(), ps.getGeometryPropertyName(),
                    ps.getDescription(), ps.getUnitOfMeasure(),
                    ps.getStroke(),fill,ps.getDisplacement(),ps.getPerpendicularOffset());
        }else if(symbol instanceof LineSymbolizer){
            LineSymbolizer ls = (LineSymbolizer) symbol;
            Stroke oldStroke = ls.getStroke();
            Stroke stroke = sf.stroke(sf.literal(color),oldStroke.getOpacity(),oldStroke.getWidth(),
                    oldStroke.getLineJoin(),oldStroke.getLineCap(),oldStroke.getDashArray(),oldStroke.getDashOffset());
            return sf.lineSymbolizer(ls.getName(), ls.getGeometryPropertyName(),
                    ls.getDescription(), ls.getUnitOfMeasure(), stroke, ls.getPerpendicularOffset());
        }else if(symbol instanceof PointSymbolizer){
            PointSymbolizer ps = (PointSymbolizer) symbol;
            Graphic oldGraphic = ps.getGraphic();
            Mark oldMark = (Mark) oldGraphic.graphicalSymbols().get(0);
            Fill fill = sf.fill(sf.literal(color),oldMark.getFill().getOpacity());
            List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
            symbols.add(sf.mark(oldMark.getWellKnownName(), fill, oldMark.getStroke()));
            Graphic graphic = sf.graphic(symbols, oldGraphic.getOpacity(),oldGraphic.getSize(),
                    oldGraphic.getRotation(),oldGraphic.getAnchorPoint(),oldGraphic.getDisplacement());
            return sf.pointSymbolizer(graphic,ps.getGeometryPropertyName());
        }else{
            throw new IllegalArgumentException("unexpected symbolizer type : " + symbol);
        }

    }

    /**
     * Create a rule for given property name and object value.
     * 
     * @param property rule filter property
     * @param obj rule filter property value
     * @param idx color index
     * @return rule created rule
     */
    protected MutableRule createRule(final PropertyName property, final Object obj, int idx){
        final MutableStyleFactory sf = GeotkFX.getStyleFactory();
        final FilterFactory ff = GeotkFX.getFilterFactory();
        
        final MutableRule r = sf.rule(createSymbolizer(idx));
        r.setFilter(ff.equals(property, ff.literal(obj)));
        r.setDescription(sf.description(obj.toString(), obj.toString()));
        r.setName(obj.toString());
        return r;
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
            setCellValueFactory(new Callback<CellDataFeatures<MutableRule, Symbolizer>, ObservableValue<Symbolizer>>() {
                @Override
                public ObservableValue<Symbolizer> call(CellDataFeatures<MutableRule, Symbolizer> param) {
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
                    final CellEditEvent<MutableRule, Symbolizer> event = (CellEditEvent<MutableRule, Symbolizer>) evt;
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
            setCellValueFactory(new Callback<CellDataFeatures<MutableRule, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(CellDataFeatures<MutableRule, String> param) {
                    final Description desc = param.getValue().getDescription();
                    return new SimpleStringProperty(String.valueOf(desc.getTitle()));
                }
            });
            setCellFactory(TextFieldTableCell.forTableColumn());
            setOnEditCommit(new EventHandler<CellEditEvent<MutableRule, String>>() {
                @Override
                public void handle(CellEditEvent<MutableRule, String> event) {
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
            setCellValueFactory(new Callback<CellDataFeatures<MutableRule, Filter>, ObservableValue<Filter>>() {
                @Override
                public ObservableValue<Filter> call(CellDataFeatures<MutableRule, Filter> param) {
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
                            }catch(CQLException ex){
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
            setOnEditCommit((CellEditEvent<MutableRule, Filter> event) -> {
                event.getRowValue().setFilter(event.getNewValue());
            });
        }
    }
        
}
