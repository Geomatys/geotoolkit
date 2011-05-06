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

import org.geotoolkit.wms.GetLegendRequest;



/**
 *
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public interface NcGetLegendRequest extends NcWMSCommonRequest, GetLegendRequest  {
    
     /**
     * Gets the name of the palette.
     * 
     * @return the name of the palette.
     */
    String getPalette();

    /**
     * Sets the name of the palette.
     * 
     * @param palette the name of the palette to set.
     */
    void setPalette(final String palette);
}
