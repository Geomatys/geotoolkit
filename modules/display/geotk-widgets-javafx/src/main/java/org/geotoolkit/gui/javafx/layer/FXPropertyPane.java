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

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXPropertyPane extends BorderPane{

    public FXPropertyPane() {
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }
    
    public String getTitle(){
        return "";
    }
    
    public Image getIcon(){
        return null;
    }
    
    public boolean init(Object candidate){
        return true;
    }
        
}
