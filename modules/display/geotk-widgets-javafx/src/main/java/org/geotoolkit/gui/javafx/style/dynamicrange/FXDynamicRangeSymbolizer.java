/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.style.dynamicrange;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import org.geotoolkit.display2d.ext.dynamicrange.DynamicRangeSymbolizer;
import org.geotoolkit.gui.javafx.style.FXStyleElementController;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXDynamicRangeSymbolizer extends FXStyleElementController<DynamicRangeSymbolizer> {
    
    @FXML
    private FXDRChannel uiChannelR;
    @FXML
    private FXDRChannel uiChannelG;
    @FXML
    private FXDRChannel uiChannelB;
    @FXML
    private FXDRChannel uiChannelA;
    
    @Override
    public Class<DynamicRangeSymbolizer> getEditedClass() {
        return DynamicRangeSymbolizer.class;
    }

    @Override
    public DynamicRangeSymbolizer newValue() {
        return new DynamicRangeSymbolizer();
    }

    @Override
    public void initialize() {
        super.initialize();
        
        uiChannelR.lock(DynamicRangeSymbolizer.DRChannel.BAND_RED);
        uiChannelG.lock(DynamicRangeSymbolizer.DRChannel.BAND_GREEN);
        uiChannelB.lock(DynamicRangeSymbolizer.DRChannel.BAND_BLUE);
        uiChannelA.lock(DynamicRangeSymbolizer.DRChannel.BAND_ALPHA);
        
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            final DynamicRangeSymbolizer element = new DynamicRangeSymbolizer();
            final List<DynamicRangeSymbolizer.DRChannel> channels = new ArrayList<>();
            channels.add(uiChannelR.valueProperty().get());
            channels.add(uiChannelG.valueProperty().get());
            channels.add(uiChannelB.valueProperty().get());
            channels.add(uiChannelA.valueProperty().get());
            element.setChannels(channels);
            value.set(element);
        };
        uiChannelR.valueProperty().addListener(changeListener);
        uiChannelG.valueProperty().addListener(changeListener);
        uiChannelB.valueProperty().addListener(changeListener);
        uiChannelA.valueProperty().addListener(changeListener);
    }
    
    @Override
    protected void updateEditor(DynamicRangeSymbolizer styleElement) {
        if(styleElement!=null){
            for(DynamicRangeSymbolizer.DRChannel c : styleElement.getChannels()){
                final String cs = c.getColorSpaceComponent();
                if(DynamicRangeSymbolizer.DRChannel.BAND_RED.equalsIgnoreCase(cs)){
                    uiChannelR.valueProperty().setValue(c);
                }else if(DynamicRangeSymbolizer.DRChannel.BAND_GREEN.equalsIgnoreCase(cs)){
                    uiChannelG.valueProperty().setValue(c);
                }else if(DynamicRangeSymbolizer.DRChannel.BAND_BLUE.equalsIgnoreCase(cs)){
                    uiChannelB.valueProperty().setValue(c);
                }else if(DynamicRangeSymbolizer.DRChannel.BAND_ALPHA.equalsIgnoreCase(cs)){
                    uiChannelA.valueProperty().setValue(c);
                }
            }
        }
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiChannelR.setLayer(layer);
        uiChannelG.setLayer(layer);
        uiChannelB.setLayer(layer);
        uiChannelA.setLayer(layer);
    }
    
}
