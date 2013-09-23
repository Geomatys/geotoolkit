/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.map;

import org.geotoolkit.coverage.io.GridCoverageReader;

/**
 * An elevation model holds a map (may not necessarly be a grid coverage) of elevation values.
 * It is possible to ask for a grid coverage or a single elevation.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface ElevationModel {

    /**
     * Return angle in degrees between Origin to North axis and light source.<br/>
     * Moreother angle is defined positive in clockwise.
     * 
     * @return angle between Origin to North axis and light source. 
     */
    double getAzimuth();
    
    /**
     * Return angle in degrees between Digital Elevation Model ground and light source.
     * 
     * @return angle in degrees between Digital Elevation Model ground and light source.
     */
    double getAltitude();
    
    /**
     * Return coefficient (or factor) in per cent to controle shadow length spread 
     * in function of maximum DEM amplitude value.
     * 
     * @return coefficient (or factor) in per cent to controle shadow length spread.
     */
    double getAmplitudeScale();

    /**
     * Return adapted reader to read Digital Elevation Model.
     * 
     * @return adapted reader to read Digital Elevation Model.
     */
    GridCoverageReader getCoverageReader();
}
