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
package org.geotoolkit.ogcapi.request.joins;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Parameters for {@code GET /collections/{collectionId}/joins}.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetJoinSourceList extends RequestParameters {

    private String collectionId;
    private Integer limit;
    private String datetime;
    private String format;

    /**
     * Identifier of the collection, as listed in the {@code id} property of the collections
     * response. (required)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @see #getCollectionId()
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @see #getCollectionId()
     */
    public GetJoinSourceList collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Limits the number of join sources returned in a single response. * Minimum = 1 * Maximum =
     * 1000 * Default = 10. Support for this parameter is optional; if the server does not support
     * it, the request is processed as if it had not been provided. (optional, default to 10)
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @see #getLimit()
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * @see #getLimit()
     */
    public GetJoinSourceList limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    /**
     * Filters the returned join sources by their execution timestamp. Either a date-time or an
     * interval, adhering to RFC 3339, section 5.6. Intervals may be bounded or half-bounded
     * (double-dots at start or end). Only join sources that have a {@code timeStamp} property that
     * intersects this value are selected. Support for this parameter is optional. (optional)
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @see #getDatetime()
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * @see #getDatetime()
     */
    public GetJoinSourceList datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * The format of the response. If no value is provided, the accept header is used to determine
     * the format. Accepted values are 'json' or 'html'. (optional)
     */
    public String getFormat() {
        return format;
    }

    /**
     * @see #getFormat()
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @see #getFormat()
     */
    public GetJoinSourceList format(String format) {
        setFormat(format);
        return this;
    }
}
