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

import javax.xml.bind.annotation.XmlElement;
import org.opengis.metadata.distribution.Distribution;
import org.geotoolkit.metadata.iso.distribution.DefaultDistribution;


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
public final class DistributionAdapter extends MetadataAdapter<DistributionAdapter,Distribution> {
    /**
     * Empty constructor for JAXB only.
     */
    public DistributionAdapter() {
    }

    /**
     * Wraps an Distribution value with a {@code MD_Distribution} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private DistributionAdapter(final Distribution metadata) {
        super(metadata);
    }

    /**
     * Returns the Distribution value wrapped by a {@code MD_Distribution} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected DistributionAdapter wrap(final Distribution value) {
        return new DistributionAdapter(value);
    }

    /**
     * Returns the {@link DefaultDistribution} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElement(name = "MD_Distribution")
    public DefaultDistribution getElement() {
        final Distribution metadata = this.metadata;
        return (metadata instanceof DefaultDistribution) ?
            (DefaultDistribution) metadata : new DefaultDistribution(metadata);
    }

    /**
     * Sets the value for the {@link DefaultDistribution}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultDistribution metadata) {
        this.metadata = metadata;
    }
}
