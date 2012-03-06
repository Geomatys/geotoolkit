/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.coverage;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.storage.DataStoreException;
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
     * @return unique id.
     */
    String getId();
    
    /**
     * @return pyramid containing this mosaic.
     */
    Pyramid getPyramid();
        
    /**
     * @return upper left corner of the mosaic, expressed in pyramid CRS.
     */
    Point2D getUpperLeftCorner();
    
    /**
     * @return size of the grid in number of columns/rows.
     */
    Dimension getGridSize();
    
    /**
     * @return size of a pixel in crs unit
     */
    double getScale();
    
    /**
     * @return image width in cell units.
     */
    Dimension getTileSize();
    
    /**
     * Envelope of the given tile.
     * 
     * @param col
     * @param row
     * @return Envelope of the given tile.
     */
    Envelope getEnvelope(int col, int row);
    
    /**
     * Envelope of the mosaic.
     * 
     * @return Envelope
     */
    Envelope getEnvelope();
    
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
    
    /**
     * Get a tile.
     * @param col : tile column index
     * @param row : row column index
     * @param hints : additional hints
     * @return RenderedImage , may be null if tile is missing.
     * @throws DataStoreException  
     */
    Tile getTile(int col, int row, Map hints) throws DataStoreException;
    
    /**
     * Get a tile as a stream
     * @param col : tile column index
     * @param row : row column index
     * @param hints : additional hints
     * @return InputStream , may be null if tile is missing.
     * @throws DataStoreException  
     */
    InputStream getTileStream(int col, int row, Map hints) throws DataStoreException;
    
    /**
     * Retrieve a set of tiles.
     * 
     * @param positions : requested tiles positions
     * @param hints : additional hints
     * @return blocking iterator over the requested tiles. Order might be different
     *          from the list of positions.
     * @throws DataStoreException 
     */
    Iterator<Tile> getTiles(Collection<? extends Point> positions, Map hints) throws DataStoreException;
    
}
