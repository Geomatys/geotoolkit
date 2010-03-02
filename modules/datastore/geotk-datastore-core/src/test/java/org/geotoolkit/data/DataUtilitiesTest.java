/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;

import org.junit.Test;
import org.opengis.feature.type.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DataUtilitiesTest extends TestCase{


    /**
     * Test that the collection id is correctly set.
     */
    @Test
    public void testCollectionId() throws Exception{
        SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
        sftb.setName("temp");
        sftb.add("att1", String.class);
        FeatureType ft = sftb.buildFeatureType();

        FeatureCollection col = DataUtilities.collection("myId", ft);

        assertEquals("myId", col.getID());

    }

}
