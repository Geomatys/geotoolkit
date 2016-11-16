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

package org.geotoolkit.display2d.primitive;

import java.util.List;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;

/**
 * A Graphic object that render nothing and fires an event when it is repainted.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GraphicProbe extends GraphicJ2D{

    private final ProbeMonitor monitor;

    public GraphicProbe(final J2DCanvas canvas, final ProbeMonitor monitor){
        super(canvas);
        this.monitor = monitor;
    }

    @Override
    public void paint(final RenderingContext2D context) {
        monitor.contextPaint(context);
    }

    @Override
    public List<Graphic> getGraphicAt(final RenderingContext context, final SearchArea mask, final VisitFilter filter, final List<Graphic> graphics) {
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

    public static interface ProbeMonitor{
        void contextPaint(RenderingContext2D context);
    }

}
