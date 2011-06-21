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
package org.geotoolkit.client.map;

import java.awt.geom.Point2D;
import org.opengis.geometry.Envelope;

/**
 * A Grid Mosaic in a grid of image. all images share common attributes :
 * - Size
 * - CRS
 * - Span
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface GridMosaic {
    
    /**
     * @return pyramid containing this mosaic.
     */
    Pyramid getPyramid();
        
    /**
     * @return upper left corner of the mosaic, expressed in pyramid CRS.
     */
    Point2D getUpperLeftCorner();
    
    /**
     * @return number of tiles along the 0 axis
     */
    int getWidth();
    
    /**
     * @return number of tiles along the 1 axis
     */
    int getHeight();
    
    /**
     * @return size of a tile along the 0 axis in crs unit
     */
    double getTileSpanX();
    
    /**
     * @return size of a tile along the 1 axis in crs unit
     */
    double getTileSpanY();
    
    /**
     * @return image width in cell units.
     */
    int getTileWidth();
    
    /**
     * @return image height in cell units.
     */
    int getTileHeight();
    
    /**
     * 
     * @param col
     * @param row
     * @return Envelope of the given tile.
     */
    Envelope getEnvelope(int col, int row);
    
    /**
     * Some services define some missing tiles.
     * WMTS for example may define for a given layer a limitation saying
     * only tiles for column 10 to 30 are available. 
     * 
     * @param col
     * @param row
     * @return true is tile is missing
     */
    boolean isMissing(int col, int row);
    
}
