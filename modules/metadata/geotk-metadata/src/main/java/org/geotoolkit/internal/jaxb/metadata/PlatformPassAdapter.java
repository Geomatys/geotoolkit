/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import org.opengis.metadata.acquisition.PlatformPass;
import org.geotoolkit.metadata.iso.acquisition.DefaultPlatformPass;
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
public final class PlatformPassAdapter extends MetadataAdapter<PlatformPassAdapter,PlatformPass> {
    /**
     * Empty constructor for JAXB only.
     */
    public PlatformPassAdapter() {
    }

    /**
     * Wraps an PlatformPass value with a {@code MI_PlatformPass} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private PlatformPassAdapter(final PlatformPass metadata) {
        super(metadata);
    }

    /**
     * Returns the PlatformPass value wrapped by a {@code MI_PlatformPass} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected PlatformPassAdapter wrap(final PlatformPass value) {
        return new PlatformPassAdapter(value);
    }

    /**
     * Returns the {@link DefaultPlatformPass} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MI_PlatformPass", namespace = Namespaces.GMI)
    public DefaultPlatformPass getElement() {
        final PlatformPass metadata = this.metadata;
        return (metadata instanceof DefaultPlatformPass) ?
            (DefaultPlatformPass) metadata : new DefaultPlatformPass(metadata);
    }

    /**
     * Sets the value for the {@link DefaultPlatformPass}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultPlatformPass metadata) {
        this.metadata = metadata;
    }
}
