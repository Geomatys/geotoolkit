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

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.opengis.referencing.ReferenceSystem;

import org.geotoolkit.internal.jaxb.RegisterableAdapter;
import org.geotoolkit.metadata.fra.FRA_DirectReferenceSystem;
import org.geotoolkit.metadata.fra.FRA_IndirectReferenceSystem;


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
public final class FRA_ReferenceSystemAdapter extends ReferenceSystemAdapter implements RegisterableAdapter {
    /**
     * Empty constructor for JAXB only.
     */
    public FRA_ReferenceSystemAdapter() {
    }

    /**
     * Wraps a Reference System value with a {@code MD_ReferenceSystem} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private FRA_ReferenceSystemAdapter(final ReferenceSystem metadata) {
        super(metadata);
    }

    /**
     * Invoked when a new adapter is created by {@link org.geotoolkit.xml.MarshallerPool}.
     *
     * @param marshaller The marshaller to be configured.
     */
    @Override
    public void register(final Marshaller marshaller) {
        marshaller.setAdapter(ReferenceSystemAdapter.class, this);
    }

    /**
     * Invoked when a new adapter is created by {@link org.geotoolkit.xml.MarshallerPool}.
     *
     * @param unmarshaller The marshaller to be configured.
     */
    @Override
    public void register(final Unmarshaller unmarshaller) {
        unmarshaller.setAdapter(ReferenceSystemAdapter.class, this);
    }

    /**
     * Returns the Reference System value covered by a {@code MD_ReferenceSystem} element.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the metadata value.
     */
    @Override
    protected ReferenceSystemAdapter wrap(ReferenceSystem value) {
        return new FRA_ReferenceSystemAdapter(value);
    }

    /**
     * Returns {@code null} since we do not marshall the {@code "MD_ReferenceSystem"} element.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    public ReferenceSystemMetadata getReferenceSystem() {
        final ReferenceSystem metadata = this.metadata;
        if (metadata instanceof FRA_DirectReferenceSystem || metadata instanceof FRA_IndirectReferenceSystem) {
            return null;
        }
        return super.getReferenceSystem();
    }

    /**
     * Returns the {@link ReferenceSystem} generated from the metadata value for the
     * French profile of metadata. This method is called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    public ReferenceSystemMetadata getDirectReferenceSystem() {
        final ReferenceSystem metadata = this.metadata;
        if (metadata instanceof FRA_DirectReferenceSystem) {
            return (FRA_DirectReferenceSystem) metadata;
        }
        return null;
    }

    /**
     * Returns the {@link ReferenceSystem} generated from the metadata value for the
     * French profile of metadata. This method is called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    public ReferenceSystemMetadata getIndirectReferenceSystem() {
        final ReferenceSystem metadata = this.metadata;
        if (metadata instanceof FRA_IndirectReferenceSystem) {
            return (FRA_IndirectReferenceSystem) metadata;
        }
        return null;
    }

    /**
     * Sets the value for the {@link ReferenceSystem}.
     * This method is called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setDirectReferenceSystem(final ReferenceSystemMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Sets the value for the {@link ReferenceSystem}.
     * This method is called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setIndirectReferenceSystem(final ReferenceSystemMetadata metadata) {
        this.metadata = metadata;
    }
}
