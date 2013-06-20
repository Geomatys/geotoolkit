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

import javax.xml.bind.annotation.XmlElementRef;
import org.opengis.metadata.distribution.DataFile;
import org.apache.sis.metadata.iso.distribution.DefaultDataFile;
import org.geotoolkit.internal.jaxb.gco.PropertyType;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.17
 * @module
 */
public final class MX_DataFile extends PropertyType<MX_DataFile, DataFile> {
    /**
     * Empty constructor for JAXB only.
     */
    public MX_DataFile() {
    }

    /**
     * Wraps an DataFile value with a {@code MX_DataFile} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private MX_DataFile(final DataFile metadata) {
        super(metadata);
    }

    /**
     * Returns the DataFile value wrapped by a {@code MX_DataFile} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected MX_DataFile wrap(final DataFile value) {
        return new MX_DataFile(value);
    }

    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     */
    @Override
    protected Class<DataFile> getBoundType() {
        return DataFile.class;
    }

    /**
     * Returns the {@link DefaultDataFile} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public DefaultDataFile getElement() {
        return skip() ? null : DefaultDataFile.castOrCopy(metadata);
    }

    /**
     * Sets the value for the {@link DefaultDataFile}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final DefaultDataFile metadata) {
        this.metadata = metadata;
    }
}
