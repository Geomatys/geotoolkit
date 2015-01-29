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
package org.geotoolkit.gui.javafx.util;

import java.util.function.Consumer;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.geotoolkit.internal.GeotkFX;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXEditTableColumn<T,R> extends TableColumn<T,R>{

    public FXEditTableColumn(Consumer editFct) {
        super("Edition");
        setSortable(false);
        setResizable(false);
        setPrefWidth(24);
        setMinWidth(24);
        setMaxWidth(24);
        setGraphic(new ImageView(GeotkFX.ICON_EDIT));

        setCellValueFactory(new Callback<CellDataFeatures<T, R>, ObservableValue<R>>() {

            @Override
            public ObservableValue<R> call(CellDataFeatures<T, R> param) {
                return new SimpleObjectProperty(param.getValue());
            }
        });

        setCellFactory((TableColumn<T,R> param) -> new ButtonTableCell(
                false,new ImageView(GeotkFX.ICON_EDIT), (Object t) -> true, new Function<Object, Object>() {
            @Override
            public Object apply(Object t) {
                editFct.accept(t);
                return t;
            }
        }));
    }
}
