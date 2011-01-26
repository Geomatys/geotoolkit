/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
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
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.opengis.referencing.cs.EllipsoidalCS;


/**
 * JAXB adapter for {@link EllipsoidalCS}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 3.04
 * @module
 */
public final class CS_EllipsoidalCS extends MetadataAdapter<CS_EllipsoidalCS, EllipsoidalCS> {
    /**
     * Empty constructor for JAXB only.
     */
    public CS_EllipsoidalCS() {
    }

    /**
     * Wraps a {@link EllipsoidalCS} value with a {@code gml:ellipsoidalCS} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CS_EllipsoidalCS(final EllipsoidalCS metadata) {
        super(metadata);
    }

    /**
     * Returns the {@link EllipsoidalCS} value wrapped by a {@code gml:ellipsoidalCS} element.
     *
     * @param  value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CS_EllipsoidalCS wrap(final EllipsoidalCS value) {
        return new CS_EllipsoidalCS(value);
    }

    /**
     * Returns the {@link DefaultEllipsoidalCS} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "EllipsoidalCS")
    public DefaultEllipsoidalCS getElement() {
        final EllipsoidalCS metadata = this.metadata;
        return (metadata instanceof DefaultEllipsoidalCS) ?
            (DefaultEllipsoidalCS) metadata : new DefaultEllipsoidalCS(metadata);
    }

    /**
     * Sets the value for the {@link DefaultCartesianCS}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultEllipsoidalCS metadata) {
        this.metadata = metadata;
    }
}
