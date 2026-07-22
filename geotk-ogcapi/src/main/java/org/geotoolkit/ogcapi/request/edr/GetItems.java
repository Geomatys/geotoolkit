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

import org.opengis.geometry.Envelope;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code items} EDR listing query ({@code /collections/{collectionId}/items}).
 *
 * <p>Unlike the other EDR queries, {@code items} is only defined at collection scope: the
 * specification does not expose an instance-scoped variant.</p>
 *
 * <p>This query type is inherited from OGC API - Features Part 1 (the {@code /items}
 * resource), not defined by EDR itself. Should a Features Part 1 client be added to this module,
 * this class should be promoted/mutualized with its equivalent instead of kept EDR-local.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetItems extends RequestParameters {

    private String collectionId;
    private Envelope bbox;
    private String datetime;
    private Integer limit;
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
    public GetItems collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Only items that have a geometry that intersects the bounding box are selected. (optional)
     *
     * @return the bbox
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public GetItems bbox(Envelope bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * A date-time or a period, per RFC 3339. (optional)
     *
     * @return the datetime
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public GetItems datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * The optional limit parameter limits the number of items presented in the response.
     * (optional)
     *
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public GetItems limit(Integer limit) {
        setLimit(limit);
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
    public GetItems f(String f) {
        setF(f);
        return this;
    }

}
