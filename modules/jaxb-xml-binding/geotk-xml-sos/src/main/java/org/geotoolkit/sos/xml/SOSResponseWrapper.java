/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.sos.xml;

import org.geotoolkit.gml.xml.v311.AbstractFeatureType;
import org.geotoolkit.gml.xml.v311.AbstractTimePrimitiveType;
import org.geotoolkit.observation.xml.v100.ObservationCollectionType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SOSResponseWrapper implements SOSResponse {

    private Object response;

    public SOSResponseWrapper(final ObservationCollectionType collection) {
        this.response = collection;
    }

    public SOSResponseWrapper(final AbstractFeatureType feature) {
        this.response = feature;
    }

    public SOSResponseWrapper(final AbstractTimePrimitiveType time) {
        this.response = time;
    }

    /**
     * @return the collection
     */
    public Object getCollection() {
        return response;
    }

    /**
     * @param collection the collection to set
     */
    public void setCollection(final Object response) {
        this.response = response;
    }
}
