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

import java.awt.Dimension;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import org.geotoolkit.gui.javafx.util.FXUtilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXPaletteCell extends ListCell<Object>{
    
    @Override
    protected void updateItem(Object item, boolean empty) {
        setText("");
        if(item!=null){
            setGraphic(new ImageView(FXUtilities.createPalettePreview(item, new Dimension((int)getWidth(), (int)getHeight()))));
        }else{
            setGraphic(null);
        }         
    }
    
}
    

