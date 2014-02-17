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
package org.geotoolkit.wps;

import java.util.List;

/**
 * Bounding box Input for WPS
 * @author Quentin Boileau
 * @module pending
 */
public class WPSInputBoundingBox extends AbstractWPSInput{
    
     
    private List<Double> lowerCorner;
    private List<Double> upperCorner;
    private String crs;
    private int dimension;


    /**
     * Constructor with all Input BoundingBox parameters
     * @param identifier
     * @param lowerCorner
     * @param upperCorner
     * @param crs
     * @param dimension 
     */
    public WPSInputBoundingBox(final String identifier, final List<Double> lowerCorner, final List<Double> upperCorner, 
            final String crs, final int dimension) {
        super(identifier);
        this.lowerCorner = lowerCorner;
        this.upperCorner = upperCorner;
        this.crs = crs;
        this.dimension = dimension;
    }

    /**
     * Return Output crs
     */
    public String getCrs() {
        return crs;
    }

    /**
     * Return Output dimension
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Return Output lowercorner
     */
    public List<Double> getLowerCorner() {
        return lowerCorner;
    }

    /**
     * Return Output uppercorner
     */
    public List<Double> getUpperCorner() {
        return upperCorner;
    }
    
}
