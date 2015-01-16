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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.style.MutableRule;
import org.opengis.style.Description;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXRule extends FXStyleElementController<MutableRule> {
    
    @FXML
    protected TextField uiName;
    
    @FXML
    protected TextField uiTitle;
    
    @FXML
    protected TextField uiAbstract;

    @FXML
    protected CheckBox uiIsElse;
    
    @FXML
    protected Button uiFilterButton;

    @FXML
    protected TextField uiMinScale;
    
    @FXML
    protected TextField uiMaxScale;
    
    
    @Override
    public Class<MutableRule> getEditedClass() {
        return MutableRule.class;
    }

    @Override
    public MutableRule newValue() {
        return getStyleFactory().rule();
    }

    @Override
    public void initialize() {
        super.initialize();
        
        uiName.setOnAction((ActionEvent event) -> {
            value.get().setName(uiName.getText());
        });
        uiTitle.setOnAction((ActionEvent event) -> {
            final Description oldDesc = value.get().getDescription();
            InternationalString title = new SimpleInternationalString(uiTitle.getText());
            InternationalString abs = (oldDesc!=null) ? oldDesc.getAbstract() : null;
            value.get().setDescription(getStyleFactory().description(title, abs));
        });
        uiAbstract.setOnAction((ActionEvent event) -> {
            final Description oldDesc = value.get().getDescription();
            InternationalString title = (oldDesc!=null) ? oldDesc.getTitle() : null;
            InternationalString abs = new SimpleInternationalString(uiAbstract.getText());
            value.get().setDescription(getStyleFactory().description(title, abs));
        });
        uiMaxScale.setOnAction((ActionEvent event) -> {
            String text = uiMaxScale.getText();
            if(text.isEmpty()){
                text = ""+Double.MAX_VALUE;
            }
            double val = Double.MAX_VALUE;
            try{
                val = Double.parseDouble(text);
            }catch(NumberFormatException ex){   }
            
            value.get().setMaxScaleDenominator(val);
        });
        uiMinScale.setOnAction((ActionEvent event) -> {
            String text = uiMinScale.getText();
            if(text.isEmpty()){
                text = "0.0";
            }
            double val = 0.0;
            try{
                val = Double.parseDouble(text);
            }catch(NumberFormatException ex){   }
            
            value.get().setMinScaleDenominator(val);
        });
        uiIsElse.setOnAction((ActionEvent event) -> {
            value.get().setElseFilter(uiIsElse.isSelected());
        });
        
    }

    @Override
    protected void updateEditor(MutableRule styleElement) {
                
        final Description desc = value.get().getDescription();
        uiTitle.setText(desc!=null && desc.getTitle()!=null ? desc.getTitle().toString() : "");
        uiAbstract.setText(desc!=null && desc.getAbstract()!=null ? desc.getAbstract().toString() : "");
        uiName.setText(value.get().getName());
        uiIsElse.setSelected(value.get().isElseFilter());
             
    }
    
}
