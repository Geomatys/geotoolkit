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
package org.geotoolkit.ncwms;

/**
 * Common interface for some ncWMS requests (GetMap, GetLegendGraphic, GetFeatureInfo)
 * 
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public interface NcWMSCommonRequest {
    
    /**
     * Gets the opacity of the layer.
     */
    Integer getOpacity();

    /**
     * Sets the opacity of the layer.
     */
    void setOpacity(final Integer opacity);

    /**
     * Gets the number of color bands in the palette.
     */
    Integer getNumColorBands();

    /**
     * Sets the number of color bands in the palette.
     */
    void setNumColorBands(final Integer numColorBands);

    /**
     * Gets the choice from a linear or logarithmic color scale.
     */
    Boolean isLogScale();

    /**
     * Sets the choice from a linear or logarithmic color scale.
     */
    void setLogScale(final Boolean logScale);
}
