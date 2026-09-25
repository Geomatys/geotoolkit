/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.storage;

import java.util.Collections;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DataStoresTest {

    private static final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();

    @Test
    public void reprojectTest() throws DataStoreException {

        CoordinateReferenceSystem inCrs = CommonCRS.WGS84.normalizedGeographic();
        CoordinateReferenceSystem outCrs = CommonCRS.WGS84.geographic();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Point.class).setName("geom").setCRS(inCrs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = ftb.build();

        final Point geometry = GF.createPoint(new Coordinate(10, 30));
        geometry.setUserData(inCrs);
        final Feature feature = type.newInstance();
        feature.setPropertyValue("geom", geometry);

        FeatureSet fs = new InMemoryFeatureSet(type, Collections.singleton(feature));

        final FeatureQuery query = DataStores.reproject(type, outCrs);
        final FeatureSet rfs = fs.subset(query);
        final Feature rfeature = rfs.features(false).findFirst().get();

        Point geom1 = (Point) rfeature.getPropertyValue("sis:geometry");
        Point geom2 = (Point) rfeature.getPropertyValue("geom");

        assertEquals(outCrs, geom1.getUserData());
        assertEquals(outCrs, geom2.getUserData());
        Assert.assertEquals(30.0, geom1.getX(), 0.0);
        Assert.assertEquals(10.0, geom1.getY(), 0.0);
        Assert.assertEquals(30.0, geom2.getX(), 0.0);
        Assert.assertEquals(10.0, geom2.getY(), 0.0);
    }
}
