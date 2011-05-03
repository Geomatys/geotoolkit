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
    
    String getName();

    InternationalString getTitle();

    InternationalString getAbstract();

    boolean canHandle(Object candidate);

    EditionDelegate createDelegate(JMap2D map, Object candidate);

}
