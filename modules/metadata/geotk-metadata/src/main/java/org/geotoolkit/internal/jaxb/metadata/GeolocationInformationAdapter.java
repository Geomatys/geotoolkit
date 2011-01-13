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

import org.opengis.metadata.spatial.GCPCollection;
import org.opengis.metadata.spatial.GeolocationInformation;

import org.geotoolkit.metadata.iso.spatial.DefaultGCPCollection;
import org.geotoolkit.metadata.iso.spatial.AbstractGeolocationInformation;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.02
 * @module
 */
public final class GeolocationInformationAdapter extends MetadataAdapter<GeolocationInformationAdapter,GeolocationInformation> {
    /**
     * Empty constructor for JAXB only.
     */
    public GeolocationInformationAdapter() {
    }

    /**
     * Wraps an GeolocationInformation value with a {@code MI_GeolocationInformation} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private GeolocationInformationAdapter(final GeolocationInformation metadata) {
        super(metadata);
    }

    /**
     * Returns the GeolocationInformation value wrapped by a {@code MI_GeolocationInformation} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected GeolocationInformationAdapter wrap(final GeolocationInformation value) {
        return new GeolocationInformationAdapter(value);
    }

    /**
     * Returns the {@link AbstractGeolocationInformation} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public AbstractGeolocationInformation getElement() {
        final GeolocationInformation metadata = this.metadata;
        if (metadata instanceof AbstractGeolocationInformation) {
            return (AbstractGeolocationInformation) metadata;
        }
        if (metadata instanceof GCPCollection) {
            return new DefaultGCPCollection((GCPCollection) metadata);
        }
        return new AbstractGeolocationInformation(metadata);
    }

    /**
     * Sets the value for the {@link AbstractGeolocationInformation}.
     * This method is systematically called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final AbstractGeolocationInformation metadata) {
        this.metadata = metadata;
    }
}
