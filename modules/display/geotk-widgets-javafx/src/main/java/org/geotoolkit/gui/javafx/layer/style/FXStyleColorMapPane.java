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

package org.geotoolkit.gui.javafx.layer.style;

import org.geotoolkit.gui.javafx.layer.FXLayerStylePane;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyle;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStyleColorMapPane extends FXLayerStylePane {
    
    public FXStyleColorMapPane() {
    }

    @Override
    public String getTitle() {
        return GeotkFX.getString(this,"title");
    }
    
    @Override
    public String getCategory() {
        return GeotkFX.getString(this,"category");
    }
    
    @Override
    public boolean init(Object candidate) {
        if(!(candidate instanceof MapLayer)) return false;        
        return true;
    }
    
    @Override
    public MutableStyle getMutableStyle() {
        return null;
    }
    
}
