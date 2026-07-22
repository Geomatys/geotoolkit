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
package org.geotoolkit.ogcapi.request.edr;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code items/{itemId}} EDR query ({@code /collections/{collectionId}/items/{itemId}}).
 *
 * <p>Unlike the other EDR queries, {@code items} is only defined at collection scope: the
 * specification does not expose an instance-scoped variant.</p>
 *
 * <p>This query type is inherited from OGC API - Features Part 1 (the {@code /items/{itemId}}
 * resource), not defined by EDR itself. Should a Features Part 1 client be added to this module,
 * this class should be promoted/mutualized with its equivalent instead of kept EDR-local.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetItem extends RequestParameters {

    private String collectionId;
    private String itemId;
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
