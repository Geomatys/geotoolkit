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

import org.apache.sis.filter.DefaultFilterFactory;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.ObjectConverter;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.opengis.filter.Filter;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.filter.FilterFactory;

/**
 * Junit test for StringToFilterConverter
 * @author Quentin Boileau
 * @module
 */
public class FilterConverterTest {


    @Test
    public void FilterConvertTest() throws NoSuchAuthorityCodeException, FactoryException, UnconvertibleObjectException {

        final ObjectConverter<? super String, ? extends Filter> converter = ObjectConverters.find(String.class, Filter.class);

        String inputString = "name = 'Smith' AND age = 30";
        Filter convertedFilter = converter.apply(inputString);
        Filter expectedFilter = buildResultFilter();
        assertEquals(expectedFilter, convertedFilter);
    }

    private Filter buildResultFilter() throws FactoryException {

        FilterFactory ff = DefaultFilterFactory.forFeatures();

        final Filter filter1 = ff.equal(ff.property("name"), ff.literal("Smith"));
        final Filter filter2 = ff.equal(ff.property("age"), ff.literal(30));
        return ff.and(filter1, filter2);
    }
}
