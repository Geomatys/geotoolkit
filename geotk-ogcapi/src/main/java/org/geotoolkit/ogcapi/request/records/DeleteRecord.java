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
 * To delete an existing record.
 *
 * <p>OGC API - Records Part 3: Create, Replace, Update, Delete, Harvest (draft).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DeleteRecord extends RequestParameters {

    private String collectionId;
    private String recordId;

    /**
     * Local identifier of the catalog. (required)
     *
     * @return the collectionId
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public DeleteRecord collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Local identifier of the record to delete. (required)
     *
     * @return the recordId
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * @param recordId the recordId to set
     * @see #getRecordId()
     */
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    /**
     * @param recordId the recordId to set
     * @see #getRecordId()
     */
    public DeleteRecord recordId(String recordId) {
        setRecordId(recordId);
        return this;
    }

}
