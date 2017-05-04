/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import org.geotoolkit.display.primitive.SpatialNode;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.opengis.display.primitive.Graphic;

/**
 * Base class for GeotoolKit implementations of {@link Graphic}
 * primitives in java2d. This implementation is designed for use with
 * {@link org.geotoolkit.display2d.canvas.J2DCanvas}.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class GraphicJ2D extends SpatialNode {

    public GraphicJ2D(J2DCanvas canvas) {
        super(canvas);
    }

    public GraphicJ2D(J2DCanvas canvas, boolean allowChildren) {
        super(canvas, allowChildren);
    }

    @Override
    public J2DCanvas getCanvas() {
        return (J2DCanvas)super.getCanvas();
    }

    /**
     * Called by the J2DCanvas to ask the graphic object to paint itself on the canvas
     * using the rendering context parameters.
     *
     * @param context : a rendering context 2d that provides a Graphics2D object
     * and all necessary parameters.
     */
    public abstract void paint(final RenderingContext2D context);

    public abstract Object getUserObject();

}
