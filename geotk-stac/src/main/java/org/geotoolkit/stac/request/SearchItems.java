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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request body for POST /search.
 *
 * <p>Searches items across one or more collections with optional spatial, temporal
 * and pagination filters. This class is both the API parameter holder and the
 * JSON body serialized directly in the POST request.</p>
 *
 * <p>All fields are optional; omitted fields are excluded from the JSON output
 * via {@link JsonInclude.Include#NON_NULL}.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on: <a href="https://api.stacspec.org/v1.0.0/item-search/#operation/postItemSearch">STAC Doc</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchItems extends RequestParameters {

    @JsonProperty("collections")
    private List<String> collections;

    @JsonProperty("bbox")
    private double[] bbox;

    @JsonProperty("datetime")
    private String datetime;

    @JsonProperty("limit")
    private Integer limit;

    /**
     * Collection IDs to search within; null or empty means all collections. (optional)
     */
    public List<String> getCollections() {
        return collections;
    }

    /**
     * @see #getCollections()
     */
    public SearchItems collections(List<String> collections) {
        this.collections = collections;
        return this;
    }

    /** @see #getCollections() */
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }

    /**
     * Bounding box filter as {@code [minLon, minLat, maxLon, maxLat]} in CRS84.
     * Only items whose geometry intersects this bbox are returned. (optional)
     */
    public double[] getBbox() {
        return bbox;
    }

    /**
     * @see #getBbox()
     */
    public SearchItems bbox(double[] bbox) {
        this.bbox = bbox;
        return this;
    }

    /** @see #getBbox() */
    public void setBbox(double[] bbox) {
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
    public SearchItems datetime(String datetime) {
        this.datetime = datetime;
        return this;
    }

    /** @see #getDatetime() */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * Maximum number of items to return per page. (optional)
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @see #getLimit()
     */
    public SearchItems limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    /** @see #getLimit() */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
