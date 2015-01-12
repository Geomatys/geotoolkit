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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.controlsfx.dialog.Dialogs;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.gui.javafx.filter.FXCQLEditor;
import org.geotoolkit.gui.javafx.layer.FXLayerStylePane;
import org.geotoolkit.gui.javafx.layer.FXPropertyPane;
import org.geotoolkit.gui.javafx.style.FXPaletteCell;
import org.geotoolkit.gui.javafx.util.ButtonTableCell;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.interval.DefaultRandomPalette;
import org.geotoolkit.style.interval.RandomPalette;
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
    
    @FXML
    private ComboBox<PropertyName> uiProperty;
    @FXML
    private CheckBox uiOther;
    @FXML
    private TableView<MutableRule> uiTable;
    @FXML
    private ComboBox<Object> uiPalette;
    @FXML
    private Button uiTemplate;
    
    private FeatureMapLayer layer;
    private final RandomPalette palette = new DefaultRandomPalette();
    private Symbolizer template;
    
    
    public FXStyleClassifSinglePane() {
        GeotkFX.loadJRXML(this);
    }

    @FXML
    void editTemplate(ActionEvent event) {
        template = FXPropertyPane.showSymbolizerDialog(this, template, layer);
        updateTemplateGlyph();
    }

    @FXML
    void generate(ActionEvent event) {
        final ObservableList<MutableRule> rules = create(uiProperty.getSelectionModel().getSelectedItem(), uiOther.isSelected());
        uiTable.setItems(rules);
    }

    @FXML
    void addValue(ActionEvent event) {
        final Optional<String> str = Dialogs.create().message(GeotkFX.getString(FXStyleClassifSinglePane.class,"newvalue")).showTextInput();
        if(str.get()==null)return;
        final String val = str.get();
        final MutableRule r = createRule(uiProperty.getSelectionModel().getSelectedItem(), val);
        uiTable.getItems().add(r);
    }

    @FXML
    void removeAll(ActionEvent event) {
        uiTable.getItems().clear();
    }
    
    @Override
    public String getTitle() {
        return GeotkFX.getString(this,"title");
    }
    
    @Override
    public String getCategory() {
        return GeotkFX.getString(this,"category");
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
        uiPalette.setItems(FXCollections.observableArrayList((Object)palette));        
        uiPalette.setCellFactory((ListView<Object> param) -> new FXPaletteCell());
        uiPalette.setButtonCell((new FXPaletteCell()));
        uiPalette.setEditable(false);
        uiProperty.setCellFactory((ListView<PropertyName> param) -> new FXPropertyCell());
        uiProperty.setButtonCell((new FXPropertyCell()));
        uiProperty.setEditable(false);
        
        uiTable.setItems(FXCollections.observableArrayList());
        uiTable.getColumns().add(new GlyphColumn());
        uiTable.getColumns().add(new NameColumn());
        uiTable.getColumns().add(new FilterColumn());
        uiTable.getColumns().add(new DeleteColumn());
        
        //this will cause the column width to fit the view area
        uiTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                
    }
    
    @Override
    public boolean init(Object candidate) {
        if(!(candidate instanceof FeatureMapLayer)) return false;    
        
        this.layer = (FeatureMapLayer) candidate;
        
        uiOther.setSelected(false);
        uiProperty.setItems(listProperties(layer));
        template = generateTemplate(layer);
        
        uiProperty.getSelectionModel().selectFirst();
        uiPalette.getSelectionModel().selectFirst();
        
        updateTemplateGlyph();
        tryRebuildExisting(layer);
        
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
            for(PropertyDescriptor desc : schema.getDescriptors()){
                final Class<?> type = desc.getType().getBinding();
                if(!Geometry.class.isAssignableFrom(type)){
                    properties.add(GeotkFX.getFilterFactory().property(desc.getName().getLocalPart()));
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
            final GeometryDescriptor geo = schema.getGeometryDescriptor();
            final Class<?> geoClass = (geo!=null)?geo.getType().getBinding():null;
            
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
    
    private void tryRebuildExisting(FeatureMapLayer layer){
        
        final ObservableList<MutableRule> candidates = FXCollections.observableArrayList();
        boolean hasOther = false;
        PropertyName currentProperty = null;
        
        //try to rebuild the previous analyze if it was one
        final List<MutableFeatureTypeStyle> ftss = layer.getStyle().featureTypeStyles();
        final Class<? extends Symbolizer> expectedType = getExpectedType();
        final List<PropertyName> properties = uiProperty.getItems();

        if(ftss.size() == 1){
            final MutableFeatureTypeStyle fts = ftss.get(0);

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

        FeatureIterator<? extends Feature> features = null;
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

        for(Object obj : differentValues){
            rules.add(createRule(property, obj));
        }

        //generate the other rule if asked
        if(other){
            MutableRule r = sf.rule(createSymbolizer());
            r.setElseFilter(true);
            r.setDescription(sf.description("other", "other"));
            rules.add(r);
        }

        return rules;
    }
    
    private Symbolizer createSymbolizer(){
        return derivateSymbolizer(template, palette.next());
    }
    
    /**
     * Derivate a symbolizer with a new color.
     */
    private Symbolizer derivateSymbolizer(final Symbolizer symbol, final Color color){
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

    private MutableRule createRule(final PropertyName property, final Object obj){
        final MutableStyleFactory sf = GeotkFX.getStyleFactory();
        final FilterFactory ff = GeotkFX.getFilterFactory();
        
        final MutableRule r = sf.rule(createSymbolizer());
        r.setFilter(ff.equals(property, ff.literal(obj)));
        r.setDescription(sf.description(obj.toString(), obj.toString()));
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
    
    private final class DeleteColumn extends TableColumn<MutableRule,MutableRule>{

        public DeleteColumn() {
            setMinWidth(36);
            setMaxWidth(36);
            setEditable(true);
            setCellValueFactory((CellDataFeatures<MutableRule, MutableRule> param) -> new SimpleObjectProperty<>(param.getValue()));
            setCellFactory(new Callback<TableColumn<MutableRule, MutableRule>, TableCell<MutableRule, MutableRule>>() {
                @Override
                public TableCell<MutableRule, MutableRule> call(TableColumn<MutableRule, MutableRule> param) {
                    return new ButtonTableCell(false,new ImageView(GeotkFX.ICON_DELETE),null,new Function<MutableRule,MutableRule>() {
                        @Override
                        public MutableRule apply(MutableRule t) {
                            uiTable.getItems().remove(t);
                            return t;
                        }
                    });
                }
            });
        }
        
    }
    
}
