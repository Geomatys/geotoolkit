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
import org.opengis.metadata.acquisition.AcquisitionInformation;
import org.apache.sis.metadata.iso.acquisition.DefaultAcquisitionInformation;
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
public final class MI_AcquisitionInformation
        extends PropertyType<MI_AcquisitionInformation, AcquisitionInformation>
{
    /**
     * Empty constructor for JAXB only.
     */
    public MI_AcquisitionInformation() {
    }

    /**
     * Wraps an AcquisitionInformation value with a {@code MI_AcquisitionInformation} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MI_AcquisitionInformation(final AcquisitionInformation metadata) {
        super(metadata);
    }

    /**
     * Returns the AcquisitionInformation value wrapped by a {@code MI_AcquisitionInformation} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MI_AcquisitionInformation wrap(final AcquisitionInformation value) {
        return new MI_AcquisitionInformation(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<AcquisitionInformation> getBoundType() {
        return AcquisitionInformation.class;
    }

    /**
     * Returns the {@link DefaultAcquisitionInformation} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultAcquisitionInformation getElement() {
        return skip() ? null : DefaultAcquisitionInformation.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultAcquisitionInformation}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultAcquisitionInformation metadata) {
        this.metadata = metadata;
    }
}
