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
package org.geotoolkit.gui.javafx.render2d.edition;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.FeatureMapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CreatePointTool extends AbstractEditionTool{

    public static final class Spi extends AbstractEditionToolSpi{

        public Spi() {
            super("CreatePoint",
                GeotkFX.getI18NString(CreatePointTool.class, "title"),
                GeotkFX.getI18NString(CreatePointTool.class, "abstract"),
                GeotkFX.ICON_ADD);
        }
    
        @Override
        public boolean canHandle(Object candidate) {
            if(candidate instanceof FeatureMapLayer){
                final FeatureMapLayer fml = (FeatureMapLayer) candidate;
                return fml.getCollection().isWritable();
            }
            return false;
        }

        @Override
        public EditionTool create() {
            return new CreatePointTool();
        }
    };


    private final BorderPane configPane = new BorderPane();
    private final BorderPane helpPane = new BorderPane();

    public CreatePointTool() {
        super(EditionHelper.getToolSpi("CreatePoint"));
    }

    @Override
    public Node getConfigurationPane() {
        return configPane;
    }

    @Override
    public Node getHelpPane() {
        return helpPane;
    }

}
