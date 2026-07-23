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
 * The {@code items/{recordId}} query ({@code /collections/{collectionId}/items/{recordId}}).
 *
 * <p>OGC API - Records Part 1: Core.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetRecord extends RequestParameters {

    private String collectionId;
    private String recordId;
    private String crs;
    private String f;

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
    public GetRecord collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Local identifier of the record. (required)
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
    public GetRecord recordId(String recordId) {
        setRecordId(recordId);
        return this;
    }

    /**
     * The coordinate reference system of the response geometry, as a URI. If not given, the
     * default CRS (CRS84) is used. (optional)
     *
     * @return the crs
     */
    public String getCrs() {
        return crs;
    }

    /**
     * @param crs the crs to set
     * @see #getCrs()
     */
    public void setCrs(String crs) {
        this.crs = crs;
    }

    /**
     * @param crs the crs to set
     * @see #getCrs()
     */
    public GetRecord crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * The output format of the response. If not given, the content type is negotiated from the
     * {@code Accept} header. (optional)
     *
     * @return the f
     */
    public String getF() {
        return f;
    }

    /**
     * @param f the f to set
     * @see #getF()
     */
    public void setF(String f) {
        this.f = f;
    }

    /**
     * @param f the f to set
     * @see #getF()
     */
    public GetRecord f(String f) {
        setF(f);
        return this;
    }

}
