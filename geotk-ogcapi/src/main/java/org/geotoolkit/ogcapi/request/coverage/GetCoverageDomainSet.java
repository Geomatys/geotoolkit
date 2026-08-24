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
package org.geotoolkit.ogcapi.request.coverage;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     The CIS Standard defines four aspects of a coverage which are described in separate
 *     properties: the domain set, the range type, the rangeset, and the domain-specific metadata.
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/19-087.html#rc_encoding-cisjson"> OGC API - Coverages - Part 1: Core</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetCoverageDomainSet extends RequestParameters {

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
    public GetCoverageDomainSet collectionId(String collectionId) {
        setCollectionId(collectionId);
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
    public GetCoverageDomainSet format(String format) {
        setFormat(format);
        return this;
    }
}
