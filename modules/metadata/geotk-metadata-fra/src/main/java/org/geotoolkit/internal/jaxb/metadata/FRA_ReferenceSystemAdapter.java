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

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.opengis.referencing.ReferenceSystem;

import org.apache.sis.internal.jaxb.AdapterReplacement;
import org.geotoolkit.metadata.fra.FRA_DirectReferenceSystem;
import org.geotoolkit.metadata.fra.FRA_IndirectReferenceSystem;
import org.apache.sis.internal.jaxb.metadata.RS_ReferenceSystem;
import org.apache.sis.internal.jaxb.metadata.ReferenceSystemMetadata;


/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.12
 *
 * @since 3.00
 * @module
 */
public final class FRA_ReferenceSystemAdapter extends RS_ReferenceSystem implements AdapterReplacement {
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
     * Invoked when a new adapter is created by {@link org.apache.sis.xml.MarshallerPool}.
     *
     * @param marshaller The marshaller to be configured.
     */
    @Override
    public void register(final Marshaller marshaller) {
        marshaller.setAdapter(RS_ReferenceSystem.class, this);
    }

    /**
     * Invoked when a new adapter is created by {@link org.apache.sis.xml.MarshallerPool}.
     *
     * @param unmarshaller The marshaller to be configured.
     */
    @Override
    public void register(final Unmarshaller unmarshaller) {
        unmarshaller.setAdapter(RS_ReferenceSystem.class, this);
    }

    /**
     * Returns the Reference System value covered by a {@code MD_ReferenceSystem} element.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the metadata value.
     */
    @Override
    protected RS_ReferenceSystem wrap(ReferenceSystem value) {
        return new FRA_ReferenceSystemAdapter(value);
    }

    /**
     * Returns {@code null} since we do not marshall the {@code "MD_ReferenceSystem"} element.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    public ReferenceSystemMetadata getElement() {
        if (skip()) return null;
        final ReferenceSystem metadata = this.metadata;
        if (metadata instanceof FRA_DirectReferenceSystem || metadata instanceof FRA_IndirectReferenceSystem) {
            return null;
        }
        return super.getElement();
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
}
