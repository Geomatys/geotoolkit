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
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class StringToCharacterConverterTest {

    @Test
    public void converterFoundTest() {
        try {
            ObjectConverters.find(String.class, Character.class);
        } catch (UnconvertibleObjectException ex) {
            Assert.fail("Converter not found");
        }
    }

    @Test
    public void conversionTest() {
        Character resultChar = ObjectConverters.convert(";", Character.class);
        Assert.assertEquals(Character.valueOf(';'), resultChar);

        resultChar = ObjectConverters.convert("a", Character.class);
        Assert.assertEquals(Character.valueOf('a'), resultChar);

        //test null input
        ObjectConverter<? super String, ? extends Character> converter = ObjectConverters.find(String.class, Character.class);
        resultChar = converter.apply(null);
        Assert.assertNull(resultChar);
    }

    /**
     * test empty String
     */
    @Test(expected = UnconvertibleObjectException.class)
    public void conversionEmptyFailTest() {
        ObjectConverters.convert("", Character.class);
    }

    /**
     * test long String
     */
    @Test(expected = UnconvertibleObjectException.class)
    public void conversionLongFailTest() {
        ObjectConverters.convert("sentence String", Character.class);
    }
}
