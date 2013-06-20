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
import org.opengis.metadata.distribution.Distributor;
import org.apache.sis.metadata.iso.distribution.DefaultDistributor;
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
public final class MD_Distributor extends PropertyType<MD_Distributor, Distributor> {
    /**
     * Empty constructor for JAXB only.
     */
    public MD_Distributor() {
    }

    /**
     * Wraps an Distributor value with a {@code MD_Distributor} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_Distributor(final Distributor metadata) {
        super(metadata);
    }

    /**
     * Returns the Distributor value wrapped by a {@code MD_Distributor} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MD_Distributor wrap(final Distributor value) {
        return new MD_Distributor(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<Distributor> getBoundType() {
        return Distributor.class;
    }

    /**
     * Returns the {@link DefaultDistributor} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultDistributor getElement() {
        return skip() ? null : DefaultDistributor.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultDistributor}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultDistributor metadata) {
        this.metadata = metadata;
    }
}
