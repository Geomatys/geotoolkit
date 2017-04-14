/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.apache.sis.internal.feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.Collection;
import org.apache.sis.feature.ReprojectFeatureType;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ReprojectFeatureTypeTest {

    private static final GeometryFactory GF = new GeometryFactory();

    @Test
    public void reprojectAttributeTest(){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Point.class).setName("attGeom").setCRS(CommonCRS.WGS84.geographic());
        final FeatureType baseType = ftb.build();

        //test view type
        final ReprojectFeatureType reprojType = new ReprojectFeatureType(baseType, CommonCRS.WGS84.normalizedGeographic());
        final Collection<? extends PropertyType> properties = reprojType.getProperties(true);
        assertEquals(1,properties.size());

        //test feature
        final Feature baseFeature = baseType.newInstance();
        baseFeature.setPropertyValue("attGeom", GF.createPoint(new Coordinate(10, 20)));

        final Feature reprojFeature = reprojType.newInstance(baseFeature);
        assertEquals(GF.createPoint(new Coordinate(20, 10)), reprojFeature.getPropertyValue("attGeom"));
    }

    @Test
    public void reprojectOperationTest(){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Point.class).setName("attGeom").setCRS(CommonCRS.WGS84.geographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType baseType = ftb.build();

        //test view type
        final ReprojectFeatureType viewType = new ReprojectFeatureType(baseType, CommonCRS.WGS84.normalizedGeographic());
        final Collection<? extends PropertyType> properties = viewType.getProperties(true);
        assertEquals(3,properties.size());
        assertTrue(viewType.getProperty("attGeom") instanceof Operation);
        assertTrue(viewType.getProperty("sis:geometry") instanceof Operation);

        //test feature
        final Feature baseFeature = baseType.newInstance();
        baseFeature.setPropertyValue("attGeom", GF.createPoint(new Coordinate(10, 20)));

        final Feature viewFeature = viewType.newInstance(baseFeature);
        assertEquals(GF.createPoint(new Coordinate(20, 10)), viewFeature.getPropertyValue("attGeom"));
        assertEquals(GF.createPoint(new Coordinate(20, 10)), viewFeature.getPropertyValue("sis:geometry"));
    }
}
