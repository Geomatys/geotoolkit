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

package org.geotoolkit.process.coverage.coveragetovector;

import org.geotoolkit.util.NumberRange;

/**
 *  Define a group of pixels with the same range.
 *
 * @author Johann Sorel (Geomatys)
 */
class Block {

    public NumberRange range;
    public int startX;
    public int endX;
    public int y;
    public Boundary boundary;

    public void reset(){
        range = null;
        startX = -1;
        endX = -1;
        y = -1;
        boundary = null;
    }

}
