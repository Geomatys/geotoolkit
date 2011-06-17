/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.osmtms.model;

import java.awt.geom.Point2D;
import org.geotoolkit.client.map.DefaultGridMosaic;
import org.geotoolkit.client.map.Pyramid;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMTMSMosaic extends DefaultGridMosaic{

    private final int scaleLevel;
    
    public OSMTMSMosaic(Pyramid pyramid, Point2D upperLeft, int width, int height, 
            int tileHeight, int tileWidth, double tileSpanX, double tileSpanY, int scaleLevel) {
        super(pyramid,upperLeft,width,height,tileHeight,tileWidth,tileSpanX,tileSpanY);
        this.scaleLevel = scaleLevel;
    }

    public int getScaleLevel() {
        return scaleLevel;
    }
    
}
