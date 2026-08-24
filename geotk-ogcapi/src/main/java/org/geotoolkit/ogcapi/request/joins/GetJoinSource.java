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
 * Parameters for {@code GET /collections/{collectionId}/joins/{joinId}}.
 *
 * <blockquote><q>
 *     The HTTP GET operation at path {root}/collections/{collectionId}/joins/{joinId} returns
 *     metadata on a specific join source that is available on the server.
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_53ac9b66-ea7c-4c7f-902f-408b4509095d"> OGC API - Joins - Part 1: Core - 8.7. Join Source</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetJoinSource extends RequestParameters {

    private String collectionId;
    private String joinId;
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
    public GetJoinSource collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #joinId(java.lang.String)
     */
    public String getJoinId() {
        return joinId;
    }

    /**
     * @see #joinId(java.lang.String)
     */
    public void setJoinId(String joinId) {
        this.joinId = joinId;
    }

    /**
     * <blockquote><q>
     *     The parameter joinId SHALL be each id property in the joins response
     *     (JSONPath: $.joins[*].id).
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_53ac9b66-ea7c-4c7f-902f-408b4509095d"> OGC API - Joins - Part 1: Core - 8.7. Join Source</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Must match the {@code id} of an existing join source (see
     *     {@code GET /collections/{collectionId}/joins}).</li>
     * </ul>
     */
    public GetJoinSource joinId(String joinId) {
        setJoinId(joinId);
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
    public GetJoinSource format(String format) {
        setFormat(format);
        return this;
    }
}
