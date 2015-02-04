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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.ChannelSelection;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXChannelSelection extends FXStyleElementController<ChannelSelection>{

    @FXML protected FXSelectedChannelType uiRed;
    @FXML protected FXSelectedChannelType uiGreen;
    @FXML protected FXSelectedChannelType uiBlue;
    @FXML protected FXSelectedChannelType uiGray;

    @FXML protected Label uiRedLbl;
    @FXML protected Label uiGreenLbl;
    @FXML protected Label uiBlueLbl;
    @FXML protected Label uiGrayLbl;

    @FXML protected RadioButton uiNone;
    @FXML protected RadioButton uiRGB;
    @FXML protected RadioButton uiSingle;
    private ToggleGroup group;
    
    @FXML
    void updateTypeChoice(ActionEvent event) {
        if(uiNone.isSelected()){
            uiRed.setVisible(false);
            uiGreen.setVisible(false);
            uiBlue.setVisible(false);
            uiGray.setVisible(false);
            uiRedLbl.setVisible(false);
            uiGreenLbl.setVisible(false);
            uiBlueLbl.setVisible(false);
            uiGrayLbl.setVisible(false);
        }else if(uiRGB.isSelected()){
            uiRed.setVisible(true);
            uiGreen.setVisible(true);
            uiBlue.setVisible(true);
            uiGray.setVisible(false);
            uiRedLbl.setVisible(true);
            uiGreenLbl.setVisible(true);
            uiBlueLbl.setVisible(true);
            uiGrayLbl.setVisible(false);       
        }else if(uiSingle.isSelected()){
            uiRed.setVisible(false);
            uiGreen.setVisible(false);
            uiBlue.setVisible(false);
            uiGray.setVisible(true);
            uiRedLbl.setVisible(false);
            uiGreenLbl.setVisible(false);
            uiBlueLbl.setVisible(false);
            uiGrayLbl.setVisible(true);
        }
    }
    
    @Override
    public Class<ChannelSelection> getEditedClass() {
        return ChannelSelection.class;
    }

    @Override
    public ChannelSelection newValue() {
        return StyleConstants.DEFAULT_RASTER_CHANNEL_RGB;
    }
    
    @Override
    public void initialize() {
        super.initialize();

        group = new ToggleGroup();
        uiNone.setToggleGroup(group);
        uiRGB.setToggleGroup(group);
        uiSingle.setToggleGroup(group);
        
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            if(uiRGB.isSelected()){
                value.set(getStyleFactory().channelSelection(
                        uiRed.valueProperty().get(),
                        uiGreen.valueProperty().get(),
                        uiBlue.valueProperty().get()));
            }else if(uiSingle.isSelected()){
                value.set(getStyleFactory().channelSelection(
                        uiGray.valueProperty().get()));
            }else{
                value.set(null);
            }
        };
        
        uiRed.valueProperty().addListener(changeListener);
        uiGreen.valueProperty().addListener(changeListener);
        uiBlue.valueProperty().addListener(changeListener);
        uiGray.valueProperty().addListener(changeListener);
    }
    
    @Override
    protected void updateEditor(ChannelSelection styleElement) {
        
        if(styleElement == null || (styleElement.getGrayChannel()==null && styleElement.getRGBChannels()==null) ){
            uiGray.valueProperty().set(null);
            uiRed.valueProperty().set(null);
            uiGreen.valueProperty().set(null);
            uiBlue.valueProperty().set(null);
        }else if(styleElement.getGrayChannel()!=null){
            uiGray.valueProperty().setValue(styleElement.getGrayChannel());
        }else if(styleElement.getRGBChannels()!=null){
            uiRed.valueProperty().setValue(styleElement.getRGBChannels()[0]);
            uiGreen.valueProperty().setValue(styleElement.getRGBChannels()[1]);
            uiBlue.valueProperty().setValue(styleElement.getRGBChannels()[2]);
        }
    }
    
    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiRed.setLayer(layer);
        uiGreen.setLayer(layer);
        uiBlue.setLayer(layer);
        uiGray.setLayer(layer);
    }
    
}
