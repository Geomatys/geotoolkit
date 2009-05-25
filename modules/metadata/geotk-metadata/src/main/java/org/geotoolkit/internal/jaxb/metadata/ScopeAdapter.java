/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
import org.opengis.metadata.quality.Scope;
import org.geotoolkit.metadata.iso.quality.DefaultScope;


/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class ScopeAdapter extends MetadataAdapter<ScopeAdapter,Scope> {
    /**
     * Empty constructor for JAXB only.
     */
    public ScopeAdapter() {
    }

    /**
     * Wraps a Scope value with a {@code MD_Scope} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private ScopeAdapter(final Scope metadata) {
        super(metadata);
    }

    /**
     * Returns the Scope value covered by a {@code MD_Scope} element.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the metadata value.
     */
    @Override
    protected ScopeAdapter wrap(final Scope value) {
        return new ScopeAdapter(value);
    }

    /**
     * Returns the {@link DefaultScope} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "DQ_Scope")
    public DefaultScope getScope() {
        final Scope metadata = this.metadata;
        return (metadata instanceof DefaultScope) ?
            (DefaultScope) metadata : new DefaultScope(metadata);
    }

    /**
     * Sets the value for the {@link DefaultScope}. This method is systematically
     * called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setScope(final DefaultScope metadata) {
        this.metadata = metadata;
    }
}
