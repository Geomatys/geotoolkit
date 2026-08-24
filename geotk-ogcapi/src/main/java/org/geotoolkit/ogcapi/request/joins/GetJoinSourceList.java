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
 * <blockquote><q>
 *     The HTTP GET operation at path {root}/collections/{collectionId}/joins returns a list of
 *     join sources available on the server that have been created for the specific collection.
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_6f9efc6a-c3cc-44b9-bbfc-37380ee3fc7c"> OGC API - Joins - Part 1: Core - 8.5. Join Sources</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetJoinSourceList extends RequestParameters {

    private String collectionId;
    private Integer limit;
    private String datetime;
    private String format;

    /**
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * <blockquote><q>
     *     The parameter collectionId SHALL be each id property in the collections response
     *     (JSONPath: $.collections[*].id).
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_4a651dea-0917-4591-886b-31d2043091bc"> OGC API - Joins - Part 1: Core - 8.4. Collection's Key Fields</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Must match the {@code id} of an existing collection (see {@code GET /collections}).</li>
     * </ul>
     */
    public GetJoinSourceList collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #limit(java.lang.Integer)
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @see #limit(java.lang.Integer)
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * <blockquote><q>
     *     The server MAY support the query parameter limit to limit the number of resources that
     *     can be returned in a single response.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_6f9efc6a-c3cc-44b9-bbfc-37380ee3fc7c"> OGC API - Joins - Part 1: Core - 8.5. Join Sources</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; support depends on the server.</li>
     *     <li>If provided but unsupported, the server processes the request as if it had not
     *     been provided.</li>
     *     <li>Minimum = 1, maximum = 1000, default = 10 (these bounds are only examples in the
     *     spec, and MAY be changed by the server).</li>
     * </ul>
     * <pre>
     * name: limit
     * in: query
     * required: false
     * schema:
     *   type: integer
     *   minimum: 1
     *   maximum: 1000
     *   default: 10
     * </pre>
     */
    public GetJoinSourceList limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    /**
     * @see #datetime(java.lang.String)
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @see #datetime(java.lang.String)
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * <blockquote><q>
     *     The server MAY support the query parameter datetime to filter the returned join
     *     sources by their execution timestamp.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_6f9efc6a-c3cc-44b9-bbfc-37380ee3fc7c"> OGC API - Joins - Part 1: Core - 8.5. Join Sources</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; support depends on the server.</li>
     *     <li>Either a date-time or an interval, following RFC 3339, section 5.6.</li>
     *     <li>Intervals may be open-ended at the start or the end using a double-dot ({@code ..}).</li>
     * </ul>
     */
    public GetJoinSourceList datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see #format(java.lang.String)
     */
    public String getFormat() {
        return format;
    }

    /**
     * @see #format(java.lang.String)
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * The format of the response. If no value is provided, the accept header is used to
     * determine the format.
     * <br>
     * <ul>
     *     <li>Optional; accepted values are {@code json} or {@code html}.</li>
     *     <li>Not defined by OGC API - Joins - Part 1: Core (draft), which has no formally
     *     specified {@code format} query parameter at the time of writing; this is a
     *     server-specific extension (no verbatim spec text available for this field).</li>
     * </ul>
     */
    public GetJoinSourceList format(String format) {
        setFormat(format);
        return this;
    }
}
