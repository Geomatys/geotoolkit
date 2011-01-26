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

import javax.xml.bind.annotation.XmlElementRef;

import org.opengis.metadata.spatial.Georectified;
import org.opengis.metadata.spatial.Georeferenceable;
import org.opengis.metadata.spatial.SpatialRepresentation;
import org.opengis.metadata.spatial.GridSpatialRepresentation;
import org.opengis.metadata.spatial.VectorSpatialRepresentation;

import org.geotoolkit.metadata.iso.spatial.DefaultGeorectified;
import org.geotoolkit.metadata.iso.spatial.DefaultGeoreferenceable;
import org.geotoolkit.metadata.iso.spatial.AbstractSpatialRepresentation;
import org.geotoolkit.metadata.iso.spatial.DefaultGridSpatialRepresentation;
import org.geotoolkit.metadata.iso.spatial.DefaultVectorSpatialRepresentation;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 2.5
 * @module
 */
public final class MD_SpatialRepresentation
        extends MetadataAdapter<MD_SpatialRepresentation, SpatialRepresentation>
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
     * Returns the {@link AbstractSpatialRepresentation} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public AbstractSpatialRepresentation getElement() {
        final SpatialRepresentation metadata = this.metadata;
        if (metadata instanceof AbstractSpatialRepresentation) {
            return (AbstractSpatialRepresentation) metadata;
        }
        if (metadata instanceof GridSpatialRepresentation) {
            if (metadata instanceof Georectified) {
                return new DefaultGeorectified((Georectified) metadata);
            }
            if (metadata instanceof Georeferenceable) {
                return new DefaultGeoreferenceable((Georeferenceable) metadata);
            }
            return new DefaultGridSpatialRepresentation((GridSpatialRepresentation) metadata);
        }
        if (metadata instanceof VectorSpatialRepresentation) {
            return new DefaultVectorSpatialRepresentation((VectorSpatialRepresentation) metadata);
        }
        return new AbstractSpatialRepresentation(metadata);
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
