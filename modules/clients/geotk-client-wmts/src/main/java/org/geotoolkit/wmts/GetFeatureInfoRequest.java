/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.wmts;


/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public interface GetFeatureInfoRequest extends GetTileRequest {

    String getInfoFormat();

    void setInfoFormat(String format);

    /**
     * Returns the column index to request into an image, in pixels.
     * It is represented by the I value in WMTS 1.0.0.
     */
    Integer getColumnIndex();

    /**
     * Sets the value for the column index to request into an image.
     */
    void setColumnIndex(Integer columnIndex);

    /**
     * Returns the raw index to request into an image, in pixels.
     * It is represented by the J value in WMTS 1.0.0.
     */
    Integer getRawIndex();

    /**
     * Sets the value for the column index to request into an image.
     */
    void setRawIndex(Integer rawIndex);

}
