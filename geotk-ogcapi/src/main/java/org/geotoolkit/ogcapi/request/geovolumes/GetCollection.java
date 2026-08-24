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

import org.opengis.geometry.Envelope;

/**
 * <blockquote><q>
 *     Retrieve the 3d-container for the specified id. Optional query items: bounding box and
 *     return format type.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-3d-geovolumes/blob/master/standard/openapi/ogcapi-3d-geovolumes-draft-0.0.2.yaml"> OGC API - 3D GeoVolumes - describeContainer operation</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetCollection extends org.geotoolkit.ogcapi.request.common.GetCollection {

    private Envelope bbox;

    /**
     * @see #bbox(org.opengis.geometry.Envelope)
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @see #bbox(org.opengis.geometry.Envelope)
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    /**
     * <blockquote><q>
     *     Only features that have a geometry that intersects the bounding box are selected.
     *     The bounding box is provided as four or six numbers, depending on whether the
     *     coordinate reference system includes a vertical axis (elevation or depth).
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-3d-geovolumes/blob/master/standard/openapi/ogcapi-3d-geovolumes-draft-0.0.2.yaml"> OGC API - 3D GeoVolumes - bbox parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Content within the collection is filtered to only what is within this bounding box.</li>
     *     <li>The bounding box CRS is derived from this envelope's own coordinate reference system.</li>
     * </ul>
     */
    public GetCollection bbox(Envelope bbox) {
        setBbox(bbox);
        return this;
    }

}
