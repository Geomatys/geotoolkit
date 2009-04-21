/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.referencing;

import javax.xml.bind.annotation.XmlElement;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;
import org.opengis.referencing.cs.CoordinateSystemAxis;


/**
 * JAXB adapter for {@link CoordinateSystemAxis}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public final class CoordinateSystemAxisAdapter extends
        MetadataAdapter<CoordinateSystemAxisAdapter, CoordinateSystemAxis>
{
    /**
     * Empty constructor for JAXB only.
     */
    public CoordinateSystemAxisAdapter() {
    }

    /**
     * Wraps a CoordinateSystemAxis value with a {@code gml:CoordinateSystemAxis} element
     * at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CoordinateSystemAxisAdapter(final CoordinateSystemAxis metadata) {
        super(metadata);
    }

    /**
     * Returns the Coordinate System Axis value wrapped by a {@code gml:CoordinateSystemAxis} element.
     *
     * @param  value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CoordinateSystemAxisAdapter wrap(final CoordinateSystemAxis value) {
        return new CoordinateSystemAxisAdapter(value);
    }

    /**
     * Returns the {@link DefaultCoordinateSystemAxis} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "CoordinateSystemAxis")
    public DefaultCoordinateSystemAxis getCoordinateSystemAxis() {
        final CoordinateSystemAxis metadata = this.metadata;
        return (metadata instanceof DefaultCoordinateSystemAxis) ?
            (DefaultCoordinateSystemAxis) metadata : new DefaultCoordinateSystemAxis(metadata);
    }

    /**
     * Sets the value for the {@link DefaultCoordinateSystemAxis}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setCoordinateSystemAxis(final DefaultCoordinateSystemAxis metadata) {
        this.metadata = metadata;
    }
}
