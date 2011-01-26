/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.metadata.quality.Result;
import org.opengis.metadata.quality.CoverageResult;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.metadata.quality.QuantitativeResult;

import org.geotoolkit.metadata.iso.quality.AbstractResult;
import org.geotoolkit.metadata.iso.quality.DefaultCoverageResult;
import org.geotoolkit.metadata.iso.quality.DefaultConformanceResult;
import org.geotoolkit.metadata.iso.quality.DefaultQuantitativeResult;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.04
 * @module
 */
public final class DQ_Result extends MetadataAdapter<DQ_Result, Result> {
    /**
     * Empty constructor for JAXB only.
     */
    public DQ_Result() {
    }

    /**
     * Wraps an Source value with a {@code DQ_Result} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private DQ_Result(final Result metadata) {
        super(metadata);
    }

    /**
     * Returns the Source value wrapped by a {@code DQ_Result} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected DQ_Result wrap(final Result value) {
        return new DQ_Result(value);
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
        if (metadata instanceof AbstractResult) {
            return (AbstractResult) metadata;
        }
        if (metadata instanceof QuantitativeResult) {
            return new DefaultQuantitativeResult((QuantitativeResult) metadata);
        }
        if (metadata instanceof CoverageResult) {
            return new DefaultCoverageResult((CoverageResult) metadata);
        }
        if (metadata instanceof ConformanceResult) {
            return new DefaultConformanceResult((ConformanceResult) metadata);
        }
        return new AbstractResult(metadata);
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
