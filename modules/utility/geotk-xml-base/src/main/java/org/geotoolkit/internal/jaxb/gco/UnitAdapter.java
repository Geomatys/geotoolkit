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
package org.geotoolkit.internal.jaxb.gco;

import javax.measure.unit.Unit;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.geotoolkit.internal.jaxb.MarshalContext;


/**
 * JAXB adapter for unit of measurement.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 2.5
 * @module
 */
public final class UnitAdapter extends XmlAdapter<String, Unit<?>> {
    /**
     * Returns a unit for the given string.
     *
     * @param  value The unit symbol.
     * @return The unit for the given symbol.
     * @throws IllegalArgumentException if the given symbol is unknown.
     */
    @Override
    public Unit<?> unmarshal(String value) throws IllegalArgumentException {
        return MarshalContext.converters().toUnit(value);
    }

    /**
     * Returns the symbol of the given unit.
     *
     * @param  value The unit.
     * @return The unit symbol.
     */
    @Override
    public String marshal(final Unit<?> value) {
        return (value != null) ? value.toString() : null;
    }
}
