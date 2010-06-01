/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010 Geomatys
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
 *
 *    Created on July 21, 2003, 4:00 PM
 */

package org.geotoolkit.feature.type;

import com.vividsolutions.jts.geom.Point;

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.junit.Test;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.junit.Assert.*;

/**
 * Test Feature Type Utilities.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FeatureTypeUtilitiesTest {

    @Test
    public void testReprojectType() throws Exception{
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("attGeom1", Point.class, 0,1,false,null);
        ftb.add(DefaultName.valueOf("attGeom2"), Point.class, DefaultGeographicCRS.WGS84,0,1,false,null);
        final FeatureType ft = ftb.buildFeatureType();

        final CoordinateReferenceSystem crs = CRS.decode("EPSG:27582");
        final FeatureType res = FeatureTypeUtilities.transform(ft, crs);


        assertEquals(crs, ((GeometryDescriptor)res.getDescriptor("attGeom1")).getCoordinateReferenceSystem() );
        assertEquals(crs, ((GeometryDescriptor)res.getDescriptor("attGeom2")).getCoordinateReferenceSystem() );
    }


}
