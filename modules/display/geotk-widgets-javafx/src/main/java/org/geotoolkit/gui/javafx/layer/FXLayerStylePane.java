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

import javafx.scene.layout.BorderPane;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyle;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class FXLayerStylePane extends BorderPane{

    public String getTitle() {
        return "";
    }

    public String getCategory(){
        return "";
    }

    public abstract MutableStyle getMutableStyle();

    public abstract boolean init(MapLayer layer, Object rootStyleElement);

}
