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
import org.opengis.metadata.acquisition.Platform;
import org.geotoolkit.metadata.iso.acquisition.DefaultPlatform;
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
public final class PlatformAdapter extends MetadataAdapter<PlatformAdapter,Platform> {
    /**
     * Empty constructor for JAXB only.
     */
    public PlatformAdapter() {
    }

    /**
     * Wraps an PlatformPass value with a {@code MI_PlatformPass} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private PlatformAdapter(final Platform metadata) {
        super(metadata);
    }

    /**
     * Returns the value wrapped by a {@code MI_PlatformPass} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected PlatformAdapter wrap(final Platform value) {
        return new PlatformAdapter(value);
    }

    /**
     * Returns the {@link DefaultPlatform} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MI_Platform", namespace = Namespaces.GMI)
    public DefaultPlatform getElement() {
        final Platform metadata = this.metadata;
        return (metadata instanceof DefaultPlatform) ?
            (DefaultPlatform) metadata : new DefaultPlatform(metadata);
    }

    /**
     * Sets the value for the {@link DefaultPlatform}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultPlatform metadata) {
        this.metadata = metadata;
    }
}
