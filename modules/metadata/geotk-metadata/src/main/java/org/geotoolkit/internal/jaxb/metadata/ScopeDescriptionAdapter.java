/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import org.opengis.metadata.maintenance.ScopeDescription;
import org.geotoolkit.metadata.iso.maintenance.DefaultScopeDescription;


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
public final class ScopeDescriptionAdapter
        extends MetadataAdapter<ScopeDescriptionAdapter,ScopeDescription>
{
    /**
     * Empty constructor for JAXB only.
     */
    public ScopeDescriptionAdapter() {
    }

    /**
     * Wraps an ScopeDescription value with a {@code MD_ScopeDescription} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private ScopeDescriptionAdapter(final ScopeDescription metadata) {
        super(metadata);
    }

    /**
     * Returns the ScopeDescription value wrapped by a {@code MD_ScopeDescription} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected ScopeDescriptionAdapter wrap(final ScopeDescription value) {
        return new ScopeDescriptionAdapter(value);
    }

    /**
     * Returns the {@link DefaultScopeDescription} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MD_ScopeDescription")
    public DefaultScopeDescription getElement() {
        final ScopeDescription metadata = this.metadata;
        return (metadata instanceof DefaultScopeDescription) ?
            (DefaultScopeDescription) metadata : new DefaultScopeDescription(metadata);
    }

    /**
     * Sets the value for the {@link DefaultScopeDescription}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultScopeDescription metadata) {
        this.metadata = metadata;
    }
}
