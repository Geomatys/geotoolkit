/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.uom;

import javax.measure.unit.Unit;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.geotoolkit.measure.Units;


/**
 * JAXB adapter for unit of measurement.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
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
        if (value != null) {
            value = value.trim();
            if (value.length() != 0) {
                return Units.valueOf(value);
            }
        }
        return null;
    }

    /**
     * Returns the symbol of the given unit.
     *
     * @param  value The unit.
     * @return The unit symbol.
     */
    @Override
    public String marshal(final Unit<?> value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
