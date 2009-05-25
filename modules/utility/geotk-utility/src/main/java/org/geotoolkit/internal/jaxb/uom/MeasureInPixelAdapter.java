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
package org.geotoolkit.internal.jaxb.uom;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * The ISO-19103 {@code Measures} with a {@code unit of measure} defined, using the
 * {@code gco} namespace linked to the {@link http://www.isotc211.org/2005/gco} URL.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
public final class MeasureInPixelAdapter extends XmlAdapter<MeasureInPixelAdapter, Double> {
    /**
     * A proxy representation of the {@code <gco:Measure>} element.
     */
    @XmlElement(name = "Measure")
    private UOMProxy measure;

    /**
     * Empty constructor used only by JAXB.
     */
    public MeasureInPixelAdapter() {
    }

    /**
     * Constructs an adapter for the given value.
     *
     * @param value The value.
     */
    private MeasureInPixelAdapter(final Double value) {
        measure = new UOMProxy(value, "pixel");
    }

    /**
     * Allows JAXB to generate a Double object using the value found in the adapter.
     *
     * @param value The value wrapped in an adapter.
     * @return The double value extracted from the adapter.
     */
    @Override
    public Double unmarshal(final MeasureInPixelAdapter value) {
        if (value == null || value.measure == null) {
            return null;
        }
        return value.measure.value;
    }

    /**
     * Allows JAXB to change the result of the marshalling process, according to the
     * ISO-19139 standard and its requirements about {@code measures}.
     *
     * @param value The double value we want to integrate into a {@code <gco:Measure>} element.
     * @return An adaptation of the double value, that is to say a double value surrounded
     *         by {@code <gco:Measure>} element, with an {@code uom} attribute.
     */
    @Override
    public MeasureInPixelAdapter marshal(final Double value) {
        if (value == null) {
            return null;
        }
        return new MeasureInPixelAdapter(value);
    }
}
