/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.style.util.converter;

import java.awt.Color;

import org.geotoolkit.feature.util.converter.SimpleConverter;
import org.geotoolkit.internal.InternalUtilities;
import org.apache.sis.util.UnconvertibleObjectException;


/**
 * Implementation of ObjectConverter to convert a String into a Color.
 *
 * @module
 */
public class StringToColorConverter extends SimpleConverter<String, Color> {

    public StringToColorConverter(){
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Color> getTargetClass() {
        return Color.class;
    }

    @Override
    public Color apply(String source) throws UnconvertibleObjectException {
        if (source == null) {
            return null;
        }
        source = source.trim();
        try {
            return new java.awt.Color(InternalUtilities.parseColor(source), true);
        } catch (NumberFormatException e) {
            throw new UnconvertibleObjectException(e);
        }
    }
}
