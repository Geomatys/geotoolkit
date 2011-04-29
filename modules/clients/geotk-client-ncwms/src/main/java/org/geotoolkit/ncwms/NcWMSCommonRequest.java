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
 *
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public interface NcWMSCommonRequest {
    
    /**
     * Gets the opacity of the layer.
     * 
     * @return the opacity of the layer.
     */
    Integer getOpacity();

    /**
     * Sets the opacity of the layer.
     * 
     * @param opacity The choice of using a logarithmic color scale or not.
     */
    void setOpacity(final Integer opacity);
    
    /**
     * Gets the color scale range.
     * 
     * @return the color scale range.
     */
    String getColorScaleRange();

    /**
     * Sets the color scale range.
     * 
     * @param colorScaleRange the color scale range to set.
     */
    void setColorScaleRange(final String colorScaleRange);

    /**
     * Gets the number of color bands in the palette.
     * 
     * @return the number of color bands in the palette.
     */
    Integer getNumColorBands();

    /**
     * Sets the number of color bands in the palette.
     * 
     * @param numColorBands the number of color bands in the palette.
     */
    void setNumColorBands(final Integer numColorBands);

    /**
     * Gets the choice from a linear or logarithmic color scale
     * 
     * @return if we choose a logarithmic color scale or not.
     */
    Boolean isLogScale();

    /**
     * Sets the choice from a linear or logarithmic color scale
     * 
     * @param logScale The choice of using a logarithmic color scale or not.
     */
    void setLogScale(final Boolean logScale);
}
