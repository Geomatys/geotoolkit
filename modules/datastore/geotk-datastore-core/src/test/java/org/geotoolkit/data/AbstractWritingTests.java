/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.data;

import junit.framework.TestCase;
import org.geotoolkit.factory.FactoryFinder;
import org.junit.Test;
import org.opengis.filter.FilterFactory;

/**
 * Generic writing tests for datastore.
 * Tests writers and append writers.
 *
 * @author Johann Sorel (Geomatys)
 * todo make generic tests
 */
public abstract class AbstractWritingTests extends TestCase{

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);


    protected abstract DataStore getDataStore();

    @Test
    public void testDataStore(){
        final DataStore store = getDataStore();
        assertNotNull(store);
    }


}
