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
package org.geotoolkit.process.converters;

import javax.measure.quantity.Length;
import javax.measure.quantity.Quantity;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test for StringToUnitConverter
 * @author Quentin Boileau
 * @module pending
 */
public class UnitConverterTest {


    @Test
    public void FilterConvertTest() throws NoSuchAuthorityCodeException, FactoryException, NonconvertibleObjectException {

        final ObjectConverter<String,Unit> converter = StringToUnitConverter.getInstance();
        
        String inputString = "mm";
        Unit convertedUnit= converter.convert(inputString);
        Unit expectedUnit = SI.MILLIMETRE;
        assertEquals(expectedUnit, convertedUnit);

        inputString = "kg";
        try{
            convertedUnit = converter.convert(inputString);
            fail("should not pass");
        }catch(NonconvertibleObjectException ex){
            //ok
        }

        
        inputString = "fail";
        try{
            convertedUnit = converter.convert(inputString);
            fail("should not pass");
        }catch(NonconvertibleObjectException ex){
            //ok
        }

    }
}
