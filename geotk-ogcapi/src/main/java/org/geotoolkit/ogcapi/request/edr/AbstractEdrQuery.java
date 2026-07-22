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
 * Fields shared by every EDR query-type request ({@code position}, {@code radius}, {@code area},
 * {@code cube}, {@code trajectory}, {@code corridor}, {@code locations} and {@code location}):
 * the collection (and optionally instance) being queried, an optional temporal filter, and the
 * response format.
 *
 * <p>Only plain getters/setters are declared here, not fluent builder methods: a fluent method
 * declared on this class would return {@code AbstractEdrQuery}, breaking the chained-builder
 * pattern once a subclass method is called afterwards. Each subclass instead redeclares its own
 * one-line fluent method per inherited field, forwarding to the corresponding setter.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public abstract class AbstractEdrQuery extends RequestParameters {

    private String collectionId;

    private String instanceId;

    private String datetime;

    private String f;

    /**
     * @return the identifier of the collection to query.
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the identifier of the collection to query.
     * @see #getCollectionId()
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @return the identifier of the instance to query, or {@code null} to query the collection
     * directly.
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * @param instanceId the identifier of the instance to query.
     * @see #getInstanceId()
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * @return the temporal filter (a date-time or interval), or {@code null}.
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the temporal filter (a date-time or interval).
     * @see #getDatetime()
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * @return the requested response format, or {@code null} for content negotiation.
     */
    public String getF() {
        return f;
    }

    /**
     * @param f the requested response format.
     * @see #getF()
     */
    public void setF(String f) {
        this.f = f;
    }
}
