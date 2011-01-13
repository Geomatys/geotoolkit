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
import org.opengis.metadata.acquisition.Requirement;
import org.geotoolkit.metadata.iso.acquisition.DefaultRequirement;
import org.geotoolkit.xml.Namespaces;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.16
 *
 * @since 3.02
 * @module
 */
public final class RequirementAdapter extends MetadataAdapter<RequirementAdapter,Requirement> {
    /**
     * Empty constructor for JAXB only.
     */
    public RequirementAdapter() {
    }

    /**
     * Wraps an Requirement value with a {@code MI_Requirement} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private RequirementAdapter(final Requirement metadata) {
        super(metadata);
    }

    /**
     * Returns the Requirement value wrapped by a {@code MI_Requirement} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected RequirementAdapter wrap(final Requirement value) {
        return new RequirementAdapter(value);
    }

    /**
     * Returns the {@link DefaultRequirement} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MI_Requirement", namespace = Namespaces.GMI)
    public DefaultRequirement getElement() {
        final Requirement metadata = this.metadata;
        return (metadata instanceof DefaultRequirement) ?
            (DefaultRequirement) metadata : new DefaultRequirement(metadata);
    }

    /**
     * Sets the value for the {@link DefaultRequirement}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultRequirement metadata) {
        this.metadata = metadata;
    }
}
