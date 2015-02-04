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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javax.measure.unit.Unit;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.map.MapLayer;
import static org.geotoolkit.style.StyleConstants.DEFAULT_CONTRAST_ENHANCEMENT;
import org.opengis.filter.expression.Expression;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.Description;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXRasterSymbolizer extends FXStyleElementController<RasterSymbolizer>{
    
    @FXML private RadioButton uiChoiceColorNone;
    @FXML private RadioButton uiChoiceColorRGB;
    @FXML private RadioButton uiChoiceColorMap;
    @FXML private BorderPane uiColorPane;

    @FXML private RadioButton uiChoiceOutlineNone;
    @FXML private RadioButton uiChoiceOutlineLine;
    @FXML private RadioButton uiChoiceOutlinePolygon;
    @FXML private BorderPane uiOutlinePane;

    @FXML private FXNumberExpression uiOpacity;
    @FXML private FXShadedRelief uiReliefShading;
    @FXML private FXContrastEnhancement uiContrast;
    @FXML private FXSymbolizerInfo uiInfo;
    @FXML private TabPane uiTabs;

    private FXChannelSelection uiChannelSelection;
    private FXColorMap uiColorMap;
    private FXLineSymbolizer uiLineSymbolizer;
    private FXPolygonSymbolizer uiPolygonSymbolizer;

    @Override
    public void initialize() {
        super.initialize();

        uiChannelSelection = new FXChannelSelection();
        uiColorMap = new FXColorMap();
        uiLineSymbolizer = new FXLineSymbolizer();
        uiPolygonSymbolizer = new FXPolygonSymbolizer();


        final ToggleGroup groupColor = new ToggleGroup();
        uiChoiceColorNone.setToggleGroup(groupColor);
        uiChoiceColorRGB.setToggleGroup(groupColor);
        uiChoiceColorMap.setToggleGroup(groupColor);
        uiChoiceColorNone.setSelected(true);

        final ToggleGroup groupOutline = new ToggleGroup();
        uiChoiceOutlineNone.setToggleGroup(groupOutline);
        uiChoiceOutlineLine.setToggleGroup(groupOutline);
        uiChoiceOutlinePolygon.setToggleGroup(groupOutline);
        uiChoiceOutlineNone.setSelected(true);


        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            value.set(create());
        };

        uiOpacity.valueProperty().addListener(changeListener);
        uiReliefShading.valueProperty().addListener(changeListener);
        uiContrast.valueProperty().addListener(changeListener);
        uiInfo.valueProperty().addListener(changeListener);
        uiLineSymbolizer.valueProperty().addListener(changeListener);
        uiPolygonSymbolizer.valueProperty().addListener(changeListener);
        uiChannelSelection.valueProperty().addListener(changeListener);
        uiColorMap.valueProperty().addListener(changeListener);
    }

    @FXML
    private void updateColorChoice(ActionEvent event) {
        if(uiChoiceColorNone.isSelected()){
            Platform.runLater(() -> {
                uiColorPane.setCenter(null);
                uiColorPane.autosize();
                uiTabs.autosize();
                if(updating) return;
                value.set(create());
                });
        }else if(uiChoiceColorRGB.isSelected()){
            Platform.runLater(() -> {
                uiColorPane.setCenter(uiChannelSelection);
                uiColorPane.autosize();
                uiTabs.autosize();
                if(updating) return;
                value.set(create());
                });
        }else if(uiChoiceColorMap.isSelected()){
            Platform.runLater(() -> {
                uiColorPane.setCenter(uiColorMap);
                if(uiColorMap.valueProperty().get()==null){
                    uiColorMap.valueProperty().set(uiColorMap.newValue());
                }
                uiColorPane.autosize();
                uiTabs.autosize();
                if(updating) return;
                value.set(create());
                });
        }
    }

    @FXML
    private void updateOutlineChoice(ActionEvent event) {
        if(uiChoiceOutlineNone.isSelected()){
            Platform.runLater(() -> {
                uiOutlinePane.setCenter(null);
                uiOutlinePane.autosize();
                uiTabs.autosize();
                if(updating) return;
                value.set(create());
                });
        }else if(uiChoiceOutlineLine.isSelected()){
            Platform.runLater(() -> {
                uiOutlinePane.setCenter(uiLineSymbolizer);
                if(uiLineSymbolizer.valueProperty().get()==null){
                    uiLineSymbolizer.valueProperty().set(uiLineSymbolizer.newValue());
                }
                uiOutlinePane.autosize();
                uiTabs.autosize();
                if(updating) return;
                value.set(create());
                });
        }else if(uiChoiceOutlinePolygon.isSelected()){
            Platform.runLater(() -> {
                uiOutlinePane.setCenter(uiPolygonSymbolizer);
                if(uiPolygonSymbolizer.valueProperty().get()==null){
                    uiPolygonSymbolizer.valueProperty().set(uiPolygonSymbolizer.newValue());
                }
                uiOutlinePane.autosize();
                uiTabs.autosize();
                if(updating) return;
                value.set(create());
                });
        }
    }
    
    @Override
    public Class<RasterSymbolizer> getEditedClass() {
        return RasterSymbolizer.class;
    }

    @Override
    public RasterSymbolizer newValue() {
        return getStyleFactory().rasterSymbolizer();
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiOpacity.setLayer(layer);
        uiReliefShading.setLayer(layer);
        uiContrast.setLayer(layer);
        uiChannelSelection.setLayer(layer);
        uiColorMap.setLayer(layer);
    }

    public RasterSymbolizer create() {
        Symbolizer outline = null;
        if(uiChoiceOutlineLine.isSelected()){
            outline = uiLineSymbolizer.valueProperty().get();
        }else if(uiChoiceOutlinePolygon.isSelected()){
            outline = uiPolygonSymbolizer.valueProperty().get();
        }

        final ChannelSelection chanSelect;
        final ColorMap colorMap;
        if(uiChoiceColorRGB.isSelected()){
            chanSelect = uiChannelSelection.valueProperty().get();
            colorMap = null;
        }else if(uiChoiceColorMap.isSelected()){
            chanSelect = getStyleFactory().channelSelection(getStyleFactory()
                    .selectedChannelType(""+uiColorMap.getSelectedBand(),DEFAULT_CONTRAST_ENHANCEMENT));
            colorMap = uiColorMap.valueProperty().get();
        }else{
            chanSelect = null;
            colorMap = null;
        }

        final String name = uiInfo.getName();
        final Description desc = uiInfo.getDescription();
        final Unit uom = uiInfo.getUnit();
        final Expression geom = uiInfo.getGeom();
        return getStyleFactory().rasterSymbolizer(
                name,geom,desc,uom,
                uiOpacity.valueProperty().get(),
                chanSelect,
                OverlapBehavior.AVERAGE,
                colorMap,
                uiContrast.valueProperty().get(),
                uiReliefShading.valueProperty().get(),
                outline);

    }

    @Override
    protected void updateEditor(RasterSymbolizer rs) {
        uiInfo.parse(rs);
        uiOpacity.valueProperty().set(rs.getOpacity());
        uiReliefShading.valueProperty().set(rs.getShadedRelief());
        uiContrast.valueProperty().set(rs.getContrastEnhancement());

        if(rs.getColorMap()!=null && rs.getColorMap().getFunction()!=null){
            uiChoiceColorMap.setSelected(true);
            uiColorMap.valueProperty().set(rs.getColorMap());
        }else if(rs.getChannelSelection()!=null){
            uiChoiceColorRGB.setSelected(true);
            uiChannelSelection.valueProperty().set(rs.getChannelSelection());
        }else{
            uiChoiceColorNone.setSelected(true);
        }

        final Symbolizer sym = rs.getImageOutline();
        if(sym instanceof LineSymbolizer){
            uiChoiceOutlineLine.setSelected(true);
            uiLineSymbolizer.valueProperty().set((LineSymbolizer)sym);
        }else if(sym instanceof PolygonSymbolizer){
            uiChoiceOutlinePolygon.setSelected(true);
            uiPolygonSymbolizer.valueProperty().set((PolygonSymbolizer)sym);
        }else{
            uiChoiceOutlineNone.setSelected(true);
        }
        
    }
    
}
