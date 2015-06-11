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

import java.util.Collection;
import java.util.List;
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
     * @return unique id.
     */
    String getId();
    
    /**
     * @return the pyramid set in which this pyramid is contained.
     */
    PyramidSet getPyramidSet();
    
    /**
     * @return the crs used for all mosaic.
     */
    CoordinateReferenceSystem getCoordinateReferenceSystem();
    
    /**
     * @return unmodifiable list of all mosaics.
     * Waring : in multidimensional pyramids, multiple mosaic at the same scale
     * may exist.
     */
    List<GridMosaic> getMosaics();
    
    /**
     * @return the different scales available in the pyramid.
     * The scale value is expressed in CRS unit by image cell (pixel usually)
     */
    double[] getScales();
    
    /**
     * @param index of the wanted scale, must match an available index of the scales table.
     * @return Collection<GridMosaic> available mosaics at this scale.
     * Waring : in multidimensional pyramids, multiple mosaic at the same scale
     * may exist.
     */
    Collection<GridMosaic> getMosaics(int index);
    
}
