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
import org.opengis.metadata.FeatureTypeList;
import org.geotoolkit.metadata.iso.DefaultFeatureTypeList;


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
public final class MD_FeatureTypeList extends MetadataAdapter<MD_FeatureTypeList, FeatureTypeList> {
    /**
     * Empty constructor for JAXB only.
     */
    public MD_FeatureTypeList() {
    }

    /**
     * Wraps an FeatureTypeList value with a {@code MD_FeatureTypeList} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_FeatureTypeList(final FeatureTypeList metadata) {
        super(metadata);
    }

    /**
     * Returns the FeatureTypeList value wrapped by a {@code MD_FeatureTypeList} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MD_FeatureTypeList wrap(final FeatureTypeList value) {
        return new MD_FeatureTypeList(value);
    }

    /**
     * Returns the {@link DefaultFeatureTypeList} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultFeatureTypeList getElement() {
        final FeatureTypeList metadata = this.metadata;
        return (metadata instanceof DefaultFeatureTypeList) ?
            (DefaultFeatureTypeList) metadata : new DefaultFeatureTypeList(metadata);
    }

    /**
     * Sets the value for the {@link DefaultFeatureTypeList}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultFeatureTypeList metadata) {
        this.metadata = metadata;
    }
}
