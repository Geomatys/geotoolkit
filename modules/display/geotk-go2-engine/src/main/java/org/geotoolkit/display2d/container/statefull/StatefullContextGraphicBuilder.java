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
package org.geotoolkit.display2d.container.statefull;

import java.util.Collection;
import java.util.Collections;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.container.ContextGraphicBuilder;
import org.geotoolkit.map.MapContext;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class StatefullContextGraphicBuilder implements ContextGraphicBuilder{

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<? extends GraphicJ2D> createGraphics(ReferencedCanvas2D canvas, MapContext context) {
        return Collections.singleton(new StatefullContextJ2D(canvas, context));
    }

}
