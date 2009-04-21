/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalPrimitive;
import org.geotoolkit.internal.jaxb.metadata.MetadataAdapter;


/**
 * JAXB adapter for {@link TemporalPrimitive}, in order to integrate the value in an element
 * complying with OGC/ISO standard. Note that the CRS is formatted using the GML schema,
 * not the ISO 19139 one.
 * <p>
 * The current implementation is not functional, but is nevertheless declared because this
 * is a required dependency for metadata annotation. This class needs to be subclassed in
 * a temporal module.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public class TemporalPrimitiveAdapter extends MetadataAdapter<TemporalPrimitiveAdapter,TemporalPrimitive> {
    /**
     * Empty constructor for JAXB.
     */
    protected TemporalPrimitiveAdapter() {
    }

    /**
     * Wraps a Temporal Primitive valueat marshalling-time.
     *
     * @param metadata The metadata value to marshall.
     */
    protected TemporalPrimitiveAdapter(final TemporalPrimitive metadata) {
        super(metadata);
    }

    /**
     * Returns the Vertical CRS value wrapped by a {@code gml:VerticalCRS} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected TemporalPrimitiveAdapter wrap(final TemporalPrimitive value) {
        return new TemporalPrimitiveAdapter(value);
    }

    /**
     * Returns the {@link TemporalPrimitive} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     *
     * @return The temporal primitive, or {@code null}.
     *
     * @todo Add other TemporalPrimitive than Period.
     */
    @XmlElement(name = "TimePeriod")
    public TimePeriod getTemporalPrimitive() {
        final TemporalPrimitive metadata = this.metadata;
        if (metadata instanceof Period) {
            return new TimePeriod((Period) metadata);
        }
        return null;
    }

    /**
     * Sets the value from the {@link TemporalPrimitive}.
     * This method is called at unmarshalling-time by JAXB.
     *
     * @param period The adapter to set.
     */
    public void setTemporalPrimitive(final TimePeriod period) {
        // Empty in this implementation. Needs to be overloaded by subclasses.
    }
}
