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

package org.geotoolkit.display2d.ext.grid;


import java.util.List;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display.primitive.Graphic;
import org.opengis.geometry.Envelope;

/**
 * Graphic decoration to paint a grid.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GraphicGridJ2D extends GraphicJ2D{

    private final GridTemplate template;

    public GraphicGridJ2D(final J2DCanvas canvas, final GridTemplate template){
        super(canvas);
        ensureNonNull("template", template);
        this.template = template;
    }

    @Override
    public boolean paint(final RenderingContext2D context) {
        J2DGridUtilities.paint(context, template);
        return false;
    }

    @Override
    public List<Graphic> getGraphicAt(final RenderingContext context, final SearchArea mask, final List<Graphic> graphics) {
        return graphics;
    }

    @Override
    public Object getUserObject() {
        return null;
    }

    @Override
    public Envelope getEnvelope() {
        return null;
    }


}
