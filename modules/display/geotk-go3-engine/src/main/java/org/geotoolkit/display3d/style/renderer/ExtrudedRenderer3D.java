/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.display3d.style.renderer;

import java.util.Collection;
import org.geotoolkit.display3d.primitive.A3DGraphic;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.opengis.display.canvas.Canvas;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class ExtrudedRenderer3D implements GraphicBuilder<A3DGraphic>{

    @Override
    public Collection<A3DGraphic> createGraphics(MapLayer layer, Canvas canvas) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Class<A3DGraphic> getGraphicType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
