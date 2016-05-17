/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.gui.javafx.style.isoline;

import org.geotoolkit.gui.javafx.style.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.ext.isoline.symbolizer.IsolineSymbolizer;
import org.geotoolkit.map.MapLayer;

/**
 * TODO : Deactivated for now because of multiple UI artifacts. To reactivate it,
 * uncomment in following META-INF :
 * org.geotoolkit.gui.javafx.style.FXStyleElementController
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXIsolineSymbolizer extends FXStyleElementController<IsolineSymbolizer> {

    private final FXRasterSymbolizer uiRaster = new FXRasterSymbolizer();
    private final FXLineSymbolizer uiLine = new FXLineSymbolizer();
    private final FXTextSymbolizer uiText = new FXTextSymbolizer();

    public FXIsolineSymbolizer() {
        super(false);

        final TabPane tabs = new TabPane();
        tabs.getTabs().add(new Tab("Raster", uiRaster));
        tabs.getTabs().add(new Tab("Line", uiLine));
        tabs.getTabs().add(new Tab("Text", uiText));
        setCenter(tabs);


        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;

            value.set(new IsolineSymbolizer(
                    uiRaster.valueProperty().get(),
                    uiLine.valueProperty().get(),
                    uiText.valueProperty().get()));
        };
        uiRaster.valueProperty().addListener(changeListener);
        uiLine.valueProperty().addListener(changeListener);
        uiText.valueProperty().addListener(changeListener);
    }

    @Override
    public Class<IsolineSymbolizer> getEditedClass() {
        return IsolineSymbolizer.class;
    }

    @Override
    public IsolineSymbolizer newValue() {
        return new IsolineSymbolizer(
                GO2Utilities.STYLE_FACTORY.rasterSymbolizer(),
                GO2Utilities.STYLE_FACTORY.lineSymbolizer(),
                GO2Utilities.STYLE_FACTORY.textSymbolizer());
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiRaster.setLayer(layer);
        uiLine.setLayer(layer);
        uiText.setLayer(layer);
    }

    @Override
    protected void updateEditor(IsolineSymbolizer styleElement) {
        uiRaster.valueProperty().setValue(styleElement.getRasterSymbolizer());
        uiLine.valueProperty().setValue(styleElement.getLineSymbolizer());
        uiText.valueProperty().set(styleElement.getTextSymbolizer());
    }

}
