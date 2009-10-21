/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.primitive;

import org.geotoolkit.display.primitive.ReferencedGraphic2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;

/**
 * Base class for Geotoolkit implementations of {@link org.opengis.go.display.primitive.Graphic}
 * primitives in java2d. This implementation is designed for use with
 * {@link org.geotoolkit.display2d.canvas.J2DCanvas}.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface GraphicJ2D extends ReferencedGraphic2D {

    /**
     * Called by the J2DCanvas to ask the graphic object to paint itself on the canvas
     * using the rendering context parameters.
     *
     * @param context : a rendering context 2d that provides a Graphics2D object
     * and all necessary parameters.
     */
    public abstract void paint(final RenderingContext2D context);

    public Object getUserObject();
    
}
