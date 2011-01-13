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
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.metadata.identification.RepresentativeFraction;
import org.geotoolkit.metadata.iso.identification.DefaultRepresentativeFraction;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 2.5
 * @module
 */
public final class RepresentativeFractionAdapter
        extends MetadataAdapter<RepresentativeFractionAdapter,RepresentativeFraction>
{
    /**
     * Empty constructor for JAXB only.
     */
    public RepresentativeFractionAdapter() {
    }

    /**
     * Wraps an RepresentativeFraction value with a {@code MD_RepresentativeFraction}
     * element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private RepresentativeFractionAdapter(final RepresentativeFraction metadata) {
        super(metadata);
    }

    /**
     * Returns the RepresentativeFraction value wrapped by a
     * {@code MD_RepresentativeFraction} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected RepresentativeFractionAdapter wrap(final RepresentativeFraction value) {
        return new RepresentativeFractionAdapter(value);
    }

    /**
     * Returns the {@link DefaultRepresentativeFraction} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MD_RepresentativeFraction")
    public DefaultRepresentativeFraction getElement() {
        final RepresentativeFraction metadata = this.metadata;
        return (metadata instanceof DefaultRepresentativeFraction) ?
            (DefaultRepresentativeFraction) metadata :
            new DefaultRepresentativeFraction(metadata.getDenominator());
    }

    /**
     * Sets the value for the {@link DefaultRepresentativeFraction}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultRepresentativeFraction metadata) {
        this.metadata = metadata;
    }
}
