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
import org.opengis.referencing.datum.GeodeticDatum;
import org.apache.sis.internal.jaxb.gco.PropertyType;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;


/**
 * JAXB adapter for {@link GeodeticDatum}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.04
 * @module
 */
public final class CD_GeodeticDatum extends PropertyType<CD_GeodeticDatum, GeodeticDatum> {
    /**
     * Empty constructor for JAXB only.
     */
    public CD_GeodeticDatum() {
    }

    /**
     * Wraps a Vertical Datum value with a {@code gml:geodeticDatum} element at marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    private CD_GeodeticDatum(final GeodeticDatum metadata) {
        super(metadata);
    }

    /**
     * Returns the GeodeticDatum value wrapped by a {@code gml:geodeticDatum} element.
     *
     * @param  value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected CD_GeodeticDatum wrap(final GeodeticDatum value) {
        return new CD_GeodeticDatum(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<GeodeticDatum> getBoundType() {
        return GeodeticDatum.class;
    }

    /**
     * Returns the {@link DefaultGeodeticDatum} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @XmlElement(name = "GeodeticDatum")
    public DefaultGeodeticDatum getElement() {
        return skip() ? null : DefaultGeodeticDatum.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultGeodeticDatum}.
     * This method is systematically called at unmarshalling-time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultGeodeticDatum metadata) {
        this.metadata = metadata;
    }
}
