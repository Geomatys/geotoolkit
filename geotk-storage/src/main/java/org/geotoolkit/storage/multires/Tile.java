/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.multires;

import java.awt.Point;
import org.apache.sis.storage.Resource;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Tile extends Resource {

    /**
     * Returns the position of the tile in the mosaic.
     * x/y coordinate are columns and rows.
     *
     * @return position of the tile in the mosaic.
     */
    Point getPosition();
}
