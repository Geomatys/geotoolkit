/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.feature.util.converter;

import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 * Convert a String into a Character.
 * If input String is {@code null}, output Character will be also {@code null}.
 * If input String is empty of contain more than one character, an {@link UnconvertibleObjectException} will be thrown.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class StringToCharacterConverter extends SimpleConverter<String, Character> {

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Character> getTargetClass() {
        return Character.class;
    }

    @Override
    public Character apply(String s) throws UnconvertibleObjectException {
        if (s == null) {
            return null;
        }

        if (s.length() != 1) {
            throw new UnconvertibleObjectException("Input String is empty or contain more than one character.");
        }
        return s.charAt(0);
    }

    @Override
    public ObjectConverter<Character, String> inverse() throws UnsupportedOperationException {
        return new CharacterToStringConverter();
    }
}
