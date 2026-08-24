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
 * Parameters for {@code DELETE /collections/{collectionId}/joins/{joinId}}.
 *
 * <blockquote><q>
 *     The HTTP DELETE operation at path {root}/collections/{collectionId}/joins/{joinId}
 *     deletes the specific join source from the server.
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5dd24bd9-597b-402a-a68f-dbfedd4214c4"> OGC API - Joins - Part 1: Core - 9.2. Join Source Delete</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DeleteJoinSource extends RequestParameters {

    private String collectionId;
    private String joinId;

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
    public DeleteJoinSource collectionId(String collectionId) {
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
     *     The parameter joinId is the id property of a join source in the joins response
     *     (JSONPath: $.joins.id).
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/22-026.html#_5dd24bd9-597b-402a-a68f-dbfedd4214c4"> OGC API - Joins - Part 1: Core - 9.2. Join Source Delete</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Must match the {@code id} of an existing join source (see
     *     {@code GET /collections/{collectionId}/joins}).</li>
     * </ul>
     */
    public DeleteJoinSource joinId(String joinId) {
        setJoinId(joinId);
        return this;
    }
}
