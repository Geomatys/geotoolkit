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

import org.opengis.metadata.spatial.Georectified;
import org.opengis.metadata.spatial.Georeferenceable;
import org.opengis.metadata.spatial.SpatialRepresentation;

import org.geotoolkit.internal.jaxb.gco.PropertyType;
import org.geotoolkit.internal.jaxb.gmi.MI_Georectified;
import org.geotoolkit.internal.jaxb.gmi.MI_Georeferenceable;
import org.apache.sis.metadata.iso.spatial.AbstractSpatialRepresentation;


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
public final class MD_SpatialRepresentation
        extends PropertyType<MD_SpatialRepresentation, SpatialRepresentation>
{
    /**
     * Empty constructor for JAXB only.
     */
    public MD_SpatialRepresentation() {
    }

    /**
     * Wraps an SpatialRepresentation value with a {@code MD_SpatialRepresentation}
     * element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_SpatialRepresentation(final SpatialRepresentation metadata) {
        super(metadata);
    }

    /**
     * Returns the SpatialRepresentation value wrapped by a {@code MD_SpatialRepresentation} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MD_SpatialRepresentation wrap(final SpatialRepresentation value) {
        return new MD_SpatialRepresentation(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<SpatialRepresentation> getBoundType() {
        return SpatialRepresentation.class;
    }

    /**
     * Returns the {@link AbstractSpatialRepresentation} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public AbstractSpatialRepresentation getElement() {
        if (skip()) return null;
        final SpatialRepresentation metadata = this.metadata;
        if (metadata instanceof Georectified) {
            return MI_Georectified.castOrCopy((Georectified) metadata);
        }
        if (metadata instanceof Georeferenceable) {
            return MI_Georeferenceable.castOrCopy((Georeferenceable) metadata);
        }
        return AbstractSpatialRepresentation.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link AbstractSpatialRepresentation}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final AbstractSpatialRepresentation metadata) {
        this.metadata = metadata;
    }
}
