/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.gco;

import javax.measure.unit.SI;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * The ISO-19103 {@code Distance} with a {@code unit of measure} defined, using the
 * {@code gco} namespace linked to the {@code http://www.isotc211.org/2005/gco} URL.
 * <p>
 * This class is identical to {@link GO_Measure} except for the name of the
 * element, which is {@code "Distance"}.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 2.5
 * @module
 */
public final class GO_Distance extends XmlAdapter<GO_Distance, Double> {
    /**
     * A proxy representation of the {@code <gco:Distance>} element.
     */
    @XmlElement(name = "Distance")
    private Measure distance;

    /**
     * Empty constructor used only by JAXB.
     */
    public GO_Distance() {
    }

    /**
     * Constructs an adapter for the given value.
     *
     * @param value The value.
     */
    private GO_Distance(final Double value) {
        distance = new Measure(value, SI.METRE);
    }

    /**
     * Allows JAXB to generate a Double object using the value found in the adapter.
     *
     * @param value The value wrapped in an adapter.
     * @return The double value extracted from the adapter.
     */
    @Override
    public Double unmarshal(final GO_Distance value) {
        if (value != null) {
            final Measure distance = value.distance;
            if (distance != null) {
                return distance.value;
            }
        }
        return null;
    }

    /**
     * Allows JAXB to change the result of the marshalling process, according to the
     * ISO-19139 standard and its requirements about {@code measures}.
     *
     * @param value The double value we want to integrate into a {@code <gco:Distance>} element.
     * @return An adaptation of the double value, that is to say a double value surrounded
     *         by {@code <gco:Distance>} element, with an {@code uom} attribute.
     */
    @Override
    public GO_Distance marshal(final Double value) {
        return (value != null) ? new GO_Distance(value) : null;
    }
}
