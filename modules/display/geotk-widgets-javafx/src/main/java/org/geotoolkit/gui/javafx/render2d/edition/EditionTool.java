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
import javafx.scene.image.Image;
import org.geotoolkit.gui.javafx.render2d.FXCanvasHandler;
import org.opengis.util.InternationalString;

/**
 * Edition tool.
 * Used to edit or create objects for a map layer.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface EditionTool extends FXCanvasHandler {

    /**
     * @return name of tool, can be used as identifier.
     * If a text is needed for user interface, use title or abstract.
     */
    String getName();

    /**
     * @return title of this tool.
     */
    InternationalString getTitle();

    /**
     * @return description of the tool.
     */
    InternationalString getAbstract();

    /**
     * A Small icon for a toolbar.
     * @return tool icon, can be null.
     */
    Image getIcon();

    /**
     * @param candidate object to test
     * @return true if the tool can edit the given object. false otherwise
     */
    boolean canHandle(Object candidate);
    
    /**
     * Tool configuration pane.
     * @return tool configuration pane, can be null.
     */
    Node getConfigurationPane();

    /**
     * Tool help pane.
     * @return tool help pane, can be null.
     */
    Node getHelpPane();

}
