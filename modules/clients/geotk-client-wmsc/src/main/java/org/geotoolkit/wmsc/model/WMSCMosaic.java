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
package org.geotoolkit.wmsc.model;

import java.awt.geom.Point2D;
import org.geotoolkit.client.map.GridMosaic;
import org.geotoolkit.client.map.Pyramid;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCMosaic implements GridMosaic{

    @Override
    public Pyramid getPyramid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Point2D getUpperLeftCorner() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getWidth() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHeight() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getTileSpanX() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getTileSpanY() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTileWidth() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTileHeight() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Envelope getEnvelope(int col, int row) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isMissing(int col, int row) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
