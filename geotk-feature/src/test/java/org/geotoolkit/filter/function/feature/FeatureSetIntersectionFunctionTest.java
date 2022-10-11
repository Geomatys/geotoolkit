/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.filter.function.feature;

import java.util.ArrayList;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.storage.MemoryFeatureSet;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.GeographicCRS;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureSetIntersectionFunctionTest {

    /**
     * Test the geometry resolution is used in FeatureSet query.
     */
    @Test
    public void testResolutionExtracted() {

        final GeographicCRS crs = CommonCRS.WGS84.normalizedGeographic();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Geometry.class).setName("geom").setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = ftb.build();
        final FeatureSet target = new MemoryFeatureSet(null, type, new ArrayList<>());

        { //default geometry factory has maximum resolution
            final GeometryFactory gf = new GeometryFactory();
            final Geometry geom = gf.createLineString(new CoordinateArraySequence(new Coordinate[]{new CoordinateXY(0, 0),new CoordinateXY(1, 1)}, 2));
            geom.setUserData(crs);

            FeatureQuery query = FeatureSetIntersectionFunction.createQuery(geom, target);
            Assert.assertEquals(0.0, query.getLinearResolution().getValue().doubleValue(), 0.0);
        }

        { //user defined precision model
            final GeometryFactory gf = new GeometryFactory(new PrecisionModel(10));
            final Geometry geom = gf.createLineString(new CoordinateArraySequence(new Coordinate[]{new CoordinateXY(0, 0),new CoordinateXY(1, 1)}, 2));
            geom.setUserData(crs);

            final FeatureQuery query = FeatureSetIntersectionFunction.createQuery(geom, target);
            Assert.assertEquals(10.0, query.getLinearResolution().getValue().doubleValue(), 0.0);
        }

    }
}
