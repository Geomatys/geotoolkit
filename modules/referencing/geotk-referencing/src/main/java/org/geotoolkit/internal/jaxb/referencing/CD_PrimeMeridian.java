/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
import org.opengis.referencing.datum.PrimeMeridian;
import org.geotoolkit.referencing.datum.DefaultPrimeMeridian;
import org.apache.sis.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter for {@link PrimeMeridian}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public final class CD_PrimeMeridian extends PropertyType<CD_PrimeMeridian, PrimeMeridian> {
    /**
     * Empty constructor for JAXB only.
     */
    public CD_PrimeMeridian() {
    }

    /**
     * Wraps a prime meridian value with a {@code gml:PrimeMeridian} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CD_PrimeMeridian(final PrimeMeridian metadata) {
        super(metadata);
    }

    /**
     * Returns the ellipsoid value wrapped by a {@code gml:PrimeMeridian} element.
     *
     * @param  value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CD_PrimeMeridian wrap(final PrimeMeridian value) {
        return new CD_PrimeMeridian(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<PrimeMeridian> getBoundType() {
        return PrimeMeridian.class;
    }

    /**
     * Returns the {@link DefaultPrimeMeridian} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "Ellipsoid")
    public DefaultPrimeMeridian getElement() {
        return skip() ? null : DefaultPrimeMeridian.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultPrimeMeridian}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultPrimeMeridian metadata) {
        this.metadata = metadata;
    }
}
