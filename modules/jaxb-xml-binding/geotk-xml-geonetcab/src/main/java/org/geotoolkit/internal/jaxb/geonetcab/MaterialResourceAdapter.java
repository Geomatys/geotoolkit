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

import javax.xml.bind.annotation.XmlElement;
import org.geotoolkit.geotnetcab.GNC_MaterialResource;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 * @module pending
 */
public class MaterialResourceAdapter extends MetadataAdapter<MaterialResourceAdapter, GNC_MaterialResource>
{
    /**
     * Empty constructor for JAXB only.
     */
    public MaterialResourceAdapter() {
    }

    /**
     * Wraps an ContentInformation value with a {@code GNC_MaterialResource} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MaterialResourceAdapter(final GNC_MaterialResource metadata) {
        super(metadata);
    }

    /**
     * Returns the ContentInformation value wrapped by a {@code MD_ContentInformation} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MaterialResourceAdapter wrap(final GNC_MaterialResource value) {
        return new MaterialResourceAdapter(value);
    }

    /**
     * Returns the {@link AbstractContentInformation} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name="GNC_MaterialResource", namespace="http://www.mdweb-project.org/files/xsd")
    public GNC_MaterialResource getElement() {
        return metadata;
    }

    /**
     * Sets the value for the {@link AbstractContentInformation}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final GNC_MaterialResource metadata) {
        this.metadata = metadata;
    }
}
