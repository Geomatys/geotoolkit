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

import javafx.fxml.FXML;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.LineSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLineSymbolizer extends FXStyleElementController<FXLineSymbolizer,LineSymbolizer> {
    
    @FXML
    protected FXStroke uiStroke;

    @Override
    public Class<LineSymbolizer> getEditedClass() {
        return LineSymbolizer.class;
    }
    
    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public LineSymbolizer newValue() {
        return StyleConstants.DEFAULT_LINE_SYMBOLIZER;
    }
    
    @Override
    protected void updateEditor(LineSymbolizer styleElement) {
        super.updateEditor(styleElement);
        uiStroke.valueProperty().setValue(styleElement.getStroke());
    }

}
