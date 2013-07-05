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
package org.geotoolkit.internal.jaxb.referencing;

import javax.xml.bind.annotation.XmlElement;
import org.opengis.referencing.datum.ImageDatum;
import org.geotoolkit.referencing.datum.DefaultImageDatum;
import org.apache.sis.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter for {@link ImageDatum}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.04
 * @module
 */
public final class CD_ImageDatum extends PropertyType<CD_ImageDatum, ImageDatum> {
    /**
     * Empty constructor for JAXB only.
     */
    public CD_ImageDatum() {
    }

    /**
     * Wraps a Vertical Datum value with a {@code gml:imageDatum} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CD_ImageDatum(final ImageDatum metadata) {
        super(metadata);
    }

    /**
     * Returns the ImageDatum value wrapped by a {@code gml:imageDatum} element.
     *
     * @param  value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CD_ImageDatum wrap(final ImageDatum value) {
        return new CD_ImageDatum(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<ImageDatum> getBoundType() {
        return ImageDatum.class;
    }

    /**
     * Returns the {@link DefaultImageDatum} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "ImageDatum")
    public DefaultImageDatum getElement() {
        return skip() ? null : DefaultImageDatum.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultImageDatum}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultImageDatum metadata) {
        this.metadata = metadata;
    }
}
