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
package org.geotoolkit.ogcapi.request.records;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     For every resource in a collection, the server shall support the HTTP
 *     DELETE operation.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-records/blob/master/extensions/transactions/create-replace-update-delete-harvest/standard/abstract_tests/simpletx/delete/ATS_delete-op.adoc"> OGC API - Records - Part 3: Create, Replace, Update, Delete, Harvest (draft) - Delete operation</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DeleteRecord extends RequestParameters {

    private String collectionId;
    private String recordId;

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
     *     local identifier of a collection
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/collectionId.yaml"> OGC API - Features - Part 1: Core - collectionId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Must match the {@code id} of an existing collection (see {@code GET /collections}).</li>
     * </ul>
     */
    public DeleteRecord collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #recordId(java.lang.String)
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * @see #recordId(java.lang.String)
     */
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    /**
     * <blockquote><q>
     *     The parameter record SHALL be the id property of a record obtained by
     *     previously having queried the Records resource of the collection.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-records/blob/master/extensions/transactions/create-replace-update-delete-harvest/standard/requirements/records/REQ_resource-endpoint.adoc"> OGC API - Records - Part 3: Create, Replace, Update, Delete, Harvest (draft) - Resource endpoint</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public DeleteRecord recordId(String recordId) {
        setRecordId(recordId);
        return this;
    }

}
