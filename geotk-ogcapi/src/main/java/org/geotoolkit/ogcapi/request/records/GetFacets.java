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
 *     The Facets resource SHALL support the HTTP GET operation and the media type
 *     application/facets+json.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-records/blob/master/extensions/facets/standard/requirements/simple/REQ_media_type.adoc"> OGC API - Records - Part 2: Facets (draft) - Simple Facets</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetFacets extends RequestParameters {

    private String collectionId;
    private String facets;
    private String f;

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
    public GetFacets collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #facets(java.lang.String)
     */
    public String getFacets() {
        return facets;
    }

    /**
     * @see #facets(java.lang.String)
     */
    public void setFacets(String facets) {
        this.facets = facets;
    }

    /**
     * <blockquote><q>
     *     The facets parameter is represented by a comma-separated list of values (,),
     *     delimited by colons (:), with the following tokens:
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-records/blob/master/extensions/facets/openapi/parameters/facets.yaml"> OGC API - Records - Part 2: Facets (draft) - facets parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>{@code property-name}: mandatory; the name of the facet to be included in the search response, matching one of the facets advertised in the facets definition.</li>
     *     <li>{@code bucket-count}: optional; the maximum number of buckets to return for that facet.</li>
     *     <li>{@code sorting}: optional; one of {@code value_asc}, {@code value_desc}, {@code count_asc}, {@code count_desc} (defaults to the value advertised in the facets definition).</li>
     * </ul>
     * <pre>facets=keywords:20:value_asc,organization:10,theme::count_desc,format</pre>
     */
    public GetFacets facets(String facets) {
        setFacets(facets);
        return this;
    }

    /**
     * @see #f(java.lang.String)
     */
    public String getF() {
        return f;
    }

    /**
     * @see #f(java.lang.String)
     */
    public void setF(String f) {
        this.f = f;
    }

    /**
     * <blockquote><q>
     *     The optional f parameter indicates the output format that the server
     *     shall provide as part of the response document. The default format is JSON.
     *     </q><i><small><a href="https://docs.ogc.org/is/19-072/19-072.html#json-api-definition"> OGC API - Common - Part 1: Core</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Allowed values: {@code json}, {@code html}.</li>
     *     <li>Default: {@code json}.</li>
     * </ul>
     */
    public GetFacets f(String f) {
        setF(f);
        return this;
    }

}
