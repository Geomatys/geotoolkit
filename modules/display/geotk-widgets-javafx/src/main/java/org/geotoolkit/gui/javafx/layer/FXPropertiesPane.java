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

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.map.MapItem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXPropertiesPane extends BorderPane{

    private final TabPane tabPane = new TabPane();

    public FXPropertiesPane(MapItem item, FXPropertyPane ... propertyPanes) {
        setCenter(tabPane);

        for(FXPropertyPane pane : propertyPanes){
            if(pane.init(item)){
                final Tab tab = new Tab(pane.getTitle());
                tab.setClosable(false);
                tab.setContent(pane);
                tabPane.getTabs().add(tab);
            }
        }

    }

}
