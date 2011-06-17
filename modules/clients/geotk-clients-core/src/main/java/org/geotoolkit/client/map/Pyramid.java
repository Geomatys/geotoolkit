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

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A Pyramid is a collection of mosaic in the same CRS but at different
 * scale levels.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface Pyramid {
    
    /**
     * @return the pyramid set in which this pyramid is contained.
     */
    PyramidSet getPyramidSet();
    
    /**
     * @return the crs used for all mosaic.
     */
    CoordinateReferenceSystem getCoordinateReferenceSystem();
    
    /**
     * @return the different scales available in the pyramid.
     * The scale value is expressed in CRS unit by image cell (pixel usually)
     */
    double[] getScales();
    
    /**
     * @param index of the wanted mosaic, must match an available index of the scales table.
     * @return  GridMosaic
     */
    GridMosaic getMosaic(int index);
    
}
