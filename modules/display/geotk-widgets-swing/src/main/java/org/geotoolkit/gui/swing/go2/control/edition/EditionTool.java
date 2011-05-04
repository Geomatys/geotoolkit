/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.gui.swing.go2.control.edition;

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface EditionTool {

    /**
     * @return priority : higher value will move the tool up in the EditionTools list.
     */
    int getPriority();
    
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
     * @param candidate object to test
     * @return true if the tool can edit the given object. false otherwise
     */
    boolean canHandle(Object candidate);

    /**
     * @param map : the map on which the tool will work
     * @param candidate : the object to edit
     * @return EditionDelegate : the editor object
     */
    EditionDelegate createDelegate(JMap2D map, Object candidate);

}
