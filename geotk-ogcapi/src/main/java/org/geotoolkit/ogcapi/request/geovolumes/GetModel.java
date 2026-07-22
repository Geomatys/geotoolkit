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
 * To retrieve a shared 3D model referenced by tiled point features, from the "Tiled points
 * instantiating 3D models" requirements class.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetModel extends RequestParameters {

    private String modelId;

    /**
     * The {@code modelID} property of a tiled point feature obtained by previously having queried the
     * tile data. (required)
     *
     * @return the modelId
     */
    public String getModelId() {
        return modelId;
    }

    /**
     * @param modelId the modelId to set
     * @see #getModelId()
     */
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    /**
     * @param modelId the modelId to set
     * @see #getModelId()
     */
    public GetModel modelId(String modelId) {
        setModelId(modelId);
        return this;
    }

}
