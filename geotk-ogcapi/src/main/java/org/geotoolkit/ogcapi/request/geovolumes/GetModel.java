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
package org.geotoolkit.ogcapi.request.geovolumes;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     The server SHALL support retrieving a 3D model for a given model id as an HTTP GET
 *     request using that link template, typically resolving to  to /collections/{collectionId}/models/{modelID}.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-3d-geovolumes/blob/master/standard/sections/clause_11_points.adoc"> OGC API - 3D GeoVolumes - Tiled points instantiating 3D models</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetModel extends RequestParameters {

    private String modelId;

    /**
     * @see #modelId(java.lang.String)
     */
    public String getModelId() {
        return modelId;
    }

    /**
     * @see #modelId(java.lang.String)
     */
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    /**
     * <blockquote><q>
     *     Each point tile feature SHALL contain a non-zero 64-bit integer model identifier
     *     called modelID.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-3d-geovolumes/blob/master/standard/sections/clause_11_points.adoc"> OGC API - 3D GeoVolumes - Tiled points instantiating 3D models</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>The model identifier of a tiled point feature obtained by previously having queried the tile data.</li>
     * </ul>
     */
    public GetModel modelId(String modelId) {
        setModelId(modelId);
        return this;
    }

}
