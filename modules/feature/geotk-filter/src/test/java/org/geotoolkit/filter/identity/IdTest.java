/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.filter.identity;

import org.geotoolkit.filter.DefaultFilterFactory2;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class IdTest {

    
    public IdTest() {
    }

    @Test
    public void testFeatureId() {
        String strid = "testFeatureType.1";

        FeatureId id1 = FF.featureId(strid);
        FeatureId id2 = FF.featureId(strid);

        assertEquals(strid, id1.getID());
        assertEquals(id1, id2);

        assertTrue(id1.matches(FEATURE_1));
    }

    @Test
    public void testGmlFeatureId(){
        //geotoolkit doesnt handle GML objects
    }

}
