/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.symbolizer;

import java.util.List;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class S52DifferedGraphic extends GraphicJ2D{

    public S52DifferedGraphic(J2DCanvas canvas) {
        super(canvas,false);
    }

    @Override
    public Object getUserObject() {
        return null;
    }

    @Override
    public Envelope getEnvelope() {
        return null;
    }

    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        return graphics;
    }

    @Override
    public void paint(RenderingContext2D context) {

    }

}
