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
 * Interface for GetTimeseries requests.
 * @author Fabien BERNARD (Geomatys)
 * @module pending
 */
public interface NcGetTimeseriesRequest extends NcGetFeatureInfoRequest {

    /**
     * Returns the DateBegin in ISO8601 format.
     */
    String getDateBegin();

    /**
     * Sets the DateBegin in ISO8601 format.
     */
    void setDateBegin(final String dateBegin);
    
    /**
     * Returns the DateEnd in ISO8601 format.
     */
    String getDateEnd();

    /**
     * Sets the DateEnd in ISO8601 format.
     */
    void setDateEnd(final String dateEnd);
}
