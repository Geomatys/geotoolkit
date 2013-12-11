/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.apache.sis.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter for {@link CoordinateSystemAxis}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 * @module
 */
public final class CS_CoordinateSystemAxis extends
        PropertyType<CS_CoordinateSystemAxis, CoordinateSystemAxis>
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
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<CoordinateSystemAxis> getBoundType() {
        return CoordinateSystemAxis.class;
    }

    /**
     * Returns the {@link DefaultCoordinateSystemAxis} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "CoordinateSystemAxis")
    public DefaultCoordinateSystemAxis getElement() {
        return DefaultCoordinateSystemAxis.castOrCopy(metadata);
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
