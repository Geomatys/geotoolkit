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
package org.geotoolkit.ogcapi.request.common;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     This requirements class defines the Queryables resource for discovering a list of
 *     resource properties with their types and constraints that may be used to construct filter expressions on a collection of resources, for example, a set of features.
 *     </q><i><small><a href="https://docs.ogc.org/is/19-079r2/19-079r2.html#queryables"> OGC API - Features - Part 3: Filtering</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetCollectionQueryables extends RequestParameters {

    private String collectionId;
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
     *     <li>Required.</li>
     *     <li>Must match the {@code id} of an existing collection (see {@code GET /collections}).</li>
     * </ul>
     */
    public GetCollectionQueryables collectionId(String collectionId) {
        this.collectionId = collectionId;
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
    public GetCollectionQueryables format(String format) {
        setFormat(format);
        return this;
    }
}
