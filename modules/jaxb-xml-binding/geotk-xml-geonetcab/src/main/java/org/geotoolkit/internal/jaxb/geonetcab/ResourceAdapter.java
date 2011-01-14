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

package org.geotoolkit.internal.jaxb.geonetcab;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import org.geotoolkit.geotnetcab.GNC_Resource;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module pending
 */
public class ResourceAdapter extends MetadataAdapter<ResourceAdapter, GNC_Resource>
{
    /**
     * Empty constructor for JAXB only.
     */
    public ResourceAdapter() {
    }

    /**
     * Wraps an ContentInformation value with a {@code GNC_Resource} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private ResourceAdapter(final GNC_Resource metadata) {
        super(metadata);
    }

    /**
     * Returns the ContentInformation value wrapped by a {@code GNC_Resource} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected ResourceAdapter wrap(final GNC_Resource value) {
        return new ResourceAdapter(value);
    }

    /**
     * Returns the {@link GNC_Resource} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public GNC_Resource getElement() {
        if (metadata.getHref() == null) {
            return metadata;
        }
        return null;
    }

    /**
     * Sets the value for the {@link AbstractContentInformation}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final GNC_Resource metadata) {
        this.metadata = metadata;
    }

    /**
     * Returns the {@link String} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlAttribute(name="href", namespace="http://www.w3.org/1999/xlink")
    public String getReference() {
        if (metadata.getHref() != null) {
            return metadata.getHref();
        }
        return null;
    }

    /**
     * Sets the value for the {@link String}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setReference(final String href) {
        if (href != null) {
            this.metadata = new GNC_Resource();
            this.metadata.setHref(href);
        }
    }
 }
}
