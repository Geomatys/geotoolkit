/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.request.map;

import java.util.List;

/**
 * To retrieve information about features shown on a map produced by an equivalent {@link GetMap}
 * request, at a given pixel location.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetFeatureInfo extends GetMap {

    private List<String> queryLayers;
    private Integer x;
    private Integer y;
    private String infoFormat;
    private Integer featureCount;

    /**
     * Layer(s) to query, amongst the ones requested by {@link #getLayers()}. (required)
     *
     * @return the queryLayers
     */
    public List<String> getQueryLayers() {
        return queryLayers;
    }

    /**
     * @param queryLayers the queryLayers to set
     * @see #getQueryLayers()
     */
    public void setQueryLayers(List<String> queryLayers) {
        this.queryLayers = queryLayers;
    }

    /**
     * @param queryLayers the queryLayers to set
     * @see #getQueryLayers()
     */
    public GetFeatureInfo queryLayers(List<String> queryLayers) {
        setQueryLayers(queryLayers);
        return this;
    }

    /**
     * Pixel column, on the map image, to query. (required)
     *
     * @return the x
     */
    public Integer getX() {
        return x;
    }

    /**
     * @param x the x to set
     * @see #getX()
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * @param x the x to set
     * @see #getX()
     */
    public GetFeatureInfo x(Integer x) {
        setX(x);
        return this;
    }

    /**
     * Pixel row, on the map image, to query. (required)
     *
     * @return the y
     */
    public Integer getY() {
        return y;
    }

    /**
     * @param y the y to set
     * @see #getY()
     */
    public void setY(Integer y) {
        this.y = y;
    }

    /**
     * @param y the y to set
     * @see #getY()
     */
    public GetFeatureInfo y(Integer y) {
        setY(y);
        return this;
    }

    /**
     * Mime-type expected for the returned feature information. (required)
     *
     * @return the infoFormat
     */
    public String getInfoFormat() {
        return infoFormat;
    }

    /**
     * @param infoFormat the infoFormat to set
     * @see #getInfoFormat()
     */
    public void setInfoFormat(String infoFormat) {
        this.infoFormat = infoFormat;
    }

    /**
     * @param infoFormat the infoFormat to set
     * @see #getInfoFormat()
     */
    public GetFeatureInfo infoFormat(String infoFormat) {
        setInfoFormat(infoFormat);
        return this;
    }

    /**
     * Maximum number of features to return, across all query layers. (optional)
     *
     * @return the featureCount
     */
    public Integer getFeatureCount() {
        return featureCount;
    }

    /**
     * @param featureCount the featureCount to set
     * @see #getFeatureCount()
     */
    public void setFeatureCount(Integer featureCount) {
        this.featureCount = featureCount;
    }

    /**
     * @param featureCount the featureCount to set
     * @see #getFeatureCount()
     */
    public GetFeatureInfo featureCount(Integer featureCount) {
        setFeatureCount(featureCount);
        return this;
    }

}
