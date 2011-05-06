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
import org.opengis.metadata.spatial.GeometricObjects;
import org.geotoolkit.metadata.iso.spatial.DefaultGeometricObjects;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 2.5
 * @module
 */
public final class MD_GeometricObjects extends MetadataAdapter<MD_GeometricObjects, GeometricObjects> {
    /**
     * Empty constructor for JAXB only.
     */
    public MD_GeometricObjects() {
    }

    /**
     * Wraps an GeometricObjects value with a {@code MD_GeometricObjects} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_GeometricObjects(final GeometricObjects metadata) {
        super(metadata);
    }

    /**
     * Returns the GeometricObjects value wrapped by a {@code MD_GeometricObjects} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MD_GeometricObjects wrap(final GeometricObjects value) {
        return new MD_GeometricObjects(value);
    }

    /**
     * Returns the {@link DefaultGeometricObjects} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultGeometricObjects getElement() {
        if (skip()) return null;
        final GeometricObjects metadata = this.metadata;
        return (metadata instanceof DefaultGeometricObjects) ?
            (DefaultGeometricObjects) metadata : new DefaultGeometricObjects(metadata);
    }

    /**
     * Sets the value for the {@link DefaultGeometricObjects}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultGeometricObjects metadata) {
        this.metadata = metadata;
    }
}
