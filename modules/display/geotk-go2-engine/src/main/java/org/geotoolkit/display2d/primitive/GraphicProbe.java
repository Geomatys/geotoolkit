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
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.opengis.display.primitive.Graphic;

/**
 * A Graphic object that render nothing and fires an event when it is repainted.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GraphicProbe extends AbstractGraphicJ2D{

    private final ProbeMonitor monitor;

    public GraphicProbe(ReferencedCanvas2D canvas, ProbeMonitor monitor){
        super(canvas,canvas.getObjectiveCRS2D());
        this.monitor = monitor;
    }

    @Override
    public void paint(RenderingContext2D context) {
        monitor.contextPaint(context);
    }

    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        return graphics;
    }

    public static interface ProbeMonitor{

        void contextPaint(RenderingContext2D context);

    }

}
