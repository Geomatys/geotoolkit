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
import org.opengis.metadata.MetadataExtensionInformation;
import org.apache.sis.metadata.iso.DefaultMetadataExtensionInformation;
import org.geotoolkit.internal.jaxb.gco.PropertyType;


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
public final class MD_MetadataExtensionInformation
        extends PropertyType<MD_MetadataExtensionInformation, MetadataExtensionInformation>
{
    /**
     * Empty constructor for JAXB only.
     */
    public MD_MetadataExtensionInformation() {
    }

    /**
     * Wraps an MetadataExtensionInformation value with a {@code MD_MetadataExtensionInformation}
     * element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_MetadataExtensionInformation(final MetadataExtensionInformation metadata) {
        super(metadata);
    }

    /**
     * Returns the MetadataExtensionInformation value wrapped by a
     * {@code MD_MetadataExtensionInformation} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MD_MetadataExtensionInformation wrap(final MetadataExtensionInformation value) {
        return new MD_MetadataExtensionInformation(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<MetadataExtensionInformation> getBoundType() {
        return MetadataExtensionInformation.class;
    }

    /**
     * Returns the {@link DefaultMetadataExtensionInformation} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultMetadataExtensionInformation getElement() {
        return skip() ? null : DefaultMetadataExtensionInformation.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultMetadataExtensionInformation}.
     * This method is systematically called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultMetadataExtensionInformation metadata) {
        this.metadata = metadata;
    }
}
