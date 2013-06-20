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
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlElementRef;
import org.opengis.metadata.extent.GeographicExtent;
import org.apache.sis.metadata.iso.extent.AbstractGeographicExtent;
import org.geotoolkit.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 2.5
 * @module
 */
public final class EX_GeographicExtent extends PropertyType<EX_GeographicExtent, GeographicExtent> {
    /**
     * Empty constructor for JAXB only.
     */
    public EX_GeographicExtent() {
    }

    /**
     * Wraps an GeographicExtent value with a {@code EX_GeographicExtent} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private EX_GeographicExtent(final GeographicExtent metadata) {
        super(metadata);
    }

    /**
     * Returns the GeographicExtent value wrapped by a {@code EX_GeographicExtent} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected EX_GeographicExtent wrap(final GeographicExtent value) {
        return new EX_GeographicExtent(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<GeographicExtent> getBoundType() {
        return GeographicExtent.class;
    }

    /**
     * Returns the {@link AbstractGeographicExtent} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public AbstractGeographicExtent getElement() {
        return skip() ? null : AbstractGeographicExtent.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link AbstractGeographicExtent}. This method
     * is systematically called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final AbstractGeographicExtent metadata) {
        this.metadata = metadata;
    }
}
