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
package org.geotoolkit.util.converter;

import org.geotoolkit.processing.util.converter.StringToMapConverter;
import java.util.Map;

import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.ObjectConverter;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test for StringToMapConverter
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MapConverterTest extends org.geotoolkit.test.TestBase {


    @Test
    public void FilterConvertTest() throws NoSuchAuthorityCodeException, FactoryException, UnconvertibleObjectException {

        final ObjectConverter<String,Map> converter = StringToMapConverter.getInstance();

        final String inputString = "{login=jean, password=secret}";
        final Map result = converter.apply(inputString);

        assertEquals(2, result.size());
        assertTrue( result.containsKey("login"));
        assertTrue( result.containsKey("password"));
        assertEquals("jean", result.get("login"));
        assertEquals("secret", result.get("password"));

    }

}
