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

import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.processing.util.converter.StringToSortByConverter;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

/**
 * Junit test for StringToSortByConverter
 * @author Quentin Boileau
 * @module
 */
public class SortByConverterTest extends org.geotoolkit.test.TestBase {


    @Test
    public void FilterConvertTest() throws NoSuchAuthorityCodeException, FactoryException, UnconvertibleObjectException {

        final ObjectConverter<String,SortBy[]> converter = StringToSortByConverter.getInstance();

        String inputString = "name:ASC,age:DESC";
        SortBy[] convertedSortBy = converter.apply(inputString);
        SortBy[] expectedSortBy = buildResultSortBy();
        assertArrayEquals(expectedSortBy, convertedSortBy);
    }

    private SortBy[] buildResultSortBy() throws FactoryException {
        SortBy[] sorters = new SortBy[2];
        sorters[0] = DefaultFactories.forBuildin(FilterFactory.class).sort("name", SortOrder.ASCENDING);
        sorters[1] = DefaultFactories.forBuildin(FilterFactory.class).sort("age", SortOrder.DESCENDING);

        return sorters;
    }
}
