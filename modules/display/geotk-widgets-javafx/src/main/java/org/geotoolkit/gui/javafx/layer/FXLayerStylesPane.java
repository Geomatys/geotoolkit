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

package org.geotoolkit.gui.javafx.layer;

import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import org.geotoolkit.internal.GeotkFXBundle;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLayerStylesPane extends FXPropertyPane{
    
    private final SplitPane split = new SplitPane();
    private final ListView views = new ListView();
    private final FXLayerStylePane[] editors;
    
    public FXLayerStylesPane(FXLayerStylePane ... styleEditors) {
        this.editors = styleEditors;
        split.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setCenter(split);
        
        split.getItems().add(views);
        
        if(styleEditors.length>0){
            split.getItems().add(styleEditors[0]);
        }
        
    }

    @Override
    public String getTitle() {
        return GeotkFXBundle.getString(this,"style");
    }

    @Override
    public boolean init(Object candidate) {
        if(!(candidate instanceof MapLayer)) return false;
        for(FXLayerStylePane editor : editors){
            editor.init(candidate);
        }
        return true;
    }
    
}
