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
import org.opengis.referencing.cs.EllipsoidalCS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.apache.sis.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter for {@link EllipsoidalCS}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.04
 * @module
 */
public final class CS_EllipsoidalCS extends PropertyType<CS_EllipsoidalCS, EllipsoidalCS> {
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
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<EllipsoidalCS> getBoundType() {
        return EllipsoidalCS.class;
    }

    /**
     * Returns the {@link DefaultEllipsoidalCS} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "EllipsoidalCS")
    public DefaultEllipsoidalCS getElement() {
        return DefaultEllipsoidalCS.castOrCopy(metadata);
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
