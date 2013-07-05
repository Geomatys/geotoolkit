/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.referencing.cs.AffineCS;
import org.geotoolkit.referencing.cs.DefaultAffineCS;
import org.apache.sis.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter for {@link AffineCS}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.04
 * @module
 */
public final class CS_AffineCS extends PropertyType<CS_AffineCS, AffineCS> {
    /**
     * Empty constructor for JAXB only.
     */
    public CS_AffineCS() {
    }

    /**
     * Wraps a {@link AffineCS} value with a {@code gml:affineCS} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CS_AffineCS(final AffineCS metadata) {
        super(metadata);
    }

    /**
     * Returns the {@link AffineCS} value wrapped by a {@code gml:affineCS} element.
     *
     * @param  value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CS_AffineCS wrap(final AffineCS value) {
        return new CS_AffineCS(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<AffineCS> getBoundType() {
        return AffineCS.class;
    }

    /**
     * Returns the {@link DefaultAffineCS} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "AffineCS")
    public DefaultAffineCS getElement() {
        return skip() ? null : DefaultAffineCS.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultAffineCS}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultAffineCS metadata) {
        this.metadata = metadata;
    }
}
