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

import javax.xml.bind.annotation.XmlElementRef;

import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicDescription;

import org.geotoolkit.metadata.iso.extent.AbstractGeographicExtent;
import org.geotoolkit.metadata.iso.extent.DefaultBoundingPolygon;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicDescription;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 2.5
 * @module
 */
public final class GeographicExtentAdapter extends
        MetadataAdapter<GeographicExtentAdapter,GeographicExtent>
{
    /**
     * Empty constructor for JAXB only.
     */
    public GeographicExtentAdapter() {
    }

    /**
     * Wraps an GeographicExtent value with a {@code EX_GeographicExtent} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private GeographicExtentAdapter(final GeographicExtent metadata) {
        super(metadata);
    }

    /**
     * Returns the GeographicExtent value wrapped by a {@code EX_GeographicExtent} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected GeographicExtentAdapter wrap(final GeographicExtent value) {
        return new GeographicExtentAdapter(value);
    }

    /**
     * Returns the {@link AbstractGeographicExtent} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public AbstractGeographicExtent getElement() {
        final GeographicExtent metadata = this.metadata;
        if (metadata instanceof AbstractGeographicExtent) {
            return (AbstractGeographicExtent) metadata;
        }
        if (metadata instanceof BoundingPolygon) {
            return new DefaultBoundingPolygon((BoundingPolygon) metadata);
        }
        if (metadata instanceof GeographicBoundingBox) {
            return new DefaultGeographicBoundingBox((GeographicBoundingBox) metadata);
        }
        if (metadata instanceof GeographicDescription) {
            return new DefaultGeographicDescription((GeographicDescription) metadata);
        }
        return new AbstractGeographicExtent(metadata);
    }

    /**
     * Sets the value for the {@link AbstractGeographicExtent}. This method
     * is systematically called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final AbstractGeographicExtent metadata) {
        this.metadata = metadata;
    }
}
