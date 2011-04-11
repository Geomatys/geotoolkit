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
 * Junit test for StringToFilterConverter
 * @author Quentin Boileau
 * @module pending
 */
public class FilterConverterTest {


    @Test
    public void FilterConvertTest() throws NoSuchAuthorityCodeException, FactoryException, NonconvertibleObjectException {

        final ObjectConverter<String,Filter> converter = StringToFilterConverter.getInstance();
        
        String inputString = "name = 'Smith' AND age = 30";
        Filter convertedFilter = converter.convert(inputString);
        Filter expectedFilter = buildResultFilter();
        assertEquals(expectedFilter, convertedFilter);
    }

    private Filter buildResultFilter() throws FactoryException {

        FilterFactory2 ff = (FilterFactory2) FactoryFinder.getFilterFactory(
            new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

        final Filter filter1 = ff.equals(ff.property(new DefaultName("name")), ff.literal("Smith"));
        final Filter filter2 = ff.equals(ff.property(new DefaultName("age")), ff.literal(30));
        return ff.and(filter1, filter2);
      
    }
}
