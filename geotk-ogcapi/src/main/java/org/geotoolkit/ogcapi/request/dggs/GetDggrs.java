/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.request.dggs;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for retrieving the description of a Discrete Global Grid Reference System
 * (DGGRS): {@code GET /dggs/{dggrsId}}, or, when {@link #getCollectionId()} is set,
 * {@code GET /collections/{collectionId}/dggs/{dggrsId}}.
 *
 * <blockquote><q>
 *     For retrieving the description of a particular available discrete global grid reference system
 *     </q><i><small><a href="https://docs.ogc.org/is/21-038r1/21-038r1.html#_d0363b84-b790-c745-d8ef-fc1693548296"> OGC API - Discrete Global Grid Systems - Discrete Global Grid Reference System (DGGRS) description</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetDggrs extends RequestParameters {

    private String collectionId;
    private String dggrsId;
    private String format;

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
     *     <li>Optional: only set when requesting the DGGRS description for a specific collection.</li>
     * </ul>
     */
    public GetDggrs collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #dggrsId(java.lang.String)
     */
    public String getDggrsId() {
        return dggrsId;
    }

    /**
     * @see #dggrsId(java.lang.String)
     */
    public void setDggrsId(String dggrsId) {
        this.dggrsId = dggrsId;
    }

    /**
     * <blockquote><q>
     *     Identifier for a supported Discrete Global Grid System
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/dggrsId.yaml"> OGC API - Discrete Global Grid Systems - dggrsId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetDggrs dggrsId(String dggrsId) {
        setDggrsId(dggrsId);
        return this;
    }

    /**
     * @see #format(java.lang.String)
     */
    public String getFormat() {
        return format;
    }

    /**
     * @see #format(java.lang.String)
     */
    public void setFormat(String format) {
        this.format = format;
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
    public GetDggrs format(String format) {
        setFormat(format);
        return this;
    }
}
