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
 * Convert a Character into a String.
 * If input Character is {@code null}, output String will be also {@code null}.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class CharacterToStringConverter extends SimpleConverter<Character, String > {

    @Override
    public Class<Character> getSourceClass() {
        return Character.class;
    }

    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }

    @Override
    public String apply(Character character) throws UnconvertibleObjectException {
        if (character == null) {
            return null;
        }
        return String.valueOf(character.charValue());
    }

    @Override
    public ObjectConverter<String, Character> inverse() throws UnsupportedOperationException {
        return new StringToCharacterConverter();
    }
}
