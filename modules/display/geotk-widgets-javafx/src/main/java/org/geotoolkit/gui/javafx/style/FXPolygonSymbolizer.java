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
import javafx.scene.control.Label;
import javafx.scene.control.SpinnerValueFactory;
import javax.measure.Unit;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.style.PolygonSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXPolygonSymbolizer extends FXStyleElementController<PolygonSymbolizer> {

    private static final double LIMIT = 1000000000;

    @FXML private FXSymbolizerInfo uiInfo;
    @FXML protected FXFill uiFill;
    @FXML protected FXStroke uiStroke;
    @FXML protected FXDisplacement uiDisplacement;
    @FXML private Label uiOffsetLbl;
    @FXML private FXNumberExpression uiOffset;


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

        uiOffset.getEditor().getSpinner().setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(-LIMIT, +LIMIT, 0, 1));

        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            final String name = uiInfo.getName();
            final Description desc = uiInfo.getDescription();
            final Unit uom = uiInfo.getUnit();
            final Expression geom = uiInfo.getGeom();
            value.set(getStyleFactory().polygonSymbolizer(
                    name,geom,desc,uom,
                    uiStroke.valueProperty().get(),
                    uiFill.valueProperty().get(),
                    uiDisplacement.valueProperty().get(),
                    uiOffset.valueProperty().get()));
        };
        uiFill.valueProperty().addListener(changeListener);
        uiStroke.valueProperty().addListener(changeListener);
        uiInfo.valueProperty().addListener(changeListener);
        uiOffset.valueProperty().addListener(changeListener);
        uiDisplacement.valueProperty().addListener(changeListener);

        FXStyleElementController.configureAdvancedProperty(
                uiOffset,uiOffsetLbl);
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiFill.setLayer(layer);
        uiStroke.setLayer(layer);
        uiInfo.setLayer(layer);
        uiOffset.setLayer(layer);
        uiDisplacement.setLayer(layer);
    }

    @Override
    protected void updateEditor(PolygonSymbolizer styleElement) {
        uiFill.valueProperty().setValue(styleElement.getFill());
        uiStroke.valueProperty().setValue(styleElement.getStroke());
        uiInfo.parse(styleElement);
        uiOffset.valueProperty().set(styleElement.getPerpendicularOffset());
        uiDisplacement.valueProperty().set(styleElement.getDisplacement());
    }

}
