/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.display.canvas;

import org.opengis.display.canvas.CanvasController;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractCanvasController implements CanvasController{

    /**
     * Small number for floating point comparaisons.
     */
    protected static final double EPS = 1E-12;
    
    //////////////////////////////////////////////////////////////////////////
    // Obsolete methods, TODO should be removed from geoapi //////////////////
    //////////////////////////////////////////////////////////////////////////

    @Override
    public void setTitle(InternationalString title) {
        throw new UnsupportedOperationException("Not supported. Obsolete.");
    }

}
