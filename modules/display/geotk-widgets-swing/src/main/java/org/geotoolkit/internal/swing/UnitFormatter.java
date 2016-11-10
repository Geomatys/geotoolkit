/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.swing;

import java.util.Locale;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.text.InternationalFormatter;

import javax.measure.Unit;
import org.apache.sis.measure.UnitFormat;

import org.apache.sis.measure.Units;


/**
 * A formatter for {@link JFormattedTextField} selecting the units of measurement.
 * The field should be created as below:
 *
 * {@preformat java
 *     JFormattedTextField unitField = new JFormattedTextField(new UnitFormatter(locale));
 *     unitField.setValue(unit);
 *     // ...
 *     Unit<?> unit = (Unit<?>) unitField.getValue();
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.13
 * @module
 */
@SuppressWarnings("serial")
public final class UnitFormatter extends InternationalFormatter {
    /**
     * Creates a new formatter.
     *
     * @param locale The locale for units parsing and formatting.
     */
    public UnitFormatter(final Locale locale) {
        super(new UnitFormat(locale));
    }

    /**
     * Returns the unit representation of the given text, or {@code null}
     * if the text is null or empty.
     */
    @Override
    public Unit<?> stringToValue(String text) throws ParseException {
        if (text == null || (text = text.trim()).isEmpty()) {
            return null;
        }
        Unit<?> unit;
        try {
            unit = (Unit<?>) super.stringToValue(text);
        } catch (ParseException e) {
            try {
                // Tries UCUM syntax.
                unit = Units.valueOf(text);
            } catch (IllegalArgumentException ignore) {
                throw e;
            }
        }
        return unit;
    }
}
