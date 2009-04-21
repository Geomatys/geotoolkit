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

import javax.xml.bind.annotation.XmlElementRef;
import org.opengis.metadata.spatial.SpatialRepresentation;
import org.geotoolkit.metadata.iso.spatial.AbstractSpatialRepresentation;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.0
 *
 * @since 2.5
 * @module
 */
public final class SpatialRepresentationAdapter
        extends MetadataAdapter<SpatialRepresentationAdapter,SpatialRepresentation>
{
    /**
     * Empty constructor for JAXB only.
     */
    public SpatialRepresentationAdapter() {
    }

    /**
     * Wraps an SpatialRepresentation value with a {@code MD_SpatialRepresentation}
     * element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private SpatialRepresentationAdapter(final SpatialRepresentation metadata) {
        super(metadata);
    }

    /**
     * Returns the SpatialRepresentation value wrapped by a {@code MD_SpatialRepresentation} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected SpatialRepresentationAdapter wrap(final SpatialRepresentation value) {
        return new SpatialRepresentationAdapter(value);
    }

    /**
     * Returns the {@link AbstractSpatialRepresentation} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElementRef
    public AbstractSpatialRepresentation getSpatialRepresentation() {
        final SpatialRepresentation metadata = this.metadata;
        return (metadata instanceof AbstractSpatialRepresentation) ?
            (AbstractSpatialRepresentation) metadata : new AbstractSpatialRepresentation(metadata);
    }

    /**
     * Sets the value for the {@link AbstractSpatialRepresentation}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setSpatialRepresentation(final AbstractSpatialRepresentation metadata) {
        this.metadata = metadata;
    }
}
