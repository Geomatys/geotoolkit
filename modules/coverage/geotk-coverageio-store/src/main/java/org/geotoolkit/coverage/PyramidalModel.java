/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * May be implemented by Coverage reference when the underlying structure is a 
 * paramid.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface PyramidalModel {
    
    PyramidSet getPyramidSet() throws DataStoreException;
    
    /**
     * 
     * @return true if model can be modified
     */
    boolean isWriteable();
    
    /**
     * 
     * @param crs
     * @return created pyramid
     * @throws DataStoreException  
     */
    Pyramid createPyramid(CoordinateReferenceSystem crs) throws DataStoreException;
    
    /**
     * 
     * @param pyramidId : pyramid id in which to insert the mosaic
     * @param gridSize : size in number of column and row
     * @param tilePixelSize : size of a tile in pixel
     * @param upperleft : upperleft corner position in pyramid crs
     * @param pixelscale : size of a pixel in crs unit
     * @return created mosaic
     * @throws DataStoreException  
     */
    GridMosaic createMosaic(String pyramidId, Dimension gridSize, 
            Dimension tilePixelSize, Point2D upperleft, double pixelscale) throws DataStoreException;
    
    /**
     * Write a complete mosaic level used the given rendered image.
     * The rendered image size and tile size must match the mosaic definition.
     * 
     * @param pyramidId
     * @param mosaicId
     * @param image
     * @param onlyMissing : set to true to fill only missing tiles
     * @throws DataStoreException  
     */
    void writeTiles(String pyramidId, String mosaicId, RenderedImage image, boolean onlyMissing) throws DataStoreException;
    
    /**
     * Write or update a single tile in the mosaic.
     * Rendered image size must match mosaic tile size.
     * 
     * @param pyramidId : pyramid id in which to insert the tile
     * @param mosaicId : mosaic id in which to insert the tile
     * @param col : position of the tile , column
     * @param row : position of the tile , row
     * @param image : image to insert
     * @throws DataStoreException  
     */
    void writeTile(String pyramidId, String mosaicId, int col, int row, 
            RenderedImage image) throws DataStoreException;
    
}
