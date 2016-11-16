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

import javax.measure.Unit;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.measure.Units;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test for StringToUnitConverter
 * @author Quentin Boileau
 * @module
 */
public class UnitConverterTest extends org.geotoolkit.test.TestBase {


    @Test
    public void FilterConvertTest() throws NoSuchAuthorityCodeException, FactoryException, UnconvertibleObjectException {

        final ObjectConverter<? super String, ? extends Unit> converter = ObjectConverters.find(String.class, Unit.class);

        String inputString = "mm";
        Unit convertedUnit= converter.apply(inputString);
        Unit expectedUnit = Units.MILLIMETRE;
        assertEquals(inputString, expectedUnit, convertedUnit);

        inputString = "kg";
        convertedUnit = converter.apply(inputString);
        expectedUnit  = Units.KILOGRAM;
        assertEquals(inputString, expectedUnit, convertedUnit);

        inputString = "fail";
        try{
            convertedUnit = converter.apply(inputString);
            fail("should not pass");
        }catch(UnconvertibleObjectException ex){
            //ok
        }
    }
}
