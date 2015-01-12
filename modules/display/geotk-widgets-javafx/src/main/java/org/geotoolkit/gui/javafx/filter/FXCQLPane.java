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

package org.geotoolkit.gui.javafx.filter;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.gui.javafx.contexttree.menu.LayerPropertiesItem;
import org.geotoolkit.gui.javafx.util.FXDialog;
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
        final FXCQLEditor editor = new FXCQLEditor();
        editor.setFilter(filter);
        
        final FXDialog dialog = new FXDialog();
        dialog.setContent(editor);
        dialog.getActions().add(new LayerPropertiesItem.CloseAction(dialog));
        dialog.setModal(true);
        dialog.setVisible(owner,true);
        
        return editor.getFilter();
    }
    
}
