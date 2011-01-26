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

import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.FeatureCatalogueDescription;
import org.opengis.metadata.content.ImageDescription;

import org.geotoolkit.metadata.iso.content.AbstractContentInformation;
import org.geotoolkit.metadata.iso.content.DefaultCoverageDescription;
import org.geotoolkit.metadata.iso.content.DefaultFeatureCatalogueDescription;
import org.geotoolkit.metadata.iso.content.DefaultImageDescription;


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
public final class MD_ContentInformation
        extends MetadataAdapter<MD_ContentInformation, ContentInformation>
{
    /**
     * Empty constructor for JAXB only.
     */
    public MD_ContentInformation() {
    }

    /**
     * Wraps an ContentInformation value with a {@code MD_ContentInformation} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MD_ContentInformation(final ContentInformation metadata) {
        super(metadata);
    }

    /**
     * Returns the ContentInformation value wrapped by a {@code MD_ContentInformation} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MD_ContentInformation wrap(final ContentInformation value) {
        return new MD_ContentInformation(value);
    }

    /**
     * Returns the {@link AbstractContentInformation} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public AbstractContentInformation getElement() {
        final ContentInformation metadata = this.metadata;
        if (metadata instanceof AbstractContentInformation) {
            return (AbstractContentInformation) metadata;
        }
        if (metadata instanceof ImageDescription) {
            return new DefaultImageDescription((ImageDescription) metadata);
        }
        if (metadata instanceof CoverageDescription) {
            return new DefaultCoverageDescription((CoverageDescription) metadata);
        }
        if (metadata instanceof FeatureCatalogueDescription) {
            return new DefaultFeatureCatalogueDescription((FeatureCatalogueDescription) metadata);
        }
        return new AbstractContentInformation(metadata);
    }

    /**
     * Sets the value for the {@link AbstractContentInformation}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final AbstractContentInformation metadata) {
        this.metadata = metadata;
    }
}
