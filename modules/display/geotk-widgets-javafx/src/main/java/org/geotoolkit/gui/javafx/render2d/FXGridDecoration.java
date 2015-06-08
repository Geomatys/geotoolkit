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
package org.geotoolkit.gui.javafx.render2d;

import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;

/**
 * Map decoration with an empty component panel.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXGridDecoration extends GridPane implements FXMapDecoration {

    protected FXMap map = null;

    public FXGridDecoration(){
        setBackground(new Background(new BackgroundFill(null,null,null)));
    }

    @Override
    public void refresh() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setMap2D(FXMap map) {
        this.map = map;
    }

    @Override
    public FXMap getMap2D() {
        return map;
    }

    @Override
    public Node getComponent() {
        return this;
    }


}
