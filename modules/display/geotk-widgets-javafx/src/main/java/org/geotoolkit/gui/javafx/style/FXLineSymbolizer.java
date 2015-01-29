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

package org.geotoolkit.gui.javafx.style;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javax.measure.unit.Unit;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.style.LineSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLineSymbolizer extends FXStyleElementController<LineSymbolizer> {

    @FXML
    private FXSymbolizerInfo uiInfo;
    @FXML
    protected FXStroke uiStroke;
    @FXML
    private FXNumberExpression uiOffset;
    
    @Override
    public Class<LineSymbolizer> getEditedClass() {
        return LineSymbolizer.class;
    }
    
    @Override
    public LineSymbolizer newValue() {
        return getStyleFactory().lineSymbolizer(StyleConstants.DEFAULT_STROKE, null);
    }
    
    @Override
    public void initialize() {
        super.initialize();
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            final String name = uiInfo.getName();
            final Description desc = uiInfo.getDescription();
            final Unit uom = uiInfo.getUnit();
            final Expression geom = uiInfo.getGeom();
            value.set(getStyleFactory().lineSymbolizer(
                    name,geom,desc,uom,uiStroke.valueProperty().get(), uiOffset.valueProperty().get()));
        };
        uiStroke.valueProperty().addListener(changeListener);
        uiInfo.valueProperty().addListener(changeListener);
        uiOffset.valueProperty().addListener(changeListener);
    }
        
    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiStroke.setLayer(layer);
        uiInfo.setLayer(layer);
        uiOffset.setLayer(layer);
    }
    
    @Override
    protected void updateEditor(LineSymbolizer styleElement) {
        uiStroke.valueProperty().setValue(styleElement.getStroke());
        uiInfo.parse(styleElement);
        uiOffset.valueProperty().set(styleElement.getPerpendicularOffset());
    }

}
