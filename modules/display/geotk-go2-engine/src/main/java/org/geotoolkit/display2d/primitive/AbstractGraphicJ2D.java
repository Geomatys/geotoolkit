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

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.primitive.AbstractReferencedGraphic2D;
import org.geotoolkit.display2d.canvas.RenderingContext2D;

import org.opengis.display.primitive.Graphic;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * Base class for Geotoolkit implementations of {@link Graphic}
 * primitives in java2d. This implementation is designed for use with
 * {@link org.geotoolkit.display2d.canvas.J2DCanvas}.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractGraphicJ2D extends AbstractReferencedGraphic2D implements GraphicJ2D {

    /**
     * Constructs a new graphic using the specified objective CRS.
     *
     * @param  crs The objective coordinate reference system.
     * @throws IllegalArgumentException if {@code crs} is null or has an incompatible number of
     *         dimensions.
     */
    protected AbstractGraphicJ2D(final ReferencedCanvas2D canvas,final CoordinateReferenceSystem crs)
            throws IllegalArgumentException{
        super(canvas,crs);
    }

    /**
     * Called by the J2DCanvas to ask the graphic object to paint itself on the canvas
     * using the rendering context parameters.
     *
     * @param context : a rendering context 2d that provides a Graphics2D object
     * and all necessary parameters.
     */
    @Override
    public abstract void paint(final RenderingContext2D context);

    @Override
    public Object getUserObject(){
        return null;
    }
    
}
