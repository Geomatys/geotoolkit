/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
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

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.canvas.painter.BackgroundPainter;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class GridPainter implements BackgroundPainter{

    private final GridTemplate template;

    public GridPainter(GridTemplate template){
        this.template = template;
    }

    @Override
    public void paint(RenderingContext2D context) {
        J2DGridUtilities.paint(context, template);
    }

}
