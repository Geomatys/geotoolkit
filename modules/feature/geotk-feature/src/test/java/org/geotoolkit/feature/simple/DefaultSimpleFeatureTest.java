/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.feature.simple;

import org.geotoolkit.feature.FeatureBuilder;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.feature.Feature;
import org.junit.Test;
import org.geotoolkit.feature.GeometryAttribute;
import org.geotoolkit.feature.type.FeatureType;

import org.opengis.geometry.BoundingBox;
import org.geotoolkit.geometry.DefaultBoundingBox;
import static org.junit.Assert.*;
import org.opengis.feature.MismatchedFeatureException;

public class DefaultSimpleFeatureTest {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final double DELTA = 0.0000001;

    private FeatureType schema;
    private Feature feature;

    public DefaultSimpleFeatureTest() throws MismatchedFeatureException {
        schema = FeatureTypeUtilities.createType("buildings", "the_geom:MultiPolygon,name:String,ADDRESS:String");
        feature = FeatureBuilder.build(schema, new Object[] {null, "ABC", "Random Road, 12"}, "building.1");
    }

    @Test
    public void testGetProperty() {
        assertEquals("ABC", feature.getProperty("name").getValue());
        assertNull(feature.getProperty("NOWHERE"));
        assertEquals(0, feature.getProperties("NOWHERE").size());
    }

    @Test
    public void testGetPropertyNullValue(){
        assertNotNull(feature.getProperty("the_geom"));
        assertNull(feature.getProperty("the_geom").getValue());
    }

    @Test
    public void testGeometryPropertyType(){
        assertTrue("expected GeometryAttribute, got " + feature.getProperty("the_geom").getClass().getName(),
                feature.getProperty("the_geom") instanceof GeometryAttribute);
    }

    @Test
    public void testDefaultGeometryProperty(){
        assertTrue("expected GeometryAttribute, got " + feature.getProperty("the_geom").getClass().getName(),
                feature.getProperty("the_geom") instanceof GeometryAttribute);
        assertNotNull(feature.getDefaultGeometryProperty());
        assertNull(feature.getDefaultGeometryProperty().getValue());
    }

    /**
     * check all the ways to update a simple feature updates the bbox properly.
     */
    @Test
    public void testBBoxUpdate(){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("point", Point.class,CommonCRS.WGS84.normalizedGeographic());
        final SimpleFeatureType type = ftb.buildSimpleFeatureType();

        final Object[] properties = new Object[]{
            GF.createPoint(new Coordinate(10, 10))
        };

        final Feature feature = new DefaultSimpleFeature(type, new DefaultFeatureId("-1"), properties, false);

        BoundingBox bbox = DefaultBoundingBox.castOrCopy(feature.getBounds());
        assertEquals(10,bbox.getMinX(),DELTA);
        assertEquals(10,bbox.getMinY(),DELTA);
        assertEquals(10,bbox.getMaxX(),DELTA);
        assertEquals(10,bbox.getMaxY(),DELTA);

        feature.getDefaultGeometryProperty().setValue(GF.createPoint(new Coordinate(20, 20)));
        bbox = DefaultBoundingBox.castOrCopy(feature.getBounds());
        assertEquals(20,bbox.getMinX(),DELTA);
        assertEquals(20,bbox.getMinY(),DELTA);
        assertEquals(20,bbox.getMaxX(),DELTA);
        assertEquals(20,bbox.getMaxY(),DELTA);

        feature.getProperty("point").setValue(GF.createPoint(new Coordinate(30, 30)));
        bbox = DefaultBoundingBox.castOrCopy(feature.getBounds());
        assertEquals(30,bbox.getMinX(),DELTA);
        assertEquals(30,bbox.getMinY(),DELTA);
        assertEquals(30,bbox.getMaxX(),DELTA);
        assertEquals(30,bbox.getMaxY(),DELTA);

        feature.setPropertyValue("point",GF.createPoint(new Coordinate(40, 40)));
        bbox = DefaultBoundingBox.castOrCopy(feature.getBounds());
        assertEquals(40,bbox.getMinX(),DELTA);
        assertEquals(40,bbox.getMinY(),DELTA);
        assertEquals(40,bbox.getMaxX(),DELTA);
        assertEquals(40,bbox.getMaxY(),DELTA);

        properties[0] = GF.createPoint(new Coordinate(50, 50));
        bbox = DefaultBoundingBox.castOrCopy(feature.getBounds());
        assertEquals(50,bbox.getMinX(),DELTA);
        assertEquals(50,bbox.getMinY(),DELTA);
        assertEquals(50,bbox.getMaxX(),DELTA);
        assertEquals(50,bbox.getMaxY(),DELTA);


    }

}
