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
import org.opengis.referencing.ReferenceSystem;

import org.geotoolkit.xml.Namespaces;


/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface.
 * See package documentation for more information about JAXB and interface.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public class ReferenceSystemAdapter extends MetadataAdapter<ReferenceSystemAdapter,ReferenceSystem> {
    /**
     * Empty constructor for JAXB only.
     */
    public ReferenceSystemAdapter() {
    }

    /**
     * Wraps a Reference System value with a {@code MD_ReferenceSystem} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    ReferenceSystemAdapter(final ReferenceSystem metadata) {
        super(metadata);
    }

    /**
     * Returns the Reference System value covered by a {@code MD_ReferenceSystem} element.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the metadata value.
     */
    @Override
    protected ReferenceSystemAdapter wrap(ReferenceSystem value) {
        return new ReferenceSystemAdapter(value);
    }

    /**
     * Returns the {@link ReferenceSystem} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "MD_ReferenceSystem")
    public ReferenceSystemMetadata getReferenceSystem() {
        final ReferenceSystem metadata = this.metadata;
        if (metadata instanceof ReferenceSystemMetadata) {
            return (ReferenceSystemMetadata) metadata;
        } else {
            return new ReferenceSystemMetadata(metadata);
        }
    }

    /**
     * A hook for the French profile.
     *
     * @return The metadata to be marshalled.
     *
     * @todo We need a better plugin mechanism.
     */
    @XmlElement(name = "FRA_DirectReferenceSystem", namespace = Namespaces.FRA)
    public ReferenceSystemMetadata getDirectReferenceSystem() {
        return null;
    }

    /**
     * A hook for the French profile.
     *
     * @return The metadata to be marshalled.
     *
     * @todo We need a better plugin mechanism.
     */
    @XmlElement(name = "FRA_IndirectReferenceSystem", namespace = Namespaces.FRA)
    public ReferenceSystemMetadata getIndirectReferenceSystem() {
        return null;
    }

    /**
     * Sets the value for the {@link ReferenceSystem}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setReferenceSystem(final ReferenceSystemMetadata metadata) {
        this.metadata = metadata;
    }
}
