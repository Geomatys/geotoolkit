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
package org.geotoolkit.wms;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public interface GetFeatureInfoRequest extends GetMapRequest {

    String getInfoFormat();

    void setInfoFormat(String format);

    String[] getQueryLayers();

    void setQueryLayers(String[] layers);

    /**
     * Returns the column index to request into an image, in pixels.
     * It is represented by the X value in WMS 1.1.1 or I in WMS 1.3.0.
     */
    Integer getColumnIndex();

    /**
     * Sets the value for the column index to request into an image.
     */
    void setColumnIndex(Integer columnIndex);

    /**
     * Returns the raw index to request into an image, in pixels.
     * It is represented by the Y value in WMS 1.1.1 or J in WMS 1.3.0.
     */
    Integer getRawIndex();

    /**
     * Sets the value for the column index to request into an image.
     */
    void setRawIndex(Integer rawIndex);

}
