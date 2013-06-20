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

import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.ImageDescription;

import org.geotoolkit.internal.jaxb.gco.PropertyType;
import org.geotoolkit.internal.jaxb.gmi.MI_ImageDescription;
import org.geotoolkit.internal.jaxb.gmi.MI_CoverageDescription;
import org.apache.sis.metadata.iso.content.AbstractContentInformation;


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
public final class MD_ContentInformation
        extends PropertyType<MD_ContentInformation, ContentInformation>
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
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<ContentInformation> getBoundType() {
        return ContentInformation.class;
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
        if (skip()) return null;
        final ContentInformation metadata = this.metadata;
        if (metadata instanceof ImageDescription) {
            return MI_ImageDescription.castOrCopy((ImageDescription) metadata);
        }
        if (metadata instanceof CoverageDescription) {
            return MI_CoverageDescription.castOrCopy((CoverageDescription) metadata);
        }
        return AbstractContentInformation.castOrCopy(metadata);
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
