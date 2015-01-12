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
import javafx.fxml.FXML;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.PolygonSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXPolygonSymbolizer extends FXStyleElementController<FXPolygonSymbolizer,PolygonSymbolizer> {
    
    @FXML
    protected FXFill uiFill;    
    @FXML
    protected FXStroke uiStroke;

    
    @Override
    public Class<PolygonSymbolizer> getEditedClass() {
        return PolygonSymbolizer.class;
    }

    @Override
    public PolygonSymbolizer newValue() {
        return StyleConstants.DEFAULT_POLYGON_SYMBOLIZER;
    }
    
    @Override
    public void initialize() {
        super.initialize();
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            final PolygonSymbolizer symbolizer;
            symbolizer = getStyleFactory().polygonSymbolizer(uiStroke.valueProperty().get(), uiFill.valueProperty().get(), null);
            value.set(symbolizer);
        };
        uiFill.valueProperty().addListener(changeListener);
        uiStroke.valueProperty().addListener(changeListener);
    }
    
    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiFill.setLayer(layer);
        uiStroke.setLayer(layer);
    }
    
    @Override
    protected void updateEditor(PolygonSymbolizer styleElement) {
        uiFill.valueProperty().setValue(styleElement.getFill());
        uiStroke.valueProperty().setValue(styleElement.getStroke());
    }

}
