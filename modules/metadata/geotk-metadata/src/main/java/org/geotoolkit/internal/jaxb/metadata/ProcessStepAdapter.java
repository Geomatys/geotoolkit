/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.metadata.lineage.ProcessStep;
import org.geotoolkit.metadata.iso.lineage.DefaultProcessStep;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
public final class ProcessStepAdapter extends MetadataAdapter<ProcessStepAdapter,ProcessStep> {
    /**
     * Empty constructor for JAXB only.
     */
    public ProcessStepAdapter() {
    }

    /**
     * Wraps an ProcessStep value with a {@code LI_ProcessStep} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private ProcessStepAdapter(final ProcessStep metadata) {
        super(metadata);
    }

    /**
     * Returns the ProcessStep value wrapped by a {@code LI_ProcessStep} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected ProcessStepAdapter wrap(final ProcessStep value) {
        return new ProcessStepAdapter(value);
    }

    /**
     * Returns the {@link DefaultProcessStep} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "LI_ProcessStep")
    public DefaultProcessStep getProcessStep() {
        final ProcessStep metadata = this.metadata;
        return (metadata instanceof DefaultProcessStep) ?
            (DefaultProcessStep) metadata : new DefaultProcessStep(metadata);
    }

    /**
     * Sets the value for the {@link DefaultProcessStep}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setProcessStep(final DefaultProcessStep metadata) {
        this.metadata = metadata;
    }
}
