/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.service;

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.J2DCanvas;

/**
 * Provide the possibility to extend the work of the portrayal service.
 * This can used to add several graphic object or decorations on the canvas
 * before it is renderer.
 * 
 * Don't call repainting or refreshing actions on the canvas in here. This will 
 * be called at the last moment in the portrayal service.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface PortrayalExtension {

    void completeCanvas(J2DCanvas canvas) throws PortrayalException;

}
