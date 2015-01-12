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
package org.geotoolkit.gui.javafx.render2d;

import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;

/**
 * Color Decoration
 * 
 * @author Johann Sorel
 * @module pending
 */
public class FXColorDecoration extends Pane implements FXMapDecoration{

    public FXColorDecoration(){
        super();
        setBackground(new Background(new BackgroundFill(new javafx.scene.paint.Color(0, 0, 0, 0), null,null)));        
    }
    
    @Override
    public void refresh() {
    }

    @Override
    public Node getComponent() {
        return this;
    }

    @Override
    public void setMap2D(final FXMap map) {
        
    }

    @Override
    public FXMap getMap2D() {
        return null;
    }

    @Override
    public void dispose() {
    }

}
