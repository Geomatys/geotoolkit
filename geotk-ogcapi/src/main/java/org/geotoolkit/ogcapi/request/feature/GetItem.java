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
package org.geotoolkit.ogcapi.request.feature;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code items/{itemId}} query ({@code /collections/{collectionId}/items/{itemId}}).
 *
 * <p>OGC API - Features Part 1: Core. Also reused as-is by OGC API - EDR's {@code Locations}
 * query, which is defined in terms of this same resource.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetItem extends RequestParameters {

    private String collectionId;
    private String itemId;
    private String crs;
    private String f;

    /**
     * Local identifier of the collection.
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
    public GetItem collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Local identifier of the item.
     *
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     * @see #getItemId()
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * @param itemId the itemId to set
     * @see #getItemId()
     */
    public GetItem itemId(String itemId) {
        setItemId(itemId);
        return this;
    }

    /**
     * OGC API - Features - Part 2: CRS by Reference. The coordinate reference system of the
     * response geometry, as a URI. If not given, the default CRS (CRS84) is used. (optional)
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
    public GetItem crs(String crs) {
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
    public GetItem f(String f) {
        setF(f);
        return this;
    }

}
