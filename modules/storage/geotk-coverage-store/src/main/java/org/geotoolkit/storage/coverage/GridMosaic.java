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
package org.geotoolkit.storage.coverage;

import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import org.apache.sis.storage.DataStoreException;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 * A Grid Mosaic in a grid of image. all images share common attributes :
 * - Size
 * - CRS
 * - Span
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface GridMosaic {
    
    /**
     * Sentinel object used to notify the end of the queue.
     */
    public static final Object END_OF_QUEUE = new Object();
    
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
    DirectPosition getUpperLeftCorner();
    
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
     * @return true if tile is missing
     * @throws org.opengis.coverage.PointOutsideCoverageException if the queried coordinate is not an allowed tile indice.
     */
    boolean isMissing(int col, int row) throws PointOutsideCoverageException;
    
    /**
     * Get a tile.
     * @param col : tile column index
     * @param row : row column index
     * @param hints : additional hints. Can be null.
     * @return TileReference , may be null if tile is missing.
     * @throws DataStoreException  
     */
    TileReference getTile(int col, int row, Map hints) throws DataStoreException;
        
    /**
     * Retrieve a set of TileReferences.<p>
     * The end of the queue is notified by the {@link GridMosaic#END_OF_QUEUE} object.<p>
     * The returned queue may implement Canceleable if for some reason there is no need
     * to continue iteration on the queue.
     * 
     * @param positions : requested tiles positions
     * @param hints : additional hints
     * @return blocking queue over the requested tiles. 
     *         Order might be different from the list of positions.
     * @throws DataStoreException 
     */
    BlockingQueue<Object> getTiles(Collection<? extends Point> positions, Map hints) throws DataStoreException;

    /**
     * Method to optimize mosaic browsing by returning a {@link java.awt.Rectangle} of where data are.
     * This rectangle can contain all grid or a part of it, but it shouldn't never exceed grid size.
     * It can also be {@code null} if mosaic is empty.
     *
     * @return {@link java.awt.Rectangle} of data area or null if all
     * tiles of the mosaic are missing.
     */
    Rectangle getDataArea();

}
