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

import org.geotoolkit.feature.AbstractProperty;
import org.geotoolkit.feature.DefaultProperty;
import org.geotoolkit.filter.DefaultPropertyName;
import org.geotoolkit.filter.sort.DefaultSortBy;
import org.opengis.filter.sort.SortOrder;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.ObjectConverter;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import org.opengis.filter.sort.SortBy;
import static org.junit.Assert.*;

/**
 * Junit test for StringToSortByConverter
 * @author Quentin Boileau
 * @module pending
 */
public class SortByConverterTest {


    @Test
    public void FilterConvertTest() throws NoSuchAuthorityCodeException, FactoryException, NonconvertibleObjectException {

        final ObjectConverter<String,SortBy[]> converter = StringToSortByConverter.getInstance();
        
        String inputString = "name:ASC,age:DESC";
        SortBy[] convertedSortBy = converter.convert(inputString);
        SortBy[] expectedSortBy = buildResultSortBy();
        assertArrayEquals(expectedSortBy, convertedSortBy);
    }

    private SortBy[] buildResultSortBy() throws FactoryException {
        SortBy[] sorters = new SortBy[2];
        sorters[0] = new DefaultSortBy(new DefaultPropertyName("name"), SortOrder.ASCENDING);
        sorters[1] = new DefaultSortBy(new DefaultPropertyName("age"), SortOrder.DESCENDING);

        return sorters;
    }
}
