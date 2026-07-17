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
package org.geotoolkit.stac.request;

import java.util.List;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for GET /collections.
 *
 * <p>Lists all collections available on the STAC server, with optional
 * spatial and temporal filters.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on: <a href="https://api.stacspec.org/v1.0.0/ogcapi-features/#operation/getCollections">STAC Doc</a>
 */
public class GetCollections extends RequestParameters {

    private Integer limit;
    private List<Double> bbox;
    private String datetime;

    /**
     * Maximum number of collections to return per page. (optional)
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @see #getLimit()
     */
    public GetCollections limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    /** @see #getLimit() */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Bounding box filter as {@code [minLon, minLat, maxLon, maxLat]} in CRS84.
     * Only collections intersecting this bbox are returned. (optional)
     */
    public List<Double> getBbox() {
        return bbox;
    }

    /**
     * @see #getBbox()
     */
    public GetCollections bbox(List<Double> bbox) {
        this.bbox = bbox;
        return this;
    }

    /** @see #getBbox() */
    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    /**
     * Temporal filter as an RFC 3339 date-time or open/closed interval
     * (e.g. {@code "2024-01-01T00:00:00Z/2024-06-01T00:00:00Z"}).
     * Use {@code ".."} for an open end. (optional)
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @see #getDatetime()
     */
    public GetCollections datetime(String datetime) {
        this.datetime = datetime;
        return this;
    }

    /** @see #getDatetime() */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
