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

import javax.xml.bind.annotation.XmlElementRef;
import org.opengis.metadata.acquisition.Objective;
import org.geotoolkit.metadata.iso.acquisition.DefaultObjective;


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
public final class MI_Objective extends MetadataAdapter<MI_Objective, Objective> {
    /**
     * Empty constructor for JAXB only.
     */
    public MI_Objective() {
    }

    /**
     * Wraps an Objective value with a {@code MI_Objective} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MI_Objective(final Objective metadata) {
        super(metadata);
    }

    /**
     * Returns the Objective value wrapped by a {@code MI_Objective} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MI_Objective wrap(final Objective value) {
        return new MI_Objective(value);
    }

    /**
     * Returns the {@link DefaultObjective} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultObjective getElement() {
        if (skip()) return null;
        final Objective metadata = this.metadata;
        return (metadata instanceof DefaultObjective) ?
            (DefaultObjective) metadata : new DefaultObjective(metadata);
    }

    /**
     * Sets the value for the {@link DefaultObjective}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultObjective metadata) {
        this.metadata = metadata;
    }
}
