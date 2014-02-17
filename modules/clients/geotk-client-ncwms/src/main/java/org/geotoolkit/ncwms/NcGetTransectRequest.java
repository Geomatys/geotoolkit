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

import org.geotoolkit.client.Request;


/** 
 * Interface for GetTransect requests.
 *
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public interface NcGetTransectRequest extends Request {    
    
    /**
     * Returns the layer name.
     */
    String getLayer();
    
    /**
     * Sets the layer name.
     */
    void setLayer(final String name);
    
    /**
     * Returns the CRS code.
     */
    String getCrs();

    /**
     * Sets the CRS code.         
     */
    void setCrs(final String crsCode);
    
    /**
     * Returns coordinates of a line: x1%y1,x2%y2 ...
     */
    String getLineString();
    
    /**
     * Sets coordinates of a line: x1%y1,x2%y2 ...
     */
    void setLineString(final String lineString);    
    
    /**
     * Returns the mimetype of the output format.
     */
    String getFormat();
    
    /**
     * Sets the mimetype of the output format.
     */
    void setFormat(final String format);
    
    /**
     * Returns the Time in ISO8601 format.
     */
    String getTime();
    
    /**
     * Sets the Time in ISO8601 format.
     */
    void setTime(final String time);    
    
    /**
     * Returns the Elevation.
     */
    String getElevation();
    
    /**
     * Sets the Elevation.
     */
    void setElevation(final String elevation);
    
}
