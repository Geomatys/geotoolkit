/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import org.geotoolkit.metadata.iso.quality.AbstractResult;
import org.opengis.metadata.quality.Result;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 3.04
 * @module
 */
public final class ResultAdapter extends MetadataAdapter<ResultAdapter,Result> {
    /**
     * Empty constructor for JAXB only.
     */
    public ResultAdapter() {
    }

    /**
     * Wraps an Source value with a {@code DQ_Result} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private ResultAdapter(final Result metadata) {
        super(metadata);
    }

    /**
     * Returns the Source value wrapped by a {@code DQ_Result} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected ResultAdapter wrap(final Result value) {
        return new ResultAdapter(value);
    }

    /**
     * Returns the {@link AbstractResult} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public AbstractResult getElement() {
        final Result metadata = this.metadata;
        return (metadata instanceof AbstractResult) ?
            (AbstractResult) metadata : new AbstractResult(metadata);
    }

    /**
     * Sets the value for the {@link AbstractResult}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final AbstractResult metadata) {
        this.metadata = metadata;
    }
}
