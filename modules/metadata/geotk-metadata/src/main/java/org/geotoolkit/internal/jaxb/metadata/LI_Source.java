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
import org.opengis.metadata.lineage.Source;
import org.geotoolkit.internal.jaxb.gmi.LE_Source;
import org.geotoolkit.internal.jaxb.gco.PropertyType;
import org.apache.sis.metadata.iso.lineage.DefaultSource;


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
public final class LI_Source extends PropertyType<LI_Source, Source> {
    /**
     * Empty constructor for JAXB only.
     */
    public LI_Source() {
    }

    /**
     * Wraps an Source value with a {@code LI_Source} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private LI_Source(final Source metadata) {
        super(metadata);
    }

    /**
     * Returns the Source value wrapped by a {@code LI_Source} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected LI_Source wrap(final Source value) {
        return new LI_Source(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<Source> getBoundType() {
        return Source.class;
    }

    /**
     * Returns the {@link DefaultSource} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultSource getElement() {
        return skip() ? null : LE_Source.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultSource}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultSource metadata) {
        this.metadata = metadata;
    }
}
