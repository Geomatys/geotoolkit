/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.jaxb.metadata.direct;

import org.opengis.metadata.citation.OnlineResource;
import org.apache.sis.metadata.iso.citation.DefaultOnlineResource;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
public final class CI_OnlineResource extends MetadataAdapter<OnlineResource, DefaultOnlineResource> {
    /**
     * Converts a GeoAPI interface to the Geotk implementation for XML marshalling.
     *
     * @param  value The bound type value, here the GeoAPI interface.
     * @return The adapter for the given value, here the Geotk implementation.
     */
    @Override
    public DefaultOnlineResource marshal(final OnlineResource value) {
        return DefaultOnlineResource.castOrCopy(value);
    }
}
