/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 - 2014, Geomatys
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

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.List;
import javax.swing.ProgressMonitor;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * May be implemented by Coverage reference when the underlying structure is a
 * pyramid.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface PyramidalCoverageReference extends CoverageReference{
    
    PyramidSet getPyramidSet() throws DataStoreException;

    /**
     * Get the defined mode in which datas are stored.
     * 
     * @return ViewType, never null
     * @throws org.apache.sis.storage.DataStoreException
     */
    ViewType getPackMode() throws DataStoreException;
    
    /**
     * Set stored data mode.
     * This won't change the data itself.
     * 
     * This method should be called before adding any data.
     * 
     * @param packMode 
     * @throws org.apache.sis.storage.DataStoreException 
     */
    void setPackMode(ViewType packMode) throws DataStoreException;
    
    /**
     * List sample dimensions.
     * 
     * This method should be called before adding any data.
     * 
     * @return can be null
     * @throws DataStoreException 
     */
    List<GridSampleDimension> getSampleDimensions() throws DataStoreException;

    /**
     * Set sample dimensions.
     * 
     * @param dimensions 
     * @throws org.apache.sis.storage.DataStoreException 
     */
    void setSampleDimensions(final List<GridSampleDimension> dimensions) throws DataStoreException;
    
    /**
     * Get default color model.
     * 
     * @return ColorModel can be null
     * @throws org.apache.sis.storage.DataStoreException
     */
    ColorModel getColorModel() throws DataStoreException;
    
    /**
     * Set color model, the store is not require to respect completely the model.
     * The object given is a hint for the store to choose more accurately
     * the storage parameters.
     * 
     * This method should be called before adding any data.
     * 
     * @param colorModel
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setColorModel(ColorModel colorModel) throws DataStoreException;
    
    /**
     * Get sample model.
     * 
     * @return SampleModel can be null
     * @throws org.apache.sis.storage.DataStoreException
     */
    SampleModel getSampleModel() throws DataStoreException;
    
    /**
     * Set sample model, the store is not require to respect completely the model
     * The object given is a hint for the store to choose more accurately
     * the storage parameters.
     * 
     * This method should be called before adding any data.
     * 
     * @param sampleModel
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setSampleModel(SampleModel sampleModel) throws DataStoreException;
    
    /**
     *
     * @return true if model can be modified
     * @throws org.geotoolkit.coverage.io.CoverageStoreException
     */
    @Override
    boolean isWritable() throws CoverageStoreException;

    /**
     *
     * @param crs
     * @return created pyramid
     * @throws DataStoreException
     */
    Pyramid createPyramid(CoordinateReferenceSystem crs) throws DataStoreException;

    /**
     * Delete given pyramid.
     *
     * @throws DataStoreException
     */
    void deletePyramid(String pyramidId) throws DataStoreException;

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
             Dimension tilePixelSize, DirectPosition upperleft, double pixelscale) throws DataStoreException;

    /**
     * Delete given mosaic.
     *
     * @throws DataStoreException
     */
    void deleteMosaic(String pyramidId, String mosaicId) throws DataStoreException;

    /**
     * Write a complete mosaic level used the given rendered image.
     * The rendered image size and tile size must match the mosaic definition.
     *
     * @param pyramidId
     * @param mosaicId
     * @param image
     * @param onlyMissing : set to true to fill only missing tiles
     * @param monitor A progress monitor in order to eventually cancel the process. May be {@code null}.
     * @throws DataStoreException
     */
    void writeTiles(String pyramidId, String mosaicId, RenderedImage image, boolean onlyMissing, ProgressMonitor monitor) throws DataStoreException;

    /**
     * Write a part of mosaic level from the given rendered image and rectangle area
     * The rendered image size and tile size must match the mosaic definition.
     *
     * @param pyramidId
     * @param mosaicId
     * @param image
     * @param area Rectangle2D that define area to copy in grid system (edges exclusive)
     * @param onlyMissing : set to true to fill only missing tiles
     * @param monitor A progress monitor in order to eventually cancel the process. May be {@code null}.
     * @throws DataStoreException
     */
    void writeTiles(String pyramidId, String mosaicId, RenderedImage image, Rectangle area, boolean onlyMissing,
                    ProgressMonitor monitor) throws DataStoreException;


    /**
     * Write or update a single tile in the mosaic.
     * Rendered image size must match mosaic tile size.
     *
     * @param pyramidId : pyramid id in which to insert the tile
     * @param mosaicId : mosaic id in which to insert the tile
     * @param tileX : position of the tile , column
     * @param tileY : position of the tile , row
     * @param image : image to insert
     * @throws DataStoreException
     */
    void writeTile(String pyramidId, String mosaicId, int tileX, int tileY, RenderedImage image) throws DataStoreException;

    void deleteTile(String pyramidId, String mosaicId, int tileX, int tileY) throws DataStoreException;
}
