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
import org.geotoolkit.metadata.iso.acquisition.DefaultPlan;
import org.opengis.metadata.acquisition.Plan;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 3.02
 * @module
 */
public final class PlanAdapter extends MetadataAdapter<PlanAdapter,Plan> {
    /**
     * Empty constructor for JAXB only.
     */
    public PlanAdapter() {
    }

    /**
     * Wraps an Plan value with a {@code MI_Plan} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private PlanAdapter(final Plan metadata) {
        super(metadata);
    }

    /**
     * Returns the Plan value wrapped by a {@code MI_Plan} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected PlanAdapter wrap(final Plan value) {
        return new PlanAdapter(value);
    }

    /**
     * Returns the {@link DefaultPlan} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MI_Plan")
    public DefaultPlan getElement() {
        final Plan metadata = this.metadata;
        return (metadata instanceof DefaultPlan) ?
            (DefaultPlan) metadata : new DefaultPlan(metadata);
    }

    /**
     * Sets the value for the {@link DefaultPlan}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultPlan metadata) {
        this.metadata = metadata;
    }
}
