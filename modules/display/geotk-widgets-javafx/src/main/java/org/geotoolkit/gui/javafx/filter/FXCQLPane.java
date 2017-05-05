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

package org.geotoolkit.gui.javafx.filter;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.map.MapItem;
import org.opengis.filter.Filter;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXCQLPane extends BorderPane {

    public FXCQLPane() {

    }

    public static Filter show(Node owner, Filter filter, MapItem target) throws CQLException{
        final FXCQLEditor editor = new FXCQLEditor(true);
        editor.setFilter(filter);

        final Dialog dialog = new Dialog();
        final DialogPane pane = new DialogPane();
        pane.setContent(editor);
        pane.getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.initModality(Modality.APPLICATION_MODAL);

        return editor.getFilter();
    }

}
