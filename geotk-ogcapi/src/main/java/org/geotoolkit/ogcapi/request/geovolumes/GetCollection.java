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
 * To retrieve a single 3D-Container (GeoVolume).
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetCollection extends org.geotoolkit.ogcapi.request.common.GetCollection {

    private Envelope bbox;

    /**
     * Content within the 3D-Container is filtered to only what is within this bounding box, from the
     * "Spatial Query Extension" requirements class. (optional)
     *
     * @return the bbox
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public GetCollection bbox(Envelope bbox) {
        setBbox(bbox);
        return this;
    }

}
