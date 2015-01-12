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

import java.awt.Color;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import jidefx.scene.control.field.NumberField;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageUtilities;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getFilterFactory;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.gui.javafx.util.FXNumberSpinner;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.process.coverage.statistics.StatisticOp;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.style.StyleConstants;
import static org.geotoolkit.style.StyleConstants.DEFAULT_CATEGORIZE_LOOKUP;
import static org.geotoolkit.style.StyleConstants.DEFAULT_FALLBACK;
import org.geotoolkit.style.function.Categorize;
import org.geotoolkit.style.function.DefaultInterpolationPoint;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Jenks;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.geotoolkit.style.interval.Palette;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.ColorMap;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXColorMap extends FXStyleElementController<FXColorMap,ColorMap> {
    
    private static final Literal TRS = new DefaultLiteral(new Color(0, 0, 0, 0));
    
    @FXML
    private CheckBox uiInvert;
    @FXML
    private Label uiMinimumLbl;
    @FXML
    private CheckBox uiNaN;
    @FXML
    private Label uiMethodLbl;
    @FXML
    private FXNumberSpinner uiBand;
    @FXML
    private Label uiMaximumLbl;
    @FXML
    private TableView<InterOrCategorize> uiTable;
    @FXML
    private Button uiAddOne;
    @FXML
    private Label uiDynamic;
    @FXML
    private Label uiBandLbl;
    @FXML
    private Button uiGenerate;
    @FXML
    private Button uiRemoveAll;
    @FXML
    private Label uiNoData;
    @FXML
    private FXNumberSpinner uiMinimum;
    @FXML
    private ComboBox<Class> uiMethod;
    @FXML
    private Label uiPaletteLbl;
    @FXML
    private Label uiDivisionLbl;
    @FXML
    private FXNumberSpinner uiDivision;
    @FXML
    private FXNumberSpinner uiMaximum;
    @FXML
    private ComboBox<Object> uiPalette;

    private Class function = null;
    private final Value1Column value1Col = new Value1Column();
    private final Value2Column value2Col = new Value2Column();
    private final ColorColumn colorCol = new ColorColumn();
    
    
    @FXML
    void methodChange(ActionEvent event) {
        final Object method = uiMethod.getSelectionModel().getSelectedItem();

        if(Interpolate.class.equals(method)){
            if(Interpolate.class.isAssignableFrom(function)){
                //nothing to do
                return;
            }else if(Categorize.class.isAssignableFrom(function)){
                final List<InterOrCategorize> points = new ArrayList<>(uiTable.getItems());
                if(points.size()>0){
                    //remove the first element which is the -inf thredhold
                    points.remove(0);
                }
                uiTable.getItems().setAll(points);
            }else{
                uiTable.getItems().clear();
            }
            function = Interpolate.class;
        }else if(Categorize.class.equals(method)){
            if(Categorize.class.isAssignableFrom(function)){
                //nothing to do
                return;
            }else if(Interpolate.class.isAssignableFrom(function)){
                //we need to convert from interpolate to categorize
                final List<InterOrCategorize> points = new ArrayList<>(uiTable.getItems());
                points.add(new InterOrCategorize(StyleConstants.CATEGORIZE_LESS_INFINITY, TRS));
                points.addAll(uiTable.getItems());
                uiTable.getItems().setAll(points);
            }else{
                final List<InterOrCategorize> points = new ArrayList<>(uiTable.getItems());
                points.add(new InterOrCategorize(StyleConstants.CATEGORIZE_LESS_INFINITY, TRS));
                points.add(new InterOrCategorize(StyleConstants.LITERAL_ONE_FLOAT, TRS));
                uiTable.getItems().setAll(points);
            }
            function = Categorize.class;
        }else{
            if(function == Jenks.class){
                //nothing to do
                return;
            }else{
                uiTable.getItems().clear();
            }
            function = Jenks.class;
        }
        postParse();
        
        //ensure the NaN is set as defined
        nanChange(null);
    }
    
    @FXML
    void nanChange(ActionEvent event) {

        final boolean withNaN = uiNaN.isSelected();

        if(function == Interpolate.class || function == Categorize.class){
            final List<InterOrCategorize> points = uiTable.getItems();
            for(int i=0,n=points.size();i<n;i++){
                final InterOrCategorize entry = points.get(i);

                final Object num = entry.value.get().evaluate(null);
                if(num instanceof Number && (Double.isNaN(((Number)num).doubleValue()) || Float.isNaN(((Number)num).floatValue()))){
                    if(withNaN){
                        //color model already has a NaN
                        return;
                    }else{
                        //remove it
                        points.remove(i);
                        return;
                    }
                }
            }

            if(withNaN){
                //add NaN entry
                points.add(new InterOrCategorize(getFilterFactory().literal(Float.NaN), TRS));
            }

            postParse();
        }
    }
    
    @FXML
    void fitToData(ActionEvent event) {
        if(layer != null && layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer)layer;
            final CoverageReference cref = cml.getCoverageReference();
            GridCoverageReader reader = null;
            GeneralGridGeometry gridGeometry = null;
            GridCoverageReadParam readParam = null;
            GridCoverage coverage = null;
            GridCoverage2D coverage2D = null;
            RenderedImage image = null;
            try {
                reader = cref.acquireReader();
                gridGeometry = reader.getGridGeometry(cref.getImageIndex());

                if (gridGeometry.isDefined(GeneralGridGeometry.GRID_TO_CRS)
                        && gridGeometry.isDefined(GeneralGridGeometry.EXTENT)) {
                    MathTransform gridToCRS = gridGeometry.getGridToCRS();
                    GridEnvelope extent = gridGeometry.getExtent();
                    int dim = extent.getDimension();
                    double[] low = new double[dim];
                    double[] high = new double[dim];
                    low[0] = extent.getLow(0);
                    high[0] = extent.getHigh(0);
                    low[1] = extent.getLow(1);
                    high[1] = extent.getHigh(1);
                    GeneralEnvelope sliceExtent = new GeneralEnvelope(gridGeometry.getCoordinateReferenceSystem());
                    for (int i = 0; i < dim; i++) {
                        sliceExtent.setRange(i, low[i], high[i]);
                    }

                    readParam = new GridCoverageReadParam();
                    readParam.setEnvelope(CRS.transform(gridToCRS, sliceExtent));
                    readParam.setCoordinateReferenceSystem(gridGeometry.getCoordinateReferenceSystem());

                    coverage = reader.read(cref.getImageIndex(), readParam);
                    coverage2D = CoverageUtilities.firstSlice(coverage);
                    image = coverage2D.getRenderedImage();
                    final Map<String, Object> an = StatisticOp.analyze(image);
                    final double[] minArray = (double[]) an.get(StatisticOp.MINIMUM);
                    final double[] maxArray = (double[]) an.get(StatisticOp.MAXIMUM);

                    final Integer index = uiBand.valueProperty().get().intValue();
                    uiMinimum.valueProperty().setValue(minArray[index]);
                    uiMaximum.valueProperty().setValue(maxArray[index]);
                }
                cref.recycle(reader);
            } catch (CoverageStoreException ex) {
                Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(),ex);
            } catch (DataStoreException ex) {
                Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(),ex);
            } catch (TransformException ex) {
                Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
    
    @FXML
    void addValue(ActionEvent event) {
        uiTable.getItems().add(new InterOrCategorize());
    }

    @FXML
    void removeAll(ActionEvent event) {
        uiTable.getItems().clear();
    }

    @FXML
    void generate(ActionEvent event) {

        if(!(layer instanceof CoverageMapLayer)){
            return;
        }

        uiTable.getItems().clear();

        final List<InterOrCategorize> lst = new ArrayList<>();
        //add the NaN if specified
        if(uiNaN.isSelected()){
            lst.add(new InterOrCategorize(Double.NaN, new Color(0, 0, 0, 0)));
        }

        boolean mustInterpolation = true;
        final Object paletteValue = (Object) uiPalette.getSelectionModel().getSelectedItem();
        List<Entry<Double, Color>> steps = new ArrayList<>();

        if (paletteValue instanceof Palette) {
            final Palette palette = (Palette) paletteValue;
            steps = palette.getSteps();
        } else if (paletteValue instanceof String) {
            try {
                final Color[] paletteColors = FXUtilities.PF.getColors(String.valueOf(paletteValue));
                final double stepValue = 1.0f/(paletteColors.length-1);
                for (int i = 0; i < paletteColors.length; i++) {
                    final double fragment = i * stepValue;
                    steps.add(new AbstractMap.SimpleEntry(fragment, paletteColors[i]));
                }
            } catch (IOException ex) {
                Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        for(int i=0,n=steps.size();i<n;i++){
            final double k = steps.get(i).getKey();
            if(k < -0.01 || k > 1.01){
                mustInterpolation = false;
            }
        }

        //recalculate number of steps
        final int nbStep = (Integer)uiDivision.valueProperty().get().intValue();
        if(steps.size() != nbStep){
            //recalculate steps
            double min = steps.get(0).getKey();
            double max = min;
            final List<InterpolationPoint> points = new ArrayList<InterpolationPoint>();
            for(int i=0;i<steps.size();i++){
                points.add(new DefaultInterpolationPoint(steps.get(i).getKey(), getStyleFactory().literal(steps.get(i).getValue())));
                min = Math.min(min, steps.get(i).getKey());
                max = Math.max(max, steps.get(i).getKey());
            }
            Interpolate inter = getStyleFactory().interpolateFunction(DEFAULT_CATEGORIZE_LOOKUP, points,Method.COLOR, Mode.LINEAR, DEFAULT_FALLBACK);

            //rebuild steps
            steps.clear();
            for(int i=0;i<nbStep;i++){
                final double val = min + ( (max-min)/(nbStep-1) * i );
                final Color color = inter.evaluate(val, Color.class);
                steps.add(new AbstractMap.SimpleEntry(val,color));
            }
        }

        if(uiInvert.isSelected()){
            final List<Entry<Double, Color>> inverted = new ArrayList<Entry<Double, Color>>();
            for(int i=0,n=steps.size();i<n;i++){
                final double k = steps.get(i).getKey();
                inverted.add(new AbstractMap.SimpleImmutableEntry(
                        k, steps.get(n-1-i).getValue()));
            }
            steps = inverted;
        }
        if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer)layer;
            try {
                if(mustInterpolation){
                    double min = uiMinimum.valueProperty().get().doubleValue();
                    double max = uiMaximum.valueProperty().get().doubleValue();
                    lst.addAll(getInterpolationPoints(min, max, steps));
                }else{
                    for(int s=0,l=steps.size();s<l;s++){
                        final Entry<Double, Color> step = steps.get(s);
                        lst.add(new InterOrCategorize(step.getKey(), step.getValue()));
                    }
                }

            } catch (CoverageStoreException ex) {
                Loggers.JAVAFX.log(Level.INFO, ex.getMessage(),ex);
            }
        }

        uiTable.getItems().setAll(lst);

    }
    
    private List<InterOrCategorize> getInterpolationPoints(final double min, final double max, 
            List<Entry<Double, Color>> steps) throws CoverageStoreException {
        final List<InterOrCategorize> lsts = new ArrayList<>();
        for(int s=0,l=steps.size();s<l;s++){
            final Entry<Double, Color> step = steps.get(s);
            lsts.add(new InterOrCategorize(min + (step.getKey()*(max-min)), step.getValue()));
        }
        return lsts;
    }
    
    @Override
    public Class<ColorMap> getEditedClass() {
        return ColorMap.class;
    }

    @Override
    public ColorMap newValue() {
        return StyleConstants.DEFAULT_RASTER_COLORMAP;
    }

    @Override
    protected void updateEditor(ColorMap target) {
        uiTable.getItems().clear();
        if(target!=null && target.getFunction()!=null){
            function = target.getFunction().getClass();
            if(Interpolate.class.isAssignableFrom(function)){
                final List<InterpolationPoint> points = ((Interpolate)target.getFunction()).getInterpolationPoints();
                uiTable.getItems().setAll(toInterOrCategorize(points));
            }else if(Categorize.class.isAssignableFrom(function)){
                final Map<Expression,Expression> th = ((Categorize)target.getFunction()).getThresholds();
                uiTable.getItems().setAll(toInterOrCategorize(th.entrySet()));
            }else if(Jenks.class.isAssignableFrom(function)){
                final Jenks jenks = (Jenks) target.getFunction();
                uiTable.getItems().clear();
                final String paletteName = jenks.getPalette().evaluate(null, String.class);
                if (paletteName != null) {
                    uiPalette.getSelectionModel().select(paletteName);
                } else {
                    uiPalette.getSelectionModel().select(0);
                }
                uiDivision.valueProperty().set(jenks.getClassNumber().evaluate(null, Integer.class));
            }else{
                function = Interpolate.class;
                uiTable.getItems().clear();
                Loggers.JAVAFX.log(Level.WARNING, "Unknowned colormap function : {0}", function);
            }
        }else{
            //create an empty interpolate colormodel
            function = Interpolate.class;
            uiTable.getItems().clear();
        }

        postParse();
    }
    
    private static List<InterOrCategorize> toInterOrCategorize(Collection cdts){
        final List<InterOrCategorize> lst = new ArrayList<>();
        for(Object obj : cdts){
            if(obj instanceof InterpolationPoint){
                final InterpolationPoint ip = (InterpolationPoint) obj;
                lst.add(new InterOrCategorize(getFilterFactory().literal(ip.getData()), ip.getValue()));
            }else if(obj instanceof Entry){
                final Entry<Expression,Expression> step = (Entry<Expression,Expression>) obj;
                lst.add(new InterOrCategorize(step.getKey(), step.getValue()));
            }
        }
        return lst;
    }
    
    private void postParse(){
        uiInvert.setDisable(false);
        uiMinimum.getNumberField().setEditable(true);
        uiMaximum.getNumberField().setEditable(true);

        if(Interpolate.class.isAssignableFrom(function)){
            uiMethod.getSelectionModel().select(Interpolate.class);
            uiPalette.setItems(FXCollections.observableList(FXUtilities.PALETTES));
            value2Col.setVisible(false);

            final List<InterOrCategorize> ips = uiTable.getItems();
            //restore NaN and min/max values
            boolean hasNaN = false;
            double min = Double.NaN;
            double max = Double.NaN;
            for(int i=0,n=ips.size();i<n;i++){
                final double v = ips.get(i).value.get().evaluate(null, Double.class);
                if(!Double.isNaN(v)){
                    min = Double.isNaN(min) ? v : Math.min(v, min);
                    max = Double.isNaN(max) ? v : Math.max(v, max);
                }else{
                    hasNaN = true;
                }
            }
            if(!Double.isNaN(min)){
                uiMinimum.valueProperty().set(min);
                uiMaximum.valueProperty().set(max);
            }
            uiDivision.valueProperty().set(ips.size()-(hasNaN?1:0));
            uiNaN.setSelected(hasNaN);

        }else if(Categorize.class.isAssignableFrom(function)){
            uiMethod.getSelectionModel().select(Categorize.class);
            uiPalette.setItems(FXCollections.observableList(FXUtilities.PALETTES));
            value2Col.setVisible(true);

            final List<InterOrCategorize> ips = uiTable.getItems();
            //restore NaN and min/max values
            boolean hasNaN = false;
            double min = Double.NaN;
            double max = Double.NaN;
            for(int i=0,n=ips.size();i<n;i++){
                final InterOrCategorize exps = ips.get(i);
                Object val = exps.value.get().evaluate(n, Number.class);
                if(val instanceof Number){
                    final double v = ((Number)val).doubleValue();
                    if(!Double.isNaN(v)){
                        if(!Double.isInfinite(v)){
                            min = Double.isNaN(min) ? v : Math.min(v, min);
                            max = Double.isNaN(max) ? v : Math.max(v, max);
                        }
                    }else{
                        hasNaN = true;
                    }
                }
            }
            if(!Double.isNaN(min)){
                uiMinimum.valueProperty().set(min);
                uiMaximum.valueProperty().set(max);
            }
            uiDivision.valueProperty().set(ips.size()-(hasNaN?2:1));
            uiNaN.setSelected(hasNaN);

        }else if(Jenks.class.isAssignableFrom(function)){
            uiMethod.getSelectionModel().select(Jenks.class);
            uiPalette.setItems(FXCollections.observableList(FXUtilities.PALETTES_NAMED));
            uiInvert.setDisable(true);
            uiMinimum.setDisable(true);
            uiMaximum.setDisable(true);
        }

        //disable and hide value table for jenks method
        final boolean isJenks = (function == Jenks.class);
        uiAddOne.setVisible(!isJenks);
        uiRemoveAll.setVisible(!isJenks);
        uiTable.setVisible(!isJenks);
        uiDynamic.setVisible(isJenks);
        //uiNoData.setVisible(function instanceof Jenks);
        //noDataContainer.setVisible(function instanceof Jenks);

        final boolean da = !(layer instanceof CoverageMapLayer);
        uiPalette.setDisable(da);
        uiPaletteLbl.setDisable(da);
        uiBand.setDisable(da);
        uiBandLbl.setDisable(da);
        uiNaN.setDisable(da);
        uiInvert.setDisable(da);
        uiGenerate.setDisable(da);
        uiDivision.setDisable(da);
        uiDivisionLbl.setDisable(da);
    }
    
    private void initBandSpinner() {
        //update nbBands spinner
        try {
            if (layer instanceof CoverageMapLayer) {
                final CoverageReference covRef = ((CoverageMapLayer) layer).getCoverageReference();
                final GridCoverageReader reader = covRef.acquireReader();
                final GeneralGridGeometry gridGeometry = reader.getGridGeometry(covRef.getImageIndex());

                if (gridGeometry.isDefined(GeneralGridGeometry.GRID_TO_CRS)
                        && gridGeometry.isDefined(GeneralGridGeometry.EXTENT)) {
                    MathTransform gridToCRS = gridGeometry.getGridToCRS();
                    GridEnvelope extent = gridGeometry.getExtent();
                    int dim = extent.getDimension();
                    double[] low = new double[dim];
                    double[] high = new double[dim];
                    low[0] = extent.getLow(0);
                    high[0] = extent.getHigh(0);
                    low[1] = extent.getLow(1);
                    high[1] = extent.getHigh(1);
                    GeneralEnvelope sliceExtent = new GeneralEnvelope(gridGeometry.getCoordinateReferenceSystem());
                    for (int i = 0; i < dim; i++) {
                        sliceExtent.setRange(i, low[i], high[i]);
                    }

                    GridCoverageReadParam readParam = new GridCoverageReadParam();
                    readParam.setEnvelope(CRS.transform(gridToCRS, sliceExtent));
                    readParam.setCoordinateReferenceSystem(gridGeometry.getCoordinateReferenceSystem());

                    final GridCoverage coverage = reader.read(covRef.getImageIndex(), readParam);
                    final int nbBands = coverage.getNumSampleDimensions() - 1;
                    uiBand.minValueProperty().set(0);
                    uiBand.maxValueProperty().set(nbBands);
                }
                covRef.recycle(reader);
            }
        } catch (CoverageStoreException ex) {
            Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
        } catch (DataStoreException ex) {
            Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
        } catch (TransformException ex) {
            Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        initBandSpinner();
    }
    
    @Override
    public void initialize() {
        super.initialize();
        
        uiNoData.setVisible(false);
        uiDynamic.setVisible(false);
        //TODO no data editor
        
        uiDivision.getNumberField().setNumberType(NumberField.NumberType.Integer);
        uiDivision.valueProperty().set(10);        
        uiBand.getNumberField().setNumberType(NumberField.NumberType.Integer);
        uiBand.valueProperty().set(0);
        uiMinimum.valueProperty().set(0);
        uiMaximum.valueProperty().set(1);
        uiNaN.setSelected(true);
        
        uiPalette.setItems(FXCollections.observableList(FXUtilities.PALETTES));        
        uiPalette.setCellFactory((ListView<Object> param) -> new FXPaletteCell());
        uiPalette.setButtonCell((new FXPaletteCell()));
        
        final List<Class> methods = new ArrayList<Class>();
        methods.add(Interpolate.class);
        methods.add(Categorize.class);
        methods.add(Jenks.class);
        
        uiMethod.setItems(FXCollections.observableList(methods));        
        uiMethod.setCellFactory((ListView<Class> param) -> new ListCell<Class>(){            
            @Override
            protected void updateItem(Class item, boolean empty) {
                if(item instanceof Class){
                    setText(((Class)item).getSimpleName());
                }else{
                    setText("");
                }  
            }
        });
        uiMethod.setButtonCell(new ListCell<Class>(){            
            @Override
            protected void updateItem(Class item, boolean empty) {
                if(item instanceof Class){
                    setText(((Class)item).getSimpleName());
                }else{
                    setText("");
                }  
            }
        });
        function = Interpolate.class;
        uiMethod.getSelectionModel().select(Interpolate.class);
        
        uiTable.setItems(FXCollections.observableArrayList());
//        uiTable.getColumns().add(value1Col);
//        uiTable.getColumns().add(value2Col);
//        uiTable.getColumns().add(colorCol);
        
        uiTable.itemsProperty().addListener((ObservableValue<? extends ObservableList<InterOrCategorize>> observable, ObservableList<InterOrCategorize> oldValue, ObservableList<InterOrCategorize> newValue) -> {
            valueProperty().set(buildColorMap());
        });
        
    }
        
    private ColorMap buildColorMap(){
          
        
        return null;
    }
    
    
    private static class InterOrCategorize {
        private final ObjectProperty<Expression> value = new SimpleObjectProperty<>();
        private final ObjectProperty<Expression> color = new SimpleObjectProperty<>();

        public InterOrCategorize() {
        }
        
        public InterOrCategorize(Double value, Color color) {
            this.value.set(getFilterFactory().literal(value));
            this.color.set(getStyleFactory().literal(color));
        }
        
        public InterOrCategorize(Expression value, Expression color) {
            this.value.set(value);
            this.color.set(color);
        }
        
        public InterOrCategorize(Map.Entry<Expression,Expression> entry) {
            this.value.set(entry.getKey());
            this.value.set(entry.getValue());
        }
        
        
    }
    
    private static class Value1Column extends TableColumn<InterOrCategorize, Expression>{
        public Value1Column() {            
            setCellValueFactory((CellDataFeatures<InterOrCategorize, Expression> param) -> (ObservableValue)param.getValue().value);
        }
        
    }
    
    private static class Value2Column extends TableColumn<InterOrCategorize, Expression>{
        public Value2Column() {            
            setCellValueFactory((TableColumn.CellDataFeatures<InterOrCategorize, Expression> param) -> (ObservableValue)param.getValue().value);            
        }
        
    }
    
    private static class ColorColumn extends TableColumn<InterOrCategorize, Expression>{
        public ColorColumn() {            
            setCellValueFactory((TableColumn.CellDataFeatures<InterOrCategorize, Expression> param) -> (ObservableValue)param.getValue().color);            
        }
        
    }
    
    
}
