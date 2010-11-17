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

    /**
     * Returns the output format, never {@code null}.
     */
    String getInfoFormat();

    /**
     * Sets the information format to use. Must be called.
     */
    void setInfoFormat(String format);

    /**
     * Returns the layer(s) to request, never {@code null}. Should not be empty.
     */
    String[] getQueryLayers();

    /**
     * Sets the layer(s) to request. Must be called.
     */
    void setQueryLayers(String ... layers);

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

    /**
     * Gets the maximum number of features per layer for which
     * feature information shall be returned. Its value is a positive integer. The default value is 1 if this parameter is
     * omitted or is other than a positive integer.
     */
    Integer getFeatureCount();

    /**
     * Sets the maximum number of features per layer for which
     * feature information shall be returned. Its value is a positive integer. The default value is 1 if this parameter is
     * omitted or is other than a positive integer.
     */
    void setFeatureCount(Integer featureCount);

}
