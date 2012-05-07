/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display3d.container;

import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class MapLayerNode<T extends MapLayer> extends MapItemNode<T> {

    public MapLayerNode(A3DCanvas canvas, T mapitem) {
        super(canvas, mapitem);
    }
    
}
