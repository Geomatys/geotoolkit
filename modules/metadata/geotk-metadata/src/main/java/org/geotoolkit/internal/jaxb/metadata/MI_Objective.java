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
import org.opengis.metadata.acquisition.Objective;
import org.apache.sis.metadata.iso.acquisition.DefaultObjective;
import org.geotoolkit.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.02
 * @module
 */
public final class MI_Objective extends PropertyType<MI_Objective, Objective> {
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
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<Objective> getBoundType() {
        return Objective.class;
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
        return skip() ? null : DefaultObjective.castOrCopy(metadata);
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
