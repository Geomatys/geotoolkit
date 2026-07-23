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
 * The {@code facets} query ({@code /collections/{collectionId}/facets}).
 *
 * <p>OGC API - Records Part 2: Facets (draft).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetFacets extends RequestParameters {

    private String collectionId;
    private String facets;
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
    public GetFacets collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * A comma-separated list of facet requests, each in the form
     * {@code property:bucketCount:sorting} (e.g. {@code type:10:count-desc}). (optional)
     *
     * @return the facets
     */
    public String getFacets() {
        return facets;
    }

    /**
     * @param facets the facets to set
     * @see #getFacets()
     */
    public void setFacets(String facets) {
        this.facets = facets;
    }

    /**
     * @param facets the facets to set
     * @see #getFacets()
     */
    public GetFacets facets(String facets) {
        setFacets(facets);
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
    public GetFacets f(String f) {
        setF(f);
        return this;
    }

}
