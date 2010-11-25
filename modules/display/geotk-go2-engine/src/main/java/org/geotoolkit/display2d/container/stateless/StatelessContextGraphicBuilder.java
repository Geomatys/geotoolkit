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
package org.geotoolkit.display2d.container.stateless;

import java.util.Collection;
import java.util.Collections;

import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.container.ContextGraphicBuilder;
import org.geotoolkit.map.MapContext;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatelessContextGraphicBuilder implements ContextGraphicBuilder{

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<? extends GraphicJ2D> createGraphics(J2DCanvas canvas, MapContext context) {
        return Collections.singleton(new StatelessContextJ2D(canvas, context));
    }

}
