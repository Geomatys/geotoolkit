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
 * Request parameters for retrieving the JSON definition of a Discrete Global Grid Reference
 * System (DGGRS), as linked from the DGGRS description by an
 * {@code http://www.opengis.net/def/rel/ogc/1.0/dggrs-definition} relation.
 *
 * <blockquote><q>
 *     A JSON DGGRS definition is intended to provide a complete and unambiguous definition
 *     of a DGGRS, including the topology of the grids hierarchy, the mapping to a particular
 *     underlying Earth model, the zone identifier reference system and the deterministic sub-zone order...
 *     </q><i><small><a href="https://docs.ogc.org/is/21-038r1/21-038r1.html#_7ebfa115-515e-4b2c-45c0-bddba8606f56"> OGC API - Discrete Global Grid Systems - Annex B.1: JSON Schema for defining a DGGRS</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetDggrsDefinition extends RequestParameters {

    private String collectionId;
    private String dggrsId;

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
     *     <li>Optional: only set when requesting the DGGRS definition for a specific collection.</li>
     * </ul>
     */
    public GetDggrsDefinition collectionId(String collectionId) {
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
    public GetDggrsDefinition dggrsId(String dggrsId) {
        setDggrsId(dggrsId);
        return this;
    }
}
