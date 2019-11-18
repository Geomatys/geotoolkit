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

import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.apache.sis.cql.CQLException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.gui.javafx.filter.FXCQLEditor;
import org.geotoolkit.style.MutableRule;
import org.opengis.filter.Filter;
import org.opengis.style.Description;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXRule extends FXStyleElementController<MutableRule> {

    @FXML protected TextField uiName;
    @FXML protected TextField uiTitle;
    @FXML protected TextField uiAbstract;
    @FXML protected CheckBox uiIsElse;
    @FXML protected Button uiFilterButton;
    @FXML protected TextField uiMinScale;
    @FXML protected TextField uiMaxScale;
    @FXML protected Label uiCQL;

    @FXML
    private void editFilter(ActionEvent event) {
        final MutableRule rule = value.get();
        if(rule==null) return;
        try {
            final Filter filter = FXCQLEditor.showFilterDialog(this, layer, rule.getFilter());
            if(filter!=null){
                rule.setFilter(filter);
                uiCQL.setText(CQL.write(filter));
            }
        } catch (CQLException | DataStoreException ex) {
            Logging.getLogger("org.geotoolkit.gui.javafx.style").log(Level.WARNING, ex.getMessage(), ex);
        }
    }

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

        uiName.setOnKeyReleased((KeyEvent event) -> {
            value.get().setName(uiName.getText());
        });
        uiTitle.setOnKeyReleased((KeyEvent event) -> {
            final Description oldDesc = value.get().getDescription();
            InternationalString title = new SimpleInternationalString(uiTitle.getText());
            InternationalString abs = (oldDesc!=null) ? oldDesc.getAbstract() : null;
            value.get().setDescription(getStyleFactory().description(title, abs));
        });
        uiAbstract.setOnKeyReleased((KeyEvent event) -> {
            final Description oldDesc = value.get().getDescription();
            InternationalString title = (oldDesc!=null) ? oldDesc.getTitle() : null;
            InternationalString abs = new SimpleInternationalString(uiAbstract.getText());
            value.get().setDescription(getStyleFactory().description(title, abs));
        });
        uiMaxScale.setOnKeyReleased((KeyEvent event) -> {
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
        uiMinScale.setOnKeyReleased((KeyEvent event) -> {
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
        uiMinScale.setText(""+styleElement.getMinScaleDenominator());
        uiMaxScale.setText(""+styleElement.getMaxScaleDenominator());

        uiCQL.setText(CQL.write(styleElement.getFilter()));

    }

}
