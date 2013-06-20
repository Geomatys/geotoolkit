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
import org.opengis.metadata.lineage.ProcessStep;
import org.geotoolkit.internal.jaxb.gco.PropertyType;
import org.geotoolkit.internal.jaxb.gmi.LE_ProcessStep;
import org.apache.sis.metadata.iso.lineage.DefaultProcessStep;


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
public final class LI_ProcessStep extends PropertyType<LI_ProcessStep, ProcessStep> {
    /**
     * Empty constructor for JAXB only.
     */
    public LI_ProcessStep() {
    }

    /**
     * Wraps an ProcessStep value with a {@code LI_ProcessStep} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private LI_ProcessStep(final ProcessStep metadata) {
        super(metadata);
    }

    /**
     * Returns the ProcessStep value wrapped by a {@code LI_ProcessStep} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected LI_ProcessStep wrap(final ProcessStep value) {
        return new LI_ProcessStep(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<ProcessStep> getBoundType() {
        return ProcessStep.class;
    }

    /**
     * Returns the {@link DefaultProcessStep} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultProcessStep getElement() {
        return skip() ? null : LE_ProcessStep.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultProcessStep}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultProcessStep metadata) {
        this.metadata = metadata;
    }
}
