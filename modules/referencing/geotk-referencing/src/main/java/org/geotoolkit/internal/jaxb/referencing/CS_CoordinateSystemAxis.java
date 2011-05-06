/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
 * @version 3.05
 *
 * @since 3.00
 * @module
 */
public final class CS_CoordinateSystemAxis extends
        MetadataAdapter<CS_CoordinateSystemAxis, CoordinateSystemAxis>
{
    /**
     * Empty constructor for JAXB only.
     */
    public CS_CoordinateSystemAxis() {
    }

    /**
     * Wraps a CoordinateSystemAxis value with a {@code gml:CoordinateSystemAxis} element
     * at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CS_CoordinateSystemAxis(final CoordinateSystemAxis metadata) {
        super(metadata);
    }

    /**
     * Returns the Coordinate System Axis value wrapped by a {@code gml:CoordinateSystemAxis} element.
     *
     * @param  value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CS_CoordinateSystemAxis wrap(final CoordinateSystemAxis value) {
        return new CS_CoordinateSystemAxis(value);
    }

    /**
     * Returns the {@link DefaultCoordinateSystemAxis} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "CoordinateSystemAxis")
    public DefaultCoordinateSystemAxis getElement() {
        if (skip()) return null;
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
    public void setElement(final DefaultCoordinateSystemAxis metadata) {
        this.metadata = metadata;
    }
}
